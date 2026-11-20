/**
 * Thin static wrapper around Cypress typing interactions, exposing a
 * fluent `theText(text).into(selector)` pattern. Always clears first so
 * repeated calls in the same test never leave stale characters behind.
 */
export default class Type {
  /**
   * @param {string} text
   * @returns {{ into: (selector: string) => Cypress.Chainable }}
   */
  static theText = (text) => ({
    into: (selector) =>
      cy.get(selector).clear({ force: true }).type(text, { force: true }),
  });

  /**
   * Type into the Nth (zero-based) element matching the selector.
   * @param {string} text
   * @param {number} index
   * @returns {{ into: (selector: string) => Cypress.Chainable }}
   */
  static theTextInIndex = (text, index) => ({
    into: (selector) =>
      cy
        .get(selector)
        .eq(index)
        .clear({ force: true })
        .type(text, { force: true }),
  });
}
