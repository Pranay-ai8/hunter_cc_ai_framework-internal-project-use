import globalSels from '../selectors/globalSels.js';
import Click from '../Interactions/click.js';

const { header } = globalSels;

/**
 * Cross-module static helpers only — module-specific logic belongs in that
 * module's own Page Object instead.
 */
export default class HelperFunction {
  /**
   * Placeholder for centralized `cy.intercept` registration, used by
   * multiple specs' `beforeEach` hooks once API scenarios exist for this
   * application. SauceDemo has no documented API surface today
   * (RequirementAnalysis.md §13), so no intercepts are registered yet.
   */
  static globalIntercept = () => {
    // Intentionally empty — no API surface to intercept for this app yet.
  };

  /**
   * Open the hamburger menu and reset the app state (clears the cart and
   * resets sort order) without logging out. Used by specs that need a
   * guaranteed-clean cart/sort state before asserting.
   */
  static resetAppState = () => {
    Click.on(header.burgerMenuButton);
    Click.on(header.menuResetAppStateLink);
    cy.get(header.burgerCloseButton).click({ force: true });
  };

  /**
   * Open the hamburger menu and log out, ending the session.
   */
  static logout = () => {
    Click.on(header.burgerMenuButton);
    Click.on(header.menuLogoutLink);
  };

  /**
   * Convert a product's display name into the slug SauceDemo uses for its
   * `id="add-to-cart-<slug>"` / `id="remove-<slug>"` button IDs
   * (e.g. "Sauce Labs Backpack" -> "sauce-labs-backpack").
   * Used by the Inventory, Product Detail, and Cart Page Objects, which is
   * why it lives here rather than being duplicated in each of them.
   * @param {string} productName
   * @returns {string}
   */
  static productSlug = (productName) =>
    productName
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '');
}
