package com.cresanmanagement.eyeq.pageobjects.global;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Action class for the Global module (Tickets list + details; module switcher to Site Pulse; Site
 * Pulse dashboard; Device Detail/All Devices via the dashboard's "See All Devices" link).
 *
 * <p><b>LIVE-VERIFIED 2026-10-20</b> (real device, confirmed Test backend) - see
 * {@code GlobalPageObjectRepository}'s class javadoc for the full evidence basis. Headline finding: the
 * Module Switcher is per-site permission gated - confirmed non-functional on "Amalthea" (disabled title),
 * but fully functional on "Main Site" (real switcher overlay, real Site Pulse dashboard with gauges/tiles/
 * lowest-uptime list, real "See All Devices" entry point).
 */
public class GlobalPage extends Base {

    private static final Logger logger = LogManager.getLogger(GlobalPage.class.getName());
    private final GlobalPageObjectRepository repo;

    public GlobalPage() {
        repo = new GlobalPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    // =================================================================================================
    // Module Switcher (FR-GLB-001) - LIVE-VERIFIED NOT REACHABLE
    // =================================================================================================

    /** LIVE-VERIFIED. True once the confirmed "Tickets" breadcrumb title is visible (used as the Global-tab-landed signal, NOT a working switcher). */
    public boolean isTicketsScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.ticketsTitle);
            return isElementVisible(repo.ticketsTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * FIX (2026-10-23, root-caused from a real failed run): re-tapping the already-active "Global"
     * bottom-nav tab does NOT reset to Tickets - confirmed live that this is the same "re-tapping an
     * active tab does not reset to the module's default screen" behavior already found for Virtual Guard
     * (see {@code VirtualGuardPage#returnToSurveillance}'s javadoc). A test that runs after another Global
     * test left the shared session on Site Pulse (e.g. TC_FUNC_GLB_002 running before TC_FUNC_GLB_003) then
     * finds {@code isTicketsScreenDisplayed()} failing, since tapping the tab again just stays on Site
     * Pulse. Returns to Tickets via the real module-switcher journey if currently on Site Pulse; no-ops if
     * already on Tickets. Uses a genuinely zero-wait raw {@code findElements} check (not
     * {@code isSitePulseDisplayed()}'s own slow, genuine-first-arrival-wait version) since this is a
     * snapshot/postcondition check of already-settled state, not a wait for a transition in progress.
     */
    public void returnToTickets() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        boolean onSitePulse = !getDriverForFramework().findElements(
                By.xpath("//android.widget.TextView[@text='Site Pulse']")).isEmpty();
        if (!onSitePulse) {
            return;
        }
        openModuleSwitcher();
        selectTicketsModule();
    }

    /** LIVE-VERIFIED. True if the title's clickable ancestor reports {@code enabled="false"} (confirmed the real, settled state - not a loading-race artifact). */
    public boolean isModuleSwitcherDisabled() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return "false".equals(repo.ticketsTitleClickableAncestor.getAttribute("enabled"));
    }

    /**
     * LIVE-VERIFIED (confirmed to do nothing - 2026-10-20 crawl showed byte-for-byte identical page source
     * before/after). Attempts a direct tap on the "Tickets" title despite its confirmed {@code enabled="false"}
     * state, for TC_FUNC_GLB_004's "tap the already-active module" no-op check.
     */
    public void tapTicketsTitleDespiteDisabled() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            repo.ticketsTitleClickableAncestor.click();
        } catch (Exception e) {
            logger.debug("Tap on the confirmed-disabled title threw (also an acceptable confirmation of non-interactivity): " + e.getClass().getSimpleName());
        }
    }

    // =================================================================================================
    // Tickets List (FR-GLB-002) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED. True once the confirmed "Open Tickets" list header is visible. */
    public boolean isOpenTicketsHeaderDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.openTicketsHeader);
    }

    /** LIVE-VERIFIED structural pattern. Taps the "Open" or "Closed" toggle option. */
    public void selectStatusToggle(String statusLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + statusLabel);
        WebElement toggle = getDriverForFramework().findElement(
                By.xpath(String.format(GlobalPageObjectRepository.TOGGLE_OPTION_XPATH_TEMPLATE, statusLabel)));
        clickWithHeal(toggle, "Status Toggle: " + statusLabel);
    }

    /** LIVE-VERIFIED. Types into the confirmed single-EditText keyword search field. */
    public void searchTickets(String keyword) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.ticketsSearchInput, "Tickets Search Input", keyword);
        settleAfterSearchDebounce(); // see Base#settleAfterSearchDebounce's javadoc - confirmed app-wide search-filter debounce (~1-1.5s), root-caused live 2026-10-20
    }

    /** Clears the keyword search field. */
    public void clearTicketsSearch() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.ticketsSearchInput.clear();
    }

    /** LIVE-VERIFIED structural pattern. Taps the "From" date field to open the system Date Picker (the picker's own appearance was not independently captured this pass). */
    public void tapFromDateField() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.fromDateField, "From Date Field");
    }

    /** LIVE-VERIFIED structural pattern. Taps the "To" date field. */
    public void tapToDateField() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.toDateField, "To Date Field");
    }

    /** LIVE-VERIFIED. True while a date field still shows the confirmed placeholder (i.e. no date selected yet). */
    public boolean isFromDateUnset() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return GlobalPageObjectRepository.DATE_FIELD_PLACEHOLDER_TEXT.equals(repo.fromDateField.getAttribute("text"))
                || !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='" + GlobalPageObjectRepository.DATE_FIELD_PLACEHOLDER_TEXT + "']")).isEmpty();
    }

    /** LIVE-VERIFIED structural pattern. Taps the sort control (confirmed real label "Newest at top"). UNVERIFIED what tapping it actually does (e.g. opens options vs. toggles order) - not exercised live. */
    public void tapSortControl() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.sortControl, "Sort Control");
    }

    /** LIVE-VERIFIED structural pattern. Count of currently visible ticket rows. */
    public int countVisibleTicketRows() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]")).size();
    }

    /**
     * LIVE-VERIFIED structural pattern. True once a ticket row whose text contains {@code searchTerm} is
     * visible.
     *
     * <p><b>Fixed 2026-10-27</b> after a confirmed live failure: selecting the Open/Closed status toggle
     * (now done centrally by {@code GlobalTests#navigateToTickets}) triggers its own backend-driven list
     * reload, and this read with a single, immediate, zero-wait {@code findElements} call - occasionally
     * racing that reload and reporting the known ticket as absent. Now bounded-waits (10s) for a match
     * before concluding it's genuinely absent.
     */
    public boolean isTicketRowDisplayed(String searchTerm) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + searchTerm);
        By locator = By.xpath(String.format(GlobalPageObjectRepository.TICKET_ROW_XPATH_TEMPLATE, searchTerm));
        try {
            WebElement row = new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(10))
                    .until(d -> {
                        List<WebElement> found = d.findElements(locator);
                        return found.isEmpty() ? null : found.get(0);
                    });
            return row.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED structural pattern. Taps the first ticket row whose text contains {@code searchTerm}.
     *
     * <p><b>Fixed 2026-10-27</b>: same reload-race bug class as {@link #isTicketRowDisplayed(String)} (see
     * its javadoc) - now bounded-waits (10s) for a match before giving up.
     */
    public void tapTicketRow(String searchTerm) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + searchTerm);
        By locator = By.xpath(String.format(GlobalPageObjectRepository.TICKET_ROW_XPATH_TEMPLATE, searchTerm));
        List<WebElement> rows;
        try {
            rows = new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(10))
                    .until(d -> {
                        List<WebElement> found = d.findElements(locator);
                        return found.isEmpty() ? null : found;
                    });
        } catch (TimeoutException e) {
            rows = List.of();
        }
        if (rows.isEmpty()) {
            throw new IllegalStateException("No ticket row found matching '" + searchTerm + "'.");
        }
        clickWithHeal(rows.get(0), "Ticket Row: " + searchTerm);
    }

    /** LIVE-VERIFIED (negative confirmation). True if NO "Create Ticket"/"+"/"New Ticket" control exists anywhere on screen - the confirmed real/expected state. */
    public boolean isCreateTicketControlAbsent() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !isElementVisible(repo.createTicketControlShouldNotExist);
    }

    /** UNVERIFIED placeholder (no empty-result state observed live). True once the (unconfirmed-copy) empty-state message is shown. */
    public boolean isTicketsEmptyStateDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.ticketsEmptyStateMessage);
    }

    // =================================================================================================
    // Ticket Details (FR-GLB-003) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED structural pattern. True once the tapped ticket's title is shown on the Details screen (same title text as its list row). */
    public boolean isTicketDetailsDisplayed(String ticketTitle) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + ticketTitle);
        try {
            WebElement el = getDriverForFramework().findElement(By.xpath("//android.widget.TextView[@text='" + ticketTitle + "']"));
            return el.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /** LIVE-VERIFIED. True once the confirmed "Created on:" label is visible. */
    public boolean isCreatedOnLabelDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.createdOnLabel);
    }

    /** LIVE-VERIFIED structural pattern. Raw text of the value immediately following "Created on:". */
    public String getCreatedOnValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            WebElement value = getDriverForFramework().findElement(
                    By.xpath("//android.widget.TextView[@text='Created on:']/following-sibling::android.widget.TextView[1]"));
            return value.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** LIVE-VERIFIED. True once the confirmed "Last updated on:" label is visible. */
    public boolean isLastUpdatedOnLabelDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.lastUpdatedOnLabel);
    }

    /** LIVE-VERIFIED structural pattern. Raw text of the value immediately following "Last updated on:". */
    public String getLastUpdatedOnValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            WebElement value = getDriverForFramework().findElement(
                    By.xpath("//android.widget.TextView[@text='Last updated on:']/following-sibling::android.widget.TextView[1]"));
            return value.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** LIVE-VERIFIED structural pattern. True once a ticket-number-like element containing {@code number} is visible (see repository javadoc's two-number caveat). */
    public boolean isTicketNumberDisplayed(String number) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + number);
        return !getDriverForFramework().findElements(By.xpath("//*[contains(@text,'" + number + "')]")).isEmpty();
    }

    /** LIVE-VERIFIED. True once the confirmed "Updates" section header is visible (TC_FUNC_GLB_021: its own content structure remains unconfirmed/TBD, not implemented here). */
    public boolean isUpdatesSectionHeaderDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.updatesSectionHeader);
    }

    // =================================================================================================
    // Module Switcher (FR-GLB-001) - LIVE-VERIFIED (functional on "Main Site")
    // =================================================================================================

    /** LIVE-VERIFIED (on "Main Site"). Taps the current module title (whichever is active - "Tickets" or "Site Pulse") to open the module-switcher overlay - confirmed functional there (unlike Amalthea, where the "Tickets" title is confirmed disabled). */
    public void openModuleSwitcher() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.moduleSwitcherTrigger, "Module Title (Module Switcher trigger)");
    }

    /** LIVE-VERIFIED (on "Main Site"). Selects "Site Pulse" from the open module-switcher overlay. */
    public void selectSitePulseModule() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.sitePulseSwitcherOption, "Site Pulse Switcher Option");
    }

    /** LIVE-VERIFIED (on "Main Site"). Selects "Tickets" from the open module-switcher overlay. */
    public void selectTicketsModule() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.ticketsSwitcherOption, "Tickets Switcher Option");
    }

    /** LIVE-VERIFIED. True once the module-switcher overlay's "Site Pulse" option is visible (used as the overlay's own open signal). */
    public boolean isModuleSwitcherOpen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.sitePulseSwitcherOption);
    }

    // =================================================================================================
    // Site Pulse Dashboard (FR-GLB-004) - LIVE-VERIFIED (real device, "Main Site")
    // =================================================================================================

    /** LIVE-VERIFIED. True once the confirmed "Site Pulse" screen title is visible. */
    public boolean isSitePulseDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.sitePulseTitle);
            return isElementVisible(repo.sitePulseTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** LIVE-VERIFIED structural pattern. Raw text of the value immediately preceding the given confirmed gauge/tile label. */
    public String getGaugeOrTileValue(String labelText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + labelText);
        try {
            WebElement value = getDriverForFramework().findElement(
                    By.xpath(String.format(GlobalPageObjectRepository.VALUE_PRECEDING_LABEL_XPATH_TEMPLATE, labelText)));
            return value.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** LIVE-VERIFIED. True once the confirmed "Devices With Lowest Uptime" section header is visible. */
    public boolean isLowestUptimeListDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.lowestUptimeListHeader);
    }

    /** LIVE-VERIFIED structural pattern. True once a lowest-uptime device row whose text contains {@code deviceName} is visible. */
    public boolean isLowestUptimeDeviceRowDisplayed(String deviceName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + deviceName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(GlobalPageObjectRepository.LOWEST_UPTIME_DEVICE_ROW_XPATH_TEMPLATE, deviceName)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /** LIVE-VERIFIED structural pattern. Taps the first lowest-uptime device row whose text contains {@code deviceName}. */
    public void tapLowestUptimeDeviceRow(String deviceName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + deviceName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(GlobalPageObjectRepository.LOWEST_UPTIME_DEVICE_ROW_XPATH_TEMPLATE, deviceName)));
        if (rows.isEmpty()) {
            throw new IllegalStateException("No lowest-uptime device row found matching '" + deviceName + "'.");
        }
        clickWithHeal(rows.get(0), "Lowest-Uptime Device Row: " + deviceName);
    }

    /** LIVE-VERIFIED. Taps the confirmed real "See All Devices" link - the entry point to the All Devices list independent of any other navigation. */
    public void tapSeeAllDevices() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.seeAllDevicesLink, "See All Devices Link");
    }

    // =================================================================================================
    // Device Detail / All Devices (FR-GLB-005) - UNVERIFIED beyond the entry point (see repository javadoc)
    // =================================================================================================

    /** UNVERIFIED structural pattern. True once a device row whose text contains {@code deviceName} is visible on the All Devices list. */
    public boolean isDeviceRowDisplayed(String deviceName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + deviceName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(GlobalPageObjectRepository.DEVICE_ROW_XPATH_TEMPLATE, deviceName)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /** UNVERIFIED structural pattern. Taps the first device row whose text contains {@code deviceName} on the All Devices list. */
    public void tapDeviceRow(String deviceName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + deviceName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(GlobalPageObjectRepository.DEVICE_ROW_XPATH_TEMPLATE, deviceName)));
        if (rows.isEmpty()) {
            throw new IllegalStateException("No device row found matching '" + deviceName + "' on the All Devices list.");
        }
        clickWithHeal(rows.get(0), "Device Row: " + deviceName);
    }

    /** UNVERIFIED structural pattern. True once an element containing {@code text} is visible (generic helper for Device Detail field checks not yet given dedicated locators). */
    public boolean isTextDisplayed(String text) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + text);
        return !getDriverForFramework().findElements(By.xpath("//*[contains(@text,'" + text + "')]")).isEmpty();
    }
}
