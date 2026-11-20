/// <reference types="cypress" />
import InventoryPage from '../../../../pageObjects/inventory/inventoryPage.js';
import CartPage from '../../../../pageObjects/cart/cartPage.js';
import MenuPage from '../../../../pageObjects/menu/menuPage.js';
import globalSels from '../../../../../utils/selectors/globalSels.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import productData from '../../../../fixtures/Inventory/products.json';
import userData from '../../../../fixtures/SignIn/user.json';

const { header } = globalSels;
const { standard } = userData;
const { productA } = productData;

describe('Navigation - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_MENU_001 — Hamburger Menu Displays All Expected Links
  it('TC_MENU_001: Opening the hamburger menu displays All Items, About, Logout, and Reset App State', () => {
    MenuPage.open();

    MenuPage.getMenuLinkTexts().then((texts) => {
      expect(texts).to.have.length(4);
      expect(texts).to.deep.equal(['All Items', 'About', 'Logout', 'Reset App State']);
    });
  });

  // TC_MENU_002 — All Items Navigates to Inventory
  it('TC_MENU_002: Selecting All Items from the hamburger menu navigates to the Inventory page', () => {
    CartPage.openViaCartIcon();
    Verify.theUrl().contains('/cart.html');

    MenuPage.open();
    MenuPage.clickAllItems();

    Verify.theUrl().contains('/inventory.html');
  });

  // TC_MENU_003 — Reset App State Clears the Cart Without Logout
  it('TC_MENU_003: Reset App State clears the cart contents without logging the user out', () => {
    InventoryPage.addToCart(productA);
    Verify.theElement(header.shoppingCartBadge).hasText('1');

    MenuPage.open();
    MenuPage.clickResetAppState();
    MenuPage.close();

    Verify.theElement(header.shoppingCartBadge).doesNotExist();
    Verify.theElement(header.burgerMenuButton).isVisible();
    Verify.theUrl().doesNotContain('/login');
  });

  // TC_MENU_004 — Reset App State Resets Sort Order Without Logout
  it('TC_MENU_004: Reset App State resets the product sort order to default without logging the user out', () => {
    InventoryPage.selectSortOption('hilo');

    MenuPage.open();
    MenuPage.clickResetAppState();
    MenuPage.close();

    Verify.theElement('.product_sort_container').hasValue('az');
    Verify.theElement(header.burgerMenuButton).isVisible();
  });
});

// TC_MENU_006 — Automation Candidate = Partial: "About" destination is
// undocumented in either source PRD (GAP-FR-04). Observed, not hard-asserted.
describe('Navigation - About Link (Manual)', { tags: '@manual' }, () => {
  it('TC_MENU_006: About navigates away from the application [MANUAL: destination undocumented, GAP-FR-04]', () => {
    cy.loginAs(standard.username, standard.password);

    MenuPage.open();

    // Read the link's destination without actually clicking it: the "About"
    // link navigates to an external superdomain (Sauce Labs marketing site),
    // and Cypress hard-fails a test on an uncontrolled cross-origin
    // navigation. Since the destination is undocumented (GAP-FR-04) and this
    // is a Partial/manual candidate, observing the href is sufficient to
    // record actual behavior without risking a structural test-runner error.
    cy.get('#about_sidebar_link')
      .should('have.attr', 'href')
      .then((href) => {
        cy.log(
          `TC_MENU_006 observation: About link points to "${href}" (not navigated, ` +
            'to avoid an uncontrolled cross-origin navigation). Destination is ' +
            'undocumented in either source PRD (GAP-FR-04).'
        );
        expect(href, 'About link has a non-empty destination').to.not.equal('');
      });
  });
});
