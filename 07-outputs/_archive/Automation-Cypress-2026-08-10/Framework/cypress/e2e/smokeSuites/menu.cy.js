/// <reference types="cypress" />
import MenuPage from '../../pageObjects/menu/menuPage.js';
import { Verify } from '../../../utils/assertions/index.js';
import userData from '../../fixtures/SignIn/user.json';

const { standard } = userData;

describe('Navigation - Critical Path', { tags: '@smoke' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_MENU_005 — Logout Ends Session and Returns to Login
  it('TC_MENU_005: Selecting Logout ends the session and returns the user to the Login page', () => {
    MenuPage.open();
    MenuPage.clickLogout();

    Verify.theUrl().equals(`${Cypress.config('baseUrl')}`);
    Verify.theElement('#login-button').isVisible();
  });
});
