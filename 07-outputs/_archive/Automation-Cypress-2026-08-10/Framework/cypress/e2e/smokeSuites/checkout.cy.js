/// <reference types="cypress" />
import InventoryPage from '../../pageObjects/inventory/inventoryPage.js';
import CartPage from '../../pageObjects/cart/cartPage.js';
import CheckoutPage from '../../pageObjects/checkout/checkoutPage.js';
import { Verify } from '../../../utils/assertions/index.js';
import globalSels from '../../../utils/selectors/globalSels.js';
import productData from '../../fixtures/Inventory/products.json';
import userData from '../../fixtures/SignIn/user.json';
import customerInfo from '../../fixtures/Checkout/customerInfo.json';

const { header } = globalSels;
const { standard } = userData;
const { valid } = customerInfo;

describe('Checkout - Critical Path', { tags: '@smoke' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
    InventoryPage.addToCart(productData.productA);
    CartPage.openViaCartIcon();
  });

  // TC_CHECKOUT_001 — Checkout Button Navigates to Step 1
  it('TC_CHECKOUT_001: Clicking Checkout on the Cart page navigates to Checkout Step 1', () => {
    CartPage.clickCheckout();

    Verify.theUrl().contains('/checkout-step-one.html');
  });

  // TC_CHECKOUT_005 — Valid Step 1 Submission Proceeds to Step 2
  it('TC_CHECKOUT_005: Submitting valid customer info on Step 1 navigates to Step 2 (Order Overview)', () => {
    CartPage.clickCheckout();
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();

    Verify.theUrl().contains('/checkout-step-two.html');
  });

  // TC_CHECKOUT_007 — Finish Completes Purchase and Clears Cart
  it('TC_CHECKOUT_007: Clicking Finish completes the purchase, clears the cart, and navigates to Order Confirmation', () => {
    CartPage.clickCheckout();
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();
    CheckoutPage.clickFinish();

    Verify.theUrl().contains('/checkout-complete.html');
    Verify.theElement('.complete-header').isVisible();
    Verify.theElement(header.shoppingCartBadge).doesNotExist();
  });
});
