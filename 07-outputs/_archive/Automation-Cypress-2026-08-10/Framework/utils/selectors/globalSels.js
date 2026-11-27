/**
 * Selectors reused across 3+ domains: the hamburger menu (visible on every
 * authenticated page) and the shopping cart icon/badge (visible on
 * Inventory, Product Detail, Cart, and Checkout).
 */
export default {
  header: {
    burgerMenuButton: '#react-burger-menu-btn',
    burgerCloseButton: '#react-burger-cross-btn',
    menuAllItemsLink: '#inventory_sidebar_link',
    menuAboutLink: '#about_sidebar_link',
    menuLogoutLink: '#logout_sidebar_link',
    menuResetAppStateLink: '#reset_sidebar_link',
    shoppingCartLink: '.shopping_cart_link',
    shoppingCartBadge: '.shopping_cart_badge',
  },
};
