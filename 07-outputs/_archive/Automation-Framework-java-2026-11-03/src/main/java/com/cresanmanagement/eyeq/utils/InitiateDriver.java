package com.cresanmanagement.eyeq.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

/**
 * Singleton driver-creation entry point. {@link #createDriver()} reads config.properties' SETUP key
 * and branches accordingly.
 *
 * This project only ever runs SETUP=Local (BrowserStack remote execution was explicitly declined) -
 * see 03-standards/appium-java-standards.md's "BrowserStack Integration" section for the
 * createBrowserStackDriver() mechanism this scaffold intentionally omits. If BrowserStack is requested
 * later, add device_conf.json, BrowserStackConfig.java, BrowserStackAppUploader.java, and a
 * createBrowserStackDriver() branch here - do not resurrect dead config keys without the code to back them.
 */
public final class InitiateDriver {

    private static final Logger logger = LogManager.getLogger(InitiateDriver.class.getName());
    private static InitiateDriver instance;

    private AppiumDriver appiumDriver;

    private InitiateDriver() {
    }

    public static synchronized InitiateDriver getInstance() {
        if (instance == null) {
            instance = new InitiateDriver();
        }
        return instance;
    }

    /**
     * Creates (or returns) the current local Appium session.
     *
     * <p><b>STRUCTURAL SAFEGUARD (2026-10-19, after TWO real production-environment incidents):</b> this
     * is the ONE call every single session-creation path in this project already goes through - every
     * {@code @Test} method (via {@code framework.TestBase#setUp}), the suite-level listener (via
     * {@code EnvironmentSetup#runOnce}), AND any raw/manual exploratory {@code public static void main}
     * script used for this project's "Live UI Hierarchy Verification" crawling methodology. Relying on
     * TestNG lifecycle hooks alone (the first incident's fix) left exploratory sessions - an explicit,
     * intended part of this workflow - completely uncovered, since a plain {@code main()} never goes
     * through any TestNG lifecycle at all (second incident). Enforcing the mandatory devmode/Test-backend
     * switch HERE, on every driver before it is handed back to ANY caller, closes that gap structurally
     * rather than per-call-site - see {@code EnvironmentSetup}'s own class javadoc for the full incident
     * history. {@link EnvironmentSetup#ensureTestEnvironment(AppiumDriver)} is itself idempotent (does
     * real work only once per JVM), so this adds negligible overhead to every session after the first.
     */
    public AppiumDriver createDriver() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        HashMap<String, String> config = PropertyReader.getInstance().getPropValues("config.properties");
        String setup = config.getOrDefault("SETUP", "Local");

        if ("BrowserStack".equalsIgnoreCase(setup)) {
            throw new UnsupportedOperationException(
                    "SETUP=BrowserStack was requested, but this project's scaffold does not include the "
                            + "BrowserStack integration (device_conf.json / BrowserStackConfig / BrowserStackAppUploader) - "
                            + "BrowserStack was declined for this project. See InitiateDriver.java's class comment.");
        }

        AppiumDriver driver = createLocalDriver(config);
        DriverFactory.setDriverForFramework(driver);
        EnvironmentSetup.ensureTestEnvironment(driver);
        return driver;
    }

    private AppiumDriver createLocalDriver(HashMap<String, String> config) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String runOn = config.getOrDefault("RUN_ON", "ANDROID_APP");

        if (!"ANDROID_APP".equalsIgnoreCase(runOn)) {
            throw new IllegalArgumentException(
                    "Unsupported RUN_ON '" + runOn + "' - this project is Android-only. "
                            + "Add an IOSDriver branch here if iOS support is ever requested.");
        }

        String udid = DeviceConfig.getInstance().getDeviceID(runOn);
        String deviceName = DeviceConfig.getInstance().getDeviceName(runOn);
        String platformVersion = DeviceConfig.getInstance().getPlatformVersion(runOn);

        keepScreenAwakeAndUnlocked(udid);
        killAnyZombieUiAutomator2Server(udid);

        // NoReset=true by default (overridable via config.properties) - REQUIRED so the suite-level
        // devmode/environment-switch EnvironmentSetup performs once (see testng/SuiteCreationListener
        // + utils/EnvironmentSetup) survives into every later @Test method's own driver session.
        // Verified empirically: with noReset=false, Appium clears the app's data (including the
        // SharedPreferences-backed backend-URL setting the devmode trigger writes) before EVERY new
        // session, which would silently undo the suite-level setup before the very first real test
        // runs. Trade-off, documented rather than hidden: true also preserves normal app state
        // (e.g. a "remembered device" login prefill) across sessions within the same suite run - Page
        // Objects/tests that need a guaranteed pristine Login form should account for that rather than
        // assume a fresh install every time.
        boolean noReset = Boolean.parseBoolean(config.getOrDefault("NoReset", "true"));

        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid(udid)
                .setDeviceName(deviceName)
                .setPlatformVersion(platformVersion)
                .setApp(config.get("AppPath"))
                .setAppPackage(config.get("AndroidAppPackage"))
                .setAppActivity(config.get("AndroidAppActivity"))
                .setAutomationName("UiAutomator2")
                // Raised from 120s to 240s (2026-10-26) after root-causing a confirmed, 100%-reproducible
                // session death: OnboardingAuthenticationTests#restoreQaPasswordToOriginal() does a
                // deliberate, documented ~180-second real-time wait (a WebDriverWait wrapping a pure
                // System.currentTimeMillis() elapsed-time check - the "non-Thread.sleep() real-time wait"
                // idiom used elsewhere in this project, e.g. Base#briefRealTimeSettle()) for a real Test
                // backend cooldown. That lambda never actually calls any method on the driver, so NO
                // WebDriver/Appium traffic occurs for the full ~180 seconds - with the previous 120s
                // timeout, the server auto-terminated the shared session for inactivity partway through
                // every single time this wait ran (confirmed live via automation.log:
                // "NoSuchSessionException: A session is either terminated or not started" landing almost
                // exactly 120s after the wait's last logged line, then TestBase's liveness-recovery
                // silently created a brand-new session and the NEXT test continued fine - which is why
                // this was easy to miss as "just one flaky test" rather than a deterministic timeout
                // mismatch). 240s gives the known 180s wait a 60s margin; raise again if a longer
                // deliberate real-time wait is ever added.
                .setNewCommandTimeout(Duration.ofSeconds(240))
                .setNoReset(noReset)
                .setAutoGrantPermissions(true);

        String serverAddress = config.getOrDefault("SeverAddress", "http://0.0.0.0:4723");
        try {
            URL url = URI.create(serverAddress).toURL();
            appiumDriver = new AndroidDriver(url, options);
        } catch (Exception e) {
            logger.error("Failed to create AndroidDriver against " + serverAddress, e);
            throw new RuntimeException("Failed to create AndroidDriver against " + serverAddress
                    + " - is a local Appium server running? (AppiumServer.init() can start one programmatically.)", e);
        }

        // Attempted fix (2026-10-20) for "Cannot set AccessibilityNodeInfo's field 'mSealed' to 'true'" -
        // the error message names this UiAutomator2 setting as a documented workaround (forces element
        // lookups through the older XPath1 finder instead of XPath2). CONFIRMED NOT EFFECTIVE on its own:
        // the exception still recurred with this set. Left in place as a harmless, possibly-still-useful
        // setting, but the REAL confirmed root cause of that exception (and a cluster of related symptoms)
        // was a zombie io.appium.uiautomator2.server process left running by a prior dead session - see
        // killAnyZombieUiAutomator2Server(), called above, for the actual fix.
        try {
            ((io.appium.java_client.HasSettings) appiumDriver).setSetting("enforceXPath1", true);
        } catch (Exception e) {
            logger.warn("Could not apply the 'enforceXPath1' UiAutomator2 setting (non-fatal).", e);
        }
        return appiumDriver;
    }

    /**
     * Best-effort real-device safety step: keeps the screen awake while USB-powered and dismisses any
     * keyguard, before every session is created.
     *
     * <p><b>Live-run finding (2026-10-16, real Pixel 3 XL / Android 12):</b> a long-running @Test method
     * (one that waits through a real login round-trip, e.g. {@code isSiteSelectionScreenDisplayed}'s
     * 90-second wait) let the device's screen turn off and lock mid-test, after which Appium could no
     * longer find ANY element (confirmed via the failure screenshot: it showed the device's lock
     * screen, not the app) - a real-hardware failure mode the emulator never exhibited (emulators don't
     * sleep by default). {@code svc power stayon usb} keeps the screen on for as long as the device is
     * on USB power (which it always is during a wired test run) - harmless and a no-op on an emulator,
     * so this runs unconditionally rather than branching on device type.
     */
    private void keepScreenAwakeAndUnlocked(String udid) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        runAdbBestEffort(udid, "shell", "svc", "power", "stayon", "usb");
        runAdbBestEffort(udid, "shell", "input", "keyevent", "KEYCODE_WAKEUP");
        runAdbBestEffort(udid, "shell", "wm", "dismiss-keyguard");
    }

    /**
     * Real, confirmed finding (2026-10-20, suite-level architecture validation): when a prior session
     * died unexpectedly (e.g. the client-side connection dropped before {@code driver.quit()} could reach
     * the server), the on-device {@code io.appium.uiautomator2.server} instrumentation process can survive
     * as a zombie - confirmed live via {@code adb shell ps -A}, caught mid-incident continuously spewing
     * "Could not take screenshot: UiAutomation returned null" in a tight ~100ms loop, forever, completely
     * independent of any new session. This zombie competes for the same on-device Accessibility/
     * UiAutomation resources a brand-new session also needs, and is the confirmed real cause of a cluster
     * of symptoms initially misattributed to other things: the "mSealed AccessibilityNodeInfo" exception
     * (which an {@code enforceXPath1} setting change did NOT fix, because the real cause was never about
     * XPath implementation), "'POST /elements' cannot be proxied... instrumentation process is not
     * running (probably crashed)", and even a transient "device not found" from {@code adb devices}
     * (a sufficiently confused device with two UiAutomation clients fighting can affect ADB itself).
     * Force-stopping this package before every new session - regardless of whether a zombie is actually
     * present - guarantees a clean slate rather than hoping the previous session cleaned up after itself.
     */
    private void killAnyZombieUiAutomator2Server(String udid) {
        runAdbBestEffort(udid, "shell", "am", "force-stop", "io.appium.uiautomator2.server");
    }

    private void runAdbBestEffort(String udid, String... args) {
        try {
            String[] command = new String[args.length + 3];
            command[0] = "adb";
            command[1] = "-s";
            command[2] = udid;
            System.arraycopy(args, 0, command, 3, args.length);
            new ProcessBuilder(command).redirectErrorStream(true).start().waitFor();
        } catch (Exception e) {
            logger.warn("Best-effort adb command failed (non-fatal): " + String.join(" ", args), e);
        }
    }

    public AppiumDriver getAppiumDriver() {
        return appiumDriver;
    }

    public void quitDriver() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if (appiumDriver != null) {
            try {
                appiumDriver.quit();
            } catch (Exception e) {
                logger.error("Error while quitting AppiumDriver", e);
            } finally {
                appiumDriver = null;
            }
        }
    }
}
