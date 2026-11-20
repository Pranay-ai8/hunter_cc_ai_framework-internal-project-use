import { Given, Then, When } from "@cucumber/cucumber";
import { ScenarioContext } from "../../Framework/src/utils/ScenarioContext";
import { resolveTestDataToken } from "../../Framework/src/utils/testDataResolver";
import { resolvePassword } from "../../Framework/src/utils/passwordResolver";

Given(
  "the user is logged in and on the inventory page for product detail tests",
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
  },
);

When(
  "the user clicks the product name {string}",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    this.testData.currentProductName = productName;
    await this.inventoryPage.clickProductName(productName);
  },
);

When(
  "the user clicks the product image {string}",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    this.testData.currentProductName = productName;
    await this.inventoryPage.clickProductImage(productName);
  },
);

Then(
  "the product detail page should be displayed",
  async function (this: ScenarioContext) {
    await this.productDetailPage.assertOnDetailPage();
  },
);

Then(
  "the product detail page should show the same product",
  async function (this: ScenarioContext) {
    await this.productDetailPage.assertOnDetailPage();
    const expected = String(this.testData.currentProductName ?? "");
    if (expected) await this.productDetailPage.assertProductName(expected);
  },
);

When(
  "the user adds the product to the cart from the detail page",
  async function (this: ScenarioContext) {
    await this.productDetailPage.clickAddToCart();
  },
);

Then(
  "the detail page cart badge should show {int}",
  async function (this: ScenarioContext, expected: number) {
    await this.productDetailPage.assertCartBadgeCount(expected);
  },
);

Then(
  "the detail page button should read {string}",
  async function (this: ScenarioContext, label: string) {
    await this.productDetailPage.assertButtonLabel(
      label as "Add to cart" | "Remove",
    );
  },
);

When(
  "the user clicks Back to Products",
  async function (this: ScenarioContext) {
    await this.productDetailPage.clickBackToProducts();
  },
);

Then(
  "the user should be back on the inventory page with all products visible",
  async function (this: ScenarioContext) {
    await this.inventoryPage.assertOnInventoryPage();
    await this.inventoryPage.assertProductCount(6);
  },
);
