package com.cresanmanagement.eyeq.pageobjects.accountsettings;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only. Android-only project: @AndroidFindBy only.
 *
 * <p><b>LIVE-VERIFIED 2026-10-20</b> (real device {@code 8AHY0LBTH}, confirmed Test backend, site
 * "Amalthea") via a throwaway crawl ({@code CrawlAccountSettingsLive.java}, deleted after use - see
 * {@code docs/dom-snapshots/acctsettings_live_*.xml}). The entry point (top-bar Profile icon, real
 * {@code content-desc="Profile"}) and the real {@code accountSettingsContainer} resource-id live in
 * {@code MainAppShellPageObjectRepository}/{@code MainAppShellPage} (FR-SHELL-001's own scope); this
 * repository covers only the Account Settings screen's own content.
 *
 * <p>Confirmed real structure:
 * <ul>
 *   <li>Entry list: header (avatar/name/email), then "General Information" / "Security & Access" / "About"
 *       rows (each: a clickable {@code android.view.View} wrapping a non-clickable TextView - the standard
 *       row pattern used project-wide), then a separate "Log out" row below a divider.</li>
 *   <li>General Information: "Profile Avatar" section with body copy ("Upload a clear photo. Supported
 *       formats: JPG, PNG, or WEBP. Max size: 5MB." - matches the documented upload rule), an "Upload Photo"
 *       button and a "Reset" button (disabled when no custom avatar is set), "First Name"/"Last Name"
 *       EditText fields (confirmed real values: First Name "Anupama", Last Name "Mundaragi &gt;alert(hacked)" -
 *       a pre-existing injection-test string already stored in this real QA account, rendered safely as
 *       plain text, not a product of this crawl), a "Time Zone" label (its value control was not reached -
     * genuinely off-screen below the visible scroll position at crawl time), and "Save Changes"/"Cancel"
 *       buttons (both disabled until a field is actually changed).</li>
 *   <li>Tapping "Upload Photo" opens a REAL native AlertDialog titled "Change profile photo" with three
 *       real {@code android.widget.Button}s: "Choose from Gallery", "Take a Photo", "Dismiss". "Choose from
 *       Gallery" genuinely launches the device's Google Photos app via a real intent (confirmed via a
 *       foreground-package change to {@code com.google.android.apps.photos}) - selecting a specific image
 *       from within that external app's own UI was not reliably scriptable within this session's scope
 *       (see AccountSettingsTests' avatar-upload tests for the resulting SkipException gaps).</li>
 *   <li>Security &amp; Access: "Enable Multifactor Authentication" + helper text + a toggle (a
 *       {@code android.view.View[@checkable='true']} immediately following the helper text), "Allow
 *       Fingerprint" + a toggle. Enabling MFA reveals a real "Authentication Method" section with a
 *       "Select" control; tapping it opens a popup listing exactly TWO real options: "Email" and "Text" -
 *       CORRECTION of FunctionalTestCases.md's own framing: the real choice is NOT "only Email", and the
 *       other real option is literally labelled "Text", not "SMS", and no "Authenticator App" option
 *       exists at all. Toggling MFA also causes "Save Changes"/"Cancel" to become enabled (a visible,
 *       real nuance not mentioned in the doc's "immediate confirmation toast, no round-trip" framing -
 *       both can be true: the toggle's own visual state flips immediately, while the screen's shared
 *       Save/Cancel footer still gates persistence of the change).</li>
 *   <li>About: "Application Version" = "0.3.0", "Last Updated Date" = "2026-10-20", "API Environment" =
 *       "Test" (CONFIRMS the documented release-readiness risk for TC_FUNC_ACCT_028 - this build's API
 *       Environment genuinely reads "Test"), and two real links labelled "About EyeQ" and "Privacy Policy"
 *       (not the doc's shorthand "About"/"Privacy").</li>
 *   <li>Log Out: tapping the "Log out" row opens a REAL native AlertDialog titled "Log Out?" with body
 *       "Are you sure you want to log out?" and two real {@code android.widget.Button}s: "Cancel" and
 *       "Log out". Confirmed "Cancel" closes the dialog and leaves Account Settings open/logged in.</li>
 * </ul>
 */
public class AccountSettingsPageObjectRepository extends Object_Base {

    public AccountSettingsPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    // =================================================================================================
    // Entry list (FR-ACCT-001)
    // =================================================================================================

    @AndroidFindBy(xpath = "//android.widget.ImageView[@content-desc='Back']")
    public WebElement backButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'@')]")
    public WebElement profileEmailText;

    public static final String ENTRY_ROW_XPATH_TEMPLATE =
            "//android.widget.TextView[@text='%s']/parent::android.view.View[@clickable='true']";

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='General Information']/parent::android.view.View[@clickable='true']")
    public WebElement generalInformationRow;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Security & Access']/parent::android.view.View[@clickable='true']")
    public WebElement securityAccessRow;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='About']/parent::android.view.View[@clickable='true']")
    public WebElement aboutRow;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Log out']/parent::android.view.View[@clickable='true']")
    public WebElement logOutRow;

    // =================================================================================================
    // General Information (FR-ACCT-002)
    // =================================================================================================

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='General Information']")
    public WebElement generalInformationScreenTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='First Name']/following-sibling::android.widget.EditText[1]")
    public WebElement firstNameField;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Last Name']/following-sibling::android.widget.EditText[1]")
    public WebElement lastNameField;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Time Zone']")
    public WebElement timeZoneLabel;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Upload Photo']/parent::android.view.View")
    public WebElement uploadPhotoButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Reset']/parent::android.view.View")
    public WebElement resetAvatarButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Save Changes']/parent::android.view.View")
    public WebElement saveChangesButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Cancel']/parent::android.view.View")
    public WebElement cancelChangesButton;

    @AndroidFindBy(id = "com.cresanmanagement.eyeq:id/alertTitleTextView")
    public WebElement changeProfilePhotoDialogTitle;

    @AndroidFindBy(xpath = "//android.widget.Button[@text='Choose from Gallery']")
    public WebElement chooseFromGalleryButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@text='Take a Photo']")
    public WebElement takePhotoButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@text='Dismiss']")
    public WebElement dismissPhotoDialogButton;

    // =================================================================================================
    // Security & Access (FR-ACCT-003)
    // =================================================================================================

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Security & Access']")
    public WebElement securityAccessScreenTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Enable Multifactor Authentication']/following::android.view.View[@checkable='true'][1]")
    public WebElement mfaToggle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Allow Fingerprint']/following::android.view.View[@checkable='true'][1]")
    public WebElement fingerprintToggle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Authentication Method']")
    public WebElement authenticationMethodHeader;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Select']")
    public WebElement authenticationMethodSelectControl;

    /** LIVE-VERIFIED real option - the only two that exist are "Email" and "Text" (no "Authenticator App"). */
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Email']")
    public WebElement authMethodOptionEmail;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Text']")
    public WebElement authMethodOptionText;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Authenticator App']")
    public WebElement authMethodOptionAuthenticatorApp_UNCONFIRMED_SHOULD_NOT_EXIST;

    // =================================================================================================
    // About (FR-ACCT-004)
    // =================================================================================================

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Application Version']/following-sibling::android.widget.TextView[1]")
    public WebElement appVersionValue;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Last Updated Date']/following-sibling::android.widget.TextView[1]")
    public WebElement lastUpdatedDateValue;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='API Environment']/following-sibling::android.widget.TextView[1]")
    public WebElement apiEnvironmentValue;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='About EyeQ']/parent::android.view.View")
    public WebElement aboutEyeQLink;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Privacy Policy']/parent::android.view.View")
    public WebElement privacyPolicyLink;

    // =================================================================================================
    // Log Out (FR-ACCT-005)
    // =================================================================================================

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Log Out?']")
    public WebElement logOutConfirmDialogTitle;

    @AndroidFindBy(xpath = "//android.widget.Button[@text='Cancel']")
    public WebElement logOutDialogCancelButton;

    @AndroidFindBy(xpath = "//android.widget.Button[@text='Log out']")
    public WebElement logOutDialogConfirmButton;
}
