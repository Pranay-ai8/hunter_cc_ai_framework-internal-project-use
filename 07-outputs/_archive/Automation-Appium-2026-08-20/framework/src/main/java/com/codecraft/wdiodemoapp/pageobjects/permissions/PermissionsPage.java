package com.codecraft.wdiodemoapp.pageobjects.permissions;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PermissionsPage extends Base {

    private final PermissionsPageObjectRepository repo;

    public PermissionsPage() {
        repo = new PermissionsPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isPermissionsScreenDisplayed() {
        waitUntilElementIsVisible(repo.permissionsScreen);
        return isElementVisible(repo.permissionsScreen);
    }

    private WebElement switchFor(String permission) {
        return switch (permission.toLowerCase()) {
            case "camera" -> repo.cameraSwitch;
            case "microphone" -> repo.microphoneSwitch;
            case "location" -> repo.locationSwitch;
            case "photos" -> repo.photosSwitch;
            default -> throw new IllegalArgumentException("Unknown permission: " + permission);
        };
    }

    public boolean isPermissionGranted(String permission) {
        WebElement toggle = switchFor(permission);
        waitUntilElementIsVisible(toggle);
        return isSwitchOn(toggle);
    }

    /**
     * Live-run finding: Android and iOS handle an already-decided permission completely
     * differently, so this branches on platform rather than sharing one code path.
     * <p>
     * Android (this suite runs with autoGrantPermissions=true, so the permission is already
     * granted before the test starts): tapping the switch off does NOT revoke it in-place — a
     * native "Change in Settings" dialog appears, directing the user to system Settings instead.
     * This is correct OS security behavior, not a bug; {@link #dismissChangeInSettingsDialogIfPresent()}
     * cancels it (staying in-app) so the permission remains granted.
     * <p>
     * iOS has no autoGrantPermissions equivalent and no in-app "Change in Settings" redirect —
     * the first toggle instead surfaces a real system permission alert entirely outside the
     * app's own accessibility tree (confirmed live via screenshot), which {@code autoAcceptAlerts}
     * (set in {@code InitiateDriver}) accepts automatically without any app-level code needed here.
     */
    public void togglePermission(String permission) {
        WebElement toggle = switchFor(permission);
        boolean before = isSwitchOn(toggle);
        clickWithHeal(toggle, capitalize(permission) + " Permission Switch");
        if (isAndroid()) {
            dismissChangeInSettingsDialogIfPresent();
        } else {
            // Live-run finding: after autoAcceptAlerts accepts the system permission alert, the
            // switch takes a beat (confirmed live: up to ~500ms) to reflect the grant — checking
            // immediately after the click reads the pre-grant state. Poll briefly rather than
            // asserting a fixed sleep, since the real delay will vary by device/load.
            long deadline = System.currentTimeMillis() + 3_000;
            while (System.currentTimeMillis() < deadline && isSwitchOn(switchFor(permission)) == before) {
                sleep(150);
            }
        }
    }

    private static String capitalize(String value) {
        return value.isEmpty() ? value : Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /** Android-only — see {@link #togglePermission(String)}. */
    public boolean isChangeInSettingsDialogDisplayed() {
        List<WebElement> cancelButton = driver.findElements(By.id("android:id/button2"));
        return !cancelButton.isEmpty() && isElementVisible(cancelButton.get(0));
    }

    private void dismissChangeInSettingsDialogIfPresent() {
        if (isChangeInSettingsDialogDisplayed()) {
            driver.findElement(By.id("android:id/button2")).click(); // CANCEL — stay in-app
        }
    }
}
