Feature: Order Confirmation - SauceDemo
  As a SauceDemo user who has just completed checkout
  I want confirmation of my order
  So that I know my purchase succeeded

  # TC_ORDERCONFIRM_001 | FR-23
  @smoke @regression @Key:TC_ORDERCONFIRM_001
  Scenario: Success message displayed after checkout
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    And user clicks finish
    Then user is navigated to the order confirmation page
    And a success message is displayed
    And the success message reads "Thank you for your order!"

  # TC_ORDERCONFIRM_002 | FR-24
  @regression @Key:TC_ORDERCONFIRM_002
  Scenario: Back Home navigates to Inventory
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    And user clicks finish
    When user clicks Back Home
    Then user is redirected to the inventory page

  # TC_ORDERCONFIRM_003 | FR-21
  @regression @Key:TC_ORDERCONFIRM_003
  Scenario: Cart is empty after order completion
    Given user is logged in as "<Username>"
    And user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks checkout
    And user fills checkout information with first name "<FirstName>", last name "<LastName>", postal code "<PostalCode>"
    And user clicks continue on checkout step one
    And user clicks finish
    And user clicks Back Home
    Then the cart badge is not visible
