package com.cresanmanagement.eyeq.pageobjects.licenseplate;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only - no business logic. Android-only project: @AndroidFindBy only.
 *
 * <p><b>Evidence basis (LIVE-VERIFIED 2026-10-19/20, real device {@code 8AHY0LBTH}, confirmed Test backend,
 * real QA account on real site "Amalthea"):</b> this module was originally written in CODE-WRITING-ONLY
 * mode, then live-crawled end to end. Raw dumps: {@code docs/dom-snapshots/licenseplate_live_*.xml}. Every
 * field below is marked LIVE-VERIFIED (directly observed) or UNVERIFIED (still desk-sourced) individually.
 *
 * <p><b>FINAL RESOLUTION (2026-10-26, per explicit user confirmation):</b> the correct, authoritative,
 * consistent real screen/menu-item text is "<b>Vehicle Scans</b>" - confirmed directly by the user. The
 * two correction passes below (2026-10-22's "Plate Scans" on udid 8BQX1DY81, and a subsequent brief
 * OR-tolerant/either-value mitigation on 2026-10-26 after a THIRD device, udid OZRK5HK77PGI4HR8, showed
 * "Vehicle Scans" again) are both superseded: the single "Plate Scans" observation on 8BQX1DY81 is a
 * one-off anomaly (a stale cache, a different app build installed on that one device, or similar) - NOT a
 * real variant automation needs to tolerate. {@link #vehicleScansFeedTitle}/
 * {@link #vehicleScansTitleClickableAncestor} are locked back to "Vehicle Scans" only; the OR-xpath
 * tolerance is removed.
 *
 * <p><b>HEADLINE LIVE FINDINGS (2026-10-19/20, udid 8AHY0LBTH):</b>
 * <ul>
 *   <li><b>Naming conflict RESOLVED:</b> the real screen/menu-item name is "<b>Vehicle Scans</b>", not
 *       "Plate Scans" - confirmed both as the bottom-nav-tab destination screen's own title AND as the
 *       menu item's label inside the real bottom sheet. FunctionalTestCases.md's "Plate Scans" naming
 *       throughout is wrong relative to the built app; UITestCases.md's Figma-sourced "Vehicle Scans" is
 *       correct. Re-confirmed independently on a third device (udid OZRK5HK77PGI4HR8) 2026-10-26, and per
 *       the user's own direct confirmation the same day - see the FINAL RESOLUTION note above.</li>
 *   <li><b>Menu-reopen mechanism RESOLVED:</b> tapping the "License Plate" bottom-nav tab navigates
 *       straight to the Vehicle Scans screen (does NOT open a menu) - identical to Virtual Guard's
 *       confirmed finding. The menu is opened by tapping the SCREEN'S OWN TITLE (a clickable
 *       {@code android.view.View} wrapping the title TextView, no resource-id on this screen, unlike
 *       Virtual Guard's {@code navTitleTextView}). The menu renders as a REAL Android
 *       {@code design_bottom_sheet} (standard Material BottomSheetDialog, resource-id
 *       {@code com.cresanmanagement.eyeq:id/design_bottom_sheet}), with header text "License Plate
 *       Management" (newly confirmed, not previously documented anywhere) and a real dismiss-scrim
 *       (resource-id {@code com.cresanmanagement.eyeq:id/touch_outside}).</li>
 *   <li><b>Row structural pattern RECONFIRMED:</b> every row (menu items, location filter options) is a
 *       clickable {@code android.view.View} wrapping a non-clickable {@code android.widget.TextView}
 *       descendant - the same pattern confirmed everywhere else in this app. Vehicle cards are a LARGER
 *       variant of the same shape: ONE clickable {@code android.view.View} wraps ALL 7 of that vehicle's
 *       field TextViews (not just one).</li>
 *   <li><b>Camera/Location Filter is NOT a separate bottom sheet reached via a filter icon</b> (the
 *       original desk-sourced guess was wrong). It is an INLINE, IN-PLACE EXPANDING DROPDOWN: a clickable
 *       {@code android.view.View} immediately following the search EditText, displaying the current
 *       selection as its own label text (defaults to "All Locations"). Tapping it expands a list of
 *       location rows (same row pattern) directly in place, pushing the stat tiles/list down - it does NOT
 *       open a new window/dialog. No "Camera" sub-filter option was observed in this dropdown (consistent
 *       with FR-LP-005 being "Partially Confirmed" re: Camera mechanics) - only Location options appeared.
 *       No "Apply"/"Clear" button was observed either; this UI shape suggests selection may apply
 *       immediately, which has NOT been independently confirmed (no option was actually selected during
 *       the crawl, only the dropdown's open state was observed).</li>
 *   <li><b>Vehicle Details field list CONTRADICTS the documented 6-field list.</b> RequirementAnalysis.md/
 *       FunctionalTestCases.md (TC_FUNC_LP_037-042) document: VIN, Plate/State (as ONE field), Current
 *       Location, Shop, Model, Year. Live evidence (2 real vehicle records) shows SEVEN separate fields:
 *       VIN, Plate, State, Location, Make, Model, Year - i.e. "Plate/State" is actually two separate
 *       fields ("Plate" and "State"), "Current Location" is labelled just "Location", a "Make" field
 *       exists that is NOT documented anywhere, and NO "Shop" field was found on either observed vehicle
 *       card. This is flagged as a genuine requirement-documentation discrepancy, not silently "fixed" -
 *       {@code LicensePlatePage#getFieldValue(SHOP_LABEL_UNCONFIRMED)} will correctly return null since the field does
 *       not exist, and TC_FUNC_LP_040 is updated to assert and document that absence rather than assume
 *       the documented field is correct.</li>
 *   <li><b>Checkpoint Timeline confirmed:</b> header text "Checkpoint Timeline"; confirmed EXACT empty-state
 *       copy "There are currently no checkpoints for this vehicle." (both vehicle records checked during
 *       the crawl had zero checkpoint history, so only the empty state was observed - the populated-state
 *       row shape remains UNVERIFIED).</li>
 *   <li><b>Real stat tile labels confirmed:</b> "Unique Vehicle Count" and "Total Vehicle Count" (in that
 *       left-to-right order) - each tile's numeric VALUE TextView immediately PRECEDES its label TextView
 *       in document order (both direct children of the same container), not a generic resource-id-based
 *       pattern as originally guessed.</li>
 * </ul>
 *
 * <p><b>STILL-DOCUMENTED GAP (confirmed unchanged by the crawl):</b> FR-LP-001's menu lists 5 items -
 * "Vehicle Scans", Vehicles, Lists Management, Cycle Time Tracking, Notification Settings (all 5 LIVE-
 * CONFIRMED present in the real bottom sheet) - but NO TC_FUNC_LP_* row anywhere exercises Cycle Time
 * Tracking or Notification Settings. This module still has NO locators/methods for those two screens -
 * writing any would invent UI not in evidence.
 */
public class LicensePlatePageObjectRepository extends Object_Base {

    public LicensePlatePageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // =================================================================================================
    // Shared row/list-item structural pattern (LIVE-VERIFIED - see class javadoc)
    // =================================================================================================

    /**
     * LIVE-VERIFIED (menu items, location filter options - docs/dom-snapshots/licenseplate_live_02b_*.xml,
     * licenseplate_live_03b_*.xml). Used as the default template for every single-field row-shaped control
     * in this module.
     */
    public static final String ROW_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'%s')]]";

    // =================================================================================================
    // License Plate Menu (FR-LP-001) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED. The real Material BottomSheetDialog container - confirmed resource-id. */
    @AndroidFindBy(xpath = "//*[@resource-id='com.cresanmanagement.eyeq:id/design_bottom_sheet']")
    public WebElement licensePlateMenuBottomSheet;

    /** LIVE-VERIFIED. The menu's own header text, newly discovered (not previously documented anywhere). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='License Plate Management']")
    public WebElement licensePlateMenuHeader;

    /** LIVE-VERIFIED. The scrim/backdrop that dismisses the menu bottom sheet when tapped outside its bounds - confirmed resource-id. */
    @AndroidFindBy(xpath = "//*[@resource-id='com.cresanmanagement.eyeq:id/touch_outside']")
    public WebElement licensePlateMenuDismissScrim;

    // =================================================================================================
    // Vehicle Scans feed (FR-LP-002) - FINAL RESOLUTION 2026-10-26 (per explicit user confirmation): the
    // correct, authoritative, consistent real text is "Vehicle Scans" every time. The 2026-10-22 "Plate
    // Scans" observation on udid 8BQX1DY81 is a one-off anomaly on that specific device (a stale cache, a
    // different app build installed there, or similar) - NOT a real variant automation needs to tolerate.
    // The brief OR-tolerant ("Plate Scans' or 'Vehicle Scans'") mitigation tried earlier the same day is
    // reverted; locators are locked back to "Vehicle Scans" only, consistent with the 2026-10-19/20 and
    // 2026-10-26 (udid OZRK5HK77PGI4HR8) live confirmations and the user's own direct confirmation.
    // =================================================================================================

    /** LIVE-VERIFIED (confirmed independently on multiple devices; see class javadoc's FINAL RESOLUTION note). Confirmed exact screen title (also the clickable menu-reopen trigger - see {@link #vehicleScansTitleClickableAncestor}). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Vehicle Scans']")
    public WebElement vehicleScansFeedTitle;

    /** LIVE-VERIFIED (confirmed independently on multiple devices; see class javadoc's FINAL RESOLUTION note). The clickable ancestor View wrapping the title - tapping THIS (not the TextView, which is itself non-clickable) opens the License Plate menu. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Vehicle Scans']]")
    public WebElement vehicleScansTitleClickableAncestor;

    /** LIVE-VERIFIED. Confirmed single-EditText-per-screen pattern. TC_FUNC_LP_012 - plate-substring search. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement vehicleScansSearchInput;

    /**
     * LIVE-VERIFIED structural pattern. The Location filter control is the clickable View immediately
     * following the search EditText (confirmed sibling relationship, NOT a separate filter icon as
     * originally guessed) - its own TextView label shows the CURRENT selection, defaulting to "All
     * Locations". Targeted by position (not by its changing text value) so it remains correct regardless
     * of the currently-applied filter.
     */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]/following-sibling::android.view.View[@clickable='true'][1]")
    public WebElement locationFilterControl;

    /**
     * LIVE-VERIFIED. Confirmed real default/reset option text inside the location dropdown (also doubles
     * as the filter control's own default label before any selection).
     */
    public static final String LOCATION_FILTER_ALL_OPTION_TEXT = "All Locations";

    /**
     * LIVE-VERIFIED structural pattern (see {@code ROW_XPATH_TEMPLATE}) for the inline-expanding location
     * dropdown's option rows (confirmed real options during the crawl: "All Locations", "Andy Test
     * Location", "Another Test Location", "Carwash", "Deck", "Service Drive", "Shop" - this last "Shop" is
     * a LOCATION name, unrelated to Vehicle Details' documented-but-unconfirmed "Shop" FIELD; do not
     * confuse the two).
     */
    public static final String LOCATION_OPTION_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    // =================================================================================================
    // Vehicles List's Camera/Location Filter (FR-LP-005, Vehicles List variant) - LIVE-VERIFIED 2026-10-27
    //
    // <p>The SAME {@link #locationFilterControl} click opens a STRUCTURALLY DIFFERENT UI here than on the
    // Vehicle Scans feed: not a flat dropdown of location names with an "All Locations" reset row, but a
    // full-screen Camera/Location Filter sheet - a hierarchical checkbox tree ("Select All Cameras" ->
    // "Group 1"/"Group 2" -> individual cameras e.g. "Front Entrance", "Back Entrance", "Parking Lot",
    // "Lobby", "Hallway - Flow 1/2") with "Filter" (apply+close) and "Clear" (uncheck all, sheet stays
    // open) buttons - confirmed live via logs/page-source-dumps/CRAWL_CF1_sheet_opened.xml and
    // CRAWL_CF5_after_tapping_clear.xml. This explains why verifyDismissingFilterWithoutSelectionLeavesListUnchanged
    // and verifyClearingAppliedFilterRevertsListToUnfiltered previously failed: they reused the Vehicle
    // Scans feed's "All Locations" dropdown assumptions, which structurally don't exist here. Confirmed
    // live: native {@code navigateBack()} dismisses the sheet without applying any change (no selection
    // persists, see CRAWL_CF8_after_native_back.xml).
    // =================================================================================================

    /** LIVE-VERIFIED. Confirmed real "apply and close" button text inside the Vehicles List's Camera/Location Filter sheet. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Filter']")
    public WebElement cameraFilterApplyButton;

    /** LIVE-VERIFIED. Confirmed real "uncheck all" button text (sheet stays open after tapping - {@link #cameraFilterApplyButton} must be tapped afterward to apply the cleared state). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Clear']")
    public WebElement cameraFilterClearButton;

    /** LIVE-VERIFIED. Confirmed real "select all" row text - used as the sheet's own open/expanded signal. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Select All Cameras']")
    public WebElement cameraFilterSelectAllCamerasLabel;

    /**
     * LIVE-VERIFIED structural pattern. Each camera/group row is a {@code CheckBox} immediately followed
     * (as a preceding-sibling from the label's perspective) by its own label TextView - confirmed live for
     * both group headers ("Group 1"/"Group 2") and individual cameras (e.g. "Front Entrance").
     */
    public static final String CAMERA_FILTER_CHECKBOX_XPATH_TEMPLATE =
            "//android.widget.TextView[@text='%s']/preceding-sibling::android.widget.CheckBox[1]";

    /**
     * LIVE-VERIFIED label text for the "Unique Vehicle Count" stat tile (left tile). The VALUE is the
     * PRECEDING sibling TextView (see {@code LicensePlatePage#getStatTileValue}).
     */
    public static final String UNIQUE_VEHICLE_COUNT_LABEL = "Unique Vehicle Count";

    /** LIVE-VERIFIED label text for the "Total Vehicle Count" stat tile (right tile). */
    public static final String TOTAL_VEHICLE_COUNT_LABEL = "Total Vehicle Count";

    /**
     * UNVERIFIED structural pattern (see {@code ROW_XPATH_TEMPLATE}) - individual scan rows were visible
     * live (real plates "109FGC"/"AX48004", State "CO", Locations "Carwash"/"Service Drive", timestamps
     * like "Oct 07, 2026, 12:10 AM" with no explicit GMT-offset text visible), but the row's own
     * OUTER clickable-wrapper shape (vs. a plain non-clickable list row, since TC_FUNC_LP_022 confirms
     * tapping a scan row's destination was never captured) was not specifically isolated during this pass.
     */
    public static final String SCAN_ROW_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** UNVERIFIED placeholder - no empty-result state was observed live (the crawl's search used real matching data); no confirmed empty-state copy exists for this screen. */
    @AndroidFindBy(xpath = "//*[contains(@text,'No scans') or contains(@text,'no results') or contains(@text,'No results')]")
    public WebElement vehicleScansEmptyStateMessage;

    // =================================================================================================
    // Vehicles List (FR-LP-003) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED. Confirmed exact screen title. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Vehicles']")
    public WebElement vehiclesListTitle;

    /** LIVE-VERIFIED. Confirmed single-EditText-per-screen pattern. TC_FUNC_LP_024/025 - VIN/plate search. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement vehiclesSearchInput;

    /**
     * LIVE-VERIFIED structural pattern: each vehicle card is ONE clickable {@code android.view.View}
     * wrapping ALL 7 of that vehicle's field TextViews (VIN/Plate/State/Location/Make/Model/Year) as
     * descendants - confirmed via 2 real cards (bounds spanning the full field-grid height each). Matching
     * on any field value (VIN, plate, etc.) correctly resolves to the owning card.
     */
    public static final String VEHICLE_CARD_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** UNVERIFIED placeholder - no empty-result state was observed live (the crawl's site had real vehicle records); no confirmed empty-state copy exists for this screen. */
    @AndroidFindBy(xpath = "//*[contains(@text,'No vehicles') or contains(@text,'no results') or contains(@text,'No results')]")
    public WebElement vehiclesEmptyStateMessage;

    // =================================================================================================
    // Vehicle Details (FR-LP-004) - LIVE-VERIFIED, field list CORRECTED from documented evidence
    // =================================================================================================

    /** LIVE-VERIFIED. Confirmed exact screen title. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Vehicle Details']")
    public WebElement vehicleDetailsTitle;

    /**
     * LIVE-VERIFIED structural pattern for reading a field's value from its label: the value TextView is
     * the label TextView's own immediate FOLLOWING SIBLING (confirmed via the real field layout order -
     * e.g. "VIN" immediately followed by "2T2ZK1BA9AC028363" as the very next sibling TextView under the
     * same parent). This is a corrected, more precise template than the original document-order
     * {@code following::} guess (which could cross into an unrelated field if siblings were nested
     * differently) - confirmed accurate for all 7 real fields observed.
     */
    public static final String FIELD_VALUE_XPATH_TEMPLATE = "//android.widget.TextView[@text='%s']/following-sibling::android.widget.TextView[1]";

    /** LIVE-VERIFIED real field labels (CORRECTED - see class javadoc's field-list discrepancy). */
    public static final String VIN_LABEL = "VIN";
    public static final String PLATE_LABEL = "Plate";
    public static final String STATE_LABEL = "State";
    /**
     * CORRECTED 2026-10-27 from "Location" to "Current Location" - a live crawl of the real Vehicle
     * Details Compose screen ({@code logs/page-source-dumps/CRAWL_VD1_vehicle_details_for_known_vin.xml})
     * confirmed the actual field label is "Current Location" (and the actual Plate/State are shown as one
     * combined "License Plate" field reading e.g. "BLU9458 - GA", not two separate fields - a separate,
     * pre-existing discrepancy from {@link #PLATE_LABEL}/{@link #STATE_LABEL} this fix does not change).
     * The stale "Location" value never matched anything on the real Details screen, causing
     * {@code verifyCurrentLocationFieldDisplaysCorrectly} to fail with a null field read.
     */
    public static final String LOCATION_LABEL = "Current Location";
    public static final String MAKE_LABEL = "Make";
    public static final String MODEL_LABEL = "Model";
    public static final String YEAR_LABEL = "Year";

    /**
     * NOT CONFIRMED TO EXIST (see class javadoc). Documented by RequirementAnalysis.md/
     * FunctionalTestCases.md as one of 6 Vehicle Details fields, but absent from both real vehicle records
     * observed live. Kept as a constant (rather than deleted outright) so {@code LicensePlatePage} and
     * {@code LicensePlateTests} can reference it explicitly to assert/document its absence, instead of
     * silently dropping TC_FUNC_LP_040's coverage.
     */
    public static final String SHOP_LABEL_UNCONFIRMED = "Shop";

    /** LIVE-VERIFIED exact section header text. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Checkpoint Timeline']")
    public WebElement checkpointTimelineHeader;

    /** UNVERIFIED structural pattern (see {@code ROW_XPATH_TEMPLATE}) for individual checkpoint entries - both real vehicles checked live had zero checkpoint history, so the populated-row shape was never observed. */
    public static final String CHECKPOINT_ENTRY_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** LIVE-VERIFIED exact empty-state copy for a vehicle with zero checkpoint history. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='There are currently no checkpoints for this vehicle.']")
    public WebElement checkpointTimelineEmptyStateMessage;

    // =================================================================================================
    // Lists Management (FR-REBUILD-15) - still UNVERIFIED (not reached during this crawl pass - the crawl
    // prioritized Vehicle Scans/Vehicles/Vehicle Details; Lists Management needs a follow-up live check)
    // =================================================================================================

    /** UNVERIFIED. TC_FUNC_LP_056's own step references the "Lists Management" screen by that name; not independently confirmed this pass (menu item IS confirmed present/labelled correctly - see {@code licensePlateMenuHeader}'s sibling rows). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Lists Management']")
    public WebElement listsManagementTitle;

    /** UNVERIFIED structural pattern (see {@code ROW_XPATH_TEMPLATE}) for each configured plate list row - not reached this pass. */
    public static final String PLATE_LIST_ROW_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** UNVERIFIED placeholder - no confirmed empty-state copy exists for a site with zero configured lists. */
    @AndroidFindBy(xpath = "//*[contains(@text,'No lists') or contains(@text,'no lists')]")
    public WebElement listsManagementEmptyStateMessage;
}
