package com.cresanmanagement.eyeq.utils;

import com.cresanmanagement.eyeq.Pages;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Mandatory environment setup: switches the app's backend API base URL to the Test environment via the
 * hidden devmode/QA trigger (FR-AUTH-011 - confirmed by the user as an intentional, access-controlled QA
 * tool, not a leftover) before any test or exploratory session logs in normally.
 *
 * <p><b>STRUCTURAL FIX (2026-10-19, after a SECOND real production incident):</b> the real enforcement
 * point for this is now {@link InitiateDriver#createDriver()} itself, which calls
 * {@link #ensureTestEnvironment(AppiumDriver)} on every driver it creates, BEFORE handing that driver back
 * to ANY caller. This class's own {@link #runOnce()} (still called from
 * {@code testng.SuiteCreationListener#onStart} and {@code framework.TestBase#setUp} for explicit,
 * fail-fast-at-the-right-moment semantics) is now a thin, redundant-but-harmless wrapper around that same
 * enforcement - seet this class's own incident history below for why a test-lifecycle-only fix (the FIRST
 * incident's fix) was not enough.
 *
 * <p>Reads {@code src/test/resources/testdata/environmentsetup.csv} directly via {@link CSVLoader} rather
 * than the normal {@code @TestDataSource}-on-a-@Test-method injection lifecycle, since this can now run
 * from inside driver creation itself, not just from a TestNG-managed method.
 *
 * <p><b>INCIDENT HISTORY:</b>
 * <ul>
 *   <li><b>Incident 1 (2026-10-19, earlier):</b> a targeted single-method run
 *   ({@code mvn test -Dtest=ClassName#methodName}) was found to never invoke
 *   {@code testng.SuiteCreationListener#onStart} at all, so the mandatory switch never ran and a test
 *   logged into PRODUCTION. Fixed at the time by also calling {@link #runOnce()} from
 *   {@code TestBase#setUp} - but that fix only covers the formal TestNG test-execution lifecycle.</li>
 *   <li><b>Incident 2 (2026-10-19, same day):</b> a RAW, non-TestNG exploratory session (a throwaway
 *   {@code public static void main} crawl script, used for this project's "Live UI Hierarchy
 *   Verification" methodology on new modules) called {@code InitiateDriver.getInstance().createDriver()}
 *   directly - bypassing BOTH {@code SuiteCreationListener.onStart} AND {@code TestBase.setUp} entirely,
 *   since neither is a TestNG-managed entry point for a plain {@code main()}. That session logged into
 *   PRODUCTION again (confirmed via logcat: every call went to {@code api.eyeqmonitoring.com}), for the
 *   exact same underlying reason as Incident 1 just at a different call site. This proved relying on
 *   test-LIFECYCLE hooks can never be complete, since manual/exploratory sessions - an explicit, intended
 *   part of this project's workflow for crawling new modules - don't go through any TestNG lifecycle at
 *   all. The only call site EVERY session-creation path is guaranteed to share is
 *   {@code InitiateDriver.createDriver()} itself, so the enforcement now lives there.</li>
 * </ul>
 */
public final class EnvironmentSetup {

    private static final Logger logger = LogManager.getLogger(EnvironmentSetup.class.getName());
    private static final String CSV_PATH = "src/test/resources/testdata/environmentsetup.csv";

    /**
     * Guards against running the full devmode-switch sequence more than once per JVM - checked by
     * {@link InitiateDriver#createDriver()} on EVERY driver it creates (test-driven or manual/exploratory)
     * so the very first one performs the real switch and every subsequent one, in the same JVM, is a
     * cheap no-op rather than needlessly repeating the whole devmode/Set-Base-URL/OK sequence.
     */
    private static final java.util.concurrent.atomic.AtomicBoolean hasRun = new java.util.concurrent.atomic.AtomicBoolean(false);

    private EnvironmentSetup() {
    }

    /**
     * Explicit, standalone entry point retained for {@code testng.SuiteCreationListener#onStart} and
     * {@code framework.TestBase#setUp} - opens a short-lived driver (which, via
     * {@link InitiateDriver#createDriver()}, performs the real check/switch internally if not already
     * done this JVM) and immediately closes it again. Safe to call redundantly from multiple places; only
     * ever does real work once per JVM (see {@link #hasRun}).
     */
    public static void runOnce() {
        if (hasRun.get()) {
            logger.debug("EnvironmentSetup already confirmed earlier in this JVM - runOnce() is a no-op.");
            return;
        }
        AppiumDriver driver = InitiateDriver.getInstance().createDriver();
        DriverFactory.setDriverForFramework(driver);
        InitiateDriver.getInstance().quitDriver();
        DriverFactory.removeDriverForFramework();
    }

    /**
     * Performs the full devmode -> Test-backend switch sequence on the GIVEN, already-created driver -
     * but only the FIRST time this is called within a given JVM (subsequent calls are no-ops; see
     * {@link #hasRun}). Called by {@link InitiateDriver#createDriver()} on every driver it creates, BEFORE
     * that driver is returned to any caller - see this class's own javadoc for why this is the structural
     * fix, not the test-lifecycle-hook fix from the first incident.
     *
     * <p>Does NOT create or quit the driver itself - the caller (ultimately
     * {@code InitiateDriver.createDriver()}) owns that driver's full lifecycle; this method only uses it
     * (via {@code DriverFactory.getDriverForFramework()}, which must already be set to it) to perform the
     * UI-driven switch, then leaves it exactly as it found it (on a blank Login screen) for the caller to
     * hand back to whoever asked for a session.
     */
    public static void ensureTestEnvironment(AppiumDriver driver) {
        if (!hasRun.compareAndSet(false, true)) {
            logger.debug("EnvironmentSetup already confirmed earlier in this JVM - skipping (idempotent).");
            return;
        }
        logger.info("Running mandatory environment setup (devmode -> Test backend switch)...");

        if (!new java.io.File(CSV_PATH).exists()) {
            hasRun.set(false);
            throw new IllegalStateException(
                    "Cannot run EnvironmentSetup: " + CSV_PATH + " does not exist. This file holds real "
                            + "test-environment credentials and is deliberately gitignored (never committed) - "
                            + "each developer/CI secret-injection step must create it locally. See ReadMe.md's "
                            + "\"Environment Setup\" section for the exact column format to recreate it.");
        }

        Map<String, String> devModeTrigger = CSVLoader.loadRowByKey(CSV_PATH, "DevModeTrigger");
        String appPackage = PropertyReader.getInstance().getPropValues("config.properties", "AndroidAppPackage");

        try {
            // Retried up to twice: clearAppData()+relaunchApp() immediately followed by interacting with
            // the resulting Login screen was found live to occasionally race a Compose UI recomposition
            // pass right after relaunch, surfacing as a StaleElementReferenceException on the email field
            // (confirmed 2026-10-19) - a transient timing issue, not a locator/logic bug. A short
            // confirmed-visible wait before interacting, plus one retry of the whole clear-relaunch-switch
            // sequence if it still races, resolves it without masking a genuine failure (the final attempt
            // still throws/fails loudly as before).
            Exception lastError = null;
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    // Force a known-blank Login screen FIRST, regardless of whatever state a prior session
                    // (this test suite's own, or a manual/exploratory one sharing the same device under
                    // NoReset=true) left the app in - see this class's incident history above. The devmode
                    // trigger itself is not a real account, so clearing app data here never risks losing
                    // real session data.
                    Pages.OnboardingAuthenticationPage().clearAppData(appPackage);
                    Pages.OnboardingAuthenticationPage().relaunchApp(appPackage);
                    // Let the freshly-relaunched Login screen settle (a real, bounded element-visibility
                    // wait via isLoginScreenDisplayed() - not a fixed sleep) before interacting with it.
                    Pages.OnboardingAuthenticationPage().isLoginScreenDisplayed();

                    Pages.OnboardingAuthenticationPage().switchEnvironmentToTest(
                            devModeTrigger.get("Username"), devModeTrigger.get("Password"));

                    // Fail loudly rather than silently trust the switch happened: re-confirm the app
                    // actually landed back on a blank Login screen afterward (switchEnvironmentToTest()'s
                    // own documented postcondition) before declaring success.
                    if (!Pages.OnboardingAuthenticationPage().isLoginScreenDisplayed()) {
                        throw new IllegalStateException(
                                "switchEnvironmentToTest() returned without throwing, but the app is not "
                                        + "showing a blank Login screen afterward as expected - refusing to "
                                        + "trust the Test-backend switch actually completed.");
                    }
                    logger.info("Environment setup complete - backend switched to Test (verified: Login screen confirmed afterward).");
                    return;
                } catch (org.openqa.selenium.StaleElementReferenceException staleEx) {
                    lastError = staleEx;
                    logger.warn("Environment setup attempt " + attempt + " hit a transient StaleElementReferenceException "
                            + "(known Compose-recomposition race right after relaunch) - retrying.", staleEx);
                }
            }
            throw lastError;
        } catch (Exception e) {
            hasRun.set(false);
            logger.error("Environment setup FAILED - the devmode/Test-backend switch did not complete. "
                    + "Every subsequent session's login will likely fail against (or silently run against) "
                    + "the wrong backend.", e);
            throw new RuntimeException("Mandatory environment setup failed", e);
        }
    }
}
