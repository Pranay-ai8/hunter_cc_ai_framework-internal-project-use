import { Page } from '@playwright/test';

export abstract class BasePage {
  constructor(protected readonly page: Page) {}

  async gotoPath(path: string, baseUrl: string) {
    await this.page.goto(new URL(path, baseUrl).toString());
  }

  get errorBanner() {
    return this.page.locator('.error-message-container');
  }

  get hamburgerMenuButton() {
    return this.page.locator('#react-burger-menu-btn');
  }

  get logoutLink() {
    return this.page.locator('#logout_sidebar_link');
  }

  get resetAppStateLink() {
    return this.page.locator('#reset_sidebar_link');
  }

  get allItemsLink() {
    return this.page.locator('#inventory_sidebar_link');
  }

  get aboutLink() {
    return this.page.locator('#about_sidebar_link');
  }

  get shoppingCartLink() {
    return this.page.locator('.shopping_cart_link');
  }

  get shoppingCartBadge() {
    return this.page.locator('.shopping_cart_badge');
  }

  async openMenu() {
    await this.hamburgerMenuButton.click();
  }
}
