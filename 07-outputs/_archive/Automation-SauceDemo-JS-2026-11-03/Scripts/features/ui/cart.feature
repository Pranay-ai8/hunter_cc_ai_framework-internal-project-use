Feature: Cart - SauceDemo
  As a logged-in SauceDemo user
  I want to manage items in my shopping cart
  So that I can review my selections before checkout

  # TC_CART_001 | FR-14
  @smoke @regression @Key:TC_CART_001
  Scenario: Single item added appears on the Cart page
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    Then the cart contains exactly 1 item
    And the cart contains "<Product1>"

  # TC_CART_002 | FR-14
  @regression @Key:TC_CART_002
  Scenario: Multiple items added independently appear on the Cart page
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    And user adds "<Product2>" to the cart from the inventory page
    Then the cart badge shows count "2"
    When user opens the cart
    Then the cart contains exactly 2 items
    And the cart contains "<Product1>"
    And the cart contains "<Product2>"

  # TC_CART_003 | FR-15
  @regression @Key:TC_CART_003
  Scenario: Remove item from the Cart page
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user removes "<Product1>" from the cart page
    Then the cart contains exactly 0 items
    And the cart badge is not visible on the cart page

  # TC_CART_004 | FR-16
  @regression @Key:TC_CART_004
  Scenario: Remove item directly from the Inventory page
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    Then the "<Product1>" button now reads "Remove"
    When user removes "<Product1>" from the cart from the inventory page
    Then the "<Product1>" button now reads "Add to cart"
    And the cart badge is not visible

  # TC_CART_005 | FR-17
  @regression @Key:TC_CART_005
  Scenario: Cart contents persist across Inventory/Detail/Cart navigation
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    And user clicks the product name link for "<Product2>"
    And user clicks Back to Products
    And user opens the cart
    Then the cart contains "<Product1>"

  # TC_CART_006 | FR-17
  @regression @Key:TC_CART_006
  Scenario: Continue Shopping returns to Inventory
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user clicks continue shopping
    Then user is redirected to the inventory page
    And the cart badge shows count "1"

  # TC_CART_007 | FR-17
  @regression @Key:TC_CART_007
  Scenario: Empty cart displays correctly
    Given user is logged in as "<Username>"
    When user opens the cart
    Then the cart contains exactly 0 items
    And the cart badge is not visible on the cart page

  # TC_CART_008 | FR-28 | Partial — confirmed live during this generation's DOM verification pass
  # that problem_user's cart "Remove" button IS functional (item count 1 -> 0). The PDF's claim
  # of a non-functional Remove button does not reproduce; the docx's account is corroborated.
  # See AutomationSummary.md for detail. Kept Partial per the original cross-source conflict.
  @partial @Key:TC_CART_008
  Scenario: problem_user's cart Remove button behavior is verified
    Given user is logged in as "<Username>"
    When user adds "<Product1>" to the cart from the inventory page
    And user opens the cart
    And user removes "<Product1>" from the cart page
    Then the observed Remove-button behavior for problem_user is logged
