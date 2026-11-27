package com.codecraft.saucedemo.tests.orderconfirmation;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.cart.CartPage;
import com.codecraft.saucedemo.pages.web.checkout.CheckoutPage;
import com.codecraft.saucedemo.pages.web.inventory.InventoryPage;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.pages.web.orderconfirmation.OrderConfirmationPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Module: Order Confirmation — source: 07-outputs/TestCases/FunctionalTestCases.md
 * Target: https://www.saucedemo.com/checkout-complete.html
 */
public class OrderConfirmationTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/orderconfirmation.csv";

    @DataProvider(name = "orderConfirmationData")
    public Object[][] orderConfirmationData(Method method) {
        return TestDataArgumentsProvider.singleRow(CSV, method);
    }

    private OrderConfirmationPage completeCheckout(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: login should reach Inventory page");
        InventoryPage inventoryPage = new InventoryPage(driver());
        inventoryPage.addToCartByName(data.get("ProductName"));
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver());
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.assertTrue(checkoutPage.isOnStepTwo(), "Precondition: should reach Step 2");
        checkoutPage.clickFinish();
        return new OrderConfirmationPage(driver());
    }

    // TC_ORDERCONFIRM_001
    @Test(dataProvider = "orderConfirmationData", groups = {"smoke", "regression"})
    public void successMessageDisplayedAfterCheckout(Map<String, String> data) {
        OrderConfirmationPage confirmationPage = completeCheckout(data);
        AssertionUtil.softAssertTrue(confirmationPage.isOnConfirmationPage(), "Should be on Order Confirmation page");
        AssertionUtil.softAssertTrue(confirmationPage.isOrderCompleteMessageDisplayed(), "Success message should be displayed");
        AssertionUtil.assertAll();
        ExtentReportManager.pass("Order Confirmation success message: " + confirmationPage.getCompleteHeaderText());
    }

    // TC_ORDERCONFIRM_002
    @Test(dataProvider = "orderConfirmationData", groups = {"regression"})
    public void backHomeNavigatesToInventory(Map<String, String> data) {
        OrderConfirmationPage confirmationPage = completeCheckout(data);
        AssertionUtil.assertTrue(confirmationPage.isOnConfirmationPage(), "Precondition: should be on Order Confirmation page");
        confirmationPage.clickBackHome();
        InventoryPage inventoryPage = new InventoryPage(driver());
        AssertionUtil.assertTrue(inventoryPage.isOnInventoryPage(), "Back Home should navigate to the Inventory page");
    }

    // TC_ORDERCONFIRM_003
    @Test(dataProvider = "orderConfirmationData", groups = {"regression"})
    public void cartIsEmptyAfterOrderCompletion(Map<String, String> data) {
        OrderConfirmationPage confirmationPage = completeCheckout(data);
        confirmationPage.clickBackHome();
        InventoryPage inventoryPage = new InventoryPage(driver());
        AssertionUtil.assertTrue(inventoryPage.isOnInventoryPage(), "Precondition: should be on Inventory page");
        AssertionUtil.assertTrue(inventoryPage.isCartBadgeAbsent(), "No cart badge should be displayed after order completion");
    }
}
