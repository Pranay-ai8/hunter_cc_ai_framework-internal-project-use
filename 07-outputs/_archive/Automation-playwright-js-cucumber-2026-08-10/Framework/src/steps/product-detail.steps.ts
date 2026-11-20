import { When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

Then('the product detail page is displayed', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(/inventory-item\.html/);
});

When('the user adds the product to cart from the detail page', async function (this: ScenarioContext) {
  await this.productDetailPage.addToCartButton.click();
});

When('the user clicks Back to Products', async function (this: ScenarioContext) {
  await this.productDetailPage.backToProductsButton.click();
});
