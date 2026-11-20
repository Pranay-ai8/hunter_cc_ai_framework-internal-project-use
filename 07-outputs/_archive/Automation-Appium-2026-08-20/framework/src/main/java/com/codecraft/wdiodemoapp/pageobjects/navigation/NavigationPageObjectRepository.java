package com.codecraft.wdiodemoapp.pageobjects.navigation;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class NavigationPageObjectRepository extends Object_Base {

    public NavigationPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // Bottom tab bar (default-starred items) — identifiers confirmed identical on iOS live.
    @AndroidFindBy(accessibility = "Home")
    @iOSXCUITFindBy(accessibility = "Home")
    public WebElement tabHome;

    @AndroidFindBy(accessibility = "Webview")
    @iOSXCUITFindBy(accessibility = "Webview")
    public WebElement tabWebview;

    @AndroidFindBy(accessibility = "Login")
    @iOSXCUITFindBy(accessibility = "Login")
    public WebElement tabLogin;

    @AndroidFindBy(accessibility = "Forms")
    @iOSXCUITFindBy(accessibility = "Forms")
    public WebElement tabForms;

    @AndroidFindBy(accessibility = "Swipe")
    @iOSXCUITFindBy(accessibility = "Swipe")
    public WebElement tabSwipe;

    @AndroidFindBy(accessibility = "Drag")
    @iOSXCUITFindBy(accessibility = "Drag")
    public WebElement tabDrag;

    @AndroidFindBy(accessibility = "Menu")
    @iOSXCUITFindBy(accessibility = "Menu")
    public WebElement tabMenu;

    // Side panel
    @AndroidFindBy(accessibility = "tab-side-menu-panel")
    @iOSXCUITFindBy(accessibility = "tab-side-menu-panel")
    public WebElement sideMenuPanel;

    @AndroidFindBy(accessibility = "side-menu-item-home")
    @iOSXCUITFindBy(accessibility = "side-menu-item-home")
    public WebElement sideMenuItemHome;

    @AndroidFindBy(accessibility = "side-menu-item-webview")
    @iOSXCUITFindBy(accessibility = "side-menu-item-webview")
    public WebElement sideMenuItemWebview;

    @AndroidFindBy(accessibility = "side-menu-item-login")
    @iOSXCUITFindBy(accessibility = "side-menu-item-login")
    public WebElement sideMenuItemLogin;

    @AndroidFindBy(accessibility = "side-menu-item-forms")
    @iOSXCUITFindBy(accessibility = "side-menu-item-forms")
    public WebElement sideMenuItemForms;

    @AndroidFindBy(accessibility = "side-menu-item-swipe")
    @iOSXCUITFindBy(accessibility = "side-menu-item-swipe")
    public WebElement sideMenuItemSwipe;

    @AndroidFindBy(accessibility = "side-menu-item-drag")
    @iOSXCUITFindBy(accessibility = "side-menu-item-drag")
    public WebElement sideMenuItemDrag;

    @AndroidFindBy(accessibility = "side-menu-item-permissions")
    @iOSXCUITFindBy(accessibility = "side-menu-item-permissions")
    public WebElement sideMenuItemPermissions;

    @AndroidFindBy(accessibility = "side-menu-item-data-management")
    @iOSXCUITFindBy(accessibility = "side-menu-item-data-management")
    public WebElement sideMenuItemDataManagement;
}
