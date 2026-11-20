@menu
Feature: Hamburger Menu Navigation

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in as "standard_user"

  # TC_MENU_001
  @regression
  Scenario: Hamburger menu displays all expected links
    When the user opens the hamburger menu
    Then the menu shows All Items, About, Logout, and Reset App State

  # TC_MENU_002
  @regression
  Scenario: All Items navigates to inventory
    When the user adds the first product to the cart
    And the user opens the cart page
    And the user opens the hamburger menu
    And the user clicks All Items in the menu
    Then exactly 6 products are visible

  # TC_MENU_003
  @regression
  Scenario: Reset App State clears the cart without logout
    When the user adds the first product to the cart
    And the user opens the hamburger menu
    And the user clicks Reset App State
    Then the cart badge is not displayed
    And exactly 6 products are visible

  # TC_MENU_004 — confirmed live 2026-08-11: Reset App State does NOT reset sort
  # order (only clears the cart). This corrects the documented assumption behind
  # FR-26 (RequirementAnalysis.md), matching the same live-confirmation pattern
  # already applied to GAP-FR-05.
  @regression
  Scenario: Reset App State does not affect sort order, without logout
    When the user sorts products by "hilo"
    And the user opens the hamburger menu
    And the user clicks Reset App State
    Then the product sort dropdown still shows "hilo"

  # TC_MENU_005
  @smoke
  Scenario: Logout ends the session and returns to login
    When the user opens the hamburger menu
    And the user clicks Logout
    Then the user is on the login page

  # TC_MENU_006 — 'About' destination undocumented (GAP-FR-04), observational
  @regression @observational
  Scenario: About link destination is recorded
    When the user opens the hamburger menu
    And the user clicks About in the menu
    Then the resulting destination is recorded for confirmation
