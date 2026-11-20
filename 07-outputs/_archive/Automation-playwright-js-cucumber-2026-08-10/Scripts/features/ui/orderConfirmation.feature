Feature: SauceDemo - Order Confirmation

  Background:
    Given the user is logged in as "<Username>" with password "<Password>"
    And the user adds "Sauce Labs Backpack" to the cart
    And the user opens the cart
    And the user proceeds to checkout
    And the user fills customer info with first name "<FirstName>" last name "<LastName>" postal code "<PostalCode>"
    And the user continues to Step 2
    And the user clicks Finish

  # TC_ORDERCONFIRM_001
  @Key:ValidCustomer @smoke
  Scenario: Success message displayed after checkout
    Then the order success message should be displayed

  # TC_ORDERCONFIRM_002
  @Key:ValidCustomer @regression
  Scenario: Back Home navigates to Inventory
    When the user clicks Back Home
    Then the Inventory page should display exactly 6 products

  # TC_ORDERCONFIRM_003
  @Key:ValidCustomer @regression
  Scenario: Cart is empty after order completion
    When the user clicks Back Home
    Then the cart badge should not be visible
