@RANCH @smoke
Feature: Ranch Configuration — Smoke
  As a Super Admin
  I want to create a new ranch through the 5-step wizard
  So that the ranch is available for booking and operations

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_001 @smoke
  Scenario: Create a Ranch with only mandatory fields across all 5 steps
    # TC_RANCH_001
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin fills Initial Settings with the test data
    And the Super Admin reaches Review & Create and submits
    Then the Ranch should be created successfully with a success toast displayed

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_001 @smoke
  Scenario: Create Ranch Wizard loads on Step 1 with a 5-step indicator showing Step 1 as current
    # TC_UI_RANCH_001
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then the wizard should load on Step 1 with a 5-step indicator showing Step 1 as current
