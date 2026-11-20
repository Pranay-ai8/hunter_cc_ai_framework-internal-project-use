Feature: Product Catalog - Smoke

  # TC_INVENTORY_001
  @DataFile:resources/data/smokedata.csv @Key:six_products_displayed @smoke @regression
  Scenario: Exactly 6 products are displayed with full details
    Given the user is logged in on the inventory page using the resolved test data
    Then exactly 6 products should be displayed with full details

  # TC_INVENTORY_006
  @DataFile:resources/data/smokedata.csv @Key:add_to_cart_badge_smoke @smoke @regression
  Scenario: Add to Cart updates the badge in real time
    Given the user is logged in on the inventory page using the resolved test data
    When the user adds "td.productName" to the cart from the inventory page
    Then the inventory cart badge should show 1
    And the inventory button for "td.productName" should read "Remove"
