import { $, browser } from '@wdio/globals';
import Page from './page.js';
import Utils, { CONTEXT_REF } from '../helper/Utils.js';

const utils = new Utils();

/**
 * Selectors verified live — see AutomationSummary.md "Live Selector Verification".
 * `android.webkit.WebView` / `XCUIElementTypeWebView` are class-name selectors
 * (WebdriverIO auto-detects the "class name" strategy for bare class-name-shaped
 * strings on both platforms).
 */
class WebviewPage extends Page {
    get webView() {
        return this.isIOS() ? $('XCUIElementTypeWebView') : $('android.webkit.WebView');
    }

    async isWebViewDisplayed() {
        return this.isDisplayed(this.webView);
    }

    /**
     * Confirms a WEBVIEW_* context is available WITHOUT performing the actual
     * switch. See AutomationSummary.md for why the full context switch (which
     * needs a matching chromedriver for the emulator's System WebView) is
     * exercised as a best-effort, non-blocking check rather than a hard
     * assertion in this suite.
     */
    async webviewContextIsAvailable() {
        try {
            await utils.waitForWebViewContextLoaded(10000);
            return true;
        } catch (e) {
            return false;
        }
    }

    async switchToWebviewContext() {
        await utils.switchToContext(CONTEXT_REF.WEBVIEW);
        return browser.getContext();
    }

    async switchToNativeContext() {
        await utils.switchToContext(CONTEXT_REF.NATIVE);
    }

    async getCurrentContext() {
        return browser.getContext();
    }
}

export default new WebviewPage();
