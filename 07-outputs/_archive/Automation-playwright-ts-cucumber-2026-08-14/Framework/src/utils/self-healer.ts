// src/utils/self-healer.ts
//
// Self-healing locator layer for the Playwright + TypeScript + Cucumber stack.
// Ported from the battle-tested JavaScript reference implementation
// (07-outputs/_archive/Automation-playwright-js-cucumber-2026-08-10/Framework/src/helper/self-healer.js)
// per 03-standards/playwright-typescript-standards.md's "Self-Healing Locators" section.
//
// Key TypeScript-specific deltas from the JavaScript original (see the standard for the
// full rationale):
//   - Locators here are `get` properties returning an already-resolved Playwright `Locator`,
//     not a raw selector string — there is nothing to introspect/tokenize on the locator
//     side, so the human-readable `hint` string is the ONLY keyword source. There is no
//     locatorKeywords/combinedKeywords pass.
//   - Healed candidates are resolved to a real `Locator` via a typed switch
//     (`resolveCandidate`) — never `eval`/`new Function` (forbidden by this stack's lint
//     config and TypeScript strict mode).
import * as fs from 'fs-extra';
import * as path from 'path';
import { Locator, Page } from '@playwright/test';

export interface HealResult {
  success: boolean;
  locator: Locator | null;
  confidence: number;
}

interface Candidate {
  tag: string;
  id: string;
  class: string;
  name: string;
  role: string;
  ariaLabel: string;
  dataTestId: string;
  placeholder: string;
  type: string;
  forAttr: string;
  href: string;
  text: string;
}

interface ScoredCandidate {
  candidate: Candidate;
  score: number;
}

interface CachedHeal {
  candidate: Candidate;
  confidence: number;
  timestamp: number;
}

// healing-cache.json / healing-report.log live at the shared ancestor of Framework/,
// PageObjects/, and Scripts/ (07-outputs/Automation/) — outside src/, since they are
// runtime artifacts, not source. __dirname here is Framework/src/utils, so three '..'
// steps back out of utils -> src -> Framework, landing one level above Framework/.
const CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
const CACHE_FILE = path.resolve(__dirname, '..', '..', '..', 'healing-cache.json');
const LOG_FILE = path.resolve(__dirname, '..', '..', '..', 'healing-report.log');
const INTERACTIVE_SELECTORS = ['button', 'a', 'input', 'select', 'textarea', '[role]', '[data-testid]', 'label'];
const MAX_DOM_SNAPSHOT_ELEMENTS = 150;
const CONFIDENCE_THRESHOLD = 0.75;

function normalizeWords(text: string | undefined | null): string[] {
  return (text || '')
    .replace(/[#[\]().,/\\:'"?!\-*_=+<>{}]/g, ' ')
    .toLowerCase()
    .split(/\s+/)
    .filter((word) => word.length >= 3);
}

function uniqueWords(words: string[]): string[] {
  return Array.from(new Set(words));
}

/**
 * Tokenizes only the candidate's own identity-bearing attributes into a word SET
 * (not a raw concatenated string) so matching is exact-word, not substring.
 *
 * Deliberately excludes `class`: CSS class lists are structural/styling noise in most
 * real apps (BEM/utility classes like "btn_primary btn_action"), not semantic identity —
 * including them inflates the word-set size with irrelevant tokens and unfairly dilutes
 * precision for elements that simply carry several CSS classes. See the JavaScript
 * standard's rule 3 for the concrete failure this caused historically.
 */
function candidateWordSet(candidate: Candidate): Set<string> {
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
    .join(' ');
  return new Set(normalizeWords(text));
}

/**
 * Precision/recall (F1) scoring — identical model to the JavaScript reference. recall =
 * how much of the hint this candidate covers; precision = how much of the candidate's own
 * identity is made up of that match, so a large unrelated element sharing one incidental
 * word doesn't win just by being wordy.
 */
function scoreCandidate(candidate: Candidate, keywords: string[]): number {
  if (keywords.length === 0) return 0;
  const words = candidateWordSet(candidate);
  if (words.size === 0) return 0;

  const matched = keywords.filter((keyword) => words.has(keyword));
  if (matched.length === 0) return 0;

  const recall = matched.length / keywords.length;
  const precision = matched.length / words.size;
  let score = (2 * recall * precision) / (recall + precision);

  // Bonus for stable, intentional identifiers (data-testid/id) directly containing a
  // matched keyword — the most trustworthy signal a human meant this element.
  if (candidate.dataTestId && matched.some((k) => candidate.dataTestId.toLowerCase().includes(k))) {
    score += 0.15;
  }
  if (candidate.id && matched.some((k) => candidate.id.toLowerCase().includes(k))) {
    score += 0.1;
  }

  return Math.min(1, score);
}

/**
 * Resolves the best-scoring candidate to an actual Playwright `Locator` via a typed
 * switch — never string-building/`eval`/`new Function` (forbidden: `no-new-func`).
 */
function resolveCandidate(page: Page, candidate: Candidate): Locator {
  if (candidate.role && candidate.name) {
    return page.getByRole(candidate.role as never, { name: candidate.name });
  }
  if (candidate.dataTestId) {
    return page.getByTestId(candidate.dataTestId);
  }
  if (candidate.ariaLabel) {
    return page.getByLabel(candidate.ariaLabel);
  }
  if (candidate.text && ['button', 'a', 'label', 'span'].includes(candidate.tag)) {
    return page.getByText(candidate.text);
  }
  if (candidate.id) {
    return page.locator(`#${candidate.id}`);
  }
  if (candidate.name) {
    return page.locator(`[name="${candidate.name}"]`);
  }
  const firstClass = candidate.class?.split(/\s+/).find(Boolean);
  if (firstClass) {
    return page.locator(`.${firstClass}`);
  }
  return page.locator(candidate.tag);
}

function loadCache(): Record<string, CachedHeal> {
  try {
    if (!fs.existsSync(CACHE_FILE)) return {};
    return fs.readJsonSync(CACHE_FILE) as Record<string, CachedHeal>;
  } catch {
    return {};
  }
}

function saveCache(cache: Record<string, CachedHeal>): void {
  try {
    fs.writeJsonSync(CACHE_FILE, cache, { spaces: 2 });
  } catch {
    // swallow cache-write failures so healing still proceeds
  }
}

function getCacheKey(pageUrl: string, hint: string): string {
  return `${pageUrl}|${hint}`;
}

function appendLog(hint: string, confidence: number, error: Error): void {
  try {
    fs.appendFileSync(
      LOG_FILE,
      `[${new Date().toISOString()}] HEALED: hint="${hint}" (confidence: ${confidence}, original error: ${
        error.message.split('\n')[0]
      })\n`,
    );
  } catch {
    // ignore report file failures
  }
}

async function collectCandidates(page: Page): Promise<Candidate[]> {
  // Single-argument page.evaluate() — Playwright's evaluate() throws "Too many arguments"
  // on more than one positional argument after the page function. Bundle everything into
  // one object.
  return page.evaluate(
    ({ selectors, limit }) => {
      const elements = Array.from(document.querySelectorAll(selectors.join(','))).slice(0, limit);
      return elements.map((el) => ({
        tag: el.tagName.toLowerCase(),
        id: el.id || '',
        class: el.className || '',
        name: el.getAttribute('name') || '',
        role: el.getAttribute('role') || '',
        ariaLabel: el.getAttribute('aria-label') || '',
        dataTestId: el.getAttribute('data-testid') || '',
        placeholder: el.getAttribute('placeholder') || '',
        type: el.getAttribute('type') || '',
        forAttr: el.getAttribute('for') || '',
        href: el.getAttribute('href') || '',
        text: (el.textContent || '').trim().replace(/\s+/g, ' '),
      }));
    },
    { selectors: INTERACTIVE_SELECTORS, limit: MAX_DOM_SNAPSHOT_ELEMENTS },
  );
}

/**
 * Attempts to find a healed replacement `Locator` for an action that just failed.
 *
 * The `hint` string (e.g. "Login Button") is the only keyword source — there is no raw
 * locator string to tokenize in this stack's getter-based locator convention.
 */
export async function findHealedLocator(page: Page, hint: string, error: Error): Promise<HealResult> {
  const pageUrl = page.url();
  const cacheKey = getCacheKey(pageUrl, hint);
  const cache = loadCache();
  const cached = cache[cacheKey];
  if (cached && Date.now() - cached.timestamp < CACHE_TTL_MS) {
    const cachedLocator = resolveCandidate(page, cached.candidate);
    if ((await cachedLocator.count()) > 0) {
      return { success: true, locator: cachedLocator, confidence: cached.confidence };
    }
    // Cached candidate no longer resolves (DOM changed since caching) — fall through to
    // fresh detection below rather than returning a stale, invalid heal.
  }

  const hintKeywords = uniqueWords(normalizeWords(hint));
  const candidates = await collectCandidates(page);

  const scored: ScoredCandidate[] = candidates
    .map((candidate) => ({ candidate, score: scoreCandidate(candidate, hintKeywords) }))
    .filter((entry) => entry.score > 0)
    .sort((a, b) => b.score - a.score);

  const best = scored[0];
  if (!best || best.score < CONFIDENCE_THRESHOLD) {
    return { success: false, locator: null, confidence: best ? best.score : 0 };
  }

  const locator = resolveCandidate(page, best.candidate);
  const isValid = (await locator.count()) > 0;
  if (!isValid) {
    return { success: false, locator: null, confidence: best.score };
  }

  const cacheEntry: CachedHeal = { candidate: best.candidate, confidence: best.score, timestamp: Date.now() };
  cache[cacheKey] = cacheEntry;
  saveCache(cache);
  appendLog(hint, best.score, error);

  return { success: true, locator, confidence: best.score };
}
