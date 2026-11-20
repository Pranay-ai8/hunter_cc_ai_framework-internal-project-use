// PageObjects/order-confirmation.page.ts
// Module: Order Confirmation
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";

export class OrderConfirmationPage extends BasePage {
  // ==================== Locators ====================

  get completeHeader(): Locator {
    return this.page.locator(".complete-header");
  }

  get backHomeButton(): Locator {
    return this.page.locator("#back-to-products");
  }

  get cartBadge(): Locator {
    return this.page.locator(".shopping_cart_badge");
  }

  // ==================== Validations ====================

  async assertOnConfirmationPage(): Promise<void> {
    await expect(this.page).toHaveURL(/checkout-complete\.html/);
  }

  async assertOrderComplete(): Promise<void> {
    await expect(this.completeHeader).toBeVisible();
  }

  async assertCartBadgeCleared(): Promise<void> {
    await expect(this.cartBadge).toHaveCount(0);
  }

  // ==================== Actions ====================

  async clickBackHome(): Promise<void> {
    await this.withHeal(this.backHomeButton, (l) => l.click(), "Back Home Button");
  }
}
