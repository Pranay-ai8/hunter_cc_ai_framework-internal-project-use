Feature: Guide Portal & Role-Based Visibility — Regression
  As a Guide
  I want pricing hidden from my view and my relevant tag counts/bookings visible
  So that role-based visibility boundaries are enforced correctly
  #
  # IMPORTANT — LIVE VERIFICATION GAP (see docs/dom-snapshots/guide.json):
  # This session authenticated as Super Admin only; no Guide-role credentials were
  # available. Super Admin's own Staff & Guide List was confirmed to have NO row-level
  # link, "View as Guide", or impersonation control reachable to preview an actual Guide
  # session. Every scenario below therefore exercises guide-portal.page.ts's UNVERIFIED,
  # documentation-only locators (see that file's header) — each routed through withHeal()
  # so a first real run against genuine Guide-role credentials self-heals and logs drift
  # rather than silently passing against an unconfirmed locator. Re-run and re-verify this
  # entire feature once Guide-role credentials are provisioned.

  # ---------- Pricing visibility restriction (TC_GUIDE_001-004, TC_UI_GUIDE_001-003) ----------

  @DataFile:resources/data/guidedata.csv @Key:guide_no_pricing_invoice @regression @P1 @security-relevant
  Scenario: Guide role does not see pricing when viewing a booking's invoice
    # TC_GUIDE_001
    Given User is logged in as "Guide"
    When I navigate to the invoice/billing view for booking "td.bookingId"
    Then no pricing fields should be visible on the current screen

  @DataFile:resources/data/guidedata.csv @Key:guide_no_pricing_harvest_booking @regression @P1 @security-relevant
  Scenario: Guide role does not see pricing anywhere in Harvest Log or Booking Detail screens
    # TC_GUIDE_002
    Given User is logged in as "Guide"
    When I navigate to the Booking Detail screen for "td.bookingId"
    Then no pricing fields should be visible on the current screen
    When I navigate to the Harvest Log screen for booking "td.bookingId"
    Then no pricing fields should be visible on the current screen

  @DataFile:resources/data/guidedata.csv @Key:guide_no_pricing_network_payloads @regression @P1 @security-relevant
  Scenario: Pricing data is absent from the app's own network responses during normal Guide-session usage
    # TC_GUIDE_003
    Given User is logged in as "Guide"
    When I capture network responses while navigating to the invoice/booking/harvest screens for "td.bookingId"
    Then no pricing field should appear in any captured network response payload

  @DataFile:resources/data/guidedata.csv @Key:guide_direct_nav_invoice_blocked @regression @P1
  Scenario: Direct navigation to a Ranch Admin-only pricing screen does not expose pricing data to a Guide
    # TC_GUIDE_004
    Given User is logged in as "Guide"
    When I directly navigate to the Generate Invoice page URL
    Then the Guide should be blocked/redirected or no pricing data should be exposed

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_no_pricing_dashboard @regression @P1 @security-relevant
  Scenario: No pricing field/column is rendered on the Guide's dashboard/booking list
    # TC_UI_GUIDE_001
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    Then no pricing fields should be visible on the current screen

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_no_pricing_booking_detail @regression @P1 @security-relevant
  Scenario: No pricing field is rendered on the Guide's Booking Detail screen
    # TC_UI_GUIDE_002
    Given User is logged in as "Guide"
    When I navigate to the Booking Detail screen for "td.bookingId"
    Then no pricing fields should be visible on the current screen

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_no_pricing_harvest @regression @P1 @security-relevant
  Scenario: No pricing field is rendered on the Guide's Harvest Log screen
    # TC_UI_GUIDE_003
    Given User is logged in as "Guide"
    When I navigate to the Harvest Log screen for booking "td.bookingId"
    Then no pricing fields should be visible on the current screen

  # ---------- Whitetail Deer tag count display (TC_GUIDE_005-007, TC_UI_GUIDE_004) ----------

  @DataFile:resources/data/guidedata.csv @Key:guide_bucks_tag_count @regression @P2
  Scenario: Bucks tag count is visible to the Guide role
    # TC_GUIDE_005
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    Then the Bucks tag count should display "td.allocatedBucks" allocated and "td.remainingBucks" remaining

  @DataFile:resources/data/guidedata.csv @Key:guide_does_tag_count @regression @P2
  Scenario: Does tag count is visible to the Guide role, shown separately from Bucks
    # TC_GUIDE_006
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    Then the Does tag count should display "td.allocatedDoes" allocated and "td.remainingDoes" remaining
    And the Bucks and Does tag counts should be distinct elements

  @DataFile:resources/data/guidedata.csv @Key:guide_tag_count_updates_after_harvest @regression @P2
  Scenario: Displayed tag counts accurately reflect allocation/consumption after a new harvest is logged
    # TC_GUIDE_007
    Given User is logged in as "Guide"
    And I note the current Bucks tag count on the Guide dashboard
    When a new Whitetail Deer Buck harvest is logged
    And I return to the Guide dashboard
    Then the Bucks tag count should have decreased by exactly one consumed tag

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_bucks_does_distinct_elements @regression @P3
  Scenario: Bucks and Does tag counts render as separately labeled UI elements
    # TC_UI_GUIDE_004
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    Then the Bucks and Does tag counts should be distinct elements

  # ---------- Dashboard list structure, responsive, cross-browser, accessibility (TC_UI_GUIDE_005-010) ----------

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_dashboard_structure @regression @P3 @clarification-pending
  Scenario: Dashboard renders a bookings list with expected columns, independent of which bookings appear
    # TC_UI_GUIDE_005
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    Then the bookings list should render with its expected column headers and at least one row

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_responsive_mobile @regression @P3
  Scenario: Guide dashboard/list view remains usable at mobile viewport (375px)
    # TC_UI_GUIDE_006
    Given User is logged in as "Guide"
    And the viewport is set to "375" by "812"
    When I navigate to the Guide dashboard
    Then the dashboard should render usably with no horizontal overflow and readable tag counts

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_responsive_desktop @regression @P3
  Scenario: Guide dashboard/list view renders correctly at desktop viewport (1280px)
    # TC_UI_GUIDE_007
    Given User is logged in as "Guide"
    And the viewport is set to "1280" by "800"
    When I navigate to the Guide dashboard
    Then the dashboard should render with no overlapping content

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_cross_browser @regression @P3 @cross-browser
  Scenario: Guide portal screens render consistently across supported browsers
    # TC_UI_GUIDE_008
    Given User is logged in as "Guide"
    When I run the pricing-absence check across each supported browser
    Then no pricing fields should be visible on the current screen in any supported browser

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_a11y_focus @regression @P3 @accessibility
  Scenario: Guide portal interactive elements are keyboard-reachable with visible focus
    # TC_UI_GUIDE_009
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    And I tab through the Guide dashboard elements
    Then every interactive element should receive keyboard focus in logical order with a visible focus indicator

  @DataFile:resources/data/guidedata.csv @Key:ui_guide_a11y_column_headers @regression @P3 @accessibility
  Scenario: Guide dashboard list exposes accessible column headers
    # TC_UI_GUIDE_010
    Given User is logged in as "Guide"
    When I navigate to the Guide dashboard
    Then the bookings list column headers should use semantic header markup or an equivalent accessible name
