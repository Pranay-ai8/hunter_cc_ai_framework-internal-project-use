package com.codecraft.saucedemo.tests.login;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;

/**
 * Module: Authentication — source: 07-outputs/TestCases/FunctionalTestCases.md
 * Target: https://www.saucedemo.com/ (live, public application)
 */
public class LoginTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/login.csv";

    @DataProvider(name = "loginData")
    public Object[][] loginData(Method method) {
        return com.codecraft.saucedemo.utils.TestDataArgumentsProvider.singleRow(CSV, method);
    }

    // TC_LOGIN_001
    @Test(dataProvider = "loginData", groups = {"smoke", "regression"})
    public void validLoginRedirectsToInventory(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "User should be redirected to the Inventory page");
        ExtentReportManager.pass("Login successful, redirected to Inventory");
    }

    // TC_LOGIN_002
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void lockedOutUserIsBlocked(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.softAssertTrue(loginPage.isErrorDisplayed(), "Lock-out error should be visible");
        AssertionUtil.softAssertTrue(loginPage.getErrorText().contains("Sorry, this user has been locked out."),
                "Error text should match documented lock-out message");
        AssertionUtil.assertAll();
    }

    // TC_LOGIN_003
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void blankUsernameShowsRequiredError(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.getErrorText().contains("Username is required"),
                "Should show 'Username is required.'");
    }

    // TC_LOGIN_004
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void blankPasswordShowsRequiredError(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.getErrorText().contains("Password is required"),
                "Should show 'Password is required.'");
    }

    // TC_LOGIN_005
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void unknownCredentialsRejected(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(
                loginPage.getErrorText().contains("Username and password do not match any user in this service"),
                "Should show the documented mismatch error");
    }

    // TC_LOGIN_006
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void performanceGlitchUserLoginHasDelay(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        long start = System.currentTimeMillis();
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Should eventually reach Inventory page");
        long elapsedMs = System.currentTimeMillis() - start;
        AssertionUtil.assertTrue(elapsedMs > 3000,
                "performance_glitch_user should be measurably slower (~5s documented delay), took " + elapsedMs + "ms");
    }

    // TC_LOGIN_007
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void problemUserLogsInSuccessfully(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(),
                "problem_user login itself should succeed (defects manifest post-login — see InventoryTest/CartTest/CheckoutTest)");
    }

    // TC_LOGIN_008
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void errorUserLogsInSuccessfully(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "error_user login itself should succeed");
    }

    // TC_LOGIN_009
    @Test(dataProvider = "loginData", groups = {"regression"})
    public void visualUserLogsInSuccessfully(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "visual_user login itself should succeed");
    }

    // TC_LOGIN_010 — Automation Candidate: Partial (GAP-FR-03). A real 10-minute idle
    // wait is impractical for CI; kept as a real, runnable test excluded from
    // smoke/regression groups. Uses Selenium 4's FluentWait for bounded polling
    // (never Thread.sleep() — per 03-standards/selenium-standards.md "Waiting Strategy",
    // FluentWait's internal poll interval is the explicitly endorsed mechanism for this).
    // TODO: requires manual setup — run standalone; takes ~10 minutes wall-clock time.
    @Test(dataProvider = "loginData", groups = {"partial"})
    public void sessionAutoExpiresAfterTenMinutes(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: standard_user should reach Inventory");

        WebDriver driver = driver();
        boolean expired;
        try {
            expired = new FluentWait<>(driver)
                    .withTimeout(Duration.ofMinutes(10).plusSeconds(30))
                    .pollingEvery(Duration.ofSeconds(30))
                    .ignoring(NoSuchElementException.class)
                    .until(d -> {
                        d.navigate().refresh();
                        return !d.getCurrentUrl().contains("/inventory.html");
                    });
        } catch (org.openqa.selenium.TimeoutException e) {
            expired = false;
        }
        AssertionUtil.assertTrue(expired,
                "Session should have expired after 10 minutes of inactivity, requiring re-authentication (AS-01)");
    }
}
