@INVOICE @regression
Feature: Invoicing — Regression
  As a Ranch Admin / Super Admin
  I want invoices to generate, calculate, display and gate correctly
  So that billing data stays accurate and auditable

  # ---------- Invoice Auto-Population (TC_INVOICE_002-008) ----------

  @DataFile:resources/data/invoicedata.csv @Key:invoice_autopop_bookingid @regression @P2
  Scenario: Booking ID auto-populates correctly on invoice preview
    # TC_INVOICE_002
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview "Booking ID" field should display the test data booking ID

  @DataFile:resources/data/invoicedata.csv @Key:invoice_autopop_ranch @regression @P2
  Scenario: Ranch name auto-populates correctly on invoice preview
    # TC_INVOICE_003
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview "Ranch" field should display the test data ranch name

  @DataFile:resources/data/invoicedata.csv @Key:invoice_autopop_dates @regression @P2
  Scenario: Check-in/Check-out dates auto-populate correctly on invoice preview
    # TC_INVOICE_004
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview Check-in/Check-out field should display the test data dates

  @DataFile:resources/data/invoicedata.csv @Key:invoice_duration_multinight @regression @P2
  Scenario: Duration auto-calculates correctly for a multi-night stay
    # TC_INVOICE_005
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview Duration field should display the test data expected duration

  @DataFile:resources/data/invoicedata.csv @Key:invoice_duration_minnight @regression @P2
  Scenario: Duration auto-calculates correctly for the minimum 1-night stay
    # TC_INVOICE_006
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview Duration field should display the test data expected duration

  @DataFile:resources/data/invoicedata.csv @Key:invoice_autopop_billto @regression @P2
  Scenario: Bill To (customer) details auto-populate correctly
    # TC_INVOICE_007
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview "Bill To" field should display the test data hunter name

  @DataFile:resources/data/invoicedata.csv @Key:invoice_autopop_charges @regression @P2
  Scenario: Selected charges reflect accurately on invoice preview
    # TC_INVOICE_008
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview charges list should display exactly the test data charges

  # ---------- Invoice Calculation (TC_INVOICE_009-010, TAX RATE CONFIRMED 8.5% LIVE) ----------

  @DataFile:resources/data/invoicedata.csv @Key:invoice_total_multicharge @regression @P1
  Scenario: Total reflects sum of selected charges plus the confirmed 8.5% tax rate
    # TC_INVOICE_009 — reclassified automatable: live Dev Invoices list confirmed the Tax
    # column is consistently 8.5% of (Lodging+Harvest+Add-ons) across sampled rows
    # (see docs/dom-snapshots/invoice.json). Recommend FunctionalTestCases.md owner
    # reclassify Automation Candidate TBD -> YES.
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    And I select all available charges
    Then the invoice Total should equal the test data subtotal plus 8.5% tax

  @DataFile:resources/data/invoicedata.csv @Key:invoice_total_singlecharge @regression @P2
  Scenario: Total calculation boundary with only a single minimum charge selected
    # TC_INVOICE_010 — same tax-rate reclassification rationale as TC_INVOICE_009
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    And I select the single available charge
    Then the invoice Total should equal the test data subtotal plus 8.5% tax

  # ---------- Invoice Preview / Generation / Deletion (TC_INVOICE_011-012, 015-016) ----------

  @DataFile:resources/data/invoicedata.csv @Key:invoice_preview_predates_creation @regression @P2
  Scenario: Preview displays before the invoice is created
    # TC_INVOICE_011
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the invoice preview should render without an invoice having been created yet

  @DataFile:resources/data/invoicedata.csv @Key:invoice_notcheckedout_excluded @regression @P2
  Scenario: Booking not yet checked out is not available for invoice generation
    # TC_INVOICE_012
    Given User is logged in as "Ranch Admin"
    And a pending booking exists that has not been checked out, using the test data
    When I open the Generate Invoice page
    And I switch to the "Booking" tab
    Then the test data booking should not appear in the Select Booking list

  @DataFile:resources/data/invoicedata.csv @Key:invoice_delete @regression @P2
  Scenario: Delete (soft-delete/discard) a generated invoice
    # TC_INVOICE_015
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    And I delete the invoice and confirm
    Then the test data booking should no longer be tagged "Invoiced" and should be selectable again

  @DataFile:resources/data/invoicedata.csv @Key:invoice_discard_replace @regression @P1
  Scenario: Create a new invoice for the same booking after discard
    # TC_INVOICE_016
    Given User is logged in as "Ranch Admin"
    And the test data invoice for the test data booking has just been discarded
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    And I generate the invoice
    Then the test data booking should be tagged "Invoiced" with a new invoice number

  # ---------- One-Invoice-Per-Booking Rule & Gating UI (TC_INVOICE_017-023) ----------

  @DataFile:resources/data/invoicedata.csv @Key:invoice_block_second_booking @regression @P1
  Scenario: Second invoice for an already-invoiced booking is blocked
    # TC_INVOICE_017
    Given User is logged in as "Ranch Admin"
    And the test data booking already has an active invoice
    When I open the Generate Invoice page
    And I switch to the "Booking" tab
    Then the test data booking should appear grayed out with its invoiced tag and be non-selectable

  @DataFile:resources/data/invoicedata.csv @Key:invoice_block_second_harvest @regression @P1
  Scenario: Second invoice for an already-invoiced unlinked harvest is blocked
    # TC_INVOICE_018
    Given User is logged in as "Ranch Admin"
    And the test data unlinked harvest already has an active invoice
    When I open the Generate Invoice page
    And I switch to the "Harvests without booking" tab
    Then the test data harvest should appear grayed out with its invoiced tag and be non-selectable

  @DataFile:resources/data/invoicedata.csv @Key:invoice_tag_shows_correct_ids @regression @P2
  Scenario: Invoiced tag displays correct Booking ID and Invoice number
    # TC_INVOICE_019
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I generate an invoice for the test data booking
    Then the test data booking should show an Invoiced tag containing the test data invoice number

  @DataFile:resources/data/invoicedata.csv @Key:invoice_list_grays_out_invoiced @regression @P2
  Scenario: Select Booking list grays out already-invoiced bookings
    # TC_INVOICE_020
    Given User is logged in as "Ranch Admin"
    And the test data invoiced booking already has an active invoice
    And the test data open booking has no invoice
    When I open the Generate Invoice page
    Then the test data invoiced booking should be grayed out and the test data open booking should remain selectable

  @DataFile:resources/data/invoicedata.csv @Key:invoice_harvest_tab_gating @regression @P1
  Scenario: "Harvests without booking" tab enforces the same one-invoice rule and tag/gating behavior
    # TC_INVOICE_021
    Given User is logged in as "Ranch Admin"
    And the test data invoiced harvest already has an active invoice
    And the test data open harvest has no invoice
    When I open the Generate Invoice page
    And I switch to the "Harvests without booking" tab
    Then the test data invoiced harvest should be grayed out and the test data open harvest should remain selectable

  @DataFile:resources/data/invoicedata.csv @Key:invoice_live_tag_update @regression @P3
  Scenario: Invoiced tag updates live without a hard refresh
    # TC_INVOICE_022
    Given User is logged in as "Ranch Admin"
    And the test data booking is not yet invoiced, visible in the Select Booking list
    When an invoice is generated for the test data booking in a second session
    Then the Select Booking list should show the test data booking as Invoiced without a page reload

  @DataFile:resources/data/invoicedata.csv @Key:invoice_preview_empty_state @regression @P3
  Scenario: Empty state displays when no booking/harvest is selected
    # TC_INVOICE_023
    Given User is logged in as "Ranch Admin"
    When I open the Generate Invoice page
    And I make no selection on either tab
    Then the Invoice Preview panel should display an empty state message

  # ---------- Two-Tab Navigation (TC_UI_INVOICE_003-005) ----------

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_booking_tab_default @regression @P2
  Scenario: "Booking" tab is active by default on page load
    # TC_UI_INVOICE_003
    Given User is logged in as "Ranch Admin"
    When I open the Generate Invoice page
    Then the "Booking" tab should be the active tab

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_switch_to_harvest_tab @regression @P2
  Scenario: Clicking "Harvests without booking" tab switches the active tab and displayed list
    # TC_UI_INVOICE_004
    Given User is logged in as "Ranch Admin"
    And I am on the Generate Invoice page with the "Booking" tab active
    When I switch to the "Harvests without booking" tab
    Then the "Harvests without booking" tab should be the active tab

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_switch_back_to_booking_tab @regression @P3
  Scenario: Switching back to "Booking" tab restores its list content
    # TC_UI_INVOICE_005
    Given User is logged in as "Ranch Admin"
    And I am on the Generate Invoice page with the "Booking" tab active
    When I switch to the "Harvests without booking" tab
    And I switch to the "Booking" tab
    Then the "Booking" tab should be the active tab

  # ---------- Invoice Preview Layout & Download/Delete Controls (TC_UI_INVOICE_002, 006-011) ----------

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_preview_labels @regression @P2
  Scenario: Invoice Preview panel renders all expected field labels
    # TC_UI_INVOICE_002
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then the Invoice Preview panel should show labeled fields for Booking ID, Ranch, dates, Duration, Bill To, Charges, Tax and Total

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_pdf_button_visible @regression @P2
  Scenario: PDF download button is visible with correct label/icon
    # TC_UI_INVOICE_006
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    Then the download controls should be visible

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_pdf_download_triggers @regression @P2
  Scenario: Clicking Download PDF triggers a file download
    # TC_UI_INVOICE_007
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    And I download the invoice as PDF
    Then a PDF file download should have been triggered

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_excel_button_visible @regression @P2
  Scenario: Excel download button is visible with correct label/icon
    # TC_UI_INVOICE_008
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    Then the download controls should be visible

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_excel_download_triggers @regression @P2
  Scenario: Clicking Download Excel triggers a file download
    # TC_UI_INVOICE_009
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    And I download the invoice as Excel
    Then an Excel file download should have been triggered

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_delete_opens_dialog @regression @P2
  Scenario: Delete/Discard button is visible and opens a confirmation dialog
    # TC_UI_INVOICE_010
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    And I click Delete/Discard
    Then a confirmation dialog should be visible

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_delete_cancel @regression @P3
  Scenario: Confirmation dialog closes via Cancel without performing the delete
    # TC_UI_INVOICE_011
    Given User is logged in as "Ranch Admin"
    And a generated invoice exists for the test data booking
    When I open the test data invoice
    And I click Delete/Discard
    And I cancel the delete confirmation
    Then the confirmation dialog should be closed and the invoice unchanged

  # ---------- Invoiced Tag & Row Styling (TC_UI_INVOICE_012-014) ----------

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_badge_rendering @regression @P3
  Scenario: "Invoiced" tag renders as a distinct badge element on the Select Booking list
    # TC_UI_INVOICE_012
    Given User is logged in as "Ranch Admin"
    And the test data booking already has an active invoice
    When I open the Generate Invoice page
    Then the test data booking should display a distinct Invoiced badge containing its invoice number

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_disabled_row_styling @regression @P3
  Scenario: Invoiced rows render with a non-interactive/disabled visual state
    # TC_UI_INVOICE_013
    Given User is logged in as "Ranch Admin"
    And the test data invoiced booking already has an active invoice
    And the test data open booking has no invoice
    When I open the Generate Invoice page
    Then the test data invoiced booking should carry a disabled DOM state distinct from the test data open booking

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_harvest_tab_styling @regression @P3
  Scenario: Tag and disabled-row styling render consistently on "Harvests without booking" tab
    # TC_UI_INVOICE_014
    Given User is logged in as "Ranch Admin"
    And the test data unlinked harvest already has an active invoice
    When I open the Generate Invoice page
    And I switch to the "Harvests without booking" tab
    Then the test data harvest should display the same Invoiced badge and disabled-row treatment as the Booking tab

  # ---------- Responsive, Cross-Browser, Accessibility (TC_UI_INVOICE_015-019) ----------

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_mobile_viewport @regression @P2
  Scenario: Generate Invoice page remains usable at mobile viewport (375px)
    # TC_UI_INVOICE_015
    Given User is logged in as "Ranch Admin"
    When I set the viewport to 375 by 812
    And I open the Generate Invoice page
    Then all sections should remain visible with no horizontal overflow

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_desktop_viewport @regression @P3
  Scenario: Generate Invoice page renders correctly at desktop viewport (1280px)
    # TC_UI_INVOICE_016
    Given User is logged in as "Ranch Admin"
    When I set the viewport to 1280 by 800
    And I open the Generate Invoice page
    Then tabs, Select Booking list and Invoice Preview panel should render side-by-side with no overlap

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_cross_browser @regression @P3
  Scenario: Generate Invoice page renders and functions consistently across supported browsers
    # TC_UI_INVOICE_017
    Given User is logged in as "Ranch Admin"
    When I open the Generate Invoice page
    Then the page-load, tab-switch and download-trigger checks should pass on this browser project

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_keyboard_focus @regression @P3
  Scenario: Tab controls and download buttons are keyboard-reachable with visible focus
    # TC_UI_INVOICE_018
    Given User is logged in as "Ranch Admin"
    When I open the Generate Invoice page
    And I press Tab repeatedly from page load
    Then each control should receive focus in order with a visible focus indicator

  @DataFile:resources/data/invoicedata.csv @Key:ui_invoice_accessible_labels @regression @P3
  Scenario: Invoice preview fields and Select Booking list expose accessible labels
    # TC_UI_INVOICE_019
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    Then each invoice preview field and Select Booking row should expose an accessible name

  # ---------- Generic: Duplicate-Submission Prevention & Cross-OS (TC_GEN_INVOICE_001-002) ----------

  @DataFile:resources/data/invoicedata.csv @Key:gen_invoice_rapid_duplicate_prevention @regression @P1
  Scenario: Rapidly clicking "Generate Invoice" does not create more than one invoice
    # TC_GEN_INVOICE_001
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    And I click Generate Invoice 5 times rapidly
    Then exactly one invoice should exist for the test data booking and it should be tagged Invoiced

  @DataFile:resources/data/invoicedata.csv @Key:gen_invoice_cross_os @regression @P2
  Scenario: Invoice generation, preview, and PDF/Excel download complete successfully on each confirmed OS
    # TC_GEN_INVOICE_002
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I generate an invoice for the test data booking
    And I download the invoice as PDF
    And I download the invoice as Excel
    Then both downloads should have been triggered successfully on this OS runner
