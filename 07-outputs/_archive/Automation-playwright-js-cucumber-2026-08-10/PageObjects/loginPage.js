const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Authentication — https://www.saucedemo.com/
 * Locators are the REAL, documented selectors from RequirementAnalysis.md §12
 * (source: SauceDemo_Understanding_Document-ClaudeCode.docx Section 6).
 */
class LoginPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            usernameInput: "#user-name",
            passwordInput: "#password",
            loginButton: "#login-button",
            errorMessageBanner: ".error-message-container",
        };
    }

    async navigate() {
        await this.base.goto("/");
    }

    async login(username, password) {
        await this.base.typeText(this.Elements.usernameInput, username, "Username");
        await this.base.typePassword(this.Elements.passwordInput, password, "Password");
        await this.base.waitAndClick(this.Elements.loginButton, "Login");
    }

    async isErrorVisible() {
        return this.base.isVisible(this.Elements.errorMessageBanner);
    }

    async errorText() {
        return this.base.textContent(this.Elements.errorMessageBanner);
    }

    isOnInventoryPage() {
        return this.page.url().includes("/inventory.html");
    }

    isOnLoginPage() {
        return this.page.url() === "https://www.saucedemo.com/";
    }
}

module.exports = LoginPage;
