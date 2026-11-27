Feature: Navigation - Regression

  # TC_MENU_001
  @DataFile:resources/data/menudata.csv @Key:menu_items_visible @regression
  Scenario: Hamburger menu displays all expected links
    Given the user is logged in for menu tests
    When the user opens the hamburger menu
    Then the menu should display All Items, About, Logout, and Reset App State

  # TC_MENU_002
  @DataFile:resources/data/menudata.csv @Key:all_items_navigation @regression
  Scenario: All Items navigates to the Inventory page
    Given the user is logged in for menu tests
    Given the user is on a page other than inventory
    When the user clicks All Items from the menu
    Then the user should be navigated to the inventory page from the menu

  # TC_MENU_003
  @DataFile:resources/data/menudata.csv @Key:reset_clears_cart @regression
  Scenario: Reset App State clears the cart without logout
    Given the user is logged in for menu tests
    Given the user has added products to the cart for reset tests
    When the user clicks Reset App State from the menu
    Then the cart should be cleared and the user should remain logged in

  # TC_MENU_004 — Assertion corrected against verified live behavior: Reset App State does
  # NOT reset the sort order on https://www.saucedemo.com/ (contradicts the stale claim in
  # RequirementAnalysis.md §9 / FunctionalTestCases.md). See AutomationSummary.md.
  @DataFile:resources/data/menudata.csv @Key:reset_resets_sort @regression
  Scenario: Reset App State does not affect sort order, and the user remains logged in
    Given the user is logged in for menu tests
    Given the user has applied a non-default sort order
    When the user clicks Reset App State from the menu
    Then the sort order should remain unchanged after reset and the user should remain logged in

  # TC_MENU_006 — Partial: 'About' menu destination is undocumented (GAP-FR-04).
  # Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/menudata.csv @Key:about_link @inprogress
  Scenario: About navigates away from the application and the destination is recorded
    Given the user is logged in for menu tests
    When the user clicks About from the menu
    Then the resulting destination should be recorded without error
