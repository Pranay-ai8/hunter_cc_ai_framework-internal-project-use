package com.codecraft.saucedemo.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;

import java.nio.file.Paths;

/**
 * Initializes/flushes a single ExtentReports instance per suite run, per
 * 03-standards/selenium-standards.md "Reporting &amp; Logging". A per-thread
 * {@code ExtentTest} handle supports safe parallel TestNG execution.
 */
public final class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static synchronized void initReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "SauceDemo (Swag Labs)");
            extent.setSystemInfo("Stack", "Selenium WebDriver 4.x + Java 17 + TestNG + Maven");
        }
    }

    public static void startTest(String className, String methodName) {
        ExtentTest test = extent.createTest(className + " :: " + methodName);
        CURRENT_TEST.set(test);
    }

    public static void info(String message) {
        if (CURRENT_TEST.get() != null) {
            CURRENT_TEST.get().info(message);
        }
    }

    public static void pass(String message) {
        if (CURRENT_TEST.get() != null) {
            CURRENT_TEST.get().pass(message);
        }
    }

    public static void fail(String message) {
        if (CURRENT_TEST.get() != null) {
            CURRENT_TEST.get().fail(message);
        }
    }

    public static void endTest(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            return;
        }
        try {
            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    test.pass("Test passed");
                    break;
                case ITestResult.FAILURE:
                    test.fail(result.getThrowable());
                    break;
                case ITestResult.SKIP:
                    test.skip(result.getThrowable() != null ? result.getThrowable().getMessage() : "Skipped");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.warn("Failed to finalize ExtentTest entry: {}", e.getMessage());
        } finally {
            CURRENT_TEST.remove();
        }
    }

    public static void attachScreenshot(String path) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null && path != null) {
            try {
                test.fail("Failure screenshot",
                        MediaEntityBuilder.createScreenCaptureFromPath(Paths.get(path).toString()).build());
            } catch (Exception e) {
                log.warn("Failed to attach screenshot to report: {}", e.getMessage());
            }
        }
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
