package com.codecraft.saucedemo.driver;

/**
 * Supported browsers. Per 03-standards/selenium-standards.md, {@code DriverFactory}
 * creates a browser instance per this enum using WebDriverManager for binary setup.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;

    public static BrowserType from(String value) {
        if (value == null || value.isBlank()) {
            return CHROME;
        }
        return BrowserType.valueOf(value.trim().toUpperCase());
    }
}
