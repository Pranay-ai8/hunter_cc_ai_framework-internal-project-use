import { Page } from '@playwright/test';
import { BasePage } from './base.page';

export class CartPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  get cartItems() {
    return this.page.locator('.cart_item');
  }

  get checkoutButton() {
    return this.page.locator('#checkout');
  }

  get continueShoppingButton() {
    return this.page.locator('#continue-shopping');
  }

  removeButtonFor(productName: string) {
    return this.page.locator('.cart_item').filter({ hasText: productName }).locator('button', { hasText: 'Remove' });
  }
}
