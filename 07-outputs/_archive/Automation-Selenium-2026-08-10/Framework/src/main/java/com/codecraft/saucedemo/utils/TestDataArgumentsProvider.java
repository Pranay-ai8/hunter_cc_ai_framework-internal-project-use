package com.codecraft.saucedemo.utils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Thin adapter between {@link CSVLoader} and TestNG {@code @DataProvider} methods.
 * Convention: each test class exposes one {@code @DataProvider} that injects the
 * currently-running {@code java.lang.reflect.Method} (TestNG supports this) and looks
 * up the CSV row whose {@code Key} column equals the test method's name — one row per
 * test method, keeping the "one @Test method per test case" rule from
 * 03-standards/selenium-standards.md while still externalizing test data to CSV.
 */
public final class TestDataArgumentsProvider {

    private TestDataArgumentsProvider() {
    }

    public static Object[][] singleRow(String csvPath, Method testMethod) {
        Map<String, String> row = CSVLoader.loadRowByKey(csvPath, testMethod.getName());
        return new Object[][]{{row}};
    }

    public static Object[][] singleRow(String csvPath, String key) {
        Map<String, String> row = CSVLoader.loadRowByKey(csvPath, key);
        return new Object[][]{{row}};
    }
}
