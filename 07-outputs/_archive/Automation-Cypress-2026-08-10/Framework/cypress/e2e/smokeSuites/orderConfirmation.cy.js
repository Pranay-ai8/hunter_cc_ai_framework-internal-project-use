/// <reference types="cypress" />
import InventoryPage from '../../pageObjects/inventory/inventoryPage.js';
import CartPage from '../../pageObjects/cart/cartPage.js';
import CheckoutPage from '../../pageObjects/checkout/checkoutPage.js';
import { Verify } from '../../../utils/assertions/index.js';
import productData from '../../fixtures/Inventory/products.json';
import userData from '../../fixtures/SignIn/user.json';
import customerInfo from '../../fixtures/Checkout/customerInfo.json';

const { standard } = userData;
const { valid } = customerInfo;

describe('Order Confirmation - Critical Path', { tags: '@smoke' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
    InventoryPage.addToCart(productData.productA);
    CartPage.openViaCartIcon();
    CartPage.clickCheckout();
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();
    CheckoutPage.clickFinish();
  });

  // TC_ORDERCONFIRM_001 — Success Message Displayed After Checkout
  it('TC_ORDERCONFIRM_001: Order Confirmation page displays a success message after completing checkout', () => {
    Verify.theUrl().contains('/checkout-complete.html');
    Verify.theElement('.complete-header').contains('Thank you for your order!');
  });
});
