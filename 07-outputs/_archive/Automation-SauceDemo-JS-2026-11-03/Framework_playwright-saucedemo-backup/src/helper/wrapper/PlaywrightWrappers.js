const { expect } = require("@playwright/test");
const { findHealedLocator } = require("../self-healer");

class PlaywrightWrapper {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger || console;
    }

    // ---------------------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------------------

    async goto(pathOrUrl) {
        const baseUrl = process.env.BASEURL;
        if (!baseUrl) throw new Error("BASEURL is undefined. Check your .env file and dotenv.config()");
        const url = new URL(pathOrUrl, baseUrl).toString();
        const response = await this.page.goto(url, { waitUntil: "domcontentloaded", timeout: 30000 });
        if (!response || !response.ok()) {
            throw new Error(`Navigation failed: ${response ? response.status() : "NA"} for URL: ${url}`);
        }
        this.logger.info ? this.logger.info(`Navigated to ${url}`) : this.logger(`Navigated to ${url}`);
    }

    async waitForURL(expectedURL, timeout = 10000) {
        await expect(this.page).toHaveURL(expectedURL, { timeout });
    }

    // ---------------------------------------------------------------------
    // Element resolution / waits
    // ---------------------------------------------------------------------

    async waitForElement(locator, state = "visible", timeout = 15000) {
        const element = this.page.locator(locator).first();
        await element.waitFor({ state, timeout });
        return element;
    }

    // ---------------------------------------------------------------------
    // Self-healing core — see src/helper/self-healer.js
    // ---------------------------------------------------------------------

    /**
     * Tries `action` against `locator`. On failure, attempts a rule-based self-heal
     * via findHealedLocator() and retries the same action against the healed locator.
     * Only action-oriented methods (click/type/select) route through this — never
     * read/assertion methods.
     */
    async withHeal(locator, action, objName = "", actionDesc = "action", fallbackLocator = null) {
        const target = fallbackLocator || locator;
        try {
            const element = this.page.locator(target).first();
            await element.waitFor({ state: "attached", timeout: 3000 });
            return await action(element);
        } catch (originalError) {
            this._log("warn", `Locator failed for ${actionDesc} on "${objName || target}" — attempting self-heal. Reason: ${originalError.message.split("\n")[0]}`);

            const healResult = await findHealedLocator(this.page, target, originalError, objName || actionDesc);
            if (!healResult.success) {
                this._log("error", `Self-heal failed for "${objName || target}" (confidence=${healResult.confidence.toFixed(2)}). Rethrowing original error.`);
                throw originalError;
            }

            this._log("warn", `Self-healed "${objName || target}" -> "${healResult.locator}" (confidence=${healResult.confidence.toFixed(2)}).`);
            const healedElement = this.page.locator(healResult.locator).first();
            return await action(healedElement);
        }
    }

    _log(level, message) {
        if (this.logger && typeof this.logger[level] === "function") {
            this.logger[level](message);
        } else {
            console.log(`[${level.toUpperCase()}] ${message}`);
        }
    }

    // ---------------------------------------------------------------------
    // Action-oriented methods — routed through withHeal()
    // ---------------------------------------------------------------------

    async waitAndClick(locator, objName = "") {
        await this.withHeal(locator, async (element) => {
            await element.waitFor({ state: "visible" });
            await element.click();
        }, objName, "click");
        this._log("info", `Clicked on element: ${objName || locator}`);
    }

    async typeText(locator, text, objName = "") {
        await this.withHeal(locator, async (element) => {
            await element.waitFor({ state: "visible" });
            await element.fill(text);
        }, objName, "type");
        this._log("info", `Typed "${text}" into element: ${objName || locator}`);
    }

    async typePassword(locator, text, objName = "") {
        await this.withHeal(locator, async (element) => {
            await element.waitFor({ state: "visible" });
            await element.fill(text);
        }, objName, "type");
        this._log("info", `Typed password into element: ${objName || locator} (value not logged)`);
    }

    async selectOption(locator, value, objName = "") {
        await this.withHeal(locator, async (element) => {
            await element.waitFor({ state: "visible" });
            await element.selectOption(value);
        }, objName, "select");
        this._log("info", `Selected option "${value}" on element: ${objName || locator}`);
    }

    // ---------------------------------------------------------------------
    // Read / assertion methods — never routed through withHeal()
    // ---------------------------------------------------------------------

    async isVisible(locator) {
        return this.page.locator(locator).first().isVisible();
    }

    async textContent(locator) {
        return (await this.page.locator(locator).first().textContent() || "").trim();
    }

    async count(locator) {
        return this.page.locator(locator).count();
    }

    async allTextContents(locator) {
        return this.page.locator(locator).allTextContents();
    }

    async filterByText(locator, text) {
        return this.page.locator(locator).filter({ hasText: text });
    }
}

module.exports = PlaywrightWrapper;
