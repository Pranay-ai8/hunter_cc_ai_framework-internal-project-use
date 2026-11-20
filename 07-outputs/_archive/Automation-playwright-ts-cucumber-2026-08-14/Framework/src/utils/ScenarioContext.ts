import { World, setWorldConstructor } from '@cucumber/cucumber';
import { APIRequestContext, BrowserContext, Page } from '@playwright/test';
import { Logger } from 'winston';
import { HTMLSubStepLogger } from './htmlSubStepLogger';
import { SoftAssert } from './assert';
import { ApiFactory } from '../helper/api-factory';
import { LoginPage } from '../../../PageObjects/login.page';
import { InventoryPage } from '../../../PageObjects/inventory.page';
import { ProductDetailPage } from '../../../PageObjects/product-detail.page';
import { CartPage } from '../../../PageObjects/cart.page';
import { CheckoutPage } from '../../../PageObjects/checkout.page';
import { OrderConfirmationPage } from '../../../PageObjects/order-confirmation.page';
import { MenuPage } from '../../../PageObjects/menu.page';

/**
 * ScenarioContext — Cucumber World subclass.
 * Single source of per-scenario state; no global singletons, so scenarios can run
 * safely in parallel. Every page object is exposed via a private field + lazy getter —
 * never instantiate a page object directly (`new XyzPage(...)`) inside a step file.
 */
export class ScenarioContext extends World {
  page!: Page;
  context?: BrowserContext;
  logger!: Logger;
  subStepLogger!: HTMLSubStepLogger;
  softAssert!: SoftAssert;
  apiFactory?: ApiFactory;
  api?: APIRequestContext;
  testData: Record<string, unknown> = {};
  perfMeasurements: Record<string, number> = {};

  private _loginPage?: LoginPage;
  get loginPage(): LoginPage {
    if (!this._loginPage) this._loginPage = new LoginPage(this.page, this.subStepLogger);
    return this._loginPage;
  }

  private _inventoryPage?: InventoryPage;
  get inventoryPage(): InventoryPage {
    if (!this._inventoryPage) this._inventoryPage = new InventoryPage(this.page, this.subStepLogger);
    return this._inventoryPage;
  }

  private _productDetailPage?: ProductDetailPage;
  get productDetailPage(): ProductDetailPage {
    if (!this._productDetailPage) this._productDetailPage = new ProductDetailPage(this.page, this.subStepLogger);
    return this._productDetailPage;
  }

  private _cartPage?: CartPage;
  get cartPage(): CartPage {
    if (!this._cartPage) this._cartPage = new CartPage(this.page, this.subStepLogger);
    return this._cartPage;
  }

  private _checkoutPage?: CheckoutPage;
  get checkoutPage(): CheckoutPage {
    if (!this._checkoutPage) this._checkoutPage = new CheckoutPage(this.page, this.subStepLogger);
    return this._checkoutPage;
  }

  private _orderConfirmationPage?: OrderConfirmationPage;
  get orderConfirmationPage(): OrderConfirmationPage {
    if (!this._orderConfirmationPage)
      this._orderConfirmationPage = new OrderConfirmationPage(this.page, this.subStepLogger);
    return this._orderConfirmationPage;
  }

  private _menuPage?: MenuPage;
  get menuPage(): MenuPage {
    if (!this._menuPage) this._menuPage = new MenuPage(this.page, this.subStepLogger);
    return this._menuPage;
  }
}

setWorldConstructor(ScenarioContext);
