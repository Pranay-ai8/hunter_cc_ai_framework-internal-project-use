package com.codecraft.wdiodemoapp.permissionstests;

import com.codecraft.wdiodemoapp.framework.TestBase;
import com.codecraft.wdiodemoapp.Pages;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Permission handling is genuinely different per platform (verified live on both):
 * <p>
 * <b>Android</b> — this suite runs with {@code appium:autoGrantPermissions=true}, so all 4
 * permission switches are pre-granted before any test observes them, and tapping a granted
 * switch off does NOT revoke it in-place — Android surfaces a native "Change in Settings" dialog
 * instead, correctly refusing in-app revocation of a dangerous permission.
 * <p>
 * <b>iOS</b> — there is no autoGrantPermissions equivalent; every switch starts OFF, and the
 * first tap triggers a real system permission alert entirely outside the app's own accessibility
 * tree (confirmed live via screenshot), auto-accepted by {@code appium:autoAcceptAlerts=true} (set
 * in {@code InitiateDriver}), which then flips the switch to granted.
 * <p>
 * Rather than pretend these are the same behavior, each test below asserts what's actually true
 * on the platform it runs against via {@code isAndroid()}/{@code isIOS()}.
 */
public class PermissionsTests extends TestBase {

    @Test(description = "TC-PERM-001/002 (adjusted, cross-platform): Camera permission switch reflects the "
            + "platform-correct default state — granted on Android (autoGrantPermissions), not yet granted on "
            + "iOS (no such capability exists there)", groups = {"smoke", "regression"})
    public void verifyCameraPermissionDefaultState() {
        Pages.NavigationPage().goToPermissionsViaSideMenu();
        Assert.assertTrue(Pages.PermissionsPage().isPermissionsScreenDisplayed());
        assertDefaultPermissionState("camera");
    }

    @Test(description = "TC-PERM-004 (adjusted, cross-platform): Microphone permission switch reflects the "
            + "platform-correct default state", groups = {"regression"})
    public void verifyMicrophonePermissionDefaultState() {
        Pages.NavigationPage().goToPermissionsViaSideMenu();
        assertDefaultPermissionState("microphone");
    }

    @Test(description = "TC-PERM-005 (adjusted, cross-platform): Location permission switch reflects the "
            + "platform-correct default state", groups = {"regression"})
    public void verifyLocationPermissionDefaultState() {
        Pages.NavigationPage().goToPermissionsViaSideMenu();
        assertDefaultPermissionState("location");
    }

    @Test(description = "TC-PERM-006 (adjusted, cross-platform): Photo library permission switch reflects the "
            + "platform-correct default state", groups = {"regression"})
    public void verifyPhotoLibraryPermissionDefaultState() {
        Pages.NavigationPage().goToPermissionsViaSideMenu();
        assertDefaultPermissionState("photos");
    }

    private void assertDefaultPermissionState(String permission) {
        boolean granted = Pages.PermissionsPage().isPermissionGranted(permission);
        if (Pages.PermissionsPage().isAndroid()) {
            Assert.assertTrue(granted, permission + " should already be granted under autoGrantPermissions");
        } else {
            Assert.assertFalse(granted, permission + " should not be granted yet — iOS has no "
                    + "autoGrantPermissions equivalent, so it starts undecided until the system alert is accepted");
        }
    }

    @Test(description = "TC-PERM-003 (corrected via live execution, cross-platform): Toggling a permission "
            + "switch behaves according to each platform's real OS security model rather than a naive "
            + "assumption that it always flips. Android: tapping an already-granted switch is BLOCKED "
            + "in-app (a 'Change in Settings' dialog appears instead, and the permission stays granted once "
            + "cancelled). iOS: the first tap grants it via a real system alert, auto-accepted.",
            groups = {"regression"})
    public void verifyTogglingPermissionMatchesPlatformSecurityModel() {
        Pages.NavigationPage().goToPermissionsViaSideMenu();
        boolean before = Pages.PermissionsPage().isPermissionGranted("camera");

        Pages.PermissionsPage().togglePermission("camera");

        boolean after = Pages.PermissionsPage().isPermissionGranted("camera");
        if (Pages.PermissionsPage().isAndroid()) {
            Assert.assertTrue(before, "Camera permission should already be granted under autoGrantPermissions");
            Assert.assertEquals(after, before,
                    "Permission should remain granted — Android blocks in-app revocation of an "
                            + "already-granted dangerous permission and requires system Settings instead");
        } else {
            Assert.assertFalse(before, "Camera permission should start ungranted on iOS");
            Assert.assertTrue(after,
                    "Permission should become granted after the system alert is auto-accepted");
        }
    }
}
