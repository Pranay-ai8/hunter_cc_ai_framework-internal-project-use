Feature: Harvest Log & Compliance — Smoke
  As a checked-in Guide / Ranch Admin / Super Admin
  I want to log and view Harvest Log records
  So that hunting compliance stays accurate

  @DataFile:resources/data/harvestdata.csv @Key:harvest_create_valid @smoke @P1
  Scenario: Checked-in user creates a Harvest Log with all confirmed fields valid
    # TC_HARVEST_001
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select date of kill
    And I fill time of kill "td.timeOfKill"
    And I select animal "td.animalName"
    And I select category "td.categoryName"
    And I fill age "td.age"
    And I select sex "td.sex"
    And I fill notes "td.notes"
    And I submit the Harvest Log
    Then the Harvest Log should be saved successfully and appear in the Harvest Log list

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_page_structure @smoke @P2
  Scenario: Harvest Log Form page loads with title and all sections visible
    # TC_UI_HARVEST_001
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    Then the Harvest Log Form page title should be descriptive and its core sections should be visible
