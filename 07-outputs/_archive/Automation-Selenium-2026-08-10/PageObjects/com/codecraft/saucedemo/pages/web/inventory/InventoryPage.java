package com.codecraft.saucedemo.pages.web.inventory;

import com.codecraft.saucedemo.base.BasePage;
import com.codecraft.saucedemo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module: Product Catalog — https://www.saucedemo.com/inventory.html
 * Locators are the REAL, documented selectors from RequirementAnalysis.md §12.
 */
public class InventoryPage extends BasePage {

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators ====

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> itemPrices;

    @FindBy(css = ".inventory_item_img img")
    private List<WebElement> itemImages;

    @FindBy(css = ".product_sort_container")
    private WebElement sortDropdown;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartLink;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement hamburgerMenuButton;

    // Dynamic locator pattern (locator-standards.md "Dynamic Locator Patterns") — the
    // Add-to-cart/Remove button id is derived from the product name by the app itself
    // (e.g. "add-to-cart-sauce-labs-backpack"), which is brittle to hardcode for every
    // product; scoping to the item card containing the matching product name is the
    // stable alternative recommended by locator-standards.md "Locator Scoping".
    private WebElement inventoryItemByName(String productName) {
        // contains(@class, ...) rather than @class='...': the live app renders
        // class="inventory_item_name " with a trailing space, so an exact-match
        // predicate never matches and this wait would time out every time.
        By locator = By.xpath(
                "//div[@class='inventory_item'][.//div[contains(@class,'inventory_item_name') and normalize-space()='"
                        + productName + "']]");
        return WaitUtils.waitForPresence(driver, locator);
    }

    // ==== Validations ====

    public boolean isOnInventoryPage() {
        return WaitUtils.waitForUrlContains(driver, "/inventory.html");
    }

    public int getProductCount() {
        return WaitUtils.waitForVisibilityOfAll(driver, inventoryItems).size();
    }

    public List<String> getProductNames() {
        WaitUtils.waitForVisibilityOfAll(driver, itemNames);
        return itemNames.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<Double> getProductPrices() {
        WaitUtils.waitForVisibilityOfAll(driver, itemPrices);
        return itemPrices.stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "").trim()))
                .collect(Collectors.toList());
    }

    public List<String> getProductImageSources() {
        WaitUtils.waitForVisibilityOfAll(driver, itemImages);
        return itemImages.stream().map(e -> e.getAttribute("src")).collect(Collectors.toList());
    }

    public int getCartBadgeCount() {
        if (!isDisplayed(cartBadge)) {
            return 0;
        }
        return Integer.parseInt(cartBadge.getText().trim());
    }

    public boolean isCartBadgeAbsent() {
        return !isDisplayed(cartBadge);
    }

    public boolean isAddToCartButtonVisibleFor(String productName) {
        WebElement button = inventoryItemByName(productName).findElement(By.cssSelector("button.btn_inventory"));
        return button.getText().trim().equalsIgnoreCase("Add to cart");
    }

    // ==== Page Actions ====

    public InventoryPage addToCartByName(String productName) {
        WebElement item = inventoryItemByName(productName);
        WebElement button = item.findElement(By.cssSelector("button.btn_inventory"));
        WaitUtils.waitForClickable(driver, button).click();
        return this;
    }

    public InventoryPage removeFromCartByName(String productName) {
        WebElement item = inventoryItemByName(productName);
        WebElement button = item.findElement(By.cssSelector("button.btn_inventory"));
        WaitUtils.waitForClickable(driver, button).click();
        return this;
    }

    public InventoryPage selectSortOption(String label) {
        WebElement dropdown = waitForClickable(sortDropdown);
        new Select(dropdown).selectByVisibleText(label);
        return this;
    }

    public void clickProductName(String productName) {
        WebElement item = inventoryItemByName(productName);
        WebElement nameLink = item.findElement(By.cssSelector(".inventory_item_name"));
        WaitUtils.waitForClickable(driver, nameLink).click();
    }

    public void clickProductImage(String productName) {
        WebElement item = inventoryItemByName(productName);
        WebElement image = item.findElement(By.cssSelector(".inventory_item_img a"));
        WaitUtils.waitForClickable(driver, image).click();
    }

    public void openCart() {
        waitForClickable(cartLink).click();
    }

    public void openHamburgerMenu() {
        waitForClickable(hamburgerMenuButton).click();
    }
}
