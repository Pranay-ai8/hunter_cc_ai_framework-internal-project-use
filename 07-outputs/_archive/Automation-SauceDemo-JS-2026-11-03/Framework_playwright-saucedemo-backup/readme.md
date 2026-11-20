# SauceDemo — Playwright + JavaScript + Cucumber (BDD) Automation Suite

Automation suite for **SauceDemo / Swag Labs** (https://www.saucedemo.com/), generated per `03-standards/playwright-javascript-standards.md`.

## Stack

- Playwright (driver only) + Cucumber (`@cucumber/cucumber`) as the BDD test runner
- Plain JavaScript / CommonJS — no TypeScript
- Page Object Model: pages compose a shared `PlaywrightWrapper` (never extend a base class)
- Self-healing locators (`src/helper/self-healer.js`) wired into `waitAndClick`/`typeText`/`typePassword`/`selectOption`

## Folder split (Mode A)

This generation run uses a 3-way split under `07-outputs/Automation/`:

```
Automation/
├── Framework/     <- this folder: scaffold, helpers, support, config, test data
├── PageObjects/   <- one <name>Page.js per module
├── Scripts/
│   ├── features/ui/   <- one <module>.feature per module
│   └── steps/ui/       <- one <module>Steps.js per module
├── docs/dom-snapshots/ <- Live DOM Verification snapshots (one per module)
├── healing-cache.json  <- self-healing cache (created at runtime)
├── healing-report.log  <- self-healing audit log (created at runtime)
└── AutomationSummary.md
```

`config/cucumber.js`'s `paths`/`require` point at `../Scripts/features` and `../Scripts/steps` (relative to `Framework/` as the working directory), and `hooks.js` requires page objects from `../../../PageObjects/`. Run everything from inside `Framework/` — the paths assume that cwd.

## Setup

```bash
cd 07-outputs/Automation/Framework
npm install
npx playwright install chromium
```

Environment file: `src/helper/env/.env.PRD` already points at the live public app (`BASEURL=https://www.saucedemo.com/`) with the 6 fixed SauceDemo persona usernames — these are public, documented demo credentials (not real secrets), all sharing password `secret_sauce`. Copy `.env.PRD.example` if you need to recreate it.

## Running

```bash
npm run test:smoke        # @smoke tagged scenarios, ENV=PRD
npm run test:regression   # @regression tagged scenarios, ENV=PRD
npm run test:sanity       # @sanity tagged scenarios, ENV=PRD
npm run test:failed       # re-runs scenarios listed in @rerun.txt from the last run
```

Reports: `test-results/cucumber-report.json`, `test-results/html-report/index.html` (posttest), `allure-results/` (`npm run allure:generate` / `allure:serve`), per-scenario Winston logs under `test-results/logs/`, screenshots on step failure under `test-results/screenshots/`, and Playwright traces under `test-results/trace/`.

## Adding a new Page Object

1. Create `<name>Page.js` under `../PageObjects/`, composing `new PlaywrightWrapper(page, logger)` in the constructor and declaring locators as plain strings in `this.Elements`.
2. Require it in `src/support/hooks.js`'s `Before` hook and register it on `fixture.pages.<name>`.

## Adding a new feature/scenario

1. Add the scenario to the relevant `.feature` file under `../Scripts/features/ui/`, tagged with `@smoke`/`@regression`/`@sanity` and `@Key:<id>` for data-driven scenarios.
2. Add/extend the matching `<module>Steps.js` under `../Scripts/steps/ui/`, importing `{ fixture }` from `../../../Framework/src/support/pageFixture` and delegating to `fixture.pages.<name>` methods.
3. Add a new row to `src/helper/util/test-data/testData.csv` (pipe-delimited) keyed by the new `@Key:`.

## Self-Healing Locators

`waitAndClick`, `typeText`, `typePassword`, and `selectOption` in `src/helper/wrapper/PlaywrightWrappers.js` route through `withHeal()`, which — on a locator failure — calls `src/helper/self-healer.js`'s `findHealedLocator()` to score live DOM candidates (precision/recall F1, `class` excluded from scoring) and retries the action against the best match above a 0.75 confidence threshold. Successful heals are cached (1 hour TTL) in `healing-cache.json` and logged to `healing-report.log`. Read/assertion methods (`isVisible`, `textContent`, `count`, `allTextContents`) never route through healing.
