@Sanity
Feature: Consolidated End-to-End Sanity Test Suite

  # ==========================================
  # 1. LOGIN & AUTHENTICATION MODULE
  # ==========================================

  @login @Key:TC001 @smoke
  Scenario: Verify successful login with valid credentials
    Given User is on the main landing page
    When User clicks on the Login to Portal button
    And User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then The button state changes to Signing in
    And A toast notification "Login successful!" appears
    And User is redirected to the Dashboard

  @login @Key:TC002 @smoke
  Scenario: Verify user logout functionality
    Given User navigates to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected to the Dashboard
    When User clicks on the profile avatar at the top right
    And User clicks on the Log out button
    Then User is logged out and redirected to the login page
    And A toast notification "Logged out successfully" appears

  @login @Key:TC018 @smoke
  Scenario: Verify Forgot Password link visibility and navigation
    Given User navigates to the login page
    When User clicks the Forgot Password link
    Then User is redirected to the Forgot Password page

  @login @Key:TC044 @smoke
  Scenario: Verify successful submission with valid registered email on Forgot Password page
    Given User navigates to the login page
    When User clicks the Forgot Password link
    And User is redirected to the Forgot Password page
    When User enters valid registered email "<username>" in Forgot Password Email field
    And User clicks the Send Reset Code button
    Then User is redirected to the Check Your Email screen

  @login @Key:TC052 @smoke
  Scenario: Verify redirection to originally requested protected URL after login
    Given User navigates directly to a protected route "/dashboard"
    Then User is redirected to the login page
    When User enters valid email "<username>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected directly back to originally requested protected page "/dashboard"

  # =========================================================================
  # 2. DASHBOARD
  # =========================================================================

  @UI @High @Functional @Key:TC_DB_002 @smoke
  Scenario: TC_DB_002 Verify + New Booking CTA button navigation
    Given User is logged in as Super Admin
    When User clicks on the "+ New Booking" button
    Then User should be redirected to "/bookings/new" page

  @UI @Critical @Functional @Key:TC_DB_005 @smoke
  Scenario: TC_DB_005 Verify Ranch Filter dropdown on Booking Calendar
    Given User is logged in as Super Admin
    When User selects the first available ranch option from the calendar filter
    Then The calendar should re-filter to display events for the selected ranch
    When User selects "All Ranches" from the calendar ranch filter
    Then The calendar should display events for all ranches

  @UI @High @Functional @Navigation @Key:TC_DB_010 @smoke
  Scenario: TC_DB_010 Verify "View All" navigation for Recent Bookings section
    Given User is logged in as Super Admin
    When User scrolls to the "Recent Bookings" section
    And User clicks the "View All" button beside Recent Bookings header
    Then User should be redirected to "/bookings" page

  @UI @High @Functional @Navigation @Key:TC_DB_013 @smoke
  Scenario: TC_DB_013 Verify "View All" navigation for Active Ranches section
    Given User is logged in as Super Admin
    When User scrolls to the "Active Ranches" section
    And User clicks the "View All" button beside Active Ranches header
    Then User should be redirected to "/ranches" page

  @UI @High @Critical @Navigation @System @Key:TC_DB_015 @smoke
  Scenario: TC_DB_015 Verify cross-module sidebar navigation stability
    Given User is logged in as Super Admin
    When User navigates through sidebar items "Bookings", "Ranches", "Staff & Guides", and "Invoices"
    And User clicks "Dashboard" from the left navigation menu
    Then User should be redirected to "/dashboard" page
    And The dashboard title should be "Dashboard"

  @UI @Critical @Functional @Key:TC_DB_003 @smoke
  Scenario: TC_DB_003 Validate accuracy of KPI Metric Cards data against DB
    Given User is logged in as Super Admin
    When User fetches the current KPI metrics from the database
    Then The dashboard KPI cards should accurately match the database records

  @UI @Critical @Functional @Key:TC_DB_030 @smoke
  Scenario: TC_DB_030 Verify XSS sanitization on Dashboard Booking titles and Ranch names
    Given User is logged in as Super Admin
    Then Script payload tags should render strictly as plain text strings without execution

 # ==========================================
 # 3. RANCHES MODULE
 # ==========================================

  @smoke @Key:RTC001
  Scenario: Verify navigation to Ranches module
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then User is navigated to the Ranches page
    And The Ranches sidebar item is highlighted
    And The page title Ranch Management is visible

  @Key:RTC009 @smoke
  Scenario: Verify Search functionality with exact Ranch Name
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "Koramnaga Ranch" in the search bar
    Then Table filters to show ONLY "Koramnaga Ranch"

  @smoke @Key:RTC019
  Scenario: Verify opening Detailed View by clicking a Ranch row
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks on any Ranch row (e.g., "Koramnaga Ranch")
    Then User is navigated to the read-only Ranch Details page displaying complete metadata and an 'Edit Ranch' button

  @smoke @Key:RTC022
  Scenario: Verify navigation to Create New Ranch wizard
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    Then User is navigated to 'Create New Ranch' wizard page initialized at 'Step 1 of 5' (20% Complete)

  @smoke @Key:RTC035
  Scenario: Verify final 'Create Ranch' button submission
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User completes Step 2 required fields and clicks 'Next >'
    And User clicks 'Add Room' button
    And User inputs valid room data from testData
    And User clicks 'Next >'
    And User configures default preferences from testData
    And User clicks 'Next >'
    And User clicks 'Create Ranch' button
    Then Ranch is saved and user is redirected back to Ranches list
    And A toast notification "Ranch created successfully!" appears

  @smoke @Key:RTC056
  Scenario: Verify navigation to Edit Ranch wizard step 1 from Details view
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User navigates to Ranch Details page
    And User clicks green 'Edit Ranch' button
    Then User is navigated to the 'Edit Ranch' wizard page initialized at 'Step 1 of 5'

  @smoke @Key:RTC080
  Scenario: Verify successful submission and toast message on 'Save Changes'
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Save Changes' button
    Then Application saves changes, redirects user, and displays success toast alert

# ==========================================
# 4. RANCH ADMIN MODULE
# ==========================================

   @UI @High @Critical @Sanity @Key:TC_RAM_001 @smoke
  Scenario: TC_RAM_001 Verify successful creation of a new Ranch Admin
    Given User is logged in as Super Admin
    And User navigates to the "Ranch Admins" module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "John Doe", Email "john@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then The modal should close and a success toast "Admin created successfully!" should appear
    And The grid should display a new row containing "John Doe"

    @UI @High @Critical @Negative @Key:TC_RAM_002 @smoke
    Scenario: TC_RAM_002 Verify mandatory field validations on form submission
      Given User is logged in as Super Admin
      And User navigates to the "Ranch Admins" module from the left navigation menu
      When User clicks the "+ Create New Admin" button for ranch admin
      And User leaves all fields empty and submits the form
      Then Form submission should be blocked with inline errors under all mandatory fields

    @UI @High @Critical @Security @BusinessLogic @Key:TC_RAM_005 @smoke
    Scenario: TC_RAM_005 Verify duplicate email prevention
      Given User is logged in as Super Admin
      And User navigates to the "Ranch Admins" module from the left navigation menu
      When User clicks the "+ Create New Admin" button for ranch admin
      And User fills the admin form with Name "Dupe User", Email "mike@qdr.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
      And User clicks the "Create Admin" button for ranch admin
      Then Submission should fail with a duplicate email error

    @UI @High @Functional @Key:TC_RAM_018 @smoke
    Scenario: TC_RAM_018 Validate Edit Ranch Admin modal populates existing data
      Given User is logged in as Super Admin
      And User navigates to the "Ranch Admins" module from the left navigation menu
      When User clicks "Edit" for an available admin row
      Then The edit modal should be pre-filled with the selected admin details

    @UI @High @Critical @Security @RBAC @Key:TC_RAM_026 @smoke
  Scenario: TC_RAM_026 Verify Ranch Admin only sees their assigned ranch details upon login
    When User logs in with Ranch Admin credentials for scenario "TC_RAM_026"
    Then The grid or dashboard should display only "Powderhorn Ranch"
    When User navigates to the "Ranches" module from the left navigation menu
    Then The grid or dashboard should display only "Powderhorn Ranch"
    And The ranch filter selector should be locked or contain only "Powderhorn Ranch"

    @Key:TC_RAM_033 @smoke
    Scenario: TC_RAM_033 Verify successful update of Ranch Admin via Edit modal
      Given User is logged in as Super Admin
      And User navigates to the "Ranch Admins" module from the left navigation menu
      When User clicks the "+ Create New Admin" button for ranch admin
      And User fills the admin form with Name "EditTarget", Email "edittarget@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
      And User clicks the "Create Admin" button for ranch admin
      And User clicks "Edit" for admin row "EditTarget"
      And User changes Full Name to "Updated User"
      And User clicks the "Update Admin" button for ranch admin
      Then The Updating loader should display before the modal closes
      And The modal should close and a success toast "Ranch admin updated successfully!" should appear
      And The grid should display a new row containing "Updated User"
    
    @UI @High @Critical @Functional @StateTransition @Key:TC_RAM_035 @smoke
    Scenario: TC_RAM_035 Verify successful record deletion via confirmation modal
      Given User is logged in as Super Admin
      And User navigates to the "Ranch Admins" module from the left navigation menu
      When User deletes an available record on the current page
      Then The delete confirmation modal should display
      When User confirms the deletion
      Then The modal should close and a success toast "Ranch admin deleted successfully!" should appear

    @UI @High @Critical @Security @Key:TC_RAM_021 @smoke
    Scenario: TC_RAM_021 Validate SQL injection prevention in Search and Form inputs
      Given User is logged in as Super Admin
      And User navigates to the "Ranch Admins" module from the left navigation menu
      When User enters SQL or regex payload "' OR '1'='1" in the search field
      Then The search should handle the input literally without server errors

  # ==========================================
  # 5. BOOKINGS MODULE
  # ==========================================

  @smoke @Key:BTC001
  Scenario: Verify navigation to Bookings module
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    Then User is navigated to the Booking Management page with title, subtitle, and New Booking button

  @Key:BTC004 @smoke
  Scenario: Verify Search by exact Booking ID
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the search bar
    Then Table filters instantly to display ONLY the exact booking ID

  @Key:BTC012 @smoke
  Scenario: Verify Status filter selection 'Pending'
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User selects 'Pending' from Status dropdown
    Then Table updates to display ONLY bookings with "pending" status badges or empty state

  @smoke @Key:BTC037
  Scenario: Verify navigating to Detailed Booking View by clicking table row
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on booking row 'BK-2026-1002'
    Then User is navigated to detailed booking page for 'BK-2026-1002'

  @Key:BTC075 @smoke
  Scenario: Verify final 'Create Booking' button submission on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Steps 1 to 4 with Ranch 'test ranch', Dates '{today+5}' to '{today+9}', room 'The king room' for 3 guests, Hunter 'John Hunter Smith', and Guide 'athul pp'
    And User clicks green 'Create Booking' button on Step 5
    Then Booking is created, user is redirected to list page, and pending booking appears at the top

  @Key:BTC038 @smoke
  Scenario: Verify Cancelling a pending booking via Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clears the search bar using backspace
    And User locates a row with 'pending' status and clicks Actions menu
    And User clicks 'Cancel' option from Actions menu
    Then Booking status badge changes to 'cancelled'
    And A success toast alert appears: 'Booking cancelled successfully'

  @Key:BTC035 @smoke
  Scenario: Verify Restoring a cancelled booking via Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'cancelled' status and clicks Actions menu
    And User clicks 'Restore Booking' option from Actions menu
    Then Booking status badge changes back from 'cancelled'
    And A success toast alert appears: 'Booking restored successfully!'

  @Key:BTC036 @smoke
  Scenario: Verify Approving a pending booking via Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'pending' status and clicks Actions menu
    And User clicks 'Approve' option from Actions menu
    Then Booking status badge changes to 'approved'
    And A success toast alert appears: 'Booking approved successfully!'

  @smoke @Key:BTC052
  Scenario: Verify navigation to 'Create New Booking' wizard
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks green '+ New Booking' button at top right
    Then User is navigated to 'Create New Booking' wizard starting at Step 1 of 5

  @smoke @Key:BTC075_REC
  Scenario: Verify final 'Create Booking' button submission for Recreational type on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 for 'Recreational' booking and clicks 'Next'
    And User completes Step 2 room selection and clicks 'Next'
    And User completes Step 3 party selection and clicks 'Next'
    And User completes Step 4 details and clicks 'Next'
    And User clicks green 'Create Booking' button on Step 5
    Then Booking is created, user is redirected to list page, and pending booking appears at the top

  @smoke @Key:BTC075_MAINT
  Scenario: Verify final 'Create Booking' button submission for Maintenance type on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks '+ New Booking' button
    And User completes Step 1 for 'Maintenance' booking and clicks 'Next'
    And User completes Step 2 room selection and clicks 'Next'
    And User completes Step 3 party selection and clicks 'Next'
    And User completes Step 4 details and clicks 'Next'
    And User clicks green 'Create Booking' button on Step 5
    Then Booking is created, user is redirected to list page, and pending booking appears at the top

  @smoke @Key:BTC044
  Scenario: Verify navigating to Edit Booking page via Quick Actions
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    Then User is navigated to 'Edit Booking' form view

  @Key:BTC047 @smoke
  Scenario: Verify Guide Assignment modification on Edit Booking page
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on 'approved' booking row 'BK-2026-1002'
    And User clicks 'Edit Booking' under Quick Actions
    And User clicks 'x' on assigned guide
    And User clicks 'Select a guide' dropdown and chooses 'alboo tt'
    Then Assigned guide chip is updated to new guide

     @smoke @Key:BTC135
  Scenario: Verify Approving a pending booking via Quick Actions panel
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on a 'pending' booking row
    And User clicks 'Approve Booking' button in Quick Actions panel
    Then Booking status badge updates to 'approved' and success alert appears

  @smoke @Key:BTC136
  Scenario: Verify Restoring a cancelled booking via Quick Actions panel
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on a 'cancelled' booking row
    Then 'Edit Booking' button is in disabled state
    When User clicks 'Restore Booking' button in Quick Actions panel
    Then Booking status is restored and redirected to active booking state

  @smoke @Key:BTC137
  Scenario: Verify Actions menu options on an 'Approved' status booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'approved' status and clicks Actions menu
    Then Actions menu popover opens displaying 'Edit' option

  @smoke @Key:BTC138
  Scenario: Verify Actions menu options on a 'Cancelled' status booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User locates a row with 'cancelled' status and clicks Actions menu
    Then Actions menu popover opens displaying 'Edit' and 'Restore Booking' options

  @smoke @Key:BTC139
  Scenario: Verify Check-in Preparation checklist toggles on Approved booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User clicks directly on an 'approved' booking row
    And User toggles checklist items under 'Check-in Preparation'
    Then Checklist items update their completion states

  @smoke @Key:BTC140
  Scenario: Verify logging harvest details on Checked In booking
    Given User is logged in as Super Admin
    When User clicks on 'Bookings' under GUEST OPERATIONS in the left sidebar
    And User opens Actions menu for a 'Checked In' Hunting booking and clicks 'Log Harvest'
    And User fills harvest log form and submits the harvest log
    Then Harvest log submission completes successfully


  # =========================================================================
  # 6. GUESTS
  # =========================================================================

    @UI @High @Functional @Key:TC_GST_001 @smoke
    Scenario: TC_GST_001 Verify filtering Guest Directory by Guest Type
      Given User is logged in as Super Admin
      And User navigates to the "Guests" module from the sidebar
      When User selects "Hunters" from the Guest Type dropdown
      Then The table should update to display guests of type "Hunters"
      When User selects "Non-Hunter Guests" from the Guest Type dropdown
      Then The table should update to display guests of type "Guest"

    @UI @High @Critical @Functional @StateTransition @Key:TC_GST_005 @smoke
    Scenario: TC_GST_005 Verify successful update of Guest Profile details
      Given User is logged in as Super Admin
      And User navigates to the "Guests" module from the sidebar
      When User clicks "Edit" on guest row "CONFIGURED_GUEST"
      And User modifies Date of Birth and Address with "CONFIGURED_DATA"
      And User clicks the guest form "Save Changes" button
      Then The Save button should show a Saving state
      And User should be redirected to the Guest Profile summary page
      And A success toast "CONFIGURED_MESSAGE" should display

    @UI @High @Functional @Key:TC_GST_014 @smoke
    Scenario: TC_GST_014 Validate Duplicate Email Prevention across Guest Profiles (Business Logic)
      Given User is logged in as Super Admin
      And User navigates to the "Guests" module from the sidebar
      When User clicks "Edit" on guest row "CONFIGURED_GUEST"
      And User enters duplicate email "CONFIGURED_EMAIL" on guest form
      And User clicks the guest form "Save Changes" button
      Then Submission should fail with a duplicate email error for guest module

    @UI @High @Critical @FieldValidation @Negative @Key:TC_GST_004 @smoke
    Scenario: TC_GST_004 Validate Edit Guest Profile mandatory field validations
      Given User is logged in as Super Admin
      And User navigates to the "Guests" module from the sidebar
      When User clicks "Edit" on guest row "CONFIGURED_GUEST"
      And User clears the Full Name input field
      And User clears the Email Address input field
      And User clicks the guest form "Save Changes" button
      Then Submission should be blocked with inline errors under Full Name and Email Address
@UI @High @Functional @Key:TC_GST_028 @smoke
  Scenario: TC_GST_028 Verify "Signed Waiver" PDF preview/download link on Guest Profile summary
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User clicks on completed guest row "CONFIGURED_GUEST"
    And User clicks the signed waiver PDF preview button
    Then The signed waiver PDF should open or download successfully
    
  @UI @Medium @Low @Visual @Key:TC_GST_029 @smoke
  Scenario: TC_GST_029 Verify Signed Waiver empty state when waiver is missing/not submitted
    Given User logged in as Super Admin
    When User navigates to the Guests module from the left navigation menu
    When User clicks on pending guest row "CONFIGURED_GUEST"
    Then The Signed Waiver card should display a not-available message "CONFIGURED_EMPTY_MESSAGE"

  # ==========================================
  # 7. STAFF & GUIDES MODULE
  # ==========================================

  @smoke @Key:SGC001
  Scenario: Verify navigation to Staff & Guides module
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    Then User is navigated to the Staff & Guides page with title, subtitle, and Add Guide button

  @Key:SGC004 @smoke
  Scenario: Verify Search functionality by Guide Name
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Table filters dynamically to display guide records matching the entered name

  @smoke @Key:SGC022 
  Scenario: Verify navigating to Detailed Guide View by clicking guide row
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on a guide row in the table
    Then User is navigated to Guide Details page displaying header, badges, and quick actions

  @smoke @Key:SGC024
  Scenario: Verify 'Edit Profile' button functionality on Guide Details page
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on a guide row in the table
    And User clicks 'Edit Profile' button under Quick Actions panel
    And User advances to Step 2 and updates Years of Guiding Experience
    Then Guide profile is updated successfully with success alert
    And User reverts the Years of Guiding Experience back to original value

  @Key:SGC027 @smoke
  Scenario: Verify changing Guide status to 'Inactive' via Change Status modal
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row with 'Active' status and clicks Actions menu
    And User clicks 'Change Status' from Actions menu
    And User selects 'Inactive' in Change Status modal and clicks Save
    Then Guide status updates to 'Inactive' in table with success alert

  @Key:SGC028 @smoke
  Scenario: Verify changing Guide status back to 'Active' via Change Status modal
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row with 'Inactive' status and clicks Actions menu
    And User clicks 'Change Status' from Actions menu
    And User selects 'Active' in Change Status modal and clicks Save
    Then Guide status updates to 'Active' in table with success alert

  @smoke @Key:SGC046
  Scenario: Verify navigation to 'Add New Guide' wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    Then User is navigated to 'Add New Guide' wizard page starting at Step 1 of 5 'Ranch Selection' initialized at 20% Complete

  @smoke @Key:SGC062
  Scenario: Verify final 'Create Guide' button submission on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 5 of Add Guide wizard
    And User clicks green 'Create Guide' button at bottom right
    Then Guide profile is created successfully and user is redirected to Staff & Guides list

  @smoke @Key:SGC040
  Scenario: Verify final 'Update Guide' button submission on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Update Guide' button in wizard
    Then User is redirected back to Staff & Guides list page with success alert

  @Key:SGC081 @High @Type:Functional @GuideLogin @login @smoke
  Scenario: Verify guide user login and access to assigned bookings and harvest logs in Guide Portal
    Given User navigates to the login page
    When User enters valid email "<email>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected to the Guide Portal with "Test Ranch" header and "Guide Portal" subtitle
    And Left navigation menu displays only Guide Portal links "Dashboard", "Bookings", and "Harvest Logs"
    When User clicks on "Bookings" in the left navigation menu
    Then The Booking Management page displays all assigned bookings under the guide
    When User clicks on "Harvest Logs" in the left navigation menu
    Then The Harvest Logs page displays assigned harvest records and tag allocations

  # ==========================================
  # 8. HARVEST LOGS
  # ==========================================

    @UI @High @Critical @Functional @Navigation @Key:TC_HL_001 @smoke
    Scenario: TC_HL_001 Verify navigation to Harvest Logs module from login page
      Given User is logged in as Super Admin
      When User navigates to the "Harvest Logs" module from the left navigation menu
      Then User should be on "/harvest" harvest logs page
      And The harvest logs page title should be "Harvest Logs"
      And The harvest logs page subtitle should be "View and manage harvest records"

    @UI @High @Functional @StateTransition @Key:TC_HL_002 @smoke
    Scenario: TC_HL_002 Verify + Log New Harvest button navigation
      Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    Then User should be redirected to "/harvest/new" page

    @UI @High @Critical @Functional @Key:TC_HL_013 @smoke
    Scenario: TC_HL_013 Verify successful creation of a new Tag Allocation
      Given User is logged in as Super Admin
      When User navigates to the "Harvest Logs" module from the left navigation menu
      When User clicks the harvest log "Tag Settings" button
      And User clicks the harvest log "+ Add Tag Allocation" button
      And User fills the tag allocation form with an available ranch, category "Turkey", sex "Female", and allocated tags "4"
      And User clicks the harvest log "Create Allocation" button
      Then A harvest log success toast "Tag allocation created successfully" should display
      And The new allocation row should display in the allocation table

@UI @Medium @Functional @Key:TC_HL_018 @smoke
  Scenario: TC_HL_018 Validate Edit Tag Allocation workflow
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And User clicks "Edit" on an existing allocation row
    And User updates the "Allocated Tags" value to "5"
    And User clicks the harvest log "Update Allocation" button
    Then A success toast should display
    And The allocation grid should reflect the updated allocated and remaining tag counts


      @UI @Medium @Validation @Key:TC_HL_019 @smoke
  Scenario: TC_HL_019 Verify Choose Ranch dependency on Log New Harvest form
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    Then The "Animal" dropdown should be disabled
    When User selects "Powderhorn Ranch" from the "Choose Ranch" dropdown
    Then The "Animal" dropdown should be enabled
    And The ranch address details should display

    @UI  @HarvestLogs @Validation @Key:TC_HL_024 @smoke
Scenario: TC_HL_024 Validate "Missing Required Fields" real-time warning banner
  Given User is logged in as Super Admin and navigated to Harvest Logs module
  When User clicks the harvest log "+ Log New Harvest" button
  And User clicks the "Submit Harvest Log" button without filling any fields
  Then Form submission should be blocked
  And A warning banner listing "Ranch selection required", "Hunter selection required", "Animal selection required", and "Sex selection required" should display
  And A red toast popup should appear for harvest log
 
@UI @High @Functional @Key:TC_HL_007 @smoke
  Scenario: TC_HL_007 Verify Search input filtering by Harvest ID and Species
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User enters "Whitetail" in the harvest logs search box
    Then The harvest logs grid should filter in real-time to records matching "Whitetail"

  # ==========================================
  # 9. INVOICE MANAGEMENT MODULE
  # ==========================================

  @smoke @Key:IVC001
  Scenario: Verify navigation to Invoice Management module
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then User is navigated to 'Invoice Management' page with title, subtitle 'View and manage booking invoices', and '+ Generate New Invoice' button visible

  @Key:IVC004 @smoke
  Scenario: Verify Search by exact Invoice Number
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User types search string "<searchString>" in the invoice search bar
    Then Invoices table filters instantly to display ONLY the exact invoice ID "<invoiceId>"

  @Key:IVC015 @smoke
  Scenario: Verify 'Download PDF' action from Actions menu in List View
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "Download PDF" from Invoices Actions menu
    Then A toast notification "Invoice PDF downloaded successfully." appears on Invoices page

  @smoke @Key:IVC038
  Scenario: Verify successful Invoice Creation via 'Create Invoice' button
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects an available completed booking from dropdown
    And User clicks green 'Create Invoice' button
    Then Invoice is created and user is redirected to 'Invoice Management' list page
    And A toast notification "Invoice Created" appears on Invoices page
