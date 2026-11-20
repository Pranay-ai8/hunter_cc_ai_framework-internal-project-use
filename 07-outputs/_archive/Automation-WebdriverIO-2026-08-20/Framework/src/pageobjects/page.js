import { browser } from '@wdio/globals';
import { findHealedLocator } from '../helper/SelfHealer.js';

/**
 * Base page object. Holds behavior generic across every screen — not
 * WebdriverIO API wrappers for their own sake, only what real page objects in
 * this project were found duplicating verbatim.
 */
export default class Page {
    /** Wait for an element to be displayed, then return whether it is. */
    async isDisplayed(element) {
        await element.waitForDisplayed();
        return element.isDisplayed();
    }

    isAndroid() {
        return browser.capabilities.platformName?.toLowerCase() === 'android';
    }

    isIOS() {
        return browser.capabilities.platformName?.toLowerCase() === 'ios';
    }

    /**
     * Android exposes a switch/toggle's on-state via the `checked` attribute;
     * XCUITest has no such attribute — iOS switches expose it via `value`
     * ("1"/"0"). See AutomationSummary.md "Live Selector Verification".
     */
    async isSwitchOn(element) {
        if (this.isIOS()) {
            return (await element.getAttribute('value')) === '1';
        }
        return (await element.getAttribute('checked')) === 'true';
    }

    /**
     * Some emulator/keyboard combinations throw if the keyboard isn't
     * currently shown — treat that as a no-op rather than a real failure.
     */
    async hideKeyboardSafely() {
        try {
            await browser.hideKeyboard();
        } catch (e) {
            // No-op if the keyboard was already hidden/never shown.
        }
    }

    /**
     * Self-healing click — ported from the Appium-Java sibling framework's
     * `Base.clickWithHeal()` (see `Utils/SelfHealer.java`'s javadoc for the full
     * rationale). Waits and clicks normally; on failure, hands `hint` (a
     * human-readable name, e.g. "Login Button") to `SelfHealer.findHealedLocator`
     * and retries against the healed element. Rethrows the original error
     * unchanged if healing fails, so a genuine failure is never silently lost.
     *
     * Uses `waitForDisplayed()`, not `waitForClickable()` — the latter throws
     * "only available for desktop and mobile browsers" on a native element,
     * a confirmed live bug elsewhere in this project (see AutomationSummary.md).
     */
    async clickWithHeal(element, hint) {
        try {
            await element.waitForDisplayed();
            await element.click();
        } catch (originalError) {
            const healed = await this.healOrRethrow(hint, originalError);
            await healed.click();
        }
    }

    /**
     * Self-healing type-into-field — see `clickWithHeal` above. `setValue()`
     * already clears the field before typing (confirmed working as-is on both
     * platforms in this project), so this does not add Java sibling's extra
     * explicit click()/clearValue() steps — those solved an iOS SecureTextField
     * focus issue specific to that stack's client library, not one observed here.
     */
    async typeWithHeal(element, hint, text) {
        try {
            await element.waitForDisplayed();
            await element.setValue(text);
        } catch (originalError) {
            const healed = await this.healOrRethrow(hint, originalError);
            await healed.setValue(text);
        }
    }

    async healOrRethrow(hint, originalError) {
        const healed = await findHealedLocator(hint, originalError);
        if (!healed.success) {
            throw originalError;
        }
        console.info(`Self-healed [${hint}] with confidence ${healed.confidence}`);
        return healed.element;
    }
}
