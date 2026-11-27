@forms
Feature: Forms
  As a user
  I want the Forms screen's controls (text input, switch, dropdown, buttons) to behave correctly
  So that this representative set of native input widgets can be trusted

  Background:
    Given the app is launched
    And I navigate to the Forms tab

  @smoke
  Scenario: Text input accepts and retains a value
    When I type the sample text into the text input
    Then the typed result should contain the sample text

  @regression
  Scenario: Switch toggles state on tap
    When I toggle the switch
    Then the switch state should have flipped

  @regression
  Scenario: Dropdown opens and allows a selection
    When I select the sample dropdown option
    Then the dropdown value should match the sample dropdown option

  @regression
  Scenario: Active and Inactive buttons can be tapped without error
    When I tap the Active button
    And I tap the Inactive button
    Then the Forms screen should remain displayed

  @regression
  Scenario: Special character input is handled without crashing
    When I type the special-character sample into the text input
    Then the typed result should contain the special-character sample
