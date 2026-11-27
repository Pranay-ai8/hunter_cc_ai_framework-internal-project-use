/**
 * Thin static wrapper around `cy.intercept`. SauceDemo has no documented
 * API surface (RequirementAnalysis.md §13), so this file is not exercised
 * by any current spec, but is kept as part of the standard Interactions
 * layer for future API-backed modules.
 */
export default class Intercept {
  /**
   * Register a network intercept under a named alias.
   * @param {string} method - HTTP method (GET, POST, ...)
   * @param {string} url - URL or route matcher
   * @param {string} alias - alias name, without the leading `@`
   */
  static theApi = (method, url, alias) => cy.intercept(method, url).as(alias);
}
