@checkout
Feature: Checkout

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in as "standard_user"
    And the user adds the first product to the cart
    And the user opens the cart page

  # TC_CHECKOUT_001
  @smoke
  Scenario: Checkout button navigates to Step 1
    When the user clicks Checkout
    Then the customer information form is displayed

  # TC_CHECKOUT_002
  @regression
  Scenario: Blank First Name is blocked
    When the user clicks Checkout
    And the user submits customer info with first name "", last name "Doe", postal code "12345"
    Then an inline checkout error is displayed
    And the customer information form is displayed

  # TC_CHECKOUT_003
  @regression
  Scenario: Blank Last Name is blocked
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "", postal code "12345"
    Then an inline checkout error is displayed
    And the customer information form is displayed

  # TC_CHECKOUT_004
  @regression
  Scenario: Blank Postal Code is blocked
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code ""
    Then an inline checkout error is displayed
    And the customer information form is displayed

  # TC_CHECKOUT_005
  @smoke
  Scenario: Valid Step 1 submission proceeds to Step 2
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"
    Then the order overview page is displayed

  # TC_CHECKOUT_006
  @regression
  Scenario: Step 2 displays correct items, tax, and total
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"
    Then the grand total equals the item total plus tax

  # TC_CHECKOUT_007
  @smoke
  Scenario: Finish completes the purchase and clears the cart
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"
    And the user clicks Finish
    Then the order confirmation page is displayed
    And the cart badge is not displayed

  # TC_CHECKOUT_008 — confirmed live 2026-08-07: Step 1 Cancel returns to Cart
  @regression
  Scenario: Cancel on Step 1 returns to the cart page
    When the user clicks Checkout
    And the user cancels checkout on step 1
    Then 1 item is listed in the cart

  # TC_CHECKOUT_009 — confirmed live 2026-08-07: Step 2 Cancel returns to Inventory
  @regression
  Scenario: Cancel on Step 2 returns to the inventory page
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"
    And the user cancels checkout on step 2
    Then exactly 6 products are visible

  # TC_CHECKOUT_010 — error_user non-deterministic, observational
  @regression @observational
  Scenario: error_user checkout is observed for random errors
    Given the user logs out
    And the user logs in as "error_user"
    And the user adds the first product to the cart
    And the user opens the cart page
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"
    Then no unhandled application crash occurs

  # TC_CHECKOUT_011 — problem_user Last Name dispute, observational (see GapAnalysis.md conflict)
  @regression @observational
  Scenario: problem_user's Last Name field behavior is recorded
    Given the user logs out
    And the user logs in as "problem_user"
    And the user adds the first product to the cart
    And the user opens the cart page
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"
    Then the resulting checkout step is recorded for defect verification

  # TC_CHECKOUT_012
  @regression
  Scenario: Postal Code accepts a long alphanumeric value without error
    When the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "A1B2C3-LONGCODE-0001"
    Then no unhandled application crash occurs
