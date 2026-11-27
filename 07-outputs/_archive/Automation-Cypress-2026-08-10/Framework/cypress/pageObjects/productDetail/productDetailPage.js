import sel from '../../../utils/selectors/productDetail.js';
import Click from '../../../utils/Interactions/click.js';
import HelperFunction from '../../../utils/helpers/crossModuleFunctions.js';

const { productDetailPage } = sel;

/**
 * Product Detail Page Object — static class, never instantiated.
 */
export default class ProductDetailPage {
  /**
   * Add the currently displayed product to the cart.
   * @param {string} productName
   */
  static addToCart = (productName) =>
    Click.forcefullyOn(`#add-to-cart-${HelperFunction.productSlug(productName)}`);

  /**
   * Click "Back to Products" to return to the Inventory page.
   */
  static clickBackToProducts = () => Click.on(productDetailPage.backToProductsButton);

  /**
   * Read the displayed product name on the detail page.
   * @returns {Cypress.Chainable<JQuery>}
   */
  static getName = () => cy.get(productDetailPage.itemName);

  /**
   * Read the displayed product description on the detail page.
   * @returns {Cypress.Chainable<JQuery>}
   */
  static getDescription = () => cy.get(productDetailPage.itemDesc);

  /**
   * Read the displayed product price on the detail page.
   * @returns {Cypress.Chainable<JQuery>}
   */
  static getPrice = () => cy.get(productDetailPage.itemPrice);
}
