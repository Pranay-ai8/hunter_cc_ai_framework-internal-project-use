package com.codecraft.wdiodemoapp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the external test-data source for a data-driven test method.
 * Resolved into a {@code Map<String,String>} by
 * {@link com.codecraft.wdiodemoapp.utils.TestDataArgumentsProvider}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestDataSource {
    String value();
    String type() default "csv";
    String key();
}
