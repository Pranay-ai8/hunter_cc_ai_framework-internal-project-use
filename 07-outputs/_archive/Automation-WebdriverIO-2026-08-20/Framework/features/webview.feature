@webview
Feature: WebView Screen
  As a user
  I want the Webview screen to load its hybrid content
  So that Appium can automate the embedded web content when needed

  Background:
    Given the app is launched
    And I navigate to the Webview tab

  @smoke
  Scenario: WebView screen loads its content
    Then the Webview screen should be displayed

  @regression
  Scenario: A WEBVIEW context becomes available after opening the Webview screen
    Then a WEBVIEW context should become available
