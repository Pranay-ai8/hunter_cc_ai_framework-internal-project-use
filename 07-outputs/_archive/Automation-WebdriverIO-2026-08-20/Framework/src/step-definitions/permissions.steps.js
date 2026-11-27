import { Given, When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import PermissionsPage from '../pageobjects/permissions.page.js';

// Live-run finding: Android pre-grants every permission via
// appium:autoGrantPermissions, but iOS has no such capability and genuinely
// starts every permission ungranted — this is a real platform security-model
// difference, not a bug, so the expected default is platform-aware even though
// the Gherkin wording ("granted by default") stays the same on both platforms.
Then(/^the "([^"]*)" permission should be granted by default$/, async (permission) => {
    const granted = await PermissionsPage.isPermissionGranted(permission);
    expect(granted).toBe(PermissionsPage.isAndroid());
});

When(/^I toggle the "([^"]*)" permission switch$/, async (permission) => {
    await PermissionsPage.togglePermission(permission);
});

// The following two steps back the "Toggling an already-granted permission is
// blocked in-app on Android" @regression scenario, which is Android-only by
// its own scenario title/premise (Android pre-grants; toggling an already-
// granted switch surfaces an in-app "Change in Settings" redirect rather than
// revoking it). iOS starts ungranted, so this scenario's precondition doesn't
// hold there — it needs an iOS-specific scenario of its own, not a blanket
// platform-aware assertion here, since "toggle then still granted" isn't even
// the right shape for iOS's first-toggle-grants behavior.
Given(/^the "([^"]*)" permission is already granted$/, async (permission) => {
    const granted = await PermissionsPage.isPermissionGranted(permission);
    expect(granted).toBe(true);
});

Then(/^the "([^"]*)" permission should remain granted$/, async (permission) => {
    const granted = await PermissionsPage.isPermissionGranted(permission);
    expect(granted).toBe(true);
});
