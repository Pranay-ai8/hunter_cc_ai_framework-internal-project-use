@regression
@HarvestLogs @UI
Feature: Harvest Logs Module


  # --- Navigation ---

  @UI @High @Critical @Functional @Navigation @Key:TC_HL_001
  Scenario: TC_HL_001 Verify navigation to Harvest Logs module from login page
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    Then User should be on "/harvest" harvest logs page
    And The harvest logs page title should be "Harvest Logs"
    And The harvest logs page subtitle should be "View and manage harvest records"

  @UI @High @Functional @StateTransition @Key:TC_HL_002
  Scenario: TC_HL_002 Verify + Log New Harvest button navigation
   Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    Then User should be redirected to "/harvest/new" page

  @UI @Medium @Functional @Key:TC_HL_003
  Scenario: TC_HL_003 Verify Tag Settings button navigation
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    Then User should be redirected to "/harvest/tags-settings" harvest page

  # --- Directory Filters, Search & Layout ---

  @UI @High @Functional @Key:TC_HL_004
  Scenario: TC_HL_004 Verify dynamic appearance and action of Clear Filters button
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User selects "Powderhorn Ranch" from the "All Ranches" dropdown
    Then The "Clear Filters" button should appear
    When User clicks the harvest log "Clear Filters" button
    Then The ranch filter should reset to "All Ranches"
    And The "Clear Filters" button should be hidden
    And The harvest logs grid should re-fetch the default records

  @UI @High @Functional @Key:TC_HL_005
  
  Scenario: TC_HL_005 Validate Date Range filtering (From Date and To Date)
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User sets the "From Date" filter to "-30 days"
    And User sets the "To Date" filter to "Today"
    Then The harvest logs grid should display only records with a Kill Date between "-30 days" and "Today"

  @UI @High @Medium @Negative @BoundaryValue @Key:TC_HL_006
  Scenario: TC_HL_006 Validate Date Range BVA - From Date greater than To Date (Negative)
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User sets the "From Date" filter to "+10 days"
    And User sets the "To Date" filter to "Today"
    Then The system should prevent the selection or display the inline error "From Date cannot be later than To Date"

  @UI @High @Functional @Key:TC_HL_007
  Scenario: TC_HL_007 Verify Search input filtering by Harvest ID and Species
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User enters "Whitetail" in the harvest logs search box
    Then The harvest logs grid should filter in real-time to records matching "Whitetail"

  @UI @High @Critical @Security @Negative @Key:TC_HL_008
  Scenario: TC_HL_008 Validate Search box special character & SQL injection handling
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User enters "' OR '1'='1" in the harvest logs search box
    Then The search should handle the payload safely without a server error or exposed database error

  @UI @Medium @Low @Key:TC_HL_009
  Scenario: TC_HL_009 Verify grid Layout View toggle between Table View and Card View
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log Card View toggle icon
    Then The harvest logs grid should display in Card layout
    When User clicks the harvest log Table View toggle icon
    Then The harvest logs grid should display in Table layout with columns "Harvest ID", "Species", "Animal", "Kill Date", "Tag", "Weight", and "Actions"

  @UI @Medium @EdgeCase @Visual @Key:TC_HL_010
  Scenario: TC_HL_010 Verify Empty State display when no records match filters
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User enters "xyz999" in the harvest logs search box
    Then The harvest logs grid should display the "No harvest logs found" empty state without layout breakage

  @UI @High @Functional @Key:TC_HL_011
  Scenario: TC_HL_011 Verify All Species dropdown selection and filtering
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User dynamically selects an available species from the "All Species" dropdown
    Then The harvest logs grid should display only records matching the selected species

  @UI @High @Critical @ErrorHandling @Security @Key:TC_HL_012
  Scenario: TC_HL_012 Validate session expiration while interacting with filters
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User's session token expires
    And User changes a harvest logs filter
    Then User should be redirected to "/login" page with a session timeout message

  # --- Tag Allocation (Tag Settings) ---

   @UI @High @Critical @Functional @Key:TC_HL_013
  Scenario: TC_HL_013 Verify successful creation of a new Tag Allocation
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And User clicks the harvest log "+ Add Tag Allocation" button
    And User fills the tag allocation form with an available ranch, category "Turkey", sex "Female", and allocated tags "7"
    And User clicks the harvest log "Create Allocation" button
    Then A harvest log success toast "Tag allocation created successfully" should display
    And The new allocation row should display in the allocation table

  @UI @High @Validation @Key:TC_HL_014
  Scenario: TC_HL_014 Validate mandatory field enforcement on Tag Allocation creation
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And User clicks the harvest log "+ Add Tag Allocation" button
    And User leaves "Ranch" and "Allocated Tags" blank in the tag allocation form
    And User clicks the harvest log "Create Allocation" button
    Then Form submission should be blocked
    And A harvest log validation error "Please fill in all required fields" should display

  @Key:TC_HL_015
  Scenario Outline: TC_HL_015 Validate Boundary Value Analysis (BVA) for Allocated Tags input
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And User clicks the harvest log "+ Add Tag Allocation" button
    And User fills the tag allocation form with an available ranch, category "Turkey", sex "Female", and allocated tags "<tags>"
    And User clicks the harvest log "Create Allocation" button
    Then The allocation submission result should be "accepted successfully"

    Examples:
      | tags |
      | 1    |
      | 9   |

  @UI @Medium @Validation @Key:TC_HL_016
  Scenario: TC_HL_016 Verify mathematical accuracy of Remaining Tags and Usage %
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    Then Every allocation row should mathematically calculate Remaining Tags and Usage progress

  @UI @Medium @Validation @Key:TC_HL_017
  Scenario: TC_HL_017 Verify duplicate Tag Allocation prevention (Same Season/Species/Sex)
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And a 'Deer' tag allocation already exists for ranch "test ranch"
    And User clicks the harvest log "+ Add Tag Allocation" button
    And User fills the tag allocation form with ranch "test ranch", category "Deer", sex "Male", and allocated tags "5"
    And User clicks the harvest log "Create Allocation" button
    Then The system should block creation with the error "A tag allocation already exists for Deer that overlaps with these dates"

  @UI @Medium @Functional @Key:TC_HL_018
  Scenario: TC_HL_018 Validate Edit Tag Allocation workflow
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And User clicks "Edit" on an existing allocation row
    And User updates the "Allocated Tags" value to "2"
    And User clicks the harvest log "Update Allocation" button
    Then A success toast should display
    And The allocation grid should reflect the updated allocated and remaining tag counts

  # --- Log New Harvest Form ---

  @UI @Medium @Validation @Key:TC_HL_019
  Scenario: TC_HL_019 Verify Choose Ranch dependency on Log New Harvest form
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    Then The "Animal" dropdown should be disabled
    When User selects "Powderhorn Ranch" from the "Choose Ranch" dropdown
    Then The "Animal" dropdown should be enabled
    And The ranch address details should display

  @UI @High @Medium @Navigation @Key:TC_HL_020
  Scenario: TC_HL_020 Validate Back to Harvest Logs breadcrumb navigation
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    And User clicks the harvest log "Back to Harvest Logs" breadcrumb link
    Then User should be redirected to "/harvest" page
    And No draft data should be saved

  @UI @Medium @Negative @Security @Key:TC_HL_021
  Scenario: TC_HL_021 Validate Photo upload file restrictions on Log New Harvest form
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    And User attempts to upload the file "archive.zip" under Harvest Photos
    Then The upload should be rejected with the inline error "Only image files (JPG, PNG) are allowed."

  @UI @Medium @Mobile @Responsiveness @Key:TC_HL_022
  Scenario: TC_HL_022 Verify layout responsiveness of Tag Allocation modal on mobile devices
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    Given User sets the browser viewport to "375x812"
    When User clicks the "Tag Settings" button
    And User clicks the "Add Tag Allocation" button
    Then The Tag Allocation modal should adapt cleanly to the mobile viewport without horizontal overflow

  
  @UI @High @Critical @FieldValidation @Negative @Key:TC_HL_024
  Scenario: TC_HL_024 Validate "Missing Required Fields" real-time warning banner
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    And User clicks the "Submit Harvest Log" button without filling any fields
    Then Form submission should be blocked
    And A warning banner listing "Stand selection required", "Animal selection required", and "Sex selection required" should display
    And A red toast popup should appear for harvest log

  
  @UI @Medium @Low @Functional @Key:TC_HL_027
  Scenario: TC_HL_027 Verify optional Booking Selection dropdown behavior
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    And User selects "Powderhorn Ranch" from the "Choose Ranch" dropdown
    And User selects "No booking" from the "Choose Booking (Optional)" dropdown
    Then The form should allow logging an independent harvest without linking to a booking


  @UI @Medium @StateTransition @Key:TC_HL_029
  Scenario: TC_HL_029 Validate Cancel button form reset and navigation
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    And User fills in partial harvest log details
    And User clicks the "Cancel" button
    Then The form draft data should clear
    And User should be redirected to "/harvest" page

  @UI @High @Medium @Negative @BusinessLogic @Key:TC_HL_030
  Scenario: TC_HL_030 Verify Date of Kill constraint against future dates (Negative)
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "+ Log New Harvest" button
    And User selects the future date "08/25/2030" in the "Date of Kill" calendar picker
    Then The calendar should disable future dates or display a validation error "Date of Kill cannot be in the future"

  # --- Generic: Form Validation & Boundary Values ---

  @UI @High @Critical @Generic @FormValidation @Key:GTC_HL_001
  Scenario: GTC_HL_001 Verify mandatory field enforcement on empty form submission across Harvest Logs
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the harvest log "Tag Settings" button
    And User clicks the harvest log "+ Add Tag Allocation" button
    And User clicks the harvest log "Create Allocation" button
    Then Form submission should be blocked
    And A harvest log validation error "Please fill in all required fields" should display

  @UI @High @Medium @Generic @BoundaryValue @Key:GTC_HL_002
  Scenario Outline: GTC_HL_002 Validate character boundary limits (BVA) on numeric input fields
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the "+ Log New Harvest" button
    And User enters "<Value>" in a numeric input field
    Then The numeric field result should be "<ExpectedResult>"

    Examples:
      | Value  | ExpectedResult                     |
      | -5     | blocked with inline error          |
      | 0      | blocked with inline error          |
      | 1      | accepted smoothly                  |
      | 999999 | accepted smoothly                  |

  # --- Generic: File Uploads ---

  @UI @High @Generic @FileUpload @Key:GTC_HL_003
  Scenario: GTC_HL_003 Verify successful upload of valid image attachments (JPG, PNG)
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the "+ Log New Harvest" button
    And User uploads the valid image file "harvest_photo.jpg" under Harvest Photos
    Then The image should upload successfully with a thumbnail preview and file size details

  @UI @High @Generic @FileUpload @Security @Key:TC_HL_GTC_004
  Scenario: GTC_HL_004 Validate file upload rejection for unsupported extensions
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the "+ Log New Harvest" button
    And User attempts to upload the unsupported files "script.exe" and "archive.zip" under Harvest Photos
    Then Only the supported image file types should be shown as an upload option

  @UI @Medium @Generic @FileUpload @Key:TC_HL_GTC_006
  Scenario: GTC_HL_006 Verify multiple file batch upload behavior and queueing
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the "+ Log New Harvest" button
    And User selects 5 valid image files simultaneously under Harvest Photos
    Then All 5 image files should attach and render thumbnails in the upload queue simultaneously

  # --- Generic: Network, Offline & Error Handling ---

  @UI @Medium @Generic @Performance @NetworkThrottling @Key:TC_HL_GTC_NET_001
  Scenario: GTC_HL_NET_001 Verify module performance on Slow 3G / High Latency network connection
    When The network is throttled to "Slow 3G"
    Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    And User applies a ranch or date filter
    Then A loading spinner or skeleton loader should display
    And The data should render correctly without a timeout crash


  
  # --- Generic: Responsiveness, Accessibility & Cross-Platform ---

  @UI @Medium @Generic @Responsiveness @Key:GTC_HL_009
  Scenario: GTC_HL_009 Verify Dashboard & Directory layout responsiveness across Viewports
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User resizes the browser to viewport "375x812"
    Then The layout should adapt without overlapping buttons
    When User resizes the browser to viewport "768x1024"
    Then The layout should adapt without overlapping buttons
    When User resizes the browser to viewport "1440x900"
    Then The layout should adapt without overlapping buttons

  @UI @Medium @Low @Generic @Accessibility @Key:GTC_HL_010
  Scenario: GTC_HL_010 Verify page zoom handling up to 200% without layout breakage
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User sets the browser zoom to "200%"
    Then All text and interactive controls should remain accessible without clipping or overlapping

  @UI @Medium @Generic @Accessibility @Key:GTC_HL_011
  Scenario: GTC_HL_011 Verify Keyboard Tab Focus Order and Focus Ring visibility on forms
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    When User clicks the "+ Log New Harvest" button
    And User presses Tab key repeatedly through the form fields
    Then Focus should move logically from top to bottom through all controls
    And The active element should display a clear focus ring



  @UI @Medium @Generic @CrossBrowser @Key:GTC_HL_016
  Scenario: GTC_HL_016 Verify module rendering consistency across major Web Browsers
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    Then The Harvest Logs module should render uniformly across "Chrome", "Firefox", "Safari", and "Edge" with zero functional defects

  @UI @Medium @Generic @CrossPlatform @Key:GTC_HL_017
  Scenario: GTC_HL_017 Verify mobile browser compatibility on iOS Safari and Android Chrome
  Given User logged in as Super Admin
    When User navigates to the "Harvest Logs" module from the left navigation menu
    Then The date pickers, file uploaders, and modal submit buttons should behave correctly on "iOS Safari" and "Android Chrome"

  