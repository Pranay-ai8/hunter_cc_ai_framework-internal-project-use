import { $ } from '@wdio/globals';
import Page from './page.js';

/**
 * Selectors verified live against a real Appium session (UiAutomator2, Android
 * emulator Pixel_6_API_33, app com.wdiodemoapp) before being written here —
 * see AutomationSummary.md "Live Selector Verification".
 */
class HomePage extends Page {
    get homeScreen() {
        return $('~Home-screen');
    }

    // Android exposes visible text via @text; there is no separate testID for the
    // intro branding/description, so we match on the rendered copy itself.
    get introBranding() {
        return $('//*[@text="WEBDRIVER"]');
    }

    get introDescription() {
        return $('//*[contains(@text,"Demo app for the appium-boilerplate")]');
    }

    async isHomeScreenDisplayed() {
        return this.isDisplayed(this.homeScreen);
    }

    async isIntroductoryContentDisplayed() {
        return (await this.introBranding.isExisting()) && (await this.introDescription.isExisting());
    }
}

export default new HomePage();
