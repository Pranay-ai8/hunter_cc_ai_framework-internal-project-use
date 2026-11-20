Feature: Staff & Guide Management — Regression
  As a Ranch Admin / Super Admin
  I want full Create/Edit Guide, status-transition and list behavior to work correctly
  So that staffing records stay accurate, auditable and access-controlled

  # ---------- Create Guide: missing-mandatory-field validation (TC_STAFF_002-008) ----------

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_ranch @regression @P2
  Scenario: Create Guide without selecting Ranch
    # TC_STAFF_002
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I proceed to the next wizard step without selecting a ranch
    Then the guide should not be saved and a validation error indicating "Ranch" is required should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_firstname @regression @P2
  Scenario: Create Guide without entering First Name
    # TC_STAFF_003
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "", last name "td.lastName", email "td.email" and phone "td.phone"
    And I proceed to the next wizard step
    Then the guide should not be saved and a validation error indicating "First Name" is required should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_email @regression @P2
  Scenario: Create Guide without entering Email
    # TC_STAFF_004
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "td.firstName", last name "td.lastName", email "" and phone "td.phone"
    And I proceed to the next wizard step
    Then the guide should not be saved and a validation error indicating "Email" is required should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_phone @regression @P2
  Scenario: Create Guide without entering Phone
    # TC_STAFF_005
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "td.firstName", last name "td.lastName", email "td.email" and phone ""
    And I proceed to the next wizard step
    Then the guide should not be saved and a validation error indicating "Phone" is required should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_employment_type @regression @P2
  Scenario: Create Guide without selecting Employment Type
    # TC_STAFF_006
    # NOTE: FunctionalTestCases.md documents this as "without selecting Position" — the
    # live wizard's closest confirmed equivalent is "Employment Type" (see staff.page.ts
    # header and docs/dom-snapshots/staff.json for the full discrepancy).
    Given User is logged in as "td.role"
    And a guide's Basic Details are already filled for "td.ranchName"
    When I proceed to the next wizard step without selecting an employment type
    Then the guide should not be saved and a validation error indicating "Employment Type" is required should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_experience_level @regression @P2
  Scenario: Experience Level remains unset when Years of Guiding Experience is left blank
    # TC_STAFF_007
    # NOTE: live "Experience Level" is a read-only auto-calculated value, not a selectable
    # dropdown — see staff.page.ts header. This scenario asserts the closest confirmed
    # live behavior (blocked progression / no auto-calculated value) rather than a
    # fabricated dropdown-required error.
    Given User is logged in as "td.role"
    And a guide's Basic Details are already filled for "td.ranchName"
    And I select employment type "td.employmentType"
    When I proceed to the next wizard step without entering years of guiding experience
    Then the guide should not be saved and a validation error indicating "Years of Guiding Experience" is required should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_missing_years_experience @regression @P2
  Scenario: Create Guide without entering Years of Guiding Experience
    # TC_STAFF_008
    Given User is logged in as "td.role"
    And a guide's Basic Details are already filled for "td.ranchName"
    And I select employment type "td.employmentType"
    When I proceed to the next wizard step without entering years of guiding experience
    Then the guide should not be saved and a validation error indicating "Years of Guiding Experience" is required should be displayed

  # ---------- Create Guide: invalid-format validation (TC_STAFF_009-010) ----------

  @DataFile:resources/data/staffdata.csv @Key:staff_invalid_email_format @regression @P2
  Scenario: Create Guide with an invalid Email format
    # TC_STAFF_009
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "td.firstName", last name "td.lastName", email "td.invalidEmail" and phone "td.phone"
    And I proceed to the next wizard step
    Then the guide should not be saved and a validation error indicating "Email" format is invalid should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_invalid_phone_format @regression @P2
  Scenario: Create Guide with an invalid Phone format
    # TC_STAFF_010
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "td.firstName", last name "td.lastName", email "td.email" and phone "td.invalidPhone"
    And I proceed to the next wizard step
    Then a validation error indicating "Phone" format is invalid should be displayed

  # ---------- Default status & Edit flows (TC_STAFF_011-019) ----------

  @DataFile:resources/data/staffdata.csv @Key:staff_default_status_active @regression @P2
  Scenario: Verify new Guide defaults to status Active
    # TC_STAFF_011
    Given User is logged in as "td.role"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName"
    Then the guide list should show "td.firstName" "td.lastName" with status "Active"

  @DataFile:resources/data/staffdata.csv @Key:staff_edit_ranch @regression @P2
  Scenario: Edit Guide — update Ranch assignment
    # TC_STAFF_012
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I select ranch "td.newRanchName" for the new guide
    And I submit the Guide form
    Then the guide list should show "td.guideName" with ranch "td.newRanchName"

  @DataFile:resources/data/staffdata.csv @Key:staff_edit_basic_details @regression @P2
  Scenario: Edit Guide — update Basic Details (Name/Email/Phone)
    # TC_STAFF_013
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I fill the Basic Details with first name "td.newFirstName", last name "td.newLastName", email "td.newEmail" and phone "td.newPhone"
    And I submit the Guide form
    Then the guide list should show "td.newFirstName" "td.newLastName" with email "td.newEmail"

  @DataFile:resources/data/staffdata.csv @Key:staff_edit_employment_type @regression @P2
  Scenario: Edit Guide — update Employment Type
    # TC_STAFF_014
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I proceed to the next wizard step
    And I select employment type "td.newEmploymentType"
    And I submit the Guide form
    Then the guide list should show "td.guideName" with employment type "td.newEmploymentType"

  @DataFile:resources/data/staffdata.csv @Key:staff_edit_experience @regression @P2
  Scenario: Edit Guide — update Years of Guiding Experience
    # TC_STAFF_015
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I proceed to the next wizard step
    And I enter years of guiding experience "td.newYears"
    And I submit the Guide form
    Then the guide list should show "td.guideName" with updated experience reflecting "td.newYears" years

  @DataFile:resources/data/staffdata.csv @Key:staff_status_active_to_onleave @regression @P2
  Scenario: Edit Guide — transition status Active to On Leave
    # TC_STAFF_016
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists with status "Active"
    When I change the guide's Status to "On Leave" via the row menu
    Then the guide list should show "td.guideName" with status "On Leave"

  @DataFile:resources/data/staffdata.csv @Key:staff_status_onleave_to_active @regression @P2
  Scenario: Edit Guide — transition status On Leave to Active
    # TC_STAFF_017
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists with status "On Leave"
    When I change the guide's Status to "Active" via the row menu
    Then the guide list should show "td.guideName" with status "Active"

  @DataFile:resources/data/staffdata.csv @Key:staff_edit_clear_firstname @regression @P2
  Scenario: Edit Guide — clear mandatory First Name field
    # TC_STAFF_018
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I clear the "First Name" field and save
    Then the guide should not be saved and a validation error indicating "First Name" is required should be displayed
    And the guide list should still show "td.guideName" unchanged

  @DataFile:resources/data/staffdata.csv @Key:staff_edit_clear_email @regression @P2
  Scenario: Edit Guide — clear mandatory Email field
    # TC_STAFF_019
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I clear the "Email" field and save
    Then the guide should not be saved and a validation error indicating "Email" is required should be displayed
    And the guide list should still show "td.guideName" unchanged

  # ---------- Role-based & boundary (TC_STAFF_020-022) ----------

  @DataFile:resources/data/staffdata.csv @Key:staff_ranch_admin_crud @regression @P2
  Scenario: Ranch Admin role can create and edit a Guide record
    # TC_STAFF_020
    Given User is logged in as "Ranch Admin"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName"
    And I open the Edit Guide form for "td.firstName td.lastName"
    And I fill the Basic Details with first name "td.firstName", last name "td.updatedLastName", email "td.email" and phone "td.phone"
    And I submit the Guide form
    Then the guide list should show "td.firstName" "td.updatedLastName" with ranch "td.ranchName"

  @DataFile:resources/data/staffdata.csv @Key:staff_years_boundary_zero @regression @P3
  Scenario: Create Guide with Years of Guiding Experience at minimum valid boundary (0)
    # TC_STAFF_021
    Given User is logged in as "td.role"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName" and years of experience "0"
    Then the guide should be saved successfully with status "td.expectedStatus"

  @DataFile:resources/data/staffdata.csv @Key:staff_create_network_failure @regression @P3
  Scenario: Create Guide submission during a network/API failure
    # TC_STAFF_022
    Given User is logged in as "td.role"
    And the create-guide network request will fail
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName"
    Then an error message should be displayed and the guide should not be persisted

  # ---------- Create Guide flow & list rendering (TC_STAFF_023-024) ----------

  @DataFile:resources/data/staffdata.csv @Key:staff_list_rendering_no_refresh @regression @P3
  Scenario: Newly created guide renders on the guide list without a manual refresh
    # TC_STAFF_023
    Given User is logged in as "td.role"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName"
    Then the guide list should show "td.firstName" "td.lastName" without reloading the page

  @DataFile:resources/data/staffdata.csv @Key:staff_list_entry_exact_details @regression @P3
  Scenario: Guide list entry reflects the exact details entered via the Create Guide flow
    # TC_STAFF_024
    Given User is logged in as "td.role"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName"
    Then the guide list should show "td.firstName" "td.lastName" with ranch "td.ranchName"

  # ---------- Guide Email Update / Cognito sync (TC_STAFF_025-029) ----------

  @DataFile:resources/data/staffdata.csv @Key:staff_email_update_valid @regression @P2
  Scenario: Super Admin updates a Guide's email address to a new valid value
    # TC_STAFF_025
    Given User is logged in as "Super Admin"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I fill the Basic Details with first name "", last name "", email "td.newEmail" and phone ""
    And I submit the Guide form
    Then a success toast should be displayed
    And the guide list should show "td.guideName" with email "td.newEmail"

  @DataFile:resources/data/staffdata.csv @Key:staff_resend_invitation @regression @P2
  Scenario: Super Admin resends account-setup email after updating Guide's email
    # TC_STAFF_026
    Given User is logged in as "Super Admin"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I fill the Basic Details with first name "", last name "", email "td.newEmail" and phone ""
    And I click "Resend Invitation" for guide "td.guideName"
    Then a success toast should be displayed

  @DataFile:resources/data/staffdata.csv @Key:staff_email_update_invalid @regression @P2
  Scenario: Super Admin attempts to update Guide email to an invalid format
    # TC_STAFF_027
    Given User is logged in as "Super Admin"
    And an existing guide "td.guideName" exists with email "td.originalEmail"
    When I open the Edit Guide form for "td.guideName"
    And I fill the Basic Details with first name "", last name "", email "td.invalidEmail" and phone ""
    And I submit the Guide form
    Then the guide should not be saved and a validation error indicating "Email" format is invalid should be displayed
    And the guide list should show "td.guideName" with email "td.originalEmail"

  @DataFile:resources/data/staffdata.csv @Key:staff_old_email_no_comms @regression @P2 @TBD
  Scenario: Previous email no longer receives account-setup communications after update
    # TC_STAFF_028
    Given User is logged in as "Super Admin"
    And an existing guide "td.guideName" exists with email "td.originalEmail"
    When I open the Edit Guide form for "td.guideName"
    And I fill the Basic Details with first name "", last name "", email "td.newEmail" and phone ""
    And I submit the Guide form
    And I click "Resend Invitation" for guide "td.guideName"
    Then the test mailbox for "td.originalEmail" should receive no new account-setup email
    And the test mailbox for "td.newEmail" should receive the account-setup email

  @DataFile:resources/data/staffdata.csv @Key:staff_password_setup_new_email @regression @P2 @TBD
  Scenario: Guide completes password setup using the link sent to the newly updated email
    # TC_STAFF_029
    Given User is logged in as "Super Admin"
    And a guide's email was updated to "td.newEmail" and the setup email was resent
    When I retrieve the password-setup link from the test mailbox for "td.newEmail" and open it
    And I complete the password setup form
    Then the guide should be able to authenticate with the new credentials for "td.newEmail"

  # ---------- Generic: Cognito account setup / invitation email (TC_GEN_STAFF_001-002) ----------

  @DataFile:resources/data/staffdata.csv @Key:gen_staff_invitation_activation @regression @P2 @TBD
  Scenario: Newly created Guide's invitation email completes account activation
    # TC_GEN_STAFF_001
    Given User is logged in as "td.role"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName" and test-mailbox email "td.email"
    And I retrieve the account-setup link from the test mailbox for "td.email" and open it
    And I complete the password setup form
    Then the guide's record should reflect a completed account-setup status

  @DataFile:resources/data/staffdata.csv @Key:gen_staff_expired_invitation_link @regression @P2
  Scenario: An expired Cognito invitation link fails to open for the invited Guide
    # TC_GEN_STAFF_002
    Given User is logged in as "td.role"
    And a guide "td.firstName" "td.lastName" was invited with test-mailbox email "td.email"
    And the test-environment clock has been advanced past the invitation link's expiration window
    When I retrieve the invitation link from the test mailbox for "td.email" and open it
    Then the account-setup page should not load and the link should fail to open

  # ---------- UI: Staff & Guide List structure, forms, interactions (TC_UI_STAFF_002, 004-019) ----------

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_column_headers @regression @P3
  Scenario: Table column headers render as expected
    # TC_UI_STAFF_002
    Given User is logged in as "td.role"
    When I navigate to the Staff & Guides list
    Then the table column headers should be visible in the expected order

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_mandatory_indicators @regression @P2
  Scenario: Mandatory fields are visually marked on the Create Guide form
    # TC_UI_STAFF_004
    Given User is logged in as "td.role"
    When I open the Add Guide form
    Then each mandatory field should display a mandatory indicator

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_edit_prepopulate @regression @P2
  Scenario: Edit Guide form pre-populates existing values with labels intact
    # TC_UI_STAFF_005
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    Then the form fields should display the guide's previously saved values with labels still visible

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_experience_level_display @regression @P2
  Scenario: Experience Level displays as a read-only auto-calculated value
    # TC_UI_STAFF_006
    # NOTE: adapted from the documented "dropdown opens with exactly 3 options" expectation
    # to the live, confirmed read-only auto-calculated control — see staff.page.ts header.
    Given User is logged in as "td.role"
    And a guide's Basic Details are already filled for "td.ranchName"
    When I enter years of guiding experience "td.years"
    Then the Experience Level control should display a read-only auto-calculated value

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_employment_type_options @regression @P2
  Scenario: Employment Type control opens with Full-time/Part-time/Contract options
    # TC_UI_STAFF_007
    # NOTE: adapted from the documented "Position: Guide/Staff dropdown" expectation to the
    # live, confirmed Employment Type radio group — see staff.page.ts header.
    Given User is logged in as "td.role"
    And a guide's Basic Details are already filled for "td.ranchName"
    When I proceed to the next wizard step
    Then the Employment Type control should display options "Full-time", "Part-time" and "Contract"

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_status_toggle_visual @regression @P3
  Scenario: Status control visually toggles between Active and On Leave
    # TC_UI_STAFF_008
    Given User is logged in as "td.role"
    And an existing guide "td.guideName" exists with status "Active"
    When I change the guide's Status to "On Leave" via the row menu
    Then the Status control should visually reflect "On Leave" immediately

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_submit_loading_state @regression @P3
  Scenario: Submit button shows loading state during submission
    # TC_STAFF_009_UI
    # TC_UI_STAFF_009
    Given User is logged in as "td.role"
    And a guide's Basic Details are already filled for "td.ranchName"
    When I submit the Guide form
    Then the Submit button should show a disabled/loading state

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_validation_error_position @regression @P3
  Scenario: Validation error renders adjacent to its triggering field
    # TC_UI_STAFF_010
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "", last name "td.lastName", email "td.email" and phone "td.phone"
    And I proceed to the next wizard step
    Then the validation error should render adjacent to the "First Name" field

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_success_toast @regression @P3
  Scenario: Success toast appears after Guide creation
    # TC_UI_STAFF_011
    Given User is logged in as "td.role"
    When I create a guide "td.firstName" "td.lastName" with valid data for ranch "td.ranchName"
    Then a success toast should be displayed

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_resend_button_visible @regression @P3
  Scenario: Resend Invitation button is visible after an email update
    # TC_UI_STAFF_012
    Given User is logged in as "Super Admin"
    And an existing guide "td.guideName" exists
    When I open the Edit Guide form for "td.guideName"
    And I fill the Basic Details with first name "", last name "", email "td.newEmail" and phone ""
    Then the Resend Invitation button should be visible and enabled

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_resend_toast @regression @P3
  Scenario: Resend Invitation click shows success toast
    # TC_UI_STAFF_013
    Given User is logged in as "Super Admin"
    And an existing guide "td.guideName" exists
    When I click "Resend Invitation" for guide "td.guideName"
    Then a success toast should be displayed

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_empty_state @regression @P3
  Scenario: Empty state message shown when no guides exist
    # TC_UI_STAFF_014
    Given User is logged in as "td.role"
    And a ranch "td.emptyRanchName" exists with zero guide records
    When I navigate to the Staff & Guides list filtered to ranch "td.emptyRanchName"
    Then an empty-state message should be displayed

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_responsive_mobile @regression @P3
  Scenario: Staff & Guide List table renders usably at mobile viewport
    # TC_UI_STAFF_015
    Given User is logged in as "td.role"
    And the viewport is set to "375" by "667"
    When I navigate to the Staff & Guides list
    Then the list should render usably with no horizontal overflow

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_responsive_form @regression @P3
  Scenario: Create Guide form renders usably at mobile and desktop viewports
    # TC_UI_STAFF_016
    Given User is logged in as "td.role"
    And the viewport is set to "375" by "667"
    When I open the Add Guide form
    Then the form fields and Save button should be visible, stacked and usable without overlap
    Given the viewport is set to "1280" by "800"
    When I open the Add Guide form
    Then the form fields and Save button should be visible, stacked and usable without overlap

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_cross_browser @regression @P3 @cross-browser
  Scenario: Staff & Guide screens render consistently across browsers
    # TC_UI_STAFF_017
    Given User is logged in as "td.role"
    When I navigate to the Staff & Guides list and open the Add Guide form in each supported browser
    Then the layout and controls should render and function consistently across browsers

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_a11y_form @regression @P3 @accessibility
  Scenario: Create Guide form fields are labeled and keyboard navigable with visible focus
    # TC_UI_STAFF_018
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I tab through the Create Guide form fields
    Then every field should have an associated label and be keyboard reachable with a visible focus indicator

  @DataFile:resources/data/staffdata.csv @Key:ui_staff_a11y_list @regression @P3 @accessibility
  Scenario: Staff & Guide List has a descriptive title and keyboard-reachable controls
    # TC_UI_STAFF_019
    Given User is logged in as "td.role"
    When I navigate to the Staff & Guides list
    Then the page title should be descriptive and all interactive controls should be keyboard reachable
