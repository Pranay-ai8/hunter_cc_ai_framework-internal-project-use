package com.codecraft.saucedemo.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to {@code MAX_RETRY_COUNT} times before it is reported as a
 * true failure. Useful for the non-deterministic {@code error_user}/{@code
 * performance_glitch_user} scenarios flagged in FunctionalTestCases.md, without masking
 * genuine regressions (bounded retry count). Wired via {@link AnnotationTransformer}.
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
