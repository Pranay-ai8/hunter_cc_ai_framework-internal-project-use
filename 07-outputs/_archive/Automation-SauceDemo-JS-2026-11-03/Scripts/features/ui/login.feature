Feature: Authentication - SauceDemo Login
  As a SauceDemo user
  I want to log in with my credentials
  So that I can access the product catalog

  # TC_LOGIN_001 | FR-01
  @smoke @regression @Key:TC_LOGIN_001
  Scenario: Successful login with standard_user
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user is redirected to the inventory page
    And exactly 6 products are visible on the inventory page

  # TC_LOGIN_002 | FR-02
  @regression @Key:TC_LOGIN_002
  Scenario: locked_out_user is blocked from logging in
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user sees a login error containing "Sorry, this user has been locked out."
    And user remains on the login page

  # TC_LOGIN_003 | FR-03
  @regression @Key:TC_LOGIN_003
  Scenario: Blank username shows required error
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user sees a login error containing "Username is required"
    And user remains on the login page

  # TC_LOGIN_004 | FR-04
  @regression @Key:TC_LOGIN_004
  Scenario: Blank password shows required error
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user sees a login error containing "Password is required"
    And user remains on the login page

  # TC_LOGIN_005 | FR-05
  @regression @Key:TC_LOGIN_005
  Scenario: Unknown credentials are rejected
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user sees a login error containing "Username and password do not match any user in this service"
    And user remains on the login page

  # TC_LOGIN_006 | FR-06
  @regression @Key:TC_LOGIN_006
  Scenario: performance_glitch_user login has an artificial delay
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>" and measures login duration
    Then user is redirected to the inventory page
    And the measured login duration is at least 2000 ms

  # TC_LOGIN_007 | FR-07
  @regression @Key:TC_LOGIN_007
  Scenario: problem_user logs in successfully
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user is redirected to the inventory page

  # TC_LOGIN_008 | FR-07
  @regression @Key:TC_LOGIN_008
  Scenario: error_user logs in successfully
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user is redirected to the inventory page

  # TC_LOGIN_009 | FR-07
  @regression @Key:TC_LOGIN_009
  Scenario: visual_user logs in successfully
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user is redirected to the inventory page

  # TC_LOGIN_010 | FR-08 | Partial — real-time 10-minute wait is impractical for CI (GAP-FR-03);
  # documented here structurally per the standard's Partial-candidate convention, not executed
  # against a real 10-minute idle period. Excluded from @smoke/@regression.
  @partial @Key:TC_LOGIN_010
  Scenario: Session auto-expires after inactivity (documented, not executed with a real 10-minute wait)
    Given user is on the SauceDemo login page
    When user logs in with username "<Username>" and password "<Password>"
    Then user is redirected to the inventory page
    And the session-timeout behavior is documented as pending a dedicated long-running environment
