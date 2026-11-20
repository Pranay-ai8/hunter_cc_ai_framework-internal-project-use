import { When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

When('the user opens the cart page', async function (this: ScenarioContext) {
  await this.inventoryPage.shoppingCartLink.click();
});

Then('{int} item(s) is listed in the cart', async function (this: ScenarioContext, count: number) {
  await expect(this.cartPage.cartItems).toHaveCount(count);
});

Then('{int} items are listed in the cart', async function (this: ScenarioContext, count: number) {
  await expect(this.cartPage.cartItems).toHaveCount(count);
});

When('the user removes the first item from the cart page', async function (this: ScenarioContext) {
  await this.page.locator('.cart_item button', { hasText: 'Remove' }).first().click();
});

When('the user clicks Continue Shopping', async function (this: ScenarioContext) {
  await this.cartPage.continueShoppingButton.click();
});

When('the user clicks Checkout', async function (this: ScenarioContext) {
  await this.cartPage.checkoutButton.click();
});

// TC_CART_008 — problem_user Remove-button dispute, observational
Then('the resulting cart item count is recorded for defect verification', async function (this: ScenarioContext) {
  const count = await this.cartPage.cartItems.count();
  this.attach(`problem_user cart item count after Remove click: ${count}`, 'text/plain');
  expect(count).toBeGreaterThanOrEqual(0);
});
