import { $ } from '@wdio/globals';
import Page from './page.js';

/**
 * Selectors verified live — see AutomationSummary.md "Live Selector Verification".
 *
 * Live-run finding (carried over from this app's Appium-Java sibling framework,
 * re-confirmed here): a plain `id=` lookup on Android auto-prefixes the current
 * app package when the resource-id has no colon. This dropdown's underlying
 * value node has resource-id="text_input" with NO package prefix, so `id=` would
 * search for "com.wdiodemoapp:id/text_input" and never match — an xpath on the
 * literal resource-id is used instead.
 */
class FormsPage extends Page {
    get formsScreen() {
        return $('~Forms-screen');
    }

    get textInput() {
        return $('~text-input');
    }

    get textInputResult() {
        return $('~input-text-result');
    }

    get switchControl() {
        return $('~switch');
    }

    get switchText() {
        return $('~switch-text');
    }

    get dropdown() {
        return $('~Dropdown');
    }

    get dropdownValue() {
        return $('//*[@resource-id="text_input"]');
    }

    get buttonActive() {
        return $('~button-Active');
    }

    get buttonInactive() {
        return $('~button-Inactive');
    }

    get dialogOkButton() {
        return $('id=android:id/button1');
    }

    async isFormsScreenDisplayed() {
        return this.isDisplayed(this.formsScreen);
    }

    async typeText(text) {
        await this.typeWithHeal(this.textInput, 'Text Input', text);
        await this.hideKeyboardSafely();
    }

    async getTypedResult() {
        await this.textInputResult.waitForDisplayed();
        return this.textInputResult.getText();
    }

    /**
     * Live-run finding: this method previously re-implemented the check inline
     * with a hardcoded Android-only `checked` attribute — same method name as
     * the base `Page.isSwitchOn(element)` but zero-arity, which in JS silently
     * shadows the base class's platform-aware version entirely rather than
     * overloading it. Delegating via `super` restores iOS support (`value`
     * attribute) for this convenience wrapper. Never exercised on iOS before
     * (this scenario is `@regression`, and only `@smoke` had been run on iOS).
     */
    async isSwitchOn() {
        return super.isSwitchOn(this.switchControl);
    }

    async toggleSwitch() {
        await this.clickWithHeal(this.switchControl, 'Switch Toggle');
    }

    async openDropdown() {
        await this.clickWithHeal(this.dropdown, 'Dropdown');
    }

    /**
     * Android renders this dropdown as a tappable AlertDialog-style option list.
     *
     * Live-run finding: `waitForClickable()` throws "only available for desktop
     * and mobile browsers" on a native-app element — it's a browser-only
     * WebdriverIO command, not applicable here regardless of platform. Use
     * `waitForDisplayed()` (as every other native interaction in this project
     * does) instead.
     */
    async selectDropdownOption(optionText) {
        await this.openDropdown();
        const option = await $(`//*[@text="${optionText}"]`);
        await option.waitForDisplayed();
        await option.click();
    }

    async getDropdownValue() {
        return this.dropdownValue.getText();
    }

    /**
     * Live-run finding (also confirmed in the Java sibling framework): tapping
     * "Active" surfaces a native confirmation dialog ("This button is active" —
     * ASK ME LATER / CANCEL / OK); tapping "Inactive" does not. Dismiss
     * defensively either way so callers never need to know which button does this.
     */
    async tapActiveButton() {
        await this.clickWithHeal(this.buttonActive, 'Active Button');
        await this.dismissButtonStateDialogIfPresent();
    }

    async tapInactiveButton() {
        await this.clickWithHeal(this.buttonInactive, 'Inactive Button');
        await this.dismissButtonStateDialogIfPresent();
    }

    async dismissButtonStateDialogIfPresent() {
        if (await this.dialogOkButton.isExisting()) {
            try {
                await this.dialogOkButton.click();
            } catch (e) {
                // Already dismissed — nothing to do.
            }
        }
    }
}

export default new FormsPage();
