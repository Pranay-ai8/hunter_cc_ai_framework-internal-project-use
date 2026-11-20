package com.cresanmanagement.eyeq.pageobjects.mainappshell;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only - no business logic. Android-only project: @AndroidFindBy only.
 *
 * <p><b>Evidence basis:</b> written in CODE-WRITING-ONLY mode (2026-10-19 strategy directive - no live
 * device/Appium sessions this pass). Nothing in this module has been independently live-inspected the way
 * Site Management's first-run Site Selection screen was; every locator below is UNVERIFIED, desk-sourced
 * from {@code 07-outputs/RequirementAnalysis/RequirementAnalysis.md} Section 10.3 (FR-SHELL-001/002) and
 * {@code 07-outputs/TestCases/UITestCases.md}'s TC_UI_SUP_001/003/009, TC_UI_VG_001/003/007,
 * TC_UI_LP_001/005, and TC_UI_ACCT_001 rows (the latter only far enough to confirm arrival at Account
 * Settings - the Account Settings module itself is out of scope here and will get its own
 * Page Object/Page Action Class).
 *
 * <p><b>IMPORTANT EVIDENCE CONFLICTS (flagged per the project's "report conflicts, do not assume" rule,
 * not silently resolved):</b>
 * <ul>
 *   <li><b>Bottom-nav tab labels</b>: a Live UI Hierarchy Verification crawl of the Site Selection screen
 *       (done for the Site Management module, see {@code SiteManagementPage#countVisibleSiteRows}'s
 *       javadoc) observed the bottom nav already rendering underneath that screen with LIVE-CONFIRMED
 *       clickable-View labels "Virtual Guard", "Global", "Support" (no License Plate tab on that
 *       particular, production-contaminated site). TC_UI_SUP_001's Figma-sourced copy instead describes
 *       four tabs labelled "Guard, LPM, Global, Support" - i.e. abbreviated. "Guard" is a substring of
 *       "Virtual Guard" so the {@code contains()} xpath pattern below tolerates either reading for that
 *       tab; "LPM" is NOT a substring of "License Plate" and does NOT share any tolerant match, so the
 *       License Plate locator below defaults to the full "License Plate" wording used consistently
 *       throughout RequirementAnalysis.md/FunctionalTestCases.md, flagged here as needing live
 *       confirmation - if the real app renders "LPM", {@code mainappshell.csv}'s {@code TabLicensePlate}
 *       value MUST be corrected on the next live pass, since a silent mismatch would make every License
 *       Plate bottom-nav test report a false "tab absent".</li>
 *   <li><b>Virtual Guard bottom-sheet menu items</b>: FR-SHELL-002/TC_FUNC_SHELL_008 (and
 *       FR-VG-001) describe the menu as "Surveillance, Guarding Stats, Forensic Clips, Contact, and
 *       Arm/Disarm" (5 items). TC_UI_VG_007's own Figma-sourced copy for the equivalent "Bottom Sheet
 *       Open (5 items)" screen lists only "Virtual Guard" (heading), "Camera Views", "Guarding Stats",
 *       "Forensic Clips" - i.e. "Camera Views" where the functional spec says "Surveillance", and no
 *       explicit mention of "Contact"/"Arm/Disarm" rows (though the screen is independently titled "(5
 *       items)", consistent with all 5 functional-spec items actually being present, just not all quoted
 *       in that particular UI test case's expected-copy list). This module's CSV/locators use the
 *       functional-spec wording ("Surveillance") as primary since TC_FUNC_SHELL_008 is the case being
 *       implemented here; a live pass MUST confirm whether the rendered label is actually "Surveillance"
 *       or "Camera Views".</li>
 *   <li><b>License Plate bottom-sheet menu items</b>: FR-SHELL-002/TC_FUNC_SHELL_009 and TC_UI_LP_001
 *       agree exactly - "Plate Scans, Vehicles, Lists Management, Cycle Time Tracking, Notification
 *       Settings" - no conflict here.</li>
 * </ul>
 */
public class MainAppShellPageObjectRepository extends Object_Base {

    public MainAppShellPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // =================================================================================================
    // Persistent Top Bar (FR-SHELL-001) - UNVERIFIED
    // =================================================================================================

    /**
     * LIVE-VERIFIED 2026-10-20 - see {@code SiteManagementPageObjectRepository#topBarSiteSwitcherControl}'s
     * javadoc for the full evidence basis (duplicated here since this module, not Site Management, owns
     * the FR-SHELL-001 requirement the control belongs to). The real control is the clickable View
     * immediately preceding the real Profile ImageView in the TopBar - the original guess (requiring an
     * ImageView inside the site control itself) matched zero elements live.
     *
     * <p><b>MITIGATION (2026-10-23, root-caused from repeated real failures, NOT a confirmed full fix):</b>
     * this locator is confirmed to intermittently time out for the full 90s DEFAULT_WAIT even though the
     * same real TopBar structure is confirmed live, repeatedly, to be present and unchanged at the moment
     * of failure (byte-for-byte identical to passing runs) - the same unexplained class of Appium/
     * UiAutomator2 XPath-engine flakiness already seen for the Virtual Guard menu's "Forensic Clips" row
     * (see {@code VirtualGuardPage#tapCurrentScreenTitleToOpenMenu}'s javadoc). Switched from a
     * {@code preceding-sibling::} (backward-axis) query to the semantically-equivalent
     * {@code following-sibling::} (forward-axis) form below, targeting the exact same element - forward-axis
     * XPath queries are generally simpler for an XPath engine to evaluate than backward-axis ones (no
     * reverse-index/full-subtree-scan needed), which may avoid whatever specific engine behavior causes the
     * intermittent failure. This is a reasoned, technically-justified mitigation attempt based on that
     * general XPath-engine principle, not a confirmed root cause fix - if it still fails intermittently,
     * the real cause is in Appium/UiAutomator2 itself, not in this project's locator choice.
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][following-sibling::android.widget.ImageView[@content-desc='Profile']]")
    public WebElement topBarSiteSwitcherControl;

    /**
     * LIVE-VERIFIED 2026-10-20 (real device {@code 8BQX1DY81}, site "Amalthea") via
     * {@code docs/dom-snapshots/acctsettings_live_00_main_shell_before_tap.xml}. The real control is an
     * {@code android.widget.ImageView} with a stable, non-data-driven {@code content-desc="Profile"} -
     * the original UNVERIFIED guess (a clickable View wrapping an ImageView with no TextView) matched
     * ZERO elements live; this app's TopBar ImageView is directly clickable itself (same "Compose button
     * pattern B" already confirmed elsewhere in this project), not wrapped by a separate clickable ancestor.
     */
    @AndroidFindBy(xpath = "//android.widget.ImageView[@content-desc='Profile']")
    public WebElement topBarProfileIcon;

    /**
     * LIVE-VERIFIED 2026-10-20 via a manual adb crawl: tapping the Profile icon opens Account Settings,
     * confirmed to show exactly "General Information", "Security & Access", "About" rows and a "Log out"
     * row below a real avatar/name/email header. A real, stable resource-id {@code accountSettingsContainer}
     * exists for the whole overlay and is the preferred signal (used by {@code isAccountSettingsScreenDisplayed}).
     * This row-text locator is kept only as a fallback/secondary confirmation. Only used here to CONFIRM
     * ARRIVAL at Account Settings (this module's own scope per FR-SHELL-001); the Account Settings module
     * itself has its own Page Object/Page Action Class with its own locators for operating within that screen.
     */
    @AndroidFindBy(xpath = "//*[@text='General Information' or @text='General Info']")
    public WebElement accountSettingsGeneralInformationRow;

    /** LIVE-VERIFIED real, stable resource-id for the whole Account Settings overlay container. */
    @AndroidFindBy(id = "com.cresanmanagement.eyeq:id/accountSettingsContainer")
    public WebElement accountSettingsContainer;

    // =================================================================================================
    // Persistent Bottom Navigation (FR-SHELL-002) - UNVERIFIED
    // =================================================================================================

    /**
     * LIVE-VERIFIED on the confirmed Test backend (2026-10-19 - see
     * {@code docs/dom-snapshots/virtualguard_live_00_site_selection.xml}): each bottom-nav tab is a
     * clickable {@code android.view.View} CONTAINING a non-clickable {@code android.widget.TextView}
     * descendant whose own {@code text} is the tab's label (e.g. "Virtual Guard"/"Global"/"Support" all
     * confirmed rendering this exact shape) - the row View's own {@code text} attribute is always empty.
     *
     * <p><b>CORRECTION (2026-10-19):</b> this template previously (and WRONGLY) matched on the tab View's
     * own {@code text} attribute directly, the same bug found and fixed in
     * {@code SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE} - see that field's javadoc for
     * the full correction history; both templates describe the same underlying app-wide "clickable row"
     * Compose component. {@code MainAppShellPage} builds this xpath dynamically per tab label (CSV-driven,
     * never hardcoded) rather than exposing one static field per tab, since which tabs exist is itself
     * per-site data (FR-SHELL-002's feature-gating).
     */
    public static final String BOTTOM_NAV_TAB_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'%s')]]";

    /**
     * UNVERIFIED (not yet independently confirmed for the bottom-sheet menu specifically - only the
     * Site Selection screen and the bottom-nav tabs have been live-confirmed so far), but modeled on the
     * SAME now-corrected clickable-View-wrapping-a-TextView pattern shared by every other row-shaped
     * control confirmed in this app (see {@code BOTTOM_NAV_TAB_XPATH_TEMPLATE}'s javadoc), since bottom
     * sheet menu rows are a reasonable candidate for reusing the same underlying list-row component.
     * Built dynamically per row label by {@code MainAppShellPage}.
     */
    public static final String BOTTOM_SHEET_MENU_ITEM_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'%s')]]";

    /**
     * UNVERIFIED placeholder for "some bottom-sheet menu is currently open" - no confirmed sheet title or
     * container resource-id exists in evidence. Modeled narrowly as the presence of the License Plate
     * menu's first confirmed-copy item ("Vehicle Scans") OR the Virtual Guard menu's first functional-spec
     * item ("Surveillance"), since no single page-agnostic "a bottom sheet is open" signal is documented.
     * FIX (2026-10-27, real test failure: {@code verifyLicensePlateTabOpensItsBottomSheetMenuWhenSiteEnabled}
     * leaked its open menu into the next test): this previously read "Plate Scans" - a stale, pre-correction
     * label. The authoritative, LIVE-CONFIRMED License Plate menu-item/screen-title text is "Vehicle Scans"
     * (see licenseplate.csv's LpMenuItem1 resolution notes), so the old text never matched and this check
     * always reported the License Plate menu as closed, even while it was genuinely open.
     * Used only as a best-effort dismiss-without-selection check (TC_FUNC_SHELL_011); a live pass should
     * replace this with a real sheet-container locator once one is confirmed.
     */
    @AndroidFindBy(xpath = "//*[@text='Vehicle Scans' or @text='Surveillance' or @text='Camera Views']")
    public WebElement anyBottomSheetMenuItemSignal;
}
