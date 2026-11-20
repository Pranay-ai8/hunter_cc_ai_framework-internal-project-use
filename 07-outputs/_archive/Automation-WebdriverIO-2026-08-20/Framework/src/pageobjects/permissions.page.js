import { $, browser } from '@wdio/globals';
import Page from './page.js';

/**
 * Selectors verified live — see AutomationSummary.md "Live Selector Verification".
 *
 * Android-specific behavior: this suite runs with `appium:autoGrantPermissions=true`,
 * so all 4 permission switches are pre-granted before any test observes them, and
 * tapping an already-granted switch off does NOT revoke it in-place — Android
 * surfaces a native "Change in Settings" dialog instead (correct OS security
 * behavior, not a bug). `dismissChangeInSettingsDialogIfPresent()` cancels it
 * (staying in-app) so the permission remains granted.
 */
class PermissionsPage extends Page {
    get permissionsScreen() {
        return $('~Permissions-screen');
    }

    get cancelButton() {
        return $('id=android:id/button2');
    }

    switchFor(permission) {
        return $(`~permission-switch-${permission}`);
    }

    async isPermissionsScreenDisplayed() {
        return this.isDisplayed(this.permissionsScreen);
    }

    async isPermissionGranted(permission) {
        const toggle = this.switchFor(permission);
        await toggle.waitForDisplayed();
        return this.isSwitchOn(toggle);
    }

    /**
     * iOS has no autoGrantPermissions equivalent and no in-app "Change in
     * Settings" redirect — the first toggle instead surfaces a real system
     * permission alert entirely outside the app's own accessibility tree,
     * which `appium:autoAcceptAlerts` (set in wdio.conf.js) accepts
     * automatically without any app-level code needed here. The switch also
     * takes a beat (confirmed live: up to ~500ms) to reflect the grant after
     * that alert is accepted, so poll briefly rather than asserting immediately.
     */
    async togglePermission(permission) {
        const toggle = this.switchFor(permission);
        const before = await this.isSwitchOn(toggle);
        await this.clickWithHeal(toggle, `${permission} Permission Switch`);
        if (this.isAndroid()) {
            await this.dismissChangeInSettingsDialogIfPresent();
            return;
        }
        const deadline = Date.now() + 3000;
        while (Date.now() < deadline && (await this.isSwitchOn(this.switchFor(permission))) === before) {
            await browser.pause(150);
        }
    }

    async isChangeInSettingsDialogDisplayed() {
        return this.cancelButton.isExisting();
    }

    async dismissChangeInSettingsDialogIfPresent() {
        if (await this.isChangeInSettingsDialogDisplayed()) {
            await this.cancelButton.click();
        }
    }
}

export default new PermissionsPage();
