import sel from '../../../utils/selectors/orderConfirmation.js';
import Click from '../../../utils/Interactions/click.js';

const { orderConfirmationPage } = sel;

/**
 * Order Confirmation Page Object — static class, never instantiated.
 */
export default class OrderConfirmationPage {
  /**
   * Read the success header text on the Order Confirmation page.
   * @returns {Cypress.Chainable<JQuery>}
   */
  static getSuccessHeader = () => cy.get(orderConfirmationPage.completeHeader);

  /**
   * Click "Back Home" to return to the Inventory page.
   */
  static clickBackHome = () => Click.on(orderConfirmationPage.backHomeButton);
}
