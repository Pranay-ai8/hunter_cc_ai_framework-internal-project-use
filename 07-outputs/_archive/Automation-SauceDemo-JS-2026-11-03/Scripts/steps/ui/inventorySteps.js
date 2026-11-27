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

const SORT_LABEL_TO_VALUE = {
    "Name (A to Z)": "az",
    "Name (Z to A)": "za",
    "Price (low to high)": "lohi",
    "Price (high to low)": "hilo"
};

Then("every product shows a name, description, image, and price", async function () {
    const names = await fixture.pages.inventoryPage.getProductNames();
    expect(names.length).toBe(6);
    for (const name of names) {
        expect(name.trim().length).toBeGreaterThan(0);
    }
    const prices = await fixture.pages.inventoryPage.getProductPrices();
    expect(prices.length).toBe(6);
    for (const price of prices) {
        expect(price).toBeGreaterThan(0);
    }
    const imgSrcs = await fixture.pages.inventoryPage.getProductImageSrcs();
    expect(imgSrcs.length).toBe(6);
    for (const src of imgSrcs) {
        expect(src).toBeTruthy();
    }
});

When("user sorts products by {string}", async function (sortLabelPlaceholder) {
    const sortLabel = resolvePlaceholder(sortLabelPlaceholder);
    const sortValue = SORT_LABEL_TO_VALUE[sortLabel];
    await fixture.pages.inventoryPage.sortBy(sortValue);
});

Then("the products are displayed in ascending alphabetical order by name", async function () {
    const names = await fixture.pages.inventoryPage.getProductNames();
    const sorted = [...names].sort((a, b) => a.localeCompare(b));
    expect(names).toEqual(sorted);
});

Then("the products are displayed in descending alphabetical order by name", async function () {
    const names = await fixture.pages.inventoryPage.getProductNames();
    const sorted = [...names].sort((a, b) => b.localeCompare(a));
    expect(names).toEqual(sorted);
});

Then("the products are displayed in ascending order by price", async function () {
    const prices = await fixture.pages.inventoryPage.getProductPrices();
    const sorted = [...prices].sort((a, b) => a - b);
    expect(prices).toEqual(sorted);
});

Then("the products are displayed in descending order by price", async function () {
    const prices = await fixture.pages.inventoryPage.getProductPrices();
    const sorted = [...prices].sort((a, b) => b - a);
    expect(prices).toEqual(sorted);
});

Then("the cart badge is not visible", async function () {
    expect(await fixture.pages.inventoryPage.isCartBadgeVisible()).toBeFalsy();
});

When("user adds {string} to the cart from the inventory page", async function (productPlaceholder) {
    const productName = resolvePlaceholder(productPlaceholder);
    await fixture.pages.inventoryPage.addProductToCartByName(productName);
});

Then("the cart badge shows count {string}", async function (expectedCount) {
    const count = await fixture.pages.inventoryPage.getCartBadgeCount();
    expect(String(count)).toBe(expectedCount);
});

Then("the {string} button now reads {string}", async function (productPlaceholder, expectedLabel) {
    const productName = resolvePlaceholder(productPlaceholder);
    const isRemove = await fixture.pages.inventoryPage.isProductButtonShowingRemove(productName);
    expect(isRemove).toBe(expectedLabel.toLowerCase() === "remove");
});

Then("all product images share the same broken image source", async function () {
    const srcs = await fixture.pages.inventoryPage.getProductImageSrcs();
    const uniqueSrcs = new Set(srcs);
    fixture.logger.info(`problem_user image sources: ${JSON.stringify(srcs)}`);
    // Confirmed live: all 6 products resolve to the same placeholder/404 image asset.
    expect(uniqueSrcs.size).toBe(1);
});

When("user sorts products by {string} and observes for runtime errors", async function (sortLabelPlaceholder) {
    const sortLabel = resolvePlaceholder(sortLabelPlaceholder);
    const sortValue = SORT_LABEL_TO_VALUE[sortLabel];
    let errorSeen = false;
    const handler = () => { errorSeen = true; };
    fixture.page.on("pageerror", handler);
    try {
        await fixture.pages.inventoryPage.sortBy(sortValue);
        await fixture.page.waitForTimeout(300);
    } finally {
        fixture.page.off("pageerror", handler);
    }
    fixture.testData.__errorObserved = errorSeen;
});

Then("the observation is logged without a hard pass-or-fail assertion", async function () {
    fixture.logger.info(`TC_INVENTORY_008: error_user sort runtime-error observed = ${fixture.testData.__errorObserved}. Non-deterministic by design — no hard assertion made.`);
});

Then("a screenshot of the inventory page is captured for visual comparison", async function () {
    const screenshot = await fixture.page.screenshot();
    this.attach(screenshot, "image/png");
    fixture.logger.info("TC_INVENTORY_009: visual_user inventory screenshot captured for manual/visual-regression review.");
});
