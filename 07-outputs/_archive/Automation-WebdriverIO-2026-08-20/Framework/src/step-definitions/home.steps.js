import { Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import HomePage from '../pageobjects/home.page.js';

Then(/^the Home screen should be displayed$/, async () => {
    await expect(HomePage.homeScreen).toBeDisplayed();
});

Then(/^the Home screen should display the WEBDRIVER introductory content$/, async () => {
    const displayed = await HomePage.isIntroductoryContentDisplayed();
    expect(displayed).toBe(true);
});
