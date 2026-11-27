package com.codecraft.wdiodemoapp.pageobjects.login;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class LoginPageObjectRepository extends Object_Base {

    public LoginPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(accessibility = "Login-screen")
    @iOSXCUITFindBy(accessibility = "Login-screen")
    public WebElement loginScreen;

    @AndroidFindBy(accessibility = "input-email")
    @iOSXCUITFindBy(accessibility = "input-email")
    public WebElement inputEmail;

    @AndroidFindBy(accessibility = "input-password")
    @iOSXCUITFindBy(accessibility = "input-password")
    public WebElement inputPassword;

    @AndroidFindBy(accessibility = "button-LOGIN")
    @iOSXCUITFindBy(accessibility = "button-LOGIN")
    public WebElement loginButton;

    @AndroidFindBy(accessibility = "button-login-container")
    @iOSXCUITFindBy(accessibility = "button-login-container")
    public WebElement loginTabToggle;

    @AndroidFindBy(accessibility = "button-sign-up-container")
    @iOSXCUITFindBy(accessibility = "button-sign-up-container")
    public WebElement signUpTabToggle;

    // The success alert is a NATIVE OS dialog on both platforms, so it has no shared app testID.
    // Android: app-themed dialog (confirmed live — this specific title TextView is
    // com.wdiodemoapp:id/alert_title, NOT android:id/alert_title like its own message/button1).
    // iOS: a UIAlertController exposes its title/message/button as plain accessibility names
    // (no resource-id concept exists on iOS at all) — confirmed live via screenshot + source dump.
    @AndroidFindBy(id = "com.wdiodemoapp:id/alert_title")
    @iOSXCUITFindBy(accessibility = "Success")
    public WebElement alertTitle;

    @AndroidFindBy(id = "android:id/message")
    @iOSXCUITFindBy(accessibility = "You are logged in!")
    public WebElement alertMessage;

    @AndroidFindBy(id = "android:id/button1")
    @iOSXCUITFindBy(accessibility = "OK")
    public WebElement alertOkButton;
}
