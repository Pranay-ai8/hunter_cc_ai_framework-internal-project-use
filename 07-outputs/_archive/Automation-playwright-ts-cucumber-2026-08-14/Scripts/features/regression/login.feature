Feature: Authentication - Regression

  # TC_LOGIN_002
  @DataFile:resources/data/logindata.csv @Key:locked_out_login @regression
  Scenario: locked_out_user is blocked with the documented lock-out error
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then login should be rejected with the error "td.expectedError"
    And the user should remain on the login page

  # TC_LOGIN_003
  @DataFile:resources/data/logindata.csv @Key:blank_username @regression
  Scenario: Blank username shows the required-field error
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then login should be rejected with the error "td.expectedError"
    And the user should remain on the login page

  # TC_LOGIN_004
  @DataFile:resources/data/logindata.csv @Key:blank_password @regression
  Scenario: Blank password shows the required-field error
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then login should be rejected with the error "td.expectedError"
    And the user should remain on the login page

  # TC_LOGIN_005
  @DataFile:resources/data/logindata.csv @Key:unknown_credentials @regression
  Scenario: Unknown username/password combination is rejected
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then login should be rejected with the error "td.expectedError"
    And the user should remain on the login page

  # TC_LOGIN_006
  @DataFile:resources/data/logindata.csv @Key:performance_glitch_login @regression
  Scenario: performance_glitch_user logs in with an artificial delay
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data and measures render time
    Then the render time should be at least 2000 ms slower than a standard login

  # TC_LOGIN_007
  @DataFile:resources/data/logindata.csv @Key:problem_user_login @regression
  Scenario: problem_user logs in successfully
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then the user should be redirected to the inventory page

  # TC_LOGIN_008
  @DataFile:resources/data/logindata.csv @Key:error_user_login @regression
  Scenario: error_user logs in successfully
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then the user should be redirected to the inventory page

  # TC_LOGIN_009
  @DataFile:resources/data/logindata.csv @Key:visual_user_login @regression
  Scenario: visual_user logs in successfully
    Given the user is on the SauceDemo login page
    When the user logs in using the resolved test data
    Then the user should be redirected to the inventory page

  # TC_LOGIN_010 — Partial automation candidate (GAP-FR-03: exact post-timeout UX undocumented;
  # a real 10-minute wait is impractical for CI). Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/logindata.csv @Key:session_timeout @inprogress
  Scenario: Session auto-expires after inactivity (reduced stand-in wait — see docs/test-suites)
    Given the user is on the SauceDemo login page
    Given the user remains idle for 10 minutes after logging in
    Then attempting an action should redirect the user back to the login page
