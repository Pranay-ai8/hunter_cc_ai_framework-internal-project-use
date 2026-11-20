const { Given, When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");
const { replaceStepPlaceholders } = require("../../../Framework/src/support/hooks");

let loginStartMs;

Given("the user is on the SauceDemo login page", async function () {
    await fixture.pages.loginPage.navigate();
});

When("the user logs in with username {string} and password {string}", async function (usernameToken, passwordToken) {
    const username = replaceStepPlaceholders(usernameToken);
    const password = replaceStepPlaceholders(passwordToken);
    await fixture.pages.loginPage.login(username, password);
});

When("the user times the login with username {string} and password {string}", async function (usernameToken, passwordToken) {
    const username = replaceStepPlaceholders(usernameToken);
    const password = replaceStepPlaceholders(passwordToken);
    loginStartMs = Date.now();
    await fixture.pages.loginPage.login(username, password);
});

When("the user waits 10 minutes without interacting with the page", { timeout: 11 * 60 * 1000 }, async function () {
    await new Promise((resolve) => setTimeout(resolve, 10 * 60 * 1000));
});

Then("the user should be redirected to the Inventory page", async function () {
    expect(fixture.pages.loginPage.isOnInventoryPage()).toBeTruthy();
});

Then("an error message containing {string} should be displayed", async function (expectedText) {
    expect(await fixture.pages.loginPage.isErrorVisible()).toBeTruthy();
    expect(await fixture.pages.loginPage.errorText()).toContain(expectedText);
});

Then("the login should have taken measurably longer than a standard login", async function () {
    const elapsedMs = Date.now() - loginStartMs;
    expect(elapsedMs).toBeGreaterThan(3000); // ~5s documented delay; loose lower bound to avoid flakiness
});

Then("the session should have expired requiring re-authentication", async function () {
    const stillLoggedIn = fixture.pages.loginPage.isOnInventoryPage();
    expect(stillLoggedIn).toBeFalsy();
});
