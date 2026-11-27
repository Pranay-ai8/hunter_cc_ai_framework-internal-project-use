import { browser } from '@wdio/globals';

export const CONTEXT_REF = {
    NATIVE: 'native',
    WEBVIEW: 'webview',
};

export default class Utils {
    /**
     * Returns the list of currently available Appium contexts
     * (e.g. ['NATIVE_APP', 'WEBVIEW_com.wdiodemoapp']).
     */
    async getCurrentContexts() {
        return browser.getContexts();
    }

    /**
     * Waits (polling) until a WEBVIEW_* context is registered by the driver.
     * Live-run finding on this app: the WEBVIEW context registers essentially
     * immediately after navigating to the Webview tab (well under 1s observed),
     * but we still poll rather than check once, since the underlying discovery
     * is timing-dependent per Appium/driver docs.
     */
    async waitForWebViewContextLoaded(timeout = 10000) {
        return browser.waitUntil(
            async () => {
                const contexts = await this.getCurrentContexts();
                return contexts.some((context) => String(context).toUpperCase().startsWith('WEBVIEW'));
            },
            {
                timeout,
                timeoutMsg: 'A WEBVIEW_* context did not become available in time',
                interval: 250,
            },
        );
    }

    /**
     * Switches the active Appium context to native or webview.
     * @param {string} contextRef one of CONTEXT_REF.NATIVE / CONTEXT_REF.WEBVIEW
     */
    async switchToContext(contextRef) {
        const contexts = await this.getCurrentContexts();
        if (contextRef === CONTEXT_REF.NATIVE) {
            const nativeContext = contexts.find((context) => String(context) === 'NATIVE_APP') || contexts[0];
            await browser.switchContext(nativeContext);
            return;
        }
        const webviewContext = contexts.find((context) => String(context).toUpperCase().startsWith('WEBVIEW'));
        if (!webviewContext) {
            throw new Error('No WEBVIEW context available');
        }
        await browser.switchContext(webviewContext);
    }
}
