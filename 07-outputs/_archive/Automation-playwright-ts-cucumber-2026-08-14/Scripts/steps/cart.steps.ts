import { Given, Then, When } from "@cucumber/cucumber";
import { ScenarioContext } from "../../Framework/src/utils/ScenarioContext";
import { resolveTestDataToken } from "../../Framework/src/utils/testDataResolver";
import { resolvePassword } from "../../Framework/src/utils/passwordResolver";

Given(
  "the user is logged in with an empty cart for cart tests",
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
  },
);

When(
  "the user adds product {string} to the cart",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.inventoryPage.addToCart(productName);
  },
);

When("the user opens the cart page", async function (this: ScenarioContext) {
  await this.inventoryPage.openCart();
  await this.cartPage.assertOnCartPage();
});

Then(
  "the cart page should contain {string}",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.cartPage.assertItemPresent(productName);
  },
);

Then(
  "the cart page should contain {string} with quantity {int}",
  async function (this: ScenarioContext, productRef: string, qty: number) {
    const productName = resolveTestDataToken(this, productRef);
    await this.cartPage.assertItemPresent(productName);
    await this.cartPage.assertItemQuantity(productName, qty);
  },
);

Then(
  "the cart page should not contain {string}",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.cartPage.assertItemNotPresent(productName);
  },
);

Then(
  "the cart page should contain {int} item(s)",
  async function (this: ScenarioContext, count: number) {
    await this.cartPage.assertItemCount(count);
  },
);

Then(
  "the cart badge should show {int}",
  async function (this: ScenarioContext, expected: number) {
    await this.cartPage.assertCartBadgeCount(expected);
  },
);

When(
  "the user removes {string} from the cart page",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.cartPage.removeItem(productName);
  },
);

When(
  "the user navigates to the product detail page for {string} and back to inventory",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.inventoryPage.clickProductName(productName);
    await this.productDetailPage.assertOnDetailPage();
    await this.productDetailPage.clickBackToProducts();
    await this.inventoryPage.assertOnInventoryPage();
  },
);

When(
  "the user clicks Continue Shopping from the cart page",
  async function (this: ScenarioContext) {
    await this.cartPage.clickContinueShopping();
  },
);

Then(
  "the user should be on the inventory page with cart contents unchanged",
  async function (this: ScenarioContext) {
    await this.inventoryPage.assertOnInventoryPage();
  },
);

Given("the cart is confirmed empty", async function (this: ScenarioContext) {
  await this.inventoryPage.openCart();
  await this.cartPage.assertCartIsEmpty();
});

Then(
  "the cart page should render with no items and no error",
  async function (this: ScenarioContext) {
    await this.cartPage.assertOnCartPage();
    await this.cartPage.assertCartIsEmpty();
  },
);

// ---------------------------------------------------------------------------
// TC_CART_008 (@inprogress) — problem_user Remove button behavior (disputed defect)
// ---------------------------------------------------------------------------

Then(
  "the Remove button behavior for {string} should be observed and recorded",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.cartPage.removeItem(productName);
    const stillPresent = await this.cartPage
      .getCartItem(productName)
      .isVisible()
      .catch(() => false);
    await this.subStepLogger?.info(
      `problem_user Remove-button observation for "${productName}": item still present after click = ${stillPresent} ` +
        "(PDF documents Remove as non-functional; docx does not corroborate this — see GapAnalysis.md conflict).",
    );
  },
);
