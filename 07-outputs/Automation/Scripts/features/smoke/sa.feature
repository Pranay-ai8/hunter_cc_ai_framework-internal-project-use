@SA @smoke
Feature: Super Admin & Ranch Admin Management — Smoke
  As a Super Admin
  I want to create and manage Ranch Admin accounts
  So that each ranch has an administrator

  @DataFile:resources/data/sadata.csv @Key:tc_sa_001 @smoke
  Scenario: Super Admin creates a Ranch Admin with all mandatory fields valid and welcome email toggle ON
    # TC_SA_001
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin creates a Ranch Admin using the test data
    Then the Ranch Admin account should be created successfully
    And a success toast should confirm the Ranch Admin was created

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_001 @smoke
  Scenario: Create Ranch Admin Modal loads with all expected fields and controls visible
    # TC_UI_SA_001
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    Then the Create Ranch Admin modal should be displayed with all expected fields visible

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_002 @smoke
  Scenario: "Create Ranch Admin" button unconditionally opens the Create Ranch Admin modal
    # TC_UI_SA_002
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    Then the Create Ranch Admin modal should be displayed with all expected fields visible
