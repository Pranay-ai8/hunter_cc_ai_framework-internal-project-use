import { Page } from '@playwright/test';
import { BasePage } from './base.page';

export class ProductDetailPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  get backToProductsButton() {
    return this.page.locator('#back-to-products');
  }

  get addToCartButton() {
    return this.page.locator('.btn_inventory');
  }

  get productName() {
    return this.page.locator('.inventory_details_name');
  }
}
