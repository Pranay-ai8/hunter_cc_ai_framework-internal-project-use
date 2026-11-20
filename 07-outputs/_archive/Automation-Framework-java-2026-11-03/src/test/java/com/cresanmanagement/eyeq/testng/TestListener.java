package com.cresanmanagement.eyeq.testng;

import com.cresanmanagement.eyeq.framework.TestBase;
import com.cresanmanagement.eyeq.reports.ExtentReportManager;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IMethodInstance;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Combined TestNG listener: method ordering (delegates to {@link PriorityInterceptor}), automatic
 * retry-analyzer injection, log4j2 lifecycle logging, screenshot-on-failure to {@code Screenshots/},
 * and driving {@link ExtentReportManager}. Registered in CC_TestNG.xml / CC_Sample_TestNG.xml's
 * {@code <listeners>} block alongside {@link MobileEmailReport}.
 */
public class TestListener extends TestBase implements INewListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class.getName());
    private static final Path SCREENSHOTS_DIR = Paths.get("Screenshots");
    private final PriorityInterceptor priorityInterceptor = new PriorityInterceptor();

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        return priorityInterceptor.intercept(methods, context);
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // See 03-standards/appium-java-standards.md's "Retry" section: every test method gets a
        // RetryAnalyzer automatically - no per-method `retryAnalyzer` attribute needed.
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("STARTED: " + result.getTestClass().getName() + "." + result.getMethod().getMethodName());
        ExtentReportManager.startTest(result.getTestClass().getRealClass().getSimpleName(), result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("PASSED: " + result.getMethod().getMethodName());
        ExtentReportManager.pass("Test passed.");
        ExtentReportManager.endTest(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("FAILED: " + result.getMethod().getMethodName(), result.getThrowable());
        String screenshotPath = captureScreenshot(result.getMethod().getMethodName());
        ExtentReportManager.fail(result.getThrowable(), screenshotPath);
        ExtentReportManager.endTest(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("SKIPPED: " + result.getMethod().getMethodName());
        ExtentReportManager.skip(result.getThrowable() != null ? result.getThrowable().getMessage() : "Test skipped.");
        ExtentReportManager.endTest(result);
    }

    private String captureScreenshot(String methodName) {
        try {
            Files.createDirectories(SCREENSHOTS_DIR);
            if (DriverFactory.getDriverForFramework() == null) {
                logger.warn("No active driver session - cannot capture failure screenshot for " + methodName);
                return null;
            }
            File source = ((TakesScreenshot) DriverFactory.getDriverForFramework()).getScreenshotAs(OutputType.FILE);
            Path destination = SCREENSHOTS_DIR.resolve(methodName + "_" + System.currentTimeMillis() + ".png");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toString();
        } catch (Exception e) {
            logger.warn("Failed to capture failure screenshot for " + methodName, e);
            return null;
        }
    }
}
