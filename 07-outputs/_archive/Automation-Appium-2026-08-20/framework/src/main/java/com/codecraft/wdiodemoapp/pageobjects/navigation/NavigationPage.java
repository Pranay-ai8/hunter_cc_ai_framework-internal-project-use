package com.codecraft.wdiodemoapp.pageobjects.navigation;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;

public class NavigationPage extends Base {

    private final NavigationPageObjectRepository repo;

    public NavigationPage() {
        repo = new NavigationPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public void goToWebviewTab() {
        clickWithHeal(repo.tabWebview, "Webview Tab");
    }

    public void goToLoginTab() {
        clickWithHeal(repo.tabLogin, "Login Tab");
    }

    public void goToFormsTab() {
        clickWithHeal(repo.tabForms, "Forms Tab");
    }

    public void goToSwipeTab() {
        clickWithHeal(repo.tabSwipe, "Swipe Tab");
    }

    public void goToDragTab() {
        clickWithHeal(repo.tabDrag, "Drag Tab");
    }

    public void goToHomeTab() {
        clickWithHeal(repo.tabHome, "Home Tab");
    }

    public void openMenu() {
        clickWithHeal(repo.tabMenu, "Menu Tab");
        waitUntilElementIsVisible(repo.sideMenuPanel);
    }

    public boolean isSideMenuDisplayed() {
        return isElementVisible(repo.sideMenuPanel);
    }

    public void goToPermissionsViaSideMenu() {
        openMenu();
        clickWithHeal(repo.sideMenuItemPermissions, "Permissions Side Menu Item");
    }

    public void goToDataManagementViaSideMenu() {
        openMenu();
        clickWithHeal(repo.sideMenuItemDataManagement, "Data Management Side Menu Item");
    }

    public boolean isSideMenuItemVisible(String module) {
        return switch (module.toLowerCase()) {
            case "home" -> isElementVisible(repo.sideMenuItemHome);
            case "webview" -> isElementVisible(repo.sideMenuItemWebview);
            case "login" -> isElementVisible(repo.sideMenuItemLogin);
            case "forms" -> isElementVisible(repo.sideMenuItemForms);
            case "swipe" -> isElementVisible(repo.sideMenuItemSwipe);
            case "drag" -> isElementVisible(repo.sideMenuItemDrag);
            case "permissions" -> isElementVisible(repo.sideMenuItemPermissions);
            case "data-management" -> isElementVisible(repo.sideMenuItemDataManagement);
            default -> false;
        };
    }
}
