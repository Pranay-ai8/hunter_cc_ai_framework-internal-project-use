package com.cresanmanagement.eyeq.framework;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import com.cresanmanagement.eyeq.utils.InitiateDriver;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.Map;

/**
 * Base class for every test class.
 *
 * <p><b>REDESIGNED 2026-10-20 for suite-level session reuse</b> (replacing the earlier "one Appium
 * session per {@code @Test} method" convention - see git history for that version). The shared session is
 * now normally created ONCE by {@code testng.SuiteCreationListener#onStart}, before any test runs; this
 * class no longer creates or quits a full session per test. See that listener's class javadoc for the
 * full incident history explaining why the old per-test full relaunch was itself the cause of a real
 * production-environment incident, not just an efficiency concern.
 *
 * <p>{@code @AfterMethod} no longer does a full {@code clearAppDataAndRelaunch()} - that pattern (a
 * {@code resetToCleanLoginOnTestBackend()} helper copy-pasted into every module's Tests class, calling
 * {@code clearAppDataAndRelaunch()} + {@code switchEnvironmentToTest()} directly and unconditionally after
 * every single test) is RETIRED project-wide. Each module's own tests are now responsible for their own
 * lightweight, context-appropriate reset in their own test body/finally block (e.g.
 * {@code SiteManagementPage#ensureSiteSelected} to reach whatever site/screen the next assertion needs) -
 * full {@code clearAppDataAndRelaunch()} is reserved for genuine recovery scenarios (a test whose own
 * subject IS force-killing/backgrounding the app) or a last-resort fallback, never routine between-test
 * cleanup.
 */
public class TestBase extends DriverFactory {

    protected static final Logger logger = LogManager.getLogger(TestBase.class.getName());

    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";

    /** Same package-based detection {@code SuiteCreationListener} uses - see its javadoc for why. */
    private static final String LOGIN_FLOW_TEST_PACKAGE = "com.cresanmanagement.eyeq.onboardingauthenticationtests";

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        AppiumDriver existing = getDriverForFramework();
        if (existing == null) {
            // No suite-level session exists yet - this happens for a targeted single-method run
            // (mvn test -Dtest=ClassName#methodName), which TestNG never routes through
            // ISuiteListener#onStart (the same gap documented in EnvironmentSetup's Incident 1). Create
            // the one session this run needs here instead, reproducing SuiteCreationListener's own
            // launch+environment-switch(+login) sequence so a single-method run is just as safe as a full
            // suite run, never a silently-uncovered path.
            logger.info("No shared driver session found (likely a targeted single-method run) - creating one now.");
            AppiumDriver driver = InitiateDriver.getInstance().createDriver();
            setDriverForFramework(driver);
            if (!isLoginFlowTestClass()) {
                loginSharedSessionOnce();
            }
            return;
        }

        // A shared suite-level session already exists - confirm it is still actually alive before trusting
        // it for this test, rather than silently proceeding into a dead session's confusing downstream
        // failures. A cheap, read-only call is enough to detect a dropped connection.
        try {
            existing.getPageSource();
        } catch (Exception e) {
            logger.warn("The shared suite-level driver session appears to have died ("
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + ") - recreating it as an "
                    + "exceptional fallback. This is NOT expected routine behavior; if this recurs, the "
                    + "underlying Appium/device connection stability should be investigated.", e);
            removeDriverForFramework();
            AppiumDriver driver = InitiateDriver.getInstance().createDriver();
            setDriverForFramework(driver);
            // createDriver() with NoReset=true re-attaches automation to whatever the app process is
            // ALREADY showing (the app itself never died, only the automation layer needed reattaching) -
            // it is NOT guaranteed to be a fresh Login screen, so only attempt a recovery login if the app
            // genuinely IS showing one; otherwise trust whatever state it's already in.
            // A snapshot check of already-settled state (the reattach above is not a transition in
            // progress) - the fast-bounded variant is correct here (see its javadoc).
            if (!isLoginFlowTestClass() && Pages.OnboardingAuthenticationPage().isLoginScreenDisplayedFast()) {
                loginSharedSessionOnce();
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Intentionally does NOT quit the driver or clear app data - the session is shared across the
        // whole suite now (see class javadoc). Quitting happens once, in
        // testng.SuiteCreationListener#onFinish. Individual tests own their own lightweight per-test
        // reset in their own test body/finally block.
    }

    private boolean isLoginFlowTestClass() {
        return getClass().getPackageName().equals(LOGIN_FLOW_TEST_PACKAGE);
    }

    private void loginSharedSessionOnce() {
        Map<String, String> qaUser = CSVLoader.loadRowByKey(ENV_CSV, "QaUser");
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        authPage.login(qaUser.get("Username"), qaUser.get("Password"));
        if (!authPage.isSiteSelectionScreenDisplayed()) {
            throw new IllegalStateException(
                    "Fallback single-method-run login did not land on Site Selection as expected.");
        }
    }
}
