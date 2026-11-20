@regression @invoice
Feature: Invoice Management Module
  As an admin user
  I want to navigate and interact with the Invoice Management module
  So that I can search, download, discard, and manage booking invoices

  @Key:IVC001 @High @Type:Navigation
  Scenario: Verify navigation to Invoice Management module
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then User is navigated to 'Invoice Management' page with title, subtitle 'View and manage booking invoices', and '+ Generate New Invoice' button visible

  @Key:IVC002 @Low @Type:UI
  Scenario: Verify default view and UI elements on Invoice Management page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then Page displays search bar, List/Grid view toggles, '+ Generate New Invoice' button, and main invoices table
    And Default view on Invoices page is 'List View' highlighted green

  @Key:IVC003 @Low @Type:UI
  Scenario: Verify List View column headers on Invoice Management page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then Table column headers display Customer, Invoice, Booking, Date, Lodging, Harvest, Add-ons, Tax, Total, Actions

  @Key:IVC004 @Medium @Type:Functional
  Scenario: Verify Search by exact Invoice Number
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User types search string "<searchString>" in the invoice search bar
    Then Invoices table filters instantly to display ONLY the exact invoice ID "<invoiceId>"

  @Key:IVC005 @Medium @Type:Functional
  Scenario: Verify Search by Customer Name
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User types search string "<searchString>" in the invoice search bar
    Then Invoices table filters to show invoices associated with "<customerName>"

  @Key:IVC006 @Medium @Type:Functional
  Scenario: Verify Search functionality is case-insensitive
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User types search string "<searchString>" in the invoice search bar
    Then Invoices table successfully filters and returns "<invoiceId>" regardless of character casing

  @Key:IVC007 @Medium @Type:Functional
  Scenario: Verify Search with non-existent keyword
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User types search string "<searchString>" in the invoice search bar
    Then Invoices table displays an empty state message

  @Key:IVC008 @Medium @Type:Functional
  Scenario: Verify clearing Search bar restores full list
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User types search string "<searchString>" in the invoice search bar
    And User clears the invoice search bar
    Then Full list of invoices is restored in the table

  @Key:IVC009 @Medium @Type:UI
  Scenario: Verify switching layout from List View to Grid View
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User switches layout to "grid" view
    Then Layout switches from table rows to individual invoice cards highlighted green

  @Key:IVC010 @Medium @Type:UI
  Scenario: Verify quick action buttons on Grid View invoice card
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User switches layout to "grid" view
    Then Invoice card displays quick action buttons "View", "Excel", "PDF" on card "<invoiceId>"

  @Key:IVC011 @Medium @Type:UI
  Scenario: Verify switching layout from Grid View back to List View
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User switches layout to "grid" view
    And User switches layout to "list" view
    Then Layout switches back from cards to structured table format highlighted green

  @Key:IVC012 @Low @Type:UI
  Scenario: Verify per-page pagination dropdown options
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks pagination per-page dropdown on Invoices page
    Then Dropdown opens revealing options 10, 20, 50
    When User selects per-page option "20"
    Then Invoices pagination summary text recalculates accurately

  @Key:IVC013 @Medium @Type:UI
  Scenario: Verify Actions menu options on an Invoice row in List View
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    Then Invoices popover menu opens displaying View Invoice, Download Excel, Download PDF, and Delete Invoice

  @Key:IVC014 @High @Type:Functional
  Scenario: Verify 'Download Excel' action from Actions menu in List View
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "Download Excel" from Invoices Actions menu
    Then A toast notification "Invoice Excel downloaded successfully." appears on Invoices page

  @Key:IVC015 @High @Type:Functional
  Scenario: Verify 'Download PDF' action from Actions menu in List View
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "Download PDF" from Invoices Actions menu
    Then A toast notification "Invoice PDF downloaded successfully." appears on Invoices page

  @Key:IVC016 @Medium @Type:Functional
  Scenario: Verify 'Download Excel' quick action button from Grid View card
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User switches layout to "grid" view
    And User clicks quick action button "Excel" on card "<invoiceId>"
    Then A toast notification "Invoice Excel downloaded successfully." appears on Invoices page

  @Key:IVC017 @Medium @Type:Functional
  Scenario: Verify 'Download PDF' quick action button from Grid View card
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User switches layout to "grid" view
    And User clicks quick action button "PDF" on card "<invoiceId>"
    Then A toast notification "Invoice PDF downloaded successfully." appears on Invoices page

  @Key:IVC018 @High @Type:Navigation
  Scenario: Verify navigating to Invoice Details view via 'View Invoice' option
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "View Invoice" from Invoices Actions menu
    Then User is navigated to detailed Invoice view page displaying action buttons and charges

  @Key:IVC019 @Medium @Type:UI
  Scenario: Verify layout and pre-filled metadata on Invoice Details page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "View Invoice" from Invoices Actions menu
    Then Invoice Details page displays Bill To and Summary cards for "<customerName>" and total "<totalAmount>"

  @Key:IVC020 @High @Type:Functional
  Scenario: Verify 'Download Excel' action from Detailed Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "View Invoice" from Invoices Actions menu
    And User clicks "Download Excel" button in Invoice Details top action bar
    Then A toast notification "Invoice Excel downloaded successfully." appears on Invoices page

  @Key:IVC021 @High @Type:Functional
  Scenario: Verify 'Download PDF' action from Detailed Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "View Invoice" from Invoices Actions menu
    And User clicks "Download PDF" button in Invoice Details top action bar
    Then A toast notification "Invoice PDF downloaded successfully." appears on Invoices page

  @Key:IVC022 @Medium @Type:Navigation
  Scenario: Verify '< Back to Invoices' link on Detailed Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "View Invoice" from Invoices Actions menu
    And User clicks '< Back to Invoices' link on Detailed Invoice page
    Then User is navigated back to 'Invoice Management' page with title, subtitle 'View and manage booking invoices', and '+ Generate New Invoice' button visible

  @Key:IVC023 @High @Type:Functional
  Scenario: Verify 'Discard Invoice' button functionality on Detailed Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "View Invoice" from Invoices Actions menu
    And User clicks "Discard Invoice" button in Invoice Details top action bar
    And User confirms deletion in modal dialog
    Then Invoice is discarded and user is redirected to Invoice Management list page

  @Key:IVC024 @High @Type:Functional
  Scenario: Verify 'Delete Invoice' option from List View Actions menu
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks Actions menu on invoice row "<invoiceId>"
    And User selects option "Delete Invoice" from Invoices Actions menu
    And User confirms deletion in modal dialog
    Then Invoice "<invoiceId>" is deleted from the table list

  @Key:IVC025 @High @Type:Navigation
  Scenario: Verify navigation to 'Generate Invoice' page via '+ Generate New Invoice' button
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    Then User is navigated to 'Generate Invoice' page displaying header title, subtitle 'Create invoices for completed bookings', '< Back to Invoices' link, 'Select Booking' card, and empty state 'Invoice Preview' card

  @Key:IVC026 @Low @Type:UI
  Scenario: Verify initial Empty State preview on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    Then 'Invoice Preview' card displays placeholder text 'Select a booking to generate invoice'

  @Key:IVC027 @Medium @Type:UI
  Scenario: Verify 'Checked-Out Booking *' dropdown options on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User opens 'Checked-Out Booking' dropdown menu
    Then Dropdown opens listing completed bookings

  @Key:IVC028 @High @Type:Functional
  Scenario: Verify dynamic Invoice Preview rendering upon selecting a checked-out booking
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    Then 'Invoice Preview' card dynamically loads Ranch header, Invoice Type, Date, Bill To, Booking Details, Charges itemized list, Subtotal, Tax, and Total

  @Key:IVC029 @High @Type:Functional
  Scenario: Verify auto-population of Bill To fields upon selecting a booking
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    Then Bill To fields Name, Email, Phone, Address, City, State, and Zip Code are automatically populated

  @Key:IVC030 @Medium @Type:Functional
  Scenario: Verify editing Bill To fields on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User edits Name field to "Updated Name" and Phone field to "+1 555-9999"
    Then Bill To fields reflect updated values in real-time inside the 'Invoice Preview' card

  @Key:IVC031 @High @Type:Functional
  Scenario: Verify Charges section checkboxes and dynamic subtotal recalculation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User checks add-on checkboxes for "Airport Pickup" and "Trophy Processing"
    Then Checking add-ons dynamically recalculates Subtotal, Tax, and Total in both Charges summary and Invoice Preview card

  @Key:IVC032 @High @Type:Validation
  Scenario: Verify unchecking all charges prevents invoice creation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User unchecks all checkboxes in Charges section
    Then Subtotal becomes $0.00, Tax becomes $0.00, Total becomes $0.00, and 'Create Invoice' button is disabled

  @Key:IVC033 @Medium @Type:Functional
  Scenario: Verify Adjustments section: Tax Rate (%) numeric input and recalculation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User edits Tax Rate field to "9.38"
    Then Tax amount and Total automatically recalculate in real-time in both Summary and Invoice Preview card

  @Key:IVC034 @Medium @Type:Boundary
  Scenario: Verify Adjustments section: Tax Rate (%) decimal boundary testing
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User edits Tax Rate field to "-5"
    Then Field rejects non-numeric input or displays validation error indicating tax percentage must be positive

  @Key:IVC035 @High @Type:Functional
  Scenario: Verify Adjustments section: Discount ($) numeric input and recalculation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User enters a valid discount dollar amount "10" into Discount field
    Then Discount amount is subtracted from Subtotal, Tax recalculates, and Total updates dynamically

  @Key:IVC036 @High @Type:Validation
  Scenario: Verify Discount ($) exceeding Subtotal validation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User enters a Discount value "300" greater than the total Subtotal
    Then System blocks submission or caps discount to Subtotal value

  @Key:IVC037 @Medium @Type:Functional
  Scenario: Verify Optional 'Discount Reason' text field
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User enters a valid discount dollar amount "10" into Discount field
    And User enters "VIP Guest Promo" into Discount Reason field
    Then Discount Reason field accepts text

  @Key:IVC038 @High @Type:Functional
  Scenario: Verify successful Invoice Creation via 'Create Invoice' button
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User clicks green 'Create Invoice' button
    Then Invoice is created and user is redirected to 'Invoice Management' list page
    And A toast notification "Invoice Created" appears on Invoices page

  @Key:IVC039 @High @Type:ErrorHandling
  Scenario: Verify Duplicate Invoice creation restriction for same Booking ID
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects an already invoiced booking ID
    And User clicks green 'Create Invoice' button
    Then Backend rejects creation and red error toast alert appears

  @Key:IVC040 @Medium @Type:Navigation
  Scenario: Verify '< Back to Invoices' link on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User clicks '< Back to Invoices' link at top left
    Then Invoice generation is cancelled and user returns to Invoice Management list page

  @Key:IVC041 @Medium @Type:Validation
  Scenario: Verify Bill To Email field format validation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User types invalid email string 'hunterqdr' into Bill To Email field
    Then Red inline validation error appears under Email field

  @Key:IVC042 @Medium @Type:Validation
  Scenario: Verify Bill To mandatory fields validation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User clears mandatory Bill To fields
    And User clicks green 'Create Invoice' button
    Then Inline validation errors appear under empty mandatory fields and submission is blocked

  @Key:IVC043 @Medium @Type:Boundary
  Scenario: Verify ZIP Code numeric and length validation in Bill To section
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User enters non-numeric or short ZIP code 'abc' into Bill To section
    Then Field rejects non-numeric input or flags inline validation error

  @Key:IVC044 @Medium @Type:UI
  Scenario: Verify Phone Number international formatting in Bill To section
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects US country code and enters valid phone digits '+1 (555) 000-0002'
    Then Phone field formats international phone number correctly

  @Key:IVC045 @Medium @Type:Functional
  Scenario: Verify 'Create Invoice' button loading state during submission
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User selects completed booking "BK-2026-1006" from dropdown
    And User clicks green 'Create Invoice' button
    Then Invoice create button visually switches to disabled or loading state

  @Key:IVC046 @Low @Type:UI
  Scenario: Verify Toast Alert notification rendering and auto-dismissal
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User triggers an export action on Invoices page
    Then Toast notification banner renders at bottom right and auto-dismisses smoothly

  @Key:IVC049 @Medium @Type:Accessibility
  Scenario: Verify keyboard Tab key accessibility across Invoice Management page controls
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User presses Tab key repeatedly across invoice table controls
    Then Keyboard focus moves sequentially across search, view toggles, and action buttons

  @Key:IVC050 @Medium @Type:Accessibility
  Scenario: Verify keyboard Tab key accessibility on Generate Invoice form
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User presses Tab key repeatedly across Generate Invoice form
    Then Focus moves sequentially through Booking dropdown, Bill To fields, and submission button

  @Key:IVC051 @Medium @Type:Usability
  Scenario: Verify Cross-Browser Compatibility for Invoice module
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then Invoice Management page UI elements and layout render properly

  @Key:IVC056 @Low @Type:UI
  Scenario: Verify currency formatting consistency across all Invoice screens
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then All monetary values display standard currency format with dollar sign and two decimal places

  @Key:IVC057 @Medium @Type:Boundary
  Scenario: Verify rounding logic on Tax and Total calculations
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User sets Tax Rate to '8.5%' on subtotal '$250.00'
    Then Calculated Tax displays '$21.25' and Total displays '$271.25' rounded to 2 decimal places

  @Key:IVC058 @High @Type:StateTransitions
  Scenario: Verify Invoice state transition from Booking Checkout to Invoiced state
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then Invoiced bookings reflect completed invoice status in system

  @Key:IVC059 @High @Type:StateTransitions
  Scenario: Verify deleting an invoice restores booking to 'Un-invoiced' state
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User deletes an existing invoice record
    Then Associated booking becomes available again in Checked-Out Booking dropdown

  @Key:IVC061 @Medium @Type:Validation
  Scenario: Verify leading and trailing spaces in text fields are trimmed on Invoice Preview
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User enters Bill To Name with leading and trailing spaces ' Test Hunter '
    Then Invoice Preview card displays clean trimmed text 'Test Hunter'

  @Key:IVC062 @Medium @Type:UI
  Scenario: Verify sidebar menu links visibility and active highlight
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    Then Left sidebar menu renders correctly and 'Invoices' link is highlighted green

  @Key:IVC063 @Medium @Type:Navigation
  Scenario: Verify breadcrumb trail and back navigation on Generate Invoice page
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User clicks on the '+ Generate New Invoice' action button
    And User clicks '< Back to Invoices' breadcrumb link
    Then User is redirected back to main Invoice Management table list page

  @Key:IVC064 @High @Type:Workflow
  Scenario: Verify browser Back button behavior after invoice creation
    Given User is logged in as Super Admin
    When User clicks on 'Invoices' under RECORD KEEPING in the left sidebar
    And User completes invoice creation
    And User clicks browser 'Back' button on invoice creation
    Then System retains user safely on list view page without duplicate resubmission

