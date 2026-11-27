package com.cresanmanagement.eyeq.pageobjects.onboardingauthentication;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only - no business logic.
 *
 * Android-only project: @AndroidFindBy only (no @iOSXCUITFindBy - see pom.xml/agent doc scope notes).
 *
 * LIVE UI HIERARCHY VERIFICATION (per 03-standards/locator-standards.md /
 * 03-standards/appium-java-standards.md's "Live UI Hierarchy Verification" section) was performed
 * against a real booted emulator (emulator-5554) and the real APK on 2026-10-16. Raw page-source
 * snapshots are persisted under {@code docs/dom-snapshots/onboardingauthentication_*.xml} for
 * traceability.
 *
 * FINDING - the Login screen is a Jetpack Compose UI with NO {@code resource-id} or
 * {@code content-desc} on any of its interactive elements (confirmed by inspecting the live
 * hierarchy, not assumed) - this is a real app gap, not a framework limitation. Per
 * locator-standards.md item 3 ("If nothing plausible exists... stop and flag it"), the field order
 * (email first, then password) was confirmed structurally via each EditText's adjacent label
 * ("Email Address"/"Password") and by observing the actual typed values round-trip correctly through
 * a live session - index-based XPath is therefore a documented, verified limitation here, not a
 * guess. This should be raised with engineering as a testability/accessibility gap (mirrors the
 * password-toggle-has-no-content-desc finding already in RequirementAnalysis.md Section 11/23 - the
 * clickable password-visibility-toggle View nested in the password EditText likewise has no
 * content-desc).
 *
 * FINDING - the visible "Login" text and the "Set Base URL" text are both Compose {@code TextView}s
 * that themselves report {@code clickable="false"}; the actual tappable element is their parent
 * {@code android.view.View}, which reports {@code clickable="true"}. Clicking the TextView directly
 * silently no-ops (confirmed live: the first exploration pass typed devmode/please and tapped the
 * "Login" TextView with zero effect - the screen never changed). All button-style elements here are
 * therefore located by their clickable ANCESTOR View, not the visible text node itself.
 *
 * <p><b>LIVE-RUN BUG FOUND AND FIXED (2026-10-16):</b> {@code emailInput}/{@code passwordInput} were
 * originally unscoped {@code (//android.widget.EditText)[1]}/{@code [2]} - "the Nth EditText anywhere
 * on screen." The Site Selection screen (a DIFFERENT screen entirely, reached after a successful
 * login) also has exactly one EditText (its search box, confirmed via
 * {@code docs/dom-snapshots/onboardingauthentication_08_after_qa_login_submit.xml}: resource-id
 * {@code mainConstraintLayout}, not {@code loginRootContainer}). A test with a race condition
 * (TC_FUNC_AUTH_016 force-kill-mid-submission: the real login sometimes completes and reaches Site
 * Selection before the "mid-submission" kill lands) caused a retry's {@code enterCredentialsWithoutSubmitting}
 * to blindly type the QA login email into Site Selection's search box, since the unscoped xpath
 * matched whatever the first EditText on screen happened to be. Fixed by scoping both locators inside
 * {@code loginRootContainer} - the Login activity's own root container resource-id, confirmed present
 * on the Login screen and absent from Site Selection - so they can now only ever match on the actual
 * Login screen. {@link com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage#login}
 * and friends also now call {@code isLoginScreenDisplayed()} as a precondition guard before typing
 * anything, failing loudly instead of silently mis-typing if the app isn't actually on Login.
 */
public class OnboardingAuthenticationPageObjectRepository extends Object_Base {

    public OnboardingAuthenticationPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // --- Login screen (also reused for the devmode/please trigger - same two fields) ---
    // Scoped to loginRootContainer (the Login activity's own root resource-id) - see the class-level
    // "LIVE-RUN BUG FOUND AND FIXED" note above for why an unscoped xpath is unsafe here.

    private static final String LOGIN_SCREEN_ROOT =
            "//*[@resource-id='com.cresanmanagement.eyeq:id/loginRootContainer']";

    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT + "//android.widget.EditText[1]")
    public WebElement emailInput;

    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT + "//android.widget.EditText[2]")
    public WebElement passwordInput;

    /** Presence check used as a precondition guard before any login-field typing (see class javadoc). */
    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT)
    public WebElement loginScreenRoot;

    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT + "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Login']]")
    public WebElement loginButton;

    // Confirmed live (2026-10-16): tapping this clickable View toggles the password EditText's
    // displayed text between masked dots and the real plain-text value. The sibling non-clickable
    // View at the same EditText (bounds [74,880][414,937] on the verified build) is a decoy/underline
    // element, not the toggle - this xpath's [@clickable='true'] filter excludes it.
    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT + "//android.widget.EditText[2]//android.view.View[@clickable='true']")
    public WebElement passwordVisibilityToggle;

    // Generic backend-rejection error - confirmed live to appear identically for BOTH a registered
    // email with the wrong password and a wholly unregistered email (TC_FUNC_AUTH_005/006/TC_GEN_SECX_011
    // all produce this exact text) - i.e. the app does not disclose which field was wrong or whether the
    // account exists, a deliberate finding worth asserting, not just incidental.
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='The email or password you entered was incorrect - please try again']")
    public WebElement genericLoginErrorMessage;

    // Client-side email-format validation - confirmed live to fire for an excessively long email, a
    // Unicode/emoji email, and a SQL-injection-style string in the email field (TC_FUNC_AUTH_011/013,
    // TC_GEN_FORM_012) - this is new, previously-unconfirmed behavior for this build (RequirementAnalysis
    // Section 16/20 had flagged inline email-format validation as unconfirmed; it is now confirmed
    // present, at least for these malformed-input shapes).
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Make sure you enter a valid email address']")
    public WebElement emailFormatErrorMessage;

    // --- Developer Mode dialog (devmode/please environment-switch trigger - FR-AUTH-011) ---
    // Real confirmed text: "Developer Mode" title, 5 radio rows (Test/Dev/Staging/Production/
    // Other-Custom), and a "Set Base URL" action - NOT "Set URL" as colloquially described; the
    // literal on-screen text is "Set Base URL".

    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'Test (')]]")
    public WebElement testEnvironmentRadioRow;

    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Set Base URL']]")
    public WebElement setBaseUrlButton;

    // --- Native "Success" confirmation AlertDialog shown after Set Base URL (real resource-ids -
    // this part of the flow is a plain Android AlertDialog, not Compose, hence real IDs exist here) ---

    @AndroidFindBy(id = "com.cresanmanagement.eyeq:id/alertConfirmButton")
    public WebElement environmentSwitchSuccessOkButton;

    // --- Native OS runtime-permission dialog ("Allow EyeQ to send you notifications?") - confirmed
    // live to appear after a successful login reaches Site Selection on Android 13+, despite
    // InitiateDriver's autoGrantPermissions=true capability. Package is the OS permission controller,
    // not this app, but @AndroidFindBy's id() attribute works for any resource-id regardless of
    // owning package. ---

    @AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
    public WebElement notificationPermissionAllowButton;

    // --- Post-login landing screen confirmation (Site Selection - see RequirementAnalysis.md
    // Section 7.1: "first run" branch). Every session in this framework is effectively first-run,
    // since config.properties' NoReset=true only skips WIPING app data across sessions within one
    // suite run - it does not fabricate a prior "remembered device" login state for a fresh QA
    // account, confirmed live: the Login form was observed blank on a brand-new session even with
    // NoReset=true. ---

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Site Selection']")
    public WebElement siteSelectionTitle;

    // --- No-connectivity gate (TC_FUNC_AUTH_015) - confirmed live on real hardware (Pixel 3 XL,
    // Android 12, 2026-10-16): the app does not show an inline error on the Login screen when
    // offline - it navigates to a dedicated, separate activity
    // (com.cresanmanagement.eyeq.views.misc.NoInternetActivity, confirmed via `adb shell dumpsys
    // window`'s mCurrentFocus) displaying this exact text, and does so promptly/app-wide rather than
    // only in reaction to a specific network call like tapping Login - confirmed by observing the
    // transition happen immediately after enabling airplane mode, before any login attempt. ---

    @AndroidFindBy(xpath = "//*[@text='There is no internet connection.']")
    public WebElement noInternetConnectionMessage;

    // --- Post-login biometric-enrollment offer - confirmed live on real hardware (Pixel 3 XL, Android
    // 12, 2026-10-16): appears IMMEDIATELY after tapping Login with correct credentials, on EVERY
    // successful login (not a one-time/first-login-only prompt, and not scoped to the Biometric test
    // cluster - it blocks the path to Site Selection for every login-dependent test unless dismissed).
    // This is an in-app screen (part of this app's own UI, not a system dialog/permission prompt),
    // activity com.cresanmanagement.eyeq.views.onboarding.FaceIdActivity (confirmed via `adb shell
    // dumpsys window`'s mCurrentFocus - the app's own code names this "FaceId" even though the
    // on-screen copy says "Fingerprint", the same iOS/Android terminology mismatch already tracked in
    // RequirementAnalysis.md Section 11/23), titled "Fingerprint Available" with two buttons: "Enable
    // Fingerprint" (primary) and "Not this time" (secondary) - confirmed via a live `uiautomator dump`
    // (docs/dom-snapshots/current_screen_check.xml, captured 2026-10-16).
    //
    // LIVE-RUN BUG FOUND AND FIXED (2026-10-16, same pattern as the Login screen's TextView-vs-View
    // issue above): the dump confirms "Not this time" is itself a non-interactive
    // android.widget.TextView (clickable="false") inside a clickable="true" android.view.View ancestor
    // (bounds [72,2512][1368,2621] on the verified build) - an unscoped `//*[@text='Not this time']`
    // xpath matches the TextView, not its clickable parent, and per the already-confirmed finding for
    // this app's Compose screens, Appium's element.click() silently no-ops on a clickable="false" node
    // rather than bubbling the tap to the ancestor. Fixed by locating the clickable ancestor View
    // directly, exactly like loginButton/setBaseUrlButton/testEnvironmentRadioRow above. Dismissed via
    // "Not this time" since these tests are not exercising biometric enrollment. ---

    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Not this time']]")
    public WebElement fingerprintAvailableNotThisTimeButton;

    // =================================================================================================
    // LIVE-VERIFIED 2026-10-16 (real device 8AHY0LBTH, Pixel 3 XL) — the locators below were originally
    // written in code-writing-only mode (desk-sourced from RequirementAnalysis.md/UITestCases.md only)
    // and have since been corrected against real driver.getPageSource() dumps captured via a throwaway
    // exploration harness (OnboardingExplorer.java, scratchpad-only, not part of this deliverable). Every
    // correction below replaced an assumption that turned out to be wrong when actually inspected - see
    // each locator's comment for exactly what changed and why. Raw dom-snapshots are persisted under
    // docs/dom-snapshots/onboardingauthentication_live_*.xml for traceability, same convention as the
    // Login screen's original verification pass.
    // =================================================================================================

    // --- Login screen navigation links (still on the Login screen itself, scoped to LOGIN_SCREEN_ROOT
    // like the rest of that screen's confirmed locators) ---

    /**
     * LIVE-VERIFIED CORRECTION: the real on-screen text is "Forgot password?" (lowercase "p", trailing
     * "?") - NOT "Forgot Password" as assumed from the functional spec's own casing. More importantly,
     * this TextView is ITSELF {@code clickable="true"} - unlike every other button on this screen
     * (Login, Set Base URL, SSO), it is NOT wrapped in a separate clickable ancestor View. Confirmed via
     * {@code docs/dom-snapshots/onboardingauthentication_live_00_fresh_login_screen.xml}.
     */
    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT + "//android.widget.TextView[@text='Forgot password?']")
    public WebElement forgotPasswordLink;

    /**
     * LIVE-VERIFIED CORRECTION: text "Activate Account" was correctly guessed, but like
     * {@link #forgotPasswordLink} this TextView is directly {@code clickable="true"} - NOT wrapped in a
     * clickable ancestor View as originally assumed. Confirmed via the same dom-snapshot as above.
     */
    @AndroidFindBy(xpath = LOGIN_SCREEN_ROOT + "//android.widget.TextView[@text='Activate Account']")
    public WebElement activateAccountLink;

    // --- Remembered Login / Quick Re-auth (FR-AUTH-003) — RequirementAnalysis.md lines 115/169/193 ---
    //
    // LIVE-VERIFIED FINDING (2026-10-16): this entire cluster's precondition was tested directly - log in
    // successfully with the QA account, force-stop the app, relaunch it WITHOUT clearing app data (i.e.
    // the one plausible mechanism this framework could use to reach a "remembered device" state). Result:
    // the app resumes DIRECTLY to Site Selection (confirmed via
    // docs/dom-snapshots/onboardingauthentication_live_21_after_forcekill_relaunch_no_clear.xml) - a
    // continued logged-in session, NOT a Login screen with a locked/pre-filled email. None of "Not you?",
    // "Fingerprint", or "EyeQ Cloud VMS" text was present anywhere on the resulting screen. This
    // confirms (not just repeats) the earlier suspicion: force-kill+relaunch-without-clearing is NOT the
    // mechanism that produces this app's "remembered device" Login variant. The locators below remain
    // unverified structurally (never actually seen on screen) - they are kept as-written for whenever the
    // real mechanism (most likely an explicit in-app "Log Out" action that logs out but leaves the device
    // registered, as opposed to a full clearAppData/pm-clear reset - probably exposed in the
    // not-yet-built/tested Account Settings module) is identified and confirmed.

    /** UNVERIFIED (precondition unreachable - see note above) — text "Not you?" confirmed only as a navigation-label string in UITestCases.md TC_UI_AUTH_021; exact element type/ancestor structure never inspected live. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Not you?']]")
    public WebElement notYouButton;

    /** UNVERIFIED (precondition unreachable - see note above) — RequirementAnalysis.md lines 115/169/193: SSO is disabled/unavailable on a remembered device. Control text per PRD is "Login with EyeQ Cloud VMS" (RequirementAnalysis.md line 83/120, and confirmed live present/enabled on the ordinary (non-remembered) Login screen's own SSO button). Used here ONLY to assert absence/disabled state for TC_FUNC_AUTH_025 — SSO itself (FR-AUTH-004) remains explicitly out of scope per FunctionalTestCases.md's exclusion list (Section 1, line 11). */
    @AndroidFindBy(xpath = "//android.view.View[.//android.widget.TextView[contains(@text,'EyeQ Cloud VMS')]]")
    public WebElement ssoLoginButton;

    /** UNVERIFIED (precondition unreachable - see note above) — RequirementAnalysis.md line 115 says a remembered device "offers Login with Fingerprint", but that phrase is RequirementAnalysis's own paraphrase, not quoted UI copy — the real on-screen label text is unconfirmed. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'Fingerprint')]]")
    public WebElement loginWithFingerprintButton;

    // --- Forgot Password - Email Entry (FR-AUTH-005) — live-verified screen title "Reset Password",
    // subtitle "Enter your email address and we'll send you a code to reset your password." ---

    /**
     * LIVE-VERIFIED: confirmed to be the sole EditText on this screen (hint "Enter your email"), exactly
     * as assumed. Still deliberately NOT scoped to a root container since none exists on this screen
     * (confirmed: no distinguishing resource-id anywhere in its hierarchy) - the collision risk this
     * screen shares with Site Selection's single search box (same class of bug already found once on
     * Login) is a real, still-open risk if a test ever reaches this screen from an unexpected prior
     * state; it has simply not been triggered in the flows exercised so far. Confirmed via
     * docs/dom-snapshots/onboardingauthentication_live_02_forgot_password_screen.xml.
     */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement forgotPasswordEmailInput;

    /** LIVE-VERIFIED: exact text "Send Code" and the clickable-ancestor-View pattern were both confirmed correct as originally written, on both the Forgot Password AND Activate Account email-entry screens (identical locator reused for both, confirmed independently on each). */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Send Code']]")
    public WebElement sendCodeButton;

    /** LIVE-VERIFIED: exact text "Back to Login" and the clickable-ancestor-View pattern were both confirmed correct, on the Forgot Password and Activate Account email-entry screens, and on the Contact Support overlay. NOTE: the New Password / Create Your Password screens use a DIFFERENT, plain "Back" control instead - see {@link #backButtonPlain} below; do not reuse this locator there. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Back to Login']]")
    public WebElement backToLoginButton;

    // --- OTP Verification (FR-AUTH-006) — shared screen structure for both flows: title
    // "Authentication", subtitle "Enter the 6-digit code sent to your email", field label "One-Time
    // Password". CRITICAL CONFIRMED FINDING: the Test backend accepts ANY syntactically-valid 6-digit
    // code (confirmed with both "000000" and "111111", independently, via BOTH the Forgot Password and
    // Activate Account flows, each on a freshly pm-cleared session with a freshly-requested code) - there
    // is no real OTP-correctness check in this environment. This resolves GapAnalysis.md GAP-ENV-01 for
    // every case that was gated on it (see OnboardingAuthenticationPage's now-removed
    // requireOtpCaptureCapability() gate). Cross-account isolation (whether Account B's code would be
    // rejected for Account A) could NOT be tested - no second QA account is provisioned (see
    // onboardingauthentication.csv's SecondAccountB row) - this remains an open, flagged security
    // question, not confirmed either way. ---

    /**
     * LIVE-VERIFIED CORRECTION: this is a SINGLE EditText accepting up to 6 characters - NOT 6 separate
     * digit boxes as the Figma-derived UITestCases.md description ("all 6 boxes... evenly sized")
     * suggested. Confirmed: typing an 8-character attempt results in exactly 6 characters retained
     * (capped), and typing non-numeric characters ("AB!@") results in an EMPTY field (silently rejected,
     * not partially accepted). Confirmed via
     * docs/dom-snapshots/onboardingauthentication_live_fmt_04_overlong_attempt.xml and
     * _live_fmt_05_nonnumeric_attempt.xml.
     */
    @AndroidFindBy(xpath = "//android.widget.EditText")
    public WebElement otpInput;

    /**
     * LIVE-CONFIRMED (2026-10-19): the exact, OTP-screen-only title text "Authentication" - used as the
     * screen-identity signal INSTEAD of {@link #otpInput} for presence/absence checks, after discovering
     * live that {@code otpInput}'s unscoped {@code //android.widget.EditText} xpath is unsuitable for an
     * ABSENCE check: Appium's PageFactory proxy re-resolves that xpath fresh on every call, and both
     * adjacent screens (Forgot Password's email entry, and the New Password screen reached after
     * Submit) ALSO have at least one EditText - so "wait for otpInput to become invisible" never
     * completed (ran the full 90s timeout, confirmed via two live test failures) because the proxy kept
     * finding a DIFFERENT screen's EditText and reporting it as still displayed. This title text is
     * confirmed unique to this screen among every screen inspected in this module.
     */
    @AndroidFindBy(xpath = "//*[@text='Authentication']")
    public WebElement otpScreenTitle;

    /**
     * LIVE-VERIFIED CORRECTION: the submit button's text differs by flow - "Submit and Reset Password"
     * on the Forgot Password flow, but plain "Submit" on the Activate Account flow (confirmed via
     * docs/dom-snapshots/onboardingauthentication_live_act_01_after_send_code.xml). Matched here via
     * {@code contains()} on the shared "Submit" substring so one locator covers both flows.
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[contains(@text,'Submit')]]")
    public WebElement otpSubmitButton;

    /**
     * LIVE-VERIFIED CORRECTION: there is no "Incorrect code..." error text anywhere in this build - that
     * assumption cannot even be exercised, because (per the CRITICAL CONFIRMED FINDING above) the Test
     * backend never actually rejects a syntactically-valid code as incorrect. The real, confirmed
     * client-side validation message, which DOES fire for a blank or partial (fewer-than-6-digit) OTP
     * entry, is "Enter the complete 6-digit code." - confirmed via
     * docs/dom-snapshots/onboardingauthentication_live_fmt_01_blank_submit.xml and
     * _live_fmt_03_after_partial_submit.xml.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Enter the complete 6-digit code.']")
    public WebElement otpIncompleteCodeError;

    /**
     * LIVE-VERIFIED CORRECTION: the real control text is "Didn't receive the code?" - NOT "Resend Code
     * (0:29)" as the (apparently stale/earlier-iteration) Figma-derived UITestCases.md description
     * assumed. Confirmed via docs/dom-snapshots/onboardingauthentication_live_04_after_send_code_tap.xml.
     * NOTE: whether tapping it reveals a distinct "Resend"/cooldown-timer sub-control, or resends
     * immediately itself, remains UNCONFIRMED - a tooling limitation (the embedded apostrophe in "Didn't"
     * breaks naive XPath string-literal construction; this needs a properly quote-escaped XPath or a
     * different locator strategy to actually click it) prevented testing the tap's effect in this pass.
     * TC_FUNC_AUTH_044/045/051 (resend cooldown behavior) remain UNVERIFIED pending that follow-up.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'receive the code')]")
    public WebElement otpResendLink;

    // otpBackButton intentionally removed: no "Back"/"Back to Login" text was found anywhere on the OTP
    // screen itself in the live dumps (only "Didn't receive the code?" appears below the OTP field) -
    // TC_FUNC_AUTH_049's "back/navigation-back control" most likely refers to the Android system back
    // gesture/button (driver.navigate().back(), already available as Base#navigateBack()), not an
    // in-app element. OnboardingAuthenticationPage#tapOtpBack() now uses that instead of a locator.

    // --- Set New Password (Reset Password flow) / Create Your Password (Activate Account flow)
    // (FR-AUTH-007) — live-verified: Reset-flow title "Reset Password"; Activate-flow title "Create Your
    // Password" + subtitle "Set a strong password to protect your account and start using EyeQ
    // Monitoring.". Both flows share: field 1 label "New Password" (hint "Enter new password"), the same
    // 5-item rule checklist (ALWAYS visible/static text, not a post-submit error - see
    // otpIncompleteCodeError's sibling note below), field 2 label "Confirm New Password" (hint "Re-enter
    // new password"). Confirmed via docs/dom-snapshots/onboardingauthentication_live_07_after_didnt_receive_tap.xml
    // (Reset flow) and _live_act_03_after_submit_000000.xml (Activate flow). ---

    /** LIVE-VERIFIED: confirmed first of two password EditTexts on this screen, exactly as assumed. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement newPasswordInput;

    /** LIVE-VERIFIED: confirmed second of two password EditTexts, exactly as assumed. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[2]")
    public WebElement confirmPasswordInput;

    /** LIVE-VERIFIED CORRECTION: the Reset Password flow's real submit-button text is "Confirm Password Reset" - NOT the placeholder "Submit" guess. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Confirm Password Reset']]")
    public WebElement confirmPasswordResetButton;

    /**
     * LIVE-VERIFIED CORRECTION: the Activate Account flow's real submit-button text is "Confirm New
     * Password" - the exact SAME text as this screen's field-2 label above it. This is very likely the
     * concrete instance of the "duplicate button/field label defect" that TC_FUNC_AUTH_064 (currently
     * TBD in FunctionalTestCases.md pending exactly this kind of confirmation) was looking for - worth
     * flagging back for that case to be resolved, even though it is not itself in this pass's scope.
     */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Confirm New Password']]")
    public WebElement confirmNewPasswordButton;

    /** LIVE-VERIFIED CORRECTION: this screen's back control reads plain "Back" (NOT "Back to Login" like the email-entry screens) - confirmed on both the Reset and Activate flow variants. */
    @AndroidFindBy(xpath = "//android.view.View[@clickable='true'][.//android.widget.TextView[@text='Back']]")
    public WebElement backButtonPlain;

    /**
     * LIVE-VERIFIED CORRECTION and mechanism finding: these 5 TextViews are an ALWAYS-VISIBLE static
     * checklist (confirmed present even before any text is typed), not a validation message that
     * appears only on invalid submission - RequirementAnalysis.md's "rule-specific validation message"
     * assumption does not match the real UI. Their own {@code selected}/{@code checked}/
     * {@code content-desc} attributes do NOT change between a satisfied and unsatisfied state (confirmed
     * by comparing a fresh screen, a password missing only the uppercase rule, and an all-rules-satisfied
     * password - all three showed identical attribute values on these 5 elements), so they cannot be used
     * to assert per-rule pass/fail directly via UiAutomator. The real, CONFIRMED automatable signal is
     * {@link #confirmPasswordResetButton}/{@link #confirmNewPasswordButton}'s own {@code enabled}
     * attribute: false when ANY rule is unsatisfied, true only once ALL 5 are satisfied (confirmed
     * live: false on a fresh screen, false with an 11-rule-of-5-satisfying "missing uppercase only"
     * password, true once corrected to satisfy all 5) - see
     * {@code OnboardingAuthenticationPage#isSetPasswordSubmitEnabled()}.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='At least 10 characters required' or "
            + "@text='At least 6 distinct characters' or @text='Uppercase letter' or "
            + "@text='Lowercase letter' or @text='At least one special character']")
    public java.util.List<WebElement> passwordRuleChecklistItems;

    // --- Activate Account (FR-AUTH-008) — live-verified screen title "Activate account" (lowercase
    // "a"), subtitle "Enter your email address and we'll send you a code to activate your account." ---

    /** LIVE-VERIFIED: confirmed sole EditText on this screen, same as forgotPasswordEmailInput. */
    @AndroidFindBy(xpath = "(//android.widget.EditText)[1]")
    public WebElement activateAccountEmailInput;

    // activateAccountSubmitButton deliberately reuses sendCodeButton above - LIVE-CONFIRMED: the real
    // button text on THIS screen genuinely is "Send Code" (RequirementAnalysis.md's "assumed by analogy"
    // caveat is now resolved/confirmed correct, resolving TC_FUNC_AUTH_075 too). Confirmed via
    // docs/dom-snapshots/onboardingauthentication_live_11_activate_account_screen.xml.
    //
    // ALSO LIVE-CONFIRMED: submitting the QA account's own email (an already-active, NOT genuinely
    // "pending activation" account) through THIS screen's Send Code still proceeded all the way through
    // OTP Verification to the Create Your Password screen without any activation-eligibility rejection -
    // i.e. this Test backend does not appear to validate activation-pending status either. This removes
    // the practical blocker TC_FUNC_AUTH_071 was gated on (no provisioned pending-activation account) -
    // any syntactically valid, already-known email works for exercising this screen's happy path in this
    // environment, though this is a Test-backend-leniency finding worth flagging, not necessarily the
    // production behavior.

    // --- Contact Support (Login footer) (FR-AUTH-010) — live-verified overlay content: title "Contact
    // Support", body "Our team is here to help. Reach out to us through any of the methods below:" ---

    /**
     * LIVE-VERIFIED CORRECTION (important structural finding): "contact support" is NOT a separately
     * clickable element at all - it is a {@code ClickableSpan} embedded inside the single, otherwise
     * NON-clickable footer TextView ({@code clickable="false"}, but {@code text-has-clickable-span="true"}),
     * whose full text is "Please contact support for access or assistance." A normal {@code element.click()}
     * on this TextView (which taps its bounds' center) does NOT reliably land on the span - confirmed
     * live that it silently no-ops when the whole-element center is tapped. The only way found to open the
     * overlay was tapping specific COORDINATES estimated from the span's substring position within the
     * full sentence's bounds - this cannot be expressed as a normal {@code @AndroidFindBy} locator/click,
     * so {@code OnboardingAuthenticationPage#openContactSupportFromLoginFooter()} computes and taps
     * coordinates directly instead of calling {@code clickWithHeal} on this element. Confirmed working via
     * docs/dom-snapshots/onboardingauthentication_live_32_after_coord_tap.xml.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'contact support')]")
    public WebElement contactSupportLoginFooterText;

    /**
     * CORRECTED 2026-10-27: this was hardcoded to {@code contains(@text,'678-6555')} based on a
     * 2026-10-20 finding that the Login-footer overlay's phone number ("(888) 678-6555") genuinely
     * differed from the Support tab's ("+1 888-675-6555") - a real, confirmed cross-screen data
     * discrepancy (see SupportTests.java TC_FUNC_SUP_007's defect-confirmation test). A live crawl
     * confirmed that stale hardcoded value no longer matches anything: the overlay's phone number is
     * now "+1 888-675-6555" - IDENTICAL to the Support tab's, meaning the underlying data discrepancy
     * appears to have been fixed since. Hardcoding either value into the locator is fragile/
     * self-fulfilling by design (it can only ever find an element whose exact value was already
     * assumed) - replaced with a structural locator (the TextView immediately preceding the confirmed,
     * stable "support@eyeqmonitoring.com" email row) so this keeps working regardless of what the
     * phone number's digits are at any given time, and TC_FUNC_SUP_007's own runtime comparison - not
     * this locator - is what should detect whether the two screens' values ever diverge again.
     */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='support@eyeqmonitoring.com']/preceding-sibling::android.widget.TextView[1]")
    public WebElement contactSupportPhoneNumberText;

    /** LIVE-VERIFIED: the Login-footer overlay's email genuinely is "support@eyeqmonitoring.com" - same cross-screen confirmation as above. */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'support@eyeqmonitoring.com')]")
    public WebElement contactSupportEmailText;

    // contactSupportBackToLoginButton deliberately reuses backToLoginButton above - LIVE-CONFIRMED exact
    // text "Back to Login" closes this specific overlay (the overlay renders on TOP of the still-present
    // Login screen rather than navigating away from it, confirmed by the dump showing both the overlay's
    // and the Login screen's content together).
}
