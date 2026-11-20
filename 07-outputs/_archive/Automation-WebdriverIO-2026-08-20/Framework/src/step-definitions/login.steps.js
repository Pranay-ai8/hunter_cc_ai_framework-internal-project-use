import { Given, When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import LoginPage from '../pageobjects/login.page.js';
import testData from '../helper/TestData.js';

Given(/^the email and password fields are empty$/, async () => {
    await LoginPage.clearFields();
});

When(/^I enter a randomly generated email address$/, async () => {
    await LoginPage.enterEmail(LoginPage.generateRandomEmail());
});

When(/^I enter the email "([^"]*)"$/, async (email) => {
    await LoginPage.enterEmail(email);
});

When(/^I enter an invalid-format email$/, async () => {
    await LoginPage.enterEmail(testData.login.invalidEmailFormat);
});

When(/^I enter the password "([^"]*)"$/, async (password) => {
    await LoginPage.enterPassword(password);
});

When(/^I enter the valid password$/, async () => {
    await LoginPage.enterPassword(testData.login.validPassword);
});

When(/^I tap the Login button$/, async () => {
    await LoginPage.tapLoginButton();
});

When(/^I open the Sign Up tab$/, async () => {
    await LoginPage.openSignUpTab();
});

When(/^I open the Login tab$/, async () => {
    await LoginPage.openLoginTab();
});

Then(/^a success alert confirming I am logged in should be displayed$/, async () => {
    const displayed = await LoginPage.isSuccessAlertDisplayed();
    expect(displayed).toBe(true);
    await LoginPage.dismissAlert();
});

Then(/^the validation message containing "([^"]*)" should be displayed$/, async (partialText) => {
    const displayed = await LoginPage.isValidationMessageDisplayed(partialText);
    expect(displayed).toBe(true);
});
