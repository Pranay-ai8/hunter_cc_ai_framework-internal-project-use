const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Checkout — https://www.saucedemo.com/checkout-step-one.html and checkout-step-two.html
 */
class CheckoutPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            firstNameInput: "#first-name",
            lastNameInput: "#last-name",
            postalCodeInput: "#postal-code",
            continueButton: "#continue",
            cancelButton: "text=Cancel",
            errorMessageBanner: ".error-message-container",
            subtotalLabel: ".summary_subtotal_label",
            taxLabel: ".summary_tax_label",
            totalLabel: ".summary_total_label",
            finishButton: "#finish",
            cartItems: ".cart_item",
        };
    }

    async fillCustomerInfo(firstName, lastName, postalCode) {
        await this.base.typeText(this.Elements.firstNameInput, firstName, "First Name");
        await this.base.typeText(this.Elements.lastNameInput, lastName, "Last Name");
        await this.base.typeText(this.Elements.postalCodeInput, postalCode, "Postal Code");
    }

    async continueToStepTwo() {
        await this.base.waitAndClick(this.Elements.continueButton, "Continue Button");
    }

    async cancel() {
        await this.base.waitAndClick(this.Elements.cancelButton, "Cancel Button");
    }

    async finish() {
        await this.base.waitAndClick(this.Elements.finishButton, "Finish Button");
    }

    async isErrorVisible() {
        return this.base.isVisible(this.Elements.errorMessageBanner);
    }

    isOnStepOne() {
        return this.page.url().includes("checkout-step-one.html");
    }

    isOnStepTwo() {
        return this.page.url().includes("checkout-step-two.html");
    }

    isOnCartPage() {
        return this.page.url().includes("cart.html");
    }

    async cartItemCountOnOverview() {
        return this.base.count(this.Elements.cartItems);
    }

    /**
     * Structural check: grand total = item total + tax. Exact tax formula is
     * undocumented (GAP-FR-02) — avoids a brittle hardcoded expected value.
     */
    async grandTotalEqualsSubtotalPlusTax() {
        const parseCurrency = (text) => parseFloat(text.replace(/[^0-9.]/g, ""));
        const subtotal = parseCurrency(await this.base.textContent(this.Elements.subtotalLabel));
        const tax = parseCurrency(await this.base.textContent(this.Elements.taxLabel));
        const total = parseCurrency(await this.base.textContent(this.Elements.totalLabel));
        return Math.abs(total - (subtotal + tax)) < 0.01;
    }
}

module.exports = CheckoutPage;
