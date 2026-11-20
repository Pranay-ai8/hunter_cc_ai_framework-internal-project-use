/// <reference types="cypress" />
import LoginPage from '../../pageObjects/signIn/siginPage.js';
import { Verify } from '../../../utils/assertions/index.js';
import userData from '../../fixtures/SignIn/user.json';

const { standard } = userData;

describe('Authentication - Critical Path', { tags: '@smoke' }, () => {
  beforeEach(() => {
    LoginPage.toVisit();
  });

  // TC_LOGIN_001 — Login - Successful Login with standard_user
  it('TC_LOGIN_001: standard_user logs in and is redirected to the Inventory page with 6 products', () => {
    LoginPage.doUserLogin(standard.username, standard.password);

    Verify.theUrl().contains('/inventory.html');
    Verify.theElement('.inventory_item').appearsNTimes(6);
  });
});
