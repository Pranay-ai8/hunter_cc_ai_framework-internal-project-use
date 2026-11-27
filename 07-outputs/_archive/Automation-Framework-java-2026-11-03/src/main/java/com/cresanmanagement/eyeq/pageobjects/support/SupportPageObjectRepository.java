package com.cresanmanagement.eyeq.pageobjects.support;

import com.cresanmanagement.eyeq.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Locators only. Android-only project: @AndroidFindBy only.
 *
 * <p><b>LIVE-VERIFIED 2026-10-20</b> (real device {@code 8BQX1DY81}, confirmed Test backend, site
 * "Amalthea") via {@code docs/dom-snapshots/support_live_00_support_tab.xml}. The Support tab's real
 * content (Jetpack Compose UI) is:
 * <pre>
 * TextView "Support"                              (screen title)
 * TextView "Contact Support"                       (header)
 * TextView "Our team is here to help. Reach out..." (body copy)
 * TextView "+1 888-675-6555"           clickable=true  (Compose UI button pattern "B" - the
 *                                                        TextView itself is directly clickable,
 *                                                        no wrapping ancestor needed - distinct
 *                                                        from the "clickable ancestor wraps a
 *                                                        non-clickable TextView" row pattern used
 *                                                        for lists/menus elsewhere in this app)
 *   View clickable=true [content-desc="Copied!"]  (the phone row's copy-icon control, immediately
 *                                                   following the phone TextView in document order)
 * TextView "support@eyeqmonitoring.com" clickable=true  (same "B" pattern)
 *   View clickable=true [content-desc="Copied!"]  (the email row's copy-icon control)
 * </pre>
 * <b>FINDING:</b> {@code content-desc="Copied!"} is a STATIC accessibility label baked onto each
 * copy-icon control at all times - it is NOT a dynamic post-tap confirmation toast. This dump was
 * captured BEFORE either copy icon was ever tapped and both icons already carry this content-desc.
 * Do not assert on this text appearing/changing after a tap as evidence of a successful copy; verify
 * via the real device clipboard instead ({@code Base#getClipboardText()}).
 *
 * <p><b>No ticket-submission form or any other input control exists anywhere on this screen</b> -
 * confirmed by the full dump containing zero {@code android.widget.EditText} and zero
 * "Submit"/"Send" buttons (TC_FUNC_SUP_005).
 *
 * <p>Both the phone number and the copy icons are positioned relative to each other, not by a stable
 * resource-id (this app does not assign resource-ids to most Compose leaf nodes) - locators below use
 * {@code following-sibling} anchored on the confirmed real text, the same pattern already established
 * project-wide for label/value pairs (see e.g. LicensePlatePageObjectRepository's FIELD_VALUE_XPATH_TEMPLATE).
 */
public class SupportPageObjectRepository extends Object_Base {

    public SupportPageObjectRepository(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Support']")
    public WebElement supportScreenTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Contact Support']")
    public WebElement contactSupportHeader;

    /** LIVE-VERIFIED real phone number. NOTE: differs from the Login-footer overlay's "(888) 678-6555" - see TC_FUNC_SUP_007. */
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'675-6555')]")
    public WebElement supportPhoneNumber;

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'675-6555')]/following-sibling::android.view.View[@clickable='true'][1]")
    public WebElement supportPhoneCopyButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='support@eyeqmonitoring.com']")
    public WebElement supportEmail;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='support@eyeqmonitoring.com']/following-sibling::android.view.View[@clickable='true'][1]")
    public WebElement supportEmailCopyButton;

    /** Used by TC_FUNC_SUP_005 to confirm absence of any ticket-submission form/input control. */
    public static final String ANY_EDIT_TEXT_XPATH = "//android.widget.EditText";

    /** Used by TC_FUNC_SUP_005 to confirm absence of a "Submit"/"Send" ticket-creation control. */
    public static final String SUBMIT_OR_SEND_BUTTON_XPATH =
            "//*[@text='Submit' or @text='Send' or @content-desc='Submit' or @content-desc='Send']";
}
