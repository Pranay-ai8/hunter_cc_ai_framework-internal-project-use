package com.codecraft.wdiodemoapp.pageobjects.permissions;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class PermissionsPageObjectRepository extends Object_Base {

    public PermissionsPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(accessibility = "Permissions-screen")
    @iOSXCUITFindBy(accessibility = "Permissions-screen")
    public WebElement permissionsScreen;

    @AndroidFindBy(accessibility = "permission-switch-camera")
    @iOSXCUITFindBy(accessibility = "permission-switch-camera")
    public WebElement cameraSwitch;

    @AndroidFindBy(accessibility = "permission-switch-microphone")
    @iOSXCUITFindBy(accessibility = "permission-switch-microphone")
    public WebElement microphoneSwitch;

    @AndroidFindBy(accessibility = "permission-switch-location")
    @iOSXCUITFindBy(accessibility = "permission-switch-location")
    public WebElement locationSwitch;

    @AndroidFindBy(accessibility = "permission-switch-photos")
    @iOSXCUITFindBy(accessibility = "permission-switch-photos")
    public WebElement photosSwitch;
}
