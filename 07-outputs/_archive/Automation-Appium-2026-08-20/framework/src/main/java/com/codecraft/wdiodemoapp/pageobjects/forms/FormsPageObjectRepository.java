package com.codecraft.wdiodemoapp.pageobjects.forms;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class FormsPageObjectRepository extends Object_Base {

    public FormsPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(accessibility = "Forms-screen")
    @iOSXCUITFindBy(accessibility = "Forms-screen")
    public WebElement formsScreen;

    @AndroidFindBy(accessibility = "text-input")
    @iOSXCUITFindBy(accessibility = "text-input")
    public WebElement textInput;

    @AndroidFindBy(accessibility = "input-text-result")
    @iOSXCUITFindBy(accessibility = "input-text-result")
    public WebElement textInputResult;

    @AndroidFindBy(accessibility = "switch")
    @iOSXCUITFindBy(accessibility = "switch")
    public WebElement switchControl;

    @AndroidFindBy(accessibility = "switch-text")
    @iOSXCUITFindBy(accessibility = "switch-text")
    public WebElement switchText;

    @AndroidFindBy(accessibility = "Dropdown")
    @iOSXCUITFindBy(accessibility = "Dropdown")
    public WebElement dropdown;

    // iOS-only: tapping the "Dropdown" container itself did not reliably open the picker (live
    // testing: silent no-op via both element.click() and a raw W3C coordinate tap); tapping this
    // chevron icon specifically does. This dropdown renders as a native XCUIElementTypePickerWheel
    // spinner on iOS, not a tappable option list like Android's AlertDialog-style list — see
    // FormsPage.selectDropdownOption() for the resulting platform-specific interaction.
    @iOSXCUITFindBy(accessibility = "dropdown-chevron")
    public WebElement dropdownChevronIOS;

    // Android: plain `id=` auto-prefixes the app package when the resource-id has no colon,
    // which breaks this deliberately unprefixed value — xpath matches it literally instead.
    // iOS: no "resource-id" concept exists at all, but this same value IS exposed directly as
    // the element's accessibility `name` (confirmed live) — accessibility lookup works as-is.
    @AndroidFindBy(xpath = "//*[@resource-id='text_input']")
    @iOSXCUITFindBy(accessibility = "text_input")
    public WebElement dropdownValue;

    @AndroidFindBy(accessibility = "button-Active")
    @iOSXCUITFindBy(accessibility = "button-Active")
    public WebElement buttonActive;

    @AndroidFindBy(accessibility = "button-Inactive")
    @iOSXCUITFindBy(accessibility = "button-Inactive")
    public WebElement buttonInactive;
}
