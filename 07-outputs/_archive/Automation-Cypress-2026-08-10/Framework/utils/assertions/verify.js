/**
 * Fluent, curried assertion helpers. Compose these from Page Objects/specs
 * instead of asserting inline with raw `cy.get(sel).should(...)`.
 */
export default class Verify {
  /**
   * Start a fluent assertion chain scoped to a single selector.
   * @param {string} selector
   * @param {string} [alias] - optional human-readable name shown in the Cypress log
   * @returns {object} fluent assertion methods, each returning the underlying Cypress chainable
   */
  static theElement = (selector, alias) => {
    const label = alias || selector;
    const get = () => cy.get(selector, { log: false }).should('exist');

    return {
      contains: (text) =>
        get().should('contain.text', text, { message: `${label} contains "${text}"` }),
      hasText: (text) => get().should('have.text', text),
      hasValue: (value) => get().should('have.value', value),
      isVisible: () => get().should('be.visible'),
      isNotVisible: () => cy.get(selector, { log: false }).should('not.be.visible'),
      isDisabled: () => get().should('be.disabled'),
      isEnabled: () => get().should('be.enabled'),
      doesNotExist: () => cy.get(selector, { log: false }).should('not.exist'),
      doesNotContain: (text) => get().should('not.contain.text', text),
      appearsNTimes: (n) => cy.get(selector, { log: false }).should('have.length', n),
      hasClass: (className) => get().should('have.class', className),
      hasCssBackedImage: () =>
        get().should(($el) => {
          const src = $el.attr('src') || '';
          expect(src, `${label} has a non-empty image src`).to.not.equal('');
        }),
    };
  };

  /**
   * Assertions against the current browser URL.
   * @returns {object} fluent assertion methods
   */
  static theUrl = () => ({
    contains: (pathFragment) => cy.url().should('include', pathFragment),
    equals: (url) => cy.url().should('eq', url),
    matches: (regex) => cy.url().should('match', regex),
    doesNotContain: (pathFragment) => cy.url().should('not.include', pathFragment),
  });

  /**
   * Assertions against the current document title.
   * @returns {object} fluent assertion methods
   */
  static theTitle = () => ({
    is: (title) => cy.title().should('eq', title),
  });

  /**
   * Assertions against `localStorage`/`sessionStorage`-backed app state
   * (used sparingly — SauceDemo persists cart/session data client-side).
   * @param {string} key
   * @returns {object} fluent assertion methods
   */
  static theLocalStorageItem = (key) => ({
    isAbsent: () =>
      cy.window().then((win) => {
        expect(win.localStorage.getItem(key)).to.be.null;
      }),
  });
}
