// The individual hamburger menu links (All Items/About/Logout/Reset App
// State) live in `globalSels.js` since they are reused across every
// domain's cross-page navigation. This file adds the menu-specific
// container selector needed to assert "exactly these 4 links" as a set.
export default {
  menuPage: {
    menuItemList: '.bm-item-list',
    menuLink: '.bm-item-list a.menu-item',
  },
};
