package com.codecraft.wdiodemoapp;

import com.codecraft.wdiodemoapp.pageobjects.drag.DragPage;
import com.codecraft.wdiodemoapp.pageobjects.forms.FormsPage;
import com.codecraft.wdiodemoapp.pageobjects.home.HomePage;
import com.codecraft.wdiodemoapp.pageobjects.login.LoginPage;
import com.codecraft.wdiodemoapp.pageobjects.navigation.NavigationPage;
import com.codecraft.wdiodemoapp.pageobjects.permissions.PermissionsPage;
import com.codecraft.wdiodemoapp.pageobjects.swipe.SwipePage;
import com.codecraft.wdiodemoapp.pageobjects.webview.WebviewPage;

public class Pages {

    private static <T> T getPage(Class<T> pageType) {
        try {
            return pageType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate page: " + pageType.getName(), e);
        }
    }

    public static HomePage HomePage() {
        return getPage(HomePage.class);
    }

    public static NavigationPage NavigationPage() {
        return getPage(NavigationPage.class);
    }

    public static WebviewPage WebviewPage() {
        return getPage(WebviewPage.class);
    }

    public static LoginPage LoginPage() {
        return getPage(LoginPage.class);
    }

    public static FormsPage FormsPage() {
        return getPage(FormsPage.class);
    }

    public static SwipePage SwipePage() {
        return getPage(SwipePage.class);
    }

    public static DragPage DragPage() {
        return getPage(DragPage.class);
    }

    public static PermissionsPage PermissionsPage() {
        return getPage(PermissionsPage.class);
    }
}
