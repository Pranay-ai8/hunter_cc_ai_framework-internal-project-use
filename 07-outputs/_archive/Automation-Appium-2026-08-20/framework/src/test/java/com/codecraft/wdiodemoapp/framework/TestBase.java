package com.codecraft.wdiodemoapp.framework;

import com.codecraft.wdiodemoapp.utils.DriverFactory;
import com.codecraft.wdiodemoapp.utils.InitiateDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class TestBase extends DriverFactory {

    protected static final Logger logger = LogManager.getLogger(TestBase.class.getName());

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger.info("Creating Appium driver session...");
        InitiateDriver.getInstance().createDriver();
        setDriverForFramework(InitiateDriver.getInstance().getAppiumDriver());
        logger.info("Driver session created.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Quitting Appium driver session...");
        InitiateDriver.getInstance().quitDriver();
    }
}
