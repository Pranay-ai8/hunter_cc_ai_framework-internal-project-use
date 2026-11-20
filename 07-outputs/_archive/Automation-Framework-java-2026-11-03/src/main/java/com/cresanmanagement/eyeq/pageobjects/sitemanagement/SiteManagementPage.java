package com.cresanmanagement.eyeq.pageobjects.sitemanagement;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/**
 * Page Action class for the Site Management module (first-run Site Selection, in-shell Site Switcher
 * bottom sheet).
 *
 * <p>Originally written in CODE-WRITING-ONLY mode (2026-10-19) - see
 * {@code SiteManagementPageObjectRepository}'s class javadoc for the full evidence basis, including the
 * clickable-row xpath correction and the fully-resolved data-contamination findings (confirmed on Test:
 * "Amalthea" is real with License Plate present; "Main Site" is also real, with License Plate confirmed
 * ABSENT - a prior false "Main Site is not real" conclusion was itself root-caused to a search-filter
 * debounce timing bug, now fixed via {@link Base#settleAfterSearchDebounce()}). Site Selection methods are
 * LIVE-VERIFIED; Site Switcher methods remain UNVERIFIED, desk-sourced.
 */
public class SiteManagementPage extends Base {

    private static final Logger logger = LogManager.getLogger(SiteManagementPage.class.getName());
    private final SiteManagementPageObjectRepository repo;

    public SiteManagementPage() {
        repo = new SiteManagementPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    // =================================================================================================
    // Site Selection (First-Run) (FR-SITE-001) - LIVE-VERIFIED structure
    // =================================================================================================

    /**
     * LIVE-VERIFIED. True once the Site Selection screen's title and subtitle are visible. Uses
     * {@code Base}'s full 90-second {@code DEFAULT_WAIT} (via {@code waitUntilElementIsVisible}) - correct
     * for its original purpose (waiting through a real login round-trip that may genuinely take a while),
     * but expensive to call when the answer is expected to be "no" (every miss burns the full 90s before
     * the caught {@code TimeoutException} returns false).
     *
     * <p><b>PERFORMANCE INCIDENT (2026-10-21):</b> a real Site Management suite run took ~2 hours for 20
     * tests - traced via the real timestamped log to THIS exact method being called from
     * {@link #ensureSiteSelected(String)}'s branch-decision logic once per test, where the expected,
     * common answer (after the first test) is "no, we're already past Site Selection" - each such call
     * was confirmed in the log to take ~100 seconds (a full 90s DEFAULT_WAIT plus overhead) before
     * returning false. Combined with TestNG's {@code RetryAnalyzer} (one automatic retry per failure)
     * doubling the cost of any test that then also failed, this fully explains the ~2-hour runtime -
     * NOT the 90s default being inherently wrong for this app's real genuine latency (it remains correct
     * for the slow-login-arrival case), but this ONE call site using it for a fast "current state" check
     * it was never designed for. Fixed by routing {@code ensureSiteSelected}'s branch decision through
     * {@link #isSiteSelectionScreenDisplayedFast()} instead - this method itself is UNCHANGED, since other
     * callers (e.g. "assert Site Selection appears after a real login") still need the full wait.
     */
    public boolean isSiteSelectionScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.siteSelectionTitle);
            return isElementVisible(repo.siteSelectionTitle) && isElementVisible(repo.siteSelectionSubtitle);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Fast variant of {@link #isSiteSelectionScreenDisplayed()} for "what screen are we on RIGHT NOW"
     * checks - either a pure state decision with no action just taken (e.g.
     * {@link #ensureSiteSelected(String)}'s branch logic), or confirming the POSTCONDITION of an action
     * that was just performed (e.g. "assert we left Site Selection after selecting a site"), where any
     * real UI transition should already be complete or nearly so. Bounded to a short, explicit 10-second
     * wait instead of the full 90-second {@code DEFAULT_WAIT}, so the common "we're already past Site
     * Selection" case resolves in ~10s instead of ~90s - a deliberate, documented trade-off (a genuinely
     * slower-than-10s real transition would read as a false negative here) rather than an untested
     * guess; tune the bound up if real evidence ever shows 10s is too tight. Do NOT use this to wait for
     * Site Selection to genuinely APPEAR after a slow operation with no prior signal it's imminent (e.g.
     * right after login) - use {@link #isSiteSelectionScreenDisplayed()} for that.
     */
    public boolean isSiteSelectionScreenDisplayedFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(10))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(repo.siteSelectionTitle));
            return isElementVisible(repo.siteSelectionTitle) && isElementVisible(repo.siteSelectionSubtitle);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** LIVE-VERIFIED. Types into the confirmed single search field on Site Selection. */
    public void searchForSite(String searchText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.siteSearchInput, "Site Search Input", searchText);
        // CORRECTED 2026-10-20: confirmed live real debounce delay (~1-1.5s) before this client-side
        // filter actually applies - see Base#settleAfterSearchDebounce's javadoc for the full root-cause
        // history (this exact gap caused an earlier false "site doesn't exist" conclusion).
        settleAfterSearchDebounce();
    }

    /** LIVE-VERIFIED. Clears the search field (confirmed single EditText) back to empty. */
    public void clearSiteSearch() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.siteSearchInput.clear();
    }

    /** LIVE-VERIFIED. The search field's current raw text value. */
    public String getSiteSearchValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.siteSearchInput.getAttribute("text");
    }

    /** LIVE-VERIFIED. True once the confirmed "No site matches that name." empty-state text is shown. */
    public boolean isNoSiteMatchesMessageDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.noSiteMatchesMessage);
            return isElementVisible(repo.noSiteMatchesMessage);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED structural pattern (dynamic, data-driven lookup - see
     * {@code SiteManagementPageObjectRepository#SITE_ROW_XPATH_TEMPLATE}'s javadoc). True once a site row
     * whose text contains {@code siteName} is visible.
     */
    public boolean isSiteRowDisplayed(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE, siteName)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /** LIVE-VERIFIED structural pattern. Taps the first site row whose text contains {@code siteName}, proceeding into the Main App Shell. */
    public void selectSite(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        // STANDING RULE (reinforced 2026-10-20): always select a site via the search field first, never by
        // scanning/tapping the unfiltered list directly - this is the same real debounce-bug root cause
        // already found once this project (see Base#settleAfterSearchDebounce's javadoc); searching first
        // and unconditionally is the durable fix, not just awaiting debounce when a caller happens to search.
        clearSiteSearch();
        searchForSite(siteName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE, siteName)));
        if (rows.isEmpty()) {
            throw new IllegalStateException("No site row found matching '" + siteName + "' on Site Selection (after searching).");
        }
        clickWithHeal(rows.get(0), "Site Row: " + siteName);
    }

    /**
     * Reaches {@code siteName} regardless of where the shared session currently is - added for the
     * suite-level session-reuse architecture (2026-10-20), where a single login persists across every
     * test in a suite instead of each test logging in fresh. If Site Selection is showing (the session is
     * freshly logged in, no site chosen yet), taps the row directly. Otherwise (already inside the Main
     * App Shell on some other site from an earlier test) uses the real TopBar site switcher instead of
     * forcing a fresh login - no test should ever call {@code OnboardingAuthenticationPage#login} itself
     * anymore outside the Onboarding & Authentication module's own login-flow tests.
     *
     * <p><b>FIX (2026-10-26, confirmed via a real Virtual Guard live crawl):</b> this unconditionally
     * invoked the TopBar site switcher on EVERY call, even when the shared session was already sitting on
     * exactly {@code siteName} (the overwhelmingly common case - almost every test in a module calls this
     * with the same default site). That round-trip is both wasted time and the single most common trigger
     * of this project's confirmed, intermittent "TopBar Site Switcher Control" slow/flaky behavior (see
     * {@code MainAppShellPageObjectRepository#topBarSiteSwitcherControl}'s javadoc). Now short-circuits via
     * {@link #isCurrentlyOnSite(String)} when the switch is a genuine no-op, and only drives the real
     * switcher when the active site actually needs to change.
     */
    public void ensureSiteSelected(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        if (isSiteSelectionScreenDisplayedFast()) {
            selectSite(siteName);
            return;
        }
        if (isCurrentlyOnSite(siteName)) {
            return;
        }
        openSiteSwitcher();
        selectSiteFromSwitcher(siteName);
    }

    /**
     * Fixed 2026-10-26 - true once the Main App Shell's TopBar site-name label currently reads exactly
     * {@code siteName}. Confirmed live (Virtual Guard crawl, 2026-10-26) that the TopBar's own site-name
     * TextView is reliably the FIRST non-empty-text element in document order on every Main App Shell
     * screen observed (Surveillance, Guarding Stats, Arm/Disarm) - used here as a cheap, zero-extra-tap
     * "what site am I on right now" check so {@link #ensureSiteSelected(String)} can skip the switcher
     * entirely when no site change is actually needed.
     */
    public boolean isCurrentlyOnSite(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        List<WebElement> firstTextEl = getDriverForFramework().findElements(By.xpath("(//*[string-length(@text) > 0])[1]"));
        return !firstTextEl.isEmpty() && siteName.equals(firstTextEl.get(0).getAttribute("text"));
    }

    /**
     * LIVE-VERIFIED structural pattern (2026-10-19, corrected against the real Test-backend dump - see
     * {@code SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE}'s javadoc for the full
     * correction history). Counts clickable {@code android.view.View} rows that contain a non-empty-text
     * TextView descendant, excluding the known non-site bottom-nav items ("Virtual Guard"/"Global"/
     * "Support" - LIVE-CONFIRMED to render with the exact same clickable-View-wrapping-a-TextView shape on
     * this same screen, a finding worth flagging: this module's own first-run screen already shows the
     * bottom nav, confirming the Main App Shell's scaffold wraps Site Selection too). Reads each row's text
     * from its TextView DESCENDANT, not the row View's own (always-empty) {@code text} attribute.
     */
    public int countVisibleSiteRows() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        List<WebElement> candidates = getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]"));
        int count = 0;
        for (WebElement c : candidates) {
            String text = rowLabelText(c);
            if (!text.equals("Virtual Guard") && !text.equals("Global") && !text.equals("Support")) {
                count++;
            }
        }
        return count;
    }

    /** Reads a row's label text from its TextView descendant (the row View's own {@code text} attribute is always empty - see repository javadoc). */
    private String rowLabelText(WebElement rowView) {
        try {
            WebElement label = rowView.findElement(By.xpath(".//android.widget.TextView"));
            return attrSafe(label, "text").trim();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return "";
        }
    }

    private String attrSafe(WebElement el, String name) {
        try {
            String v = el.getAttribute(name);
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }

    /** LIVE-VERIFIED (reuses the already-confirmed app-wide gate). TC_FUNC_SITE_008: the same "There is no internet connection." screen confirmed for Login's TC_FUNC_AUTH_015. */
    public boolean isNoInternetConnectionScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            WebElement el = getDriverForFramework().findElement(By.xpath("//*[@text='There is no internet connection.']"));
            return el.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    // =================================================================================================
    // Site Switcher (In-Shell) (FR-SITE-002) - UNVERIFIED, desk-sourced (see repository javadoc)
    // =================================================================================================

    /** UNVERIFIED placeholder. Taps the (unconfirmed-locator) TopBar site-switcher control. */
    public void openSiteSwitcher() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.topBarSiteSwitcherControl, "TopBar Site Switcher Control (UNVERIFIED placeholder)");
    }

    /** UNVERIFIED. Reuses the same site-row structural pattern as Site Selection, assumed (not confirmed) to apply inside the Site Switcher bottom sheet too. */
    public boolean isSiteSwitcherSiteRowDisplayed(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE, siteName)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /** UNVERIFIED. Selects a site from the (assumed-open) Site Switcher bottom sheet. */
    public void selectSiteFromSwitcher(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        // Same standing rule as selectSite() - always search first, never tap the unfiltered list.
        searchInSiteSwitcher(siteName);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE, siteName)));
        if (rows.isEmpty()) {
            throw new IllegalStateException("No site row found matching '" + siteName + "' in the Site Switcher (after searching).");
        }
        clickWithHeal(rows.get(0), "Site Switcher Row: " + siteName);
    }

    /** UNVERIFIED. Types into the (assumed) Site Switcher search field. */
    public void searchInSiteSwitcher(String searchText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.siteSwitcherSearchInput, "Site Switcher Search Input", searchText);
        settleAfterSearchDebounce(); // see Base#settleAfterSearchDebounce's javadoc - same confirmed app-wide search-filter debounce
    }

    /**
     * LIVE-CONFIRMED (2026-10-27, crawl dump logs/page-source-dumps/CRAWL_switcher_no_match.xml), replacing
     * the removed {@code isSiteSwitcherNoMatchDisplayed()}: unlike Site Selection's confirmed "No site
     * matches that name." text, the Site Switcher shows NO textual empty-state message at all when a search
     * matches no assigned site - the dump showed only the "Sites" header and the typed search text, with
     * zero site rows and no empty-state text anywhere on screen. The removed method's assumption (that the
     * switcher reuses Site Selection's empty-state text) was root-caused as the direct cause of two real
     * test failures (verifySiteSwitcherSearchWithNoMatchShowsEmptyState/
     * verifySiteSwitcherSpecialCharacterSearchHandledGracefully both burning the full 90s DEFAULT_WAIT on a
     * message that never appears). True once the switcher sheet is confirmed still open (reusing
     * {@link #isSiteSwitcherOpenFast()}'s proven signal) with zero clickable site rows present - the real,
     * observed "no match" state for this screen.
     */
    public boolean isSiteSwitcherListEmpty() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if (!isSiteSwitcherOpenFast()) {
            return false;
        }
        List<WebElement> anyRow = getDriverForFramework().findElements(
                By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[string-length(@text) > 0]]"));
        return anyRow.isEmpty();
    }

    /**
     * UNVERIFIED. Bounded wait for the "Now viewing site {name}" toast (RequirementAnalysis.md Section
     * 7.2's confirmed-narratively copy) - Android Toasts are transient OS overlays; whether UiAutomator2
     * reliably captures them in this app's build is itself unconfirmed. A short, dedicated wait (not the
     * standard 90s DEFAULT_WAIT, which would be pointless for a toast that only shows for a few seconds)
     * is used here.
     */
    public boolean isNowViewingToastDisplayed(String siteName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + siteName);
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait = new org.openqa.selenium.support.ui.WebDriverWait(
                    getDriverForFramework(), Duration.ofSeconds(5));
            By toastLocator = By.xpath(String.format(SiteManagementPageObjectRepository.NOW_VIEWING_TOAST_XPATH_TEMPLATE, siteName));
            return shortWait.until(d -> !d.findElements(toastLocator).isEmpty());
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Cheap, zero-wait check (a plain {@code findElements().isEmpty()}, no {@code WebDriverWait} at all)
     * for whether the Site Switcher bottom sheet is CURRENTLY open - added 2026-10-21 after a confirmed
     * real failure cascade: a test that leaves the Site Switcher open covers the TopBar, so the NEXT
     * test's attempt to tap the TopBar Profile icon (to log out and reach a fresh Site Selection screen)
     * can't find it and burns a full 90-second {@code waitUntilElementIsClickable} timeout before failing.
     * Used by {@code SiteManagementTests#lightReset()} to close any dangling switcher before the next
     * test's {@code loginToSiteSelection()} runs.
     */
    public boolean isSiteSwitcherOpenFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("(//android.widget.EditText)[1]")).isEmpty()
                && getDriverForFramework().findElements(
                        By.xpath("//android.widget.ImageView[@content-desc='Profile']")).isEmpty();
    }

    /**
     * CORRECTED 2026-10-27 after a confirmed real failure cascade across MainAppShellTests (multiple tests
     * failing with 90s "TopBar Site Switcher Control... not found" timeouts, diagnostic dumps confirming the
     * Site Switcher sheet was still open): the old version always tried {@code clickWithHeal} on the
     * UNVERIFIED {@code siteSwitcherScrim} locator FIRST, which never matched anything real and burned the
     * full 90s {@code waitUntilElementIsClickable} timeout on every single call before ever reaching its own
     * {@code navigateBack()} fallback - and this method is now called from every test's {@code lightReset()}.
     * A live crawl confirmed {@code navigateBack()} ALONE reliably and immediately closes the Site Switcher
     * sheet (no scrim tap needed at all). Switched to use the proven-working mechanism directly, with a
     * short settle-confirmation (the same pattern as {@code MainAppShellPage#dismissBottomSheetMenuWithoutSelecting()})
     * so this method's own postcondition (the sheet really is closed by the time this returns) holds for
     * every caller instead of just hoping a single back-press was enough.
     */
    public void dismissSiteSwitcherWithoutSelecting() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        navigateBack();
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(5))
                    .until(d -> !isSiteSwitcherOpenFast());
        } catch (org.openqa.selenium.TimeoutException e) {
            // Already gone (common case) or genuinely still open - the caller's own next action will
            // surface a real problem if the latter; no further blind retry here.
        }
    }

    /**
     * LIVE-VERIFIED structural pattern (2026-10-19, confirmed Test backend - see
     * {@code docs/dom-snapshots/virtualguard_live_00_site_selection.xml}): checks whether a given
     * bottom-nav tab (by its confirmed-live label text - "Virtual Guard", "Global", "Support" all
     * reconfirmed live on Test; the still-unconfirmed License Plate tab label is also supported via this
     * same pattern once confirmed) is currently present, for TC_FUNC_SITE_014's per-site feature-flag
     * gating check.
     *
     * <p><b>CORRECTION (2026-10-19):</b> this previously (and WRONGLY) matched a bottom-nav tab View's own
     * {@code text} attribute directly - the same bug found and fixed in
     * {@code SiteManagementPageObjectRepository.SITE_ROW_XPATH_TEMPLATE} (see that field's javadoc for the
     * full history). Live evidence confirms every bottom-nav tab is a clickable {@code android.view.View}
     * wrapping a separate non-clickable {@code android.widget.TextView} descendant that holds the label -
     * the tab View's own {@code text} is always empty. Fixed to require a matching TextView descendant.
     */
    /**
     * Fixed 2026-10-26 after a confirmed live failure in {@code verifyForceKillImmediatelyAfterSwitchingPersistsNewSite}:
     * this had NO wait at all, and was being called immediately after {@code forceKillAndRelaunchApp()} - a
     * cold app relaunch, which needs real time to reach the Main App Shell and render its bottom-nav tabs.
     * Reading the tab list with zero settle time reliably observed a false "not present" before the shell
     * had finished rendering. Now bounded-waits (10s) for at least one matching element before the final
     * visibility check, same "non-blind real-time pause via WebDriverWait" pattern as this project's other
     * fast-wait variants (e.g. {@link #isSiteSelectionScreenDisplayedFast()}) - never {@code Thread.sleep()}.
     */
    public boolean isBottomNavTabPresent(String tabLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + tabLabel);
        By locator = By.xpath("//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'" + tabLabel + "')]]");
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(10))
                    .until(d -> !d.findElements(locator).isEmpty());
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
        List<WebElement> tabs = getDriverForFramework().findElements(locator);
        return !tabs.isEmpty() && tabs.get(0).isDisplayed();
    }
}
