import sel from '../../../utils/selectors/cart.js';
import Click from '../../../utils/Interactions/click.js';
import HelperFunction from '../../../utils/helpers/crossModuleFunctions.js';
import globalSels from '../../../utils/selectors/globalSels.js';

const { cartPage } = sel;
const { header } = globalSels;

/**
 * Cart Page Object — static class, never instantiated.
 */
export default class CartPage {
  /**
   * Navigate directly to the Cart page.
   * @param {string} [path]
   */
  static toVisit = (path = '/cart.html') => cy.visit(path);

  /**
   * Open the Cart page via the shopping cart icon.
   */
  static openViaCartIcon = () => Click.on(header.shoppingCartLink);

  /**
   * Remove a product from the Cart page by its display name.
   * @param {string} productName
   */
  static removeItem = (productName) =>
    Click.forcefullyOn(`#remove-${HelperFunction.productSlug(productName)}`);

  /**
   * Click "Continue Shopping" to return to the Inventory page.
   */
  static clickContinueShopping = () => Click.on(cartPage.continueShoppingButton);

  /**
   * Click "Checkout" to begin the checkout flow.
   */
  static clickCheckout = () => Click.on(cartPage.checkoutButton);

  /**
   * Read all cart line-item names currently displayed.
   * @returns {Cypress.Chainable<string[]>}
   */
  static getItemNames = () =>
    cy.get(cartPage.cartItemName).then(($els) => Cypress.$.makeArray($els).map((el) => el.innerText.trim()));
}
