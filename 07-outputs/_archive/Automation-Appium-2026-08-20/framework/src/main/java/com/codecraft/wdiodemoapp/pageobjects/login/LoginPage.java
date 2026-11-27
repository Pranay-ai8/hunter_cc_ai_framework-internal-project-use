package com.codecraft.wdiodemoapp.pageobjects.login;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class LoginPage extends Base {

    private final LoginPageObjectRepository repo;

    public LoginPage() {
        repo = new LoginPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isLoginScreenDisplayed() {
        waitUntilElementIsVisible(repo.loginScreen);
        return isElementVisible(repo.loginScreen);
    }

    public void enterEmail(String email) {
        typeWithHeal(repo.inputEmail, "Email Input", email);
        hideKeyboard();
    }

    public void enterPassword(String password) {
        typeWithHeal(repo.inputPassword, "Password Input", password);
        hideKeyboard();
    }

    public void tapLoginButton() {
        clickWithHeal(repo.loginButton, "Login Button");
    }

    public void openSignUpTab() {
        clickWithHeal(repo.signUpTabToggle, "Sign Up Tab");
    }

    public void openLoginTab() {
        clickWithHeal(repo.loginTabToggle, "Login Tab Toggle");
    }

    public boolean isSuccessAlertDisplayed() {
        try {
            waitUntilElementIsVisible(repo.alertTitle);
            return repo.alertTitle.getText().equalsIgnoreCase("Success")
                    && repo.alertMessage.getText().toLowerCase().contains("logged in");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Live-run finding: {@code appium:autoAcceptAlerts} (needed on iOS for the real system
     * permission alerts — see PermissionsPage) applies to EVERY {@code XCUIElementTypeAlert}, not
     * just OS-level ones — it also auto-taps this app-level "Success" alert's default button
     * before this method gets a chance to. Treat "already gone" as success rather than racing a
     * hard wait-then-click against it.
     */
    public void dismissAlert() {
        List<WebElement> okButton = driver.findElements(By.id("android:id/button1"));
        if (isIOS()) {
            okButton = driver.findElements(io.appium.java_client.AppiumBy.accessibilityId("OK"));
        }
        if (okButton.isEmpty()) {
            return; // already dismissed (e.g. by autoAcceptAlerts on iOS)
        }
        try {
            clickWithHeal(okButton.get(0), "Alert OK Button");
        } catch (RuntimeException e) {
            logger.debug("Alert OK button disappeared before it could be clicked — already dismissed");
        }
    }

    /**
     * Android's UI hierarchy exposes visible text via {@code @text}; iOS's has no such attribute
     * — the equivalent content lives in {@code @label}/{@code @name}/{@code @value} (confirmed
     * live). Checking all four keeps this one query correct on both platforms.
     */
    public boolean isValidationMessageDisplayed(String partialText) {
        try {
            List<WebElement> matches = driver.findElements(By.xpath(
                    "//*[contains(@text,'" + partialText + "') or contains(@label,'" + partialText
                            + "') or contains(@name,'" + partialText + "') or contains(@value,'" + partialText + "')]"));
            return !matches.isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
