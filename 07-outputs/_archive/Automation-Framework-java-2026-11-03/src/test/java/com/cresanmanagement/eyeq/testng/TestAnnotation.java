package com.cresanmanagement.eyeq.testng;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Standalone {@link IAnnotationTransformer} scaffold, kept separate from {@link TestListener}'s own
 * {@code transform()} (which is what actually wires {@link RetryAnalyzer} onto every test method - see
 * 03-standards/appium-java-standards.md's "Retry" section). Not registered anywhere by default; extend
 * this class instead of duplicating {@link TestListener#transform} if a future requirement needs a
 * second, independent annotation-transformation concern (e.g. per-group timeouts).
 */
public class TestAnnotation implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Intentionally empty - see class-level comment. TestListener.transform() is the active
        // annotation transformer registered via CC_TestNG.xml's <listeners> block.
    }
}
