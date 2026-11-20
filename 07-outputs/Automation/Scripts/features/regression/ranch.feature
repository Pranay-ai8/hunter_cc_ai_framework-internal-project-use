@RANCH @regression
Feature: Ranch Configuration — Regression
  As a Super Admin
  I want the Ranch Create/Edit wizard's validation, persistence, and business rules to be correct
  So that ranch records stay accurate and operational rules (e.g. Turnover Days) are enforced

  # ---------- Full-flow / Edit mode ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_002 @regression
  Scenario: Create a Ranch with mandatory and optional fields populated
    # TC_RANCH_002
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin adds a Room named "Cabin A" with 2 beds and cost "150" per night
    And the Super Admin clicks Next
    And the Super Admin fills Initial Settings with the test data
    And the Super Admin reaches Review & Create and submits
    Then the Ranch should be created successfully with a success toast displayed

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_003 @regression
  Scenario: Values persist when navigating back and forward across wizard steps
    # TC_RANCH_003
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin clicks Back twice
    Then previously entered Basic Information and Ranch Details values should remain present

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_004 @regression
  Scenario: Add and remove Rooms on the Rooms step
    # TC_RANCH_004
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin adds a Room named "Cabin A" with 2 beds and cost "150" per night
    And the Super Admin adds a Room named "Cabin B" with 4 beds and cost "250" per night
    And the Super Admin removes the Room named "Cabin A"
    Then only Room "Cabin B" should remain in the Rooms list

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_005 @regression
  Scenario: Edit mode pre-populates all 5 steps from the existing ranch record
    # TC_RANCH_005
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens an existing ranch in Edit mode
    Then each of the 5 steps should display the existing ranch's previously saved values

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_006 @regression @Exception
  Scenario: Edit mode shows an error state when the ranch data fetch fails
    # TC_RANCH_006
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens an existing ranch in Edit mode
    Then the wizard should display an error state instead of the pre-populated ranch form

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_007 @regression
  Scenario: Editing an existing ranch's field and saving persists the change
    # TC_RANCH_007
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens an existing ranch in Edit mode
    And the Super Admin updates the Ranch Description and clicks Save Changes
    Then the Ranch should be created successfully with a success toast displayed

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_008 @regression
  Scenario: Adding a new Huntable Animal to an existing ranch persists on save
    # TC_RANCH_008
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens an existing ranch in Edit mode
    And the Super Admin adds a new Huntable Animal "Elk" and saves
    Then the Ranch should be created successfully with a success toast displayed

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_009 @regression @Negative @Boundary
  Scenario: Removing all Huntable Animals from an existing ranch is blocked on save
    # TC_RANCH_009
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens an existing ranch in Edit mode
    And the Super Admin removes all Huntable Animals from an existing ranch and attempts to Save Changes
    Then a mandatory-field validation error should be shown for Huntable Animals on Ranch Details

  # ---------- Step-level mandatory / format validation ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_010 @regression @Negative
  Scenario: Basic Information step blocks progression when all 5 required fields are blank
    # TC_RANCH_010
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin leaves all Basic Information fields blank
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_011 @regression @Negative
  Scenario: Ranch Details step blocks progression when required fields are blank/unselected
    # TC_RANCH_011
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin selects 0 Huntable Animal(s)
    And the Super Admin clicks Next
    Then the wizard should remain on the "Ranch Details" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_012 @regression @Negative
  Scenario: Initial Settings step blocks submission when all 5 required fields are unset
    # TC_RANCH_012
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin leaves all Initial Settings fields unset
    And the Super Admin attempts to proceed or submit
    Then the wizard should remain on the "Initial Settings" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_013 @regression
  Scenario: Submit button label matches "Create Ranch" in create mode and "Save Changes" in edit mode
    # TC_RANCH_013
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then the submit button should read "Create Ranch"
    When the Super Admin opens an existing ranch in Edit mode
    Then the submit button should read "Save Changes"

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_014 @regression @Negative
  Scenario: Basic Information blocks progression when Contact Email format is invalid
    # TC_RANCH_014
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin enters an invalid-format "Contact Email" on Basic Information with the remaining fields valid
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_014b @regression @Negative
  Scenario: Basic Information blocks progression when Contact Phone format is invalid
    # TC_RANCH_014 (Contact Phone variant)
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin enters an invalid-format "Contact Phone" on Basic Information with the remaining fields valid
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_015 @regression @Boundary
  Scenario: Exactly one Huntable Animal selected is accepted (minimum valid boundary)
    # TC_RANCH_015
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin selects 1 Huntable Animal(s)
    And the Super Admin clicks Next
    Then the wizard should advance to the "Rooms" step

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_016 @regression
  Scenario: Waiver Expiration defaults to 365 on a new ranch
    # TC_RANCH_016
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    Then the Waiver Expiration field should be pre-populated with 365

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_017 @regression
  Scenario: Wizard allows proceeding past the Rooms step without adding any room
    # TC_RANCH_017
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    Then the wizard should advance to the "Initial Settings" step

  # ---------- Turnover Day booking block (FR-RANCH-002) ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_018 @regression @Negative
  Scenario: Booking creation is blocked on a ranch's Turnover Day
    # TC_RANCH_018
    Given the user is logged in as a "Super Admin" user
    And a ranch exists with a configured Turnover Day
    When a booking-capable user attempts to create a booking on the ranch's Turnover Day
    Then booking creation should be blocked with an appropriate error message

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_019 @regression
  Scenario: Booking creation succeeds on a date that is not a Turnover Day
    # TC_RANCH_019
    Given the user is logged in as a "Super Admin" user
    And a ranch exists with a configured Turnover Day
    When a booking-capable user creates a booking on a date that is not the Turnover Day
    Then the booking should be created successfully without a Turnover Day block

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_020 @regression @Boundary
  Scenario: Dates immediately adjacent to a Turnover Day are not blocked
    # TC_RANCH_020
    Given the user is logged in as a "Super Admin" user
    And a ranch exists with a configured Turnover Day
    When a booking-capable user creates a booking on a date that is not the Turnover Day
    Then the booking should be created successfully without a Turnover Day block

  # ---------- FR-RANCH-003 generic field-level validation ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_021 @regression
  Scenario: Basic Information step enforces generic field-level validation independent of FR-RANCH-001
    # TC_RANCH_021
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin leaves "Ranch Name" blank on Basic Information and fills the remaining fields validly
    And the Super Admin enters an invalid-format "Contact Email" on Basic Information with the remaining fields valid
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  # ---------- Per-field mandatory validation (FR-RANCH-001, TC_RANCH_022-030) ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_022 @regression @Negative
  Scenario: Basic Information step blocks progression when Ranch Name is left blank (individually)
    # TC_RANCH_022
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin leaves "Ranch Name" blank on Basic Information and fills the remaining fields validly
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_023 @regression @Negative
  Scenario: Basic Information step blocks progression when Full Address is left blank (individually)
    # TC_RANCH_023
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin leaves "Full Address" blank on Basic Information and fills the remaining fields validly
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_024 @regression @Negative
  Scenario: Basic Information step blocks progression when County is left blank (individually)
    # TC_RANCH_024
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin leaves "County" blank on Basic Information and fills the remaining fields validly
    And the Super Admin clicks Next
    Then the wizard should remain on the "Basic Information" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_025 @regression @Negative
  Scenario: Ranch Details step blocks progression when Property Size is left blank (individually)
    # TC_RANCH_025
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin leaves "Property Size" blank on Ranch Details with the remaining fields valid
    And the Super Admin clicks Next
    Then the wizard should remain on the "Ranch Details" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_026 @regression @Negative
  Scenario: Ranch Details step blocks progression when Ranch Description is left blank (individually)
    # TC_RANCH_026
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin leaves "Ranch Description" blank on Ranch Details with the remaining fields valid
    And the Super Admin clicks Next
    Then the wizard should remain on the "Ranch Details" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_027 @regression @Negative
  Scenario: Initial Settings step blocks submission when Turnover Days is left unset (individually)
    # TC_RANCH_027
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin leaves "Turnover Days" unset on Initial Settings with the remaining fields valid
    And the Super Admin attempts to proceed or submit
    Then the wizard should remain on the "Initial Settings" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_028 @regression @Negative
  Scenario: Initial Settings step blocks submission when Waiver Signing Deadline is left unset (individually)
    # TC_RANCH_028
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin leaves "Waiver Signing Deadline" unset on Initial Settings with the remaining fields valid
    And the Super Admin attempts to proceed or submit
    Then the wizard should remain on the "Initial Settings" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_029 @regression @Negative
  Scenario: Initial Settings step blocks submission when Harvest Report Email Frequency is left unset (individually)
    # TC_RANCH_029
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin leaves "Harvest Report Email Frequency" unset on Initial Settings with the remaining fields valid
    And the Super Admin attempts to proceed or submit
    Then the wizard should remain on the "Initial Settings" step and not advance

  @DataFile:resources/data/ranchdata.csv @Key:tc_ranch_030 @regression @Negative
  Scenario: Initial Settings step blocks submission when Time Zone is left unset (individually)
    # TC_RANCH_030
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin leaves "Time Zone" unset on Initial Settings with the remaining fields valid
    And the Super Admin attempts to proceed or submit
    Then the wizard should remain on the "Initial Settings" step and not advance

  # ---------- Generic: Resilience / Network ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_gen_ranch_002 @regression
  Scenario: Ranch wizard retains entered data when the browser tab loses and regains focus
    # TC_GEN_RANCH_002
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    Then previously entered Basic Information and Ranch Details values should remain present

  @DataFile:resources/data/ranchdata.csv @Key:tc_gen_ranch_003 @regression @Boundary
  Scenario: Ranch wizard does not retain unsaved data after the browser is force-closed and reopened mid-fill
    # TC_GEN_RANCH_003
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    Then the wizard should load on Step 1 with a 5-step indicator showing Step 1 as current

  @DataFile:resources/data/ranchdata.csv @Key:tc_gen_ranch_004 @regression @CriticalPath
  Scenario: Network connectivity loss while submitting the final step does not silently discard entered data
    # TC_GEN_RANCH_004
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

  # ---------- UI: Structure / Navigation / Interactive / Responsive / Cross-Browser / Accessibility ----------

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_002 @regression
  Scenario: A loading indicator is displayed while the existing ranch record is being fetched
    # TC_UI_RANCH_002
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens an existing ranch in Edit mode
    Then a loading indicator should be visibly displayed while the ranch data loads

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_003 @regression
  Scenario: Summary cards render for all 4 prior steps, matching entered data
    # TC_UI_RANCH_003
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin fills Initial Settings with the test data
    And the Super Admin clicks Next
    Then a summary card should be rendered for each of the 4 prior steps matching the entered data

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_004 @regression
  Scenario: Step indicator highlights Step 2 as current after advancing from Step 1
    # TC_UI_RANCH_004
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    Then the wizard should advance to the "Ranch Details" step

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_005 @regression
  Scenario: "Back" button on Step 2 unconditionally returns to Step 1
    # TC_UI_RANCH_005
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin clicks Back
    Then the wizard should advance to the "Basic Information" step

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_006 @regression
  Scenario: Mandatory-field indicators are displayed for all 5 Basic Information fields
    # TC_UI_RANCH_006
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then a mandatory-field indicator should be visible next to each of the 5 Basic Information fields

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_007 @regression
  Scenario: Mandatory-field indicators are displayed for Property Size, Ranch Description, and Huntable Animals
    # TC_UI_RANCH_007
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin clicks Next
    Then a mandatory-field indicator should be visible next to Property Size, Ranch Description, and Huntable Animals

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_008 @regression
  Scenario: Mandatory-field indicators are displayed for all 5 Initial Settings fields
    # TC_UI_RANCH_008
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    Then a mandatory-field indicator should be visible next to each of the 5 Initial Settings fields

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_009 @regression
  Scenario: Beds stepper control increments and decrements the displayed bed count
    # TC_UI_RANCH_009
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin adds a Room named "Cabin A" with 1 beds and cost "150" per night
    And the Super Admin clicks the Beds stepper increment twice then decrement once
    Then the displayed Beds count should reflect the stepper's current value

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_010 @regression
  Scenario: Turnover Days dropdown opens and displays its list of options
    # TC_UI_RANCH_010
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin opens the Turnover Days dropdown
    Then the Turnover Days dropdown should display its list of options

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_011 @regression
  Scenario: Time Zone searchable dropdown filters displayed options as the user types
    # TC_UI_RANCH_011
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin fills Basic Information with the test data
    And the Super Admin clicks Next
    And the Super Admin fills Ranch Details with the test data
    And the Super Admin clicks Next
    And the Super Admin proceeds past the Rooms step without adding any room
    And the Super Admin types "Denver" into the Time Zone dropdown
    Then the Time Zone option list should filter to matches containing "Denver"

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_012 @regression
  Scenario: "Next" button renders in a visually disabled state when required fields are unfilled
    # TC_UI_RANCH_012
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then the "Next" button should render in a visually disabled state

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_013 @regression
  Scenario: Submit button label matches create/edit mode (UI presentation)
    # TC_UI_RANCH_013
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then the submit button should read "Create Ranch"

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_014 @regression @Responsive
  Scenario: Wizard remains fully usable at mobile viewport (375px)
    # TC_UI_RANCH_014
    Given the viewport is set to 375 by 667 for the ranch wizard
    And the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then all fields, action buttons and the step indicator should remain visible and usable without overflow

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_015 @regression @Responsive
  Scenario: Rooms table does not horizontally overflow the viewport on mobile
    # TC_UI_RANCH_015
    Given the viewport is set to 375 by 667 for the ranch wizard
    And the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin adds a Room named "Cabin A" with 2 beds and cost "150" per night
    Then the Rooms table should not cause horizontal page overflow

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_016 @regression @Responsive
  Scenario: Wizard remains fully usable at desktop viewport (1280px)
    # TC_UI_RANCH_016
    Given the viewport is set to 1280 by 800 for the ranch wizard
    And the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then all fields, action buttons and the step indicator should remain visible and usable without overflow

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_017 @regression @CrossBrowser
  Scenario: Wizard renders and functions consistently across Chrome, Firefox, Safari and Edge
    # TC_UI_RANCH_017
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then the Create Ranch wizard should render and function consistently across Chrome, Firefox, Safari and Edge

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_018 @regression @AccessibilityBasic
  Scenario: Tab key moves focus through fields within a step in a logical order
    # TC_UI_RANCH_018
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    And the Super Admin presses Tab repeatedly on the Basic Information step
    Then focus should move through the Basic Information fields in a logical order

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_020 @regression @AccessibilityBasic
  Scenario: Form fields across all 5 steps have visible, correctly associated labels
    # TC_UI_RANCH_020
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then every form field across all 5 steps should have a visible, correctly associated label

  @DataFile:resources/data/ranchdata.csv @Key:tc_ui_ranch_021 @regression @AccessibilityBasic
  Scenario: Step indicator communicates the current step via an accessible name/state
    # TC_UI_RANCH_021
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Configuration
    When the Super Admin opens the Create Ranch wizard
    Then the step indicator should expose the current step via an accessible attribute
