Feature: Product Catalog - Regression

  # TC_INVENTORY_002
  @DataFile:resources/data/inventorydata.csv @Key:sort_name_az @regression
  Scenario: Sort by Name (A to Z)
    Given the user is logged in on the inventory page using the resolved test data
    When the user sorts the inventory using the resolved test data
    Then the products should be sorted by name ascending

  # TC_INVENTORY_003
  @DataFile:resources/data/inventorydata.csv @Key:sort_name_za @regression
  Scenario: Sort by Name (Z to A)
    Given the user is logged in on the inventory page using the resolved test data
    When the user sorts the inventory using the resolved test data
    Then the products should be sorted by name descending

  # TC_INVENTORY_004
  @DataFile:resources/data/inventorydata.csv @Key:sort_price_lohi @regression
  Scenario: Sort by Price (low to high)
    Given the user is logged in on the inventory page using the resolved test data
    When the user sorts the inventory using the resolved test data
    Then the products should be sorted by price ascending

  # TC_INVENTORY_005
  @DataFile:resources/data/inventorydata.csv @Key:sort_price_hilo @regression
  Scenario: Sort by Price (high to low)
    Given the user is logged in on the inventory page using the resolved test data
    When the user sorts the inventory using the resolved test data
    Then the products should be sorted by price descending

  # TC_INVENTORY_007 — Partial: problem_user defect is disputed between source PRDs
  # (GapAnalysis.md Conflicting Requirements). Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/inventorydata.csv @Key:problem_user_images @inprogress
  Scenario: problem_user's product images are observed for defects
    Given the user is logged in on the inventory page using the resolved test data
    Then the product images should be observed for defects

  # TC_INVENTORY_008 — Partial: error_user's sort behavior is documented as random
  # (not suitable for a deterministic hard assertion). Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/inventorydata.csv @Key:error_user_sort @inprogress
  Scenario: error_user's sort interaction is observed without a deterministic assertion
    Given the user is logged in on the inventory page using the resolved test data
    Then repeated sort interactions should be observed without assuming a single deterministic outcome

  # TC_INVENTORY_009 — Partial: visual_user requires visual-regression tooling, not a
  # structural assertion. Excluded from @smoke/@regression tag runs.
  @DataFile:resources/data/inventorydata.csv @Key:visual_user_layout @inprogress
  Scenario: visual_user's inventory layout is captured for visual comparison
    Given the user is logged in on the inventory page using the resolved test data
    Then the inventory layout should be captured for visual comparison
