Feature: Guest / Hunter Directory — Smoke
  As a Ranch Admin / Super Admin
  I want the Guest Directory to load correctly
  So that the critical guest-visibility path is healthy before every deeper regression run

  @DataFile:resources/data/guestdata.csv @Key:ui_guest_directory_structure @smoke @P2
  Scenario: Guest Directory - Page Load - Page structure loads with table visible
    # TC_UI_GUEST_001
    Given User is logged in as "td.role"
    When I navigate to the Guest Directory
    Then the Guest Directory should display a descriptive title
    And the guest table should be visible with its structure rendered
