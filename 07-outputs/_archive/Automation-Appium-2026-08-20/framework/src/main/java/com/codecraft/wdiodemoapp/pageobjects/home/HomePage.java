package com.codecraft.wdiodemoapp.pageobjects.home;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;
import org.openqa.selenium.By;

public class HomePage extends Base {

    private final HomePageObjectRepository repo;

    public HomePage() {
        repo = new HomePageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isHomeScreenDisplayed() {
        waitUntilElementIsVisible(repo.homeScreen);
        return isElementVisible(repo.homeScreen);
    }

    /** Android exposes visible text via @text; iOS has no such attribute, using @label/@name/@value instead. */
    public boolean isIntroductoryContentDisplayed() {
        return !driver.findElements(By.xpath(
                "//*[@text='WEBDRIVER' or @label='WEBDRIVER' or @name='WEBDRIVER' or @value='WEBDRIVER']")).isEmpty()
                && !driver.findElements(By.xpath(
                "//*[contains(@text,'Demo app for the appium-boilerplate') or contains(@label,'Demo app for the appium-boilerplate') "
                        + "or contains(@name,'Demo app for the appium-boilerplate') or contains(@value,'Demo app for the appium-boilerplate')]"))
                .isEmpty();
    }
}
