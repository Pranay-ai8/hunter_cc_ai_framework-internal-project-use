// PageObjects/product-detail.page.ts
// Module: Product Detail
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";

export class ProductDetailPage extends BasePage {
  // ==================== Locators ====================

  get productName(): Locator {
    return this.page.locator(".inventory_details_name");
  }

  get productDescription(): Locator {
    return this.page.locator(".inventory_details_desc");
  }

  get productPrice(): Locator {
    return this.page.locator(".inventory_details_price");
  }

  get actionButton(): Locator {
    return this.page.locator(
      '.inventory_details .btn_inventory, button[data-test^="add-to-cart-"], button[data-test^="remove-"]',
    );
  }

  get backToProductsButton(): Locator {
    return this.page.locator("#back-to-products");
  }

  get cartBadge(): Locator {
    return this.page.locator(".shopping_cart_badge");
  }

  // ==================== Validations ====================

  async assertOnDetailPage(): Promise<void> {
    await expect(this.page).toHaveURL(/inventory-item\.html/);
    await expect(this.productName).toBeVisible();
    await expect(this.productDescription).toBeVisible();
    await expect(this.productPrice).toBeVisible();
    await expect(this.actionButton).toBeVisible();
  }

  async assertProductName(expected: string): Promise<void> {
    await expect(this.productName).toHaveText(expected);
  }

  async assertButtonLabel(
    expectedLabel: "Add to cart" | "Remove",
  ): Promise<void> {
    await expect(this.actionButton).toHaveText(expectedLabel);
  }

  async assertCartBadgeCount(expected: number): Promise<void> {
    if (expected === 0) {
      await expect(this.cartBadge).toHaveCount(0);
    } else {
      await expect(this.cartBadge).toHaveText(String(expected));
    }
  }

  async getProductNameText(): Promise<string> {
    return (await this.productName.textContent())?.trim() ?? "";
  }

  // ==================== Actions ====================

  async clickAddToCart(): Promise<void> {
    await this.logInfo("Adding product to cart from Product Detail page");
    await this.withHeal(this.actionButton, (l) => l.click(), "Add To Cart Button");
  }

  async clickBackToProducts(): Promise<void> {
    await this.withHeal(this.backToProductsButton, (l) => l.click(), "Back To Products Button");
  }
}
