Feature: SauceDemo - Navigation / Hamburger Menu

  Background:
    Given the user is logged in as "<Username>" with password "<Password>"

  # TC_MENU_001
  @Key:StandardUser @regression
  Scenario: Hamburger menu displays all expected links
    When the user opens the hamburger menu
    Then all menu links should be visible

  # TC_MENU_002
  @Key:StandardUser @regression
  Scenario: All Items navigates to Inventory
    When the user opens the cart
    And the user opens the hamburger menu
    And the user clicks All Items
    Then the Inventory page should display exactly 6 products

  # TC_MENU_003
  @Key:StandardUser @regression
  Scenario: Reset App State clears the cart without logout
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should be visible
    When the user opens the hamburger menu
    And the user clicks Reset App State
    Then the cart badge should not be visible

  # TC_MENU_004
  @Key:StandardUser @regression
  Scenario: Reset App State resets sort order without logout
    When the user sorts products by "Price (high to low)"
    And the user opens the hamburger menu
    And the user clicks Reset App State
    Then the Inventory page should display exactly 6 products

  # TC_MENU_005
  @Key:StandardUser @smoke
  Scenario: Logout ends session and returns to Login
    When the user opens the hamburger menu
    And the user clicks Logout
    Then the user should be redirected to the Login page

  # TC_MENU_006 — Automation Candidate: Partial. "About" destination is
  # undocumented in either source PRD (GAP-FR-04).
  @Key:StandardUser @manual
  Scenario: About navigates away from the application
    When the user opens the hamburger menu
    And the user clicks About
