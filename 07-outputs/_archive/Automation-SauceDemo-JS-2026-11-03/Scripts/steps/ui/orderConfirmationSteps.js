const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

Then("the success message reads {string}", async function (expectedText) {
    const actualText = await fixture.pages.orderConfirmationPage.getCompleteHeaderText();
    expect(actualText).toBe(expectedText);
});

When("user clicks Back Home", async function () {
    await fixture.pages.orderConfirmationPage.clickBackHome();
});
