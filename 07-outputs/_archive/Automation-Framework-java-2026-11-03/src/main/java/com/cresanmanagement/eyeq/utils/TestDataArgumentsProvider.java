package com.cresanmanagement.eyeq.utils;

import com.cresanmanagement.eyeq.annotations.TestDataSource;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Resolves a @Test method's {@link TestDataSource} annotation into the matching CSV row. TestNG
 * auto-injects a {@code java.lang.reflect.Method} parameter with the currently-running test method, so
 * the test body needs no reflection lookup of its own:
 *
 * <pre>
 * {@literal @}Test(description = "...")
 * {@literal @}TestDataSource(value = "src/test/resources/testdata/login.csv", key = "ValidUser")
 * public void verifyLogin(Method testMethod) {
 *     Map&lt;String, String&gt; data = TestDataArgumentsProvider.resolve(testMethod);
 *     ...
 * }
 * </pre>
 */
public final class TestDataArgumentsProvider {

    private TestDataArgumentsProvider() {
    }

    public static Map<String, String> resolve(Method testMethod) {
        TestDataSource source = testMethod.getAnnotation(TestDataSource.class);
        if (source == null) {
            throw new IllegalStateException("Method " + testMethod.getName() + " has no @TestDataSource annotation");
        }
        if (!"csv".equalsIgnoreCase(source.type())) {
            throw new UnsupportedOperationException("TestDataSource type '" + source.type() + "' is not implemented - only 'csv' is supported by this scaffold");
        }
        return CSVLoader.loadRowByKey(source.value(), source.key());
    }
}
