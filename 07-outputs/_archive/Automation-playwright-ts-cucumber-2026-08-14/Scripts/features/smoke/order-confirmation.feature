Feature: Order Confirmation - Smoke

  # TC_ORDERCONFIRM_001
  @DataFile:resources/data/smokedata.csv @Key:success_message_smoke @smoke @regression
  Scenario: Success message displayed after checkout
    Given the user has completed checkout using the resolved test data
    Then a success message should be displayed on the confirmation page
