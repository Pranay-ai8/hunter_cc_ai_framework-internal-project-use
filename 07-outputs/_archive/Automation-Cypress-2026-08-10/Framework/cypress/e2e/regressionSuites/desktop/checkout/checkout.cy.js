/// <reference types="cypress" />
import InventoryPage from '../../../../pageObjects/inventory/inventoryPage.js';
import CartPage from '../../../../pageObjects/cart/cartPage.js';
import CheckoutPage from '../../../../pageObjects/checkout/checkoutPage.js';
import LoginPage from '../../../../pageObjects/signIn/siginPage.js';
import { Verify } from '../../../../../utils/assertions/index.js';
import productData from '../../../../fixtures/Inventory/products.json';
import userData from '../../../../fixtures/SignIn/user.json';
import customerInfo from '../../../../fixtures/Checkout/customerInfo.json';

const { standard, error, problem } = userData;
const { productA } = productData;
const { valid, longAlphanumericPostalCode } = customerInfo;

describe('Checkout - Regression', { tags: '@regression' }, () => {
  beforeEach(() => {
    cy.loginAs(standard.username, standard.password);
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();
    CartPage.clickCheckout();
  });

  // TC_CHECKOUT_002 — Blank First Name Blocked
  it('TC_CHECKOUT_002: Checkout Step 1 is rejected with an inline error when First Name is blank', () => {
    CheckoutPage.fillCustomerInfo({ lastName: valid.lastName, postalCode: valid.postalCode });
    CheckoutPage.clickContinue();

    Verify.theElement('.error-message-container').isVisible();
    Verify.theUrl().contains('/checkout-step-one.html');
  });

  // TC_CHECKOUT_003 — Blank Last Name Blocked
  it('TC_CHECKOUT_003: Checkout Step 1 is rejected with an inline error when Last Name is blank', () => {
    CheckoutPage.fillCustomerInfo({ firstName: valid.firstName, postalCode: valid.postalCode });
    CheckoutPage.clickContinue();

    Verify.theElement('.error-message-container').isVisible();
    Verify.theUrl().contains('/checkout-step-one.html');
  });

  // TC_CHECKOUT_004 — Blank Postal Code Blocked
  it('TC_CHECKOUT_004: Checkout Step 1 is rejected with an inline error when Postal Code is blank', () => {
    CheckoutPage.fillCustomerInfo({ firstName: valid.firstName, lastName: valid.lastName });
    CheckoutPage.clickContinue();

    Verify.theElement('.error-message-container').isVisible();
    Verify.theUrl().contains('/checkout-step-one.html');
  });

  // TC_CHECKOUT_006 — Step 2 Displays Correct Items, Tax, and Total
  it('TC_CHECKOUT_006: Order Overview displays the correct cart items, item total, tax, and grand total', () => {
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();

    Verify.theElement('.cart_item').appearsNTimes(1);
    Verify.theElement('.inventory_item_name').contains(productA);

    CheckoutPage.getSubtotal().then((subtotal) => {
      CheckoutPage.getTax().then((tax) => {
        CheckoutPage.getTotal().then((total) => {
          expect(subtotal).to.be.greaterThan(0);
          expect(tax).to.be.at.least(0);
          // Exact tax formula is undocumented (GAP-FR-02) — assert the
          // relationship, not a hardcoded expected amount.
          expect(total).to.be.closeTo(subtotal + tax, 0.01);
        });
      });
    });
  });

  // TC_CHECKOUT_008 — Cancel on Step 1 Returns to Cart
  it('TC_CHECKOUT_008: Clicking Cancel on Checkout Step 1 returns the user to the Cart page', () => {
    CheckoutPage.clickCancelStepOne();

    Verify.theUrl().contains('/cart.html');
    Verify.theElement('.cart_item').appearsNTimes(1);
  });

  // TC_CHECKOUT_009 — Cancel on Step 2 Returns to Inventory (confirmed live 2026-08-07, GAP-FR-05)
  it('TC_CHECKOUT_009: Clicking Cancel on the Order Overview page returns the user to the Inventory page', () => {
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();

    CheckoutPage.clickCancelStepTwo();

    Verify.theUrl().contains('/inventory.html');
  });

  // TC_CHECKOUT_012 — Postal Code Accepts Long/Alphanumeric Value Without Error
  it('TC_CHECKOUT_012: Postal Code field accepts an unusually long/alphanumeric value without an application error', () => {
    CheckoutPage.fillCustomerInfo(longAlphanumericPostalCode);
    CheckoutPage.clickContinue();

    // GAP-BR-01: format/length rule undocumented — accept either a clean
    // proceed to Step 2 or a visible validation message, but never a crash.
    cy.get('body').then(($body) => {
      const proceededToStepTwo = $body.find('.summary_info').length > 0;
      const showsValidationError = $body.find('.error-message-container').length > 0;
      expect(
        proceededToStepTwo || showsValidationError,
        'Application either proceeds to Step 2 or shows a validation message'
      ).to.be.true;
    });
  });
});

// TC_CHECKOUT_010/011 — Automation Candidate = Partial: error_user's documented
// non-deterministic errors (docx §8) and the disputed problem_user Last-Name-field
// conflict between source PRDs (see GapAnalysis.md). Observed, not hard-asserted.
describe('Checkout - Defect Personas (Manual)', { tags: '@manual' }, () => {
  // TC_CHECKOUT_010 — error_user May Throw a Random Error During Checkout
  it('TC_CHECKOUT_010: error_user may throw a random error during checkout [MANUAL: non-deterministic, docx §8]', () => {
    LoginPage.toVisit();
    LoginPage.doUserLogin(error.username, error.password);
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();
    CartPage.clickCheckout();
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();

    cy.log(
      'TC_CHECKOUT_010 observation: reached this point without the test runner ' +
        'crashing. error_user\'s error behavior is explicitly "random" per the docx ' +
        'and is not suitable for a deterministic pass/fail assertion.'
    );
  });

  // TC_CHECKOUT_011 — problem_user's Last Name Field May Fail
  it("TC_CHECKOUT_011: problem_user's Last Name field may fail during checkout [MANUAL: persona-defect conflict, see GapAnalysis.md]", () => {
    LoginPage.toVisit();
    LoginPage.doUserLogin(problem.username, problem.password);
    InventoryPage.addToCart(productA);
    CartPage.openViaCartIcon();
    CartPage.clickCheckout();
    CheckoutPage.fillCustomerInfo(valid);
    CheckoutPage.clickContinue();

    cy.get('body').then(($body) => {
      const proceededToStepTwo = $body.find('.summary_info').length > 0;
      cy.log(
        `TC_CHECKOUT_011 observation: proceeded to Step 2 = ${proceededToStepTwo}. ` +
          'Per the docx, the Last Name field "may fail" for problem_user; the PDF does ' +
          'not mention this defect. Not hard-asserting either outcome.'
      );
    });
  });
});
