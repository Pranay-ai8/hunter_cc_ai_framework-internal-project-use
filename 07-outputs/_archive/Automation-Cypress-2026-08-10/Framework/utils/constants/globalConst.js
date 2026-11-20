/**
 * Cross-domain constant values used by `utils/helpers/crossModuleFunctions.js`
 * and multiple page objects.
 */
export const application = {
  authentication: 'Authentication',
  productCatalog: 'Product Catalog',
  productDetail: 'Product Detail',
  cart: 'Cart',
  checkout: 'Checkout',
  orderConfirmation: 'Order Confirmation',
  navigation: 'Navigation',
};

export const routes = {
  login: '/',
  inventory: '/inventory.html',
  productDetail: '/inventory-item.html',
  cart: '/cart.html',
  checkoutStepOne: '/checkout-step-one.html',
  checkoutStepTwo: '/checkout-step-two.html',
  checkoutComplete: '/checkout-complete.html',
};

export const personas = {
  standard: 'standard_user',
  lockedOut: 'locked_out_user',
  problem: 'problem_user',
  performanceGlitch: 'performance_glitch_user',
  error: 'error_user',
  visual: 'visual_user',
};

export const sharedPassword = 'secret_sauce';
