const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");
const { fixture } = require("../../../Framework/src/support/pageFixture");

When("the user opens the first product by its name", async function () {
    await fixture.pages.inventoryPage.openProductByName();
});

When("the user opens the first product by its image", async function () {
    await fixture.pages.inventoryPage.openProductByImage();
});

Then("the Product Detail page should be visible", async function () {
    expect(await fixture.pages.productDetailPage.isDetailVisible()).toBeTruthy();
});

When("the user adds the current product to the cart from the Detail page", async function () {
    await fixture.pages.productDetailPage.addToCart();
});

When("the user navigates back to products from the Detail page", async function () {
    await fixture.pages.productDetailPage.backToProducts();
});
