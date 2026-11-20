package com.codecraft.saucedemo.utils;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

/**
 * Centralized hard + thread-safe soft assertions over TestNG Assert/SoftAssert.
 * Per 03-standards/selenium-standards.md — tests/Page Objects route assertions through
 * this class rather than calling TestNG Assert/SoftAssert directly.
 */
public final class AssertionUtil {

    private static final ThreadLocal<SoftAssert> SOFT_ASSERT = ThreadLocal.withInitial(SoftAssert::new);

    private AssertionUtil() {
    }

    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    public static void assertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);
    }

    public static void assertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    public static void softAssertTrue(boolean condition, String message) {
        SOFT_ASSERT.get().assertTrue(condition, message);
    }

    public static void softAssertFalse(boolean condition, String message) {
        SOFT_ASSERT.get().assertFalse(condition, message);
    }

    public static void softAssertEquals(Object actual, Object expected, String message) {
        SOFT_ASSERT.get().assertEquals(actual, expected, message);
    }

    public static void assertAll() {
        try {
            SOFT_ASSERT.get().assertAll();
        } finally {
            SOFT_ASSERT.remove();
        }
    }
}
