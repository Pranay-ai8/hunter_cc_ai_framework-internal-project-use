@product-detail
Feature: Product Detail

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in as "standard_user"

  # TC_PRODUCTDETAIL_001
  @regression
  Scenario: Clicking product name opens the detail page
    When the user opens the first product's detail page by name
    Then the product detail page is displayed

  # TC_PRODUCTDETAIL_003
  @regression
  Scenario: Add to cart from product detail page updates the badge
    When the user opens the first product's detail page by name
    And the user adds the product to cart from the detail page
    Then the cart badge shows "1"

  # TC_PRODUCTDETAIL_004
  @regression
  Scenario: Back to Products returns to inventory
    When the user opens the first product's detail page by name
    And the user clicks Back to Products
    Then exactly 6 products are visible
