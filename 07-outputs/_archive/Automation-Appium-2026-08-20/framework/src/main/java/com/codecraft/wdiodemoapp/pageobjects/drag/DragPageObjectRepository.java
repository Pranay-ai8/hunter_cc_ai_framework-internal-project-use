package com.codecraft.wdiodemoapp.pageobjects.drag;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class DragPageObjectRepository extends Object_Base {

    public DragPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(accessibility = "Drag-drop-screen")
    @iOSXCUITFindBy(accessibility = "Drag-drop-screen")
    public WebElement dragScreen;

    // drag-*/drop-* piece and slot identifiers confirmed identical on iOS live.
    @AndroidFindBy(accessibility = "renew")
    @iOSXCUITFindBy(accessibility = "renew")
    public WebElement renewButton;

    @AndroidFindBy(accessibility = "button-Retry")
    @iOSXCUITFindBy(accessibility = "button-Retry")
    public WebElement retryButton;

    // Puzzle piece/slot ids follow the pattern {l|c|r}{1|2|3} (3x3 grid),
    // located dynamically in DragPage via By.xpath(contains(@content-desc,...) or contains(@name,...))
}
