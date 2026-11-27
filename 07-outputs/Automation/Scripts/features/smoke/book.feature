Feature: Booking Management — Smoke
  As a Ranch Admin / Super Admin
  I want the core booking creation and listing flows to work
  So that the critical booking path is healthy before every deeper regression run

  @DataFile:resources/data/bookdata.csv @Key:book_create_mandatory_only @smoke @P1
  Scenario: Create a booking with all mandatory fields populated and no optional data
    # TC_BOOK_001
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    And I select booking type "td.bookingType"
    And I select ranch "td.ranchName" with check-in "td.checkInDate" and check-out "td.checkOutDate"
    And I proceed to the Accommodation step
    And I select room "td.roomName"
    And I proceed to the Party Details step
    And I add a hunter with name "td.hunterName", email "td.hunterEmail" and address "td.hunterAddress", "td.hunterCity", "td.hunterState", "td.hunterZip"
    And I proceed to the Assignment step
    And I proceed to the Review step
    And I submit the booking
    Then the booking should be created successfully with a generated Booking ID
    And the booking should appear in the All Bookings list

  @DataFile:resources/data/bookdata.csv @Key:book_unique_id_and_listing @smoke @P1
  Scenario: Verify a unique Booking ID is generated and the booking is listed correctly after successful creation
    # TC_BOOK_014
    Given User is logged in as "td.role"
    And a valid new booking is submitted for ranch "td.ranchName" with hunter "td.hunterName"
    When I navigate to the Bookings list
    And I search for the created booking by Booking ID
    Then the booking should be found with its correct data and status displayed

  @DataFile:resources/data/bookdata.csv @Key:ui_book_new_booking_form_structure @smoke @P2
  Scenario: Booking - New Booking Form - Page structure loads with all sections visible
    # TC_UI_BOOK_001
    Given User is logged in as "td.role"
    When I open the New Booking wizard
    Then the New Booking form should display a descriptive title
    And the Ranch, Dates, Accommodation, Party Details, Assignment and Review sections should all be reachable

  @DataFile:resources/data/bookdata.csv @Key:ui_book_all_bookings_structure @smoke @P2
  Scenario: Booking - All Bookings List - Page structure loads with table and columns visible
    # TC_UI_BOOK_002
    Given User is logged in as "td.role"
    When I navigate to the Bookings list
    Then the Bookings list should display a descriptive title
    And the bookings table should be visible with its column headers rendered

  @DataFile:resources/data/bookdata.csv @Key:ui_book_new_request_button_opens_form @smoke @P2
  Scenario: Booking - Calendar Navigation - "+ New Booking Request" button always opens the request form
    # TC_UI_BOOK_007
    Given Guide user is logged in
    And I am viewing the Guide Booking Request calendar
    When I click the "+ New Booking Request" button
    Then the New Booking Request form should open
