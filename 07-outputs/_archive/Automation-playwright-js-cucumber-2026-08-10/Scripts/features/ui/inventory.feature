Feature: SauceDemo - Product Catalog

  Background:
    Given the user is logged in as "<Username>" with password "<Password>"

  # TC_INVENTORY_001
  @Key:StandardUser @smoke
  Scenario: Exactly 6 products displayed with full details
    Then the Inventory page should display exactly 6 products

  # TC_INVENTORY_002
  @Key:StandardUser @regression
  Scenario: Sort by Name (A to Z)
    When the user sorts products by "Name (A to Z)"
    Then the product names should be sorted ascending

  # TC_INVENTORY_003
  @Key:StandardUser @regression
  Scenario: Sort by Name (Z to A)
    When the user sorts products by "Name (Z to A)"
    Then the product names should be sorted descending

  # TC_INVENTORY_004
  @Key:StandardUser @regression
  Scenario: Sort by Price (low to high)
    When the user sorts products by "Price (low to high)"
    Then the product prices should be sorted ascending

  # TC_INVENTORY_005
  @Key:StandardUser @regression
  Scenario: Sort by Price (high to low)
    When the user sorts products by "Price (high to low)"
    Then the product prices should be sorted descending

  # TC_INVENTORY_006
  @Key:StandardUser @smoke
  Scenario: Add to Cart updates badge in real time
    Then the cart badge should not be visible
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should be visible

  # TC_INVENTORY_007 — Automation Candidate: Partial. Conflict between source PRDs
  # on problem_user's exact defect (see GapAnalysis.md Conflicting Requirements).
  @Key:ProblemUser @manual
  Scenario: problem_user sees broken/incorrect product images
    When the user records product image sources for later comparison

  # TC_INVENTORY_008 — Automation Candidate: Partial. error_user's errors are
  # explicitly documented as "random" — not suitable for a deterministic assertion.
  @Key:ErrorUser @manual
  Scenario: error_user sort interaction may throw a random error
    When the user sorts products by "Price (high to low)"
    And the user sorts products by "Name (A to Z)"

  # TC_INVENTORY_009 — Automation Candidate: Partial. visual_user's defects need a
  # visual-regression baseline/tool, not a simple structural assertion.
  @Key:VisualUser @manual
  Scenario: visual_user shows CSS/layout defects
    When the user captures a screenshot named "visual_user-inventory"
