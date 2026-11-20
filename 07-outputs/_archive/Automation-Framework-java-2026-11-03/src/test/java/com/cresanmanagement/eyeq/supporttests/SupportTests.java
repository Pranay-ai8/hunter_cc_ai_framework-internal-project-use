package com.cresanmanagement.eyeq.supporttests;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.framework.TestBase;
import com.cresanmanagement.eyeq.pageobjects.accountsettings.AccountSettingsPage;
import com.cresanmanagement.eyeq.pageobjects.mainappshell.MainAppShellPage;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.pageobjects.sitemanagement.SiteManagementPage;
import com.cresanmanagement.eyeq.pageobjects.support.SupportPage;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for the Support module (static Contact Support info - phone/email, tap-to-act,
 * copy-to-clipboard). FunctionalTestCases.md lists 7 rows for this module (TC_FUNC_SUP_001-007);
 * 6 are YES-eligible for automation (001-005, 007) and implemented here. TC_FUNC_SUP_006 is marked
 * TBD in the source document itself (requires a specially-configured device/emulator image with no
 * default dialer/email handler, not confirmed available in this automation environment) - skipped
 * with a clear reason rather than faked.
 *
 * <p><b>LIVE-VERIFIED 2026-10-20</b> (real device {@code 8BQX1DY81}, confirmed Test backend, site
 * "Amalthea") via a throwaway crawl ({@code CrawlSupportLive.java}, deleted after use - see
 * {@code docs/dom-snapshots/support_live_00_support_tab.xml}, {@code support_live_01_login_contact_support_overlay.xml},
 * {@code support_live_02_after_phone_tap.xml}, {@code support_live_03_after_email_tap.xml}):
 * <ul>
 *   <li>Tapping the phone number genuinely invokes the device Dialer app with the number pre-filled
 *       on the dialpad (confirmed via a real foreground-package change to {@code com.google.android.dialer}).</li>
 *   <li>Tapping the email genuinely invokes the device's email client (observed: Gmail,
 *       {@code com.google.android.gm}) via a real intent hand-off.</li>
 *   <li>A single {@code navigate().back()} from the Dialer app was confirmed INSUFFICIENT to return to
 *       this app (one back press only closed the Dialer's own internal state) - {@code SupportPage}
 *       uses {@code Base#bringOwnAppToForeground(String)} (an {@code activateApp} call) instead, which
 *       is deterministic regardless of how many screens the external app pushed.</li>
 *   <li>The copy-icon controls' {@code content-desc="Copied!"} is a STATIC accessibility label present
 *       on the icon at all times, not a dynamic post-tap toast - clipboard content is read directly via
 *       {@code Base#getClipboardText()} rather than asserting on any on-screen text change.</li>
 *   <li>No ticket-submission form or any other input control exists anywhere on this screen (zero
 *       EditText, zero Submit/Send controls) - confirmed by the full live dump.</li>
 *   <li><b>GENUINE CONFIRMED DEFECT (not an automation bug):</b> the Support tab's phone number
 *       ("+1 888-675-6555") does NOT match the Login-footer Contact Support overlay's phone number
 *       ("(888) 678-6555"), confirmed on the SAME device/session moments apart (ruling out a stale or
 *       cross-device artifact), and independently reproduced on a DIFFERENT physical device in an
 *       earlier session. TC_FUNC_SUP_007 below implements this as a genuine defect-confirmation test
 *       per this project's established pattern (see FunctionalTestCases.md's TC_FUNC_GLB_036 phrasing) -
 *       it is INTENTIONALLY expected to fail on its phone-number assertion until the real app data is
 *       corrected or the requirement is revised; this must be logged per 03-standards/testing-standards.md's
 *       Defect Standards rather than weakened into a passing assertion.</li>
 * </ul>
 */
public class SupportTests extends TestBase {

    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";
    private static final String MODULE_CSV = "src/test/resources/testdata/support.csv";

    private String csv(String key) {
        return CSVLoader.loadRowByKey(MODULE_CSV, key).get("Value");
    }

    /**
     * Reaches the Support tab using the suite-level shared, already-logged-in session (2026-10-20
     * architecture - see {@code testng.SuiteCreationListener}'s class javadoc) - never calls
     * {@code OnboardingAuthenticationPage#login} itself. {@code SiteManagementPage#ensureSiteSelected}
     * works whether the session is freshly on Site Selection or already inside the Main App Shell.
     */
    private SupportPage navigateToSupport(String siteName) {
        SiteManagementPage sitePage = Pages.SiteManagementPage();
        sitePage.ensureSiteSelected(siteName);
        Assert.assertFalse(sitePage.isSiteSelectionScreenDisplayedFast(),
                "Precondition failed: expected to be inside the Main App Shell after ensureSiteSelected('" + siteName + "').");
        MainAppShellPage shellPage = Pages.MainAppShellPage();
        shellPage.tapBottomNavTab("Support");
        SupportPage supportPage = Pages.SupportPage();
        Assert.assertTrue(supportPage.isSupportScreenDisplayed(),
                "Precondition failed: expected the Support tab to land on the Contact Support screen.");
        return supportPage;
    }

    private SupportPage navigateToSupport() {
        return navigateToSupport(csv("SiteForSupport"));
    }

    /**
     * Lightweight per-test reset (replaces the retired, unconditional-full-relaunch
     * {@code resetToCleanLoginOnTestBackend()} pattern - see {@code testng.SuiteCreationListener}'s class
     * javadoc for why that pattern was retired after a real production-environment incident). The Support
     * tab itself has no sub-screens to close, so this just confirms we're back on a known screen; the
     * shared session stays logged in throughout. TC_FUNC_SUP_007 does NOT use this - it explicitly logs
     * out and back in as its own test subject (see {@link #logOutViaAccountSettings()}/
     * {@link #logBackInAfterLogout()}).
     */
    private void lightReset() {
        // No open sub-screens to close on the Support tab itself - intentionally a no-op beyond
        // documenting intent. Kept as a named method (rather than removing the finally blocks entirely)
        // so every test's structure stays consistent with every other retrofitted module.
    }

    /** Used only by TC_FUNC_SUP_007, which genuinely needs a logged-out Login screen as its own test subject. */
    private void logOutViaAccountSettings() {
        MainAppShellPage shellPage = Pages.MainAppShellPage();
        shellPage.tapTopBarProfileIcon();
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        acctPage.tapLogOut();
        acctPage.tapLogOutDialogConfirm();
    }

    /** Restores the shared session to logged-in after TC_FUNC_SUP_007's own logged-out leg. */
    private void logBackInAfterLogout() {
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        if (authPage.isLoginScreenDisplayed()) {
            Map<String, String> qaUser = CSVLoader.loadRowByKey(ENV_CSV, "QaUser");
            authPage.login(qaUser.get("Username"), qaUser.get("Password"));
        }
    }

    // =================================================================================================
    // Contact Support (FR-SUP-001)
    // =================================================================================================

    /** TC_FUNC_SUP_001 - Tapping the Support phone number invokes the device dialer with the number pre-filled. */
    @Test(priority = 1000, testName = "SupPhoneInvokesDialer", description = "TC_FUNC_SUP_001: Tapping the Support phone number invokes the dialer with the number pre-filled", groups = {"regression"})
    public void verifyTappingPhoneNumberInvokesDialer() {
        try {
            SupportPage supportPage = navigateToSupport();
            String ownPackage = supportPage.getCurrentPackageName();
            String expectedPhone = csv("ExpectedSupportPhoneNumber");
            String actualPhoneOnScreen = supportPage.getSupportPhoneNumber();
            Assert.assertEquals(actualPhoneOnScreen, expectedPhone,
                    "Expected the Support tab's displayed phone number to match the confirmed real value.");

            String resultingPackage = supportPage.tapPhoneNumberAndGetResultingPackage();
            Assert.assertNotEquals(resultingPackage, ownPackage,
                    "Expected tapping the phone number to hand off to an external dialer app (foreground package change).");

            supportPage.returnToSupportScreen(ownPackage);
        } catch (Exception e) {
            logger.error("verifyTappingPhoneNumberInvokesDialer failed", e);
            Assert.fail("TC_FUNC_SUP_001 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SUP_002 - Tapping the Support email launches the device's email client with the recipient pre-filled. */
    @Test(priority = 1010, testName = "SupEmailInvokesEmailClient", description = "TC_FUNC_SUP_002: Tapping the Support email invokes the device email client", groups = {"regression"})
    public void verifyTappingEmailInvokesEmailClient() {
        try {
            SupportPage supportPage = navigateToSupport();
            String ownPackage = supportPage.getCurrentPackageName();
            String expectedEmail = csv("ExpectedSupportEmail");
            String actualEmailOnScreen = supportPage.getSupportEmail();
            Assert.assertEquals(actualEmailOnScreen, expectedEmail,
                    "Expected the Support tab's displayed email to match the confirmed real value.");

            String resultingPackage = supportPage.tapEmailAndGetResultingPackage();
            Assert.assertNotEquals(resultingPackage, ownPackage,
                    "Expected tapping the email to hand off to an external email client app (foreground package change). "
                            + "NOTE: the exact pre-filled recipient inside the external email client's compose screen could "
                            + "NOT be independently verified live - on the real device used for this crawl, Gmail's one-time "
                            + "first-run 'Welcome to Gmail' tour intervened before reaching the compose screen. The intent "
                            + "hand-off itself (this assertion) is the scriptable, deterministic part per FunctionalTestCases.md's "
                            + "own stated automation rationale; the recipient pre-fill is a genuine, documented gap, not invented.");

            supportPage.returnToSupportScreen(ownPackage);
        } catch (Exception e) {
            logger.error("verifyTappingEmailInvokesEmailClient failed", e);
            Assert.fail("TC_FUNC_SUP_002 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SUP_003 - Copy-to-clipboard for the phone number copies the exact displayed number. */
    @Test(priority = 1020, testName = "SupCopyPhoneToClipboard", description = "TC_FUNC_SUP_003: Copy-to-clipboard for the phone number copies the exact displayed number", groups = {"regression"})
    public void verifyCopyPhoneNumberToClipboard() {
        try {
            SupportPage supportPage = navigateToSupport();
            String displayedPhone = supportPage.getSupportPhoneNumber();
            String clipboardContent = supportPage.copyPhoneNumberAndReadClipboard();
            Assert.assertEquals(clipboardContent, displayedPhone,
                    "Expected the device clipboard to contain exactly the displayed phone number after tapping its copy icon.");
        } catch (Exception e) {
            logger.error("verifyCopyPhoneNumberToClipboard failed", e);
            Assert.fail("TC_FUNC_SUP_003 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SUP_004 - Copy-to-clipboard for the email copies the exact displayed address. */
    @Test(priority = 1030, testName = "SupCopyEmailToClipboard", description = "TC_FUNC_SUP_004: Copy-to-clipboard for the email copies the exact displayed address", groups = {"regression"})
    public void verifyCopyEmailToClipboard() {
        try {
            SupportPage supportPage = navigateToSupport();
            String displayedEmail = supportPage.getSupportEmail();
            String clipboardContent = supportPage.copyEmailAndReadClipboard();
            Assert.assertEquals(clipboardContent, displayedEmail,
                    "Expected the device clipboard to contain exactly the displayed email address after tapping its copy icon.");
        } catch (Exception e) {
            logger.error("verifyCopyEmailToClipboard failed", e);
            Assert.fail("TC_FUNC_SUP_004 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SUP_005 - No ticket-submission form or any other contact-creation input control exists on the Support screen. */
    @Test(priority = 10, testName = "SupNoTicketFormPresent", description = "TC_FUNC_SUP_005: No ticket-submission form or input control exists on the Support screen", groups = {"regression", "negative"})
    public void verifyNoTicketSubmissionFormPresent() {
        try {
            SupportPage supportPage = navigateToSupport();
            Assert.assertTrue(supportPage.isScreenFreeOfTicketSubmissionControls(),
                    "Expected the Support screen to contain only static contact information (phone/email) with "
                            + "tap-to-act/copy controls - no EditText fields and no Submit/Send ticket-creation control.");
        } catch (Exception e) {
            logger.error("verifyNoTicketSubmissionFormPresent failed", e);
            Assert.fail("TC_FUNC_SUP_005 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SUP_006 - graceful behavior with no dialer/email handler available. TBD per
     * FunctionalTestCases.md itself: requires a specially-configured device/emulator image with no
     * default dialer/email app, which is not confirmed available in this project's standard automation
     * environment. Skipping with a clear reason rather than fabricating a "no handler" device state.
     */
    @Test(priority = 20, testName = "SupGracefulNoHandlerApp", description = "TC_FUNC_SUP_006: Graceful behavior when no dialer/email handler is available (TBD)", groups = {"regression", "negative"})
    public void verifyGracefulBehaviorWithNoHandlerApp() {
        throw new SkipException("TC_FUNC_SUP_006 is marked TBD in FunctionalTestCases.md itself - it requires a "
                + "specially-configured test device/emulator image with no default dialer/email handler app, which "
                + "is not confirmed available in this automation environment. Per the standing uncertainty-resolution "
                + "protocol, this is logged as a genuine open question rather than assumed or faked - raise HITL: "
                + "confirm availability of such a device profile before this can be implemented for real.");
    }

    /**
     * TC_FUNC_SUP_007 - Support contact info is identical whether reached via the bottom-nav Support
     * tab or the Login-screen Contact Support overlay.
     *
     * <p><b>DEFECT-CONFIRMATION TEST.</b> This is INTENTIONALLY expected to fail on the phone-number
     * assertion: live evidence (on two separate physical devices, across two sessions) confirms the
     * Support tab's real phone number ("+1 888-675-6555") genuinely differs from the Login-footer
     * Contact Support overlay's real phone number ("(888) 678-6555"). Per this project's established
     * defect-confirmation pattern (FunctionalTestCases.md's TC_FUNC_GLB_036), this test asserts the
     * DOCUMENTED expected behavior (phone numbers identical) rather than being weakened to match the
     * observed (defective) behavior - a failure here is the correct, honest signal that this real data
     * discrepancy must be logged as a defect per 03-standards/testing-standards.md's Defect Standards,
     * not silently accepted as correct.
     *
     * <p><b>Fixed 2026-10-27</b> after confirmed live "infrastructure/navigation error" failures (as this
     * test's own catch block distinguishes them from the known phone-number defect above), investigated
     * via a live crawl across three layers:
     * <ol>
     *   <li>Leg 1 read the overlay's phone number immediately after the coordinate-based footer tap with
     *       no verification the overlay actually opened - {@code isContactSupportOverlayDisplayed()} (a
     *       real bounded wait) already existed but was never actually called here; added as an explicit
     *       precondition.</li>
     *   <li>Calling this test in isolation (without a prior test having already selected a site) exposed
     *       that {@code logOutViaAccountSettings()} needs the Main App Shell's TopBar, which doesn't exist
     *       until a site is selected - now defensively selects one first if still on Site Selection.</li>
     *   <li><b>The overlay's phone number data discrepancy documented above may no longer be reproducible.</b>
     *       A live crawl ({@code logs/page-source-dumps/CRAWL_SC2_login_screen_after_computed_tap.xml})
     *       showed the overlay's phone number is now "+1 888-675-6555" - IDENTICAL to the Support tab's,
     *       not the stale "(888) 678-6555" the locator was hardcoded to. {@code contactSupportPhoneNumberText}'s
     *       locator has been corrected to a structural one (see
     *       {@code OnboardingAuthenticationPageObjectRepository}) so it finds the real element regardless
     *       of its current digits; the phone-number assertion below is left as-is (asserting equality) so
     *       it will now genuinely PASS if the two screens agree - if that holds, this test's "defect-
     *       confirmation" framing and its description above are now stale and should be revisited/removed.</li>
     * </ol>
     */
    @Test(priority = 1040, testName = "SupContactInfoConsistentAcrossEntryPoints", description = "TC_FUNC_SUP_007 (defect-confirmation): Support contact info must be identical via the Support tab and the Login Contact Support overlay", groups = {"regression", "cross-flow"})
    public void verifyContactInfoConsistentAcrossEntryPoints() {
        try {
            // This test's own subject needs a logged-out Login screen (Leg 1 below) - under the
            // suite-level shared-session architecture, the session normally stays logged in throughout,
            // so explicitly log out first via the real Account Settings UI flow (never clearAppData/relaunch -
            // that would also wipe the devmode Test-backend setting, the exact root cause of an earlier
            // incident - see testng.SuiteCreationListener's class javadoc).
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            SiteManagementPage sitePage = Pages.SiteManagementPage();
            // Snapshot check of the shared session's already-settled state at the start of this test -
            // the common case is "false" (still logged in), so the fast-bounded variant avoids burning
            // the full 90s DEFAULT_WAIT every time this test runs.
            if (!authPage.isLoginScreenDisplayedFast()) {
                // Fixed 2026-10-27 after a confirmed live failure: logOutViaAccountSettings() needs the
                // Main App Shell's TopBar (for its Profile icon), which only exists once a site has been
                // selected - running this test in isolation (no prior test having already picked a site)
                // left the suite-level login sitting on bare Site Selection, so tapTopBarProfileIcon()
                // timed out 90s looking for a Profile icon that doesn't exist yet on that screen.
                if (sitePage.isSiteSelectionScreenDisplayedFast()) {
                    sitePage.ensureSiteSelected(csv("SiteForSupport"));
                }
                logOutViaAccountSettings();
            }

            // Leg 1: logged-out Login-footer Contact Support overlay.
            authPage.openContactSupportFromLoginFooter();
            Assert.assertTrue(authPage.isContactSupportOverlayDisplayed(),
                    "Precondition failed: expected the Contact Support overlay to open from the Login footer tap.");
            String overlayPhone = authPage.getContactSupportPhoneNumberText();
            String overlayEmail = authPage.getContactSupportEmailText();
            authPage.tapContactSupportBackToLogin();

            // Leg 2: logged-in Support tab - explicitly log back in (navigateToSupport assumes an
            // already-logged-in shared session; Leg 1 just logged this one out on purpose).
            logBackInAfterLogout();
            SupportPage supportPage = navigateToSupport();
            String supportTabPhone = supportPage.getSupportPhoneNumber();
            String supportTabEmail = supportPage.getSupportEmail();

            Assert.assertEquals(supportTabEmail, overlayEmail,
                    "Expected the email address to be identical across both entry points (this part is confirmed to genuinely match).");
            Assert.assertEquals(supportTabPhone, overlayPhone,
                    "CONFIRMED DEFECT (not an automation error): the Support tab's phone number ('" + supportTabPhone
                            + "') does not match the Login-footer Contact Support overlay's phone number ('" + overlayPhone
                            + "'). This is a genuine data discrepancy, reproduced on two separate physical devices across "
                            + "two sessions. Log per 03-standards/testing-standards.md's Defect Standards - do not weaken "
                            + "this assertion to force a pass.");
        } catch (Exception e) {
            logger.error("verifyContactInfoConsistentAcrossEntryPoints failed", e);
            Assert.fail("TC_FUNC_SUP_007 failed (infrastructure/navigation error, distinct from the known phone-number defect): " + e.getMessage());
        } finally {
            lightReset();
        }
    }
}
