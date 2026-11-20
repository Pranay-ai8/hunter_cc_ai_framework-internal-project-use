package com.cresanmanagement.eyeq.testng;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;

/**
 * Optional WebDriver event logging. Selenium 4 removed the old {@code WebDriverEventListener}
 * interface in favor of {@link WebDriverListener} + {@link org.openqa.selenium.support.events.EventFiringDecorator}
 * (a decorator wrapping the driver instance, rather than a listener registered directly on
 * RemoteWebDriver) - this class implements the modern replacement.
 *
 * Not wired into {@code InitiateDriver} by default (see 02-templates/AppiumFramework.md's TestNG
 * Listeners table: "Optional WebDriver event logging"). To enable it, wrap the created driver before
 * calling {@code setDriverForFramework(...)}:
 * <pre>
 * AppiumDriver decorated = new EventFiringDecorator&lt;&gt;(AppiumDriver.class, new EventListener())
 *         .decorate(appiumDriver);
 * </pre>
 */
public class EventListener implements WebDriverListener {

    private static final Logger logger = LogManager.getLogger(EventListener.class.getName());

    @Override
    public void beforeClick(WebElement element) {
        logger.debug("About to click: " + element);
    }

    @Override
    public void afterClick(WebElement element) {
        logger.debug("Clicked: " + element);
    }

    @Override
    public void beforeFindElement(WebDriver driver, org.openqa.selenium.By locator) {
        logger.debug("About to find element: " + locator);
    }

    @Override
    public void onError(Object target, java.lang.reflect.Method method, Object[] args, InvocationTargetException e) {
        logger.error("WebDriver call failed: " + method.getName(), e.getTargetException());
    }
}
