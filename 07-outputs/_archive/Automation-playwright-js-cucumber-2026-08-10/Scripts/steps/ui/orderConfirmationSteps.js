const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

When("the user clicks Back Home", async function () {
    await fixture.pages.orderConfirmationPage.backHome();
});
