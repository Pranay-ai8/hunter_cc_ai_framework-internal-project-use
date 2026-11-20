Feature: SauceDemo - Authentication

  # TC_LOGIN_001
  @Key:ValidLogin @smoke
  Scenario: Successful login with standard_user
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then the user should be redirected to the Inventory page

  # TC_LOGIN_002
  @Key:LockedOutLogin @regression
  Scenario: locked_out_user is blocked
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then an error message containing "Sorry, this user has been locked out." should be displayed

  # TC_LOGIN_003
  @Key:BlankUsername @regression
  Scenario: Blank username shows required error
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then an error message containing "Username is required" should be displayed

  # TC_LOGIN_004
  @Key:BlankPassword @regression
  Scenario: Blank password shows required error
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then an error message containing "Password is required" should be displayed

  # TC_LOGIN_005
  @Key:InvalidCredentials @regression
  Scenario: Unknown credentials rejected
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then an error message containing "Username and password do not match any user in this service" should be displayed

  # TC_LOGIN_006
  @Key:PerformanceGlitchLogin @regression
  Scenario: performance_glitch_user login has an artificial delay
    Given the user is on the SauceDemo login page
    When the user times the login with username "<Username>" and password "<Password>"
    Then the user should be redirected to the Inventory page
    And the login should have taken measurably longer than a standard login

  # TC_LOGIN_007
  @Key:ProblemUserLogin @regression
  Scenario: problem_user logs in successfully
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then the user should be redirected to the Inventory page

  # TC_LOGIN_008
  @Key:ErrorUserLogin @regression
  Scenario: error_user logs in successfully
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then the user should be redirected to the Inventory page

  # TC_LOGIN_009
  @Key:VisualUserLogin @regression
  Scenario: visual_user logs in successfully
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    Then the user should be redirected to the Inventory page

  # TC_LOGIN_010 — Automation Candidate: Partial. A real 10-minute wait is
  # impractical for CI (GAP-FR-03). Kept as a manual/long-running check.
  @Key:ValidLogin @manual
  Scenario: Session auto-expires after 10 minutes of inactivity
    Given the user is on the SauceDemo login page
    When the user logs in with username "<Username>" and password "<Password>"
    And the user waits 10 minutes without interacting with the page
    Then the session should have expired requiring re-authentication
