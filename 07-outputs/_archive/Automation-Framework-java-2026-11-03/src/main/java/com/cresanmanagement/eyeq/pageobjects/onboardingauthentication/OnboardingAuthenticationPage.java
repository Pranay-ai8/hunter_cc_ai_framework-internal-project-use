package com.cresanmanagement.eyeq.pageobjects.onboardingauthentication;

import com.cresanmanagement.eyeq.utils.Base;
import com.cresanmanagement.eyeq.utils.DriverFactory;
import com.cresanmanagement.eyeq.utils.PropertyReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;

/**
 * Page Action class for the Onboarding & Authentication module (Splash, Login, SSO, Forgot Password,
 * OTP Verification, Set/Create Password, Activate Account, Biometric re-auth, Contact Support, and
 * the FR-AUTH-011 devmode/environment-switch trigger).
 *
 * {@link #login(String, String)} and {@link #switchEnvironmentToTest(String, String)} are fully
 * implemented and Live-UI-Hierarchy-verified (see OnboardingAuthenticationPageObjectRepository.java's
 * class comment for the verification findings/snapshots). Every other method on this screen's
 * surface (forgot password, OTP, activate account, biometric, contact support) remains a scaffold
 * TODO - out of scope for this pass, which implements the proof-of-concept Login flow end to end.
 */
public class OnboardingAuthenticationPage extends Base {

    private static final Logger logger = LogManager.getLogger(OnboardingAuthenticationPage.class.getName());
    private final OnboardingAuthenticationPageObjectRepository repo;

    public OnboardingAuthenticationPage() {
        repo = new OnboardingAuthenticationPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    /**
     * TC_FUNC_AUTH_004 "Login - Valid Credentials Success" path: enters the given credentials on the
     * Login screen and taps Login. Also absorbs the Android 13+ runtime notification-permission
     * dialog if the OS happens to show it right after a successful login (confirmed live - not
     * guaranteed on every run, but reproducible enough on a freshly-cleared app that it must be
     * handled rather than left to intermittently fail the test).
     *
     * Does NOT assert a landing screen itself - see {@link #isSiteSelectionScreenDisplayed()}.
     */
    public void login(String email, String password) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        requireLoginScreen();
        typeWithHeal(repo.emailInput, "Email Input", email);
        typeWithHeal(repo.passwordInput, "Password Input", password);
        hideKeyboard();
        clickWithHeal(repo.loginButton, "Login Button");
        dismissPostLoginScreens();
    }

    /**
     * Dismisses the screens confirmed live to appear right after a successful login, before Site
     * Selection: the in-app "Fingerprint Available" biometric-enrollment offer (real hardware, Android
     * 12 - see OnboardingAuthenticationPageObjectRepository's comment for the confirmed screenshot;
     * dismissed via "Not this time" since these tests don't exercise biometric enrollment) and the
     * Android 13+ runtime notification-permission dialog. Called by every login-submission path
     * ({@link #login}, {@link #tapLoginButton}, {@link #tapLoginButtonRapidly}) - not just {@code
     * login()} - after a live-run gap was found where the other two entry points skipped this and hung
     * waiting for Site Selection with the Fingerprint screen still blocking it.
     *
     * <p><b>Real-hardware-only (2026-10-19):</b> the "Fingerprint Available" screen is confirmed to be a
     * real-hardware-only behavior - an AVD emulator has no biometric sensor to offer enrollment for and
     * will never show it. Attempting the dismissal there is harmless in principle ({@code
     * dismissSystemDialogIfPresent} already no-ops safely if the element never appears, via a bounded 5s
     * wait), but every such no-op wait still costs real wall-clock time on every single login across the
     * whole suite, which matters when a battery-constrained real-device run is replaced by a
     * no-battery-constraint emulator run meant to be fast. Gated behind {@link
     * com.cresanmanagement.eyeq.utils.DeviceConfig#isRunningOnEmulator(String)} so it is only attempted on
     * real hardware. The notification-permission dialog is NOT gated - it is an OS-level dialog that can
     * appear on either an emulator or real hardware depending on the OS image/API level, not a
     * hardware-only feature.
     */
    private void dismissPostLoginScreens() {
        boolean isEmulator = com.cresanmanagement.eyeq.utils.DeviceConfig.getInstance().isRunningOnEmulator("ANDROID_APP");
        if (!isEmulator) {
            dismissSystemDialogIfPresent(repo.fingerprintAvailableNotThisTimeButton);
        } else {
            logger.debug("Skipping the 'Fingerprint Available' dismissal attempt - running on an emulator, "
                    + "which never shows this real-hardware-only screen.");
        }
        dismissSystemDialogIfPresent(repo.notificationPermissionAllowButton);
    }

    /**
     * FR-AUTH-011: the hidden devmode/please trigger. Submitting it on the Login screen (the same
     * two fields {@link #login} uses) opens a "Developer Mode" dialog instead of attempting a normal
     * login; selecting an environment and tapping "Set Base URL" (NOT "Set URL" - that is the actual
     * on-screen text, confirmed live) switches the app's backend, shows a native "Success"
     * confirmation AlertDialog, and returns to a fresh Login screen once OK is tapped.
     *
     * Intended to be called once per suite, before any real login - see
     * {@code utils.EnvironmentSetup#runOnce()} / {@code testng.SuiteCreationListener#onStart}.
     */
    public void switchEnvironmentToTest(String devModeUsername, String devModePassword) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        requireLoginScreen();
        typeWithHeal(repo.emailInput, "Email Input (devmode trigger)", devModeUsername);
        typeWithHeal(repo.passwordInput, "Password Input (devmode trigger)", devModePassword);
        hideKeyboard();
        clickWithHeal(repo.loginButton, "Login Button (devmode submit)");

        clickWithHeal(repo.testEnvironmentRadioRow, "Test Environment Radio Row");
        clickWithHeal(repo.setBaseUrlButton, "Set Base URL Button");
        clickWithHeal(repo.environmentSwitchSuccessOkButton, "Environment Switch Success OK Button");
    }

    /**
     * Confirms the post-login landing screen is Site Selection - the deterministic outcome for this
     * framework's execution model (NoReset=true preserves app data across a suite run but does not
     * fabricate a "remembered device"/returning-user state for a fresh QA account; confirmed live
     * that the Login form itself still renders blank on each new session). TC_FUNC_AUTH_004's
     * alternate "Main App Shell (returning user)" branch is not reachable under this setup and is
     * therefore not asserted here.
     */
    public boolean isSiteSelectionScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        waitUntilElementIsVisible(repo.siteSelectionTitle);
        return isElementVisible(repo.siteSelectionTitle);
    }

    /**
     * True when the Login screen's two input fields are visible. Waits for up to
     * {@code Base.waitUntilElementIsVisible}'s own timeout (not a fixed sleep) so this can be called
     * right after an app-lifecycle action (relaunch, force-kill recovery) without a separate wait.
     */
    public boolean isLoginScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.emailInput);
            return isElementVisible(repo.emailInput) && isElementVisible(repo.passwordInput);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Fast-bounded (10s) variant of {@link #isLoginScreenDisplayed()} for branch-decision /
     * postcondition use (e.g. "are we already logged in, or do we need to log in?", called once
     * per test by shared per-module reset/navigation helpers). The slow variant above burns its
     * full {@code Base.DEFAULT_WAIT} (90s) whenever the real answer is "no, not on Login" - which
     * is the common case once a suite-level session is already logged in. That 90s tax, multiplied
     * across every test in a class that calls it as a decision check, was a confirmed contributor
     * to Site Management's inflated runtime (same root cause as
     * {@code SiteManagementPage.isSiteSelectionScreenDisplayedFast()}). Use this method for
     * decision/postcondition checks; keep the slow variant only for genuine first-arrival waits
     * (e.g. asserting Login reappears after a real relaunch/force-kill/logout action).
     */
    public boolean isLoginScreenDisplayedFast() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(10))
                    .until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(repo.emailInput));
            return isElementVisible(repo.emailInput) && isElementVisible(repo.passwordInput);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** Types into both Login fields WITHOUT tapping Login - for background/resume-before-submit cases. */
    public void enterCredentialsWithoutSubmitting(String email, String password) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        requireLoginScreen();
        typeWithHeal(repo.emailInput, "Email Input", email);
        typeWithHeal(repo.passwordInput, "Password Input", password);
        hideKeyboard();
    }

    /** Taps Login without re-entering credentials (field values are whatever is already present). */
    public void tapLoginButton() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.loginButton, "Login Button");
        dismissPostLoginScreens();
    }

    /** Taps Login {@code times} times in quick succession (no waits between taps) - for duplicate-tap tests. */
    public void tapLoginButtonRapidly(int times) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + times + " taps");
        for (int i = 0; i < times; i++) {
            repo.loginButton.click();
        }
        dismissPostLoginScreens();
    }

    public String getEmailFieldValue() {
        return repo.emailInput.getAttribute("text");
    }

    /**
     * Returns the password field's raw displayed text - masked dots by default, or the real plain-text
     * value once {@link #togglePasswordVisibility()} has revealed it (confirmed live: this app's
     * masking is a genuine visual/accessibility-tree state change, not a UiAutomator-level masking
     * Appium can't see through).
     */
    public String getPasswordFieldDisplayedText() {
        return repo.passwordInput.getAttribute("text");
    }

    /** TC_GEN_SECX_007: toggles the password field between masked and revealed plain-text display. */
    public void togglePasswordVisibility() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.passwordVisibilityToggle, "Password Visibility Toggle");
    }

    /** TC_FUNC_AUTH_005/006, TC_GEN_SECX_011: the shared "incorrect email or password" backend error. */
    public boolean isGenericLoginErrorDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        waitUntilElementIsVisible(repo.genericLoginErrorMessage);
        return isElementVisible(repo.genericLoginErrorMessage);
    }

    /** TC_FUNC_AUTH_011/013, TC_GEN_FORM_012: the client-side "valid email address" format error. */
    public boolean isEmailFormatErrorDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        waitUntilElementIsVisible(repo.emailFormatErrorMessage);
        return isElementVisible(repo.emailFormatErrorMessage);
    }

    /** TC_FUNC_AUTH_002/003: backgrounds the app for {@code seconds} then lets Appium auto-restore it. */
    public void backgroundAppAndResume(int seconds) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        minimizeApplicationAndRestore(seconds);
    }

    /**
     * TC_FUNC_AUTH_003/016: force-stops the app process and relaunches it from Splash - for recovery
     * assertions after an abrupt kill (either at idle on Splash, or mid-login-submission).
     */
    public void forceKillAndRelaunchApp() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String appPackage = PropertyReader.getInstance().getPropValues("config.properties", "AndroidAppPackage");
        forceStopApp(appPackage);
        relaunchApp(appPackage);
    }

    /** TC_FUNC_AUTH_015: toggles airplane mode on/off to simulate no network connectivity. */
    public void setAirplaneMode(boolean enabled) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + enabled);
        toggleAirplaneMode(enabled);
    }

    /**
     * TC_GEN_LOGIN_013: clears the app's local data (forcing a logged-out state on next launch) and
     * relaunches it. CAUTION (see {@code Base#clearAppData}): this also wipes the suite-level
     * devmode/Test-backend switch - callers MUST re-run {@link #switchEnvironmentToTest(String, String)}
     * afterward before any other test in the same suite run depends on the Test backend still being
     * selected.
     */
    public void clearAppDataAndRelaunch() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String appPackage = PropertyReader.getInstance().getPropValues("config.properties", "AndroidAppPackage");
        clearAppData(appPackage);
        relaunchApp(appPackage);
    }

    /**
     * TC_FUNC_AUTH_015: confirms the app's dedicated "There is no internet connection." screen
     * (NoInternetActivity) is displayed - confirmed live on real hardware that this is a prompt,
     * app-wide connectivity gate, not an inline Login-screen error (see
     * OnboardingAuthenticationPageObjectRepository's "No-connectivity gate" note).
     */
    public boolean isNoInternetConnectionScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.noInternetConnectionMessage);
            return isElementVisible(repo.noInternetConnectionMessage);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Precondition guard called by every method that types into the Login screen's fields. Fails
     * loudly with a clear exception if the app is not actually on the Login screen right now, instead
     * of silently matching whatever happens to be "the first EditText" on a different screen.
     *
     * <p>Added after a live-run bug (2026-10-16, see OnboardingAuthenticationPageObjectRepository's
     * class javadoc): a race condition in a force-kill-mid-submission test left the app on the Site
     * Selection screen (which also has exactly one EditText - its search box) when the next action
     * assumed it was on Login, causing the QA login email to be typed into Site Selection's search
     * field. The locators themselves are now scoped to {@code loginRootContainer} so they can no
     * longer match on another screen at all - this guard is a second, independent line of defense that
     * surfaces the problem as an immediate, diagnosable failure instead of a silent wrong-field type.
     */
    private void requireLoginScreen() {
        try {
            waitUntilElementIsVisible(repo.loginScreenRoot);
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new IllegalStateException(
                    "Expected to be on the Login screen (loginRootContainer not found/visible) but the app "
                            + "appears to be showing a different screen. Refusing to type login credentials "
                            + "into an unknown field.", e);
        }
    }

    // =================================================================================================
    // LIVE-VERIFIED 2026-10-16 (real device 8AHY0LBTH, Pixel 3 XL) — methods below were originally written
    // in code-writing-only mode and have since been corrected against real driver sessions. See
    // OnboardingAuthenticationPageObjectRepository's own LIVE-VERIFIED section for the full per-locator
    // evidence; this class's methods are updated to match (single-OTP-field, corrected button texts,
    // the enabled-attribute password-rule signal, coordinate-based Contact Support tap, etc.).
    // =================================================================================================

    // --- Remembered Login / Quick Re-auth (FR-AUTH-003) ---

    /**
     * GATED precondition for the entire Remembered Login cluster (TC_FUNC_AUTH_021/024/025/026/027/028).
     * LIVE-VERIFIED (2026-10-16): directly tested the one plausible mechanism this framework could use to
     * reach a "remembered device" state - log in successfully, force-stop the app, relaunch it WITHOUT
     * clearing app data. Confirmed result: the app resumes DIRECTLY to Site Selection (a continued
     * logged-in session), not a Login screen with a locked/pre-filled email - none of "Not you?",
     * "Fingerprint", or "EyeQ Cloud VMS" text was present anywhere on the resulting screen (see
     * docs/dom-snapshots/onboardingauthentication_live_21_after_forcekill_relaunch_no_clear.xml). This
     * precondition remains genuinely unreachable under this automation environment - the most likely real
     * mechanism is an explicit in-app "Log Out" action (as opposed to a full data-clearing reset),
     * probably exposed in the not-yet-built/tested Account Settings module. Per the uncertainty-resolution
     * protocol, this throws rather than guess further at an unconfirmed mechanism.
     */
    public void requireRememberedLoginStateReachable() {
        throw new SkipException(
                "Remembered Login precondition ('device previously remembered a logged-in account') is not "
                        + "reachable via force-kill+relaunch-without-clearing-data - LIVE-CONFIRMED 2026-10-16: "
                        + "that sequence resumes directly to Site Selection (a continued session), not a "
                        + "locked-email Login variant. The real mechanism is most likely an explicit in-app "
                        + "'Log Out' action (probably in the not-yet-tested Account Settings module), not yet "
                        + "confirmed. Skipping pending that confirmation.");
    }

    /** GATED (precondition unreachable - see above). TC_FUNC_AUTH_024/028: true once the locked-email "Not you?" control is visible. */
    public boolean isNotYouButtonDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.notYouButton);
    }

    /** GATED (precondition unreachable - see above). TC_FUNC_AUTH_024/028: resets the locked email field back to a fresh, editable Login state. */
    public void tapNotYouButton() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.notYouButton, "Not You Button");
    }

    /** GATED (precondition unreachable - see above). TC_FUNC_AUTH_025: expected to be absent/not-displayed on a remembered device. */
    public boolean isSsoLoginButtonDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.ssoLoginButton);
    }

    /** LIVE-VERIFIED locator (ssoLoginButton confirmed present/enabled on the ordinary Login screen); used here for TC_FUNC_AUTH_026/080 to confirm a biometric/fingerprint login trigger is absent on a device with no biometric enrolled. */
    public boolean isLoginWithFingerprintButtonDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.loginWithFingerprintButton);
    }

    // --- Forgot Password (FR-AUTH-005) — LIVE-VERIFIED ---

    /** LIVE-VERIFIED. Navigates from the Login screen to the Forgot Password email-entry screen via the directly-clickable "Forgot password?" TextView. */
    public void navigateToForgotPasswordFromLogin() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        requireLoginScreen();
        clickWithHeal(repo.forgotPasswordLink, "Forgot Password Link");
    }

    /** LIVE-VERIFIED. TC_FUNC_AUTH_029/030/031/032/034/035: enters an email and taps the confirmed "Send Code" button. Waits for the loading spinner to clear and the "Reset Password" title to be gone before returning, so callers don't race a still-in-flight navigation (a real bug hit during live verification). */
    public void submitForgotPasswordEmail(String email) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.forgotPasswordEmailInput, "Forgot Password Email Input", email);
        clickWithHeal(repo.sendCodeButton, "Send Code Button");
        waitForScreenTransitionAwayFrom("Reset Password");
    }

    /**
     * Polls (bounded, via repeated short waits rather than a fixed sleep) until BOTH the app's loading
     * spinner (resource-id {@code rlLoading}) is gone AND the given title text is no longer present,
     * confirming the in-flight network transition has completed. Needed because this app shows a
     * transient loading overlay on top of the previous screen's own elements for 2-5s after Send
     * Code/OTP Submit, which can otherwise be mistaken for "nothing happened" if read too early
     * (confirmed live during this screen's own verification).
     */
    private void waitForScreenTransitionAwayFrom(String titleText) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(getDriverForFramework(), java.time.Duration.ofSeconds(20))
                    .pollingEvery(java.time.Duration.ofMillis(500))
                    .until(d -> {
                        boolean loadingVisible = !d.findElements(org.openqa.selenium.By.id(
                                "com.cresanmanagement.eyeq:id/rlLoading")).isEmpty();
                        boolean stillOnPriorTitle = !d.findElements(org.openqa.selenium.By.xpath(
                                "//*[@text='" + titleText + "']")).isEmpty();
                        return !loadingVisible && !stillOnPriorTitle;
                    });
        } catch (org.openqa.selenium.TimeoutException e) {
            // Same bounded-wait semantics as the original Thread.sleep()-based polling loop this replaced
            // (2026-10-20, standing "never Thread.sleep()" rule) - give up silently after 20s rather than
            // throwing, leaving it to the caller's own subsequent assertions to surface a real failure.
        }
    }

    /** LIVE-VERIFIED. TC_FUNC_AUTH_033/038's "Back to Login" variant for the Forgot Password screen. */
    public void tapBackToLoginFromForgotPassword() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.backToLoginButton, "Back to Login Button (Forgot Password)");
    }

    // --- OTP Verification (FR-AUTH-006) — shared by both the Forgot Password and Activate Account flows.
    // LIVE-VERIFIED: a SINGLE EditText accepting up to 6 characters, not 6 separate boxes - see
    // OnboardingAuthenticationPageObjectRepository#otpInput's javadoc. CONFIRMED: the Test backend accepts
    // ANY syntactically-valid 6-digit code - GapAnalysis.md GAP-ENV-01 is resolved for navigation
    // purposes; see docs/dom-snapshots/onboardingauthentication_live_otpcode_111111.xml and
    // _live_act_03_after_submit_111111.xml for the evidence (two codes, two flows, each on a fresh
    // session/request). ---

    /**
     * LIVE-VERIFIED. True once the OTP screen is displayed - checked via its confirmed-unique title
     * "Authentication" (see {@code OnboardingAuthenticationPageObjectRepository#otpScreenTitle}'s
     * javadoc), NOT the OTP input field itself: that field's unscoped locator was found live to also
     * match other screens' EditTexts, making it unreliable for screen-identity checks (only safe for
     * direct typing once we already know we're on this screen).
     */
    public boolean isOtpVerificationScreenDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.otpScreenTitle);
            return isElementVisible(repo.otpScreenTitle);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * LIVE-VERIFIED CORRECTION. Types {@code code} into the single OTP field. Confirmed live behavior
     * this method relies on rather than attempts to simulate itself: typing more than 6 characters
     * results in only the first 6 being retained (app-side cap - TC_FUNC_AUTH_041), and typing
     * non-numeric characters results in an EMPTY field (app-side rejection - TC_FUNC_AUTH_042) - both are
     * therefore observed via {@link #getOtpFieldValue()} after calling this, not enforced here.
     */
    public void enterOtp(String code) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + code.length() + " chars");
        waitUntilElementIsVisible(repo.otpInput);
        typeWithHeal(repo.otpInput, "OTP Input", code);
    }

    /**
     * NOT yet live-verified as of 2026-10-22 (written while the device was occupied by a long-running
     * regression suite - TC_FUNC_AUTH_050's own scenario). Sets the device clipboard to {@code code}, taps
     * the single OTP field to focus it, then pastes via Android's standard PASTE key event (no on-screen
     * context-menu lookup needed - see {@code Base#pasteIntoFocusedField}). Confirmed via
     * {@link #enterOtp(String)}'s own javadoc that this screen's OTP control is a SINGLE EditText, not 6
     * segmented boxes, so a plain paste into one field is the correct action for this build (the original
     * FunctionalTestCases.md wording, "all 6 digit segments populate", assumed a segmented-box UI the real
     * app does not have - the real, single-field equivalent is simply that the pasted code appears in full).
     */
    public void pasteOtp(String code) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + code.length() + " chars");
        waitUntilElementIsVisible(repo.otpInput);
        setClipboardText(code);
        clickWithHeal(repo.otpInput, "OTP Input (tap to focus before paste)");
        pasteIntoFocusedField();
    }

    /** LIVE-VERIFIED. Taps the confirmed Submit control (text varies by flow - "Submit and Reset Password" vs plain "Submit" - matched via the repository's shared contains("Submit") locator). */
    public void tapOtpSubmit() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.otpSubmitButton, "OTP Submit Button");
        waitForScreenTransitionAwayFrom("Authentication");
    }

    /**
     * LIVE-VERIFIED CORRECTION. There is no "Incorrect code" error in this build - the Test backend does
     * not reject a syntactically-valid code as wrong (see class-level note above). The real, confirmed
     * client-side validation this method now checks for fires on a BLANK or PARTIAL (fewer-than-6-digit)
     * OTP submission: "Enter the complete 6-digit code." TC_FUNC_AUTH_039 ("OTP - Incorrect Code") has
     * been re-pointed at this confirmed behavior instead - see its test method's updated javadoc.
     */
    public boolean isOtpIncompleteCodeErrorDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.otpIncompleteCodeError);
            return isElementVisible(repo.otpIncompleteCodeError);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** LIVE-VERIFIED CORRECTION. TC_FUNC_AUTH_044/045/051: the confirmed "Didn't receive the code?" control's displayed text. */
    public String getOtpResendLinkText() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.otpResendLink.getText();
    }

    /**
     * UNVERIFIED (tooling limitation, not a confirmed app behavior - see
     * OnboardingAuthenticationPageObjectRepository#otpResendLink's javadoc): taps the "Didn't receive the
     * code?" control. Whether this reveals a distinct cooldown/resend sub-control, or acts immediately,
     * was not confirmed during this live pass due to an XPath apostrophe-escaping issue in the
     * exploration tooling - a future pass must properly quote-escape the locator and re-verify
     * TC_FUNC_AUTH_044/045/051's actual resulting behavior.
     */
    public void tapOtpResendLink() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.otpResendLink.click();
    }

    /**
     * LIVE-VERIFIED CORRECTION. TC_FUNC_AUTH_049: no "Back"/"Back to Login" element exists on the OTP
     * screen itself (confirmed absent from every live dump of this screen) - uses the Android system
     * back navigation instead (already available via {@link Base#navigateBack()}).
     *
     * <p><b>LIVE-RUN BUG FOUND AND FIXED (2026-10-19):</b> the OTP input field auto-focuses when this
     * screen loads (confirmed live), opening the soft keyboard WITHOUT the test ever explicitly typing
     * anything. On Android, when the keyboard is showing, the system back gesture's FIRST press only
     * dismisses the keyboard - it does NOT also navigate away - requiring a second press to actually leave
     * the screen. A single {@code navigateBack()} call therefore silently no-op'd (confirmed live:
     * TC_FUNC_AUTH_049 failed with the app still showing the Authentication/OTP screen afterward). Fixed
     * by explicitly hiding the keyboard FIRST (a safe no-op via {@link Base#hideKeyboard()} if it's
     * already closed) before the back press, so the back press is guaranteed to act on the screen itself.
     */
    public void tapOtpBack() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        hideKeyboard();
        navigateBack();
    }

    /** LIVE-VERIFIED CORRECTION. TC_FUNC_AUTH_042: the raw displayed text of the single OTP field, to confirm non-numeric input was rejected (results in an empty string, confirmed live). */
    public String getOtpFieldValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.otpInput.getAttribute("text");
    }

    /**
     * Bounded wait confirming the OTP screen has genuinely been left - checked via its confirmed-unique
     * title "Authentication" becoming invisible, NOT the OTP input field (see
     * {@link #isOtpVerificationScreenDisplayed()}'s javadoc for why that field's locator is unsuitable
     * for an absence check: it matches other screens' EditTexts too, so waiting for it to "become
     * invisible" never completes - confirmed live, ran the full 90s timeout twice before this fix). Used
     * by tests asserting "we left the OTP screen" (TC_FUNC_AUTH_039/049) instead of a single immediate
     * {@link #isOtpVerificationScreenDisplayed()} read, which was also found live to occasionally still
     * see the title for a brief instant right after tapping Submit or using system back (a Compose
     * crossfade/transition artifact).
     */
    public boolean hasLeftOtpVerificationScreen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitForElementToBeInvisible(repo.otpScreenTitle);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // --- Set New Password / Create Your Password (FR-AUTH-007) — LIVE-VERIFIED, GAP-ENV-01 RESOLVED.
    // Reached via submitting ANY syntactically-valid 6-digit code on the OTP screen (confirmed - see
    // class-level OTP note above); the previous requireOtpCaptureCapability() SkipException gate has been
    // removed since the capability it was waiting on is now confirmed to exist (a test-backend bypass,
    // not a real mailbox/IMAP capability, but functionally equivalent for automation purposes). ---

    /** LIVE-VERIFIED. Enters the new/first password field. */
    public void enterNewPassword(String password) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.newPasswordInput, "New Password Input", password);
    }

    /** LIVE-VERIFIED. Enters the confirm/second password field. */
    public void enterConfirmPassword(String password) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.confirmPasswordInput, "Confirm Password Input", password);
    }

    /** LIVE-VERIFIED. The new/first password field's current raw displayed text, to confirm input round-trips correctly (e.g. for Unicode/emoji boundary tests). */
    public String getNewPasswordFieldValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.newPasswordInput.getAttribute("text");
    }

    /**
     * LIVE-VERIFIED CORE MECHANISM for the whole password-rule test cluster. The submit button's
     * {@code enabled} attribute is confirmed to be {@code false} when ANY of the 5 rules is unsatisfied
     * and {@code true} only once ALL 5 are satisfied (confirmed live: false on a fresh screen, false with
     * a 4-of-5-rules-satisfying password, true once corrected to satisfy all 5 - see
     * docs/dom-snapshots/onboardingauthentication_live_pwd_*.xml). This replaces the originally-assumed
     * "rule-specific validation message" mechanism, which does not exist in this build - the 5 checklist
     * TextViews are static/always-visible and do not expose a pass/fail state via any UiAutomator
     * attribute (confirmed: identical selected/checked/content-desc values regardless of password
     * validity). {@code isResetFlow} selects which of the two (differently-labelled) submit buttons to
     * check.
     */
    public boolean isSetPasswordSubmitEnabled(boolean isResetFlow) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: isResetFlow=" + isResetFlow);
        WebElement button = isResetFlow ? repo.confirmPasswordResetButton : repo.confirmNewPasswordButton;
        return Boolean.parseBoolean(button.getAttribute("enabled"));
    }

    /** LIVE-VERIFIED. Taps the Reset-flow submit control ("Confirm Password Reset"). Only effective when {@link #isSetPasswordSubmitEnabled(boolean)} is true - tapping while disabled is expected to no-op. */
    public void tapConfirmPasswordReset() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.confirmPasswordResetButton.click();
        waitForScreenTransitionAwayFrom("Reset Password");
    }

    /** LIVE-VERIFIED. Taps the Activate-flow submit control ("Confirm New Password" - also this screen's field-2 label, a likely duplicate-label defect; see repository javadoc). Only effective when {@link #isSetPasswordSubmitEnabled(boolean)} is true. */
    public void tapConfirmNewPassword() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        repo.confirmNewPasswordButton.click();
        waitForScreenTransitionAwayFrom("Create Your Password");
    }

    /**
     * True once the Login screen is shown again after a successful password set/create
     * (TC_FUNC_AUTH_065/066).
     *
     * <p><b>LIVE-RUN FINDING (2026-10-19, real device):</b> this check was found to return a false
     * negative even when the password change demonstrably succeeded (confirmed independently by a
     * subsequent real login with the new password reaching Site Selection) - a one-shot
     * {@code isLoginScreenDisplayed()} read right after tapping the submit button isn't reliable here,
     * likely because the app briefly shows a post-reset system dialog (notification-permission) or takes
     * longer to settle on real hardware than the emulator. Hardened by dismissing the same post-action
     * dialogs {@link #dismissPostLoginScreens} absorbs after a real login, before checking.
     */
    public boolean isLoginScreenDisplayedAfterPasswordSet() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        dismissSystemDialogIfPresent(repo.notificationPermissionAllowButton);
        return isLoginScreenDisplayed();
    }

    /** LIVE-VERIFIED. Taps this screen's plain "Back" control (confirmed distinct from "Back to Login" - see repository javadoc). */
    public void tapSetPasswordBack() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.backButtonPlain, "Set/Create Password Back Button");
    }

    // --- Activate Account (FR-AUTH-008) — LIVE-VERIFIED ---

    /** LIVE-VERIFIED. TC_FUNC_AUTH_070: navigates from the Login screen to Activate Account via the directly-clickable "Activate Account" TextView. */
    public void navigateToActivateAccountFromLogin() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        requireLoginScreen();
        clickWithHeal(repo.activateAccountLink, "Activate Account Link");
    }

    /**
     * LIVE-VERIFIED. TC_FUNC_AUTH_071/074: enters an email and taps the confirmed "Send Code" button -
     * RequirementAnalysis.md's "assumed by analogy" caveat for this screen's label is now confirmed
     * correct (resolves TC_FUNC_AUTH_075 too). ALSO LIVE-CONFIRMED: this Test backend does not appear to
     * require the email to have a genuinely pending activation - the QA account's own (already-active)
     * email was accepted all the way through to Create Your Password - so TC_FUNC_AUTH_071 no longer
     * needs a dedicated pending-activation account; see the test method's updated implementation.
     */
    public void submitActivateAccountEmail(String email) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.activateAccountEmailInput, "Activate Account Email Input", email);
        clickWithHeal(repo.sendCodeButton, "Activate Account Submit Button ('Send Code', confirmed)");
        waitForScreenTransitionAwayFrom("Activate account");
    }

    // --- Biometric Re-authentication (FR-AUTH-009) — LIVE-VERIFIED ---

    /** LIVE-VERIFIED. TC_FUNC_AUTH_080: true when NO biometric/fingerprint login trigger is present on the (non-remembered) Login screen - confirmed absent on every live Login-screen dump captured in this project. */
    public boolean isBiometricTriggerAbsentOnLoginScreen() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !isElementVisible(repo.loginWithFingerprintButton);
    }

    // --- Contact Support (Login footer) (FR-AUTH-010) — LIVE-VERIFIED ---

    /**
     * LIVE-VERIFIED CORRECTION. "contact support" is a {@code ClickableSpan} embedded inside a
     * non-clickable TextView, not a separately tappable element - a normal {@code clickWithHeal}/
     * {@code element.click()} silently no-ops (confirmed live). This method instead reads the footer
     * TextView's full text and screen {@code bounds}, locates the "contact support" substring's
     * proportional horizontal position within that text, and taps that COMPUTED coordinate directly via
     * {@link Base#tapByCoordinates(int, int)} - confirmed live to reliably open the overlay. This
     * approach (proportional-width estimate) assumes a single-line, left-to-right, non-wrapped rendering
     * of the sentence; if the real device/app ever wraps this sentence across lines the estimate would
     * need revisiting, but was confirmed correct on the real device (Pixel 3 XL) this was verified on.
     */
    public void openContactSupportFromLoginFooter() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        requireLoginScreen();
        WebElement footer = repo.contactSupportLoginFooterText;
        String fullText = footer.getAttribute("text");
        String bounds = footer.getAttribute("bounds"); // e.g. "[78,2533][1363,2617]"
        int[] parsed = parseBounds(bounds);
        int x1 = parsed[0];
        int y1 = parsed[1];
        int x2 = parsed[2];
        int y2 = parsed[3];
        String span = "contact support";
        int spanStart = fullText.indexOf(span);
        if (spanStart < 0) {
            throw new IllegalStateException("Expected the footer text to contain 'contact support' but it did not: '" + fullText + "'");
        }
        int spanMid = spanStart + span.length() / 2;
        int width = x2 - x1;
        int tapX = x1 + (int) (width * ((double) spanMid / fullText.length()));
        int tapY = (y1 + y2) / 2;
        tapByCoordinates(tapX, tapY);
    }

    /** Parses a UiAutomator-style bounds string "[x1,y1][x2,y2]" into {x1,y1,x2,y2}. */
    private int[] parseBounds(String bounds) {
        String cleaned = bounds.replace("]", "").replace("[", "|");
        String[] parts = cleaned.split("\\|");
        String[] p1 = parts[1].split(",");
        String[] p2 = parts[2].split(",");
        return new int[]{
                Integer.parseInt(p1[0].trim()), Integer.parseInt(p1[1].trim()),
                Integer.parseInt(p2[0].trim()), Integer.parseInt(p2[1].trim())
        };
    }

    /** LIVE-VERIFIED. True once both the phone and email contact rows are visible in the overlay. */
    public boolean isContactSupportOverlayDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            waitUntilElementIsVisible(repo.contactSupportPhoneNumberText);
            return isElementVisible(repo.contactSupportPhoneNumberText) && isElementVisible(repo.contactSupportEmailText);
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** LIVE-VERIFIED. TC_FUNC_AUTH_083: taps the displayed phone number "(888) 678-6555" (expected to invoke the device dialer). */
    public void tapContactSupportPhoneNumber() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.contactSupportPhoneNumberText, "Contact Support Phone Number Text");
    }

    /** LIVE-VERIFIED. TC_FUNC_AUTH_084: taps the displayed email address "support@eyeqmonitoring.com" (expected to invoke the device email client). */
    public void tapContactSupportEmail() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.contactSupportEmailText, "Contact Support Email Text");
    }

    /** LIVE-VERIFIED. Closes the Contact Support overlay via its confirmed "Back to Login" control. */
    public void tapContactSupportBackToLogin() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.backToLoginButton, "Contact Support Back to Login Button");
    }

    /**
     * LIVE-VERIFIED. Reads the Login-footer Contact Support overlay's displayed phone number text
     * (added for the Support module's TC_FUNC_SUP_007 cross-screen comparison). Added 2026-10-20 after
     * confirming, on two separate physical devices, that this overlay's phone number genuinely differs
     * from the Support tab's phone number - see SupportTests.java's TC_FUNC_SUP_007.
     */
    public String getContactSupportPhoneNumberText() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.contactSupportPhoneNumberText.getText();
    }

    /** LIVE-VERIFIED. Reads the Login-footer Contact Support overlay's displayed email text (TC_FUNC_SUP_007). */
    public String getContactSupportEmailText() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.contactSupportEmailText.getText();
    }

    // --- Added for the Onboarding & Authentication Generic test case batch (2026-10-28) ---

    /** TC_GEN_FP_004: enters an email into the Forgot Password email field WITHOUT tapping Send Code, so the raw field value can be inspected before submission. */
    public void enterForgotPasswordEmailOnly(String email) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        typeWithHeal(repo.forgotPasswordEmailInput, "Forgot Password Email Input", email);
    }

    /** TC_GEN_FP_004: the Forgot Password email field's raw displayed text, to confirm whether leading/trailing spaces are trimmed on display. */
    public String getForgotPasswordEmailFieldValue() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return repo.forgotPasswordEmailInput.getAttribute("text");
    }

    /** TC_GEN_FP_004: taps the confirmed "Send Code" button without re-typing the email (field value is whatever is already present). */
    public void tapSendCodeButton() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        clickWithHeal(repo.sendCodeButton, "Send Code Button");
        waitForScreenTransitionAwayFrom("Reset Password");
    }

    /**
     * TC_GEN_SECX_008: true when the Login screen's email field is NOT editable (its {@code enabled}
     * attribute is {@code false}), the confirmed signal for a "remembered device" locked-email state -
     * same attribute-based mechanism already proven for {@link #isSetPasswordSubmitEnabled(boolean)}.
     */
    public boolean isEmailFieldLocked() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return !Boolean.parseBoolean(repo.emailInput.getAttribute("enabled"));
    }

    /**
     * TC_GEN_OTP_054: true only when every element FunctionalTestCases.md expects on the OTP Verification
     * screen that this build actually has is visible - title, the (single, confirmed-live - see
     * {@link #enterOtp(String)}'s javadoc) OTP input field, the Resend control, and the Submit button. The
     * spec's "segmented input" wording does not apply to this build (confirmed single EditText).
     */
    public boolean areOtpScreenCoreElementsDisplayed() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        return isElementVisible(repo.otpScreenTitle) && isElementVisible(repo.otpInput)
                && isElementVisible(repo.otpResendLink) && isElementVisible(repo.otpSubmitButton);
    }

    /** TC_GEN_OTP_034: taps the Resend control {@code times} times in quick succession (no waits between taps) - for rapid-tap stability tests. */
    public void tapOtpResendLinkRapidly(int times) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + times + " taps");
        for (int i = 0; i < times; i++) {
            repo.otpResendLink.click();
        }
    }

    /**
     * TC_GEN_OTP_044: taps the OTP Submit button {@code times} times in quick succession (no waits between
     * taps) - for duplicate-submission tests.
     *
     * <p><b>LIVE-RUN FINDING (2026-10-28):</b> unlike {@link #tapLoginButtonRapidly(int)}'s Login button
     * (confirmed to stay present through its own post-tap transition), this screen's Submit button is
     * confirmed to disappear immediately once the first tap successfully navigates away - a raw click loop
     * with no guard then throws {@code NoSuchElementException} on the 2nd+ tap. That failure IS itself
     * confirmation that the first tap's navigation already succeeded (so no further taps can reach a now-gone
     * button to duplicate-process anything), so it is caught and treated as "stop tapping, already navigated"
     * rather than a real test failure.
     */
    public void tapOtpSubmitRapidly(int times) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + times + " taps");
        for (int i = 0; i < times; i++) {
            try {
                repo.otpSubmitButton.click();
            } catch (org.openqa.selenium.WebDriverException alreadyNavigatedAway) {
                logger.debug("tapOtpSubmitRapidly: Submit button no longer present after " + i
                        + " tap(s) - already navigated away, stopping further taps.");
                return;
            }
        }
    }
}
