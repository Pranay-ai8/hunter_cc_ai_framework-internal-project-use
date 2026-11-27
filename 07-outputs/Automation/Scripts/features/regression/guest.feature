Feature: Guest / Hunter Directory — Regression
  As a Ranch Admin / Super Admin
  I want guest/hunter records to auto-populate, paginate and validate correctly
  So that the Guest Directory remains an accurate, low-friction record of every booking's party

  # ---------- Guest Auto-Population (TC_GUEST_001-005) ----------

  @DataFile:resources/data/guestdata.csv @Key:guest_autopop_hunter @regression @P2
  Scenario: Verify a new booking's Hunter Details auto-populate a corresponding Guest Directory entry
    # TC_GUEST_001
    Given User is logged in as "td.role"
    And a new booking is created with hunter "td.hunterName", email "td.hunterEmail" and license details
    When I navigate to the Guest Directory
    And I search for "td.hunterName"
    Then a Guest Directory entry for "td.hunterName" should exist with correct Name, Email, Ranch, Type "Hunter" and license details

  @DataFile:resources/data/guestdata.csv @Key:guest_autopop_guest @regression @P2
  Scenario: Verify a new booking's optional Guest Details auto-populate a corresponding Guest Directory entry
    # TC_GUEST_002
    Given User is logged in as "td.role"
    And a new booking is created with guest "td.guestName" and email "td.guestEmail"
    When I navigate to the Guest Directory
    And I search for "td.guestName"
    Then a Guest Directory entry for "td.guestName" should exist with correct Name, Email, Ranch and Type "Guest"

  @DataFile:resources/data/guestdata.csv @Key:guest_edit_syncs_phone @regression @P2
  Scenario: Verify editing an existing booking's hunter/guest details updates the corresponding Guest Directory record
    # TC_GUEST_003
    Given User is logged in as "td.role"
    And booking "td.bookingId" exists with a Guest Directory entry already populated for "td.hunterName"
    When I open booking "td.bookingId" for edit
    And I update the Hunter's phone number to "td.newPhone"
    And I save the booking edit
    And I navigate to the Guest Directory
    And I search for "td.hunterName"
    Then the Guest Directory entry should reflect phone number "td.newPhone"

  @DataFile:resources/data/guestdata.csv @Key:guest_type_distinguishes_hunter_guest @regression @P3
  Scenario: Verify the Guest Directory "Type" field correctly distinguishes Hunter vs. Guest entries from the same booking
    # TC_GUEST_004
    Given User is logged in as "td.role"
    And a booking is created with both hunter "td.hunterName" and guest "td.guestName"
    When I navigate to the Guest Directory
    Then "td.hunterName" should display Type "Hunter"
    And "td.guestName" should display Type "Guest"

  @DataFile:resources/data/guestdata.csv @Key:guest_waiver_status_reflects_actual @regression @P2
  Scenario: Verify the Guest Directory entry reflects the current waiver status for that hunter/guest
    # TC_GUEST_005
    Given User is logged in as "td.role"
    And a new booking is created for hunter "td.hunterName" with a waiver dispatched but not yet signed
    When I navigate to the Guest Directory
    And I search for "td.hunterName"
    Then the Waiver Status for "td.hunterName" should display "td.expectedWaiverStatus"

  # ---------- Guest Pagination (TC_GUEST_006-010) ----------

  @DataFile:resources/data/guestdata.csv @Key:guest_pagination_controls_visible @regression @P2
  Scenario: Verify pagination controls display when total guest records exceed one page's capacity
    # TC_GUEST_006
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then pagination controls should be displayed

  @DataFile:resources/data/guestdata.csv @Key:guest_pagination_next_shows_correct_set @regression @P2
  Scenario: Verify navigating to the next page shows the correct next set of guest records
    # TC_GUEST_007
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    And I note the displayed guest records
    And I go to the next page
    Then page 2 should display the correct next set of records with no duplication or omission

  @DataFile:resources/data/guestdata.csv @Key:guest_pagination_last_page_partial @regression @P3
  Scenario: Verify the last page displays the correct remaining (partial) set of guest records
    # TC_GUEST_008
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    And I go to the last page
    Then the last page should display only the correct remaining records with no blank or duplicate entries

  @DataFile:resources/data/guestdata.csv @Key:guest_pagination_next_disabled_last @regression @P3
  Scenario: Verify the "Next" pagination control is disabled/inactive on the last page
    # TC_GUEST_009
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    And I go to the last page
    Then the Next pagination control should be disabled

  @DataFile:resources/data/guestdata.csv @Key:guest_pagination_previous_disabled_first @regression @P3
  Scenario: Verify the "Previous" pagination control is disabled/inactive on the first page
    # TC_GUEST_010
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then the Previous pagination control should be disabled

  # ---------- Guest Optional Fields (TC_GUEST_011-017) ----------

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_age_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully with Age left blank
    # TC_GUEST_011
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I add a non-hunter guest with name "td.guestName" and email "td.guestEmail" leaving Age blank
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be saved successfully with no validation error for the omitted Age field

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_mobile_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully with Mobile Number left blank
    # TC_GUEST_012
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I add a non-hunter guest with name "td.guestName" and email "td.guestEmail" leaving Mobile Number blank
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be saved successfully with no validation error for the omitted Mobile Number field

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_street_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully with Street Address left blank
    # TC_GUEST_013
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" with guest "td.guestName" exists
    When I edit guest "td.guestName" leaving Street Address blank
    And I save the guest profile
    Then the record should be saved successfully with no validation error for the omitted Street Address field

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_city_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully with City left blank
    # TC_GUEST_014
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" with guest "td.guestName" exists
    When I edit guest "td.guestName" leaving City blank
    And I save the guest profile
    Then the record should be saved successfully with no validation error for the omitted City field

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_state_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully with State left blank
    # TC_GUEST_015
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" with guest "td.guestName" exists
    When I edit guest "td.guestName" leaving State blank
    And I save the guest profile
    Then the record should be saved successfully with no validation error for the omitted State field

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_zip_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully with Zip Code left blank
    # TC_GUEST_016
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" with guest "td.guestName" exists
    When I edit guest "td.guestName" leaving Zip Code blank
    And I save the guest profile
    Then the record should be saved successfully with no validation error for the omitted Zip Code field

  @DataFile:resources/data/guestdata.csv @Key:guest_optional_all_six_blank @regression @P2
  Scenario: Verify a guest/booking can be saved successfully when all six now-optional fields are left blank simultaneously
    # TC_GUEST_017
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" with guest "td.guestName" exists
    When I edit guest "td.guestName" leaving Age, Mobile Number, Street Address, City, State and Zip Code all blank
    And I save the guest profile
    Then the record should be saved successfully with no validation error for any of the six omitted fields

  # ---------- Personal Details Email Form (TC_GUEST_018-019) ----------

  @DataFile:resources/data/guestdata.csv @Key:guest_email_form_dispatched @regression @P2
  Scenario: Verify adding a hunter/non-hunter guest to a booking triggers dispatch of the personal-details email form
    # TC_GUEST_018
    Given User is logged in as "td.role"
    When I create a new booking with hunter/guest email "td.testMailboxEmail"
    And I check the test mailbox for "td.testMailboxEmail"
    Then an email containing the personal-details form should be dispatched to "td.testMailboxEmail"

  @DataFile:resources/data/guestdata.csv @Key:guest_email_form_submission_updates_profile @regression @P2
  Scenario: Verify submitting the personal-details email form updates the corresponding guest/hunter profile
    # TC_GUEST_019
    Given a personal-details form email has been dispatched to "td.testMailboxEmail"
    When I open the personal-details form link received at "td.testMailboxEmail"
    And I complete and submit the personal-details form with "td.updatedDetails"
    And I navigate to the Guest Directory
    And I search for "td.testMailboxEmail"
    Then the guest/hunter profile record should be updated with "td.updatedDetails"

  # ---------- GuestDetails Form Migration Sanity (TC_GUEST_020) ----------

  @DataFile:resources/data/guestdata.csv @Key:guest_form_migration_sanity @regression @sanity @P4
  Scenario: Sanity check that the GuestDetails form (post React Hook Form migration) renders, accepts input, and submits successfully
    # TC_GUEST_020
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists
    When I navigate to booking "td.bookingId"'s Guest Details page
    And I enter valid values into the existing fields
    And I submit the Guest Details form
    Then the form should save successfully with no new or changed business behavior

  # ---------- UI: Navigation, Form Validation (TC_UI_GUEST_002-005) ----------

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_entry_form_structure @regression @P3
  Scenario: Guest Directory - Guest Entry Form - Page structure loads with all sections visible
    # TC_UI_GUEST_002
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I switch the Party Details form to Non-Hunter Guest
    Then all expected Guest Details fields and sections should render and be visible

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_row_opens_detail @regression @P3
  Scenario: Guest Directory - Navigation - Clicking a guest row opens that guest's detail view
    # TC_UI_GUEST_003
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    And I click guest row "td.guestName"
    Then the clicked guest's detail view should open

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_six_fields_not_marked @regression @P2
  Scenario: Guest Directory - Guest Entry Form - Age, Mobile Number, Street Address, City, State, and Zip Code show no mandatory asterisk
    # TC_UI_GUEST_004
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I switch the Party Details form to Non-Hunter Guest
    Then none of Age, Mobile Number, Street Address, City, State or Zip Code should display a mandatory indicator

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_column_headers @regression @P2
  Scenario: Guest Directory - Table Structure - Column headers match expected labels
    # TC_UI_GUEST_005
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then the column headers should be Guest, Ranch, Type, License, Waiver Status, Actions

  # ---------- UI: Interactive Elements (TC_UI_GUEST_006-007) ----------

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_pagination_controls_labeled @regression @P3
  Scenario: Guest Directory - Pagination - Controls are visible and labeled
    # TC_UI_GUEST_006
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then the page-number display and Next/Previous controls should be visible and labeled

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_pagination_next_updates_indicator @regression @P3
  Scenario: Guest Directory - Pagination - Clicking Next updates the visible current-page indicator
    # TC_UI_GUEST_007
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    And I note the current-page indicator
    And I go to the next page
    Then the current-page indicator should visually update to reflect page 2

  # TC_UI_GUEST_008 (empty-state message) is skipped: Automation Candidate = TBD in
  # UITestCases.md (same unconfirmed zero-records provisioning gap as TC_UI_BOOK_022).

  # ---------- UI: Responsive, Cross-Browser, Accessibility (TC_UI_GUEST_009-014) ----------

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_mobile_viewport @regression @P3
  Scenario: Guest Directory - Responsive - Table avoids horizontal overflow at mobile viewport (375px)
    # TC_UI_GUEST_009
    Given User is logged in as "td.role"
    When I set the viewport to 375 by 812
    And I navigate to the Guest Directory
    Then the guest table should not cause horizontal page overflow

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_desktop_viewport_columns @regression @P4
  Scenario: Guest Directory - Responsive - All columns visible at desktop viewport (1280px)
    # TC_UI_GUEST_010
    Given User is logged in as "td.role"
    When I set the viewport to 1280 by 800
    And I navigate to the Guest Directory
    Then all documented columns should be visible without truncation

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_cross_browser @regression @P3
  Scenario: Guest Directory - Cross-Browser - Renders and functions consistently across Chrome, Firefox, Safari, and Edge
    # TC_UI_GUEST_011
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then the page layout and pagination interaction should be consistent and functional on this browser project

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_table_headers_semantic @regression @P3
  Scenario: Guest Directory - Accessibility - Table column headers are properly associated
    # TC_UI_GUEST_012
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then the column headers should use proper table header semantics associated with their data cells

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_entry_form_labels @regression @P2
  Scenario: Guest Directory - Accessibility - Guest entry form fields (including now-optional fields) have associated labels
    # TC_UI_GUEST_013
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I switch the Party Details form to Non-Hunter Guest
    Then every field including the six now-optional fields should have an associated, programmatically-determinable label

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_keyboard_navigable @regression @P3
  Scenario: Guest Directory - Accessibility - Page is keyboard navigable with a descriptive title
    # TC_UI_GUEST_014
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    And I press Tab repeatedly from page load
    Then the page should have a visible, descriptive title and pagination controls/rows should be reachable via Tab with a visible indicator
