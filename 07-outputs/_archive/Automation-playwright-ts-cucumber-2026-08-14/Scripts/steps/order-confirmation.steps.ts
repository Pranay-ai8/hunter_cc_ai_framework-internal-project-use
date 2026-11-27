import { Given, Then, When } from "@cucumber/cucumber";
import { ScenarioContext } from "../../Framework/src/utils/ScenarioContext";
import { resolveTestDataToken } from "../../Framework/src/utils/testDataResolver";
import { resolvePassword } from "../../Framework/src/utils/passwordResolver";

Given(
  "the user has completed checkout using the resolved test data",
  async function (this: ScenarioContext) {
    const username = resolveTestDataToken(this, "td.username");
    const passwordField = String(this.testData.password ?? "AUTO");
    const password =
      passwordField === "AUTO"
        ? resolvePassword(username)
        : resolveTestDataToken(this, passwordField);
    const productName = resolveTestDataToken(this, "td.productName");
    const firstName = resolveTestDataToken(this, "td.firstName");
    const lastName = resolveTestDataToken(this, "td.lastName");
    const postalCode = resolveTestDataToken(this, "td.postalCode");

    await this.loginPage.navigate();
    await this.loginPage.login(username, password);
    await this.inventoryPage.assertOnInventoryPage();
    await this.menuPage.clickResetAppState();
    await this.inventoryPage.addToCart(productName);
    await this.inventoryPage.openCart();
    await this.cartPage.clickCheckout();
    await this.checkoutPage.assertOnStepOne();
    await this.checkoutPage.fillCustomerInfo(firstName, lastName, postalCode);
    await this.checkoutPage.clickContinue();
    await this.checkoutPage.assertOnStepTwo();
    await this.checkoutPage.clickFinish();
    await this.orderConfirmationPage.assertOnConfirmationPage();
  },
);

Then(
  "a success message should be displayed on the confirmation page",
  async function (this: ScenarioContext) {
    await this.orderConfirmationPage.assertOrderComplete();
  },
);

When("the user clicks Back Home", async function (this: ScenarioContext) {
  await this.orderConfirmationPage.clickBackHome();
});

Then(
  "the user should be navigated to the inventory page from confirmation",
  async function (this: ScenarioContext) {
    await this.inventoryPage.assertOnInventoryPage();
  },
);

Then(
  "the inventory page cart badge should be cleared",
  async function (this: ScenarioContext) {
    await this.inventoryPage.assertCartBadgeCount(0);
  },
);
