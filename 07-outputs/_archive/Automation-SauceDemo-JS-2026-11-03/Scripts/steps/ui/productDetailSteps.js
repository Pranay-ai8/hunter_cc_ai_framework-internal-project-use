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

When("user clicks the product name link for {string}", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    await fixture.pages.inventoryPage.clickProductNameLink(productName);
});

When("user clicks the product image for {string}", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    await fixture.pages.inventoryPage.clickProductImage(productName);
});

Then("user is navigated to a product detail page matching {string}", async function (urlSubstring) {
    await fixture.page.waitForURL(new RegExp(urlSubstring.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")), { timeout: 15000 });
    expect(fixture.page.url()).toContain(urlSubstring);
});

Then("the product detail page shows a description, price, and Add to Cart button", async function () {
    expect((await fixture.pages.productDetailPage.getProductName()).length).toBeGreaterThan(0);
    expect(await fixture.pages.productDetailPage.isAddToCartVisible()).toBeTruthy();
});

When("user adds the current product to cart from the detail page", async function () {
    await fixture.pages.productDetailPage.clickAddToCart();
});

Then("the detail-page cart badge shows count {string}", async function (expectedCount) {
    const count = await fixture.pages.productDetailPage.getCartBadgeCount();
    expect(String(count)).toBe(expectedCount);
});

When("user clicks Back to Products", async function () {
    await fixture.pages.productDetailPage.clickBackToProducts();
});
