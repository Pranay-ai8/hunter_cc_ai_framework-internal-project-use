package com.codecraft.wdiodemoapp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule-based self-healing for stale/renamed Appium locators — ported from this framework's
 * Playwright-Java {@code SelfHealer} (see {@code 03-standards/playwright-java-standards.md}
 * "Self-Healing Locators" and {@code 07-outputs/_archive/Automation-java-2026-08-05/.../SelfHealer.java}),
 * adapted for the Android UI hierarchy instead of the DOM.
 *
 * <p>Same algorithm as the web reference implementations (identical scoring model — see below),
 * with Android-specific candidate collection and locator resolution:
 * <ol>
 *   <li>Snapshot candidate elements from {@code driver.getPageSource()} (the UiAutomator2 XML
 *       hierarchy) instead of a browser DOM query — this is the Appium equivalent of a DOM
 *       snapshot and requires no extra round-trip beyond what Appium already exposes.</li>
 *   <li>Score each candidate by precision/recall (F1) over {@code content-desc}/{@code resource-id}/
 *       {@code text} against a hint's keyword set — deliberately EXCLUDING {@code class} (the
 *       Android widget type, e.g. {@code android.widget.Button}, is structural noise, not semantic
 *       identity, exactly like excluding CSS {@code class} on the web side).</li>
 *   <li>Resolve the best candidate (score &gt;= CONFIDENCE_THRESHOLD) to a real {@code By}:
 *       {@code content-desc} -&gt; {@code AppiumBy.accessibilityId} (strongest signal, analogous to
 *       data-testid/aria-label) -&gt; {@code resource-id} -&gt; {@code text} -&gt; bare class-name xpath.
 *       Live-run finding baked in here: a {@code resource-id} with no {@code package:type/} prefix
 *       (a real, observed React Native pattern) breaks {@code By.id(...)}, which silently
 *       auto-prefixes the current app package — so any unprefixed resource-id is resolved via
 *       xpath instead, matching the literal string.</li>
 *   <li>Validate the resolved locator actually matches something on screen right now.</li>
 *   <li>Cache successful heals for CACHE_TTL_MS in {@code healing-cache.json} and log every heal
 *       to {@code healing-report.log}, both at the project root (one level above {@code Framework/},
 *       matching the web-stack convention).</li>
 * </ol>
 */
public final class SelfHealer {

    private static final Logger log = LogManager.getLogger(SelfHealer.class);

    private static final long CACHE_TTL_MS = 60 * 60 * 1000L; // 1 hour
    private static final double CONFIDENCE_THRESHOLD = 0.75;
    private static final int MAX_SNAPSHOT_ELEMENTS = 150;

    private static final Path PROJECT_ROOT = resolveProjectRoot();
    private static final Path CACHE_FILE = PROJECT_ROOT.resolve("healing-cache.json");
    private static final Path LOG_FILE = PROJECT_ROOT.resolve("healing-report.log");

    private static final Object CACHE_LOCK = new Object();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern ELEMENT_PATTERN = Pattern.compile("<([\\w.]+)\\s+([^>]*?)/?>");
    private static final Pattern ATTR_PATTERN = Pattern.compile("(\\S+)=\"([^\"]*)\"");

    private SelfHealer() {
    }

    /** Result of a healing attempt. {@code element} is a real, resolved WebElement, never a string. */
    public record HealResult(boolean success, WebElement element, double confidence) {
        static HealResult failure(double confidence) {
            return new HealResult(false, null, confidence);
        }
    }

    /** Serializable "recipe" cached to disk so a healed locator can be rebuilt on a cache hit. */
    public static class CacheEntry {
        public String strategy; // ACCESSIBILITY_ID | RESOURCE_ID | RESOURCE_ID_XPATH | TEXT | CLASS
        public String value;
        public double confidence;
        public long timestamp;

        public CacheEntry() {
            // required by Jackson for deserialization
        }

        CacheEntry(String strategy, String value, double confidence, long timestamp) {
            this.strategy = strategy;
            this.value = value;
            this.confidence = confidence;
            this.timestamp = timestamp;
        }
    }

    private static class Candidate {
        String tag = "";
        // Android's content-desc and iOS's name both function as the app's own testID-driven
        // accessibility identifier (confirmed live: this app's testID props map 1:1 to both) —
        // unified into one field so scoring/resolution needs no per-platform branching.
        String accessibilityId = "";
        String resourceId = ""; // Android only — no iOS equivalent exists.
        String text = ""; // Android's @text, or iOS's @label/@value folded in at collection time.
        String checkable = "";
        String clickable = "";
    }

    /**
     * Attempts to find a real, on-screen replacement for a locator that just failed, using only
     * {@code hint} (the descriptive name of the element, e.g. "Login Button") as the healing
     * signal. Returns a failed {@link HealResult} — never throws — so callers can decide whether
     * to rethrow the original exception.
     */
    public static HealResult findHealedLocator(AppiumDriver driver, String hint, Exception originalError) {
        String screenKey = safeScreenKey(driver);
        String cacheKey = screenKey + "|" + hint;

        CacheEntry cached = getFreshCacheEntry(cacheKey);
        if (cached != null) {
            WebElement rebuilt = rebuildElement(driver, cached);
            if (rebuilt != null) {
                log.info("SelfHealer: cache hit for hint [{}] (confidence {})", hint, cached.confidence);
                return new HealResult(true, rebuilt, cached.confidence);
            }
            // Cached recipe no longer resolves to anything real — fall through to fresh detection.
        }

        List<String> hintKeywords = uniqueWords(normalizeWords(hint));
        if (hintKeywords.isEmpty()) {
            return HealResult.failure(0);
        }

        List<Candidate> candidates = collectCandidates(driver);

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

        ResolvedLocator resolved = resolveCandidate(bestCandidate);
        WebElement element = resolved == null ? null : safeFind(driver, resolved.by);
        if (element == null) {
            log.warn("SelfHealer: best-scoring candidate for hint [{}] did not resolve to a real element", hint);
            return HealResult.failure(bestScore);
        }

        CacheEntry entry = new CacheEntry(resolved.strategy, resolved.value, bestScore, System.currentTimeMillis());
        putCacheEntry(cacheKey, entry);
        appendLog(hint, bestScore, resolved.strategy, resolved.value, originalError);

        return new HealResult(true, element, bestScore);
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

    /** Word set built ONLY from a candidate's own identity-bearing attributes (see class javadoc). */
    private static Set<String> candidateWordSet(Candidate candidate) {
        String joined = String.join(" ", candidate.accessibilityId, resourceIdLocalName(candidate.resourceId), candidate.text);
        return new HashSet<>(normalizeWords(joined));
    }

    /** The part of a resource-id after the last '/' (and ':id' prefix), e.g. "button-LOGIN" from "com.app:id/button-LOGIN". */
    private static String resourceIdLocalName(String resourceId) {
        if (resourceId == null || resourceId.isEmpty()) {
            return "";
        }
        int slash = resourceId.lastIndexOf('/');
        return slash >= 0 ? resourceId.substring(slash + 1) : resourceId;
    }

    /** Precision/recall (F1) scoring — identical model to the web-stack SelfHealer implementations. */
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

        // Bonus for stable, intentional identifiers directly containing a matched keyword —
        // content-desc/name is this app's data-testid/aria-label equivalent (see Requirement
        // Analysis §12 — "stable test selectors" is the app's own stated design goal).
        if (!candidate.accessibilityId.isEmpty()) {
            String accessibilityIdLower = candidate.accessibilityId.toLowerCase(Locale.ROOT);
            if (matched.stream().anyMatch(accessibilityIdLower::contains)) {
                score += 0.15;
            }
        }
        if (!candidate.resourceId.isEmpty()) {
            String resourceIdLower = resourceIdLocalName(candidate.resourceId).toLowerCase(Locale.ROOT);
            if (matched.stream().anyMatch(resourceIdLower::contains)) {
                score += 0.1;
            }
        }

        return Math.min(1.0, score);
    }

    // ---------------------------------------------------------------- UI hierarchy snapshot

    private static List<Candidate> collectCandidates(AppiumDriver driver) {
        String pageSource;
        try {
            pageSource = driver.getPageSource();
        } catch (Exception e) {
            log.warn("SelfHealer: getPageSource() failed: {}", e.getMessage());
            return Collections.emptyList();
        }

        List<Candidate> result = new ArrayList<>();
        Matcher elementMatcher = ELEMENT_PATTERN.matcher(pageSource);
        while (elementMatcher.find() && result.size() < MAX_SNAPSHOT_ELEMENTS) {
            String tag = elementMatcher.group(1);
            if ("hierarchy".equals(tag)) {
                continue;
            }
            Map<String, String> attrs = parseAttributes(elementMatcher.group(2));

            // Android: content-desc/resource-id/text. iOS: name (the accessibility identifier —
            // confirmed live to carry this app's same testID values as Android's content-desc),
            // and label/value fill the text role since iOS has no @text attribute — label and
            // value are near-always identical for static text on iOS (confirmed live), so prefer
            // one rather than concatenating both (which would break an exact-match xpath rebuild).
            String accessibilityId = attrs.getOrDefault("content-desc", attrs.getOrDefault("name", ""));
            String resourceId = attrs.getOrDefault("resource-id", "");
            String text = !attrs.getOrDefault("text", "").isEmpty() ? attrs.get("text")
                    : !attrs.getOrDefault("label", "").isEmpty() ? attrs.get("label")
                    : attrs.getOrDefault("value", "");
            String clickable = attrs.getOrDefault("clickable", "false");
            String checkable = attrs.getOrDefault("checkable", "false");

            // Only elements carrying at least one identity-bearing attribute are useful
            // healing candidates — skip pure layout containers (ViewGroup/FrameLayout/
            // XCUIElementTypeOther with none of these set), which would only add noise and slow
            // scoring.
            if (accessibilityId.isEmpty() && resourceId.isEmpty() && text.isEmpty()) {
                continue;
            }

            Candidate candidate = new Candidate();
            candidate.tag = tag;
            candidate.accessibilityId = accessibilityId;
            candidate.resourceId = resourceId;
            candidate.text = text;
            candidate.clickable = clickable;
            candidate.checkable = checkable;
            result.add(candidate);
        }
        return result;
    }

    private static Map<String, String> parseAttributes(String rawAttrs) {
        Map<String, String> attrs = new HashMap<>();
        Matcher attrMatcher = ATTR_PATTERN.matcher(rawAttrs);
        while (attrMatcher.find()) {
            attrs.put(attrMatcher.group(1), attrMatcher.group(2));
        }
        return attrs;
    }

    // ---------------------------------------------------------------- resolution (candidate -> real By)

    private static class ResolvedLocator {
        By by;
        String strategy;
        String value;
    }

    private static ResolvedLocator resolveCandidate(Candidate candidate) {
        // 1. content-desc (Android) / name (iOS) -> accessibility id — strongest signal on
        //    either platform, this app's stable-selector convention (confirmed live).
        if (!candidate.accessibilityId.isEmpty()) {
            ResolvedLocator resolved = new ResolvedLocator();
            resolved.by = AppiumBy.accessibilityId(candidate.accessibilityId);
            resolved.strategy = "ACCESSIBILITY_ID";
            resolved.value = candidate.accessibilityId;
            return resolved;
        }
        // 2. resource-id -> By.id if properly namespaced ("pkg:id/name"), else xpath (see class
        //    javadoc — By.id silently mis-prefixes an unprefixed resource-id).
        if (!candidate.resourceId.isEmpty()) {
            ResolvedLocator resolved = new ResolvedLocator();
            if (candidate.resourceId.contains(":")) {
                resolved.by = By.id(candidate.resourceId);
                resolved.strategy = "RESOURCE_ID";
            } else {
                resolved.by = By.xpath("//*[@resource-id='" + candidate.resourceId + "']");
                resolved.strategy = "RESOURCE_ID_XPATH";
            }
            resolved.value = candidate.resourceId;
            return resolved;
        }
        // 3. visible text -> xpath match against whichever attribute the platform actually uses
        //    (@text on Android, @label/@value on iOS — there is no single shared attribute name).
        if (!candidate.text.isEmpty()) {
            ResolvedLocator resolved = new ResolvedLocator();
            resolved.by = textXpath(candidate.text);
            resolved.strategy = "TEXT";
            resolved.value = candidate.text;
            return resolved;
        }
        return null;
    }

    private static By textXpath(String text) {
        return By.xpath("//*[@text='" + text + "' or @label='" + text + "' or @value='" + text + "']");
    }

    private static WebElement rebuildElement(AppiumDriver driver, CacheEntry entry) {
        try {
            By by = switch (entry.strategy) {
                case "ACCESSIBILITY_ID" -> AppiumBy.accessibilityId(entry.value);
                case "RESOURCE_ID" -> By.id(entry.value);
                case "RESOURCE_ID_XPATH" -> By.xpath("//*[@resource-id='" + entry.value + "']");
                case "TEXT" -> textXpath(entry.value);
                default -> null;
            };
            return by == null ? null : safeFind(driver, by);
        } catch (Exception e) {
            return null;
        }
    }

    private static WebElement safeFind(AppiumDriver driver, By by) {
        try {
            List<WebElement> found = driver.findElements(by);
            return found.isEmpty() ? null : found.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private static String safeScreenKey(AppiumDriver driver) {
        try {
            // No stable "URL" equivalent for a native screen — the currently focused activity
            // is the closest analogue to the web SelfHealer's page.url() cache-partition key.
            return ((io.appium.java_client.android.AndroidDriver) driver).currentActivity();
        } catch (Exception e) {
            return "";
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

    private static void appendLog(String hint, double score, String strategy, String value, Exception originalError) {
        String firstLine;
        if (originalError == null || originalError.getMessage() == null) {
            firstLine = "n/a";
        } else {
            firstLine = originalError.getMessage().split("\\R", 2)[0];
        }
        String line = String.format(Locale.ROOT,
                "[%s] HEALED: hint=\"%s\" strategy=%s value=\"%s\" confidence=%.2f originalError=\"%s\"%n",
                Instant.now(), hint, strategy, value, score, firstLine);
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
        // (Framework/); one level up is Automation-Mobile/ — the shared ancestor, matching the
        // web-stack SelfHealer's PROJECT_ROOT convention.
        Path cwd = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        Path parent = cwd.getParent();
        return parent != null ? parent : cwd;
    }
}
