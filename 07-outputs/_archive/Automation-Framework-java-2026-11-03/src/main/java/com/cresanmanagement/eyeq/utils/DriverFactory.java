package com.cresanmanagement.eyeq.utils;

import io.appium.java_client.AppiumDriver;

/**
 * Holder for the active {@link AppiumDriver} session.
 *
 * Never instantiate {@link AppiumDriver} directly in test or page classes - always go through
 * {@link #getDriverForFramework()} / {@link #setDriverForFramework(AppiumDriver)}.
 *
 * <p><b>FIX (2026-10-21):</b> this was previously a {@code ThreadLocal<AppiumDriver>}, documented at the
 * time as supporting "parallel TestNG execution (one Appium session per @Test method) stays
 * thread-isolated" - a goal of the OLD, retired per-test-method session design. That design was replaced
 * project-wide (2026-10-20) by a suite-level shared-session architecture: ONE {@link AppiumDriver} is
 * created once in {@code testng.SuiteCreationListener#onStart} and reused by every test in the suite. A
 * {@code ThreadLocal} is actively wrong for that goal - it only publishes the driver to the exact thread
 * that called {@link #setDriverForFramework(AppiumDriver)}, so any other thread's lookup silently (and
 * incorrectly) sees no driver at all.
 *
 * <p>This was not theoretical: a real run of {@code CC_TestNG.xml} with {@code parallel="tests"
 * thread-count="2"} confirmed it live (see that file's own fix note and logs/automation1.log,
 * 2026-10-21 18:37:23) - the ONE shared driver {@code onStart()} created was invisible to both parallel
 * worker threads, each of which then tried to open its OWN brand-new session against the SAME single
 * physical device at the same time, crashing the device's UiAutomator2 instrumentation process and
 * cascading into every one of that run's 177 tests being skipped. The suite XML is now fixed to run
 * sequentially (this architecture has no use for parallel TestNG execution - there is one shared session
 * and, in this project, one physical device), and this field is now a plain {@code static} so a future
 * reader isn't misled into thinking thread-isolation is still an intentional, supported feature here.
 */
public class DriverFactory {

    public static AppiumDriver driver;

    public static AppiumDriver getDriverForFramework() {
        return driver;
    }

    public static void setDriverForFramework(AppiumDriver localdriver) {
        driver = localdriver;
    }

    public static void removeDriverForFramework() {
        driver = null;
    }
}
