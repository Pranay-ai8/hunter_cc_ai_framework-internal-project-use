// PageObjects/cart.page.ts
// Module: Cart
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";

export class CartPage extends BasePage {
  // ==================== Locators ====================

  get cartItems(): Locator {
    return this.page.locator(".cart_item");
  }

  get cartItemNames(): Locator {
    return this.page.locator(".cart_item .inventory_item_name");
  }

  get continueShoppingButton(): Locator {
    return this.page.locator("#continue-shopping");
  }

  get checkoutButton(): Locator {
    return this.page.locator("#checkout");
  }

  get cartBadge(): Locator {
    return this.page.locator(".shopping_cart_badge");
  }

  getCartItem(productName: string): Locator {
    return this.cartItems.filter({
      has: this.page.locator(".inventory_item_name"),
      hasText: productName,
    });
  }

  // ==================== Validations ====================

  async assertOnCartPage(): Promise<void> {
    await expect(this.page).toHaveURL(/cart\.html/);
  }

  async assertItemCount(expected: number): Promise<void> {
    await expect(this.cartItems).toHaveCount(expected);
  }

  async assertItemPresent(productName: string): Promise<void> {
    await expect(this.getCartItem(productName)).toBeVisible();
  }

  async assertItemNotPresent(productName: string): Promise<void> {
    await expect(this.getCartItem(productName)).toHaveCount(0);
  }

  async assertItemQuantity(
    productName: string,
    expectedQty: number,
  ): Promise<void> {
    await expect(
      this.getCartItem(productName).locator(".cart_quantity"),
    ).toHaveText(String(expectedQty));
  }

  async assertCartBadgeCount(expected: number): Promise<void> {
    if (expected === 0) {
      await expect(this.cartBadge).toHaveCount(0);
    } else {
      await expect(this.cartBadge).toHaveText(String(expected));
    }
  }

  async getItemNames(): Promise<string[]> {
    return this.cartItemNames.allTextContents();
  }

  async assertCartIsEmpty(): Promise<void> {
    await expect(this.cartItems).toHaveCount(0);
    await expect(this.cartBadge).toHaveCount(0);
  }

  // ==================== Actions ====================

  async removeItem(productName: string): Promise<void> {
    await this.logInfo(`Removing "${productName}" from Cart page`);
    await this.withHeal(
      this.getCartItem(productName).locator("button"),
      (l) => l.click(),
      `Remove ${productName} Button`,
    );
  }

  async clickCheckout(): Promise<void> {
    await this.withHeal(this.checkoutButton, (l) => l.click(), "Checkout Button");
  }

  async clickContinueShopping(): Promise<void> {
    await this.withHeal(this.continueShoppingButton, (l) => l.click(), "Continue Shopping Button");
  }
}
