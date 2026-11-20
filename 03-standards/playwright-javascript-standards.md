# Playwright JavaScript Automation Standards

## Objective

Establish enterprise-grade standards for **Playwright with plain JavaScript (Cucumber BDD, no TypeScript)** automation, so AI-generated frameworks and test artifacts for this stack are maintainable, scalable, reusable, and CI/CD-ready.

These standards apply whenever the user selects **JavaScript** as the automation language for Playwright automation, and are modeled on the reference implementation at `semnox-playwright-demo`.

This is a **third sibling stack**, alongside `03-standards/playwright-typescript-standards.md` (TypeScript + Cucumber) and `03-standards/playwright-java-standards.md` (Java + TestNG). A project is always exactly one of these three stacks — never mixed within one project.

The JavaScript stack shares the same **Cucumber BDD** philosophy as the TypeScript stack (feature files → steps → page objects → Playwright), but differs structurally: no TypeScript compiler, no `ScenarioContext`/`World` class, and a `PlaywrightWrapper` helper class mediates all Playwright calls from page objects instead of native Playwright locators/`expect` calls used directly.

---

## Technology Stack

| Layer | Choice |
|---|---|
| Automation Tool | Playwright (`@playwright/test`, driver only — no Playwright Test Runner) |
| BDD Runner | Cucumber (`@cucumber/cucumber`) |
| Language | Plain JavaScript (CommonJS — `require`/`module.exports`), no TypeScript |
| Design Pattern | Page Object Model, with a shared `PlaywrightWrapper` action helper |
| Shared State | A single global `fixture` object (`pageFixture.js`), not a Cucumber `World` subclass |
| Assertions | `@playwright/test`'s `expect`, plus a custom `assert.js` wrapper |
| Reporting | Allure (`allure-cucumberjs`) + `multiple-cucumber-html-reporter` + custom HTML sub-step logger + Winston logs |
| Parallel Execution | Cucumber `parallel` option (defaults to `1` — sequential) |
| Data | Pipe-delimited CSV (single default `testData.csv`, optionally more), keyed by `@Key:` tags; JSON supported for API test data |
| API Testing | First-class support via `src/helper/api/` (client, context, endpoints, schemas, validators) |
| CI/CD | GitHub Actions (`ci.yml`) |
| Perf | Optional Lighthouse integration (`src/helper/lighthouse/runLighthouse.js`) |

> This framework uses **Cucumber as the test runner and Playwright as the browser driver**, written in **plain JavaScript** — no TypeScript compilation step, no `tsconfig.json`, no type annotations. Do not introduce `.ts` files into a project using this stack.

---

## Project Structure

```
<project-root>/
├── config/
│   └── cucumber.js                 # Cucumber profiles: default, rerun
├── package.json                     # CommonJS ("type" unset/commonjs), Cucumber + Playwright deps
├── @rerun.txt                        # Auto-generated list of failed scenarios for reruns
├── src/
│   ├── helper/
│   │   ├── api/
│   │   │   ├── client/               # API client wrapper(s)
│   │   │   ├── context/              # ApiContext — request/response state for API scenarios
│   │   │   ├── endpoints/            # API endpoint path constants
│   │   │   ├── logging/              # API-specific request/response logging
│   │   │   ├── models/               # Plain JS data models/DTOs for API payloads
│   │   │   ├── resolver/             # Resolves API test data placeholders
│   │   │   ├── schemas/              # JSON schema definitions for response validation
│   │   │   └── validators/           # Schema/response validators (e.g., using `ajv`)
│   │   ├── browsers/
│   │   │   └── browserManager.js     # invokeBrowser() — launches chromium/firefox/webkit per BROWSER env var
│   │   ├── commonConfig/
│   │   │   └── configuration.json    # testDataLocation, defaultTimeOut, playwrightTimeout, navigationTimeout, expectTimeout
│   │   ├── env/
│   │   │   ├── .env.STG / .env.PRD / .env.<ENV>
│   │   │   └── env.js                # getEnv() — loads the active .env.<ENV> via dotenv
│   │   ├── lighthouse/
│   │   │   └── runLighthouse.js      # Optional Lighthouse performance audit runner
│   │   ├── parsers/
│   │   │   └── CSVParser.js          # Pipe-delimited CSV loader (static parseData(filePath, separator))
│   │   ├── report/
│   │   │   ├── init.js               # pretest — resets/prepares report output dirs
│   │   │   ├── report.js             # posttest — generates the multi-cucumber-html-report
│   │   │   └── runCucumberWithReport.js  # test:* scripts entry point — runs cucumber-js then report.js
│   │   ├── self-healer.js            # findHealedLocator() — see "Self-Healing Locators" below
│   │   ├── util/
│   │   │   ├── logger.js             # Winston logger factory (per-scenario file logger)
│   │   │   └── test-data/
│   │   │       ├── testData.csv      # Default pipe-delimited UI test data (Key, Env, ...)
│   │   │       └── apiTestData.json  # Default API test data
│   │   └── wrapper/
│   │       ├── PlaywrightWrappers.js # Shared action helper: goto, waitAndClick, typeText, waitForURL, etc.
│   │       └── assert.js             # Soft/hard assertion helpers
│   ├── pages/
│   │   └── <name>Page.js             # camelCase file, PascalCase class; composes a PlaywrightWrapper instance
│   ├── support/
│   │   ├── pageFixture.js            # The shared `fixture` object — page, logger, testData, api, pages registry
│   │   ├── hooks.js                  # BeforeAll/Before/BeforeStep/AfterStep/After/AfterAll
│   │   ├── customTypes.js            # Custom Cucumber parameter types
│   │   ├── htmllSubStepLogger.js     # HTML badge sub-step logger (Cucumber/Allure attachments)
│   │   └── allureReporter.js         # Cucumber formatter wiring Allure result output
│   └── test/
│       ├── features/
│       │   ├── ui/
│       │   │   └── <module>.feature
│       │   └── api/
│       │       └── <module>.feature
│       └── steps/
│           ├── ui/
│           │   └── <module>Steps.js
│           └── api/
│               └── <module>Steps.js
├── test-results/                     # cucumber-report.json, screenshots/, trace/
├── allure-results/ / allure-report/
└── .github/
    └── workflows/
        └── ci.yml                    # GitHub Actions — separate API and UI test jobs
```

---

## Cucumber Configuration Standards

Every project must define `config/cucumber.js` with:

```javascript
module.exports = {
    default: {
        tags: process.env.npm_config_TAGS || "",
        formatOptions: { snippetInterface: "async-await" },
        paths: ["src/test/features/**/*.feature"],
        publishQuiet: true,
        dryRun: false,
        require: [
            "src/support/customTypes.js",
            "src/support/hooks.js",
            "src/test/steps/**/*.js"
        ],
        format: [
            "progress-bar",
            "json:test-results/cucumber-report.json",
            "./src/support/allureReporter.js",
            "rerun:@rerun.txt"
        ],
        parallel: 1
    },
    rerun: {
        formatOptions: { snippetInterface: "async-await" },
        publishQuiet: true,
        dryRun: false,
        require: [
            "src/support/customTypes.js",
            "src/support/hooks.js",
            "src/test/steps/**/*.js"
        ],
        format: [
            "progress-bar",
            "json:test-results/cucumber-report.json",
            "./src/support/allureReporter.js",
            "rerun:@rerun.txt"
        ],
        parallel: 1
    }
};
```

- No `requireModule: ['ts-node/register']` — this stack runs `.js` files directly, no transpilation step.
- `paths: ["src/test/features/**/*.feature"]` picks up both `ui/` and `api/` subfolders automatically.
- `format` always includes the Allure formatter (`./src/support/allureReporter.js`) and a `rerun:@rerun.txt` writer to support `npm run test:failed`.

---

## Feature File Standards

### Folder = domain, tag = execution/data filter

Feature files are split by **domain** (`ui/`, `api/`), not by suite-per-folder like the TypeScript stack. Suite membership is expressed purely through tags:

| Tag | Meaning |
|---|---|
| `@smoke` | Fast critical-path scenario, run via `npm run test:smoke` |
| `@regression` | Full acceptance scenario, run via `npm run test:regression` |
| `@sanity` | Production sanity scenario (PRD env), run via `npm run test:sanity` |
| `@api` | API-only scenario — hooks skip browser/context/page setup entirely |
| `@chain` | Chained/sequential API scenarios, run via `npm run test:api:chain` |
| `@Key:<id>` | Selects the test data row from the default CSV (or a scenario-specific data source) |

### Scenario tagging convention

```gherkin
Feature: Semnox Demo - Birthday Party

  @Key:Simple_01 @smoke
  Scenario: Party booking Simple 1
    Given User navigates to the application home page
    When  User navigates to packages screen
    Then  User clicks on Buy now of Guru K advance and selects slot "<slot>" and Date "<Date>"
    Then  User proceed to secure checkout by verifying the selected slot "<slot>"
```

- Placeholders in step text use **angle-bracket syntax**: `<columnName>` (not `td.columnName` as in the TypeScript stack). The `Before` hook resolves these at runtime by looking up the row matched by `@Key:` (and `Env`) in the CSV, substituting nested/array values as needed.
- Every scenario needing test data must carry a `@Key:<id>` tag; the row is located by matching `Key`/`key` (or `TestCaseID`) and the active `Env`.
- API scenarios are tagged `@api` (and `@chain` when part of a multi-step chain) — the `Before` hook detects this tag and skips browser/page creation entirely, initializing only `fixture.api.context`.

---

## Page Object Standards

### No shared BasePage — pages compose a `PlaywrightWrapper`

Unlike the TypeScript stack's `BasePage` inheritance, JavaScript page objects **compose** an instance of the shared `PlaywrightWrapper` helper rather than extending a base class:

```javascript
// src/helper/wrapper/PlaywrightWrappers.js
const { expect } = require("@playwright/test");

class PlaywrightWrapper {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
    }

    async waitForElement(locator) {
        const element = this.page.locator(locator);
        await element.waitFor({ state: "visible" });
        return element;
    }

    async goto(path) {
        const baseUrl = process.env.BASEURL;
        if (!baseUrl) throw new Error("BASEURL is undefined. Check your .env file and dotenv.config()");
        const url = new URL(path, baseUrl).toString();
        const response = await this.page.goto(url, { waitUntil: "domcontentloaded", timeout: 60000 });
        if (!response || !response.ok()) {
            throw new Error(`Navigation failed: ${response ? response.status() : "NA"} for URL: ${url}`);
        }
    }

    async waitAndClick(locator, objName = "") {
        const element = await this.waitForElement(locator);
        await element.click();
        await this.logger.success(`Clicked on element: ${objName || locator}`);
    }

    async typeText(locator, text, objName = "") { /* fill + log */ }
    async typePassword(locator, text, objName = "") { /* fill without logging the value */ }
    async waitForURL(expectedURL, timeout = 5000) { /* expect(page).toHaveURL(...) */ }
}

module.exports = PlaywrightWrapper;
```

- All navigation/interaction methods live on `PlaywrightWrapper` (`goto`, `waitForElement`, `waitAndClick`, `typeText`, `typePassword`, `waitForURL`, `waitForURLAfterAction`, `navigateTo`, ...). Add new shared interaction primitives here, never duplicate them per page.
- `goto(path)` always resolves against `process.env.BASEURL` via `new URL(path, baseUrl)` — never hardcode a full URL in a page object.
- `waitAndClick`/`typeText` accept an optional `objName` for human-readable logging — always pass one for anything beyond a trivial click.
- `typePassword` must never log the actual secret value.

### File & Class Naming

- File name: `<name>Page.js` — camelCase file name (e.g., `loginPage.js`, `birthdayPartyPage.js`, `contactPage.js`).
- Class name: PascalCase (e.g., `class LoginPage { ... }`), exported via `module.exports = LoginPage;` (default export, not a named export).

### Page Object structure — string-selector `Elements` map + composed wrapper

```javascript
const { expect } = require("@playwright/test");
const PlaywrightWrapper = require("../helper/wrapper/PlaywrightWrappers");

class LoginPage {
    constructor(page, logger) {
        this.page = page;
        this.logger = logger;
        this.base = new PlaywrightWrapper(page, logger);
        this.Elements = {
            usernameInput: "#username",
            passwordInput: "#password",
            loginBtn: "#submit",
            logoutBtn: "//a[text()='Log out']",
            successMessage: "//h1[contains(normalize-space(),'Logged In Successfully')]",
            loginPageTitle: "Test Login | Practice Test Automation"
        };
    }

    async navigateToLoginPage() {
        await this.base.goto("practice-test-login/");
        await expect(this.page).toHaveTitle(this.Elements.loginPageTitle);
    }

    async enterUserName(user) {
        await this.base.typeText(this.Elements.usernameInput, user, "Username");
    }

    async enterPassword(password) {
        await this.base.typePassword(this.Elements.passwordInput, password, "Password");
    }

    async clickLoginButton() {
        await this.base.waitAndClick(this.Elements.loginBtn, "Login");
    }

    async loginUser(user, password) {
        await this.enterUserName(user);
        await this.enterPassword(password);
        await this.clickLoginButton();
    }

    async verifySuccessfulLogin() {
        await this.base.waitForURL(/logged-in-successfully/);
        await this.base.waitForElement(this.Elements.logoutBtn);
        return this.page.locator(this.Elements.successMessage).isVisible();
    }
}

module.exports = LoginPage;
```

- Locators are declared as a single `this.Elements = { ... }` object literal in the constructor — plain CSS or XPath **strings**, not Playwright `Locator` objects and not getters. Resolve to a live `Locator` only inside `PlaywrightWrapper` methods (`this.page.locator(this.Elements.x)`).
- Constructor signature is always `constructor(page, logger)` — `logger` is the per-scenario Winston logger (or the `subStepLogger`, depending on what the wrapper needs), always forwarded into `this.base = new PlaywrightWrapper(page, logger)`.
- High-level business action methods (`loginUser`, `navigateToLoginPage`) compose lower-level ones — this is the primary reuse mechanism (no fluent `return this` chaining convention here, unlike the Java stack).
- Prefer CSS selectors with stable IDs/`data-testid` first, falling back to descriptive XPath only when necessary — see `03-standards/locator-standards.md` priority order; because locators here are raw strings (not Playwright semantic locator calls), favor `#id`, `[data-testid="..."]`, and stable attribute selectors over deep positional XPath.

---

## Live DOM Verification

See `03-standards/locator-standards.md`'s "Live DOM Verification" section for the full rationale and cross-stack process — this section covers only this stack's mechanics.

Before writing a module's page object for the first time, run a throwaway script (not part of the generated framework — a one-off snapshot tool, deleted after use) that:

1. Launches Playwright directly (`chromium.launch()`), navigates to the module's screen (performing any required setup, e.g. login), and collects the same candidate shape `self-healer.js` gathers at runtime:

```javascript
const candidates = await page.evaluate(
    ({ selectors, limit }) => {
        const elements = Array.from(document.querySelectorAll(selectors.join(","))).slice(0, limit);
        return elements.map((el) => ({
            tag: el.tagName.toLowerCase(),
            id: el.id || "", class: el.className || "", name: el.getAttribute("name") || "",
            role: el.getAttribute("role") || "", ariaLabel: el.getAttribute("aria-label") || "",
            dataTestId: el.getAttribute("data-testid") || "", placeholder: el.getAttribute("placeholder") || "",
            forAttr: el.getAttribute("for") || "", text: (el.textContent || "").trim().replace(/\s+/g, " "),
        }));
    },
    { selectors: ["button", "a", "input", "select", "textarea", "[role]", "[data-testid]", "label"], limit: 150 }
);
```

2. Cross-check every locator you're about to put in `this.Elements` against this snapshot before writing it — not after the fact.
3. Save the raw snapshot to `docs/dom-snapshots/<module>.json` (create the folder if absent) so the verification is auditable later, and note any documented-vs-actual discrepancy directly in the generation summary.

---

## Self-Healing Locators

Every project generated for this stack must include a self-healing layer so a stale/renamed locator degrades to an automatic, logged recovery instead of an immediate hard failure. This is **mandatory scaffold**, not optional — generate `src/helper/self-healer.js` and wire it into `PlaywrightWrapper` exactly as specified below.

### Module: `src/helper/self-healer.js`

Exports a single function: `findHealedLocator(page, originalLocator, error, hint = "")`. Given a locator string that just failed, it returns either `{ success: true, locator: "<playwright locator expression string>", confidence, strategy: "rule-based" }` or `{ success: false, locator: null, confidence, strategy: "rule-based" }`.

**Constants:**

```javascript
const CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
const CACHE_FILE = path.resolve(__dirname, "..", "..", "..", "healing-cache.json");
const LOG_FILE = path.resolve(__dirname, "..", "..", "..", "healing-report.log");
const INTERACTIVE_SELECTORS = ["button", "a", "input", "select", "textarea", "[role]", "[data-testid]", "label"];
const MAX_DOM_SNAPSHOT_ELEMENTS = 150;
const CONFIDENCE_THRESHOLD = 0.75;
```

`healing-cache.json` and `healing-report.log` live one level above `Framework/` (a shared ancestor of `PageObjects`/`Scripts` in Mode A's split layout) so every consumer resolves the same files regardless of which sibling folder it runs from.

**Algorithm — read this before modifying, several of these rules were discovered the hard way by actually running the mechanism against a live app, not by inspection:**

1. **Keywords come ONLY from what you're looking FOR — never from `error.message` or a DOM dump.** Build three separate keyword sets: `locatorKeywords` (tokenized from `originalLocator` alone), `hintKeywords` (tokenized from the optional human-readable `hint`, e.g. `"Login Button"`), and `combinedKeywords` (their union). Score every candidate against all three and take the **max** — never pool them into one set. A corrupted/renamed locator fragment (e.g. `"checkout12"` for the real `"checkout"`) is untranslatable noise that must not be allowed to drag down recall for an otherwise-clean hint match; conversely a clean locator with no hint must still be able to heal on its own.
2. **Tokenize into word SETS, not raw concatenated strings**, and match by exact word membership — a substring scan (`str.includes(keyword)`) lets short keywords spuriously match unrelated text.
3. **Exclude `class` from the candidate's scoring word set.** CSS class lists are structural/styling noise in real apps (BEM/utility classes like `"btn_primary btn_action"`), not semantic identity — including them inflates the word-set size and dilutes precision for elements that simply carry several classes. `class` is still fine to use as a last-resort fallback inside `buildLocatorExpression` (a `.` selector), just not for scoring.
4. **Score with precision/recall (F1), not `matchCount / totalKeywords`.** `recall = matched.length / keywords.length` (how much of the query this candidate covers), `precision = matched.length / candidateWordSet.size` (how focused the candidate's own identity is on that match — an unrelated, wordy element sharing one incidental word must not win just by being wordy). `score = 2 * recall * precision / (recall + precision)`, then add a bonus (`+0.15` for `data-testid`, `+0.1` for `id`) when a matched keyword is directly contained in that attribute — the two most trustworthy, intentional identifiers on a page.
5. **`page.evaluate(pageFunction, arg)` takes exactly ONE argument.** Unlike Puppeteer, Playwright's `evaluate()` throws `"Too many arguments"` if you pass more than one positional argument after the function — bundle everything into a single object: `page.evaluate(({ selectors, limit }) => {...}, { selectors, limit })`.
6. **Cache successful heals** for `CACHE_TTL_MS` keyed by `${pageUrl}|${originalLocator}`, and **validate** a healed locator actually resolves (`locator.count() > 0`) before returning success — a scored-but-nonexistent candidate must not be handed back as a fix.
7. **Log every successful heal** to `healing-report.log` with the original locator, the healed expression, the confidence score, and the first line of the original error — this is the audit trail for whether the mechanism is doing anything real, and it must reflect genuine cache/log activity, not silently no-op.

### Wiring into `PlaywrightWrapper`

Add a `withHeal(locator, action, objName = "", actionDesc = "action", fallbackLocator = null)` method. It tries the original locator with a short (`3000ms`) `waitFor({ state: "attached" })`, and on failure calls `findHealedLocator(this.page, healTarget, originalError, objName || actionDesc)` before retrying the same `action` against the healed locator. Route the four action-oriented wrapper methods through it: `waitAndClick`, `typeText`, `typePassword`, `selectOption`. Pass `objName` through as the `hint` — it is the single strongest signal of intent available, stronger than the (possibly stale) locator string itself.

**Self-healing covers actions (click/type/select), not reads/assertions, by design.** `isVisible`, `textContent`, `count`, `allTextContents`, `filterByText`, and similar read-only wrapper methods must NOT route through `withHeal` — silently healing an assertion's target risks pointing it at the wrong element and turning a real failure into a false pass, a materially different risk than healing a click. If a project has a specific, reviewed need to heal a read-path locator, treat it as a deliberate exception and document why in that page object, not as a default.

### Constraints

- Never derive healing keywords from `error.message` or a full-page DOM dump — see rule 1 above.
- Never include `class` in the candidate scoring word set — see rule 3 above.
- Never call `page.evaluate()` with more than one positional argument.
- Never wire `withHeal` into read/assertion wrapper methods without an explicit, documented reason.

---

## Shared Fixture (`fixture`) Standards

`src/support/pageFixture.js` exports a single mutable object — this stack does **not** use a `World`/`ScenarioContext` class:

```javascript
const fixture = {
    page: undefined,
    logger: undefined,
    testData: {},
    env: "",
    subStepLogger: undefined,
    pages: {},
    api: {
        client: undefined,
        lastRequest: undefined,
        lastResponse: undefined,
        testData: {},
        activeExpected: {},
        context: undefined
    }
};

module.exports = { fixture };
```

- `fixture.pages.<pageName>` is where instantiated page objects are registered by the `Before` hook (or lazily by a step, following the same key naming as the page file, e.g. `fixture.pages.loginPage`, `fixture.pages.demoPage`).
- `fixture.testData` holds the resolved test-data row for the current scenario; `fixture.api.*` holds API-specific per-scenario state.
- Step definitions always import `{ fixture }` from `../../../support/pageFixture` and read/write through it — never create a separate parallel state store.
- Because `fixture` is a shared module-level object (not per-`World`-instance), **parallel execution is not safe with this pattern** — `config/cucumber.js` must keep `parallel: 1` unless the project is refactored to a `World`-scoped design.

---

## Hooks Standards (`src/support/hooks.js`)

| Hook | Responsibility |
|---|---|
| `BeforeAll` | Call `getEnv()`; load the default `testData.csv` (and `apiTestData.json` if present) into module-level arrays |
| `Before` | Detect `@api` tag to skip browser setup for API-only scenarios; otherwise launch/reuse the browser via `invokeBrowser()`, create context/page, instantiate page objects into `fixture.pages`, create the per-scenario Winston logger and `HTMLSubStepLogger`, resolve `@Key:`/`Env` test data and replace `<placeholder>` tokens in step text |
| `BeforeStep` / `AfterStep` | Log each step to both the sub-step HTML logger and Winston; capture a screenshot on step failure |
| `After` | Stop tracing, attach screenshot/video/trace link on pass, log failure details, close the page/context |
| `AfterAll` | Close the shared browser |

- Test data resolution (`findDataByKeyAndEnv`, `findApiCaseByKeyEnvCase`) matches on `Key`/`Env` (and `CaseId` for API cases) with case-insensitive `Env` comparison.
- Placeholder replacement (`replaceStepPlaceholders`) walks `<nested.key>` dot-paths against the resolved data row, leaving unresolved placeholders untouched (never silently blank) so a mismatch is visible in the report.

---

## Configuration Standards

- `src/helper/commonConfig/configuration.json` holds shared, non-secret settings: `testDataLocation`, `defaultTimeOut`, `playwrightTimeout`, `navigationTimeout`, `expectTimeout`.
- `src/helper/env/env.js`'s `getEnv()` loads `src/helper/env/.env.<ENV>` via `dotenv.config({ override: true, path: ... })`, driven by the `ENV` environment variable (no default — the project logs an error if `ENV` is unset, so always pass `ENV=STG`/`ENV=PRD` explicitly).
- `BASEURL`, credentials, and any secret values belong in `.env.<ENV>` files — never in `configuration.json` (shared/non-secret) or hardcoded in page objects/steps.
- `src/helper/browsers/browserManager.js`'s `invokeBrowser()` reads `BROWSER` (`chrome`/`chromium`/`firefox`/`webkit`/`safari`) and launches accordingly; it also supports a `CHROME_EXECUTABLE` override and auto-detects a system Chrome install as a fallback.

---

## API Testing Standards

This stack has first-class API testing support, organized under `src/helper/api/`:

- `client/` — the API client wrapper (built on Playwright's `APIRequestContext` or an HTTP library)
- `context/ApiContext.js` — per-scenario API state container, instantiated fresh in the `Before` hook and exposed as `fixture.api.context`
- `endpoints/` — endpoint path constants
- `models/` — plain JS request/response payload shapes
- `resolver/` — resolves placeholders/dynamic values in API test data
- `schemas/` + `validators/` — JSON schema definitions and validators (e.g., via `ajv`) for response shape assertions
- `logging/` — request/response logging for API calls, separate from the UI `logger.js`

API scenarios are tagged `@api` (in `src/test/features/api/`) and their step definitions live in `src/test/steps/api/`. The `Before` hook detects the `@api` tag and skips all browser/page/context setup, initializing only `fixture.api.*`.

---

## Test Data Standards

- Default UI test data: `src/helper/util/test-data/testData.csv`, pipe-delimited (`|` separator, via `CSVParser.parseData(filePath, "|")`), loaded once in `BeforeAll`.
- Default API test data: `src/helper/util/test-data/apiTestData.json`, loaded once in `BeforeAll` if present.
- Rows are matched by `Key`/`key` (or `TestCaseID`) plus `Env`/`env` (case-insensitive) — always include both columns for new rows.
- Additional CSV files may be introduced per module if `testData.csv` grows unwieldy, but must be wired into `BeforeAll` explicitly — there is no per-scenario `@DataFile:` tag convention in this stack (unlike the TypeScript stack); a single `@Key:` selects a row from whichever file(s) are loaded.
- Passwords/secrets referenced from test data should still resolve through environment variables where possible — do not commit real credentials into `testData.csv`.

---

## Reporting Standards

Every execution must produce:

- **Cucumber JSON report**: `test-results/cucumber-report.json`
- **Allure results/report**: `allure-results/` → `allure generate` → `allure-report/` (`npm run allure:generate`, `allure:open`, `allure:serve`)
- **Multi-Cucumber HTML report**: generated by `src/helper/report/report.js` (posttest script) via `multiple-cucumber-html-reporter`
- **Sub-step HTML badges**: `HTMLSubStepLogger` (`src/support/htmllSubStepLogger.js`) — `info`, `success`, `failure` attached per step
- **Winston logs**: per-scenario log file via `src/helper/util/logger.js`
- **Screenshots**: on step failure (`AfterStep`) and on scenario completion (`After`)
- **Video**: attached when the scenario passed and a video was recorded
- **Trace**: `test-results/trace/<scenarioId>.zip`, with an "Open Trace" link attached to the report
- **Rerun file**: `@rerun.txt` for `npm run test:failed`

---

## Code Quality Standards

- Plain JavaScript, CommonJS modules (`require`/`module.exports`) — no ES module `import`/`export`, no TypeScript syntax, no build/transpile step.
- Use `async/await` — never `.then()` chaining.
- All page object and wrapper methods are `async` where they perform I/O.
- Use the Winston `logger` (via `fixture.logger`) and the `HTMLSubStepLogger` (via `fixture.subStepLogger`) for status output — avoid bare `console.log` in step definitions and page objects (utility/bootstrap scripts like `browserManager.js`'s startup diagnostics are the accepted exception).
- Keep `PlaywrightWrapper` as the single place raw `page.locator(...)`/`page.goto(...)` calls are made from page-object code — page objects call `this.base.<method>`, not `this.page.<rawMethod>` directly, except for simple state reads (e.g., `this.page.locator(...).isVisible()`) after using the wrapper to establish state.

---

## Security Standards

- Never commit `.env.<ENV>` files under `src/helper/env/` with real secrets — ensure they're `.gitignore`d, and keep only placeholder/example values in source control.
- Never log `typePassword` values or other secrets via `logger`/`subStepLogger`.
- Use CI/CD secrets (GitHub Actions `secrets.*`) for all credentials, injected as environment variables at run time.

---

## CI/CD Standards

- `.github/workflows/ci.yml` — GitHub Actions workflow with **separate jobs for API and UI tests** (`api-tests`, and typically a `ui-tests` job depending on the same pattern), each setting `ENV` based on branch (`master`/`main` → `PRD`, else `STG`) and `BROWSER`.
- Steps: checkout → `actions/setup-node@v4` (Node 22) → `npm ci` → `npx playwright install --with-deps` (as needed) → run the relevant `npm run test:*` script → upload `test-results/`/`allure-results/` as artifacts.
- Optional `workflow_dispatch` input for ad-hoc Lighthouse URL audits.

---

## AI Generation Compliance

AI-generated Playwright + JavaScript artifacts must:

- Follow all standards in this document — plain CommonJS JavaScript, Cucumber BDD, shared `fixture` object, `PlaywrightWrapper`-mediated page objects. Never introduce TypeScript files or a `ScenarioContext`/`World` class into this stack.
- Map every generated scenario to a Test Case ID (in a comment above the scenario or in the scenario name).
- Use page objects from `src/pages/` exclusively, accessed via `fixture.pages.<pageName>` — never inline raw selectors in step files.
- Externalize all test data to the CSV/JSON files under `src/helper/util/test-data/`, referenced via `@Key:` tags and `<placeholder>` step-text tokens.
- Include the correct suite tag (`@smoke`, `@regression`, `@sanity`) and, for API scenarios, `@api` (and `@chain` if sequential).
- Output feature files to `src/test/features/{ui,api}/`, page objects to `src/pages/`, step definitions to `src/test/steps/{ui,api}/`.
- Register any new page object in `fixture.pages` inside `hooks.js`'s `Before` hook.

---

## Deliverables Checklist

Before finalizing, verify:

- [ ] `config/cucumber.js` follows the standard profile structure, no `ts-node/register`
- [ ] Feature file placed under `src/test/features/ui/` or `src/test/features/api/` with correct tags
- [ ] `@Key:` tag present on every new scenario needing test data; `<placeholder>` tokens used in step text
- [ ] Page object composes a `PlaywrightWrapper` instance (`this.base = new PlaywrightWrapper(page, logger)`), never extends a base class
- [ ] Locators declared as plain strings in a single `this.Elements` object
- [ ] `src/helper/self-healer.js` present; `waitAndClick`/`typeText`/`typePassword`/`selectOption` route through `PlaywrightWrapper.withHeal`, passing `objName` as the healing hint
- [ ] Read/assertion wrapper methods (`isVisible`, `textContent`, `count`, `allTextContents`) do NOT route through `withHeal`
- [ ] Step definitions import and use `{ fixture }` from `pageFixture.js`, never a `this: World`-style context
- [ ] New page object registered under `fixture.pages` in `hooks.js`
- [ ] No hardcoded waits (`page.waitForTimeout`)
- [ ] No hardcoded credentials — sourced from `.env.<ENV>` or test data, never inline
- [ ] `typePassword` calls never log the raw value
- [ ] API scenarios tagged `@api`, using `fixture.api.context`, no unnecessary browser/page setup
- [ ] Lint/format tooling (if configured) passes
- [ ] CI workflow references the correct `npm run test:*` script
