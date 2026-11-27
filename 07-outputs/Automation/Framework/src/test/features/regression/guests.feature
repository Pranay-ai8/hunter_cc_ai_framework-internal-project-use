@regression
@Guests
Feature: Guests Directory & Profile Management

  @UI @High @Functional @Key:TC_GST_001
  Scenario: TC_GST_001 Verify filtering Guest Directory by Guest Type
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User selects "Hunters" from the Guest Type dropdown
    Then The table should update to display guests of type "Hunters"
    When User selects "Non-Hunter Guests" from the Guest Type dropdown
    Then The table should update to display guests of type "Guest"

  @UI @High @Functional @Key:TC_GST_002
  Scenario: TC_GST_002 Verify filtering Guest Directory by Ranch selection
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User selects "CONFIGURED_RANCH" from the Ranch dropdown
    Then The grid should filter to guests belonging to "CONFIGURED_RANCH"
    When User selects "All Ranches" from the Ranch dropdown
    Then Selecting "All Ranches" should reset the ranch filter

  @UI @Medium @Low @UI @Key:TC_GST_003
  Scenario: TC_GST_003 Verify Guest Directory layout view toggle between Table and Card View
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User clicks the Card View toggle icon on Guest Directory
    Then The Guest Directory should display in Card layout
    When User clicks the Table View toggle icon on Guest Directory
    Then The Guest Directory should display in Table layout

  @UI @High @Critical @FieldValidation @Negative @Key:TC_GST_004
  Scenario: TC_GST_004 Validate Edit Guest Profile mandatory field validations
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User clears the Full Name input field
    And User clears the Email Address input field
    And User clicks the guest form "Save Changes" button
    Then Submission should be blocked with an inline error under Full Name

  @UI @High @Critical @Functional @StateTransition @Key:TC_GST_005
  Scenario: TC_GST_005 Verify successful update of Guest Profile details
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User modifies Date of Birth and Address with "CONFIGURED_DATA"
    And User clicks the guest form "Save Changes" button
    Then The Save button should show a Saving state
    And User should be redirected to the Guest Profile summary page
    And A success toast "CONFIGURED_MESSAGE" should display

  @UI @High @Security @Positive @Key:TC_GST_006
  Scenario: TC_GST_006 Validate Document upload and profile update workflow
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    When User selects Document Type "ID Proof"
    And User uploads file "id_proof.pdf"
    Then The document "id_proof.pdf" should attach successfully
    When User clicks "Save Changes" button
    Then Guest profile update success message should display

  @UI @Medium @Functional @Key:TC_GST_007
  Scenario: TC_GST_007 Verify SMS consent checkbox behavior and persistence
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    When User toggles the SMS consent checkbox on guest edit form
    And User clicks the guest form "Save Changes" button
    And User clicks "Edit Profile" on the guest summary page
    Then The SMS consent checkbox state should persist as updated

  @UI @High @Medium @Functional @Key:TC_GST_008
  Scenario: TC_GST_008 Validate Hunter License Information fields for Hunter guest type
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on hunter guest row "CONFIGURED_HUNTER"
    And User enters License Number "TX-998822" and License Expiration Date "12/31/2026"
    And User clicks the guest form "Save Changes" button
    Then The Hunter License Information should display License "TX-998822" and Expiration "12/31/2026" on the profile summary

  @UI @High @Navigation @Key:TC_GST_009
  Scenario: TC_GST_009 Verify "Back to Guest Details" and "Back to Guests" breadcrumb navigation
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User clicks the "Back to Guest Details" breadcrumb link
    Then User should be on the Guest Profile summary view
    When User clicks the "Back to Guests" breadcrumb link
    Then User should be on the main Guest Directory page

  @UI @Medium @Functional @Key:TC_GST_010
  Scenario: TC_GST_010 Validate Emergency Contact section field inputs and persistence
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User fills Contact Name "Jane Hunter", Contact Phone "+1 555-000-0003", and Relationship "Spouse"
    Then User clicks the guest form "Save Changes" button

  @UI @Medium @Functional @Key:TC_GST_011
  Scenario: TC_GST_011 Verify Search input real-time filtering by Partial Name, Email, and Phone
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User types "hunter@qdr" in the guest search bar
    Then The table grid should update dynamically without a full page reload

  @UI @High @Critical @Security @Negative @Key:TC_GST_012
  Scenario: TC_GST_012 Validate Search box SQL injection & Script Payload handling (Security)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User enters payload "' OR '1'='1" in the search input box
    Then The search should handle the input safely without JS execution or a server error
    When User enters payload "<script>alert('XSS')</script>" in the search input box
    Then The search should handle the input safely without JS execution or a server error

  @UI @High @Critical @FieldValidation @Negative @Key:TC_GST_013
  Scenario: TC_GST_013 Validate Email field format syntax validation on Edit Guest Profile (Negative)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters guest email "plainaddress"
    And User clicks the guest form "Save Changes" button
    Then An invalid email format error should display under the email field

  @UI @High @Functional @Key:TC_GST_014
  Scenario: TC_GST_014 Validate Duplicate Email Prevention across Guest Profiles (Business Logic)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters duplicate email "CONFIGURED_EMAIL" on guest form
    And User clicks the guest form "Save Changes" button
    Then Submission should fail with a duplicate email error for guest module

  @UI @Medium @Positive @Key:TC_GST_015
  Scenario: TC_GST_015 Validate Phone Number input rejects non-numeric characters
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    When User attempts to enter non-numeric phone number "abcdef"
    Then The phone number input field should reject non-numeric characters and remain empty

  @UI @High @Negative @BusinessLogic @Key:TC_GST_016
  Scenario: TC_GST_016 Validate Date of Birth Future Date Restriction in Personal Information (Negative)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters Date of Birth "08/25/2030"
    And User clicks the guest form "Save Changes" button
    Then The future date of birth should be blocked

  @UI @Medium @Functional @Key:TC_GST_017
  Scenario: TC_GST_017 Validate ZIP Code Boundary Value Analysis (BVA) & format checks
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    When User enters 5-digit ZIP code "78701" on guest form
    Then The ZIP code field should accept "78701" as valid
    When User enters 9-digit ZIP code "78701-1234" on guest form
    Then The ZIP code field should accept "78701-1234" as valid
    When User enters invalid ZIP code "ABCDE" on guest form
    And User clicks the guest form "Save Changes" button
    Then An invalid ZIP code error "Invalid ZIP code format" should display under the ZIP field

  @UI @High @BusinessLogic @Negative @Key:TC_GST_018
  Scenario: TC_GST_018 Validate Hunter License Expiration Date Past Date Check (Negative / Warning)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters License Expiration Date "01/01/2020"
    And User clicks the guest form "Save Changes" button
    Then A License Expired warning should display

  @UI @Medium @Low @Usability @Key:TC_GST_019
  Scenario: TC_GST_019 Verify Document Upload Drag and Drop functionality
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    When User selects Document Type "ID Proof"
    And User drags file "passport_copy.pdf" into the upload dropzone
    Then The document "passport_copy.pdf" should attach successfully

  @UI @Medium @Negative @BusinessLogic @Key:TC_GST_020
  Scenario: TC_GST_020 Validate Document Upload Duplicate File Attachment prevention
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    When User selects Document Type "License"
    And User uploads file "license_copy.pdf"
    And User uploads file "license_copy.pdf"
    Then A duplicate document attachment error should display

  @UI @Medium @StateTransition @Key:TC_GST_021
  Scenario: TC_GST_021 Validate Cancel button behavior on Edit Guest Profile form
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User changes Full Name and Address to "CONFIGURED_DATA"
    And User clicks the guest form "Cancel" button
    Then The form should discard changes and return to the Guest Profile summary with original data "CONFIGURED_GUEST"

  @UI @High @Performance @EdgeCase @Key:TC_GST_022
  Scenario: TC_GST_022 Validate Rapid Double-Click on "Save Changes" button (Race Condition / Performance)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters phone number "CONFIGURED_PHONE"
    And User rapidly double-clicks the Save Changes button
    Then The Save Changes button should disable immediately with a Saving state

  @UI @Low @UI @Key:TC_GST_023
  Scenario: TC_GST_023 Verify Guest Directory Pagination and Records-Per-Page selection
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User changes the results per page dropdown to "20"
    Then The results summary text should display "Showing 1 to 2 of 2 results"

  @UI @Medium @DataValidation @Key:TC_GST_024
  Scenario: TC_GST_024 Validate Full Name input leading/trailing whitespace trim handling
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters Full Name "CONFIGURED_UNTRIMMED_NAME"
    And User clicks the guest form "Save Changes" button
    Then The profile summary should display the trimmed name "CONFIGURED_TRIMMED_NAME"

  @UI @Medium @EdgeCase @Key:TC_GST_025
  Scenario: TC_GST_025 Validate Full Name Support for Unicode & International Accented Characters
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters Full Name "CONFIGURED_UNICODE_NAME"
    And User clicks the guest form "Save Changes" button
    Then The profile summary should render the Unicode name "CONFIGURED_UNICODE_NAME" correctly

  @UI @High @Critical @Security @Key:TC_GST_026
  Scenario: TC_GST_026 Validate XSS payload sanitization across all text input fields (Security)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters payload "<script>alert('XSS')</script>" in Address, Emergency Contact Name, or License fields
    And User clicks the guest form "Save Changes" button
    Then The script payload should render as sanitized plain text on the profile summary

  @UI @High @Functional @Key:TC_GST_028
  Scenario: TC_GST_028 Verify "Signed Waiver" PDF preview/download link on Guest Profile summary
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User clicks on completed guest row "CONFIGURED_GUEST"
    And User clicks the signed waiver PDF preview button
    Then The signed waiver PDF should open or download successfully

  @UI @Medium @Low @Visual @Key:TC_GST_029
  Scenario: TC_GST_029 Verify Signed Waiver empty state when waiver is missing/not submitted
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User clicks on pending guest row "CONFIGURED_GUEST"
    Then The Signed Waiver card should display a not-available message "CONFIGURED_EMPTY_MESSAGE"

  @UI @Medium @Accessibility @Key:TC_GST_031
  Scenario: TC_GST_031 Verify Keyboard Accessibility & Focus order (a11y) on Edit Guest Profile form
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User presses Tab key repeatedly from the page top
    Then Focus should move logically through all Edit Guest Profile form fields

  @UI @Medium @Responsive @Key:TC_GST_032
  Scenario: TC_GST_032 Verify Guest Profile responsiveness on Mobile and Tablet viewports
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    Given User sets the viewport size to 375 pixels by 812 pixels
    Then The sidebar should collapse into a hamburger menu
    When User taps the hamburger menu and selects "Guests"
    When User clicks "Edit" on guest row "CONFIGURED_GUEST"
    Then The form grids should collapse cleanly into a single column layout

  @UI @Medium @Low @Functional @Key:TC_GST_033
  Scenario: TC_GST_033 Validate Emergency Contact Phone number formatting & country code selection
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User fills Emergency Contact Name "CONFIGURED_EMERGENCY_NAME"
    And User selects country code "CONFIGURED_COUNTRY_CODE"
    And User enters emergency phone number "CONFIGURED_PHONE"
    And User clicks the guest form "Save Changes" button
    Then The Emergency Contact phone should display formatted as "CONFIGURED_FORMATTED_PHONE"

  @UI @Low @FieldValidation @BoundaryValue @Key:TC_GST_034
  Scenario: TC_GST_034 Validate State dropdown / text input length validation
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_GUEST"
    And User enters State value "TX"
    Then The State field value should not exceed 2 characters
    When User enters State value "TEXAS_USA_OVER_LIMIT"
    And User clicks the guest form "Save Changes" button
    Then A toast error "state must be a 2-letter US abbreviation" should display

  @UI @Medium @BusinessLogic @UI @Key:TC_GST_037
  Scenario: TC_GST_037 Verify Non-Hunter Guest type profile editing (Hunter License section hidden/disabled)
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks "Edit" on guest row "CONFIGURED_NON_HUNTER_GUEST"
    Then The Hunter License Information section should be hidden or disabled

  @UI @High @Functional @Navigation @Key:TC_GST_038
  Scenario: TC_GST_038 Validate "Quick Actions" card CTA buttons on Guest Profile summary page
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    And User clicks on guest row "CONFIGURED_GUEST"
    And User clicks the "Edit Profile" button on the Quick Actions card
    Then User should be redirected to the guest edit profile URL

  @UI @Medium @CrossBrowser @UI @Key:TC_GST_039
  Scenario: TC_GST_039 Verify Cross-Browser UI layout consistency for Guest Directory & Profile pages
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    Then Core Guest Directory elements should render without visual bugs
    When User clicks on guest row "CONFIGURED_GUEST"
    Then Guest Profile summary elements should render without visual bugs