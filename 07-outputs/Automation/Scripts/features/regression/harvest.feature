Feature: Harvest Log & Compliance — Regression
  As a checked-in Guide / Ranch Admin / Super Admin
  I want Harvest Log creation, tag allocation, category management and photo upload to work correctly
  So that regulatory compliance records stay accurate and auditable

  # ---------- Create/Edit Harvest Log validation (TC_HARVEST_002-009) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_missing_ranch @regression @P2
  Scenario: Create Harvest Log without selecting Ranch
    # TC_HARVEST_002
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I submit the Harvest Log
    Then the Harvest Log should not be saved and a validation error indicating "Ranch" is required should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:harvest_missing_date_of_kill @regression @P2
  Scenario: Create Harvest Log without entering Harvest Date
    # TC_HARVEST_003
    # NOTE: live field is "Date of Kill" (split from "Time of Kill") — see harvest.page.ts header.
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I submit the Harvest Log
    Then the Harvest Log should not be saved and a validation error indicating "Date of Kill" is required should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:harvest_missing_animal_category @regression @P2
  Scenario: Create Harvest Log without entering Harvest Details (Animal/Category)
    # TC_HARVEST_004
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select date of kill
    And I submit the Harvest Log
    Then the Harvest Log should not be saved and a validation error indicating "Animal" is required should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:harvest_notes_freetext @regression @P2
  Scenario: Notes field accepts free-text entry with no enumerated value list
    # TC_HARVEST_005
    # NOTE: best-available live stand-in for the documented "Regulatory Compliance" field
    # — see harvest.page.ts header.
    Given User is logged in as "td.role"
    When I create a Harvest Log for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" with notes "td.freeTextNotes"
    Then the Harvest Log should be saved successfully with notes "td.freeTextNotes"

  @DataFile:resources/data/harvestdata.csv @Key:harvest_blocked_before_checkin @regression @P2
  Scenario: Harvest Log creation is unavailable before booking check-in
    # TC_HARVEST_006
    Given User is logged in as "td.role"
    And a booking "td.bookingId" has not been checked in
    When I attempt to initiate Harvest Log creation for booking "td.bookingId"
    Then Harvest Log creation should be unavailable or blocked

  @DataFile:resources/data/harvestdata.csv @Key:harvest_edit_details @regression @P2
  Scenario: Edit an existing Harvest Log's Harvest Details
    # TC_HARVEST_007
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.harvestId" exists
    When I open the Edit Harvest Log form for "td.harvestId"
    And I select date of kill
    And I fill time of kill "td.newTimeOfKill"
    And I submit the Harvest Log
    Then the Harvest Log should be saved successfully

  @DataFile:resources/data/harvestdata.csv @Key:harvest_edit_notes @regression @P2
  Scenario: Edit an existing Harvest Log's Regulatory Compliance note
    # TC_HARVEST_008
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.harvestId" exists
    When I open the Edit Harvest Log form for "td.harvestId"
    And I fill notes "td.newNotes"
    And I submit the Harvest Log
    Then the Harvest Log should be saved successfully with notes "td.newNotes"

  @DataFile:resources/data/harvestdata.csv @Key:harvest_edit_clear_date @regression @P2
  Scenario: Edit Harvest Log — clear mandatory Date of Kill
    # TC_HARVEST_009
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.harvestId" exists
    When I open the Edit Harvest Log form for "td.harvestId"
    And I clear the Date of Kill field and save
    Then the Harvest Log should not be saved and a validation error indicating "Date of Kill" is required should be displayed

  # ---------- API integration (TC_HARVEST_010-011) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_persists_after_reload @regression @P2
  Scenario: Harvest Log submitted via the frontend form is persisted end-to-end
    # TC_HARVEST_010
    Given User is logged in as "td.role"
    When I create a Harvest Log for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" with notes "td.notes"
    And I reload the Harvest Log list
    Then the Harvest Log should be saved successfully

  @DataFile:resources/data/harvestdata.csv @Key:harvest_network_failure @regression @P2
  Scenario: Frontend handles a failed Harvest Log submission without a false success state
    # TC_HARVEST_011
    Given User is logged in as "td.role"
    And the harvest-log network request will fail
    When I create a Harvest Log for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" with notes "td.notes"
    Then an error message should be displayed and the Harvest Log should not be persisted

  # ---------- Super Admin enhancements (TC_HARVEST_012-020) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_delete_confirm @regression @P1
  Scenario: Super Admin deletes a Harvest Log with confirmation
    # TC_HARVEST_012
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I delete Harvest Log "td.harvestId" confirming the prompt
    Then Harvest Log "td.harvestId" should no longer appear in the list

  @DataFile:resources/data/harvestdata.csv @Key:harvest_delete_cancel @regression @P2
  Scenario: Super Admin cancels the delete confirmation prompt
    # TC_HARVEST_013
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I delete Harvest Log "td.harvestId" cancelling the prompt
    Then Harvest Log "td.harvestId" should remain unchanged in the list

  @DataFile:resources/data/harvestdata.csv @Key:harvest_delete_restricted_nonadmin @regression @P1
  Scenario: Non-Super-Admin role cannot delete a Harvest Log
    # TC_HARVEST_014
    Given User is logged in as "Ranch Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I open the Harvest Log "td.harvestId"
    Then the Delete action should be unavailable or blocked

  @DataFile:resources/data/harvestdata.csv @Key:harvest_add_past_booking @regression @P1
  Scenario: Super Admin adds a Harvest Log against a past (checked-out) booking
    # TC_HARVEST_015
    Given User is logged in as "Super Admin"
    And a past checked-out booking "td.pastBookingId" exists
    When I create a Harvest Log against past booking "td.pastBookingId" for ranch "td.ranchName", animal "td.animalName", category "td.categoryName"
    Then the Harvest Log should be saved successfully

  @DataFile:resources/data/harvestdata.csv @Key:harvest_edit_past_booking @regression @P2
  Scenario: Super Admin edits a Harvest Log against a past booking
    # TC_HARVEST_016
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists against a past booking
    When I open the Edit Harvest Log form for "td.harvestId"
    And I fill notes "td.newNotes"
    And I submit the Harvest Log
    Then the Harvest Log should be saved successfully with notes "td.newNotes"

  @DataFile:resources/data/harvestdata.csv @Key:harvest_delete_past_booking @regression @P1
  Scenario: Super Admin deletes a Harvest Log against a past booking with confirmation
    # TC_HARVEST_017
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists against a past booking
    When I delete Harvest Log "td.harvestId" confirming the prompt
    Then Harvest Log "td.harvestId" should no longer appear in the list

  @DataFile:resources/data/harvestdata.csv @Key:harvest_past_booking_restricted_nonadmin @regression @P1
  Scenario: Non-Super-Admin role cannot add/edit/delete harvest logs against past bookings
    # TC_HARVEST_018
    Given User is logged in as "Ranch Admin"
    And a past checked-out booking "td.pastBookingId" exists
    When I attempt to initiate Harvest Log creation for booking "td.pastBookingId"
    Then Harvest Log creation should be unavailable or blocked

  @DataFile:resources/data/harvestdata.csv @Key:harvest_unlinked_create @regression @P1
  Scenario: Super Admin logs a new Harvest not linked to any booking
    # TC_HARVEST_019
    Given User is logged in as "Super Admin"
    When I create a Harvest Log for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" with notes "td.notes"
    Then the Harvest Log should be saved successfully

  @DataFile:resources/data/harvestdata.csv @Key:harvest_unlinked_restricted_nonadmin @regression @P1
  Scenario: Non-Super-Admin role cannot log an unlinked Harvest
    # TC_HARVEST_020
    Given User is logged in as "Ranch Admin"
    When I open the Log New Harvest form
    Then the Delete action should be unavailable or blocked

  # ---------- Tag Allocation & Consumption (TC_HARVEST_021-025) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_tag_consume_one @regression @P1
  Scenario: Creating a Harvest Log consumes one tag from the category's allocation
    # TC_HARVEST_021
    Given User is logged in as "td.role"
    And the "td.categoryName" tag allocation for ranch "td.ranchName" has "5" remaining
    When I create a Harvest Log for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" with notes "td.notes"
    Then the "td.categoryName" tag count for ranch "td.ranchName" should now be "4"

  @DataFile:resources/data/harvestdata.csv @Key:harvest_tag_decrement_sequence @regression @P1
  Scenario: Successive Harvest Log creations decrement the category allocation one at a time
    # TC_HARVEST_022
    Given User is logged in as "td.role"
    And the "td.categoryName" tag allocation for ranch "td.ranchName" has "3" remaining
    When I repeatedly create Harvest Logs for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" "3" times
    Then the "td.categoryName" tag count for ranch "td.ranchName" should now be "0"

  @DataFile:resources/data/harvestdata.csv @Key:harvest_tag_exhausted_blocked @regression @P1 @critical-path
  Scenario: Harvest Log creation is blocked once a category's tag allocation is exhausted
    # TC_HARVEST_023
    Given User is logged in as "td.role"
    And the "td.categoryName" tag allocation for ranch "td.ranchName" has "0" remaining
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "td.categoryName"
    Then a tag-exhausted warning should be displayed and the Harvest Log should not be saved

  @DataFile:resources/data/harvestdata.csv @Key:harvest_tag_last_available @regression @P1 @boundary
  Scenario: Consuming the last available tag for a category succeeds at the boundary
    # TC_HARVEST_024
    Given User is logged in as "td.role"
    And the "td.categoryName" tag allocation for ranch "td.ranchName" has "1" remaining
    When I create a Harvest Log for ranch "td.ranchName", animal "td.animalName", category "td.categoryName" with notes "td.notes"
    Then the "td.categoryName" tag count for ranch "td.ranchName" should now be "0"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "td.categoryName"
    Then a tag-exhausted warning should be displayed and the Harvest Log should not be saved

  @DataFile:resources/data/harvestdata.csv @Key:harvest_tag_per_ranch_isolation @regression @P1
  Scenario: Tag allocation/consumption is tracked independently per ranch
    # TC_HARVEST_025
    Given User is logged in as "td.role"
    And the "td.categoryName" tag allocation for ranch "td.ranchNameA" has "2" remaining
    And the "td.categoryName" tag allocation for ranch "td.ranchNameB" has "2" remaining
    When I create a Harvest Log for ranch "td.ranchNameA", animal "td.animalName", category "td.categoryName" with notes "td.notes"
    Then the "td.categoryName" tag count for ranch "td.ranchNameA" should now be "1"
    And the "td.categoryName" tag count for ranch "td.ranchNameB" should now be "2"

  # ---------- Animal Category Management (TC_HARVEST_026-031) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_category_create @regression @P2
  Scenario: Super Admin creates a new custom Animal Category
    # TC_HARVEST_026
    Given User is logged in as "Super Admin"
    When I open Animal Category Management
    And I create an animal category "td.newCategoryName"
    Then animal category "td.newCategoryName" should be saved and available for selection

  @DataFile:resources/data/harvestdata.csv @Key:harvest_category_view_list @regression @P3
  Scenario: Super Admin views the list of existing Animal Categories
    # TC_HARVEST_027
    Given User is logged in as "Super Admin"
    When I open Animal Category Management
    Then all existing categories should display correctly in the list

  @DataFile:resources/data/harvestdata.csv @Key:harvest_category_edit @regression @P2
  Scenario: Super Admin edits an existing custom Animal Category
    # TC_HARVEST_028
    Given User is logged in as "Super Admin"
    And a custom animal category "td.oldCategoryName" exists
    When I edit animal category "td.oldCategoryName" to "td.newCategoryName"
    Then animal category "td.newCategoryName" should be saved and available for selection

  @DataFile:resources/data/harvestdata.csv @Key:harvest_category_delete @regression @P2
  Scenario: Super Admin deletes a custom Animal Category
    # TC_HARVEST_029
    Given User is logged in as "Super Admin"
    And a custom animal category "td.categoryToDelete" exists
    When I delete animal category "td.categoryToDelete"
    Then animal category "td.categoryToDelete" should no longer be available for selection

  @DataFile:resources/data/harvestdata.csv @Key:harvest_category_restricted_nonadmin @regression @P2
  Scenario: Non-Super-Admin role cannot create/edit/delete an Animal Category
    # TC_HARVEST_030
    Given User is logged in as "Ranch Admin"
    When I open Animal Category Management
    Then the Delete action should be unavailable or blocked

  @DataFile:resources/data/harvestdata.csv @Key:harvest_category_blank_name @regression @P3
  Scenario: Create Animal Category with a blank category name
    # TC_HARVEST_031
    Given User is logged in as "Super Admin"
    When I open Animal Category Management
    And I create an animal category ""
    Then the Harvest Log should not be saved and a validation error indicating "Category Name" is required should be displayed

  # ---------- Category-conditional field display (TC_HARVEST_032-034) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_deer_antler_fields @regression @P2
  Scenario: Selecting "Deer" category displays the full Deer field set
    # TC_HARVEST_032
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "Deer"
    Then the Antler Information field group should be visible

  @DataFile:resources/data/harvestdata.csv @Key:harvest_other_antler_fields @regression @P2
  Scenario: Selecting "Other" category replicates the full Deer field set
    # TC_HARVEST_033
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "Other"
    Then the Antler Information field group should be visible

  @DataFile:resources/data/harvestdata.csv @Key:harvest_nondeer_no_antler @regression @P2
  Scenario: A non-Deer, non-Other category does not display the Deer/Antler field set
    # TC_HARVEST_034
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "td.nonDeerCategoryName"
    Then the Antler Information field group should not be visible

  # ---------- Animal Name Display (TC_HARVEST_035-036) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_animal_name_deer @regression @P2
  Scenario: Final Harvest Results display animal name instead of species for category "Deer"
    # TC_HARVEST_035
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.deerHarvestId" exists for category "Deer" named "td.animalName"
    When I open the Final Harvest Results view for "td.deerHarvestId"
    Then the animal name "td.animalName" should display instead of species

  @DataFile:resources/data/harvestdata.csv @Key:harvest_animal_name_other @regression @P2
  Scenario: Final Harvest Results display animal name instead of species for category "Other"
    # TC_HARVEST_036
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.otherHarvestId" exists for category "Other" named "td.animalName"
    When I open the Final Harvest Results view for "td.otherHarvestId"
    Then the animal name "td.animalName" should display instead of species

  # ---------- Photo Upload (TC_HARVEST_038-039, 041) ----------

  @DataFile:resources/data/harvestdata.csv @Key:harvest_photo_gallery_select @regression @P2
  Scenario: Select an existing photo from the device gallery/camera roll
    # TC_HARVEST_038
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.photoFilePath" from the library
    Then the attached photo preview should be visible

  @DataFile:resources/data/harvestdata.csv @Key:harvest_photo_picker_cancel @regression @P3
  Scenario: Cancel the gallery/file picker without selecting a photo
    # TC_HARVEST_039
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I cancel the photo picker
    Then the attached photo preview should not be visible

  @DataFile:resources/data/harvestdata.csv @Key:harvest_photo_replace_gallery @regression @P3
  Scenario: Replace an existing Harvest Log photo via gallery selection
    # TC_HARVEST_041
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.harvestId" exists with an attached photo
    When I open the Edit Harvest Log form for "td.harvestId"
    And I attach photo "td.newPhotoFilePath" from the library
    And I submit the Harvest Log
    Then the attached photo preview should be visible

  # ---------- Generic: Harvest Log photo validation & resilience (TC_GEN_HARVEST_001,002,004,005,007,008,009) ----------

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_category_required @regression @P1
  Scenario: Harvest Log creation requires an Animal Category to be selected
    # TC_GEN_HARVEST_001
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I submit the Harvest Log
    Then the Harvest Log should not be saved and a validation error indicating "Category" is required should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_photo_unsupported_format @regression @P2
  Scenario: Harvest Log photo selection rejects a file with an unsupported (non-image) format
    # TC_GEN_HARVEST_002
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.unsupportedFormatFilePath" from the library
    Then a file-format validation error should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_photo_empty_file @regression @P3
  Scenario: Harvest Log photo selection rejects a 0-byte (empty) file
    # TC_GEN_HARVEST_004
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.emptyFilePath" from the library
    Then a file-format validation error should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_photo_special_chars @regression @P3
  Scenario: Harvest Log photo with special characters in its filename uploads successfully
    # TC_GEN_HARVEST_005
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.specialCharsFilePath" from the library
    Then the attached photo preview should be visible

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_camera_permission_denied @regression @P3
  Scenario: Browser camera-permission denial is handled gracefully when attempting to capture a new Harvest Log photo
    # TC_GEN_HARVEST_007
    Given User is logged in as "td.role"
    And the browser camera permission will be denied
    When I open the Log New Harvest form
    And I attempt to take a photo
    Then a camera-permission error or fallback message should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_photo_corrupted @regression @P3
  Scenario: Harvest Log photo selection rejects/flags a corrupted image file
    # TC_GEN_HARVEST_008
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.corruptedFilePath" from the library
    Then a file-format validation error should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:gen_harvest_photo_offline @regression @P3
  Scenario: Attempting to attach a Harvest Log photo while offline shows a clear network-failure message instead of silently failing
    # TC_GEN_HARVEST_009
    Given User is logged in as "td.role"
    And the network connection is offline
    When I open the Log New Harvest form
    And I attach photo "td.photoFilePath" from the library
    Then a network-failure message should be displayed
    When the network connection is restored and I retry attaching photo "td.photoFilePath" from the library
    Then the attached photo preview should be visible

  # ---------- UI: Harvest Log form, delete modal, categories, photo, results (TC_UI_HARVEST_002-023 excl. 014) ----------

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_category_page_structure @regression @P2
  Scenario: Animal Category Management screen loads with list and Add Category control visible
    # TC_UI_HARVEST_002
    Given User is logged in as "Super Admin"
    When I open Animal Category Management
    Then the category list should render and the Add Category control should be visible

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_core_fields_labels @regression @P2
  Scenario: Core fields render with labels and mandatory indicators
    # TC_UI_HARVEST_003
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    Then the Ranch, Date of Kill, Time of Kill, Animal and Category fields should be visible with labels and mandatory indicators

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_deer_expansion_no_reload @regression @P2
  Scenario: Selecting "Deer" visually expands the Antler Information field group without reload
    # TC_UI_HARVEST_004
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "Deer"
    Then the Antler Information field group should be visible without a page reload

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_other_expansion_no_reload @regression @P2
  Scenario: Selecting "Other" visually expands the same Deer-style field group
    # TC_UI_HARVEST_005
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "Other"
    Then the Antler Information field group should be visible without a page reload

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_nondeer_no_gap @regression @P3
  Scenario: A non-Deer/non-Other category leaves the Antler field group collapsed with no layout gap
    # TC_UI_HARVEST_006
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "td.nonDeerCategoryName"
    Then the Antler Information field group should not be visible and no layout gap should remain

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_delete_modal_open @regression @P2
  Scenario: Confirmation modal opens on Delete click
    # TC_UI_HARVEST_007
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I click Delete on Harvest Log "td.harvestId"
    Then the delete confirmation modal should be visible with Confirm and Cancel buttons

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_delete_modal_cancel @regression @P3
  Scenario: Confirmation modal closes via Cancel without deleting
    # TC_UI_HARVEST_008
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I delete Harvest Log "td.harvestId" cancelling the prompt
    Then the delete confirmation modal should be closed
    And Harvest Log "td.harvestId" should remain unchanged in the list

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_delete_modal_escape @regression @P3
  Scenario: Confirmation modal closes via Escape key
    # TC_UI_HARVEST_009
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I click Delete on Harvest Log "td.harvestId"
    And I press Escape to dismiss the delete modal
    Then the delete confirmation modal should be closed

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_category_form_fields @regression @P3
  Scenario: Add/Edit Category form fields render with labels
    # TC_UI_HARVEST_010
    Given User is logged in as "Super Admin"
    When I open Animal Category Management
    And I open the Add Category form
    Then the Category Name field should be visible with a label and mandatory indicator

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_category_row_controls @regression @P3
  Scenario: Each category row shows Edit and Delete controls
    # TC_UI_HARVEST_011
    Given User is logged in as "Super Admin"
    And a custom animal category "td.oldCategoryName" exists
    When I open Animal Category Management
    Then category row "td.oldCategoryName" should display Edit and Delete controls

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_tag_exhausted_visual @regression @P3
  Scenario: Tag-exhausted state visually disables Create and shows an inline message
    # TC_UI_HARVEST_012
    Given User is logged in as "td.role"
    And the "td.categoryName" tag allocation for ranch "td.ranchName" has "0" remaining
    When I open the Log New Harvest form
    And I select ranch "td.ranchName" for the harvest log
    And I select animal "td.animalName"
    And I select category "td.categoryName"
    Then a tag-exhausted warning should be displayed and the Harvest Log should not be saved

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_category_empty_state @regression @P3
  Scenario: Empty state shown when no custom categories exist
    # TC_UI_HARVEST_013
    Given User is logged in as "Super Admin"
    And a ranch "td.emptyRanchName" exists with zero guide records
    When I open Animal Category Management for ranch "td.emptyRanchName"
    Then an empty-state message should be displayed

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_photo_options_visible @regression @P2
  Scenario: Upload control displays both Take Photo and Choose from Gallery options
    # TC_UI_HARVEST_015
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    Then the Take Photo and Choose from Library options should be visible

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_photo_preview @regression @P2
  Scenario: Selecting a gallery photo displays a preview thumbnail
    # TC_UI_HARVEST_016
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.photoFilePath" from the library
    Then the attached photo preview should be visible

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_photo_remove @regression @P3
  Scenario: Remove control reverts the preview to the empty state
    # TC_UI_HARVEST_017
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I attach photo "td.photoFilePath" from the library
    And I remove the attached photo
    Then the attached photo preview should not be visible

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_results_animal_name_label @regression @P3
  Scenario: Final Harvest Results screen renders an "Animal Name" label/field
    # TC_UI_HARVEST_018
    Given User is logged in as "td.role"
    And an existing Harvest Log "td.deerHarvestId" exists for category "Deer" named "td.animalName"
    When I open the Final Harvest Results view for "td.deerHarvestId"
    Then an "Animal Name" field label should be visible

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_responsive_mobile @regression @P3
  Scenario: Harvest Log Form renders usably at mobile viewport
    # TC_UI_HARVEST_019
    Given User is logged in as "td.role"
    And the viewport is set to "375" by "667"
    When I open the Log New Harvest form
    Then the Harvest Log form should render usably with no horizontal overflow

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_responsive_desktop @regression @P3
  Scenario: Harvest screens render correctly at desktop viewport
    # TC_UI_HARVEST_020
    Given User is logged in as "td.role"
    And the viewport is set to "1280" by "800"
    When I open the Log New Harvest form
    Then the Harvest Log form should render usably with no horizontal overflow

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_cross_browser @regression @P3 @cross-browser
  Scenario: Form, category-conditional fields and delete modal render consistently across browsers
    # TC_UI_HARVEST_021
    Given User is logged in as "Super Admin"
    When I open the Log New Harvest form in each supported browser
    And I select category "Deer"
    Then the Antler Information field group should be visible without a page reload

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_a11y_form @regression @P3 @accessibility
  Scenario: Fields are labeled and keyboard navigable with visible focus
    # TC_UI_HARVEST_022
    Given User is logged in as "td.role"
    When I open the Log New Harvest form
    And I tab through the Harvest Log form fields
    Then every field should have an associated label and be keyboard reachable with a visible focus indicator

  @DataFile:resources/data/harvestdata.csv @Key:ui_harvest_a11y_delete_modal @regression @P3 @accessibility
  Scenario: Confirmation modal is keyboard dismissible with correct focus handling
    # TC_UI_HARVEST_023
    Given User is logged in as "Super Admin"
    And an existing Harvest Log "td.harvestId" exists
    When I click Delete on Harvest Log "td.harvestId"
    Then focus should move into the modal
    When I press Escape to dismiss the delete modal
    Then the delete confirmation modal should be closed and focus should return to the Delete control
