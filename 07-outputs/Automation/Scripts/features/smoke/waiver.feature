@WAIVER @smoke
Feature: Waivers & Document Workflow — Smoke
  As a Ranch Admin / Super Admin
  I want waivers to dispatch automatically at booking creation
  So that every hunter/guest signs the correct liability document

  @DataFile:resources/data/waiverdata.csv @Key:waiver_dispatch_on_booking @smoke @P1
  Scenario: Docuseal waiver is automatically dispatched at booking creation
    # TC_WAIVER_001
    Given User is logged in as "Ranch Admin"
    When I create a new booking for the test data hunter at the test data ranch
    Then the test data hunter's waiver status should show "Sent" immediately without a manual trigger

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_section_visible @smoke @P2
  Scenario: Waiver section loads with status area and Send Waiver button visible
    # TC_UI_WAIVER_001
    Given User is logged in as "Ranch Admin"
    And the test data guest exists on the test data booking
    When I open the Guest Details page for the test data guest
    Then the Waiver section should be visible with a status indicator and a Send Waiver button
