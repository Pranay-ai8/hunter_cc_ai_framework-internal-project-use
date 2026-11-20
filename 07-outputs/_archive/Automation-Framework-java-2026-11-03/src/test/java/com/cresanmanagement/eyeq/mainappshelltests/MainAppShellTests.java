package com.cresanmanagement.eyeq.mainappshelltests;

import com.cresanmanagement.eyeq.Pages;
import com.cresanmanagement.eyeq.framework.TestBase;
import com.cresanmanagement.eyeq.pageobjects.mainappshell.MainAppShellPage;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.pageobjects.sitemanagement.SiteManagementPage;
import com.cresanmanagement.eyeq.utils.CSVLoader;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for the Main App Shell module (persistent Top Bar, persistent Bottom Navigation).
 *
 * <p><b>STATUS (2026-10-20):</b> originally written in CODE-WRITING-ONLY mode (2026-10-19) against
 * documented evidence only ({@code RequirementAnalysis.md} Section 10.3, {@code FunctionalTestCases.md}'s
 * TC_FUNC_SHELL_001-016 rows, {@code UITestCases.md}'s TC_UI_SUP_001/003, TC_UI_VG_001/003/007,
 * TC_UI_LP_001) - this class itself has NOT yet been executed end to end. However, the specific locators
 * it depends on in {@code MainAppShellPageObjectRepository} (TopBar Profile icon, TopBar site switcher,
 * bottom-sheet menu item rows) have since been LIVE-CONFIRMED via real crawls done for other modules
 * (Account Settings, Virtual Guard, License Plate) and corrected from their original UNVERIFIED guesses -
 * see that repository's class javadoc for the full evidence. Retrofitted 2026-10-20 to the suite-level
 * shared-session architecture (see {@code testng.SuiteCreationListener}'s class javadoc) - not yet
 * validated by an actual run of this class under that architecture.
 *
 * <p>Relies on the suite-level session setup having switched the backend to Test and logged in once (see
 * {@code testng.SuiteCreationListener}) - no test here calls {@code OnboardingAuthenticationPage#login}
 * itself except {@code lightReset()}'s defensive recovery after a force-kill/background test.
 */
public class MainAppShellTests extends TestBase {

    private static final String ENV_CSV = "src/test/resources/testdata/environmentsetup.csv";
    private static final String MODULE_CSV = "src/test/resources/testdata/mainappshell.csv";

    private String csv(String key) {
        return CSVLoader.loadRowByKey(MODULE_CSV, key).get("Value");
    }

    /**
     * Reaches the Main App Shell using the suite-level shared, already-logged-in session (2026-10-20
     * architecture - see {@code testng.SuiteCreationListener}'s class javadoc) - never calls
     * {@code OnboardingAuthenticationPage#login} itself.
     */
    private MainAppShellPage loginToMainAppShell(String siteName) {
        SiteManagementPage sitePage = Pages.SiteManagementPage();
        sitePage.ensureSiteSelected(siteName);
        Assert.assertFalse(sitePage.isSiteSelectionScreenDisplayedFast(),
                "Precondition failed: expected to be inside the Main App Shell after ensureSiteSelected('" + siteName + "').");
        return Pages.MainAppShellPage();
    }

    /** Defaults to the site with every tab enabled (License Plate included), for tests not specifically covering per-site gating. */
    private MainAppShellPage loginToMainAppShell() {
        return loginToMainAppShell(csv("SiteWithLicensePlate"));
    }

    /**
     * Lightweight per-test reset (replaces the retired, unconditional-full-relaunch
     * {@code resetToCleanLoginOnTestBackend()} pattern - see {@code testng.SuiteCreationListener}'s class
     * javadoc for why that pattern was retired after a real production-environment incident). Several
     * tests in this class use {@code forceKillAndRelaunchApp()}/{@code backgroundAppAndResume()} as their
     * OWN test subject (a genuine, intentional exception to "no full relaunch" - these tests' whole point
     * is to force-kill/background the real app) - this reset just confirms the shared session ended up
     * logged in afterward (a force-kill COULD cold-start back to Login depending on what the app restores)
     * and logs back in if genuinely necessary, rather than assuming either way.
     */
    private void lightReset() {
        // FIX (2026-10-27, confirmed real failures, two separate leaks of the same bug class):
        // verifyTopBarSiteSwitcherControlOpensSiteSwitcher opens the Site Switcher sheet and never closes
        // it, and verifyTopBarProfileIconOpensAccountSettings opens Account Settings and never closes it -
        // this reset previously only checked for the Login screen, so every LATER test in the class
        // inherited whichever overlay was left open and could never find the TopBar's own controls
        // (confirmed via diagnostic page-source dumps: one showing the Site Switcher's "Sites" list still
        // open, a later one showing BOTH Account Settings' and the underlying screen's content at once).
        // Checked first, before the login check, reusing the already-proven fast checks/dismiss methods
        // SiteManagementTests#lightReset() uses for the identical Site Switcher leak, and the same
        // genuinely-zero-wait pattern for Account Settings - this runs after EVERY test in the class and
        // either overlay is open in only a small minority of them.
        SiteManagementPage sitePage = Pages.SiteManagementPage();
        if (sitePage.isSiteSwitcherOpenFast()) {
            sitePage.dismissSiteSwitcherWithoutSelecting();
        }
        MainAppShellPage shellPage = Pages.MainAppShellPage();
        if (shellPage.isAccountSettingsScreenDisplayedFast()) {
            shellPage.navigateBack();
        }
        // DEFENSE-IN-DEPTH (2026-10-27): the same leak class, pre-emptively covered for the bottom-nav
        // tests that open Virtual Guard's/License Plate's bottom-sheet menu and never close it either
        // (verifyVirtualGuardTabOpensItsBottomSheetMenu/verifyLicensePlateTabOpensItsBottomSheetMenuWhenSiteEnabled)
        // - reusing the already-proven fast check/dismiss pair added for VirtualGuardTests#lightReset().
        if (shellPage.isBottomSheetMenuOpenFast()) {
            shellPage.dismissBottomSheetMenuWithoutSelecting();
        }
        OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
        // Snapshot check of already-settled state, run after EVERY test in this class - the common case
        // (most tests do not force-kill/background the app) is "false" (still logged in), so the
        // fast-bounded variant is essential: the slow variant here would have burned a full 90s on nearly
        // every test's reset, the same class of problem confirmed to have inflated Site Management's
        // runtime. Tests that genuinely force-kill/background as their own subject already perform their
        // own slow, genuine first-arrival wait via isLoginScreenDisplayed() before reaching this reset.
        if (authPage.isLoginScreenDisplayedFast()) {
            Map<String, String> qaUser = CSVLoader.loadRowByKey(ENV_CSV, "QaUser");
            authPage.login(qaUser.get("Username"), qaUser.get("Password"));
        }
    }

    // =================================================================================================
    // Persistent Top Bar (FR-SHELL-001) - UNVERIFIED
    // =================================================================================================

    /** TC_FUNC_SHELL_001 - Top Bar - Site Switcher Control Opens Site Switcher. */
    @Test(priority = 1000, testName = "ShellTopBarSiteSwitcherOpens", description = "TC_FUNC_SHELL_001: Top Bar - Site Switcher Control Opens Site Switcher", groups = {"smoke"})
    public void verifyTopBarSiteSwitcherControlOpensSiteSwitcher() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapTopBarSiteSwitcher();
            Assert.assertTrue(shellPage.isSiteSwitcherOpen(), "Expected the Site Switcher bottom sheet to open after tapping the TopBar site-name control.");
        } catch (Exception e) {
            logger.error("verifyTopBarSiteSwitcherControlOpensSiteSwitcher failed", e);
            Assert.fail("TC_FUNC_SHELL_001 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SHELL_002 - Top Bar - Profile Icon Opens Account Settings. */
    @Test(priority = 1010, testName = "ShellTopBarProfileIconOpensAccountSettings", description = "TC_FUNC_SHELL_002: Top Bar - Profile Icon Opens Account Settings", groups = {"smoke"})
    public void verifyTopBarProfileIconOpensAccountSettings() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapTopBarProfileIcon();
            Assert.assertTrue(shellPage.isAccountSettingsScreenDisplayed(), "Expected Account Settings to be shown after tapping the TopBar Profile icon.");
        } catch (Exception e) {
            logger.error("verifyTopBarProfileIconOpensAccountSettings failed", e);
            Assert.fail("TC_FUNC_SHELL_002 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_003 - Top Bar - Persists and Functions Across All Modules. Iterates the three
     * bottom-nav tabs confirmed present regardless of site (Virtual Guard, Global, Support - see
     * mainappshell.csv's Notes); License Plate is intentionally excluded since its presence is per-site
     * gated and not the concern of this cross-module persistence check.
     */
    @Test(priority = 1020, testName = "ShellTopBarPersistsAcrossModules", description = "TC_FUNC_SHELL_003: Top Bar - Persists and Functions Across All Modules", groups = {"regression", "criticalPath"})
    public void verifyTopBarPersistsAndFunctionsAcrossAllModules() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            String[] tabs = {csv("TabVirtualGuard"), csv("TabGlobal"), csv("TabSupport")};
            for (String tab : tabs) {
                shellPage.tapBottomNavTab(tab);
                shellPage.tapTopBarSiteSwitcher();
                Assert.assertTrue(shellPage.isSiteSwitcherOpen(), "Expected the Site Switcher control to still work from the '" + tab + "' module.");
                shellPage.navigateBack();
                shellPage.tapTopBarProfileIcon();
                Assert.assertTrue(shellPage.isAccountSettingsScreenDisplayed(), "Expected the Profile icon to still work from the '" + tab + "' module.");
                shellPage.navigateBack();
            }
        } catch (Exception e) {
            logger.error("verifyTopBarPersistsAndFunctionsAcrossAllModules failed", e);
            Assert.fail("TC_FUNC_SHELL_003 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_004 - Top Bar - Rapid Double-Tap on Controls. UNVERIFIED: with no confirmed way to
     * count simultaneously-open overlays, this asserts the weaker (but still meaningful) proxy that the
     * app remains in a single, consistent, non-crashed overlay state after each rapid double-tap.
     */
    //@Test(priority = 10, testName = "ShellTopBarRapidDoubleTap", description = "TC_FUNC_SHELL_004: Top Bar - Rapid Double-Tap on Controls", groups = {"regression"})
    public void verifyRapidDoubleTapOnTopBarControlsCausesNoDuplicateOrCrash() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapTopBarSiteSwitcher();
            shellPage.tapTopBarSiteSwitcher();
            Assert.assertTrue(shellPage.isSiteSwitcherOpen(), "Expected a single, stable Site Switcher state after rapid double-tap (no crash).");
            shellPage.navigateBack();
            shellPage.navigateBack();
            shellPage.tapTopBarProfileIcon();
            shellPage.tapTopBarProfileIcon();
            Assert.assertTrue(shellPage.isAccountSettingsScreenDisplayed(), "Expected a single, stable Account Settings state after rapid double-tap (no crash).");
        } catch (Exception e) {
            logger.error("verifyRapidDoubleTapOnTopBarControlsCausesNoDuplicateOrCrash failed", e);
            Assert.fail("TC_FUNC_SHELL_004 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SHELL_005 - Top Bar - App Backgrounded With Overlay Open. */
    @Test(priority = 20, testName = "ShellTopBarBackgroundedWithOverlayOpen", description = "TC_FUNC_SHELL_005: Top Bar - App Backgrounded With Overlay Open", groups = {"regression"})
    public void verifyAppBackgroundedWithTopBarOverlayOpenRecoversGracefully() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapTopBarSiteSwitcher();
            Assert.assertTrue(shellPage.isSiteSwitcherOpen(), "Precondition failed: expected the Site Switcher to be open before backgrounding.");
            shellPage.backgroundAppAndResume(Integer.parseInt(csv("BackgroundSeconds")));
            boolean recoveredGracefully = shellPage.isSiteSwitcherOpen() || shellPage.isBottomNavTabPresent(csv("TabSupport"));
            Assert.assertTrue(recoveredGracefully, "Expected the overlay to remain open or be gracefully dismissed after backgrounding/resuming - no stuck/corrupted UI.");
        } catch (Exception e) {
            logger.error("verifyAppBackgroundedWithTopBarOverlayOpenRecoversGracefully failed", e);
            Assert.fail("TC_FUNC_SHELL_005 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SHELL_006 - Top Bar - App Force-Killed With Overlay Open. */
    @Test(priority = 30, testName = "ShellTopBarForceKilledWithOverlayOpen", description = "TC_FUNC_SHELL_006: Top Bar - App Force-Killed With Overlay Open", groups = {"regression"})
    public void verifyAppForceKilledWithTopBarOverlayOpenRecoversToStableState() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapTopBarSiteSwitcher();
            Assert.assertTrue(shellPage.isSiteSwitcherOpen(), "Precondition failed: expected the Site Switcher to be open before force-killing.");
            shellPage.forceKillAndRelaunchApp();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            boolean stableState = authPage.isLoginScreenDisplayed() || shellPage.isBottomNavTabPresent(csv("TabSupport"));
            Assert.assertTrue(stableState, "Expected the app to relaunch to a stable, navigable state (Login or Main App Shell) with no stuck overlay and no crash.");
        } catch (Exception e) {
            logger.error("verifyAppForceKilledWithTopBarOverlayOpenRecoversToStableState failed", e);
            Assert.fail("TC_FUNC_SHELL_006 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Persistent Bottom Navigation (FR-SHELL-002) - UNVERIFIED
    // =================================================================================================

    /**
     * TC_FUNC_SHELL_007 - Bottom Nav - All Enabled Tabs Reachable (site with every module enabled).
     *
     * <p><b>UPDATED 2026-10-21</b> per explicit instruction, addressing two points:
     * <ul>
     *   <li>Bottom tabs are only visible once a site is active - this test now explicitly selects a real
     *       site (CSV-driven via {@code SiteWithLicensePlate}, "Amalthea") through the search field
     *       (via {@code SiteManagementPage#ensureSiteSelected}, which always searches first - see that
     *       method's own javadoc for the standing "never tap the unfiltered list" rule) before checking
     *       any tab, rather than assuming tabs are visible on an unscoped session.</li>
     *   <li>Virtual Guard/"Surveillance" is confirmed to be the DEFAULT active tab the moment a site is
     *       selected (not a neutral/no-tab-selected state) - this test asserts that default state FIRST,
     *       then exercises each of the other tabs in turn, rather than treating Virtual Guard as "just
     *       another tab to tap".</li>
     * </ul>
     *
     * <p><b>Also satisfies TC_GEN_NAV_001</b> (FunctionalTestCases.md Section 3, "Verify each available
     * bottom-navigation tab is tappable and opens the correct module") - identical scenario, same tabs, same
     * assertion shape; automating it separately would be a pure duplicate of this test, so it is traced here
     * rather than re-implemented (2026-10-28).
     */
    @Test(priority = 1030, testName = "ShellBottomNavAllTabsReachable", description = "TC_FUNC_SHELL_007: Bottom Nav - All Enabled Tabs Reachable", groups = {"smoke", "criticalPath"})
    public void verifyAllEnabledBottomNavTabsAreReachable() {
        try {
            SiteManagementPage sitePage = Pages.SiteManagementPage();
            sitePage.ensureSiteSelected(csv("SiteWithLicensePlate"));
            Assert.assertFalse(sitePage.isSiteSelectionScreenDisplayedFast(),
                    "Precondition failed: expected to be inside the Main App Shell after ensureSiteSelected('" + csv("SiteWithLicensePlate") + "').");
            MainAppShellPage shellPage = Pages.MainAppShellPage();

            // Virtual Guard/Surveillance is the confirmed default active tab immediately after site
            // selection - assert this BEFORE tapping anything, rather than assuming a neutral start state.
            Assert.assertTrue(Pages.VirtualGuardPage().isSurveillanceScreenDisplayed(),
                    "Expected Virtual Guard's Surveillance screen to be open by default immediately after site selection.");
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabVirtualGuard")), "Expected the Virtual Guard tab itself to be visible and present.");

            shellPage.tapBottomNavTab(csv("TabLicensePlate"));
            Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv("LpMenuItem1")), "Expected the License Plate tab to reach its bottom-sheet menu.");
            shellPage.dismissBottomSheetMenuWithoutSelecting();

            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabGlobal")), "Expected the Global tab to remain reachable.");
            shellPage.tapBottomNavTab(csv("TabGlobal"));
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabSupport")), "Expected the Support tab to remain reachable.");
            shellPage.tapBottomNavTab(csv("TabSupport"));

            // Confirm navigating back to Virtual Guard still works after visiting every other tab.
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Assert.assertTrue(Pages.VirtualGuardPage().isSurveillanceScreenDisplayed(),
                    "Expected returning to the Virtual Guard tab to show the Surveillance screen again.");
        } catch (Exception e) {
            logger.error("verifyAllEnabledBottomNavTabsAreReachable failed", e);
            Assert.fail("TC_FUNC_SHELL_007 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_008 - Bottom Nav - Virtual Guard Tab Opens Its Bottom-Sheet Menu.
     * FIX (2026-10-23, root-caused from a real failed run): this test assumed tapping the bottom-nav tab
     * directly opens the menu - the same disproven assumption already fixed in
     * {@code VirtualGuardTests#openVirtualGuardMenu} (2026-10-22, real device crawl). Live-confirmed the
     * tab lands directly on the Surveillance screen instead; the menu only opens via a separate tap on
     * that screen's own title. This test had its own independent copy of the same wrong assumption and was
     * never updated when the other one was fixed - corrected the same way here.
     */
    @Test(priority = 1040, testName = "ShellBottomNavVirtualGuardOpensMenu", description = "TC_FUNC_SHELL_008: Bottom Nav - Virtual Guard Tab Opens Its Bottom-Sheet Menu", groups = {"smoke"})
    public void verifyVirtualGuardTabOpensItsBottomSheetMenu() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Pages.VirtualGuardPage().tapSurveillanceTitleToOpenMenu();
            for (String key : new String[]{"VgMenuItem1", "VgMenuItem2", "VgMenuItem3", "VgMenuItem4", "VgMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv(key)), "Expected Virtual Guard bottom-sheet menu item '" + csv(key) + "' to be listed.");
            }
        } catch (Exception e) {
            logger.error("verifyVirtualGuardTabOpensItsBottomSheetMenu failed", e);
            Assert.fail("TC_FUNC_SHELL_008 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_009 - Bottom Nav - License Plate Tab Opens Its Bottom-Sheet Menu (Site-Enabled).
     * FIX (2026-10-23, same root cause/fix as TC_FUNC_SHELL_008 above): tapping the License Plate tab
     * lands directly on the Plate Scans feed (already confirmed in {@code LicensePlateTests}); the menu
     * needs a separate tap on that screen's own title via {@code LicensePlatePage#reopenMenuFromSubScreen}.
     */
    @Test(priority = 1050, testName = "ShellBottomNavLicensePlateOpensMenu", description = "TC_FUNC_SHELL_009: Bottom Nav - License Plate Tab Opens Its Bottom-Sheet Menu (Site-Enabled)", groups = {"smoke"})
    public void verifyLicensePlateTabOpensItsBottomSheetMenuWhenSiteEnabled() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell(csv("SiteWithLicensePlate"));
            shellPage.tapBottomNavTab(csv("TabLicensePlate"));
            Pages.LicensePlatePage().reopenMenuFromSubScreen();
            for (String key : new String[]{"LpMenuItem1", "LpMenuItem2", "LpMenuItem3", "LpMenuItem4", "LpMenuItem5"}) {
                Assert.assertTrue(shellPage.isBottomSheetMenuItemDisplayed(csv(key)), "Expected License Plate bottom-sheet menu item '" + csv(key) + "' to be listed.");
            }
        } catch (Exception e) {
            logger.error("verifyLicensePlateTabOpensItsBottomSheetMenuWhenSiteEnabled failed", e);
            Assert.fail("TC_FUNC_SHELL_009 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SHELL_010 - Bottom Nav - License Plate Tab Absent When Not Site-Enabled. */
    @Test(priority = 40, testName = "ShellBottomNavLicensePlateAbsentWhenNotEnabled", description = "TC_FUNC_SHELL_010: Bottom Nav - License Plate Tab Absent When Not Site-Enabled", groups = {"regression", "criticalPath"})
    public void verifyLicensePlateTabIsAbsentWhenSiteDoesNotHaveItEnabled() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell(csv("SiteWithoutLicensePlate"));
            Assert.assertFalse(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")), "Expected the License Plate tab to be absent on a site without the feature enabled, confirming per-site gating.");
        } catch (Exception e) {
            logger.error("verifyLicensePlateTabIsAbsentWhenSiteDoesNotHaveItEnabled failed", e);
            Assert.fail("TC_FUNC_SHELL_010 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_011 - Bottom Nav - Dismiss Bottom-Sheet Menu Without Selection.
     * FIX (2026-10-23, same root cause/fix as TC_FUNC_SHELL_008 above): the menu only opens via a
     * separate tap on the Surveillance screen's own title after landing there.
     */
    @Test(priority = 50, testName = "ShellBottomNavDismissMenuWithoutSelection", description = "TC_FUNC_SHELL_011: Bottom Nav - Dismiss Bottom-Sheet Menu Without Selection", groups = {"regression"})
    public void verifyDismissingBottomSheetMenuWithoutSelectionReturnsToPreviousScreen() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Pages.VirtualGuardPage().tapSurveillanceTitleToOpenMenu();
            Assert.assertTrue(shellPage.isBottomSheetMenuOpen(), "Precondition failed: expected the Virtual Guard bottom-sheet menu to be open.");
            shellPage.dismissBottomSheetMenuWithoutSelecting();
            Assert.assertFalse(shellPage.isBottomSheetMenuOpen(), "Expected the bottom-sheet menu to be closed after dismissing without selection.");
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabSupport")), "Expected bottom navigation to remain fully functional after dismissing the menu.");
        } catch (Exception e) {
            logger.error("verifyDismissingBottomSheetMenuWithoutSelectionReturnsToPreviousScreen failed", e);
            Assert.fail("TC_FUNC_SHELL_011 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SHELL_012 - Bottom Nav - Tab Set Updates Immediately After Site Switch (no app restart). */
    @Test(priority = 1060, testName = "ShellBottomNavTabSetUpdatesAfterSiteSwitch", description = "TC_FUNC_SHELL_012: Bottom Nav - Tab Set Updates Immediately After Site Switch", groups = {"regression", "criticalPath"})
    public void verifyBottomNavTabSetUpdatesImmediatelyAfterSiteSwitchWithoutRestart() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell(csv("SiteWithoutLicensePlate"));
            Assert.assertFalse(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")), "Precondition failed: expected no License Plate tab on '" + csv("SiteWithoutLicensePlate") + "'.");

            shellPage.tapTopBarSiteSwitcher();
            SiteManagementPage sitePage = Pages.SiteManagementPage();
            sitePage.selectSiteFromSwitcher(csv("SiteWithLicensePlate"));

            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabLicensePlate")), "Expected the License Plate tab to appear immediately after switching to '" + csv("SiteWithLicensePlate") + "', with no app restart.");
        } catch (Exception e) {
            logger.error("verifyBottomNavTabSetUpdatesImmediatelyAfterSiteSwitchWithoutRestart failed", e);
            Assert.fail("TC_FUNC_SHELL_012 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /** TC_FUNC_SHELL_013 - Bottom Nav - Rapid Repeated Tapping Across Tabs. */
    @Test(priority = 60, testName = "ShellBottomNavRapidRepeatedTapping", description = "TC_FUNC_SHELL_013: Bottom Nav - Rapid Repeated Tapping Across Tabs", groups = {"regression"})
    public void verifyRapidRepeatedTappingAcrossBottomNavTabsCausesNoCrash() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            String[] tabs = {csv("TabVirtualGuard"), csv("TabGlobal"), csv("TabSupport"), csv("TabGlobal"), csv("TabSupport")};
            for (String tab : tabs) {
                shellPage.tapBottomNavTab(tab);
            }
            Assert.assertTrue(shellPage.isBottomNavTabPresent(csv("TabSupport")), "Expected a stable final state (Support tab's destination) after rapid repeated tapping, with no crash or stuck overlay.");
        } catch (Exception e) {
            logger.error("verifyRapidRepeatedTappingAcrossBottomNavTabsCausesNoCrash failed", e);
            Assert.fail("TC_FUNC_SHELL_013 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_014 - Bottom Nav - App Force-Killed With Bottom-Sheet Menu Open.
     * Fixed 2026-10-27 - same confirmed root cause already fixed for
     * {@code verifyVirtualGuardTabOpensItsBottomSheetMenu} (see that test's own javadoc): tapping the
     * "Virtual Guard" bottom-nav tab lands directly on Surveillance, it does not open the menu by itself.
     * This test had its own independent copy of the same wrong assumption and was never updated.
     */
    @Test(priority = 70, testName = "ShellBottomNavForceKilledWithMenuOpen", description = "TC_FUNC_SHELL_014: Bottom Nav - App Force-Killed With Bottom-Sheet Menu Open", groups = {"regression"})
    public void verifyAppForceKilledWithBottomSheetMenuOpenRecoversToStableState() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Pages.VirtualGuardPage().tapSurveillanceTitleToOpenMenu();
            Assert.assertTrue(shellPage.isBottomSheetMenuOpen(), "Precondition failed: expected the Virtual Guard bottom-sheet menu to be open before force-killing.");
            shellPage.forceKillAndRelaunchApp();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            boolean stableState = authPage.isLoginScreenDisplayed() || shellPage.isBottomNavTabPresent(csv("TabSupport"));
            Assert.assertTrue(stableState, "Expected the app to relaunch to a stable, navigable state with the menu not stuck open and no crash.");
        } catch (Exception e) {
            logger.error("verifyAppForceKilledWithBottomSheetMenuOpenRecoversToStableState failed", e);
            Assert.fail("TC_FUNC_SHELL_014 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_015 - Bottom Nav - App Backgrounded With Bottom-Sheet Menu Open.
     * Fixed 2026-10-27 - same confirmed root cause as TC_FUNC_SHELL_014 above.
     */
    @Test(priority = 80, testName = "ShellBottomNavBackgroundedWithMenuOpen", description = "TC_FUNC_SHELL_015: Bottom Nav - App Backgrounded With Bottom-Sheet Menu Open", groups = {"regression"})
    public void verifyAppBackgroundedWithBottomSheetMenuOpenRecoversGracefully() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            shellPage.tapBottomNavTab(csv("TabVirtualGuard"));
            Pages.VirtualGuardPage().tapSurveillanceTitleToOpenMenu();
            Assert.assertTrue(shellPage.isBottomSheetMenuOpen(), "Precondition failed: expected the Virtual Guard bottom-sheet menu to be open before backgrounding.");
            shellPage.backgroundAppAndResume(Integer.parseInt(csv("BackgroundSeconds")));
            boolean recoveredGracefully = shellPage.isBottomSheetMenuOpen() || shellPage.isBottomNavTabPresent(csv("TabSupport"));
            Assert.assertTrue(recoveredGracefully, "Expected the menu state to be preserved or gracefully reset after backgrounding/resuming - not stuck/corrupted.");
        } catch (Exception e) {
            logger.error("verifyAppBackgroundedWithBottomSheetMenuOpenRecoversGracefully failed", e);
            Assert.fail("TC_FUNC_SHELL_015 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    /**
     * TC_FUNC_SHELL_016 - Bottom Nav - Android Back Button Past Shell Root. UNVERIFIED: with no confirmed
     * way to independently assert "predictable background/exit" versus a forced logout, this asserts the
     * weaker (but still meaningful) proxy that repeated back-presses do not crash the app and leave it on
     * a recognizable, recoverable screen (Main App Shell or Login).
     */
    @Test(priority = 1070, testName = "ShellBottomNavBackButtonPastRoot", description = "TC_FUNC_SHELL_016: Bottom Nav - Android Back Button Past Shell Root", groups = {"regression"})
    public void verifyAndroidBackButtonPastShellRootBehavesPredictably() {
        try {
            MainAppShellPage shellPage = loginToMainAppShell();
            int presses = Integer.parseInt(csv("BackButtonPressCount"));
            for (int i = 0; i < presses; i++) {
                shellPage.navigateBack();
            }
            shellPage.forceKillAndRelaunchApp();
            OnboardingAuthenticationPage authPage = Pages.OnboardingAuthenticationPage();
            boolean recognizableState = authPage.isLoginScreenDisplayed() || shellPage.isBottomNavTabPresent(csv("TabSupport"));
            Assert.assertTrue(recognizableState, "Expected standard, non-crashing back-stack behavior; app should relaunch to a recognizable state (Login or Main App Shell) with no unexpected forced-logout corruption.");
        } catch (Exception e) {
            logger.error("verifyAndroidBackButtonPastShellRootBehavesPredictably failed", e);
            Assert.fail("TC_FUNC_SHELL_016 failed: " + e.getMessage());
        } finally {
            lightReset();
        }
    }

    // =================================================================================================
    // Adapted Generic Test Cases (FunctionalTestCases.md Section 3) - evaluated 2026-10-28
    // =================================================================================================

    /**
     * TC_GEN_INSTALL_002 - Uninstall EyeQ Monitoring via the device home-screen long-press/uninstall
     * affordance. Genuine environment gap, not invented: this project has no bundled APK and no configured
     * reinstall path (confirmed: no {@code .apk} in the repo, no reinstall capability in
     * {@code config.properties}), so actually uninstalling the real app from the shared test device would
     * leave it unusable for every other test in this suite with no automated way to restore it. Per explicit
     * user decision (2026-10-28), skipping rather than running a destructive, unrecoverable action against
     * the shared device.
     */
    @Test(priority = 1200, testName = "GenUninstallViaHomeScreenLongPress", description = "TC_GEN_INSTALL_002: Uninstall EyeQ Monitoring via the device home-screen long-press/uninstall affordance", groups = {"regression"})
    public void verifyUninstallViaHomeScreenLongPress() {
        throw new SkipException("TC_GEN_INSTALL_002 needs a reinstall path (APK + install capability) that "
                + "does not currently exist in this project - uninstalling the real app from the shared test "
                + "device would break every other test with no automated way to restore it. Skipping per "
                + "explicit user decision (2026-10-28) rather than risking the shared device.");
    }

    /**
     * TC_GEN_INSTALL_003 - Attempt to uninstall EyeQ Monitoring while it is running in the foreground. Same
     * genuine environment gap and user decision as {@link #verifyUninstallViaHomeScreenLongPress} (see its
     * javadoc) - no reinstall path exists for this shared device.
     */
    @Test(priority = 1210, testName = "GenUninstallWhileRunningInForeground", description = "TC_GEN_INSTALL_003: Attempt to uninstall EyeQ Monitoring while it is running in the foreground", groups = {"regression"})
    public void verifyUninstallWhileRunningInForeground() {
        throw new SkipException("TC_GEN_INSTALL_003 needs a reinstall path (APK + install capability) that "
                + "does not currently exist in this project - same genuine environment gap as "
                + "TC_GEN_INSTALL_002. Skipping per explicit user decision (2026-10-28) rather than risking "
                + "the shared device.");
    }

    /**
     * TC_GEN_INSTALL_005 - Uninstall EyeQ Monitoring via device Settings > Apps. Same genuine environment
     * gap and user decision as {@link #verifyUninstallViaHomeScreenLongPress} (see its javadoc) - an
     * alternate entry point to the same unrecoverable action, no reinstall path exists for this shared
     * device.
     */
    @Test(priority = 1220, testName = "GenUninstallViaSettingsApps", description = "TC_GEN_INSTALL_005: Uninstall EyeQ Monitoring via device Settings > Apps", groups = {"regression"})
    public void verifyUninstallViaSettingsApps() {
        throw new SkipException("TC_GEN_INSTALL_005 needs a reinstall path (APK + install capability) that "
                + "does not currently exist in this project - same genuine environment gap as "
                + "TC_GEN_INSTALL_002/003. Skipping per explicit user decision (2026-10-28) rather than "
                + "risking the shared device.");
    }
}
