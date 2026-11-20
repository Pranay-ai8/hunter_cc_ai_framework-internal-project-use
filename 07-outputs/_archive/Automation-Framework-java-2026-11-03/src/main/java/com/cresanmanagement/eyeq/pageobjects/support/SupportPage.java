package com.cresanmanagement.eyeq.pageobjects.support;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.time.Duration;

/**
 * Page Action class for the Support module (static contact info - phone/email, tap-to-act,
 * copy-to-clipboard).
 *
 * <p><b>LIVE-VERIFIED 2026-10-20</b> (real device {@code 8BQX1DY81}, confirmed Test backend, site
 * "Amalthea"). Tapping the phone number genuinely invokes the device Dialer (package
 * {@code com.google.android.dialer}) with the number pre-filled on the dialpad; tapping the email
 * genuinely invokes the device's email client (observed: Gmail, package {@code com.google.android.gm})
 * via a mailto: intent. Both confirmed via {@link Base#getCurrentPackageName()} foreground-package
 * checks, matching this project's established intent-verification approach (see
 * {@code OnboardingAuthenticationPage#getCurrentPackageName} javadoc for the same pattern applied to
 * the Login-footer Contact Support overlay).
 */
public class SupportPage extends Base {

    private static final Logger logger = LogManager.getLogger(SupportPage.class.getName());
    private final SupportPageObjectRepository repo;

    public SupportPage() {
        repo = new SupportPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isSupportScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.supportScreenTitle) && isElementVisible(repo.contactSupportHeader);
    }

    public String getSupportPhoneNumber() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.supportPhoneNumber.getText();
    }

    public String getSupportEmail() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.supportEmail.getText();
    }

    /**
     * TC_FUNC_SUP_001: taps the phone number and returns the foreground app package observed
     * afterward, so the caller can assert a real hand-off to an external dialer occurred.
     */
    public String tapPhoneNumberAndGetResultingPackage() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String ownPackage = getCurrentPackageName();
        clickWithHeal(repo.supportPhoneNumber, "Support Phone Number");
        waitForForegroundPackageChange(ownPackage);
        return getCurrentPackageName();
    }

    /**
     * TC_FUNC_SUP_002: taps the email address and returns the foreground app package observed
     * afterward, so the caller can assert a real hand-off to an external email client occurred.
     */
    public String tapEmailAndGetResultingPackage() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String ownPackage = getCurrentPackageName();
        clickWithHeal(repo.supportEmail, "Support Email");
        waitForForegroundPackageChange(ownPackage);
        return getCurrentPackageName();
    }

    /**
     * Deterministically returns from whatever external app a phone/email tap handed off to, back to
     * this app's own Support screen. LIVE-CONFIRMED a single {@code navigate().back()} is not reliably
     * sufficient (one back press only closed the Dialer app's own internal state on the real device
     * used for this crawl) - see {@link Base#bringOwnAppToForeground(String)} javadoc for the full
     * finding. Waits for both our own package to regain foreground and the Support screen itself to
     * be visible again before returning, rather than the earlier throwaway crawl script's bug of
     * calling findElement() immediately with no wait at all.
     */
    public void returnToSupportScreen(String ownAppPackage) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + ownAppPackage);
        if (!ownAppPackage.equals(getCurrentPackageName())) {
            bringOwnAppToForeground(ownAppPackage);
        }
        new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(15))
                .until(d -> ownAppPackage.equals(getCurrentPackageName()));
        waitUntilElementIsVisible(repo.supportScreenTitle);
    }

    private void waitForForegroundPackageChange(String ownPackage) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), Duration.ofSeconds(10))
                    .until(d -> !ownPackage.equals(getCurrentPackageName()));
        } catch (org.openqa.selenium.TimeoutException e) {
            logger.warn("Foreground package did not change within 10s after the tap - the intent "
                    + "may not have fired, or no handler app is available on this device/environment.");
        }
    }

    /** TC_FUNC_SUP_003: taps the phone row's copy icon, then returns the real device clipboard content. */
    public String copyPhoneNumberAndReadClipboard() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.supportPhoneCopyButton, "Support Phone Copy Button");
        return getClipboardText();
    }

    /** TC_FUNC_SUP_004: taps the email row's copy icon, then returns the real device clipboard content. */
    public String copyEmailAndReadClipboard() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.supportEmailCopyButton, "Support Email Copy Button");
        return getClipboardText();
    }

    /** TC_FUNC_SUP_005: true if no EditText / Submit / Send control exists anywhere on the current screen. */
    public boolean isScreenFreeOfTicketSubmissionControls() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        boolean noEditText = getDriverForFramework()
                .findElements(By.xpath(SupportPageObjectRepository.ANY_EDIT_TEXT_XPATH)).isEmpty();
        boolean noSubmitOrSend = getDriverForFramework()
                .findElements(By.xpath(SupportPageObjectRepository.SUBMIT_OR_SEND_BUTTON_XPATH)).isEmpty();
        return noEditText && noSubmitOrSend;
    }
}
