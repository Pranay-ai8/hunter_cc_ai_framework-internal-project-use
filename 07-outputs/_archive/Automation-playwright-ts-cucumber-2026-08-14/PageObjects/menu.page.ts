// PageObjects/menu.page.ts
// Module: Navigation (hamburger menu)
// Application: SauceDemo (https://www.saucedemo.com/)

import { expect, Locator } from "@playwright/test";
import BasePage from "../Framework/src/pages/base.page";

export class MenuPage extends BasePage {
  // ==================== Locators ====================

  get menuButton(): Locator {
    return this.page.locator("#react-burger-menu-btn");
  }

  get closeMenuButton(): Locator {
    return this.page.locator("#react-burger-cross-btn");
  }

  get allItemsLink(): Locator {
    return this.page.locator("#inventory_sidebar_link");
  }

  get aboutLink(): Locator {
    return this.page.locator("#about_sidebar_link");
  }

  get logoutLink(): Locator {
    return this.page.locator("#logout_sidebar_link");
  }

  get resetAppStateLink(): Locator {
    return this.page.locator("#reset_sidebar_link");
  }

  // ==================== Validations ====================

  async assertMenuItemsVisible(): Promise<void> {
    await expect(this.allItemsLink).toBeVisible();
    await expect(this.aboutLink).toBeVisible();
    await expect(this.logoutLink).toBeVisible();
    await expect(this.resetAppStateLink).toBeVisible();
  }

  async assertOnLoginPage(): Promise<void> {
    await expect(this.page).toHaveURL("https://www.saucedemo.com/");
  }

  // ==================== Actions ====================

  async openMenu(): Promise<void> {
    await this.withHeal(this.menuButton, (l) => l.click(), "Menu Button");
    await this.allItemsLink.waitFor({ state: "visible" });
  }

  async clickAllItems(): Promise<void> {
    await this.openMenu();
    await this.withHeal(this.allItemsLink, (l) => l.click(), "All Items Link");
  }

  async clickAbout(): Promise<void> {
    await this.openMenu();
    await this.withHeal(this.aboutLink, (l) => l.click(), "About Link");
  }

  async clickLogout(): Promise<void> {
    await this.logInfo("Logging out via hamburger menu");
    await this.openMenu();
    await this.withHeal(this.logoutLink, (l) => l.click(), "Logout Link");
  }

  async clickResetAppState(): Promise<void> {
    await this.logInfo("Resetting app state via hamburger menu");
    await this.openMenu();
    await this.withHeal(this.resetAppStateLink, (l) => l.click(), "Reset App State Link");
    await this.withHeal(this.closeMenuButton, (l) => l.click(), "Close Menu Button");
  }
}
