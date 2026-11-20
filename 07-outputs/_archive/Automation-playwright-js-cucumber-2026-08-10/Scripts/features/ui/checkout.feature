Feature: SauceDemo - Checkout

  Background:
    Given the user is logged in as "<Username>" with password "<Password>"
    And the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    And the user proceeds to checkout

  # TC_CHECKOUT_001
  @Key:StandardUser @smoke
  Scenario: Checkout button navigates to Step 1
    Then the user should be on Checkout Step 1

  # TC_CHECKOUT_002
  @Key:BlankFirstName @regression
  Scenario: Blank First Name blocked
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    Then a checkout error message should be displayed

  # TC_CHECKOUT_003
  @Key:BlankLastName @regression
  Scenario: Blank Last Name blocked
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    Then a checkout error message should be displayed

  # TC_CHECKOUT_004
  @Key:BlankPostalCode @regression
  Scenario: Blank Postal Code blocked
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    Then a checkout error message should be displayed

  # TC_CHECKOUT_005
  @Key:ValidCustomer @smoke
  Scenario: Valid Step 1 submission proceeds to Step 2
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    Then the user should be on Checkout Step 2

  # TC_CHECKOUT_006
  @Key:ValidCustomer @regression
  Scenario: Step 2 displays correct items, tax, and total
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    Then the Order Overview should list 1 item
    And the grand total should equal the item total plus tax

  # TC_CHECKOUT_007
  @Key:ValidCustomer @smoke
  Scenario: Finish completes purchase and clears cart
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    And the user clicks Finish
    Then the order success message should be displayed

  # TC_CHECKOUT_008
  @Key:StandardUser @regression
  Scenario: Cancel on Step 1 returns to Cart
    When the user cancels checkout
    Then the user should be on the Cart page

  # TC_CHECKOUT_009 — Confirmed against the live site: unlike Step 1 (which returns
  # to Cart), Cancel on Step 2 returns to the Inventory page, not Cart. GAP-FR-05
  # resolved: the two Cancel buttons do NOT behave identically.
  @Key:ValidCustomer @regression
  Scenario: Cancel on Step 2 returns to Inventory
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    And the user cancels checkout
    Then the user should be redirected to the Inventory page

  # TC_CHECKOUT_010 — Automation Candidate: Partial. error_user's errors are
  # explicitly documented as "random" — not suitable for a deterministic assertion.
  @Key:ErrorUserCheckout @manual
  Scenario: error_user may throw a random error during checkout
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    And the user clicks Finish

  # TC_CHECKOUT_011 — Automation Candidate: Partial. Conflict between source PRDs
  # on problem_user's exact defect (see GapAnalysis.md Conflicting Requirements).
  @Key:ProblemUserCheckout @manual
  Scenario: problem_user Last Name field may fail
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2

  # TC_CHECKOUT_012
  @Key:LongPostalCode @regression
  Scenario: Postal Code accepts long/alphanumeric value without error
    When the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
