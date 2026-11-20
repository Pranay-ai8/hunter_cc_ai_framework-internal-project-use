const PlaywrightWrapper = require("../Framework/src/helper/wrapper/PlaywrightWrappers");

/**
 * Module: Product Catalog — https://www.saucedemo.com/inventory.html
 */
class InventoryPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            inventoryItems: ".inventory_item",
            inventoryItemNames: ".inventory_item_name",
            inventoryItemPrices: ".inventory_item_price",
            inventoryItemImages: ".inventory_item_img img",
            sortDropdown: ".product_sort_container",
            cartBadge: ".shopping_cart_badge",
            cartLink: ".shopping_cart_link",
        };
    }

    async productCount() {
        return this.base.count(this.Elements.inventoryItems);
    }

    async productNames() {
        return this.base.allTextContents(this.Elements.inventoryItemNames);
    }

    async productPrices() {
        const texts = await this.base.allTextContents(this.Elements.inventoryItemPrices);
        return texts.map((t) => parseFloat(t.replace("$", "")));
    }

    async sortBy(label) {
        await this.base.selectOption(this.Elements.sortDropdown, label, "Product Sort Dropdown");
    }

    async addToCart(productName) {
        const card = await this.base.filterByText(this.Elements.inventoryItems, productName);
        await this.base.withHeal(
            () => card.getByRole("button", { name: "Add to cart" }),
            (element) => element.click(),
            `Add to cart button for '${productName}'`,
            "click",
            "page.getByRole('button', { name: 'Add to cart' })"
        );
        this.logger.info(`Added '${productName}' to cart`);
    }

    async removeFromCart(productName) {
        const card = await this.base.filterByText(this.Elements.inventoryItems, productName);
        await this.base.withHeal(
            () => card.getByRole("button", { name: "Remove" }),
            (element) => element.click(),
            `Remove button for '${productName}'`,
            "click",
            "page.getByRole('button', { name: 'Remove' })"
        );
        this.logger.info(`Removed '${productName}' from cart via Inventory page`);
    }

    async openCart() {
        await this.base.waitAndClick(this.Elements.cartLink, "Shopping Cart Link");
    }

    async isCartBadgeVisible() {
        return this.base.isVisible(this.Elements.cartBadge);
    }

    async openProductByName() {
        await this.base.clickFirst(this.Elements.inventoryItemNames, "First Product Name");
    }

    async openProductByImage() {
        await this.base.clickFirst(this.Elements.inventoryItemImages, "First Product Image");
    }

    /**
     * Observational check for problem_user's documented broken/swapped images —
     * returns raw src attributes rather than asserting, since the exact defect is
     * disputed between source PRDs (see GapAnalysis.md Conflicting Requirements).
     */
    async productImageSources() {
        return this.page.locator(this.Elements.inventoryItemImages).evaluateAll((imgs) => imgs.map((img) => img.src));
    }

    async captureScreenshot(name) {
        await this.page.screenshot({ path: `test-results/screenshots/${name}_${Date.now()}.png` });
    }
}

module.exports = InventoryPage;
