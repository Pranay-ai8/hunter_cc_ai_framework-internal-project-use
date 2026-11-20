const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

function resolvePlaceholder(rawValue) {
    if (typeof rawValue !== "string") return rawValue;
    return rawValue.replace(/<([\w.]+)>/g, (match, key) => {
        const value = key.split(".").reduce((acc, part) => (acc && acc[part] !== undefined ? acc[part] : undefined), fixture.testData);
        return value !== undefined ? value : match;
    });
}

When("user clicks checkout", async function () {
    await fixture.pages.cartPage.clickCheckout();
});

Then("user is navigated to checkout step one", async function () {
    await fixture.page.waitForURL(/checkout-step-one\.html/, { timeout: 15000 });
    expect(fixture.page.url()).toContain("checkout-step-one.html");
});

When(
    "user fills checkout information with first name {string}, last name {string}, postal code {string}",
    async function (firstNamePlaceholder, lastNamePlaceholder, postalCodePlaceholder) {
        const firstName = resolvePlaceholder(firstNamePlaceholder);
        const lastName = resolvePlaceholder(lastNamePlaceholder);
        const postalCode = resolvePlaceholder(postalCodePlaceholder);
        await fixture.pages.checkoutPage.fillCustomerInformation(firstName, lastName, postalCode);
    }
);

When("user clicks continue on checkout step one", async function () {
    await fixture.pages.checkoutPage.clickContinue();
});

Then("a checkout inline error is visible", async function () {
    expect(await fixture.pages.checkoutPage.isErrorVisible()).toBeTruthy();
});

Then("user remains on checkout step one", async function () {
    expect(fixture.page.url()).toContain("checkout-step-one.html");
});

Then("user is navigated to checkout step two", async function () {
    await fixture.page.waitForURL(/checkout-step-two\.html/, { timeout: 15000 });
    expect(fixture.page.url()).toContain("checkout-step-two.html");
});

Then("the order overview lists {string}", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    const names = await fixture.pages.checkoutPage.getOrderOverviewItemNames();
    expect(names).toContain(productName);
});

Then("the grand total equals the item total plus tax", async function () {
    const itemTotal = await fixture.pages.checkoutPage.getItemTotal();
    const tax = await fixture.pages.checkoutPage.getTax();
    const total = await fixture.pages.checkoutPage.getGrandTotal();
    expect(Math.round((itemTotal + tax) * 100) / 100).toBeCloseTo(total, 2);
});

When("user clicks finish", async function () {
    await fixture.pages.checkoutPage.clickFinish();
});

Then("user is navigated to the order confirmation page", async function () {
    await fixture.page.waitForURL(/checkout-complete\.html/, { timeout: 15000 });
    expect(fixture.page.url()).toContain("checkout-complete.html");
});

Then("a success message is displayed", async function () {
    expect(await fixture.pages.orderConfirmationPage.isCompleteHeaderVisible()).toBeTruthy();
    const text = await fixture.pages.orderConfirmationPage.getCompleteHeaderText();
    expect(text.length).toBeGreaterThan(0);
});

When("user clicks cancel on checkout step one", async function () {
    await fixture.pages.checkoutPage.clickCancelStepOne();
});

Then("user is navigated to the cart page", async function () {
    await fixture.page.waitForURL(/cart\.html/, { timeout: 15000 });
    expect(fixture.page.url()).toContain("cart.html");
});

When("user clicks cancel on checkout step two", async function () {
    await fixture.pages.checkoutPage.clickCancelStepTwo();
});

When("user clicks continue on checkout step one and observes for runtime errors", async function () {
    let errorSeen = false;
    const handler = () => { errorSeen = true; };
    fixture.page.on("pageerror", handler);
    try {
        await fixture.pages.checkoutPage.clickContinue();
        await fixture.page.waitForTimeout(300);
    } finally {
        fixture.page.off("pageerror", handler);
    }
    fixture.testData.__checkoutErrorObserved = errorSeen;
});

Then("the checkout observation is logged without a hard pass-or-fail assertion", async function () {
    fixture.logger.info(`TC_CHECKOUT_010: error_user checkout runtime-error observed = ${fixture.testData.__checkoutErrorObserved}. Non-deterministic by design — no hard assertion made.`);
});

Then("the observed problem_user checkout-continue behavior is logged", async function () {
    const url = fixture.page.url();
    const errorVisible = await fixture.pages.checkoutPage.isErrorVisible();
    fixture.logger.info(`TC_CHECKOUT_011: problem_user checkout-continue result — url=${url}, inlineErrorVisible=${errorVisible}.`);
});

Then("the application either proceeds to step two or shows a clear validation message without crashing", async function () {
    const url = fixture.page.url();
    const onStepTwo = /checkout-step-two\.html/.test(url);
    const errorVisible = await fixture.pages.checkoutPage.isErrorVisible();
    expect(onStepTwo || errorVisible).toBeTruthy();
});
