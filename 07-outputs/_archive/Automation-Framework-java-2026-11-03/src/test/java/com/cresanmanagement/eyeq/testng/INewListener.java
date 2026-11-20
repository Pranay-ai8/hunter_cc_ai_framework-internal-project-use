package com.cresanmanagement.eyeq.testng;

import org.testng.IAnnotationTransformer;
import org.testng.IMethodInterceptor;
import org.testng.ITestListener;

/**
 * Marker interface combining the three TestNG listener contracts {@link TestListener} implements in
 * one place: method ordering ({@link IMethodInterceptor}), annotation transformation (retry-analyzer
 * injection, {@link IAnnotationTransformer}), and test lifecycle events ({@link ITestListener}).
 */
public interface INewListener extends IMethodInterceptor, IAnnotationTransformer, ITestListener {
}
