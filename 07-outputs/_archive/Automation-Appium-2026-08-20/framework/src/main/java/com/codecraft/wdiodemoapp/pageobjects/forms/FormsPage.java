package com.codecraft.wdiodemoapp.pageobjects.forms;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FormsPage extends Base {

    private final FormsPageObjectRepository repo;

    public FormsPage() {
        repo = new FormsPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isFormsScreenDisplayed() {
        waitUntilElementIsVisible(repo.formsScreen);
        return isElementVisible(repo.formsScreen);
    }

    public void typeText(String text) {
        typeWithHeal(repo.textInput, "Text Input", text);
        hideKeyboard();
    }

    public String getTypedResult() {
        waitUntilElementIsVisible(repo.textInputResult);
        return repo.textInputResult.getText();
    }

    public boolean isSwitchOn() {
        return isSwitchOn(repo.switchControl);
    }

    public void toggleSwitch() {
        clickWithHeal(repo.switchControl, "Switch Toggle");
    }

    public String getSwitchText() {
        return repo.switchText.getText();
    }

    public void openDropdown() {
        // Live-run finding: on iOS, tapping the "Dropdown" container itself is a silent no-op
        // (confirmed with both element.click() and a raw W3C coordinate tap) — the chevron icon
        // must be tapped specifically. See FormsPageObjectRepository.dropdownChevronIOS.
        if (isIOS()) {
            clickWithHeal(repo.dropdownChevronIOS, "Dropdown Chevron");
        } else {
            clickWithHeal(repo.dropdown, "Dropdown");
        }
    }

    /**
     * Live-run finding: Android renders this dropdown as a tappable AlertDialog-style option
     * list (find the option by its visible text, click it). iOS renders the SAME dropdown as a
     * native spinning {@code XCUIElementTypePickerWheel} instead — there is no per-option element
     * to click at all; the platform-correct interaction is to set the wheel's value directly via
     * {@code sendKeys} (Appium's iOS driver spins the wheel to match) and confirm with "Done".
     */
    public void selectDropdownOption(String optionText) {
        openDropdown();
        if (isIOS()) {
            WebElement pickerWheel = driver.findElement(By.className("XCUIElementTypePickerWheel"));
            pickerWheel.sendKeys(optionText);
            WebElement done = driver.findElement(io.appium.java_client.AppiumBy.accessibilityId("Done"));
            waitUntilElementIsClickable(done);
            done.click();
        } else {
            // Android's native picker list exposes option text via @text.
            WebElement option = driver.findElement(By.xpath("//*[@text='" + optionText + "']"));
            waitUntilElementIsClickable(option);
            option.click();
        }
    }

    public String getDropdownValue() {
        return repo.dropdownValue.getText();
    }

    /**
     * Live-run finding: tapping "Active" surfaces a native confirmation dialog
     * ("This button is" / "This button is active" — ASK ME LATER / CANCEL / OK);
     * tapping "Inactive" does not. Dismiss defensively either way so callers never
     * have to know which button does this.
     */
    public void tapActiveButton() {
        clickWithHeal(repo.buttonActive, "Active Button");
        dismissButtonStateDialogIfPresent();
    }

    public void tapInactiveButton() {
        clickWithHeal(repo.buttonInactive, "Inactive Button");
        dismissButtonStateDialogIfPresent();
    }

    /**
     * Live-run finding: on iOS, {@code appium:autoAcceptAlerts} (needed for real system
     * permission alerts — see PermissionsPage) also auto-taps THIS app-level dialog's default
     * button before this method runs, sometimes leaving a stale reference between the presence
     * check and the click. Treat both "never appeared" and "vanished before we could click it" as
     * success rather than a failure — either way the dialog is gone and Active/Inactive can proceed.
     */
    private void dismissButtonStateDialogIfPresent() {
        // Android: app-themed AlertDialog, positive button is android:id/button1.
        // iOS: UIAlertController, button exposed by its accessibility name "OK" (confirmed live)
        // — no resource-id concept exists on iOS.
        By okLocator = isIOS() ? io.appium.java_client.AppiumBy.accessibilityId("OK") : By.id("android:id/button1");
        java.util.List<WebElement> okButton = driver.findElements(okLocator);
        if (okButton.isEmpty()) {
            return;
        }
        try {
            if (isElementVisible(okButton.get(0))) {
                okButton.get(0).click();
            }
        } catch (RuntimeException e) {
            logger.debug("Button-state dialog OK vanished before it could be clicked — already dismissed");
        }
    }
}
