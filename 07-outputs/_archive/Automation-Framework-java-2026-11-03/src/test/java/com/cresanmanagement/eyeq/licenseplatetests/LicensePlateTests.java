package com.cresanmanagement.eyeq.licenseplatetests;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.framework.TestBase;
import com.cresanmanagement.eyeq.pageobjects.licenseplate.LicensePlatePage;
import com.cresanmanagement.eyeq.pageobjects.licenseplate.LicensePlatePageObjectRepository;
import com.cresanmanagement.eyeq.pageobjects.mainappshell.MainAppShellPage;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.pageobjects.sitemanagement.SiteManagementPage;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for the License Plate module (menu, Vehicle Scans feed, Vehicles List + Details, the
 * in-place Location filter, Lists Management).
 *
 * <p><b>LIVE-VERIFIED 2026-10-19/20</b> (real device {@code 8AHY0LBTH}, confirmed Test backend, real QA
 * account on real site "Amalthea") - see {@code LicensePlatePageObjectRepository}'s class javadoc for the
 * full evidence basis. Headline corrections from the original write-only pass:
 * <ul>
 *   <li>The "Plate Scans" vs. "Vehicle Scans" naming conflict is RESOLVED in favor of "Vehicle Scans" -
 *       confirmed live as both the bottom-nav destination screen's title and the menu item label, and
 *       re-confirmed on a third device (udid OZRK5HK77PGI4HR8, Xiaomi/MIUI) 2026-10-26.
 *       <b>FINAL RESOLUTION 2026-10-26 (per explicit user confirmation):</b> this is the correct,
 *       authoritative, consistent value every time - the single 2026-10-22 "Plate Scans" observation on
 *       udid 8BQX1DY81 was a one-off anomaly on that specific device, not a real variant. A brief
 *       OR-tolerant mitigation tried the same day is reverted - see
 *       {@code LicensePlatePageObjectRepository}'s class javadoc for the full correction history.</li>
 *   <li>TC_FUNC_LP_010's menu-reopen uncertainty is RESOLVED: tapping the bottom-nav tab navigates
 *       straight to Vehicle Scans (does not open a menu); tapping the SCREEN'S OWN TITLE opens the real
 *       Material bottom-sheet menu (confirmed resource-id {@code design_bottom_sheet}, header "License
 *       Plate Management", dismiss-scrim resource-id {@code touch_outside}) - identical to Virtual Guard's
 *       confirmed pattern.</li>
 *   <li>The Camera/Location Filter is an IN-PLACE EXPANDING DROPDOWN, not a separate bottom sheet with
 *       Apply/Clear buttons - the original write-only guess was wrong and all filter-related tests are
 *       rewritten accordingly. No "Camera" sub-filter was observed (consistent with FR-LP-005 being
 *       Partially Confirmed).</li>
 *   <li>Real test data (VIN, plate, location, Make/Model/Year/State) is now filled into
 *       {@code licenseplate.csv} from live observation - 5 of the original 12 skip-pending tests (024, 025,
 *       026, 027, 049) now run for real instead of skipping. 4 still skip (020, 033, 054, 059 - all need a
 *       second License-Plate-enabled site, not yet identified).</li>
 *   <li>Vehicle Details' field list is CORRECTED: 7 real fields (VIN, Plate, State, Location, Make, Model,
 *       Year), not the documented 6 (VIN, Plate/State, Current Location, Shop, Model, Year). No "Shop"
 *       field was found on either real vehicle record checked - TC_FUNC_LP_040 now asserts and documents
 *       that absence explicitly rather than assuming the documented field is correct.</li>
 * </ul>
 * Lists Management (TC_FUNC_LP_004/056) was reached only as far as confirming the menu item label and tap
 * navigation attempt; its own screen content was not independently verified this pass and remains
 * UNVERIFIED. Still implements all 46 YES rows from FunctionalTestCases.md's License Plate section.
 *
 * <p>Relies on the suite-level environment setup having switched the backend to Test, and on
 * {@code OnboardingAuthenticationPage#login}/{@code SiteManagementPage#selectSite}/
 * {@code MainAppShellPage#tapBottomNavTab} - all LIVE-VERIFIED - to reach the License Plate module.
 */
public class LicensePlateTests extends TestBase {

    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";
    private static final String MODULE_CSV = "src/test/resources/testdata/licenseplate.csv";

    private String csv(String key) {
        return CSVLoader.loadRowByKey(MODULE_CSV, key).get("Value");
    }

    private void skipIfBlank(String value, String tcId, String csvKey) {
        if (value == null || value.isBlank()) {
            throw new SkipException(tcId + " needs real test data not yet confirmed/provisioned - see "
                    + "licenseplate.csv's '" + csvKey + "' row Notes. Skipping rather than inventing a value.");
        }
    }

    /**
     * Reaches the Vehicle Scans feed using the suite-level shared, already-logged-in session
     * (2026-10-20 architecture - see {@code testng.SuiteCreationListener}'s class javadoc) - never calls
     * {@code OnboardingAuthenticationPage#login} itself. {@code SiteManagementPage#ensureSiteSelected}
     * works whether the session is freshly on Site Selection or already inside the Main App Shell.
     */
    private LicensePlatePage navigateToVehicleScans(String siteName) {
        SiteManagementPage sitePage = Pages.SiteManagementPage();
        sitePage.ensureSiteSelected(siteName);
        Assert.assertFalse(sitePage.isSiteSelectionScreenDisplayedFast(),
                "Precondition failed: expected to be inside the Main App Shell after ensureSiteSelected('" + siteName + "').");
        MainAppShellPage shellPage = Pages.MainAppShellPage();
        shellPage.tapBottomNavTab(csv("TabLicensePlate"));
        LicensePlatePage lpPage = Pages.LicensePlatePage();
        Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(),
                "Precondition failed: expected the License Plate tab to land directly on the Vehicle Scans feed.");
        return lpPage;
    }

    private LicensePlatePage navigateToVehicleScans() {
        return navigateToVehicleScans(csv("SiteWithLicensePlate"));
    }

    /** LIVE-VERIFIED: opens the License Plate menu by tapping the Vehicle Scans screen's own title. */
    private LicensePlatePage openLicensePlateMenu(String siteName) {
        LicensePlatePage lpPage = navigateToVehicleScans(siteName);
        lpPage.reopenMenuFromSubScreen();
        Assert.assertTrue(lpPage.isLicensePlateMenuOpen(), "Precondition failed: expected the License Plate menu to open.");
        return lpPage;
    }

    private LicensePlatePage openLicensePlateMenu() {
        return openLicensePlateMenu(csv("SiteWithLicensePlate"));
    }

    /**
     * Lightweight per-test reset (replaces the retired, unconditional-full-relaunch
     * {@code resetToCleanLoginOnTestBackend()} pattern - see {@code testng.SuiteCreationListener}'s class
     * javadoc for why that pattern was retired after a real production-environment incident). Closes any
     * open License Plate menu/dialog and returns to the Vehicle Scans feed; the shared session stays
     * logged in throughout.
     */
    private void lightReset() {
        try {
            LicensePlatePage lpPage = Pages.LicensePlatePage();
            if (lpPage.isLicensePlateMenuOpen()) {
                lpPage.dismissLicensePlateMenuWithoutSelecting();
            }
            // Fixed 2026-10-26 - see LicensePlatePage#returnToVehicleScans()'s javadoc for the full,
            // confirmed root-cause: this call used to be missing, which left the shared session on
            // whatever sub-screen a test last navigated to (e.g. the Vehicles List) whenever that test's
            // own flow closed/never opened the menu on its way there. The NEXT test's navigateToVehicleScans()
            // then unconditionally burned a 90-second precondition timeout trying to reach Vehicle Scans -
            // a 100%-reproducible failure confirmed across multiple real runs (2026-10-21, 22, 23, 26),
            // always for whichever test happened to run right after verifyTappingVehiclesNavigatesToVehiclesList.
            lpPage.returnToVehicleScans();
        } catch (Exception e) {
            logger.warn("lightReset: non-fatal cleanup issue, continuing", e);
        }
    }

    // =================================================================================================
    // License Plate Menu / Site Feature-Flag Gating (FR-LP-001)
    // =================================================================================================

    /** TC_FUNC_LP_001 - License Plate menu displays all defined menu items on an enabled site. */
    @Test(priority = 1000, testName = "LpMenuDisplaysAllItems", description = "TC_FUNC_LP_001: License Plate menu displays all defined menu items on an enabled site", groups = {"smoke"})
    public void verifyLicensePlateMenuDisplaysAllFiveItems() {
        try {
            openLicensePlateMenu();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            for (String key : new String[]{"LpMenuItem1", "LpMenuItem2", "LpMenuItem3", "LpMenuItem4", "LpMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv(key)), "Expected License Plate menu item '" + csv(key) + "' to be visible.");
            }
        } catch (Exception e) {
            logger.error("verifyLicensePlateMenuDisplaysAllFiveItems failed", e);
            Assert.fail("TC_FUNC_LP_001 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_002 - Tapping "Vehicle Scans" navigates to the Vehicle Scans feed. */
    @Test(priority = 1010, testName = "LpMenuVehicleScansNavigates", description = "TC_FUNC_LP_002: Tapping Vehicle Scans navigates to the Vehicle Scans feed", groups = {"smoke", "regression"})
    public void verifyTappingVehicleScansNavigatesToVehicleScansFeed() {
        try {
            LicensePlatePage lpPage = openLicensePlateMenu();
            Pages.MainAppShellPage().tapBottomSheetMenuItem(csv("LpMenuItem1"));
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected navigation to the Vehicle Scans feed after tapping 'Vehicle Scans'.");
        } catch (Exception e) {
            logger.error("verifyTappingVehicleScansNavigatesToVehicleScansFeed failed", e);
            Assert.fail("TC_FUNC_LP_002 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_003 - Tapping "Vehicles" navigates to the Vehicles List. */
    @Test(priority = 1020, testName = "LpMenuVehiclesNavigates", description = "TC_FUNC_LP_003: Tapping Vehicles navigates to the Vehicles List", groups = {"smoke", "regression"})
    public void verifyTappingVehiclesNavigatesToVehiclesList() {
        try {
            LicensePlatePage lpPage = openLicensePlateMenu();
            Pages.MainAppShellPage().tapBottomSheetMenuItem(csv("LpMenuItem2"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected navigation to the Vehicles List after tapping 'Vehicles'.");
        } catch (Exception e) {
            logger.error("verifyTappingVehiclesNavigatesToVehiclesList failed", e);
            Assert.fail("TC_FUNC_LP_003 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_004 - Tapping "Lists Management" navigates to the Lists Management screen. */
    @Test(priority = 1030, testName = "LpMenuListsManagementNavigates", description = "TC_FUNC_LP_004: Tapping Lists Management navigates to the Lists Management screen", groups = {"regression"})
    public void verifyTappingListsManagementNavigatesToListsManagementScreen() {
        try {
            LicensePlatePage lpPage = openLicensePlateMenu();
            Pages.MainAppShellPage().tapBottomSheetMenuItem(csv("LpMenuItem3"));
            Assert.assertTrue(lpPage.isListsManagementDisplayed(), "Expected navigation to the Lists Management screen after tapping 'Lists Management' (UNVERIFIED - this screen's own content was not independently reached/confirmed this crawl pass).");
        } catch (Exception e) {
            logger.error("verifyTappingListsManagementNavigatesToListsManagementScreen failed", e);
            Assert.fail("TC_FUNC_LP_004 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_005 - Dismissing the License Plate menu without selection performs no navigation. */
    @Test(priority = 10, testName = "LpMenuDismissWithoutSelectionNoNavigation", description = "TC_FUNC_LP_005: Dismissing the License Plate menu without selection performs no navigation", groups = {"regression", "negative"})
    public void verifyDismissingLicensePlateMenuWithoutSelectionPerformsNoNavigation() {
        try {
            LicensePlatePage lpPage = openLicensePlateMenu();
            lpPage.dismissLicensePlateMenuWithoutSelecting();
            Assert.assertFalse(lpPage.isLicensePlateMenuOpen(), "Expected the License Plate menu to close after dismissing without selection.");
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected to remain on the Vehicle Scans feed (the screen the menu was opened from), no module navigation triggered.");
        } catch (Exception e) {
            logger.error("verifyDismissingLicensePlateMenuWithoutSelectionPerformsNoNavigation failed", e);
            Assert.fail("TC_FUNC_LP_005 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_006 - License Plate tab is present on a site with the capability enabled. */
    @Test(priority = 1040, testName = "LpTabPresentOnEnabledSite", description = "TC_FUNC_LP_006: License Plate tab is present on a site with the capability enabled", groups = {"regression", "criticalPath", "multiSite"})
    public void verifyLicensePlateTabPresentOnEnabledSite() {
        try {
            Pages.SiteManagementPage().ensureSiteSelected(csv("SiteWithLicensePlate"));
            Assert.assertTrue(Pages.MainAppShellPage().isBottomNavTabPresent(csv("TabLicensePlate")),
                    "Expected the License Plate tab visible after selecting '" + csv("SiteWithLicensePlate") + "' (LIVE-CONFIRMED has-LP).");
        } catch (Exception e) {
            logger.error("verifyLicensePlateTabPresentOnEnabledSite failed", e);
            Assert.fail("TC_FUNC_LP_006 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_007 - License Plate tab is absent on a site with the capability disabled.
     * LIVE-CONFIRMED (2026-10-20): 'SiteWithoutLicensePlate' ('Main Site') genuinely lacks License Plate -
     * see licenseplate.csv/sitemanagement.csv's Notes for the full root-cause history of the earlier false
     * "Main Site isn't real" conclusion (a search-filter debounce timing bug, now fixed).
     */
    @Test(priority = 20, testName = "LpTabAbsentOnDisabledSite", description = "TC_FUNC_LP_007: License Plate tab is absent on a site with the capability disabled", groups = {"regression", "criticalPath", "multiSite"})
    public void verifyLicensePlateTabAbsentOnDisabledSite() {
        try {
            Pages.SiteManagementPage().ensureSiteSelected(csv("SiteWithoutLicensePlate"));
            Assert.assertFalse(Pages.MainAppShellPage().isBottomNavTabPresent(csv("TabLicensePlate")),
                    "Expected the License Plate tab absent after selecting '" + csv("SiteWithoutLicensePlate") + "' (LIVE-CONFIRMED no-LP).");
        } catch (Exception e) {
            logger.error("verifyLicensePlateTabAbsentOnDisabledSite failed", e);
            Assert.fail("TC_FUNC_LP_007 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_008 - License Plate tab disappears immediately when switching from an enabled to a disabled site. */
    @Test(priority = 30, testName = "LpTabDisappearsOnSwitchToDisabledSite", description = "TC_FUNC_LP_008: License Plate tab disappears immediately when switching from an enabled to a disabled site", groups = {"regression", "multiSite"})
    public void verifyLicensePlateTabDisappearsWhenSwitchingToDisabledSite() {
        try {
            navigateToVehicleScans(csv("SiteWithLicensePlate"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")), "Precondition failed: expected the License Plate tab present on the enabled site.");
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(csv("SiteWithoutLicensePlate"));
            Assert.assertFalse(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")),
                    "Expected the License Plate tab to disappear immediately after switching sites, no app restart required (LIVE-CONFIRMED no-LP on 'Main Site' - see TC_FUNC_LP_007).");
        } catch (Exception e) {
            logger.error("verifyLicensePlateTabDisappearsWhenSwitchingToDisabledSite failed", e);
            Assert.fail("TC_FUNC_LP_008 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_009 - License Plate tab appears immediately when switching from a disabled to an enabled site. */
    @Test(priority = 40, testName = "LpTabAppearsOnSwitchToEnabledSite", description = "TC_FUNC_LP_009: License Plate tab appears immediately when switching from a disabled to an enabled site", groups = {"regression", "multiSite"})
    public void verifyLicensePlateTabAppearsWhenSwitchingToEnabledSite() {
        try {
            SiteManagementPage sitePage = Pages.SiteManagementPage();
            sitePage.ensureSiteSelected(csv("SiteWithoutLicensePlate"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            Assert.assertFalse(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")), "Precondition failed: expected no License Plate tab on the disabled site.");
            shellPage.tapTopBarSiteSwitcher();
            sitePage.selectSiteFromSwitcher(csv("SiteWithLicensePlate"));
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")),
                    "Expected the License Plate tab to appear immediately after switching to '" + csv("SiteWithLicensePlate") + "'.");
        } catch (Exception e) {
            logger.error("verifyLicensePlateTabAppearsWhenSwitchingToEnabledSite failed", e);
            Assert.fail("TC_FUNC_LP_009 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_010 - License Plate menu reopens correctly after navigating into a sub-screen and back.
     *
     * <p><b>CORRECTED 2026-10-27</b> after a confirmed live failure (90-second precondition timeout on
     * {@code isVehicleScansFeedDisplayed()} after {@code navigateBack()} from the Vehicles List - see
     * logs/automation.log 2026-10-27 13:04:03-13:06:17). A dedicated live crawl
     * ({@code logs/page-source-dumps/CRAWL_B2_after_native_back_from_vehicles_list.xml}) confirmed the
     * real app behavior: the native Android back button from the Vehicles List is a NO-OP (the screen
     * stays on "Vehicles" - it does not return to Vehicle Scans). The original 2026-10-20 "LIVE-CONFIRMED"
     * claim for this was wrong (or tested a different sub-screen). "Vehicles" is a MENU destination, not a
     * drill-down like Vehicle Details - the only confirmed way back to Vehicle Scans from it is
     * {@link LicensePlatePage#returnToVehicleScans()} (which detects the CURRENT sub-screen by its own
     * title and taps THAT title to reopen the menu; {@code reopenMenuFromSubScreen()} only works when
     * already on the Vehicle Scans screen itself - confirmed live 2026-10-27 after it timed out 90s trying
     * to tap a "Vehicle Scans" title that doesn't exist while still on the Vehicles List). Replaced the raw
     * {@code navigateBack()} step with {@code returnToVehicleScans()}.
     */
    @Test(priority = 1050, testName = "LpMenuReopensAfterSubScreenAndBack", description = "TC_FUNC_LP_010: License Plate menu reopens correctly after navigating into a sub-screen and back", groups = {"regression"})
    public void verifyLicensePlateMenuReopensCorrectlyAfterSubScreenAndBack() {
        try {
            LicensePlatePage lpPage = openLicensePlateMenu();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomSheetMenuItem(csv("LpMenuItem2"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Precondition failed: expected to reach Vehicles List before navigating back.");
            lpPage.returnToVehicleScans();
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Precondition failed: expected returning from Vehicles List to land back on the Vehicle Scans feed.");
            lpPage.reopenMenuFromSubScreen();
            Assert.assertTrue(lpPage.isLicensePlateMenuOpen(), "Precondition failed: expected the menu to reopen via the title tap.");
            for (String key : new String[]{"LpMenuItem1", "LpMenuItem2", "LpMenuItem3", "LpMenuItem4", "LpMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv(key)), "Expected menu item '" + csv(key) + "' to reappear with no stale/duplicated state.");
            }
        } catch (Exception e) {
            logger.error("verifyLicensePlateMenuReopensCorrectlyAfterSubScreenAndBack failed", e);
            Assert.fail("TC_FUNC_LP_010 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Vehicle Scans Feed (FR-LP-002)
    // =================================================================================================

    /** TC_FUNC_LP_011 - Vehicle Scans feed loads with stat tiles and scan list for the active site. */
    @Test(priority = 1060, testName = "VehicleScansLoadsWithStatTilesAndList", description = "TC_FUNC_LP_011: Vehicle Scans feed loads with stat tiles and scan list for the active site", groups = {"smoke"})
    public void verifyVehicleScansFeedLoadsWithStatTilesAndScanList() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            Assert.assertNotNull(lpPage.getStatTileValue(LicensePlatePageObjectRepository.UNIQUE_VEHICLE_COUNT_LABEL), "Expected the 'Unique Vehicle Count' stat tile to be present.");
            Assert.assertNotNull(lpPage.getStatTileValue(LicensePlatePageObjectRepository.TOTAL_VEHICLE_COUNT_LABEL), "Expected the 'Total Vehicle Count' stat tile to be present.");
        } catch (Exception e) {
            logger.error("verifyVehicleScansFeedLoadsWithStatTilesAndScanList failed", e);
            Assert.fail("TC_FUNC_LP_011 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_012 - Plate-substring search filters both the scan list and the stat tiles. */
    @Test(priority = 1070, testName = "VehicleScansSubstringSearchFilters", description = "TC_FUNC_LP_012: Plate-substring search filters both the scan list and the stat tiles", groups = {"regression"})
    public void verifyPlateSubstringSearchFiltersListAndStatTiles() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.searchVehicleScans(csv("PartialPlateSearch"));
            Assert.assertTrue(lpPage.isScanRowDisplayed(csv("PartialPlateSearch")), "Expected the scan list to show a result matching the known real plate substring '" + csv("PartialPlateSearch") + "'.");
        } catch (Exception e) {
            logger.error("verifyPlateSubstringSearchFiltersListAndStatTiles failed", e);
            Assert.fail("TC_FUNC_LP_012 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_013 - Location filter alone filters both the scan list and the stat tiles. */
    @Test(priority = 1080, testName = "VehicleScansLocationFilterFilters", description = "TC_FUNC_LP_013: Location filter alone filters both the scan list and the stat tiles", groups = {"regression"})
    public void verifyLocationFilterAloneFiltersListAndStatTiles() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.openLocationFilter();
            Assert.assertTrue(lpPage.isLocationFilterExpanded(), "Precondition failed: expected the Location filter dropdown to expand.");
            lpPage.selectLocationFilterOption(csv("KnownLocationName"));
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected the feed to filter by the selected location without crashing.");
        } catch (Exception e) {
            logger.error("verifyLocationFilterAloneFiltersListAndStatTiles failed", e);
            Assert.fail("TC_FUNC_LP_013 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_014 - Plate-substring search and Location filter combine when both are applied. */
    @Test(priority = 1090, testName = "VehicleScansSearchAndLocationFilterCombine", description = "TC_FUNC_LP_014: Plate-substring search and Location filter combine when both are applied", groups = {"regression"})
    public void verifyPlateSearchAndLocationFilterCombine() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.searchVehicleScans(csv("PartialPlateSearch"));
            lpPage.openLocationFilter();
            lpPage.selectLocationFilterOption(csv("KnownLocationName"));
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected both criteria to apply jointly without crashing.");
        } catch (Exception e) {
            logger.error("verifyPlateSearchAndLocationFilterCombine failed", e);
            Assert.fail("TC_FUNC_LP_014 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_015 - Plate-substring search with no matches shows an empty result. */
    @Test(priority = 50, testName = "VehicleScansSearchNoMatch", description = "TC_FUNC_LP_015: Plate-substring search with no matches shows an empty result", groups = {"regression", "negative"})
    public void verifyPlateSearchWithNoMatchesShowsEmptyResult() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.searchVehicleScans(csv("NoMatchPlateSearch"));
            Assert.assertTrue(lpPage.isVehicleScansEmptyStateDisplayed() || lpPage.countVisibleScanRows() == 0,
                    "Expected an empty-result state (or zero visible scan rows) for a deliberately non-matching search.");
        } catch (Exception e) {
            logger.error("verifyPlateSearchWithNoMatchesShowsEmptyResult failed", e);
            Assert.fail("TC_FUNC_LP_015 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_016 - Invalid/special-character search input is handled gracefully. */
    @Test(priority = 60, testName = "VehicleScansSpecialCharSearch", description = "TC_FUNC_LP_016: Invalid/special-character search input is handled gracefully", groups = {"regression", "negative"})
    public void verifyInvalidSpecialCharacterSearchHandledGracefully() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.searchVehicleScans(csv("SpecialCharSearch"));
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected the feed to remain stable (no crash) after special-character/emoji input.");
        } catch (Exception e) {
            logger.error("verifyInvalidSpecialCharacterSearchHandledGracefully failed", e);
            Assert.fail("TC_FUNC_LP_016 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_017 - Minimum-length (1-character) search input filters by substring. */
    @Test(priority = 1100, testName = "VehicleScansMinLengthSearch", description = "TC_FUNC_LP_017: Minimum-length (1-character) search input filters by substring", groups = {"regression", "boundary"})
    public void verifyMinimumLengthSearchFiltersBySubstring() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.searchVehicleScans(csv("MinLengthSearch"));
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected the feed to accept and filter on a single-character search without crashing.");
        } catch (Exception e) {
            logger.error("verifyMinimumLengthSearchFiltersBySubstring failed", e);
            Assert.fail("TC_FUNC_LP_017 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_019 - Clearing search and filter resets the feed to the full unfiltered list. */
    @Test(priority = 1110, testName = "VehicleScansClearResetsToFullList", description = "TC_FUNC_LP_019: Clearing search and filter resets the feed to the full unfiltered list", groups = {"regression"})
    public void verifyClearingSearchAndFilterResetsToFullList() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            int countBefore = lpPage.countVisibleScanRows();
            lpPage.searchVehicleScans(csv("NoMatchPlateSearch"));
            lpPage.clearVehicleScansSearch();
            int countAfter = lpPage.countVisibleScanRows();
            Assert.assertEquals(countAfter, countBefore, "Expected the feed to reset to the full unfiltered scan list after clearing the search.");
        } catch (Exception e) {
            logger.error("verifyClearingSearchAndFilterResetsToFullList failed", e);
            Assert.fail("TC_FUNC_LP_019 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_020 - Vehicle Scans feed re-scopes correctly after switching the active site. */
    @Test(priority = 1120, testName = "VehicleScansRescopesAfterSiteSwitch", description = "TC_FUNC_LP_020: Vehicle Scans feed re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyVehicleScansFeedRescopesAfterSiteSwitch() {
        String secondSite = csv("SecondSiteWithLicensePlate");
        skipIfBlank(secondSite, "TC_FUNC_LP_020", "SecondSiteWithLicensePlate");
        try {
            LicensePlatePage lpPage = navigateToVehicleScans(csv("SiteWithLicensePlate"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(secondSite);
            shellPage.tapBottomNavTab(csv("TabLicensePlate"));
            Assert.assertTrue(lpPage.isVehicleScansFeedDisplayed(), "Expected Vehicle Scans to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyVehicleScansFeedRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_LP_020 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Vehicles List (FR-LP-003)
    // =================================================================================================

    private LicensePlatePage navigateToVehiclesList() {
        LicensePlatePage lpPage = openLicensePlateMenu();
        Pages.MainAppShellPage().tapBottomSheetMenuItem(csv("LpMenuItem2"));
        Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Precondition failed: expected the Vehicles List to load.");
        return lpPage;
    }

    /** TC_FUNC_LP_023 - Vehicles List loads with vehicle cards for the active site. */
    @Test(priority = 1130, testName = "VehiclesListLoadsWithCards", description = "TC_FUNC_LP_023: Vehicles List loads with vehicle cards for the active site", groups = {"smoke"})
    public void verifyVehiclesListLoadsWithVehicleCards() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            Assert.assertTrue(lpPage.isVehicleCardDisplayed(csv("KnownVIN")), "Expected the known vehicle's card to be visible in the list.");
        } catch (Exception e) {
            logger.error("verifyVehiclesListLoadsWithVehicleCards failed", e);
            Assert.fail("TC_FUNC_LP_023 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_024 - VIN search returns matching vehicle(s).
     *
     * <p><b>Together with TC_FUNC_LP_025, also satisfies TC_GEN_NAV_004</b> (FunctionalTestCases.md
     * Section 3, "Verify Vehicles List search returns results matching the entered VIN or plate value") -
     * the same full-value VIN/plate search scenario, split across these two existing tests; traced here
     * rather than re-implemented as a duplicate (2026-10-28).
     */
    @Test(priority = 1140, testName = "VehiclesVinSearchReturnsMatch", description = "TC_FUNC_LP_024: VIN search returns matching vehicle(s)", groups = {"regression"})
    public void verifyVinSearchReturnsMatchingVehicle() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("KnownVIN"));
            Assert.assertTrue(lpPage.isVehicleCardDisplayed(csv("KnownVIN")), "Expected a vehicle card matching the known VIN to be returned.");
        } catch (Exception e) {
            logger.error("verifyVinSearchReturnsMatchingVehicle failed", e);
            Assert.fail("TC_FUNC_LP_024 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_025 - Plate search returns matching vehicle(s). */
    @Test(priority = 1150, testName = "VehiclesPlateSearchReturnsMatch", description = "TC_FUNC_LP_025: Plate search returns matching vehicle(s)", groups = {"regression"})
    public void verifyPlateSearchReturnsMatchingVehicle() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("KnownPlateNumber"));
            Assert.assertTrue(lpPage.isVehicleCardDisplayed(csv("KnownPlateNumber")), "Expected a vehicle card matching the known plate number to be returned.");
        } catch (Exception e) {
            logger.error("verifyPlateSearchReturnsMatchingVehicle failed", e);
            Assert.fail("TC_FUNC_LP_025 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_026 - Location filter narrows the Vehicles List. */
    @Test(priority = 1160, testName = "VehiclesLocationFilterNarrows", description = "TC_FUNC_LP_026: Location filter narrows the Vehicles List", groups = {"regression"})
    public void verifyLocationFilterNarrowsVehiclesList() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.openLocationFilter();
            lpPage.selectLocationFilterOption(csv("KnownLocationName"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected the Vehicles List to filter by the selected location without crashing.");
        } catch (Exception e) {
            logger.error("verifyLocationFilterNarrowsVehiclesList failed", e);
            Assert.fail("TC_FUNC_LP_026 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_027 - VIN/plate search and Location filter combine when both are applied.
     * Fixed 2026-10-26 after a confirmed live failure ("No Location filter option found matching 'Carwash'"):
     * this combined a VIN search for {@code KnownVIN} (which narrows the list to exactly one vehicle, real
     * confirmed location {@code ExpectedLocation}="Service Drive" - see TC_FUNC_LP_039) with an unrelated
     * second location, {@code KnownLocationName}="Carwash" (confirmed real only on the separate Vehicle
     * Scans feed's full, unfiltered dropdown - see licenseplate.csv's Notes). If the Vehicles List's own
     * Location filter scopes its options to the currently-filtered result set (the behavior this test is
     * meant to exercise - "both criteria apply together"), selecting a location the VIN-matched vehicle
     * isn't even at will correctly find no such option - a self-contradictory test scenario, not a bug.
     * Switched to {@code ExpectedLocation}, the known vehicle's own real, confirmed location, so the two
     * filter criteria are consistent with each other.
     */
    @Test(priority = 1170, testName = "VehiclesSearchAndLocationFilterCombine", description = "TC_FUNC_LP_027: VIN/plate search and Location filter combine when both are applied", groups = {"regression"})
    public void verifyVinPlateSearchAndLocationFilterCombine() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("KnownVIN"));
            lpPage.openLocationFilter();
            lpPage.selectLocationFilterOption(csv("ExpectedLocation"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected both criteria to apply together without crashing.");
        } catch (Exception e) {
            logger.error("verifyVinPlateSearchAndLocationFilterCombine failed", e);
            Assert.fail("TC_FUNC_LP_027 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_028 - Search value with no matching vehicle shows an empty result. */
    @Test(priority = 70, testName = "VehiclesSearchNoMatch", description = "TC_FUNC_LP_028: Search value with no matching vehicle shows an empty result", groups = {"regression", "negative"})
    public void verifySearchWithNoMatchingVehicleShowsEmptyResult() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("UnknownVehicleSearch"));
            Assert.assertTrue(lpPage.isVehiclesEmptyStateDisplayed() || lpPage.countVisibleVehicleCards() == 0,
                    "Expected an empty-result state (or zero visible vehicle cards) for a deliberately non-matching search.");
        } catch (Exception e) {
            logger.error("verifySearchWithNoMatchingVehicleShowsEmptyResult failed", e);
            Assert.fail("TC_FUNC_LP_028 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_029 - Invalid-format search input is handled gracefully.
     *
     * <p><b>Also satisfies TC_GEN_NAV_009</b> (FunctionalTestCases.md Section 3, "Verify Vehicles List
     * search handles special characters without crashing or producing an unhandled error") - identical
     * special-character/no-crash scenario on the same search field; traced here rather than re-implemented
     * as a duplicate (2026-10-28).
     */
    @Test(priority = 80, testName = "VehiclesInvalidFormatSearch", description = "TC_FUNC_LP_029: Invalid-format search input is handled gracefully", groups = {"regression", "negative"})
    public void verifyInvalidFormatSearchHandledGracefully() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("InvalidFormatVehicleSearch"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected the Vehicles List to remain stable (no crash) after invalid-format input.");
        } catch (Exception e) {
            logger.error("verifyInvalidFormatSearchHandledGracefully failed", e);
            Assert.fail("TC_FUNC_LP_029 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_030 - Minimum-length search input filters by substring across VIN and plate. */
    @Test(priority = 1180, testName = "VehiclesMinLengthSearch", description = "TC_FUNC_LP_030: Minimum-length search input filters by substring across VIN and plate", groups = {"regression", "boundary"})
    public void verifyMinimumLengthSearchFiltersAcrossVinAndPlate() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("MinLengthSearch"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected the Vehicles List to accept and filter on a single-character search without crashing.");
        } catch (Exception e) {
            logger.error("verifyMinimumLengthSearchFiltersAcrossVinAndPlate failed", e);
            Assert.fail("TC_FUNC_LP_030 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_032 - Clearing search/filter restores the full Vehicles List. */
    @Test(priority = 1190, testName = "VehiclesClearRestoresFullList", description = "TC_FUNC_LP_032: Clearing search/filter restores the full Vehicles List", groups = {"regression"})
    public void verifyClearingSearchFilterRestoresFullVehiclesList() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            int countBefore = lpPage.countVisibleVehicleCards();
            lpPage.searchVehicles(csv("UnknownVehicleSearch"));
            lpPage.clearVehiclesSearch();
            int countAfter = lpPage.countVisibleVehicleCards();
            Assert.assertEquals(countAfter, countBefore, "Expected the full vehicle list for the site to be restored after clearing the search.");
        } catch (Exception e) {
            logger.error("verifyClearingSearchFilterRestoresFullVehiclesList failed", e);
            Assert.fail("TC_FUNC_LP_032 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_033 - Vehicles List re-scopes correctly after switching the active site. */
    @Test(priority = 1200, testName = "VehiclesRescopesAfterSiteSwitch", description = "TC_FUNC_LP_033: Vehicles List re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyVehiclesListRescopesAfterSiteSwitch() {
        String secondSite = csv("SecondSiteWithLicensePlate");
        skipIfBlank(secondSite, "TC_FUNC_LP_033", "SecondSiteWithLicensePlate");
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(secondSite);
            shellPage.tapBottomNavTab(csv("TabLicensePlate"));
            lpPage.reopenMenuFromSubScreen();
            shellPage.tapBottomSheetMenuItem(csv("LpMenuItem2"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected the vehicle registry to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyVehiclesListRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_LP_033 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Vehicle Details (FR-LP-004)
    // =================================================================================================

    /** TC_FUNC_LP_036 - Tapping a vehicle card navigates to Vehicle Details. */
    @Test(priority = 1210, testName = "VehicleCardNavigatesToDetails", description = "TC_FUNC_LP_036: Tapping a vehicle card navigates to Vehicle Details", groups = {"smoke"})
    public void verifyTappingVehicleCardNavigatesToVehicleDetails() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapFirstVehicleCard();
            Assert.assertTrue(lpPage.isVehicleDetailsDisplayed(), "Expected the Vehicle Details screen to open for the tapped vehicle.");
        } catch (Exception e) {
            logger.error("verifyTappingVehicleCardNavigatesToVehicleDetails failed", e);
            Assert.fail("TC_FUNC_LP_036 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** Shared helper for TC_FUNC_LP_037/038/039/041/042: reads a field and asserts it matches the known real value. */
    private void verifyKnownFieldValue(LicensePlatePage lpPage, String labelConstant, String expectedKey, String tcId) {
        String value = lpPage.getFieldValue(labelConstant);
        Assert.assertNotNull(value, tcId + ": expected the '" + labelConstant + "' field to be present.");
        String expected = csv(expectedKey);
        Assert.assertEquals(value, expected, tcId + ": expected the '" + labelConstant + "' field to match the known vehicle record.");
    }

    /** TC_FUNC_LP_037 - VIN field displays correctly. */
    @Test(priority = 1220, testName = "VehicleDetailsVinField", description = "TC_FUNC_LP_037: VIN field displays correctly", groups = {"regression"})
    public void verifyVinFieldDisplaysCorrectly() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.VIN_LABEL, "KnownVIN", "TC_FUNC_LP_037");
        } catch (Exception e) {
            logger.error("verifyVinFieldDisplaysCorrectly failed", e);
            Assert.fail("TC_FUNC_LP_037 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_038 - Plate/State field displays correctly. CORRECTED 2026-10-20: live evidence shows
     * these are TWO separate fields ("Plate" and "State"), not one combined "Plate/State" field as
     * documented - both are asserted here.
     */
    @Test(priority = 1230, testName = "VehicleDetailsPlateStateField", description = "TC_FUNC_LP_038: Plate/State field displays correctly", groups = {"regression"})
    public void verifyPlateStateFieldDisplaysCorrectly() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.PLATE_LABEL, "KnownPlateNumber", "TC_FUNC_LP_038");
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.STATE_LABEL, "ExpectedState", "TC_FUNC_LP_038");
        } catch (Exception e) {
            logger.error("verifyPlateStateFieldDisplaysCorrectly failed", e);
            Assert.fail("TC_FUNC_LP_038 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_039 - Current Location field displays correctly (CORRECTED 2026-10-20: real label is just "Location"). */
    @Test(priority = 1240, testName = "VehicleDetailsCurrentLocationField", description = "TC_FUNC_LP_039: Current Location field displays correctly", groups = {"regression"})
    public void verifyCurrentLocationFieldDisplaysCorrectly() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.LOCATION_LABEL, "ExpectedLocation", "TC_FUNC_LP_039");
        } catch (Exception e) {
            logger.error("verifyCurrentLocationFieldDisplaysCorrectly failed", e);
            Assert.fail("TC_FUNC_LP_039 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_040 - Shop field displays correctly. LIVE-CONFIRMED DISCREPANCY (2026-10-20): no "Shop"
     * field exists on either of the 2 real vehicle records checked (fields present: VIN, Plate, State,
     * Location, Make, Model, Year). RequirementAnalysis.md/FunctionalTestCases.md's documented field list
     * appears to be wrong on this point. Rather than silently drop this YES test case, it now asserts and
     * documents the confirmed absence, and additionally confirms the real (undocumented) "Make" field IS
     * present, so this test still exercises real, meaningful coverage instead of becoming a no-op.
     */
    @Test(priority = 1250, testName = "VehicleDetailsShopField", description = "TC_FUNC_LP_040: Shop field displays correctly", groups = {"regression"})
    public void verifyShopFieldDisplaysCorrectly() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            Assert.assertFalse(lpPage.isFieldLabelPresent(LicensePlatePageObjectRepository.SHOP_LABEL_UNCONFIRMED),
                    "TC_FUNC_LP_040: LIVE-CONFIRMED (2026-10-20) - no 'Shop' field exists on this real vehicle record; "
                            + "documenting the confirmed absence rather than assuming the documented field is correct. "
                            + "If this assertion ever fails, Shop has been added to the app and this test/requirement doc should be updated together.");
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.MAKE_LABEL, "ExpectedMake", "TC_FUNC_LP_040");
        } catch (Exception e) {
            logger.error("verifyShopFieldDisplaysCorrectly failed", e);
            Assert.fail("TC_FUNC_LP_040 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_041 - Model field displays correctly. */
    @Test(priority = 1260, testName = "VehicleDetailsModelField", description = "TC_FUNC_LP_041: Model field displays correctly", groups = {"regression"})
    public void verifyModelFieldDisplaysCorrectly() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.MODEL_LABEL, "ExpectedModel", "TC_FUNC_LP_041");
        } catch (Exception e) {
            logger.error("verifyModelFieldDisplaysCorrectly failed", e);
            Assert.fail("TC_FUNC_LP_041 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_042 - Year field displays correctly. */
    @Test(priority = 1270, testName = "VehicleDetailsYearField", description = "TC_FUNC_LP_042: Year field displays correctly", groups = {"regression"})
    public void verifyYearFieldDisplaysCorrectly() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            verifyKnownFieldValue(lpPage, LicensePlatePageObjectRepository.YEAR_LABEL, "ExpectedYear", "TC_FUNC_LP_042");
        } catch (Exception e) {
            logger.error("verifyYearFieldDisplaysCorrectly failed", e);
            Assert.fail("TC_FUNC_LP_042 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_043 - Checkpoint Timeline displays chronological location-history entries. LIVE-CONFIRMED
     * (2026-10-20): the known vehicle (KnownVIN) has ZERO checkpoint history - this asserts the confirmed
     * real empty-state copy rather than a generic either/or guess.
     */
    @Test(priority = 1280, testName = "VehicleDetailsCheckpointTimelineDisplays", description = "TC_FUNC_LP_043: Checkpoint Timeline displays chronological location-history entries", groups = {"regression"})
    public void verifyCheckpointTimelineDisplaysChronologicalEntries() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapVehicleCard(csv("KnownVIN"));
            Assert.assertTrue(lpPage.isCheckpointTimelineDisplayed(), "Expected the Checkpoint Timeline section header to be present.");
            Assert.assertTrue(lpPage.isCheckpointTimelineEmptyStateDisplayed(),
                    "Expected the confirmed real empty-state message ('There are currently no checkpoints for this vehicle.') for the known vehicle, which has zero checkpoint history.");
        } catch (Exception e) {
            logger.error("verifyCheckpointTimelineDisplaysChronologicalEntries failed", e);
            Assert.fail("TC_FUNC_LP_043 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_044 - Checkpoint Timeline timestamps display with the correct GMT-offset. UNVERIFIED weak
     * proxy: the known vehicle (KnownVIN) has zero checkpoint history, so no populated timestamp could be
     * observed live; this falls back to checking the FIRST available vehicle (whichever has entries, if
     * any), since no vehicle with confirmed checkpoint history was identified this pass.
     */
    @Test(priority = 1290, testName = "VehicleDetailsCheckpointGmtOffset", description = "TC_FUNC_LP_044: Checkpoint Timeline timestamps display with the correct GMT-offset", groups = {"regression"})
    public void verifyCheckpointTimelineTimestampsShowGmtOffset() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.tapFirstVehicleCard();
            if (lpPage.countCheckpointEntries() > 0) {
                String entryText = lpPage.getCheckpointEntryText(1);
                Assert.assertNotNull(entryText, "Expected the first checkpoint entry to be readable (GMT-offset correctness itself is not verifiable without a vehicle with confirmed checkpoint history - see javadoc).");
            } else {
                logger.warn("TC_FUNC_LP_044: no checkpoint entries present on the first available vehicle - GMT-offset format could not be exercised this run. No vehicle with checkpoint history was identified during the live crawl.");
            }
        } catch (Exception e) {
            logger.error("verifyCheckpointTimelineTimestampsShowGmtOffset failed", e);
            Assert.fail("TC_FUNC_LP_044 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_046 - Navigating back from Vehicle Details preserves the prior Vehicles List state.
     *
     * <p><b>Also satisfies TC_GEN_NAV_003</b> (FunctionalTestCases.md Section 3, "Verify the Android system
     * back action returns the user to the previous screen") - identical scenario (native back from Vehicle
     * Details to the Vehicles List); traced here rather than re-implemented as a duplicate (2026-10-28).
     */
    @Test(priority = 1300, testName = "VehicleDetailsBackPreservesListState", description = "TC_FUNC_LP_046: Navigating back from Vehicle Details preserves the prior Vehicles List state", groups = {"regression"})
    public void verifyNavigatingBackFromVehicleDetailsPreservesPriorListState() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.searchVehicles(csv("KnownVIN"));
            lpPage.tapFirstVehicleCard();
            Assert.assertTrue(lpPage.isVehicleDetailsDisplayed(), "Precondition failed: expected to reach Vehicle Details.");
            lpPage.navigateBack();
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected to return to the Vehicles List.");
            Assert.assertEquals(lpPage.getVehiclesSearchValue(), csv("KnownVIN"), "Expected the prior search value to be preserved after navigating back.");
        } catch (Exception e) {
            logger.error("verifyNavigatingBackFromVehicleDetailsPreservesPriorListState failed", e);
            Assert.fail("TC_FUNC_LP_046 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Vehicle Camera/Location Filter (FR-LP-005) - CORRECTED 2026-10-20: this is an in-place expanding
    // dropdown (confirmed live), NOT a separate bottom sheet with Apply/Clear buttons as originally guessed.
    // =================================================================================================

    /** TC_FUNC_LP_048 - Camera/Location Filter opens with selectable options. */
    @Test(priority = 1310, testName = "FilterOpensWithOptions", description = "TC_FUNC_LP_048: Camera/Location Filter opens with selectable options", groups = {"regression"})
    public void verifyCameraLocationFilterOpensWithOptions() {
        try {
            LicensePlatePage lpPage = navigateToVehicleScans();
            lpPage.openLocationFilter();
            Assert.assertTrue(lpPage.isLocationFilterExpanded(), "Expected the Location filter dropdown to expand with selectable options.");
        } catch (Exception e) {
            logger.error("verifyCameraLocationFilterOpensWithOptions failed", e);
            Assert.fail("TC_FUNC_LP_048 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_049 - Selecting a Location and applying filters the underlying list. */
    @Test(priority = 1320, testName = "FilterSelectLocationApplies", description = "TC_FUNC_LP_049: Selecting a Location and applying filters the underlying list", groups = {"regression"})
    public void verifySelectingLocationAndApplyingFiltersList() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.openLocationFilter();
            lpPage.selectLocationFilterOption(csv("KnownLocationName"));
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Expected the underlying list to filter by the selected location.");
        } catch (Exception e) {
            logger.error("verifySelectingLocationAndApplyingFiltersList failed", e);
            Assert.fail("TC_FUNC_LP_049 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_051 - Dismissing the filter without a selection leaves the list unchanged.
     *
     * <p><b>CORRECTED 2026-10-27</b>: the original test assumed the Vehicles List's "Location Filter
     * Control" opens the same flat dropdown as the Vehicle Scans feed, and modeled "dismiss" as a guessed
     * re-tap toggle. A live crawl confirmed the real UI: on the Vehicles List, this control instead opens
     * a full-screen Camera/Location Filter sheet (a hierarchical camera checkbox tree with "Filter"/
     * "Clear" buttons - see {@code LicensePlatePageObjectRepository}'s FR-LP-005 section javadoc), and the
     * confirmed real dismiss-without-selecting mechanism is the native Android back button (verified live:
     * no selection persists and the list is untouched - see
     * {@code logs/page-source-dumps/CRAWL_CF8_after_native_back.xml} from the investigating crawl).
     */
    @Test(priority = 90, testName = "FilterDismissWithoutSelectionNoChange", description = "TC_FUNC_LP_051: Dismissing the filter without a selection leaves the list unchanged", groups = {"regression", "negative"})
    public void verifyDismissingFilterWithoutSelectionLeavesListUnchanged() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            int countBefore = lpPage.countVisibleVehicleCards();
            lpPage.openLocationFilter();
            Assert.assertTrue(lpPage.isCameraFilterSheetOpen(), "Precondition failed: expected the Camera/Location Filter sheet to be open.");
            lpPage.navigateBack();
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Precondition failed: expected native back to dismiss the filter sheet and return to the Vehicles List.");
            int countAfter = lpPage.countVisibleVehicleCards();
            Assert.assertEquals(countAfter, countBefore, "Expected the underlying list to remain unfiltered/unchanged after dismissing the filter without selecting an option.");
        } catch (Exception e) {
            logger.error("verifyDismissingFilterWithoutSelectionLeavesListUnchanged failed", e);
            Assert.fail("TC_FUNC_LP_051 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_LP_052 - Clearing an applied filter reverts the list.
     *
     * <p><b>CORRECTED 2026-10-27</b>: same root issue as TC_FUNC_LP_051's correction above - the Vehicles
     * List's filter is the Camera/Location Filter sheet (checkbox tree), not the Vehicle Scans feed's
     * flat "All Locations" dropdown. LIVE-CONFIRMED real mechanism: check a camera's box, tap "Filter" to
     * apply; to clear, reopen, tap "Clear" (unchecks every box but the sheet stays open - confirmed live),
     * then tap "Filter" again to apply the now-cleared (unfiltered) state.
     */
    @Test(priority = 1330, testName = "FilterClearRevertsList", description = "TC_FUNC_LP_052: Clearing an applied filter reverts the list", groups = {"regression"})
    public void verifyClearingAppliedFilterRevertsListToUnfiltered() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            int countBefore = lpPage.countVisibleVehicleCards();
            lpPage.openLocationFilter();
            Assert.assertTrue(lpPage.isCameraFilterSheetOpen(), "Precondition failed: expected the Camera/Location Filter sheet to be open.");
            lpPage.toggleCameraFilterOption(csv("KnownCameraName"));
            lpPage.tapCameraFilterApply();
            Assert.assertTrue(lpPage.isVehiclesListDisplayed(), "Precondition failed: expected applying the filter to return to the Vehicles List.");
            lpPage.openLocationFilter();
            lpPage.tapCameraFilterClear();
            lpPage.tapCameraFilterApply();
            int countAfter = lpPage.countVisibleVehicleCards();
            Assert.assertEquals(countAfter, countBefore, "Expected the list to revert to its unfiltered state after clearing the applied camera filter.");
        } catch (Exception e) {
            logger.error("verifyClearingAppliedFilterRevertsListToUnfiltered failed", e);
            Assert.fail("TC_FUNC_LP_052 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_054 - Filter options re-scope correctly after switching the active site. */
    @Test(priority = 1340, testName = "FilterRescopesAfterSiteSwitch", description = "TC_FUNC_LP_054: Filter options re-scope correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyFilterOptionsRescopeAfterSiteSwitch() {
        String secondSite = csv("SecondSiteWithLicensePlate");
        skipIfBlank(secondSite, "TC_FUNC_LP_054", "SecondSiteWithLicensePlate");
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            lpPage.openLocationFilter();
            Assert.assertTrue(lpPage.isLocationFilterExpanded(), "Precondition failed: expected the filter open on Site A.");
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.navigateBack();
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(secondSite);
            shellPage.tapBottomNavTab(csv("TabLicensePlate"));
            lpPage.reopenMenuFromSubScreen();
            shellPage.tapBottomSheetMenuItem(csv("LpMenuItem2"));
            lpPage.openLocationFilter();
            Assert.assertTrue(lpPage.isLocationFilterExpanded(), "Expected the filter to re-scope and open correctly reflecting Site B's options.");
        } catch (Exception e) {
            logger.error("verifyFilterOptionsRescopeAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_LP_054 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Lists Management (FR-REBUILD-15) - UNVERIFIED (not independently reached this crawl pass)
    // =================================================================================================

    /**
     * TC_FUNC_LP_056 - Lists Management screen loads with configured plate list(s) for the active site.
     * UNVERIFIED: the menu item's label is LIVE-CONFIRMED, but the destination screen's own content was not
     * independently reached/verified this crawl pass.
     */
    @Test(priority = 1350, testName = "ListsManagementLoadsWithLists", description = "TC_FUNC_LP_056: Lists Management screen loads with configured plate list(s) for the active site", groups = {"regression"})
    public void verifyListsManagementLoadsWithConfiguredLists() {
        try {
            LicensePlatePage lpPage = openLicensePlateMenu();
            Pages.MainAppShellPage().tapBottomSheetMenuItem(csv("LpMenuItem3"));
            Assert.assertTrue(lpPage.isListsManagementDisplayed(), "Expected the Lists Management screen to load without error.");
        } catch (Exception e) {
            logger.error("verifyListsManagementLoadsWithConfiguredLists failed", e);
            Assert.fail("TC_FUNC_LP_056 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_LP_059 - Lists Management re-scopes correctly after switching the active site. */
    @Test(priority = 1360, testName = "ListsManagementRescopesAfterSiteSwitch", description = "TC_FUNC_LP_059: Lists Management re-scopes correctly after switching the active site", groups = {"regression", "multiSite"})
    public void verifyListsManagementRescopesAfterSiteSwitch() {
        String secondSite = csv("SecondSiteWithLicensePlate");
        skipIfBlank(secondSite, "TC_FUNC_LP_059", "SecondSiteWithLicensePlate");
        try {
            LicensePlatePage lpPage = openLicensePlateMenu(csv("SiteWithLicensePlate"));
            MainAppShellPage shellPage = Pages.MainAppShellPage();
            shellPage.tapBottomSheetMenuItem(csv("LpMenuItem3"));
            Assert.assertTrue(lpPage.isListsManagementDisplayed(), "Precondition failed: expected Lists Management to load on Site A.");
            shellPage.navigateBack();
            shellPage.tapTopBarSiteSwitcher();
            Pages.SiteManagementPage().selectSiteFromSwitcher(secondSite);
            shellPage.tapBottomNavTab(csv("TabLicensePlate"));
            lpPage.reopenMenuFromSubScreen();
            shellPage.tapBottomSheetMenuItem(csv("LpMenuItem3"));
            Assert.assertTrue(lpPage.isListsManagementDisplayed(), "Expected Lists Management to re-scope and load correctly on Site B.");
        } catch (Exception e) {
            logger.error("verifyListsManagementRescopesAfterSiteSwitch failed", e);
            Assert.fail("TC_FUNC_LP_059 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Adapted Generic Test Cases (FunctionalTestCases.md Section 3) - automated 2026-10-28
    // =================================================================================================

    /**
     * TC_GEN_NAV_005 - Vehicles List search matches on a partial VIN substring (not the full value, and not
     * the extreme single-character case already covered by TC_FUNC_LP_030). Uses a real, derived prefix of
     * the already-confirmed real {@code KnownVIN} value rather than inventing a new one.
     */
    @Test(priority = 1370, testName = "GenVehiclesSearchPartialVinSubstring", description = "TC_GEN_NAV_005: Vehicles List search matches on a partial VIN substring", groups = {"regression"})
    public void verifyVehiclesSearchMatchesPartialVinSubstring() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            String knownVin = csv("KnownVIN");
            String partialVin = knownVin.substring(0, Math.min(8, knownVin.length()));
            lpPage.searchVehicles(partialVin);
            Assert.assertTrue(lpPage.isVehicleCardDisplayed(knownVin), "Expected a partial VIN substring search ('" + partialVin + "') to return the matching vehicle.");
        } catch (Exception e) {
            logger.error("verifyVehiclesSearchMatchesPartialVinSubstring failed", e);
            Assert.fail("TC_GEN_NAV_005 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_GEN_NAV_007 - Vehicles List search is not case-sensitive. Asserts against the known, correctly-cased
     * plate value in both legs (rather than the search input's own casing) so the assertion's own
     * case-sensitive XPath {@code contains()} match doesn't mask a real app-level case-insensitivity bug.
     */
    @Test(priority = 1380, testName = "GenVehiclesSearchNotCaseSensitive", description = "TC_GEN_NAV_007: Vehicles List search is not case-sensitive", groups = {"regression"})
    public void verifyVehiclesSearchIsNotCaseSensitive() {
        try {
            LicensePlatePage lpPage = navigateToVehiclesList();
            String knownPlate = csv("KnownPlateNumber");
            lpPage.searchVehicles(knownPlate.toUpperCase());
            Assert.assertTrue(lpPage.isVehicleCardDisplayed(knownPlate), "Expected an all-uppercase search to return the known vehicle.");
            lpPage.clearVehiclesSearch();
            lpPage.searchVehicles(knownPlate.toLowerCase());
            Assert.assertTrue(lpPage.isVehicleCardDisplayed(knownPlate), "Expected an all-lowercase search to return the same known vehicle, confirming the search is not case-sensitive.");
        } catch (Exception e) {
            logger.error("verifyVehiclesSearchIsNotCaseSensitive failed", e);
            Assert.fail("TC_GEN_NAV_007 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }
}
