import { Page } from '@playwright/test';
import { BasePage } from './base.page';

export class CheckoutPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  // Step 1 — Customer Information
  get firstNameField() {
    return this.page.locator('#first-name');
  }

  get lastNameField() {
    return this.page.locator('#last-name');
  }

  get postalCodeField() {
    return this.page.locator('#postal-code');
  }

  get continueButton() {
    return this.page.locator('#continue');
  }

  get cancelButtonStepOne() {
    return this.page.locator('#cancel');
  }

  async fillCustomerInfo(firstName: string, lastName: string, postalCode: string) {
    await this.firstNameField.fill(firstName);
    await this.lastNameField.fill(lastName);
    await this.postalCodeField.fill(postalCode);
  }

  // Step 2 — Order Overview
  get summaryItems() {
    return this.page.locator('.cart_item');
  }

  get itemTotalLabel() {
    return this.page.locator('.summary_subtotal_label');
  }

  get taxLabel() {
    return this.page.locator('.summary_tax_label');
  }

  get totalLabel() {
    return this.page.locator('.summary_total_label');
  }

  get finishButton() {
    return this.page.locator('#finish');
  }

  get cancelButtonStepTwo() {
    return this.page.locator('#cancel');
  }
}
