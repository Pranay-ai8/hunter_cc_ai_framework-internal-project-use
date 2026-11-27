Feature: Checkout - SauceDemo
  As a logged-in SauceDemo user with items in my cart
  I want to complete the checkout flow
  So that I can finish my purchase

  # TC_CHECKOUT_001 | FR-18
  @smoke @regression @Key:TC_CHECKOUT_001
  Scenario: Checkout button navigates to Step 1
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    When user clicks checkout
    Then user is navigated to checkout step one

  # TC_CHECKOUT_002 | FR-19
  @regression @Key:TC_CHECKOUT_002
  Scenario: Blank First Name blocked on Step 1
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    When user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    Then a checkout inline error is visible
    And user remains on checkout step one

  # TC_CHECKOUT_003 | FR-19
  @regression @Key:TC_CHECKOUT_003
  Scenario: Blank Last Name blocked on Step 1
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    When user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    Then a checkout inline error is visible
    And user remains on checkout step one

  # TC_CHECKOUT_004 | FR-19
  @regression @Key:TC_CHECKOUT_004
  Scenario: Blank Postal Code blocked on Step 1
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    When user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    Then a checkout inline error is visible
    And user remains on checkout step one

  # TC_CHECKOUT_005 | FR-19
  @smoke @regression @Key:TC_CHECKOUT_005
  Scenario: Valid Step 1 submission proceeds to Step 2
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    When user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    Then user is navigated to checkout step two

  # TC_CHECKOUT_006 | FR-20
  @regression @Key:TC_CHECKOUT_006
  Scenario: Step 2 displays correct items, tax, and total
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    Then the order overview lists "<Product1>"
    And the grand total equals the item total plus tax

  # TC_CHECKOUT_007 | FR-21
  @smoke @regression @Key:TC_CHECKOUT_007
  Scenario: Finish completes purchase and clears cart
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    When user clicks finish
    Then user is navigated to the order confirmation page
    And a success message is displayed
    And the cart badge is not visible

  # TC_CHECKOUT_008 | FR-22 | Confirmed live 2026-08-07 (GAP-FR-05): Step 1 Cancel -> Cart.
  @regression @Key:TC_CHECKOUT_008
  Scenario: Cancel on Step 1 returns to Cart
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    When user clicks cancel on checkout step one
    Then user is navigated to the cart page

  # TC_CHECKOUT_009 | FR-22 | Confirmed live 2026-08-07 (GAP-FR-05): Step 2 Cancel -> Inventory
  # (NOT Cart — the two Cancel buttons do not behave identically).
  @regression @Key:TC_CHECKOUT_009
  Scenario: Cancel on Step 2 returns to Inventory
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    When user clicks cancel on checkout step two
    Then user is redirected to the inventory page

  # TC_CHECKOUT_010 | FR-29 | Partial — error_user's random errors during checkout are
  # non-deterministic by design; observed, not hard-asserted.
  @partial @Key:TC_CHECKOUT_010
  Scenario: error_user checkout is observed for random errors
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    When user clicks continue on checkout step one and observes for runtime errors
    Then the checkout observation is logged without a hard pass-or-fail assertion

  # TC_CHECKOUT_011 | FR-28 | Partial — confirmed live during this generation's DOM verification
  # pass that problem_user's checkout Step 1 submission IS blocked (inline error shown, stays on
  # checkout-step-one.html) even with all 3 fields validly filled, corroborating the docx's
  # "Last Name field may fail" claim over the PDF's silence on it. See AutomationSummary.md.
  @partial @Key:TC_CHECKOUT_011
  Scenario: problem_user's checkout Step 1 submission is verified
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    When user clicks continue on checkout step one
    Then the observed problem_user checkout-continue behavior is logged

  # TC_CHECKOUT_012 | FR-19 | Boundary — Postal Code format/length undocumented (GAP-BR-01).
  @regression @Key:TC_CHECKOUT_012
  Scenario: Postal Code accepts long/alphanumeric value without error
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    When user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    Then the application either proceeds to step two or shows a clear validation message without crashing
