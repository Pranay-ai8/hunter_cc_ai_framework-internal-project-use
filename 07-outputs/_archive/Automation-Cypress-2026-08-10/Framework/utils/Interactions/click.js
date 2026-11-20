/**
 * Thin static wrapper around Cypress click interactions.
 * Never instantiated — compose these statics from Page Objects/specs
 * instead of calling `cy.get(sel).click()` directly.
 */
export default class Click {
  /**
   * Click the first/only element matching the selector.
   * @param {string} selector
   */
  static on = (selector) => cy.get(selector).should('be.visible').click();

  /**
   * Click an element identified by a CSS selector scoped inside
   * a container that contains the given visible text.
   * @param {string} containerSelector
   * @param {string} text
   */
  static onContainText = (containerSelector, text) =>
    cy.contains(containerSelector, text).click();

  /**
   * Click the first element matching the selector when multiple exist.
   * @param {string} selector
   */
  static onFirst = (selector) => cy.get(selector).first().click();

  /**
   * Click the last element matching the selector when multiple exist.
   * @param {string} selector
   */
  static onLast = (selector) => cy.get(selector).last().click();

  /**
   * Click the Nth (zero-based) element matching the selector.
   * @param {string} selector
   * @param {number} index
   */
  static onIndexNo = (selector, index) => cy.get(selector).eq(index).click();

  /**
   * Click every element matching the selector, one at a time.
   * @param {string} selector
   */
  static onMultiple = (selector) => {
    cy.get(selector).each(($el) => {
      cy.wrap($el).click();
    });
  };

  /**
   * Force-click an element even if Cypress considers it not actionable
   * (e.g. covered, zero-size, or off-screen).
   * @param {string} selector
   */
  static forcefullyOn = (selector) => cy.get(selector).click({ force: true });
}
