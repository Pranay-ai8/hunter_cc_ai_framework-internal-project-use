import { Given, Then, When } from "@cucumber/cucumber";
import { expect } from "@playwright/test";
import { ScenarioContext } from "../../Framework/src/utils/ScenarioContext";
import { resolveTestDataToken } from "../../Framework/src/utils/testDataResolver";
import { resolvePassword } from "../../Framework/src/utils/passwordResolver";

/**
 * Resolves a login password field: "AUTO" means "look up the password for this
 * username in resources/data/.env.passwords"; anything else (including an
 * intentionally blank/wrong value used by negative test cases) is used as-is.
 */
function resolveLoginPassword(
  context: ScenarioContext,
  username: string,
  passwordField: string,
): string {
  if (passwordField === "AUTO") return resolvePassword(username);
  return resolveTestDataToken(context, passwordField);
}

// ---------------------------------------------------------------------------
// TC_LOGIN_001 (smoke), TC_LOGIN_002..005, TC_LOGIN_007..009
// ---------------------------------------------------------------------------

Given(
  "the user is on the SauceDemo login page",
  async function (this: ScenarioContext) {
    await this.loginPage.navigate();
  },
);

When(
  "the user logs in using the resolved test data",
  async function (this: ScenarioContext) {
    const username = resolveTestDataToken(this, "td.username");
    const password = resolveLoginPassword(
      this,
      username,
      String(this.testData.password ?? "AUTO"),
    );
    await this.loginPage.login(username, password);
  },
);

Then(
  "the user should be redirected to the inventory page",
  async function (this: ScenarioContext) {
    await this.loginPage.assertLoggedInToInventory();
  },
);

Then(
  "login should be rejected with the error {string}",
  async function (this: ScenarioContext, errorRef: string) {
    const expected = resolveTestDataToken(this, errorRef);
    await this.loginPage.assertErrorMessage(expected);
  },
);

Then(
  "the user should remain on the login page",
  async function (this: ScenarioContext) {
    await this.loginPage.assertRemainsOnLoginPage();
  },
);

// ---------------------------------------------------------------------------
// TC_LOGIN_006 — performance_glitch_user artificial delay
// ---------------------------------------------------------------------------

When(
  "the user logs in using the resolved test data and measures render time",
  async function (this: ScenarioContext) {
    const username = resolveTestDataToken(this, "td.username");
    const password = resolveLoginPassword(
      this,
      username,
      String(this.testData.password ?? "AUTO"),
    );
    this.perfMeasurements.loginRenderTimeMs =
      await this.loginPage.loginAndMeasureRenderTime(username, password);
  },
);

Then(
  "the render time should be at least {int} ms slower than a standard login",
  async function (this: ScenarioContext, thresholdMs: number) {
    const measured = this.perfMeasurements.loginRenderTimeMs;
    expect(
      measured,
      "performance_glitch_user render time was not measured",
    ).toBeGreaterThanOrEqual(thresholdMs);
  },
);

// ---------------------------------------------------------------------------
// TC_LOGIN_010 (@inprogress) — session auto-expiry after inactivity
// GAP-FR-03: exact post-timeout UX is undocumented; a real 10-minute wait is
// impractical for CI, so this scenario uses a short stand-in wait and documents the
// gap rather than hard-asserting a specific redirect. Expected to be flaky/failing
// until the real timeout duration and UX are confirmed against the live app.
// ---------------------------------------------------------------------------

Given(
  "the user remains idle for {int} minutes after logging in",
  async function (this: ScenarioContext, _idleMinutes: number) {
    const username = resolveTestDataToken(this, "td.username");
    const password = resolveLoginPassword(
      this,
      username,
      String(this.testData.password ?? "AUTO"),
    );
    await this.loginPage.login(username, password);
    await this.loginPage.assertLoggedInToInventory();
    await this.subStepLogger?.info(
      `TC_LOGIN_010: waiting a reduced stand-in period instead of a real ${_idleMinutes}-minute idle (GAP-FR-03, impractical for CI)`,
    );
    // NOTE: intentional, documented exception to the "no fixed waits" rule — this scenario
    // exists specifically to probe time-based session expiry and is @inprogress/excluded
    // from @smoke/@regression runs. See docs/test-suites/authentication/tc-login-010.md.
    await this.page.waitForTimeout(3000);
  },
);

Then(
  "attempting an action should redirect the user back to the login page",
  async function (this: ScenarioContext) {
    await this.inventoryPage.addToCart("Sauce Labs Backpack");
    await this.loginPage.assertRemainsOnLoginPage();
  },
);
