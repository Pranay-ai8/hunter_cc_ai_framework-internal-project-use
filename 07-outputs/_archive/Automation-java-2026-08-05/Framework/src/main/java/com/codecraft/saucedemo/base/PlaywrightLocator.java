package com.codecraft.saucedemo.base;

import com.codecraft.saucedemo.utils.SelfHealer;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default {@link ILocator} implementation wrapping {@code com.microsoft.playwright.Locator}.
 *
 * <p>Per 03-standards/playwright-java-standards.md "Self-Healing Locators": the ACTION methods
 * ({@code click()}, {@code fill()}, {@code selectOption()}, {@code check()}) attempt a short
 * (~3s) wait-then-act, and on {@link PlaywrightException} fall back to
 * {@link SelfHealer#findHealedLocator} using {@code name} as the healing hint before retrying
 * against the healed locator. If healing fails (no candidate above the confidence threshold),
 * the original exception is rethrown unchanged. READ methods ({@code textContent()},
 * {@code isVisible()}, {@code isEnabled()}) deliberately do NOT gain this wrapping — silently
 * healing an assertion's target risks turning a real failure into a false pass.</p>
 */
public class PlaywrightLocator implements ILocator {

    private static final Logger log = LogManager.getLogger(PlaywrightLocator.class);
    private static final int HEAL_WAIT_TIMEOUT_MS = 3000;

    private final String name;
    private final Locator locator;

    public PlaywrightLocator(String name, Locator locator) {
        this.name = name;
        this.locator = locator;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void click() {
        try {
            waitAttached();
            log.info("Clicking [{}]", name);
            locator.click();
        } catch (PlaywrightException originalError) {
            Locator healed = healOrRethrow(originalError);
            log.info("Retrying click on healed locator for [{}]", name);
            healed.click();
        }
    }

    @Override
    public void fill(String text) {
        try {
            waitAttached();
            log.info("Filling [{}] with '{}'", name, text);
            locator.fill(text);
        } catch (PlaywrightException originalError) {
            Locator healed = healOrRethrow(originalError);
            log.info("Retrying fill on healed locator for [{}]", name);
            healed.fill(text);
        }
    }

    @Override
    public void selectOption(String label) {
        try {
            waitAttached();
            log.info("Selecting option '{}' on [{}]", label, name);
            locator.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(label));
        } catch (PlaywrightException originalError) {
            Locator healed = healOrRethrow(originalError);
            log.info("Retrying selectOption on healed locator for [{}]", name);
            healed.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(label));
        }
    }

    @Override
    public String textContent() {
        return locator.textContent();
    }

    @Override
    public boolean isVisible() {
        return locator.isVisible();
    }

    @Override
    public boolean isEnabled() {
        return locator.isEnabled();
    }

    @Override
    public void check() {
        try {
            waitAttached();
            log.info("Checking [{}]", name);
            locator.check();
        } catch (PlaywrightException originalError) {
            Locator healed = healOrRethrow(originalError);
            log.info("Retrying check on healed locator for [{}]", name);
            healed.check();
        }
    }

    @Override
    public com.microsoft.playwright.Locator raw() {
        return locator;
    }

    private void waitAttached() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.ATTACHED)
                .setTimeout(HEAL_WAIT_TIMEOUT_MS));
    }

    /**
     * Attempts to heal {@code originalError} via {@link SelfHealer}. Returns the healed
     * {@link Locator} on success; rethrows {@code originalError} unchanged otherwise.
     */
    private Locator healOrRethrow(PlaywrightException originalError) {
        log.warn("Locator [{}] failed ({}); attempting self-heal", name, originalError.getMessage());
        SelfHealer.HealResult healed = SelfHealer.findHealedLocator(locator.page(), name, originalError);
        if (!healed.success()) {
            throw originalError;
        }
        log.info("Self-healed [{}] with confidence {}", name, healed.confidence());
        return healed.locator();
    }
}
