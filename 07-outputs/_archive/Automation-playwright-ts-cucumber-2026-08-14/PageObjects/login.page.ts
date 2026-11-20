// PageObjects/login.page.ts
// Module: Authentication
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";
import { ConfigurationManager } from "../Framework/src/config/ConfigurationManager";

export class LoginPage extends BasePage {
  // ==================== Locators ====================

  get usernameInput(): Locator {
    return this.page.locator("#user-name");
  }

  get passwordInput(): Locator {
    return this.page.locator("#password");
  }

  get loginButton(): Locator {
    return this.page.locator("#login-button");
  }

  get errorBanner(): Locator {
    return this.page.locator(".error-message-container");
  }

  get errorText(): Locator {
    return this.page.locator('[data-test="error"]');
  }

  // ==================== Validations ====================

  async isErrorDisplayed(): Promise<boolean> {
    return this.errorBanner.isVisible();
  }

  async assertErrorMessage(expectedMessage: string): Promise<void> {
    await expect(this.errorText).toBeVisible();
    await expect(this.errorText).toHaveText(expectedMessage);
  }

  async assertRemainsOnLoginPage(): Promise<void> {
    await expect(this.page).toHaveURL(ConfigurationManager.config().baseUrl);
  }

  async assertLoggedInToInventory(): Promise<void> {
    await expect(this.page).toHaveURL(/inventory\.html/, { timeout: 15000 });
    await expect(this.page.locator(".inventory_list")).toBeVisible();
  }

  // ==================== Actions ====================

  async navigate(): Promise<void> {
    await this.page.goto(ConfigurationManager.config().baseUrl);
    await this.waitForLoadingToComplete();
    await this.logInfo("Navigated to Login page");
  }

  async enterUsername(username: string): Promise<void> {
    await this.withHeal(this.usernameInput, (l) => l.fill(username), "Username Input");
  }

  async enterPassword(password: string): Promise<void> {
    await this.withHeal(this.passwordInput, (l) => l.fill(password), "Password Input");
  }

  async clickLogin(): Promise<void> {
    await this.withHeal(this.loginButton, (l) => l.click(), "Login Button");
  }

  async login(username: string, password: string): Promise<void> {
    await this.logInfo(`Logging in as "${username}"`);
    if (username) await this.enterUsername(username);
    if (password) await this.enterPassword(password);
    await this.clickLogin();
    await this.logSuccess("Login form submitted");
  }

  /**
   * Logs in and returns the elapsed time (ms) until the Inventory page has rendered.
   * Used by performance_glitch_user's artificial-delay verification (TC_LOGIN_006).
   */
  async loginAndMeasureRenderTime(
    username: string,
    password: string,
  ): Promise<number> {
    const start = Date.now();
    await this.login(username, password);
    await this.page.waitForURL(/inventory\.html/, { timeout: 30000 });
    await this.page.locator(".inventory_list").waitFor({ state: "visible" });
    return Date.now() - start;
  }
}
