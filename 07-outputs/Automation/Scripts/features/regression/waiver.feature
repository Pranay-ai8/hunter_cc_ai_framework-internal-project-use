@WAIVER @regression
Feature: Waivers & Document Workflow — Regression
  As a Ranch Admin / Super Admin
  I want waiver dispatch, signing status and resend behavior to work correctly
  So that liability compliance is enforced for every guest

  # ---------- Waiver Dispatch (TC_WAIVER_002-004) ----------

  @DataFile:resources/data/waiverdata.csv @Key:waiver_dispatch_multihunter @regression @P1
  Scenario: Waiver is dispatched to every listed hunter on a multi-hunter booking
    # TC_WAIVER_002
    Given User is logged in as "Ranch Admin"
    When I create a new booking listing the test data hunters
    Then both test data hunters should show a dispatched waiver status

  @DataFile:resources/data/waiverdata.csv @Key:waiver_dispatch_uses_ranch_template @regression @P1
  Scenario: Waiver dispatch uses the ranch's configured Docuseal template
    # TC_WAIVER_003
    Given User is logged in as "Ranch Admin"
    And the test data ranch has the test data Docuseal template configured
    When I create a new booking for the test data hunter at the test data ranch
    Then the dispatched waiver for the test data hunter should reference the test data template

  @DataFile:resources/data/waiverdata.csv @Key:waiver_dispatch_crossranch_templates @regression @P2
  Scenario: Bookings at different ranches each dispatch their own correct configured template
    # TC_WAIVER_004
    Given User is logged in as "Ranch Admin"
    And ranch A and ranch B each have their own test data Docuseal template configured
    When I create a booking at ranch A and a booking at ranch B, using the test data hunters
    Then each ranch's dispatched waiver should reference that ranch's own configured template

  # ---------- Signed Document Display (TC_WAIVER_005-007) ----------

  @DataFile:resources/data/waiverdata.csv @Key:waiver_signed_document_displays @regression @P1
  Scenario: Signed waiver eventually displays in the UI after the hunter completes e-signature
    # TC_WAIVER_005 — Docuseal signing occurs via an emailed link outside the app; QA
    # mailbox/API access to retrieve and follow that link is confirmed available
    # (GapAnalysis.md RESOLVED). Exact arrival latency not asserted — GAP-BR-02 open.
    Given User is logged in as "Ranch Admin"
    And a waiver has been dispatched to the test data hunter
    When the test data hunter completes the e-signature via the emailed Docuseal link
    Then the signed document should eventually display on the test data hunter's record

  @DataFile:resources/data/waiverdata.csv @Key:waiver_pending_status_before_signature @regression @P2
  Scenario: Waiver status reflects unsigned/pending state prior to signature completion
    # TC_WAIVER_006
    Given User is logged in as "Ranch Admin"
    And a waiver has been dispatched to the test data hunter and not yet signed
    When I open the Guest Details page for the test data hunter
    Then the waiver status should display "Pending Signature" with no signed document shown

  @DataFile:resources/data/waiverdata.csv @Key:waiver_template_configuration @regression @P2
  Scenario: Ranch Admin/Super Admin selects the ranch's Docuseal template from the UI
    # TC_WAIVER_007
    Given User is logged in as "Super Admin"
    And I am editing the test data ranch configuration
    When I configure the Docuseal template as the test data template
    Then the test data ranch's configured Docuseal template should persist as the test data template

  # ---------- Waiver Resend (TC_WAIVER_008-011) ----------

  @DataFile:resources/data/waiverdata.csv @Key:waiver_resend_prechecking @regression @P2
  Scenario: "Send Waiver" button resends the waiver prior to check-in
    # TC_WAIVER_008
    Given User is logged in as "Ranch Admin"
    And the test data hunter's waiver has already been dispatched, prior to check-in
    When I open the Guest Details page for the test data hunter
    And I click Send Waiver
    Then the waiver should be resent successfully with a new dispatch event

  @DataFile:resources/data/waiverdata.csv @Key:waiver_resend_postcheckin @regression @P1
  Scenario: "Send Waiver" button successfully resends the waiver after check-in
    # TC_WAIVER_009
    Given User is logged in as "Ranch Admin"
    And the test data hunter has already checked in
    When I open the Guest Details page for the test data hunter
    And I click Send Waiver
    Then the waiver should be resent successfully even though the hunter has already checked in

  @DataFile:resources/data/waiverdata.csv @Key:waiver_resend_already_signed_no_error @regression @P3
  Scenario: Resending a waiver for a guest whose waiver is already signed does not error
    # TC_WAIVER_010
    Given User is logged in as "Ranch Admin"
    And the test data guest's waiver status is "Signed"
    When I open the Guest Details page for the test data guest
    And I click Send Waiver
    Then no system error should occur and the waiver status should remain consistent

  @DataFile:resources/data/waiverdata.csv @Key:waiver_resend_rapid_clicks @regression @P3
  Scenario: Multiple rapid "Send Waiver" clicks do not cause duplicate/erroneous dispatch failures
    # TC_WAIVER_011
    Given User is logged in as "Ranch Admin"
    And the test data hunter's waiver has been dispatched
    When I open the Guest Details page for the test data hunter
    And I click Send Waiver 3 times in rapid succession
    Then the system should handle the repeated clicks gracefully without an error state

  # ---------- Non-Hunter Guest Waiver (TC_WAIVER_012-013) ----------

  @DataFile:resources/data/waiverdata.csv @Key:waiver_nonhunter_guest_mandatory @regression @P1
  Scenario: Non-hunter guest listed on a booking receives mandatory waiver dispatch same as hunters
    # TC_WAIVER_012
    Given User is logged in as "Ranch Admin"
    When I create a new booking listing the test data hunter and non-hunter guest
    Then both the test data hunter and the test data guest should show a dispatched waiver status

  @DataFile:resources/data/waiverdata.csv @Key:waiver_onlyguests_mandatory @regression @P2
  Scenario: Booking with only non-hunter guests (no hunters) still dispatches mandatory waivers to all guests
    # TC_WAIVER_013
    Given User is logged in as "Ranch Admin"
    When I create a new booking listing only the test data non-hunter guests
    Then both test data guests should show a dispatched waiver status

  # ---------- UI: Docuseal Template Dropdown (TC_UI_WAIVER_002-003) ----------

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_template_dropdown_visible @regression @P3
  Scenario: Docuseal Template dropdown is visible with a label
    # TC_UI_WAIVER_002
    Given User is logged in as "Super Admin"
    And I am editing the test data ranch configuration at the Initial Settings step
    Then the Docuseal Template dropdown should be visible with a descriptive label

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_template_dropdown_lists_options @regression @P3
  Scenario: Docuseal Template dropdown opens and lists available templates
    # TC_UI_WAIVER_003
    Given User is logged in as "Super Admin"
    And I am editing the test data ranch configuration at the Initial Settings step
    When I open the Docuseal Template dropdown
    Then the dropdown should display the available templates as selectable options

  # ---------- UI: Send Waiver Button State (TC_UI_WAIVER_004-005) ----------

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_sendbutton_precheckin @regression @P2
  Scenario: Send Waiver button is visible and clickable before check-in
    # TC_UI_WAIVER_004
    Given User is logged in as "Ranch Admin"
    And the test data guest has not yet checked in
    When I open the Guest Details page for the test data guest
    Then the Send Waiver button should be visible and enabled

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_sendbutton_postcheckin @regression @P2
  Scenario: Send Waiver button remains visible and clickable after check-in
    # TC_UI_WAIVER_005
    Given User is logged in as "Ranch Admin"
    And the test data guest has already checked in
    When I open the Guest Details page for the test data guest
    Then the Send Waiver button should be visible and enabled

  # ---------- UI: Notifications (TC_UI_WAIVER_006) ----------

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_send_success_toast @regression @P3
  Scenario: Send Waiver click shows a success toast
    # TC_UI_WAIVER_006
    Given User is logged in as "Ranch Admin"
    And I am on the Guest Details page for the test data guest
    When I click Send Waiver
    Then a success toast should render in the standard notification area

  # ---------- UI: Responsive, Cross-Browser, Accessibility (TC_UI_WAIVER_008-011) ----------

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_mobile_viewport @regression @P3
  Scenario: Waiver section renders usably at mobile viewport
    # TC_UI_WAIVER_008
    Given User is logged in as "Ranch Admin"
    When I set the viewport to 375 by 667
    And I open the Guest Details page for the test data guest
    Then the waiver status indicator and Send Waiver button should be visible with no horizontal overflow

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_desktop_viewport @regression @P3
  Scenario: Docuseal Template step renders correctly at desktop viewport
    # TC_UI_WAIVER_009
    Given User is logged in as "Super Admin"
    When I set the viewport to 1280 by 800
    And I am editing the test data ranch configuration at the Initial Settings step
    Then the Docuseal Template dropdown and label should render fully visible with no truncation

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_cross_browser @regression @P3
  Scenario: Guest Details Waiver section and Template dropdown render consistently across browsers
    # TC_UI_WAIVER_010
    Given User is logged in as "Ranch Admin"
    And I am on the Guest Details page for the test data guest
    And I am editing the test data ranch configuration at the Initial Settings step
    Then layout and interactive controls should render and function consistently on this browser project

  @DataFile:resources/data/waiverdata.csv @Key:ui_waiver_keyboard_accessibility @regression @P3
  Scenario: Send Waiver button, Template dropdown and signed-document link are keyboard accessible with descriptive names
    # TC_UI_WAIVER_011
    Given User is logged in as "Ranch Admin"
    And I am on the Guest Details page for the test data guest
    When I press Tab to reach the Send Waiver button and the Docuseal Template dropdown
    Then both controls should be reachable via Tab with a visible focus indicator and the signed-document link should have a descriptive accessible name
