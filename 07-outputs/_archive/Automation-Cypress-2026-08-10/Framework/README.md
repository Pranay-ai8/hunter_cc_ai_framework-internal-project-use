# SauceDemo Cypress Automation Framework

Cypress + plain JavaScript (ES Modules) automation suite for [SauceDemo](https://www.saucedemo.com/), generated from `07-outputs/TestCases/FunctionalTestCases.md` per `03-standards/cypress-javascript-standards.md`.

## Prerequisites

- Node.js 18+
- Internet access to `https://www.saucedemo.com/` (live, public site)

## Setup

```bash
cd 07-outputs/Automation-Cypress/Framework
npm install
```

## Environment Configuration

The suite defaults `baseUrl` to `https://www.saucedemo.com/`. Override via:

```bash
export CYPRESS_BASE_URL=https://www.saucedemo.com/
```

No API base URL or secrets are required — SauceDemo has no backend/API surface (per `RequirementAnalysis.md` §13).

## Running Tests

```bash
npm run cy:open                  # interactive runner
npm run cy:run                   # headless, full spec set
npm run test:smoke                # @smoke-tagged critical path
npm run test:regression           # @regression-tagged full desktop regression
npm run test:regression:desktop   # same as above, explicit desktop path
npm run test:manual               # @manual-tagged Partial/non-deterministic scenarios (informational only)
npm run report:merge && npm run report:generate   # merge + generate mochawesome HTML report
```

## Folder Structure

```
cypress.config.js         Cypress e2e config (baseUrl, specPattern, reporter, grep plugin)
package.json
cypress/
  e2e/
    smokeSuites/                     One spec per module area with @smoke scenarios
    regressionSuites/desktop/<domain>/  One spec per module, @regression + @manual describes
  fixtures/
    SignIn/user.json                 6 fixed personas + shared password
    Checkout/customerInfo.json        Valid + boundary customer-info payloads
  pageObjects/<domain>/<module>.js   Static-class Page Objects
  support/
    commands.js
    e2e.js
utils/
  assertions/verify.js + index.js    Fluent Verify assertion class
  constants/                         Messages, URLs, button text grouped by domain
  helpers/crossModuleFunctions.js    Cross-module helpers (login-state reset, etc.)
  Interactions/                      Click, Type, Check, AliasWait, Intercept wrappers
  selectors/                        One selector file per domain + globalSels.js
.github/workflows/ci.yml            Smoke + regression CI jobs
```

## Adding a New Page Object

1. Create `cypress/pageObjects/<domain>/<module>.js` as a **static class** (never instantiated), composing `utils/Interactions/*` and `utils/assertions/Verify`.
2. Add matching selectors to `utils/selectors/<domain>.js` (or `globalSels.js` if reused across 3+ domains).
3. Add any new fixed strings to `utils/constants/<domain>/<module>.js`.

## Adding a New Spec

1. Place the spec under `cypress/e2e/smokeSuites/` (critical-path) or `cypress/e2e/regressionSuites/desktop/<domain>/` (full coverage).
2. Tag it via `describe(..., { tags: '@smoke' | '@regression' | '@manual' }, ...)`.
3. Reference the source TC ID in a leading comment or the `it(...)` title.

## The 8 Partial/Manual Scenarios

Automation-candidate = "Partial" test cases (non-deterministic personas, disputed defect behavior, or an impractical real-time wait) are generated as real `it(...)` blocks inside a separate `describe(..., { tags: '@manual' }, ...)` block in their module's regression spec file, with an explanatory comment. They are excluded from `@smoke`/`@regression` runs and can be executed on demand via `npm run test:manual`. See `../AutomationSummary.md` for the full list and rationale.
