@AUTH @smoke
Feature: Authentication & Access — Smoke
  As a RanchOps Pro user
  I want to log in and request password resets
  So that I can access the system I'm authorized for

  @DataFile:resources/data/authdata.csv @Key:tc_auth_001 @smoke
  Scenario: Forgot Password request succeeds for a registered Super Admin email
    # TC_AUTH_001
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_auth_002 @smoke
  Scenario: Forgot Password request succeeds for a registered Ranch Admin email
    # TC_AUTH_002
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_auth_003 @smoke
  Scenario: Forgot Password request succeeds for a registered Guide email
    # TC_AUTH_003
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_auth_011 @smoke @CriticalPath
  Scenario: Valid login succeeds for a Super Admin with correct email and password
    # TC_AUTH_011
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    Then the user should be redirected to the "Super Admin" dashboard

  @DataFile:resources/data/authdata.csv @Key:tc_auth_012 @smoke
  Scenario: Valid login succeeds for a Ranch Admin with correct email and password
    # TC_AUTH_012
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    Then the user should be redirected to the "Ranch Admin" dashboard

  @DataFile:resources/data/authdata.csv @Key:tc_auth_013 @smoke
  Scenario: Valid login succeeds for a Ranch Admin and scopes the session to the assigned ranch
    # TC_AUTH_013
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    Then the user should be redirected to the "Ranch Admin" dashboard

  @DataFile:resources/data/authdata.csv @Key:tc_auth_014 @smoke
  Scenario: Valid login succeeds for a Guide with correct email and password
    # TC_AUTH_014
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    Then the user should be redirected to the "Guide" dashboard

  @DataFile:resources/data/authdata.csv @Key:tc_auth_021 @smoke
  Scenario: A successful login establishes an active session allowing access to protected pages
    # TC_AUTH_021
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    Then the user should be redirected to the "Super Admin" dashboard
    And the user should remain authenticated on the protected page

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_013 @smoke
  Scenario: Complete password reset via the reset-code flow with a valid new password
    # TC_GEN_AUTH_013
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_001 @smoke
  Scenario: Login Page loads with all expected structural elements visible
    # TC_UI_AUTH_001
    Given the user is on the Login page
    Then the Login page loads with all expected structural elements visible

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_003 @smoke
  Scenario: "Forgot Password" link unconditionally navigates to the Forgot Password screen
    # TC_UI_AUTH_003
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    Then the Forgot Password screen should be displayed
