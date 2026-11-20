package com.cresanmanagement.eyeq.testng;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orders @Test methods within a class by their source line number (top-to-bottom execution order)
 * using Javassist to read each method's bytecode line-number table, rather than relying on TestNG's
 * {@code priority} attribute (which every method would otherwise need set explicitly) or reflection
 * declaration order (which the JVM does not guarantee matches source order).
 *
 * Delegated to by {@link TestListener#intercept} - this class holds the actual ordering logic as its
 * own single-responsibility unit, per 02-templates/AppiumFramework.md's TestNG Listeners table.
 */
public class PriorityInterceptor implements IMethodInterceptor {

    private static final Logger logger = LogManager.getLogger(PriorityInterceptor.class.getName());

    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        return methods.stream()
                .sorted(Comparator.comparingInt(this::sourceLineNumber))
                .collect(Collectors.toList());
    }

    private int sourceLineNumber(IMethodInstance methodInstance) {
        ITestNGMethod testNGMethod = methodInstance.getMethod();
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new javassist.ClassClassPath(testNGMethod.getRealClass()));
            CtClass ctClass = pool.get(testNGMethod.getRealClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(testNGMethod.getMethodName());
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            LineNumberAttribute lineNumberAttribute =
                    (LineNumberAttribute) methodInfo.getCodeAttribute().getAttribute(LineNumberAttribute.tag);
            // toLineNumber(0) converts bytecode offset 0 (the start of the method body) to its
            // corresponding source line number.
            return lineNumberAttribute == null ? Integer.MAX_VALUE : lineNumberAttribute.toLineNumber(0);
        } catch (Exception e) {
            logger.warn("Could not resolve source line number for " + testNGMethod.getMethodName()
                    + " - falling back to declaration order for this method", e);
            return Integer.MAX_VALUE;
        }
    }
}
