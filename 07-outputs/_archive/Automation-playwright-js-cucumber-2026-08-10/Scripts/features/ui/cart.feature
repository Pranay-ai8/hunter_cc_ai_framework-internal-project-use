Feature: SauceDemo - Cart

  Background:
    Given the user is logged in as "<Username>" with password "<Password>"

  # TC_CART_001
  @Key:StandardUser @smoke
  Scenario: Single item added appears on Cart page
    When the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    Then "Sauce Labs Backpack" should be in the cart

  # TC_CART_002
  @Key:StandardUser @regression
  Scenario: Multiple items added independently appear on Cart page
    When the user adds "Sauce Labs Backpack" to the cart
    And the user adds "Sauce Labs Bike Light" to the cart
    And the user opens the cart
    Then "Sauce Labs Backpack" should be in the cart
    And "Sauce Labs Bike Light" should be in the cart
    And the cart should contain 2 items

  # TC_CART_003
  @Key:StandardUser @regression
  Scenario: Remove item from Cart page
    When the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    And the user removes "Sauce Labs Backpack" from the Cart page
    Then "Sauce Labs Backpack" should not be in the cart

  # TC_CART_004
  @Key:StandardUser @regression
  Scenario: Remove item directly from Inventory page
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should be visible
    When the user removes "Sauce Labs Backpack" from the Inventory page
    Then the cart badge should not be visible

  # TC_CART_005
  @Key:StandardUser @regression
  Scenario: Cart persists across Inventory/Detail/Cart navigation
    When the user adds "Sauce Labs Backpack" to the cart
    And the user opens the first product by its name
    And the user navigates back to products from the Detail page
    And the user opens the cart
    Then "Sauce Labs Backpack" should be in the cart

  # TC_CART_006
  @Key:StandardUser @regression
  Scenario: Continue Shopping returns to Inventory
    When the user opens the cart
    And the user clicks Continue Shopping
    Then the Inventory page should display exactly 6 products

  # TC_CART_007
  @Key:StandardUser @regression
  Scenario: Empty cart displays correctly
    When the user opens the cart
    Then the cart should contain 0 items

  # TC_CART_008 — Automation Candidate: Partial. Conflict between source PRDs on
  # problem_user's exact defect (see GapAnalysis.md Conflicting Requirements).
  @Key:ProblemUser @manual
  Scenario: problem_user Remove button behavior in Cart
    When the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    And the user removes "Sauce Labs Backpack" from the Cart page
