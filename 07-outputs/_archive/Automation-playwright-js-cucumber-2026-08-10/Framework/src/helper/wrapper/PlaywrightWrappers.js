const { expect } = require("@playwright/test");
const { findHealedLocator } = require("../self-healer");

const FAST_TIMEOUT = 3_000;

/**
 * Shared action helper composed by every page object. Per playwright-javascript-standards.md
 * "Page Object Standards" — all navigation/interaction primitives live here, never duplicated per page.
 */
class PlaywrightWrapper {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
    }

    async waitForElement(locator) {
        const element = this.page.locator(locator);
        await element.waitFor({ state: "visible" });
        return element;
    }

    async goto(path) {
        const baseUrl = process.env.BASEURL;
        if (!baseUrl) {
            throw new Error("BASEURL is undefined. Check your .env.<ENV> file and dotenv.config().");
        }
        const url = new URL(path, baseUrl).toString();
        const response = await this.page.goto(url, { waitUntil: "domcontentloaded", timeout: 60000 });
        if (!response || !response.ok()) {
            throw new Error(`Navigation failed: ${response ? response.status() : "NA"} for URL: ${url}`);
        }
    }

    _createLocator(locator) {
        if (typeof locator === "function") {
            return locator();
        }
        if (typeof locator !== "string") {
            throw new Error("Locator must be a string or function returning a locator.");
        }
        const expressionLike = /^\s*(page\.|this\.page\.)/;
        if (expressionLike.test(locator)) {
            return new Function("page", `return ${locator}`)(this.page);
        }
        return this.page.locator(locator);
    }

    async withHeal(locator, action, objName = "", actionDesc = "action", fallbackLocator = null) {
        let originalError;
        let attemptedLocator;

        try {
            const element = this._createLocator(locator);
            attemptedLocator = typeof locator === "string" ? locator : fallbackLocator;
            await element.waitFor({ state: "attached", timeout: FAST_TIMEOUT });
            await action(element);
            this.logger.info(`${actionDesc} succeeded: ${objName || locator}`);
            return;
        } catch (error) {
            originalError = error;
        }

        const healTarget = typeof locator === "string" ? locator : fallbackLocator;
        if (!healTarget) {
            this.logger.warn(`[self-heal] No fallback locator provided for scoped action: "${objName || actionDesc}"`);
            throw originalError;
        }

        this.logger.warn(`[self-heal] Original locator failed: "${healTarget}". Recording failure before retry.`);
        // objName (e.g. "Login Button") is the strongest available signal of intent —
        // pass it as a hint so keyword matching isn't limited to whatever words happen
        // to appear in the raw locator string.
        const healed = await findHealedLocator(this.page, healTarget, originalError, objName || actionDesc);
        if (!healed.success || !healed.locator) {
            this.logger.warn(`[self-heal] No healed locator available for "${healTarget}"`);
            throw originalError;
        }

        this.logger.info(`[self-heal] Retrying with healed locator: "${healTarget}" → "${healed.locator}" (confidence=${healed.confidence})`);
        const healedLocator = new Function("page", `return ${healed.locator}`)(this.page);
        await action(healedLocator);
    }

    async waitAndClick(locator, objName = "") {
        await this.withHeal(locator, (element) => element.click(), objName, "click");
    }

    async typeText(locator, text, objName = "") {
        await this.withHeal(locator, (element) => element.fill(text), objName, "type text into");
    }

    async typePassword(locator, text, objName = "") {
        await this.withHeal(locator, (element) => element.fill(text), objName, "type password into");
    }

    async selectOption(locator, label, objName = "") {
        await this.withHeal(
            locator,
            async (element) => {
                await element.selectOption({ label });
            },
            objName,
            "select option"
        );
    }

    async waitForURL(expectedUrlPattern, timeout = 15000) {
        await expect(this.page).toHaveURL(expectedUrlPattern, { timeout });
    }

    async textContent(locator) {
        const element = await this.waitForElement(locator);
        return element.textContent();
    }

    async isVisible(locator) {
        return this.page.locator(locator).isVisible();
    }

    async isEnabled(locator) {
        return this.page.locator(locator).isEnabled();
    }

    async count(locator) {
        return this.page.locator(locator).count();
    }

    async allTextContents(locator) {
        return this.page.locator(locator).allTextContents();
    }

    async clickFirst(locator, objName = "") {
        await this.page.locator(locator).first().click();
        this.logger.info(`Clicked first match of: ${objName || locator}`);
    }

    async filterByText(locator, text) {
        return this.page.locator(locator).filter({ hasText: text });
    }

    async goBack() {
        await this.page.goBack();
    }
}

module.exports = PlaywrightWrapper;
