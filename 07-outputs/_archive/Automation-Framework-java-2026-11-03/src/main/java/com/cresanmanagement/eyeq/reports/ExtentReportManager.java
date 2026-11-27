package com.cresanmanagement.eyeq.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestResult;

/**
 * Static ExtentReports instance + ThreadLocal<ExtentTest>, one entry per @Test method. Driven
 * entirely by {@code testng.TestListener}/{@code testng.MobileEmailReport} - no per-test-method
 * boilerplate or annotation is needed in test classes themselves.
 *
 * Ported from the reference Automation-java project's proven ExtentReportManager/WebEmailReport pair -
 * see 03-standards/appium-java-standards.md's "Reporting" section for why this replaced an earlier,
 * never-actually-implemented Allure design.
 */
public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static synchronized void initReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport/EyeQMonitoringReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    public static void startTest(String className, String methodName) {
        currentTest.set(extent.createTest(className + " :: " + methodName));
    }

    public static void pass(String message) {
        if (currentTest.get() != null) {
            currentTest.get().pass(message);
        }
    }

    public static void fail(Throwable t, String screenshotPath) {
        if (currentTest.get() == null) {
            return;
        }
        if (screenshotPath != null) {
            try {
                currentTest.get().fail(t, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                return;
            } catch (Exception ignored) {
                // fall through to text-only failure
            }
        }
        currentTest.get().fail(t);
    }

    public static void skip(String message) {
        if (currentTest.get() != null) {
            currentTest.get().skip(message);
        }
    }

    public static void endTest(ITestResult result) {
        currentTest.remove();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
