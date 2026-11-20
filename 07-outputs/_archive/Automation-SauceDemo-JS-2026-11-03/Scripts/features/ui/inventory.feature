Feature: Product Catalog - SauceDemo Inventory
  As a logged-in SauceDemo user
  I want to browse, sort, and add products to my cart
  So that I can shop for items

  # TC_INVENTORY_001 | FR-09
  @smoke @regression @Key:TC_INVENTORY_001
  Scenario: Exactly 6 products displayed with full details
    Given user is logged in as "<Username>"
    Then exactly 6 products are visible on the inventory page
    And every product shows a name, description, image, and price

  # TC_INVENTORY_002 | FR-10
  @regression @Key:TC_INVENTORY_002
  Scenario: Sort products by Name (A to Z)
    Given user is logged in as "<Username>"
    When user sorts products by "<SortLabel>"
    Then the products are displayed in ascending alphabetical order by name

  # TC_INVENTORY_003 | FR-10
  @regression @Key:TC_INVENTORY_003
  Scenario: Sort products by Name (Z to A)
    Given user is logged in as "<Username>"
    When user sorts products by "<SortLabel>"
    Then the products are displayed in descending alphabetical order by name

  # TC_INVENTORY_004 | FR-10
  @regression @Key:TC_INVENTORY_004
  Scenario: Sort products by Price (low to high)
    Given user is logged in as "<Username>"
    When user sorts products by "<SortLabel>"
    Then the products are displayed in ascending order by price

  # TC_INVENTORY_005 | FR-10
  @regression @Key:TC_INVENTORY_005
  Scenario: Sort products by Price (high to low)
    Given user is logged in as "<Username>"
    When user sorts products by "<SortLabel>"
    Then the products are displayed in descending order by price

  # TC_INVENTORY_006 | FR-11
  @smoke @regression @Key:TC_INVENTORY_006
  Scenario: Add to cart updates badge in real time
    Given user is logged in as "<Username>"
    Then the cart badge is not visible
    When user adds "<Product1>" to the cart from the inventory page
    Then the cart badge shows count "1"
    And the "<Product1>" button now reads "Remove"

  # TC_INVENTORY_007 | FR-28 | Partial — confirmed live during this generation's DOM verification
  # pass that problem_user's product images are indeed broken (all 6 return the same 404 image
  # asset), corroborating both source PRDs. See AutomationSummary.md for the full finding.
  @partial @Key:TC_INVENTORY_007
  Scenario: problem_user sees broken/incorrect product images
    Given user is logged in as "<Username>"
    Then all product images share the same broken image source

  # TC_INVENTORY_008 | FR-29 | Partial — error_user's sort-dropdown error is documented as
  # non-deterministic ("random"); observed, not hard-asserted.
  @partial @Key:TC_INVENTORY_008
  Scenario: error_user sort interaction is observed for random errors
    Given user is logged in as "<Username>"
    When user sorts products by "<SortLabel>" and observes for runtime errors
    Then the observation is logged without a hard pass-or-fail assertion

  # TC_INVENTORY_009 | FR-30 | Partial — visual_user's CSS/layout defects need visual-regression
  # tooling (screenshot diffing), not a simple structural assertion.
  @partial @Key:TC_INVENTORY_009
  Scenario: visual_user inventory page is captured for visual-regression review
    Given user is logged in as "<Username>"
    Then a screenshot of the inventory page is captured for visual comparison
