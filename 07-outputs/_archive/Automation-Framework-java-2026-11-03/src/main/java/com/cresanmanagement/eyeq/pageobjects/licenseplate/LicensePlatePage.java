package com.cresanmanagement.eyeq.pageobjects.licenseplate;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/**
 * Page Action class for the License Plate module (Vehicle Scans feed, Vehicles registry + details, the
 * in-place Location filter dropdown, Lists Management). Present only on sites with the License Plate
 * capability enabled (per-site feature flag) - see RequirementAnalysis.md Section 4 Business Constraints.
 *
 * <p><b>LIVE-VERIFIED 2026-10-19/20</b> (real device, confirmed Test backend, real site "Amalthea") - see
 * {@code LicensePlatePageObjectRepository}'s class javadoc for the full evidence basis, the resolved
 * "Vehicle Scans" vs. "Plate Scans" naming conflict, the confirmed menu-reopen mechanism, and the
 * corrected Vehicle Details field list (7 real fields, no confirmed "Shop" field). Methods for Lists
 * Management remain UNVERIFIED (not reached during this crawl pass).
 *
 * <p>Opening the License Plate bottom-sheet menu's FIRST time (tapping the bottom-nav tab) is handled by
 * {@code MainAppShellPage} as before; tapping a menu item is also still {@code MainAppShellPage}'s
 * {@code tapBottomSheetMenuItem}. This class adds {@link #reopenMenuFromSubScreen()} for the LIVE-CONFIRMED
 * "tap the screen's own title" re-open mechanism, used from any of this module's sub-screens.
 */
public class LicensePlatePage extends Base {

    private static final Logger logger = LogManager.getLogger(LicensePlatePage.class.getName());
    private final LicensePlatePageObjectRepository repo;

    public LicensePlatePage() {
        repo = new LicensePlatePageObjectRepository(DriverFactory.getDriverForFramework());
    }

    // =================================================================================================
    // License Plate Menu (FR-LP-001) - LIVE-VERIFIED
    // =================================================================================================

    /**
     * LIVE-VERIFIED. Re-opens the License Plate bottom-sheet menu by tapping the Vehicle Scans screen's
     * own clickable title - the confirmed real mechanism (NOT re-tapping the bottom-nav tab, which
     * navigates to the default screen instead of reopening any menu, per Virtual Guard's identically
     * confirmed finding).
     */
    public void reopenMenuFromSubScreen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.vehicleScansTitleClickableAncestor, "Vehicle Scans Title (menu re-open)");
    }

    /**
     * Returns to the Vehicle Scans root screen from any License Plate sub-screen, regardless of which one -
     * added 2026-10-26 after root-causing a confirmed, 100%-reproducible real-device failure (consistent
     * across runs on 2026-10-21, 10-22, 10-23, and 10-26):
     * {@code verifyTappingListsManagementNavigatesToListsManagementScreen} and any test running right after
     * {@code verifyTappingVehiclesNavigatesToVehiclesList} would fail a 90-second precondition timeout on
     * {@link #isVehicleScansFeedDisplayed()}, never even reaching its own menu-item tap (confirmed via the
     * real stack trace landing inside the precondition assert, not the tap). Root cause: the old
     * {@code LicensePlateTests#lightReset()} only closed an already-OPEN menu; it never navigated back to
     * Vehicle Scans when a test ended on a sub-screen with the menu already closed (e.g. the Vehicles List,
     * left there by a menu-item tap). The next test's {@code ensureSiteSelected} -> site-switcher ->
     * {@code tapBottomNavTab} sequence was then relied on to land back on Vehicle Scans on its own, but does
     * NOT reliably do so when already on one of this module's own sub-screens - the exact same "re-tap an
     * already-active tab doesn't reset to the default screen" bug class already confirmed and fixed for
     * Virtual Guard ({@code VirtualGuardPage#returnToSurveillance()}) and Global
     * ({@code GlobalPage#returnToTickets()}). This follows the identical, proven pattern: detect which known
     * sub-screen is currently showing by its own title text (raw {@code findElements}, not the slow
     * PageFactory-backed checks - see {@code VirtualGuardPage#returnToSurveillance()}'s own javadoc for why),
     * tap that title to re-open the menu (the already-LIVE-VERIFIED {@link #reopenMenuFromSubScreen()}
     * mechanism), then select "Vehicle Scans" from it. "Vehicle Details" is a drill-down screen reached from
     * the Vehicles List (not itself a menu destination), so it is handled with one {@code navigateBack()}
     * first rather than a menu re-open.
     */
    public void returnToVehicleScans() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if (!getDriverForFramework().findElements(
                By.xpath("//android.widget.TextView[@text='Vehicle Scans']")).isEmpty()) {
            return;
        }
        if (!getDriverForFramework().findElements(
                By.xpath("//android.widget.TextView[@text='Vehicle Details']")).isEmpty()) {
            navigateBack();
        }
        String currentTitle = null;
        for (String candidate : new String[]{"Vehicles", "Lists Management"}) {
            if (!getDriverForFramework().findElements(
                    By.xpath("//android.widget.TextView[@text='" + candidate + "']")).isEmpty()) {
                currentTitle = candidate;
                break;
            }
        }
        if (currentTitle == null) {
            return;
        }
        List<WebElement> clickableTitle = getDriverForFramework().findElements(By.xpath(
                "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='" + currentTitle + "']]"));
        if (clickableTitle.isEmpty()) {
            return;
        }
        clickWithHeal(clickableTitle.get(0), currentTitle + " Title (menu re-open)");
        com.cresanmanagement.eyeq.Pages.MainAppShellPage().tapBottomSheetMenuItem("Vehicle Scans");
    }

    /** LIVE-VERIFIED. True once the real Material bottom-sheet menu (confirmed resource-id + header text) is open. */
    public boolean isLicensePlateMenuOpen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.licensePlateMenuHeader);
    }

    /** LIVE-VERIFIED. Dismisses the menu by tapping its confirmed real dismiss-scrim (resource-id {@code touch_outside}). */
    public void dismissLicensePlateMenuWithoutSelecting() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.licensePlateMenuDismissScrim, "License Plate Menu Dismiss Scrim");
    }

    // =================================================================================================
    // Vehicle Scans Feed (FR-LP-002) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED. True once the confirmed "Vehicle Scans" screen title is visible. */
    public boolean isVehicleScansFeedDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.vehicleScansFeedTitle);
            return isElementVisible(repo.vehicleScansFeedTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED structural pattern. Raw text of the stat tile value for the given confirmed label
     * ({@link LicensePlatePageObjectRepository#UNIQUE_VEHICLE_COUNT_LABEL} or
     * {@link LicensePlatePageObjectRepository#TOTAL_VEHICLE_COUNT_LABEL}) - the value TextView is the
     * label's immediate PRECEDING sibling.
     */
    public String getStatTileValue(String labelText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + labelText);
        try {
            WebElement value = getDriverForFramework().findElement(
                    By.xpath("//android.widget.TextView[@text='" + labelText + "']/preceding-sibling::android.widget.TextView[1]"));
            return value.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** LIVE-VERIFIED. Types into the confirmed single-EditText plate-substring search field. */
    public void searchVehicleScans(String searchText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.vehicleScansSearchInput, "Vehicle Scans Search Input", searchText);
        settleAfterSearchDebounce(); // see Base#settleAfterSearchDebounce's javadoc - confirmed app-wide search-filter debounce (~1-1.5s), root-caused live 2026-10-20
    }

    /**
     * Fixed 2026-10-26 after a confirmed live failure in {@code verifyClearingSearchAndFilterResetsToFullList}
     * ("expected [10] but found [7]"): this cleared the field but never waited for the same confirmed
     * app-wide search-filter debounce (~1-1.5s) that {@link #searchVehicleScans(String)} already waits for -
     * reading the row count immediately after {@code clear()} caught the list mid-reload, still showing the
     * prior filtered subset. See {@code Base#settleAfterSearchDebounce()}'s javadoc for the full root-cause.
     */
    public void clearVehicleScansSearch() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.vehicleScansSearchInput.clear();
        settleAfterSearchDebounce();
    }

    /** LIVE-VERIFIED. Taps the Location filter control (shows the current selection as its own label, defaults to "All Locations") to expand the inline options dropdown. */
    public void openLocationFilter() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.locationFilterControl, "Location Filter Control");
    }

    /**
     * LIVE-VERIFIED structural pattern. Selects a Location option whose text contains {@code locationName}
     * from the expanded inline dropdown (call {@link #openLocationFilter()} first). No separate "Apply"
     * step is believed necessary (no such control was observed in the expanded dropdown - UNVERIFIED
     * whether selection applies immediately, since no option was actually selected during the crawl).
     *
     * <p><b>Fixed 2026-10-27</b> after a confirmed live failure in
     * {@code verifyClearingAppliedFilterRevertsListToUnfiltered} ("No Location filter option found
     * matching 'All Locations'" on the SECOND {@code openLocationFilter()}+select within the same test,
     * immediately after a prior selection had just triggered a list re-filter + dropdown
     * collapse/re-expand recomposition). Same missing-wait-after-a-UI-triggering-action bug class as
     * {@link #isLocationFilterExpanded()} - this method read the option list with a single, immediate,
     * zero-wait {@code findElements} call. A first attempted fix (15s bounded wait) still wasn't
     * consistently enough live - widened to the same 90s order of magnitude used everywhere else in this
     * framework for a real, potentially-slow app operation (see {@code Base#DEFAULT_WAIT}).
     */
    public void selectLocationFilterOption(String locationName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + locationName);
        By locator = By.xpath(String.format(LicensePlatePageObjectRepository.LOCATION_OPTION_XPATH_TEMPLATE, locationName));
        List<WebElement> options;
        try {
            options = new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(90))
                    .until(d -> {
                        List<WebElement> found = d.findElements(locator);
                        return found.isEmpty() ? null : found;
                    });
        } catch (TimeoutException e) {
            options = List.of();
        }
        if (options.isEmpty()) {
            throw new IllegalStateException("No Location filter option found matching '" + locationName + "'.");
        }
        clickWithHeal(options.get(0), "Location Filter Option: " + locationName);
    }

    /**
     * LIVE-VERIFIED. True once the inline Location dropdown is expanded (checked via the confirmed real "All
     * Locations" reset option row being visible).
     *
     * <p>Fixed 2026-10-26 after a confirmed live failure in {@code verifyDismissingFilterWithoutSelectionLeavesListUnchanged}
     * ("Precondition failed: expected the filter dropdown to be open" immediately after {@code openLocationFilter()}):
     * this read the row count with ZERO settle time for the dropdown's expand animation/recomposition after
     * the tap - the same missing-wait-after-a-UI-triggering-action bug class already found and fixed for
     * {@code clearVehicleScansSearch()}/{@code clearVehiclesSearch()} and {@code isBottomNavTabPresent()}
     * today.
     *
     * <p><b>Widened 2026-10-27</b>, first from 5s to 15s, then to 90s, after confirmed live failures
     * ({@code verifyCameraLocationFilterOpensWithOptions}, {@code verifyDismissingFilterWithoutSelectionLeavesListUnchanged})
     * still timing out at both 5s AND 15s on this screen's filter dropdown within a test run - a live
     * crawl confirmed the same dropdown expands fully and correctly (all 7 real options, see
     * {@code logs/page-source-dumps/CRAWL_A3_reopened_after_selection_immediate.xml}) on a warm REOPEN with
     * no explicit wait at all, so the locator/logic is correct; this operation's real-device latency is
     * just highly variable (observed exceeding 15s at least once live) - widened to the same 90s order of
     * magnitude used everywhere else in this framework for a real, potentially-slow app operation.
     */
    public boolean isLocationFilterExpanded() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        By locator = By.xpath(String.format(LicensePlatePageObjectRepository.LOCATION_OPTION_XPATH_TEMPLATE, LicensePlatePageObjectRepository.LOCATION_FILTER_ALL_OPTION_TEXT));
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(90))
                    .until(d -> d.findElements(locator).size() > 1); // the control's own current-value label also matches "All Locations" by default, so >1 means the dropdown's own reset row is additionally present
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED 2026-10-27. True once the Vehicles List's Camera/Location Filter sheet (the
     * hierarchical camera checkbox tree - see {@code LicensePlatePageObjectRepository}'s FR-LP-005
     * section javadoc) is open, checked via its confirmed real "Select All Cameras" row.
     */
    public boolean isCameraFilterSheetOpen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.cameraFilterSelectAllCamerasLabel);
            return isElementVisible(repo.cameraFilterSelectAllCamerasLabel);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED 2026-10-27. Toggles the checkbox for a given camera/group label (e.g. "Group 1",
     * "Front Entrance") inside the Vehicles List's Camera/Location Filter sheet (call
     * {@link #openLocationFilter()} first).
     */
    public void toggleCameraFilterOption(String label) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + label);
        By locator = By.xpath(String.format(LicensePlatePageObjectRepository.CAMERA_FILTER_CHECKBOX_XPATH_TEMPLATE, label));
        List<WebElement> checkboxes = getDriverForFramework().findElements(locator);
        if (checkboxes.isEmpty()) {
            throw new IllegalStateException("No Camera Filter checkbox found for label '" + label + "'.");
        }
        clickWithHeal(checkboxes.get(0), "Camera Filter Checkbox: " + label);
    }

    /** LIVE-VERIFIED 2026-10-27. Taps "Filter" to apply the current checkbox selection and close the sheet. */
    public void tapCameraFilterApply() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.cameraFilterApplyButton, "Camera Filter Apply (Filter button)");
    }

    /**
     * LIVE-VERIFIED 2026-10-27. Taps "Clear" to uncheck every camera/group checkbox - the sheet stays
     * open afterward (confirmed live), so {@link #tapCameraFilterApply()} must be called separately to
     * apply the cleared (unfiltered) state and close the sheet.
     */
    public void tapCameraFilterClear() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.cameraFilterClearButton, "Camera Filter Clear");
    }

    /** LIVE-VERIFIED structural pattern. Count of currently visible scan rows (for filtered-vs-unfiltered comparisons). */
    public int countVisibleScanRows() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]")).size();
    }

    /** LIVE-VERIFIED structural pattern. True once a scan row whose text contains {@code searchTerm} is visible. */
    public boolean isScanRowDisplayed(String searchTerm) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + searchTerm);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(LicensePlatePageObjectRepository.SCAN_ROW_XPATH_TEMPLATE, searchTerm)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /** UNVERIFIED placeholder (no empty-result state observed live). True once the (unconfirmed-copy) empty-state message is shown. */
    public boolean isVehicleScansEmptyStateDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.vehicleScansEmptyStateMessage);
    }

    // =================================================================================================
    // Vehicles List (FR-LP-003) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED. True once the confirmed "Vehicles" title is visible. */
    public boolean isVehiclesListDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.vehiclesListTitle);
            return isElementVisible(repo.vehiclesListTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** LIVE-VERIFIED. Types into the confirmed single-EditText VIN/plate search field. */
    public void searchVehicles(String searchText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.vehiclesSearchInput, "Vehicles Search Input", searchText);
        settleAfterSearchDebounce(); // see Base#settleAfterSearchDebounce's javadoc - confirmed app-wide search-filter debounce (~1-1.5s), root-caused live 2026-10-20
    }

    /**
     * Fixed 2026-10-26 - same confirmed root cause as {@link #clearVehicleScansSearch()}: a live failure in
     * {@code verifyClearingSearchFilterRestoresFullVehiclesList} ("expected [9] but found [7]") showed the
     * same missing-debounce-wait-after-clear pattern on this module's Vehicles List search field.
     */
    public void clearVehiclesSearch() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.vehiclesSearchInput.clear();
        settleAfterSearchDebounce();
    }

    /** LIVE-VERIFIED structural pattern. Count of currently visible vehicle cards. */
    public int countVisibleVehicleCards() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]")).size();
    }

    /** LIVE-VERIFIED structural pattern. True once a vehicle card whose text contains {@code searchTerm} (VIN or plate) is visible. */
    public boolean isVehicleCardDisplayed(String searchTerm) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + searchTerm);
        List<WebElement> cards = getDriverForFramework().findElements(
                By.xpath(String.format(LicensePlatePageObjectRepository.VEHICLE_CARD_XPATH_TEMPLATE, searchTerm)));
        return !cards.isEmpty() && cards.get(0).isDisplayed();
    }

    /** LIVE-VERIFIED structural pattern. Taps the first vehicle card whose text contains {@code searchTerm}. */
    public void tapVehicleCard(String searchTerm) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + searchTerm);
        List<WebElement> cards = getDriverForFramework().findElements(
                By.xpath(String.format(LicensePlatePageObjectRepository.VEHICLE_CARD_XPATH_TEMPLATE, searchTerm)));
        if (cards.isEmpty()) {
            throw new IllegalStateException("No vehicle card found matching '" + searchTerm + "'.");
        }
        clickWithHeal(cards.get(0), "Vehicle Card: " + searchTerm);
    }

    /**
     * LIVE-VERIFIED structural pattern, CORRECTED (2026-10-20) after a naive "any clickable View with
     * non-empty text" selector was confirmed live to ALSO match the TopBar's site-switcher control (it
     * precedes real vehicle cards in document order and was mistakenly tapped once during the crawl,
     * landing on the Site Switcher instead of a vehicle). This version anchors on the confirmed real "VIN"
     * field label - present on every card - and walks up to its nearest clickable ancestor (the card
     * itself), which cannot accidentally match unrelated chrome.
     */
    public void tapFirstVehicleCard() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        List<WebElement> cards = getDriverForFramework().findElements(
                By.xpath("(//android.widget.TextView[@text='" + LicensePlatePageObjectRepository.VIN_LABEL + "'])[1]/ancestor::android.view.View[@clickable='true'][1]"));
        if (cards.isEmpty()) {
            throw new IllegalStateException("No vehicle card found on the Vehicles List.");
        }
        clickWithHeal(cards.get(0), "First Vehicle Card");
    }

    /** LIVE-VERIFIED mechanism. The VIN/plate search field's current raw text value. */
    public String getVehiclesSearchValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.vehiclesSearchInput.getAttribute("text");
    }

    /** UNVERIFIED placeholder (no empty-result state observed live). True once the (unconfirmed-copy) empty-state message is shown. */
    public boolean isVehiclesEmptyStateDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.vehiclesEmptyStateMessage);
    }

    // =================================================================================================
    // Vehicle Details (FR-LP-004) - LIVE-VERIFIED, field list CORRECTED (see repository javadoc)
    // =================================================================================================

    /** LIVE-VERIFIED. True once the confirmed "Vehicle Details" title is visible. */
    public boolean isVehicleDetailsDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.vehicleDetailsTitle);
            return isElementVisible(repo.vehicleDetailsTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED structural pattern. Reads a field's value by its confirmed label text (VIN/Plate/State/Location/Make/Model/Year). Returns null if the label itself is not found (e.g. the unconfirmed "Shop" field).
     *
     * <p><b>Fixed 2026-10-27</b> after a confirmed live failure in {@code verifyCurrentLocationFieldDisplaysCorrectly}
     * ("expected the 'Location' field to be present" - null returned) immediately after navigating onto the
     * Vehicle Details screen: this read with a single, immediate, zero-wait {@code findElement} call, racing
     * the screen's own field render. A first attempted fix (10s bounded wait) still wasn't consistently
     * enough specifically for the "Location" field (VIN/Plate/State read fine even with zero wait,
     * suggesting Location's value may depend on a slower async step, e.g. a reverse-geocode lookup, rather
     * than just ordinary Compose recomposition) - widened to the same 90s order of magnitude already used
     * everywhere else in this framework for a real, potentially-slow app operation (see {@code Base#DEFAULT_WAIT}).
     */
    public String getFieldValue(String labelText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + labelText);
        By locator = By.xpath(String.format(LicensePlatePageObjectRepository.FIELD_VALUE_XPATH_TEMPLATE, labelText));
        try {
            WebElement value = new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(90))
                    .until(d -> {
                        List<WebElement> found = d.findElements(locator);
                        return found.isEmpty() ? null : found.get(0);
                    });
            return value.getAttribute("text");
        } catch (TimeoutException e) {
            return null;
        }
    }

    /** LIVE-VERIFIED. True only if a field labelled exactly {@code labelText} exists anywhere on screen (used to confirm/deny field presence, e.g. the unconfirmed "Shop" field). */
    public boolean isFieldLabelPresent(String labelText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + labelText);
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='" + labelText + "']")).isEmpty();
    }

    /** LIVE-VERIFIED. True once the confirmed "Checkpoint Timeline" section header is visible. */
    public boolean isCheckpointTimelineDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.checkpointTimelineHeader);
    }

    /** UNVERIFIED structural pattern (populated state never observed live - both real vehicles checked had zero history). Count of currently visible checkpoint timeline entries. */
    public int countCheckpointEntries() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]")).size();
    }

    /** UNVERIFIED structural pattern (see {@link #countCheckpointEntries()}). Raw text of the Nth (1-based) checkpoint timeline entry. */
    public String getCheckpointEntryText(int indexOneBased) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: entry " + indexOneBased);
        List<WebElement> entries = getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]"));
        if (indexOneBased < 1 || indexOneBased > entries.size()) {
            return null;
        }
        try {
            WebElement label = entries.get(indexOneBased - 1).findElement(By.xpath(".//android.widget.TextView"));
            return label.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** LIVE-VERIFIED exact copy. True once the confirmed "There are currently no checkpoints for this vehicle." empty-state message is shown. */
    public boolean isCheckpointTimelineEmptyStateDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.checkpointTimelineEmptyStateMessage);
    }

    // =================================================================================================
    // Lists Management (FR-REBUILD-15) - REACHABILITY LIVE-VERIFIED 2026-10-22 (real device crawl, udid
    // 8BQX1DY81): confirmed reachable via License Plate's menu (tap the "Plate Scans" title, then tap the
    // "Lists Management" row), confirmed to load real content (resource-id listManagementHost/
    // listManagementComposeView, real plate-list rows like "vipg"/"rest"/"res" each showing a
    // "1/2/3 plates" count, a "Create Plate List"/"Create Notification" action, and an "Alerts" section).
    // isListsManagementDisplayed()'s title locator is confirmed correct. The exact row/empty-state
    // sub-structure below (countVisiblePlateListRows/isPlateListRowDisplayed/
    // isListsManagementEmptyStateDisplayed) was NOT individually re-verified this pass and remains
    // UNVERIFIED in its specifics - only top-level reachability and title-match are now confirmed.
    // =================================================================================================

    /** LIVE-VERIFIED reachability 2026-10-22 (title text and screen reachability confirmed; see section note above). */
    public boolean isListsManagementDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.listsManagementTitle);
            return isElementVisible(repo.listsManagementTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** UNVERIFIED structural pattern. True once a configured plate list row whose text contains {@code listName} is visible. */
    public boolean isPlateListRowDisplayed(String listName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + listName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(LicensePlatePageObjectRepository.PLATE_LIST_ROW_XPATH_TEMPLATE, listName)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /** UNVERIFIED structural pattern. Count of currently visible configured plate list rows. */
    public int countVisiblePlateListRows() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]")).size();
    }

    /** UNVERIFIED placeholder. True once the (unconfirmed-copy) empty-state message is shown. */
    public boolean isListsManagementEmptyStateDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.listsManagementEmptyStateMessage);
    }
}
