package com.codecraft.saucedemo.base;

import com.codecraft.saucedemo.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.util.Set;

/**
 * Base class for every Page Object. Per 03-standards/selenium-standards.md:
 * concrete Page Objects declare {@code public <Module>Page(WebDriver driver) { super(driver); }},
 * declare locators as {@code @FindBy}-annotated {@code WebElement} fields, and route every
 * interaction through an explicit wait — never {@code Thread.sleep()}.
 */
public abstract class BasePage {

    private static final Logger log = LogManager.getLogger(BasePage.class);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = WaitUtils.newWait(driver);
        PageFactory.initElements(driver, this);
    }

    // ==== Shared Page Helpers ====

    protected WebElement waitForVisibility(WebElement element) {
        return WaitUtils.waitForVisibility(wait, element);
    }

    protected WebElement waitForClickable(WebElement element) {
        return WaitUtils.waitForClickable(wait, element);
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return waitForVisibility(element).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException e) {
            return false;
        }
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void switchToNewWindow() {
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                log.info("Switched to new window/tab: {}", handle);
                return;
            }
        }
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
