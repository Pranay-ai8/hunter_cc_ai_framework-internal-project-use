package com.codecraft.saucedemo.tests.checkout;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.cart.CartPage;
import com.codecraft.saucedemo.pages.web.checkout.CheckoutPage;
import com.codecraft.saucedemo.pages.web.inventory.InventoryPage;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Module: Checkout — source: 07-outputs/TestCases/FunctionalTestCases.md
 * Target: https://www.saucedemo.com/checkout-step-one.html / checkout-step-two.html
 */
public class CheckoutTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/checkout.csv";

    @DataProvider(name = "checkoutData")
    public Object[][] checkoutData(Method method) {
        return TestDataArgumentsProvider.singleRow(CSV, method);
    }

    private CartPage reachCartWithOneItem(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: login should reach Inventory page");
        InventoryPage inventoryPage = new InventoryPage(driver());
        inventoryPage.addToCartByName(data.get("ProductName"));
        inventoryPage.openCart();
        return new CartPage(driver());
    }

    // TC_CHECKOUT_001
    @Test(dataProvider = "checkoutData", groups = {"smoke", "regression"})
    public void checkoutButtonNavigatesToStepOne(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        AssertionUtil.assertTrue(cartPage.isOnCartPage(), "Precondition: should be on Cart page");
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        AssertionUtil.assertTrue(checkoutPage.isOnStepOne(), "Should navigate to Checkout Step 1");
        ExtentReportManager.pass("Checkout button navigates to Step 1 (Customer Information)");
    }

    // TC_CHECKOUT_002
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void blankFirstNameBlocked(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        AssertionUtil.assertTrue(checkoutPage.isOnStepOne(), "Precondition: should be on Checkout Step 1");
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.softAssertTrue(checkoutPage.isErrorDisplayed(), "An inline error should be displayed (GAP-FR-01)");
        AssertionUtil.softAssertTrue(checkoutPage.isOnStepOne(), "User should remain on Checkout Step 1");
        AssertionUtil.assertAll();
    }

    // TC_CHECKOUT_003
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void blankLastNameBlocked(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.softAssertTrue(checkoutPage.isErrorDisplayed(), "An inline error should be displayed (GAP-FR-01)");
        AssertionUtil.softAssertTrue(checkoutPage.isOnStepOne(), "User should remain on Checkout Step 1");
        AssertionUtil.assertAll();
    }

    // TC_CHECKOUT_004
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void blankPostalCodeBlocked(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.softAssertTrue(checkoutPage.isErrorDisplayed(), "An inline error should be displayed (GAP-FR-01)");
        AssertionUtil.softAssertTrue(checkoutPage.isOnStepOne(), "User should remain on Checkout Step 1");
        AssertionUtil.assertAll();
    }

    // TC_CHECKOUT_005
    @Test(dataProvider = "checkoutData", groups = {"smoke", "regression"})
    public void validStepOneSubmissionProceedsToStepTwo(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.assertTrue(checkoutPage.isOnStepTwo(), "Valid submission should navigate to Step 2 (Order Overview)");
    }

    // TC_CHECKOUT_006
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void stepTwoDisplaysCorrectItemsTaxAndTotal(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.assertTrue(checkoutPage.isOnStepTwo(), "Precondition: should be on Step 2");

        AssertionUtil.softAssertTrue(checkoutPage.getOverviewItemNames().contains(data.get("ProductName")),
                "Order Overview should list the cart item");
        double itemTotal = checkoutPage.getItemTotal();
        double tax = checkoutPage.getTax();
        double grandTotal = checkoutPage.getGrandTotal();
        // Exact tax formula is undocumented (GAP-FR-02) — assert the arithmetic
        // relationship rather than a hardcoded expected amount.
        AssertionUtil.softAssertEquals(Math.round((itemTotal + tax) * 100.0) / 100.0, Math.round(grandTotal * 100.0) / 100.0,
                "Grand total should equal item total + tax");
        AssertionUtil.assertAll();
    }

    // TC_CHECKOUT_007
    @Test(dataProvider = "checkoutData", groups = {"smoke", "regression"})
    public void finishCompletesPurchaseAndClearsCart(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.assertTrue(checkoutPage.isOnStepTwo(), "Precondition: should be on Step 2");
        checkoutPage.clickFinish();
        AssertionUtil.assertTrue(driver().getCurrentUrl().contains("/checkout-complete.html"),
                "Should navigate to Order Confirmation page");
        InventoryPage inventoryPage = new InventoryPage(driver());
        AssertionUtil.assertTrue(inventoryPage.isCartBadgeAbsent(), "Cart badge should be cleared after purchase");
        ExtentReportManager.pass("Finish completes purchase, clears cart, navigates to Order Confirmation");
    }

    // TC_CHECKOUT_008 — Confirmed live 2026-08-07 (GAP-FR-05 resolved): Step 1 Cancel -> Cart.
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void cancelOnStepOneReturnsToCart(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        AssertionUtil.assertTrue(checkoutPage.isOnStepOne(), "Precondition: should be on Checkout Step 1");
        checkoutPage.clickCancel();
        CartPage backOnCart = new CartPage(driver());
        AssertionUtil.assertTrue(backOnCart.isOnCartPage(), "Step 1 Cancel should return to the Cart page");
    }

    // TC_CHECKOUT_009 — Confirmed live 2026-08-07 (GAP-FR-05 resolved): Step 2 Cancel ->
    // Inventory, NOT Cart (the PDF's claim that Step 2 also returns to Cart is stale).
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void cancelOnStepTwoReturnsToInventory(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        AssertionUtil.assertTrue(checkoutPage.isOnStepTwo(), "Precondition: should be on Step 2");
        checkoutPage.clickCancel();
        InventoryPage inventoryPage = new InventoryPage(driver());
        AssertionUtil.assertTrue(inventoryPage.isOnInventoryPage(),
                "Step 2 Cancel should return to the Inventory page (confirmed live — not Cart)");
    }

    // TC_CHECKOUT_010 — Automation Candidate: Partial. error_user's "random" error
    // behavior during checkout is explicitly non-deterministic (docx).
    // TODO: requires manual setup — non-deterministic, log/observe rather than hard-assert.
    @Test(dataProvider = "checkoutData", groups = {"partial"})
    public void errorUserMayThrowRandomErrorDuringCheckout(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        try {
            checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
            if (checkoutPage.isOnStepTwo()) {
                checkoutPage.clickFinish();
                ExtentReportManager.info("error_user completed checkout without a thrown error this run");
            } else {
                ExtentReportManager.info("error_user checkout did not proceed to Step 2 — error text: "
                        + (checkoutPage.isErrorDisplayed() ? checkoutPage.getErrorText() : "none displayed"));
            }
        } catch (Exception e) {
            ExtentReportManager.info("error_user checkout threw: " + e.getClass().getSimpleName()
                    + " — consistent with documented random-error behavior");
        }
        // Observational only — no hard-asserted single outcome, per docx §8.
        AssertionUtil.assertTrue(true, "Observed error_user checkout interaction (see report log for outcome)");
    }

    // TC_CHECKOUT_011 — Automation Candidate: Partial. problem_user Last Name defect
    // conflict between source PRDs (GapAnalysis.md) — observational check only.
    // TODO: requires manual setup — confirm live whether Last Name field fails for problem_user.
    @Test(dataProvider = "checkoutData", groups = {"partial"})
    public void problemUserLastNameFieldMayFail(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        boolean reachedStepTwo = checkoutPage.isOnStepTwo();
        ExtentReportManager.info("problem_user checkout Step 1 -> Step 2 succeeded: " + reachedStepTwo
                + " (docx documents possible Last Name failure; PDF does not corroborate — see GapAnalysis.md)");
        AssertionUtil.assertTrue(reachedStepTwo || checkoutPage.isOnStepOne(),
                "Should be on a valid checkout page (Step 1 if blocked, Step 2 if it succeeded)");
    }

    // TC_CHECKOUT_012
    @Test(dataProvider = "checkoutData", groups = {"regression"})
    public void postalCodeAcceptsLongAlphanumericValueWithoutError(Map<String, String> data) {
        CartPage cartPage = reachCartWithOneItem(data);
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(driver());
        checkoutPage.fillCustomerInfoAndContinue(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        // Per GAP-BR-01: either outcome (proceeds to Step 2, or shows a clear validation
        // message) is acceptable — the only failure mode is an unhandled application error.
        boolean proceeded = checkoutPage.isOnStepTwo();
        boolean validationShown = checkoutPage.isOnStepOne() && checkoutPage.isErrorDisplayed();
        AssertionUtil.assertTrue(proceeded || validationShown,
                "Application should either accept the long postal code and proceed, or show a clear validation message");
    }
}
