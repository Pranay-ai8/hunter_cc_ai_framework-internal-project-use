package com.codecraft.wdiodemoapp.pageobjects.home;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class HomePageObjectRepository extends Object_Base {

    public HomePageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // Confirmed live on both platforms: this app's testID props map 1:1 to Android content-desc
    // and iOS accessibility name, so every accessibility-based locator in this project carries
    // the identical string across @AndroidFindBy/@iOSXCUITFindBy unless noted otherwise.
    @AndroidFindBy(accessibility = "Home-screen")
    @iOSXCUITFindBy(accessibility = "Home-screen")
    public WebElement homeScreen;
}
