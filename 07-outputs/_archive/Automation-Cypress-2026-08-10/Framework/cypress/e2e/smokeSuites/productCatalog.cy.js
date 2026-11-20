/// <reference types="cypress" />
import InventoryPage from '../../pageObjects/inventory/inventoryPage.js';
import { Verify } from '../../../utils/assertions/index.js';
import globalSels from '../../../utils/selectors/globalSels.js';
import productData from '../../fixtures/Inventory/products.json';
import userData from '../../fixtures/SignIn/user.json';
import HelperFunction from '../../../utils/helpers/crossModuleFunctions.js';

const { header } = globalSels;
const { standard } = userData;

describe('Product Catalog - Critical Path', { tags: '@smoke' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_INVENTORY_001 — Exactly 6 Products Displayed with Full Details
  it('TC_INVENTORY_001: Inventory page displays exactly 6 products, each with name, description, image, and price', () => {
    Verify.theElement('.inventory_item').appearsNTimes(productData.expectedProductCount);
    Verify.theElement('.inventory_item_name').appearsNTimes(productData.expectedProductCount);
    Verify.theElement('.inventory_item_desc').appearsNTimes(productData.expectedProductCount);
    Verify.theElement('.inventory_item_price').appearsNTimes(productData.expectedProductCount);
    Verify.theElement('.inventory_item_img img').appearsNTimes(productData.expectedProductCount);
  });

  // TC_INVENTORY_006 — Add to Cart Updates Badge in Real Time
  it('TC_INVENTORY_006: Adding a product to the cart updates the cart badge immediately without a page reload', () => {
    Verify.theElement(header.shoppingCartBadge).doesNotExist();

    InventoryPage.addToCart(productData.productA);

    Verify.theElement(header.shoppingCartBadge).hasText('1');
    Verify.theElement(`#remove-${HelperFunction.productSlug(productData.productA)}`).isVisible();
  });
});
