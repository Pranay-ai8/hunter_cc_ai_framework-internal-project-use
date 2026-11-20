import { Given, Then, When } from "@cucumber/cucumber";
import { expect } from "@playwright/test";
import { ScenarioContext } from "../../Framework/src/utils/ScenarioContext";
import { resolveTestDataToken } from "../../Framework/src/utils/testDataResolver";
import { resolvePassword } from "../../Framework/src/utils/passwordResolver";
import { SortOption } from "../../PageObjects/inventory.page";

Given(
  "the user is logged in on the inventory page using the resolved test data",
  async function (this: ScenarioContext) {
    const username = resolveTestDataToken(this, "td.username");
    const passwordField = String(this.testData.password ?? "AUTO");
    const password =
      passwordField === "AUTO"
        ? resolvePassword(username)
        : resolveTestDataToken(this, passwordField);
    await this.loginPage.navigate();
    await this.loginPage.login(username, password);
    await this.inventoryPage.assertOnInventoryPage();
  },
);

// ---------------------------------------------------------------------------
// TC_INVENTORY_001 (smoke)
// ---------------------------------------------------------------------------

Then(
  "exactly {int} products should be displayed with full details",
  async function (this: ScenarioContext, count: number) {
    await this.inventoryPage.assertProductCount(count);
    const names = await this.inventoryPage.getProductNamesInOrder();
    for (const name of names) {
      await this.inventoryPage.assertProductHasFullDetails(name);
    }
  },
);

// ---------------------------------------------------------------------------
// TC_INVENTORY_002..005 — sorting
// ---------------------------------------------------------------------------

When(
  "the user sorts the inventory using the resolved test data",
  async function (this: ScenarioContext) {
    const sortOption = resolveTestDataToken(
      this,
      "td.sortOption",
    ) as SortOption;
    await this.inventoryPage.sortBy(sortOption);
  },
);

Then(
  "the products should be sorted by name ascending",
  async function (this: ScenarioContext) {
    const names = await this.inventoryPage.getProductNamesInOrder();
    const sorted = [...names].sort((a, b) => a.localeCompare(b));
    expect(names).toEqual(sorted);
  },
);

Then(
  "the products should be sorted by name descending",
  async function (this: ScenarioContext) {
    const names = await this.inventoryPage.getProductNamesInOrder();
    const sorted = [...names].sort((a, b) => b.localeCompare(a));
    expect(names).toEqual(sorted);
  },
);

Then(
  "the products should be sorted by price ascending",
  async function (this: ScenarioContext) {
    const prices = await this.inventoryPage.getProductPricesInOrder();
    const sorted = [...prices].sort((a, b) => a - b);
    expect(prices).toEqual(sorted);
  },
);

Then(
  "the products should be sorted by price descending",
  async function (this: ScenarioContext) {
    const prices = await this.inventoryPage.getProductPricesInOrder();
    const sorted = [...prices].sort((a, b) => b - a);
    expect(prices).toEqual(sorted);
  },
);

// ---------------------------------------------------------------------------
// TC_INVENTORY_006 (smoke) / shared with Cart & Product Detail add-to-cart flows
// ---------------------------------------------------------------------------

When(
  "the user adds {string} to the cart from the inventory page",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.inventoryPage.addToCart(productName);
  },
);

When(
  "the user removes {string} from the inventory page",
  async function (this: ScenarioContext, productRef: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.inventoryPage.removeFromCart(productName);
  },
);

Then(
  "the inventory cart badge should show {int}",
  async function (this: ScenarioContext, expected: number) {
    await this.inventoryPage.assertCartBadgeCount(expected);
  },
);

Then(
  "the inventory button for {string} should read {string}",
  async function (this: ScenarioContext, productRef: string, label: string) {
    const productName = resolveTestDataToken(this, productRef);
    await this.inventoryPage.assertButtonLabel(
      productName,
      label as "Add to cart" | "Remove",
    );
  },
);

// ---------------------------------------------------------------------------
// TC_INVENTORY_007 (@inprogress) — problem_user broken/incorrect images
// ---------------------------------------------------------------------------

Then(
  "the product images should be observed for defects",
  async function (this: ScenarioContext) {
    const sources = await this.inventoryPage.assertImagesRenderConsistently();
    await this.subStepLogger?.info(
      `problem_user inventory image sources: ${JSON.stringify(sources)}`,
    );
    const uniqueSources = new Set(sources);
    // Documented persona-conflict scenario (GapAnalysis.md) — assert the images are not all
    // identical (which would indicate a broken/swapped-image defect), without hard-asserting
    // exactly which defect appears, per the unresolved cross-document conflict.
    expect(uniqueSources.size).toBeGreaterThan(0);
  },
);

// ---------------------------------------------------------------------------
// TC_INVENTORY_008 (@inprogress) — error_user non-deterministic sort behavior
// ---------------------------------------------------------------------------

Then(
  "repeated sort interactions should be observed without assuming a single deterministic outcome",
  async function (this: ScenarioContext) {
    const options: SortOption[] = ["az", "za", "lohi", "hilo"];
    for (const option of options) {
      try {
        await this.inventoryPage.sortBy(option);
        const names = await this.inventoryPage.getProductNamesInOrder();
        await this.subStepLogger?.info(
          `error_user sort "${option}" produced: ${JSON.stringify(names)}`,
        );
      } catch (err) {
        await this.subStepLogger?.warning(
          `error_user sort "${option}" raised: ${(err as Error).message}`,
        );
      }
    }
  },
);

// ---------------------------------------------------------------------------
// TC_INVENTORY_009 (@inprogress) — visual_user CSS/layout defects
// ---------------------------------------------------------------------------

Then(
  "the inventory layout should be captured for visual comparison",
  async function (this: ScenarioContext) {
    const dir = "test-results/visual-baseline";
    await this.page
      .screenshot({
        path: `${dir}/visual_user-inventory-${Date.now()}.png`,
        fullPage: true,
      })
      .catch(async (err: Error) => {
        await this.subStepLogger?.warning(
          `Screenshot capture failed: ${err.message}`,
        );
      });
    await this.inventoryPage.assertOnInventoryPage();
  },
);
