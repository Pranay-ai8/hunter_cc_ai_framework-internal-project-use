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

When("user opens the cart", async function () {
    await fixture.pages.inventoryPage.openCart();
});

Then("the cart contains exactly {int} item", async function (expectedCount) {
    const count = await fixture.pages.cartPage.getCartItemCount();
    expect(count).toBe(expectedCount);
});

Then("the cart contains exactly {int} items", async function (expectedCount) {
    const count = await fixture.pages.cartPage.getCartItemCount();
    expect(count).toBe(expectedCount);
});

Then("the cart contains {string}", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    const names = await fixture.pages.cartPage.getCartItemNames();
    expect(names).toContain(productName);
});

When("user removes {string} from the cart page", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    await fixture.pages.cartPage.removeItemByName(productName);
});

Then("the cart badge is not visible on the cart page", async function () {
    expect(await fixture.pages.cartPage.isCartBadgeVisible()).toBeFalsy();
});

When("user removes {string} from the cart from the inventory page", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    await fixture.pages.inventoryPage.removeProductFromCartByName(productName);
});

When("user clicks continue shopping", async function () {
    await fixture.pages.cartPage.clickContinueShopping();
});

Then("the observed Remove-button behavior for problem_user is logged", async function () {
    const count = await fixture.pages.cartPage.getCartItemCount();
    fixture.logger.info(`TC_CART_008: problem_user cart item count after Remove click = ${count} (functional Remove = ${count === 0}).`);
});
