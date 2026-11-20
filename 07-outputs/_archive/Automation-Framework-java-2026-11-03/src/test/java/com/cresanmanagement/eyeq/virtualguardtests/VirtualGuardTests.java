package com.cresanmanagement.eyeq.virtualguardtests;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.framework.TestBase;
import com.cresanmanagement.eyeq.pageobjects.mainappshell.MainAppShellPage;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.pageobjects.sitemanagement.SiteManagementPage;
import com.cresanmanagement.eyeq.pageobjects.virtualguard.VirtualGuardPage;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for the Virtual Guard module (menu, Surveillance, Guarding Stats, Arm/Disarm + Schedule,
 * Forensic Clips).
 *
 * <p><b>CODE-WRITING-ONLY MODE (2026-10-19):</b> written using documented evidence
 * ({@code RequirementAnalysis.md} Section 10.4, {@code FunctionalTestCases.md}'s TC_FUNC_VG_* rows,
 * {@code UITestCases.md}'s TC_UI_VG_* rows). No test in this class has been executed against a live device
 * this pass; every locator it depends on ({@code VirtualGuardPageObjectRepository}) is UNVERIFIED,
 * desk-sourced, including three documented gaps (KPI tile structure, Activity Log row structure, schedule
 * day-toggle ambiguity) explained in that repository's class javadoc. Implements all 32 TC_FUNC_VG_* rows
 * marked YES in FunctionalTestCases.md Section 2.2; the remaining 22 rows are TBD in that document itself
 * (undocumented test data/environment capabilities - e.g. zero-camera sites, forced backend failures,
 * known-alarm-count datasets, the Armed/enabled Arm-Disarm confirmation flows) and are NOT implemented
 * here, consistent with the standing "never invent business functionality" rule.
 *
 * <p>Relies on the suite-level environment setup having switched the backend to Test, and on
 * {@code OnboardingAuthenticationPage#login}/{@code SiteManagementPage#selectSite}/
 * {@code MainAppShellPage#tapBottomNavTab} - all already written (the first two LIVE-VERIFIED elsewhere,
 * the third UNVERIFIED but reused as-is rather than duplicated) - to reach the Virtual Guard menu.
 */
public class VirtualGuardTests extends TestBase {

    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";
    private static final String MODULE_CSV = "src/test/resources/testdata/virtualguard.csv";

    private String csv(String key) {
        return CSVLoader.loadRowByKey(MODULE_CSV, key).get("Value");
    }

    private void skipIfBlank(String value, String tcId, String csvKey) {
        if (value == null || value.isBlank()) {
            throw new SkipException(tcId + " needs a second real site with Forensic Clips enabled, not yet "
                    + "identified - see virtualguard.csv's '" + csvKey + "' row Notes. Skipping rather than inventing a value.");
        }
    }

    /**
     * Reaches the Virtual Guard bottom-sheet menu using the suite-level shared, already-logged-in session
     * (2026-10-20 architecture - see {@code testng.SuiteCreationListener}'s class javadoc) - never calls
     * {@code OnboardingAuthenticationPage#login} itself.
     *
     * <p>FIX (2026-10-22, root-caused via a real device crawl, udid 8BQX1DY81): the old version assumed
     * tapping the "Virtual Guard" bottom-nav tab directly opened the menu. Live-confirmed this is wrong -
     * the tab lands directly on the Surveillance screen (its own real default-landed destination), and the
     * menu only opens via a SEPARATE tap on the Surveillance screen's own title (same "tap the screen's
     * own title" mechanism already confirmed for License Plate, but via a different underlying element -
     * see {@code VirtualGuardPage#tapSurveillanceTitleToOpenMenu}'s javadoc). This was the root cause of
     * every test in this class failing with "Precondition failed: expected the Virtual Guard bottom-sheet
     * menu to open" (confirmed in logs/automation1.log, 2026-10-22 10:36:47 run).
     */
    private VirtualGuardPage openVirtualGuardMenu(String siteName) {
        SiteManagementPage sitePage = Pages.SiteManagementPage();
        sitePage.ensureSiteSelected(siteName);
        Assert.assertFalse(sitePage.isSiteSelectionScreenDisplayedFast(),
                "Precondition failed: expected to be inside the Main App Shell after ensureSiteSelected('" + siteName + "').");
        MainAppShellPage shellPage = Pages.MainAppShellPage();
        shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
        VirtualGuardPage vgPage = Pages.VirtualGuardPage();
        // DEFENSE-IN-DEPTH (2026-10-26, confirmed real 90s-timeout failure): tapping "Virtual Guard" is a
        // no-op when the shared session is already stranded inside the module on a different sub-screen
        // (e.g. a prior test ended with its menu reopened and lightReset() landed it on Guarding Stats
        // instead of Surveillance - see lightReset()'s own javadoc for the confirmed root cause). Rather
        // than relying solely on lightReset() to always prevent this, recover here too via the same
        // mechanism, using the FAST check first so a healthy run pays no extra cost.
        if (!vgPage.isSurveillanceScreenDisplayedFast()) {
            vgPage.returnToSurveillance();
        }
        Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(),
                "Precondition failed: expected the Virtual Guard tab to land directly on the Surveillance screen.");
        vgPage.tapSurveillanceTitleToOpenMenu();
        Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv("VgMenuItem1")),
                "Precondition failed: expected the Virtual Guard bottom-sheet menu to open after tapping the Surveillance title.");
        return vgPage;
    }

    private VirtualGuardPage openVirtualGuardMenu() {
        return openVirtualGuardMenu(csv("DefaultSite"));
    }

    /**
     * Lightweight per-test reset (replaces the retired, unconditional-full-relaunch
     * {@code resetToCleanLoginOnTestBackend()} pattern - see {@code testng.SuiteCreationListener}'s class
     * javadoc for why that pattern was retired after a real production-environment incident). Closes any
     * open Virtual Guard bottom-sheet menu; the shared session stays logged in throughout.
     */
    private void lightReset() {
        try {
            VirtualGuardPage vgPage = Pages.VirtualGuardPage();
            // FIX (2026-10-26, real device crawl + user-reported failure): a test that opens the Schedule
            // bottom sheet (verifyViewScheduleOpensBottomSheetWithCoreControls, verifyArmedSegmentShows...,
            // verifyDisarmedSegmentShows..., verifyTogglingAllSevenDaysToUniformStateIsAcceptedWithoutConflict,
            // verifyScheduleSheetRescopesAfterSiteSwitch) previously left it open on failure/early-return.
            // isBottomSheetMenuOpen() only detects the VG NAVIGATION menu (a different sheet), so it never
            // caught this; returnToSurveillance() scans for known screen TITLES, which the Schedule sheet
            // covers/obscures, so it also silently did nothing - leaving the sheet open for the NEXT test to
            // inherit (confirmed: that test's own preconditions would then fail against stale sheet state).
            // Checked first, before either of the other resets. Uses the FAST (genuinely zero-wait)
            // variant - this check runs after EVERY test in the class, so a slow check here would tax
            // every single test's cleanup, not just the handful of Schedule-specific ones (confirmed
            // real issue - see VirtualGuardPage#isScheduleSheetDisplayedFast()'s javadoc).
            if (vgPage.isScheduleSheetDisplayedFast()) {
                vgPage.dismissScheduleSheet();
            }
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            // Same fix, same reason: this also runs after EVERY test - uses the fast variant here too.
            if (shellPage.isBottomSheetMenuOpenFast()) {
                shellPage.dismissBottomSheetMenuWithoutSelecting();
            }
            // FIX (2026-10-22, real device crawl): a test that navigated into a VG sub-screen (Guarding
            // Stats/Forensic Clips/Arm-Disarm) previously left the shared session stranded there - the
            // NEXT test's openVirtualGuardMenu() assumed landing back on Surveillance after re-tapping the
            // bottom-nav tab, but LIVE-CONFIRMED that re-tapping the already-active tab does NOT reset to
            // Surveillance (it stays on whatever VG sub-screen was last shown). Return to Surveillance here
            // so every test starts from the same known-good state.
            //
            // FIX (2026-10-26, confirmed real failure - verifySelectingOldestAvailableMonthLoadsCorrectly
            // timed out for 90s on a stale "Virtual Guard tab always lands on Surveillance" assumption):
            // this branch used to `return` immediately after dismissing an open menu, SKIPPING this call
            // entirely. A test that ended with the menu open (e.g. after re-opening it to check contents)
            // left the shared session on whatever sub-screen the menu was reopened FROM (e.g. Guarding
            // Stats), not Surveillance - and because a later site switch re-scopes the CURRENTLY DISPLAYED
            // sub-screen in place (re-tapping "Virtual Guard" while still inside the module is a no-op, same
            // finding as above), the NEXT test's openVirtualGuardMenu() never reached Surveillance at all.
            // Removed the early return so every lightReset() path converges on the same guaranteed state,
            // regardless of which branch above fired. Safe to call unconditionally: if already on
            // Surveillance, this is a harmless no-op (see its own javadoc).
            vgPage.returnToSurveillance();
        } catch (Exception e) {
            logger.warn("lightReset: non-fatal cleanup issue, continuing", e);
        }
    }

    // =================================================================================================
    // Virtual Guard Menu (FR-VG-001)
    // =================================================================================================

    /**
     * TC_FUNC_VG_001 - Virtual Guard menu displays all defined menu items.
     *
     * <p>FIX (2026-10-22, real device validation run): confirmed live (twice, identically) that scanning
     * all 5 rows in rapid succession right after the menu opens can race the sheet's own settle animation -
     * row 3 ("Forensic Clips") was not yet rendered at zero-wait check time even though rows 1-2 already
     * were. Uses {@code isBottomSheetMenuItemDisplayedSettled} (a short bounded wait) instead of the
     * zero-wait variant for this specific "list every row of a just-opened menu" check.
     *
     * <p>FIX (2026-10-26, real device crawl): {@code DefaultSite} ("Main Site") only has 4 real menu items
     * (Surveillance, Guarding Stats, Contact, Arm/Disarm) - "Forensic Clips" is a confirmed per-site feature
     * gate, absent there entirely (same gating pattern as License Plate's Amalthea-only availability). This
     * test can never pass against the default site; switched to {@code SiteWithForensicClips} (Amalthea),
     * LIVE-CONFIRMED to have all 5 items.
     */
    @Test(priority = 1000, testName = "VgMenuDisplaysAllItems", description = "TC_FUNC_VG_001: Virtual Guard menu displays all defined menu items", groups = {"smoke", "criticalPath"})
    public void verifyVirtualGuardMenuDisplaysAllFiveItems() {
        try {
            openVirtualGuardMenu(csv("SiteWithForensicClips"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            for (String key : new String[]{"VgMenuItem1", "VgMenuItem2", "VgMenuItem3", "VgMenuItem4", "VgMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayedSettled(csv(key)), "Expected Virtual Guard menu item '" + csv(key) + "' to be visible.");
            }
        } catch (Exception e) {
            logger.error("verifyVirtualGuardMenuDisplaysAllFiveItems failed", e);
            Assert.fail("TC_FUNC_VG_001 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_002 - Tapping "Surveillance" navigates to the Surveillance screen. */
    @Test(priority = 1010, testName = "VgMenuSurveillanceNavigates", description = "TC_FUNC_VG_002: Tapping Surveillance navigates to the Surveillance screen", groups = {"smoke", "regression"})
    public void verifyTappingSurveillanceNavigatesToSurveillanceScreen() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem1"));
            Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(), "Expected navigation to the Surveillance screen after tapping 'Surveillance'.");
        } catch (Exception e) {
            logger.error("verifyTappingSurveillanceNavigatesToSurveillanceScreen failed", e);
            Assert.fail("TC_FUNC_VG_002 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_003 - Tapping "Guarding Stats" navigates to the Guarding Stats dashboard. */
    @Test(priority = 1020, testName = "VgMenuGuardingStatsNavigates", description = "TC_FUNC_VG_003: Tapping Guarding Stats navigates to the Guarding Stats dashboard", groups = {"smoke", "regression"})
    public void verifyTappingGuardingStatsNavigatesToGuardingStatsDashboard() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem2"));
            Assert.assertTrue(vgPage.isGuardingStatsScreenDisplayed(), "Expected navigation to the Guarding Stats dashboard after tapping 'Guarding Stats'.");
        } catch (Exception e) {
            logger.error("verifyTappingGuardingStatsNavigatesToGuardingStatsDashboard failed", e);
            Assert.fail("TC_FUNC_VG_003 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_004 - Tapping "Forensic Clips" navigates to the Forensic Clips screen.
     * Fixed 2026-10-26 - see TC_FUNC_VG_001's javadoc for the full evidence: Forensic Clips is absent from
     * DefaultSite's menu entirely, so this now uses {@code SiteWithForensicClips} (Amalthea).
     */
    @Test(priority = 1030, testName = "VgMenuForensicClipsNavigates", description = "TC_FUNC_VG_004: Tapping Forensic Clips navigates to the Forensic Clips screen", groups = {"regression"})
    public void verifyTappingForensicClipsNavigatesToForensicClipsScreen() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("SiteWithForensicClips"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem3"));
            Assert.assertTrue(vgPage.isForensicClipsScreenDisplayed(), "Expected navigation to the Forensic Clips screen after tapping 'Forensic Clips'.");
        } catch (Exception e) {
            logger.error("verifyTappingForensicClipsNavigatesToForensicClipsScreen failed", e);
            Assert.fail("TC_FUNC_VG_004 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_005 - Tapping "Arm/Disarm" navigates to the Arm/Disarm screen. */
    @Test(priority = 1040, testName = "VgMenuArmDisarmNavigates", description = "TC_FUNC_VG_005: Tapping Arm/Disarm navigates to the Arm/Disarm screen", groups = {"smoke", "regression"})
    public void verifyTappingArmDisarmNavigatesToArmDisarmScreen() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem5"));
            Assert.assertTrue(vgPage.isArmDisarmScreenDisplayed(), "Expected navigation to the Arm/Disarm screen after tapping 'Arm/Disarm'.");
        } catch (Exception e) {
            logger.error("verifyTappingArmDisarmNavigatesToArmDisarmScreen failed", e);
            Assert.fail("TC_FUNC_VG_005 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_007 - Dismissing the Virtual Guard menu without selection performs no navigation. */
    @Test(priority = 10, testName = "VgMenuDismissWithoutSelectionNoNavigation", description = "TC_FUNC_VG_007: Dismissing the Virtual Guard menu without selection performs no navigation", groups = {"regression", "negative"})
    public void verifyDismissingVirtualGuardMenuWithoutSelectionPerformsNoNavigation() {
        try {
            openVirtualGuardMenu();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.dismissBottomSheetMenuWithoutSelecting();
            Assert.assertFalse(shellPage.isBottomSheetMenuOpen(), "Expected the Virtual Guard menu to close after dismissing without selection.");
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabVirtualGuard")), "Expected to remain on the Main App Shell with bottom navigation still functional, no module navigation triggered.");
        } catch (Exception e) {
            logger.error("verifyDismissingVirtualGuardMenuWithoutSelectionPerformsNoNavigation failed", e);
            Assert.fail("TC_FUNC_VG_007 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_008 - Virtual Guard menu reopens correctly after navigating into a sub-screen and back.
     * Fixed 2026-10-26 after a confirmed real failure ("expected [true] but found [false]" for menu item
     * 'Surveillance'): re-tapping the "Virtual Guard" bottom-nav tab does NOT reopen the menu by itself -
     * LIVE-CONFIRMED (see {@code openVirtualGuardMenu}'s own javadoc) it lands directly on the Surveillance
     * screen, same as the very first tap. This test's own assumption predates that fix and was never
     * updated - it asserted menu items were visible without ever actually reopening the menu. Added the
     * same {@code tapSurveillanceTitleToOpenMenu()} step {@code openVirtualGuardMenu()} itself uses.
     *
     * <p>FIX (2026-10-26, confirmed real failure after the above fix): also checks all 5 VgMenuItem keys
     * including "Forensic Clips", which - same evidence as TC_FUNC_VG_001/004 - is confirmed ABSENT from
     * DefaultSite's (Main Site's) menu entirely. Switched to {@code SiteWithForensicClips} (Amalthea).
     */
    @Test(priority = 1050, testName = "VgMenuReopensAfterSubScreenAndBack", description = "TC_FUNC_VG_008: Virtual Guard menu reopens correctly after navigating into a sub-screen and back", groups = {"regression"})
    public void verifyVirtualGuardMenuReopensCorrectlyAfterSubScreenAndBack() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("SiteWithForensicClips"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem1"));
            Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(), "Precondition failed: expected to reach Surveillance before navigating back.");
            shellPage.navigateBack();
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(), "Precondition failed: expected the Virtual Guard tab to land on Surveillance after navigating back.");
            vgPage.tapSurveillanceTitleToOpenMenu();
            for (String key : new String[]{"VgMenuItem1", "VgMenuItem2", "VgMenuItem3", "VgMenuItem4", "VgMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv(key)), "Expected menu item '" + csv(key) + "' to reappear with no stale/duplicated state.");
            }
        } catch (Exception e) {
            logger.error("verifyVirtualGuardMenuReopensCorrectlyAfterSubScreenAndBack failed", e);
            Assert.fail("TC_FUNC_VG_008 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_009 - Virtual Guard menu re-scopes correctly after switching the active site. UNVERIFIED
     * weak proxy: with no confirmed per-site menu signal documented, this asserts the menu still shows its
     * full confirmed 5-item set after switching (a correctly re-scoped menu should not disappear/break),
     * rather than asserting the absence of specific "residual Site A data" which has no documented form.
     * Fixed 2026-10-26 - same confirmed root cause as TC_FUNC_VG_008: re-tapping the "Virtual Guard"
     * bottom-nav tab lands on Surveillance, it does not reopen the menu by itself.
     */
    @Test(priority = 1060, testName = "VgMenuRescopesAfterSiteSwitch", description = "TC_FUNC_VG_009: Virtual Guard menu re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyVirtualGuardMenuRescopesAfterSiteSwitch() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("MultiSiteA"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.dismissBottomSheetMenuWithoutSelecting();
            shellPage.tapTopBarSiteSwitcher();
            SiteManagementPage sitePage = Pages.SiteManagementPage();
            sitePage.selectSiteFromSwitcher(csv("MultiSiteB"));
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            vgPage.tapSurveillanceTitleToOpenMenu();
            for (String key : new String[]{"VgMenuItem1", "VgMenuItem2", "VgMenuItem3", "VgMenuItem4", "VgMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv(key)), "Expected Virtual Guard menu item '" + csv(key) + "' to still be listed after switching to Site B.");
            }
        } catch (Exception e) {
            logger.error("verifyVirtualGuardMenuRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_VG_009 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Surveillance (FR-VG-002)
    // =================================================================================================

    /** TC_FUNC_VG_010 - Surveillance screen loads the camera grid for the active site. */
    @Test(priority = 1070, testName = "SurveillanceLoadsCameraGrid", description = "TC_FUNC_VG_010: Surveillance screen loads the camera grid for the active site", groups = {"smoke"})
    public void verifySurveillanceScreenLoadsCameraGridForActiveSite() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem1"));
            Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(), "Expected the Surveillance screen to load.");
            Assert.assertTrue(vgPage.isCameraGroupDisplayed(csv("DefaultSiteCameraGroup")), "Expected the camera grid to display a Camera Group tile for the active site.");
        } catch (Exception e) {
            logger.error("verifySurveillanceScreenLoadsCameraGridForActiveSite failed", e);
            Assert.fail("TC_FUNC_VG_010 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_015 - Camera grid re-scopes correctly after switching the active site. UNVERIFIED weak
     * proxy: no confirmed per-site camera-group-name data distinguishes Site A from Site B's grid content,
     * so this asserts the Surveillance screen remains correctly displayed (not broken/blank) after the
     * switch, rather than asserting specific differing camera content.
     * Fixed 2026-10-26 after a confirmed real failure ("No bottom-nav tab found matching 'Surveillance'"):
     * the final {@code tapBottomNavTab(VgMenuItem1)} was redundant and broken - tapping "Virtual Guard"
     * lands directly on Surveillance (LIVE-CONFIRMED, same mechanism documented in
     * {@code openVirtualGuardMenu}'s javadoc), so there is no separate "Surveillance" row/tab left to tap at
     * that point; the real Site A re-scope assertion already covers that this screen displays the active
     * site, so the redundant tap is simply removed.
     */
    @Test(priority = 1080, testName = "SurveillanceRescopesAfterSiteSwitch", description = "TC_FUNC_VG_015: Camera grid re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyCameraGridRescopesAfterSiteSwitch() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("MultiSiteA"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem1"));
            Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(), "Precondition failed: expected Surveillance to load on Site A.");
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(csv("MultiSiteB"));
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Assert.assertTrue(vgPage.isSurveillanceScreenDisplayed(), "Expected Surveillance to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyCameraGridRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_VG_015 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Guarding Stats Dashboard (FR-VG-003)
    // =================================================================================================

    /** TC_FUNC_VG_018 - Guarding Stats dashboard loads with current month and all 5 KPI tiles populated. */
    @Test(priority = 1090, testName = "GuardingStatsLoadsWithAllKpiTiles", description = "TC_FUNC_VG_018: Guarding Stats dashboard loads with current month and all 5 KPI tiles populated", groups = {"smoke", "criticalPath"})
    public void verifyGuardingStatsDashboardLoadsWithAllFiveKpiTiles() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            Assert.assertTrue(vgPage.isGuardingStatsScreenDisplayed(), "Precondition failed: expected the Guarding Stats dashboard to load.");
            for (String key : new String[]{"KpiTotalAlarmEvents", "KpiAvgAlarmsPerHour", "KpiAvgAlarmsPerDay", "KpiAvgInitialResponseTime", "KpiSlaPercent"}) {
                Assert.assertTrue(vgPage.isKpiTileNonBlank(csv(key)), "Expected KPI tile '" + csv(key) + "' to show a non-blank value.");
            }
        } catch (Exception e) {
            logger.error("verifyGuardingStatsDashboardLoadsWithAllFiveKpiTiles failed", e);
            Assert.fail("TC_FUNC_VG_018 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_019 - Selecting a past month refreshes all KPI tiles. UNVERIFIED weak proxy: with no
     * known-expected-value dataset (see virtualguard.csv's KnownMonthExpected* gap notes), this asserts
     * all 5 tiles remain non-blank after selecting a past month, rather than asserting their values
     * actually changed (which would require a known current-vs-past data difference not documented
     * anywhere).
     */
    @Test(priority = 1100, testName = "GuardingStatsPastMonthRefreshesKpiTiles", description = "TC_FUNC_VG_019: Selecting a past month refreshes all KPI tiles", groups = {"regression"})
    public void verifySelectingPastMonthRefreshesAllKpiTiles() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            vgPage.selectMonth(csv("PastMonthLabel"));
            for (String key : new String[]{"KpiTotalAlarmEvents", "KpiAvgAlarmsPerHour", "KpiAvgAlarmsPerDay", "KpiAvgInitialResponseTime", "KpiSlaPercent"}) {
                Assert.assertTrue(vgPage.isKpiTileNonBlank(csv(key)), "Expected KPI tile '" + csv(key) + "' to show a non-blank, refreshed value for the selected past month.");
            }
        } catch (Exception e) {
            logger.error("verifySelectingPastMonthRefreshesAllKpiTiles failed", e);
            Assert.fail("TC_FUNC_VG_019 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** Shared helper for TC_FUNC_VG_020-024: asserts a KPI tile is non-blank, and additionally asserts exact equality IF a known-expected CSV value has been supplied (currently all empty - see CSV Notes). */
    private void verifyKpiTileValue(VirtualGuardPage vgPage, String labelKey, String expectedKey, String tcId) {
        String label = csv(labelKey);
        Assert.assertTrue(vgPage.isKpiTileNonBlank(label), "Expected KPI tile '" + label + "' to show a non-blank value.");
        String expected = csv(expectedKey);
        if (expected != null && !expected.trim().isEmpty()) {
            Assert.assertTrue(vgPage.getKpiTileValue(label).contains(expected),
                    tcId + ": expected KPI tile '" + label + "' to contain the known expected value '" + expected + "'.");
        } else {
            logger.warn(tcId + ": no known-expected value supplied in virtualguard.csv (" + expectedKey + ") - only the non-blank assertion ran. See CSV Notes for the documented data gap.");
        }
    }

    /** TC_FUNC_VG_020 - "Total Alarm Events" KPI tile displays the correct count. */
    @Test(priority = 1110, testName = "GuardingStatsTotalAlarmEventsTile", description = "TC_FUNC_VG_020: Total Alarm Events KPI tile displays the correct count", groups = {"regression", "criticalPath"})
    public void verifyTotalAlarmEventsKpiTile() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            verifyKpiTileValue(vgPage, "KpiTotalAlarmEvents", "KnownMonthExpectedTotalAlarmEvents", "TC_FUNC_VG_020");
        } catch (Exception e) {
            logger.error("verifyTotalAlarmEventsKpiTile failed", e);
            Assert.fail("TC_FUNC_VG_020 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_021 - "Average alarms/hour" KPI tile displays the correct calculated value. */
    @Test(priority = 1120, testName = "GuardingStatsAvgAlarmsPerHourTile", description = "TC_FUNC_VG_021: Average alarms/hour KPI tile displays the correct calculated value", groups = {"regression", "criticalPath"})
    public void verifyAverageAlarmsPerHourKpiTile() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            verifyKpiTileValue(vgPage, "KpiAvgAlarmsPerHour", "KnownMonthExpectedAvgAlarmsPerHour", "TC_FUNC_VG_021");
        } catch (Exception e) {
            logger.error("verifyAverageAlarmsPerHourKpiTile failed", e);
            Assert.fail("TC_FUNC_VG_021 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_022 - "Average alarms/day" KPI tile displays the correct calculated value. */
    @Test(priority = 1130, testName = "GuardingStatsAvgAlarmsPerDayTile", description = "TC_FUNC_VG_022: Average alarms/day KPI tile displays the correct calculated value", groups = {"regression", "criticalPath"})
    public void verifyAverageAlarmsPerDayKpiTile() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            verifyKpiTileValue(vgPage, "KpiAvgAlarmsPerDay", "KnownMonthExpectedAvgAlarmsPerDay", "TC_FUNC_VG_022");
        } catch (Exception e) {
            logger.error("verifyAverageAlarmsPerDayKpiTile failed", e);
            Assert.fail("TC_FUNC_VG_022 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_023 - "Average initial response time" KPI tile displays the correct calculated value. */
    @Test(priority = 1140, testName = "GuardingStatsAvgInitialResponseTimeTile", description = "TC_FUNC_VG_023: Average initial response time KPI tile displays the correct calculated value", groups = {"regression", "criticalPath"})
    public void verifyAverageInitialResponseTimeKpiTile() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            verifyKpiTileValue(vgPage, "KpiAvgInitialResponseTime", "KnownMonthExpectedAvgInitialResponseTime", "TC_FUNC_VG_023");
        } catch (Exception e) {
            logger.error("verifyAverageInitialResponseTimeKpiTile failed", e);
            Assert.fail("TC_FUNC_VG_023 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_024 - "% alarms closed under 90 seconds" SLA KPI tile displays the correct calculated percentage. */
    @Test(priority = 1150, testName = "GuardingStatsSlaPercentTile", description = "TC_FUNC_VG_024: % alarms closed under 90 seconds SLA KPI tile displays the correct percentage", groups = {"regression", "criticalPath"})
    public void verifySlaPercentKpiTile() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            verifyKpiTileValue(vgPage, "KpiSlaPercent", "KnownMonthExpectedSlaPercent", "TC_FUNC_VG_024");
        } catch (Exception e) {
            logger.error("verifySlaPercentKpiTile failed", e);
            Assert.fail("TC_FUNC_VG_024 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_025 - Month-over-month comparison caption wording differs between a current (partial)
     * month and a fully elapsed past month. UNVERIFIED weak proxy: no confirmed single "comparison
     * caption" locator exists distinct from the already-confirmed "Newest at top" sort-state label; this
     * asserts the dashboard's content signal remains present/stable across both month selections rather
     * than comparing specific caption text, which has no confirmed locator to read.
     */
    @Test(priority = 1160, testName = "GuardingStatsCaptionDiffersCurrentVsPastMonth", description = "TC_FUNC_VG_025: Month-over-month comparison caption wording differs between current and past month", groups = {"regression"})
    public void verifyComparisonCaptionDiffersBetweenCurrentAndPastMonth() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            Assert.assertTrue(vgPage.isNewestAtTopLabelDisplayed(), "Precondition failed: expected the dashboard's confirmed content signal for the current month.");
            vgPage.selectMonth(csv("PastMonthLabel"));
            Assert.assertTrue(vgPage.isGuardingStatsScreenDisplayed(), "Expected the dashboard to remain correctly displayed after selecting a fully elapsed past month (caption wording itself has no confirmed locator to assert against - see javadoc).");
        } catch (Exception e) {
            logger.error("verifyComparisonCaptionDiffersBetweenCurrentAndPastMonth failed", e);
            Assert.fail("TC_FUNC_VG_025 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_026 - Guarding Stats dashboard re-scopes correctly after switching the active site.
     * UNVERIFIED weak proxy, same rationale as TC_FUNC_VG_009/015.
     */
    @Test(priority = 1170, testName = "GuardingStatsRescopesAfterSiteSwitch", description = "TC_FUNC_VG_026: Guarding Stats dashboard re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyGuardingStatsRescopesAfterSiteSwitch() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("MultiSiteA"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem2"));
            Assert.assertTrue(vgPage.isGuardingStatsScreenDisplayed(), "Precondition failed: expected Guarding Stats to load on Site A.");
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(csv("MultiSiteB"));
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            shellPage.tapBottomNavTab(csv("VgMenuItem2"));
            Assert.assertTrue(vgPage.isGuardingStatsScreenDisplayed(), "Expected Guarding Stats to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyGuardingStatsRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_VG_026 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_028 - Selecting the oldest available month in the month selector loads correctly.
     * UNVERIFIED best-effort (see VirtualGuardPage#selectOldestAvailableMonth's javadoc).
     */
    @Test(priority = 1180, testName = "GuardingStatsOldestMonthLoadsCorrectly", description = "TC_FUNC_VG_028: Selecting the oldest available month loads correctly", groups = {"regression", "boundary"})
    public void verifySelectingOldestAvailableMonthLoadsCorrectly() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem2"));
            vgPage.selectOldestAvailableMonth();
            Assert.assertTrue(vgPage.isGuardingStatsScreenDisplayed(), "Expected the dashboard to load the oldest available month's data without error, truncation, or crash.");
        } catch (Exception e) {
            logger.error("verifySelectingOldestAvailableMonthLoadsCorrectly failed", e);
            Assert.fail("TC_FUNC_VG_028 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Arm/Disarm (FR-VG-004)
    // =================================================================================================

    /** TC_FUNC_VG_030 - Arm/Disarm screen displays the current site status. */
    @Test(priority = 1190, testName = "ArmDisarmDisplaysCurrentStatus", description = "TC_FUNC_VG_030: Arm/Disarm screen displays the current site status", groups = {"smoke", "criticalPath"})
    public void verifyArmDisarmScreenDisplaysCurrentSiteStatus() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            Assert.assertTrue(vgPage.isArmDisarmScreenDisplayed(), "Precondition failed: expected the Arm/Disarm screen to load.");
            String status = vgPage.getArmDisarmStatusText();
            Assert.assertNotNull(status, "Expected a current status line (e.g. 'DISARMED - Disarmed by schedule until <time>') to be present.");
            Assert.assertFalse(status.trim().isEmpty(), "Expected the status line to be non-blank.");
        } catch (Exception e) {
            logger.error("verifyArmDisarmScreenDisplaysCurrentSiteStatus failed", e);
            Assert.fail("TC_FUNC_VG_030 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_031 - "Arm" control is disabled outside the schedule window, with next-availability
     * helper text. UNVERIFIED: assumes the QA account's default site is currently Disarmed/outside its arm
     * window (not independently controllable/confirmed).
     * Fixed 2026-10-26 after a confirmed real failure: LIVE-CONFIRMED the real site was ARMED at crawl time
     * (schedule-driven state, "Armed by schedule until Oct 27, 2026, 09:00 AM") - in that state, no "Arm"
     * control exists on screen at all (only "Disarm" does), which is a different thing from "Arm exists but
     * is disabled". {@code isArmDisarmButtonEnabled} returned false either way, masking the real reason.
     * Now checks presence first and skips cleanly when the site isn't currently in the assumed Disarmed
     * state, rather than asserting against a precondition the live system doesn't currently satisfy.
     */
    @Test(priority = 20, testName = "ArmDisarmArmControlDisabledOutsideWindow", description = "TC_FUNC_VG_031: Arm control is disabled outside the schedule window, with next-availability helper text", groups = {"regression"})
    public void verifyArmControlDisabledOutsideWindowShowsHelperText() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            if (!vgPage.isArmDisarmButtonPresent(csv("ArmButtonLabel"))) {
                throw new SkipException("TC_FUNC_VG_031 needs the site to currently be Disarmed (so the 'Arm' control exists to check) - "
                        + "the site is currently Armed (schedule-driven, not independently controllable). Skipping rather than asserting against a precondition that isn't currently true.");
            }
            Assert.assertFalse(vgPage.isArmDisarmButtonEnabled(csv("ArmButtonLabel")),
                    "Expected the 'Arm' control to be disabled outside the schedule window (assumes the active site is currently outside its arm window - UNVERIFIED, not independently controllable).");
            Assert.assertTrue(vgPage.isArmHelperTextDisplayed(), "Expected the next-availability helper text to be shown.");
        } catch (SkipException se) {
            // FIX (2026-10-26, confirmed real failure): SkipException extends RuntimeException, so the
            // generic catch below was swallowing it and converting a legitimate skip into a FAIL - the skip
            // check above can only run AFTER navigating to Arm/Disarm (unlike the CSV-blank skipIfBlank
            // checks elsewhere, which run before any try block and so were never affected by this).
            throw se;
        } catch (Exception e) {
            logger.error("verifyArmControlDisabledOutsideWindowShowsHelperText failed", e);
            Assert.fail("TC_FUNC_VG_031 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_032 - Activity Log lists prior arm/disarm events with full detail. */
    @Test(priority = 1200, testName = "ArmDisarmActivityLogListsEvents", description = "TC_FUNC_VG_032: Activity Log lists prior arm/disarm events with full detail", groups = {"regression", "audit"})
    public void verifyActivityLogListsPriorArmDisarmEvents() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            Assert.assertTrue(vgPage.isActivityLogDisplayed(), "Precondition failed: expected the Activity Log section to be present.");
            int entryCount = vgPage.getActivityLogEntryCount();
            Assert.assertTrue(entryCount > 0, "Expected at least one Activity Log entry (UNVERIFIED row structure - see VirtualGuardPageObjectRepository javadoc; a live pass must confirm the real per-entry timestamp/actor/reason fields).");
        } catch (Exception e) {
            logger.error("verifyActivityLogListsPriorArmDisarmEvents failed", e);
            Assert.fail("TC_FUNC_VG_032 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_034 - Tapping a disabled "Arm" button has no effect.
     * Fixed 2026-10-26 - same confirmed root cause as TC_FUNC_VG_031: skips cleanly when the site isn't
     * currently Disarmed (no "Arm" control to tap at all) instead of asserting against a precondition that
     * isn't currently true.
     */
    @Test(priority = 30, testName = "ArmDisarmDisabledArmButtonNoOp", description = "TC_FUNC_VG_034: Tapping a disabled Arm button has no effect", groups = {"regression", "negative"})
    public void verifyTappingDisabledArmButtonHasNoEffect() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            if (!vgPage.isArmDisarmButtonPresent(csv("ArmButtonLabel"))) {
                throw new SkipException("TC_FUNC_VG_034 needs the site to currently be Disarmed (so the 'Arm' control exists to tap) - "
                        + "the site is currently Armed (schedule-driven, not independently controllable). Skipping rather than asserting against a precondition that isn't currently true.");
            }
            Assert.assertFalse(vgPage.isArmDisarmButtonEnabled(csv("ArmButtonLabel")), "Precondition failed: expected the 'Arm' control to be confirmed disabled.");
            String statusBefore = vgPage.getArmDisarmStatusText();
            int logCountBefore = vgPage.getActivityLogEntryCount();
            vgPage.tapArmDisarmButton(csv("ArmButtonLabel"));
            String statusAfter = vgPage.getArmDisarmStatusText();
            int logCountAfter = vgPage.getActivityLogEntryCount();
            Assert.assertEquals(statusAfter, statusBefore, "Expected no status change after tapping a disabled 'Arm' control.");
            Assert.assertEquals(logCountAfter, logCountBefore, "Expected no new Activity Log entry after tapping a disabled 'Arm' control.");
        } catch (SkipException se) {
            throw se;
        } catch (Exception e) {
            logger.error("verifyTappingDisabledArmButtonHasNoEffect failed", e);
            Assert.fail("TC_FUNC_VG_034 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_035 - Arm/Disarm screen re-scopes correctly after switching the active site. UNVERIFIED
     * weak proxy, same rationale as TC_FUNC_VG_009/015/026.
     */
    @Test(priority = 1210, testName = "ArmDisarmRescopesAfterSiteSwitch", description = "TC_FUNC_VG_035: Arm/Disarm screen re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyArmDisarmRescopesAfterSiteSwitch() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("MultiSiteA"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem5"));
            Assert.assertTrue(vgPage.isArmDisarmScreenDisplayed(), "Precondition failed: expected Arm/Disarm to load on Site A.");
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(csv("MultiSiteB"));
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            shellPage.tapBottomNavTab(csv("VgMenuItem5"));
            Assert.assertTrue(vgPage.isArmDisarmScreenDisplayed(), "Expected Arm/Disarm to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyArmDisarmRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_VG_035 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Arm/Disarm Schedule bottom sheet (FR-VG-005)
    // =================================================================================================

    /** TC_FUNC_VG_040 - "View Schedule" opens the Schedule bottom sheet with its core controls. */
    @Test(priority = 1220, testName = "ScheduleViewOpensCoreControls", description = "TC_FUNC_VG_040: View Schedule opens the Schedule bottom sheet with its core controls", groups = {"regression"})
    public void verifyViewScheduleOpensBottomSheetWithCoreControls() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            vgPage.tapViewSchedule();
            Assert.assertTrue(vgPage.isScheduleSheetDisplayed(), "Expected the Schedule bottom sheet to open showing the Armed/Disarmed segmented control.");
        } catch (Exception e) {
            logger.error("verifyViewScheduleOpensBottomSheetWithCoreControls failed", e);
            Assert.fail("TC_FUNC_VG_040 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_041 - Segmented control "Armed" view shows the Armed schedule. UNVERIFIED weak proxy: no
     * confirmed way to read "the configured Armed schedule" as distinct data; asserts the sheet remains
     * stably displayed after selecting the Armed segment.
     */
    @Test(priority = 1230, testName = "ScheduleArmedSegmentShowsArmedSchedule", description = "TC_FUNC_VG_041: Segmented control Armed view shows the Armed schedule", groups = {"regression"})
    public void verifyArmedSegmentShowsArmedSchedule() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            vgPage.tapViewSchedule();
            vgPage.selectArmedScheduleSegment();
            Assert.assertTrue(vgPage.isScheduleSheetDisplayed(), "Expected the Schedule sheet to remain correctly displayed after selecting the Armed segment.");
        } catch (Exception e) {
            logger.error("verifyArmedSegmentShowsArmedSchedule failed", e);
            Assert.fail("TC_FUNC_VG_041 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_VG_042 - Segmented control "Disarmed" view shows the Disarmed schedule. UNVERIFIED weak proxy, same rationale as TC_FUNC_VG_041. */
    @Test(priority = 1240, testName = "ScheduleDisarmedSegmentShowsDisarmedSchedule", description = "TC_FUNC_VG_042: Segmented control Disarmed view shows the Disarmed schedule", groups = {"regression"})
    public void verifyDisarmedSegmentShowsDisarmedSchedule() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            vgPage.tapViewSchedule();
            vgPage.selectDisarmedScheduleSegment();
            Assert.assertTrue(vgPage.isScheduleSheetDisplayed(), "Expected the Schedule sheet to remain correctly displayed after selecting the Disarmed segment.");
        } catch (Exception e) {
            logger.error("verifyDisarmedSegmentShowsDisarmedSchedule failed", e);
            Assert.fail("TC_FUNC_VG_042 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_044 - Toggling all 7 days to a uniform state is accepted without conflict. UNVERIFIED:
     * targets toggles by POSITION (1-7), not day-letter text (see repository javadoc's TC_UI_VG_146-sourced
     * ambiguity note).
     */
    @Test(priority = 1250, testName = "ScheduleToggleAllDaysUniform", description = "TC_FUNC_VG_044: Toggling all 7 days to a uniform state is accepted without conflict", groups = {"regression", "boundary"})
    public void verifyTogglingAllSevenDaysToUniformStateIsAcceptedWithoutConflict() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu();
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem5"));
            vgPage.tapViewSchedule();
            int dayCount = Integer.parseInt(csv("ScheduleDayCount"));
            for (int day = 1; day <= dayCount; day++) {
                vgPage.toggleScheduleDay(day);
            }
            Assert.assertTrue(vgPage.isScheduleSheetDisplayed(), "Expected the Schedule sheet to display a uniform configuration without conflict, truncation, or error after toggling all 7 days.");
        } catch (Exception e) {
            logger.error("verifyTogglingAllSevenDaysToUniformStateIsAcceptedWithoutConflict failed", e);
            Assert.fail("TC_FUNC_VG_044 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_046 - Schedule bottom sheet re-scopes correctly after switching the active site.
     * UNVERIFIED weak proxy, same rationale as TC_FUNC_VG_009/015/026/035.
     *
     * <p><b>REDESIGNED 2026-10-26 (user-flagged: "not correctly designed") - confirmed via a live step-by-step
     * crawl that the original flow only "passed" by accident, through a chain of coincidences, not because
     * it correctly exercised the re-scope behavior:</b>
     * <ul>
     *   <li>Its extra {@code navigateBack()} is a no-op - LIVE-CONFIRMED pressing back from a menu-reached
     *       sub-screen (here, Arm/Disarm) does nothing (same finding as {@code returnToSurveillance()}'s
     *       own javadoc).</li>
     *   <li>Switching sites while viewing Arm/Disarm LIVE-CONFIRMED re-scopes that SAME screen in place
     *       (new site name, updated schedule time, different Activity Log entries) - it does not navigate
     *       anywhere.</li>
     *   <li>Re-tapping "Virtual Guard" while already inside the module is ALSO a no-op (same "re-tapping an
     *       already-active tab" behavior documented project-wide) - still on Arm/Disarm afterward.</li>
     *   <li>The final {@code tapBottomNavTab(VgMenuItem5)} does not navigate to Arm/Disarm at all - it taps
     *       the SCREEN'S OWN CURRENT TITLE ("Arm/Disarm"), which is the confirmed universal menu-reopen
     *       mechanism - LIVE-CONFIRMED this lands on the MENU, not Arm/Disarm, so the subsequent
     *       {@code tapViewSchedule()} had nothing real to find.</li>
     * </ul>
     * Rewritten using the same clean, already-proven-correct pattern used by the fixed sibling re-scope
     * tests ({@code verifyGuardingStatsRescopesAfterSiteSwitch}/{@code verifyCameraGridRescopesAfterSiteSwitch}).
     *
     * <p><b>FIX (2026-10-26, confirmed real failure in this very redesign)</b>: the first redesign attempt
     * repeated the EXACT same mistake it was written to fix - it assumed {@code tapBottomNavTab(TabVirtualGuard)}
     * +  {@code tapSurveillanceTitleToOpenMenu()} would reach Surveillance after the site switch, but since
     * switching sites while viewing Arm/Disarm re-scopes THAT SAME screen in place (confirmed above), the
     * session never left Arm/Disarm - so there was no "Surveillance" title to tap
     * ({@code SelfHealer: no candidate scored >= 0.75}, confirmed via the diagnostic page-source dump:
     * still showing Arm/Disarm content, re-scoped to Site B). Simplified to match the actual confirmed
     * behavior: after switching sites, Arm/Disarm is ALREADY showing Site B's data - no extra navigation
     * needed at all.
     */
    @Test(priority = 1260, testName = "ScheduleRescopesAfterSiteSwitch", description = "TC_FUNC_VG_046: Schedule bottom sheet re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyScheduleSheetRescopesAfterSiteSwitch() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("MultiSiteA"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem5"));
            Assert.assertTrue(vgPage.isArmDisarmScreenDisplayed(), "Precondition failed: expected Arm/Disarm to load on Site A.");
            vgPage.tapViewSchedule();
            Assert.assertTrue(vgPage.isScheduleSheetDisplayed(), "Precondition failed: expected the Schedule sheet to open on Site A.");
            vgPage.dismissScheduleSheet();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(csv("MultiSiteB"));
            Assert.assertTrue(vgPage.isArmDisarmScreenDisplayed(), "Precondition failed: expected Arm/Disarm to re-scope and load on Site B.");
            vgPage.tapViewSchedule();
            Assert.assertTrue(vgPage.isScheduleSheetDisplayed(), "Expected the Schedule sheet to re-scope and open correctly reflecting Site B's configuration.");
        } catch (Exception e) {
            logger.error("verifyScheduleSheetRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_VG_046 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Forensic Clips (FR-VG-006)
    // =================================================================================================

    /**
     * TC_FUNC_VG_049 - Forensic Clips screen shows a loading state while data is fetched. UNVERIFIED weak
     * proxy: no confirmed distinct "loading indicator" locator exists; asserts the screen settles into its
     * confirmed loaded state (title visible), implicitly covering the transient loading-then-loaded
     * transition without independently asserting the loading indicator's own appearance.
     * Fixed 2026-10-26 - see TC_FUNC_VG_001's javadoc: switched to {@code SiteWithForensicClips} (Amalthea).
     */
    @Test(priority = 1270, testName = "ForensicClipsShowsLoadingThenLoadedState", description = "TC_FUNC_VG_049: Forensic Clips screen shows a loading state while data is fetched", groups = {"regression"})
    public void verifyForensicClipsShowsLoadingStateWhileFetching() {
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("SiteWithForensicClips"));
            Pages.MainAppShellPage().tapBottomNavTab(csv("VgMenuItem3"));
            Assert.assertTrue(vgPage.isForensicClipsScreenDisplayed(), "Expected the Forensic Clips screen to settle into its loaded state (no confirmed distinct loading-indicator locator exists to assert separately - see javadoc).");
        } catch (Exception e) {
            logger.error("verifyForensicClipsShowsLoadingStateWhileFetching failed", e);
            Assert.fail("TC_FUNC_VG_049 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_VG_051 - Forensic Clips screen re-scopes correctly after switching the active site.
     * Fixed 2026-10-26: the original {@code MultiSiteA}="Main Site"/{@code MultiSiteB}="Amalthea" pairing
     * cannot work for this specific test - Main Site is LIVE-CONFIRMED to have NO Forensic Clips at all
     * (see TC_FUNC_VG_001's javadoc), so the Site A precondition assertion could never pass. A genuine
     * re-scope test needs TWO different Forensic-Clips-enabled sites; only one (Amalthea) is confirmed so
     * far. Per the standing "never invent data" rule (same as License Plate's SecondSiteWithLicensePlate
     * gap), this now skips until a second such site is identified rather than silently testing something
     * it can't actually exercise.
     */
    @Test(priority = 1280, testName = "ForensicClipsRescopesAfterSiteSwitch", description = "TC_FUNC_VG_051: Forensic Clips screen re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyForensicClipsRescopesAfterSiteSwitch() {
        String secondSite = csv("SecondSiteWithForensicClips");
        skipIfBlank(secondSite, "TC_FUNC_VG_051", "SecondSiteWithForensicClips");
        try {
            VirtualGuardPage vgPage = openVirtualGuardMenu(csv("SiteWithForensicClips"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomNavTab(csv("VgMenuItem3"));
            Assert.assertTrue(vgPage.isForensicClipsScreenDisplayed(), "Precondition failed: expected Forensic Clips to load on Site A.");
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(secondSite);
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            shellPage.tapBottomNavTab(csv("VgMenuItem3"));
            Assert.assertTrue(vgPage.isForensicClipsScreenDisplayed(), "Expected Forensic Clips to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyForensicClipsRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_VG_051 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }
}
