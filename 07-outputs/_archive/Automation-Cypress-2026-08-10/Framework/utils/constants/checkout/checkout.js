/**
 * Checkout module constants. Exact inline validation error text is
 * undocumented (GAP-FR-01 in GapAnalysis.md) — specs assert error
 * *visibility*/*prefix*, not an unconfirmed exact string, per the
 * GapAnalysis recommendation.
 */
export const toastMessages = {
  firstNameRequiredPrefix: 'Error: First Name is required',
  lastNameRequiredPrefix: 'Error: Last Name is required',
  postalCodeRequiredPrefix: 'Error: Postal Code is required',
};

export const buttonsInnerText = {
  continue: 'Continue',
  finish: 'Finish',
  cancel: 'Cancel',
};

export const urlText = {
  stepOne: '/checkout-step-one.html',
  stepTwo: '/checkout-step-two.html',
  complete: '/checkout-complete.html',
  cart: '/cart.html',
  inventory: '/inventory.html',
};
