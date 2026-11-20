Feature: Product Detail - Regression

  # TC_PRODUCTDETAIL_001
  @DataFile:resources/data/productdetaildata.csv @Key:click_product_name @regression
  Scenario: Clicking a product's name opens its Detail page
    Given the user is logged in and on the inventory page for product detail tests
    When the user clicks the product name "td.productName"
    Then the product detail page should be displayed

  # TC_PRODUCTDETAIL_002
  @DataFile:resources/data/productdetaildata.csv @Key:click_product_image @regression
  Scenario: Clicking a product's image opens its Detail page
    Given the user is logged in and on the inventory page for product detail tests
    When the user clicks the product image "td.productName"
    Then the product detail page should show the same product

  # TC_PRODUCTDETAIL_003
  @DataFile:resources/data/productdetaildata.csv @Key:add_to_cart_detail @regression
  Scenario: Add to Cart from Product Detail updates the badge
    Given the user is logged in and on the inventory page for product detail tests
    When the user clicks the product name "td.productName"
    And the user adds the product to the cart from the detail page
    Then the detail page cart badge should show 1
    And the detail page button should read "Remove"

  # TC_PRODUCTDETAIL_004
  @DataFile:resources/data/productdetaildata.csv @Key:back_to_products @regression
  Scenario: Back to Products returns to the Inventory page
    Given the user is logged in and on the inventory page for product detail tests
    When the user clicks the product name "td.productName"
    And the user clicks Back to Products
    Then the user should be back on the inventory page with all products visible
