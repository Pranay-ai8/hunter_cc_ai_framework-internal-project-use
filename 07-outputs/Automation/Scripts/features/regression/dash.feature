@DASH @regression
Feature: Dashboard & Reporting — Regression
  As a Super Admin
  I want the Dashboard's calendar, metrics, recent bookings and active ranches to be accurate
  So that I can trust the operational overview it presents

  # ---------- Booking Calendar (TC_DASH_001-002) ----------

  @DataFile:resources/data/dashdata.csv @Key:dash_calendar_matches_booking @regression @P2
  Scenario: Calendar displays booking events matching actual booking data
    # TC_DASH_001
    Given User is logged in as "Super Admin"
    And the test data booking exists with its test data dates
    When I navigate to the Dashboard
    Then the Booking Calendar should display an event for the test data booking on the correct dates

  @DataFile:resources/data/dashdata.csv @Key:dash_calendar_reflects_new_booking @regression @P2
  Scenario: Calendar reflects a newly created booking
    # TC_DASH_002
    Given User is logged in as "Super Admin"
    And I am viewing the Dashboard Booking Calendar
    When I create a new booking with dates within the visible calendar range, using the test data
    Then the Booking Calendar should now display an event for the test data booking

  # ---------- Recent Bookings (TC_DASH_003-004) ----------

  @DataFile:resources/data/dashdata.csv @Key:dash_recentbookings_includes_recent @regression @P2
  Scenario: Recent Bookings table includes bookings created within the last 6 days
    # TC_DASH_003
    Given User is logged in as "Super Admin"
    And the test data booking was created within the last 6 days
    When I navigate to the Dashboard
    Then the test data booking should appear in the Recent Bookings table

  @DataFile:resources/data/dashdata.csv @Key:dash_recentbookings_excludes_old @regression @P2
  Scenario: Recent Bookings table excludes bookings older than the 6-day window
    # TC_DASH_004
    Given User is logged in as "Super Admin"
    And the test data booking was created more than 6 days ago
    When I navigate to the Dashboard
    Then the test data booking should not appear in the Recent Bookings table

  # ---------- Summary Metric Cards (TC_DASH_005-014, 016) ----------

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_totalranches @regression @P2
  Scenario: Total Ranches metric reflects the actual ranch count
    # TC_DASH_005
    Given User is logged in as "Super Admin"
    And the test data ranch count is configured
    When I navigate to the Dashboard
    Then the Total Ranches metric card should display the test data ranch count

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_totalranches_updates @regression @P3
  Scenario: Total Ranches metric updates after a new ranch is added
    # TC_DASH_006
    Given User is logged in as "Super Admin"
    And the Total Ranches metric currently reads the test data current count
    When I create a new ranch via the Ranch wizard
    And I navigate to the Dashboard
    Then the Total Ranches metric card should display the test data expected count

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_activebookings_includes_pending @regression @P2
  Scenario: Active Bookings metric includes pending bookings per the stated rule
    # TC_DASH_007
    Given User is logged in as "Super Admin"
    And the test data active and pending bookings exist
    When I navigate to the Dashboard
    Then the Active Bookings metric card should display the test data expected total

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_activebookings_updates @regression @P2
  Scenario: Active Bookings metric accurately reflects underlying booking data after a change
    # TC_DASH_008
    Given User is logged in as "Super Admin"
    And the Active Bookings metric currently reads the test data current count
    When one active booking is canceled or completed
    And I navigate to the Dashboard
    Then the Active Bookings metric card value should have updated

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_ranchadmins @regression @P2
  Scenario: Ranch Admins count metric reflects the actual number of Ranch Admin users
    # TC_DASH_009
    Given User is logged in as "Super Admin"
    And the test data Ranch Admin user count exists
    When I navigate to the Dashboard
    Then the Ranch Admins metric card should display the test data admin count

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_totalguides @regression @P2
  Scenario: Total Guides metric reflects the actual number of Guide records
    # TC_DASH_010
    Given User is logged in as "Super Admin"
    And the test data Guide record count exists
    When I navigate to the Dashboard
    Then the Total Guides metric card should display the test data guide count

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_revenue_sums_current_month @regression @P2
  Scenario: This Month Revenue metric reflects the sum of invoiced revenue for the current calendar month
    # TC_DASH_011
    Given User is logged in as "Super Admin"
    And the test data invoices were generated this month
    When I navigate to the Dashboard
    Then the This Month Revenue metric card should display the test data expected sum

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_revenue_excludes_prior_month @regression @P3
  Scenario: This Month Revenue excludes invoices from a prior calendar month
    # TC_DASH_012
    Given User is logged in as "Super Admin"
    And the test data invoice was generated last month
    When I navigate to the Dashboard
    Then the This Month Revenue metric card value should not include the test data prior-month amount

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_pendingwaivers @regression @P2
  Scenario: Pending Waivers metric reflects the accurate count of outstanding/unsigned waivers
    # TC_DASH_013
    Given User is logged in as "Super Admin"
    And the test data pending-waiver count exists
    When I navigate to the Dashboard
    Then the Pending Waivers metric card should display the test data pending waiver count

  @DataFile:resources/data/dashdata.csv @Key:dash_metric_pendingwaivers_decreases @regression @P3
  Scenario: Pending Waivers metric decreases once a pending waiver is signed
    # TC_DASH_014
    Given User is logged in as "Super Admin"
    And the Pending Waivers metric currently reads the test data current count
    When one pending waiver is marked signed via the application's status mechanism
    And I navigate to the Dashboard
    Then the Pending Waivers metric card should display the test data expected count

  @DataFile:resources/data/dashdata.csv @Key:dash_empty_zero_state @regression @P3
  Scenario: Metric cards display correct zero/empty state with no underlying data
    # TC_DASH_016
    Given User is logged in as "Super Admin" on a fresh empty test tenant
    When I navigate to the Dashboard
    Then all 6 metric cards should display the zero/empty equivalent

  # ---------- Active Ranches Section (TC_DASH_015) ----------

  @DataFile:resources/data/dashdata.csv @Key:dash_activeranches_listing @regression @P2
  Scenario: Section lists ranches matching actual active-ranch data
    # TC_DASH_015
    Given User is logged in as "Super Admin"
    And the test data ranches are active
    When I navigate to the Dashboard
    Then the Active Ranches section should list the test data ranches

  # ---------- UI: Page Structure & Layout (TC_UI_DASH_002-007) ----------

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_page_title @regression @P3
  Scenario: Page title is correct
    # TC_UI_DASH_002
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    Then the page heading should read "Dashboard"

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_calendar_layout @regression @P2
  Scenario: Calendar renders with a navigable month view and visible event markers
    # TC_UI_DASH_003
    Given User is logged in as "Super Admin"
    And the test data booking exists within the current month
    When I navigate to the Dashboard
    Then the Booking Calendar should display a month grid with prev/next controls and an event marker on the test data booking's date

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_calendar_month_nav @regression @P3
  Scenario: Clicking next/previous month control changes the displayed month
    # TC_UI_DASH_004
    Given User is logged in as "Super Admin"
    And I am viewing the Booking Calendar on the current month
    When I click the "next month" control
    Then the calendar should advance to display the following month's label and grid

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_recentbookings_headers @regression @P3
  Scenario: Recent Bookings table renders with the live column headers
    # TC_UI_DASH_005 — DISCREPANCY: live table columns are Guest Name, Ranch, Dates,
    # Guide, Hunters, Guests, Type, Status — not "Booking ID, Ranch, Dates, Status" as
    # loosely described by this TC's documented expected result. See dash.json.
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    Then the Recent Bookings table headers should match the live column set

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_metriccards_layout @regression @P2
  Scenario: All 6 metric cards render with a label and a value area
    # TC_UI_DASH_006
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    Then each of the 6 metric cards should display its expected label text and a populated value area

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_activeranches_layout @regression @P3
  Scenario: Active Ranches section renders as a list/grid with entries visible
    # TC_UI_DASH_007
    Given User is logged in as "Super Admin"
    And the test data ranch is active
    When I navigate to the Dashboard
    Then the Active Ranches section should render a visible table structure containing the test data ranch

  # ---------- Responsive, Cross-Browser, Accessibility (TC_UI_DASH_008-012) ----------

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_mobile_viewport @regression @P2
  Scenario: Metric cards stack sensibly at mobile viewport (375px)
    # TC_UI_DASH_008
    Given User is logged in as "Super Admin"
    When I set the viewport to 375 by 812
    And I navigate to the Dashboard
    Then the 6 metric cards should stack into a narrow column with no horizontal overflow

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_desktop_viewport @regression @P3
  Scenario: Dashboard widgets render correctly at desktop viewport (1280px)
    # TC_UI_DASH_009
    Given User is logged in as "Super Admin"
    When I set the viewport to 1280 by 800
    And I navigate to the Dashboard
    Then Booking Calendar, Recent Bookings, metric cards and Active Ranches should render in their designed layout with no overlap

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_cross_browser @regression @P3
  Scenario: Dashboard renders consistently across supported browsers
    # TC_UI_DASH_010
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    Then the page-structure check should pass on this browser project

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_keyboard_focus @regression @P3
  Scenario: Metric cards and calendar controls are keyboard-reachable with visible focus
    # TC_UI_DASH_011
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    And I press Tab repeatedly from page load
    Then each interactive element should receive focus in logical order with a visible focus indicator

  @DataFile:resources/data/dashdata.csv @Key:ui_dash_icon_alt_text @regression @P3
  Scenario: Metric card icons have accessible alt text/names
    # TC_UI_DASH_012
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    Then every metric card icon should expose meaningful alt text or an accessible name
