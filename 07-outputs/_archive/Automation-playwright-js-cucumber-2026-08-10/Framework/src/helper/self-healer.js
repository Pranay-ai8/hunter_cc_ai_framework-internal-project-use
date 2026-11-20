const fs = require("fs-extra");
const path = require("path");

const CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
const CACHE_FILE = path.resolve(__dirname, "..", "..", "..", "healing-cache.json");
const LOG_FILE = path.resolve(__dirname, "..", "..", "..", "healing-report.log");
const MAX_DOM_SNAPSHOT_ELEMENTS = 150;
const INTERACTIVE_SELECTORS = [
    "button",
    "a",
    "input",
    "select",
    "textarea",
    "[role]",
    "[data-testid]",
    "label",
];
const CONFIDENCE_THRESHOLD = 0.75;

function normalizeWords(text) {
    return (text || "")
        .replace(/[#\[\]\(\)\{\}\.\,\/\\\:\'\"\?\!\-*_=+<>]/g, " ")
        .toLowerCase()
        .split(/\s+/)
        .filter((word) => word.length >= 3);
}

function uniqueWords(words) {
    return Array.from(new Set(words));
}

function buildLocatorExpression(candidate) {
    if (candidate.role && candidate.name) {
        return `page.getByRole(${JSON.stringify(candidate.role)}, { name: ${JSON.stringify(candidate.name)} })`;
    }
    if (candidate.dataTestId) {
        return `page.getByTestId(${JSON.stringify(candidate.dataTestId)})`;
    }
    if (candidate.ariaLabel) {
        return `page.getByLabel(${JSON.stringify(candidate.ariaLabel)})`;
    }
    if (candidate.label) {
        return `page.getByLabel(${JSON.stringify(candidate.label)})`;
    }
    if (candidate.text && ["button", "a", "label", "span"].includes(candidate.tag)) {
        return `page.getByText(${JSON.stringify(candidate.text)})`;
    }
    if (candidate.id) {
        return `page.locator(${JSON.stringify(`#${candidate.id}`)})`;
    }
    if (candidate.name) {
        return `page.locator(${JSON.stringify(`[name="${candidate.name}"]`)})`;
    }
    if (candidate.dataTestId) {
        return `page.locator(${JSON.stringify(`[data-testid="${candidate.dataTestId}"]`)})`;
    }
    if (candidate.class) {
        const firstClass = candidate.class.split(/\s+/).find(Boolean);
        if (firstClass) {
            return `page.locator(${JSON.stringify(`.${firstClass}`)})`;
        }
    }
    return `page.locator(${JSON.stringify(candidate.tag)})`;
}

/**
 * Tokenizes only the candidate's own identity-bearing attributes into a word SET
 * (not a raw concatenated string) so matching is exact-word, not substring — a
 * keyword like "id" can no longer spuriously match unrelated text containing "id".
 *
 * Deliberately excludes `class`: CSS class lists are structural/styling noise in
 * most real apps (BEM/utility classes like "btn_primary btn_action"), not semantic
 * identity — including them inflates the word-set size with irrelevant tokens and
 * unfairly dilutes precision for elements that simply have several CSS classes.
 * `class` is still used as a locator-building fallback in buildLocatorExpression.
 */
function candidateWordSet(candidate) {
    const text = [
        candidate.id,
        candidate.name,
        candidate.role,
        candidate.ariaLabel,
        candidate.dataTestId,
        candidate.placeholder,
        candidate.forAttr,
        candidate.text,
    ]
        .filter(Boolean)
        .join(" ");
    return new Set(normalizeWords(text));
}

/**
 * Precision/recall (F1) scoring: recall = how much of what we're looking for this
 * candidate covers; precision = how much of the candidate's own identity is made up
 * of that match (so a giant, unrelated element that happens to contain one matching
 * word doesn't win just by being wordy). A candidate that matches every keyword with
 * nothing extra scores 1.0; a candidate sharing one incidental word out of many scores
 * close to 0 — the opposite of the old substring-count-over-total-keywords formula,
 * which let noise keywords dilute every score toward the same low baseline.
 */
function scoreCandidate(candidate, keywords) {
    if (keywords.length === 0) {
        return 0;
    }
    const words = candidateWordSet(candidate);
    if (words.size === 0) {
        return 0;
    }

    const matched = keywords.filter((keyword) => words.has(keyword));
    if (matched.length === 0) {
        return 0;
    }

    const recall = matched.length / keywords.length;
    const precision = matched.length / words.size;
    let score = (2 * recall * precision) / (recall + precision);

    // Bonus for stable, intentional identifiers (data-testid/id) directly containing
    // a matched keyword — these are the most trustworthy signal a human meant this
    // element, worth more than incidental text/aria-label overlap.
    if (candidate.dataTestId && matched.some((k) => candidate.dataTestId.toLowerCase().includes(k))) {
        score += 0.15;
    }
    if (candidate.id && matched.some((k) => candidate.id.toLowerCase().includes(k))) {
        score += 0.1;
    }

    return Math.min(1, score);
}

function loadCache() {
    try {
        if (!fs.existsSync(CACHE_FILE)) {
            return {};
        }
        return fs.readJsonSync(CACHE_FILE);
    } catch (error) {
        return {};
    }
}

function saveCache(cache) {
    try {
        fs.writeJsonSync(CACHE_FILE, cache, { spaces: 2 });
    } catch (error) {
        // swallow cache-write failures so healing still proceeds
    }
}

function getCacheKey(pageUrl, locator) {
    return `${pageUrl}|${locator}`;
}

async function validateLocatorExpression(page, locatorExpression) {
    try {
        const locator = new Function("page", `return ${locatorExpression}`)(page);
        const count = await locator.count();
        return count > 0;
    } catch (error) {
        return false;
    }
}

async function findHealedLocator(page, originalLocator, error, hint = "") {
    const pageUrl = page.url();
    const cacheKey = getCacheKey(pageUrl, originalLocator);
    const cache = loadCache();
    const cached = cache[cacheKey];
    if (cached && Date.now() - cached.timestamp < CACHE_TTL_MS) {
        return { ...cached, success: true };
    }

    // Keywords come ONLY from what we're looking FOR — the original locator string
    // and/or an optional human-readable hint (e.g. "Login Button") — never from
    // error.message or a DOM dump. Either of those describes what's on the page
    // rather than what we want, and previously injected structural noise (literal
    // attribute-name labels like "class"/"type"/"name" from the snapshot's own
    // formatting) that matched nearly every element and diluted every real score.
    //
    // Locator and hint are scored SEPARATELY (plus once combined) rather than pooled
    // into one shared keyword set — self-healing exists precisely for when the
    // locator itself is the broken/stale part (e.g. "#checkout12" for the real
    // "#checkout"), so an untranslatable fragment lifted from a corrupted locator
    // must not be allowed to drag down recall for an otherwise-clean hint match.
    const locatorKeywords = uniqueWords(normalizeWords(originalLocator));
    const hintKeywords = uniqueWords(normalizeWords(hint));
    const combinedKeywords = uniqueWords([...locatorKeywords, ...hintKeywords]);

    const candidates = await page.evaluate(
        ({ selectors, limit }) => {
            const elements = Array.from(document.querySelectorAll(selectors.join(","))).slice(0, limit);
            return elements.map((el) => ({
                tag: el.tagName.toLowerCase(),
                id: el.id || "",
                class: el.className || "",
                name: el.getAttribute("name") || "",
                role: el.getAttribute("role") || "",
                ariaLabel: el.getAttribute("aria-label") || "",
                dataTestId: el.getAttribute("data-testid") || "",
                placeholder: el.getAttribute("placeholder") || "",
                type: el.getAttribute("type") || "",
                forAttr: el.getAttribute("for") || "",
                href: el.getAttribute("href") || "",
                text: (el.textContent || "").trim().replace(/\s+/g, " "),
            }));
        },
        { selectors: INTERACTIVE_SELECTORS, limit: MAX_DOM_SNAPSHOT_ELEMENTS }
    );

    const scored = candidates
        .map((candidate) => ({
            candidate,
            score: Math.max(
                scoreCandidate(candidate, hintKeywords),
                scoreCandidate(candidate, locatorKeywords),
                scoreCandidate(candidate, combinedKeywords)
            ),
        }))
        .filter((entry) => entry.score > 0)
        .sort((a, b) => b.score - a.score);

    const best = scored[0];
    if (!best || best.score < CONFIDENCE_THRESHOLD) {
        return {
            success: false,
            locator: null,
            confidence: best ? best.score : 0,
            strategy: "rule-based",
        };
    }

    const locatorExpression = buildLocatorExpression(best.candidate);
    const isValid = await validateLocatorExpression(page, locatorExpression);
    if (!isValid) {
        return {
            success: false,
            locator: null,
            confidence: best.score,
            strategy: "rule-based",
        };
    }

    const result = {
        success: true,
        locator: locatorExpression,
        confidence: best.score,
        strategy: "rule-based",
        timestamp: Date.now(),
    };

    cache[cacheKey] = result;
    saveCache(cache);
    try {
        fs.appendFileSync(
            LOG_FILE,
            `[${new Date().toISOString()}] HEALED: "${originalLocator}" → "${locatorExpression}" ` +
                `(confidence: ${best.score}, original error: ${error.message.split("\n")[0]})\n`
        );
    } catch (error) {
        // ignore report file failures
    }

    return result;
}

module.exports = {
    findHealedLocator,
};
