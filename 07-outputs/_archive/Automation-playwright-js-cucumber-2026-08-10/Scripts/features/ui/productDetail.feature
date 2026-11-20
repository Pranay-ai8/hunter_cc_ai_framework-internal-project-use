Feature: SauceDemo - Product Detail

  Background:
    Given the user is logged in as "<Username>" with password "<Password>"

  # TC_PRODUCTDETAIL_001
  @Key:StandardUser @regression
  Scenario: Clicking product name opens Detail page
    When the user opens the first product by its name
    Then the Product Detail page should be visible

  # TC_PRODUCTDETAIL_002
  @Key:StandardUser @regression
  Scenario: Clicking product image opens Detail page
    When the user opens the first product by its image
    Then the Product Detail page should be visible

  # TC_PRODUCTDETAIL_003
  @Key:StandardUser @regression
  Scenario: Add to Cart from Detail page updates badge
    When the user opens the first product by its name
    And the user adds the current product to the cart from the Detail page
    Then the cart badge should be visible

  # TC_PRODUCTDETAIL_004
  @Key:StandardUser @regression
  Scenario: Back to Products returns to Inventory
    When the user opens the first product by its name
    And the user navigates back to products from the Detail page
    Then the Inventory page should display exactly 6 products
