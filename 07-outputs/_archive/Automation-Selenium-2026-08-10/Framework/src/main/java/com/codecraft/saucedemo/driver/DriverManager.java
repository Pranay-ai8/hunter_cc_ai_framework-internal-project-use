package com.codecraft.saucedemo.driver;

import org.openqa.selenium.WebDriver;

/**
 * Wraps the active WebDriver instance in a {@code ThreadLocal} so parallel TestNG
 * execution (see config/testng_config.xml {@code parallel="methods"}) is thread-safe.
 * Per 03-standards/selenium-standards.md — never expose a static, non-ThreadLocal
 * {@code WebDriver} field.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
