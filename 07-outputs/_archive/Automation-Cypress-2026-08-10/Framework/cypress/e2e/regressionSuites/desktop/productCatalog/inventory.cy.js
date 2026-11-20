/// <reference types="cypress" />
import InventoryPage from '../../../../pageObjects/inventory/inventoryPage.js';
import LoginPage from '../../../../pageObjects/signIn/siginPage.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import userData from '../../../../fixtures/SignIn/user.json';

const { standard, problem, error, visual } = userData;

describe('Product Catalog - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
  });

  // TC_INVENTORY_002 — Sort by Name (A to Z)
  it('TC_INVENTORY_002: Selecting Name (A to Z) reorders products alphabetically ascending', () => {
    InventoryPage.selectSortOption('az');

    InventoryPage.getProductNames().then((names) => {
      const sorted = [...names].sort((a, b) => a.localeCompare(b));
      expect(names).to.deep.equal(sorted);
    });
  });

  // TC_INVENTORY_003 — Sort by Name (Z to A)
  it('TC_INVENTORY_003: Selecting Name (Z to A) reorders products alphabetically descending', () => {
    InventoryPage.selectSortOption('za');

    InventoryPage.getProductNames().then((names) => {
      const sorted = [...names].sort((a, b) => b.localeCompare(a));
      expect(names).to.deep.equal(sorted);
    });
  });

  // TC_INVENTORY_004 — Sort by Price (Low to High)
  it('TC_INVENTORY_004: Selecting Price (low to high) reorders products ascending by price', () => {
    InventoryPage.selectSortOption('lohi');

    InventoryPage.getProductPrices().then((prices) => {
      const sorted = [...prices].sort((a, b) => a - b);
      expect(prices).to.deep.equal(sorted);
    });
  });

  // TC_INVENTORY_005 — Sort by Price (High to Low)
  it('TC_INVENTORY_005: Selecting Price (high to low) reorders products descending by price', () => {
    InventoryPage.selectSortOption('hilo');

    InventoryPage.getProductPrices().then((prices) => {
      const sorted = [...prices].sort((a, b) => b - a);
      expect(prices).to.deep.equal(sorted);
    });
  });
});

// The following three scenarios are Automation Candidate = Partial:
// disputed persona-defect conflicts / documented non-determinism (see
// FunctionalTestCases.md Clarifications Required and GapAnalysis.md
// Conflicting Requirements). Kept out of @smoke/@regression; runnable on
// demand via `npm run test:manual`.
describe('Product Catalog - Defect Personas (Manual)', { tags: '@manual' }, () => {
  // TC_INVENTORY_007 — problem_user Sees Broken/Incorrect Product Images
  it('TC_INVENTORY_007: problem_user Inventory page renders incorrect/swapped product images [MANUAL: persona-defect conflict, see GapAnalysis.md]', () => {
    LoginPage.toVisit();
    LoginPage.doUserLogin(problem.username, problem.password);

    Verify.theElement('.inventory_item_img img').isVisible();
    cy.log(
      'TC_INVENTORY_007 is a documented Partial candidate — the PDF and docx conflict ' +
        'on whether problem_user exhibits broken images, a non-functional cart Remove ' +
        'button, or both. Observing image src values only; no hard pass/fail assertion ' +
        'on "incorrect" images until the conflict is resolved against the live app.'
    );
  });

  // TC_INVENTORY_008 — error_user Sort Interaction May Throw a Random Error
  it('TC_INVENTORY_008: error_user sort interaction may throw a random error [MANUAL: non-deterministic, docx §8]', () => {
    LoginPage.toVisit();
    LoginPage.doUserLogin(error.username, error.password);

    InventoryPage.selectSortOption('za');
    InventoryPage.selectSortOption('lohi');
    cy.log(
      'TC_INVENTORY_008 is a documented Partial candidate — error_user\'s error behavior ' +
        'is explicitly "random" per the docx and not suitable for a deterministic assertion. ' +
        'This block only observes that repeated sort interactions do not hang the runner.'
    );
  });

  // TC_INVENTORY_009 — visual_user Shows CSS/Layout Defects
  it('TC_INVENTORY_009: visual_user Inventory page renders reproducible CSS/layout defects [MANUAL: needs visual-regression baseline]', () => {
    LoginPage.toVisit();
    LoginPage.doUserLogin(visual.username, visual.password);

    Verify.theElement('.inventory_item').appearsNTimes(6);
    cy.log(
      'TC_INVENTORY_009 is a documented Partial candidate — visual/layout defects need a ' +
        'visual-regression tool with a standard_user baseline, not a structural assertion.'
    );
  });
});
