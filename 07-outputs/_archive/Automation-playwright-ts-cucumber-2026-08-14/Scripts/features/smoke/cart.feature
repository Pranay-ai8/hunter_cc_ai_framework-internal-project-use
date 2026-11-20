Feature: Cart - Smoke

  # TC_CART_001
  @DataFile:resources/data/smokedata.csv @Key:single_item_cart_smoke @smoke @regression
  Scenario: A single item added to the cart appears on the Cart page
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productName" to the cart
    And the user opens the cart page
    Then the cart page should contain "td.productName" with quantity 1
