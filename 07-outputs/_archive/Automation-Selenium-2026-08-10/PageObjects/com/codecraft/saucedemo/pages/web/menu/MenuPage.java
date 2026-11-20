package com.codecraft.saucedemo.pages.web.menu;

import com.codecraft.saucedemo.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module: Navigation / Hamburger Menu — available on every page after login.
 */
public class MenuPage extends BasePage {

    public MenuPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators ====

    @FindBy(id = "react-burger-menu-btn")
    private WebElement hamburgerMenuButton;

    @FindBy(id = "react-burger-cross-btn")
    private WebElement closeMenuButton;

    @FindBy(id = "inventory_sidebar_link")
    private WebElement allItemsLink;

    @FindBy(id = "about_sidebar_link")
    private WebElement aboutLink;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    @FindBy(id = "reset_sidebar_link")
    private WebElement resetAppStateLink;

    @FindBy(css = ".bm-item-list a")
    private List<WebElement> menuLinks;

    // ==== Validations ====

    public boolean isMenuOpen() {
        return isDisplayed(allItemsLink);
    }

    public List<String> getMenuItemLabels() {
        return waitForVisibilityList(menuLinks).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private List<WebElement> waitForVisibilityList(List<WebElement> elements) {
        elements.forEach(this::waitForVisibility);
        return elements;
    }

    // ==== Page Actions ====

    public void openMenu() {
        waitForClickable(hamburgerMenuButton).click();
    }

    public void clickAllItems() {
        waitForClickable(allItemsLink).click();
    }

    public void clickAbout() {
        waitForClickable(aboutLink).click();
    }

    public void clickLogout() {
        waitForClickable(logoutLink).click();
    }

    public void clickResetAppState() {
        waitForClickable(resetAppStateLink).click();
    }

    /**
     * 'About' (TC_MENU_006) may open a new tab/window rather than navigating the
     * current one, depending on the target's {@code rel}/{@code target} attributes —
     * destination is undocumented (GAP-FR-04). Switches if a second window appeared.
     */
    public void switchToNewWindowIfOpened() {
        if (driver.getWindowHandles().size() > 1) {
            switchToNewWindow();
        }
    }
}
