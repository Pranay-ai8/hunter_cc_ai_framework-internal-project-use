import sel from '../../../utils/selectors/signIn.js';
import Click from '../../../utils/Interactions/click.js';
import Type from '../../../utils/Interactions/type.js';

const { loginPage } = sel;

/**
 * Login Page Object — static class, never instantiated.
 */
export default class LoginPage {
  /**
   * Navigate to the Login page (SauceDemo's root URL).
   * @param {string} [path]
   */
  static toVisit = (path = '/') => cy.visit(path);

  /**
   * Type a username into the username field.
   * @param {string} username
   */
  static enterUsername = (username) => Type.theText(username).into(loginPage.usernameInput);

  /**
   * Type a password into the password field.
   * @param {string} password
   */
  static enterPassword = (password) => Type.theText(password).into(loginPage.passwordInput);

  /**
   * Click the Login button.
   */
  static clickLogin = () => Click.on(loginPage.loginButton);

  /**
   * Perform a full login: enter username, password, and submit.
   * @param {string} username
   * @param {string} password
   */
  static doUserLogin = (username, password) => {
    LoginPage.enterUsername(username);
    LoginPage.enterPassword(password);
    LoginPage.clickLogin();
  };

  /**
   * Read the visible text of the login error banner.
   * @returns {Cypress.Chainable<string>}
   */
  static getErrorText = () => cy.get(loginPage.errorBanner);
}
