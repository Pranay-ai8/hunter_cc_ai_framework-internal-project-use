package com.cresanmanagement.eyeq.pageobjects.sitemanagement;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only - no business logic. Android-only project: @AndroidFindBy only.
 *
 * <p><b>Evidence basis:</b> this module's code was written in CODE-WRITING-ONLY mode (2026-10-19 strategy
 * directive: no live device/Appium sessions this pass). A short Live UI Hierarchy Verification crawl of
 * the first-run Site Selection screen WAS completed just before that directive took effect, and its
 * findings ARE used below where noted "LIVE-VERIFIED" - raw dumps saved under
 * {@code docs/dom-snapshots/sitemanagement_site_*.xml}. Everything else is UNVERIFIED, desk-sourced from
 * {@code 07-outputs/RequirementAnalysis/RequirementAnalysis.md} Section 7.2/10.2 and
 * {@code 07-outputs/TestCases/UITestCases.md}'s Site Management/Support rows (TC_UI_SITE_*, TC_UI_SUP_003,
 * TC_UI_VG_003), clearly marked per-locator. Per the project's "no sampling" standard, every locator a
 * future live pass needs to confirm/correct is called out explicitly rather than silently assumed correct.
 *
 * <p><b>DATA-CONTAMINATION CAVEAT - FULLY RESOLVED (2026-10-20 update):</b> an earlier Live UI Hierarchy
 * Verification crawl ran BEFORE this project's environment-safeguard structural fix existed (see
 * {@code utils.EnvironmentSetup}'s incident history) and was later confirmed, via {@code adb logcat}, to
 * have executed against PRODUCTION, not the Test backend - so this CSV's site names were deliberately kept
 * as pre-existing placeholders rather than trusting that crawl's observed data. A SECOND crawl, run after
 * the structural fix and independently confirmed-live to be on the Test backend, DIRECTLY CONFIRMED on
 * Test that "Amalthea" is a real assigned site with License Plate present, but WRONGLY concluded "Main
 * Site" was not a real site (its {@code selectSite("Main Site")} attempt threw "No site row found").
 *
 * <p><b>ROOT CAUSE FOUND (2026-10-20, THIRD pass):</b> that second crawl's conclusion about "Main Site"
 * was itself a false negative, caused by a genuine, confirmed real app behavior: the Site Selection search
 * field applies its client-side filter after a real ~1-1.5 second DEBOUNCE delay, and the second crawl's
 * search-then-immediately-assert code read the stale, pre-filter result set before concluding the site
 * didn't exist. Re-searching "Main Site" WITH a proper settle wait (now built into
 * {@code SiteManagementPage#searchForSite} via {@code Base#settleAfterSearchDebounce}) correctly filters to
 * exactly one real result, and selecting it successfully navigates into the Main App Shell. "Main Site" IS
 * real, and its License Plate tab is CONFIRMED absent (resolving {@code SiteA}'s intended "no License
 * Plate" role exactly as originally documented) - see {@code sitemanagement.csv}'s SiteA Notes for the full
 * evidence. The account's real site list is also large and includes other generated-looking names
 * ("Acclaimed Till Manufacturing", "Astro World", "Banging Beer Security", etc.) - these remain independently
 * confirmed real too, just no longer needed for SiteA's role.
 */
public class SiteManagementPageObjectRepository extends Object_Base {

    public SiteManagementPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // =================================================================================================
    // Site Selection (First-Run) (FR-SITE-001)
    // =================================================================================================

    /** LIVE-VERIFIED (2026-10-19): confirmed exact title text, per docs/dom-snapshots/sitemanagement_site_00_site_selection_fresh.xml. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Site Selection']")
    public WebElement siteSelectionTitle;

    /** LIVE-VERIFIED (2026-10-19): confirmed exact subtitle text. */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Please select one of your available sites to view']")
    public WebElement siteSelectionSubtitle;

    /**
     * LIVE-VERIFIED (2026-10-19): confirmed to be the sole EditText on this screen. Hint text
     * "Search for site" is UNVERIFIED (UITestCases.md TC_UI_SITE_001's Figma-sourced description only -
     * the live crawl's dump did not show a non-empty {@code text} attribute for this field when empty,
     * which is expected/normal for a hint vs. an actual value, but means the exact hint string itself was
     * not independently re-confirmed live).
     */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement siteSearchInput;

    /**
     * LIVE-VERIFIED (2026-10-19): confirmed exact empty-state text when a search matches no assigned
     * site, per docs/dom-snapshots/sitemanagement_site_01_search_amalthea.xml (TC_FUNC_SITE_003).
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='No site matches that name.']")
    public WebElement noSiteMatchesMessage;

    /**
     * LIVE-VERIFIED on the confirmed Test backend (2026-10-19, real QA account, real assigned sites -
     * see {@code docs/dom-snapshots/virtualguard_live_00_site_selection.xml}): each site row is a
     * clickable {@code android.view.View} CONTAINING a non-clickable {@code android.widget.TextView}
     * descendant whose own {@code text} attribute is the site's name - e.g.
     * {@code <android.view.View clickable="true">...<android.widget.TextView text="Amalthea" clickable="false"/></android.view.View>}.
     * The row View itself always has an EMPTY {@code text} attribute.
     *
     * <p><b>CORRECTION (2026-10-19):</b> this template previously (and WRONGLY) matched on the row View's
     * own {@code text} attribute directly (
     * {@code //android.view.View[@clickable='true'][contains(@text,'%s')]}), based on an earlier crawl
     * that was later found to have run against PRODUCTION data before this project's environment-safeguard
     * fix existed (see this class's earlier javadoc revision / {@code SiteManagementPage}'s git history).
     * That pattern silently matches ZERO rows against this confirmed-correct, Test-verified structure
     * (confirmed by reproducing the failure live: {@code SiteManagementPage.selectSite("Amalthea")} threw
     * "No site row found" even though "Amalthea" was visibly present in the same dump, because the
     * OLD xpath's {@code [contains(@text,'%s')]} predicate was being evaluated against the row View's own
     * empty {@code text}, not its child TextView's). Fixed to require a matching TextView DESCENDANT
     * instead. Used as a template by {@code SiteManagementPage} for dynamic, data-driven site-name lookups
     * (xpath built per-call with the CSV-provided site name) - not exposed as a single static field here
     * since the target site name is always test-data-driven, never hardcoded.
     *
     * <p>UNVERIFIED: the row's "selected" visual state (primary-blue background + check icon, per
     * UITestCases.md TC_UI_SITE_003) is still not independently confirmed - selecting a site navigates
     * away from this screen entirely (into the Main App Shell) rather than leaving a "selected" row behind
     * to inspect, so whether a {@code selected}/{@code checked} accessibility attribute flips, or only a
     * purely visual (color) change occurs that UiAutomator cannot see, remains unconfirmed.
     */
    public static final String SITE_ROW_XPATH_TEMPLATE = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'%s')]]";

    // =================================================================================================
    // Site Switcher (In-Shell) (FR-SITE-002) - UNVERIFIED, desk-sourced from RequirementAnalysis.md
    // Section 7.2 ("top-bar site name → bottom sheet 'Sites' (search + list) → selecting a site shows a
    // 'Now viewing site {name}' toast") and UITestCases.md TC_UI_SUP_003/TC_UI_VG_003 (confirms a
    // tappable TopBar site-name control with a globe icon + caret opens "the site-switcher bottom sheet",
    // but neither gives a resource-id or exact locator - only that tapping it "triggers a
    // navigation/overlay event"). NONE of this section has been live-inspected.
    // =================================================================================================

    /**
     * LIVE-VERIFIED 2026-10-20 (Account Settings module crawl) via
     * {@code docs/dom-snapshots/acctsettings_live_00_main_shell_before_tap.xml}: the real TopBar is a
     * single {@code topBarComposeView} containing exactly two top-level controls - a clickable
     * {@code android.view.View} wrapping the current site-name TextView (THIS control), immediately
     * followed by the Profile {@code ImageView} (content-desc="Profile", see
     * {@code MainAppShellPageObjectRepository#topBarProfileIcon}). The original UNVERIFIED guess required
     * an {@code ImageView} INSIDE the clickable site control, which does not match the real structure
     * (no ImageView child was present - any globe/caret glyph is not a separate accessibility node) and
     * would have matched zero elements live, the same category of bug already found and fixed for the
     * Profile icon. Anchored on the confirmed-real Profile icon's position instead of guessing further.
     *
     * <p><b>MITIGATION (2026-10-23) - see {@code MainAppShellPageObjectRepository#topBarSiteSwitcherControl}'s
     * javadoc for the full evidence and reasoning</b>: switched from {@code preceding-sibling::} to the
     * semantically-equivalent {@code following-sibling::} form to mitigate a confirmed, unexplained
     * intermittent Appium/UiAutomator2 XPath-engine failure - not a confirmed full root-cause fix.
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][following-sibling::android.widget.ImageView[@content-desc='Profile']]")
    public WebElement topBarSiteSwitcherControl;

    /** UNVERIFIED. Site Switcher bottom sheet's own search field - assumed to follow the same single-EditText pattern as Site Selection's search (same confirmed app-wide pattern), but this specific screen was never reached live. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement siteSwitcherSearchInput;

    /**
     * LIVE-VERIFIED (2026-10-19, confirmed Test backend): this exact toast ("Now viewing site Amalthea")
     * was captured in {@code docs/dom-snapshots/virtualguard_live_01_after_site_select.xml} as a real
     * {@code android.widget.Toast} element (text exactly matches the "Now viewing site {name}" format
     * RequirementAnalysis.md Section 7.2 describes narratively) after a (first-run) Site Selection, which
     * also confirms Android Toasts ARE visible to UiAutomator2's page source in this app/OS combination -
     * the "may not be reliably visible" concern is resolved. UNVERIFIED specifically for the IN-SHELL Site
     * Switcher trigger path (FR-SITE-002) - this confirmation came from the first-run Site Selection flow,
     * not yet from re-selecting a site via the Site Switcher bottom sheet - but since both paths plausibly
     * share the same underlying site-activation code, this is a reasonable, now evidence-backed assumption
     * rather than a blind guess. {@code SiteManagementPage} builds this xpath dynamically per expected site
     * name rather than exposing a single static field.
     */
    public static final String NOW_VIEWING_TOAST_XPATH_TEMPLATE = "//*[contains(@text,'Now viewing site') and contains(@text,'%s')]";

    /**
     * UNVERIFIED placeholder. No confirmed locator exists anywhere in evidence for a control that
     * dismisses the Site Switcher bottom sheet WITHOUT selecting a site (TC_FUNC_SITE_017) - bottom
     * sheets in this app's confirmed Compose UI are typically dismissed by tapping the scrim/backdrop
     * outside the sheet; this models that as a tap at a fixed, near-top-of-screen coordinate outside the
     * (assumed) sheet's own bounds, which a live pass MUST confirm or replace with an actual control if
     * one exists (e.g. a close "X" icon) instead.
     */
    @AndroidFindBy(xpath = "//android.view.View[@resource-id='com.cresanmanagement.eyeq:id/bottomSheetScrim' or contains(@resource-id,'scrim')]")
    public WebElement siteSwitcherScrim;
}
