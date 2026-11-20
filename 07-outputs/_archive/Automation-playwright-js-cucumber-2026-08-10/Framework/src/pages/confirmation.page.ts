import { Page } from '@playwright/test';
import { BasePage } from './base.page';

export class ConfirmationPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  get completeHeader() {
    return this.page.locator('.complete-header');
  }

  get backHomeButton() {
    return this.page.locator('#back-to-products');
  }
}
