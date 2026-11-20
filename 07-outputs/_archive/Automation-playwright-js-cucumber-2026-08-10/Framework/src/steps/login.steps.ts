import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';
import { BASE_URL, CREDENTIALS, SauceDemoUser } from '../config/env';

let loginStartedAt = 0;
let loginElapsedMs = 0;

Given('the user is on the SauceDemo login page', async function (this: ScenarioContext) {
  await this.page.goto(BASE_URL);
});

When('the user logs in as {string}', async function (this: ScenarioContext, username: SauceDemoUser) {
  await this.loginPage.login(username, CREDENTIALS[username]);
});

When(
  'the user logs in as {string} and elapsed time is measured',
  async function (this: ScenarioContext, username: SauceDemoUser) {
    loginStartedAt = Date.now();
    await this.loginPage.login(username, CREDENTIALS[username]);
    await this.page.waitForURL('**/inventory.html', { timeout: 15000 });
    loginElapsedMs = Date.now() - loginStartedAt;
  }
);

When(
  'the user attempts to log in with username {string} and password {string}',
  async function (this: ScenarioContext, username: string, password: string) {
    await this.loginPage.login(username, password);
  }
);

Then('the user is redirected to the inventory page', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(/inventory\.html/);
});

Then('the user remains on the login page', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(BASE_URL);
});

Then('the user is on the login page', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(BASE_URL);
});

Then('an error banner reading {string} is displayed', async function (this: ScenarioContext, expectedText: string) {
  await expect(this.page.locator('[data-test="error"]')).toContainText(expectedText);
});

Then('the login took at least {int} seconds', async function (this: ScenarioContext, minSeconds: number) {
  expect(loginElapsedMs).toBeGreaterThanOrEqual(minSeconds * 1000);
});

Then('the session should expire after 10 minutes of inactivity', async function (this: ScenarioContext) {
  // Intentionally not executed — a real 10-minute wait is impractical for CI.
  // Tracked as @inprogress; see FunctionalTestCases.md TC_LOGIN_010 notes.
  return 'skipped';
});

Given('the user logs out', async function (this: ScenarioContext) {
  await this.inventoryPage.openMenu();
  await this.inventoryPage.logoutLink.click();
});
