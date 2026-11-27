Feature: Booking Management — Regression
  As a Ranch Admin / Super Admin / Guide
  I want booking creation, editing, assignment and status rules to behave correctly
  So that the ranch's booking data stays accurate and policy-compliant

  # ---------- Create Booking — mandatory/optional fields & validation (TC_BOOK_002-013) ----------

  @DataFile:resources/data/bookdata.csv @Key:book_missing_ranch @regression @P1
  Scenario: Attempt to create a booking without selecting a Ranch
    # TC_BOOK_002
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select booking type "td.bookingType"
    And I leave Ranch unselected and attempt to proceed
    Then a validation error indicating Ranch is mandatory should be displayed
    And no Booking ID should be generated

  @DataFile:resources/data/bookdata.csv @Key:book_missing_checkin @regression @P1
  Scenario: Attempt to create a booking without selecting a Check-in date
    # TC_BOOK_003
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" leaving Check-in blank
    Then a validation error indicating Check-in date is mandatory should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_missing_checkout @regression @P1
  Scenario: Attempt to create a booking without selecting a Check-out date
    # TC_BOOK_004
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" and check-in "td.checkInDate" leaving Check-out blank
    Then a validation error indicating Check-out date is mandatory should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_checkout_before_checkin @regression @P1
  Scenario: Attempt to create a booking where Check-out date is before Check-in date
    # TC_BOOK_005
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    Then a validation error that Check-out date must be after Check-in date should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_checkout_equals_checkin @regression @P2
  Scenario: Attempt to create a booking where Check-out date equals Check-in date (same-day)
    # TC_BOOK_006
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkInDate"
    Then a validation error that Check-out date must be after Check-in date should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_minimum_valid_stay @regression @P2
  Scenario: Create a booking with the minimum valid stay (Check-out = Check-in + 1 day)
    # TC_BOOK_007
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be created successfully with a generated Booking ID

  @DataFile:resources/data/bookdata.csv @Key:book_missing_room @regression @P1
  Scenario: Attempt to create a booking without selecting a Room
    # TC_BOOK_008
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step leaving Room unselected
    Then a validation error indicating Room selection is mandatory should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_room_overlap_conflict @regression @P1
  Scenario: Attempt to book a Room that is already booked by another confirmed booking for overlapping dates
    # TC_BOOK_009
    Given User is logged in as "td.role"
    And a confirmed booking already occupies room "td.roomName" for "td.checkInDate" to "td.checkOutDate"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.overlapCheckIn" and check-out "td.overlapCheckOut"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then a room-availability conflict error should be displayed and no duplicate booking should be created

  @DataFile:resources/data/bookdata.csv @Key:book_available_rooms_filtered @regression @P2
  Scenario: Verify the Available Rooms list reflects only rooms actually available for the selected dates
    # TC_BOOK_010
    Given User is logged in as "td.role"
    And room "td.bookedRoomName" is already booked for "td.checkInDate" to "td.checkOutDate"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    Then room "td.bookedRoomName" should not be offered as available
    And room "td.availableRoomName" should be offered as available

  @DataFile:resources/data/bookdata.csv @Key:book_missing_hunter_name @regression @P1
  Scenario: Attempt to create a booking with Hunter Name left blank
    # TC_BOOK_011
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I attempt to add a hunter leaving Full Name blank
    Then a validation error for the missing mandatory Hunter Details field should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_no_guest_details_optional @regression @P2
  Scenario: Create a booking without any Guest Details (Guest Details is optional)
    # TC_BOOK_012
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be created successfully with no Guest Details recorded

  @DataFile:resources/data/bookdata.csv @Key:book_no_guide_optional @regression @P2
  Scenario: Create a booking without assigning any Guide
    # TC_BOOK_013
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step leaving Guide unassigned
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be created successfully without an assigned Guide

  # ---------- Turnover Day, Edit Booking, Status Display (TC_BOOK_015-021) ----------

  @DataFile:resources/data/bookdata.csv @Key:book_turnover_day_blocked @regression @P1
  Scenario: Attempt to create a booking with a Check-in date that falls on the ranch's configured Turnover Day
    # TC_BOOK_015
    Given User is logged in as "td.role"
    And ranch "td.ranchName" has a configured Turnover Day of "td.turnoverDay"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.turnoverDay" and check-out "td.checkOutDate"
    Then an error indicating the date is a Turnover Day and unavailable for booking should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_edit_happy_path @regression @P1
  Scenario: Edit an existing booking's dates, room, hunter/guest details, and guide assignment (happy path)
    # TC_BOOK_016
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists
    When I open booking "td.bookingId" for edit
    And I update the booking's dates to "td.newCheckIn" and "td.newCheckOut", room to "td.newRoomName" and guide to "td.newGuideName"
    And I save the booking edit
    Then the All Bookings list should reflect the updated booking data immediately

  @DataFile:resources/data/bookdata.csv @Key:book_edit_checkout_before_checkin @regression @P2
  Scenario: Attempt to edit a booking so that Check-out date is before the updated Check-in date
    # TC_BOOK_017
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists
    When I open booking "td.bookingId" for edit
    And I update the booking's dates to "td.newCheckIn" and "td.invalidCheckOut"
    And I save the booking edit
    Then a validation error that Check-out must be after Check-in should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_edit_room_conflict @regression @P1
  Scenario: Edit a booking's room/dates to a combination that conflicts with another existing booking's availability
    # TC_BOOK_018
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists
    And a second booking already occupies room "td.conflictingRoomName" for "td.conflictCheckIn" to "td.conflictCheckOut"
    When I open booking "td.bookingId" for edit
    And I update the booking's room to "td.conflictingRoomName" and dates to "td.conflictCheckIn" and "td.conflictCheckOut"
    And I save the booking edit
    Then a room-availability conflict error should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_status_pending_display @regression @P2
  Scenario: Verify a booking in Pending status is correctly displayed as "Pending" in list and detail/edit view
    # TC_BOOK_019
    Given User is logged in as "td.role"
    And a booking "td.bookingId" exists with status "Pending"
    When I navigate to the Bookings list
    Then booking "td.bookingId" should display status "Pending" in the list
    When I open booking "td.bookingId"
    Then the booking detail view should display status "Pending"

  @DataFile:resources/data/bookdata.csv @Key:book_status_approved_display @regression @P2
  Scenario: Verify a booking in Approved status is correctly displayed as "Approved" in list and detail/edit view
    # TC_BOOK_020
    Given User is logged in as "td.role"
    And a booking "td.bookingId" exists with status "Approved"
    When I navigate to the Bookings list
    Then booking "td.bookingId" should display status "Approved" in the list
    When I open booking "td.bookingId"
    Then the booking detail view should display status "Approved"

  # TC_BOOK_021 (Cancelled status display) is skipped: Automation Candidate = TBD in
  # FunctionalTestCases.md (no confirmed UI path creates a Cancelled booking). Live
  # verification this session found Dev data already contains a seeded Cancelled booking
  # (BK-2026-1232) — see docs/dom-snapshots/book.json — which may resolve this once the
  # functional agent reclassifies it YES; not automated here per the TBD skip rule.

  # ---------- Ranch Info, Ranch-Scoped Suggestions, Address, Classification (TC_BOOK_022-030) ----------

  @DataFile:resources/data/bookdata.csv @Key:book_ranch_name_displayed @regression @P2
  Scenario: Verify the booking list and booking detail view correctly display the Ranch Name for each booking
    # TC_BOOK_022
    Given User is logged in as "td.role"
    And a booking "td.bookingId" exists for ranch "td.ranchName"
    When I navigate to the Bookings list
    Then booking "td.bookingId" should display Ranch Name "td.ranchName" in the list
    When I open booking "td.bookingId"
    Then the booking detail view should display Ranch Name "td.ranchName"

  @DataFile:resources/data/bookdata.csv @Key:book_ranch_scoped_suggestions @regression @P2
  Scenario: Verify hunter/guest search suggestions in New Booking are scoped only to the currently selected Ranch
    # TC_BOOK_023
    Given User is logged in as "td.role"
    And hunter "td.otherRanchHunterName" exists only under ranch "td.otherRanchName"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "td.searchTerm" in the Hunter search field
    Then hunter "td.otherRanchHunterName" should not appear in the suggestion list

  @DataFile:resources/data/bookdata.csv @Key:book_ranch_switch_rescopes_suggestions @regression @P3
  Scenario: Verify switching the selected Ranch during booking creation updates the hunter/guest suggestion scope
    # TC_BOOK_024
    Given User is logged in as "td.role"
    And hunter "td.ranchAHunterName" exists only under ranch "td.ranchName"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "td.searchTerm" in the Hunter search field
    And I switch the Ranch selection to "td.otherRanchName"
    And I re-enter "td.searchTerm" in the Hunter search field
    Then hunter "td.ranchAHunterName" should not appear in the suggestion list

  @DataFile:resources/data/bookdata.csv @Key:book_address_provided @regression @P1
  Scenario: Create a booking with Address provided in Hunter Information
    # TC_BOOK_025
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be created successfully and the Address should be saved and displayed correctly

  @DataFile:resources/data/bookdata.csv @Key:book_address_blank @regression @P1
  Scenario: Attempt to create a booking with Address left blank in Hunter Information
    # TC_BOOK_026
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I attempt to add a hunter leaving Street Address blank
    Then a validation error indicating Address is mandatory should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_address_whitespace_only @regression @P2
  Scenario: Attempt to create a booking with Address containing only whitespace characters
    # TC_BOOK_027
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I attempt to add a hunter with Street Address "td.whitespaceAddress"
    Then a validation error indicating Address is mandatory should be displayed

  @DataFile:resources/data/bookdata.csv @Key:book_classification_default_hunting @regression @P2
  Scenario: Verify Booking Classification defaults to "Hunting" when not explicitly changed
    # TC_BOOK_028
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking without changing Booking Classification
    Then the booking should be saved with Booking Classification "Hunting"

  @DataFile:resources/data/bookdata.csv @Key:book_classification_recreational @regression @P2
  Scenario: Select "Recreational" as the Booking Classification during creation
    # TC_BOOK_029
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select booking type "Recreational"
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be saved with Booking Classification "Recreational"

  @DataFile:resources/data/bookdata.csv @Key:book_classification_maintenance @regression @P2
  Scenario: Select "Maintenance" as the Booking Classification during creation
    # TC_BOOK_030
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select booking type "Maintenance"
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be saved with Booking Classification "Maintenance"

  @DataFile:resources/data/bookdata.csv @Key:book_classification_ranch_admin_edit @regression @P3
  Scenario: Verify a Ranch Admin (non-Super Admin) can edit the Booking Classification field
    # TC_BOOK_031
    Given Ranch Admin user is logged in
    And an existing booking "td.bookingId" exists
    When I open booking "td.bookingId" for edit
    And I change Booking Classification to "Recreational"
    And I save the booking edit
    Then the Booking Classification change should be saved successfully

  # ---------- Guide Assignment & Guide Booking Requests (TC_BOOK_032-045) ----------

  @DataFile:resources/data/bookdata.csv @Key:book_guide_assign_max_four @regression @P1
  Scenario: Assign the maximum of 4 guides to a single booking
    # TC_BOOK_032
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I assign guides "td.guide1", "td.guide2", "td.guide3" and "td.guide4"
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be saved with all 4 guides assigned and displayed on the booking details page

  @DataFile:resources/data/bookdata.csv @Key:book_guide_assign_fifth_blocked @regression @P1
  Scenario: Attempt to assign a 5th guide to a booking that already has 4 guides assigned
    # TC_BOOK_033
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" already has 4 guides assigned
    When I open booking "td.bookingId" for edit
    And I attempt to assign a 5th guide "td.guide5"
    Then the system should prevent selecting or saving a 5th guide

  @DataFile:resources/data/bookdata.csv @Key:book_guide_assign_one @regression @P2
  Scenario: Assign exactly 1 guide to a booking (lower non-zero boundary)
    # TC_BOOK_034
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I assign guide "td.guide1"
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be saved with 1 guide assigned and the guide's name displayed on the Booking Details page

  @DataFile:resources/data/bookdata.csv @Key:book_guide_cannot_modify_assignment @regression @P1
  Scenario: Verify a Guide-role user cannot add, edit, or remove guide assignments on a booking
    # TC_BOOK_035
    Given Guide user is logged in
    And booking "td.bookingId" exists with guide assignments
    When I open booking "td.bookingId"
    Then the guide assignment controls should be absent, disabled, or any attempted change should be rejected

  @DataFile:resources/data/bookdata.csv @Key:book_guide_add_and_remove @regression @P2
  Scenario: Super Admin/Ranch Admin edits an existing booking to add and remove assigned guides
    # TC_BOOK_036
    Given User is logged in as "td.role"
    And booking "td.bookingId" has 2 guides assigned including "td.guideToRemove"
    When I open booking "td.bookingId" for edit
    And I remove guide "td.guideToRemove"
    And I assign guide "td.guideToAdd"
    And I save the booking edit
    Then the guide-assignment changes should be reflected on the booking details page

  @DataFile:resources/data/bookdata.csv @Key:book_guide_calendar_occupied_vs_empty @regression @P2
  Scenario: Verify the availability calendar shows existing bookings as "Occupied" for a Guide with assignments, versus an empty calendar for a first-run Guide with none
    # TC_BOOK_037
    Given Guide user "td.guideWithBookings" is logged in
    And I am viewing the Guide Booking Request calendar
    Then occupied slots should be marked "Occupied" on the calendar
    Given Guide user "td.guideWithoutBookings" is logged in
    And I am viewing the Guide Booking Request calendar
    Then no occupied slots should be shown on the calendar

  @DataFile:resources/data/bookdata.csv @Key:book_guide_submits_request @regression @P1
  Scenario: Guide submits a new booking request via "+ New Booking Request" against the availability calendar
    # TC_BOOK_038
    Given Guide user is logged in
    And I am viewing the Guide Booking Request calendar
    When I click the "+ New Booking Request" button
    And I complete the request with ranch "td.ranchName", dates "td.checkInDate" to "td.checkOutDate" and room "td.roomName"
    And I submit the booking request
    Then the request should be created in a pending-approval state visible to Ranch Admin/Super Admin

  @DataFile:resources/data/bookdata.csv @Key:book_admin_approves_guide_request @regression @P1
  Scenario: Ranch Admin/Super Admin approves a Guide's submitted booking request
    # TC_BOOK_039
    Given User is logged in as "td.role"
    And a Guide-submitted pending booking request "td.requestId" exists
    When I navigate to the pending booking requests
    And I open request "td.requestId"
    And I approve the booking request
    Then the request should transition to a confirmed booking visible in All Bookings

  @DataFile:resources/data/bookdata.csv @Key:book_guide_cannot_approve @regression @P2
  Scenario: Verify a Guide-role user cannot approve their own or any booking request
    # TC_BOOK_040
    Given Guide user is logged in
    And a pending booking request "td.requestId" exists
    When I navigate to the pending booking requests
    Then the Guide-role user should have no ability to approve the request

  @DataFile:resources/data/bookdata.csv @Key:book_auto_checkin_transition @regression @P1
  Scenario: Verify a booking automatically transitions to Checked-In status on its Check-in date at the ranch's configured check-in time/time zone
    # TC_BOOK_041
    Given User is logged in as "td.role"
    And booking "td.bookingId" has Check-in date "td.checkInDate" at ranch "td.ranchName" with a configured check-in time
    When the test-environment clock advances to the ranch's configured check-in time on "td.checkInDate"
    Then booking "td.bookingId" should automatically transition to status "Checked-In"

  @DataFile:resources/data/bookdata.csv @Key:book_auto_checkout_transition @regression @P1
  Scenario: Verify a booking automatically transitions to Checked-Out status on its Check-out date at the ranch's configured check-out time/time zone
    # TC_BOOK_042
    Given User is logged in as "td.role"
    And booking "td.bookingId" has Check-out date "td.checkOutDate" at ranch "td.ranchName" with a configured check-out time
    When the test-environment clock advances to the ranch's configured check-out time on "td.checkOutDate"
    Then booking "td.bookingId" should automatically transition to status "Checked-Out"

  @DataFile:resources/data/bookdata.csv @Key:book_manual_override_before_auto @regression @P1
  Scenario: Verify a manual Check-in Status override applied before the scheduled automatic transition is not overwritten
    # TC_BOOK_043
    Given User is logged in as "td.role"
    And booking "td.bookingId" exists prior to its scheduled auto check-in time
    When I manually set booking "td.bookingId" Check-in Status to "Checked In"
    And the test-environment clock advances past the scheduled automatic transition time
    Then booking "td.bookingId" should still display status "Checked In"

  @DataFile:resources/data/bookdata.csv @Key:book_manual_override_after_auto @regression @P1
  Scenario: Verify a manual Check-in Status override applied after an automatic transition has already occurred is accepted and not reverted
    # TC_BOOK_044
    Given User is logged in as "td.role"
    And booking "td.bookingId" has already undergone its automatic transition
    When I manually set booking "td.bookingId" Check-in Status to "td.manualStatus"
    And the test-environment clock advances past the next automatic transition cycle
    Then booking "td.bookingId" should still display status "td.manualStatus"

  @DataFile:resources/data/bookdata.csv @Key:book_guide_sees_own_booking_dashboard @regression @P3
  Scenario: Verify an assigned guide can see the booking reflected on their own dashboard
    # TC_BOOK_045
    Given User is logged in as "td.role"
    And I assign guide "td.guideName" to booking "td.bookingId"
    When Guide user "td.guideName" is logged in
    And I navigate to the guide's dashboard
    Then booking "td.bookingId" should appear on the assigned Guide's dashboard

  # ---------- Generic: DOB/Calendar Validation, Network Failure, Search, Cross-Browser (TC_GEN_BOOK_001-012) ----------

  @DataFile:resources/data/bookdata.csv @Key:gen_book_dob_future_rejected @regression @P2
  Scenario: Verify Hunter Date of Birth rejects a future date
    # TC_GEN_BOOK_001
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I attempt to add a hunter with Date of Birth "01-01-2100"
    Then a validation error rejecting the future Date of Birth should be displayed

  @DataFile:resources/data/bookdata.csv @Key:gen_book_invalid_calendar_date @regression @P3
  Scenario: Verify Check-in/Check-out date fields reject an invalid calendar date (leap year)
    # TC_GEN_BOOK_002
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I attempt to select check-in date "29-Feb-2023"
    Then the date picker should disallow the invalid date or reject it with a validation error

  @DataFile:resources/data/bookdata.csv @Key:gen_book_network_failure_no_duplicate @regression @P2
  Scenario: Verify Booking creation fails gracefully (no partial/duplicate booking) on network loss during submission
    # TC_GEN_BOOK_003
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I disable network connectivity
    And I submit the booking
    Then a network/connection error should be shown and no partial or duplicate booking should be created
    When I restore network connectivity and retry submission
    Then the booking should be created successfully

  @DataFile:resources/data/bookdata.csv @Key:gen_book_search_exact_match @regression @P2
  Scenario: Verify a valid hunter/guest keyword returns matching, ranch-scoped suggestions
    # TC_GEN_BOOK_004
    Given User is logged in as "td.role"
    And ranch "td.ranchName" has an existing hunter "td.hunterName"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "td.hunterName" in the Hunter search field
    Then hunter "td.hunterName" should appear in the suggestion list scoped to "td.ranchName"

  @DataFile:resources/data/bookdata.csv @Key:gen_book_search_partial_match @regression @P3
  Scenario: Verify a partial hunter/guest keyword returns partial matches
    # TC_GEN_BOOK_005
    Given User is logged in as "td.role"
    And ranch "td.ranchName" has an existing hunter "td.hunterName"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "td.partialKeyword" in the Hunter search field
    Then hunter "td.hunterName" should appear as a partial match in the suggestion list

  @DataFile:resources/data/bookdata.csv @Key:gen_book_search_no_match @regression @P3
  Scenario: Verify a non-matching keyword shows a "no results" state, not an error
    # TC_GEN_BOOK_006
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "Zzxqplmv" in the Hunter search field
    Then no suggestions should be shown and no application error should occur

  @DataFile:resources/data/bookdata.csv @Key:gen_book_search_case_insensitive @regression @P3
  Scenario Outline: Verify matches are returned regardless of keyword case
    # TC_GEN_BOOK_007
    Given User is logged in as "td.role"
    And ranch "td.ranchName" has an existing hunter "td.hunterName"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "<keyword>" in the Hunter search field
    Then hunter "td.hunterName" should appear in the suggestion list

    Examples:
      | keyword |
      | JOHN    |
      | john    |
      | JoHn    |

  @DataFile:resources/data/bookdata.csv @Key:gen_book_search_special_chars @regression @P3
  Scenario: Verify the lookup field handles special characters gracefully, without error
    # TC_GEN_BOOK_008
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I begin typing "@#$%^&*()" in the Hunter search field
    Then no application error should occur and the suggestion list should be correctly empty

  @DataFile:resources/data/bookdata.csv @Key:gen_book_cross_browser_booking_id @regression @P2
  Scenario: Verify Booking creation completes successfully (Booking ID generated) on each confirmed browser
    # TC_GEN_BOOK_009
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be created successfully with a generated Booking ID on this browser project

  @DataFile:resources/data/bookdata.csv @Key:gen_book_dst_timezone_handling @regression @P2
  Scenario: Auto check-in/out fires at the ranch's configured local time correctly across a Daylight Saving Time transition
    # TC_GEN_BOOK_010
    Given User is logged in as "td.role"
    And ranch "td.ranchName" is configured with Time Zone "America/Chicago"
    And booking "td.bookingId" has check-in the day before the DST transition and check-out the day after
    When the test-environment clock advances across the DST transition to the configured check-in time
    Then booking "td.bookingId" should auto check-in at the correct local time with no drift
    When the test-environment clock advances to the configured check-out time
    Then booking "td.bookingId" should auto check-out at the correct local time with no drift

  @DataFile:resources/data/bookdata.csv @Key:gen_book_midcycle_timezone_change @regression @P2
  Scenario: Auto check-in/out continues to fire at the correct local time after a ranch's configured Time Zone is manually changed
    # TC_GEN_BOOK_011
    Given User is logged in as "td.role"
    And ranch "td.ranchName" is configured with Time Zone "America/New_York"
    And booking "td.bookingId" has a future check-in date with auto check-in not yet fired
    When I edit ranch "td.ranchName" and change Time Zone to "America/Denver"
    And the test-environment clock advances to the configured check-in time on the check-in date
    Then booking "td.bookingId" should auto check-in using the "America/Denver" local time

  @DataFile:resources/data/bookdata.csv @Key:gen_book_scheduled_local_time_accuracy @regression @P1
  Scenario: Auto check-in fires at exactly the ranch's configured local check-in time on a booking's check-in date
    # TC_GEN_BOOK_012
    Given User is logged in as "td.role"
    And ranch "td.ranchName" is configured with Time Zone "America/Chicago" and a known auto check-in time
    And booking "td.bookingId" has Check-in date equal to today in the ranch's local time
    When the test-environment clock reaches the ranch's configured local check-in time
    Then booking "td.bookingId" status should transition to "Checked In" at exactly the configured time

  # ---------- UI: Page Load & Structure, Navigation (TC_UI_BOOK_003-006) ----------

  @DataFile:resources/data/bookdata.csv @Key:ui_book_detail_structure @regression @P2
  Scenario: Booking - Booking Detail View - Page structure loads with all sections visible
    # TC_UI_BOOK_003
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists
    When I open booking "td.bookingId"
    Then the booking detail view should display a descriptive title
    And dates, room, hunter/guest details, guide assignment and status sections should all be visible

  @DataFile:resources/data/bookdata.csv @Key:ui_book_calendar_structure @regression @P2
  Scenario: Booking - Guide Booking Request Calendar - Page structure loads with calendar and action button visible
    # TC_UI_BOOK_004
    Given Guide user is logged in
    When I am viewing the Guide Booking Request calendar
    Then the calendar grid should render
    And the "+ New Booking Request" button should be visible

  @DataFile:resources/data/bookdata.csv @Key:ui_book_calendar_date_opens_request @regression @P2
  Scenario: Booking - Calendar Navigation - Clicking a calendar date opens the New Booking Request flow
    # TC_UI_BOOK_005
    Given Guide user is logged in
    And I am viewing the Guide Booking Request calendar
    When I click an unoccupied calendar date
    Then the New Booking Request form should open

  @DataFile:resources/data/bookdata.csv @Key:ui_book_row_opens_detail @regression @P2
  Scenario: Booking - List Navigation - Clicking a booking row opens its Detail/Edit view
    # TC_UI_BOOK_006
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists
    When I navigate to the Bookings list
    And I click booking row "td.bookingId"
    Then the Booking Detail/Edit view for "td.bookingId" should open

  # ---------- UI: Form Validation, Interactive Elements (TC_UI_BOOK_008-018) ----------

  @DataFile:resources/data/bookdata.csv @Key:ui_book_mandatory_fields_marked @regression @P2
  Scenario: Booking - New Booking Form - Mandatory fields are visually marked
    # TC_UI_BOOK_008
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    Then Ranch, Check-in, Check-out, Room and Hunter Name should each display a mandatory indicator

  @DataFile:resources/data/bookdata.csv @Key:ui_book_address_mandatory_marked @regression @P2
  Scenario: Booking - New Booking Form - Address field under Hunter Information shows a mandatory indicator
    # TC_UI_BOOK_009
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I reach the Hunter Information section
    Then the Address field label should display a mandatory indicator

  @DataFile:resources/data/bookdata.csv @Key:ui_book_guest_fields_not_marked @regression @P3
  Scenario: Booking - New Booking Form - Guest Details fields are visually marked as optional
    # TC_UI_BOOK_010
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I switch the Party Details form to Non-Hunter Guest
    Then the Guest Details field labels should show no mandatory asterisk

  @DataFile:resources/data/bookdata.csv @Key:ui_book_checkin_date_picker @regression @P2
  Scenario: Booking - New Booking Form - Check-in date picker opens, allows selection, and closes
    # TC_UI_BOOK_011
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I click the Check-in date field
    Then the date picker should open
    When I select a date from the date picker
    Then the date picker should close with the date selected

  @DataFile:resources/data/bookdata.csv @Key:ui_book_dates_display_formatted @regression @P2
  Scenario: Booking - New Booking Form - Selected dates display correctly formatted in the Check-in/Check-out fields
    # TC_UI_BOOK_012
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select check-in date "td.checkInDate" and check-out date "td.checkOutDate" via the date pickers
    Then both fields should display the selected dates in the expected format

  @DataFile:resources/data/bookdata.csv @Key:ui_book_guide_dropdown_opens @regression @P2
  Scenario: Booking - Guide Assignment - Multi-select dropdown opens and lists available guides
    # TC_UI_BOOK_013
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I proceed to the Assignment step
    And I click the Guide multi-select field
    Then the dropdown should open and display the list of available guides

  @DataFile:resources/data/bookdata.csv @Key:ui_book_guide_chip_added @regression @P2
  Scenario: Booking - Guide Assignment - Selecting a guide displays a selected-guide chip/tag
    # TC_UI_BOOK_014
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I proceed to the Assignment step
    And I assign guide "td.guide1"
    Then a chip representing guide "td.guide1" should appear in the field

  @DataFile:resources/data/bookdata.csv @Key:ui_book_guide_chip_removed @regression @P2
  Scenario: Booking - Guide Assignment - Removing a selected-guide chip removes it from the display
    # TC_UI_BOOK_015
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I proceed to the Assignment step
    And I assign guide "td.guide1"
    And I remove the chip for guide "td.guide1"
    Then the chip for guide "td.guide1" should be removed from the field display

  @DataFile:resources/data/bookdata.csv @Key:ui_book_available_rooms_renders @regression @P2
  Scenario: Booking - New Booking Form - Available Rooms list renders after Check-in/Check-out dates are selected
    # TC_UI_BOOK_016
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    Then an Available Rooms list should render and be visible

  @DataFile:resources/data/bookdata.csv @Key:ui_book_classification_dropdown_options @regression @P2
  Scenario: Booking - Booking Classification - Dropdown opens and displays all 3 options
    # TC_UI_BOOK_017
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I click the Booking Classification dropdown
    Then the dropdown should display exactly 3 options: Hunting, Recreational, Maintenance

  @DataFile:resources/data/bookdata.csv @Key:ui_book_edit_checkin_status_visible @regression @P2
  Scenario: Booking - Edit Check-in Status - Control is visible, clickable, and shows the current status
    # TC_UI_BOOK_018
    Given User is logged in as "td.role"
    And an existing booking "td.bookingId" exists with a known Check-in status
    When I open booking "td.bookingId"
    Then the Edit Check-in Status control should be visible, clickable, and display the booking's current status

  # TC_UI_BOOK_019 (status badge styling for Pending/Approved/Cancelled) is skipped:
  # Automation Candidate = TBD in UITestCases.md pending a confirmed Cancelled-status
  # provisioning path (same HITL question as TC_BOOK_021 above).

  # ---------- UI: Error States (TC_UI_BOOK_020-022) ----------

  @DataFile:resources/data/bookdata.csv @Key:ui_book_ranch_error_proximity @regression @P2
  Scenario: Booking - New Booking Form - Mandatory-field error message appears near the Ranch field and clears on correction
    # TC_UI_BOOK_020
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I leave Ranch unselected and attempt to proceed
    Then the validation error should display near the Ranch field with plain-language text
    When I select ranch "td.ranchName"
    Then the validation error near the Ranch field should disappear

  @DataFile:resources/data/bookdata.csv @Key:ui_book_date_error_proximity @regression @P2
  Scenario: Booking - New Booking Form - Check-out-before-Check-in error message appears near the date fields and clears on correction
    # TC_UI_BOOK_021
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.invalidCheckOutDate"
    Then the validation error should display near the date fields with plain-language text
    When I correct the Check-out date to "td.checkOutDate"
    Then the validation error near the date fields should disappear

  # TC_UI_BOOK_022 (empty-state message for All Bookings) is skipped: Automation
  # Candidate = TBD in UITestCases.md (unconfirmed whether QA can provision a
  # zero-bookings scope on the shared Dev environment without side effects).

  # ---------- UI: Responsive Layouts, Cross-Browser, Accessibility (TC_UI_BOOK_023-034 excl. smoke) ----------

  @DataFile:resources/data/bookdata.csv @Key:ui_book_mobile_viewport_form @regression @P2
  Scenario: Booking - New Booking Form - Renders usably at mobile viewport (375px)
    # TC_UI_BOOK_023
    Given User is logged in as "td.role"
    When I set the viewport to 375 by 812
    And I open the New Booking wizard
    Then all form fields and action buttons should remain visible and usable with no horizontal page overflow

  @DataFile:resources/data/bookdata.csv @Key:ui_book_desktop_viewport_form @regression @P3
  Scenario: Booking - New Booking Form - Renders correctly at desktop viewport (1280px)
    # TC_UI_BOOK_024
    Given User is logged in as "td.role"
    When I set the viewport to 1280 by 800
    And I open the New Booking wizard
    Then all form sections should render in the expected desktop layout with no overlapping or hidden content

  @DataFile:resources/data/bookdata.csv @Key:ui_book_mobile_viewport_list @regression @P3
  Scenario: Booking - All Bookings List - Table avoids horizontal overflow at mobile viewport (375px)
    # TC_UI_BOOK_025
    Given User is logged in as "td.role"
    When I set the viewport to 375 by 812
    And I navigate to the Bookings list
    Then the bookings table should not cause horizontal page overflow

  @DataFile:resources/data/bookdata.csv @Key:ui_book_mobile_viewport_calendar @regression @P3
  Scenario: Booking - Guide Booking Request Calendar - Renders usably at mobile viewport (375px)
    # TC_UI_BOOK_026
    Given Guide user is logged in
    When I set the viewport to 375 by 812
    And I am viewing the Guide Booking Request calendar
    Then the calendar grid and "+ New Booking Request" button should remain visible and usable with no horizontal page overflow

  @DataFile:resources/data/bookdata.csv @Key:ui_book_cross_browser_form @regression @P2
  Scenario: Booking - New Booking Form - Renders and functions consistently across Chrome, Firefox, Safari, and Edge
    # TC_UI_BOOK_027
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    Then the layout and core interactions should be consistent and functional on this browser project

  @DataFile:resources/data/bookdata.csv @Key:ui_book_cross_browser_list @regression @P3
  Scenario: Booking - All Bookings List - Renders and functions consistently across Chrome, Firefox, Safari, and Edge
    # TC_UI_BOOK_028
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the list layout and row-click navigation should be consistent and functional on this browser project

  @DataFile:resources/data/bookdata.csv @Key:ui_book_cross_browser_calendar @regression @P3
  Scenario: Booking - Guide Booking Request Calendar - Renders and functions consistently across Chrome, Firefox, Safari, and Edge
    # TC_UI_BOOK_029
    Given Guide user is logged in
    When I am viewing the Guide Booking Request calendar
    Then the calendar layout and button interaction should be consistent and functional on this browser project

  @DataFile:resources/data/bookdata.csv @Key:ui_book_form_fields_labeled @regression @P2
  Scenario: Booking - New Booking Form - All form fields have associated labels
    # TC_UI_BOOK_030
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    Then every form field should have an associated, programmatically-determinable label

  @DataFile:resources/data/bookdata.csv @Key:ui_book_keyboard_navigable @regression @P2
  Scenario: Booking - New Booking Form - Fully keyboard navigable with visible focus
    # TC_UI_BOOK_031
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I press Tab repeatedly from the first field
    Then focus should move through all fields and action buttons in logical order with a visible indicator

  @DataFile:resources/data/bookdata.csv @Key:ui_book_guide_select_keyboard @regression @P3
  Scenario: Booking - Guide Assignment - Multi-select is keyboard operable with visible focus
    # TC_UI_BOOK_032
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I proceed to the Assignment step
    And I tab to the Guide multi-select field and open it via keyboard
    And I navigate and select an option via arrow keys and Enter
    Then the multi-select should open, navigate and select via keyboard alone with visible focus

  @DataFile:resources/data/bookdata.csv @Key:ui_book_list_page_title @regression @P4
  Scenario: Booking - All Bookings List - Page has a visible, descriptive title
    # TC_UI_BOOK_033
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the page should have a visible, descriptive title

  @DataFile:resources/data/bookdata.csv @Key:ui_book_calendar_button_keyboard @regression @P3
  Scenario: Booking - Guide Booking Request Calendar - "+ New Booking Request" button is keyboard reachable with visible focus
    # TC_UI_BOOK_034
    Given Guide user is logged in
    When I am viewing the Guide Booking Request calendar
    And I tab through the page until reaching the "+ New Booking Request" button
    And I activate it with Enter
    Then the button should be reachable via Tab with a visible focus indicator and should activate the request form
