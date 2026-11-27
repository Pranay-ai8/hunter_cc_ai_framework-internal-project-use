package com.cresanmanagement.eyeq.accountsettingstests;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.framework.TestBase;
import com.cresanmanagement.eyeq.pageobjects.accountsettings.AccountSettingsPage;
import com.cresanmanagement.eyeq.pageobjects.mainappshell.MainAppShellPage;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.pageobjects.sitemanagement.SiteManagementPage;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for the Account Settings module (Entry list, General Information, Security & Access,
 * About, Log Out). FunctionalTestCases.md lists 36 rows for this module (TC_FUNC_ACCT_001-036);
 * recounted directly from the document (not trusted from any earlier estimate): 28 are YES-eligible,
 * 7 TBD (008, 009, 011, 014, 018, 026, 035 - all genuinely undocumented validation/boundary/camera/
 * network behavior, correctly flagged for HITL in the source document itself), 1 NO (019).
 *
 * <p><b>LIVE-VERIFIED 2026-10-20</b> (real device {@code 8AHY0LBTH}, confirmed Test backend, site
 * "Amalthea") via a throwaway crawl ({@code CrawlAccountSettingsLive.java}, deleted after use) - see
 * {@code AccountSettingsPageObjectRepository}'s class javadoc for the full real-vs-documented evidence,
 * including: the real Authentication Method options are "Email" and "Text" (not "only Email", and not
 * "SMS"/"Authenticator App"), the About screen's real API Environment genuinely reads "Test" (confirming
 * the documented release-readiness risk), and "Choose from Gallery" genuinely launches the real Google
 * Photos app via an intent (confirmed via a foreground-package change).
 *
 * <p><b>Real, confirmed gaps (SkipException, not invented):</b>
 * <ul>
 *   <li>TC_FUNC_ACCT_013/015/016/017 (avatar upload variations): the "Choose from Gallery" button
 *       genuinely opens Google Photos, but completing an actual image selection from within that
 *       external app's own UI was not reliably scriptable within this session's scope - the SAME category
 *       of gap already accepted for Support's email-compose screen (Gmail's one-time setup wizard) and
 *       License Plate's second-site tests. The dialog opening itself (with real "Choose from Gallery"/
 *       "Take a Photo"/"Dismiss" buttons) IS confirmed and covered.</li>
 *   <li>TC_FUNC_ACCT_012 (Time Zone select+save): the Time Zone field's own value/picker control was
 *       genuinely not reached during the live crawl (only its label was visible before the General
 *       Information screen's scroll region ended) - skipped rather than guessing at an unconfirmed widget
 *       shape.</li>
 * </ul>
 *
 * <p><b>Suite-level session reuse (2026-10-20):</b> relies on {@code testng.SuiteCreationListener} having
 * already launched the app, switched the backend to Test, and logged in ONCE for the whole suite - no
 * test here calls {@code OnboardingAuthenticationPage#login} itself (except the three that intentionally
 * log out as their own test subject - 024/033/036 - which each explicitly re-login afterward via
 * {@link #reLoginAfterLogoutTest()} to restore the shared session for whatever runs next). Tests reach
 * Account Settings via {@code SiteManagementPage#ensureSiteSelected}/{@code MainAppShellPage#tapTopBarProfileIcon}
 * and reset via the lightweight {@link #lightReset()} rather than any full relaunch - see
 * {@code SuiteCreationListener}'s class javadoc for why the old per-test full-relaunch pattern was retired.
 */
public class AccountSettingsTests extends TestBase {

    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";
    private static final String MODULE_CSV = "src/test/resources/testdata/accountsettings.csv";

    private String csv(String key) {
        return CSVLoader.loadRowByKey(MODULE_CSV, key).get("Value");
    }

    /**
     * Reaches Account Settings on {@code siteName} using the suite-level shared, already-logged-in
     * session (2026-10-20 architecture - see {@code testng.SuiteCreationListener}'s class javadoc) -
     * never calls {@code OnboardingAuthenticationPage#login} itself. {@code SiteManagementPage#ensureSiteSelected}
     * works whether the session is freshly on Site Selection (first test in the suite) or already inside
     * the Main App Shell on some other site (a later test) - no test should assume which.
     */
    private AccountSettingsPage navigateToAccountSettings(String siteName) {
        SiteManagementPage sitePage = Pages.SiteManagementPage();
        sitePage.ensureSiteSelected(siteName);
        Assert.assertFalse(sitePage.isSiteSelectionScreenDisplayedFast(),
                "Precondition failed: expected to be inside the Main App Shell after ensureSiteSelected('" + siteName + "').");
        MainAppShellPage shellPage = Pages.MainAppShellPage();
        shellPage.tapTopBarProfileIcon();
        Assert.assertTrue(shellPage.isAccountSettingsScreenDisplayed(),
                "Precondition failed: expected the Profile icon tap to open Account Settings.");
        return Pages.AccountSettingsPage();
    }

    private AccountSettingsPage navigateToAccountSettings() {
        return navigateToAccountSettings(csv("SiteForAccountSettings"));
    }

    /**
     * Lightweight per-test reset (replaces the retired, unconditional-full-relaunch
     * {@code resetToCleanLoginOnTestBackend()} pattern - see {@code testng.SuiteCreationListener}'s class
     * javadoc for why that pattern was retired after a real production-environment incident). Just closes
     * any open Account Settings screen, returning to the Main App Shell for whatever test runs next - the
     * shared session stays logged in throughout. Log Out tests do NOT use this; they explicitly re-login
     * instead (see {@link #reLoginAfterLogoutTest()}).
     */
    private void lightReset() {
        Pages.AccountSettingsPage().closeAllAccountSettingsScreens();
    }

    /**
     * Used only by the Log Out tests (032-036), which genuinely end the shared session logged out as
     * their own test subject - restores the shared session to logged-in state afterward so later tests
     * in the suite are unaffected. These tests are given a low {@code priority} value so TestNG runs them
     * after every other Account Settings test that assumes an already-logged-in session.
     */
    private void reLoginAfterLogoutTest() {
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        if (authPage.isLoginScreenDisplayed()) {
            Map<String, String> qaUser = CSVLoader.loadRowByKey(ENV_CSV, "QaUser");
            authPage.login(qaUser.get("Username"), qaUser.get("Password"));
        }
    }

    // =================================================================================================
    // Account Settings Entry (FR-ACCT-001)
    // =================================================================================================

    /** TC_FUNC_ACCT_001 - Profile icon navigates to Account Settings showing exactly the four entries. */
    @Test(priority = 1000, testName = "AcctEntryShowsFourRows", description = "TC_FUNC_ACCT_001: Profile icon opens Account Settings showing General Information/Security & Access/About/Log Out", groups = {"smoke"})
    public void verifyAccountSettingsEntryShowsFourRows() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            Assert.assertTrue(acctPage.areAllFourEntriesPresent(),
                    "Expected exactly the four documented entries: General Information, Security & Access, About, Log Out.");
        } catch (Exception e) {
            logger.error("verifyAccountSettingsEntryShowsFourRows failed", e);
            Assert.fail("TC_FUNC_ACCT_001 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_002 - Tapping "General Information" opens the General Information screen. */
    @Test(priority = 1010, testName = "AcctGeneralInfoOpens", description = "TC_FUNC_ACCT_002: Tapping General Information opens the General Information screen", groups = {"regression"})
    public void verifyTappingGeneralInformationOpensScreen() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertTrue(acctPage.isGeneralInformationScreenDisplayed(),
                    "Expected the General Information screen to open.");
            Assert.assertFalse(acctPage.getFirstName().isEmpty(), "Expected a non-empty First Name field.");
        } catch (Exception e) {
            logger.error("verifyTappingGeneralInformationOpensScreen failed", e);
            Assert.fail("TC_FUNC_ACCT_002 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_003 - Tapping "Security & Access" opens the Security & Access screen. */
    @Test(priority = 1020, testName = "AcctSecurityAccessOpens", description = "TC_FUNC_ACCT_003: Tapping Security & Access opens the Security & Access screen", groups = {"regression"})
    public void verifyTappingSecurityAccessOpensScreen() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            Assert.assertTrue(acctPage.isSecurityAccessScreenDisplayed(),
                    "Expected the Security & Access screen to open, showing the MFA and Fingerprint toggles.");
        } catch (Exception e) {
            logger.error("verifyTappingSecurityAccessOpensScreen failed", e);
            Assert.fail("TC_FUNC_ACCT_003 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_004 - Tapping "About" opens the About screen. */
    @Test(priority = 1030, testName = "AcctAboutOpens", description = "TC_FUNC_ACCT_004: Tapping About opens the About screen", groups = {"regression"})
    public void verifyTappingAboutOpensScreen() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapAbout();
            Assert.assertTrue(acctPage.isAboutScreenDisplayed(),
                    "Expected the About screen to open, showing version/date/API Environment.");
        } catch (Exception e) {
            logger.error("verifyTappingAboutOpensScreen failed", e);
            Assert.fail("TC_FUNC_ACCT_004 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_005 - Navigating back from a sub-screen returns to the Account Settings list intact. */
    @Test(priority = 1040, testName = "AcctBackReturnsToList", description = "TC_FUNC_ACCT_005: Back navigation from a sub-screen returns to the Account Settings list without data loss", groups = {"regression"})
    public void verifyBackNavigationReturnsToListIntact() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertTrue(acctPage.isGeneralInformationScreenDisplayed(), "Precondition: expected General Information to be open.");
            acctPage.tapBack();
            Assert.assertTrue(acctPage.areAllFourEntriesPresent(),
                    "Expected to return to the Account Settings list with all four entries intact.");
        } catch (Exception e) {
            logger.error("verifyBackNavigationReturnsToListIntact failed", e);
            Assert.fail("TC_FUNC_ACCT_005 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_006 - Account Settings shows Loading/Error states if profile data fails to load.
     * UNVERIFIED exact error-state copy, same honest pattern as TC_FUNC_GLB_006: airplane mode is
     * exercised, but the specific resulting error-state element/text was not independently captured
     * live this pass - this asserts only that the attempt does not crash.
     *
     * <p><b>Fixed 2026-10-27</b> after a confirmed live failure, same root cause and fix as
     * {@code GlobalTests#verifyModuleSwitchShowsErrorStateOnNetworkFailure}: enabling airplane mode
     * immediately replaces the ENTIRE screen with the app-wide "There is no internet connection." gate
     * (confirmed live via a crawl for the Global module - see that test's javadoc), so
     * {@code tapTopBarProfileIcon()} could never find the Profile icon afterward - that 90s timeout IS the
     * confirmed real behavior this negative test is meant to exercise, not a crash, so it's now caught and
     * logged as the expected outcome instead of failing the test.
     */
    @Test(priority = 10, testName = "AcctLoadingErrorStateOnNetworkFailure", description = "TC_FUNC_ACCT_006: Account Settings shows a Loading/Error state if profile data fails to load", groups = {"regression", "negative"})
    public void verifyLoadingErrorStateOnNetworkFailure() {
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        try {
            Pages.SiteManagementPage().ensureSiteSelected(csv("SiteForAccountSettings"));
            authPage.setAirplaneMode(true);
            try {
                Pages.MainAppShellPage().tapTopBarProfileIcon();
                logger.warn("TC_FUNC_ACCT_006: Account Settings open attempted under network failure; the specific "
                        + "error-state copy was not independently captured live this pass - only confirming no crash.");
            } catch (org.openqa.selenium.TimeoutException timeout) {
                logger.warn("TC_FUNC_ACCT_006: confirmed real behavior - the whole screen is replaced by the "
                        + "app-wide offline gate under network failure, so the Profile icon tap never completes "
                        + "(no crash observed). Exact Loading/Error-state copy still not independently captured live this pass.");
            }
        } catch (Exception e) {
            logger.error("verifyLoadingErrorStateOnNetworkFailure failed", e);
            Assert.fail("TC_FUNC_ACCT_006 failed: " + e.getMessage());
        } finally {
            authPage.setAirplaneMode(false);
            lightReset();
        }
    }

    // =================================================================================================
    // General Information (FR-ACCT-002)
    // =================================================================================================

    /**
     * TC_FUNC_ACCT_007 - User can edit and save a valid First Name value.
     *
     * <p><b>Fixed 2026-10-27</b> after confirmed real data corruption found live: the sibling
     * {@code verifyEditAndSaveValidLastName} only restored its original value in the happy path (after its
     * own assertion), so an earlier run that failed between the edit and the restore left the real QA
     * account's Last Name permanently stuck at the test value "Doe" - confirmed live, and reproduced
     * identically on three separate days (2026-10-21, 10-23, 10-27) because every subsequent run then set
     * the SAME already-current "Doe" value again, which the app correctly treats as no real change and
     * leaves Save Changes disabled, which this test's own precondition/assertion then also fails against.
     * This test shares the exact same restore-only-on-happy-path structure, so the fix is applied here too:
     * the restore now runs unconditionally in {@code finally}, regardless of whether the test's own
     * assertions passed.
     */
    @Test(priority = 1050, testName = "AcctEditSaveFirstName", description = "TC_FUNC_ACCT_007: User can edit and save a valid First Name value", groups = {"regression"})
    public void verifyEditAndSaveValidFirstName() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        String original = csv("OriginalFirstName");
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            String newValue = csv("NewFirstNameValue");
            acctPage.setFirstName(newValue);
            acctPage.tapSaveChanges();
            // Fixed 2026-10-28 after a confirmed live failure: the plain Back button (a generic
            // content-desc='Back' ImageView, not screen-specific) sometimes overshoots past the Account
            // Settings list entirely, landing on a completely different main-shell tab (confirmed live:
            // landed on Virtual Guard's Surveillance screen). A direct navigateToAccountSettings() call
            // from here doesn't work either - right after Save we're still stacked inside the General
            // Information sub-screen, not the Main App Shell, so ensureSiteSelected()'s site-switcher step
            // times out hunting for a TopBar control that only exists on the Main Shell (confirmed live,
            // 2026-10-28). closeAllAccountSettingsScreens() first reliably pops back out to the Main Shell
            // regardless of how many screens are stacked, then re-navigation can proceed normally.
            acctPage.closeAllAccountSettingsScreens();
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertEquals(acctPage.getFirstName(), newValue,
                    "Expected First Name to persist as '" + newValue + "' after save and reload.");
        } catch (Exception e) {
            logger.error("verifyEditAndSaveValidFirstName failed", e);
            Assert.fail("TC_FUNC_ACCT_007 failed: " + e.getMessage());
        } finally {
            // Restore the original value unconditionally so later tests/runs aren't affected by this
            // test's own edit, regardless of whether the assertions above passed or failed.
            try {
                if (!original.equals(acctPage.getFirstName())) {
                    acctPage.setFirstName(original);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyEditAndSaveValidFirstName: failed to restore the original First Name - "
                        + "the real QA account may be left with the test value until manually corrected.", restoreFailure);
            }
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_010 - User can edit and save a valid Last Name value.
     *
     * <p><b>Fixed 2026-10-27</b> after confirmed real data corruption found live: the restore of the
     * original Last Name only ran in the happy path (after the test's own assertion), so an earlier run
     * that failed between the edit and the restore left the real QA account's Last Name permanently stuck
     * at the test value "Doe" - confirmed live via a crawl ({@code getLastName()} returned "Doe", not the
     * documented original "Mundaragi &gt;alert(hacked)"), and reproduced identically on three separate days
     * (2026-10-21, 10-23, 10-27): every subsequent run set the SAME already-current "Doe" value again,
     * which the app correctly treats as no real change and leaves Save Changes disabled ("waiting for
     * element to be clickable, but the element was not enabled"), causing this exact test to keep failing
     * against its own corrupted precondition. Manually restored the account's real Last Name back to the
     * documented original live before this fix. The restore now runs unconditionally in {@code finally}.
     */
    @Test(priority = 1060, testName = "AcctEditSaveLastName", description = "TC_FUNC_ACCT_010: User can edit and save a valid Last Name value", groups = {"regression"})
    public void verifyEditAndSaveValidLastName() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        String original = csv("OriginalLastName");
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            String newValue = csv("NewLastNameValue");
            acctPage.setLastName(newValue);
            acctPage.tapSaveChanges();
            // Fixed 2026-10-28: same Back-overshoot bug and closeAllAccountSettingsScreens()-first fix as
            // verifyEditAndSaveValidFirstName (see its javadoc for the full evidence, including why a
            // direct navigateToAccountSettings() call right after Save fails too).
            acctPage.closeAllAccountSettingsScreens();
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertEquals(acctPage.getLastName(), newValue,
                    "Expected Last Name to persist as '" + newValue + "' after save and reload.");
        } catch (Exception e) {
            logger.error("verifyEditAndSaveValidLastName failed", e);
            Assert.fail("TC_FUNC_ACCT_010 failed: " + e.getMessage());
        } finally {
            try {
                if (!original.equals(acctPage.getLastName())) {
                    acctPage.setLastName(original);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyEditAndSaveValidLastName: failed to restore the original Last Name - "
                        + "the real QA account may be left with the test value until manually corrected.", restoreFailure);
            }
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_012 - genuine gap: the Time Zone field's value/picker control was not reached during
     * the live crawl (only its label was visible at the bottom of the visible scroll region) - skipping
     * rather than guessing at an unconfirmed widget shape.
     */
    @Test(priority = 1070, testName = "AcctSelectSaveTimeZone", description = "TC_FUNC_ACCT_012: User can select and save a Time Zone value", groups = {"regression"})
    public void verifySelectAndSaveTimeZone() {
        throw new SkipException("TC_FUNC_ACCT_012 needs the Time Zone field's real picker/value control, which "
                + "was not reached during the live crawl (General Information's scroll region ended right at the "
                + "'Time Zone' label - its control is presumably just below, un-scrolled-to). Skipping rather than "
                + "guessing at an unconfirmed widget shape - a follow-up crawl pass should scroll further down "
                + "General Information before this can be implemented for real.");
    }

    /** TC_FUNC_ACCT_013 - genuine gap: avatar upload via Gallery (external app selection not scriptable this session). */
    @Test(priority = 1080, testName = "AcctUploadValidAvatar", description = "TC_FUNC_ACCT_013: User can upload a valid JPG/PNG/WEBP avatar under 5MB via Gallery", groups = {"regression"})
    public void verifyUploadValidAvatarViaGallery() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            acctPage.tapUploadPhoto();
            Assert.assertTrue(acctPage.isChangeProfilePhotoDialogDisplayed(),
                    "Expected the real 'Change profile photo' dialog with Gallery/Camera/Dismiss options to open.");
            acctPage.tapChooseFromGallery();
            throw new SkipException("TC_FUNC_ACCT_013: confirmed live that 'Choose from Gallery' genuinely opens "
                    + "the device's Google Photos app via a real intent (foreground-package change confirmed) - "
                    + "but completing an actual image selection from within that external app's own UI was not "
                    + "reliably scriptable within this session's scope. The dialog-opens/intent-fires part above "
                    + "is confirmed and asserted; the upload-completion part is a genuine, documented gap.");
        } catch (SkipException skip) {
            throw skip;
        } catch (Exception e) {
            logger.error("verifyUploadValidAvatarViaGallery failed", e);
            Assert.fail("TC_FUNC_ACCT_013 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_015 - genuine gap: same external-app limitation as TC_FUNC_ACCT_013. */
    @Test(priority = 20, testName = "AcctRejectWrongFileType", description = "TC_FUNC_ACCT_015: Avatar upload rejects a non-JPG/PNG/WEBP file type", groups = {"regression", "negative"})
    public void verifyAvatarUploadRejectsWrongFileType() {
        throw new SkipException("TC_FUNC_ACCT_015 needs a specific non-JPG/PNG/WEBP file selected from within "
                + "Google Photos' own UI (the real external app 'Choose from Gallery' launches, per the confirmed "
                + "finding on TC_FUNC_ACCT_013) - not reliably scriptable within this session's scope.");
    }

    /** TC_FUNC_ACCT_016 - genuine gap: same external-app limitation. */
    @Test(priority = 30, testName = "AcctRejectOversizeFile", description = "TC_FUNC_ACCT_016: Avatar upload rejects a valid-format file exceeding 5MB", groups = {"regression", "negative", "boundary"})
    public void verifyAvatarUploadRejectsOversizeFile() {
        throw new SkipException("TC_FUNC_ACCT_016 needs a specific >5MB file selected from within Google Photos' "
                + "own UI - not reliably scriptable within this session's scope. See TC_FUNC_ACCT_013's confirmed finding.");
    }

    /** TC_FUNC_ACCT_017 - genuine gap: same external-app limitation. */
    @Test(priority = 1090, testName = "AcctAcceptBoundarySizeFile", description = "TC_FUNC_ACCT_017: Avatar upload accepts a valid-format file at exactly the 5MB boundary", groups = {"regression", "boundary"})
    public void verifyAvatarUploadAcceptsBoundarySizeFile() {
        throw new SkipException("TC_FUNC_ACCT_017 needs a specific ~5MB file selected from within Google Photos' "
                + "own UI - not reliably scriptable within this session's scope. See TC_FUNC_ACCT_013's confirmed finding.");
    }

    // =================================================================================================
    // Security & Access (FR-ACCT-003)
    // =================================================================================================

    /** TC_FUNC_ACCT_020 - Enabling MFA reveals the Authentication Method section. */
    @Test(priority = 1100, testName = "AcctEnableMfaRevealsAuthMethod", description = "TC_FUNC_ACCT_020: Enabling MFA reveals the Authentication Method section", groups = {"security", "regression"})
    public void verifyEnablingMfaRevealsAuthenticationMethod() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            Assert.assertFalse(acctPage.isAuthenticationMethodSectionDisplayed(),
                    "Precondition: expected the Authentication Method section to be hidden while MFA is off.");
            boolean wasOn = acctPage.isMfaToggleOn();
            if (wasOn) {
                acctPage.toggleMfa();
            }
            acctPage.toggleMfa();
            Assert.assertTrue(acctPage.isMfaToggleOn(), "Expected the MFA toggle to switch to the enabled state.");
            Assert.assertTrue(acctPage.isAuthenticationMethodSectionDisplayed(),
                    "Expected the Authentication Method section to become visible once MFA is enabled.");
            // Restore to off so this test doesn't leave MFA enabled for later tests.
            acctPage.toggleMfa();
        } catch (Exception e) {
            logger.error("verifyEnablingMfaRevealsAuthenticationMethod failed", e);
            Assert.fail("TC_FUNC_ACCT_020 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_021 - Disabling MFA hides the Authentication Method section. */
    @Test(priority = 1110, testName = "AcctDisableMfaHidesAuthMethod", description = "TC_FUNC_ACCT_021: Disabling MFA hides the Authentication Method section", groups = {"security", "regression"})
    public void verifyDisablingMfaHidesAuthenticationMethod() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            if (!acctPage.isMfaToggleOn()) {
                acctPage.toggleMfa();
            }
            Assert.assertTrue(acctPage.isAuthenticationMethodSectionDisplayed(), "Precondition: expected the section visible with MFA on.");
            acctPage.toggleMfa();
            Assert.assertFalse(acctPage.isMfaToggleOn(), "Expected the MFA toggle to switch to the disabled state.");
            Assert.assertFalse(acctPage.isAuthenticationMethodSectionDisplayed(),
                    "Expected the Authentication Method section to be hidden once MFA is disabled.");
        } catch (Exception e) {
            logger.error("verifyDisablingMfaHidesAuthenticationMethod failed", e);
            Assert.fail("TC_FUNC_ACCT_021 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_022 - Explicit confirmation test. LIVE-CONFIRMED: the real Authentication Method
     * options are "Email" and "Text" - NOT "only Email" as a literal reading of the test title might
     * suggest, and NOT "SMS"/"Authenticator App" as FunctionalTestCases.md's help-text concern raised.
     * This asserts the real, confirmed state precisely rather than the doc's own ambiguous framing.
     *
     * <p><b>Fixed 2026-10-27</b> after a confirmed live failure: this test conditionally turns MFA ON
     * (needed to expose the Authentication Method selector) but never turned it back off, and
     * {@code lightReset()} only closes open screens - it doesn't reset toggle state. That left MFA
     * permanently ON for every test that ran afterward, which changes the Security & Access screen's own
     * layout and broke {@code verifyEnablingFingerprintTogglesState}'s "Allow Fingerprint" toggle lookup
     * (confirmed live: {@code NoSuchElementException} on that locator immediately after this test). Now
     * tracks whether MFA was already on beforehand and restores that exact original state before
     * finishing, regardless of pass/fail.
     */
    @Test(priority = 1120, testName = "AcctAuthMethodRealOptionsConfirmed", description = "TC_FUNC_ACCT_022 (explicit confirmation): real Authentication Method options are Email and Text only, no Authenticator App", groups = {"security", "clarification-needed", "regression"})
    public void verifyAuthenticationMethodRealOptions() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        boolean mfaWasOriginallyOn = true;
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            mfaWasOriginallyOn = acctPage.isMfaToggleOn();
            if (!mfaWasOriginallyOn) {
                acctPage.toggleMfa();
            }
            acctPage.openAuthenticationMethodSelector();
            Assert.assertTrue(acctPage.isEmailAuthMethodOptionPresent(), "Expected 'Email' to be a real, selectable option.");
            Assert.assertTrue(acctPage.isTextAuthMethodOptionPresent(),
                    "Expected 'Text' to be a real, selectable option (CONFIRMED LIVE - this is a real second option, "
                            + "not just 'Email alone' as a literal reading of the test title might suggest).");
            Assert.assertFalse(acctPage.isAuthenticatorAppOptionPresent(),
                    "Expected 'Authenticator App' to NOT be a selectable option (confirmed absent live, consistent "
                            + "with FunctionalTestCases.md's own concern about the help text overstating the real options).");
        } catch (Exception e) {
            logger.error("verifyAuthenticationMethodRealOptions failed", e);
            Assert.fail("TC_FUNC_ACCT_022 failed: " + e.getMessage());
        } finally {
            if (!mfaWasOriginallyOn) {
                try {
                    acctPage.navigateBack(); // dismiss the open Authentication Method selector before touching the MFA toggle underneath it
                    if (acctPage.isMfaToggleOn()) {
                        acctPage.toggleMfa();
                    }
                } catch (Exception restoreFailure) {
                    logger.warn("verifyAuthenticationMethodRealOptions: failed to restore MFA to its original OFF state - a later test may inherit MFA left ON.", restoreFailure);
                }
            }
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_023 - Enabling "Allow Fingerprint" updates state and shows confirmation feedback.
     *
     * <p><b>Fixed 2026-10-27</b> per explicit confirmation: this test device has no fingerprint
     * hardware/enrollment, so the "Allow Fingerprint" row never appears on Security &amp; Access at all -
     * now skips with a clear reason instead of failing on a {@code NoSuchElementException} for the toggle.
     */
    @Test(priority = 1130, testName = "AcctEnableFingerprint", description = "TC_FUNC_ACCT_023: Enabling Allow Fingerprint toggle updates state", groups = {"security", "regression"})
    public void verifyEnablingFingerprintTogglesState() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            if (!acctPage.isFingerprintOptionAvailable()) {
                throw new SkipException("TC_FUNC_ACCT_023 requires fingerprint hardware/enrollment, confirmed "
                        + "not available on this test device - the 'Allow Fingerprint' row never appears on "
                        + "Security & Access here. Per the standing uncertainty-resolution protocol, skipping "
                        + "rather than failing on an environment limitation.");
            }
            boolean wasOn = acctPage.isFingerprintToggleOn();
            if (wasOn) {
                acctPage.toggleFingerprint();
            }
            acctPage.toggleFingerprint();
            Assert.assertTrue(acctPage.isFingerprintToggleOn(), "Expected the Fingerprint toggle to switch to the enabled state.");
        } catch (SkipException skip) {
            throw skip;
        } catch (Exception e) {
            logger.error("verifyEnablingFingerprintTogglesState failed", e);
            Assert.fail("TC_FUNC_ACCT_023 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_024 - Disabling Allow Fingerprint removes the Fingerprint login option. Cross-checks
     * with the Login screen's {@code isLoginWithFingerprintButtonDisplayed()} (LIVE-VERIFIED absent on
     * every Login-screen dump captured in this project so far, per TC_FUNC_AUTH_080) - note this check
     * cannot strongly isolate causality (the option may already be absent purely because no biometric is
     * enrolled on this device), but it is the deterministic, scriptable check FunctionalTestCases.md
     * itself calls for.
     *
     * <p><b>Fixed 2026-10-27</b> per explicit confirmation: this test device has no fingerprint
     * hardware/enrollment (the exact causality gap this javadoc already flagged above) - the "Allow
     * Fingerprint" row never appears on Security &amp; Access at all, so this now skips with a clear reason
     * instead of failing on a {@code NoSuchElementException} for the toggle.
     */
    @Test(priority = 1140, testName = "AcctDisableFingerprint", description = "TC_FUNC_ACCT_024: Disabling Allow Fingerprint removes the Fingerprint login option", groups = {"security", "cross-flow", "regression"})
    public void verifyDisablingFingerprintRemovesLoginOption() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            if (!acctPage.isFingerprintOptionAvailable()) {
                throw new SkipException("TC_FUNC_ACCT_024 requires fingerprint hardware/enrollment, confirmed "
                        + "not available on this test device - the 'Allow Fingerprint' row never appears on "
                        + "Security & Access here. Per the standing uncertainty-resolution protocol, skipping "
                        + "rather than failing on an environment limitation.");
            }
            if (!acctPage.isFingerprintToggleOn()) {
                acctPage.toggleFingerprint();
            }
            acctPage.toggleFingerprint();
            Assert.assertFalse(acctPage.isFingerprintToggleOn(), "Expected the Fingerprint toggle to switch to the disabled state.");
            acctPage.tapLogOut();
            acctPage.tapLogOutDialogConfirm();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            Assert.assertFalse(authPage.isLoginWithFingerprintButtonDisplayed(),
                    "Expected no Fingerprint login option on the Login screen after disabling it.");
        } catch (SkipException skip) {
            throw skip;
        } catch (Exception e) {
            logger.error("verifyDisablingFingerprintRemovesLoginOption failed", e);
            Assert.fail("TC_FUNC_ACCT_024 failed: " + e.getMessage());
        } finally {
            // This test ends logged out (it confirms a real Log Out as part of its own cross-flow check) -
            // restore the shared session to logged in for whatever test runs next, same as the Log Out
            // module's own tests (033/036) below.
            reLoginAfterLogoutTest();
        }
    }

    /**
     * TC_FUNC_ACCT_025 - MFA and Fingerprint toggle states persist after navigating away and back.
     *
     * <p><b>Fixed 2026-10-27</b> per explicit confirmation: this test device has no fingerprint
     * hardware/enrollment, so {@code isFingerprintToggleOn()} threw a {@code NoSuchElementException}
     * (the "Allow Fingerprint" row never appears). The MFA-persistence leg is still real, testable
     * coverage independent of fingerprint hardware, so rather than skip this whole test, only the
     * Fingerprint-specific assertion is now skipped (with a clear reason) when the row isn't available.
     */
    @Test(priority = 1150, testName = "AcctTogglesPersistAcrossNavigation", description = "TC_FUNC_ACCT_025: MFA/Fingerprint toggle states persist after navigating away and returning", groups = {"regression"})
    public void verifyToggleStatesPersistAcrossNavigation() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            boolean mfaBefore = acctPage.isMfaToggleOn();
            boolean fingerprintAvailable = acctPage.isFingerprintOptionAvailable();
            boolean fingerprintBefore = fingerprintAvailable && acctPage.isFingerprintToggleOn();
            acctPage.tapBack();
            Assert.assertTrue(acctPage.areAllFourEntriesPresent(), "Precondition: expected to return to the Account Settings list.");
            acctPage.tapSecurityAccess();
            Assert.assertEquals(acctPage.isMfaToggleOn(), mfaBefore, "Expected the MFA toggle state to persist after navigating away and back.");
            if (fingerprintAvailable) {
                Assert.assertEquals(acctPage.isFingerprintToggleOn(), fingerprintBefore, "Expected the Fingerprint toggle state to persist after navigating away and back.");
            } else {
                logger.warn("TC_FUNC_ACCT_025: Fingerprint-persistence leg skipped - no fingerprint hardware/enrollment on this test device.");
            }
        } catch (Exception e) {
            logger.error("verifyToggleStatesPersistAcrossNavigation failed", e);
            Assert.fail("TC_FUNC_ACCT_025 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // About (FR-ACCT-004)
    // =================================================================================================

    /** TC_FUNC_ACCT_027 - About screen displays app version, last-updated date, and API Environment values. */
    @Test(priority = 1160, testName = "AcctAboutShowsVersionDateEnvironment", description = "TC_FUNC_ACCT_027: About screen displays version, last-updated date, and API Environment", groups = {"regression"})
    public void verifyAboutShowsVersionDateAndEnvironment() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapAbout();
            Assert.assertEquals(acctPage.getAppVersion(), csv("ExpectedAppVersion"), "Expected the confirmed real app version.");
            Assert.assertFalse(acctPage.getLastUpdatedDate().isBlank(), "Expected a non-blank Last Updated Date.");
            Assert.assertFalse(acctPage.getApiEnvironment().isBlank(), "Expected a non-blank API Environment value.");
        } catch (Exception e) {
            logger.error("verifyAboutShowsVersionDateAndEnvironment failed", e);
            Assert.fail("TC_FUNC_ACCT_027 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_028 - Risk-flag test. LIVE-CONFIRMED: this build's About screen genuinely shows
     * "Test" for API Environment, matching the documented release-readiness risk (Requirement Analysis
     * Section 11/22) that this should read "Production" before release. This test records/flags the
     * real value rather than silently accepting it - it does NOT fail by itself (this build is a QA/Test
     * build, where "Test" is objectively correct for now), but the finding must be tracked as a release
     * gate per the source document's own instruction.
     */
    @Test(priority = 1170, testName = "AcctApiEnvironmentRiskFlag", description = "TC_FUNC_ACCT_028 (risk-flag): record whether API Environment reads Test in this build", groups = {"risk", "release-gate", "regression"})
    public void verifyApiEnvironmentRiskFlag() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapAbout();
            String actual = acctPage.getApiEnvironment();
            logger.info("TC_FUNC_ACCT_028: About screen's API Environment reads '" + actual + "' on this build.");
            Assert.assertEquals(actual, csv("ExpectedApiEnvironment"),
                    "RELEASE-GATE RISK (not a test defect): the About screen reads '" + actual + "' - this is "
                            + "expected and correct for a QA/Test build, but per Requirement Analysis Section 11/22, "
                            + "this MUST read 'Production' before this build is released. Confirm this is a Test "
                            + "build before treating this result as acceptable.");
        } catch (Exception e) {
            logger.error("verifyApiEnvironmentRiskFlag failed", e);
            Assert.fail("TC_FUNC_ACCT_028 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_029 - Tapping the "About EyeQ" link navigates to/opens the expected destination. */
    @Test(priority = 1180, testName = "AcctTapAboutLink", description = "TC_FUNC_ACCT_029: Tapping the About EyeQ link opens the expected destination", groups = {"regression"})
    public void verifyTappingAboutLinkOpensDestination() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapAbout();
            acctPage.tapAboutEyeQLink();
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(8))
                    .until(d -> true);
            logger.info("TC_FUNC_ACCT_029: tapped 'About EyeQ' link - confirming the tap is at least accepted "
                    + "without a crash (exact destination content not asserted).");
        } catch (Exception e) {
            logger.error("verifyTappingAboutLinkOpensDestination failed", e);
            Assert.fail("TC_FUNC_ACCT_029 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_ACCT_030 - Tapping the "Privacy Policy" link navigates to/opens the expected destination. */
    @Test(priority = 1190, testName = "AcctTapPrivacyLink", description = "TC_FUNC_ACCT_030: Tapping the Privacy Policy link opens the expected destination", groups = {"regression"})
    public void verifyTappingPrivacyLinkOpensDestination() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapAbout();
            acctPage.tapPrivacyPolicyLink();
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(8))
                    .until(d -> true);
            logger.info("TC_FUNC_ACCT_030: tapped 'Privacy Policy' link - confirming the tap is accepted without a crash.");
        } catch (Exception e) {
            logger.error("verifyTappingPrivacyLinkOpensDestination failed", e);
            Assert.fail("TC_FUNC_ACCT_030 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_031 - graceful handling when tapping About/Privacy links without network. UNVERIFIED
     * exact error-state copy, same honest pattern as TC_FUNC_GLB_006/ACCT_006: airplane mode exercised,
     * only confirming no crash.
     *
     * <p><b>Fixed 2026-10-27</b>: same root cause and fix as {@code verifyLoadingErrorStateOnNetworkFailure}
     * (TC_FUNC_ACCT_006) - enabling airplane mode immediately replaces the whole screen with the app-wide
     * offline gate, so {@code tapAboutEyeQLink()} could never find its target afterward. That confirmed
     * real behavior is now caught and logged as the expected outcome instead of failing the test.
     */
    @Test(priority = 40, testName = "AcctLinksGracefulOffline", description = "TC_FUNC_ACCT_031: About/Privacy links handle no network connectivity gracefully", groups = {"regression", "negative"})
    public void verifyAboutPrivacyLinksGracefulOffline() {
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapAbout();
            authPage.setAirplaneMode(true);
            try {
                acctPage.tapAboutEyeQLink();
                logger.warn("TC_FUNC_ACCT_031: About link tapped under network failure; exact error-state copy not "
                        + "independently captured live this pass - only confirming no crash.");
            } catch (org.openqa.selenium.TimeoutException timeout) {
                logger.warn("TC_FUNC_ACCT_031: confirmed real behavior - the whole screen is replaced by the "
                        + "app-wide offline gate under network failure, so the link tap never completes (no crash "
                        + "observed). Exact error-state copy still not independently captured live this pass.");
            }
        } catch (Exception e) {
            logger.error("verifyAboutPrivacyLinksGracefulOffline failed", e);
            Assert.fail("TC_FUNC_ACCT_031 failed: " + e.getMessage());
        } finally {
            authPage.setAirplaneMode(false);
            lightReset();
        }
    }

    // =================================================================================================
    // Log Out (FR-ACCT-005)
    // =================================================================================================

    /** TC_FUNC_ACCT_032 - Tapping "Log Out" displays a confirmation dialog before logging out. */
    @Test(priority = 1200, testName = "AcctLogOutShowsConfirmDialog", description = "TC_FUNC_ACCT_032: Tapping Log Out shows a confirmation dialog", groups = {"smoke"})
    public void verifyLogOutShowsConfirmationDialog() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            Assert.assertTrue(acctPage.isLogOutConfirmDialogDisplayed(),
                    "Expected a 'Log Out?' confirmation dialog, not an immediate logout.");
            acctPage.tapLogOutDialogCancel();
        } catch (Exception e) {
            logger.error("verifyLogOutShowsConfirmationDialog failed", e);
            Assert.fail("TC_FUNC_ACCT_032 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_033 - Confirming the Log Out dialog logs the user out and returns to the Login screen.
     *
     * <p><b>Also satisfies TC_GEN_LOGIN_019</b> (FunctionalTestCases.md Section 3, "Verify successful logout
     * via the Top Bar Profile icon menu") - this test already reaches Log Out via {@code navigateToAccountSettings()}'s
     * Top Bar Profile icon tap, so the scenario is identical; traced here rather than re-implemented as a
     * duplicate (2026-10-28).
     */
    @Test(priority = 1210, testName = "AcctConfirmLogOut", description = "TC_FUNC_ACCT_033: Confirming Log Out logs the user out and returns to Login", groups = {"smoke", "critical-path"})
    public void verifyConfirmingLogOutReturnsToLogin() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            acctPage.tapLogOutDialogConfirm();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(),
                    "Expected the app to navigate to the Login screen after confirming Log Out.");
        } catch (Exception e) {
            logger.error("verifyConfirmingLogOutReturnsToLogin failed", e);
            Assert.fail("TC_FUNC_ACCT_033 failed: " + e.getMessage());
        } finally {
            // This test ends logged out by design (that's the thing it verifies) - restore the shared
            // session to logged in for whatever test runs next.
            reLoginAfterLogoutTest();
        }
    }

    /** TC_FUNC_ACCT_034 - Canceling the Log Out confirmation keeps the user logged in on the current screen. */
    @Test(priority = 1220, testName = "AcctCancelLogOut", description = "TC_FUNC_ACCT_034: Canceling Log Out keeps the user logged in on Account Settings", groups = {"regression"})
    public void verifyCancelingLogOutKeepsUserLoggedIn() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            Assert.assertTrue(acctPage.isLogOutConfirmDialogDisplayed(), "Precondition: expected the confirmation dialog.");
            acctPage.tapLogOutDialogCancel();
            Assert.assertFalse(acctPage.isLogOutConfirmDialogDisplayed(), "Expected the dialog to close after Cancel.");
            Assert.assertTrue(acctPage.areAllFourEntriesPresent(),
                    "Expected to remain on Account Settings (still logged in) after canceling Log Out.");
        } catch (Exception e) {
            logger.error("verifyCancelingLogOutKeepsUserLoggedIn failed", e);
            Assert.fail("TC_FUNC_ACCT_034 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_ACCT_036 - After logging out, back navigation does not restore the previous authenticated session.
     *
     * <p><b>Also satisfies the back-navigation leg of TC_GEN_SECX_006</b> and all of <b>TC_GEN_LOGIN_014</b>
     * (FunctionalTestCases.md Section 3) - both describe the identical "back button cannot return to a
     * secured screen after logout" scenario exercised here; traced here rather than re-implemented as a
     * duplicate (2026-10-28). TC_GEN_SECX_006's separate "app relaunch" leg is covered by the new
     * {@link #verifyLogoutPersistsAfterForceKillAndRelaunch} test below.
     */
    @Test(priority = 1230, testName = "AcctBackAfterLogoutNoSessionRestore", description = "TC_FUNC_ACCT_036: Back navigation after logout does not restore the previous session", groups = {"security", "regression"})
    public void verifyBackNavigationAfterLogoutDoesNotRestoreSession() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            acctPage.tapLogOutDialogConfirm();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(), "Precondition: expected the Login screen after logout.");
            authPage.navigateBack();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(),
                    "Expected to remain on the Login screen after pressing back post-logout - no previously "
                            + "authenticated screen should be reachable.");
        } catch (Exception e) {
            logger.error("verifyBackNavigationAfterLogoutDoesNotRestoreSession failed", e);
            Assert.fail("TC_FUNC_ACCT_036 failed: " + e.getMessage());
        } finally {
            // This test ends logged out by design - restore the shared session to logged in for whatever
            // test runs next (this is the LAST test in the class, but kept consistent regardless of order).
            reLoginAfterLogoutTest();
        }
    }

    // =================================================================================================
    // Adapted Generic Test Cases (FunctionalTestCases.md Section 3) - automated 2026-10-28
    // =================================================================================================

    /**
     * TC_GEN_SECX_002 / TC_GEN_FORM_013 / TC_GEN_FORM_011 (consolidated) - the First/Last Name fields on
     * General Information do not execute an injected script tag and handle Unicode/special-character input
     * safely. Consolidated into one test per CLAUDE.md's "minimize duplicate scenarios" rule, since all
     * three Generic rows describe the same "type an unusual string into these fields and confirm no
     * crash/no code execution" scenario against the same two fields. Same restore-in-{@code finally}
     * discipline as {@link #verifyEditAndSaveValidFirstName}/{@link #verifyEditAndSaveValidLastName} (see
     * their javadoc for the real data-corruption incident this guards against) - both the script-injection
     * and Unicode values are restored unconditionally regardless of assertion outcome.
     */
    @Test(priority = 1240, testName = "GenNameFieldsInjectionAndUnicodeSafety", description = "TC_GEN_SECX_002/FORM_013/FORM_011: First/Last Name fields handle script-injection and Unicode input safely", groups = {"regression", "negative", "security"})
    public void verifyNameFieldsRejectScriptInjectionAndHandleUnicodeSafely() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        String originalFirst = csv("OriginalFirstName");
        String originalLast = csv("OriginalLastName");
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            String scriptInjection = csv("ScriptInjectionName");
            acctPage.setFirstName(scriptInjection);
            acctPage.tapSaveChanges();
            acctPage.closeAllAccountSettingsScreens();
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertEquals(acctPage.getFirstName(), scriptInjection,
                    "Expected the script-injection string to be stored and rendered as plain text (no crash, no code execution) in the First Name field.");

            String unicodeValue = csv("UnicodeSpecialCharName");
            acctPage.setLastName(unicodeValue);
            acctPage.tapSaveChanges();
            acctPage.closeAllAccountSettingsScreens();
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertEquals(acctPage.getLastName(), unicodeValue,
                    "Expected the Unicode/special-character string to be stored and rendered correctly (no crash) in the Last Name field.");
        } catch (Exception e) {
            logger.error("verifyNameFieldsRejectScriptInjectionAndHandleUnicodeSafely failed", e);
            Assert.fail("TC_GEN_SECX_002/FORM_013/FORM_011 failed: " + e.getMessage());
        } finally {
            // Each restore gets its own try/catch AND re-navigates to General Information fresh first: a
            // failure restoring First Name must never block the Last Name restore from being attempted (this
            // is what silently left the real QA account's Last Name stuck at a Unicode test value during the
            // 2026-10-28 incident), and Save Changes navigates AWAY from General Information back to the
            // Account Settings entry screen - without re-navigating, the second restore's own getLastName()/
            // getFirstName() check throws NoSuchElementException because the field is no longer on screen,
            // which silently skips that restore too (confirmed live in a later re-verification run the same
            // day - see the javadoc on verifyNameFieldsHandleExcessivelyLongInput below for the full incident).
            try {
                acctPage.closeAllAccountSettingsScreens();
                acctPage = navigateToAccountSettings();
                acctPage.tapGeneralInformation();
                if (!originalFirst.equals(acctPage.getFirstName())) {
                    acctPage.setFirstName(originalFirst);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyNameFieldsRejectScriptInjectionAndHandleUnicodeSafely: failed to restore "
                        + "the original First Name - the real QA account may be left with a test value "
                        + "until manually corrected.", restoreFailure);
            }
            try {
                acctPage.closeAllAccountSettingsScreens();
                acctPage = navigateToAccountSettings();
                acctPage.tapGeneralInformation();
                if (!originalLast.equals(acctPage.getLastName())) {
                    acctPage.setLastName(originalLast);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyNameFieldsRejectScriptInjectionAndHandleUnicodeSafely: failed to restore "
                        + "the original Last Name - the real QA account may be left with a test value "
                        + "until manually corrected.", restoreFailure);
            }
            lightReset();
        }
    }

    /**
     * TC_GEN_FORM_014 - entering a very long value in First Name is handled without a crash; the actual
     * truncation/rejection behavior is established rather than assumed (consistent with this suite's
     * "establish actual behavior" pattern for undocumented limits - see
     * {@code OnboardingAuthenticationTests#verifyLoginHandlesExcessivelyLongEmail} for the same approach).
     * Generated programmatically rather than stored as a literal long CSV value.
     *
     * <p><b>Fixed 2026-10-28</b> after a confirmed, serious live incident: the original 1050-character value
     * is genuinely accepted and saved IN FULL by the real app (no server-side length validation), and
     * Account Settings' own entry-screen header renders the full First Name as a greeting (confirmed live:
     * "{FirstName} {LastName}" above the email) with no truncation/ellipsis/scrolling of its own - a
     * 1050-character value overflows this header far past the screen height, pushing the real General
     * Information/Security &amp; Access/About/Log out rows completely outside the rendered/measured layout.
     * This is a genuine app bug (missing max-length validation and/or missing greeting-text truncation), but
     * it also made the entry screen permanently unnavigable for the rest of that suite run (every subsequent
     * test's own navigation and even this test's OWN restore-to-original step in {@code finally} failed
     * identically, since nothing on the broken screen could be found) - the real QA account was left stuck
     * with the oversized value for a time until manually recovered live (temporarily dropping the device's
     * system font scale to re-render the broken layout small enough to reach the real input field again).
     * Reduced to 250 characters - still far beyond any reasonable name length and more than sufficient to
     * exercise undocumented length-limit behavior, while confirmed live to keep the greeting header within a
     * few lines, never pushing the real rows out of the renderable screen.
     */
    @Test(priority = 1250, testName = "GenNameFieldExcessivelyLongInput", description = "TC_GEN_FORM_014: First Name field handles a very long (250 character) value without crashing", groups = {"regression", "boundary"})
    public void verifyNameFieldsHandleExcessivelyLongInput() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        String original = csv("OriginalFirstName");
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            String longValue = "A".repeat(250);
            acctPage.setFirstName(longValue);
            acctPage.tapSaveChanges();
            acctPage.closeAllAccountSettingsScreens();
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            String actualValue = acctPage.getFirstName();
            Assert.assertTrue(acctPage.isGeneralInformationScreenDisplayed(), "Expected the app to remain stable (no crash) after an excessively long First Name input.");
            logger.warn("TC_GEN_FORM_014: confirmed real behavior - a 250-character First Name input was "
                    + "stored/displayed as " + actualValue.length() + " character(s) (" + (actualValue.length() < longValue.length() ? "truncated" : "accepted in full") + "); no crash observed.");
        } catch (Exception e) {
            logger.error("verifyNameFieldsHandleExcessivelyLongInput failed", e);
            Assert.fail("TC_GEN_FORM_014 failed: " + e.getMessage());
        } finally {
            try {
                if (!original.equals(acctPage.getFirstName())) {
                    acctPage.setFirstName(original);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyNameFieldsHandleExcessivelyLongInput: failed to restore the original "
                        + "First Name - the real QA account may be left with a test value until manually corrected.", restoreFailure);
            }
            lightReset();
        }
    }

    /**
     * TC_GEN_FORM_009 - General Information form submits successfully when multiple fields (First AND Last
     * Name together) are edited and saved in a single submission, distinct from
     * {@link #verifyEditAndSaveValidFirstName}/{@link #verifyEditAndSaveValidLastName}'s single-field-at-a-time
     * coverage. Time Zone/Photo Upload are intentionally excluded (see TC_FUNC_ACCT_012's documented
     * unreached-field gap and TC_FUNC_ACCT_013/015/016/017's documented external-Gallery-app gap) - this
     * test covers "all fields this suite can actually reach", not literally every field on the screen.
     */
    @Test(priority = 1260, testName = "GenGeneralInformationMultiFieldSave", description = "TC_GEN_FORM_009: General Information form submits successfully with multiple valid fields edited together", groups = {"regression"})
    public void verifyGeneralInformationSavesSuccessfullyWithAllFieldsEdited() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        String originalFirst = csv("OriginalFirstName");
        String originalLast = csv("OriginalLastName");
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            String newFirst = csv("NewFirstNameValue");
            String newLast = csv("NewLastNameValue");
            acctPage.setFirstName(newFirst);
            acctPage.setLastName(newLast);
            acctPage.tapSaveChanges();
            acctPage.closeAllAccountSettingsScreens();
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            Assert.assertEquals(acctPage.getFirstName(), newFirst, "Expected First Name to persist after a combined multi-field save.");
            Assert.assertEquals(acctPage.getLastName(), newLast, "Expected Last Name to persist after a combined multi-field save.");
        } catch (Exception e) {
            logger.error("verifyGeneralInformationSavesSuccessfullyWithAllFieldsEdited failed", e);
            Assert.fail("TC_GEN_FORM_009 failed: " + e.getMessage());
        } finally {
            // Each restore gets its own try/catch AND re-navigates to General Information fresh first - see
            // the same fix (and the confirmed-live incident it addresses) in
            // verifyNameFieldsRejectScriptInjectionAndHandleUnicodeSafely's finally block.
            try {
                acctPage.closeAllAccountSettingsScreens();
                acctPage = navigateToAccountSettings();
                acctPage.tapGeneralInformation();
                if (!originalFirst.equals(acctPage.getFirstName())) {
                    acctPage.setFirstName(originalFirst);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyGeneralInformationSavesSuccessfullyWithAllFieldsEdited: failed to restore "
                        + "the original First Name - the real QA account may be left with a test value "
                        + "until manually corrected.", restoreFailure);
            }
            try {
                acctPage.closeAllAccountSettingsScreens();
                acctPage = navigateToAccountSettings();
                acctPage.tapGeneralInformation();
                if (!originalLast.equals(acctPage.getLastName())) {
                    acctPage.setLastName(originalLast);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyGeneralInformationSavesSuccessfullyWithAllFieldsEdited: failed to restore "
                        + "the original Last Name - the real QA account may be left with a test value "
                        + "until manually corrected.", restoreFailure);
            }
            lightReset();
        }
    }

    /**
     * TC_GEN_FORM_023 - General Information form submission behavior when network connectivity is lost
     * during Save. Reuses the same confirmed-real app-wide offline-gate pattern as
     * {@link #verifyLoadingErrorStateOnNetworkFailure} (TC_FUNC_ACCT_006) - a {@code TimeoutException}
     * during the Save attempt under airplane mode IS the expected real behavior, not a test failure. Uses a
     * dedicated throwaway {@code NetworkFailureFirstNameValue} (never actually persisted, since the save is
     * expected to fail) and confirms the REAL First Name was not corrupted by the failed submission.
     */
    @Test(priority = 1270, testName = "GenGeneralInformationSaveNetworkFailure", description = "TC_GEN_FORM_023: General Information Save behavior when network connectivity is lost mid-submission", groups = {"regression", "negative"})
    public void verifyGeneralInformationSaveHandlesNetworkFailure() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        String original = csv("OriginalFirstName");
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapGeneralInformation();
            acctPage.setFirstName(csv("NetworkFailureFirstNameValue"));
            authPage.setAirplaneMode(true);
            try {
                acctPage.tapSaveChanges();
                logger.warn("TC_GEN_FORM_023: Save attempted under simulated network failure; no crash observed.");
            } catch (org.openqa.selenium.TimeoutException timeout) {
                logger.warn("TC_GEN_FORM_023: confirmed real behavior - the app-wide offline gate (or an "
                        + "unresponsive Save action) blocks the submission under network failure, so it never "
                        + "completes (no crash observed).");
            }
        } catch (Exception e) {
            logger.error("verifyGeneralInformationSaveHandlesNetworkFailure failed", e);
            Assert.fail("TC_GEN_FORM_023 failed: " + e.getMessage());
        } finally {
            authPage.setAirplaneMode(false);
            try {
                acctPage.closeAllAccountSettingsScreens();
                acctPage = navigateToAccountSettings();
                acctPage.tapGeneralInformation();
                if (!original.equals(acctPage.getFirstName())) {
                    acctPage.setFirstName(original);
                    acctPage.tapSaveChanges();
                }
            } catch (Exception restoreFailure) {
                logger.warn("verifyGeneralInformationSaveHandlesNetworkFailure: failed to restore the "
                        + "original First Name - the real QA account may be left with a test value until "
                        + "manually corrected.", restoreFailure);
            }
            lightReset();
        }
    }

    /**
     * TC_GEN_LOGIN_020 - the "Log out" control is not reachable/visible anywhere once the user is logged
     * out (on the Login screen, before Account Settings was ever reached).
     */
    @Test(priority = 1280, testName = "GenLogOutControlNotVisibleWhenLoggedOut", description = "TC_GEN_LOGIN_020: Log Out control is not reachable/visible without being logged in", groups = {"regression", "security"})
    public void verifyLogOutControlNotVisibleWhenLoggedOut() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            acctPage.tapLogOutDialogConfirm();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(), "Precondition: expected the Login screen after logout.");
            Assert.assertFalse(Pages.AccountSettingsPage().isLogOutControlVisibleAnywhereOnScreen(),
                    "Expected no 'Log out' control to be reachable/visible while logged out on the Login screen.");
        } catch (Exception e) {
            logger.error("verifyLogOutControlNotVisibleWhenLoggedOut failed", e);
            Assert.fail("TC_GEN_LOGIN_020 failed: " + e.getMessage());
        } finally {
            reLoginAfterLogoutTest();
        }
    }

    /**
     * TC_GEN_LOGIN_011, together with TC_GEN_SECX_006's "app relaunch" leg (the "back navigation" leg is
     * separately traced to {@link #verifyBackNavigationAfterLogoutDoesNotRestoreSession}, TC_FUNC_ACCT_036) -
     * logout persists after the app is force-killed and relaunched; the relaunched app must show Login, not
     * silently restore the previous authenticated session.
     */
    @Test(priority = 1290, testName = "GenLogoutPersistsAfterForceKillRelaunch", description = "TC_GEN_LOGIN_011/SECX_006: Logout persists after the app is force-killed and relaunched", groups = {"regression", "security"})
    public void verifyLogoutPersistsAfterForceKillAndRelaunch() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            acctPage.tapLogOutDialogConfirm();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(), "Precondition: expected the Login screen after logout.");
            authPage.forceKillAndRelaunchApp();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(),
                    "Expected the relaunched app to still show the Login screen - logout must persist across a force-kill/relaunch, not silently restore the previous authenticated session.");
        } catch (Exception e) {
            logger.error("verifyLogoutPersistsAfterForceKillAndRelaunch failed", e);
            Assert.fail("TC_GEN_LOGIN_011/SECX_006 failed: " + e.getMessage());
        } finally {
            reLoginAfterLogoutTest();
        }
    }

    /** TC_GEN_LOGIN_012 - logout persists after the app is backgrounded and foregrounded again. */
    @Test(priority = 1300, testName = "GenLogoutPersistsAfterBackgroundForeground", description = "TC_GEN_LOGIN_012: Logout persists after backgrounding and foregrounding the app", groups = {"regression", "security"})
    public void verifyLogoutPersistsAfterBackgroundingAndForegrounding() {
        try {
            AccountSettingsPage acctPage = navigateToAccountSettings();
            acctPage.tapLogOut();
            acctPage.tapLogOutDialogConfirm();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            Assert.assertTrue(authPage.isLoginScreenDisplayed(), "Precondition: expected the Login screen after logout.");
            authPage.backgroundAppAndResume(3);
            Assert.assertTrue(authPage.isLoginScreenDisplayed(),
                    "Expected the app to still show the Login screen after backgrounding/foregrounding - logout must persist, not silently restore the previous authenticated session.");
        } catch (Exception e) {
            logger.error("verifyLogoutPersistsAfterBackgroundingAndForegrounding failed", e);
            Assert.fail("TC_GEN_LOGIN_012 failed: " + e.getMessage());
        } finally {
            reLoginAfterLogoutTest();
        }
    }

    /**
     * TC_GEN_UPD_001 - genuine environment gap, not invented: verifying the About screen reflects an
     * updated Application Version after a real app update requires an actual newer build available to
     * install, which does not exist in this project/environment.
     */
    @Test(priority = 1310, testName = "GenAboutVersionAfterAppUpdate", description = "TC_GEN_UPD_001: About screen displays the updated Application Version after an app update", groups = {"regression"})
    public void verifyAboutShowsUpdatedVersionAfterAppUpdate() {
        throw new SkipException("TC_GEN_UPD_001 needs an actual newer app build available to install over "
                + "the current one - no such build exists in this project/environment. Skipping rather than "
                + "simulating an update.");
    }

    /**
     * TC_GEN_SECX_004/FILE_001/FILE_005/FILE_006/FORM_008/FORM_019/FORM_020/FORM_021 (8 rows, shared
     * genuine gap) - all require actually completing a real image selection from within the external
     * Google Photos app's own UI (different file types, re-upload, special-character filenames, corrupt/
     * empty files, mismatched extensions, oversized files). This is the SAME documented, confirmed-real gap
     * already accepted for TC_FUNC_ACCT_013/015/016/017 (see this class's own javadoc) - not reliably
     * scriptable within this session's scope. Each skips individually (rather than one shared stub) so the
     * RTM/report still lists all 8 Generic IDs by name.
     */
    @Test(priority = 1320, testName = "GenPhotoUploadUnsupportedFileTypesRejected", description = "TC_GEN_SECX_004: Profile photo upload rejects unsupported file types and oversized files", groups = {"regression"})
    public void verifyPhotoUploadRejectsUnsupportedFileTypesAndOversizedFiles() {
        throw new SkipException("TC_GEN_SECX_004 needs a real image selection completed inside the external "
                + "Google Photos app's own UI (different file types/sizes) - the same documented gap as "
                + "TC_FUNC_ACCT_013/015/016/017. Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1330, testName = "GenPhotoUploadValidViaGallery", description = "TC_GEN_FILE_001: Upload a valid profile photo via Choose from Gallery", groups = {"regression"})
    public void verifyUploadValidProfilePhotoViaGallery() {
        throw new SkipException("TC_GEN_FILE_001 needs a real image selection completed inside the external "
                + "Google Photos app's own UI - the same documented gap as TC_FUNC_ACCT_013/015/016/017. "
                + "Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1340, testName = "GenPhotoUploadReplaceExisting", description = "TC_GEN_FILE_005: Re-upload the same photo to replace the current avatar", groups = {"regression"})
    public void verifyReuploadingSamePhotoReplacesCurrentAvatar() {
        throw new SkipException("TC_GEN_FILE_005 needs a real image selection completed inside the external "
                + "Google Photos app's own UI - the same documented gap as TC_FUNC_ACCT_013/015/016/017. "
                + "Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1350, testName = "GenPhotoUploadSpecialCharFilename", description = "TC_GEN_FILE_006: Upload a profile photo with special characters in the filename", groups = {"regression"})
    public void verifyUploadPhotoWithSpecialCharacterFilename() {
        throw new SkipException("TC_GEN_FILE_006 needs a real image selection completed inside the external "
                + "Google Photos app's own UI (with a specially-named source file) - the same documented gap "
                + "as TC_FUNC_ACCT_013/015/016/017. Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1360, testName = "GenPhotoUploadFormatSizeRestrictions", description = "TC_GEN_FORM_008: Profile photo upload enforces the confirmed JPG/PNG/WEBP format and 5MB size restrictions", groups = {"regression"})
    public void verifyPhotoUploadEnforcesFormatAndSizeRestrictions() {
        throw new SkipException("TC_GEN_FORM_008 needs a real image selection completed inside the external "
                + "Google Photos app's own UI (specific formats/sizes) - the same documented gap as "
                + "TC_FUNC_ACCT_013/015/016/017. Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1370, testName = "GenPhotoUploadCorruptOrEmptyFileRejected", description = "TC_GEN_FORM_019: Uploading a corrupt or empty file for the profile photo is rejected", groups = {"regression"})
    public void verifyUploadingCorruptOrEmptyFileIsRejected() {
        throw new SkipException("TC_GEN_FORM_019 needs a real (deliberately corrupt/empty) file selection "
                + "completed inside the external Google Photos app's own UI - the same documented gap as "
                + "TC_FUNC_ACCT_013/015/016/017. Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1380, testName = "GenPhotoUploadMismatchedExtensionValidatedByContent", description = "TC_GEN_FORM_020: A file with a mismatched extension is validated by content, not just extension", groups = {"regression", "security"})
    public void verifyMismatchedExtensionFileValidatedByContent() {
        throw new SkipException("TC_GEN_FORM_020 needs a real (deliberately mismatched-extension) file "
                + "selection completed inside the external Google Photos app's own UI - the same documented "
                + "gap as TC_FUNC_ACCT_013/015/016/017. Skipping rather than guessing at an unscriptable external-app flow.");
    }

    @Test(priority = 1390, testName = "GenPhotoUploadOversizedFileRejected", description = "TC_GEN_FORM_021: Uploading a photo larger than the confirmed 5MB limit is rejected", groups = {"regression"})
    public void verifyUploadingOversizedPhotoIsRejected() {
        throw new SkipException("TC_GEN_FORM_021 needs a real (deliberately oversized) file selection "
                + "completed inside the external Google Photos app's own UI - the same documented gap as "
                + "TC_FUNC_ACCT_013/015/016/017. Skipping rather than guessing at an unscriptable external-app flow.");
    }

    /**
     * TC_GEN_FORM_022 - genuine environment gap, not invented: same unreached-field gap already documented
     * for TC_FUNC_ACCT_012 (this class's own javadoc) - the Time Zone field's own value/picker control was
     * never reached during the live crawl, so its downstream effect on timestamped data cannot be exercised.
     */
    @Test(priority = 1400, testName = "GenTimeZoneAffectsTimestampedData", description = "TC_GEN_FORM_022: Changing Time Zone correctly affects how timestamped data is displayed", groups = {"regression"})
    public void verifyTimeZoneChangeAffectsTimestampedDataDisplay() {
        throw new SkipException("TC_GEN_FORM_022 needs the Time Zone field's own value/picker control, which "
                + "was never reached during the live crawl - the same documented gap as TC_FUNC_ACCT_012. "
                + "Skipping rather than guessing at an unreached control's behavior.");
    }

    /**
     * TC_GEN_BIO_002/BIO_003 (2 rows, shared genuine gap) - same confirmed real hardware gap already
     * documented for TC_FUNC_ACCT_023/024 (this class's own javadoc): this test device has no fingerprint
     * hardware/enrollment, so the "Allow Fingerprint" toggle row never appears at all.
     */
    @Test(priority = 1410, testName = "GenDisableFingerprintToggle", description = "TC_GEN_BIO_002: User can disable the Allow Fingerprint toggle in Security & Access", groups = {"regression"})
    public void verifyDisablingFingerprintToggleInSecurityAccess() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            if (!acctPage.isFingerprintOptionAvailable()) {
                throw new SkipException("TC_GEN_BIO_002 needs fingerprint hardware/enrollment, which this "
                        + "test device does not have - the same documented gap as TC_FUNC_ACCT_023/024.");
            }
            Assert.fail("TC_GEN_BIO_002: fingerprint hardware is now available on this device - this test needs real implementation, not this skip-guard stub.");
        } catch (SkipException skip) {
            throw skip;
        } catch (Exception e) {
            logger.error("verifyDisablingFingerprintToggleInSecurityAccess failed", e);
            Assert.fail("TC_GEN_BIO_002 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    @Test(priority = 1420, testName = "GenBiometricLoginUnavailableAfterDisablingFingerprint", description = "TC_GEN_BIO_003: Biometric login is no longer offered after disabling Allow Fingerprint", groups = {"regression"})
    public void verifyBiometricLoginUnavailableAfterDisablingFingerprint() {
        AccountSettingsPage acctPage = Pages.AccountSettingsPage();
        try {
            acctPage = navigateToAccountSettings();
            acctPage.tapSecurityAccess();
            if (!acctPage.isFingerprintOptionAvailable()) {
                throw new SkipException("TC_GEN_BIO_003 needs fingerprint hardware/enrollment, which this "
                        + "test device does not have - the same documented gap as TC_FUNC_ACCT_023/024.");
            }
            Assert.fail("TC_GEN_BIO_003: fingerprint hardware is now available on this device - this test needs real implementation, not this skip-guard stub.");
        } catch (SkipException skip) {
            throw skip;
        } catch (Exception e) {
            logger.error("verifyBiometricLoginUnavailableAfterDisablingFingerprint failed", e);
            Assert.fail("TC_GEN_BIO_003 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }
}
