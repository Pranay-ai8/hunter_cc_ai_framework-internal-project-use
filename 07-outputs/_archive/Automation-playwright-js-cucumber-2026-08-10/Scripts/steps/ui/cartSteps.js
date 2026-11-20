const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

When("the user opens the cart", async function () {
    await fixture.pages.inventoryPage.openCart();
});

When("the user removes {string} from the Inventory page", async function (productName) {
    await fixture.pages.inventoryPage.removeFromCart(productName);
});

// NOTE: "the cart badge should not be visible" is defined once, in inventorySteps.js —
// do not redefine it here, or Cucumber reports "ambiguous step" across both files.

Then("{string} should be in the cart", async function (productName) {
    expect(await fixture.pages.cartPage.isItemInCart(productName)).toBeTruthy();
});

Then("{string} should not be in the cart", async function (productName) {
    expect(await fixture.pages.cartPage.isItemInCart(productName)).toBeFalsy();
});

Then("the cart should contain {int} item(s)", async function (count) {
    expect(await fixture.pages.cartPage.itemCount()).toBe(count);
});

When("the user removes {string} from the Cart page", async function (productName) {
    await fixture.pages.cartPage.removeItem(productName);
});

When("the user clicks Continue Shopping", async function () {
    await fixture.pages.cartPage.continueShopping();
});
