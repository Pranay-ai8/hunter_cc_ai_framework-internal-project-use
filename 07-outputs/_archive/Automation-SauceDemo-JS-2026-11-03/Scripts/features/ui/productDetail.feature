Feature: Product Detail - SauceDemo
  As a logged-in SauceDemo user
  I want to view a product's detail page
  So that I can review it and add it to my cart

  # TC_PRODUCTDETAIL_001 | FR-12
  @regression @Key:TC_PRODUCTDETAIL_001
  Scenario: Clicking product name opens detail page
    Given user is logged in as "<Username>"
    When user clicks the product name link for "<Product1>"
    Then user is navigated to a product detail page matching "inventory-item.html?id="
    And the product detail page shows a description, price, and Add to Cart button

  # TC_PRODUCTDETAIL_002 | FR-12
  @regression @Key:TC_PRODUCTDETAIL_002
  Scenario: Clicking product image opens detail page
    Given user is logged in as "<Username>"
    When user clicks the product image for "<Product1>"
    Then user is navigated to a product detail page matching "inventory-item.html?id="
    And the product detail page shows a description, price, and Add to Cart button

  # TC_PRODUCTDETAIL_003 | FR-12
  @regression @Key:TC_PRODUCTDETAIL_003
  Scenario: Add to cart from product detail page updates badge
    Given user is logged in as "<Username>"
    And user clicks the product name link for "<Product1>"
    Then the cart badge is not visible
    When user adds the current product to cart from the detail page
    Then the detail-page cart badge shows count "1"

  # TC_PRODUCTDETAIL_004 | FR-13
  @regression @Key:TC_PRODUCTDETAIL_004
  Scenario: Back to Products returns to Inventory
    Given user is logged in as "<Username>"
    And user clicks the product name link for "<Product1>"
    When user clicks Back to Products
    Then user is redirected to the inventory page
    And exactly 6 products are visible on the inventory page
