package com.cresanmanagement.eyeq.pageobjects.virtualguard;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only - no business logic. Android-only project: @AndroidFindBy only.
 *
 * <p><b>Evidence basis:</b> written in CODE-WRITING-ONLY mode (2026-10-19 strategy directive - no live
 * device/Appium sessions this pass). Every locator below is UNVERIFIED, desk-sourced from
 * {@code 07-outputs/RequirementAnalysis/RequirementAnalysis.md} Section 10.4 (FR-VG-001 to 006) and
 * {@code 07-outputs/TestCases/FunctionalTestCases.md}'s TC_FUNC_VG_* rows / {@code UITestCases.md}'s
 * TC_UI_VG_* rows, per-locator citations below.
 *
 * <p><b>IMPORTANT GAPS FLAGGED (not silently resolved):</b>
 * <ul>
 *   <li>KPI tile internal structure (label vs. value as separate nodes) is NOT documented anywhere -
 *       {@code getKpiTileValue} is a best-effort whole-element-text read, not an isolated numeric value.</li>
 *   <li>Activity Log row/entry structure is NOT documented - {@code activityLogEntryXpath} is a best-effort
 *       guess, not a confirmed container shape.</li>
 *   <li>Schedule bottom sheet's 7-day toggle row: TC_UI_VG_146 ITSELF explicitly flags "Th/Su labels not
 *       resolved in metadata, likely abbreviated/overlapping" - i.e. the project's own Figma-sourced
 *       evidence already confirms day labels cannot be trusted as unique text. {@code DAY_TOGGLE_XPATH_TEMPLATE}
 *       therefore targets toggles by POSITION (1st-7th), not by day-letter text, consistent with that
 *       warning - a live pass must still confirm the toggle control's actual widget class (modeled here as
 *       {@code android.widget.Switch}, a reasonable but unconfirmed Android convention).</li>
 * </ul>
 */
public class VirtualGuardPageObjectRepository extends Object_Base {

    public VirtualGuardPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // =================================================================================================
    // Surveillance (FR-VG-002)
    // =================================================================================================

    /** UNVERIFIED. TC_UI_VG_013 confirms exact copy "Surveillance" as this screen's heading. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Surveillance']")
    public WebElement surveillanceScreenTitle;

    /**
     * UNVERIFIED for Surveillance specifically, but reused from the LIVE-VERIFIED (2026-10-19, Test
     * backend) clickable-View-wrapping-a-non-clickable-TextView-descendant shape confirmed for Site
     * Selection rows and bottom-nav tabs (see {@code SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE}'s
     * and {@code MainAppShellPageObjectRepository.BOTTOM_NAV_TAB_XPATH_TEMPLATE}'s javadoc for the
     * correction history - the row View's own {@code text} attribute is always empty, NOT matchable
     * directly). {@code VirtualGuardPage} builds this dynamically per Camera Group name (CSV-driven).
     */
    public static final String CAMERA_GROUP_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'%s')]]";

    // =================================================================================================
    // Guarding Stats Dashboard (FR-VG-003)
    // =================================================================================================

    /**
     * CORRECTED 2026-10-26 (was "Guarding stats", lower-case 's' - an UITestCases.md/Figma-sourced
     * exact-copy quote that turned out NOT to match the real rendered app). LIVE-CONFIRMED via a real
     * device crawl (Main Site): the real on-screen heading is "Guarding Stats" (capital S). This was the
     * confirmed root cause of every Guarding Stats navigation/content test failing - the case-sensitive
     * exact-text xpath never matched the real title, so {@code isGuardingStatsScreenDisplayed()} always
     * returned false.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Guarding Stats']")
    public WebElement guardingStatsScreenTitle;

    /** UNVERIFIED. TC_UI_VG_100 confirms exact copy "Guarding Incidents". */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Guarding Incidents']")
    public WebElement guardingIncidentsHeader;

    /** UNVERIFIED. TC_UI_VG_100/103 confirm exact copy "Newest at top" as the incident list's default sort-state label. */
    @AndroidFindBy(xpath = "//*[@text='Newest at top']")
    public WebElement newestAtTopLabel;

    /**
     * CORRECTED 2026-10-26 (was a dangerously generic "any clickable View wrapping any TextView" guess -
     * confirmed live to be the wrong kind of bug: it could match the FIRST such element anywhere on the
     * page, e.g. the TopBar site switcher, not the month badge). LIVE-CONFIRMED via a real device crawl
     * (Main Site, Guarding Stats dashboard): the month badge reads "{Month}, {Year}" (e.g. "October, 2026")
     * and is wrapped in a clickable ancestor View, the same confirmed shape used project-wide. Tapping it
     * opens a real picker revealing multiple past months at once ("October, 2026"/"September, 2026"/
     * "August, 2026"/"July, 2026"/"June, 2026"/"May, 2026" were all simultaneously visible, no scroll
     * needed for that range) - matched here by the date-shaped substring ", 20" rather than hardcoding
     * today's specific month, so this keeps working as real time passes.
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text, ', 20')]]")
    public WebElement monthSelectorControl;

    /**
     * UNVERIFIED placeholder. No confirmed per-tile structure exists (label text vs. numeric value as
     * separate nodes is undocumented) - {@code VirtualGuardPage#getKpiTileValue} builds this dynamically
     * per KPI label (FR-VG-003's 5 confirmed names: "Total Alarm Events", "Average alarms/hour", "Average
     * alarms/day", "Average initial response time", "% alarms closed under 90 seconds") and reads the
     * MATCHED element's own raw text, which may include the label itself concatenated with its value if
     * they are not actually separate nodes - a live pass must confirm/correct this.
     */
    public static final String KPI_TILE_XPATH_TEMPLATE = "//*[contains(@text,'%s')]";

    /**
     * UNVERIFIED placeholder - TC_UI_VG_102 confirms a "sort control (phosphor/sort-ascending)" icon
     * exists but gives no resource-id. Not exercised by any TC_FUNC_VG_* row in scope here (no FUNCTIONAL
     * case tests the sort control itself), kept only for completeness/future use.
     */
    @AndroidFindBy(xpath = "//android.widget.ImageView[contains(@content-desc,'sort')]")
    public WebElement guardingIncidentsSortControl;

    // =================================================================================================
    // Forensic Clips (FR-VG-006)
    // =================================================================================================

    /** UNVERIFIED. TC_UI_VG_019/025 confirm exact copy "Forensic Clips" as this screen's heading. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Forensic Clips']")
    public WebElement forensicClipsScreenTitle;

    // =================================================================================================
    // Arm/Disarm (FR-VG-004)
    // =================================================================================================

    /** UNVERIFIED. TC_UI_VG_113/119/125/131/137 all confirm exact copy "Arm/Disarm" as this screen's heading. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Arm/Disarm']")
    public WebElement armDisarmScreenTitle;

    /**
     * UNVERIFIED placeholder. No single confirmed resource-id exists for the current status line; modeled
     * as any element whose text contains "Armed" or "Disarmed" (both confirmed substrings appear across
     * every captured status variant: "Armed by Sarah Johnson until...", "Armed by schedule until...",
     * "Disarmed by Sarah Johnson until...", "Disarmed by schedule until..." - TC_UI_VG_113/119/125/131).
     */
    @AndroidFindBy(xpath = "//*[contains(@text,'Armed') or contains(@text,'Disarmed')]")
    public WebElement armDisarmStatusText;

    /**
     * UNVERIFIED structural pattern. {@code VirtualGuardPage} builds this dynamically for either "Arm" or
     * "Disarm" (the button's label is assumed to toggle with current state, standard for this kind of
     * control, though never independently confirmed).
     */
    public static final String ARM_DISARM_BUTTON_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='%s'] or @text='%s']";

    /** UNVERIFIED. TC_UI_VG_137/140 confirm the exact helper-text phrase "Arming is available from 7:00 PM, based on your schedule" for the disabled/early-window state - matched here as a contains() prefix since the time itself is data-driven, not hardcoded. */
    @AndroidFindBy(xpath = "//*[contains(@text,'Arming is available from')]")
    public WebElement armHelperText;

    /** UNVERIFIED. TC_UI_VG_113/119/125/131/137 confirm exact copy "Activity Log" as this section's heading. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Activity Log']")
    public WebElement activityLogHeader;

    /**
     * CORRECTED 2026-10-26 (the old guess - a TextView required to contain a TextView descendant - is
     * structurally impossible in Android's view tree and always matched zero elements, silently making
     * {@code getActivityLogEntryCount()} always return 0). LIVE-CONFIRMED real structure via a real device
     * crawl (Main Site): each Activity Log entry renders as 3 consecutive plain TextView lines - a
     * timestamp ("October, 21 2026, 1:14 PM"), an actor/action line, and a reason line - with no shared
     * clickable/row container to anchor on. Counts the timestamp line specifically (the only one of the
     * three confirmed to always contain " AM" or " PM"), giving one match per real entry.
     */
    public static final String ACTIVITY_LOG_ENTRY_XPATH = "//android.widget.TextView[@text='Activity Log']/following::android.widget.TextView[contains(@text,' AM') or contains(@text,' PM')]";

    // =================================================================================================
    // Arm/Disarm Schedule bottom sheet (FR-VG-005)
    // =================================================================================================

    /**
     * CORRECTED 2026-10-26 (was a direct {@code @text='View Schedule'} match on the TextView itself).
     * LIVE-CONFIRMED via a real device crawl (Main Site Arm/Disarm screen dump): the "View Schedule" text
     * is a {@code clickable="false"} TextView wrapped inside a {@code clickable="true"} ancestor View - the
     * same clickable-View-wraps-non-clickable-TextView shape already confirmed project-wide for row/tab/
     * title controls. A raw click directly on the old TextView-only locator did not reliably open the
     * sheet during the live crawl; anchoring on the real clickable ancestor instead.
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='View Schedule']]")
    public WebElement viewScheduleControl;

    /** UNVERIFIED. TC_UI_VG_144 confirms exact copy "Armed" as one segment of the segmented control. */
    @AndroidFindBy(xpath = "//*[@text='Armed']")
    public WebElement scheduleArmedSegment;

    /** UNVERIFIED. TC_UI_VG_144 confirms exact copy "Disarmed" as the other segment of the segmented control. */
    @AndroidFindBy(xpath = "//*[@text='Disarmed']")
    public WebElement scheduleDisarmedSegment;

    /**
     * CORRECTED 2026-10-26 (was modeled as {@code android.widget.Switch} - LIVE-CONFIRMED via a real device
     * crawl that ZERO such elements exist; the day row is rendered as 7 plain {@code android.widget.TextView}
     * single-letter labels, all 7 sharing ONE common non-clickable parent View, with no individual
     * per-day clickable wrapper). Targets the Nth (1-based) day letter by POSITION, not text, since the
     * labels are confirmed-ambiguous duplicates (S/M/T/W/T/F/S - TC_UI_VG_146's own "Th/Su not resolved"
     * warning is now fully confirmed live). A raw tap on the TextView itself (not requiring a clickable
     * ancestor) is confirmed to execute without error, but LIVE-CONFIRMED to produce NO detectable change
     * anywhere in the accessibility tree afterward (no {@code selected}/{@code checked} attribute, on
     * either the label or its shared parent) - see {@code VirtualGuardPage#isScheduleDayToggledOn}'s
     * javadoc for the full implication.
     */
    public static final String DAY_TOGGLE_XPATH_TEMPLATE = "(//android.widget.TextView[@text='S' or @text='M' or @text='T' or @text='W' or @text='F'])[%d]";
}
