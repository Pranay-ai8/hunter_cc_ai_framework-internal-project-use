package com.codecraft.saucedemo.tests.inventory;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.inventory.InventoryPage;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Module: Product Catalog — source: 07-outputs/TestCases/FunctionalTestCases.md
 * Target: https://www.saucedemo.com/inventory.html
 */
public class InventoryTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/inventory.csv";

    @DataProvider(name = "inventoryData")
    public Object[][] inventoryData(Method method) {
        return TestDataArgumentsProvider.singleRow(CSV, method);
    }

    private InventoryPage loginAndReachInventory(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: login should reach Inventory page");
        return new InventoryPage(driver());
    }

    // TC_INVENTORY_001
    @Test(dataProvider = "inventoryData", groups = {"smoke", "regression"})
    public void exactlySixProductsDisplayedWithFullDetails(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        AssertionUtil.softAssertEquals(inventoryPage.getProductCount(), 6, "Exactly 6 products should be displayed");
        AssertionUtil.softAssertEquals(inventoryPage.getProductNames().size(), 6, "6 product names expected");
        AssertionUtil.softAssertEquals(inventoryPage.getProductPrices().size(), 6, "6 product prices expected");
        AssertionUtil.softAssertEquals(inventoryPage.getProductImageSources().size(), 6, "6 product images expected");
        AssertionUtil.assertAll();
        ExtentReportManager.pass("Inventory shows exactly 6 products with name, price, and image");
    }

    // TC_INVENTORY_002
    @Test(dataProvider = "inventoryData", groups = {"regression"})
    public void sortByNameAToZ(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.selectSortOption(data.get("SortOption"));
        List<String> names = inventoryPage.getProductNames();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(String::compareTo);
        AssertionUtil.assertEquals(names, sorted, "Products should be sorted A to Z by name");
    }

    // TC_INVENTORY_003
    @Test(dataProvider = "inventoryData", groups = {"regression"})
    public void sortByNameZToA(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.selectSortOption(data.get("SortOption"));
        List<String> names = inventoryPage.getProductNames();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort((a, b) -> b.compareTo(a));
        AssertionUtil.assertEquals(names, sorted, "Products should be sorted Z to A by name");
    }

    // TC_INVENTORY_004
    @Test(dataProvider = "inventoryData", groups = {"regression"})
    public void sortByPriceLowToHigh(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.selectSortOption(data.get("SortOption"));
        List<Double> prices = inventoryPage.getProductPrices();
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Double::compareTo);
        AssertionUtil.assertEquals(prices, sorted, "Products should be sorted ascending by price");
    }

    // TC_INVENTORY_005
    @Test(dataProvider = "inventoryData", groups = {"regression"})
    public void sortByPriceHighToLow(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.selectSortOption(data.get("SortOption"));
        List<Double> prices = inventoryPage.getProductPrices();
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort((a, b) -> Double.compare(b, a));
        AssertionUtil.assertEquals(prices, sorted, "Products should be sorted descending by price");
    }

    // TC_INVENTORY_006
    @Test(dataProvider = "inventoryData", groups = {"smoke", "regression"})
    public void addToCartUpdatesBadgeInRealTime(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        AssertionUtil.assertTrue(inventoryPage.isCartBadgeAbsent(), "Cart badge should be absent before adding an item");
        String productName = data.get("ProductName");
        inventoryPage.addToCartByName(productName);
        AssertionUtil.assertEquals(inventoryPage.getCartBadgeCount(), 1, "Cart badge should increment to 1 immediately");
        AssertionUtil.assertFalse(inventoryPage.isAddToCartButtonVisibleFor(productName),
                "Button should change from 'Add to cart' to 'Remove'");
    }

    // TC_INVENTORY_007 — Automation Candidate: Partial. problem_user defect conflict
    // between source PRDs (GapAnalysis.md Conflicting Requirements) — observational
    // check only, no hard pass/fail assertion tied to a single documented possibility.
    // TODO: requires manual setup — confirm which defect(s) problem_user exhibits.
    @Test(dataProvider = "inventoryData", groups = {"partial"})
    public void problemUserSeesBrokenProductImages(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        List<String> imageSources = inventoryPage.getProductImageSources();
        Set<String> distinctSources = new java.util.HashSet<>(imageSources);
        ExtentReportManager.info("problem_user product image sources: " + imageSources);
        AssertionUtil.softAssertTrue(!distinctSources.isEmpty(), "Should be able to read image sources at all");
        // Observational: standard_user shows 6 distinct images; problem_user is
        // documented (PDF) to show broken/swapped images — log rather than hard-assert.
        ExtentReportManager.info("Distinct image count: " + distinctSources.size() + " (6 expected if not defective)");
        AssertionUtil.assertAll();
    }

    // TC_INVENTORY_008 — Automation Candidate: Partial. error_user's "random" error
    // behavior is explicitly non-deterministic (docx) — observational only.
    // TODO: requires manual setup — non-deterministic, log/observe rather than hard-assert.
    @Test(dataProvider = "inventoryData", groups = {"partial"})
    public void errorUserSortInteractionMayThrowRandomError(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String[] sortOptions = {"Name (A to Z)", "Name (Z to A)", "Price (low to high)", "Price (high to low)"};
        int successCount = 0;
        for (String option : sortOptions) {
            try {
                inventoryPage.selectSortOption(option);
                successCount++;
                ExtentReportManager.info("error_user sort '" + option + "' completed without a thrown exception");
            } catch (Exception e) {
                ExtentReportManager.info("error_user sort '" + option + "' threw: " + e.getClass().getSimpleName()
                        + " — consistent with documented random-error behavior");
            }
        }
        // Not a hard pass/fail: either outcome (all succeed, or some throw) is consistent
        // with the documented "random" behavior. We only assert the page didn't crash entirely.
        AssertionUtil.assertTrue(inventoryPage.isOnInventoryPage(),
                "Should still be on the Inventory page after sort interactions (successCount=" + successCount + ")");
    }

    // TC_INVENTORY_009 — Automation Candidate: Partial. Needs a visual-regression
    // baseline/tool rather than a structural assertion.
    // TODO: requires manual setup — visual-regression tooling not wired into this suite.
    @Test(dataProvider = "inventoryData", groups = {"partial"})
    public void visualUserShowsCssLayoutDefects(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        com.codecraft.saucedemo.utils.ScreenshotUtils.capture(driver(), "visual_user_inventory_baseline_candidate");
        ExtentReportManager.info("visual_user Inventory screenshot captured for manual/visual-regression review");
        AssertionUtil.assertTrue(inventoryPage.isOnInventoryPage(),
                "visual_user should still reach and render the Inventory page structurally");
    }
}
