import './commands';
import 'cypress-mochawesome-reporter/register';
import registerCypressGrep from '@cypress/grep';

registerCypressGrep();

// SauceDemo occasionally throws intentional client-side errors for the
// error_user/problem_user personas (documented in GapAnalysis.md). Prevent
// those uncaught exceptions from failing tests that are deliberately
// observing non-deterministic defect behavior.
Cypress.on('uncaught:exception', () => {
  return false;
});
