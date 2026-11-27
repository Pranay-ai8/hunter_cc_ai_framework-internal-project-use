import { When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import FormsPage from '../pageobjects/forms.page.js';
import testData from '../helper/TestData.js';

let switchStateBeforeToggle;

When(/^I type "(.*)" into the text input$/, async (text) => {
    await FormsPage.typeText(text);
});

When(/^I type the sample text into the text input$/, async () => {
    await FormsPage.typeText(testData.forms.sampleText);
});

When(/^I type the special-character sample into the text input$/, async () => {
    await FormsPage.typeText(testData.forms.specialCharacters);
});

Then(/^the typed result should contain "(.*)"$/, async (expectedText) => {
    const result = await FormsPage.getTypedResult();
    expect(result).toContain(expectedText);
});

Then(/^the typed result should contain the sample text$/, async () => {
    const result = await FormsPage.getTypedResult();
    expect(result).toContain(testData.forms.sampleText);
});

Then(/^the typed result should contain the special-character sample$/, async () => {
    const result = await FormsPage.getTypedResult();
    expect(result).toContain(testData.forms.specialCharacters);
});

When(/^I toggle the switch$/, async () => {
    switchStateBeforeToggle = await FormsPage.isSwitchOn();
    await FormsPage.toggleSwitch();
});

Then(/^the switch state should have flipped$/, async () => {
    const after = await FormsPage.isSwitchOn();
    expect(after).not.toBe(switchStateBeforeToggle);
});

When(/^I select "([^"]*)" from the dropdown$/, async (optionText) => {
    await FormsPage.selectDropdownOption(optionText);
});

When(/^I select the sample dropdown option$/, async () => {
    await FormsPage.selectDropdownOption(testData.forms.dropdownOption);
});

Then(/^the dropdown value should be "([^"]*)"$/, async (expectedValue) => {
    const value = await FormsPage.getDropdownValue();
    expect(value).toBe(expectedValue);
});

Then(/^the dropdown value should match the sample dropdown option$/, async () => {
    const value = await FormsPage.getDropdownValue();
    expect(value).toBe(testData.forms.dropdownOption);
});

When(/^I tap the Active button$/, async () => {
    await FormsPage.tapActiveButton();
});

When(/^I tap the Inactive button$/, async () => {
    await FormsPage.tapInactiveButton();
});
