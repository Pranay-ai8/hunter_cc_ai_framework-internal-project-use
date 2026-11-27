import { $, browser } from '@wdio/globals';
import Page from './page.js';

/**
 * Bottom tab bar + side (hamburger) menu. Selectors verified live — see
 * AutomationSummary.md "Live Selector Verification".
 */
class NavigationPage extends Page {
    get tabHome() {
        return $('~Home');
    }

    get tabWebview() {
        return $('~Webview');
    }

    get tabLogin() {
        return $('~Login');
    }

    get tabForms() {
        return $('~Forms');
    }

    get tabSwipe() {
        return $('~Swipe');
    }

    get tabDrag() {
        return $('~Drag');
    }

    get tabMenu() {
        return $('~Menu');
    }

    get sideMenuPanel() {
        return $('~tab-side-menu-panel');
    }

    get sideMenuItemPermissions() {
        return $('~side-menu-item-permissions');
    }

    get sideMenuItemDataManagement() {
        return $('~side-menu-item-data-management');
    }

    sideMenuItem(module) {
        return $(`~side-menu-item-${module}`);
    }

    async goToHomeTab() {
        await this.clickWithHeal(this.tabHome, 'Home Tab');
    }

    async goToWebviewTab() {
        await this.clickWithHeal(this.tabWebview, 'Webview Tab');
    }

    async goToLoginTab() {
        await this.clickWithHeal(this.tabLogin, 'Login Tab');
    }

    async goToFormsTab() {
        await this.clickWithHeal(this.tabForms, 'Forms Tab');
    }

    async goToSwipeTab() {
        await this.clickWithHeal(this.tabSwipe, 'Swipe Tab');
    }

    async goToDragTab() {
        await this.clickWithHeal(this.tabDrag, 'Drag Tab');
    }

    async openMenu() {
        await this.clickWithHeal(this.tabMenu, 'Menu Tab');
        await this.sideMenuPanel.waitForDisplayed();
    }

    async isSideMenuDisplayed() {
        return this.sideMenuPanel.isDisplayed();
    }

    async goToPermissionsViaSideMenu() {
        await this.openMenu();
        await this.clickWithHeal(this.sideMenuItemPermissions, 'Permissions Side Menu Item');
    }

    async isSideMenuItemVisible(module) {
        return this.sideMenuItem(module).isDisplayed();
    }

    /**
     * Live-run finding: `appium:forceAppLaunch`/`appium:shouldTerminateApp` only
     * relaunch the app once per Appium session (once per spec/.feature file, not
     * once per Scenario). Within a single session, a scenario that opens the side
     * menu and never closes it (e.g. "Side menu opens from the Menu tab") leaves
     * it open for every subsequent scenario in the same file — confirmed live:
     * the bottom tab bar (`~Menu`) genuinely doesn't exist in the hierarchy while
     * the drawer overlays it, so the shared "app is launched" precondition can
     * never succeed at any timeout until the drawer is actually closed. On
     * Android, the hardware back button reliably closes it (confirmed live via
     * `mobile: pressKey` keycode 4 — `~Menu` exists again immediately after).
     * No iOS equivalent implemented yet — iOS has no hardware back button, and
     * the correct close gesture (e.g. tapping the dimmed scrim outside the
     * drawer) hasn't been live-verified; this is a known gap for iOS.
     */
    async closeSideMenuIfOpen() {
        const isOpen = await this.sideMenuPanel.isExisting().catch(() => false);
        if (!isOpen) {
            return;
        }
        if (this.isAndroid()) {
            await browser.execute('mobile: pressKey', { keycode: 4 });
        }
    }
}

export default new NavigationPage();
