package com.codecraft.wdiodemoapp.hometests;

import com.codecraft.wdiodemoapp.framework.TestBase;
import com.codecraft.wdiodemoapp.Pages;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomeTests extends TestBase {

    @Test(description = "TC-HOME-001: App launches to Home screen", groups = {"smoke", "regression"})
    public void verifyHomeScreenLoadsOnLaunch() {
        Assert.assertTrue(Pages.HomePage().isHomeScreenDisplayed(),
                "Home screen should be displayed immediately after app launch");
    }

    @Test(description = "TC-HOME-002: Home screen displays introductory content", groups = {"regression"})
    public void verifyHomeScreenDisplaysIntroductoryContent() {
        Assert.assertTrue(Pages.HomePage().isHomeScreenDisplayed(), "Home screen should be displayed");
        Assert.assertTrue(Pages.HomePage().isIntroductoryContentDisplayed(),
                "Home screen should display the WEBDRIVER intro branding and description");
    }

    @Test(description = "TC-HOME-003: Navigate from Home to Menu", groups = {"regression"})
    public void verifyNavigateFromHomeToMenu() {
        Pages.NavigationPage().openMenu();
        Assert.assertTrue(Pages.NavigationPage().isSideMenuDisplayed(), "Side menu should open from Home screen");
    }
}
