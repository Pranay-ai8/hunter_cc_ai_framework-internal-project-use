// PageObjects/checkout.page.ts
// Module: Checkout (Step 1 — Customer Information, Step 2 — Order Overview)
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";

export class CheckoutPage extends BasePage {
  // ==================== Locators — Step 1 ====================

  get firstNameInput(): Locator {
    return this.page.locator("#first-name");
  }

  get lastNameInput(): Locator {
    return this.page.locator("#last-name");
  }

  get postalCodeInput(): Locator {
    return this.page.locator("#postal-code");
  }

  get continueButton(): Locator {
    return this.page.locator("#continue");
  }

  get cancelButton(): Locator {
    return this.page.locator("#cancel");
  }

  get errorBanner(): Locator {
    return this.page.locator(".error-message-container");
  }

  get errorText(): Locator {
    return this.page.locator('[data-test="error"]');
  }

  // ==================== Locators — Step 2 (Order Overview) ====================

  get summaryItems(): Locator {
    return this.page.locator(".cart_item");
  }

  get itemTotalLabel(): Locator {
    return this.page.locator(".summary_subtotal_label");
  }

  get taxLabel(): Locator {
    return this.page.locator(".summary_tax_label");
  }

  get totalLabel(): Locator {
    return this.page.locator(".summary_total_label");
  }

  get finishButton(): Locator {
    return this.page.locator("#finish");
  }

  // ==================== Validations ====================

  async assertOnStepOne(): Promise<void> {
    await expect(this.page).toHaveURL(/checkout-step-one\.html/);
  }

  async assertOnStepTwo(): Promise<void> {
    await expect(this.page).toHaveURL(/checkout-step-two\.html/);
  }

  async assertErrorDisplayed(): Promise<void> {
    await expect(this.errorBanner).toBeVisible();
  }

  async assertErrorMessage(expected: string): Promise<void> {
    await expect(this.errorText).toHaveText(expected);
  }

  async assertRemainsOnStepOne(): Promise<void> {
    await expect(this.page).toHaveURL(/checkout-step-one\.html/);
  }

  /**
   * Parses the displayed item total, tax, and grand total and asserts
   * grand total = item total + tax (GAP-FR-02 — exact tax formula is undocumented,
   * so this asserts the internal arithmetic relationship rather than a hardcoded amount).
   */
  async assertTotalsAreConsistent(): Promise<{
    itemTotal: number;
    tax: number;
    total: number;
  }> {
    const parseMoney = (text: string | null): number =>
      Number((text ?? "").replace(/[^0-9.]/g, ""));
    const itemTotal = parseMoney(await this.itemTotalLabel.textContent());
    const tax = parseMoney(await this.taxLabel.textContent());
    const total = parseMoney(await this.totalLabel.textContent());
    expect(Math.abs(total - (itemTotal + tax))).toBeLessThan(0.01);
    return { itemTotal, tax, total };
  }

  async getSummaryItemNames(): Promise<string[]> {
    return this.summaryItems.locator(".inventory_item_name").allTextContents();
  }

  // ==================== Actions ====================

  async fillCustomerInfo(
    firstName: string,
    lastName: string,
    postalCode: string,
  ): Promise<void> {
    await this.logInfo(
      `Filling checkout Step 1: firstName="${firstName}" lastName="${lastName}" postalCode="${postalCode}"`,
    );
    if (firstName) await this.withHeal(this.firstNameInput, (l) => l.fill(firstName), "First Name Input");
    if (lastName) await this.withHeal(this.lastNameInput, (l) => l.fill(lastName), "Last Name Input");
    if (postalCode) await this.withHeal(this.postalCodeInput, (l) => l.fill(postalCode), "Postal Code Input");
  }

  async clickContinue(): Promise<void> {
    await this.withHeal(this.continueButton, (l) => l.click(), "Continue Button");
  }

  async clickCancel(): Promise<void> {
    await this.withHeal(this.cancelButton, (l) => l.click(), "Cancel Button");
  }

  async clickFinish(): Promise<void> {
    await this.logInfo("Clicking Finish to complete purchase");
    await this.withHeal(this.finishButton, (l) => l.click(), "Finish Button");
  }
}
