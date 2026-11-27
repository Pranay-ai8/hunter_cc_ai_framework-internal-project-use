Feature: Staff & Guide Management — Smoke
  As a Ranch Admin / Super Admin
  I want to create and view Guide records
  So that ranch staffing stays accurate

  @DataFile:resources/data/staffdata.csv @Key:staff_create_valid @smoke @P1
  Scenario: Create a new Guide with all mandatory fields valid
    # TC_STAFF_001
    Given User is logged in as "td.role"
    When I open the Add Guide form
    And I select ranch "td.ranchName" for the new guide
    And I proceed to the next wizard step
    And I fill the Basic Details with first name "td.firstName", last name "td.lastName", email "td.email" and phone "td.phone"
    And I select date of birth
    And I proceed to the next wizard step
    And I select employment type "td.employmentType"
    And I enter years of guiding experience "td.years"
    And I proceed to the next wizard step
    And I submit the Guide form
    Then the guide should be saved successfully with status "td.expectedStatus"
    And the guide list should show "td.firstName" "td.lastName" with ranch "td.ranchName"

  @DataFile:resources/data/staffdata.csv @Key:staff_list_page_structure @smoke @P2
  Scenario: Staff & Guide List page loads with title, header and navigation visible
    # TC_UI_STAFF_001
    Given User is logged in as "td.role"
    When I navigate to the Staff & Guides list
    Then the page title should be descriptive and the header/navigation bar should be visible
    And the guide list section should render below the header

  @DataFile:resources/data/staffdata.csv @Key:staff_form_fields_render @smoke @P2
  Scenario: Create Guide form fields render with labels
    # TC_UI_STAFF_003
    Given User is logged in as "td.role"
    When I open the Add Guide form
    Then the Basic Details fields should all be visible, each with a visible label
