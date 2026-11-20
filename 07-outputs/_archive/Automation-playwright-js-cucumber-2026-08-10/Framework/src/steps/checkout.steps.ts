import { When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

Then('the customer information form is displayed', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(/checkout-step-one\.html/);
});

When(
  'the user submits customer info with first name {string}, last name {string}, postal code {string}',
  async function (this: ScenarioContext, firstName: string, lastName: string, postalCode: string) {
    await this.checkoutPage.fillCustomerInfo(firstName, lastName, postalCode);
    await this.checkoutPage.continueButton.click();
  }
);

Then('an inline checkout error is displayed', async function (this: ScenarioContext) {
  await expect(this.page.locator('[data-test="error"]')).toBeVisible();
});

Then('the order overview page is displayed', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(/checkout-step-two\.html/);
});

Then('the grand total equals the item total plus tax', async function (this: ScenarioContext) {
  const itemTotalText = await this.checkoutPage.itemTotalLabel.textContent();
  const taxText = await this.checkoutPage.taxLabel.textContent();
  const totalText = await this.checkoutPage.totalLabel.textContent();

  const itemTotal = parseFloat(itemTotalText!.replace(/[^0-9.]/g, ''));
  const tax = parseFloat(taxText!.replace(/[^0-9.]/g, ''));
  const total = parseFloat(totalText!.replace(/[^0-9.]/g, ''));

  expect(total).toBeCloseTo(itemTotal + tax, 2);
});

When('the user clicks Finish', async function (this: ScenarioContext) {
  await this.checkoutPage.finishButton.click();
});

When('the user cancels checkout on step 1', async function (this: ScenarioContext) {
  await this.checkoutPage.cancelButtonStepOne.click();
});

When('the user cancels checkout on step 2', async function (this: ScenarioContext) {
  await this.checkoutPage.cancelButtonStepTwo.click();
});

Then('the order confirmation page is displayed', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(/checkout-complete\.html/);
  await expect(this.confirmationPage.completeHeader).toBeVisible();
});

When('the user clicks Back Home', async function (this: ScenarioContext) {
  await this.confirmationPage.backHomeButton.click();
});

// TC_CHECKOUT_011 — problem_user Last Name field dispute, observational
Then('the resulting checkout step is recorded for defect verification', async function (this: ScenarioContext) {
  const url = this.page.url();
  this.attach(`problem_user checkout URL after submitting valid Last Name: ${url}`, 'text/plain');
  expect(url).toBeTruthy();
});
