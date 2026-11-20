/// <reference types="cypress" />
import InventoryPage from '../../../../pageObjects/inventory/inventoryPage.js';
import ProductDetailPage from '../../../../pageObjects/productDetail/productDetailPage.js';
import CartPage from '../../../../pageObjects/cart/cartPage.js';
import LoginPage from '../../../../pageObjects/signIn/siginPage.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import globalSels from '../../../../../utils/selectors/globalSels.js';
import HelperFunction from '../../../../../utils/helpers/crossModuleFunctions.js';
import productData from '../../../../fixtures/Inventory/products.json';
import userData from '../../../../fixtures/SignIn/user.json';

const { header } = globalSels;
const { standard, problem } = userData;
const { productA, productB } = productData;

describe('Cart - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_CART_002 — Multiple Items Added Independently Appear on Cart Page
  it('TC_CART_002: Multiple different items added to the cart all appear correctly on the Cart page', () => {
    InventoryPage.addToCart(productA);
    InventoryPage.addToCart(productB);

    Verify.theElement(header.shoppingCartBadge).hasText('2');

    CartPage.openViaCartIcon();
    Verify.theElement('.cart_item').appearsNTimes(2);
    CartPage.getItemNames().then((names) => {
      expect(names).to.include(productA);
      expect(names).to.include(productB);
    });
  });

  // TC_CART_003 — Remove Item from Cart Page
  it('TC_CART_003: Removing an item from the Cart page removes it and decrements the badge', () => {
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();

    CartPage.removeItem(productA);

    Verify.theElement('.cart_item').doesNotExist();
    Verify.theElement(header.shoppingCartBadge).doesNotExist();
  });

  // TC_CART_004 — Remove Item Directly from Inventory Page
  it('TC_CART_004: An item already in the cart can be removed directly from the Inventory page', () => {
    InventoryPage.addToCart(productA);
    Verify.theElement(`#remove-${HelperFunction.productSlug(productA)}`).isVisible();

    InventoryPage.removeFromCart(productA);

    Verify.theElement(`#add-to-cart-${HelperFunction.productSlug(productA)}`).isVisible();
    Verify.theElement(header.shoppingCartBadge).doesNotExist();
  });

  // TC_CART_005 — Contents Persist Across Inventory/Detail/Cart Navigation
  it('TC_CART_005: Cart contents persist as the user navigates between Inventory, Product Detail, and Cart pages', () => {
    InventoryPage.addToCart(productA);

    InventoryPage.openProductByName(productA);
    ProductDetailPage.clickBackToProducts();

    CartPage.openViaCartIcon();
    Verify.theElement('.cart_item').appearsNTimes(1);
    Verify.theElement('.inventory_item_name').contains(productA);
  });

  // TC_CART_006 — Continue Shopping Returns to Inventory
  it('TC_CART_006: The Continue Shopping button on the Cart page returns the user to the Inventory page', () => {
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();

    CartPage.clickContinueShopping();

    Verify.theUrl().contains('/inventory.html');
    Verify.theElement(header.shoppingCartBadge).hasText('1');
  });

  // TC_CART_007 — Empty Cart Displays Correctly
  it('TC_CART_007: The Cart page renders correctly with zero items and no application error', () => {
    CartPage.openViaCartIcon();

    Verify.theUrl().contains('/cart.html');
    Verify.theElement('.cart_item').doesNotExist();
    Verify.theElement(header.shoppingCartBadge).doesNotExist();
  });
});

// TC_CART_008 — Automation Candidate = Partial: disputed problem_user Remove-button
// defect conflict between source PRDs (see GapAnalysis.md Conflicting Requirements).
// Observes actual behavior rather than hard-asserting either documented possibility.
describe('Cart - Defect Personas (Manual)', { tags: '@manual' }, () => {
  it("TC_CART_008: problem_user's Remove button behavior in Cart [MANUAL: persona-defect conflict, see GapAnalysis.md]", () => {
    LoginPage.toVisit();
    LoginPage.doUserLogin(problem.username, problem.password);
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();

    CartPage.removeItem(productA);

    cy.get('body').then(($body) => {
      const itemStillPresent = $body.find('.cart_item').length > 0;
      cy.log(
        `TC_CART_008 observation: after clicking Remove for problem_user, ` +
          `item still present in cart = ${itemStillPresent}. Per the PDF, Remove is ` +
          'non-functional for problem_user (expect true); per the docx this defect is ' +
          'not confirmed for problem_user (expect false). Not hard-asserting either way ' +
          'until the conflict is resolved against the live application.'
      );
    });
  });
});
