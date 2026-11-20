package com.cresanmanagement.eyeq.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a @Test method as data-driven from an external, CSV-backed test-data file - the Mode A
 * replacement for hardcoded string literals or {@code credentials.properties} (see
 * 03-standards/appium-java-standards.md's "Test Data Management" section).
 *
 * Usage:
 * <pre>
 * {@literal @}Test(description = "...")
 * {@literal @}TestDataSource(value = "src/test/resources/testdata/onboardingauthentication.csv", key = "ValidUser")
 * public void verifyLoginWithValidCredentials(Method testMethod) {
 *     Map&lt;String, String&gt; data = TestDataArgumentsProvider.resolve(testMethod);
 *     ...
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestDataSource {

    /** Path to the CSV file, relative to the project root (e.g. "src/test/resources/testdata/login.csv"). */
    String value();

    /** Data source type - only "csv" is implemented by this scaffold. */
    String type() default "csv";

    /** The row to select, matched against the CSV's "Key" column. */
    String key();
}
