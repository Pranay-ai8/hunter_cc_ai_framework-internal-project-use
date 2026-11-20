package com.cresanmanagement.eyeq.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.support.PageFactory;
import io.appium.java_client.support.pagefactory.FieldDecorator;

import java.time.Duration;

/**
 * Base class for every NATIVE Page Object Repository. Wires {@code @AndroidFindBy}-annotated fields
 * via Appium's {@link AppiumFieldDecorator} + Appium's own {@link PageFactory}
 * (io.appium.java_client.support.PageFactory - NOT Selenium's org.openqa.selenium.support.PageFactory,
 * which only accepts Selenium's own org.openqa.selenium.support.pagefactory.FieldDecorator interface;
 * AppiumFieldDecorator implements Appium's separate io.appium.java_client.support.pagefactory.FieldDecorator
 * interface instead as of this java-client version - confirmed by compiling against the real jar during
 * this generation), so repository classes never call {@code driver.findElement()} directly.
 */
public class Object_Base {

    public long wait = 10;

    public Object_Base(AppiumDriver driver) {
        FieldDecorator fieldDecorator = new AppiumFieldDecorator(driver, Duration.ofSeconds(wait));
        PageFactory.initElements(fieldDecorator, this);
    }
}
