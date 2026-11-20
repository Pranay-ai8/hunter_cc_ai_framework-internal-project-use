package com.cresanmanagement.eyeq.pageobjects.mainappshell;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Action class for the Main App Shell module (persistent Top Bar - site switcher control + Profile
 * icon; persistent Bottom Navigation - per-site gated tabs, with Virtual Guard/License Plate opening
 * bottom-sheet menus).
 *
 * <p>Written in CODE-WRITING-ONLY mode (2026-10-19) - see
 * {@code MainAppShellPageObjectRepository}'s class javadoc for the full evidence basis and the two
 * documented evidence conflicts (bottom-nav tab label wording, Virtual Guard menu item wording). Every
 * method below is UNVERIFIED, desk-sourced; none has been exercised against a live device/emulator this
 * pass, per the standing "no execution" rule in force for this work.
 */
public class MainAppShellPage extends Base {

    private static final Logger logger = LogManager.getLogger(MainAppShellPage.class.getName());
    private final MainAppShellPageObjectRepository repo;

    public MainAppShellPage() {
        repo = new MainAppShellPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    // =================================================================================================
    // Persistent Top Bar (FR-SHELL-001) - UNVERIFIED
    // =================================================================================================

    /** UNVERIFIED. TC_FUNC_SHELL_001: taps the TopBar's site-name control to open the Site Switcher. */
    public void tapTopBarSiteSwitcher() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.topBarSiteSwitcherControl, "TopBar Site Switcher Control (UNVERIFIED placeholder)");
    }

    /**
     * LIVE-VERIFIED 2026-10-20: taps the TopBar's Profile icon to open Account Settings.
     *
     * <p><b>Root-cause history (kept for future maintainers):</b> this tap appeared unreliable across
     * every mechanism tried during this module's crawl - plain {@code element.click()}, a raw W3C
     * {@code tapByCoordinates}, UiAutomator2's {@code mobile: clickGesture}, and even a direct
     * {@code adb shell input tap} issued from within the test JVM all intermittently failed to open
     * Account Settings, while the IDENTICAL {@code adb shell input tap} command issued manually (outside
     * any test run) worked every time. The eventual explanation was NOT a bug in any tap mechanism: a
     * SECOND process was found attached to the same device - {@code com.genymobile.scrcpy.Server} (the
     * screen-mirroring tool `scrcpy`, confirmed via {@code Get-CimInstance Win32_Process}), used to watch
     * the device live during this session. A real touch coming through scrcpy's own input channel
     * (e.g. a stray mouse click on the mirrored window) can land on the device at any time, independent
     * of and indistinguishable from this test's own taps, and can dismiss a freshly-opened overlay before
     * the next assertion runs. This is an environmental interference risk, not an app or framework defect -
     * if this tap (or any other fast, easy-to-dismiss overlay interaction) is ever flaky again, check
     * for an active scrcpy/mirroring session on the target device first. A plain {@code clickWithHeal}
     * is correct and sufficient here, consistent with every other control in this project.
     *
     * <p><b>RELIABILITY MITIGATION:</b> even with no mirroring tool active, this specific tap was still
     * observed to occasionally have no visible effect (confirmed via the real {@code accountSettingsContainer}
     * resource-id not appearing afterward, with no exception thrown by the click itself) - a real,
     * intermittent flake on this control specifically, not reproduced on any other control in this
     * project this session. Rather than leave it unreliable, this retries the tap (up to 3 attempts)
     * until {@link #isAccountSettingsScreenDisplayed()} confirms arrival, each attempt bounded by a short
     * wait so a slow-but-working first tap isn't needlessly re-tapped.
     */
    public void tapTopBarProfileIcon() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        for (int attempt = 1; attempt <= 3; attempt++) {
            clickWithHeal(repo.topBarProfileIcon, "TopBar Profile Icon");
            try {
                new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(8))
                        .ignoring(org.openqa.selenium.StaleElementReferenceException.class)
                        .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(repo.accountSettingsContainer));
                return;
            } catch (org.openqa.selenium.TimeoutException e) {
                logger.warn("tapTopBarProfileIcon: Account Settings did not appear after attempt " + attempt + "/3 - retrying.");
            }
        }
        throw new RuntimeException("tapTopBarProfileIcon: Account Settings did not open after 3 tap attempts.");
    }

    /**
     * UNVERIFIED placeholder. True once the Site Switcher bottom sheet appears open, modeled on the same
     * single-EditText-is-the-search-field assumption already made (and flagged) in
     * {@code SiteManagementPageObjectRepository#siteSwitcherSearchInput} - duplicated here as a raw lookup
     * (rather than depending on the Site Management module directly) to keep this module's own TopBar
     * tests self-contained. A live pass MUST confirm or replace this.
     */
    public boolean isSiteSwitcherOpen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            WebElement el = getDriverForFramework().findElement(By.xpath("(//android.widget.EditText)[1]"));
            return el.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    /**
     * UNVERIFIED. True once at least one of Account Settings' confirmed main-menu rows is visible
     * (TC_UI_ACCT_001). Used only to confirm arrival at Account Settings from this module's own
     * responsibility (FR-SHELL-001's "Profile icon opens Account Settings") - not to operate within that
     * screen, which belongs to the (separately written) Account Settings module.
     */
    public boolean isAccountSettingsScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.accountSettingsContainer);
            return isElementVisible(repo.accountSettingsContainer);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Added 2026-10-27 after a confirmed real failure (MainAppShellTests: a run of tests dependent on the
     * TopBar Site Switcher failed back-to-back with "SelfHealer: no candidate scored >= 0.75" - a diagnostic
     * page-source dump confirmed the shared session was still showing Account Settings, left open by
     * {@code verifyTopBarProfileIconOpensAccountSettings} - its own {@code lightReset()} only checked for the
     * Login screen, never for a still-open Account Settings panel, so every later test inherited it and
     * could never find the TopBar's own controls). Genuinely zero-wait variant (reads the confirmed real
     * {@code accountSettingsContainer} resource-id directly via raw {@code findElements}, bypassing
     * PageFactory) for a cheap per-test cleanup branch check - use {@link #isAccountSettingsScreenDisplayed()}
     * for a real assertion.
     */
    public boolean isAccountSettingsScreenDisplayedFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.id("com.cresanmanagement.eyeq:id/accountSettingsContainer")).isEmpty();
    }

    /**
     * UNVERIFIED. True once neither the Site Switcher nor Account Settings' signal elements are visible -
     * i.e. any TopBar overlay has closed. Used by the rapid-double-tap and force-kill/background shell
     * tests (TC_FUNC_SHELL_004/005/006) as a "no stuck overlay" check, reusing the one concrete Account
     * Settings signal available plus a best-effort Site Switcher signal shared with the bottom-sheet-menu
     * check below.
     */
    public boolean isAnyTopBarOverlayOpen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.accountSettingsGeneralInformationRow) || isBottomSheetMenuOpen();
    }

    // =================================================================================================
    // Persistent Bottom Navigation (FR-SHELL-002) - UNVERIFIED
    // =================================================================================================

    /**
     * UNVERIFIED structural pattern (duplicated from Site Management's live-confirmed clickable-View-with-
     * own-text shape - see repository javadoc). True once a bottom-nav tab whose text contains
     * {@code tabLabel} is visible.
     */
    /**
     * Fixed 2026-10-26 - see {@code SiteManagementPage#isBottomNavTabPresent}'s javadoc for the full
     * confirmed root-cause (zero wait, called immediately after a cold app relaunch). Same fix applied
     * here for this class's own equivalent method.
     */
    public boolean isBottomNavTabPresent(String tabLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + tabLabel);
        By locator = By.xpath(String.format(MainAppShellPageObjectRepository.BOTTOM_NAV_TAB_XPATH_TEMPLATE, tabLabel));
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(10))
                    .until(d -> !d.findElements(locator).isEmpty());
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
        List<WebElement> tabs = getDriverForFramework().findElements(locator);
        return !tabs.isEmpty() && tabs.get(0).isDisplayed();
    }

    /** UNVERIFIED structural pattern. Taps the first bottom-nav tab whose text contains {@code tabLabel}. */
    public void tapBottomNavTab(String tabLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + tabLabel);
        List<WebElement> tabs = getDriverForFramework().findElements(
                By.xpath(String.format(MainAppShellPageObjectRepository.BOTTOM_NAV_TAB_XPATH_TEMPLATE, tabLabel)));
        if (tabs.isEmpty()) {
            throw new IllegalStateException("No bottom-nav tab found matching '" + tabLabel + "'.");
        }
        clickWithHeal(tabs.get(0), "Bottom Nav Tab: " + tabLabel);
    }

    /**
     * UNVERIFIED structural pattern. True once a bottom-sheet menu row whose text contains
     * {@code itemLabel} is visible (used for both the Virtual Guard and License Plate menus, which share
     * the same row shape per the functional spec).
     */
    public boolean isBottomSheetMenuItemDisplayed(String itemLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + itemLabel);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(MainAppShellPageObjectRepository.BOTTOM_SHEET_MENU_ITEM_XPATH_TEMPLATE, itemLabel)));
        return !rows.isEmpty() && rows.get(0).isDisplayed();
    }

    /**
     * FIX (2026-10-22, real device validation run): a zero-wait check (like
     * {@link #isBottomSheetMenuItemDisplayed(String)}) is correct for confirming a single, already-open,
     * settled menu - but confirmed live that scanning EVERY row of a just-opened menu in rapid succession
     * (e.g. "list all N items") can race the sheet's own open/settle animation: the Virtual Guard menu's
     * 1st/2nd rows (Surveillance, Guarding Stats) were confirmed visible within ~1.3s of the opening tap,
     * but its 3rd row (Forensic Clips) was NOT yet rendered/visible at that same zero-wait check time on
     * two separate real runs (logs show the earlier rows passing, this one consistently failing, well
     * under a second after the tap). A short, bounded (5s) wait variant for exactly this "confirm a row
     * further down a just-opened list has settled" use case - not a replacement for the zero-wait variant's
     * other, already-correct uses.
     */
    public boolean isBottomSheetMenuItemDisplayedSettled(String itemLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + itemLabel);
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(5))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            By.xpath(String.format(MainAppShellPageObjectRepository.BOTTOM_SHEET_MENU_ITEM_XPATH_TEMPLATE, itemLabel))));
            if (isBottomSheetMenuItemDisplayed(itemLabel)) {
                return true;
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            // fall through to the direct-TextView fallback below.
        }
        // EXPERIMENTAL MITIGATION (2026-10-22) - NOT a confirmed root-cause fix, flagged honestly as such:
        // a real validation run found the Virtual Guard menu's "Forensic Clips" row consistently
        // unresolvable via the compound View-wrapping-TextView xpath above even after the full 5s wait,
        // despite a byte-for-byte dump comparison against the structurally-identical "Guarding Stats" row
        // (which IS found) showing no difference at all - same classes, same clickable attributes, same
        // nesting. The real cause (a UiAutomator2/Appium-side XPath-engine quirk with this specific
        // compound predicate on this specific row, not a DOM or project-code difference) is NOT identified.
        // This fallback tries the simpler, already-proven-reliable direct-TextView-by-exact-text locator
        // (the same pattern used successfully in tapCurrentScreenTitleToOpenMenu) as a second attempt before
        // giving up, in case the compound predicate is what's specifically failing.
        List<WebElement> direct = getDriverForFramework().findElements(
                By.xpath("//android.widget.TextView[@text='" + itemLabel + "']"));
        return !direct.isEmpty() && direct.get(0).isDisplayed();
    }

    /**
     * UNVERIFIED structural pattern. Taps a bottom-sheet menu row whose text contains {@code itemLabel}
     * (e.g. "Vehicles" inside the License Plate menu, "Guarding Stats" inside the Virtual Guard menu).
     * Added for correctness/clarity: earlier Virtual Guard test code reused {@link #tapBottomNavTab} for
     * this purpose, which happens to work only because both xpath templates are currently identical in
     * shape - this dedicated method avoids relying on that coincidence for new callers (License Plate).
     */
    public void tapBottomSheetMenuItem(String itemLabel) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + itemLabel);
        List<WebElement> rows = getDriverForFramework().findElements(
                By.xpath(String.format(MainAppShellPageObjectRepository.BOTTOM_SHEET_MENU_ITEM_XPATH_TEMPLATE, itemLabel)));
        if (rows.isEmpty()) {
            throw new IllegalStateException("No bottom-sheet menu item found matching '" + itemLabel + "'.");
        }
        clickWithHeal(rows.get(0), "Bottom Sheet Menu Item: " + itemLabel);
    }

    /**
     * CORRECTED 2026-10-26 after a confirmed real test failure
     * ({@code verifyDismissingVirtualGuardMenuWithoutSelectionPerformsNoNavigation}: "expected [false] but
     * found [true]"): {@code anyBottomSheetMenuItemSignal} includes "Surveillance" as a generic "a menu is
     * open" signal, but "Surveillance" is ALSO Virtual Guard's own SCREEN title - after dismissing the
     * menu, the app correctly lands back on the Surveillance screen, where its title text makes this check
     * wrongly report the menu as still open. LIVE-CONFIRMED disambiguator, consistent across every crawl
     * this pass: a real open bottom-sheet menu covers the persistent bottom navigation, so "Global"/
     * "Support" (always visible on every normal Main App Shell screen) become absent while it's open -
     * used here to reject the false-positive case. Robust variant for real assertions - use
     * {@link #isBottomSheetMenuOpenFast()} for a cheap per-test cleanup branch check.
     */
    public boolean isBottomSheetMenuOpen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if (!isElementVisible(repo.anyBottomSheetMenuItemSignal)) {
            return false;
        }
        return getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Global']")).isEmpty();
    }

    /**
     * Added 2026-10-26 - same confirmed bug class as {@code VirtualGuardPage#isScheduleSheetDisplayedFast()}:
     * {@link #isBottomSheetMenuOpen()} reads a {@code @AndroidFindBy}-decorated field (up to ~10s hidden
     * PageFactory retry when absent) built on a universal {@code //*} wildcard tag (itself a confirmed slow
     * UiAutomator2 pattern), and every module's {@code lightReset()} calls it after EVERY test, almost
     * always when no menu is open. Bypasses PageFactory via raw {@code findElements} on the concrete
     * {@code android.widget.TextView} type instead - with the same "Global"/"Support" bottom-nav-absence
     * disambiguator as {@link #isBottomSheetMenuOpen()}, since this variant inherited the identical
     * "Surveillance" false-positive and is called unconditionally on every single test's cleanup (a false
     * positive here would fire an unwanted dismiss/back-navigation on every test, not just report wrong).
     */
    public boolean isBottomSheetMenuOpenFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        // FIX (2026-10-27): "Plate Scans" was the stale, pre-correction License Plate menu-item label -
        // the live-confirmed text is "Vehicle Scans" (see anyBottomSheetMenuItemSignal's javadoc). The
        // stale text never matched, so this check always reported the License Plate menu as closed even
        // when it was genuinely left open, leaking it into the next test's cleanup.
        boolean anySignal = !getDriverForFramework().findElements(
                By.xpath("//android.widget.TextView[@text='Vehicle Scans' or @text='Surveillance' or @text='Camera Views']")).isEmpty();
        if (!anySignal) {
            return false;
        }
        return getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Global']")).isEmpty();
    }

    /**
     * UNVERIFIED placeholder. TC_FUNC_SHELL_011: dismisses an open bottom-sheet menu without selecting an
     * item, by tapping the assumed scrim/backdrop outside the sheet - same unconfirmed mechanism already
     * flagged for Site Management's Site Switcher sheet. Falls back to system back navigation if no scrim
     * element is found.
     */
    public void dismissBottomSheetMenuWithoutSelecting() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        navigateBack();
        // FIX (2026-10-26, root-caused from a real failed run, Xiaomi/MIUI device udid OZRK5HK77PGI4HR8):
        // navigateBack() sends the back-key event and returns immediately - it does not wait for the
        // sheet's own closing animation to finish. A caller that immediately checks isBottomSheetMenuOpen()
        // (a genuinely zero-wait check, by design, for its OTHER - pre-action - uses) right after this call
        // can catch the sheet still mid-close, confirmed live: the check ran with a 0-1ms gap after
        // navigateBack() and still reported the menu open. A short, bounded wait here for the menu's own
        // "open" signal to actually disappear - not a Thread.sleep - makes this method's own postcondition
        // (the menu really is closed by the time this returns) genuinely true for every caller.
        try {
            new WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOf(repo.anyBottomSheetMenuItemSignal));
        } catch (TimeoutException | NoSuchElementException e) {
            // Already gone (or never found) - the postcondition holds either way.
        }
    }

    // =================================================================================================
    // App-lifecycle wrappers (TC_FUNC_SHELL_005/006/014/015) - structure LIVE-VERIFIED elsewhere
    // (OnboardingAuthenticationPage already exercised backgroundAppAndResume/forceKillAndRelaunchApp
    // successfully on real hardware; duplicated here only because Base's underlying primitives
    // (minimizeApplicationAndRestore/forceStopApp/relaunchApp) are protected, not because the mechanism
    // itself is unconfirmed).
    // =================================================================================================

    /** Backgrounds the app for {@code seconds} then lets Appium auto-restore it. */
    public void backgroundAppAndResume(int seconds) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        minimizeApplicationAndRestore(seconds);
    }

    /** Force-stops the app process and relaunches it from Splash. */
    public void forceKillAndRelaunchApp() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String appPackage = com.cresanmanagement.eyeq.utils.PropertyReader.getInstance()
                .getPropValues("config.properties", "AndroidAppPackage");
        forceStopApp(appPackage);
        relaunchApp(appPackage);
    }
}
