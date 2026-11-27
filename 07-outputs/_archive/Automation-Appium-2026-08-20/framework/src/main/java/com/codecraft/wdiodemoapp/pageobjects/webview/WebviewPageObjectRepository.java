package com.codecraft.wdiodemoapp.pageobjects.webview;

import com.codecraft.wdiodemoapp.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class WebviewPageObjectRepository extends Object_Base {

    public WebviewPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(className = "android.webkit.WebView")
    @iOSXCUITFindBy(className = "XCUIElementTypeWebView")
    public WebElement webView;
}
