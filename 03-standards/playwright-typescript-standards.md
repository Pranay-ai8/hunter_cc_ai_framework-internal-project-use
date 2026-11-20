# Playwright Automation Standards

## Objective

Establish enterprise-grade Playwright automation standards to ensure all AI-generated scripts and frameworks are maintainable, scalable, reusable, and CI/CD-ready.

These standards apply to all Playwright automation produced by this QA-AI-Framework and are modeled on the reference implementation at `ctp-contiplus-web-qa-playwright-automation`.

---

## Technology Stack

| Layer | Choice |
|---|---|
| Automation Tool | Playwright (`@playwright/test`, driver only — no Playwright Test Runner) |
| BDD Runner | Cucumber (`@cucumber/cucumber`) |
| Language | TypeScript (strict mode) |
| Design Pattern | Page Object Model (POM), locators as getters |
| Assertions | Playwright built-in `expect` (hard) + custom soft-assert helper |
| Reporting | Cucumber HTML/JSON/JUnit + `multiple-cucumber-html-reporter` + custom email HTML report |
| Parallel Execution | Cucumber `parallel` option (defaults to `1` — sequential, override per project) |
| Data | Pipe-delimited CSV (`resources/data/*.csv`) + JSON, selected via `@Key:` / `@DataFile:` tags |
| CI/CD | GitHub Actions (reusable workflow + composite email-notification action) |
| Git Hooks | Husky + lint-staged (Prettier + ESLint on staged `.ts` files) |

> This framework uses **Cucumber as the test runner and Playwright as the browser driver** — it does **not** use `@playwright/test`'s own test runner (`test()`, `test.describe()`, fixtures). Do not generate `*.spec.ts` files using `test()`/`test.describe()` unless the project explicitly requests a non-BDD Playwright Test setup.

---

## Project Structure

```
<project-root>/
├── config/
│   └── cucumber.js                 # Cucumber profiles: default, rerun
├── features/                       # Gherkin feature files — folder = intent, tag = filter
│   ├── smoke/
│   │   └── <module>.feature
│   ├── regression/
│   │   └── <module>.feature
│   ├── e2e/
│   │   └── <module>.feature
│   └── sanity/
│       └── sanity.feature
├── src/
│   ├── config/
│   │   ├── ConfigurationManager.ts # Singleton: loads resources/.env.<ENV> + configuration.json
│   │   ├── ApiEndpoints.ts         # API endpoint path constants
│   │   └── env.ts                  # Environment helpers
│   ├── helper/
│   │   ├── hooks.ts                # BeforeAll/Before/BeforeStep/AfterStep/After/AfterAll
│   │   ├── browser-factory.ts      # Launches chromium/chrome/edge/firefox/webkit
│   │   ├── api-factory.ts          # Builds APIRequestContext with auth headers
│   │   └── auth-setup.ts           # saveAuthState() for @authenticated storageState reuse
│   ├── pages/
│   │   ├── base.page.ts            # BasePage — all page objects extend this
│   │   └── <module>.page.ts        # One page object per module (kebab-case file, PascalCase class)
│   ├── steps/
│   │   └── <module>.steps.ts       # Step definitions — thin, delegate to page objects
│   ├── report/
│   │   ├── custom/                 # init/run/report scripts driving Cucumber HTML report
│   │   └── email-html/             # HtmlEmailReport + ResultStore — post-run email summary
│   └── utils/
│       ├── ScenarioContext.ts      # Cucumber World — per-scenario state + lazy page-object getters
│       ├── customTypes.ts          # Custom Cucumber parameter types ({jObj}, {list})
│       ├── htmlSubStepLogger.ts    # HTML badge sub-step logging for the Cucumber report
│       ├── logger.ts               # winston logger factory
│       ├── assert.ts               # Soft-assertion helpers
│       ├── CSVParser.ts            # Pipe-delimited CSV loader for test data
│       ├── passwordResolver.ts     # Resolves passwords from resources/data/.env.passwords
│       ├── self-healer.ts          # findHealedLocator() — see "Self-Healing Locators" below
│       └── pdf-utils.ts            # PDF comparison/extraction helpers (if application needs it)
├── resources/
│   ├── .env.DEV / .env.STG / .env.SBX / .env.PRD
│   ├── configuration.json          # Default browser/headless/timeouts, overridable by env vars
│   └── data/
│       ├── .env.passwords          # Centralized password values, never real secrets in CSV
│       ├── smokedata.csv
│       ├── sanitydata.csv
│       ├── e2edata.csv
│       ├── <module>data.csv        # One regression data file per module
│       └── testdata.csv            # Legacy / shared keys only — do not add new scenarios here
├── docs/
│   ├── test-suites/                # SINGLE SOURCE OF TRUTH — one .md per TC, KPI index per module
│   ├── test-link/                  # TestLink-aligned mirror of test-suites (if TestLink integration used)
│   ├── gap/                        # Gap analysis notes per module
│   ├── add-scenario.md             # Step-by-step guide for adding a new scenario
│   └── test-coverage.md            # Global scenario registry
├── .github/
│   ├── workflows/
│   │   ├── web_automation.yml      # Entry-point trigger (push/PR/workflow_dispatch)
│   │   └── reusable_workflow.yml   # npm ci → playwright install → test:sanity → email report
│   ├── actions/email_notification/ # Composite SMTP email action
│   ├── agents/                     # Copilot custom agents (e.g., smoke-writer, regression-writer, test-explorer)
│   └── copilot-instructions.md     # Repo-specific Copilot instructions (architecture + conventions)
├── package.json
├── tsconfig.json                   # strict mode, path aliases: @/*, @helpers/*, @pages/*, @steps/*, @utils/*
├── eslint.config.mjs
├── .prettierrc / .prettierignore
└── .husky/                         # pre-commit: lint-staged
```

---

## Cucumber Configuration Standards

Every project must define `config/cucumber.js` with:

```javascript
const common = {
  require: [
    'src/utils/ScenarioContext.ts',
    'src/helper/hooks.ts',
    'src/utils/customTypes.ts',
    'src/steps/**/*.ts',
  ],
  requireModule: ['ts-node/register'],
  format: [
    'progress-bar',
    'html:test-results/cucumber-report.html',
    'json:test-results/cucumber-report.json',
    'junit:test-results/cucumber-report.xml',
  ],
  formatOptions: { snippetInterface: 'async-await' },
  dryRun: false,
  parallel: 1,
};

module.exports = {
  default: { ...common, paths: ['features/**/*.feature'], strict: true, retry: 0 },
  rerun: { ...common, paths: ['@rerun.txt'] },
};
```

- `paths: ['features/**/*.feature']` picks up all subfolders automatically — do not hardcode individual feature files.
- Keep a `rerun` profile pointed at `@rerun.txt` to support `npm run test:failed`.

---

## Feature File Standards

### Folder = intent, tag = execution filter

| Folder | Tag | Environment | Data File |
|---|---|---|---|
| `features/smoke/` | `@smoke` | STG | `smokedata.csv` |
| `features/regression/` | `@regression` | STG | `<module>data.csv` |
| `features/e2e/` | `@e2e` | STG | `e2edata.csv` |
| `features/sanity/` | `@sanity` | PRD | `sanitydata.csv` |

- `@sanity` scenarios always live in `features/sanity/`, never in `features/smoke/`.
- Tag scenarios under active development with `@inprogress` and remove the tag once stable.
- Tag scenarios that require a pre-authenticated session with `@authenticated` (see Auth State Persistence below).

### Scenario tagging convention

```gherkin
@DataFile:resources/data/smokedata.csv @Key:valid_login @smoke
Scenario: Successful login with valid credentials
  Given User is at Login page
  Given Login with "td.username" and "td.password.admin"
  Then Verify Home page is displayed
```

- Always specify `@DataFile:` explicitly on every new scenario — never rely on the default fallback to `testdata.csv`.
- `@Key:<id>` selects the data row within that file.
- Step text placeholders in the form `td.<column>` are replaced at runtime from the resolved data row (e.g., `td.username`, `td.password.admin`).

---

## Page Object Standards

### BasePage

All page objects **must extend `BasePage`** (`src/pages/base.page.ts`):

```typescript
// src/pages/base.page.ts
import { Page } from '@playwright/test';
import { HTMLSubStepLogger } from '../utils/htmlSubStepLogger';

export default class BasePage {
  constructor(
    protected page: Page,
    protected subStepLogger?: HTMLSubStepLogger,
  ) {}

  async waitForLoadingToComplete(): Promise<void> {
    await this.page.waitForLoadState('domcontentloaded');
  }

  protected async logInfo(message: string): Promise<void> {
    await this.subStepLogger?.info(message);
  }

  protected async logSuccess(message: string): Promise<void> {
    await this.subStepLogger?.success(message);
  }

  protected async logWarning(message: string): Promise<void> {
    await this.subStepLogger?.warning(message);
  }

  protected async logFailure(message: string): Promise<void> {
    await this.subStepLogger?.logFailure(message);
  }
}
```

- Constructor signature is always `constructor(page: Page, subStepLogger?: HTMLSubStepLogger)`. `page` is required; `subStepLogger` is optional.
- Page objects never redeclare `page` or `subStepLogger` — they are inherited `protected` fields from `BasePage`.
- Use the inherited `logInfo/logSuccess/logWarning/logFailure` helpers for HTML sub-step logging — never call `this.subStepLogger` directly from a page object.

### File & Class Naming

- File name: `<module>.page.ts` in kebab-case — e.g., `login.page.ts`, `site-form.page.ts`, `conti-alert.page.ts`.
- Class name: PascalCase — e.g., `export class LoginPage extends BasePage`.

### Locators — declared as getters, not constructor fields

```typescript
// src/pages/login.page.ts
import { expect, Locator, Page } from '@playwright/test';
import BasePage from './base.page';
import { HTMLSubStepLogger } from '../utils/htmlSubStepLogger';
import { ApiEndpoints } from '../config/ApiEndpoints';

export class LoginPage extends BasePage {
  constructor(page: Page, subStepLogger?: HTMLSubStepLogger) {
    super(page, subStepLogger);
  }

  // ==================== Locators ====================

  get usernameInput(): Locator {
    return this.page.locator('#email');
  }

  get loginButton(): Locator {
    return this.page.getByRole('button', { name: 'Login' });
  }

  // ==================== Validations ====================

  async isLoginPageDisplayed(): Promise<boolean> {
    return this.usernameInput.isVisible();
  }

  // ==================== Actions ====================

  async login(username: string, password: string): Promise<void> {
    await this.logInfo('Entering username');
    await this.usernameInput.fill(username);
    await this.logInfo('Submitting login form');
    await Promise.all([
      this.page.waitForResponse(
        (r) => r.url().includes(ApiEndpoints.LOGIN) && r.request().method() === 'POST',
      ),
      this.loginButton.click(),
    ]);
    await this.logSuccess('Login submitted successfully');
  }
}
```

- Locators are `get` properties returning `Locator` — **not** constructor-assigned `private readonly` fields, and **not** a shared `LocatorRepo` class.
- There is no separate locators file per page — locators live inline in the page class, grouped under a `// ==================== Locators ====================` section comment.
- Required sections in order (as comment banners): **Locators → Validations → Actions**.
- Methods represent business actions (`login`, `handleCookiePopup`, `changePassword`) — never expose raw Playwright calls to step definitions.
- Wait for the relevant network response via `page.waitForResponse(...)` inside `Promise.all` when submitting forms that trigger an API call — do not use fixed waits.
- `page.goto()` must always build a full URL via `ConfigurationManager.config().baseUrl` — there is no `baseURL` configured on the Playwright browser context itself.

### Locator naming (element-type suffix)

| Suffix | Element type | Example getter |
|---|---|---|
| `Input` | Text box / input | `get usernameInput()` |
| `Button` | Button | `get loginButton()` |
| `Label` / `Header` | Label / heading text | `get pageHeader()` |
| `Dropdown` / `Select` | Dropdown | `get statusDropdown()` |
| `Checkbox` | Checkbox | `get rememberMeCheckbox()` |
| `Link` | Link | `get forgotPasswordLink()` |

Prefer Playwright semantic locators (`getByRole`, `getByLabel`, `getByPlaceholder`, `getByText`) over CSS/XPath — see `03-standards/locator-standards.md` for full priority order.

---

## Live DOM Verification

See `03-standards/locator-standards.md`'s "Live DOM Verification" section for the full rationale and cross-stack process — this section covers only this stack's mechanics.

Before writing a module's page object for the first time, run a throwaway script (a one-off `.ts` snapshot tool via `ts-node`, deleted after use — not part of the generated framework) that launches Playwright directly, navigates to the module's screen (performing any required setup, e.g. login), and collects the same candidate shape `self-healer.ts` gathers at runtime (see that section's `page.evaluate` call — reuse it verbatim rather than writing a second version). Cross-check every locator you're about to declare as a `get` property against this snapshot before writing it — not after the fact. Save the raw snapshot to `docs/dom-snapshots/<module>.json` (create the folder if absent) so the verification is auditable later, and note any documented-vs-actual discrepancy directly in the generation summary.

---

## Self-Healing Locators

Every project generated for this stack must include a self-healing layer so a stale/renamed locator degrades to an automatic, logged recovery instead of an immediate hard failure. This is **mandatory scaffold**, not optional.

The TypeScript stack's convention (locators as `get` properties returning an already-resolved `Locator`, not a raw string — see "Page Object Standards" above) means healing is wired differently than the JavaScript/Java stacks, which hold a raw selector string right up until the moment of use. Here, the **hint string is the only signal available** for keyword matching — there is no supported public API to introspect an existing `Locator`'s underlying selector, so do not attempt to parse or `eval` one back out of it. Every action that wants healing coverage must pass a clear, human-readable hint (e.g. `"Login Button"`), and this stack's TypeScript strict-mode/lint rules (`no-new-func`, `@typescript-eslint/no-explicit-any: warn`) also make the JavaScript stack's `eval`-based expression-string trick inappropriate here — build healed locators through a typed switch instead, never `new Function(...)`.

### Module: `src/utils/self-healer.ts`

```typescript
import { Locator, Page } from '@playwright/test';

export interface HealResult {
  success: boolean;
  locator: Locator | null;
  confidence: number;
}

export async function findHealedLocator(page: Page, hint: string, error: Error): Promise<HealResult> { /* ... */ }
```

**Algorithm** (identical scoring model to `03-standards/playwright-javascript-standards.md`'s "Self-Healing Locators" section — read that section for the full rationale; only the TypeScript-specific deltas are called out here):

1. Tokenize `hint` into a keyword word-SET (`normalizeWords` — strip punctuation, lowercase, split on whitespace, keep tokens ≥3 chars, dedupe). There is no separate `locatorKeywords`/`combinedKeywords` pass here (no raw locator string exists to tokenize) — hint alone is the query.
2. Collect DOM candidates via a **single-argument** `page.evaluate(({ selectors, limit }) => {...}, { selectors, limit })` — Playwright's `evaluate()` throws `"Too many arguments"` on more than one positional argument after the page function; this is the exact crash the JavaScript implementation hit first and is trivial to reintroduce if this module is ported carelessly.
3. Score each candidate by precision/recall (F1) over a word set built from `id`, `name`, `role`, `ariaLabel`, `dataTestId`, `placeholder`, `forAttr`, `text` — **excluding `class`** (styling noise, not semantic identity; see the JavaScript standard's rule 3 for the concrete failure this caused). Add `+0.15`/`+0.1` bonuses when a matched keyword is directly contained in `dataTestId`/`id`.
4. Resolve the best-scoring candidate (score ≥ `CONFIDENCE_THRESHOLD = 0.75`) to an actual `Locator` via a typed switch — no string-building, no `eval`:

```typescript
function resolveCandidate(page: Page, candidate: Candidate): Locator {
  if (candidate.role && candidate.name) return page.getByRole(candidate.role as never, { name: candidate.name });
  if (candidate.dataTestId) return page.getByTestId(candidate.dataTestId);
  if (candidate.ariaLabel) return page.getByLabel(candidate.ariaLabel);
  if (candidate.text && ['button', 'a', 'label', 'span'].includes(candidate.tag)) return page.getByText(candidate.text);
  if (candidate.id) return page.locator(`#${candidate.id}`);
  if (candidate.name) return page.locator(`[name="${candidate.name}"]`);
  const firstClass = candidate.class?.split(/\s+/).find(Boolean);
  if (firstClass) return page.locator(`.${firstClass}`);
  return page.locator(candidate.tag);
}
```

5. **Validate** the resolved locator actually matches something (`await locator.count() > 0`) before returning success.
6. **Cache** successful heals for 1 hour, keyed by `${page.url()}|${hint}`, in `healing-cache.json` (resolved relative to the project root, alongside `healing-report.log`) — both files live outside `src/` since they are runtime artifacts, not source.
7. **Log** every successful heal to `healing-report.log`: hint, the confidence score, and the first line of the original error's message.

### Wiring into `BasePage`

Add a `protected async withHeal<T>(locator: Locator, action: (l: Locator) => Promise<T>, hint: string): Promise<T>` method to `src/pages/base.page.ts`. It attempts `locator.waitFor({ state: 'attached', timeout: 3000 })` then `action(locator)`; on failure, it calls `findHealedLocator(this.page, hint, error)` and retries `action` against the healed `Locator` if one was found above the confidence threshold, otherwise rethrows the original error.

Action methods call `this.withHeal(...)` around the interaction, passing the SAME getter used elsewhere for reads:

```typescript
async login(username: string, password: string): Promise<void> {
  await this.withHeal(this.usernameInput, (l) => l.fill(username), 'Username');
  await this.withHeal(this.passwordInput, (l) => l.fill(password), 'Password');
  await Promise.all([
    this.page.waitForResponse((r) => r.url().includes(ApiEndpoints.LOGIN) && r.request().method() === 'POST'),
    this.withHeal(this.loginButton, (l) => l.click(), 'Login'),
  ]);
}
```

**Self-healing covers actions (fill/click/selectOption), not reads/assertions, by design** — `isLoginPageDisplayed()`-style validation methods and `assert*` helpers must NOT route through `withHeal`. Silently healing an assertion's target risks pointing it at the wrong element and turning a real failure into a false pass — a materially different risk than healing a click. See the JavaScript standard's "Self-Healing Locators" section for the full rationale; it applies identically here.

### Constraints

- Never attempt to introspect or `eval` a `Locator`'s underlying selector — pass a hint string instead.
- Never use `new Function(...)`/`eval` to rebuild a healed locator — resolve it through a typed switch (see step 4 above); this stack's lint rules (`no-new-func`) forbid it anyway.
- Never call `page.evaluate()` with more than one positional argument.
- Never include `class` in the candidate scoring word set.
- Never wire `withHeal` into `assert*`/validation methods without an explicit, documented reason.

---

## ScenarioContext (Cucumber World) Standards

`src/utils/ScenarioContext.ts` extends Cucumber's `World` and is the single source of per-scenario state — no global singletons, enabling safe parallel execution:

```typescript
import { World, setWorldConstructor } from '@cucumber/cucumber';
import { APIRequestContext, BrowserContext, Page } from '@playwright/test';
import { Logger } from 'winston';
import { HTMLSubStepLogger } from './htmlSubStepLogger';
import { ApiFactory } from '../helper/api-factory';
import { LoginPage } from '../pages/login.page';

export class ScenarioContext extends World {
  page!: Page;
  context?: BrowserContext;
  logger!: Logger;
  subStepLogger!: HTMLSubStepLogger;
  apiFactory?: ApiFactory;
  api?: APIRequestContext;
  testData: Record<string, unknown> = {};

  private _loginPage?: LoginPage;
  get loginPage(): LoginPage {
    if (!this._loginPage) this._loginPage = new LoginPage(this.page, this.subStepLogger);
    return this._loginPage;
  }
}

setWorldConstructor(ScenarioContext);
```

### Registering a new page object

```typescript
// 1. Import
import { OrdersPage } from '../pages/orders.page';

// 2. Private field + lazy getter
private _ordersPage?: OrdersPage;

get ordersPage(): OrdersPage {
  if (!this._ordersPage) this._ordersPage = new OrdersPage(this.page, this.subStepLogger);
  return this._ordersPage;
}
```

- Every page object is exposed via a lazy getter on `ScenarioContext`, never instantiated directly in a step file (`new XyzPage(...)` in step definitions is forbidden).
- `testData: Record<string, unknown>` is the mechanism for passing data between steps within a scenario.

---

## Step Definition Standards

```typescript
import { Given, Then, When } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

Given('Login with {string} and {string}', async function (
  this: ScenarioContext,
  username: string,
  password: string,
) {
  await this.loginPage.login(username, password);
  await this.homePage.handleCookiePopup();
});

Then('Verify Home page is displayed', async function (this: ScenarioContext) {
  await expect(this.page).toHaveURL(/secure\/dashboard/, { timeout: 90000 });
});
```

- Always type `this` as `ScenarioContext`.
- Access page objects only via `this.<pageName>` lazy getters.
- Never reference locators directly in a step file — all UI interaction goes through page object methods.
- Keep steps thin: one or two page-object method calls per step, no business logic or logging inline.
- File name: `<module>.steps.ts` in `src/steps/`.

---

## Hooks Standards (`src/helper/hooks.ts`)

| Hook | Responsibility |
|---|---|
| `BeforeAll` | Launch a shared browser via `BrowserFactory`; call `saveAuthState(browser)` for `@authenticated` support |
| `Before` | Create per-scenario context/page; load test data from CSV/JSON via `@Key`/`@DataFile`; replace `td.*` placeholders; apply `storageState` when scenario is tagged `@authenticated`; reset soft-assert buckets |
| `BeforeStep` / `AfterStep` | Log each step; capture screenshot on failure |
| `After` | Collect soft-assertion failures and throw a summary error if any accumulated even though the scenario "passed" |
| `AfterAll` | Generate the HTML email report via `HtmlEmailReport` |

---

## Auth State Persistence (`@authenticated`)

- `src/helper/auth-setup.ts` exposes `saveAuthState(browser)` (one-time headless admin login, saves storage state to `.auth/user.json`) and `getAuthStatePath()`.
- Scenarios tagged `@authenticated` start the browser context with `storageState: '.auth/user.json'` — no login steps needed.
- Requires `ADMIN_USERNAME` in `resources/.env.<ENV>` and the matching password entry in `resources/data/.env.passwords`.
- `.auth/` must always be in `.gitignore`.

---

## Configuration Standards

- `src/config/ConfigurationManager.ts` is a singleton. It loads the active `resources/.env.<ENV>` file (selected via the `ENV` variable, default `STG`) merged with `resources/configuration.json`, and exposes a typed `AppConfig` (`baseUrl`, `authToken`, `orgId`, `browser`, `headless`, timeouts, paths).
- `src/config/ApiEndpoints.ts` centralizes API path constants used both by page objects (for `waitForResponse` matching) and by `ApiFactory`.
- `src/helper/browser-factory.ts` launches the browser named in `ConfigurationManager.config().browser` — support `chromium`, `chrome`, `edge`, `firefox`, `webkit`.
- `src/helper/api-factory.ts` builds a Playwright `APIRequestContext` with `Authorization` and `X-ORG-ID` headers, exposed on `ScenarioContext` as `this.api` / `this.apiFactory`.
- Never hardcode base URLs, tokens, or org IDs in page objects or steps — always read from `ConfigurationManager`.

---

## Test Data Standards

- Primary format: pipe-delimited CSV under `resources/data/`, one file per suite type/module (`smokedata.csv`, `sanitydata.csv`, `e2edata.csv`, `<module>data.csv`).
- Every new scenario must reference its data file explicitly with `@DataFile:resources/data/<file>.csv` and select its row with `@Key:<id>`.
- Do not add new scenarios to `resources/data/testdata.csv` — it is legacy/shared only.
- Passwords are centralized in `resources/data/.env.passwords` and resolved via `src/utils/passwordResolver.ts` — never place real passwords inline in CSV data.
- `src/utils/CSVParser.ts` is the single loader for this format — do not introduce a second CSV parsing utility.

---

## Documentation Standards (`docs/`)

`docs/test-suites/` is the single source of truth for test case documentation — every test case has its own `.md` file with source references, exact form field names, suggested Gherkin, test data keys, and current automation status.

### Workflow: docs → feature file → automation

```
1. Read docs/test-suites/<module>/tc-<id>-<title>.md   ← THE SPEC
2. Create features/<suite>/<module>.feature             ← Gherkin from TC spec
3. Create/update src/pages/<module>.page.ts             ← Page object
4. Write src/steps/<module>.steps.ts                    ← Step definitions
5. Add rows to resources/data/<module>data.csv          ← Test data
6. Update docs/test-suites/<module>/index.md            ← Mark TC status
7. Update docs/test-suites/index.md                     ← Update global KPI row
8. Update docs/test-coverage.md                         ← Register scenario
```

Status values: `📋 Planned`, `🔵 Active — In Progress`, `🟢 Active — Automated`, `⚠️ Failing`, `🚫 Deprecated`.

**Updating `docs/test-suites/` status is not optional** — every automation generation must update both the module-level index and the global index when a TC's status changes.

---

## Reporting Standards

Every execution must produce:

- **Cucumber HTML/JSON/JUnit report**: `test-results/cucumber-report.{html,json,xml}`
- **Enhanced multi-report**: `multiple-cucumber-html-reporter` output driven by `src/report/custom/` (`init.ts` pretest, `report.ts` posttest)
- **Email HTML summary**: generated in `AfterAll` via `src/report/email-html/html-report.ts` + `result-store.ts`
- **Sub-step HTML badges**: attached per step via `HTMLSubStepLogger` (`success`, `info`, `warning`, `failure`, `softFailure`)
- **Screenshots**: captured on step failure via `AfterStep`
- **Rerun file**: `@rerun.txt` for `npm run test:failed`

---

## Code Quality Standards

- All files must use TypeScript strict mode with path aliases (`@/*`, `@helpers/*`, `@pages/*`, `@steps/*`, `@utils/*`).
- Use `async/await` — never `.then()` chaining.
- All page object and step methods are `async`.
- Avoid `any` (`@typescript-eslint/no-explicit-any: warn`) — use proper interfaces/types.
- All async calls must be awaited (`@typescript-eslint/no-floating-promises: error`).
- Use the `winston` logger, never `console.log` (`no-console: warn`; exceptions: report scripts and env helpers).
- Formatting via Prettier: single quotes, semicolons, trailing commas (`all`), 120-char print width, 2-space indent, LF line endings.
- Husky + lint-staged run `prettier --write` and `eslint --fix` on staged `.ts` files pre-commit.

---

## npm Script Standards

Every project must expose these scripts (adjust naming to the project's suites):

```json
{
  "scripts": {
    "pretest": "npx ts-node src/report/custom/init.ts",
    "test": "cucumber-js --config=config/cucumber.js",
    "posttest": "npx ts-node src/report/custom/report.ts",
    "test:failed": "cucumber-js -p rerun @rerun.txt || true && npx ts-node src/report/custom/report.ts",
    "test:smoke": "ENV=STG TAGS=\"--tags @smoke\" npx ts-node src/report/custom/runCucumberWithReport.ts",
    "test:regression": "ENV=STG TAGS=\"--tags @regression\" npx ts-node src/report/custom/runCucumberWithReport.ts",
    "test:e2e": "ENV=STG TAGS=\"--tags @e2e\" npx ts-node src/report/custom/runCucumberWithReport.ts",
    "test:sanity": "ENV=PRD TAGS=\"--tags @sanity\" npx ts-node src/report/custom/runCucumberWithReport.ts",
    "lint": "eslint 'src/**/*.ts'",
    "lint:fix": "eslint 'src/**/*.ts' --fix",
    "format": "prettier --write 'src/**/*.ts'",
    "format:check": "prettier --check 'src/**/*.ts'",
    "prepare": "husky"
  }
}
```

---

## CI/CD Standards

- `.github/workflows/web_automation.yml` — entry-point trigger on push to main branches, PRs, and `workflow_dispatch`.
- `.github/workflows/reusable_workflow.yml` — reusable job: `npm ci` → `npx playwright install --with-deps` → run the target suite script → email report via the composite action.
- `.github/actions/email_notification/action.yml` — composite SMTP action for sending the run summary.
- Use repository secrets for all credentials — never inline them in workflow YAML.

---

## Security Standards

- Never commit `.env.*` files under `resources/` or `resources/data/.env.passwords` — ensure they are `.gitignore`d, and provide safe example templates where applicable.
- `.auth/` (persisted login storage state) must always be gitignored.
- Never log passwords, tokens, or PII — including in the HTML sub-step logger or email report.
- Use CI/CD secrets for all environment credentials.

---

## AI Generation Compliance

AI-generated Playwright + Cucumber artifacts must:

- Follow all standards in this document, including the Cucumber/BDD structure — do not fall back to `@playwright/test`'s own test runner syntax.
- Map every generated scenario to a Test Case ID documented under `docs/test-suites/<module>/`.
- Use page objects from `src/pages/` exclusively — never inline locators in feature or step files.
- Externalize all test data to `resources/data/*.csv` with explicit `@DataFile:`/`@Key:` tags.
- Include the correct suite tag (`@smoke`, `@regression`, `@e2e`, `@sanity`) and, if still under development, `@inprogress`.
- Output feature files to `features/<suite>/`, page objects to `src/pages/`, step definitions to `src/steps/`.
- Register any new page object on `ScenarioContext` as a lazy getter.
- Update `docs/test-suites/` status tables when generation is complete.

---

## Deliverables Checklist

Before finalizing, verify:

- [ ] `config/cucumber.js` follows the standard profile structure
- [ ] Feature file placed in the correct `features/<suite>/` folder with correct tag
- [ ] `@DataFile:` and `@Key:` tags present on every new scenario
- [ ] Page object extends `BasePage`, locators declared as getters
- [ ] Page object sections ordered Locators → Validations → Actions
- [ ] Step definitions type `this` as `ScenarioContext` and use lazy page-object getters
- [ ] No locators used directly in step files
- [ ] No hardcoded waits (`waitForTimeout`)
- [ ] No hardcoded credentials — passwords resolved via `passwordResolver`
- [ ] `src/utils/self-healer.ts` present; action methods (fill/click/selectOption) route through `BasePage.withHeal`, passing a human-readable hint
- [ ] `assert*`/validation methods do NOT route through `withHeal`
- [ ] New page object registered on `ScenarioContext`
- [ ] `docs/test-suites/<module>/index.md` and `docs/test-suites/index.md` updated
- [ ] Lint (`npm run lint`) and format (`npm run format:check`) pass
- [ ] CI/CD workflow references the correct suite script
