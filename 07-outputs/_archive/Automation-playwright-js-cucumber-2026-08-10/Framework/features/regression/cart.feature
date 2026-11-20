@cart
Feature: Shopping Cart

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in as "standard_user"

  # TC_CART_001
  @smoke
  Scenario: Single item added appears on the cart page
    When the user adds the first product to the cart
    And the user opens the cart page
    Then 1 item is listed in the cart

  # TC_CART_002
  @regression
  Scenario: Multiple items added independently appear on the cart page
    When the user adds the first 2 products to the cart
    And the user opens the cart page
    Then 2 items are listed in the cart

  # TC_CART_003
  @regression
  Scenario: Remove item from the cart page
    When the user adds the first product to the cart
    And the user opens the cart page
    And the user removes the first item from the cart page
    Then 0 items are listed in the cart

  # TC_CART_004
  @regression
  Scenario: Remove item directly from the inventory page
    When the user adds the first product to the cart
    And the user removes the first product from the inventory page
    Then the cart badge is not displayed

  # TC_CART_005
  @regression
  Scenario: Cart contents persist across navigation
    When the user adds the first product to the cart
    And the user opens the first product's detail page by name
    And the user clicks Back to Products
    And the user opens the cart page
    Then 1 item is listed in the cart

  # TC_CART_006
  @regression
  Scenario: Continue Shopping returns to inventory
    When the user adds the first product to the cart
    And the user opens the cart page
    And the user clicks Continue Shopping
    Then exactly 6 products are visible

  # TC_CART_007
  @regression
  Scenario: Empty cart displays correctly
    When the user opens the cart page
    Then 0 items are listed in the cart
    And the cart badge is not displayed

  # TC_CART_008 — problem_user Remove-button dispute, observational (see GapAnalysis.md conflict)
  @regression @observational
  Scenario: problem_user's cart Remove button behavior is recorded
    Given the user logs out
    And the user logs in as "problem_user"
    When the user adds the first product to the cart
    And the user opens the cart page
    And the user removes the first item from the cart page
    Then the resulting cart item count is recorded for defect verification
