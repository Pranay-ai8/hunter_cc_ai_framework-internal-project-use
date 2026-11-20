@order-confirmation
Feature: Order Confirmation

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in as "standard_user"
    And the user adds the first product to the cart
    And the user opens the cart page
    And the user clicks Checkout
    And the user submits customer info with first name "Jane", last name "Doe", postal code "12345"

  # TC_ORDERCONFIRM_001
  @smoke
  Scenario: Success message displayed after checkout
    When the user clicks Finish
    Then the order confirmation page is displayed

  # TC_ORDERCONFIRM_002
  @regression
  Scenario: Back Home navigates to inventory
    When the user clicks Finish
    And the user clicks Back Home
    Then exactly 6 products are visible

  # TC_ORDERCONFIRM_003
  @regression
  Scenario: Cart is empty after order completion
    When the user clicks Finish
    And the user clicks Back Home
    Then the cart badge is not displayed
