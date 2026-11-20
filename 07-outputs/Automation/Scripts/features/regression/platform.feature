Feature: Platform / Cross-Cutting Technical — Regression
  As any authenticated user with list access
  I want pagination and generic field validation to behave consistently across the application
  So that shared platform behavior stays correct regardless of which module's list/form is in use

  # ---------- Pagination (TC_PLATFORM_001-010) ----------

  @DataFile:resources/data/platformdata.csv @Key:platform_bookings_pagination_basic @regression @P2
  Scenario: Bookings list paginates correctly across pages
    # TC_PLATFORM_001
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    And I go to the next page
    Then the next page should display the next set of booking records with no duplicate or missing records

  @DataFile:resources/data/platformdata.csv @Key:platform_ranches_pagination_basic @regression @P2
  Scenario: Ranches list paginates correctly across pages
    # TC_PLATFORM_002
    Given User is logged in as "td.role"
    When I navigate to the Ranches list
    And I go to the next page
    Then the next page should display the next set of ranch records with no duplicate or missing records

  @DataFile:resources/data/platformdata.csv @Key:platform_guides_pagination_basic @regression @P2
  Scenario: Guides/Staff list paginates correctly across pages
    # TC_PLATFORM_003
    Given User is logged in as "td.role"
    When I navigate to the Staff & Guides list
    And I go to the next page
    Then the next page should display the next set of guide/staff records with no duplicate or missing records

  @DataFile:resources/data/platformdata.csv @Key:platform_harvest_pagination_basic @regression @P2
  Scenario: Harvest logs list paginates correctly across pages
    # TC_PLATFORM_004
    Given User is logged in as "td.role"
    When I navigate to the Harvest Logs list
    And I go to the next page
    Then the next page should display the next set of harvest log records with no duplicate or missing records

  @DataFile:resources/data/platformdata.csv @Key:platform_invoices_pagination_basic @regression @P2
  Scenario: Invoices list paginates correctly across pages
    # TC_PLATFORM_005
    Given User is logged in as "td.role"
    When I navigate to the Invoices list
    And I go to the next page
    Then the next page should display the next set of invoice records with no duplicate or missing records

  @DataFile:resources/data/platformdata.csv @Key:platform_bookings_pagination_boundary @regression @P2
  Scenario: Bookings list pagination controls behave correctly on first and last pages
    # TC_PLATFORM_006
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the Previous pagination control should be disabled
    When I go to the last page
    Then the Next pagination control should be disabled

  @DataFile:resources/data/platformdata.csv @Key:platform_ranches_pagination_boundary @regression @P2
  Scenario: Ranches list pagination controls behave correctly on first and last pages
    # TC_PLATFORM_007
    Given User is logged in as "td.role"
    When I navigate to the Ranches list
    Then the Previous pagination control should be disabled
    When I go to the last page
    Then the Next pagination control should be disabled

  @DataFile:resources/data/platformdata.csv @Key:platform_guides_pagination_boundary @regression @P2
  Scenario: Guides/Staff list pagination controls behave correctly on first and last pages
    # TC_PLATFORM_008
    Given User is logged in as "td.role"
    When I navigate to the Staff & Guides list
    Then the Previous pagination control should be disabled
    When I go to the last page
    Then the Next pagination control should be disabled

  @DataFile:resources/data/platformdata.csv @Key:platform_harvest_pagination_boundary @regression @P2
  Scenario: Harvest logs list pagination controls behave correctly on first and last pages
    # TC_PLATFORM_009
    Given User is logged in as "td.role"
    When I navigate to the Harvest Logs list
    Then the Previous pagination control should be disabled
    When I go to the last page
    Then the Next pagination control should be disabled

  @DataFile:resources/data/platformdata.csv @Key:platform_invoices_pagination_boundary @regression @P2
  Scenario: Invoices list pagination controls behave correctly on first and last pages
    # TC_PLATFORM_010
    Given User is logged in as "td.role"
    When I navigate to the Invoices list
    Then the Previous pagination control should be disabled
    When I go to the last page
    Then the Next pagination control should be disabled

  # ---------- Database Migration Sanity & Generic Field Validation (TC_PLATFORM_011-012) ----------

  @DataFile:resources/data/platformdata.csv @Key:platform_db_create_retrieve_sanity @regression @P3
  Scenario: Basic create-and-retrieve operation succeeds against the migrated PostgreSQL-backed datastore
    # TC_PLATFORM_011
    Given User is logged in as "td.role"
    When I create a new Ranch record "td.ranchName" with valid Basic Information
    And I navigate away and return to the Ranches list
    Then the newly created ranch "td.ranchName" should be retrievable with matching data

  @DataFile:resources/data/platformdata.csv @Key:platform_generic_field_validation_email @regression @P2
  Scenario: New Booking form enforces generic field-level validation (valid format) on the Hunter Email field
    # TC_PLATFORM_012
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I attempt to add a hunter with name "td.hunterName" and invalid email "hunter1@ranch"
    Then a format-validation error should be shown for Hunter Email and the booking should not be created

  # ---------- Generic: Pagination Results-Per-Page & Rapid-Click Robustness (TC_GEN_PLATFORM_001-002) ----------

  @DataFile:resources/data/platformdata.csv @Key:gen_platform_results_per_page @regression @P2
  Scenario: Verify the Bookings list displays the correct number of results per page
    # TC_GEN_PLATFORM_001
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the number of displayed bookings on page 1 should match the page-size limit

  @DataFile:resources/data/platformdata.csv @Key:gen_platform_rapid_next_click @regression @P3
  Scenario: Verify rapidly clicking "Next" loads pages correctly without skipping or breaking
    # TC_GEN_PLATFORM_002
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    And I rapidly click Next 5 times
    Then the list should land on a consistent, correct page with no skipped, duplicated or corrupted state and no application error

  # ---------- UI: Pagination Control Consistency (TC_UI_PLATFORM_001-003) ----------

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_pagination_layout_consistent @regression @P3
  Scenario: Pagination control layout/placement is visually consistent across the list views
    # TC_UI_PLATFORM_001
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the pagination control should appear in the standard position and structure
    When I navigate to the Ranches list
    Then the pagination control should appear in the standard position and structure
    When I navigate to the Guest Directory
    Then the pagination control should appear in the standard position and structure

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_pagination_indicator_format @regression @P3
  Scenario: Current-page indicator renders in a consistent, visible format across the list views
    # TC_UI_PLATFORM_002
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the results summary text should match the standard "Showing X to Y of Z results" format
    When I navigate to the Guest Directory
    Then the results summary text should match the standard "Showing X to Y of Z results" format

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_pagination_disabled_styling @regression @P3
  Scenario: Previous/Next buttons render a visually distinct disabled state at page boundaries
    # TC_UI_PLATFORM_003
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the Previous pagination control should be disabled
    When I go to the last page
    Then the Next pagination control should be disabled

  # ---------- UI: Responsive Layouts (TC_UI_PLATFORM_004-005) ----------

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_mobile_viewport @regression @P3
  Scenario: Pagination controls remain visible and usable at mobile viewport (375px)
    # TC_UI_PLATFORM_004
    Given User is logged in as "td.role"
    When I set the viewport to 375 by 812
    And I navigate to the Bookings list
    Then the pagination control should remain visible, readable and not overflow the viewport

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_desktop_viewport @regression @P3
  Scenario: Pagination controls render correctly at desktop viewport (1280px) across list views
    # TC_UI_PLATFORM_005
    Given User is logged in as "td.role"
    When I set the viewport to 1280 by 800
    And I navigate to the Bookings list
    Then the pagination control should render in its designed position with no overlap or truncation
    When I navigate to the Guest Directory
    Then the pagination control should render in its designed position with no overlap or truncation

  # ---------- UI: Cross-Browser, Accessibility (TC_UI_PLATFORM_006-007) ----------

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_cross_browser @regression @P3
  Scenario: Pagination control rendering is consistent across supported browsers
    # TC_UI_PLATFORM_006
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the pagination control should render and behave identically on this browser project

  @DataFile:resources/data/platformdata.csv @Key:ui_platform_pagination_keyboard @regression @P3
  Scenario: Pagination Next/Previous buttons are keyboard-reachable with visible focus
    # TC_UI_PLATFORM_007
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    And I press Tab until focus reaches the pagination controls
    Then Previous and Next buttons should each receive keyboard focus with a visible indicator and be activatable via Enter
