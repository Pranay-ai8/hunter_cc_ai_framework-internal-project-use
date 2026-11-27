package com.codecraft.saucedemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Rule-based self-healing for stale/renamed Playwright locators.
 *
 * Per 03-standards/playwright-java-standards.md "Self-Healing Locators": this stack has a
 * structural advantage over the JavaScript/TypeScript stacks — every locator is already
 * constructed via {@code $("Descriptive Name", page.<locatorMethod>(...))}, so the human
 * readable hint healing needs is already captured at declaration time. There is therefore
 * a SINGLE hint-based keyword set here (no separate "locator string" keyword pass, unlike
 * the JavaScript port at src/helper/self-healer.js in the JS/Cucumber stack, which has no
 * equivalent hint-capture point and must derive keywords from the raw locator string too).
 *
 * Algorithm (identical scoring model to the JavaScript reference implementation):
 * 1. Tokenize the hint into a lower-case word SET (>= 3 chars, deduped).
 * 2. Snapshot interactive DOM candidates via a single-argument {@code page.evaluate(String, Object)}
 *    call — Playwright Java throws if you pass more than one positional argument, so every
 *    value the evaluated function needs is bundled into one Map.
 * 3. Score each candidate by precision/recall (F1) over id/name/role/aria-label/data-testid/
 *    placeholder/for/text — deliberately EXCLUDING {@code class} (styling noise, not semantic
 *    identity; including it dilutes precision for otherwise-correct candidates).
 * 4. Resolve the best candidate (score >= CONFIDENCE_THRESHOLD) directly to a real
 *    {@code com.microsoft.playwright.Locator} via an explicit if/else chain
 *    (getByRole -> getByTestId -> getByLabel -> getByText -> CSS fallback) — never a string
 *    to be parsed/evaluated later.
 * 5. Validate the resolved locator actually matches something ({@code locator.count() > 0}).
 * 6. Cache successful heals for CACHE_TTL_MS in healing-cache.json at the project root.
 * 7. Log every successful heal to healing-report.log.
 */
public final class SelfHealer {

    private static final Logger log = LogManager.getLogger(SelfHealer.class);

    private static final long CACHE_TTL_MS = 60 * 60 * 1000L; // 1 hour
    private static final double CONFIDENCE_THRESHOLD = 0.75;
    private static final String[] INTERACTIVE_SELECTORS = {
            "button", "a", "input", "select", "textarea", "[role]", "[data-testid]", "label"
    };
    private static final int MAX_DOM_SNAPSHOT_ELEMENTS = 150;
    private static final Set<String> TEXT_ELIGIBLE_TAGS = Set.of("button", "a", "label", "span");

    /**
     * healing-cache.json / healing-report.log live at the project root — the shared ancestor
     * of Framework/PageObjects/Scripts in this Mode A split layout — resolved from the JVM's
     * working directory (Maven surefire forks with CWD = the module basedir, i.e. Framework/),
     * one level up.
     */
    private static final Path PROJECT_ROOT = resolveProjectRoot();
    private static final Path CACHE_FILE = PROJECT_ROOT.resolve("healing-cache.json");
    private static final Path LOG_FILE = PROJECT_ROOT.resolve("healing-report.log");

    private static final Object CACHE_LOCK = new Object();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String COLLECT_SCRIPT =
            "(args) => {"
                    + "  const elements = Array.from(document.querySelectorAll(args.selectors.join(','))).slice(0, args.limit);"
                    + "  return elements.map((el) => ({"
                    + "    tag: el.tagName.toLowerCase(),"
                    + "    id: el.id || '',"
                    + "    className: el.className || '',"
                    + "    name: el.getAttribute('name') || '',"
                    + "    role: el.getAttribute('role') || '',"
                    + "    ariaLabel: el.getAttribute('aria-label') || '',"
                    + "    dataTestId: el.getAttribute('data-testid') || '',"
                    + "    placeholder: el.getAttribute('placeholder') || '',"
                    + "    type: el.getAttribute('type') || '',"
                    + "    forAttr: el.getAttribute('for') || '',"
                    + "    href: el.getAttribute('href') || '',"
                    + "    text: (el.textContent || '').trim().replace(/\\s+/g, ' ')"
                    + "  }));"
                    + "}";

    private SelfHealer() {
    }

    /** Result of a healing attempt. {@code locator} is a real, resolved Playwright Locator, never a string. */
    public record HealResult(boolean success, Locator locator, double confidence) {
        static HealResult failure(double confidence) {
            return new HealResult(false, null, confidence);
        }
    }

    /** Serializable "recipe" cached to disk so a healed locator can be rebuilt on a cache hit. */
    public static class CacheEntry {
        public String strategy; // ROLE | TEST_ID | LABEL | TEXT | CSS
        public String role;
        public String accessibleName;
        public String value;
        public double confidence;
        public long timestamp;

        public CacheEntry() {
            // required by Jackson for deserialization
        }

        CacheEntry(String strategy, String role, String accessibleName, String value, double confidence, long timestamp) {
            this.strategy = strategy;
            this.role = role;
            this.accessibleName = accessibleName;
            this.value = value;
            this.confidence = confidence;
            this.timestamp = timestamp;
        }
    }

    private static class Candidate {
        String tag = "";
        String id = "";
        String className = "";
        String name = "";
        String role = "";
        String ariaLabel = "";
        String dataTestId = "";
        String placeholder = "";
        String type = "";
        String forAttr = "";
        String href = "";
        String text = "";
    }

    private static class ResolvedLocator {
        Locator locator;
        String strategy;
        String role;
        String accessibleName;
        String value;
    }

    /**
     * Attempts to find a real, on-page replacement for a locator that just failed, using only
     * {@code hint} (the descriptive name already passed to {@code $(name, locator)}) as the
     * healing signal. Returns a failed {@link HealResult} — never throws — so callers can
     * decide whether to rethrow the original exception.
     */
    public static HealResult findHealedLocator(Page page, String hint, Exception originalError) {
        String pageUrl;
        try {
            pageUrl = page.url();
        } catch (Exception ignored) {
            pageUrl = "";
        }
        String cacheKey = pageUrl + "|" + hint;

        CacheEntry cached = getFreshCacheEntry(cacheKey);
        if (cached != null) {
            Locator rebuilt = rebuildLocator(page, cached);
            if (rebuilt != null && safeCount(rebuilt) > 0) {
                log.info("SelfHealer: cache hit for hint [{}] (confidence {})", hint, cached.confidence);
                return new HealResult(true, rebuilt, cached.confidence);
            }
            // Cached recipe no longer resolves to anything real — fall through to fresh detection.
        }

        List<String> hintKeywords = uniqueWords(normalizeWords(hint));
        if (hintKeywords.isEmpty()) {
            return HealResult.failure(0);
        }

        List<Candidate> candidates = collectCandidates(page);

        Candidate bestCandidate = null;
        double bestScore = 0;
        for (Candidate candidate : candidates) {
            double score = scoreCandidate(candidate, hintKeywords);
            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
            }
        }

        if (bestCandidate == null || bestScore < CONFIDENCE_THRESHOLD) {
            log.warn("SelfHealer: no candidate above confidence threshold for hint [{}] (best score {})", hint, bestScore);
            return HealResult.failure(bestScore);
        }

        ResolvedLocator resolved = resolveCandidate(page, bestCandidate);
        if (resolved == null || safeCount(resolved.locator) <= 0) {
            log.warn("SelfHealer: best-scoring candidate for hint [{}] did not resolve to a real element", hint);
            return HealResult.failure(bestScore);
        }

        CacheEntry entry = new CacheEntry(resolved.strategy, resolved.role, resolved.accessibleName,
                resolved.value, bestScore, System.currentTimeMillis());
        putCacheEntry(cacheKey, entry);
        appendLog(hint, bestScore, originalError);

        return new HealResult(true, resolved.locator, bestScore);
    }

    // ---------------------------------------------------------------- tokenization / scoring

    private static List<String> normalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        String cleaned = text.replaceAll("[#\\[\\]()\\{\\}.,/\\\\:'\"?!\\-*_=+<>]", " ").toLowerCase(Locale.ROOT);
        List<String> words = new ArrayList<>();
        for (String word : cleaned.split("\\s+")) {
            if (word.length() >= 3) {
                words.add(word);
            }
        }
        return words;
    }

    private static List<String> uniqueWords(List<String> words) {
        return new ArrayList<>(new LinkedHashSet<>(words));
    }

    /**
     * Word set built ONLY from a candidate's own identity-bearing attributes — deliberately
     * excludes {@code class} (see class-level javadoc, rule 3).
     */
    private static Set<String> candidateWordSet(Candidate candidate) {
        String joined = String.join(" ",
                candidate.id, candidate.name, candidate.role, candidate.ariaLabel,
                candidate.dataTestId, candidate.placeholder, candidate.forAttr, candidate.text);
        return new HashSet<>(normalizeWords(joined));
    }

    /** Precision/recall (F1) scoring — see class-level javadoc, rule 3, for the rationale. */
    private static double scoreCandidate(Candidate candidate, List<String> keywords) {
        if (keywords.isEmpty()) {
            return 0;
        }
        Set<String> words = candidateWordSet(candidate);
        if (words.isEmpty()) {
            return 0;
        }

        List<String> matched = new ArrayList<>();
        for (String keyword : keywords) {
            if (words.contains(keyword)) {
                matched.add(keyword);
            }
        }
        if (matched.isEmpty()) {
            return 0;
        }

        double recall = (double) matched.size() / keywords.size();
        double precision = (double) matched.size() / words.size();
        double score = (2 * recall * precision) / (recall + precision);

        if (!candidate.dataTestId.isEmpty()) {
            String dataTestIdLower = candidate.dataTestId.toLowerCase(Locale.ROOT);
            if (matched.stream().anyMatch(dataTestIdLower::contains)) {
                score += 0.15;
            }
        }
        if (!candidate.id.isEmpty()) {
            String idLower = candidate.id.toLowerCase(Locale.ROOT);
            if (matched.stream().anyMatch(idLower::contains)) {
                score += 0.1;
            }
        }

        return Math.min(1.0, score);
    }

    // ---------------------------------------------------------------- DOM snapshot

    private static List<Candidate> collectCandidates(Page page) {
        Map<String, Object> arg = new HashMap<>();
        arg.put("selectors", Arrays.asList(INTERACTIVE_SELECTORS));
        arg.put("limit", MAX_DOM_SNAPSHOT_ELEMENTS);

        Object raw;
        try {
            // Playwright Java's page.evaluate(String, Object) accepts exactly ONE arg — bundle
            // both values the browser-side function needs into a single Map, never two positional args.
            raw = page.evaluate(COLLECT_SCRIPT, arg);
        } catch (Exception e) {
            log.warn("SelfHealer: DOM snapshot via page.evaluate() failed: {}", e.getMessage());
            return Collections.emptyList();
        }

        List<Candidate> result = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    Candidate candidate = new Candidate();
                    candidate.tag = str(map.get("tag"));
                    candidate.id = str(map.get("id"));
                    candidate.className = str(map.get("className"));
                    candidate.name = str(map.get("name"));
                    candidate.role = str(map.get("role"));
                    candidate.ariaLabel = str(map.get("ariaLabel"));
                    candidate.dataTestId = str(map.get("dataTestId"));
                    candidate.placeholder = str(map.get("placeholder"));
                    candidate.type = str(map.get("type"));
                    candidate.forAttr = str(map.get("forAttr"));
                    candidate.href = str(map.get("href"));
                    candidate.text = str(map.get("text"));
                    result.add(candidate);
                }
            }
        }
        return result;
    }

    private static String str(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    // ---------------------------------------------------------------- resolution (candidate -> real Locator)

    private static ResolvedLocator resolveCandidate(Page page, Candidate candidate) {
        // 1. role + accessible name (HTML `name` attribute doubling as the healing candidate's
        //    identity here) -> getByRole
        if (!candidate.role.isEmpty() && !candidate.name.isEmpty()) {
            try {
                AriaRole ariaRole = AriaRole.valueOf(candidate.role.toUpperCase(Locale.ROOT));
                ResolvedLocator resolved = new ResolvedLocator();
                resolved.locator = page.getByRole(ariaRole, new Page.GetByRoleOptions().setName(candidate.name));
                resolved.strategy = "ROLE";
                resolved.role = candidate.role;
                resolved.accessibleName = candidate.name;
                return resolved;
            } catch (IllegalArgumentException notAValidAriaRole) {
                // fall through to the next strategy
            }
        }
        // 2. data-testid -> getByTestId
        if (!candidate.dataTestId.isEmpty()) {
            ResolvedLocator resolved = new ResolvedLocator();
            resolved.locator = page.getByTestId(candidate.dataTestId);
            resolved.strategy = "TEST_ID";
            resolved.value = candidate.dataTestId;
            return resolved;
        }
        // 3. aria-label -> getByLabel
        if (!candidate.ariaLabel.isEmpty()) {
            ResolvedLocator resolved = new ResolvedLocator();
            resolved.locator = page.getByLabel(candidate.ariaLabel);
            resolved.strategy = "LABEL";
            resolved.value = candidate.ariaLabel;
            return resolved;
        }
        // 4. visible text, restricted to tags where text is normally the accessible identity -> getByText
        if (!candidate.text.isEmpty() && TEXT_ELIGIBLE_TAGS.contains(candidate.tag)) {
            ResolvedLocator resolved = new ResolvedLocator();
            resolved.locator = page.getByText(candidate.text);
            resolved.strategy = "TEXT";
            resolved.value = candidate.text;
            return resolved;
        }
        // 5. CSS fallback: #id -> [name=...] -> [data-testid=...] -> .firstClass -> bare tag
        String css = buildCssFallback(candidate);
        if (css != null) {
            ResolvedLocator resolved = new ResolvedLocator();
            resolved.locator = page.locator(css);
            resolved.strategy = "CSS";
            resolved.value = css;
            return resolved;
        }
        return null;
    }

    private static String buildCssFallback(Candidate candidate) {
        if (!candidate.id.isEmpty()) {
            return "#" + candidate.id;
        }
        if (!candidate.name.isEmpty()) {
            return "[name=\"" + candidate.name + "\"]";
        }
        if (!candidate.dataTestId.isEmpty()) {
            return "[data-testid=\"" + candidate.dataTestId + "\"]";
        }
        if (!candidate.className.isEmpty()) {
            String firstClass = candidate.className.trim().split("\\s+")[0];
            if (!firstClass.isEmpty()) {
                return "." + firstClass;
            }
        }
        if (!candidate.tag.isEmpty()) {
            return candidate.tag;
        }
        return null;
    }

    private static Locator rebuildLocator(Page page, CacheEntry entry) {
        try {
            switch (entry.strategy) {
                case "ROLE":
                    AriaRole ariaRole = AriaRole.valueOf(entry.role.toUpperCase(Locale.ROOT));
                    return page.getByRole(ariaRole, new Page.GetByRoleOptions().setName(entry.accessibleName));
                case "TEST_ID":
                    return page.getByTestId(entry.value);
                case "LABEL":
                    return page.getByLabel(entry.value);
                case "TEXT":
                    return page.getByText(entry.value);
                case "CSS":
                    return page.locator(entry.value);
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static long safeCount(Locator locator) {
        try {
            return locator.count();
        } catch (Exception e) {
            return 0;
        }
    }

    // ---------------------------------------------------------------- cache + log persistence

    private static CacheEntry getFreshCacheEntry(String key) {
        synchronized (CACHE_LOCK) {
            Map<String, CacheEntry> cache = loadCache();
            CacheEntry entry = cache.get(key);
            if (entry != null && (System.currentTimeMillis() - entry.timestamp) < CACHE_TTL_MS) {
                return entry;
            }
            return null;
        }
    }

    private static void putCacheEntry(String key, CacheEntry entry) {
        synchronized (CACHE_LOCK) {
            Map<String, CacheEntry> cache = loadCache();
            cache.put(key, entry);
            saveCache(cache);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, CacheEntry> loadCache() {
        try {
            File file = CACHE_FILE.toFile();
            if (!file.exists()) {
                return new HashMap<>();
            }
            return MAPPER.readValue(file,
                    MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, CacheEntry.class));
        } catch (Exception e) {
            log.warn("SelfHealer: failed to read healing-cache.json, starting fresh: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private static void saveCache(Map<String, CacheEntry> cache) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(CACHE_FILE.toFile(), cache);
        } catch (IOException e) {
            log.warn("SelfHealer: failed to persist healing-cache.json: {}", e.getMessage());
        }
    }

    private static void appendLog(String hint, double score, Exception originalError) {
        String firstLine;
        if (originalError == null || originalError.getMessage() == null) {
            firstLine = "n/a";
        } else {
            firstLine = originalError.getMessage().split("\\R", 2)[0];
        }
        String line = String.format(Locale.ROOT, "[%s] HEALED: hint=\"%s\" confidence=%.2f originalError=\"%s\"%n",
                Instant.now(), hint, score, firstLine);
        synchronized (CACHE_LOCK) {
            try {
                Files.writeString(LOG_FILE, line, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.warn("SelfHealer: failed to append healing-report.log: {}", e.getMessage());
            }
        }
    }

    private static Path resolveProjectRoot() {
        // Maven surefire forks its JVM with the working directory set to the module basedir
        // (Framework/); one level up is the shared ancestor of Framework/PageObjects/Scripts.
        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path parent = cwd.getParent();
        return parent != null ? parent : cwd;
    }
}
