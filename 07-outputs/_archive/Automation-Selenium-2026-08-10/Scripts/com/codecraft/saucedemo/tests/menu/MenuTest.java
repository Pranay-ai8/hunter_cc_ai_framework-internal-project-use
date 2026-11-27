package com.codecraft.saucedemo.tests.menu;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.inventory.InventoryPage;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.pages.web.menu.MenuPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Module: Navigation / Hamburger Menu — source: 07-outputs/TestCases/FunctionalTestCases.md
 */
public class MenuTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/menu.csv";

    @DataProvider(name = "menuData")
    public Object[][] menuData(Method method) {
        return TestDataArgumentsProvider.singleRow(CSV, method);
    }

    private InventoryPage loginAndReachInventory(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: login should reach Inventory page");
        return new InventoryPage(driver());
    }

    // TC_MENU_001
    @Test(dataProvider = "menuData", groups = {"regression"})
    public void hamburgerMenuDisplaysAllExpectedLinks(Map<String, String> data) {
        loginAndReachInventory(data);
        MenuPage menuPage = new MenuPage(driver());
        menuPage.openMenu();
        AssertionUtil.assertTrue(menuPage.isMenuOpen(), "Menu should be open");
        List<String> labels = menuPage.getMenuItemLabels();
        AssertionUtil.softAssertTrue(labels.contains("All Items"), "Menu should contain 'All Items'");
        AssertionUtil.softAssertTrue(labels.contains("About"), "Menu should contain 'About'");
        AssertionUtil.softAssertTrue(labels.contains("Logout"), "Menu should contain 'Logout'");
        AssertionUtil.softAssertTrue(labels.contains("Reset App State"), "Menu should contain 'Reset App State'");
        AssertionUtil.assertAll();
    }

    // TC_MENU_002
    @Test(dataProvider = "menuData", groups = {"regression"})
    public void allItemsNavigatesToInventory(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.openCart();
        MenuPage menuPage = new MenuPage(driver());
        menuPage.openMenu();
        menuPage.clickAllItems();
        InventoryPage backOnInventory = new InventoryPage(driver());
        AssertionUtil.assertTrue(backOnInventory.isOnInventoryPage(), "'All Items' should navigate to the Inventory page");
    }

    // TC_MENU_003
    @Test(dataProvider = "menuData", groups = {"regression"})
    public void resetAppStateClearsCartWithoutLogout(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.addToCartByName(data.get("ProductName"));
        AssertionUtil.assertEquals(inventoryPage.getCartBadgeCount(), 1, "Precondition: cart should have 1 item");
        MenuPage menuPage = new MenuPage(driver());
        menuPage.openMenu();
        menuPage.clickResetAppState();
        AssertionUtil.softAssertTrue(inventoryPage.isCartBadgeAbsent(), "Cart badge should be cleared");
        AssertionUtil.softAssertTrue(inventoryPage.isOnInventoryPage(), "User should remain logged in on the current page");
        AssertionUtil.assertAll();
        ExtentReportManager.pass("Reset App State clears the cart without logging out");
    }

    // TC_MENU_004
    @Test(dataProvider = "menuData", groups = {"regression"})
    public void resetAppStateResetsSortOrderWithoutLogout(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.selectSortOption(data.get("SortOption"));
        MenuPage menuPage = new MenuPage(driver());
        menuPage.openMenu();
        menuPage.clickResetAppState();
        AssertionUtil.assertTrue(inventoryPage.isOnInventoryPage(), "User should remain logged in after reset");
        // Default sort is "Name (A to Z)" — names should be ascending again after reset.
        java.util.List<String> names = inventoryPage.getProductNames();
        java.util.List<String> sorted = new java.util.ArrayList<>(names);
        sorted.sort(String::compareTo);
        AssertionUtil.assertEquals(names, sorted, "Sort order should reset to default (Name A to Z)");
    }

    // TC_MENU_005
    @Test(dataProvider = "menuData", groups = {"smoke", "regression"})
    public void logoutEndsSessionAndReturnsToLogin(Map<String, String> data) {
        loginAndReachInventory(data);
        MenuPage menuPage = new MenuPage(driver());
        menuPage.openMenu();
        menuPage.clickLogout();
        LoginPage loginPage = new LoginPage(driver());
        AssertionUtil.assertTrue(driver().getCurrentUrl().equals("https://www.saucedemo.com/")
                        || driver().getCurrentUrl().equals("https://www.saucedemo.com"),
                "Logout should return the user to the Login page");
        ExtentReportManager.pass("Logout ends session and returns to Login page");
    }

    // TC_MENU_006 — Automation Candidate: Partial. 'About' destination is undocumented
    // (GAP-FR-04) — observational check only.
    // TODO: requires manual setup — confirm and record the actual 'About' destination.
    @Test(dataProvider = "menuData", groups = {"partial"})
    public void aboutNavigatesAwayFromApplication(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        MenuPage menuPage = new MenuPage(driver());
        menuPage.openMenu();
        menuPage.clickAbout();
        menuPage.switchToNewWindowIfOpened();
        String destination = driver().getCurrentUrl();
        ExtentReportManager.info("'About' navigated to: " + destination + " (GAP-FR-04 — destination now recorded)");
        AssertionUtil.assertTrue(!destination.isBlank(), "Should land on some resolvable destination without an application error");
    }
}
