package com.cresanmanagement.eyeq.testng;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed @Test method up to {@link #MAX_RETRY_COUNT} times. Registered automatically on
 * every test method by {@link TestListener#transform} (an {@link org.testng.IAnnotationTransformer}) -
 * no per-method {@code retryAnalyzer} attribute is needed on individual {@code @Test} annotations.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = 1;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            return true;
        }
        return false;
    }
}
