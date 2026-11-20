/// <reference types="cypress" />
import InventoryPage from '../../../../pageObjects/inventory/inventoryPage.js';
import ProductDetailPage from '../../../../pageObjects/productDetail/productDetailPage.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import globalSels from '../../../../../utils/selectors/globalSels.js';
import productData from '../../../../fixtures/Inventory/products.json';
import userData from '../../../../fixtures/SignIn/user.json';

const { header } = globalSels;
const { standard } = userData;
const { productA } = productData;

describe('Product Detail - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_PRODUCTDETAIL_001 — Clicking Product Name Opens Detail Page
  it('TC_PRODUCTDETAIL_001: Clicking a product name on Inventory navigates to its Product Detail page', () => {
    InventoryPage.openProductByName(productA);

    Verify.theUrl().contains('/inventory-item.html?id=');
    Verify.theElement('.inventory_details_name').contains(productA);
    Verify.theElement('.inventory_details_price').isVisible();
    Verify.theElement('button.btn_inventory').isVisible();
  });

  // TC_PRODUCTDETAIL_002 — Clicking Product Image Opens Detail Page
  it('TC_PRODUCTDETAIL_002: Clicking a product image on Inventory navigates to its Product Detail page', () => {
    InventoryPage.openProductImageByName(productA);

    Verify.theUrl().contains('/inventory-item.html?id=');
    Verify.theElement('.inventory_details_name').contains(productA);
  });

  // TC_PRODUCTDETAIL_003 — Add to Cart Updates Badge
  it('TC_PRODUCTDETAIL_003: Adding a product to the cart from its Product Detail page updates the cart badge', () => {
    InventoryPage.openProductByName(productA);
    Verify.theElement(header.shoppingCartBadge).doesNotExist();

    ProductDetailPage.addToCart(productA);

    Verify.theElement(header.shoppingCartBadge).hasText('1');
  });

  // TC_PRODUCTDETAIL_004 — Back to Products Returns to Inventory
  it('TC_PRODUCTDETAIL_004: The Back to Products button returns the user to the Inventory page', () => {
    InventoryPage.openProductByName(productA);

    ProductDetailPage.clickBackToProducts();

    Verify.theUrl().contains('/inventory.html');
    Verify.theElement('.inventory_item').appearsNTimes(6);
  });
});
