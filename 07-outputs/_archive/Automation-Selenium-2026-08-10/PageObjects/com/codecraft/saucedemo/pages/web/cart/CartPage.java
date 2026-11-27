package com.codecraft.saucedemo.pages.web.cart;

import com.codecraft.saucedemo.base.BasePage;
import com.codecraft.saucedemo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module: Cart — https://www.saucedemo.com/cart.html
 */
public class CartPage extends BasePage {

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators ====

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = ".cart_item .inventory_item_name")
    private List<WebElement> cartItemNames;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    // Dynamic locator pattern — scoped to the cart row containing the matching
    // product name (locator-standards.md "Locator Scoping"), same rationale as
    // InventoryPage#inventoryItemByName.
    private WebElement cartItemByName(String productName) {
        // contains(@class, ...) rather than @class='...': the live app renders
        // class="inventory_item_name " with a trailing space, so an exact-match
        // predicate never matches and this wait would time out every time.
        By locator = By.xpath(
                "//div[@class='cart_item'][.//div[contains(@class,'inventory_item_name') and normalize-space()='"
                        + productName + "']]");
        return WaitUtils.waitForPresence(driver, locator);
    }

    // ==== Validations ====

    public boolean isOnCartPage() {
        return WaitUtils.waitForUrlContains(driver, "/cart.html");
    }

    public List<String> getCartItemNames() {
        if (cartItems.isEmpty()) {
            return List.of();
        }
        WaitUtils.waitForVisibilityOfAll(driver, cartItemNames);
        return cartItemNames.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public boolean isItemInCart(String productName) {
        return getCartItemNames().contains(productName);
    }

    // ==== Page Actions ====

    public CartPage removeItemByName(String productName) {
        WebElement item = cartItemByName(productName);
        WebElement removeButton = item.findElement(By.cssSelector("button.cart_button"));
        WaitUtils.waitForClickable(driver, removeButton).click();
        return this;
    }

    public void clickCheckout() {
        waitForClickable(checkoutButton).click();
    }

    public void clickContinueShopping() {
        waitForClickable(continueShoppingButton).click();
    }
}
