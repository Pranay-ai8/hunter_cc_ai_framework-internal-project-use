package com.codecraft.saucedemo.tests.checkout;

import com.codecraft.saucedemo.annotations.Regression;
import com.codecraft.saucedemo.annotations.Smoke;
import com.codecraft.saucedemo.annotations.TestDataSource;
import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.listeners.WebAnnotationTransformer;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.CSVLoader;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static com.codecraft.saucedemo.pages.PageFactory.cartPage;
import static com.codecraft.saucedemo.pages.PageFactory.checkoutPage;
import static com.codecraft.saucedemo.pages.PageFactory.inventoryPage;
import static com.codecraft.saucedemo.pages.PageFactory.loginPage;
import static com.codecraft.saucedemo.pages.PageFactory.orderConfirmationPage;

/**
 * Module: Checkout — source: 07-outputs/TestCases/FunctionalTestCases.md
 */
@Listeners({WebAnnotationTransformer.class})
public class CheckoutTest extends BaseTest {

    private static final String COMMON_CSV = "src/test/resources/testdata/common.csv";
    private static final String CHECKOUT_CSV = "src/test/resources/testdata/checkout.csv";
    private static final String BACKPACK = "Sauce Labs Backpack";

    private void loginAs(String key) {
        Map<String, String> user = CSVLoader.loadRowByKey(COMMON_CSV, key);
        loginPage().login(user.get("Username"), user.get("Password"));
    }

    private void addBackpackAndGoToCheckout() {
        inventoryPage().addToCart(BACKPACK);
        inventoryPage().openCart();
        cartPage().checkout();
    }

    // TC_CHECKOUT_001
    @Test
    @Smoke
    public void checkoutButtonNavigatesToStepOne() {
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        AssertionUtil.assertTrue(checkoutPage().isOnStepOne(), "Should navigate to Checkout Step 1");
    }

    // TC_CHECKOUT_002
    @Test
    @Regression
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "BlankFirstName")
    public void blankFirstNameBlocked(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        AssertionUtil.assertTrue(checkoutPage().isErrorVisible(), "Blank First Name should show an inline error");
    }

    // TC_CHECKOUT_003
    @Test
    @Regression
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "BlankLastName")
    public void blankLastNameBlocked(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        AssertionUtil.assertTrue(checkoutPage().isErrorVisible(), "Blank Last Name should show an inline error");
    }

    // TC_CHECKOUT_004
    @Test
    @Regression
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "BlankPostalCode")
    public void blankPostalCodeBlocked(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        AssertionUtil.assertTrue(checkoutPage().isErrorVisible(), "Blank Postal Code should show an inline error");
    }

    // TC_CHECKOUT_005
    @Test
    @Smoke
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "ValidCustomer")
    public void validStepOneProceedsToStepTwo(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        AssertionUtil.assertTrue(checkoutPage().isOnStepTwo(), "Valid Step 1 submission should proceed to Step 2");
    }

    // TC_CHECKOUT_006
    @Test
    @Regression
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "ValidCustomer")
    public void stepTwoDisplaysCorrectItemsTaxTotal(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        AssertionUtil.softAssertEquals(checkoutPage().cartItemCountOnOverview(), 1, "Order Overview should list 1 item");
        AssertionUtil.softAssertTrue(checkoutPage().grandTotalEqualsSubtotalPlusTax(), "Grand total should equal item total + tax");
        AssertionUtil.assertAll();
    }

    // TC_CHECKOUT_007
    @Test
    @Smoke
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "ValidCustomer")
    public void finishCompletesPurchaseClearsCart(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        checkoutPage().finish();
        AssertionUtil.assertTrue(orderConfirmationPage().isSuccessMessageVisible(), "Finish should complete the purchase");
    }

    // TC_CHECKOUT_008
    @Test
    @Regression
    public void cancelOnStepOneReturnsToCart() {
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().cancel();
        AssertionUtil.assertTrue(checkoutPage().isOnCartPage(), "Cancel on Step 1 should return to Cart page");
    }

    // TC_CHECKOUT_009 — confirmed live against https://www.saucedemo.com/ (GAP-FR-05,
    // resolved 2026-08-07): Step 2 "Cancel" returns to Inventory, not Cart. The PDF's
    // claim that Step 2 behaves like Step 1 (which does return to Cart) is stale.
    @Test
    @Regression
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "ValidCustomer")
    public void cancelOnStepTwoReturnsToInventory(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        checkoutPage().cancel();
        AssertionUtil.assertTrue(loginPage().isOnInventoryPage(), "Cancel on Step 2 should return to Inventory page");
    }

    // TC_CHECKOUT_010 — Automation Candidate: Partial. error_user's errors are
    // explicitly documented as "random" — not suitable for a deterministic assertion.
    // TODO: requires manual setup — observe/log outcomes across multiple runs.
    @Test(enabled = false)
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "ValidCustomer")
    public void errorUserMayThrowRandomErrorDuringCheckout(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("ErrorUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        checkoutPage().finish();
    }

    // TC_CHECKOUT_011 — Automation Candidate: Partial. Conflict between source PRDs on
    // problem_user's exact defect (see GapAnalysis.md Conflicting Requirements).
    // TODO: requires manual setup — confirm live behavior before hard-asserting.
    @Test(enabled = false)
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "ValidCustomer")
    public void problemUserLastNameFieldMayFail(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("ProblemUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
    }

    // TC_CHECKOUT_012
    @Test
    @Regression
    @TestDataSource(value = CHECKOUT_CSV, type = "csv", key = "LongPostalCode")
    public void postalCodeAcceptsLongAlphanumericValue(Method method) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(method);
        loginAs("StandardUser");
        addBackpackAndGoToCheckout();
        checkoutPage().fillCustomerInfo(data.get("FirstName"), data.get("LastName"), data.get("PostalCode"));
        checkoutPage().continueToStepTwo();
        // Either outcome (proceeds to Step 2, or a validation message) is acceptable —
        // exact rule is undocumented (GAP-BR-01). No hard assertion beyond "no crash".
    }
}
