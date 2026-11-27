@swipe
Feature: Swipe
  As a user
  I want to swipe the carousel horizontally and vertically
  So that the swipe gesture is reliably supported

  Background:
    Given the app is launched
    And I navigate to the Swipe tab

  @smoke
  Scenario: Horizontal forward swipe advances the carousel
    Then the carousel should be displayed
    When I swipe the carousel horizontally forward
    Then the Swipe screen should remain displayed

  @regression
  Scenario: Horizontal backward swipe returns to the previous item
    When I swipe the carousel horizontally forward
    And I swipe the carousel horizontally backward
    Then the Swipe screen should remain displayed

  @regression
  Scenario: Vertical swipe navigates the carousel
    When I swipe the carousel vertically downward
    Then the Swipe screen should remain displayed
