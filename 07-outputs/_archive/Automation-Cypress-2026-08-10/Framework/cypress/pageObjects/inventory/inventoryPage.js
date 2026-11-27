import sel from '../../../utils/selectors/inventory.js';
import Click from '../../../utils/Interactions/click.js';
import HelperFunction from '../../../utils/helpers/crossModuleFunctions.js';

const { inventoryPage } = sel;

/**
 * Inventory (Product Catalog) Page Object — static class, never instantiated.
 */
export default class InventoryPage {
  /**
   * Navigate directly to the Inventory page.
   * @param {string} [path]
   */
  static toVisit = (path = '/inventory.html') => cy.visit(path);

  /**
   * Add a product to the cart by its display name.
   * @param {string} productName
   */
  static addToCart = (productName) =>
    Click.forcefullyOn(`#add-to-cart-${HelperFunction.productSlug(productName)}`);

  /**
   * Remove a product from the cart directly from the Inventory page.
   * @param {string} productName
   */
  static removeFromCart = (productName) =>
    Click.forcefullyOn(`#remove-${HelperFunction.productSlug(productName)}`);

  /**
   * Select a sort option from the product sort dropdown.
   * @param {'az'|'za'|'lohi'|'hilo'} value
   */
  static selectSortOption = (value) => cy.get(inventoryPage.sortDropdown).select(value);

  /**
   * Click a product's name to open its Product Detail page.
   * @param {string} productName
   */
  static openProductByName = (productName) =>
    cy.contains(inventoryPage.itemName, productName).click();

  /**
   * Click a product's image to open its Product Detail page.
   * @param {string} productName
   */
  static openProductImageByName = (productName) =>
    cy.contains(inventoryPage.inventoryItem, productName).find('img').click();

  /**
   * Read all currently displayed product names, in DOM order.
   * @returns {Cypress.Chainable<string[]>}
   */
  static getProductNames = () =>
    cy.get(inventoryPage.itemName).then(($els) => Cypress.$.makeArray($els).map((el) => el.innerText.trim()));

  /**
   * Read all currently displayed product prices (as numbers), in DOM order.
   * @returns {Cypress.Chainable<number[]>}
   */
  static getProductPrices = () =>
    cy
      .get(inventoryPage.itemPrice)
      .then(($els) => Cypress.$.makeArray($els).map((el) => parseFloat(el.innerText.replace('$', ''))));
}
