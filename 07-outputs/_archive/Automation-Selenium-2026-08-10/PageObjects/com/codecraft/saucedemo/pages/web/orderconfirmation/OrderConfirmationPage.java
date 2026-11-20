package com.codecraft.saucedemo.pages.web.orderconfirmation;

import com.codecraft.saucedemo.base.BasePage;
import com.codecraft.saucedemo.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Module: Order Confirmation — https://www.saucedemo.com/checkout-complete.html
 */
public class OrderConfirmationPage extends BasePage {

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators ====

    @FindBy(css = ".complete-header")
    private WebElement completeHeader;

    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    // ==== Validations ====

    public boolean isOnConfirmationPage() {
        return WaitUtils.waitForUrlContains(driver, "/checkout-complete.html");
    }

    public boolean isOrderCompleteMessageDisplayed() {
        return isDisplayed(completeHeader);
    }

    public String getCompleteHeaderText() {
        return waitForVisibility(completeHeader).getText();
    }

    // ==== Page Actions ====

    public void clickBackHome() {
        waitForClickable(backHomeButton).click();
    }
}
