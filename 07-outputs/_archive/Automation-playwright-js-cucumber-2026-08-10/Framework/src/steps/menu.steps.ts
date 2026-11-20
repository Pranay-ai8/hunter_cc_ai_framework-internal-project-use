import { When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

When('the user opens the hamburger menu', async function (this: ScenarioContext) {
  await this.inventoryPage.openMenu();
});

Then(
  'the menu shows All Items, About, Logout, and Reset App State',
  async function (this: ScenarioContext) {
    await expect(this.inventoryPage.allItemsLink).toBeVisible();
    await expect(this.inventoryPage.aboutLink).toBeVisible();
    await expect(this.inventoryPage.logoutLink).toBeVisible();
    await expect(this.inventoryPage.resetAppStateLink).toBeVisible();
  }
);

When('the user clicks All Items in the menu', async function (this: ScenarioContext) {
  await this.inventoryPage.allItemsLink.click();
});

When('the user clicks Reset App State', async function (this: ScenarioContext) {
  await this.inventoryPage.resetAppStateLink.click();
});

When('the user clicks Logout', async function (this: ScenarioContext) {
  await this.inventoryPage.logoutLink.click();
});

Then('the product sort dropdown still shows {string}', async function (this: ScenarioContext, expectedValue: string) {
  await expect(this.inventoryPage.sortDropdown).toHaveValue(expectedValue);
});

// TC_MENU_006 — 'About' destination undocumented (GAP-FR-04), observational
When('the user clicks About in the menu', async function (this: ScenarioContext) {
  await this.inventoryPage.aboutLink.click();
});

Then('the resulting destination is recorded for confirmation', async function (this: ScenarioContext) {
  const url = this.page.url();
  this.attach(`'About' link navigated to: ${url}`, 'text/plain');
  expect(url).toBeTruthy();
});
