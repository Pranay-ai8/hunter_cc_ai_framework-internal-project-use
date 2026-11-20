/**
 * Thin static wrapper around Cypress checkbox/toggle interactions.
 * SauceDemo has no checkbox/toggle controls today, but this file is kept
 * as part of the standard Interactions layer for future modules.
 */
export default class Check {
  /**
   * Check a checkbox/toggle if it is not already checked.
   * @param {string} selector
   */
  static on = (selector) =>
    cy.get(selector).then(($el) => {
      if (!$el.is(':checked')) {
        cy.wrap($el).check({ force: true });
      }
    });

  /**
   * Uncheck a checkbox/toggle if it is currently checked.
   * @param {string} selector
   */
  static off = (selector) =>
    cy.get(selector).then(($el) => {
      if ($el.is(':checked')) {
        cy.wrap($el).uncheck({ force: true });
      }
    });
}
