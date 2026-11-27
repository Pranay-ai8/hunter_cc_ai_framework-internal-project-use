package com.codecraft.wdiodemoapp.utils;

import com.codecraft.wdiodemoapp.annotations.TestDataSource;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Resolves a method's {@code @TestDataSource} annotation into a
 * {@code Map<String,String>}. TestNG auto-injects the running test method via
 * a {@code java.lang.reflect.Method} parameter — declare one on the {@code @Test}
 * method and pass it straight through, no reflection lookup needed in the test itself.
 */
public final class TestDataArgumentsProvider {

    private TestDataArgumentsProvider() {
    }

    public static Map<String, String> resolve(Method testMethod) {
        TestDataSource source = testMethod.getAnnotation(TestDataSource.class);
        if (source == null) {
            throw new IllegalStateException("No @TestDataSource on method: " + testMethod.getName());
        }
        if (!"csv".equalsIgnoreCase(source.type())) {
            throw new UnsupportedOperationException("Only type=csv is currently supported: " + source.type());
        }
        return CSVLoader.loadRowByKey(source.value(), source.key());
    }
}
