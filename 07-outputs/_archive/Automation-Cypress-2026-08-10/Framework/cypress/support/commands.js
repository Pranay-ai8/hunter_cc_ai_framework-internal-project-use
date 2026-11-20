// ***********************************************
// This file is used to create custom Cypress commands
// and overwrite existing commands.
//
// For more info, see:
// https://on.cypress.io/custom-commands
// ***********************************************

import LoginPage from '../pageObjects/signIn/siginPage.js';

/**
 * Log in as the given persona, caching the session (cookies/localStorage)
 * across tests in the same spec file via `cy.session`, then always land
 * fresh on the Inventory page so every test starts from a known state.
 * Only used for personas that successfully reach the Inventory page —
 * negative-login test cases (locked_out_user, blank fields, unknown
 * credentials) drive `LoginPage` directly instead.
 */
Cypress.Commands.add('loginAs', (username, password) => {
  cy.session(
    username,
    () => {
      LoginPage.toVisit();
      LoginPage.doUserLogin(username, password);
      cy.url().should('include', '/inventory.html');
    },
    {
      validate() {
        cy.visit('/inventory.html');
        cy.get('#react-burger-menu-btn').should('exist');
      },
    }
  );
  LoginPage.toVisit('/inventory.html');
  cy.url().should('include', '/inventory.html');
});
