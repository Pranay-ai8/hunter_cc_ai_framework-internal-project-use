const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Navigation / Hamburger Menu.
 *
 * NOTE: #logout_sidebar_link and #reset_sidebar_link are directly documented in
 * RequirementAnalysis.md §12 (docx locator table). #inventory_sidebar_link and
 * #about_sidebar_link are NOT in that table — they follow the same
 * "<page>_sidebar_link" convention and are included based on the app's known
 * public structure, but should be verified against the live DOM before relying
 * on them, since they were not explicitly confirmed by either source PRD.
 */
class MenuPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            menuButton: "#react-burger-menu-btn",
            allItemsLink: "#inventory_sidebar_link",
            aboutLink: "#about_sidebar_link",
            logoutLink: "#logout_sidebar_link",
            resetAppStateLink: "#reset_sidebar_link",
        };
    }

    async open() {
        await this.base.waitAndClick(this.Elements.menuButton, "Hamburger Menu Button");
    }

    async areMenuLinksVisible() {
        const results = await Promise.all([
            this.base.isVisible(this.Elements.allItemsLink),
            this.base.isVisible(this.Elements.aboutLink),
            this.base.isVisible(this.Elements.logoutLink),
            this.base.isVisible(this.Elements.resetAppStateLink),
        ]);
        return results.every(Boolean);
    }

    async clickAllItems() {
        await this.base.waitAndClick(this.Elements.allItemsLink, "All Items Link");
    }

    async clickAbout() {
        await this.base.waitAndClick(this.Elements.aboutLink, "About Link");
    }

    async logout() {
        await this.base.waitAndClick(this.Elements.logoutLink, "Logout Link");
    }

    async resetAppState() {
        await this.base.waitAndClick(this.Elements.resetAppStateLink, "Reset App State Link");
    }

    isOnLoginPage() {
        return this.page.url() === "https://www.saucedemo.com/";
    }
}

module.exports = MenuPage;
