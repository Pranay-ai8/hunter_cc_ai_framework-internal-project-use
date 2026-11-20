@navigation
Feature: Navigation - Bottom Tab Bar and Side Menu
  As a user
  I want to move between the app's feature screens via the tab bar and side menu
  So that every module is reliably reachable

  Background:
    Given the app is launched

  @smoke
  Scenario: Side menu opens from the Menu tab
    When I open the side menu
    Then the side menu should be displayed

  @regression
  Scenario: Feature screens are reachable from the bottom tab bar and side menu
    When I navigate to the Webview tab
    Then the Webview screen should be displayed
    When I navigate to the Login tab
    Then the Login screen should be displayed
    When I navigate to the Forms tab
    Then the Forms screen should be displayed
    When I navigate to the Swipe tab
    Then the Swipe screen should be displayed
    When I navigate to the Drag tab
    Then the Drag screen should be displayed
    When I navigate to the Permissions screen via the side menu
    Then the Permissions screen should be displayed

  @regression
  Scenario: The side menu lists all configured feature items
    When I open the side menu
    Then the side menu should list the "home" item
    And the side menu should list the "webview" item
    And the side menu should list the "login" item
    And the side menu should list the "forms" item
    And the side menu should list the "swipe" item
    And the side menu should list the "drag" item
    And the side menu should list the "permissions" item

  @regression
  Scenario: Navigating away and back preserves screen reachability
    When I navigate to the Forms tab
    Then the Forms screen should be displayed
    When I navigate to the Swipe tab
    Then the Swipe screen should be displayed
    When I navigate to the Forms tab
    Then the Forms screen should be displayed
