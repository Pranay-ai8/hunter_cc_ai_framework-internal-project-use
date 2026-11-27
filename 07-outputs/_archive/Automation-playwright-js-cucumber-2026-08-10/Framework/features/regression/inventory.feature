@inventory
Feature: Product Catalog

  Background:
    Given the user is on the SauceDemo login page
    And the user logs in as "standard_user"

  # TC_INVENTORY_001
  @smoke
  Scenario: Exactly 6 products are displayed with full details
    Then exactly 6 products are visible
    And every product shows a name, price, and image

  # TC_INVENTORY_002
  @regression
  Scenario: Sort by Name (A to Z)
    When the user sorts products by "az"
    Then the products are displayed in ascending alphabetical order by name

  # TC_INVENTORY_003
  @regression
  Scenario: Sort by Name (Z to A)
    When the user sorts products by "za"
    Then the products are displayed in descending alphabetical order by name

  # TC_INVENTORY_004
  @regression
  Scenario: Sort by Price (low to high)
    When the user sorts products by "lohi"
    Then the products are displayed in ascending order by price

  # TC_INVENTORY_005
  @regression
  Scenario: Sort by Price (high to low)
    When the user sorts products by "hilo"
    Then the products are displayed in descending order by price

  # TC_INVENTORY_006
  @smoke
  Scenario: Add to cart updates the badge in real time
    When the user adds the first product to the cart
    Then the cart badge shows "1"

  # TC_INVENTORY_007 — problem_user image defect, observational (source conflict — see GapAnalysis.md)
  @regression @observational
  Scenario: problem_user's product images are recorded for defect verification
    Given the user logs out
    And the user logs in as "problem_user"
    Then the product images are captured for defect comparison

  # TC_INVENTORY_008 — error_user non-deterministic, observational only
  @regression @observational
  Scenario: error_user sort interaction is observed for random errors
    Given the user logs out
    And the user logs in as "error_user"
    When the user sorts products by "lohi"
    Then no unhandled application crash occurs

  # TC_INVENTORY_009 — visual_user layout defects, observational
  @regression @observational
  Scenario: visual_user layout is captured for visual defect verification
    Given the user logs out
    And the user logs in as "visual_user"
    Then a screenshot of the inventory page is captured for visual comparison
