/// <reference types="cypress" />
import InventoryPage from '../../pageObjects/inventory/inventoryPage.js';
import CartPage from '../../pageObjects/cart/cartPage.js';
import { Verify } from '../../../utils/assertions/index.js';
import productData from '../../fixtures/Inventory/products.json';
import userData from '../../fixtures/SignIn/user.json';

const { standard } = userData;

describe('Cart - Critical Path', { tags: '@smoke' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_CART_001 — Single Item Added Appears on Cart Page
  it('TC_CART_001: A single item added to the cart appears correctly on the Cart page', () => {
    InventoryPage.addToCart(productData.productA);
    CartPage.openViaCartIcon();

    Verify.theUrl().contains('/cart.html');
    Verify.theElement('.cart_item').appearsNTimes(1);
    Verify.theElement('.inventory_item_name').contains(productData.productA);
    Verify.theElement('.cart_quantity').hasText('1');
  });
});
