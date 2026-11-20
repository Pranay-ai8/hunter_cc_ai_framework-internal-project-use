@login
Feature: Login / Sign Up
  As a user
  I want to log in with valid credentials and be warned about invalid ones
  So that the app enforces its documented validation rules

  Background:
    Given the app is launched
    And I navigate to the Login tab

  @smoke
  Scenario: Login screen renders the expected elements
    Then the Login screen should be displayed

  @smoke
  Scenario: Valid credentials submit successfully
    When I enter a randomly generated email address
    And I enter the valid password
    And I tap the Login button
    Then a success alert confirming I am logged in should be displayed

  @regression
  Scenario: Empty field submission shows inline validation messages
    Given the email and password fields are empty
    When I tap the Login button
    Then the validation message containing "valid email address" should be displayed
    And the validation message containing "at least 8 characters" should be displayed

  @regression
  Scenario: Invalid-format email shows a validation message
    When I enter an invalid-format email
    And I enter the valid password
    And I tap the Login button
    Then the validation message containing "valid email address" should be displayed

  @regression
  Scenario: Navigating to the Sign Up tab and back preserves the Login screen
    When I open the Sign Up tab
    And I open the Login tab
    Then the Login screen should be displayed
