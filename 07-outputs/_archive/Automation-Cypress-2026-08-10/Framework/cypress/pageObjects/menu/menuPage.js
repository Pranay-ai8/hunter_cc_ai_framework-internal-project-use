import globalSels from '../../../utils/selectors/globalSels.js';
import sel from '../../../utils/selectors/menu.js';
import Click from '../../../utils/Interactions/click.js';

const { header } = globalSels;
const { menuPage } = sel;

/**
 * Hamburger Menu / Navigation Page Object — static class, never instantiated.
 */
export default class MenuPage {
  /**
   * Open the hamburger menu.
   */
  static open = () => Click.on(header.burgerMenuButton);

  /**
   * Close the hamburger menu via its close ("X") button.
   */
  static close = () => Click.on(header.burgerCloseButton);

  /**
   * Click "All Items" in the hamburger menu, navigating to Inventory.
   */
  static clickAllItems = () => Click.on(header.menuAllItemsLink);

  /**
   * Click "About" in the hamburger menu.
   */
  static clickAbout = () => Click.on(header.menuAboutLink);

  /**
   * Click "Logout" in the hamburger menu, ending the session.
   */
  static clickLogout = () => Click.on(header.menuLogoutLink);

  /**
   * Click "Reset App State" in the hamburger menu.
   */
  static clickResetAppState = () => Click.on(header.menuResetAppStateLink);

  /**
   * Read the visible text of every link currently rendered in the menu.
   * @returns {Cypress.Chainable<string[]>}
   */
  static getMenuLinkTexts = () =>
    cy.get(menuPage.menuLink).then(($els) => Cypress.$.makeArray($els).map((el) => el.innerText.trim()));
}
