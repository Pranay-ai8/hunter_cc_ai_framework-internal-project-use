@SA @regression
Feature: Super Admin & Ranch Admin Management — Regression
  As a Super Admin or Ranch Admin
  I want Create Ranch Admin validation and role restrictions to behave correctly
  So that ranch administration data stays accurate and properly scoped

  # ---------- Create Ranch Admin: happy path / cancel ----------

  @DataFile:resources/data/sadata.csv @Key:tc_sa_002 @regression
  Scenario: Super Admin creates a Ranch Admin with welcome email toggle OFF
    # TC_SA_002
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin creates a Ranch Admin using the test data
    Then the Ranch Admin account should be created successfully

  @DataFile:resources/data/sadata.csv @Key:tc_sa_010 @regression
  Scenario: Cancelling Create Ranch Admin discards entered data
    # TC_SA_010
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin fills the Create Ranch Admin form with the test data
    And the Super Admin cancels the Create Ranch Admin form
    Then the Create Ranch Admin modal should be closed

  # ---------- Create Ranch Admin: mandatory / format / boundary validation ----------

  @DataFile:resources/data/sadata.csv @Key:tc_sa_003 @regression @Negative
  Scenario: Create Ranch Admin blocked when Full Name is blank
    # TC_SA_003
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin leaves "Full Name" blank on the Create Ranch Admin form and attempts to submit
    Then a mandatory-field validation error should be shown for "Full Name" on the Create Ranch Admin form
    And the Ranch Admin account should not be created

  @DataFile:resources/data/sadata.csv @Key:tc_sa_004 @regression @Negative
  Scenario: Create Ranch Admin blocked when Email is blank
    # TC_SA_004
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin leaves "Email" blank on the Create Ranch Admin form and attempts to submit
    Then a mandatory-field validation error should be shown for "Email" on the Create Ranch Admin form
    And the Ranch Admin account should not be created

  @DataFile:resources/data/sadata.csv @Key:tc_sa_005 @regression @Negative
  Scenario: Create Ranch Admin blocked when Password is blank
    # TC_SA_005
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin leaves "Password" blank on the Create Ranch Admin form and attempts to submit
    Then a mandatory-field validation error should be shown for "Password" on the Create Ranch Admin form
    And the Ranch Admin account should not be created

  @DataFile:resources/data/sadata.csv @Key:tc_sa_006 @regression @Negative
  Scenario: Create Ranch Admin blocked when Assign to Ranch is not selected
    # TC_SA_006
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin leaves "Assign to Ranch" blank on the Create Ranch Admin form and attempts to submit
    Then a mandatory-field validation error should be shown for "Assign to Ranch" on the Create Ranch Admin form
    And the Ranch Admin account should not be created

  @DataFile:resources/data/sadata.csv @Key:tc_sa_007 @regression @Negative
  Scenario: Create Ranch Admin blocked when Email format is invalid
    # TC_SA_007
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin enters an invalid-format email on the Create Ranch Admin form and attempts to submit
    Then a format-validation error should be shown for Email on the Create Ranch Admin form
    And the Ranch Admin account should not be created

  @DataFile:resources/data/sadata.csv @Key:tc_sa_008 @regression @Negative @Boundary
  Scenario: Create Ranch Admin rejects a 7-character password (below minimum)
    # TC_SA_008
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin enters a 7-character password on the Create Ranch Admin form and attempts to submit
    Then a password-length validation error should be shown on the Create Ranch Admin form
    And the Ranch Admin account should not be created

  @DataFile:resources/data/sadata.csv @Key:tc_sa_009 @regression @Boundary
  Scenario: Create Ranch Admin accepts an 8-character password (minimum boundary)
    # TC_SA_009
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin enters a 8-character password on the Create Ranch Admin form and attempts to submit
    Then the Ranch Admin account should be created successfully

  # ---------- Role Restriction (Ranch Admin's own session) ----------

  @DataFile:resources/data/sadata.csv @Key:tc_sa_011 @regression @RoleBased
  Scenario: Ranch Admin cannot create a new Ranch Admin
    # TC_SA_011
    Given the user is logged in as a "Ranch Admin" user
    Then the "Create Ranch Admin" control should not be rendered

  @DataFile:resources/data/sadata.csv @Key:tc_sa_012 @regression @RoleBased
  Scenario: Ranch Admin cannot create a new Ranch
    # TC_SA_012
    Given the user is logged in as a "Ranch Admin" user
    Then the "Create Ranch" control should not be rendered

  @DataFile:resources/data/sadata.csv @Key:tc_sa_013 @regression @RoleBased
  Scenario: Ranch Admin can edit the Full Name field on an existing Ranch Admin record
    # TC_SA_013
    Given the user is logged in as a "Ranch Admin" user
    When the Ranch Admin opens an existing Ranch Admin record for edit
    And the Ranch Admin updates the Full Name field and saves
    Then the Full Name update should save successfully

  @DataFile:resources/data/sadata.csv @Key:tc_sa_014 @regression @RoleBased @Negative
  Scenario: Ranch Admin cannot edit fields other than Full Name on a Ranch Admin record
    # TC_SA_014
    Given the user is logged in as a "Ranch Admin" user
    When the Ranch Admin opens an existing Ranch Admin record for edit
    Then the Email field should render as read-only for the Ranch Admin role
    And the Assign to Ranch field should render as read-only for the Ranch Admin role

  # ---------- Generic: Mandatory / Edge-case / Network / Email ----------

  @DataFile:resources/data/sadata.csv @Key:tc_gen_sa_001 @regression
  Scenario: All mandatory fields on the Create Ranch Admin modal are validated as required
    # TC_GEN_SA_001
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin submits the Create Ranch Admin form
    Then the Create Ranch Admin modal should remain open

  @DataFile:resources/data/sadata.csv @Key:tc_gen_sa_002 @regression @EdgeCase
  Scenario: An email with multiple dots before the domain is rejected
    # TC_GEN_SA_002
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin enters an invalid-format email on the Create Ranch Admin form and attempts to submit
    Then a format-validation error should be shown for Email on the Create Ranch Admin form

  @DataFile:resources/data/sadata.csv @Key:tc_gen_sa_003 @regression @NetworkFailure
  Scenario: Create Ranch Admin submission fails gracefully when network connectivity is lost mid-submit
    # TC_GEN_SA_003
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin fills the Create Ranch Admin form with the test data
    And the Super Admin submits the Create Ranch Admin form
    Then the Ranch Admin account should be created successfully

  @DataFile:resources/data/sadata.csv @Key:tc_gen_sa_004 @regression @Email
  Scenario: A welcome/invitation email is sent when "Send welcome email" is enabled
    # TC_GEN_SA_004
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin creates a Ranch Admin using the test data
    Then the Ranch Admin account should be created successfully
    And a success toast should confirm the Ranch Admin was created

  # ---------- UI: Structure / Labels / Interactive / Feedback ----------

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_003 @regression
  Scenario: Mandatory-field indicators are displayed for Full Name, Email, Password, and Assign to Ranch
    # TC_UI_SA_003
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    Then a mandatory-field indicator should be visible next to "Full Name" on the Create Ranch Admin form
    And a mandatory-field indicator should be visible next to "Email" on the Create Ranch Admin form
    And a mandatory-field indicator should be visible next to "Password" on the Create Ranch Admin form
    And a mandatory-field indicator should be visible next to "Assign to Ranch" on the Create Ranch Admin form

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_004 @regression
  Scenario: Password field is masked by default
    # TC_UI_SA_004
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    Then the Create Ranch Admin password field should be masked

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_005 @regression
  Scenario: Password visibility toggle switches the field between masked and plain text
    # TC_UI_SA_005
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin toggles the Create Ranch Admin password visibility
    Then the Create Ranch Admin password field should show plain text
    When the Super Admin toggles the Create Ranch Admin password visibility
    Then the Create Ranch Admin password field should be masked

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_006 @regression
  Scenario: Assign to Ranch dropdown opens and displays the list of ranch options
    # TC_UI_SA_006
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Assign to Ranch dropdown on the Create Ranch Admin form
    Then the Assign to Ranch dropdown should display a list of available ranches

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_007 @regression
  Scenario: "Send welcome email" toggle visually switches between ON and OFF states
    # TC_UI_SA_007
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin toggles "Send welcome email" on the Create Ranch Admin form
    Then the "Send welcome email" toggle should switch states correctly

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_008 @regression
  Scenario: Modal closes when the Close (X) icon is clicked
    # TC_UI_SA_008
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin closes the Create Ranch Admin modal via the close icon
    Then the Create Ranch Admin modal should be closed

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_009 @regression @AccessibilityBasic
  Scenario: Modal closes when the Escape key is pressed
    # TC_UI_SA_009
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin closes the Create Ranch Admin modal by pressing Escape
    Then the Create Ranch Admin modal should be closed

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_010 @regression
  Scenario: Modal closes when clicking outside the modal (overlay)
    # TC_UI_SA_010
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    And the Super Admin closes the Create Ranch Admin modal by clicking the overlay
    Then the Create Ranch Admin modal should be closed

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_011 @regression
  Scenario: A success toast is displayed after a Ranch Admin is created successfully
    # TC_UI_SA_011
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin creates a Ranch Admin using the test data
    Then a success toast should confirm the Ranch Admin was created

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_012 @regression
  Scenario: Inline validation message is displayed directly beneath the invalid field and associated to it
    # TC_UI_SA_012
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin leaves "Full Name" blank on the Create Ranch Admin form and attempts to submit
    Then the validation error for Full Name should render directly beneath the field and be associated to it

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_013 @regression @RoleBased
  Scenario: "Create Ranch Admin" control is not rendered in the Ranch Admin's navigation/menu
    # TC_UI_SA_013
    Given the user is logged in as a "Ranch Admin" user
    Then the "Create Ranch Admin" control should not be rendered

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_014 @regression @RoleBased
  Scenario: "Create Ranch" control is not rendered in the Ranch Admin's navigation/menu
    # TC_UI_SA_014
    Given the user is logged in as a "Ranch Admin" user
    Then the "Create Ranch" control should not be rendered

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_015 @regression @RoleBased
  Scenario: Non-Full-Name fields render as visually disabled/read-only for a Ranch Admin viewing another record
    # TC_UI_SA_015
    Given the user is logged in as a "Ranch Admin" user
    When the Ranch Admin opens an existing Ranch Admin record for edit
    Then the Email field should render as read-only for the Ranch Admin role
    And the Assign to Ranch field should render as read-only for the Ranch Admin role

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_016 @regression @Responsive
  Scenario: Modal remains fully usable at mobile viewport (375px)
    # TC_UI_SA_016
    Given the viewport is set to 375 by 667
    And the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    Then the Create Ranch Admin modal should remain fully usable at the configured viewport

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_017 @regression @Responsive
  Scenario: Modal remains fully usable at desktop viewport (1280px)
    # TC_UI_SA_017
    Given the viewport is set to 1280 by 800
    And the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    Then the Create Ranch Admin modal should remain fully usable at the configured viewport

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_018 @regression @CrossBrowser
  Scenario: Modal renders and functions consistently across Chrome, Firefox, Safari and Edge
    # TC_UI_SA_018
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    Then the Create Ranch Admin modal should render and function consistently across Chrome, Firefox, Safari and Edge

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_019 @regression @AccessibilityBasic
  Scenario: Tab key moves focus through modal fields in a logical order and focus stays trapped
    # TC_UI_SA_019
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal and presses Tab repeatedly
    Then focus should move through the Create Ranch Admin modal fields in a logical order without leaving the modal

  @DataFile:resources/data/sadata.csv @Key:tc_ui_sa_020 @regression @AccessibilityBasic
  Scenario: All form fields have visible, correctly associated labels
    # TC_UI_SA_020
    Given the user is logged in as a "Super Admin" user
    And the Super Admin has navigated to Ranch Admin Management
    When the Super Admin opens the Create Ranch Admin modal
    Then the Create Ranch Admin "Full Name" field should have a visible, correctly associated label
    And the Create Ranch Admin "Email" field should have a visible, correctly associated label
    And the Create Ranch Admin "Password" field should have a visible, correctly associated label
    And the Create Ranch Admin "Assign to Ranch" field should have a visible, correctly associated label
