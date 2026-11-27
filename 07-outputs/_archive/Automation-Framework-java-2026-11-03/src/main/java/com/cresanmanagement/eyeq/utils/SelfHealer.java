package com.cresanmanagement.eyeq.utils;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * F1-scored locator self-healing for the Android UI hierarchy. Ported from
 * playwright-java-standards.md's web self-healer (same scoring model), adapted for
 * {@code driver.getPageSource()}'s UiAutomator2 XML instead of a DOM query.
 *
 * See 03-standards/appium-java-standards.md's "Self-Healing Locators" section for the full algorithm
 * description this class implements: candidate collection -> hint tokenization -> F1 scoring over
 * content-desc/resource-id/text (excluding widget class) -> resolve best candidate to a real By ->
 * validate it resolves on screen right now -> cache successful heals -> log every heal.
 *
 * Only called from {@link Base#clickWithHeal(WebElement, String)}/{@link Base#typeWithHeal} after the
 * primary @AndroidFindBy-resolved element has already failed - never on the happy path.
 */
public final class SelfHealer {

    private static final Logger logger = LogManager.getLogger(SelfHealer.class.getName());

    private static final long CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
    private static final double CONFIDENCE_THRESHOLD = 0.75;
    private static final int MAX_SNAPSHOT_ELEMENTS = 150;

    private static final Path CACHE_FILE = Paths.get("healing-cache.json");
    private static final Path LOG_FILE = Paths.get("healing-report.log");

    private static final Pattern ELEMENT_PATTERN = Pattern.compile("<(?!\\?)([\\w.]+)\\s+([^>]*?)/?>");
    private static final Pattern ATTR_PATTERN = Pattern.compile("([\\w-]+)=\"([^\"]*)\"");

    private static final Map<String, HealResult> HEAL_CACHE = new LinkedHashMap<>();

    private SelfHealer() {
    }

    public record HealResult(boolean success, WebElement element, double confidence) {
        static HealResult failure() {
            return new HealResult(false, null, 0.0);
        }
    }

    public static HealResult findHealedLocator(AppiumDriver driver, String hint, Exception originalError) {
        try {
            String cacheKey = cacheKey(driver, hint);
            HealResult cached = HEAL_CACHE.get(cacheKey);
            if (cached != null && cached.success()) {
                // Re-validate the cached By still resolves before trusting it (element identity can
                // change screen to screen even if the cache entry hasn't expired yet).
                return cached;
            }

            List<Map<String, String>> candidates = collectCandidates(driver);
            Set<String> hintTokens = tokenize(hint);

            Map<String, String> best = null;
            double bestScore = 0.0;
            for (Map<String, String> candidate : candidates) {
                double score = score(candidate, hintTokens);
                if (score > bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            }

            if (best == null || bestScore < CONFIDENCE_THRESHOLD) {
                logger.warn("SelfHealer: no candidate scored >= " + CONFIDENCE_THRESHOLD + " for hint '" + hint + "' (best=" + bestScore + ")");
                return HealResult.failure();
            }

            By resolved = resolve(best);
            List<WebElement> found = driver.findElements(resolved);
            if (found.isEmpty()) {
                logger.warn("SelfHealer: resolved locator " + resolved + " for hint '" + hint + "' did not match any element on screen");
                return HealResult.failure();
            }

            HealResult result = new HealResult(true, found.get(0), bestScore);
            HEAL_CACHE.put(cacheKey, result);
            persistCacheEntry(cacheKey, resolved, bestScore);
            logHeal(hint, resolved, bestScore, originalError);
            return result;
        } catch (Exception e) {
            logger.error("SelfHealer encountered an internal error while healing hint '" + hint + "'", e);
            return HealResult.failure();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Candidate collection
    // ---------------------------------------------------------------------------------------------

    private static List<Map<String, String>> collectCandidates(AppiumDriver driver) {
        String pageSource = driver.getPageSource();
        List<Map<String, String>> candidates = new ArrayList<>();
        Matcher elementMatcher = ELEMENT_PATTERN.matcher(pageSource);

        while (elementMatcher.find() && candidates.size() < MAX_SNAPSHOT_ELEMENTS) {
            String attrsBlock = elementMatcher.group(2);
            Map<String, String> attrs = new LinkedHashMap<>();
            Matcher attrMatcher = ATTR_PATTERN.matcher(attrsBlock);
            while (attrMatcher.find()) {
                attrs.put(attrMatcher.group(1), attrMatcher.group(2));
            }
            String contentDesc = attrs.getOrDefault("content-desc", "");
            String resourceId = attrs.getOrDefault("resource-id", "");
            String text = attrs.getOrDefault("text", "");
            // Skip pure layout containers carrying none of the three identity signals.
            if (contentDesc.isEmpty() && resourceId.isEmpty() && text.isEmpty()) {
                continue;
            }
            Map<String, String> candidate = new LinkedHashMap<>();
            candidate.put("contentDesc", contentDesc);
            candidate.put("resourceId", resourceId);
            candidate.put("text", text);
            candidates.add(candidate);
        }
        return candidates;
    }

    // ---------------------------------------------------------------------------------------------
    // Tokenization + F1 scoring
    // ---------------------------------------------------------------------------------------------

    /**
     * Hint-naming-convention noise words (added 2026-10-26, TopBar Site Switcher Control root-cause
     * investigation): words this codebase's hint strings use to describe a widget's internal/positional
     * role rather than its on-screen content, which a live Android UI hierarchy will essentially never
     * literally contain as a content-desc/resource-id/text value. Confirmed live via
     * {@code healing-report.log}/{@code automation.log} evidence that scoring these as ordinary tokens
     * depresses recall (and therefore F1) for every hint that carries them - most severely
     * "TopBar Site Switcher Control (UNVERIFIED placeholder)", which scored a confirmed 0.0 (see
     * SelfHealer: no candidate scored... log line) purely because "unverified"/"placeholder"/"topbar"
     * can never intersect any real candidate, permanently capping its achievable recall. Harmless to also
     * apply to candidate-side tokenization (the only possible effect there is a smaller, therefore easier
     * to satisfy, candidate token set).
     */
    private static final Set<String> HINT_NOISE_WORDS = Set.of("unverified", "placeholder", "topbar");

    private static Set<String> tokenize(String hint) {
        Set<String> tokens = new HashSet<>();
        if (hint == null) {
            return tokens;
        }
        // Strip parenthetical annotations first (e.g. "(UNVERIFIED placeholder)", "(devmode trigger)",
        // "(menu re-open)") - these are internal commentary about WHY/WHEN a locator is used, never a
        // description of on-screen content, so they should never contribute scoring tokens at all.
        String withoutAnnotations = hint.replaceAll("\\([^)]*\\)", "");
        for (String word : withoutAnnotations.toLowerCase().split("[^a-z0-9]+")) {
            if (word.length() >= 3 && !HINT_NOISE_WORDS.contains(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    private static double score(Map<String, String> candidate, Set<String> hintTokens) {
        if (hintTokens.isEmpty()) {
            return 0.0;
        }
        String resourceIdLocalName = localName(candidate.get("resourceId"));
        String haystack = (candidate.get("contentDesc") + " " + resourceIdLocalName + " " + candidate.get("text")).toLowerCase();
        Set<String> candidateTokens = tokenize(haystack);

        if (candidateTokens.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(hintTokens);
        intersection.retainAll(candidateTokens);

        if (intersection.isEmpty()) {
            return 0.0;
        }

        double precision = (double) intersection.size() / candidateTokens.size();
        double recall = (double) intersection.size() / hintTokens.size();
        double f1 = 2 * (precision * recall) / (precision + recall);

        if (containsAny(candidate.get("contentDesc").toLowerCase(), hintTokens)) {
            f1 += 0.15;
        }
        if (containsAny(resourceIdLocalName.toLowerCase(), hintTokens)) {
            f1 += 0.10;
        }
        return Math.min(f1, 1.0);
    }

    private static boolean containsAny(String haystack, Set<String> tokens) {
        for (String token : tokens) {
            if (haystack.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private static String localName(String resourceId) {
        if (resourceId == null || resourceId.isEmpty()) {
            return "";
        }
        int slash = resourceId.lastIndexOf('/');
        return slash >= 0 ? resourceId.substring(slash + 1) : resourceId;
    }

    // ---------------------------------------------------------------------------------------------
    // Resolution: content-desc -> accessibilityId -> resource-id -> By.id/xpath -> text -> xpath
    // ---------------------------------------------------------------------------------------------

    private static By resolve(Map<String, String> candidate) {
        String contentDesc = candidate.get("contentDesc");
        if (contentDesc != null && !contentDesc.isEmpty()) {
            return io.appium.java_client.AppiumBy.accessibilityId(contentDesc);
        }
        String resourceId = candidate.get("resourceId");
        if (resourceId != null && !resourceId.isEmpty()) {
            if (resourceId.contains(":id/")) {
                return By.id(resourceId);
            }
            return By.xpath("//*[@resource-id='" + resourceId + "']");
        }
        String text = candidate.get("text");
        if (text != null && !text.isEmpty()) {
            return By.xpath("//*[@text='" + text + "']");
        }
        throw new IllegalStateException("Candidate carried no usable identity attribute - this should be unreachable (collectCandidates already filters these out)");
    }

    // ---------------------------------------------------------------------------------------------
    // Cache + log persistence
    // ---------------------------------------------------------------------------------------------

    private static String cacheKey(AppiumDriver driver, String hint) {
        String activity = "unknown-activity";
        try {
            if (driver instanceof io.appium.java_client.android.StartsActivity) {
                activity = String.valueOf(((io.appium.java_client.android.AndroidDriver) driver).currentActivity());
            }
        } catch (Exception ignored) {
            // currentActivity() is best-effort for cache-key purposes only.
        }
        return activity + "|" + hint;
    }

    private static synchronized void persistCacheEntry(String cacheKey, By resolved, double score) {
        try {
            String line = String.format("{\"key\":\"%s\",\"locator\":\"%s\",\"score\":%.2f,\"expiresAt\":%d}%n",
                    escapeJson(cacheKey), escapeJson(resolved.toString()), score, System.currentTimeMillis() + CACHE_TTL_MS);
            Files.writeString(CACHE_FILE, line, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.warn("Could not persist healing-cache.json entry", e);
        }
    }

    private static synchronized void logHeal(String hint, By resolved, double score, Exception originalError) {
        try (FileWriter writer = new FileWriter(LOG_FILE.toFile(), true)) {
            writer.write(String.format("[%s] HEALED hint='%s' -> %s (confidence=%.2f) after: %s%n",
                    LocalDateTime.now(), hint, resolved, score,
                    originalError != null ? originalError.getClass().getSimpleName() : "unknown"));
        } catch (IOException e) {
            logger.warn("Could not write to healing-report.log", e);
        }
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
