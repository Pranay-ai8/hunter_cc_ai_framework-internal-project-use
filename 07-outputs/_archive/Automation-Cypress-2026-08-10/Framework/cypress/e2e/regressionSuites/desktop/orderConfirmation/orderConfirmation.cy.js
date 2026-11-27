/// <reference types="cypress" />
import InventoryPage from '../../../../pageObjects/inventory/inventoryPage.js';
import CartPage from '../../../../pageObjects/cart/cartPage.js';
import CheckoutPage from '../../../../pageObjects/checkout/checkoutPage.js';
import OrderConfirmationPage from '../../../../pageObjects/orderConfirmation/orderConfirmationPage.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import globalSels from '../../../../../utils/selectors/globalSels.js';
import productData from '../../../../fixtures/Inventory/products.json';
import userData from '../../../../fixtures/SignIn/user.json';
import customerInfo from '../../../../fixtures/Checkout/customerInfo.json';

const { header } = globalSels;
const { standard } = userData;
const { productA } = productData;
const { valid } = customerInfo;

describe('Order Confirmation - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();
    CartPage.clickCheckout();
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();
    CheckoutPage.clickFinish();
  });

  // TC_ORDERCONFIRM_002 — Back Home Navigates to Inventory
  it('TC_ORDERCONFIRM_002: The Back Home button on the Order Confirmation page navigates to the Inventory page', () => {
    OrderConfirmationPage.clickBackHome();

    Verify.theUrl().contains('/inventory.html');
  });

  // TC_ORDERCONFIRM_003 — Cart Is Empty After Order Completion
  it('TC_ORDERCONFIRM_003: The cart badge is cleared/empty after a successful checkout completion', () => {
    OrderConfirmationPage.clickBackHome();

    Verify.theElement(header.shoppingCartBadge).doesNotExist();
  });
});
