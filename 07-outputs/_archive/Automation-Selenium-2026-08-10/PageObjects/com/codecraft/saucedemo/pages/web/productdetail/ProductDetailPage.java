package com.codecraft.saucedemo.pages.web.productdetail;

import com.codecraft.saucedemo.base.BasePage;
import com.codecraft.saucedemo.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Module: Product Detail — https://www.saucedemo.com/inventory-item.html?id=&lt;N&gt;
 */
public class ProductDetailPage extends BasePage {

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators ====

    @FindBy(css = ".inventory_details_name")
    private WebElement productName;

    @FindBy(css = ".inventory_details_desc")
    private WebElement productDescription;

    @FindBy(css = ".inventory_details_price")
    private WebElement productPrice;

    @FindBy(css = ".inventory_details_img")
    private WebElement productImage;

    @FindBy(css = "button.btn_inventory")
    private WebElement addToCartButton;

    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;

    // ==== Validations ====

    public boolean isOnDetailPage() {
        return WaitUtils.waitForUrlContains(driver, "/inventory-item.html");
    }

    public String getProductName() {
        return waitForVisibility(productName).getText();
    }

    public String getProductDescription() {
        return waitForVisibility(productDescription).getText();
    }

    public String getProductPrice() {
        return waitForVisibility(productPrice).getText();
    }

    public boolean isAddToCartButtonVisible() {
        return isDisplayed(addToCartButton);
    }

    public String getAddToCartButtonText() {
        return waitForVisibility(addToCartButton).getText();
    }

    // ==== Page Actions ====

    public void addToCart() {
        waitForClickable(addToCartButton).click();
    }

    public void clickBackToProducts() {
        waitForClickable(backToProductsButton).click();
    }
}
