import { $, $$ } from '@wdio/globals';
import Page from './page.js';

/**
 * Selectors verified live — see AutomationSummary.md "Live Selector Verification".
 * The success alert is a native Android AlertDialog (app-themed) on Android, not
 * a shared app testID, hence the resource-id-based locators there. iOS has no
 * resource-id concept at all — its native UIAlertController exposes its title/
 * message/button via their own accessibility ids instead (confirmed live).
 */
class LoginPage extends Page {
    get loginScreen() {
        return $('~Login-screen');
    }

    get inputEmail() {
        return $('~input-email');
    }

    get inputPassword() {
        return $('~input-password');
    }

    get loginButton() {
        return $('~button-LOGIN');
    }

    get loginTabToggle() {
        return $('~button-login-container');
    }

    get signUpTabToggle() {
        return $('~button-sign-up-container');
    }

    get alertTitle() {
        return this.isIOS() ? $('~Success') : $('id=com.wdiodemoapp:id/alert_title');
    }

    get alertMessage() {
        return this.isIOS() ? $('~You are logged in!') : $('id=android:id/message');
    }

    get alertOkButton() {
        return this.isIOS() ? $('~OK') : $('id=android:id/button1');
    }

    async isLoginScreenDisplayed() {
        return this.isDisplayed(this.loginScreen);
    }

    async enterEmail(email) {
        // typeWithHeal()'s own waitForDisplayed() covers the race noted previously
        // (a setValue() issued immediately after switching tabs can race the
        // screen's own render/animation) in addition to self-healing on failure.
        await this.typeWithHeal(this.inputEmail, 'Email Input', email);
        await this.hideKeyboardSafely();
    }

    async enterPassword(password) {
        await this.typeWithHeal(this.inputPassword, 'Password Input', password);
        await this.hideKeyboardSafely();
    }

    /** Explicit empty-state precondition — needed because setValue()-based entry
     * steps from an earlier scenario in the same app session could otherwise
     * leave stale text in these fields (the screen is not remounted just by
     * re-selecting the Login tab). */
    async clearFields() {
        await this.inputEmail.clearValue();
        await this.inputPassword.clearValue();
    }

    async tapLoginButton() {
        await this.clickWithHeal(this.loginButton, 'Login Button');
    }

    async openSignUpTab() {
        await this.clickWithHeal(this.signUpTabToggle, 'Sign Up Tab');
    }

    async openLoginTab() {
        await this.clickWithHeal(this.loginTabToggle, 'Login Tab Toggle');
    }

    async isSuccessAlertDisplayed() {
        try {
            await this.alertTitle.waitForDisplayed();
            const title = await this.alertTitle.getText();
            const message = await this.alertMessage.getText();
            return title.toLowerCase() === 'success' && message.toLowerCase().includes('logged in');
        } catch (e) {
            return false;
        }
    }

    async dismissAlert() {
        if (await this.alertOkButton.isExisting()) {
            try {
                await this.clickWithHeal(this.alertOkButton, 'Alert OK Button');
            } catch (e) {
                // Already dismissed — nothing to do.
            }
        }
    }

    /**
     * Android exposes visible text via @text; iOS has no such attribute — the
     * equivalent lives in @label/@name/@value. Checking all four keeps this one
     * query correct on both platforms.
     */
    async isValidationMessageDisplayed(partialText) {
        const matches = await $$(
            `//*[contains(@text,"${partialText}") or contains(@label,"${partialText}") or contains(@name,"${partialText}") or contains(@value,"${partialText}")]`
        );
        return matches.length > 0;
    }

    generateRandomEmail() {
        return `qa.${Date.now()}@example.com`;
    }
}

export default new LoginPage();
