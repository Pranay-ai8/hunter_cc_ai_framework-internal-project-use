@Ranches @regression
Feature: Ranches Module
  @high @navigation @Key:RTC001
  Scenario: Verify navigation to Ranches module
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then User is navigated to the Ranches page
    And The Ranches sidebar item is highlighted
    And The page title Ranch Management is visible

  @low @ui @Key:RTC002
  Scenario: Verify default view of the Ranches module
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then The default view is 'List View'
    And The list view icon is highlighted green

  @low @ui @Key:RTC003
  Scenario: Verify UI elements on the Ranches page
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then The top section UI elements are visible

  @medium @ui @Key:RTC004
  Scenario: Verify List View column headers
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then Table headers accurately display all expected columns

  @high @functional @Key:RTC005
  Scenario: Verify Ranch data is populated in List View
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then Table rows display correct data mapping for King Ranch and Quattro Diamante Ranch

  @medium @ui @Key:RTC006
  Scenario: Verify toggling to Grid View
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks the Grid View icon
    Then The layout switches from a table to individual cards
    And The grid view icon is highlighted green

  @medium @ui @Key:RTC007
  Scenario: Verify toggling back to List View
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks the Grid View icon
    And User clicks the List View icon
    Then The default view is 'List View'
    And The list view icon is highlighted green

  @medium @ui @Key:RTC008
  Scenario: Verify Grid View card UI elements
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks the Grid View icon
    Then Each card displays Ranch Name, Location, Rooms, Capacity, Created Date, Admin name, and an Edit button

  @medium @functional @Key:RTC009
  Scenario: Verify Search functionality with exact Ranch Name
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "koramnaga ranch" in the search bar
    Then Table filters to show ONLY "koramnaga ranch"

  @medium @functional @Key:RTC010
  Scenario: Verify Search functionality with partial Ranch Name
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "koramnaga" in the search bar
    Then Table filters to show "koramnaga ranch"

  @medium @functional @Key:RTC011
  Scenario: Verify Search functionality is case-insensitive
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "koramnaga ranch" in the search bar
    Then Table filters to show "koramnaga ranch"

  @medium @functional @Key:RTC012
  Scenario: Verify Search functionality by Location
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "bangalore" in the search bar
    Then Table filters to show "koramnaga ranch"

  @medium @functional @Key:RTC013
  Scenario: Verify Search with non-existent data
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "xyz123abc" in the search bar
    Then Table displays an empty state message

  @medium @functional @Key:RTC014
  Scenario: Verify clearing the Search bar restores the full list
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User types "King" in the search bar
    And User clears the search bar
    Then Table restores to show all available ranches

  @low @ui @Key:RTC015
  Scenario: Verify default pagination text and values
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then Pagination text reads "Showing 1 to"
    And The default dropdown shows "10"

  @medium @functional @Key:RTC016
  Scenario: Verify changing pagination to 20 items per page
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks the pagination dropdown and selects "20"
    Then The page updates to display up to 20 rows

  @high @functional @Key:RTC017
  Scenario: Verify Edit button functionality in List View
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on the "koramnaga ranch" row
    Then User is navigated to the Edit Ranch page/modal

  @medium @functional @Key:RTC018
  Scenario: Verify Edit button functionality in Grid View
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks the Grid View icon
    And User clicks 'Edit' on the "koramnaga ranch" card
    Then User is navigated to the Edit Ranch page/modal

  @high @functional @Key:RTC019
  Scenario: Verify opening Detailed View by clicking a Ranch row
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks on any Ranch row (e.g., "koramnaga ranch")
    Then User is navigated to the read-only Ranch Details page displaying complete metadata and an 'Edit Ranch' button

  @high @navigation @Key:RTC020
  Scenario: Verify navigation to Edit Ranch from Detailed View
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks on any Ranch row (e.g., "koramnaga ranch")
    And User clicks the green 'Edit Ranch' button
    Then User is navigated to the Edit Ranch page/modal

  @medium @ui @Key:RTC021
  Scenario: Verify responsiveness of List View on Tablet/Mobile
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User shrinks browser width to tablet/mobile size
    Then Table gains a horizontal scrollbar or stacks into cards natively to prevent UI breakage

  @high @navigation @Key:RTC022
  Scenario: Verify navigation to Create New Ranch wizard
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    Then User is navigated to 'Create New Ranch' wizard page initialized at 'Step 1 of 5' (20% Complete)

  @medium @ui @Key:RTC023
  Scenario: Verify Country code picker dropdown in Contact Phone on Step 1
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User clicks flag icon dropdown in Contact Phone input field
    Then Country picker dropdown opens displaying list of countries with flag icons and calling codes

  @medium @validation @Key:RTC024
  Scenario: Verify phone number validation on invalid country code or digit length
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills all the forms
    And User selects US (+1) country code
    And User enters invalid digit string from testData and clicks 'Next >'
    Then Red inline error message "Enter a valid phone number" appears and prevents progressing

  @high @functional @Key:RTC025
  Scenario: Verify successful completion of Step 1 (Basic Information)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    Then Step 1 completes successfully, stepper updates to Step 2 (40% Complete), and "Ranch Details" section is loaded

  @medium @ui @Key:RTC026
  Scenario: Verify 'Add Animal Type' modal popup under Huntable Animals on Step 2
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User clicks '+ Add Animal' button
    Then 'Add Animal Type' modal opens displaying required fields

  @medium @functional @Key:RTC027
  Scenario: Verify Species dropdown dynamic filter based on Animal Category
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User clicks '+ Add Animal' button
    And User changes Animal Category selection to category from testData
    Then Species dropdown dynamically updates available species based on selected Animal Category

  @high @functional @Key:RTC028
  Scenario: Verify adding a Huntable Animal via modal on Step 2
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User clicks '+ Add Animal' button
    And User inputs valid animal data from testData and clicks 'Add Animal'
    Then Modal closes and newly configured animal card appears in Huntable Animals container

  @high @functional @Key:RTC029
  Scenario: Verify 'Add Stand' modal popup and functional addition on Step 2
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User clicks '+ Add Stand' button
    And User inputs valid stand data from testData and clicks 'Add Stand'
    Then Modal closes and newly configured stand card appears in Stands container

  @high @functional @Key:RTC030
  Scenario: Verify 'Add Add-on' modal popup and functional addition on Step 2
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User clicks '+ Add Add-on' button
    And User inputs valid add-on data from testData and clicks 'Add Add-on'
    Then Modal closes and newly configured add-on card appears in Add-ons container

  @low @ui @Key:RTC031
  Scenario: Verify Empty State UI on Step 3 (Rooms)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User completes Step 2 required fields and clicks 'Next >'
    Then Empty state card is displayed showing "No rooms added yet" with an Add First Room button

  @high @functional @Key:RTC032
  Scenario: Verify adding and editing a room on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User completes Step 2 required fields and clicks 'Next >'
    And User clicks 'Add Room' button
    And User inputs valid room data from testData
    Then A new room row is created in table format

  @high @functional @Key:RTC033
  Scenario: Verify configuring preferences on Step 4 (Initial Settings)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User completes Step 2 required fields and clicks 'Next >'
    And User clicks 'Add Room' button
    And User inputs valid room data from testData
    And User clicks 'Next >'
    And User configures default preferences from testData
    And User clicks 'Next >'
    Then Preferences are configured successfully and user navigates to Step 5 (Review & Create)

  @high @functional @Key:RTC034
  Scenario: Verify Review & Create summary content accuracy on Step 5
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User completes Step 2 required fields and clicks 'Next >'
    And User clicks 'Add Room' button
    And User inputs valid room data from testData
    And User clicks 'Next >'
    And User configures default preferences from testData
    And User clicks 'Next >'
    Then Review & Create summary section is visible

  @high @functional @Key:RTC035
  Scenario: Verify final 'Create Ranch' button submission
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User fills valid Basic Information from testData
    And User clicks 'Next >'
    And User completes Step 2 required fields and clicks 'Next >'
    And User clicks 'Add Room' button
    And User inputs valid room data from testData
    And User clicks 'Next >'
    And User configures default preferences from testData
    And User clicks 'Next >'
    And User clicks 'Create Ranch' button
    Then Ranch is saved and user is redirected back to Ranches list
    And A toast notification "Ranch created successfully!" appears


  @medium @ui @Key:RTC036
  Scenario: Verify Ranch Details read-only page layout and element visibility
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks on a ranch row to open details view
    Then Details view displays Basic Information and Ranch Details sections with formatted metadata

  @high @functional @Key:RTC037
  Scenario: Verify Step 1 fields pre-population with existing ranch data
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    Then Step 1 fields Ranch Name, Full Address, Contact Phone, and Contact Email are pre-filled with existing data

  @high @validation @Key:RTC038
  Scenario: Verify mandatory fields validation on Step 1 (Basic Information)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clears mandatory fields and clicks 'Next >'
    Then Validation error messages appear under empty fields and navigation is blocked

  @medium @validation @Key:RTC039
  Scenario: Verify Contact Email field format validation on Step 1
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User enters invalid email formats from testData and clicks 'Next >'
    Then Field shows inline error and form submission is prevented

  @medium @boundary @Key:RTC040
  Scenario: Verify Property Size (acres) numeric boundary validation
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User enters negative number or string in Property Size from testData
    Then Negative numbers and non-numerics trigger validation error

  @high @functional @Key:RTC041
  Scenario: Verify deleting an animal from Huntable Animals list
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks the trash icon on an animal card
    Then The specified animal card is removed immediately from the container

  @medium @validation @Key:RTC042
  Scenario: Verify Price ($) numeric validation for Huntable Animals
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User enters negative price in Price field from testData
    Then Field highlights error and prevents progressing with negative price

  @medium @navigation @Key:RTC043
  Scenario: Verify backward navigation through wizard steps using Back
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks '< Back' sequentially until Step 1
    Then Navigation moves back one step at a time maintaining all edited data intact

  @medium @boundary @Key:RTC044
  Scenario: Verify Cost per Night ($) numeric input and boundary on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User edits Cost field with negative values from testData
    Then Negative values are rejected with error for cost field

  @high @functional @Key:RTC045
  Scenario: Verify adding a new room row via '+ Add Room' button on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks '+ Add Room' button and fills details from testData
    Then A new blank row is added, fields accept input, and totals update correctly

  @high @functional @Key:RTC046
  Scenario: Verify deleting a room row via 'X' delete action on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'X' delete button in Actions column for a room
    Then Room row is deleted from the table layout and totals are recalculated

  @medium @functional @Key:RTC047
  Scenario: Verify Initial Settings dropdown selections on Step 4
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User selects options in Initial Settings dropdowns from testData
    Then All dropdown selections accept user choice and display chosen option correctly

  @medium @validation @Key:RTC048
  Scenario: Verify waiver days numeric validity on Step 4
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User enters invalid numeric values in Waiver fields from testData
    Then Invalid days trigger field error message requiring positive integer input

  @high @functional @Key:RTC049
  Scenario: Verify Review & Save summary accuracy on Step 5
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    Then Summary reflects all modifications made accurately without truncation

  @high @functional @Key:RTC050
  Scenario: Verify successful submission and toast message on Save Changes
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Next >'
    And User clicks 'Save Changes' button
    Then Application saves changes, redirects user, and displays success toast alert

  @medium @ui @Key:RTC051
  Scenario: Verify responsive layout of 5-step wizard on Tablet and Mobile viewports
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User resizes browser window to mobile width
    Then Progress stepper stacks or scrolls horizontally and forms remain accessible

  @high @validation @Key:RTC052
  Scenario: Verify duplicate Email validation on creation
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User enters an existing email 'test@ranch.com' on Step 1
    And User completes remaining steps to Step 5
    And User clicks 'Create Ranch' button
    Then Creation fails and error message indicates email already exists

#  @high @validation @bug @Key:RTC054
#  Scenario: Verify currency formatting validation (Max 2 decimals)
#    Given User is logged in as Super Admin
#    When User clicks on the Ranches module in the left sidebar
#    And User clicks '+ Create New Ranch' button
#    And User fills valid Basic Information on Step 1 and proceeds to Step 2
#    And User opens Add Animal modal and enters price '150.999' with 3 decimals
#    Then Price field automatically rounds to 2 decimal places or enforces max 2 decimals

  @high @validation @Key:RTC055
  Scenario: Verify wizard blocks progression if mandatory minimums aren't met
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User completes Step 1 and Step 2 and proceeds to Step 3
    And User does not add any rooms and clicks 'Next >'
    Then System blocks progression to Step 4 and displays room required error message

  @high @navigation @Key:RTC056
  Scenario: Verify navigation to Edit Ranch wizard step 1 from Details view
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User navigates to Ranch Details page
    And User clicks green 'Edit Ranch' button
    Then User is navigated to the 'Edit Ranch' wizard page initialized at 'Step 1 of 5'

  @high @critical @functional @Key:RTC058
  Scenario: Verify Step 1 fields pre-population with existing ranch data
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on 'koramnaga ranch'
    Then Step 1 fields are pre-populated with existing valid data

  @high @validation @Key:RTC059
  Scenario: Verify mandatory fields validation on Step 1 (Basic Information)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User clears mandatory fields on Step 1
    And User clicks 'Next >'
    Then Validation error messages appear under empty mandatory fields and Step 2 navigation is blocked

  @medium @validation @Key:RTC060
  Scenario: Verify Contact Email field format validation on Step 1
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User enters invalid email format 'king@gmail' into Contact Email field
    And User clicks 'Next >'
    Then Field shows inline error "Please enter a valid email address" and progression is prevented

  @medium @validation @Key:RTC061
  Scenario: Verify Contact Phone field international formatting and validation
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User enters non-numeric phone 'abc123'
    Then Phone input rejects non-numeric characters and enforces valid formatting

  @high @functional @Key:RTC062
  Scenario: Verify transition from Step 1 to Step 2 via 'Next >' button
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User ensures Step 1 has valid data and clicks 'Next >'
    Then Stepper updates to Step 2 of 5 and active step changes to Ranch Details

  @medium @ui @Key:RTC063
  Scenario: Verify UI elements and pre-populated data on Step 2 (Ranch Details)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 2
    Then Step 2 displays property size, description, huntable animals, stands, and add-ons

  @medium @boundary @Key:RTC064
  Scenario: Verify Property Size (acres) numeric boundary validation
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 2
    And User enters invalid property size '-50'
    Then System flags property size error and prevents negative value submission

  @high @functional @Key:RTC066
  Scenario: Verify deleting an animal from Huntable Animals list
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 2
    And User deletes an existing animal card from huntable animals
    Then The animal card is removed from the container

  @medium @navigation @Key:RTC068
  Scenario: Verify '< Back' button functionality on Step 2
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 2
    And User clicks '< Back' button
    Then Navigation returns to Step 1 without losing entered data

  @high @functional @Key:RTC069
  Scenario: Verify transition from Step 2 to Step 3 (Rooms)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User completes Step 1 and Step 2 and clicks 'Next >'
    Then Stepper updates to Step 3 of 5 and Rooms configuration opens

  @high @ui @Key:RTC070
  Scenario: Verify Rooms table UI and pre-populated rows on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on 'koramnaga ranch'
    And User proceeds to Step 3
    Then Rooms table displays Room Name, Beds, Cost per Night, and existing room records

  @high @boundary @Key:RTC072
  Scenario: Verify Cost per Night ($) numeric input and boundary on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 3
    And User enters negative cost '-50' for a room
    Then Cost per night rejects negative value and displays validation error

  @high @functional @Key:RTC073
  Scenario: Verify adding a new room row via '+ Add Room' button on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 3
    And User clicks '+ Add Room' button
    And User enters room name 'Suite 101', beds 2, and cost 200
    Then New room row is added and persisted in the rooms table

  @high @functional @Key:RTC074
  Scenario: Verify deleting a room row via 'X' delete action on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 3
    And User deletes a room row using delete button
    Then Room row is deleted from the table layout

  @high @functional @Key:RTC075
  Scenario: Verify transition from Step 3 to Step 4 (Initial Settings)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User completes Step 1 through Step 3 and clicks 'Next >'
    Then Stepper updates to Step 4 of 5 and Initial Settings step is displayed

  @medium @functional @Key:RTC076
  Scenario: Verify Initial Settings dropdown selections on Step 4
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 4
    And User selects settings '1 Day' turnover, '7 Days' waiver deadline, and 'America/Chicago' timezone
    Then All dropdown selections update correctly on Step 4

  @high @validation @Key:RTC077
  Scenario: Verify waiver days numeric validity on Step 4
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 4
    And User enters invalid numeric values in Waiver fields from testData
    Then Invalid days trigger field error message requiring positive integer input

  @high @functional @Key:RTC078
  Scenario: Verify transition from Step 4 to Step 5 (Review & Save)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User completes Step 1 through Step 4 and clicks 'Next >'
    Then Stepper updates to Step 5 of 5 and Review and Save summary is displayed

  @high @critical @functional @Key:RTC079
  Scenario: Verify Review & Save summary accuracy on Step 5
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User completes Step 1 to Step 4 for Ranch and proceeds to Step 5
    Then Summary reflects all modifications made accurately without truncation

  @high @critical @functional @Key:RTC080
  Scenario: Verify successful submission and toast message on 'Save Changes'
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User completes Step 1 to Step 4 for Ranch and proceeds to Step 5
    And User clicks 'Save Changes' button
    Then Application saves changes, redirects user, and displays success toast alert

  @medium @accessibility @Key:RTC083
  Scenario: Verify keyboard accessibility and selection in form dropdowns via Tab and Arrow keys
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 4
    And User navigates dropdown using Tab and Arrow keys
    Then Dropdown opens and selects option via keyboard controls

  # @medium @accessibility @Key:RTC084
  # Scenario: Verify Tab focus trap inside open Modals
  #   Given User is logged in as Super Admin
  #   When User clicks on the Ranches module in the left sidebar
  #   And User clicks '+ Create New Ranch' button
  #   And User proceeds to Step 2 and opens Add Animal modal
  #   And User presses Tab repeatedly inside the modal
  #   Then Keyboard focus stays trapped within modal elements

  @medium @validation @Key:RTC086
  Scenario: Verify entering only whitespace in required text fields
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User enters whitespace only into Ranch Name and clicks 'Next >'
    Then System trims whitespace and displays required field error message

  @medium @validation @Key:RTC091
  Scenario: Verify Contact Email rejection when multiple dots precede domain
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User enters invalid email 'info..ranch@domain.com'
    And User clicks 'Next >'
    Then Validation error displays indicating invalid email format

  @medium @boundary @Key:RTC093
  Scenario: Verify Property Size and Price fields reject decimal inputs when integer required
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User proceeds to Step 2
    And User enters decimal value '12.5' in integer field
    Then System enforces integer validation or rounds appropriately

  @medium @boundary @Key:RTC094
  Scenario: Verify Cost per Night ($) rejects negative monetary values
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User proceeds to Step 3
    And User enters negative cost '-200'
    Then Inline validation error displays requiring positive cost per night

  @medium @validation @Key:RTC095
  Scenario: Verify unchecking or submitting form without required dropdown selections
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 4
    And User checks required dropdown validations on Step 4
    Then System enforces default selections or displays validation warning

  @medium @errorhandling @Key:RTC096
  Scenario: Verify Network Failure handling during form submission
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User completes Step 1 to Step 4 for Ranch and proceeds to Step 5
    And User simulates offline network and submits form
    Then Form data is preserved on Step 5 and error message is displayed

  # @High @Type:Functional @Key:RTC097
  # Scenario: Verify rapid double-click prevention on 'Create Ranch' button
  #   Given User is logged in as Super Admin
  #   When User clicks on the Ranches module in the left sidebar
  #   And User clicks '+ Create New Ranch' button
  #   And User completes Step 1 to Step 4 for Ranch and proceeds to Step 5
  #   And User rapidly clicks 'Create Ranch' button
  #   Then Button enters disabled state preventing duplicate submissions

  @medium @usability @Key:RTC099
  Scenario: Verify Cross-Browser Compatibility for Ranch Management pages
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    Then Ranch Management page UI elements and layout render properly

  @Key:RTC053 @Medium @Type:Validation
  Scenario: Verify validation error clears immediately upon user correction
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User leaves Ranch Name blank and triggers validation error
    And User types single character into Ranch Name field
    Then Red validation error message clears immediately

  @Key:RTC057 @Medium @Type:UI
  Scenario: Verify Edit Ranch stepper component visual indicators
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    Then Stepper progress bar displays Step 1 of 5 and 20% complete indicator

  @Key:RTC065 @Medium @Type:Validation
  Scenario: Verify Website URL field format validation
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 2
    And User enters invalid website URL string
    Then Inline validation error message appears under Website URL field

  @Key:RTC067 @Medium @Type:Validation
  Scenario: Verify Price ($) numeric validation for Huntable Animals
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 2
    And User enters negative price in animal price field
    Then Field highlights error and blocks progression

  @Key:RTC071 @Medium @Type:Functional
  Scenario: Verify Beds stepper controls (- and + buttons) on Step 3
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User proceeds to Step 3
    And User clicks Beds minus and plus stepper buttons
    Then Beds count decrements and increments accurately

  @Key:RTC081 @Medium @Type:Accessibility
  Scenario: Verify forward Tab key navigation order in Step 1 form
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User presses Tab key repeatedly across Step 1 inputs
    Then Keyboard focus moves sequentially across all form fields

  @Key:RTC082 @Medium @Type:Accessibility
  Scenario: Verify backward Shift+Tab key navigation order in Step 1 form
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User presses Shift+Tab key repeatedly across Step 1 inputs
    Then Keyboard focus moves in reverse order across all form fields

  @Key:RTC085 @Medium @Type:Accessibility
  Scenario: Verify submitting forms using keyboard Enter key
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User proceeds to Step 2 and opens Add Stand modal
    And User types Stand Name and presses Enter key
    Then Stand modal form submits successfully

  @Key:RTC087 @Medium @Type:Functional
  Scenario: Verify entering Unicode and special characters in text fields
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User enters Unicode and special characters 'Ranch @#& %$★ 🌵' in Ranch Name
    Then Text fields accept Unicode characters without breaking layout

  @Key:RTC092 @Medium @Type:Validation
  Scenario: Verify Contact Email rejection on leading or trailing spaces
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User types valid email with leading or trailing spaces ' info@ranch.com '
    Then Leading and trailing spaces in email field are trimmed automatically

  @Key:RTC100 @Low @Type:UI
  Scenario: Verify Toast Alert notification rendering and auto-dismissal
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And A success toast notification is displayed on Ranches page
    Then Toast alert renders at bottom right and auto-dismisses smoothly

  @Key:RTC101 @Medium @Type:Validation
  Scenario: Verify leading and trailing spaces in text fields are trimmed on Step 5 Review screen
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User enters Ranch Name with leading and trailing spaces ' Powderhorn Ranch '
    And User proceeds to Step 5
    Then Step 5 Review screen displays clean trimmed ranch name 'Powderhorn Ranch'

  @High @Type:Functional @Key:RTC102
  Scenario: Verify trimmed text is saved accurately in database and list view
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks 'Edit' on a ranch
    And User enters text with leading spaces '  Powderhorn Ranch  '
    And User proceeds to Step 5 and saves changes
    Then Saved record in list view displays trimmed text without extra spaces

  @Key:RTC054 @Medium @Type:Validation
  Scenario: Verify currency formatting validation (Max 2 decimals)
    Given User is logged in as Super Admin
    When User clicks on the Ranches module in the left sidebar
    And User clicks '+ Create New Ranch' button
    And User proceeds to Step 2 and enters price with 3 decimal places '150.999'
    Then Field rounds input to 2 decimal places or enforces currency format validation
