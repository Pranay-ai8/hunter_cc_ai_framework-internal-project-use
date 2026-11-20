package com.codecraft.saucedemo.tests.cart;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.cart.CartPage;
import com.codecraft.saucedemo.pages.web.inventory.InventoryPage;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.pages.web.productdetail.ProductDetailPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Module: Cart — source: 07-outputs/TestCases/FunctionalTestCases.md
 * Target: https://www.saucedemo.com/cart.html
 */
public class CartTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/cart.csv";

    @DataProvider(name = "cartData")
    public Object[][] cartData(Method method) {
        return TestDataArgumentsProvider.singleRow(CSV, method);
    }

    private InventoryPage loginAndReachInventory(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: login should reach Inventory page");
        return new InventoryPage(driver());
    }

    // TC_CART_001
    @Test(dataProvider = "cartData", groups = {"smoke", "regression"})
    public void singleItemAddedAppearsOnCartPage(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductA");
        inventoryPage.addToCartByName(productName);
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.softAssertTrue(cartPage.isOnCartPage(), "Should be on the Cart page");
        AssertionUtil.softAssertTrue(cartPage.isItemInCart(productName), "Added product should appear on the Cart page");
        AssertionUtil.softAssertEquals(cartPage.getCartItemCount(), 1, "Cart should contain exactly 1 item");
        AssertionUtil.assertAll();
        ExtentReportManager.pass("Single item added and verified on Cart page");
    }

    // TC_CART_002
    @Test(dataProvider = "cartData", groups = {"regression"})
    public void multipleItemsAddedIndependentlyAppearOnCartPage(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productA = data.get("ProductA");
        String productB = data.get("ProductB");
        inventoryPage.addToCartByName(productA);
        inventoryPage.addToCartByName(productB);
        AssertionUtil.assertEquals(inventoryPage.getCartBadgeCount(), 2, "Cart badge should show count 2");
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.softAssertTrue(cartPage.isItemInCart(productA), "Product A should appear on Cart page");
        AssertionUtil.softAssertTrue(cartPage.isItemInCart(productB), "Product B should appear on Cart page");
        AssertionUtil.assertAll();
    }

    // TC_CART_003
    @Test(dataProvider = "cartData", groups = {"regression"})
    public void removeItemFromCartPage(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductA");
        inventoryPage.addToCartByName(productName);
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.assertTrue(cartPage.isItemInCart(productName), "Precondition: item should be in cart");
        cartPage.removeItemByName(productName);
        AssertionUtil.softAssertFalse(cartPage.isItemInCart(productName), "Item should be removed from the Cart page");
        AssertionUtil.softAssertTrue(cartPage.isCartEmpty(), "Cart badge/list should now be empty");
        AssertionUtil.assertAll();
    }

    // TC_CART_004
    @Test(dataProvider = "cartData", groups = {"regression"})
    public void removeItemDirectlyFromInventoryPage(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductA");
        inventoryPage.addToCartByName(productName);
        AssertionUtil.assertFalse(inventoryPage.isAddToCartButtonVisibleFor(productName),
                "Precondition: button should read 'Remove' after adding");
        inventoryPage.removeFromCartByName(productName);
        AssertionUtil.softAssertTrue(inventoryPage.isAddToCartButtonVisibleFor(productName),
                "Button should revert to 'Add to cart'");
        AssertionUtil.softAssertTrue(inventoryPage.isCartBadgeAbsent(), "Cart badge should decrement/disappear");
        AssertionUtil.assertAll();
    }

    // TC_CART_005
    @Test(dataProvider = "cartData", groups = {"regression"})
    public void cartContentsPersistAcrossNavigation(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductA");
        inventoryPage.addToCartByName(productName);
        inventoryPage.clickProductName(productName);
        ProductDetailPage detailPage = new ProductDetailPage(driver());
        AssertionUtil.assertTrue(detailPage.isOnDetailPage(), "Should be on Product Detail page");
        detailPage.clickBackToProducts();
        InventoryPage backOnInventory = new InventoryPage(driver());
        AssertionUtil.assertEquals(backOnInventory.getCartBadgeCount(), 1, "Cart badge should persist through navigation");
        backOnInventory.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.assertTrue(cartPage.isItemInCart(productName), "Item should still be present in the Cart");
    }

    // TC_CART_006
    @Test(dataProvider = "cartData", groups = {"regression"})
    public void continueShoppingReturnsToInventory(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.assertTrue(cartPage.isOnCartPage(), "Should be on the Cart page");
        cartPage.clickContinueShopping();
        InventoryPage backOnInventory = new InventoryPage(driver());
        AssertionUtil.assertTrue(backOnInventory.isOnInventoryPage(), "Should return to the Inventory page");
    }

    // TC_CART_007
    @Test(dataProvider = "cartData", groups = {"regression"})
    public void emptyCartDisplaysCorrectly(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        AssertionUtil.assertTrue(inventoryPage.isCartBadgeAbsent(), "Precondition: cart should start empty");
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.softAssertTrue(cartPage.isOnCartPage(), "Cart page should load with no application error");
        AssertionUtil.softAssertTrue(cartPage.isCartEmpty(), "Cart should show no line items");
        AssertionUtil.assertAll();
    }

    // TC_CART_008 — Automation Candidate: Partial. problem_user defect conflict between
    // source PRDs (GapAnalysis.md) — observational check only.
    // TODO: requires manual setup — confirm live whether Remove is non-functional for problem_user.
    @Test(dataProvider = "cartData", groups = {"partial"})
    public void problemUserRemoveButtonBehaviorInCart(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductA");
        inventoryPage.addToCartByName(productName);
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        AssertionUtil.assertTrue(cartPage.isItemInCart(productName), "Precondition: item should be in cart");
        cartPage.removeItemByName(productName);
        boolean stillPresent = cartPage.isItemInCart(productName);
        ExtentReportManager.info("problem_user Remove click — item still present after click: " + stillPresent
                + " (PDF documents non-functional Remove; docx does not corroborate — see GapAnalysis.md)");
        // Observational only: do not hard-assert either documented possibility.
        AssertionUtil.assertTrue(cartPage.isOnCartPage(), "Should still be on the Cart page after clicking Remove");
    }
}
