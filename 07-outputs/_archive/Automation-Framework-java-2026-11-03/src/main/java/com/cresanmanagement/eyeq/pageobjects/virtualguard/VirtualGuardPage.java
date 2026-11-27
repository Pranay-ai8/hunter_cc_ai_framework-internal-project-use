package com.cresanmanagement.eyeq.pageobjects.virtualguard;

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
 * Page Action class for the Virtual Guard module (Surveillance/camera groups, Guarding Stats dashboard,
 * Arm/Disarm + Schedule, Forensic Clips).
 *
 * <p>Written in CODE-WRITING-ONLY mode (2026-10-19) - see
 * {@code VirtualGuardPageObjectRepository}'s class javadoc for the full evidence basis and the three
 * documented gaps (KPI tile internal structure, Activity Log row structure, schedule day-toggle
 * ambiguity). Every method below is UNVERIFIED, desk-sourced; none has been exercised against a live
 * device/emulator this pass, per the standing "no execution" rule in force for this work.
 *
 * <p>Opening the Virtual Guard bottom-sheet menu itself (tapping the bottom-nav tab, reading/dismissing
 * the menu) is NOT duplicated here - tests use the already-written {@code MainAppShellPage} for that,
 * since FR-SHELL-002 (the bottom-nav/menu mechanism) is that module's own responsibility. This class only
 * covers what happens AFTER a menu item is tapped (the four destination screens) plus the Contact item's
 * presence-only check is intentionally NOT included (TC_FUNC_VG_006 is TBD - destination out of scope).
 */
public class VirtualGuardPage extends Base {

    private static final Logger logger = LogManager.getLogger(VirtualGuardPage.class.getName());
    private final VirtualGuardPageObjectRepository repo;

    public VirtualGuardPage() {
        repo = new VirtualGuardPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    // =================================================================================================
    // Surveillance (FR-VG-002)
    // =================================================================================================

    /** UNVERIFIED. True once the confirmed "Surveillance" screen title is visible. */
    public boolean isSurveillanceScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.surveillanceScreenTitle);
            return isElementVisible(repo.surveillanceScreenTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Added 2026-10-26 after a confirmed real failure (90s timeout in {@code openVirtualGuardMenu} when the
     * shared session was stranded on a different VG sub-screen from a prior test - see
     * {@code VirtualGuardTests#lightReset()}'s javadoc for the full root cause). Genuinely zero-wait variant
     * for a quick branch-decision check, bypassing PageFactory the same way as this class's other Fast
     * checks - use {@link #isSurveillanceScreenDisplayed()} for a real assertion.
     */
    public boolean isSurveillanceScreenDisplayedFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Surveillance']")).isEmpty();
    }

    /**
     * LIVE-VERIFIED (2026-10-22, real device crawl, udid 8BQX1DY81). Opens/re-opens the Virtual Guard
     * bottom-sheet menu by tapping the Surveillance screen's own title - confirmed live that the
     * {@code navTitleTextView} TextView showing "Surveillance" is ITSELF the clickable element (unlike
     * License Plate's equivalent, where the title TextView is non-clickable and a separate ancestor View
     * must be tapped instead - the two modules use different underlying structures, confirmed by direct
     * inspection, not assumed to match).
     *
     * <p>This corrects a real, confirmed bug: tapping the "Virtual Guard" bottom-nav tab lands DIRECTLY on
     * the Surveillance screen (its own real default-landed destination, not a menu) - it does NOT open any
     * menu by itself. The entire {@code VirtualGuardTests} class previously assumed (per this class's own
     * "CODE-WRITING-ONLY mode" javadoc, written without live verification) that the bottom-nav tap alone
     * opened the menu, which live-crawling now disproves. {@code VirtualGuardTests#openVirtualGuardMenu}
     * must call this method after landing on Surveillance before checking for menu items.
     */
    public void tapSurveillanceTitleToOpenMenu() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.surveillanceScreenTitle, "Surveillance Title (menu open/re-open)");
    }

    /**
     * LIVE-VERIFIED (2026-10-22, real device crawl). Generic form of {@link #tapSurveillanceTitleToOpenMenu()} -
     * opens/re-opens the Virtual Guard menu by tapping WHATEVER Virtual Guard screen's own title is
     * currently showing. Confirmed live on both Arm/Disarm's and Guarding Stats' title structures (a
     * non-clickable TextView inside a clickable wrapping View - different from Surveillance's own
     * directly-clickable TextView), by simply locating the title TextView by its exact text and clicking
     * it directly: Appium's click taps the element's center coordinates, which fall inside the real
     * touch target's bounds regardless of exactly which ancestor node reports {@code clickable=true}.
     */
    public void tapCurrentScreenTitleToOpenMenu(String currentTitleText) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + currentTitleText);
        WebElement title = getDriverForFramework().findElement(
                By.xpath("//android.widget.TextView[@text='" + currentTitleText + "']"));
        clickWithHeal(title, "Current Screen Title (" + currentTitleText + ") - menu open/re-open");
    }

    /**
     * LIVE-VERIFIED (2026-10-22, real device crawl + validation run). Returns to the Surveillance screen
     * from whichever Virtual Guard sub-screen is currently showing (Guarding Stats, Forensic Clips, or
     * Arm/Disarm), via the real "reopen menu, then select Surveillance" journey - LIVE-CONFIRMED that the
     * Android system back button does NOT navigate between Virtual Guard's own sections (pressing back
     * from a menu-reached sub-screen does nothing; only the menu itself can move between sections).
     *
     * <p><b>FIX (2026-10-22, real device validation run):</b> the first version detected the current
     * screen using {@code isElementVisible(repo.someTitleField)} against this class's {@code @AndroidFindBy}
     * fields - which turned out to NOT be genuinely zero-wait: Appium's own PageFactory element-location
     * layer silently retries for up to ~10 real seconds internally before reporting an {@code @AndroidFindBy}
     * field "not found", a wait that is invisible to (and not controllable from) this method's own code.
     * Chaining three such checks in an if/else-if meant a single call could silently cost up to ~20-30
     * real seconds, confirmed live (logs/automation1.log, validation run of 2026-10-22 13:3x, repeated
     * ~10s gaps between consecutive "isElementVisible is executed" lines with no intervening action logged).
     * Rewritten to use raw {@code findElements(By.xpath(...))} instead (the same pattern already proven
     * genuinely zero-wait elsewhere in this project, e.g. {@code isBottomNavTabPresent}/
     * {@code isSiteSwitcherOpenFast}) - this bypasses the PageFactory proxy layer entirely, so an absent
     * title is detected instantly rather than after an internal ~10s retry.
     */
    public void returnToSurveillance() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String currentTitle = null;
        for (String candidate : new String[]{"Guarding Stats", "Forensic Clips", "Arm/Disarm"}) {
            if (!getDriverForFramework().findElements(
                    By.xpath("//android.widget.TextView[@text='" + candidate + "']")).isEmpty()) {
                currentTitle = candidate;
                break;
            }
        }
        if (currentTitle == null) {
            return;
        }
        tapCurrentScreenTitleToOpenMenu(currentTitle);
        com.cresanmanagement.eyeq.Pages.MainAppShellPage().tapBottomSheetMenuItem("Surveillance");
    }

    /**
     * UNVERIFIED structural pattern. True once a camera group tile/row whose text contains {@code groupName}
     * is visible.
     *
     * <p>Fixed 2026-10-26 after a confirmed real failure (still failing even after correcting the CSV's
     * Camera Group name to the real "abcd"): LIVE-CONFIRMED via the very first device crawl of this screen
     * that the "Camera Groups" section shows a literal "Loading..." placeholder immediately after
     * navigation, with the real group names ("abcd"/"eyyy") only appearing once that finishes - the same
     * async-loading race already confirmed and fixed for {@code isKpiTileNonBlank()}.
     *
     * <p><b>FIX (2026-10-26, confirmed the first 10s bound was still too tight):</b> a direct live crawl
     * confirmed "abcd" reliably renders within ~5s when landing on Surveillance directly via the bottom-nav
     * tab, yet {@code verifySurveillanceScreenLoadsCameraGridForActiveSite}'s real path (reached via
     * re-tapping "Surveillance" as a MENU ITEM, not a fresh tab landing) still timed out at 10s twice in a
     * row - the extra navigation step appears to add enough overhead/re-fetch delay to occasionally exceed
     * that margin.
     *
     * <p><b>FIX (2026-10-27, confirmed 25s was STILL too tight):</b> two further live failures timed out at
     * 26-27s each - consistently just over the 25s bound, not random flakiness, pointing to a real load
     * time on this specific navigation path that reliably sits right around that mark. Widened to 45s for
     * genuine safety margin above the observed ~26-27s ceiling, rather than keep nudging a number that's
     * repeatedly proven just barely insufficient.
     */
    public boolean isCameraGroupDisplayed(String groupName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + groupName);
        By locator = By.xpath(String.format(VirtualGuardPageObjectRepository.CAMERA_GROUP_XPATH_TEMPLATE, groupName));
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(45))
                    .until(d -> {
                        List<WebElement> rows = d.findElements(locator);
                        return !rows.isEmpty() && rows.get(0).isDisplayed();
                    });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // =================================================================================================
    // Guarding Stats Dashboard (FR-VG-003)
    // =================================================================================================

    /** UNVERIFIED. True once the confirmed "Guarding stats" screen title is visible. */
    public boolean isGuardingStatsScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.guardingStatsScreenTitle);
            return isElementVisible(repo.guardingStatsScreenTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * CORRECTED 2026-10-26 after a confirmed real failure (the dashboard never actually changed month):
     * the old version searched for {@code monthLabel} directly on the dashboard, but the dashboard only
     * ever shows the CURRENT month's badge until it is tapped - no other month's text exists anywhere on
     * screen before that. LIVE-CONFIRMED via a real device crawl: tapping the month badge opens a real
     * picker listing multiple selectable months at once ("{Month}, {Year}" each). Now opens the picker
     * first (via the corrected {@code monthSelectorControl}), then selects the option matching
     * {@code monthLabel}.
     */
    public void selectMonth(String monthLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + monthLabel);
        clickWithHeal(repo.monthSelectorControl, "Month Selector Badge (opens picker)");
        WebElement option = getDriverForFramework().findElement(By.xpath("//android.widget.TextView[contains(@text,'" + monthLabel + "')]"));
        clickWithHeal(option, "Month Selector Option: " + monthLabel);
    }

    /**
     * CORRECTED 2026-10-26 - same root cause as {@link #selectMonth(String)}: the old version scrolled on
     * the (then-wrong) {@code monthSelectorControl} locator without ever confirming the real picker opened.
     * LIVE-CONFIRMED the real picker lists multiple months at once (no scroll needed for the 6 months
     * observed - October down to May 2026); this opens the picker, then selects whichever date-shaped
     * option is LAST in document order (the furthest-back month currently rendered) as the "oldest
     * available" approximation. If more months exist beyond what renders without scrolling, scrolling
     * within the open picker before re-reading the list would be a live pass's next step - not yet
     * confirmed necessary since none of the 6 observed needed it.
     *
     * <p>Scoped to date-shaped TextViews appearing BEFORE the "Total Alarm Events" KPI label specifically -
     * LIVE-CONFIRMED that label's own tile repeats the CURRENT month text again further down the page
     * (unrelated to the picker), which would otherwise corrupt a naive "last match on the whole page" read.
     */
    public void selectOldestAvailableMonth() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.monthSelectorControl, "Month Selector Badge (opens picker)");
        List<WebElement> options = getDriverForFramework().findElements(By.xpath(
                "//android.widget.TextView[contains(@text, ', 20')][following::android.widget.TextView[@text='Total Alarm Events']]"));
        if (options.isEmpty()) {
            throw new IllegalStateException("Month picker did not reveal any selectable month options.");
        }
        clickWithHeal(options.get(options.size() - 1), "Month Selector Option (oldest visible)");
    }

    /**
     * CORRECTED 2026-10-26 after a real device crawl (Main Site, Guarding Stats dashboard): the label and
     * its numeric value ARE separate TextView nodes (resolving the repository's documented structure gap),
     * but they are not always immediate siblings - "Total Alarm Events" specifically has an extra repeated
     * month-label TextView ("October, 2026") between its label and its value, while the other 4 KPIs don't.
     * Rather than hardcode that one exception, this scans the TextViews following the label (in document
     * order) and returns the first one that is purely numeric/decimal (the confirmed shape of every real
     * value observed live, all "0" with zero alarm activity this month) - skipping any non-numeric text
     * (month labels, comparison captions) regardless of how many of them sit in between.
     */
    public String getKpiTileValue(String kpiLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + kpiLabel);
        List<WebElement> candidates = getDriverForFramework().findElements(By.xpath(
                "//android.widget.TextView[@text='" + kpiLabel + "']/following::android.widget.TextView[translate(@text,'0123456789.%','')='' and string-length(@text) > 0]"));
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0).getAttribute("text");
    }

    /**
     * UNVERIFIED. True once at least a non-null, non-blank value is readable for the given KPI label.
     *
     * <p>Fixed 2026-10-26 after a confirmed real failure in {@code verifyGuardingStatsDashboardLoadsWithAllFiveKpiTiles}
     * ("Total Alarm Events" - "expected [true] but found [false]"): the SAME KPI's own dedicated test
     * ({@code verifyTotalAlarmEventsKpiTile}) passed later in the exact same run - confirming this is a
     * genuine async-loading race, not a locator bug. The dashboard's KPI values populate shortly after
     * navigation rather than being present instantly; checking the very FIRST KPI immediately after landing
     * on the screen (as the "all 5 tiles" test does) can read it before it has finished rendering. Now
     * bounded-retries (10s) instead of reading once, instantly.
     */
    public boolean isKpiTileNonBlank(String kpiLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + kpiLabel);
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(10))
                    .until(d -> {
                        String value = getKpiTileValue(kpiLabel);
                        return value != null && !value.trim().isEmpty();
                    });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** UNVERIFIED. True once the confirmed "Newest at top" default-sort caption is visible (used as a proxy "dashboard content has rendered" signal). */
    public boolean isNewestAtTopLabelDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.newestAtTopLabel);
    }

    // =================================================================================================
    // Forensic Clips (FR-VG-006)
    // =================================================================================================

    /** UNVERIFIED. True once the confirmed "Forensic Clips" screen title is visible. */
    public boolean isForensicClipsScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.forensicClipsScreenTitle);
            return isElementVisible(repo.forensicClipsScreenTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    // =================================================================================================
    // Arm/Disarm (FR-VG-004)
    // =================================================================================================

    /** UNVERIFIED. True once the confirmed "Arm/Disarm" screen title is visible. */
    public boolean isArmDisarmScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.armDisarmScreenTitle);
            return isElementVisible(repo.armDisarmScreenTitle);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** UNVERIFIED placeholder. Raw text of the current Arm/Disarm status line (see repository javadoc). */
    public String getArmDisarmStatusText() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            return repo.armDisarmStatusText.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** UNVERIFIED structural pattern. True once a control labelled {@code buttonLabel} ("Arm" or "Disarm") is present and enabled (clickable). */
    public boolean isArmDisarmButtonEnabled(String buttonLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + buttonLabel);
        List<WebElement> buttons = getDriverForFramework().findElements(
                By.xpath(String.format(VirtualGuardPageObjectRepository.ARM_DISARM_BUTTON_XPATH_TEMPLATE, buttonLabel, buttonLabel)));
        if (buttons.isEmpty()) {
            return false;
        }
        String enabledAttr = attrSafe(buttons.get(0), "enabled");
        return enabledAttr.isEmpty() || Boolean.parseBoolean(enabledAttr);
    }

    /**
     * Added 2026-10-26 - confirmed live that the Arm/Disarm screen shows EXACTLY ONE action button at a
     * time: "Disarm" while the site is currently Armed, "Arm" while currently Disarmed (never both).
     * {@link #isArmDisarmButtonEnabled(String)} conflates "control exists but is disabled" with "control
     * doesn't exist at all because the site is in the opposite state" - both read as {@code false}. Several
     * tests (TC_FUNC_VG_031/034) assume the site is currently Disarmed and check the "Arm" control is
     * disabled-with-helper-text; LIVE-CONFIRMED the real site was ARMED at crawl time (schedule-driven,
     * not independently controllable), which made those tests fail for the wrong reason - no "Arm" control
     * exists at all right now, rather than one existing-but-disabled. Used to distinguish the two cases and
     * skip cleanly when the assumed precondition genuinely isn't the current real state, rather than
     * failing on a misleading assertion.
     */
    public boolean isArmDisarmButtonPresent(String buttonLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + buttonLabel);
        return !getDriverForFramework().findElements(
                By.xpath(String.format(VirtualGuardPageObjectRepository.ARM_DISARM_BUTTON_XPATH_TEMPLATE, buttonLabel, buttonLabel))).isEmpty();
    }

    /**
     * UNVERIFIED structural pattern. Taps the control labelled {@code buttonLabel} - used both for the
     * TC_FUNC_VG_034 "tap while disabled has no effect" negative case (a disabled native control simply
     * swallows the tap) and for any future enabled-state positive flow.
     *
     * <p><b>FIX (2026-10-27, confirmed real failure):</b> originally used {@code clickWithHeal}, whose
     * {@code waitUntilElementIsClickable} requires the element to report {@code enabled=true} before it
     * will even attempt the click - correct behavior for a normal tap, but fundamentally incompatible with
     * THIS method's own documented negative-test purpose. Against a confirmed-disabled "Arm" control, that
     * wait correctly never succeeded and burned the full 90s timeout before failing the test outright - the
     * opposite of what {@code verifyTappingDisabledArmButtonHasNoEffect} needs (tap it anyway, then assert
     * nothing changed). Uses a raw click instead, bypassing the clickable/enabled wait entirely - on Android
     * a disabled native View simply does not fire its click listener, so a raw tap is the correct way to
     * exercise this negative case without the framework itself blocking the attempt.
     */
    public void tapArmDisarmButton(String buttonLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + buttonLabel);
        List<WebElement> buttons = getDriverForFramework().findElements(
                By.xpath(String.format(VirtualGuardPageObjectRepository.ARM_DISARM_BUTTON_XPATH_TEMPLATE, buttonLabel, buttonLabel)));
        if (buttons.isEmpty()) {
            throw new IllegalStateException("No '" + buttonLabel + "' control found on the Arm/Disarm screen.");
        }
        buttons.get(0).click();
    }

    /** UNVERIFIED. True once the confirmed "Arming is available from..." helper-text phrase is visible. */
    public boolean isArmHelperTextDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.armHelperText);
    }

    /** UNVERIFIED. Raw text of the Arm helper-text element (expected to contain the next-available-time, CSV/business-generated, never hardcoded). */
    public String getArmHelperText() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            return repo.armHelperText.getAttribute("text");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** UNVERIFIED placeholder (see repository javadoc's Activity Log structure gap). Best-effort count of rows rendered after the "Activity Log" header. */
    public int getActivityLogEntryCount() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return getDriverForFramework().findElements(By.xpath(VirtualGuardPageObjectRepository.ACTIVITY_LOG_ENTRY_XPATH)).size();
    }

    /** UNVERIFIED. True once the confirmed "Activity Log" section heading is visible. */
    public boolean isActivityLogDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.activityLogHeader);
    }

    // =================================================================================================
    // Arm/Disarm Schedule bottom sheet (FR-VG-005)
    // =================================================================================================

    /** UNVERIFIED. Taps the "View Schedule" control to open the Schedule bottom sheet. */
    public void tapViewSchedule() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.viewScheduleControl, "View Schedule Control");
    }

    /** UNVERIFIED. True once the confirmed "Armed"/"Disarmed" segmented-control labels are both visible (used as the Schedule sheet's own open signal). Full-wait variant for asserting the sheet actually opened - use {@link #isScheduleSheetDisplayedFast()} for a cheap "is it open right now" branch check. */
    public boolean isScheduleSheetDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.scheduleArmedSegment);
            return isElementVisible(repo.scheduleArmedSegment) && isElementVisible(repo.scheduleDisarmedSegment);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Added 2026-10-26 after a confirmed real issue: {@code VirtualGuardTests#lightReset()} calls an
     * "is the Schedule sheet open" check after EVERY test in the class (including Guarding Stats/
     * Surveillance/menu tests where it is essentially always closed), and {@link #isScheduleSheetDisplayed()}
     * uses the full {@code waitUntilElementIsVisible} wait plus two separate {@code @AndroidFindBy}-field
     * {@code isElementVisible} calls - each of which can silently cost up to ~10 real seconds internally via
     * Appium's own PageFactory retry before reporting "not found" (the same confirmed gotcha already
     * root-caused in {@link #returnToSurveillance()}'s own javadoc). That meant EVERY test's cleanup was
     * paying a large, usually-wasted timeout checking for a sheet that was essentially never open. Bypasses
     * the PageFactory proxy layer entirely via raw {@code findElements}, the same proven pattern already
     * used in {@link #returnToSurveillance()} - genuinely zero-wait when the sheet is closed.
     *
     * <p>FIX (2026-10-26, confirmed still slowing every test down): the first version used a universal
     * {@code //*[@text='...']} wildcard tag - a confirmed slow pattern in Appium/UiAutomator2, which must
     * evaluate the predicate against EVERY node type in the whole accessibility tree, on every single
     * test's cleanup, regardless of the WebDriverWait bypass. Narrowed to the concrete
     * {@code android.widget.TextView} type, consistent with every other confirmed-real locator in this
     * project, which UiAutomator2's XPath engine can filter far more cheaply.
     */
    public boolean isScheduleSheetDisplayedFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Armed']")).isEmpty()
                && !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Disarmed']")).isEmpty();
    }

    /** UNVERIFIED. Selects the "Armed" segment of the Schedule sheet's segmented control. */
    public void selectArmedScheduleSegment() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.scheduleArmedSegment, "Schedule Sheet - Armed Segment");
    }

    /** UNVERIFIED. Selects the "Disarmed" segment of the Schedule sheet's segmented control. */
    public void selectDisarmedScheduleSegment() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.scheduleDisarmedSegment, "Schedule Sheet - Disarmed Segment");
    }

    /**
     * CORRECTED 2026-10-26 (see repository javadoc's {@code DAY_TOGGLE_XPATH_TEMPLATE} for the full
     * confirmed real structure - a position-indexed plain TextView, not a Switch). Toggles the Nth
     * (1-based) day label by POSITION. {@code clickWithHeal} taps the element's center coordinates
     * regardless of its own {@code clickable} flag (the same mechanism already proven for other
     * non-clickable-flagged-but-real tap targets project-wide), so this executes a real tap - LIVE-CONFIRMED
     * the resulting state change (if any) is NOT reflected anywhere in the accessibility tree (see
     * {@link #isScheduleDayToggledOn(int)}'s javadoc), so this method's own effect cannot be independently
     * verified; callers should only assert on-screen stability (no crash/conflict), not the resulting
     * per-day state.
     */
    public void toggleScheduleDay(int dayIndexOneBased) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: day " + dayIndexOneBased);
        WebElement toggle = getDriverForFramework().findElement(
                By.xpath(String.format(VirtualGuardPageObjectRepository.DAY_TOGGLE_XPATH_TEMPLATE, dayIndexOneBased)));
        clickWithHeal(toggle, "Schedule Sheet - Day Toggle #" + dayIndexOneBased);
    }

    /**
     * CONFIRMED NON-FUNCTIONAL 2026-10-26: a live device crawl tapped a day label directly and re-read the
     * full accessibility tree immediately after - NEITHER the tapped label NOR its single shared parent
     * (all 7 day labels sit under ONE common non-clickable container, confirmed via a live dump) exposed
     * any {@code selected}/{@code checked} attribute change, before or after the tap. There is currently no
     * accessible signal this method - or any Appium-based check - could read to determine a day's on/off
     * state; this will ALWAYS return {@code false} regardless of the real UI state. Kept for interface
     * completeness (not currently called by any implemented test - {@code verifyTogglingAllSevenDaysToUniformStateIsAcceptedWithoutConflict}
     * only exercises {@link #toggleScheduleDay(int)} and checks overall screen stability, not per-day
     * state) but should not be relied on until a real signal is found (e.g. a resource-id or content-desc
     * the current build doesn't expose, or a different interaction surface entirely).
     */
    public boolean isScheduleDayToggledOn(int dayIndexOneBased) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: day " + dayIndexOneBased);
        try {
            WebElement toggle = getDriverForFramework().findElement(
                    By.xpath(String.format(VirtualGuardPageObjectRepository.DAY_TOGGLE_XPATH_TEMPLATE, dayIndexOneBased)));
            return Boolean.parseBoolean(attrSafe(toggle, "checked"));
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /** UNVERIFIED placeholder. Dismisses the Schedule sheet without an explicit save (no Save control is documented - see FR-VG-005's Partially Confirmed status). */
    public void dismissScheduleSheet() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        navigateBack();
    }

    private String attrSafe(WebElement el, String name) {
        try {
            String v = el.getAttribute(name);
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }
}
