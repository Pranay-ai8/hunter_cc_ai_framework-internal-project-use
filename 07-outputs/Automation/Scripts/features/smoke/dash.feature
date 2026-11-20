@DASH @smoke
Feature: Dashboard & Reporting — Smoke
  As a Super Admin
  I want the Dashboard to load with all its sections
  So that I get a complete ranch management overview at a glance

  @DataFile:resources/data/dashdata.csv @Key:dash_page_structure @smoke @P2
  Scenario: Dashboard loads with all sections present
    # TC_UI_DASH_001
    Given User is logged in as "Super Admin"
    When I navigate to the Dashboard
    Then the Booking Calendar, Recent Bookings table, 6 summary metric cards and Active Ranches section should all be visible
