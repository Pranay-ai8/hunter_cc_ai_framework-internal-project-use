package com.codecraft.saucedemo.pages.web.checkout;

import com.codecraft.saucedemo.base.BasePage;
import com.codecraft.saucedemo.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Module: Checkout — covers both Step 1 (Customer Information,
 * checkout-step-one.html) and Step 2 (Order Overview, checkout-step-two.html), a
 * single combined Page Object since both steps belong to one linear wizard flow and
 * share no overlapping locators (PageFactory resolves {@code @FindBy} fields lazily,
 * so declaring Step 2-only fields is harmless while on Step 1, and vice versa).
 */
public class CheckoutPage extends BasePage {

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ==== Locators — Step 1 (Customer Information) ====

    @FindBy(id = "first-name")
    private WebElement firstNameInput;

    @FindBy(id = "last-name")
    private WebElement lastNameInput;

    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(css = ".error-message-container")
    private WebElement errorMessageBanner;

    // ==== Locators — Step 2 (Order Overview) ====

    @FindBy(css = ".cart_item .inventory_item_name")
    private List<WebElement> overviewItemNames;

    @FindBy(css = ".summary_subtotal_label")
    private WebElement itemTotalLabel;

    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    @FindBy(css = ".summary_total_label")
    private WebElement grandTotalLabel;

    @FindBy(id = "finish")
    private WebElement finishButton;

    // ==== Validations ====

    public boolean isOnStepOne() {
        return WaitUtils.waitForUrlContains(driver, "/checkout-step-one.html");
    }

    public boolean isOnStepTwo() {
        return WaitUtils.waitForUrlContains(driver, "/checkout-step-two.html");
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessageBanner);
    }

    public String getErrorText() {
        return waitForVisibility(errorMessageBanner).getText();
    }

    public List<String> getOverviewItemNames() {
        WaitUtils.waitForVisibilityOfAll(driver, overviewItemNames);
        return overviewItemNames.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public double getItemTotal() {
        return parseCurrency(waitForVisibility(itemTotalLabel).getText());
    }

    public double getTax() {
        return parseCurrency(waitForVisibility(taxLabel).getText());
    }

    public double getGrandTotal() {
        return parseCurrency(waitForVisibility(grandTotalLabel).getText());
    }

    private double parseCurrency(String text) {
        // e.g. "Item total: $29.99" / "Tax: $2.40" / "Total: $32.39"
        String numeric = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(numeric);
    }

    // ==== Page Actions — Step 1 ====

    public CheckoutPage enterFirstName(String firstName) {
        WebElement field = waitForVisibility(firstNameInput);
        field.clear();
        field.sendKeys(firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        WebElement field = waitForVisibility(lastNameInput);
        field.clear();
        field.sendKeys(lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        WebElement field = waitForVisibility(postalCodeInput);
        field.clear();
        field.sendKeys(postalCode);
        return this;
    }

    public void clickContinue() {
        waitForClickable(continueButton).click();
    }

    public void clickCancel() {
        waitForClickable(cancelButton).click();
    }

    public void fillCustomerInfoAndContinue(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        clickContinue();
    }

    // ==== Page Actions — Step 2 ====

    public void clickFinish() {
        waitForClickable(finishButton).click();
    }
}
