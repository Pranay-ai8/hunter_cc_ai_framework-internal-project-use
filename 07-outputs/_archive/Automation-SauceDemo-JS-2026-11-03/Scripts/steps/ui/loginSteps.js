const { Given, When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

function resolvePlaceholder(rawValue) {
    if (typeof rawValue !== "string") return rawValue;
    return rawValue.replace(/<([\w.]+)>/g, (match, key) => {
        const value = key.split(".").reduce((acc, part) => (acc && acc[part] !== undefined ? acc[part] : undefined), fixture.testData);
        return value !== undefined ? value : match;
    });
}

Given("user is on the SauceDemo login page", async function () {
    await fixture.pages.loginPage.navigateToLoginPage();
});

/**
 * Shared precondition step used by every other module's feature files —
 * logs in with the persona/password resolved from the scenario's @Key: row
 * (fixture.testData.Username/Password) and waits for the Inventory page.
 */
Given("user is logged in as {string}", async function (usernamePlaceholder) {
    const username = resolvePlaceholder(usernamePlaceholder);
    const password = fixture.testData.Password || "secret_sauce";
    await fixture.pages.loginPage.navigateToLoginPage();
    await fixture.pages.loginPage.login(username, password);
    await fixture.page.waitForURL(/inventory\.html/, { timeout: 15000 });
});

When("user logs in with username {string} and password {string}", async function (username, password) {
    await fixture.pages.loginPage.login(resolvePlaceholder(username), resolvePlaceholder(password));
});

When("user logs in with username {string} and password {string} and measures login duration", async function (username, password) {
    const start = Date.now();
    await fixture.pages.loginPage.enterUsername(resolvePlaceholder(username));
    await fixture.pages.loginPage.enterPassword(resolvePlaceholder(password));
    await fixture.pages.loginPage.clickLoginButton();
    await fixture.page.waitForURL(/inventory\.html/, { timeout: 15000 });
    fixture.testData.__loginDurationMs = Date.now() - start;
});

Then("the measured login duration is at least {int} ms", async function (minMs) {
    expect(fixture.testData.__loginDurationMs).toBeGreaterThanOrEqual(minMs);
});

Then("user is redirected to the inventory page", async function () {
    await fixture.page.waitForURL(/inventory\.html/, { timeout: 15000 });
    expect(fixture.page.url()).toContain("inventory.html");
});

Then("exactly {int} products are visible on the inventory page", async function (expectedCount) {
    const count = await fixture.pages.inventoryPage.getProductCount();
    expect(count).toBe(expectedCount);
});

Then("user sees a login error containing {string}", async function (expectedSubstring) {
    expect(await fixture.pages.loginPage.isErrorVisible()).toBeTruthy();
    const actualText = await fixture.pages.loginPage.getErrorText();
    expect(actualText).toContain(expectedSubstring);
});

Then("user remains on the login page", async function () {
    expect(fixture.page.url()).toBe("https://www.saucedemo.com/");
});

Then("the session-timeout behavior is documented as pending a dedicated long-running environment", async function () {
    fixture.logger.info("TC_LOGIN_010: real 10-minute idle wait skipped in CI per GAP-FR-03 — documented as a Partial candidate.");
});
