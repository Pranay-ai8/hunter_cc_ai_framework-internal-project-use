package com.codecraft.wdiodemoapp.pageobjects.swipe;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class SwipePageObjectRepository extends Object_Base {

    public SwipePageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(accessibility = "Swipe-screen")
    @iOSXCUITFindBy(accessibility = "Swipe-screen")
    public WebElement swipeScreen;

    // Android: plain `id=` (AppiumBy.id) auto-prefixes the current app package when the
    // resource-id has no colon, which breaks these deliberately unprefixed React Native
    // resource-ids — xpath matches the literal value instead (found live: resource-id="Carousel",
    // no package prefix). iOS: the same value is exposed directly as accessibility `name`
    // (confirmed live) — no such prefixing quirk exists there.
    @AndroidFindBy(xpath = "//*[@resource-id='Carousel']")
    @iOSXCUITFindBy(accessibility = "Carousel")
    public WebElement carousel;

    @AndroidFindBy(xpath = "//*[@resource-id='__CAROUSEL_ITEM_0__']")
    @iOSXCUITFindBy(accessibility = "__CAROUSEL_ITEM_0__")
    public WebElement carouselItem0;

    @AndroidFindBy(xpath = "//*[@resource-id='__CAROUSEL_ITEM_1__']")
    @iOSXCUITFindBy(accessibility = "__CAROUSEL_ITEM_1__")
    public WebElement carouselItem1;
}
