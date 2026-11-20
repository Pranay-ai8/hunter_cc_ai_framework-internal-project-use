const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Order Confirmation — https://www.saucedemo.com/checkout-complete.html
 */
class OrderConfirmationPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            completeHeader: ".complete-header",
            backHomeButton: "text=Back Home",
        };
    }

    async isSuccessMessageVisible() {
        return this.base.isVisible(this.Elements.completeHeader);
    }

    async backHome() {
        await this.base.waitAndClick(this.Elements.backHomeButton, "Back Home Button");
    }

    isOnConfirmationPage() {
        return this.page.url().includes("checkout-complete.html");
    }
}

module.exports = OrderConfirmationPage;
