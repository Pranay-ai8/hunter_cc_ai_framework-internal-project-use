@Bookings @regression
Feature: Bookings Module
  As an admin user
  I want to navigate and interact with the Bookings module
  So that I can view, search, and manage bookings

  @Key:BTC001 @High @Type:Navigation
  Scenario: Verify navigation to Bookings module
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    Then User is navigated to the Booking Management page with title, subtitle, and New Booking button

  @Key:BTC075 @High @Type:Functional
  Scenario: Verify final 'Create Booking' button submission on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+5}' to '{today+9}', room 'The king room' for 3 guests, Hunter 'John Hunter Smith', and Guide 'athul pp'
    And User clicks green 'Create Booking' button on Step 5
    Then Booking is created, user is redirected to list page, and pending booking appears at the top

  @regression @Key:BTC041E @High @Type:Functional
  Scenario: Verify hunter added on a first-time booking receives and signs the waiver
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+auto}' to '{today+auto+4}', room 'The king room' for 3 guests, a brand-new Hunter, and Guide 'athul pp'
    And User clicks green 'Create Booking' button on Step 5, creating a real booking
    Then Booking is created, user is redirected to list page, and pending booking appears at the top
    When User opens the newly created booking
    Then The Waiver Status card confirms the waiver email was sent to the hunter
    When User retrieves the waiver invite email from MailHog and signs it as the hunter
    And User reopens the same booking
    Then The Waiver Status card shows the waiver as Signed

  @Key:BTC002 @Low @Type:UI
  Scenario: Verify default view and UI elements on Bookings page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    Then Top section shows Search input, status dropdowns, date pickers, view toggles, and data table
    And Default view is 'List View' highlighted green

  @Key:BTC003 @Low @Type:UI
  Scenario: Verify List View column headers on Bookings page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    Then Table headers display Booking Id, Hunter, Check-in, Check-out, Hunters, Guests, Guide, Waivers, Type, Status, Actions

  @Key:BTC004 @Medium @Type:Functional
  Scenario: Verify Search by exact Booking ID
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table filters instantly to display ONLY the exact booking ID

  @Key:BTC005 @Medium @Type:Functional
  Scenario: Verify Search by partial Booking ID
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table filters to show all bookings containing the partial Booking ID

  @Key:BTC006 @Medium @Type:Functional
  Scenario: Verify Search by exact Hunter Name
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table filters to show booking records associated with the exact hunter name

  @Key:BTC007 @Medium @Type:Functional
  Scenario: Verify Search by partial Hunter Name
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table filters to show bookings matching hunter names containing the partial string

  @Key:BTC008 @Medium @Type:Functional
  Scenario: Verify Search functionality is case-insensitive
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table successfully filters and returns the expected booking regardless of character casing

  @Key:BTC009 @Medium @Type:Functional
  Scenario: Verify Search with non-existent keyword
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table displays an empty state message 'No bookings found'

  @Key:BTC010 @Medium @Type:Functional
  Scenario: Verify clearing Search bar restores full list
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    And User clears the search bar using backspace
    Then Search filter is removed and full list of bookings is restored in the table

  @Key:BTC011 @Medium @Type:Functional
  Scenario: Verify Status filter selection 'All Status' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    Then Table displays all bookings regardless of status

  @Key:BTC012 @Medium @Type:Functional
  Scenario: Verify Status filter selection 'Pending'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    Then Table updates to display ONLY bookings with "pending" status badges or empty state

  @Key:BTC013 @Medium @Type:Functional
  Scenario: Verify Status filter selection 'Approved'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    Then Table updates to display ONLY bookings with "approved" status badges or empty state

  @Key:BTC014 @Medium @Type:Functional
  Scenario: Verify Status filter selection 'Cancelled'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    Then Table updates to display ONLY bookings with "cancelled" status badges or empty state

  @Key:BTC015 @Medium @Type:Functional
  Scenario: Verify Status filter selection 'Checked In'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    Then Table updates to display ONLY bookings with "checked in" status badges or empty state

  @Key:BTC016 @Medium @Type:Functional
  Scenario: Verify Status filter selection 'Checked Out'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    Then Table updates to display ONLY bookings with "checked out" status badges or empty state

  @Key:BTC017 @Medium @Type:Functional
  Scenario: Verify Guide filter selection 'All Guides' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<guideFilter>' from Guide dropdown
    Then Table displays bookings assigned to any guide or unassigned

  @Key:BTC018 @Medium @Type:Functional
  Scenario: Verify Guide filter selection 'athul pp'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<guideFilter>' from Guide dropdown
    Then Table updates to display ONLY bookings assigned to the selected guide

  @Key:BTC019 @Medium @Type:Functional
  Scenario: Verify Guide filter selection 'Clay Sultemeier'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<guideFilter>' from Guide dropdown
    Then Table updates to display ONLY bookings assigned to the selected guide

  @Key:BTC020 @Medium @Type:Functional
  Scenario: Verify Guide filter selection 'alboo tt'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<guideFilter>' from Guide dropdown
    Then Table updates to display ONLY bookings assigned to the selected guide

  @Key:BTC021 @Medium @Type:Functional
  Scenario: Verify Booking Type filter selection 'All Types' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<typeFilter>' from Type dropdown
    Then Table displays bookings of all categories

  @Key:BTC022 @Medium @Type:Functional
  Scenario: Verify Booking Type filter selection 'Hunting'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<typeFilter>' from Type dropdown
    Then Table updates to display ONLY bookings with selected type badges or empty state

  @Key:BTC023 @Medium @Type:Functional
  Scenario: Verify Booking Type filter selection 'Recreational'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<typeFilter>' from Type dropdown
    Then Table updates to display ONLY bookings with selected type badges or empty state

  @Key:BTC024 @Medium @Type:Functional
  Scenario: Verify Booking Type filter selection 'Maintenance'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<typeFilter>' from Type dropdown
    Then Table updates to display ONLY bookings with selected type badges or empty state

  @Key:BTC025 @Medium @Type:Functional
  Scenario: Verify Date Range filter using From Date and To Date pickers
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '{today}' from 'From Date' picker
    And User selects '{today+11}' from 'To Date' picker
    Then Table filters to display only bookings within the selected date window

  @Key:BTC026 @Medium @Type:Functional
  Scenario: Verify 'Clear dates' button functionality
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '{today}' from 'From Date' picker
    And User selects '{today+11}' from 'To Date' picker
    And User clicks 'Clear dates' text button next to search bar
    Then Date input fields clear back to placeholders
    And Table restores full list of bookings

  @Key:BTC027 @Medium @Type:Functional
  Scenario: Verify combined filtering (Status + Guide + Type)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Status dropdown
    And User selects '<guideFilter>' from Guide dropdown
    And User selects '<typeFilter>' from Type dropdown
    Then Table filters dynamically according to all three conditions simultaneously

  @Key:BTC028 @Medium @Type:UI
  Scenario: Verify switching layout from List View to Grid View
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks Grid View icon
    Then Layout switches from table rows to individual booking cards

  @Key:BTC029 @Medium @Type:UI
  Scenario: Verify switching layout from Grid View back to List View
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks Grid View icon
    And User clicks List View icon
    Then Layout switches back from cards to structured table format

  @Key:BTC030 @Low @Type:UI
  Scenario: Verify per-page pagination dropdown selection '10' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    Then Pagination dropdown shows '10' as default

  @Key:BTC031 @Medium @Type:Functional
  Scenario: Verify per-page pagination dropdown selection '20'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '20' from pagination dropdown
    Then Table updates to display up to 20 rows per page

  @Key:BTC032 @Medium @Type:Functional
  Scenario: Verify per-page pagination dropdown selection '50'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects '50' from pagination dropdown
    Then Table updates to display up to 50 rows per page

  @Key:BTC033 @Medium @Type:UI
  Scenario: Verify Actions menu options on a 'Pending' status booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'pending' status and clicks Actions menu
    Then Actions menu popover opens displaying 'Edit', 'Approve', and 'Cancel'

  @Key:BTC034 @High @Type:Functional
  Scenario: Verify Cancelling a pending booking via Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'pending' status and clicks Actions menu
    And User clicks 'Cancel' option from Actions menu
    Then Booking status badge changes to 'cancelled'
    And A success toast alert appears: 'Booking cancelled successfully'

  @Key:BTC035 @High @Type:Functional
  Scenario: Verify Restoring a cancelled booking via Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'cancelled' status and clicks Actions menu
    And User clicks 'Restore Booking' option from Actions menu
    Then Booking status badge changes back from 'cancelled'
    And A success toast alert appears: 'Booking restored successfully!'

  @Key:BTC036 @High @Type:Functional
  Scenario: Verify Approving a pending booking via Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'pending' status and clicks Actions menu
    And User clicks 'Approve' option from Actions menu
    Then Booking status badge changes to 'approved'
    And A success toast alert appears: 'Booking approved successfully!'

  @Key:BTC037 @High @Type:Navigation
  Scenario: Verify navigating to Detailed Booking View by clicking table row
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on booking row 'BK-2026-1002'
    Then User is navigated to detailed booking page for 'BK-2026-1002'

  @Key:BTC038 @Medium @Type:Navigation
  Scenario: Verify navigating to Detailed Booking View by clicking a booking card in Grid View
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks Grid View icon
    And User clicks directly on booking card 'BK-2026-1002'
    Then User is navigated to detailed booking page for 'BK-2026-1002'

  @Key:BTC039 @Medium @Type:Navigation
  Scenario: Verify 'Back to Bookings' header link in Detailed View
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on booking row 'BK-2026-1002'
    And User clicks '< Back to Bookings' link at top left
    Then User is redirected back to main 'Booking Management' list page



  @Key:BTC040 @Medium @Type:Functional
  Scenario: Verify Waiver Status actions - Resend Waiver to Hunter
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Resend Waiver to ProHunt' button
    Then System triggers resending waiver email notification to hunter and displays confirmation toast message

  @Key:BTC041 @Medium @Type:Functional
  Scenario: Verify Waiver Status actions - Extend Waiver Deadline
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Extend Waiver Deadline' button
    Then Modal or DatePicker opens allowing admin to select new waiver deadline date

  @Key:BTC041B @High @Type:Functional
  Scenario: Verify waiver email was sent to hunter on booking registration
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on a 'pending' Hunting booking row with an unsigned waiver
    Then The Waiver Status card confirms the waiver email was sent to the hunter

  @regression @Key:BTC041C @High @Type:Functional
  Scenario: Verify hunter signing the waiver email updates its status to Signed
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+auto+10}' to '{today+auto+14}', room 'The king room' for 3 guests, a brand-new Hunter, and Guide 'athul pp'
    And User clicks green 'Create Booking' button on Step 5, creating a real booking
    Then Booking is created, user is redirected to list page, and pending booking appears at the top
    When User opens the newly created booking
    And User clicks 'Resend Waiver to ProHunt' button
    And User retrieves the waiver invite email from MailHog and signs it as the hunter
    And User reopens the same booking
    Then The Waiver Status card shows the waiver as Signed

  @regression @Key:BTC041G @High @Type:Functional
  Scenario: Verify hunter can complete their profile and add a minor from the Complete Your Profile email
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+auto+30}' to '{today+auto+34}', room 'The king room' for 3 guests, a brand-new Hunter, and Guide 'athul pp'
    And User clicks green 'Create Booking' button on Step 5, creating a real booking
    Then Booking is created, user is redirected to list page, and pending booking appears at the top
    When User opens the newly created booking
    And User retrieves the waiver invite email from MailHog and signs it as the hunter
    And User reopens the same booking
    And User retrieves the Complete Your Profile email from MailHog and opens it
    Then Action Required banner lists the missing profile fields
    When User fills the required phone, license, and license expiration fields
    And User adds a Minor Hunter from the Complete Your Profile page
    And User clicks 'Save and Finalize' button
    Then The profile is completed successfully
    When User retrieves the waiver invite email from MailHog and signs it as the hunter
    And User reopens the same booking
    Then Waiver Status shows all party members signed

  @Key:BTC042 @High @Type:Validation
  Scenario: Verify 'Check in Guests' early check-in error handling
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks green 'Check In Guests' button under Quick Actions
    And User clicks 'Confirm check-in' in confirmation popup
    Then System blocks early check-in and renders error toast 'Cannot check in before the scheduled date'

  @Key:BTC043 @High @Type:Functional
  Scenario: Verify 'Cancel' button in Quick Actions panel
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Cancel' button in Quick Actions right sidebar
    Then Cancellation confirmation dialog pops up to confirm booking cancellation

  @Key:BTC044 @High @Type:Navigation
  Scenario: Verify navigating to Edit Booking page via Quick Actions
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    Then User is navigated to 'Edit Booking' form view

  @Key:BTC045 @Low @Type:UI
  Scenario: Verify Restricted fields warning banner on Edit Booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    Then Header displays warning badge 'Approved & Confirmed'
    And Check-in Date and Check-out Date fields display lock icons

  @Key:BTC046 @High @Type:Functional
  Scenario: Verify Party Management primary hunter removal restriction
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    Then Primary Hunter card delete action displays disabled state

  @Key:BTC047 @Medium @Type:Functional
  Scenario: Verify Guide Assignment modification on Edit Booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    And User clicks 'x' on assigned guide
    And User clicks 'Select a guide' dropdown and chooses 'alboo tt'
    Then Assigned guide chip is updated to new guide

  @Key:BTC048 @Medium @Type:Functional
  Scenario: Verify Add-ons checkboxes on Edit Booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    And User checks available add-ons
    Then Selected add-ons update total pricing

  @Key:BTC049 @Medium @Type:UI
  Scenario: Verify Change Impact Summary section and action buttons on Edit Booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    Then Section lists 'Changes Requiring Re-Approval' and 'Changes Auto-Approved'
    And Three action buttons are present: 'Cancel', 'Save Minor Changes', 'Submit for Re-Approval'

  @Key:BTC050 @Medium @Type:Navigation
  Scenario: Verify Back to Bookings link on Edit Booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    And User clicks '< Back to Bookings' top left link
    Then User exits edit mode and returns to main Bookings list page

  @Key:BTC051 @Medium @Type:Accessibility
  Scenario: Verify keyboard Tab key accessibility across search and filter controls
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User focuses on search input bar
    And User presses 'Tab' key repeatedly
    Then Focus moves sequentially through filter controls and view toggles

  @Key:BTC052 @High @Type:Navigation
  Scenario: Verify navigation to 'Create New Booking' wizard
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks green '+ New Booking' button at top right
    Then User is navigated to 'Create New Booking' wizard starting at Step 1 of 5

  @Key:BTC053 @Medium @Type:UI
  Scenario: Verify Booking Type card selection on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User clicks on 'Hunting' card
    And User clicks on 'Recreational' card
    And User clicks on 'Maintenance' card
    Then Selected Booking Type card highlights green and description updates

  @Key:BTC054 @High @Type:Functional
  Scenario: Verify Ranch dropdown selection and metadata preview on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User selects 'Hunting' booking type
    And User clicks 'Select Ranch' dropdown and chooses 'test ranch'
    Then Selected ranch loads card displaying address description and 'View details' link

  @Key:BTC055 @High @Type:Functional
  Scenario: Verify Check-in and Check-out Date picker validation on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User selects 'Hunting' booking type
    And User clicks 'Select Ranch' dropdown and chooses 'test ranch'
    And User selects Check-in Date '{today+5}' and Check-out Date '{today+9}'
    Then Duration pill '4 nights' appears automatically below date fields

  @Key:BTC056 @Medium @Type:UI
  Scenario: Verify Check-in and Check-out Time selection on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User selects 'Hunting' booking type
    And User clicks 'Select Ranch' dropdown and chooses 'test ranch'
    And User selects Check-in Date '{today+5}' and Check-out Date '{today+9}'
    And User selects Check-in Time '03:00 PM' and Check-out Time '11:00 AM'
    Then Clock modal opens and values display correctly in time fields

  @Key:BTC057 @High @Type:Functional
  Scenario: Verify transition from Step 1 to Step 2 via 'Next' button
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 with valid ranch 'test ranch', dates '{today+5}' to '{today+9}', and times '03:00 PM' and '11:00 AM'
    And User clicks green 'Next' button
    Then Wizard advances to Step 2 of 5 and stepper shows Step 1 completed

  @Key:BTC058 @High @Type:Functional
  Scenario: Verify Room selection and Number of Guests counter on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 with valid ranch 'test ranch', dates '{today+5}' to '{today+9}', and times '03:00 PM' and '11:00 AM'
    And User clicks green 'Next' button
    And User selects room 'The king room' and adjusts guest count to '3'
    Then Room card highlights green with 'Selected' badge and cost updates dynamically

  @Key:BTC059 @High @Type:Functional
  Scenario: Verify Cost Summary card calculation on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 with valid ranch 'test ranch', dates '{today+5}' to '{today+9}', and times '03:00 PM' and '11:00 AM'
    And User clicks green 'Next' button
    And User selects room 'The king room' and adjusts guest count to '3'
    Then Cost Summary breakdowns and Total Cost of '$1200' display correctly

  @Key:BTC060 @High @Type:Functional
  Scenario: Verify transition from Step 2 to Step 3 via 'Next' button
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 with valid ranch 'test ranch', dates '{today+5}' to '{today+9}', and times '03:00 PM' and '11:00 AM'
    And User clicks green 'Next' button
    And User selects room 'The king room' and adjusts guest count to '3'
    And User clicks green 'Next' button
    Then Wizard advances to Step 3 of 5

  @Key:BTC061 @Medium @Type:Functional
  Scenario: Verify Hunter Search bar autocomplete in Step 3 (Party Details)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User ensures 'Add: Hunter' is selected
    And User types 'hu' into search hunters input
    Then Search dropdown opens displaying matching registered hunters

  @Key:BTC062 @High @Type:Functional
  Scenario: Verify adding an existing Hunter to Selected Party on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User searches and clicks matching hunter 'hunt@gmail.com'
    Then Hunter is added to selected party container with green Hunter badge

  @Key:BTC063 @Medium @Type:Validation
  Scenario: Verify 'Parent hunter must be at least 18' DOB validation on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User enters new hunter name 'Minor Hunter' and email 'minor@gmail.com'
    And User selects DOB '{child_dob}' making age under 18
    Then Red inline warning displays 'Parent hunter must be at least 18' and Add Hunter button is disabled

  @Key:BTC064 @Medium @Type:Validation
  Scenario: Verify selecting a valid Adult Date of Birth on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User enters new hunter name 'Adult Hunter' and email 'adult@gmail.com'
    And User selects valid adult DOB '{adult_dob}'
    Then Age validation passes and error message disappears

  @Key:BTC065 @Medium @Type:Functional
  Scenario: Verify adding and removing Minor Hunters on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User adds 3 minor hunters and removes 1 minor card
    Then Wizard updates minors list dynamically

  @Key:BTC066 @Medium @Type:Validation
  Scenario: Verify Texas Hunter License Number and Expiration Date inputs on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User enters license number 'SAMPLE-000000' and valid future expiration date '{next_year}'
    Then License information is saved and validated

  @Key:BTC067 @Medium @Type:Functional
  Scenario: Verify Address Information mandatory fields on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User enters street address 'hjqwkfhkuw3f', city 'nnjqw', state 'TT', and ZIP code '12345'
    Then Address fields accept the entered details

  @Key:BTC068 @Medium @Type:Functional
  Scenario: Verify Emergency Contact optional section on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User enters emergency contact name 'John Doe', phone '+1555-1234', and relationship 'Spouse'
    Then Emergency contact details are captured

  @Key:BTC069 @Medium @Type:Functional
  Scenario: Verify Document Upload widget under Party Details on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User uploads document of type 'ID Proof' with file 'dummy.pdf'
    Then File attachment widget displays uploaded file details

  @Key:BTC070 @High @Type:Functional
  Scenario: Verify adding new Hunter via 'Add Hunter' button on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User fills valid hunter details 'chethan', 'megh@gmail.com', '{adult_dob}', 'SAMPLE-000000', '{next_year}' and clicks green 'Add Hunter'
    Then Hunter is saved to Selected Party and the form resets

  @Key:BTC071 @High @Type:Functional
  Scenario: Verify transition from Step 3 to Step 4 via 'Next' button
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 and 2, and adds hunter 'chethan' to party on Step 3
    And User clicks green 'Next' button
    Then Wizard advances to Step 4 of 5

  @Key:BTC072 @High @Type:Functional
  Scenario: Verify Guide Assignment dropdown on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1, 2, and 3
    And User clicks Add a guide dropdown and chooses 'athul pp'
    Then 'athul pp' guide pill with removable 'x' icon appears

  @Key:BTC073 @High @Type:Functional
  Scenario: Verify transition from Step 4 to Step 5 via 'Next' button
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1, 2, and 3, and assigns guide 'athul pp' on Step 4
    And User clicks green 'Next' button
    Then Wizard advances to Step 5 of 5 and stepper shows '100%' complete

  @Key:BTC074 @High @Type:Functional
  Scenario: Verify Review & Create Booking summary content on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+5}' to '{today+9}', room 'The king room' for 3 guests, Hunter 'John Hunter Smith', and Guide 'athul pp'
    Then Step 5 summary page displays correct details, pricing, and waiver deadline info


  @Key:BTC076 @Medium @Type:Navigation
  Scenario: Verify '< Back' button backward navigation across wizard steps
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    Then Wizard navigates backward to Step 2 and previously entered data is persisted

  @Key:BTC077 @Medium @Type:Navigation
  Scenario: Verify '< Back to Bookings' link exits wizard without saving
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User clicks '< Back to Bookings' top left link
    Then Wizard exits, progress is discarded, and user returns to main list page

  @Key:BTC078 @High @Type:Validation
  Scenario: Verify initial disabled/enabled state of 'Next' button on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    Then Step 1 Next button is disabled or triggers validation errors

  @Key:BTC080 @High @Type:Validation
  Scenario: Verify 'Add Hunter' button state validation when mandatory fields are empty
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User leaves Full Name, Email, DOB, Address fields blank and clicks 'Add Hunter' button
    Then Add Hunter button remains disabled OR missing required fields are highlighted in red

  @Key:BTC081 @Medium @Type:ErrorHandling
  Scenario: Verify inline error message on invalid Email format on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User types invalid email "huntgmai" into Email Address field on Step 3
    Then Red inline error message or validation flag appears for Email Address

  @Key:BTC082 @Medium @Type:Boundary
  Scenario: Verify ZIP Code numeric and length validation on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User enters invalid ZIP Code "12" on Step 3
    Then System flags ZIP Code as invalid or blocks submission

  @Key:BTC083 @Medium @Type:Functional
  Scenario: Verify 'Add Minor Hunter' button clickability and card generation
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User clicks '+ Add Minor Hunter' button on Step 3
    Then A new Minor Hunter form block appears with mandatory Name and DOB fields

  @Key:BTC084 @Medium @Type:Validation
  Scenario: Verify Minor DOB validation (must be under 18 years old)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User clicks '+ Add Minor Hunter' button on Step 3
    And User enters Minor Date of Birth indicating age 18 or older
    Then System displays error indicating Minor must be under 18 years old

  @Key:BTC085 @Medium @Type:Validation
  Scenario: Verify License Expiration Date validation against Booking Check-out Date
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User enters Texas Hunter License Number and Expiration Date prior to Check-out Date
    Then System displays license validation warning or blocks submission

  @Key:BTC087 @Medium @Type:ErrorHandling
  Scenario: Verify Document upload file type restriction and size limit
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User attempts to upload unsupported file "invalid_doc.zip" on Step 3
    Then System rejects file upload and displays format restriction message

  @Key:BTC088 @Medium @Type:Functional
  Scenario: Verify visibility and clickability of removable 'x' icon on Selected Party members
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 and 2, and adds hunter 'chethan' to party on Step 3
    And User clicks 'x' icon on Added Hunter chip in Selected Party container
    Then Hunter chip is removed from party list and count updates

  @Key:BTC090 @Medium @Type:Functional
  Scenario: Verify removing assigned guide on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1, 2, and 3
    And User clicks Add a guide dropdown and chooses 'athul pp'
    And User clicks 'x' on the assigned guide pill 'athul pp' on Step 4
    Then Guide pill is removed immediately and guide slot becomes available again

  @Key:BTC091 @Medium @Type:Functional
  Scenario: Verify 'Create Booking' button loading state during submission on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+5}' to '{today+9}', room 'The king room' for 3 guests, Hunter 'John Hunter Smith', and Guide 'athul pp'
    Then Create Booking button shows loading state or disables during submission

  @Key:BTC095 @Medium @Type:Boundary
  Scenario: Verify Guest count stepper bounds on Step 2 (Accommodation)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User attempts to adjust guest count stepper beyond room capacity
    Then Guest count stepper prevents decreasing below 1 or exceeding max capacity

  @Key:BTC100 @High @Type:Validation
  Scenario: Verify 'Log New Harvest' missing required fields validation
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And a 'Checked In' Hunting booking exists
    And User opens Actions menu for a 'Checked In' Hunting booking and clicks 'Log Harvest'
    And User clicks Submit Harvest Log with blank required fields
    Then Validation error alerts appear and harvest log submission is blocked

  @Key:BTC102 @Medium @Type:Navigation
  Scenario: Verify '< Back to Harvest Logs' navigation from Log Harvest form
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And a 'Checked In' Hunting booking exists
    And User opens Actions menu for a 'Checked In' Hunting booking and clicks 'Log Harvest'
    And User clicks '< Back to Harvest Logs' link
    Then User is navigated back to Harvest Logs list view

  @Key:BTC103 @Medium @Type:UI
  Scenario: Verify 'Check Out Guests' action in Detailed View for active stay
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User opens details page for an active stay and clicks 'Check Out Guests' button
    Then Check out guests modal dialog pops up with Confirm check-out button

  @Key:BTC104 @High @Type:Functional
  Scenario: Verify confirming Check Out updates status to 'Checked-Out Complete'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User opens details page for an active stay and clicks 'Check Out Guests' button
    And User clicks green 'Confirm check-out' button
    Then Modal closes and booking status updates to 'Checked-Out Complete'

  @Key:BTC107 @Medium @Type:Navigation
  Scenario: Verify clicking '+ Add Harvest' redirects to Log New Harvest form
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And a 'Checked In' Hunting booking exists
    And User clicks on a 'Checked In' Hunting booking row
    And User clicks '+ Add Harvest' button on detailed booking view
    Then User is navigated to Log New Harvest page with pre-selected Booking and Ranch

  @Key:BTC108 @High @Type:Navigation
  Scenario: Verify 'Generate Invoice' Quick Action navigation and pre-filled data
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks on any booking row
    And User clicks green 'Generate Invoice' button in Quick Actions panel
    Then User is navigated to Generate Invoice page with pre-filled booking charges

  @Key:BTC109 @Medium @Type:Navigation
  Scenario: Verify 'View Harvest Log' Quick Action navigation on checked-out booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks on a 'Checked Out' Hunting booking row
    And User clicks 'View Harvest Log' button in Quick Actions panel
    Then User is navigated to Harvest Logs list page

  @Key:BTC110 @High @Type:Functional
  Scenario: Verify Invoice creation via 'Create Invoice' button on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks on any booking row
    And User clicks green 'Generate Invoice' button in Quick Actions panel
    And User clicks green 'Create Invoice' button on Generate Invoice page
    Then Invoice is created and user is redirected to Invoice Management list page

  @Key:BTC111 @Medium @Type:Navigation
  Scenario: Verify '< Back to Invoices' header link on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks on any booking row
    And User clicks green 'Generate Invoice' button in Quick Actions panel
    And User clicks '< Back to Invoices' header link
    Then Invoice creation is cancelled and user returns to Invoice Management list

  @Key:BTC112 @Medium @Type:UI
  Scenario: Verify Actions menu options on 'Checked Out' status booking in List View
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '...' icon on a 'Checked Out' booking row
    Then Actions menu displays Generate Invoice and View Harvest Log options

  @Key:BTC113 @Medium @Type:Functional
  Scenario: Verify 'Generate Invoice' from List View Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '...' icon on a 'Checked Out' booking row
    And User selects 'Generate Invoice' from List View Actions menu
    Then User is navigated to Generate Invoice page with pre-filled booking charges

  @Key:BTC114 @Medium @Type:Functional
  Scenario: Verify 'View Harvest Log' from List View Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '...' icon on a 'Checked Out' Hunting booking row
    And User selects 'View Harvest Log' from List View Actions menu
    Then User is navigated to Harvest Logs list page

  @Key:BTC115 @High @Type:Validation
  Scenario: Verify Guide assignment requirement validation for Maintenance Booking on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User creates Maintenance booking and leaves guide field blank on Step 4
    Then System displays validation alert requiring guide assignment for maintenance bookings

  @Key:BTC117 @Medium @Type:Navigation
  Scenario: Verify all left sidebar menu items are visible and clickable
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User inspects left navigation sidebar menu items
    Then All primary navigation links are visible, clickable, and formatted correctly

  @Key:BTC120 @Medium @Type:Navigation
  Scenario: Verify redirection of all primary module links
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks primary module links in sidebar
    Then Each link navigates to its respective route without dead links or 404 errors

  @Key:BTC122 @Medium @Type:Navigation
  Scenario: Verify clicking a Breadcrumb link navigates to parent page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks on any booking row
    And User clicks 'Bookings' link in breadcrumb header
    Then User is navigated back to the parent Booking Management list view

  @Key:BTC123 @Medium @Type:Navigation
  Scenario: Verify browser Back button navigates to previous page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks on any booking row
    And User clicks browser Back button from Detailed Booking view
    Then Browser navigates backward to Bookings list page and restores list state

  @Key:BTC125 @Medium @Type:Workflow
  Scenario: Verify browser Back button behavior after form submission
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+5}' to '{today+9}', room 'The king room' for 3 guests, Hunter 'John Hunter Smith', and Guide 'athul pp'
    And User clicks green 'Create Booking' button on Step 5
    And User clicks browser Back button after creating a booking
    Then System prevents form resubmission and keeps user in safe state

  @Key:BTC126 @Medium @Type:Functional
  Scenario: Verify Search returns relevant results matching keyword
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types keyword from testData in search bar
    Then Table updates dynamically to display only records containing keyword

  @Key:BTC128 @Medium @Type:Functional
  Scenario: Verify auto-suggestions / autocomplete dropdown on Hunter search
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2, and clicks '< Back' button on Step 3
    And User clicks green 'Next' button
    And User types search prefix into Hunter search bar on Step 3
    Then Autocomplete suggestions list appears matching registered hunters

  @Key:BTC129 @Medium @Type:Functional
  Scenario: Verify Pagination navigation buttons (Next, Previous, Page Numbers)
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks pagination Next and Previous buttons
    Then Table pages transition smoothly between record subsets

  @Key:BTC132 @Medium @Type:Workflow
  Scenario: Verify browser Back and Forward navigation retains active pagination state
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User navigates to a booking details page and clicks browser Back button
    Then Bookings list page retains active view state

  @Key:BTC079 @Medium @Type:UI
  Scenario: Verify visibility and clickability of 'View details' link on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User selects a ranch on Step 1
    Then 'View details' link is visible on selected ranch card

  @Key:BTC086 @Low @Type:UI
  Scenario: Verify 'Allow SMS notifications' checkbox state and behavior
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    Then 'Allow SMS notifications' checkbox toggles enabled and disabled states smoothly

  @Key:BTC089 @Medium @Type:Boundary
  Scenario: Verify 'Add a guide...' dropdown enabled/disabled states on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1, 2, and 3
    And User selects maximum 4 guides on Step 4
    Then Guide selection dropdown displays disabled state or maximum guide limit notification

  @Key:BTC092 @High @Type:ErrorHandling
  Scenario: Verify error handling when backend server fails during Booking Creation
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 through 4
    And Network error occurs on Step 5 creation submission
    Then System stays on Step 5 retaining form data and displays error toast

  @Key:BTC093 @Medium @Type:UI
  Scenario: Verify Stepper progress bar visual states across all 5 steps
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    Then Active step circle is highlighted green and stepper progress percentage updates accurately

  @Key:BTC094 @Medium @Type:UI
  Scenario: Verify Country Code flag dropdown clickability and options list in Phone fields
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User clicks flag dropdown icon next to Phone field
    Then Flag dropdown opens displaying list of country codes and calling prefixes

  @Key:BTC096 @Medium @Type:UI
  Scenario: Verify success toast message auto-dismissal behavior
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And A success toast notification is triggered on Bookings page
    Then Toast notification remains visible for a few seconds and auto-dismisses smoothly

  @Key:BTC105 @High @Type:UI
  Scenario: Verify Final Stay Summary rendering upon Checkout completion
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User opens detailed view of a checked-out booking
    Then Final Stay Summary card renders stay duration, room name, guest count, and cost breakdown

  @Key:BTC106 @High @Type:UI
  Scenario: Verify Final Harvest Results section on checked-out booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User opens detailed view of a checked-out booking
    Then Final Harvest Results section displays recorded harvest log count or empty state with Add Harvest button

  @Key:BTC116 @Medium @Type:Validation
  Scenario: Verify leading and trailing spaces in party text fields are trimmed on Step 5 Review
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 and 2
    And User enters hunter name with leading and trailing spaces ' chethan ' on Step 3
    And User proceeds to Step 5 Review
    Then Step 5 Review page displays clean name 'chethan' with whitespace trimmed

  @Key:BTC118 @Low @Type:UI
  Scenario: Verify active menu item highlighting in sidebar
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    Then Bookings menu item in left sidebar is highlighted green

  @Key:BTC119 @Medium @Type:UI
  Scenario: Verify sidebar menu collapse and expand toggle functionality
    Given User is logged in as Super Admin
    When User clicks sidebar collapse expand toggle button
    Then Sidebar collapses gracefully showing icons only and expands back on toggle

  @Key:BTC121 @Low @Type:UI
  Scenario: Verify Breadcrumbs display correct navigation hierarchy
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User navigates to Edit Booking page
    Then Breadcrumb header displays correct hierarchy 'Bookings > Booking Details > Edit Booking'

  @Key:BTC124 @Medium @Type:Navigation
  Scenario: Verify browser Forward button navigates forward correctly
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks browser Back and then Forward buttons
    Then Browser navigates forward back to the previous page

  @Key:BTC127 @Medium @Type:Validation
  Scenario: Verify Search with special characters handles input safely
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types special characters '@#$%' into search bar
    Then Table renders empty state without application errors

  @Key:BTC130 @Medium @Type:Functional
  Scenario: Verify selecting a specific page number in Pagination
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks page number '2' in pagination control
    Then Table jumps directly to page 2 records

  @Key:BTC131 @Medium @Type:Usability
  Scenario: Verify rapid multiple clicks on 'Next' pagination button
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks pagination Next button rapidly multiple times
    Then Pagination handles rapid clicks gracefully without skipping pages or throwing errors

  @Key:BTC134 @Medium @Type:UI
  Scenario: Verify active menu item remains highlighted upon page refresh
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User refreshes the browser page
    Then 'Bookings' sidebar menu item remains highlighted green

  @Key:BTC135 @High @Type:Functional
  Scenario: Verify Approving a pending booking via Quick Actions panel
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on a 'pending' booking row
    And User clicks 'Approve Booking' button in Quick Actions panel
    Then Booking status badge updates to 'approved' and success alert appears

  @Key:BTC136 @High @Type:Functional
  Scenario: Verify Restoring a cancelled booking via Quick Actions panel
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on a 'cancelled' booking row
    Then 'Edit Booking' button is in disabled state
    When User clicks 'Restore Booking' button in Quick Actions panel
    Then Booking status is restored and redirected to active booking state

  @Key:BTC137 @Low @Type:UI
  Scenario: Verify Actions menu options on an 'Approved' status booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'approved' status and clicks Actions menu
    Then Actions menu popover opens displaying 'Edit' option

  @Key:BTC138 @Low @Type:UI
  Scenario: Verify Actions menu options on a 'Cancelled' status booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'cancelled' status and clicks Actions menu
    Then Actions menu popover opens displaying 'Edit' and 'Restore Booking' options

  @Key:BTC139 @Medium @Type:Functional
  Scenario: Verify Check-in Preparation checklist toggles on Approved booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on an 'approved' booking row
    And User toggles checklist items under 'Check-in Preparation'
    Then Checklist items update their completion states

  @Key:BTC140 @Medium @Type:Functional
  Scenario: Verify logging harvest details on Checked In booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And a 'Checked In' Hunting booking exists
    And User opens Actions menu for a 'Checked In' Hunting booking and clicks 'Log Harvest'
    And User fills harvest log form and submits the harvest log
    Then Harvest log submission completes successfully




