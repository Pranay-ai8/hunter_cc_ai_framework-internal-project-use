Feature: Checkout - Regression

  # TC_CHECKOUT_002
  @DataFile:resources/data/checkoutdata.csv @Key:blank_first_name @regression
  Scenario: Blank First Name is blocked on Checkout Step 1
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then an inline checkout error should be displayed
    And the user should remain on checkout step one

  # TC_CHECKOUT_003
  @DataFile:resources/data/checkoutdata.csv @Key:blank_last_name @regression
  Scenario: Blank Last Name is blocked on Checkout Step 1
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then an inline checkout error should be displayed
    And the user should remain on checkout step one

  # TC_CHECKOUT_004
  @DataFile:resources/data/checkoutdata.csv @Key:blank_postal_code @regression
  Scenario: Blank Postal Code is blocked on Checkout Step 1
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then an inline checkout error should be displayed
    And the user should remain on checkout step one

  # TC_CHECKOUT_006
  @DataFile:resources/data/checkoutdata.csv @Key:step2_totals @regression
  Scenario: Step 2 displays correct items, tax, and total
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then the user should be navigated to checkout step two
    And the order overview totals should be consistent

  # TC_CHECKOUT_008
  @DataFile:resources/data/checkoutdata.csv @Key:cancel_step1 @regression
  Scenario: Cancel on Step 1 returns to the Cart page
    Given the user has a product in the cart and is on checkout step one
    When the user clicks Cancel on checkout step one
    Then the user should be returned to the cart page with contents unchanged

  # TC_CHECKOUT_009
  @DataFile:resources/data/checkoutdata.csv @Key:cancel_step2 @regression
  Scenario: Cancel on Step 2 returns to the Inventory page (not the Cart)
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then the user should be navigated to checkout step two
    When the user clicks Cancel on checkout step two
    Then the user should be returned to the inventory page from checkout

  # TC_CHECKOUT_010 — Partial: error_user's checkout errors are documented as random.
  # Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/checkoutdata.csv @Key:error_user_checkout @inprogress
  Scenario: error_user's checkout flow is observed for random errors
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    Then the checkout flow should be observed for random errors

  # TC_CHECKOUT_011 — Partial: problem_user's Last Name field failure is disputed between
  # source PRDs (GapAnalysis.md Conflicting Requirements). Excluded from @smoke/@regression.
  @DataFile:resources/data/checkoutdata.csv @Key:problem_user_lastname @inprogress
  Scenario: problem_user's Last Name field acceptance during checkout is observed
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    Then the Last Name field acceptance should be observed and recorded

  # TC_CHECKOUT_012
  @DataFile:resources/data/checkoutdata.csv @Key:postal_code_boundary @regression
  Scenario: Postal Code accepts a long/alphanumeric value without an application error
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then the application should either proceed or show a validation message without crashing
