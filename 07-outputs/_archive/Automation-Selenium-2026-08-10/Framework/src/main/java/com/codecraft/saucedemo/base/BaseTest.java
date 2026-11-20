package com.codecraft.saucedemo.base;

import com.codecraft.saucedemo.config.ConfigurationManager;
import com.codecraft.saucedemo.driver.BrowserType;
import com.codecraft.saucedemo.driver.DriverFactory;
import com.codecraft.saucedemo.driver.DriverManager;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

/**
 * TestNG lifecycle base class. Every test class extends this. Per
 * 03-standards/selenium-standards.md "Base Classes" — resolves the browser from
 * config/system property, initializes the ThreadLocal-backed driver, maximizes the
 * window, captures a screenshot on failure, then quits the driver.
 */
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    @Parameters({"env"})
    public void beforeSuite(@Optional("prd") String env) {
        System.setProperty("env", env);
        ExtentReportManager.initReport();
        log.info("Suite starting against env={}, baseUrl={}", env, ConfigurationManager.baseUrl());
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest(Method method) {
        BrowserType browserType = BrowserType.from(ConfigurationManager.browser());
        WebDriver driver = DriverFactory.createDriver(browserType);
        DriverManager.setDriver(driver);
        ExtentReportManager.startTest(method.getDeclaringClass().getSimpleName(), method.getName());
        driver.get(ConfigurationManager.baseUrl());
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest(ITestResult result) {
        WebDriver driver = DriverManager.getDriver();
        if (!result.isSuccess() && driver != null) {
            ScreenshotUtils.capture(driver, result.getMethod().getMethodName());
        }
        ExtentReportManager.endTest(result);
        DriverManager.quitDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flush();
    }
}
