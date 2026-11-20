package com.codecraft.saucedemo.tests.productdetail;

import com.codecraft.saucedemo.base.BaseTest;
import com.codecraft.saucedemo.pages.web.inventory.InventoryPage;
import com.codecraft.saucedemo.pages.web.login.LoginPage;
import com.codecraft.saucedemo.pages.web.productdetail.ProductDetailPage;
import com.codecraft.saucedemo.reports.ExtentReportManager;
import com.codecraft.saucedemo.utils.AssertionUtil;
import com.codecraft.saucedemo.utils.TestDataArgumentsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Module: Product Detail — source: 07-outputs/TestCases/FunctionalTestCases.md
 * Target: https://www.saucedemo.com/inventory-item.html?id=&lt;N&gt;
 */
public class ProductDetailTest extends BaseTest {

    private static final String CSV = "src/test/resources/testdata/productdetail.csv";

    @DataProvider(name = "productDetailData")
    public Object[][] productDetailData(Method method) {
        return TestDataArgumentsProvider.singleRow(CSV, method);
    }

    private InventoryPage loginAndReachInventory(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.login(data.get("Username"), data.get("Password"));
        AssertionUtil.assertTrue(loginPage.isOnInventoryPage(), "Precondition: login should reach Inventory page");
        return new InventoryPage(driver());
    }

    // TC_PRODUCTDETAIL_001
    @Test(dataProvider = "productDetailData", groups = {"regression"})
    public void clickingProductNameOpensDetailPage(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductName");
        inventoryPage.clickProductName(productName);
        ProductDetailPage detailPage = new ProductDetailPage(driver());
        AssertionUtil.softAssertTrue(detailPage.isOnDetailPage(), "Should navigate to the Product Detail page");
        AssertionUtil.softAssertEquals(detailPage.getProductName(), productName, "Detail page should show the same product");
        AssertionUtil.softAssertTrue(detailPage.isAddToCartButtonVisible(), "Add to cart button should be visible");
        AssertionUtil.assertAll();
    }

    // TC_PRODUCTDETAIL_002
    @Test(dataProvider = "productDetailData", groups = {"regression"})
    public void clickingProductImageOpensDetailPage(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        String productName = data.get("ProductName");
        inventoryPage.clickProductImage(productName);
        ProductDetailPage detailPage = new ProductDetailPage(driver());
        AssertionUtil.softAssertTrue(detailPage.isOnDetailPage(), "Should navigate to the Product Detail page");
        AssertionUtil.softAssertEquals(detailPage.getProductName(), productName, "Detail page should show the same product");
        AssertionUtil.assertAll();
    }

    // TC_PRODUCTDETAIL_003
    @Test(dataProvider = "productDetailData", groups = {"regression"})
    public void addToCartUpdatesBadge(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.clickProductName(data.get("ProductName"));
        ProductDetailPage detailPage = new ProductDetailPage(driver());
        AssertionUtil.assertTrue(detailPage.isOnDetailPage(), "Should be on the Product Detail page");
        detailPage.addToCart();
        AssertionUtil.assertEquals(detailPage.getAddToCartButtonText(), "Remove",
                "Button should change to 'Remove' after adding to cart");
        ExtentReportManager.pass("Add to cart from Product Detail page updates button state");
    }

    // TC_PRODUCTDETAIL_004
    @Test(dataProvider = "productDetailData", groups = {"regression"})
    public void backToProductsReturnsToInventory(Map<String, String> data) {
        InventoryPage inventoryPage = loginAndReachInventory(data);
        inventoryPage.clickProductName(data.get("ProductName"));
        ProductDetailPage detailPage = new ProductDetailPage(driver());
        AssertionUtil.assertTrue(detailPage.isOnDetailPage(), "Should be on the Product Detail page");
        detailPage.clickBackToProducts();
        InventoryPage backOnInventory = new InventoryPage(driver());
        AssertionUtil.softAssertTrue(backOnInventory.isOnInventoryPage(), "Should return to the Inventory page");
        AssertionUtil.softAssertEquals(backOnInventory.getProductCount(), 6, "All 6 products should be visible again");
        AssertionUtil.assertAll();
    }
}
