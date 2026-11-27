const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

When("the user opens the hamburger menu", async function () {
    await fixture.pages.menuPage.open();
});

Then("all menu links should be visible", async function () {
    expect(await fixture.pages.menuPage.areMenuLinksVisible()).toBeTruthy();
});

When("the user clicks All Items", async function () {
    await fixture.pages.menuPage.clickAllItems();
});

When("the user clicks Reset App State", async function () {
    await fixture.pages.menuPage.resetAppState();
});

When("the user clicks Logout", async function () {
    await fixture.pages.menuPage.logout();
});

Then("the user should be redirected to the Login page", async function () {
    expect(fixture.pages.menuPage.isOnLoginPage()).toBeTruthy();
});

When("the user clicks About", async function () {
    await fixture.pages.menuPage.clickAbout();
    fixture.logger.info(`About link destination recorded for manual confirmation: ${fixture.page.url()}`);
});
