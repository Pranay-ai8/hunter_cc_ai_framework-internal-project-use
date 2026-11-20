Feature: Cart - Regression

  # TC_CART_002
  @DataFile:resources/data/cartdata.csv @Key:multiple_items_cart @regression
  Scenario: Multiple items added independently appear on the Cart page
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productA" to the cart
    And the user adds product "td.productB" to the cart
    And the user opens the cart page
    Then the cart page should contain "td.productA"
    And the cart page should contain "td.productB"
    And the cart badge should show 2

  # TC_CART_003
  @DataFile:resources/data/cartdata.csv @Key:remove_from_cart_page @regression
  Scenario: Remove item from the Cart page
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productA" to the cart
    And the user opens the cart page
    And the user removes "td.productA" from the cart page
    Then the cart page should not contain "td.productA"
    And the cart badge should show 0

  # TC_CART_004
  @DataFile:resources/data/cartdata.csv @Key:remove_from_inventory @regression
  Scenario: Remove item directly from the Inventory page
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productA" to the cart
    Then the inventory button for "td.productA" should read "Remove"
    When the user removes "td.productA" from the inventory page
    Then the inventory button for "td.productA" should read "Add to cart"
    And the inventory cart badge should show 0

  # TC_CART_005
  @DataFile:resources/data/cartdata.csv @Key:cart_persistence @regression
  Scenario: Cart contents persist across Inventory/Detail/Cart navigation
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productA" to the cart
    And the user navigates to the product detail page for "td.productA" and back to inventory
    And the user opens the cart page
    Then the cart page should contain "td.productA"

  # TC_CART_006
  @DataFile:resources/data/cartdata.csv @Key:continue_shopping @regression
  Scenario: Continue Shopping returns to the Inventory page
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productA" to the cart
    And the user opens the cart page
    And the user clicks Continue Shopping from the cart page
    Then the user should be on the inventory page with cart contents unchanged

  # TC_CART_007
  @DataFile:resources/data/cartdata.csv @Key:empty_cart @regression
  Scenario: Empty cart renders correctly with no application error
    Given the user is logged in with an empty cart for cart tests
    Given the cart is confirmed empty
    Then the cart page should render with no items and no error

  # TC_CART_008 — Partial: problem_user's Remove-button defect is disputed between source
  # PRDs (GapAnalysis.md Conflicting Requirements). Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/cartdata.csv @Key:problem_user_remove @inprogress
  Scenario: problem_user's Cart Remove button behavior is observed and recorded
    Given the user is logged in with an empty cart for cart tests
    When the user adds product "td.productA" to the cart
    And the user opens the cart page
    Then the Remove button behavior for "td.productA" should be observed and recorded
