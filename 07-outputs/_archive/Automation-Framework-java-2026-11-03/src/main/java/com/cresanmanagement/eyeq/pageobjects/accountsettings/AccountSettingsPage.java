package com.cresanmanagement.eyeq.pageobjects.accountsettings;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

/**
 * Page Action class for the Account Settings module (Entry list, General Information,
 * Security & Access, About, Log Out).
 *
 * <p>LIVE-VERIFIED 2026-10-20 - see {@code AccountSettingsPageObjectRepository}'s class javadoc for the
 * full evidence basis and confirmed real-vs-documented discrepancies.
 */
public class AccountSettingsPage extends Base {

    private static final Logger logger = LogManager.getLogger(AccountSettingsPage.class.getName());
    private final AccountSettingsPageObjectRepository repo;

    public AccountSettingsPage() {
        repo = new AccountSettingsPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    // ---------------------------------------------------------------------------------------------
    // Entry list
    // ---------------------------------------------------------------------------------------------

    public void tapGeneralInformation() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.generalInformationRow, "Account Settings: General Information Row");
    }

    public void tapSecurityAccess() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.securityAccessRow, "Account Settings: Security & Access Row");
    }

    public void tapAbout() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.aboutRow, "Account Settings: About Row");
    }

    public void tapLogOut() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.logOutRow, "Account Settings: Log out Row");
    }

    public void tapBack() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.backButton, "Account Settings: Back Button");
    }

    public boolean areAllFourEntriesPresent() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.generalInformationRow) && isElementVisible(repo.securityAccessRow)
                && isElementVisible(repo.aboutRow) && isElementVisible(repo.logOutRow);
    }

    /**
     * TC_GEN_LOGIN_020: true if a "Log out" control is present anywhere on the CURRENT screen - a raw,
     * driver-wide check (not scoped to {@code repo.logOutRow}'s own Account Settings context) so it is
     * meaningful even when called from a completely different screen (e.g. Login) where Account Settings
     * was never opened at all.
     */
    public boolean isLogOutControlVisibleAnywhereOnScreen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Log out']")).isEmpty();
    }

    // ---------------------------------------------------------------------------------------------
    // General Information
    // ---------------------------------------------------------------------------------------------

    public boolean isGeneralInformationScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.generalInformationScreenTitle);
    }

    public String getFirstName() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.firstNameField.getText();
    }

    public String getLastName() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.lastNameField.getText();
    }

    public void setFirstName(String value) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + value);
        repo.firstNameField.clear();
        typeWithHeal(repo.firstNameField, "First Name Field", value);
        hideKeyboard();
    }

    public void setLastName(String value) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + value);
        repo.lastNameField.clear();
        typeWithHeal(repo.lastNameField, "Last Name Field", value);
        hideKeyboard();
    }

    public void tapSaveChanges() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.saveChangesButton, "Save Changes Button");
    }

    public void tapCancelChanges() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.cancelChangesButton, "Cancel Changes Button");
    }

    public boolean isSaveChangesEnabled() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.saveChangesButton.isEnabled();
    }

    /** TC_FUNC_ACCT_013/015/016/017: opens the real "Change profile photo" native dialog. */
    public void tapUploadPhoto() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.uploadPhotoButton, "Upload Photo Button");
    }

    public boolean isChangeProfilePhotoDialogDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.changeProfilePhotoDialogTitle)
                && isElementVisible(repo.chooseFromGalleryButton)
                && isElementVisible(repo.takePhotoButton);
    }

    public void tapChooseFromGallery() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.chooseFromGalleryButton, "Choose from Gallery Button");
    }

    public void tapDismissPhotoDialog() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.dismissPhotoDialogButton, "Dismiss Photo Dialog Button");
    }

    public boolean isResetAvatarButtonEnabled() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.resetAvatarButton.isEnabled();
    }

    // ---------------------------------------------------------------------------------------------
    // Security & Access
    // ---------------------------------------------------------------------------------------------

    public boolean isSecurityAccessScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.securityAccessScreenTitle);
    }

    public boolean isMfaToggleOn() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return "true".equals(repo.mfaToggle.getAttribute("checked"));
    }

    public void toggleMfa() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.mfaToggle, "MFA Toggle");
    }

    /**
     * Confirmed live 2026-10-27: this "Allow Fingerprint" row does not appear on this test device at all,
     * since it has no fingerprint hardware/enrollment - used to let TC_FUNC_ACCT_023/024 skip with a clear
     * reason instead of failing on a {@code NoSuchElementException} for the toggle itself.
     */
    public boolean isFingerprintOptionAvailable() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Allow Fingerprint']")).isEmpty();
    }

    public boolean isFingerprintToggleOn() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return "true".equals(repo.fingerprintToggle.getAttribute("checked"));
    }

    public void toggleFingerprint() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.fingerprintToggle, "Fingerprint Toggle");
    }

    public boolean isAuthenticationMethodSectionDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Authentication Method']")).isEmpty();
    }

    public void openAuthenticationMethodSelector() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.authenticationMethodSelectControl, "Authentication Method Select Control");
    }

    /**
     * TC_FUNC_ACCT_022: returns true if "Authenticator App" appears as a selectable option -
     * LIVE-CONFIRMED this should be false (the two real options are "Email" and "Text" only).
     */
    public boolean isAuthenticatorAppOptionPresent() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Authenticator App']")).isEmpty();
    }

    public boolean isEmailAuthMethodOptionPresent() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Email']")).isEmpty();
    }

    public boolean isTextAuthMethodOptionPresent() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !getDriverForFramework().findElements(By.xpath("//android.widget.TextView[@text='Text']")).isEmpty();
    }

    // ---------------------------------------------------------------------------------------------
    // About
    // ---------------------------------------------------------------------------------------------

    public boolean isAboutScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.appVersionValue);
    }

    public String getAppVersion() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.appVersionValue.getText();
    }

    public String getLastUpdatedDate() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.lastUpdatedDateValue.getText();
    }

    public String getApiEnvironment() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.apiEnvironmentValue.getText();
    }

    public void tapAboutEyeQLink() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.aboutEyeQLink, "About EyeQ Link");
    }

    public void tapPrivacyPolicyLink() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.privacyPolicyLink, "Privacy Policy Link");
    }

    // ---------------------------------------------------------------------------------------------
    // Log Out
    // ---------------------------------------------------------------------------------------------

    public boolean isLogOutConfirmDialogDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.logOutConfirmDialogTitle);
    }

    public void tapLogOutDialogCancel() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.logOutDialogCancelButton, "Log Out Dialog Cancel Button");
    }

    public void tapLogOutDialogConfirm() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.logOutDialogConfirmButton, "Log Out Dialog Confirm Button");
    }

    /**
     * Lightweight per-test reset (suite-level session reuse architecture, 2026-10-20): presses back up to
     * a bounded number of times until no Account Settings UI (entry list or any sub-screen) remains
     * visible, returning the shared session to the Main App Shell underneath. Reused in place of the
     * retired {@code resetToCleanLoginOnTestBackend()} full-relaunch pattern - see
     * {@code testng.SuiteCreationListener}'s class javadoc for why that pattern was retired.
     */
    public void closeAllAccountSettingsScreens() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        for (int attempt = 0; attempt < 5 && isAnyAccountSettingsUiVisible(); attempt++) {
            navigateBack();
        }
    }

    private boolean isAnyAccountSettingsUiVisible() {
        return !getDriverForFramework().findElements(By.xpath(
                "//android.widget.TextView[@text='General Information' or @text='Security & Access' "
                        + "or @text='About' or @text='Log out']")).isEmpty();
    }
}
