// PageObjects/inventory.page.ts
// Module: Product Catalog
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";
import { ConfigurationManager } from "../Framework/src/config/ConfigurationManager";

export type SortOption = "az" | "za" | "lohi" | "hilo";

export class InventoryPage extends BasePage {
  // ==================== Locators ====================
  // NOTE: the real inventory-item-name element renders as
  // `<div class="inventory_item_name " data-test="inventory-item-name">` — note the
  // trailing space in the class attribute. A CSS class selector (`.inventory_item_name`)
  // is unaffected by this; an exact-match XPath predicate (`@class='inventory_item_name'`)
  // would silently never match. Always prefer the CSS class / data-test forms below.

  get inventoryList(): Locator {
    return this.page.locator(".inventory_list");
  }

  get inventoryItems(): Locator {
    return this.page.locator(".inventory_item");
  }

  get itemNames(): Locator {
    return this.page.locator(".inventory_item_name");
  }

  get itemPrices(): Locator {
    return this.page.locator(".inventory_item_price");
  }

  get itemImages(): Locator {
    return this.page.locator(".inventory_item_img img");
  }

  get sortDropdown(): Locator {
    return this.page.locator(".product_sort_container");
  }

  get cartBadge(): Locator {
    return this.page.locator(".shopping_cart_badge");
  }

  get cartLink(): Locator {
    return this.page.locator(".shopping_cart_link");
  }

  getProductCard(productName: string): Locator {
    return this.inventoryItems.filter({
      has: this.page.locator(".inventory_item_name"),
      hasText: productName,
    });
  }

  // ==================== Validations ====================

  async assertOnInventoryPage(): Promise<void> {
    await expect(this.page).toHaveURL(/inventory\.html/);
    await expect(this.inventoryList).toBeVisible();
  }

  async assertProductCount(expected: number): Promise<void> {
    await expect(this.inventoryItems).toHaveCount(expected);
  }

  async assertProductHasFullDetails(productName: string): Promise<void> {
    const card = this.getProductCard(productName);
    await expect(card.locator(".inventory_item_name")).toBeVisible();
    await expect(card.locator(".inventory_item_desc")).toBeVisible();
    await expect(card.locator(".inventory_item_img img")).toBeVisible();
    await expect(card.locator(".inventory_item_price")).toBeVisible();
  }

  async getCartBadgeCount(): Promise<number> {
    if (!(await this.cartBadge.isVisible().catch(() => false))) return 0;
    const text = await this.cartBadge.textContent();
    return Number(text?.trim() ?? "0");
  }

  async assertCartBadgeCount(expected: number): Promise<void> {
    if (expected === 0) {
      await expect(this.cartBadge).toHaveCount(0);
    } else {
      await expect(this.cartBadge).toHaveText(String(expected));
    }
  }

  async getProductNamesInOrder(): Promise<string[]> {
    return this.itemNames.allTextContents();
  }

  async getProductPricesInOrder(): Promise<number[]> {
    const texts = await this.itemPrices.allTextContents();
    return texts.map((t: string) => Number(t.replace("$", "").trim()));
  }

  async assertButtonLabel(
    productName: string,
    expectedLabel: "Add to cart" | "Remove",
  ): Promise<void> {
    const button = this.getProductCard(productName).locator("button");
    await expect(button).toHaveText(expectedLabel);
  }

  async assertImagesRenderConsistently(): Promise<string[]> {
    // Returns the list of image `src` attributes so problem_user's swapped/broken
    // images can be compared against a standard_user baseline by the caller.
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return this.itemImages.evaluateAll((imgs: any[]) =>
      imgs.map((img) => img.src as string),
    );
  }

  // ==================== Actions ====================

  async navigate(): Promise<void> {
    await this.page.goto(
      `${ConfigurationManager.config().baseUrl}inventory.html`,
    );
    await this.waitForLoadingToComplete();
  }

  async addToCart(productName: string): Promise<void> {
    await this.logInfo(`Adding "${productName}" to cart`);
    await this.withHeal(
      this.getProductCard(productName).locator("button"),
      (l) => l.click(),
      `Add ${productName} To Cart Button`,
    );
    await this.logSuccess(`"${productName}" added to cart`);
  }

  async removeFromCart(productName: string): Promise<void> {
    await this.logInfo(`Removing "${productName}" from cart (inventory page)`);
    await this.withHeal(
      this.getProductCard(productName).locator("button"),
      (l) => l.click(),
      `Remove ${productName} Button`,
    );
  }

  async sortBy(option: SortOption): Promise<void> {
    await this.logInfo(`Sorting inventory by "${option}"`);
    await this.withHeal(this.sortDropdown, (l) => l.selectOption(option), "Sort Dropdown");
  }

  async clickProductName(productName: string): Promise<void> {
    await this.withHeal(
      this.getProductCard(productName).locator(".inventory_item_name"),
      (l) => l.click(),
      `${productName} Product Name Link`,
    );
  }

  async clickProductImage(productName: string): Promise<void> {
    await this.withHeal(
      this.getProductCard(productName).locator(".inventory_item_img img"),
      (l) => l.click(),
      `${productName} Product Image`,
    );
  }

  async openCart(): Promise<void> {
    await this.withHeal(this.cartLink, (l) => l.click(), "Cart Link");
  }
}
