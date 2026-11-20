package com.cresanmanagement.eyeq.testng;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.utils.BatteryGuard;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import com.cresanmanagement.eyeq.utils.DeviceConfig;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import com.cresanmanagement.eyeq.utils.EnvironmentSetup;
import com.cresanmanagement.eyeq.utils.InitiateDriver;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGMethod;

import java.util.Map;

/**
 * Suite-level lifecycle hook.
 *
 * <p><b>REDESIGNED 2026-10-20 for suite-level session reuse</b> (replacing the earlier one-Appium-session-
 * per-{@code @Test}-method design): the app now launches, switches to the Test backend, and (for every
 * module except Onboarding &amp; Authentication's own login-flow tests) logs in ONCE here, at suite start -
 * not once per test. Every test in the suite then reuses this single, already-authenticated session,
 * navigating directly to whatever screen it needs (see {@code SiteManagementPage#ensureSiteSelected} for
 * the now-standard "reach a site regardless of current screen" helper every module's tests should use
 * instead of calling {@code OnboardingAuthenticationPage#login} themselves).
 *
 * <p><b>Why this changed:</b> the old per-test design called a THEN-standard {@code
 * resetToCleanLoginOnTestBackend()} helper (copy-pasted into every module's Tests class) that directly and
 * UNCONDITIONALLY called {@code clearAppDataAndRelaunch()} + {@code switchEnvironmentToTest()} in every
 * single test's {@code finally} block - bypassing {@link EnvironmentSetup#ensureTestEnvironment}'s own
 * once-per-JVM guard entirely. When that direct call hit a real UiAutomator2 exception
 * ("Cannot set AccessibilityNodeInfo's field 'mSealed'...") partway through the devmode-switch UI flow
 * (AFTER app data was already cleared, BEFORE the Test-backend switch completed), the device was left
 * relaunched with no devmode override - silently defaulting to PRODUCTION - and nothing in that JVM would
 * ever re-check or re-fix it (the guard had already latched "done" from an earlier, successful session).
 * This is also why this design is simply faster: ~24 full clear/relaunch/devmode-switch cycles per suite
 * run collapses to one.
 *
 * <p>Owns the mandatory pre-suite environment setup (devmode/QA environment-switch trigger, FR-AUTH-011 -
 * see {@link EnvironmentSetup}), which must run exactly once, before any test logs in normally. Kept
 * separate from {@code MobileEmailReport} (which owns the Extent report init/flush + HTML summary) so this
 * suite-level concern doesn't get tangled with reporting.
 */
public class SuiteCreationListener implements ISuiteListener {

    private static final Logger logger = LogManager.getLogger(SuiteCreationListener.class.getName());
    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";

    /**
     * Onboarding & Authentication's own tests exercise the login flow itself as their actual test
     * subject - they must each start from a logged-OUT Login screen and perform their own login (or
     * login-variant) as part of the test body. Every other module's tests assume an already-logged-in
     * shared session. Detected by test class package rather than a per-class annotation, since every test
     * in that one package is, by its own naming/purpose, a login-flow test (verified 2026-10-20: all ~30
     * methods in OnboardingAuthenticationTests are named with a "verifyLogin", "verifySplash",
     * "verifyPassword", or "verifyRemembered" prefix, etc.).
     */
    private static final String LOGIN_FLOW_TEST_PACKAGE = "com.cresanmanagement.eyeq.onboardingauthenticationtests";

    @Override
    public void onStart(ISuite suite) {
        logger.info("Suite started: " + suite.getName());
        // Real-device safety check (see utils/BatteryGuard) BEFORE anything else opens a driver session -
        // no point starting a run at all if the device is already critical.
        BatteryGuard.checkOrAbort(DeviceConfig.getInstance().getDeviceID("ANDROID_APP"));

        // Fail-fast pre-check retained: EnvironmentSetup.runOnce() opens a short-lived throwaway driver
        // (immediately closed) purely to confirm/perform the devmode->Test switch BEFORE the real,
        // long-lived suite driver below is created - if the switch is going to fail, fail here clearly
        // rather than inside the shared session this whole suite depends on. Idempotent (once per JVM),
        // so if createDriver() below performs the real switch itself, this becomes a harmless no-op.
        EnvironmentSetup.runOnce();

        logger.info("Creating the ONE shared Appium driver session for this entire suite run...");
        AppiumDriver driver = InitiateDriver.getInstance().createDriver();
        DriverFactory.setDriverForFramework(driver);

        // Safety net added 2026-10-27 after a confirmed real incident: a negative/network-failure test
        // (e.g. GlobalTests' airplane-mode-based TC_FUNC_GLB_006/019/024/038) enables airplane mode and
        // restores it in its own finally block, but an abruptly-interrupted run (JVM/process killed, or the
        // Appium session torn down, between setAirplaneMode(true) and that finally block) leaves the real
        // device permanently stranded offline for every suite run afterward - confirmed live: found the
        // device stuck on the app's own "NoInternetActivity" screen with airplane mode still on, with no
        // test currently running. Unconditionally forcing it off here, before anything else, makes every
        // fresh suite run self-healing regardless of how the previous run ended.
        Pages.OnboardingAuthenticationPage().setAirplaneMode(false);

        if (isLoginFlowSuite(suite)) {
            logger.info("Detected an Onboarding & Authentication login-flow suite - leaving the shared "
                    + "session on the logged-out Login screen; each test performs its own login.");
            return;
        }

        logger.info("Non-auth module suite - logging in ONCE for the whole suite to share.");
        Map<String, String> qaUser = CSVLoader.loadRowByKey(ENV_CSV, "QaUser");
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        authPage.login(qaUser.get("Username"), qaUser.get("Password"));
        if (!authPage.isSiteSelectionScreenDisplayed()) {
            throw new IllegalStateException(
                    "Suite-level login did not land on Site Selection as expected - refusing to start the "
                            + "suite with an unconfirmed session state.");
        }
        logger.info("Suite-level login confirmed (Site Selection reached) - shared session ready.");
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.info("Suite finished: " + suite.getName() + " - quitting the one shared Appium driver session.");
        InitiateDriver.getInstance().quitDriver();
        DriverFactory.removeDriverForFramework();
    }

    private boolean isLoginFlowSuite(ISuite suite) {
        for (ITestNGMethod method : suite.getAllMethods()) {
            if (method.getRealClass().getPackageName().equals(LOGIN_FLOW_TEST_PACKAGE)) {
                return true;
            }
        }
        return false;
    }
}
