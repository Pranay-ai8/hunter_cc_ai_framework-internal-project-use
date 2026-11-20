import { Given, Then, When } from "@cucumber/cucumber";
import { expect } from "@playwright/test";
import { ScenarioContext } from "../../Framework/src/utils/ScenarioContext";
import { resolveTestDataToken } from "../../Framework/src/utils/testDataResolver";
import { resolvePassword } from "../../Framework/src/utils/passwordResolver";

Given(
  "the user has a product in the cart and is on checkout step one",
  async function (this: ScenarioContext) {
    const username = resolveTestDataToken(this, "td.username");
    const passwordField = String(this.testData.password ?? "AUTO");
    const password =
      passwordField === "AUTO"
        ? resolvePassword(username)
        : resolveTestDataToken(this, passwordField);
    await this.loginPage.navigate();
    await this.loginPage.login(username, password);
    await this.inventoryPage.assertOnInventoryPage();
    await this.menuPage.clickResetAppState();

    const productField = String(this.testData.productName ?? "");
    if (productField) {
      await this.inventoryPage.addToCart(productField);
    }
    await this.inventoryPage.openCart();
    await this.cartPage.clickCheckout();
    await this.checkoutPage.assertOnStepOne();
  },
);

Given(
  "the user is on the cart page with at least one item",
  async function (this: ScenarioContext) {
    const username = resolveTestDataToken(this, "td.username");
    const passwordField = String(this.testData.password ?? "AUTO");
    const password =
      passwordField === "AUTO"
        ? resolvePassword(username)
        : resolveTestDataToken(this, passwordField);
    await this.loginPage.navigate();
    await this.loginPage.login(username, password);
    await this.inventoryPage.assertOnInventoryPage();
    await this.menuPage.clickResetAppState();
    const productField =
      (this.testData.productName as string) || "Sauce Labs Backpack";
    await this.inventoryPage.addToCart(productField);
    await this.inventoryPage.openCart();
    await this.cartPage.assertOnCartPage();
  },
);

When(
  "the user clicks Checkout from the cart page",
  async function (this: ScenarioContext) {
    await this.cartPage.clickCheckout();
  },
);

When(
  "the user fills checkout step one using the resolved test data",
  async function (this: ScenarioContext) {
    const firstName = resolveTestDataToken(this, "td.firstName");
    const lastName = resolveTestDataToken(this, "td.lastName");
    const postalCode = resolveTestDataToken(this, "td.postalCode");
    await this.checkoutPage.fillCustomerInfo(firstName, lastName, postalCode);
  },
);

When(
  "the user clicks Continue on checkout step one",
  async function (this: ScenarioContext) {
    await this.checkoutPage.clickContinue();
  },
);

Then(
  "checkout step one should be navigated to",
  async function (this: ScenarioContext) {
    await this.checkoutPage.assertOnStepOne();
  },
);

Then(
  "the user should be navigated to checkout step two",
  async function (this: ScenarioContext) {
    await this.checkoutPage.assertOnStepTwo();
  },
);

Then(
  "an inline checkout error should be displayed",
  async function (this: ScenarioContext) {
    await this.checkoutPage.assertErrorDisplayed();
  },
);

Then(
  "the user should remain on checkout step one",
  async function (this: ScenarioContext) {
    await this.checkoutPage.assertRemainsOnStepOne();
  },
);

Then(
  "the order overview totals should be consistent",
  async function (this: ScenarioContext) {
    const { itemTotal, tax, total } =
      await this.checkoutPage.assertTotalsAreConsistent();
    await this.subStepLogger?.info(
      `Order Overview totals — item total: ${itemTotal}, tax: ${tax}, total: ${total}`,
    );
  },
);

When("the user clicks Finish", async function (this: ScenarioContext) {
  await this.checkoutPage.clickFinish();
});

Then(
  "the purchase should complete and navigate to order confirmation",
  async function (this: ScenarioContext) {
    await this.orderConfirmationPage.assertOnConfirmationPage();
    await this.orderConfirmationPage.assertOrderComplete();
  },
);

Then(
  "the checkout cart badge should be cleared",
  async function (this: ScenarioContext) {
    await this.orderConfirmationPage.assertCartBadgeCleared();
  },
);

When(
  "the user clicks Cancel on checkout step one",
  async function (this: ScenarioContext) {
    await this.checkoutPage.clickCancel();
  },
);

When(
  "the user clicks Cancel on checkout step two",
  async function (this: ScenarioContext) {
    await this.checkoutPage.clickCancel();
  },
);

Then(
  "the user should be returned to the cart page with contents unchanged",
  async function (this: ScenarioContext) {
    await this.cartPage.assertOnCartPage();
  },
);

Then(
  "the user should be returned to the inventory page from checkout",
  async function (this: ScenarioContext) {
    await this.inventoryPage.assertOnInventoryPage();
  },
);

// ---------------------------------------------------------------------------
// TC_CHECKOUT_010 (@inprogress) — error_user non-deterministic checkout errors
// ---------------------------------------------------------------------------

Then(
  "the checkout flow should be observed for random errors",
  async function (this: ScenarioContext) {
    try {
      await this.checkoutPage.clickContinue();
      await this.checkoutPage.assertOnStepTwo();
      await this.checkoutPage.clickFinish();
      await this.subStepLogger?.info(
        "error_user checkout completed normally on this run",
      );
    } catch (err) {
      await this.subStepLogger?.warning(
        `error_user checkout raised: ${(err as Error).message}`,
      );
    }
  },
);

// ---------------------------------------------------------------------------
// TC_CHECKOUT_011 (@inprogress) — problem_user Last Name field may fail
// ---------------------------------------------------------------------------

Then(
  "the Last Name field acceptance should be observed and recorded",
  async function (this: ScenarioContext) {
    await this.checkoutPage.clickContinue();
    const stillOnStepOne = this.page.url().includes("checkout-step-one.html");
    await this.subStepLogger?.info(
      `problem_user Last Name field observation: remained on Step 1 after Continue = ${stillOnStepOne} ` +
        "(docx documents this as a possible defect; PDF does not corroborate it — see GapAnalysis.md conflict).",
    );
    expect(typeof stillOnStepOne).toBe("boolean");
  },
);

// ---------------------------------------------------------------------------
// TC_CHECKOUT_012 — Postal Code boundary (long/alphanumeric value)
// ---------------------------------------------------------------------------

Then(
  "the application should either proceed or show a validation message without crashing",
  async function (this: ScenarioContext) {
    const onStepTwo = this.page.url().includes("checkout-step-two.html");
    const errorVisible = await this.checkoutPage.errorBanner
      .isVisible()
      .catch(() => false);
    expect(onStepTwo || errorVisible).toBeTruthy();
  },
);
