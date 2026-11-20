package com.cresanmanagement.eyeq.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.connection.ConnectionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Common mobile utility/action methods shared by every Page Action class in NATIVE context.
 * Extends {@link DriverFactory} so {@code driver} is always the current thread's Appium session.
 *
 * Gesture/interaction methods route through Selenium's W3C {@link Actions}/{@link PointerInput} API -
 * Appium's legacy {@code TouchAction}/{@code MultiTouchAction} classes were removed in java-client 11.x
 * (see pom.xml's Selenium Version Pin comment), so this is both the modern and the only available path.
 */
public class Base extends DriverFactory {

    private static final Logger logger = LogManager.getLogger(Base.class.getName());
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(90);
    protected static final Random RANDOM = new Random();

    public static io.appium.java_client.AppiumDriver driver = getDriverForFramework();

    // ---------------------------------------------------------------------------------------------
    // Visibility / waits
    // ---------------------------------------------------------------------------------------------

    /**
     * Taps {@code dialogButton} only if it is actually present right now (bounded, short wait) -
     * for transient, not-always-reproducible native OS dialogs (e.g. an Android 13+ runtime
     * permission prompt) that may or may not appear depending on prior app/device state. Never
     * throws if the dialog never shows up - that is the expected, common case, not a failure.
     *
     * <p><b>WIDENED 2026-10-19</b> from an original 5s bound to 15s, after a live-confirmed false test
     * failure: the post-login notification-permission dialog's appearance timing was observed to vary
     * more than 5s allows for on the emulator - when it arrived late, this method had already given up
     * (correctly, by its own design - "never throws if the dialog never shows up"), but the dialog then
     * sat un-dismissed and blocked a subsequent "is Site Selection displayed" check for its own full 90s
     * wait, while the app itself appeared to keep running/navigating underneath it. 15s is a deliberate,
     * still-bounded middle ground: generous enough to absorb the observed timing variance without
     * meaningfully slowing down the (far more common) case where no dialog appears at all.
     *
     * <p><b>FIX (2026-10-22, root-caused from a real failed run - logs/automation1.log 2026-10-22
     * 09:23:48-09:24:01):</b> {@code shortWait.ignoring(StaleElementReferenceException.class)} only
     * covers the WAIT's own polling - the {@code dialogButton.click()} call right after it was
     * unprotected, so if the dialog's button went stale (DOM re-render, e.g. an animation or layout
     * shift) in the brief window between the wait succeeding and the click actually executing, the
     * {@code StaleElementReferenceException} propagated all the way up through this method, out of the
     * entire "login" call chain, and failed the caller's test - directly contradicting this method's own
     * documented contract ("Never throws if the dialog never shows up"). A stale element at click-time
     * means the transient dialog is no longer there in a clickable form either way, so it is now treated
     * the same as "never showed up": caught and swallowed, consistent with this method's existing
     * best-effort philosophy.
     */
    public void dismissSystemDialogIfPresent(WebElement dialogButton) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            WebDriverWait shortWait = new WebDriverWait(getDriverForFramework(), Duration.ofSeconds(15));
            shortWait.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
            shortWait.until(ExpectedConditions.visibilityOf(dialogButton));
            dialogButton.click();
            logger.info("Dismissed a system dialog via: " + dialogButton);
        } catch (org.openqa.selenium.TimeoutException | NoSuchElementException
                | org.openqa.selenium.StaleElementReferenceException e) {
            logger.debug("No system dialog appeared within the bounded wait (or it vanished before it "
                    + "could be clicked) - continuing normally.");
        }
    }

    public boolean isElementVisible(WebElement element) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * NOTE (added 2026-10-19 after a live-confirmed, recurring failure class): every {@code waitUntilXxx}
     * method below is configured to IGNORE {@link org.openqa.selenium.StaleElementReferenceException}
     * during its own internal polling loop (Selenium's idiomatic fix for this - a vanilla
     * {@code WebDriverWait} only ignores {@code NoSuchElementException} by default). Without this, a
     * transient Compose-recomposition race (confirmed live multiple times: right after an app relaunch,
     * and right after navigating to a new screen) causes the FIRST staleness hit to propagate immediately
     * as a hard failure instead of being retried within the wait's own timeout - this was root-caused via
     * a real incident where {@code EnvironmentSetup}'s own settle-check ({@code isLoginScreenDisplayed()})
     * kept failing this way even after an outer retry loop was added around it, because the staleness was
     * happening INSIDE a wait call that had no outer protection at all. Fixing it here, at the shared wait
     * primitive every Page Action method ultimately goes through, is more robust than adding a retry
     * wrapper around every individual caller.
     */
    public WebElement waitUntilElementIsVisible(WebElement element) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        WebDriverWait wait = new WebDriverWait(getDriverForFramework(), DEFAULT_WAIT);
        wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementToBeInvisible(WebElement element) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        WebDriverWait wait = new WebDriverWait(getDriverForFramework(), DEFAULT_WAIT);
        wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void waitUntilElementIsClickable(WebElement element) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        WebDriverWait wait = new WebDriverWait(getDriverForFramework(), DEFAULT_WAIT);
        wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitUntilElementHasText(WebElement element, String text) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        WebDriverWait wait = new WebDriverWait(getDriverForFramework(), DEFAULT_WAIT);
        wait.ignoring(org.openqa.selenium.StaleElementReferenceException.class);
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    // ---------------------------------------------------------------------------------------------
    // Self-healing action wrappers (see SelfHealer.java / 03-standards/appium-java-standards.md's
    // "Self-Healing Locators" section). Page Action classes call these instead of raw
    // element.click()/sendKeys() so a stale/renamed locator degrades to a logged, best-effort
    // recovery instead of an immediate hard failure.
    // ---------------------------------------------------------------------------------------------

    /**
     * How many extra attempts {@link #clickWithHeal}/{@link #typeWithHeal} make when the SAME locator
     * throws {@link org.openqa.selenium.StaleElementReferenceException}, before falling through to
     * {@link SelfHealer}. Added 2026-10-19 after live-confirming a recurring, transient class of failure
     * on this app's Compose UI: a screen transition's element can pass {@code waitUntilElementIsVisible}
     * and then become stale a moment later from a FOLLOW-UP recomposition pass (confirmed on the
     * Login-screen email field right after an app relaunch, and again on the Forgot Password screen's
     * email field right after navigating to it) - the locator itself is correct and the element really is
     * about to be visible/stable, it just needs a brief beat to settle. {@link SelfHealer} is designed for
     * locator DRIFT (a renamed/moved element needing a different xpath), not this - retrying the identical
     * locator resolves it far more often than searching for an alternative one would.
     */
    private static final int STALE_ELEMENT_RETRY_ATTEMPTS = 2;

    public void clickWithHeal(WebElement element, String hint) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + hint);
        RuntimeException lastStaleError = null;
        for (int attempt = 0; attempt <= STALE_ELEMENT_RETRY_ATTEMPTS; attempt++) {
            try {
                waitUntilElementIsClickable(element);
                element.click();
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException staleEx) {
                lastStaleError = staleEx;
                logger.warn("clickWithHeal('" + hint + "') hit a transient StaleElementReferenceException "
                        + "(attempt " + (attempt + 1) + "/" + (STALE_ELEMENT_RETRY_ATTEMPTS + 1) + ") - "
                        + "briefly settling and retrying the same locator before self-heal.", staleEx);
                briefRealTimeSettle();
            } catch (RuntimeException originalError) {
                logger.warn("Primary locator for '" + hint + "' failed, attempting self-heal", originalError);
                dumpPageSourceForDiagnostics(hint);
                healOrRethrow(hint, originalError).click();
                return;
            }
        }
        logger.warn("Primary locator for '" + hint + "' kept going stale after retries, attempting self-heal", lastStaleError);
        dumpPageSourceForDiagnostics(hint);
        healOrRethrow(hint, lastStaleError).click();
    }

    /**
     * DIAGNOSTIC (added 2026-10-26, TopBar Site Switcher Control root-cause investigation): dumps the raw
     * {@code driver.getPageSource()} XML to {@code logs/page-source-dumps/} at the EXACT moment a primary
     * locator fails and self-heal is about to run - i.e. the same live page source {@link SelfHealer} itself
     * is about to scan. SelfHealer does not persist the raw source it scores candidates against, so when it
     * logs "no candidate scored >= 0.75 (best=0.0)" there was previously no way to inspect what the screen
     * actually looked like at that moment without reproducing live and timing an external uiautomator dump
     * by hand. This makes that evidence automatic and exact every time any clickWithHeal/typeWithHeal call
     * falls through to self-heal, for every hint - not just the TopBar control - at negligible cost (one
     * extra getPageSource() call after a wait that has already run up to 90 seconds).
     *
     * <p>Never allowed to affect control flow: any failure here (disk I/O, a transient driver error on an
     * already-unhappy session, etc.) is swallowed and logged, never rethrown.
     */
    private void dumpPageSourceForDiagnostics(String hint) {
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get("logs", "page-source-dumps");
            java.nio.file.Files.createDirectories(dir);
            String safeHint = hint.replaceAll("[^a-zA-Z0-9]+", "_");
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));
            java.nio.file.Path file = dir.resolve(safeHint + "__" + timestamp + ".xml");
            String pageSource = getDriverForFramework().getPageSource();
            java.nio.file.Files.writeString(file, pageSource);
            logger.warn("Diagnostic page-source dump for failed hint '" + hint + "' written to " + file);
        } catch (Exception dumpError) {
            logger.warn("Could not write diagnostic page-source dump for hint '" + hint + "'", dumpError);
        }
    }

    public void typeWithHeal(WebElement element, String hint, String text) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + hint);
        RuntimeException lastStaleError = null;
        for (int attempt = 0; attempt <= STALE_ELEMENT_RETRY_ATTEMPTS; attempt++) {
            try {
                waitUntilElementIsVisible(element);
                element.clear();
                element.sendKeys(text);
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException staleEx) {
                lastStaleError = staleEx;
                logger.warn("typeWithHeal('" + hint + "') hit a transient StaleElementReferenceException "
                        + "(attempt " + (attempt + 1) + "/" + (STALE_ELEMENT_RETRY_ATTEMPTS + 1) + ") - "
                        + "briefly settling and retrying the same locator before self-heal.", staleEx);
                briefRealTimeSettle();
            } catch (RuntimeException originalError) {
                logger.warn("Primary locator for '" + hint + "' failed, attempting self-heal", originalError);
                dumpPageSourceForDiagnostics(hint);
                WebElement healed = healOrRethrow(hint, originalError);
                healed.clear();
                healed.sendKeys(text);
                return;
            }
        }
        logger.warn("Primary locator for '" + hint + "' kept going stale after retries, attempting self-heal", lastStaleError);
        dumpPageSourceForDiagnostics(hint);
        WebElement healed = healOrRethrow(hint, lastStaleError);
        healed.clear();
        healed.sendKeys(text);
    }

    /**
     * A short, bounded, non-blind real-time pause (NOT {@code Thread.sleep()} - a {@link WebDriverWait}
     * against a simple elapsed-time condition, consistent with this project's standing "no
     * Thread.sleep()" rule) used only to let a just-observed Compose recomposition settle before retrying
     * an identical locator - see {@link #STALE_ELEMENT_RETRY_ATTEMPTS}'s javadoc.
     */
    private void briefRealTimeSettle() {
        long start = System.currentTimeMillis();
        new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(3))
                .until(d -> System.currentTimeMillis() - start >= 800L);
    }

    /**
     * A short, bounded, non-blind real-time pause (NOT {@code Thread.sleep()} - same
     * {@link WebDriverWait}-against-elapsed-time pattern as {@link #briefRealTimeSettle()}), for callers
     * that type into a client-side-filtered search field and then immediately query the result list.
     *
     * <p><b>Root-caused live (2026-10-20):</b> this project's Site Selection/Vehicle Scans/Vehicles/
     * Tickets search fields are confirmed to apply their filter after a real debounce delay (measured live
     * at roughly 1-1.5 seconds) rather than instantly on each keystroke - a search-then-immediately-assert
     * call pattern (type text, then {@code findElements} in the very next line) can read the STALE,
     * pre-filter result set and wrongly conclude a real, matching item "doesn't exist". This was the exact,
     * confirmed root cause of an earlier false conclusion that site "Main Site" was not a real site for a
     * multi-site QA account (it is real and selectable - confirmed by retrying the same search WITH this
     * settle delay). Call this after every {@code typeWithHeal}/{@code sendKeys} into a search field,
     * before reading the filtered result.
     */
    protected void settleAfterSearchDebounce() {
        long start = System.currentTimeMillis();
        new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(5))
                .until(d -> System.currentTimeMillis() - start >= 1500L);
    }

    private WebElement healOrRethrow(String hint, RuntimeException originalError) {
        SelfHealer.HealResult result = SelfHealer.findHealedLocator(getDriverForFramework(), hint, originalError);
        if (result.success()) {
            return result.element();
        }
        throw originalError;
    }

    // ---------------------------------------------------------------------------------------------
    // Platform helpers
    // ---------------------------------------------------------------------------------------------

    public boolean isAndroid() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String runOn = PropertyReader.getInstance().getPropValues("config.properties", "RUN_ON");
        return runOn != null && runOn.toUpperCase().contains("ANDROID_APP");
    }

    public void hideKeyboard() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            if (getDriverForFramework() instanceof io.appium.java_client.HidesKeyboard) {
                ((io.appium.java_client.HidesKeyboard) getDriverForFramework()).hideKeyboard();
            } else {
                // iOS fallback convention (not exercised in this Android-only project):
                // tap the "Done" accessibility button if present.
                getDriverForFramework().findElement(By.xpath("//XCUIElementTypeButton[@name='Done']")).click();
            }
        } catch (Exception e) {
            logger.warn("hideKeyboard() could not hide the keyboard (it may already be hidden)", e);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Gestures (W3C Actions / PointerInput - see class-level note on TouchAction removal)
    // ---------------------------------------------------------------------------------------------

    public void tapByCoordinates(int x, int y) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: (" + x + "," + y + ")");
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 0);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new org.openqa.selenium.interactions.Pause(finger, Duration.ofMillis(100)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        getDriverForFramework().perform(Collections.singletonList(tap));
    }

    public void swipeFromElementToElement(WebElement from, WebElement to) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        org.openqa.selenium.Point source = from.getLocation();
        org.openqa.selenium.Dimension sourceSize = from.getSize();
        org.openqa.selenium.Point target = to.getLocation();
        org.openqa.selenium.Dimension targetSize = to.getSize();

        int startX = source.getX() + sourceSize.getWidth() / 2;
        int startY = source.getY() + sourceSize.getHeight() / 2;
        int endX = target.getX() + targetSize.getWidth() / 2;
        int endY = target.getY() + targetSize.getHeight() / 2;

        swipe(startX, startY, endX, endY);
    }

    /**
     * Directional scroll/swipe within the visible screen bounds - the primary gesture needed for
     * EyeQ Monitoring's scrollable list screens (Plate Scans feed, Vehicles registry, Tickets list).
     */
    public void scroll(Direction direction) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + direction);
        org.openqa.selenium.Dimension size = getDriverForFramework().manage().window().getSize();
        int centerX = size.getWidth() / 2;
        int startY;
        int endY;
        switch (direction) {
            case UP:
                startY = (int) (size.getHeight() * 0.3);
                endY = (int) (size.getHeight() * 0.7);
                break;
            case DOWN:
                startY = (int) (size.getHeight() * 0.7);
                endY = (int) (size.getHeight() * 0.3);
                break;
            default:
                throw new UnsupportedOperationException("scroll(Direction) only supports UP/DOWN - use swipeFromElementToElement for LEFT/RIGHT carousels");
        }
        swipe(centerX, startY, centerX, endY);
    }

    private void swipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        getDriverForFramework().perform(Collections.singletonList(swipe));
    }

    // ---------------------------------------------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------------------------------------------

    public void navigateBack() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        getDriverForFramework().navigate().back();
    }

    public void navigateRefresh() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        getDriverForFramework().navigate().refresh();
    }

    // ---------------------------------------------------------------------------------------------
    // App lifecycle
    // ---------------------------------------------------------------------------------------------

    public void minimizeApplicationAndRestore(int seconds) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        ((io.appium.java_client.InteractsWithApps) getDriverForFramework()).runAppInBackground(Duration.ofSeconds(seconds));
    }

    /** Force-stops the given app package (e.g. for a "force-killed mid-submission" test scenario). */
    public void forceStopApp(String appPackage) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + appPackage);
        ((io.appium.java_client.InteractsWithApps) getDriverForFramework()).terminateApp(appPackage);
    }

    /** Relaunches a previously force-stopped app package from its initial (Splash) state. */
    public void relaunchApp(String appPackage) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + appPackage);
        ((io.appium.java_client.InteractsWithApps) getDriverForFramework()).activateApp(appPackage);
    }

    /**
     * Clears the given app's local data (SharedPreferences, databases, cache) via UiAutomator2's
     * "mobile: clearApp" extension - the Appium-native equivalent of {@code adb shell pm clear}. Does
     * NOT relaunch the app afterward - call {@link #relaunchApp(String)} if the test needs to observe
     * the resulting (logged-out) state.
     *
     * CAUTION: this also wipes the devmode/environment-switch setting EnvironmentSetup established at
     * suite start (see utils/EnvironmentSetup.java) - any test using this method must restore it
     * afterward (re-run the environment switch) before yielding control back to the suite, or every
     * later test in the same run will silently start hitting the wrong backend.
     */
    @SuppressWarnings("unchecked")
    public void clearAppData(String appPackage) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + appPackage);
        java.util.Map<String, Object> args = new java.util.HashMap<>();
        args.put("appId", appPackage);
        ((org.openqa.selenium.JavascriptExecutor) getDriverForFramework()).executeScript("mobile: clearApp", args);
    }

    /**
     * Toggles airplane mode on/off via the same {@link io.appium.java_client.android.connection.ConnectionState}
     * bitmask mechanism as {@link #turnOnWifi()}/{@link #turnOffWifi()}, for "no network connectivity"
     * test scenarios.
     */
    public void toggleAirplaneMode(boolean enabled) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + enabled);
        AndroidDriver androidDriver = (AndroidDriver) getDriverForFramework();
        long currentMask = androidDriver.getConnection().getBitMask();
        long newMask = enabled
                ? (currentMask | ConnectionState.AIRPLANE_MODE_MASK)
                : (currentMask & ~ConnectionState.AIRPLANE_MODE_MASK);
        androidDriver.setConnection(new ConnectionState(newMask));
    }

    /**
     * Returns the current foreground app's package name - used to confirm that a tap-to-act control
     * (e.g. a phone number or email address) actually handed off to an external app (dialer, email
     * client) rather than silently no-op'ing, without needing to assert anything about that external
     * app's own UI.
     */
    public String getCurrentPackageName() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return ((AndroidDriver) getDriverForFramework()).getCurrentPackage();
    }

    /**
     * Deterministically returns focus to {@code ownAppPackage} after a tap handed off to an external
     * app (dialer, email client, etc.) via an OS intent. LIVE-CONFIRMED (Support module crawl,
     * 2026-10-20) that a single {@code navigate().back()} is NOT reliably sufficient to leave such an
     * external app - on this project's real device, one back press only closed the Dialer app's own
     * internal state (it stayed foregrounded), and a second back press was needed to actually return.
     * Rather than guess a press count (which may vary by external app/OS version), this activates our
     * own app package directly, which is deterministic regardless of how many screens the other app
     * pushed onto its own back stack.
     */
    public void bringOwnAppToForeground(String ownAppPackage) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + ownAppPackage);
        ((io.appium.java_client.InteractsWithApps) getDriverForFramework()).activateApp(ownAppPackage);
    }

    /** Reads the device's current clipboard text content (UiAutomator2's clipboard-read extension). */
    public String getClipboardText() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return ((io.appium.java_client.android.HasAndroidClipboard) getDriverForFramework()).getClipboardText();
    }

    /**
     * Sets the device's clipboard text content (UiAutomator2's clipboard-write extension) - for TC_FUNC_AUTH_050's
     * "paste a copied code into a field" scenario. NOT yet live-verified as of 2026-10-22 (written while the
     * device was occupied by a long-running regression suite) - pair with {@link #pasteIntoFocusedField()}
     * and confirm live before trusting this for anything beyond this one documented use.
     */
    public void setClipboardText(String text) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + text.length() + " chars");
        ((io.appium.java_client.android.HasAndroidClipboard) getDriverForFramework()).setClipboardText(text);
    }

    /**
     * Pastes the current clipboard content into whichever field currently has focus, via Android's
     * standard PASTE key event (no on-screen context-menu lookup needed). NOT yet live-verified as of
     * 2026-10-22 - the target field must already be focused (e.g. via a prior tap) before calling this.
     */
    public void pasteIntoFocusedField() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        ((io.appium.java_client.android.AndroidDriver) getDriverForFramework())
                .pressKey(new io.appium.java_client.android.nativekey.KeyEvent(io.appium.java_client.android.nativekey.AndroidKey.PASTE));
    }

    public byte[] takeScreenshot() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return ((TakesScreenshot) getDriverForFramework()).getScreenshotAs(OutputType.BYTES);
    }

    public File takeScreenshotAsFile() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return ((TakesScreenshot) getDriverForFramework()).getScreenshotAs(OutputType.FILE);
    }

    // ---------------------------------------------------------------------------------------------
    // Misc utility
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the adb executable path for Windows/Linux (relies on ANDROID_HOME/ANDROID_SDK_ROOT being
     * set); returns null on Mac, mirroring 02-templates/AppiumFramework.md's documented contract
     * (AppiumServer.java resolves the Mac case separately via Carthage/Node tooling).
     */
    public String checkOS() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String os = System.getProperty("os.name").toLowerCase();
        String sdkHome = System.getenv("ANDROID_HOME") != null ? System.getenv("ANDROID_HOME") : System.getenv("ANDROID_SDK_ROOT");
        if (os.contains("win")) {
            return sdkHome + File.separator + "platform-tools" + File.separator + "adb.exe";
        } else if (os.contains("nux") || os.contains("nix")) {
            return sdkHome + File.separator + "platform-tools" + File.separator + "adb";
        }
        return null;
    }

    public String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String generateAlphaNumericString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public String generateRandomMobileNumber() {
        StringBuilder sb = new StringBuilder("9");
        for (int i = 0; i < 9; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public String generateRandomEmail() {
        return "qa_" + generateAlphaNumericString(8).toLowerCase() + "@eyeq-test.com";
    }

    public int generateRandomNumber() {
        return RANDOM.nextInt(1_000_000);
    }

    public String[] executeCommand(String[] command) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + Arrays.toString(command));
        List<String> output = new java.util.ArrayList<>();
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            logger.error("executeCommand failed: " + Arrays.toString(command), e);
        }
        return output.toArray(new String[0]);
    }

    public void turnOffWifi() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        setWifi(false);
    }

    public void turnOnWifi() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        setWifi(true);
    }

    private void setWifi(boolean enabled) {
        AndroidDriver androidDriver = (AndroidDriver) getDriverForFramework();
        long currentMask = androidDriver.getConnection().getBitMask();
        long newMask = enabled
                ? (currentMask | ConnectionState.WIFI_MASK)
                : (currentMask & ~ConnectionState.WIFI_MASK);
        androidDriver.setConnection(new ConnectionState(newMask));
    }
}
