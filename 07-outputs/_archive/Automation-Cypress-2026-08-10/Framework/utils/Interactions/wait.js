/**
 * Alias-based waiting. Never use a bare `cy.wait(<ms>)` — always wait on a
 * named intercept alias registered via `Intercept.theApi(...)`, or rely on
 * Cypress's built-in retry-ability for non-network state changes.
 */
export default class AliasWait {
  /**
   * Wait on a previously aliased `cy.intercept(...)` call.
   * @param {string} alias - alias name, without the leading `@`
   */
  static wait = (alias) => cy.wait(`@${alias}`, { requestTimeout: 15000 });
}
