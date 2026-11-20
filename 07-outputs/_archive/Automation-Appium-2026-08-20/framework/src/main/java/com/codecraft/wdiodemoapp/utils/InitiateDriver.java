package com.codecraft.wdiodemoapp.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Local-only Android + iOS driver bootstrap for the WebdriverIO Native Demo App.
 * BrowserStack/Flutter paths from the reference framework remain out of scope for this
 * engagement (see AutomationSummary.md); Android and iOS are both fully supported, selected
 * via the Maven profile (-P android|ios), which substitutes RUN_ON in config.properties.
 */
public class InitiateDriver {

    private static final Logger logger = LogManager.getLogger(InitiateDriver.class);
    private static InitiateDriver instance;
    private AppiumDriver driver;

    private InitiateDriver() {
    }

    public static InitiateDriver getInstance() {
        if (instance == null) {
            instance = new InitiateDriver();
        }
        return instance;
    }

    public void createDriver() {
        Map<String, String> config = PropertyReader.getInstance().getPropValues("config.properties");
        String runOn = config.getOrDefault("RUN_ON", "ANDROID_APP");
        String serverUrl = config.getOrDefault("SeverAddress", "http://127.0.0.1:4723");

        try {
            URL appiumServerUrl = new URL(serverUrl);
            logger.info("Connecting to Appium server at {} (platform: {})", serverUrl, runOn);
            driver = runOn.contains("iOS")
                    ? new IOSDriver(appiumServerUrl, buildIOSOptions(config))
                    : new AndroidDriver(appiumServerUrl, buildAndroidOptions(config));
            driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(2));
            DriverFactory.setDriverForFramework(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    private UiAutomator2Options buildAndroidOptions(Map<String, String> config) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setAutomationName("UiAutomator2");
        options.setDeviceName(config.getOrDefault("DeviceName", "Android Emulator"));
        options.setAppPackage(config.get("AndroidAppPackage"));
        options.setAppActivity(config.get("AndroidAppActivity"));
        options.setCapability("appium:autoGrantPermissions", true);
        options.setCapability("appium:newCommandTimeout", 180);
        // Each test method starts a new session; force a genuine activity restart (not just
        // "bring existing process to foreground") so navigation state (e.g. an open side menu)
        // never leaks from the previous test into this one.
        options.setCapability("appium:forceAppLaunch", true);
        options.setCapability("appium:shouldTerminateApp", true);
        options.setNoReset(true);
        // Live-run finding: this sandbox's Appium/chromedriver-resolution path hangs for ~10
        // minutes trying to auto-provision a matching chromedriver for WEBVIEW context switching,
        // even though the underlying chromedriver zip is directly reachable in seconds (verified
        // manually). Disabling autodownload makes any webview-context operation fail fast instead
        // of stalling the whole suite — see AutomationSummary.md.
        options.setCapability("appium:chromedriverAutodownload", false);
        return options;
    }

    private XCUITestOptions buildIOSOptions(Map<String, String> config) {
        XCUITestOptions options = new XCUITestOptions();
        options.setPlatformName("iOS");
        options.setAutomationName("XCUITest");
        options.setDeviceName(config.getOrDefault("IOSDeviceName", "iPhone 15"));
        options.setPlatformVersion(config.getOrDefault("IOSPlatformVersion", "17.2"));
        String udid = config.get("IOSUdid");
        if (udid != null && !udid.isBlank()) {
            options.setUdid(udid);
        }
        options.setApp(config.get("IOSAppPath"));
        options.setBundleId(config.get("BundleId"));
        options.setCapability("appium:newCommandTimeout", 180);
        // iOS has no autoGrantPermissions equivalent — permission requests surface as real system
        // alerts (outside the app's own accessibility tree, confirmed live via screenshot) that
        // must be accepted for the test to proceed rather than hang waiting on a hidden dialog.
        options.setCapability("appium:autoAcceptAlerts", true);
        // Same live-run finding as Android (see buildAndroidOptions): each test method starts a
        // new session, so without forcing a genuine relaunch, XCUITest just brings the existing
        // process forward and leaves the PREVIOUS test's screen/dialog state in place — these two
        // base-driver capabilities are platform-agnostic and apply to XCUITest sessions too.
        options.setCapability("appium:forceAppLaunch", true);
        options.setCapability("appium:shouldTerminateApp", true);
        options.setNoReset(true);
        return options;
    }

    public AppiumDriver getAppiumDriver() {
        if (driver == null) {
            throw new RuntimeException("Driver has not been instantiated");
        }
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
