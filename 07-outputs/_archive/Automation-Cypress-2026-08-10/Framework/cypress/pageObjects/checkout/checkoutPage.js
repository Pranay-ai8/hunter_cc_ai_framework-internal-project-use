import sel from '../../../utils/selectors/checkout.js';
import Click from '../../../utils/Interactions/click.js';
import Type from '../../../utils/Interactions/type.js';

const { checkoutStepOne, checkoutStepTwo } = sel;

/**
 * Checkout Page Object — covers both Step 1 (Customer Information) and
 * Step 2 (Order Overview). Static class, never instantiated.
 */
export default class CheckoutPage {
  /**
   * Type the First Name field on Checkout Step 1.
   * @param {string} firstName
   */
  static enterFirstName = (firstName) => Type.theText(firstName).into(checkoutStepOne.firstNameInput);

  /**
   * Type the Last Name field on Checkout Step 1.
   * @param {string} lastName
   */
  static enterLastName = (lastName) => Type.theText(lastName).into(checkoutStepOne.lastNameInput);

  /**
   * Type the Postal Code field on Checkout Step 1.
   * @param {string} postalCode
   */
  static enterPostalCode = (postalCode) => Type.theText(postalCode).into(checkoutStepOne.postalCodeInput);

  /**
   * Fill all three Checkout Step 1 fields, skipping any left undefined so
   * negative/blank-field test cases can omit the field under test.
   * @param {{firstName?: string, lastName?: string, postalCode?: string}} customerInfo
   */
  static fillCustomerInfo = ({ firstName, lastName, postalCode } = {}) => {
    if (firstName !== undefined) CheckoutPage.enterFirstName(firstName);
    if (lastName !== undefined) CheckoutPage.enterLastName(lastName);
    if (postalCode !== undefined) CheckoutPage.enterPostalCode(postalCode);
  };

  /**
   * Click "Continue" on Checkout Step 1.
   */
  static clickContinue = () => Click.on(checkoutStepOne.continueButton);

  /**
   * Click "Cancel" on Checkout Step 1 (returns to Cart).
   */
  static clickCancelStepOne = () => Click.on(checkoutStepOne.cancelButton);

  /**
   * Read the visible text of the Checkout Step 1 error banner.
   * @returns {Cypress.Chainable<JQuery>}
   */
  static getStepOneErrorText = () => cy.get(checkoutStepOne.errorBanner);

  /**
   * Click "Finish" on Checkout Step 2 (Order Overview), completing the purchase.
   */
  static clickFinish = () => Click.on(checkoutStepTwo.finishButton);

  /**
   * Click "Cancel" on Checkout Step 2 (returns to Inventory).
   */
  static clickCancelStepTwo = () => Click.on(checkoutStepTwo.cancelButton);

  /**
   * Read the item subtotal displayed on the Order Overview page, as a number.
   * @returns {Cypress.Chainable<number>}
   */
  static getSubtotal = () =>
    cy.get(checkoutStepTwo.subtotalLabel).then(($el) => parseFloat($el.text().replace(/[^0-9.]/g, '')));

  /**
   * Read the tax amount displayed on the Order Overview page, as a number.
   * @returns {Cypress.Chainable<number>}
   */
  static getTax = () =>
    cy.get(checkoutStepTwo.taxLabel).then(($el) => parseFloat($el.text().replace(/[^0-9.]/g, '')));

  /**
   * Read the grand total displayed on the Order Overview page, as a number.
   * @returns {Cypress.Chainable<number>}
   */
  static getTotal = () =>
    cy.get(checkoutStepTwo.totalLabel).then(($el) => parseFloat($el.text().replace(/[^0-9.]/g, '')));
}
