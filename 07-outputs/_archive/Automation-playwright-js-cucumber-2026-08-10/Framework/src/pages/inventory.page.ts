import { Page } from '@playwright/test';
import { BasePage } from './base.page';

export class InventoryPage extends BasePage {
  constructor(page: Page) {
    super(page);
  }

  get sortDropdown() {
    return this.page.locator('.product_sort_container');
  }

  get inventoryItems() {
    return this.page.locator('.inventory_item');
  }

  get inventoryItemNames() {
    return this.page.locator('.inventory_item_name');
  }

  get inventoryItemPrices() {
    return this.page.locator('.inventory_item_price');
  }

  get inventoryItemImages() {
    return this.page.locator('.inventory_item_img img');
  }

  addToCartButtonFor(productName: string) {
    return this.page
      .locator('.inventory_item')
      .filter({ hasText: productName })
      .locator('.btn_inventory');
  }

  async sortBy(option: 'az' | 'za' | 'lohi' | 'hilo') {
    await this.sortDropdown.selectOption(option);
  }

  async addFirstProductToCart() {
    await this.page.locator('.btn_inventory').first().click();
  }

  async openProductByName(name: string) {
    await this.page.locator('.inventory_item_name').filter({ hasText: name }).click();
  }

  async getProductNamesInOrder(): Promise<string[]> {
    return this.inventoryItemNames.allTextContents();
  }

  async getProductPricesInOrder(): Promise<number[]> {
    const texts = await this.inventoryItemPrices.allTextContents();
    return texts.map((t) => parseFloat(t.replace('$', '')));
  }
}
