Feature: Navigation - Smoke

  # TC_MENU_005
  @DataFile:resources/data/smokedata.csv @Key:logout_smoke @smoke @regression
  Scenario: Logout ends the session and returns to the Login page
    Given the user is logged in for menu tests
    When the user clicks Logout from the menu
    Then the user should be redirected to the login page and the session should end
