package com.codecraft.saucedemo.pages.web.login;

import com.codecraft.saucedemo.base.BasePage;
import com.codecraft.saucedemo.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Module: Authentication — https://www.saucedemo.com/
 * Locators are the REAL, documented selectors from RequirementAnalysis.md §12
 * (source: SauceDemo_Understanding_Document-ClaudeCode.docx Section 6).
 */
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators ====

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = ".error-message-container")
    private WebElement errorMessageBanner;

    // ==== Validations ====

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessageBanner);
    }

    public String getErrorText() {
        return waitForVisibility(errorMessageBanner).getText();
    }

    public boolean isOnInventoryPage() {
        return WaitUtils.waitForUrlContains(driver, "/inventory.html");
    }

    // ==== Page Actions ====

    public LoginPage enterUsername(String username) {
        WebElement field = waitForVisibility(usernameInput);
        field.clear();
        field.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement field = waitForVisibility(passwordInput);
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public void clickLogin() {
        waitForClickable(loginButton).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }
}
