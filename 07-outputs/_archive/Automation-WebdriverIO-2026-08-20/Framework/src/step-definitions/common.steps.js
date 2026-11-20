import { Given } from '@wdio/cucumber-framework';
import NavigationPage from '../pageobjects/navigation.page.js';

/**
 * Shared across every feature file. Rather than a no-op marker step, this
 * waits for the bottom tab bar (present on every screen) to confirm the app
 * has actually finished launching/rendering — this also makes it a safe
 * Background step regardless of which screen a previous scenario in the same
 * app session left the app on, since the tab bar itself is what we check for.
 */
Given(/^the app is launched$/, async () => {
    // Live-run finding: a prior Scenario in the same session (see
    // NavigationPage.closeSideMenuIfOpen()'s javadoc) can leave the side-menu
    // drawer open, which hides the bottom tab bar entirely — close it first so
    // the ~Menu check below isn't racing a drawer that can never close itself.
    await NavigationPage.closeSideMenuIfOpen();

    // No explicit timeout override here — this relies on wdio.conf.js's
    // waitforTimeout (see that file for why it's raised well above a normal
    // default: this environment intermittently shows real Android-level ANRs
    // under host resource contention, which transiently blocks the app's own
    // UI with a system dialog).
    await NavigationPage.tabMenu.waitForExist();
});
