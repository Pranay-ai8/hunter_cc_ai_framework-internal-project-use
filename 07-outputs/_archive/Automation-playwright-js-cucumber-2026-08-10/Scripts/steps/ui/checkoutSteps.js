const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");
const { replaceStepPlaceholders } = require("../../../Framework/src/support/hooks");

When("the user proceeds to checkout", async function () {
    await fixture.pages.cartPage.checkout();
});

Then("the user should be on Checkout Step 1", async function () {
    expect(fixture.pages.checkoutPage.isOnStepOne()).toBeTruthy();
});

When(
    "the user fills customer info with first name {string} last name {string} postal code {string}",
    async function (firstNameToken, lastNameToken, postalCodeToken) {
        const firstName = replaceStepPlaceholders(firstNameToken);
        const lastName = replaceStepPlaceholders(lastNameToken);
        const postalCode = replaceStepPlaceholders(postalCodeToken);
        await fixture.pages.checkoutPage.fillCustomerInfo(firstName, lastName, postalCode);
    }
);

When("the user continues to Step 2", async function () {
    await fixture.pages.checkoutPage.continueToStepTwo();
});

Then("a checkout error message should be displayed", async function () {
    expect(await fixture.pages.checkoutPage.isErrorVisible()).toBeTruthy();
});

Then("the user should be on Checkout Step 2", async function () {
    expect(fixture.pages.checkoutPage.isOnStepTwo()).toBeTruthy();
});

Then("the Order Overview should list {int} item", async function (count) {
    expect(await fixture.pages.checkoutPage.cartItemCountOnOverview()).toBe(count);
});

Then("the grand total should equal the item total plus tax", async function () {
    expect(await fixture.pages.checkoutPage.grandTotalEqualsSubtotalPlusTax()).toBeTruthy();
});

When("the user clicks Finish", async function () {
    await fixture.pages.checkoutPage.finish();
});

Then("the order success message should be displayed", async function () {
    expect(await fixture.pages.orderConfirmationPage.isSuccessMessageVisible()).toBeTruthy();
});

When("the user cancels checkout", async function () {
    await fixture.pages.checkoutPage.cancel();
});

Then("the user should be on the Cart page", async function () {
    expect(fixture.pages.checkoutPage.isOnCartPage()).toBeTruthy();
});
