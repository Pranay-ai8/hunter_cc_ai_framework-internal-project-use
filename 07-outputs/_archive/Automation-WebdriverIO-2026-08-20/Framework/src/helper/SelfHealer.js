import fs from 'node:fs';
import path from 'node:path';
import { browser } from '@wdio/globals';

/**
 * Rule-based self-healing for stale/renamed WebdriverIO selectors — ported from this app's
 * Appium-Java sibling framework's `Utils/SelfHealer.java` (itself ported from the Playwright-Java
 * `SelfHealer`; see `03-standards/appium-java-standards.md`'s "Self-Healing Locators" section),
 * adapted for WebdriverIO's `browser.getPageSource()`/`browser.$()` instead of a WebElement/By API.
 *
 * Same algorithm as the Java implementation (identical scoring model):
 * 1. Snapshot candidate elements from `browser.getPageSource()` (the UiAutomator2/XCUITest XML
 *    hierarchy) instead of a browser DOM query.
 * 2. Score each candidate by precision/recall (F1) over content-desc/name, resource-id, and
 *    text/label/value against a hint's keyword set — deliberately EXCLUDING the element's tag/class
 *    (structural noise, not semantic identity).
 * 3. Resolve the best candidate (score >= CONFIDENCE_THRESHOLD) to a real selector: accessibility id
 *    (strongest signal) -> resource-id (`id=` shorthand if namespaced, else xpath — an unprefixed
 *    resource-id breaks WebdriverIO's `id=` strategy the same way it breaks Java's `By.id`) -> text
 *    xpath.
 * 4. Validate the resolved selector actually matches something on screen right now.
 * 5. Cache successful heals for CACHE_TTL_MS in `healing-cache.json` and log every heal to
 *    `healing-report.log`, both at the project root (one level above this framework's own folder,
 *    matching the Java sibling's convention).
 */

const CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
const CONFIDENCE_THRESHOLD = 0.75;
const MAX_SNAPSHOT_ELEMENTS = 150;

const PROJECT_ROOT = path.resolve(process.cwd(), '..');
const CACHE_FILE = path.join(PROJECT_ROOT, 'healing-cache.json');
const LOG_FILE = path.join(PROJECT_ROOT, 'healing-report.log');

const ELEMENT_PATTERN = /<([\w.]+)\s+([^>]*?)\/?>/g;
const ATTR_PATTERN = /(\S+)="([^"]*)"/g;

/**
 * Attempts to find a real, on-screen replacement for a selector that just failed, using only
 * `hint` (the descriptive name of the element, e.g. "Login Button") as the healing signal.
 * Returns `{ success: false, confidence }` — never throws — so callers can decide whether to
 * rethrow the original error.
 */
export async function findHealedLocator(hint, originalError) {
    const screenKey = await safeScreenKey();
    const cacheKey = `${screenKey}|${hint}`;

    const cached = getFreshCacheEntry(cacheKey);
    if (cached) {
        const rebuilt = await rebuildElement(cached);
        if (rebuilt) {
            console.info(`SelfHealer: cache hit for hint [${hint}] (confidence ${cached.confidence})`);
            return { success: true, element: rebuilt, confidence: cached.confidence };
        }
        // Cached recipe no longer resolves to anything real — fall through to fresh detection.
    }

    const hintKeywords = uniqueWords(normalizeWords(hint));
    if (hintKeywords.length === 0) {
        return { success: false, confidence: 0 };
    }

    const candidates = await collectCandidates();

    let bestCandidate = null;
    let bestScore = 0;
    for (const candidate of candidates) {
        const score = scoreCandidate(candidate, hintKeywords);
        if (score > bestScore) {
            bestScore = score;
            bestCandidate = candidate;
        }
    }

    if (!bestCandidate || bestScore < CONFIDENCE_THRESHOLD) {
        console.warn(`SelfHealer: no candidate above confidence threshold for hint [${hint}] (best score ${bestScore})`);
        return { success: false, confidence: bestScore };
    }

    const resolved = resolveCandidate(bestCandidate);
    const element = resolved ? await safeFind(resolved.selector) : null;
    if (!element) {
        console.warn(`SelfHealer: best-scoring candidate for hint [${hint}] did not resolve to a real element`);
        return { success: false, confidence: bestScore };
    }

    const entry = { strategy: resolved.strategy, value: resolved.value, confidence: bestScore, timestamp: Date.now() };
    putCacheEntry(cacheKey, entry);
    appendLog(hint, bestScore, resolved.strategy, resolved.value, originalError);

    return { success: true, element, confidence: bestScore };
}

// ---------------------------------------------------------------- tokenization / scoring

function normalizeWords(text) {
    if (!text) {
        return [];
    }
    const cleaned = text.replace(/[#[\]().,/\\:'"?!\-*_=+<>{}]/g, ' ').toLowerCase();
    return cleaned.split(/\s+/).filter((word) => word.length >= 3);
}

function uniqueWords(words) {
    return [...new Set(words)];
}

/** Word set built ONLY from a candidate's own identity-bearing attributes (see module docstring). */
function candidateWordSet(candidate) {
    const joined = [candidate.accessibilityId, resourceIdLocalName(candidate.resourceId), candidate.text].join(' ');
    return new Set(normalizeWords(joined));
}

/** The part of a resource-id after the last '/', e.g. "button-LOGIN" from "com.app:id/button-LOGIN". */
function resourceIdLocalName(resourceId) {
    if (!resourceId) {
        return '';
    }
    const slash = resourceId.lastIndexOf('/');
    return slash >= 0 ? resourceId.slice(slash + 1) : resourceId;
}

/** Precision/recall (F1) scoring — identical model to the Java/web-stack SelfHealer implementations. */
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

    // Bonus for stable, intentional identifiers directly containing a matched keyword — the
    // accessibility id is this app's own testID-driven "stable test selector" convention.
    if (candidate.accessibilityId) {
        const accessibilityIdLower = candidate.accessibilityId.toLowerCase();
        if (matched.some((keyword) => accessibilityIdLower.includes(keyword))) {
            score += 0.15;
        }
    }
    if (candidate.resourceId) {
        const resourceIdLower = resourceIdLocalName(candidate.resourceId).toLowerCase();
        if (matched.some((keyword) => resourceIdLower.includes(keyword))) {
            score += 0.1;
        }
    }

    return Math.min(1.0, score);
}

// ---------------------------------------------------------------- UI hierarchy snapshot

async function collectCandidates() {
    let pageSource;
    try {
        pageSource = await browser.getPageSource();
    } catch (e) {
        console.warn(`SelfHealer: getPageSource() failed: ${e.message}`);
        return [];
    }

    const result = [];
    for (const match of pageSource.matchAll(ELEMENT_PATTERN)) {
        if (result.length >= MAX_SNAPSHOT_ELEMENTS) {
            break;
        }
        const [, tag, attrsRaw] = match;
        if (tag === 'hierarchy') {
            continue;
        }
        const attrs = parseAttributes(attrsRaw);

        // Android: content-desc/resource-id/text. iOS: name (the accessibility identifier —
        // confirmed live to carry this app's same testID values as Android's content-desc), and
        // label/value fill the text role since iOS has no @text attribute.
        const accessibilityId = attrs['content-desc'] || attrs.name || '';
        const resourceId = attrs['resource-id'] || '';
        const text = attrs.text || attrs.label || attrs.value || '';

        // Only elements carrying at least one identity-bearing attribute are useful healing
        // candidates — skip pure layout containers, which would only add noise and slow scoring.
        if (!accessibilityId && !resourceId && !text) {
            continue;
        }

        result.push({ tag, accessibilityId, resourceId, text });
    }
    return result;
}

function parseAttributes(rawAttrs) {
    const attrs = {};
    for (const match of rawAttrs.matchAll(ATTR_PATTERN)) {
        attrs[match[1]] = match[2];
    }
    return attrs;
}

// ---------------------------------------------------------------- resolution (candidate -> real selector)

function resolveCandidate(candidate) {
    // 1. content-desc (Android) / name (iOS) -> accessibility id — strongest signal on either
    //    platform, this app's stable-selector convention (confirmed live).
    if (candidate.accessibilityId) {
        return { selector: `~${candidate.accessibilityId}`, strategy: 'ACCESSIBILITY_ID', value: candidate.accessibilityId };
    }
    // 2. resource-id -> `id=` shorthand if properly namespaced ("pkg:id/name"), else xpath (an
    //    unprefixed resource-id breaks the `id=` strategy, which silently mis-prefixes it).
    if (candidate.resourceId) {
        if (candidate.resourceId.includes(':')) {
            return { selector: `id=${candidate.resourceId}`, strategy: 'RESOURCE_ID', value: candidate.resourceId };
        }
        return { selector: `//*[@resource-id="${candidate.resourceId}"]`, strategy: 'RESOURCE_ID_XPATH', value: candidate.resourceId };
    }
    // 3. visible text -> xpath match against whichever attribute the platform actually uses
    //    (@text on Android, @label/@value on iOS — there is no single shared attribute name).
    if (candidate.text) {
        return { selector: textXpath(candidate.text), strategy: 'TEXT', value: candidate.text };
    }
    return null;
}

function textXpath(text) {
    return `//*[@text="${text}" or @label="${text}" or @value="${text}"]`;
}

async function rebuildElement(entry) {
    try {
        let selector;
        switch (entry.strategy) {
            case 'ACCESSIBILITY_ID':
                selector = `~${entry.value}`;
                break;
            case 'RESOURCE_ID':
                selector = `id=${entry.value}`;
                break;
            case 'RESOURCE_ID_XPATH':
                selector = `//*[@resource-id="${entry.value}"]`;
                break;
            case 'TEXT':
                selector = textXpath(entry.value);
                break;
            default:
                return null;
        }
        return await safeFind(selector);
    } catch (e) {
        return null;
    }
}

async function safeFind(selector) {
    try {
        const element = await browser.$(selector);
        return (await element.isExisting()) ? element : null;
    } catch (e) {
        return null;
    }
}

async function safeScreenKey() {
    try {
        // No stable "URL" equivalent for a native screen — the currently focused Android activity
        // is the closest analogue to the web SelfHealer's page.url() cache-partition key. Not
        // supported on iOS (no activity concept) — falls through to the catch, same as the Java
        // sibling's safeScreenKey() failing its AndroidDriver cast on iOS.
        return await browser.execute('mobile: getCurrentActivity', {});
    } catch (e) {
        return '';
    }
}

// ---------------------------------------------------------------- cache + log persistence

function getFreshCacheEntry(key) {
    const cache = loadCache();
    const entry = cache[key];
    if (entry && Date.now() - entry.timestamp < CACHE_TTL_MS) {
        return entry;
    }
    return null;
}

function putCacheEntry(key, entry) {
    const cache = loadCache();
    cache[key] = entry;
    saveCache(cache);
}

function loadCache() {
    try {
        if (!fs.existsSync(CACHE_FILE)) {
            return {};
        }
        return JSON.parse(fs.readFileSync(CACHE_FILE, 'utf8'));
    } catch (e) {
        console.warn(`SelfHealer: failed to read healing-cache.json, starting fresh: ${e.message}`);
        return {};
    }
}

function saveCache(cache) {
    try {
        fs.writeFileSync(CACHE_FILE, JSON.stringify(cache, null, 2));
    } catch (e) {
        console.warn(`SelfHealer: failed to persist healing-cache.json: ${e.message}`);
    }
}

function appendLog(hint, score, strategy, value, originalError) {
    const firstLine = originalError && originalError.message ? originalError.message.split(/\r?\n/)[0] : 'n/a';
    const line = `[${new Date().toISOString()}] HEALED: hint="${hint}" strategy=${strategy} value="${value}" confidence=${score.toFixed(2)} originalError="${firstLine}"\n`;
    try {
        fs.appendFileSync(LOG_FILE, line, 'utf8');
    } catch (e) {
        console.warn(`SelfHealer: failed to append healing-report.log: ${e.message}`);
    }
}
