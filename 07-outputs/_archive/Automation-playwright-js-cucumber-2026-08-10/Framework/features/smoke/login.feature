@login
Feature: Authentication

  Background:
    Given the user is on the SauceDemo login page

  # TC_LOGIN_001
  @smoke
  Scenario: Successful login with standard_user
    When the user logs in as "standard_user"
    Then the user is redirected to the inventory page
    And exactly 6 products are visible

  # TC_LOGIN_002
  @regression
  Scenario: locked_out_user is blocked at login
    When the user logs in as "locked_out_user"
    Then an error banner reading "Sorry, this user has been locked out." is displayed
    And the user remains on the login page

  # TC_LOGIN_003
  @regression
  Scenario: Blank username shows required error
    When the user attempts to log in with username "" and password "secret_sauce"
    Then an error banner reading "Username is required" is displayed

  # TC_LOGIN_004
  @regression
  Scenario: Blank password shows required error
    When the user attempts to log in with username "standard_user" and password ""
    Then an error banner reading "Password is required" is displayed

  # TC_LOGIN_005
  @regression
  Scenario: Unknown credentials are rejected
    When the user attempts to log in with username "not_a_real_user" and password "wrong_password"
    Then an error banner reading "Username and password do not match any user in this service" is displayed

  # TC_LOGIN_006
  @regression
  Scenario: performance_glitch_user login has an artificial delay
    When the user logs in as "performance_glitch_user" and elapsed time is measured
    Then the user is redirected to the inventory page
    And the login took at least 3 seconds

  # TC_LOGIN_007
  @regression
  Scenario: problem_user logs in successfully
    When the user logs in as "problem_user"
    Then the user is redirected to the inventory page

  # TC_LOGIN_008
  @regression
  Scenario: error_user logs in successfully
    When the user logs in as "error_user"
    Then the user is redirected to the inventory page

  # TC_LOGIN_009
  @regression
  Scenario: visual_user logs in successfully
    When the user logs in as "visual_user"
    Then the user is redirected to the inventory page

  # TC_LOGIN_010 — real 10-minute wait is impractical for CI (see FunctionalTestCases.md notes); tracked, not executed
  @inprogress
  Scenario: Session auto-expires after 10 minutes of inactivity
    When the user logs in as "standard_user"
    Then the session should expire after 10 minutes of inactivity
