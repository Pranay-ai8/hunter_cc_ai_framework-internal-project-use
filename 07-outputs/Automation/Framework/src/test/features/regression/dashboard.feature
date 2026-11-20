@regression
@Dashboard @UI
Feature: Dashboard Layout Verification

  Background:
    Given User is authenticated as Super Admin on the Dashboard page

  @UI @Low @Key:TC_DB_001
  Scenario: TC_DB_001 Verify Dashboard page title and header static layout
    Then The dashboard title should be "Dashboard"
    And The dashboard subtitle should be "Complete ranch management overview"

  @UI @High @Functional @Key:TC_DB_002
  Scenario: TC_DB_002 Verify + New Booking CTA button navigation
    When User clicks on the "+ New Booking" button
    Then User should be redirected to "/bookings/new" page

  @UI @Database @Critical @Key:TC_DB_003
  Scenario: TC_DB_003 Validate accuracy of KPI Metric Cards data against DB
    When User fetches the current KPI metrics from the database
    Then The dashboard KPI cards should accurately match the database records

  @UI @Medium @EdgeCase @Key:TC_DB_004
  Scenario: TC_DB_004 Verify metric card responsiveness on zero/empty state
    Then All KPI metric cards should gracefully display zero states without error text

  @UI @Critical @Functional @TC_DB_005 @Key:TC_DB_005
  Scenario: TC_DB_005 Verify Ranch Filter dropdown on Booking Calendar
    When User selects the first available ranch option from the calendar filter
    Then The calendar should re-filter to display events for the selected ranch
    When User selects "All Ranches" from the calendar ranch filter
    Then The calendar should display events for all ranches

  @UI @High @Functional @TC_DB_006 @Key:TC_DB_006
  Scenario: TC_DB_006 Verify Calendar View switches (Month, Week, Day, Agenda)
    When User switches the calendar view to "Month"
    Then The calendar should display the "Month" layout grid
    When User switches the calendar view to "Week"
    Then The calendar should display the "Week" layout grid
    When User switches the calendar view to "Day"
    Then The calendar should display the "Day" layout grid
    When User switches the calendar view to "Agenda"
    Then The calendar should display the "Agenda" view with appropriate state

  @UI @Medium @Functional @TC_DB_007 @Key:TC_DB_007
  Scenario: TC_DB_007 Verify Calendar date navigation (Today, <, >)
    When User clicks the Next month button
    Then The calendar header should display "{nextMonth}"
    When User clicks the Previous month button
    Then The calendar header should display "{currentMonth}"
    When User clicks the Today button
    Then The calendar header should highlight the current month and today's date

  @UI @Medium @Low @TC_DB_008 @Key:TC_DB_008
  Scenario: TC_DB_008 Verify Booking Type legend and event color coding
    Then The calendar legend should display correct color indicators for booking types
    And The calendar events should match their corresponding booking type legend colors

  @UI @Medium @Low @TC_DB_009 @Key:TC_DB_009
  Scenario: TC_DB_009 Verify layout view toggle for Recent Bookings section
    When User scrolls to the "Recent Bookings" section
    And User clicks the Grid Card view toggle button
    Then The Recent Bookings section should display in Card layout
    When User clicks the Table view toggle button
    Then The Recent Bookings section should display in Table layout without data loss

  @UI @High @Functional @Navigation @TC_DB_010 @Key:TC_DB_010
  Scenario: TC_DB_010 Verify "View All" navigation for Recent Bookings section
    When User scrolls to the "Recent Bookings" section
    And User clicks the "View All" button beside Recent Bookings header
    Then User should be redirected to "/bookings" page

 @UI @Critical @Functional @TC_DB_011 @Key:TC_DB_011
  Scenario: TC_DB_011 Verify action dropdown menu on Recent Bookings table
    When User scrolls to the "Recent Bookings" section
    And User clicks the "View All" button beside Recent Bookings header
    Then User should be redirected to "/bookings" page
    When User opens the action menu for the first booking row
    Then The action menu should display valid context options for the booking status
    When User selects "Edit" from the booking action menu
    Then User should be redirected to the booking edit page

  @UI @Medium @Low @TC_DB_012 @Key:TC_DB_012
  Scenario: TC_DB_012 Verify layout view toggle for Active Ranches section
    When User scrolls to the "Active Ranches" section
    And User clicks the Card view button in Active Ranches section
    Then The Active Ranches section should display in Card layout with ranch details
    When User clicks the Table view button in Active Ranches section
    Then The Active Ranches section should display in Table layout with ranch details

  @UI @High @Functional @Navigation @TC_DB_013 @Key:TC_DB_013
  Scenario: TC_DB_013 Verify "View All" navigation for Active Ranches section
    When User scrolls to the "Active Ranches" section
    And User clicks the "View All" button beside Active Ranches header
    Then User should be redirected to "/ranches" page

  @UI @High @Critical @StateTransition @Functional @TC_DB_014 @Key:TC_DB_014
  Scenario: TC_DB_014 Verify Edit Ranch workflow from Ranches overview page
    When User scrolls to the "Active Ranches" section
    And User clicks the "View All" button beside Active Ranches header
    Then User should be redirected to "/ranches" page
    When User clicks the "Edit" button for the first available ranch row
    Then User should be redirected to the ranch edit page starting at Step 1 of 5                   

  @UI @High @Critical @Navigation @System @TC_DB_015 @Key:TC_DB_015
  Scenario: TC_DB_015 Verify cross-module sidebar navigation stability
    When User navigates through sidebar items "Bookings", "Ranches", "Staff & Guides", and "Invoices"
    And User clicks "Dashboard" from the left navigation menu
    Then User should be redirected to "/dashboard" page
    And The dashboard title should be "Dashboard"

  @UI @Medium @MobileResponsiveness @TC_DB_016 @Key:TC_DB_016
  Scenario Outline: TC_DB_016 Verify Dashboard layout responsiveness on tablet/mobile viewports
    Given User sets the viewport size to <width> pixels by <height> pixels
    Then The dashboard layout should adjust gracefully without broken visual elements

    Examples:
      | width | height |
      | 768   | 1024   |
      | 375   | 812    |

  @UI @High @Functional @Navigation @TC_DB_017 @Key:TC_DB_017
  Scenario: TC_DB_017 Validate KPI Metric Card presence (Active Bookings card)
    Then The "Active Bookings" KPI metric card should be displayed with valid data

  @UI @High @Functional @Navigation @TC_DB_018 @Key:TC_DB_018
  Scenario: TC_DB_018 Validate KPI Metric Card pending waivers count
    When User clicks the "Pending Waivers" KPI metric card
    Then The "Pending Waivers" count should be retrieved and displayed accurately

  @UI @Medium @Functional @Navigation @TC_DB_019 @Key:TC_DB_019
  Scenario: TC_DB_019 Validate KPI Metric Card total ranches count
    When User clicks the "Total Ranches" KPI metric card
    Then The "Total Ranches" count should be retrieved and displayed accurately

  @UI @High @Functional @TC_DB_020 @Key:TC_DB_020
  Scenario: TC_DB_020 Verify calendar event bar click navigates directly to booking details page
    When User clicks on the first available calendar event bar
    Then User should be redirected to the booking details page

  @UI @Medium @EdgeCase @Visual @TC_DB_021 @Key:TC_DB_021
  Scenario: TC_DB_021 Verify overlapping booking events rendering on Calendar
    When User selects "All Ranches" from the calendar ranch filter
    Then Overlapping calendar event bars should stack neatly or display an overflow indicator

  @UI @Medium @Low @Formatting @TC_DB_022 @Key:TC_DB_022
  Scenario: TC_DB_022 Validate Revenue Metric calculation formatting
    Then The "This Month Revenue" KPI metric card should display properly formatted currency with dollar signs and commas

  @UI @High @Functional @Performance @TC_DB_023 @Key:TC_DB_023
  Scenario: TC_DB_023 Verify Dashboard auto-refresh on tab focus
    When User switches tab focus away and back to the Dashboard
    Then The dashboard metrics should sync without requiring a full browser reload

  @UI @Medium @EdgeCase @Visual @TC_DB_024 @Key:TC_DB_024
  Scenario: TC_DB_024 Validate Recent Bookings empty state handling
    When User scrolls to the "Recent Bookings" section
    Then The Recent Bookings section should display a clean empty state message if no bookings exist

  @UI @Medium @EdgeCase @Visual @TC_DB_025 @Key:TC_DB_025
  Scenario: TC_DB_025 Validate Active Ranches empty state handling
    When User scrolls to the "Active Ranches" section
    Then The Active Ranches section should display a clean empty state message if no ranches exist

  @UI @Medium @Functional @TC_DB_026 @Key:TC_DB_026
  Scenario: TC_DB_026 Validate Calendar view persistence when switching between Month and Day views
    When User selects the first available ranch option from the calendar filter
    And User switches the calendar view to "Day"
    And User clicks the Next month button
    And User switches the calendar view to "Month"
    Then The calendar ranch filter should remain set to the selected ranch

  @UI @High @Critical @Security @ErrorHandling @TC_DB_027 @Key:TC_DB_027
  Scenario: TC_DB_027 Validate session expiration during prolonged Dashboard inactivity
    When User session expires due to inactivity
    And User clicks on the "New Booking" button
    Then User should be redirected to "/login" page

  

 

  @UI @High @Critical @Security @TC_DB_030 @Key:TC_DB_030
  Scenario: TC_DB_030 Verify XSS sanitization on Dashboard Booking titles and Ranch names
    Then Script payload tags should render strictly as plain text strings without execution

  @UI @Medium @Accessibility @TC_DB_031 @Key:TC_DB_031
  Scenario: TC_DB_031 Verify Keyboard Accessibility & Tab Focus order on Dashboard
    When User presses Tab key repeatedly from the page header
    Then Focus should move logically through Header profile, New Booking CTA, KPI Cards, Calendar Controls, Recent Bookings controls, and Active Ranches controls

  @UI @Medium @Performance @EdgeCase @TC_DB_032 @Key:TC_DB_032
  Scenario: TC_DB_032 Validate rapid double-click on + New Booking CTA button
    When User rapidly double-clicks the New Booking button
    Then System should initiate a single navigation without duplicate history stacking

  @UI @Medium @CrossBrowser @TC_DB_033 @Key:TC_DB_033
  Scenario: TC_DB_033 Verify Cross-Browser rendering of Dashboard KPI cards and Calendar grid
    Then The Dashboard KPI cards and Calendar grid should render uniformly without broken layout

  @UI @Medium @Low @Functional @Visual @TC_DB_034 @Key:TC_DB_034
  Scenario: TC_DB_034 Validate Turnover Day event bar rendering on Booking Calendar
    Then The Turnover Day event bar should display in a distinct purple color

  @UI @Medium @DataIntegrity @TC_DB_037 @Key:TC_DB_037
  Scenario: TC_DB_037 Validate Active Ranches summary and view all detailed view
    When User scrolls to the "Active Ranches" section
    And User clicks "View All" in the Active Ranches section
    Then User should be navigated to the full Ranch Management page
    And The full ranches table should display Rooms and Capacity details

  @UI @Medium @Functional @TC_DB_038 @Key:TC_DB_038
  Scenario: TC_DB_038 Verify Agenda view date range boundary handling
    When User navigates the Agenda view using Next and Previous arrows
    Then The Agenda header should dynamically display the correct 7-day date range

 

  @UI @Medium @Navigation @StateTransition @TC_DB_040 @Key:TC_DB_040
  Scenario: TC_DB_040 Verify browser Back button behavior after "View All" CTA
    When User scrolls to the "Recent Bookings" section
    And User captures current scroll position
    And User clicks the "View All" button beside Recent Bookings header
    Then User should be redirected to "/bookings" page
    When User clicks the browser Back button
    Then Browser should return directly to the admin Dashboard page with scroll position preserved

  @UI @High @Generic @TC_DB_041 @Key:TC_DB_041
  Scenario: TC_DB_041 Validate Tag Allocation progress bar mathematical calculation accuracy
    Then The Tag Allocation progress bar fill percentage should accurately match usage calculations

  @UI @Medium @Generic @Accessibility @TC_DB_042 @Key:TC_DB_042
  Scenario: TC_DB_042 Verify browser zoom handling up to 200% on Dashboard page
    When User zooms the browser to 200%
    Then All header CTAs, dropdowns, and calendar controls should remain functional without clipping

  @UI @Low @Generic @Interrupts @TC_DB_043 @Key:TC_DB_043
  Scenario: TC_DB_043 Verify Dashboard behavior upon tab switching, minimization, and window restore
    When User switches tabs or minimizes the window for 5 seconds
    And User restores focus back to the Dashboard tab
    Then Dashboard session should remain active without UI freezing or console errors

  @UI @Medium @Generic @NetworkThrottling @TC_DB_044 @Key:TC_DB_044
  Scenario: TC_DB_044 Verify Dashboard behavior on Slow 3G / High Latency network connections
    Given DevTools network throttling is set to "Slow 3G"
    When User reloads the Dashboard page
    Then Subtle loading indicators should display and metrics should populate cleanly without timing out

  @UI @High @Generic @ErrorHandling @TC_DB_045 @Key:TC_DB_045
  Scenario: TC_DB_045 Verify system handling when backend API returns 500 Internal Server Error for Dashboard metrics
    Given Backend dashboard metrics API is mocked to return HTTP 500
    When User reloads the Dashboard page
    Then Dashboard widgets should display a graceful error fallback state without JS exceptions

  @UI @Medium @Generic @CrossPlatform @TC_DB_046 @Key:TC_DB_046
  Scenario: TC_DB_046 Verify mobile browser viewport compatibility
    When User emulates a mobile browser viewport
    Then Calendar controls, touch targets, and dropdown overlays should remain responsive without clipping