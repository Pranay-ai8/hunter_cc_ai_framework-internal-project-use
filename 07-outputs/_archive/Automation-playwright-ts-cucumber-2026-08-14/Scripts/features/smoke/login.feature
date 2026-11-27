Feature: Authentication - Smoke

  # TC_LOGIN_001
  @DataFile:resources/data/smokedata.csv @Key:valid_login_standard @smoke @regression
  Scenario: Successful login with standard_user
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then the user should be redirected to the inventory page
