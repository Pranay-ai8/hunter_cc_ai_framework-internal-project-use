/// <reference types="cypress" />
import LoginPage from '../../../../pageObjects/signIn/siginPage.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import { toastMessages } from '../../../../../utils/constants/signIn/login.js';
import userData from '../../../../fixtures/SignIn/user.json';

const { lockedOut, standard, performanceGlitch, problem, error, visual, unknown } = userData;

describe('Authentication - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    LoginPage.toVisit();
  });

  // TC_LOGIN_002 — locked_out_user Is Blocked
  it('TC_LOGIN_002: locked_out_user cannot log in and receives the documented lock-out error', () => {
    LoginPage.doUserLogin(lockedOut.username, lockedOut.password);

    Verify.theElement('.error-message-container').contains(toastMessages.lockedOut);
    Verify.theUrl().doesNotContain('/inventory.html');
  });

  // TC_LOGIN_003 — Blank Username Shows Required Error
  it('TC_LOGIN_003: Login is rejected with the documented error when the username field is blank', () => {
    LoginPage.enterPassword(standard.password);
    LoginPage.clickLogin();

    Verify.theElement('.error-message-container').contains(toastMessages.usernameRequired);
    Verify.theUrl().doesNotContain('/inventory.html');
  });

  // TC_LOGIN_004 — Blank Password Shows Required Error
  it('TC_LOGIN_004: Login is rejected with the documented error when the password field is blank', () => {
    LoginPage.enterUsername(standard.username);
    LoginPage.clickLogin();

    Verify.theElement('.error-message-container').contains(toastMessages.passwordRequired);
    Verify.theUrl().doesNotContain('/inventory.html');
  });

  // TC_LOGIN_005 — Unknown Credentials Rejected
  it('TC_LOGIN_005: Login is rejected with the exact documented error for an unknown username/password combination', () => {
    LoginPage.doUserLogin(unknown.username, unknown.password);

    Verify.theElement('.error-message-container').contains(toastMessages.invalidCredentials);
    Verify.theUrl().doesNotContain('/inventory.html');
  });

  // TC_LOGIN_006 — performance_glitch_user Login Has Artificial Delay
  it('TC_LOGIN_006: performance_glitch_user logs in successfully with a measurable artificial delay', () => {
    const startedAt = Date.now();
    LoginPage.doUserLogin(performanceGlitch.username, performanceGlitch.password);

    Verify.theUrl().contains('/inventory.html');
    cy.then(() => {
      const elapsedMs = Date.now() - startedAt;
      expect(elapsedMs, 'elapsed time before Inventory renders').to.be.greaterThan(2000);
    });
  });

  // TC_LOGIN_007 — problem_user Logs In Successfully
  it('TC_LOGIN_007: problem_user authenticates successfully and reaches the Inventory page', () => {
    LoginPage.doUserLogin(problem.username, problem.password);

    Verify.theUrl().contains('/inventory.html');
  });

  // TC_LOGIN_008 — error_user Logs In Successfully
  it('TC_LOGIN_008: error_user authenticates successfully and reaches the Inventory page', () => {
    LoginPage.doUserLogin(error.username, error.password);

    Verify.theUrl().contains('/inventory.html');
  });

  // TC_LOGIN_009 — visual_user Logs In Successfully
  it('TC_LOGIN_009: visual_user authenticates successfully and reaches the Inventory page', () => {
    LoginPage.doUserLogin(visual.username, visual.password);

    Verify.theUrl().contains('/inventory.html');
  });
});

// TC_LOGIN_010 — Automation Candidate = Partial (see FunctionalTestCases.md / GapAnalysis.md GAP-FR-03).
// A real 10-minute idle wait is impractical for CI, and the exact post-timeout UX is
// undocumented. Kept out of @smoke/@regression until a mocked/reduced timeout
// environment is available; runnable on demand via `npm run test:manual`.
describe('Authentication - Session Timeout (Manual)', { tags: '@manual' }, () => {
  it('TC_LOGIN_010: Session auto-expires after 10 minutes of inactivity [MANUAL/NON-CI: real 10-minute wait]', () => {
    cy.log(
      'TC_LOGIN_010 is a documented Partial automation candidate — see GAP-FR-03. ' +
        'Not executed with a real 10-minute wait in CI; this block exists so the ' +
        'scenario stays traceable without silently disappearing from the suite.'
    );
  });
});
