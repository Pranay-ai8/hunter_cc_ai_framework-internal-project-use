@AUTH @regression
Feature: Authentication & Access — Regression
  As a RanchOps Pro user
  I want login, session and password-reset/change behavior to be correct
  So that access control and account recovery work reliably

  # ---------- Forgot Password (unauthenticated) ----------

  @DataFile:resources/data/authdata.csv @Key:tc_auth_004 @regression @Negative
  Scenario: Forgot Password request for an unregistered email does not grant reset access
    # TC_AUTH_004
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then no reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_auth_005 @regression @Negative
  Scenario: Forgot Password request blocked when email field is left blank
    # TC_AUTH_005
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user submits the Forgot Password form with the email left blank
    Then a mandatory-field validation error should be displayed for the "Email" field

  @DataFile:resources/data/authdata.csv @Key:tc_auth_006 @regression @Negative
  Scenario: Forgot Password request blocked when email format is invalid
    # TC_AUTH_006
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user submits the Forgot Password form with an invalid-format email
    Then a format-validation error should be displayed for the "Email" field

  # ---------- Login (password-based) ----------

  @DataFile:resources/data/authdata.csv @Key:tc_auth_015 @regression @Negative @Security
  Scenario: Login is rejected for an unregistered/non-existent email
    # TC_AUTH_015
    Given the user is on the Login page
    When the user enters email "nonexistent.user@ranchops-test.com" and password "AnyPass123!" and clicks "Sign In"
    Then a generic authentication-failure message should be displayed
    And the user should remain on the Login page

  @DataFile:resources/data/authdata.csv @Key:tc_auth_016 @regression @Negative
  Scenario: Login is rejected when the email is valid but the password is incorrect
    # TC_AUTH_016
    Given the user is on the Login page
    When the user logs in with the registered email but an incorrect password
    Then a generic authentication-failure message should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_auth_017 @regression @Negative
  Scenario: Login is blocked when the Email field is left blank
    # TC_AUTH_017
    Given the user is on the Login page
    When the user enters email "" and password "Pass0123!" and clicks "Sign In"
    Then a mandatory-field validation error should be displayed for the "Email" field

  @DataFile:resources/data/authdata.csv @Key:tc_auth_018 @regression @Negative
  Scenario: Login is blocked when the Password field is left blank
    # TC_AUTH_018
    Given the user is on the Login page
    When the user enters email "superadmin.qa@ranchops-test.com" and password "" and clicks "Sign In"
    Then a mandatory-field validation error should be displayed for the "Password" field

  @DataFile:resources/data/authdata.csv @Key:tc_auth_019 @regression @Negative
  Scenario: Login is blocked when the Email field contains an invalid format
    # TC_AUTH_019
    Given the user is on the Login page
    When the user enters email "superadmin.qa" and password "Pass0123!" and clicks "Sign In"
    Then a format-validation error should be displayed for the "Email" field

  @DataFile:resources/data/authdata.csv @Key:tc_auth_020 @regression @UI
  Scenario: Password field is masked by default on the Login page
    # TC_AUTH_020
    Given the user is on the Login page
    When the user enters email "superadmin.qa@ranchops-test.com" and password "Pass0123!" and clicks "Sign In"
    Then the Password field should be masked by default

  # ---------- Session Establishment ----------

  @DataFile:resources/data/authdata.csv @Key:tc_auth_022 @regression
  Scenario: An established session persists across a browser refresh
    # TC_AUTH_022
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    And the user refreshes the browser
    Then the user should remain authenticated on the protected page

  @DataFile:resources/data/authdata.csv @Key:tc_auth_023 @regression @Security
  Scenario: Logging out terminates the session; Back navigation redirects to Login
    # TC_AUTH_023
    Given the user is on the Login page
    When the user logs in with the credentials from the test data
    And the user logs out
    And the user navigates back using the browser Back button
    Then the user should be redirected to the Login page

  @DataFile:resources/data/authdata.csv @Key:tc_auth_024 @regression @Security
  Scenario: Direct URL navigation to a protected route while unauthenticated redirects to Login
    # TC_AUTH_024
    Given the user is not authenticated
    When the user navigates directly to the dashboard URL
    Then the user should be redirected to the Login page

  # ---------- Generic: Authenticated Password Change (Profile) ----------

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_002 @regression @Negative
  Scenario: Attempt authenticated password change with an incorrect current password
    # TC_GEN_AUTH_002
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change with an incorrect current password
    Then the password change should be blocked with a validation error

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_003 @regression @Negative
  Scenario: Attempt authenticated password change without entering the current password
    # TC_GEN_AUTH_003
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change leaving the current password field blank
    Then the password change should be blocked with a validation error

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_004 @regression @Boundary
  Scenario: Attempt to set a new password below the confirmed minimum length
    # TC_GEN_AUTH_004
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change with a new password below the minimum length
    Then the password change should be blocked with a validation error

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_005 @regression @Negative
  Scenario: Confirm-password value does not match the new-password value during change
    # TC_GEN_AUTH_005
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change with mismatched new and confirm password values
    Then the password change should be blocked with a validation error

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_006 @regression @Resilience
  Scenario: Browser refresh mid-way through the password-change form discards partial data
    # TC_GEN_AUTH_006
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user refreshes the browser
    Then the authenticated Change Password screen should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_008 @regression @UI
  Scenario: Password visibility toggle works on the authenticated Change Password form
    # TC_GEN_AUTH_008
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user toggles the password visibility
    Then the password should be shown in plain text

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_009 @regression @UI
  Scenario: Confirmation message is displayed upon successful authenticated password change
    # TC_GEN_AUTH_009
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change using the test data current, new and confirm password
    Then the password change should succeed with a confirmation message

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_017 @regression
  Scenario: New password containing special characters is accepted during change
    # TC_GEN_AUTH_017
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change using the test data current, new and confirm password
    Then the password change should succeed with a confirmation message

  # ---------- Generic: Unauthenticated Password Reset (code flow) ----------

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_001 @regression
  Scenario: Initiate password reset via "Forgot Password" from the login page
    # TC_GEN_AUTH_001
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_012 @regression
  Scenario: Receive the password-reset code following a valid forgot-password request
    # TC_GEN_AUTH_012
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_014 @regression @Negative
  Scenario: Reset flow rejects mismatched new/confirm password values
    # TC_GEN_AUTH_014
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    And the user submits a password change with mismatched new and confirm password values
    Then the password change should be blocked with a validation error

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_015 @regression @Negative @Boundary
  Scenario: Attempt to use an expired password-reset code
    # TC_GEN_AUTH_015
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_016 @regression @Negative
  Scenario: Attempt to reuse an already-used password-reset code
    # TC_GEN_AUTH_016
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_019 @regression @Resilience
  Scenario: Browser tab closed mid-way through the Forgot Password form is not retained
    # TC_GEN_AUTH_019
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user refreshes the browser
    Then the Forgot Password screen should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_020 @regression @Resilience
  Scenario: Browser minimized/tab switched mid-reset is handled securely on return
    # TC_GEN_AUTH_020
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    Then the Forgot Password screen should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_021 @regression
  Scenario: Reset link requested in one session functions independent of the originating session
    # TC_GEN_AUTH_021
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_gen_auth_022 @regression @Email
  Scenario: Clicking the password-reset email link navigates to the correct Reset Password screen
    # TC_GEN_AUTH_022
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user requests a password reset code using the test data email
    Then a reset-code confirmation should be displayed

  # ---------- UI: Labels, Mandatory Indicators, Error Presentation ----------

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_002 @regression
  Scenario: Forgot Password screen loads with expected structural elements visible
    # TC_UI_AUTH_002
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    Then the Forgot Password screen loads with all expected structural elements visible

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_004 @regression
  Scenario: "Reset Password" option unconditionally navigates to the Reset Password screen
    # TC_UI_AUTH_004
    Given the user is logged in as a "Super Admin" user
    When the user navigates to Profile and selects "Change Password"
    Then the authenticated Change Password screen should be displayed

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_005 @regression
  Scenario: "Back to Login" link unconditionally returns the user to the Login screen
    # TC_UI_AUTH_005
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    And the user clicks "Back to Login"
    Then the user should be returned to the Login page

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_006 @regression @AccessibilityBasic
  Scenario: Email field has a visible, correctly associated label
    # TC_UI_AUTH_006
    Given the user is on the Login page
    Then the "Email" field should have a visible, correctly associated label

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_007 @regression @AccessibilityBasic
  Scenario: Password field has a visible, correctly associated label
    # TC_UI_AUTH_007
    Given the user is on the Login page
    Then the "Password" field should have a visible, correctly associated label

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_008 @regression
  Scenario: Email field displays a mandatory-field indicator on the Forgot Password screen
    # TC_UI_AUTH_008
    Given the user is on the Login page
    When the user clicks "Forgot/Reset Password?"
    Then a mandatory-field indicator should be visible next to the Email field on the Forgot Password screen

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_009 @regression
  Scenario: "Sign In" button renders visible and in its default enabled presentation on page load
    # TC_UI_AUTH_009
    Given the user is on the Login page
    Then the "Sign In" button should be visible and rendered in its default state

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_010 @regression
  Scenario: Authentication-failure banner is visible and positioned near the login form
    # TC_UI_AUTH_010
    Given the user is on the Login page
    When the user logs in with the registered email but an incorrect password
    Then the authentication-failure message should be visible and positioned near the Sign In form

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_011 @regression @Responsive
  Scenario: Login form remains fully usable at mobile viewport (375px)
    # TC_UI_AUTH_011
    Given the viewport is set to 375 by 667
    And the user is on the Login page
    Then the Login page fields and Sign In button should be visible without horizontal overflow

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_012 @regression @Responsive
  Scenario: Login form remains fully usable at desktop viewport (1280px)
    # TC_UI_AUTH_012
    Given the viewport is set to 1280 by 800
    And the user is on the Login page
    Then the Login page fields and Sign In button should be visible without horizontal overflow

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_013 @regression @CrossBrowser
  Scenario: Login page renders and functions consistently across Chrome, Firefox, Safari and Edge
    # TC_UI_AUTH_013
    Given the user is on the Login page
    Then the Login page should render and function consistently across Chrome, Firefox, Safari and Edge

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_014 @regression @AccessibilityBasic
  Scenario: Tab key moves focus through fields and controls in a logical order
    # TC_UI_AUTH_014
    Given the user is on the Login page
    When the user presses Tab repeatedly from page load
    Then focus should move through the Login page controls in a logical, keyboard-reachable order

  @DataFile:resources/data/authdata.csv @Key:tc_ui_auth_016 @regression @AccessibilityBasic
  Scenario: Login page has a descriptive browser title
    # TC_UI_AUTH_016
    Given the user is on the Login page
    Then the page title should be descriptive
