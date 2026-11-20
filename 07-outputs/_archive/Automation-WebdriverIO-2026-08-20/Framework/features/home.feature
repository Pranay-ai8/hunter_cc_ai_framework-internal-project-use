@home
Feature: Home Screen
  As a user of the WebdriverIO Native Demo App
  I want the app to launch to a working Home screen
  So that I can start exploring its feature screens

  Background:
    Given the app is launched

  @smoke
  Scenario: App launches to the Home screen
    Then the Home screen should be displayed

  @regression
  Scenario: Home screen displays introductory branding content
    Then the Home screen should be displayed
    And the Home screen should display the WEBDRIVER introductory content
