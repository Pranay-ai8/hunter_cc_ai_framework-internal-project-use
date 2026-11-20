Feature: Navigation - SauceDemo Hamburger Menu
  As a logged-in SauceDemo user
  I want to use the hamburger menu
  So that I can navigate, reset state, or log out

  # TC_MENU_001 | FR-25
  @regression @Key:TC_MENU_001
  Scenario: Hamburger menu displays all expected links
    Given user is logged in as "<Username>"
    When user opens the hamburger menu
    Then the menu displays exactly "All Items, About, Logout, Reset App State"

  # TC_MENU_002 | FR-25
  @regression @Key:TC_MENU_002
  Scenario: All Items navigates to Inventory
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    When user opens the hamburger menu
    And user clicks All Items
    Then user is redirected to the inventory page

  # TC_MENU_003 | FR-26
  @regression @Key:TC_MENU_003
  Scenario: Reset App State clears the cart without logout
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    When user opens the hamburger menu
    And user clicks Reset App State
    Then the cart badge is not visible
    And user is still logged in on the inventory page

  # TC_MENU_004 | FR-26
  @regression @Key:TC_MENU_004
  Scenario: Reset App State resets sort order without logout
    Given user is logged in as "<Username>"
    And user sorts products by "<SortLabel>"
    When user opens the hamburger menu
    And user clicks Reset App State
    Then the product sort order resets to the default
    And user is still logged in on the inventory page

  # TC_MENU_005 | FR-27
  @smoke @regression @Key:TC_MENU_005
  Scenario: Logout ends session and returns to Login
    Given user is logged in as "<Username>"
    When user opens the hamburger menu
    And user clicks Logout
    Then user remains on the login page

  # TC_MENU_006 | FR-25 | Partial — "About" destination was undocumented (GAP-FR-04); confirmed
  # live during this generation's DOM verification pass: it links to https://saucelabs.com/.
  @partial @Key:TC_MENU_006
  Scenario: About navigates away from the application
    Given user is logged in as "<Username>"
    Then the About menu link points to "https://saucelabs.com/"
