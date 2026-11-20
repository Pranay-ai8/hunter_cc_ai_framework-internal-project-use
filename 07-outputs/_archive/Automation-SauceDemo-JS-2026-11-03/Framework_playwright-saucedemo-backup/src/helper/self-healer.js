const fs = require("fs");
const path = require("path");

const CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
const CACHE_FILE = path.resolve(__dirname, "..", "..", "..", "healing-cache.json");
const LOG_FILE = path.resolve(__dirname, "..", "..", "..", "healing-report.log");
const INTERACTIVE_SELECTORS = ["button", "a", "input", "select", "textarea", "[role]", "[data-testid]", "label"];
const MAX_DOM_SNAPSHOT_ELEMENTS = 150;
const CONFIDENCE_THRESHOLD = 0.75;

// -------------------------------------------------------------------------
// Cache helpers
// -------------------------------------------------------------------------

function readCache() {
    try {
        if (!fs.existsSync(CACHE_FILE)) return {};
        return JSON.parse(fs.readFileSync(CACHE_FILE, "utf8"));
    } catch {
        return {};
    }
}

function writeCache(cache) {
    try {
        fs.writeFileSync(CACHE_FILE, JSON.stringify(cache, null, 2));
    } catch (err) {
        console.warn(`[self-healer] Failed to persist healing cache: ${err.message}`);
    }
}

function getCacheKey(pageUrl, originalLocator) {
    return `${pageUrl}|${originalLocator}`;
}

function getCachedHeal(pageUrl, originalLocator) {
    const cache = readCache();
    const entry = cache[getCacheKey(pageUrl, originalLocator)];
    if (!entry) return null;
    if (Date.now() - entry.timestamp > CACHE_TTL_MS) return null;
    return entry;
}

function setCachedHeal(pageUrl, originalLocator, healed) {
    const cache = readCache();
    cache[getCacheKey(pageUrl, originalLocator)] = { ...healed, timestamp: Date.now() };
    writeCache(cache);
}

// -------------------------------------------------------------------------
// Logging
// -------------------------------------------------------------------------

function logHeal(originalLocator, healedLocator, confidence, error) {
    const firstErrorLine = (error && error.message ? error.message.split("\n")[0] : "unknown error");
    const line = `[${new Date().toISOString()}] HEAL SUCCESS | original="${originalLocator}" | healed="${healedLocator}" | confidence=${confidence.toFixed(2)} | originalError="${firstErrorLine}"\n`;
    try {
        fs.appendFileSync(LOG_FILE, line);
    } catch (err) {
        console.warn(`[self-healer] Failed to write healing-report.log: ${err.message}`);
    }
}

// -------------------------------------------------------------------------
// Tokenization / scoring
// -------------------------------------------------------------------------

function tokenize(text) {
    if (!text) return new Set();
    return new Set(
        String(text)
            .replace(/[#.\[\]='"_/\-:>]/g, " ")
            .split(/\s+/)
            .map((w) => w.trim().toLowerCase())
            .filter((w) => w.length > 1)
    );
}

/**
 * Builds the candidate's own scoring word set — deliberately EXCLUDES `class`,
 * since CSS class lists are structural/styling noise (BEM/utility classes), not
 * semantic identity, and would dilute precision.
 */
function candidateWordSet(candidate) {
    const fields = [candidate.tag, candidate.id, candidate.name, candidate.role, candidate.ariaLabel, candidate.dataTestId, candidate.placeholder, candidate.forAttr, candidate.text];
    const set = new Set();
    for (const field of fields) {
        for (const word of tokenize(field)) set.add(word);
    }
    return set;
}

function scoreAgainstKeywords(candidate, keywords) {
    if (!keywords || keywords.size === 0) return 0;
    const wordSet = candidateWordSet(candidate);
    if (wordSet.size === 0) return 0;

    const matched = [...keywords].filter((k) => wordSet.has(k));
    if (matched.length === 0) return 0;

    const recall = matched.length / keywords.size;
    const precision = matched.length / wordSet.size;
    const f1 = (2 * recall * precision) / (recall + precision);

    let bonus = 0;
    const testIdWords = tokenize(candidate.dataTestId);
    const idWords = tokenize(candidate.id);
    if (matched.some((m) => testIdWords.has(m))) bonus += 0.15;
    if (matched.some((m) => idWords.has(m))) bonus += 0.1;

    return Math.min(1, f1 + bonus);
}

/**
 * Scores a candidate against locator/hint/combined keyword sets SEPARATELY and
 * takes the max — never pools them into one set (see standard rule #1).
 */
function scoreCandidate(candidate, locatorKeywords, hintKeywords, combinedKeywords) {
    const scores = [
        scoreAgainstKeywords(candidate, locatorKeywords),
        scoreAgainstKeywords(candidate, hintKeywords),
        scoreAgainstKeywords(candidate, combinedKeywords)
    ];
    return Math.max(...scores);
}

// -------------------------------------------------------------------------
// Locator expression building
// -------------------------------------------------------------------------

function buildLocatorExpression(candidate) {
    // NOTE (found by deliberately breaking a locator and healing it live, not by inspection):
    // collectCandidates() populates `dataTestId` from EITHER a real `data-testid` attribute OR a
    // `data-test` attribute (SauceDemo — and many real apps — use `data-test`, not `data-testid`).
    // Emitting only `[data-testid="..."]` here silently matched zero elements on this app and made
    // every heal attempt fail its own count()>0 validation despite a correct, high-confidence
    // candidate. Match either attribute name so the healed expression actually resolves.
    if (candidate.dataTestId) return `[data-testid="${candidate.dataTestId}"], [data-test="${candidate.dataTestId}"]`;
    if (candidate.id) return `#${candidate.id}`;
    if (candidate.name) return `${candidate.tag}[name="${candidate.name}"]`;
    if (candidate.placeholder) return `${candidate.tag}[placeholder="${candidate.placeholder}"]`;
    if (candidate.ariaLabel) return `${candidate.tag}[aria-label="${candidate.ariaLabel}"]`;
    if (candidate.role) return `${candidate.tag}[role="${candidate.role}"]`;
    if (candidate.class) {
        const firstClass = String(candidate.class).trim().split(/\s+/)[0];
        if (firstClass) return `${candidate.tag}.${firstClass}`;
    }
    if (candidate.text) return `text=${candidate.text.slice(0, 60)}`;
    return candidate.tag;
}

// -------------------------------------------------------------------------
// DOM snapshot
// -------------------------------------------------------------------------

async function collectCandidates(page) {
    return page.evaluate(
        ({ selectors, limit }) => {
            const elements = Array.from(document.querySelectorAll(selectors.join(","))).slice(0, limit);
            return elements.map((el) => ({
                tag: el.tagName.toLowerCase(),
                id: el.id || "",
                class: el.className || "",
                name: el.getAttribute("name") || "",
                role: el.getAttribute("role") || "",
                ariaLabel: el.getAttribute("aria-label") || "",
                dataTestId: el.getAttribute("data-testid") || el.getAttribute("data-test") || "",
                placeholder: el.getAttribute("placeholder") || "",
                forAttr: el.getAttribute("for") || "",
                text: (el.textContent || "").trim().replace(/\s+/g, " ")
            }));
        },
        { selectors: INTERACTIVE_SELECTORS, limit: MAX_DOM_SNAPSHOT_ELEMENTS }
    );
}

// -------------------------------------------------------------------------
// Public API
// -------------------------------------------------------------------------

/**
 * Attempts to find a working replacement for a locator that just failed.
 * @param {import('@playwright/test').Page} page
 * @param {string} originalLocator the locator string that failed
 * @param {Error} error the error thrown by the original locator wait/action
 * @param {string} hint human-readable intent (objName), the strongest signal available
 * @returns {Promise<{success: boolean, locator: string|null, confidence: number, strategy: string}>}
 */
async function findHealedLocator(page, originalLocator, error, hint = "") {
    const pageUrl = page.url();

    const cached = getCachedHeal(pageUrl, originalLocator);
    if (cached) {
        return { success: true, locator: cached.locator, confidence: cached.confidence, strategy: "rule-based-cached" };
    }

    // Keywords come ONLY from the locator string and the hint — never from error.message or a DOM dump.
    const locatorKeywords = tokenize(originalLocator);
    const hintKeywords = tokenize(hint);
    const combinedKeywords = new Set([...locatorKeywords, ...hintKeywords]);

    let candidates;
    try {
        candidates = await collectCandidates(page);
    } catch (evalErr) {
        console.warn(`[self-healer] Failed to collect DOM candidates: ${evalErr.message}`);
        return { success: false, locator: null, confidence: 0, strategy: "rule-based" };
    }

    let best = null;
    let bestScore = 0;
    for (const candidate of candidates) {
        const score = scoreCandidate(candidate, locatorKeywords, hintKeywords, combinedKeywords);
        if (score > bestScore) {
            bestScore = score;
            best = candidate;
        }
    }

    if (!best || bestScore < CONFIDENCE_THRESHOLD) {
        return { success: false, locator: null, confidence: bestScore, strategy: "rule-based" };
    }

    const healedExpression = buildLocatorExpression(best);

    // Validate the healed locator actually resolves before handing it back.
    try {
        const count = await page.locator(healedExpression).count();
        if (count === 0) {
            return { success: false, locator: null, confidence: bestScore, strategy: "rule-based" };
        }
    } catch {
        return { success: false, locator: null, confidence: bestScore, strategy: "rule-based" };
    }

    setCachedHeal(pageUrl, originalLocator, { locator: healedExpression, confidence: bestScore });
    logHeal(originalLocator, healedExpression, bestScore, error);

    return { success: true, locator: healedExpression, confidence: bestScore, strategy: "rule-based" };
}

module.exports = { findHealedLocator, CONFIDENCE_THRESHOLD, CACHE_FILE, LOG_FILE };
