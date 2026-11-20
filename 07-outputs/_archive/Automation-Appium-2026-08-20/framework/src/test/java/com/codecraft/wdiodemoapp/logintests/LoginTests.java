package com.codecraft.wdiodemoapp.logintests;

import com.codecraft.wdiodemoapp.annotations.TestDataSource;
import com.codecraft.wdiodemoapp.framework.TestBase;
import com.codecraft.wdiodemoapp.Pages;
import com.codecraft.wdiodemoapp.utils.TestDataArgumentsProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;

public class LoginTests extends TestBase {

    private static final String LOGIN_TESTDATA_CSV = "src/test/resources/testdata/login.csv";

    @Test(description = "TC-AUTH-001: Login screen renders expected elements", groups = {"smoke", "regression"})
    public void verifyLoginScreenRendersExpectedElements() {
        Pages.NavigationPage().goToLoginTab();
        Assert.assertTrue(Pages.LoginPage().isLoginScreenDisplayed(), "Login screen should render");
    }

    @Test(description = "TC-AUTH-002: Valid credentials submit successfully", groups = {"smoke", "regression"})
    @TestDataSource(value = LOGIN_TESTDATA_CSV, key = "ValidUser")
    public void verifyValidCredentialsLoginSucceeds(Method testMethod) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(testMethod);
        Pages.NavigationPage().goToLoginTab();
        Pages.LoginPage().enterEmail(Pages.LoginPage().generateRandomEmail());
        Pages.LoginPage().enterPassword(data.get("Password"));
        Pages.LoginPage().tapLoginButton();
        Assert.assertTrue(Pages.LoginPage().isSuccessAlertDisplayed(),
                "A native 'Success' / 'You are logged in!' alert should appear on valid submission");
        Pages.LoginPage().dismissAlert();
    }

    @Test(description = "TC-AUTH-003 (adjusted): Empty field submission shows inline validation messages "
            + "— actual app behavior is inline text ('Please enter a valid email address' / "
            + "'Please enter at least 8 characters'), not a popup alert as the source test case assumed; "
            + "verified live and corrected here.", groups = {"regression"})
    public void verifyEmptyFieldSubmissionShowsValidationMessages() {
        Pages.NavigationPage().goToLoginTab();
        Pages.LoginPage().tapLoginButton();
        Assert.assertTrue(Pages.LoginPage().isValidationMessageDisplayed("valid email address"),
                "Email validation message should be displayed for empty email field");
        Assert.assertTrue(Pages.LoginPage().isValidationMessageDisplayed("at least 8 characters"),
                "Password validation message should be displayed for empty password field");
    }

    @Test(description = "TC-AUTH-004: Invalid-format email shows validation message", groups = {"regression"})
    @TestDataSource(value = LOGIN_TESTDATA_CSV, key = "ValidUser")
    public void verifyInvalidFormatEmailShowsValidationMessage(Method testMethod) {
        Map<String, String> data = TestDataArgumentsProvider.resolve(testMethod);
        Pages.NavigationPage().goToLoginTab();
        Pages.LoginPage().enterEmail("not-an-email");
        Pages.LoginPage().enterPassword(data.get("Password"));
        Pages.LoginPage().tapLoginButton();
        Assert.assertTrue(Pages.LoginPage().isValidationMessageDisplayed("valid email address"),
                "Email validation message should be displayed for malformed email");
    }

    @Test(description = "TC-AUTH-006: Navigate to Sign Up tab from Login", groups = {"regression"})
    public void verifyNavigateToSignUpTab() {
        Pages.NavigationPage().goToLoginTab();
        Pages.LoginPage().openSignUpTab();
        Pages.LoginPage().openLoginTab();
        Assert.assertTrue(Pages.LoginPage().isLoginScreenDisplayed(),
                "Should be able to toggle back to the Login tab after visiting Sign Up");
    }
}
