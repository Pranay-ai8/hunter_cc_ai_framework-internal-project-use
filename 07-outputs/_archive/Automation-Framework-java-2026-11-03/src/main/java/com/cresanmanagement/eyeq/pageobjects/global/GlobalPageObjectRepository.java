package com.cresanmanagement.eyeq.pageobjects.global;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only - no business logic. Android-only project: @AndroidFindBy only.
 *
 * <p><b>Evidence basis (LIVE-VERIFIED 2026-10-20, real device {@code 8AHY0LBTH}, confirmed Test backend,
 * real QA account on real site "Amalthea"):</b> raw dumps: {@code docs/dom-snapshots/global_live_*.xml}.
 *
 * <p><b>HEADLINE LIVE FINDING, UPDATED 2026-10-20 - Module Switcher IS reachable, but only on certain
 * sites (per-site permission gated, FR-GLB-001/FR-GLB-004):</b> tapping the "Global" bottom-nav tab lands
 * directly on the Tickets screen (resource-id {@code ticketsHost}/{@code ticketsComposeView}) - same
 * "bottom-nav-tap goes straight to a sub-screen" pattern confirmed for Virtual Guard/License Plate. On
 * "Amalthea", the screen's title ("Tickets") is wrapped in a clickable {@code android.view.View} whose
 * {@code enabled} attribute is CONFIRMED {@code false} (verified both while loading and after settling, and
 * by a direct tap that provably did nothing). On "<b>Main Site</b>", the SAME title element reports
 * {@code enabled="true"}, and tapping it opens a real module-switcher overlay listing both "Tickets" and
 * "Site Pulse" - selecting "Site Pulse" loads a fully populated real dashboard (gauges, tiles, lowest-
 * uptime list, "See All Devices" link - see the fields below). This confirms FR-GLB-004's "site-permission
 * gated" status precisely: Amalthea has License Plate but lacks Site Pulse; Main Site has Site Pulse but
 * lacks License Plate (see {@code sitemanagement.csv}'s SiteA/SiteB Notes) - an inverse per-site
 * feature-flag profile between the two confirmed sites. TC_FUNC_GLB_002/003/004 and TC_FUNC_GLB_025-030 are
 * now implemented for real against "Main Site" - see {@code GlobalTests}' class javadoc.
 *
 * <p><b>Device Detail / All Devices (FR-GLB-005) - now reachable via the confirmed "See All Devices" link
 * on the Site Pulse dashboard</b> (TC_FUNC_GLB_033's own precondition's "or equivalent entry point" - this
 * IS that entry point). TC_FUNC_GLB_033/034/038 are now implemented; TC_FUNC_GLB_035/037 remain
 * TBD-equivalent/not implemented since they require a specific known device state (Paused / server-side
 * removed) not confirmed available.
 *
 * <p><b>Root-cause note on the earlier false "Main Site isn't real" conclusion</b> (from the Site
 * Management module's own history): the Site Selection search field applies its filter after a real
 * ~1-1.5s debounce delay; an earlier crawl's search-then-immediately-assert code read the stale,
 * pre-filter list before concluding the site didn't exist. See {@code Base#settleAfterSearchDebounce()}
 * and {@code sitemanagement.csv}'s SiteA Notes for the full history - this module's own
 * {@code ticketsSearchInput} usage is built on the now-fixed {@code GlobalPage#searchTickets}.
 *
 * <p><b>Tickets List / Ticket Details (FR-GLB-002/003) - fully LIVE-VERIFIED and reachable</b> (this IS the
 * screen the account lands on, on EITHER site): see the fields below.
 */
public class GlobalPageObjectRepository extends Object_Base {

    public GlobalPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // =================================================================================================
    // Shared row/list-item structural pattern (LIVE-VERIFIED - same shape confirmed across every module)
    // =================================================================================================

    /** LIVE-VERIFIED (ticket rows). Used as the default template for every row-shaped control in this module. */
    public static final String ROW_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'%s')]]";

    // =================================================================================================
    // Module Switcher (FR-GLB-001) - LIVE-VERIFIED NOT REACHABLE (see class javadoc)
    // =================================================================================================

    /** LIVE-VERIFIED. Confirmed exact breadcrumb/title text - CONFIRMED NON-INTERACTIVE (clickable ancestor has enabled="false"; a direct tap was confirmed to do nothing). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Tickets']")
    public WebElement ticketsTitle;

    /** LIVE-VERIFIED. The clickable-but-disabled ancestor wrapping the title - exposed only so tests can assert its disabled state directly, NOT to use as a working menu trigger (confirmed non-functional). */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Tickets']]")
    public WebElement ticketsTitleClickableAncestor;

    // =================================================================================================
    // Tickets List (FR-GLB-002) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED. Confirmed exact list-header text (distinct from the "Tickets" breadcrumb above it). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Open Tickets']")
    public WebElement openTicketsHeader;

    /**
     * LIVE-VERIFIED structural pattern: the Open/Closed toggle is two clickable
     * {@code android.view.View}s, each wrapping a non-clickable TextView label ("Open"/"Closed") - the
     * same confirmed row shape used everywhere else in this app.
     */
    public static final String TOGGLE_OPTION_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** LIVE-VERIFIED. Confirmed single-EditText-per-screen pattern. TC_FUNC_GLB_009/010/011 - keyword search. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement ticketsSearchInput;

    /** LIVE-VERIFIED exact label text (non-clickable, precedes the "From" date field). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='From']")
    public WebElement fromDateLabel;

    /** LIVE-VERIFIED exact label text (non-clickable, precedes the "To" date field). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='To']")
    public WebElement toDateLabel;

    /** LIVE-VERIFIED structural pattern: the "From" date field is a clickable View immediately following the "From" label, showing placeholder text "MM/DD/YYYY" when unset (confirmed opens the system Date Picker per TC_FUNC_GLB_012's narrative - the picker's own appearance was not independently captured this pass). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='From']/following::android.view.View[@clickable='true'][1]")
    public WebElement fromDateField;

    /** LIVE-VERIFIED structural pattern: the "To" date field, same shape as {@link #fromDateField}. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='To']/following::android.view.View[@clickable='true'][1]")
    public WebElement toDateField;

    /** LIVE-VERIFIED exact confirmed placeholder text shown in both unset date fields. */
    public static final String DATE_FIELD_PLACEHOLDER_TEXT = "MM/DD/YYYY";

    /** LIVE-VERIFIED structural pattern: the sort control is a clickable View wrapping its current-state label (confirmed real text "Newest at top"). Becomes enabled once the list finishes loading (unlike the permanently-disabled title). */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Newest at top']]")
    public WebElement sortControl;

    /**
     * LIVE-VERIFIED structural pattern: each ticket row is ONE clickable {@code android.view.View}
     * wrapping multiple field TextViews (confirmed real fields: ticket number "#NNNN", title, status e.g.
     * "New", and a "Last Updated: {timestamp}" line) - the same multi-field-row shape confirmed for License
     * Plate's vehicle cards.
     */
    public static final String TICKET_ROW_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** UNVERIFIED placeholder - no empty-result state was observed live (the crawl's site had real ticket records); no confirmed empty-state copy exists for this screen. */
    @AndroidFindBy(xpath = "//*[contains(@text,'No tickets') or contains(@text,'no results') or contains(@text,'No results')]")
    public WebElement ticketsEmptyStateMessage;

    /** LIVE-VERIFIED (negative confirmation): no "Create Ticket"/"+"/"New Ticket" control exists anywhere on this screen - confirmed by full inspection of every clickable element in the live dump (TopBar site-switcher/profile, Open/Closed toggle, search, date fields, sort control, ticket rows, bottom-nav tabs - nothing else). */
    @AndroidFindBy(xpath = "//*[@text='Create Ticket' or @text='New Ticket' or @content-desc='Create Ticket' or @content-desc='New Ticket' or @content-desc='Add']")
    public WebElement createTicketControlShouldNotExist;

    // =================================================================================================
    // Ticket Details (FR-GLB-003) - LIVE-VERIFIED
    // =================================================================================================

    /** LIVE-VERIFIED exact label text preceding the creation timestamp. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Created on:']")
    public WebElement createdOnLabel;

    /** LIVE-VERIFIED exact label text preceding the last-updated timestamp. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Last updated on:']")
    public WebElement lastUpdatedOnLabel;

    /** LIVE-VERIFIED exact section header text for the (per TC_FUNC_GLB_021, still-unconfirmed-structure) Updates section. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Updates']")
    public WebElement updatesSectionHeader;

    /**
     * LIVE-VERIFIED CAVEAT: the Ticket Details screen for a real ticket (list-displayed as "#7748") shows
     * TWO distinct number-like values - "#67698" and "7748" (the latter without a "#" prefix) - alongside
     * references to a "Zoho contact" ID in the ticket body, strongly suggesting this app integrates with a
     * third-party ticketing backend (Zoho Desk) and surfaces BOTH a Zoho-side ticket ID and the app's own
     * reference number as separate values. {@code GlobalPage}/{@code GlobalTests} treat the app's own
     * number (matching the Tickets List's displayed "#NNNN") as authoritative for TC_FUNC_GLB_020's
     * list-to-detail matching assertion, and do NOT assert anything about the second ("#67698"-style)
     * number, since its exact meaning/source is unconfirmed.
     */
    public static final String TICKET_NUMBER_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    // =================================================================================================
    // Module Switcher overlay (FR-GLB-001) - LIVE-VERIFIED on "Main Site"
    // =================================================================================================

    /**
     * LIVE-VERIFIED (on "Main Site"). Generic module-switcher trigger matching the title's current text
     * regardless of which module is currently active ("Tickets" or "Site Pulse") - used for re-opening the
     * switcher from either screen, as opposed to {@link #ticketsTitleClickableAncestor} which is
     * specifically "Tickets"-only (used for the Amalthea disabled-state check, where only "Tickets" is ever
     * shown).
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Tickets' or @text='Site Pulse']]")
    public WebElement moduleSwitcherTrigger;

    /** LIVE-VERIFIED (on "Main Site"). The "Site Pulse" option inside the module-switcher overlay opened by tapping the (enabled, on this site) title. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Site Pulse']]")
    public WebElement sitePulseSwitcherOption;

    /** LIVE-VERIFIED (on "Main Site"). The "Tickets" option inside the same module-switcher overlay. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Tickets']]")
    public WebElement ticketsSwitcherOption;

    // =================================================================================================
    // Site Pulse Dashboard (FR-GLB-004) - LIVE-VERIFIED (real device, "Main Site",
    // docs/dom-snapshots/main_site_select_04_site_pulse_loaded.xml)
    // =================================================================================================

    /** LIVE-VERIFIED exact screen title. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Site Pulse']")
    public WebElement sitePulseTitle;

    /** LIVE-VERIFIED exact gauge label. */
    public static final String SITE_UPTIME_GAUGE_LABEL = "Site Uptime (30 days)";

    /** LIVE-VERIFIED exact gauge label. */
    public static final String GOAL_UPTIME_GAUGE_LABEL = "EyeQ Goal Uptime";

    /** LIVE-VERIFIED exact numeric tile label (FunctionalTestCases.md's "first numeric tile" - TC_FUNC_GLB_027). */
    public static final String NUMBER_OF_DEVICES_TILE_LABEL = "Number of Devices";

    /** LIVE-VERIFIED exact numeric tile label (FunctionalTestCases.md's "second numeric tile" - TC_FUNC_GLB_028). */
    public static final String DEVICES_OFFLINE_TILE_LABEL = "Devices Offline";

    /**
     * LIVE-VERIFIED structural pattern for reading a gauge/tile's numeric value from its label: confirmed
     * the value TextView is NOT a sibling of the label in document order in the usual
     * following-sibling sense used elsewhere in this app - live inspection showed gauge/tile values
     * appear as EARLIER siblings (value THEN label, both under the same parent, but value drawn first) -
     * i.e. the value is the label's immediate PRECEDING sibling TextView (same confirmed pattern as
     * Vehicle Scans' stat tiles in the License Plate module).
     */
    public static final String VALUE_PRECEDING_LABEL_XPATH_TEMPLATE = "//android.widget.TextView[@text='%s']/preceding-sibling::android.widget.TextView[1]";

    /** LIVE-VERIFIED exact section header text for the lowest-uptime device list. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Devices With Lowest Uptime']")
    public WebElement lowestUptimeListHeader;

    /**
     * LIVE-VERIFIED structural pattern (see {@code ROW_XPATH_TEMPLATE}) for individual device entries in
     * the lowest-uptime list. Confirmed real entry observed: device name "Channels 1-4 Encoder M7016" with
     * an IP-address-style identifier "172.17.41.151" as a second field on the same row.
     */
    public static final String LOWEST_UPTIME_DEVICE_ROW_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;

    /** LIVE-VERIFIED exact link text - confirmed real entry point to the All Devices list, independent of any other navigation (satisfies TC_FUNC_GLB_033's "or equivalent entry point" precondition). */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='See All Devices']]")
    public WebElement seeAllDevicesLink;

    // =================================================================================================
    // Device Detail / All Devices (FR-GLB-005) - UNVERIFIED beyond the "See All Devices" entry point and
    // the lowest-uptime list's own device-row fields (the All Devices list screen itself, and Device
    // Detail's own field layout, were not independently reached/captured this pass)
    // =================================================================================================

    /** UNVERIFIED - not independently reached this pass; modeled on the confirmed device-identity fields already seen in the lowest-uptime list (name + IP-style identifier). */
    public static final String DEVICE_ROW_XPATH_TEMPLATE = ROW_XPATH_TEMPLATE;
}
