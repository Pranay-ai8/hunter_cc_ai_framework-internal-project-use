import { setWorldConstructor, World, IWorldOptions } from '@cucumber/cucumber';
import { Browser, BrowserContext, Page } from '@playwright/test';
import { LoginPage } from '../pages/login.page';
import { InventoryPage } from '../pages/inventory.page';
import { ProductDetailPage } from '../pages/product-detail.page';
import { CartPage } from '../pages/cart.page';
import { CheckoutPage } from '../pages/checkout.page';
import { ConfirmationPage } from '../pages/confirmation.page';

export class ScenarioContext extends World {
  browser!: Browser;
  context!: BrowserContext;
  page!: Page;
  observedErrors: string[] = [];

  constructor(options: IWorldOptions) {
    super(options);
  }

  get loginPage() {
    return new LoginPage(this.page);
  }

  get inventoryPage() {
    return new InventoryPage(this.page);
  }

  get productDetailPage() {
    return new ProductDetailPage(this.page);
  }

  get cartPage() {
    return new CartPage(this.page);
  }

  get checkoutPage() {
    return new CheckoutPage(this.page);
  }

  get confirmationPage() {
    return new ConfirmationPage(this.page);
  }
}

setWorldConstructor(ScenarioContext);
