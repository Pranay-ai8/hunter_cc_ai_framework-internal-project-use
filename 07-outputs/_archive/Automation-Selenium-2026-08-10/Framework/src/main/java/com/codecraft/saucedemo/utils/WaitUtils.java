package com.codecraft.saucedemo.utils;

import com.codecraft.saucedemo.config.ConfigurationManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Centralizes all explicit-wait logic so Page Objects never construct a raw
 * {@code new WebDriverWait(...)} inline and never call {@code Thread.sleep()}.
 * Per 03-standards/selenium-standards.md "Waiting Strategy".
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    public static Duration defaultTimeout() {
        return Duration.ofSeconds(ConfigurationManager.explicitWaitSeconds());
    }

    public static WebDriverWait newWait(WebDriver driver) {
        return new WebDriverWait(driver, defaultTimeout());
    }

    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        return newWait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits for presence of an element located dynamically at call time (e.g. an
     * xpath built from a runtime value like a product name) — for the common case
     * of a {@code @FindBy} field, prefer {@link #waitForVisibility(WebDriver, WebElement)}
     * instead, since PageFactory proxies already defer lookup until first use.
     */
    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return newWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(WebDriverWait wait, WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return newWait(driver).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickable(WebDriverWait wait, WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean waitForInvisibility(WebDriver driver, WebElement element) {
        try {
            return newWait(driver).until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static List<WebElement> waitForVisibilityOfAll(WebDriver driver, List<WebElement> elements) {
        return newWait(driver).until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Waits for the current URL to contain {@code fragment}. Returns {@code false}
     * (rather than throwing) on timeout, since this is primarily used as a boolean
     * page-state validation (e.g. {@code isOnInventoryPage()}) rather than a hard
     * precondition wait.
     */
    public static boolean waitForUrlContains(WebDriver driver, String fragment) {
        try {
            return newWait(driver).until(ExpectedConditions.urlContains(fragment));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static boolean waitForNumberOfElementsToBe(WebDriver driver, By locator, int count) {
        try {
            newWait(driver).until(ExpectedConditions.numberOfElementsToBe(locator, count));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static boolean waitForTextToBePresent(WebDriver driver, WebElement element, String text) {
        try {
            return newWait(driver).until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
