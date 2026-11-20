package com.cresanmanagement.eyeq;

import com.cresanmanagement.eyeq.pageobjects.accountsettings.AccountSettingsPage;
import com.cresanmanagement.eyeq.pageobjects.global.GlobalPage;
import com.cresanmanagement.eyeq.pageobjects.licenseplate.LicensePlatePage;
import com.cresanmanagement.eyeq.pageobjects.mainappshell.MainAppShellPage;
import com.cresanmanagement.eyeq.pageobjects.onboardingauthentication.OnboardingAuthenticationPage;
import com.cresanmanagement.eyeq.pageobjects.sitemanagement.SiteManagementPage;
import com.cresanmanagement.eyeq.pageobjects.support.SupportPage;
import com.cresanmanagement.eyeq.pageobjects.virtualguard.VirtualGuardPage;

/**
 * Central Page Object registry. Test classes use {@code Pages.<Module>Page()} - never
 * {@code new <Module>Page()} directly - so driver wiring stays centralized in one place.
 *
 * One static factory method per module identified in 07-outputs/TestCases/FunctionalTestCases.md
 * Section 1 Coverage Summary (8 modules).
 */
public class Pages {

    private static <T> T getPage(Class<T> pageType) {
        try {
            return pageType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate page object: " + pageType.getName(), e);
        }
    }

    public static OnboardingAuthenticationPage OnboardingAuthenticationPage() {
        return getPage(OnboardingAuthenticationPage.class);
    }

    public static SiteManagementPage SiteManagementPage() {
        return getPage(SiteManagementPage.class);
    }

    public static MainAppShellPage MainAppShellPage() {
        return getPage(MainAppShellPage.class);
    }

    public static VirtualGuardPage VirtualGuardPage() {
        return getPage(VirtualGuardPage.class);
    }

    public static LicensePlatePage LicensePlatePage() {
        return getPage(LicensePlatePage.class);
    }

    public static GlobalPage GlobalPage() {
        return getPage(GlobalPage.class);
    }

    public static SupportPage SupportPage() {
        return getPage(SupportPage.class);
    }

    public static AccountSettingsPage AccountSettingsPage() {
        return getPage(AccountSettingsPage.class);
    }
}
