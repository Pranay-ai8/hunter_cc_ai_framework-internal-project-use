@INVOICE @smoke
Feature: Invoicing — Smoke
  As a Ranch Admin / Super Admin
  I want to generate and view invoices for checked-out bookings
  So that guests can be billed correctly

  @DataFile:resources/data/invoicedata.csv @Key:invoice_generate_checkedout @smoke @P1
  Scenario: Generate invoice for a checked-out booking
    # TC_INVOICE_001
    Given User is logged in as "Ranch Admin"
    And a checked-out booking exists with no invoice, using the test data
    When I open the Generate Invoice page
    And I select the test data booking from the Select Booking list
    And I generate the invoice
    Then the test data booking should be tagged "Invoiced" with an invoice number

  @DataFile:resources/data/invoicedata.csv @Key:invoice_page_layout @smoke @P2
  Scenario: Generate Invoice page loads with all core sections visible
    # TC_UI_INVOICE_001
    Given User is logged in as "Ranch Admin"
    When I open the Generate Invoice page
    Then the Generate Invoice page should display the page title, tab control, Select Booking list and Invoice Preview panel
