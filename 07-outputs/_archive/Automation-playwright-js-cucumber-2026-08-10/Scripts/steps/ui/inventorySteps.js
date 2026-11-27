const { Given, When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");
const { replaceStepPlaceholders } = require("../../../Framework/src/support/hooks");

Given("the user is logged in as {string} with password {string}", async function (usernameToken, passwordToken) {
    const username = replaceStepPlaceholders(usernameToken);
    const password = replaceStepPlaceholders(passwordToken);
    await fixture.pages.loginPage.navigate();
    await fixture.pages.loginPage.login(username, password);
});

Then("the Inventory page should display exactly 6 products", async function () {
    expect(await fixture.pages.inventoryPage.productCount()).toBe(6);
});

When("the user sorts products by {string}", async function (sortLabel) {
    await fixture.pages.inventoryPage.sortBy(sortLabel);
});

Then("the product names should be sorted ascending", async function () {
    const names = await fixture.pages.inventoryPage.productNames();
    const sorted = [...names].sort();
    expect(names).toEqual(sorted);
});

Then("the product names should be sorted descending", async function () {
    const names = await fixture.pages.inventoryPage.productNames();
    const sorted = [...names].sort().reverse();
    expect(names).toEqual(sorted);
});

Then("the product prices should be sorted ascending", async function () {
    const prices = await fixture.pages.inventoryPage.productPrices();
    const sorted = [...prices].sort((a, b) => a - b);
    expect(prices).toEqual(sorted);
});

Then("the product prices should be sorted descending", async function () {
    const prices = await fixture.pages.inventoryPage.productPrices();
    const sorted = [...prices].sort((a, b) => b - a);
    expect(prices).toEqual(sorted);
});

Then("the cart badge should not be visible", async function () {
    expect(await fixture.pages.inventoryPage.isCartBadgeVisible()).toBeFalsy();
});

When("the user adds {string} to the cart", async function (productName) {
    await fixture.pages.inventoryPage.addToCart(productName);
});

Then("the cart badge should be visible", async function () {
    expect(await fixture.pages.inventoryPage.isCartBadgeVisible()).toBeTruthy();
});

When("the user records product image sources for later comparison", async function () {
    const sources = await fixture.pages.inventoryPage.productImageSources();
    fixture.logger.info(`problem_user image sources (compare against standard_user baseline): ${JSON.stringify(sources)}`);
});

When("the user captures a screenshot named {string}", async function (name) {
    await fixture.pages.inventoryPage.captureScreenshot(name);
});
