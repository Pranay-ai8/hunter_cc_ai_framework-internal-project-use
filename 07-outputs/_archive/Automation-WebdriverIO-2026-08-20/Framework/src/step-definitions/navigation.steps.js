import { When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import NavigationPage from '../pageobjects/navigation.page.js';
import WebviewPage from '../pageobjects/webview.page.js';
import LoginPage from '../pageobjects/login.page.js';
import FormsPage from '../pageobjects/forms.page.js';
import SwipePage from '../pageobjects/swipe.page.js';
import DragPage from '../pageobjects/drag.page.js';
import PermissionsPage from '../pageobjects/permissions.page.js';

When(/^I open the side menu$/, async () => {
    await NavigationPage.openMenu();
});

Then(/^the side menu should be displayed$/, async () => {
    const displayed = await NavigationPage.isSideMenuDisplayed();
    expect(displayed).toBe(true);
});

Then(/^the side menu should list the "([^"]*)" item$/, async (module) => {
    const visible = await NavigationPage.isSideMenuItemVisible(module);
    expect(visible).toBe(true);
});

When(/^I navigate to the Webview tab$/, async () => {
    await NavigationPage.goToWebviewTab();
});

Then(/^the Webview screen should be displayed$/, async () => {
    const displayed = await WebviewPage.isWebViewDisplayed();
    expect(displayed).toBe(true);
});

When(/^I navigate to the Login tab$/, async () => {
    await NavigationPage.goToLoginTab();
});

Then(/^the Login screen should be displayed$/, async () => {
    const displayed = await LoginPage.isLoginScreenDisplayed();
    expect(displayed).toBe(true);
});

When(/^I navigate to the Forms tab$/, async () => {
    await NavigationPage.goToFormsTab();
});

Then(/^the Forms screen should be displayed$/, async () => {
    const displayed = await FormsPage.isFormsScreenDisplayed();
    expect(displayed).toBe(true);
});

Then(/^the Forms screen should remain displayed$/, async () => {
    const displayed = await FormsPage.isFormsScreenDisplayed();
    expect(displayed).toBe(true);
});

When(/^I navigate to the Swipe tab$/, async () => {
    await NavigationPage.goToSwipeTab();
});

Then(/^the Swipe screen should be displayed$/, async () => {
    const displayed = await SwipePage.isSwipeScreenDisplayed();
    expect(displayed).toBe(true);
});

Then(/^the Swipe screen should remain displayed$/, async () => {
    const displayed = await SwipePage.isSwipeScreenDisplayed();
    expect(displayed).toBe(true);
});

When(/^I navigate to the Drag tab$/, async () => {
    await NavigationPage.goToDragTab();
});

Then(/^the Drag screen should be displayed$/, async () => {
    const displayed = await DragPage.isDragScreenDisplayed();
    expect(displayed).toBe(true);
});

When(/^I navigate to the Permissions screen via the side menu$/, async () => {
    await NavigationPage.goToPermissionsViaSideMenu();
});

Then(/^the Permissions screen should be displayed$/, async () => {
    const displayed = await PermissionsPage.isPermissionsScreenDisplayed();
    expect(displayed).toBe(true);
});
