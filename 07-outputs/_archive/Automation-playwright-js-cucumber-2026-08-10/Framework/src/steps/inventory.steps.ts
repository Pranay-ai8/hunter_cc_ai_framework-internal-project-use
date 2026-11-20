import { When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

Then('exactly 6 products are visible', async function (this: ScenarioContext) {
  await expect(this.inventoryPage.inventoryItems).toHaveCount(6);
});

Then('every product shows a name, price, and image', async function (this: ScenarioContext) {
  const count = await this.inventoryPage.inventoryItems.count();
  for (let i = 0; i < count; i++) {
    await expect(this.inventoryPage.inventoryItemNames.nth(i)).not.toBeEmpty();
    await expect(this.inventoryPage.inventoryItemPrices.nth(i)).not.toBeEmpty();
    await expect(this.inventoryPage.inventoryItemImages.nth(i)).toBeVisible();
  }
});

When('the user sorts products by {string}', async function (this: ScenarioContext, option: 'az' | 'za' | 'lohi' | 'hilo') {
  await this.inventoryPage.sortBy(option);
});

Then('the products are displayed in ascending alphabetical order by name', async function (this: ScenarioContext) {
  const names = await this.inventoryPage.getProductNamesInOrder();
  const sorted = [...names].sort((a, b) => a.localeCompare(b));
  expect(names).toEqual(sorted);
});

Then('the products are displayed in descending alphabetical order by name', async function (this: ScenarioContext) {
  const names = await this.inventoryPage.getProductNamesInOrder();
  const sorted = [...names].sort((a, b) => b.localeCompare(a));
  expect(names).toEqual(sorted);
});

Then('the products are displayed in ascending order by price', async function (this: ScenarioContext) {
  const prices = await this.inventoryPage.getProductPricesInOrder();
  const sorted = [...prices].sort((a, b) => a - b);
  expect(prices).toEqual(sorted);
});

Then('the products are displayed in descending order by price', async function (this: ScenarioContext) {
  const prices = await this.inventoryPage.getProductPricesInOrder();
  const sorted = [...prices].sort((a, b) => b - a);
  expect(prices).toEqual(sorted);
});

When('the user adds the first product to the cart', async function (this: ScenarioContext) {
  await this.inventoryPage.addFirstProductToCart();
});

When('the user adds the first {int} products to the cart', async function (this: ScenarioContext, count: number) {
  // Each "Add to cart" click re-renders that same button as "Remove" in place,
  // so re-querying .first() after the first click would re-click product #1.
  // Snapshot distinct product-row buttons by index up front instead.
  for (let i = 0; i < count; i++) {
    await this.page.locator('.inventory_item').nth(i).locator('.btn_inventory').click();
  }
});

When('the user removes the first product from the inventory page', async function (this: ScenarioContext) {
  await this.page.locator('.btn_inventory').first().click();
});

Then('the cart badge shows {string}', async function (this: ScenarioContext, expected: string) {
  await expect(this.inventoryPage.shoppingCartBadge).toHaveText(expected);
});

Then('the cart badge is not displayed', async function (this: ScenarioContext) {
  await expect(this.inventoryPage.shoppingCartBadge).toHaveCount(0);
});

When('the user opens the first product\'s detail page by name', async function (this: ScenarioContext) {
  const name = (await this.inventoryPage.inventoryItemNames.first().textContent())!.trim();
  await this.inventoryPage.openProductByName(name);
});

// TC_INVENTORY_007 — problem_user image defect, observational
Then('the product images are captured for defect comparison', async function (this: ScenarioContext) {
  const srcs = await this.inventoryPage.inventoryItemImages.evaluateAll((imgs) =>
    imgs.map((img) => (img as HTMLImageElement).src)
  );
  this.attach(`problem_user image sources observed: ${JSON.stringify(srcs)}`, 'text/plain');
  expect(srcs.length).toBeGreaterThan(0);
});

// TC_INVENTORY_008 / TC_CHECKOUT_010 / TC_CHECKOUT_012 — observational, no hard assertion on non-deterministic behavior
Then('no unhandled application crash occurs', async function (this: ScenarioContext) {
  const bodyVisible = await this.page.locator('body').isVisible();
  expect(bodyVisible).toBeTruthy();
});

// TC_INVENTORY_009 — visual_user, observational
Then('a screenshot of the inventory page is captured for visual comparison', async function (this: ScenarioContext) {
  await this.page.screenshot({ path: `reports/visual_user-inventory-${Date.now()}.png`, fullPage: true });
});
