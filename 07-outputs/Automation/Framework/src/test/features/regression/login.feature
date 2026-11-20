@Login @regression
Feature: Login Functionality

  @login @smoke @Key:TC001
  Scenario: Verify successful login with valid credentials
    Given User is on the main landing page
    When User clicks on the Login to Portal button
    And User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then The button state changes to Signing in
    And A toast notification "Login successful!" appears
    And User is redirected to the Dashboard

  @logout @Key:TC002
  Scenario: Verify user logout functionality
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected to the Dashboard
    When User clicks on the profile avatar at the top right
    And User clicks on the Log out button
    Then User is logged out and redirected to the login page
    And A toast notification "Logged out successfully" appears

  @login @negative @Key:TC003
  Scenario: Verify login attempt with invalid email and valid password
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then A generic error message "Invalid username or password" is displayed
    And User remains on the login page

  @login @negative @Key:TC004
  Scenario: Verify login attempt with valid email and invalid password
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then A generic error message "Invalid username or password" is displayed
    And User remains on the login page

  @login @validation @Key:TC005
  Scenario: Verify login attempt with both email and password fields empty
    Given User navigates to the login page
    When User leaves Email and Password fields completely blank
    And User clicks on the Sign In button
    Then A validation error prompts user to enter email and password
    And The login request is not sent

  @login @validation @Key:TC006
  Scenario: Verify login attempt with empty email field
    Given User navigates to the login page
    When User leaves Email field completely blank
    And User enters password "<password>"
    And User clicks on the Sign In button
    Then A validation error requesting the user to enter an email address is displayed

  @login @validation @Key:TC007
  Scenario: Verify login attempt with empty password field
    Given User navigates to the login page
    When User enters email "<username>"
    And User leaves Password field completely blank
    And User clicks on the Sign In button
    Then A validation error requesting the user to enter a password is displayed

  @login @validation @Key:TC008a @Key:TC008b
  Scenario: Verify email field accepts valid standard email formats
    Given User navigates to the login page
    When User enters email "<username>"
    Then No client-side validation errors are thrown for the email format

  @login @validation @Key:TC009a @Key:TC009b @Key:TC009c
  Scenario: Verify email field rejects invalid email formats
    Given User navigates to the login page
    When User enters email "<username>"
    And User clicks on the Sign In button
    Then A client-side validation error is displayed indicating invalid email format

  @login @validation @Key:TC010
  Scenario: Verify email field trims leading spaces before submission
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then The leading spaces are trimmed automatically
    And User is logged in successfully

  @login @validation @Key:TC011
  Scenario: Verify email field trims trailing spaces before submission
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then The trailing spaces are trimmed automatically
    And User is logged in successfully

  @login @security @Key:TC012
  Scenario: Verify password field input is masked by default
    Given User navigates to the login page
    When User enters password "<password>"
    Then Characters are replaced by bullets or asterisks immediately as typed

  @login @ui @Key:TC013
  Scenario: Verify password toggle (eye icon) unmasks the password
    Given User navigates to the login page
    When User enters password "<password>"
    And User clicks the eye icon inside the Password field
    Then Password becomes visible in plain text
    And The eye icon state changes to open

  @login @ui @Key:TC014
  Scenario: Verify password toggle (eye icon) masks the password again
    Given User navigates to the login page
    When User enters password "<password>"
    And User clicks the eye icon inside the Password field
    And User clicks the eye icon again
    Then Password is masked again with bullets or asterisks
    And The eye icon returns to closed state

  @login @security @Key:TC015
  Scenario: Verify password field preserves trailing and leading spaces
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is logged in successfully

  @login @usability @Key:TC016
  Scenario: Verify submission using the Enter key
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User presses the Enter key on the keyboard
    And User is logged in successfully

  @login @functional @Key:TC017
  Scenario: Verify double-click prevention on Sign In button
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User rapidly double-clicks the Sign In button
    Then The button enters a disabled loading state
    And Only one authentication API request is sent to the server

  @login @navigation @Key:TC018
  Scenario: Verify Forgot Password link visibility and navigation
    Given User navigates to the login page
    When User clicks the Forgot Password link
    Then User is redirected to the Forgot Password page

  @login @ui @Key:TC019
  Scenario: Verify RanchOps Pro logo and card header elements rendering
    Given User navigates to the login page
    Then The RanchOps Pro logo renders correctly
    And The main heading "RanchOps Pro" and subtitle "Sign in to your account" render correctly

  @login @accessibility @Key:TC020
  Scenario: Verify tab key navigation sequence on Login page
    Given User navigates to the login page
    When User focuses on the top element and presses Tab repeatedly
    Then Focus moves sequentially through Email, Password, Eye icon, Sign In, and Forgot Password
    And Active controls display focus rings

  @login @accessibility @Key:TC021
  Scenario: Verify focus state styling on Email and Password fields
    Given User navigates to the login page
    When User clicks into the Email field
    Then A visible green focus border highlight appears around the Email field
    When User presses Tab to focus the Password field
    Then A visible green focus border highlight appears around the Password field

  @login @usability @Key:TC022
  Scenario: Verify copy and paste functionality into Email field
    Given User navigates to the login page
    When User copies email "<username>" to clipboard
    And User pastes text into the Email field
    Then Email "<username>" is pasted successfully and accurately into the field

  @login @usability @Key:TC023
  Scenario: Verify copy and paste functionality into Password field
    Given User navigates to the login page
    When User copies password "<password>" to clipboard
    And User pastes text into the Password field
    Then Password "<password>" is pasted successfully and remains masked

  @login @ui @responsive @Key:TC026
  Scenario: Verify responsive layout on Tablet screens (768px width)
    Given User navigates to the login page
    When User resizes browser window to tablet width 768px
    Then Login card resizes appropriately and is centered
    And Text and fields remain readable without horizontal scroll

  @login @ui @responsive @Key:TC027
  Scenario: Verify responsive layout on Mobile screens (375px width)
    Given User navigates to the login page
    When User resizes browser window to mobile width 375px
    Then Login card scales to fit mobile screen width
    And Fields and buttons remain fully touch-accessible

  @login @accessibility @Key:TC032
  Scenario: Verify form submission with space key on the Sign In button
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User tabs to focus on the Sign In button
    And User presses the Spacebar key on keyboard
    Then The login form is submitted
    And User is logged in successfully


  @login @workflow @Key:TC034
  Scenario: Verify browser Back button behavior after successful login
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is logged in successfully
    When User clicks the browser Back button
    Then User is not shown the login page and session remains active

  @login @accessibility @Key:TC035
  Scenario: Verify screen reader reads Email field label correctly
    Given User navigates to the login page
    When User focuses on the Email field
    Then Screen reader can read the Email field label and properties correctly

  @login @accessibility @Key:TC036
  Scenario: Verify screen reader reads Password field label correctly
    Given User navigates to the login page
    When User focuses on the Password field
    Then Screen reader can read the Password field label and properties correctly

  @login @ui @Key:TC037
  Scenario: Verify error message disappears when user corrects input
    Given User navigates to the login page
    When User clicks on the Sign In button with blank fields
    Then A validation error requesting the user to enter an email address is displayed
    When User starts typing valid text into the Email field
    Then The validation error message is cleared dynamically

  @login @boundary @Key:TC038
  Scenario: Verify handling of extremely long email input
    Given User navigates to the login page
    When User pastes 1000 characters into the Email field
    And User clicks on the Sign In button
    Then Application handles the extremely long input gracefully

  @login @error-handling @Key:TC039
  Scenario: Verify network failure handling during login
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And Internet connection is disconnected
    And User clicks on the Sign In button
    Then A network error message is displayed

#  @login @negative @Key:TC040
#  Scenario: Verify login using a deleted/deactivated user account
#    Given User navigates to the login page
#    When User enters deactivated account credentials "<username>" and "<password>"
#    And User clicks on the Sign In button
#    Then Login fails and account deactivated error is shown

  @login @navigation @Key:TC041
  Scenario: Verify Back to Login button navigation from Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    When User clicks the Back to Login button
    Then User is redirected back to the main Login page

  @login @validation @Key:TC042
  Scenario: Verify empty email validation on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    When User leaves the Forgot Password Email field completely blank
    And User clicks the Send Reset Code button
    Then A validation error prompts user to enter an email address

  @login @validation @Key:TC043
  Scenario: Verify invalid email format validation on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    When User enters invalid email string "<username>" in Forgot Password Email field
    And User clicks the Send Reset Code button
    Then A client-side validation error is displayed indicating invalid email format

  @login @functional @Key:TC044
  Scenario: Verify successful submission with valid registered email on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    When User enters valid registered email "<username>" in Forgot Password Email field
    And User clicks the Send Reset Code button
    Then User is redirected to the Check Your Email screen

  @login @functional @Key:TC045
  Scenario: Verify submission with unregistered email on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    When User enters unregistered email "<username>" in Forgot Password Email field
    And User clicks the Send Reset Code button
    Then User is redirected to the Check Your Email screen

  @Key:TC046
  Scenario Outline: Verify email field trims spaces on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    And User enters email with spaces " <username> " in Forgot Password Email field
    And User clicks the Send Reset Code button
    Then Email spaces are trimmed automatically before submission

    Examples:
      | Key   |
      | TC046 |

  @Key:TC047
  Scenario Outline: Verify Enter key submission on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    And User enters valid registered email "<username>" in Forgot Password Email field
    And User presses the Enter key on the Forgot Password Email field
    Then User is redirected to the Check Your Email screen

    Examples:
      | Key   |
      | TC047 |

  @Key:TC048
  Scenario Outline: Verify double-click prevention on Send Reset Code button
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    And User enters valid registered email "<username>" in Forgot Password Email field
    And User rapidly double-clicks the Send Reset Code button
    Then User is redirected to the Check Your Email screen
    And Only one password reset API request is sent to the server

    Examples:
      | Key   |
      | TC048 |

  @Key:TC049
  Scenario: Verify tab key navigation sequence on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    And User focuses on the Forgot Password Email field and presses Tab repeatedly
    Then Focus moves sequentially through Email, Send Reset Code, and Back to Login

  @Key:TC050
  Scenario: Verify responsive layout on Mobile screens for Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    And User resizes browser window to mobile width 375px
    Then Forgot password card scales to fit mobile screen width

  @Key:TC051
  Scenario: Verify password field is strictly case-sensitive
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then A generic error message "Invalid username or password" is displayed

  @Key:TC052
  Scenario: Verify redirection to originally requested protected URL after login
    Given User navigates directly to a protected route "/dashboard"
    Then User is redirected to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected directly back to originally requested protected page "/dashboard"

  @Key:TC053
  Scenario: Verify rate limiting on Forgot Password reset requests
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    And User enters valid registered email "<username>" in Forgot Password Email field
    And User submits the reset form 5 times in rapid succession
    Then System blocks further requests temporarily and displays rate-limit error

  @Key:TC056
  Scenario: Verify Session Termination upon browser close
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected to the Dashboard
    When User closes the browser window completely
    And User opens a new browser window and navigates to "/dashboard"
    Then User is redirected to the login page

  @Key:TC057
  Scenario: Verify Placeholder text rendering in input fields
    Given User navigates to the login page
    Then The Email input displays placeholder "your.email@example.com"
    And The Password input displays placeholder "•••••••••"
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    Then The Forgot Password Email input displays placeholder "your.email@example.com"

  @Key:TC058
  Scenario: Verify Toast Alert notification rendering and auto-dismissal
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then A toast notification "Login successful!" appears
    And The toast notification automatically dismisses after a few seconds
