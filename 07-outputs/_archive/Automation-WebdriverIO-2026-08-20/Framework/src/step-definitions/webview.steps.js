import { Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import WebviewPage from '../pageobjects/webview.page.js';

Then(/^a WEBVIEW context should become available$/, async () => {
    const available = await WebviewPage.webviewContextIsAvailable();
    expect(available).toBe(true);
});
