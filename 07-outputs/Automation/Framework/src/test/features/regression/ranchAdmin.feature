@regression
@RanchAdmin @UI
Feature: Ranch Admin Management
  As a Super Admin
  I want to manage Ranch Administrators across the application
  So that I can create, update, delete, search, and maintain secure access for ranch personnel


  # =========================================================================
  # 1. CREATION & FIELD VALIDATIONS
  # =========================================================================

  @UI @High @Critical @Functional @Key:TC_RAM_001
  Scenario: TC_RAM_001 Verify successful creation of a new Ranch Admin
    Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "John Doe", Email "john@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then The modal should close and a success toast "Admin created successfully!" should appear
    And The grid should display a new row containing "John Doe"

  @UI @High @Critical @Negative @Key:TC_RAM_002
  Scenario: TC_RAM_002 Verify mandatory field validations on form submission
   Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User leaves all fields empty and submits the form
    Then Form submission should be blocked with inline errors under all mandatory fields

  @UI @High @Medium @BoundaryValue @Key:TC_RAM_003
  Scenario: TC_RAM_003 Validate Password Length Boundary Value Analysis (BVA)
   Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters password "P@ss12a" in the Password field
    And User clicks the "Create Admin" button for ranch admin
    Then A password length error should display
    When User enters password "P@ss12ab" in the Password field
    And User clicks the "Create Admin" button for ranch admin
    Then No password length error should display

  @UI @High @Critical @Security @FieldValidation @Key:TC_RAM_003A
  Scenario: TC_RAM_003A Validate Password Complexity Rules (Negative)
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters password "annyaaa" in the Password field
    And User clicks the "Create Admin" button for ranch admin
    Then A password complexity error should display

  @UI @High @Critical @Functional @Key:TC_RAM_003B
  Scenario: TC_RAM_003B Validate Password Complexity Rules (Positive)
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters password "Anny@1234" in the Password field
    Then Password validation should clear completely

  @UI @Low @Key:TC_RAM_004
  Scenario: TC_RAM_004 Verify password visibility toggle functionality
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters password "Secret123!" in the Password field
    And User clicks the eye icon inside the Password field
    Then The Password field type should be "text"
    When User clicks the eye icon inside the Password field
    Then The Password field type should be "password"

  @UI @High @Critical @Security @BusinessLogic @Key:TC_RAM_005
  Scenario: TC_RAM_005 Verify duplicate email prevention
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "Dupe User", Email "mike@qdr.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then Submission should fail with a duplicate email error

  @UI @Medium @FieldValidation @Key:TC_RAM_006
  Scenario: TC_RAM_006 Validate Email field syntax checks
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "Valid Name", Email "plainaddress", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then An invalid email format error should display

  @UI @Medium @Accessibility @Key:TC_RAM_007
  Scenario: TC_RAM_007 Verify keyboard accessibility & focus order (a11y)
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User presses Tab repeatedly from modal launch
    Then Focus should move logically through Close, Full Name, Email, Password, Show Password toggle, Ranch Dropdown, Cancel, and Create Admin


  @UI @Low @StateTransition @Key:TC_RAM_009
  Scenario: TC_RAM_009 Verify modal dismiss via Backdrop, ESC key, and Cancel button
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User presses the ESC key
    Then The modal should close without saving data
    When User clicks the "+ Create New Admin" button for ranch admin
    And User clicks the "Cancel" button for ranch admin
    Then The modal should close without saving data
    When User clicks the "+ Create New Admin" button for ranch admin
    And User clicks the modal backdrop outside the modal
    Then The modal should close without saving data
    When User clicks the "+ Create New Admin" button for ranch admin
    Then The form fields should reset to empty on reopen

  # =========================================================================
  # 2. GRID SEARCH, FILTERING & LAYOUT
  # =========================================================================

  @UI @Medium @Functional @Key:TC_RAM_010
  Scenario: TC_RAM_010 Verify search filter responsiveness on main grid
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "SearchTarget", Email "searchtarget@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then The modal should close and a success toast "Admin created successfully!" should appear
    When User searches for the created admin in the search bar
    Then The grid should filter to 1 row(s)
    When User types "nonexistent_query_9999" in the ranch admin search bar
  Then The grid should display a "No admins found matching" empty state

  @UI @Low @Key:TC_RAM_011
  Scenario: TC_RAM_011 Verify pagination layout and record count adjustment
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User changes the results per page dropdown to "20"
    Then The results summary text should display "Showing 1 to 5 of 5 results"

  @UI @Medium @MobileResponsiveness @Key:TC_RAM_012
  Scenario: TC_RAM_012 Verify layout responsiveness across viewports
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    Given User sets the viewport size to 375 pixels by 812 pixels for ranch admin
    Then The sidebar should collapse into a hamburger menu for ranch admin
    When User clicks the "+ Create New Admin" button for ranch admin
    Then The modal should fit the screen without clipping

@UI @High @Medium @FieldValidation @Key:TC_RAM_013
  Scenario: TC_RAM_013 Verify mandatory field validation on form submission
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters email "yghcyu" in the Email field
    And User clicks the "Create Admin" button for ranch admin
    Then Inline errors "Name is required" and "Password is required" should display

  @UI @Medium @StateTransition @Key:TC_RAM_014
  Scenario: TC_RAM_014 Verify form state reset on modal reopen
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "Draft Admin", Email "draft.admin@ranch.com", Password "Pass123!", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Cancel" button for ranch admin
    When User clicks the "+ Create New Admin" button for ranch admin
    Then The modal should open clean with empty fields and zero validation errors

@UI @High @Medium @Functional @Key:TC_RAM_015
  Scenario: TC_RAM_015 Verify successful toast notification on record creation
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "Robert Sterling", Email "robert.sterling@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then The modal should close and a success toast "Admin created successfully!" should appear

  @UI @Medium @Low @EdgeCase @Key:TC_RAM_016
  Scenario: TC_RAM_016 Validate Full Name trim whitespace handling
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name " Alex Smith", Email "alex.smith@ranch.com", Password "Password@23", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then The grid should display the trimmed name "Alex Smith"

  @UI @Medium @Functional @Key:TC_RAM_017
  Scenario: TC_RAM_017 Verify Search field real-time filtering with partial strings
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User types "qdr" in the search bar
    Then The table should update dynamically without a full page reload

  # =========================================================================
  # 3. EDIT, DELETE & SECURITY CONSTRAINTS
  # =========================================================================

  @UI @High @Functional @Key:TC_RAM_018
  Scenario: TC_RAM_018 Validate Edit Ranch Admin modal populates existing data
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks "Edit" for an available admin row
    Then The edit modal should be pre-filled with the selected admin details

  @UI @High @Critical @Functional @Negative @Key:TC_RAM_019
  Scenario: TC_RAM_019 Verify Delete Ranch Admin confirmation modal and record removal
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User deletes an available record on the current page
    Then The delete confirmation modal should display
    When User confirms the deletion
    Then The modal should close and the record should be removed from the grid

  
  @UI @High @Critical @Security @Key:TC_RAM_021
  Scenario: TC_RAM_021 Validate SQL injection prevention in Search and Form inputs
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User enters SQL or regex payload "' OR '1'='1" in the search field
    Then The search should handle the input literally without server errors

  @UI @High @Critical @Security @BusinessLogic @Key:TC_RAM_022
  Scenario: TC_RAM_022 Validate duplicate email check is case-insensitive
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "Case Test", Email "MIKE@QDR.COM", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then Submission should fail with a duplicate email error

  @UI @High @EdgeCase @Performance @Key:TC_RAM_023
  Scenario: TC_RAM_023 Verify rapid double-click on form submission (Race Condition)
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "Race Condition", Email "race@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User rapidly double-clicks the Create Admin button
    Then The submit button should disable immediately with a loading state
    And Only one record for "Race Condition" should be created

  @UI @Medium @BoundaryValue @Key:TC_RAM_024
  Scenario: TC_RAM_024 Validate Full Name character limit Boundary Value Analysis (BVA)
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters a Full Name of 255 characters and submits
    Then The Full Name should be accepted or a max-length error should display
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters a Full Name of 256 characters and submits
    Then The Full Name should be accepted or a max-length error should display

  @UI @Medium @EdgeCase @Key:TC_RAM_025
  Scenario: TC_RAM_025 Validate Full Name support for Unicode / International characters
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User enters Unicode Full Name "José-María O'Connor"
    And User fills the admin form with Name "José-María O'Connor", Email "jose.maria@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    Then The grid should render the Unicode name "José-María O'Connor" correctly

   @UI @High @Critical @Security @RBAC @Key:TC_RAM_026
  Scenario: TC_RAM_026 Verify Ranch Admin only sees their assigned ranch details upon login
    When User logs in with Ranch Admin credentials for scenario "TC_RAM_026"
    Then The grid or dashboard should display only "Powderhorn Ranch"
    When User navigates to the "Ranches" module from the left navigation menu
    Then The grid or dashboard should display only "Powderhorn Ranch"
    And The ranch filter selector should be locked or contain only "Powderhorn Ranch"

  @UI @High @Security @Negative @Key:TC_RAM_027
  Scenario: TC_RAM_027 Verify Search input handling of SQL/Regex wildcard characters
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User enters SQL or regex payload "%_*[a-z]" in the search field
    Then The search should handle the input literally without server errors


  # =========================================================================
  # 4. PAGINATION, RE-INDEXING & VIEW MODES
  # =========================================================================

  @UI @Medium @Functional @Key:TC_RAM_031
  Scenario: TC_RAM_031 Verify toggle between Table View and Grid Card View
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the Card View toggle icon
    Then The view should display in Card layout
    When User clicks the Table View toggle icon
    Then The view should display in Table layout

  @UI @Low @Key:TC_RAM_032
  Scenario: TC_RAM_032 Verify Pagination page size dropdown selection
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User changes the results per page dropdown to "50" for ranch admin
    Then The results summary text should display "Showing 1 to" for ranch admin

  @Key:TC_RAM_033
  Scenario: TC_RAM_033 Verify successful update of Ranch Admin via Edit modal
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User fills the admin form with Name "EditTarget", Email "edittarget@ranch.com", Password "P@ssword123", and Ranch "Quattro Diamante Ranch"
    And User clicks the "Create Admin" button for ranch admin
    And User clicks "Edit" for admin row "EditTarget"
    And User changes Full Name to "Updated User"
    And User clicks the "Update Admin" button for ranch admin
    Then The Updating loader should display before the modal closes
    And The modal should close and a success toast "Ranch admin updated successfully!" should appear
    And The grid should display a new row containing "Updated User"

  @UI @Medium @FieldValidation @Key:TC_RAM_034
  Scenario: TC_RAM_034 Validate Edit modal mandatory field clear validation
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks "Edit" for an available admin row
    And User clears the Full Name field
    And User clicks the "Update Admin" button for ranch admin
    Then Form submission should be blocked with inline errors under all mandatory fields

  @UI @High @Critical @Functional @StateTransition @Key:TC_RAM_035
  Scenario: TC_RAM_035 Verify successful record deletion via confirmation modal
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User deletes an available record on the current page
    Then The delete confirmation modal should display
    When User confirms the deletion
    Then The modal should close and a success toast "Ranch admin deleted successfully!" should appear

  @UI @Medium @StateTransition @Key:TC_RAM_036
  Scenario: TC_RAM_036 Verify Edit modal cancellation without saving changes
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks "Edit" for an available admin row
    And User changes Full Name to "Temporary Name"
    And User clicks the "Cancel" button for ranch admin
    Then The original values for the selected row should remain unchanged in the grid

  @UI @High @Functional @Negative @Key:TC_RAM_037
  Scenario: TC_RAM_037 Verify Delete confirmation modal cancellation
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks "Delete" for an available admin row
    And User cancels the deletion
    Then The record should remain visible in the grid

  @UI @Medium @EdgeCase @Functional @Key:TC_RAM_038
  Scenario: TC_RAM_038 Validate table count re-indexing after single and sequential deletions
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User records the initial total result count
    And User deletes an available record on the current page
    And User confirms the deletion
    Then The results summary total count should decrease by 1
    When User deletes an available record on the current page
    And User confirms the deletion
    Then The results summary total count should decrease by 1

  @UI @Low @Usability @Key:TC_RAM_039
  Scenario: TC_RAM_039 Verify Toast message notification auto-dismissal
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks "Edit" for an available admin row
    And User changes Full Name to "Mike Updated"
    And User clicks the "Update Admin" button for ranch admin
    Then The toast notification should automatically fade out

  @UI @High @BusinessLogic @Security @Key:TC_RAM_040
  Scenario: TC_RAM_040 Validate Edit modal field modification restrictions (Email immutability check)
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks "Edit" for an available admin row
    Then The Email field editable state should be recorded for review

  # =========================================================================
  # 5. CROSS-CUTTING / NON-FUNCTIONAL SCENARIOS (a11y, Network, Mobile)
  # =========================================================================



  @UI @Medium @Generic @CrossBrowser @Key:GTC_RAM_COMP_001
  Scenario: GTC_RAM_COMP_001 Verify Ranch Admin module rendering consistency across major Web Browsers
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    Then Core Ranch Admin elements should render without visual bugs

 

  @UI @Medium @Generic @Accessibility @Key:GTC_RAM_ACC_001
  Scenario: GTC_RAM_ACC_001 Verify Keyboard Tab Focus Order and Focus Ring visibility inside Create/Edit modals
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User presses Tab repeatedly from modal launch
    Then Focus should move logically through Close, Full Name, Email, Password, Show Password toggle, Ranch Dropdown, Cancel, and Create Admin

  @UI @Medium @Generic @Accessibility @Key:GTC_RAM_ACC_002
  Scenario: GTC_RAM_ACC_002 Verify Screen Reader accessibility labels on action buttons and table controls
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    Then Action buttons should expose accessible aria-labels

  @UI @Low @Generic @FormValidation @Key:GTC_RAM_FV_005
  Scenario: GTC_RAM_FV_005 Verify copy-paste functionality into Full Name, Email, and Password input fields
  Given User logged  in as Super Admin
    When User navigates to the Ranch Admin module from the left navigation menu
    When User clicks the "+ Create New Admin" button for ranch admin
    And User pastes payload "Anny Teb" into the Full Name field
    Then The field should remain responsive and not freeze

    

  @UI @Medium @Generic @CrossPlatform @Mobile @Key:GTC_RAM_COMP_002
  Scenario Outline: GTC_RAM_COMP_002 Verify mobile browser compatibility on iOS Safari and Android Chrome
    Given User emulates mobile device "<DeviceName>" for ranch admin
    When User taps the hamburger menu and selects "Ranch Admins" for ranch admin
    And User clicks the "+ Create New Admin" button for ranch admin
    Then The modal should fit the screen without clipping
    And Touch action targets and form inputs should be fully interactive

    Examples:
      | DeviceName |
      | iPhone 13  |
      | Pixel 5    |