Feature: Checkout - Smoke

  # TC_CHECKOUT_001
  @DataFile:resources/data/smokedata.csv @Key:checkout_navigate_smoke @smoke @regression
  Scenario: Checkout button navigates to Step 1
    Given the user is on the cart page with at least one item
    When the user clicks Checkout from the cart page
    Then checkout step one should be navigated to

  # TC_CHECKOUT_005
  @DataFile:resources/data/smokedata.csv @Key:valid_step1_smoke @smoke @regression
  Scenario: Valid Step 1 submission proceeds to Step 2
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then the user should be navigated to checkout step two

  # TC_CHECKOUT_007
  @DataFile:resources/data/smokedata.csv @Key:finish_purchase_smoke @smoke @regression
  Scenario: Finish completes the purchase and clears the cart
    Given the user has a product in the cart and is on checkout step one
    When the user fills checkout step one using the resolved test data
    And the user clicks Continue on checkout step one
    Then the user should be navigated to checkout step two
    When the user clicks Finish
    Then the purchase should complete and navigate to order confirmation
    And the checkout cart badge should be cleared
