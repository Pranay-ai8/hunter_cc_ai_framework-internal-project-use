@StaffsGuides @regression
Feature: Staff & Guides Module
  As an admin user
  I want to navigate and interact with the Staff & Guides module
  So that I can view, search, filter, and manage staff and guides

  @Key:SGC001 @High @Type:Navigation
  Scenario: Verify navigation to Staff & Guides module
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    Then User is navigated to the Staff & Guides page with title, subtitle, and Add Guide button

  @Key:SGC002 @Low @Type:UI
  Scenario: Verify default view and UI elements on Staff & Guides page
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    Then Top section shows Search bar, Ranches dropdown, Status dropdown, Levels dropdown, List and Grid view toggles
    And Default view on Staff & Guides page is 'List View' highlighted green

  @Key:SGC003 @Low @Type:UI
  Scenario: Verify List View column headers on Staff & Guides page
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    Then Table headers display Guide, Ranch, Experience, Active Bookings, Total Bookings, Status, Actions

  @Key:SGC004 @Medium @Type:Functional
  Scenario: Verify Search functionality by Guide Name
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Table filters dynamically to display guide records matching the entered name

  @Key:SGC005 @Medium @Type:Functional
  Scenario: Verify Search functionality by Email Address
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Table filters dynamically to display the specific guide associated with that email address

  @Key:SGC006 @Medium @Type:Functional
  Scenario: Verify Search functionality by Phone Number
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Table filters dynamically to display guide matching the phone number query

  @Key:SGC007 @Medium @Type:Functional
  Scenario: Verify Search with non-existent keyword
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Table displays an empty state message 'No guides found'

  @Key:SGC008 @Medium @Type:Functional
  Scenario: Verify clearing Search bar restores full list
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    And User clears the staff search bar
    Then Search filter is removed and full list of staff & guides is restored

  @Key:SGC009 @Medium @Type:Functional
  Scenario: Verify Ranch filter selection: 'All Ranches' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<ranchFilter>' from Ranches dropdown
    Then Table displays guides assigned across all ranches

  @Key:SGC010 @Medium @Type:Functional
  Scenario: Verify Ranch filter selection: 'Koramnaga Ranch'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<ranchFilter>' from Ranches dropdown
    Then Table updates to display ONLY guides assigned to Koramnaga Ranch

  @Key:SGC011 @Medium @Type:Functional
  Scenario: Verify Ranch filter selection: 'Test Ranch'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<ranchFilter>' from Ranches dropdown
    Then Table updates to display ONLY guides assigned to Test Ranch

  @Key:SGC012 @Medium @Type:Functional
  Scenario: Verify Status filter selection: 'All Status' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Staff Status dropdown
    Then Table displays all guides regardless of status

  @Key:SGC013 @Medium @Type:Functional
  Scenario: Verify Status filter selection: 'Active'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Staff Status dropdown
    Then Table updates to display ONLY guides with green Active status badges

  @Key:SGC014 @Medium @Type:Functional
  Scenario: Verify Status filter selection: 'Inactive'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<statusFilter>' from Staff Status dropdown
    Then Table updates to display ONLY inactive guides or empty state

  @Key:SGC015 @Medium @Type:Functional
  Scenario: Verify Level filter selection: 'All Levels' (Default)
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<levelFilter>' from Levels dropdown
    Then Table displays guides across all experience levels

  @Key:SGC016 @Medium @Type:Functional
  Scenario: Verify Level filter selection: 'Beginner (0-2 years)'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<levelFilter>' from Levels dropdown
    Then Table updates to display ONLY beginner guides with green Beginner badges

  @Key:SGC017 @Medium @Type:Functional
  Scenario: Verify Level filter selection: 'Experienced (3-7 years)'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<levelFilter>' from Levels dropdown
    Then Table updates to display ONLY experienced guides with blue Experienced badges

  @Key:SGC018 @Medium @Type:Functional
  Scenario: Verify Level filter selection: 'Expert (8+ years)'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User selects '<levelFilter>' from Levels dropdown
    Then Table updates to display ONLY expert guides with gold Expert badges

  @Key:SGC019 @Medium @Type:UI
  Scenario: Verify switching layout from List View to Grid View
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks Grid View icon on Staff & Guides page
    Then Layout switches from table rows to individual guide cards

  @Key:SGC020 @Medium @Type:UI
  Scenario: Verify switching layout from Grid View back to List View
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks Grid View icon on Staff & Guides page
    And User clicks List View icon on Staff & Guides page
    Then Layout switches back to structured table format

  @Key:SGC021 @Medium @Type:UI
  Scenario: Verify per-page pagination dropdown options
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on per-page pagination dropdown
    Then Dropdown options 10, 20, 50 are displayed
    And Selecting 20 updates per-page pagination count text

  @Key:SGC022 @High @Type:Navigation
  Scenario: Verify navigating to Detailed Guide View by clicking guide row
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on a guide row in the table
    Then User is navigated to Guide Details page displaying header, badges, and quick actions

  @Key:SGC023 @Medium @Type:Navigation
  Scenario: Verify '< Back to Staff' link on Guide Details page
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on a guide row in the table
    And User clicks '< Back to Staff' link
    Then User is navigated to the Staff & Guides page with title, subtitle, and Add Guide button

  @Key:SGC024 @High @Type:Functional
  Scenario: Verify 'Edit Profile' button functionality on Guide Details page
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on a guide row in the table
    And User clicks 'Edit Profile' button under Quick Actions panel
    And User advances to Step 2 and updates Years of Guiding Experience
    Then Guide profile is updated successfully with success alert
    And User reverts the Years of Guiding Experience back to original value

  @Key:SGC025 @High @Type:Functional
  Scenario: Verify Actions menu options in List View: 'Edit'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    Then User is navigated to 'Edit Guide' 4-step wizard page at Step 1

  @Key:SGC026 @Medium @Type:UI
  Scenario: Verify Actions menu options in List View: 'Change Status'
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'Clay Sultemeier' and clicks Actions menu
    And User clicks 'Change Status' from Actions menu
    Then 'Change status' modal opens with 'New status' dropdown, Cancel, and Save buttons

  @Key:SGC027 @High @Type:Functional
  Scenario: Verify changing Guide status to 'Inactive' via Change Status modal
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row with 'Active' status and clicks Actions menu
    And User clicks 'Change Status' from Actions menu
    And User selects 'Inactive' in Change Status modal and clicks Save
    Then Guide status updates to 'Inactive' in table with success alert

  @Key:SGC028 @High @Type:Functional
  Scenario: Verify changing Guide status back to 'Active' via Change Status modal
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row with 'Inactive' status and clicks Actions menu
    And User clicks 'Change Status' from Actions menu
    And User selects 'Active' in Change Status modal and clicks Save
    Then Guide status updates to 'Active' in table with success alert

  @Key:SGC029 @Medium @Type:Functional
  Scenario: Verify 'Cancel' button behavior in Change Status modal
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Change Status' from Actions menu
    And User clicks 'Cancel' button in Change Status modal
    Then Change Status modal closes and guide status remains unchanged

  @Key:SGC030 @High @Type:Functional
  Scenario: Verify Step 1 (Personal Information) fields pre-population in Edit Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    Then Step 1 fields First Name, Last Name, Email Address, and Phone Number are pre-filled

  @Key:SGC031 @High @Type:Validation
  Scenario: Verify mandatory fields validation on Step 1 of Edit Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clears mandatory fields in Step 1
    And User clicks 'Next' button in wizard
    Then Inline error messages appear and wizard remains on Step 1

  @Key:SGC032 @Medium @Type:Validation
  Scenario: Verify Email field format validation on Step 1 of Edit Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User enters invalid email from testData into Email field
    And User clicks 'Next' button in wizard
    Then Inline error message 'Invalid email' appears and prevents progressing

  @Key:SGC033 @Medium @Type:UI
  Scenario: Verify Phone Number international country code picker on Step 1
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks flag dropdown icon in Phone Number input
    Then Country selector dropdown opens displaying country codes

  @Key:SGC034 @High @Type:Functional
  Scenario: Verify transition from Step 1 to Step 2 in Edit Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    Then Wizard advances to Step 2 of 4 'Employment Details'

  @Key:SGC035 @High @Type:Functional
  Scenario: Verify Employment Details fields and auto-calculated Experience Level on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User changes Years of Guiding Experience to '5'
    Then Auto-calculated Experience Level badge reflects entered experience

  @Key:SGC036 @High @Type:Functional
  Scenario: Verify transition from Step 2 to Step 3 in Edit Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    Then Wizard advances to Step 3 of 4 'Account Setup'

  @Key:SGC037 @Low @Type:UI
  Scenario: Verify Account Setup read-only permissions display on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    Then Step 3 displays guide login email and read-only Access Permissions

  @Key:SGC038 @High @Type:Functional
  Scenario: Verify transition from Step 3 to Step 4 in Edit Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    Then Wizard advances to Step 4 of 4 'Review & Save'

  @Key:SGC039 @High @Type:Functional
  Scenario: Verify Review & Save summary accuracy on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    Then Step 4 displays summary card consolidating guide information

  @Key:SGC040 @High @Type:Functional
  Scenario: Verify final 'Update Guide' button submission on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Update Guide' button in wizard
    Then User is redirected back to Staff & Guides list page with success alert

  @Key:SGC041 @Medium @Type:Navigation
  Scenario: Verify '< Back' button navigation across Edit Guide wizard steps
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks 'Next' button in wizard
    And User clicks 'Back' button in wizard
    Then Wizard returns to previous step

  @Key:SGC042 @Medium @Type:Navigation
  Scenario: Verify '< Back to Guides' link exits Edit Guide wizard without saving
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User locates guide row 'athul pp' and clicks Actions menu
    And User clicks 'Edit' from Actions menu
    And User clicks 'Next' button in wizard
    And User clicks '< Back to Guides' link
    Then User is returned to main Staff & Guides list view page



  @Key:SGC046 @High @Type:Navigation
  Scenario: Verify navigation to 'Add New Guide' wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    Then User is navigated to 'Add New Guide' wizard page starting at Step 1 of 5 'Ranch Selection' initialized at 20% Complete

  @Key:SGC047 @Medium @Type:Functional
  Scenario: Verify Ranch Selection dropdown on Step 1 of Add Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User clicks 'Select Ranch (Super Admin Only)' dropdown
    And User selects '<ranchName>' from the ranch dropdown
    Then Selected ranch displays in field with subtext 'This guide will be assigned to the selected ranch'

  @Key:SGC048 @High @Type:Functional
  Scenario: Verify transition from Step 1 to Step 2 in Add Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    Then Wizard advances to Step 2 of 5 'Personal Info' at 40% Complete and Stepper bar shows Step 1 completed

  @Key:SGC049 @High @Type:Validation
  Scenario: Verify First Name and Last Name mandatory fields validation on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    And User leaves First Name and Last Name blank on Step 2
    And User clicks green 'Next >' button in wizard
    Then Red inline error messages appear below mandatory Name fields preventing progression to Step 3

  @Key:SGC050 @Medium @Type:Validation
  Scenario: Verify Email field format validation on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    And User enters First Name and Last Name on Step 2
    And User types invalid email string into 'Email Address' field
    Then Red inline error message 'Enter a valid email address' appears under Email field

  @Key:SGC051 @Medium @Type:UI
  Scenario: Verify Phone Number country picker and input on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    And User selects country flag on Step 2
    And User enters valid phone number digits
    Then Phone input accepts country code and formats phone number correctly

  @Key:SGC052 @High @Type:Validation
  Scenario: Verify Guide age validation (Must be at least 18 years old) on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    And User clicks 'Date of Birth' picker on Step 2
    And User selects a Date of Birth making age under 18
    Then Red inline error message appears 'Guide must be at least 18 years old' and 'Next >' button is blocked

  @Key:SGC053 @Medium @Type:Validation
  Scenario: Verify selecting a valid Adult Date of Birth on Step 2
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    And User clicks 'Date of Birth' picker on Step 2
    And User selects a valid Adult Date of Birth
    Then Age validation passes and 'Guide must be at least 18 years old' error disappears instantly

  @Key:SGC054 @High @Type:Functional
  Scenario: Verify transition from Step 2 to Step 3 in Add Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User selects '<ranchName>' from the ranch dropdown
    And User clicks green 'Next >' button in wizard
    And User completes valid First Name, Last Name, Email, Phone, and Adult DOB on Step 2
    And User clicks green 'Next >' button in wizard
    Then Wizard advances to Step 3 of 5 'Employment' at 60% Complete and Stepper updates to show Step 2 completed

  @Key:SGC055 @Medium @Type:Functional
  Scenario: Verify Years of Guiding Experience input and auto-calculated Experience Level badge on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 3 of Add Guide wizard
    And User observes default 'Years of Guiding Experience'
    And User types '10' into experience input
    Then Badge updates to 'Expert (8+)'
    And User types '2' into experience input
    Then Badge updates to 'Beginner (0-2)'

  @Key:SGC056 @Medium @Type:UI
  Scenario: Verify Start Date picker selection on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 3 of Add Guide wizard
    And User clicks 'Start Date' picker field on Step 3
    And User selects start date from calendar
    Then Calendar modal opens and chosen date formats properly in Start Date field

  @Key:SGC057 @Medium @Type:Functional
  Scenario: Verify Employment Type radio options selection on Step 3
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 3 of Add Guide wizard
    And User clicks 'Full-time' radio button on Step 3
    And User clicks 'Part-time' radio button
    And User clicks 'Contract' radio button
    Then Radio button selections switch smoothly highlighting chosen employment model

  @Key:SGC058 @High @Type:Functional
  Scenario: Verify transition from Step 3 to Step 4 in Add Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 3 of Add Guide wizard
    And User completes Start Date, Employment Type, and Experience Years on Step 3
    And User clicks green 'Next >' button in wizard
    Then Wizard advances to Step 4 of 5 'Account Setup' at 80% Complete and Stepper updates

  @Key:SGC059 @Low @Type:UI
  Scenario: Verify read-only Login Credentials and Guide Permissions on Step 4
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 4 of Add Guide wizard
    Then Email displays pre-filled read-only value and bullet list displays permissions

  @Key:SGC060 @High @Type:Functional
  Scenario: Verify transition from Step 4 to Step 5 in Add Guide wizard
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 4 of Add Guide wizard
    And User clicks green 'Next >' button in wizard
    Then Wizard advances to Step 5 of 5 'Review & Create' at 100% Complete

  @Key:SGC061 @Medium @Type:Functional
  Scenario: Verify Review & Create summary content accuracy on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 5 of Add Guide wizard
    And User inspects 'Review Guide Information' summary card on Step 5
    Then Summary screen accurately consolidates all guide details entered across steps 1 through 4

  @Key:SGC062 @High @Type:Functional
  Scenario: Verify final 'Create Guide' button submission on Step 5
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 5 of Add Guide wizard
    And User clicks green 'Create Guide' button at bottom right
    Then Guide profile is created successfully and user is redirected to Staff & Guides list

  @Key:SGC063 @Medium @Type:Navigation
  Scenario: Verify '< Back' button backward navigation across wizard steps
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 5 of Add Guide wizard
    And User clicks '< Back' button in wizard
    And User clicks '< Back' button in wizard
    And User clicks '< Back' button in wizard
    And User clicks '< Back' button in wizard
    Then Wizard navigates backward one step at a time maintaining all previously entered form data

  @Key:SGC064 @Medium @Type:Navigation
  Scenario: Verify '< Back to Guides' link exits wizard without saving
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User starts Add Guide wizard
    And User clicks '< Back to Guides' link at top left
    Then Wizard exits, progress is discarded, and user returns to main 'Staff & Guides' list view page

  @Key:SGC065 @Medium @Type:Functional
  Scenario: Verify 'Create Guide' button loading state during submission
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 5 of Add Guide wizard
    And User clicks green 'Create Guide' button at bottom right
    Then Button visually switches to disabled or loading state to prevent duplicate submissions

  @Key:SGC071 @Medium @Type:Navigation
  Scenario: Verify breadcrumb trail and back navigation on Guide Details page
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on a guide row in the table
    And User clicks '< Back to Staff' link
    Then Breadcrumb displays parent navigation path, and clicking it redirects back to the main 'Staff & Guides' table list page

  @Key:SGC072 @Medium @Type:Workflow
  Scenario: Verify browser Back button behavior after guide creation
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User navigates to Step 5 of Add Guide wizard
    And User clicks green 'Create Guide' button at bottom right
    And Guide profile is created successfully and user is redirected to Staff & Guides list
    And User clicks browser 'Back' button
    Then System prevents resubmitting form data or re-entering completed wizard state, retaining user safely on list view page

  @Key:SGC073 @Medium @Type:Functional
  Scenario: Verify pagination controls and per-page records rendering
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User scrolls to bottom of Staff & Guides table
    And User changes per-page dropdown from '10' to '20' or '50'
    And User clicks page numbers or Next page controls
    Then Table updates row capacity dynamically, pagination summary text recalculates accurately, and page navigation controls work smoothly

  @Key:SGC074 @Medium @Type:Functional
  Scenario: Verify search functionality ignores letter case sensitivity
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Search filter returns matching guide records regardless of character casing

  @Key:SGC075 @Medium @Type:Validation
  Scenario: Verify search filtering with special characters handles input safely
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User types search string from testData in the staff search bar
    Then Table updates to show empty state without throwing JavaScript errors or page crashes

  @Key:SGC076 @Medium @Type:Functional
  Scenario: Verify auto-suggestions / autocomplete dropdown on Ranch selection
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User clicks 'Select Ranch (Super Admin Only)' dropdown
    And User types partial ranch name from testData in ranch dropdown
    Then Dropdown dynamically filters to show matching available ranches

  @Key:SGC077 @Low @Type:Performance
  Scenario: Verify table scrolling performance and responsiveness
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User scrolls vertically through table of guide records
    Then Table renders and scrolls smoothly without visual lag, clipping, or layout shift

  @Key:SGC078 @Medium @Type:Workflow
  Scenario: Verify browser Back and Forward navigation retains active pagination state
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User navigates to Page 2 of Staff & Guides list
    And User clicks on a guide row in the table
    And User clicks browser 'Back' button
    Then List page reloads and retains focus on Page 2 records instead of resetting back to Page 1

  @Key:SGC079 @Medium @Type:Usability
  Scenario: Verify rapid multiple clicks on 'Next' button on wizard steps
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User clicks 'Next >' button on Step 1 multiple times rapidly
    Then Wizard advances to Step 2 smoothly without jumping multiple steps ahead or corrupting step state

  @Key:SGC080 @Low @Type:UI
  Scenario: Verify active sidebar menu state remains highlighted upon page refresh
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User presses F5 or Refresh browser
    Then Page reloads and 'Staff & Guides' sidebar link remains highlighted green

  @Key:SGC043 @Medium @Type:Accessibility
  Scenario: Verify keyboard Tab key accessibility across Staff & Guides page controls
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User presses Tab key repeatedly across staff table controls
    Then Keyboard focus moves sequentially across search, filter dropdowns, and table controls

  @Key:SGC044 @Medium @Type:Usability
  Scenario: Verify Cross-Browser Compatibility for Staff & Guides module
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    Then Staff & Guides page UI elements and filter dropdowns render properly

  @Key:SGC067 @Medium @Type:UI
  Scenario: Verify Stepper progress bar visual states across all 5 steps
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    Then Active step circle is highlighted green and percentage indicator updates accurately

  @Key:SGC068 @Medium @Type:Accessibility
  Scenario: Verify keyboard Tab key accessibility across Add Guide wizard forms
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User proceeds to Step 2 in wizard
    And User presses Tab key repeatedly across Step 2 wizard fields
    Then Focus moves sequentially across First Name, Last Name, Email, Phone, and DOB inputs

  @Key:SGC069 @Medium @Type:Validation
  Scenario: Verify leading and trailing spaces in text fields are trimmed on Step 5 Review
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    And User clicks on the '+ Add Guide' button
    And User enters First Name and Last Name with leading and trailing spaces ' Himanshu ' and ' Shetty '
    And User completes steps 3 and 4 to proceed to Step 5
    Then Step 5 Review screen displays clean trimmed full name 'Himanshu Shetty'

  @Key:SGC070 @Low @Type:UI
  Scenario: Verify sidebar menu links visibility and active highlight
    Given User is logged in as Super Admin
    When User clicks on 'Staff & Guides' under GUEST OPERATIONS in the left sidebar
    Then Left sidebar menu renders correctly and 'Staff & Guides' link is highlighted green

  @Key:SGC081 @High @Type:Functional @GuideLogin @login
  Scenario: Verify guide user login and access to assigned bookings and harvest logs in Guide Portal
    Given User navigates to the login page
    When User enters valid email "<email>" and password "<password>"
    And User clicks on the Sign In button
    Then User is redirected to the Guide Portal with "Test Ranch" header and "Guide Portal" subtitle
    And Left navigation menu displays only Guide Portal links "Dashboard", "Bookings", and "Harvest Logs"
    When User clicks on "Bookings" in the left navigation menu
    Then The Booking Management page displays all assigned bookings under the guide
    When User clicks on "Harvest Logs" in the left navigation menu
    Then The Harvest Logs page displays assigned harvest records and tag allocations

