Feature: Order Confirmation - Regression

  # TC_ORDERCONFIRM_002
  @DataFile:resources/data/orderconfirmationdata.csv @Key:back_home @regression
  Scenario: Back Home navigates to the Inventory page
    Given the user has completed checkout using the resolved test data
    When the user clicks Back Home
    Then the user should be navigated to the inventory page from confirmation

  # TC_ORDERCONFIRM_003
  @DataFile:resources/data/orderconfirmationdata.csv @Key:cart_cleared @regression
  Scenario: Cart is empty after order completion
    Given the user has completed checkout using the resolved test data
    When the user clicks Back Home
    Then the inventory page cart badge should be cleared
