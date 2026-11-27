package com.codecraft.wdiodemoapp.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.remote.SupportsContextSwitching;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class Base extends DriverFactory {

    // Deliberately an instance field re-read via the constructor on every `new XPage()` call
    // (each Page object is freshly constructed per Pages.XPage() call) rather than a static
    // field initialized once — a static field here would freeze onto whichever Appium session
    // was live the first time this class loaded in the JVM, going stale the moment that first
    // per-test session is quit (real bug found and fixed during live-app test execution: caused
    // NoSuchSessionException on any Page method that calls `driver.findElement(...)` directly).
    public AppiumDriver driver;

    public Base() {
        this.driver = getDriverForFramework();
    }

    public long waitTime = 20;
    public static final Logger logger = LogManager.getLogger(Base.class.getName());

    public boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void waitUntilElementIsVisible(WebElement element) {
        new WebDriverWait(driver, Duration.ofSeconds(waitTime))
                .until(ExpectedConditions.visibilityOf(element));
    }

    public void waitUntilElementIsClickable(WebElement element) {
        new WebDriverWait(driver, Duration.ofSeconds(waitTime))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Self-healing click. Per {@code 03-standards/playwright-java-standards.md} "Self-Healing
     * Locators" (ported from the Playwright-Java {@code PlaywrightLocator}/{@code SelfHealer}
     * pair for this stack's Appium/{@code @AndroidFindBy} page-object idiom, which has no
     * equivalent single wrapper class to bake healing into implicitly — see
     * {@code Utils/SelfHealer.java} javadoc for the full rationale): waits and clicks normally;
     * on failure, hands {@code hint} (a human-readable name, e.g. "Login Button") to
     * {@link SelfHealer#findHealedLocator} and retries against the healed element. Rethrows the
     * original exception unchanged if healing fails, so a genuine failure is never silently lost.
     */
    public void clickWithHeal(WebElement element, String hint) {
        try {
            waitUntilElementIsClickable(element);
            element.click();
        } catch (RuntimeException originalError) {
            healOrRethrow(hint, originalError).click();
        }
    }

    /**
     * Self-healing type-into-field — see {@link #clickWithHeal(WebElement, String)}. Live-run
     * finding #1: iOS XCUIElementTypeSecureTextField silently drops {@code sendKeys(...)} if the
     * field wasn't explicitly tapped first — {@code sendKeys} alone does not reliably focus it
     * the way it does on Android (confirmed live: password field stayed empty in a screenshot
     * while the preceding plain email field, typed the same way, worked fine). An explicit
     * {@code click()} before typing costs nothing on Android and fixes iOS.
     */
    public void typeWithHeal(WebElement element, String hint, String text) {
        try {
            waitUntilElementIsVisible(element);
            element.click();
            element.clear();
            typeText(element, text);
        } catch (RuntimeException originalError) {
            WebElement healed = healOrRethrow(hint, originalError);
            healed.click();
            healed.clear();
            typeText(healed, text);
        }
    }

    /**
     * Live-run finding #2: a single batched {@code sendKeys(text)} silently drops characters that
     * require an iOS keyboard page switch (confirmed live: {@code "!@#$%^&*()"} arrived as
     * {@code "!#$%^&*()"} — the "@" specifically, which lives on iOS's secondary symbols page —
     * while sending the exact same characters one at a time with a brief pause between each
     * preserved all of them). Android's keyboard has no equivalent issue, so this only slows down
     * iOS, where it's necessary for correctness rather than a defensive-but-needless workaround.
     */
    private void typeText(WebElement element, String text) {
        if (!isIOS()) {
            element.sendKeys(text);
            return;
        }
        for (int i = 0; i < text.length(); i++) {
            element.sendKeys(String.valueOf(text.charAt(i)));
            sleep(120);
        }
    }

    private WebElement healOrRethrow(String hint, RuntimeException originalError) {
        logger.warn("Locator [{}] failed ({}); attempting self-heal", hint, originalError.getMessage());
        SelfHealer.HealResult healed = SelfHealer.findHealedLocator(driver, hint, originalError);
        if (!healed.success()) {
            throw originalError;
        }
        logger.info("Self-healed [{}] with confidence {}", hint, healed.confidence());
        return healed.element();
    }

    /**
     * Live-run finding: on iOS, calling {@code hideKeyboard()} against this app throws
     * "Did not know how to dismiss the keyboard" internally in WebDriverAgent — and even though
     * the exception itself is caught here, something about the attempt corrupts the next field
     * interaction (confirmed live via a raw session replay: typing email -> hideKeyboard() ->
     * typing password left the password field completely empty; the identical sequence with the
     * hideKeyboard() call removed types both fields correctly). Dismissing the keyboard isn't
     * actually required for this app's flows — the next field/button is still reachable with the
     * keyboard open — so this is a no-op on iOS rather than a workaround for a real necessity.
     */
    public void hideKeyboard() {
        if (isIOS()) {
            return;
        }
        try {
            ((HidesKeyboard) driver).hideKeyboard();
        } catch (Exception e) {
            logger.debug("Keyboard already hidden or not present");
        }
    }

    /** True when the active session's platformName is Android (case-insensitive). */
    public boolean isAndroid() {
        Object platform = driver.getCapabilities().getCapability("platformName");
        return platform != null && platform.toString().equalsIgnoreCase("Android");
    }

    /** True when the active session's platformName is iOS (case-insensitive). */
    public boolean isIOS() {
        Object platform = driver.getCapabilities().getCapability("platformName");
        return platform != null && platform.toString().equalsIgnoreCase("iOS");
    }

    /**
     * Cross-platform on/off read for a native switch/toggle element. Android UiAutomator2
     * exposes {@code checked="true"/"false"}; iOS XCUITest exposes {@code value="1"/"0"} instead
     * (confirmed live — XCUIElementTypeSwitch has no "checked" attribute at all).
     */
    public boolean isSwitchOn(WebElement element) {
        if (isIOS()) {
            return "1".equals(element.getAttribute("value"));
        }
        return "true".equalsIgnoreCase(element.getAttribute("checked"));
    }

    public void navigateBack() {
        driver.navigate().back();
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Performs a single-finger swipe from (startX,startY) to (endX,endY). */
    public void swipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(150)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    /** Drags fromElement's center onto toElement's center. */
    public void dragAndDrop(WebElement fromElement, WebElement toElement) {
        Point from = centerOf(fromElement);
        Point to = centerOf(toElement);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence drag = new Sequence(finger, 0);
        drag.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.getX(), from.getY()));
        drag.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        drag.addAction(new Pause(finger, Duration.ofMillis(300)));
        drag.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(),
                from.getX() + (to.getX() - from.getX()) / 2, from.getY() + (to.getY() - from.getY()) / 2));
        drag.addAction(finger.createPointerMove(Duration.ofMillis(400), PointerInput.Origin.viewport(), to.getX(), to.getY()));
        drag.addAction(new Pause(finger, Duration.ofMillis(300)));
        drag.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(drag));
    }

    private Point centerOf(WebElement element) {
        Point location = element.getLocation();
        org.openqa.selenium.Dimension size = element.getSize();
        return new Point(location.getX() + size.getWidth() / 2, location.getY() + size.getHeight() / 2);
    }

    public List<String> getContexts() {
        return ((SupportsContextSwitching) driver).getContextHandles().stream().toList();
    }

    public void switchToContext(String contextName) {
        ((SupportsContextSwitching) driver).context(contextName);
    }

    public String generateRandomEmail() {
        return "cc" + System.currentTimeMillis() + "@mailinator.com";
    }
}
