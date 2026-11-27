const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Cart — https://www.saucedemo.com/cart.html
 */
class CartPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            cartItems: ".cart_item",
            checkoutButton: "#check",
            continueShoppingButton: "text=Continue Shopping",
        };
    }

    async removeItem(productName) {
        const row = await this.base.filterByText(this.Elements.cartItems, productName);
        await this.base.withHeal(
            () => row.getByRole("button", { name: "Remove" }),
            (element) => element.click(),
            `Remove button for '${productName}'`,
            "click",
            "page.getByRole('button', { name: 'Remove' })"
        );
        this.logger.info(`Removed '${productName}' from Cart page`);
    }

    async isItemInCart(productName) {
        const row = await this.base.filterByText(this.Elements.cartItems, productName);
        return (await row.count()) > 0;
    }

    async itemCount() {
        return this.base.count(this.Elements.cartItems);
    }

    async checkout() {
        await this.base.waitAndClick(this.Elements.checkoutButton, "Checkout Button");
    }

    async continueShopping() {
        await this.base.waitAndClick(this.Elements.continueShoppingButton, "Continue Shopping Button");
    }
}

module.exports = CartPage;
