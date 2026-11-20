const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Product Detail — https://www.saucedemo.com/inventory-item.html?id=&lt;N&gt;
 */
class ProductDetailPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            productName: ".inventory_details_name1",
            productDescription: ".inventory_details_desc",
            productPrice: ".inventory_details_price",
        };
    }

    async addToCart() {
        await this.base.withHeal(
            "page.getByRole('button', { name: 'Add to cart' })",
            (element) => element.click(),
            "Add to cart button",
            "click"
        );
        this.logger.info("Added product to cart from Detail page");
    }

    async backToProducts() {
        await this.base.withHeal(
            "page.getByRole('button', { name: 'Back to products' })",
            (element) => element.click(),
            "Back to products button",
            "click"
        );
    }

    async isDetailVisible() {
        const nameVisible = await this.base.isVisible(this.Elements.productName);
        const descVisible = await this.base.isVisible(this.Elements.productDescription);
        const priceVisible = await this.base.isVisible(this.Elements.productPrice);
        return nameVisible && descVisible && priceVisible;
    }
}

module.exports = ProductDetailPage;
