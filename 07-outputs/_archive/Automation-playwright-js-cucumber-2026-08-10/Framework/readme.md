# SauceDemo — Playwright + Cucumber (JavaScript) Automation Framework

Playwright (driver only) + Cucumber BDD + plain JavaScript/CommonJS, generated per `03-standards/playwright-javascript-standards.md` by the QA-AI-Framework **Playwright Automation Orchestrator** (Mode A — new framework, Stack Language = JavaScript).

Target application: **SauceDemo (Swag Labs)** — https://www.saucedemo.com/, a live public reference app. Locators used throughout are the **real, documented selectors** from `07-outputs/RequirementAnalysis/RequirementAnalysis.md` §12 — no placeholders. No API test cases exist for this PRD (no public API is documented), so `src/helper/api/` was not scaffolded.

## Folder Layout

This project intentionally splits module-specific artifacts out of this Framework's own `src/`, per the orchestrator's Mode A — JavaScript output table:

```
Framework/                      <- this Node project (package.json lives here)
  src/helper/                   framework infrastructure (browsers, env, parsers, report, util, wrapper)
  src/support/                  pageFixture.js, hooks.js (registers Page Objects into fixture.pages), customTypes.js, loggers
  config/cucumber.js            Cucumber profiles — paths/require globs point at ../Scripts/
../PageObjects/<name>Page.js               one Page Object per module (camelCase file, PascalCase class)
../Scripts/features/ui/<module>.feature    one feature file per module
../Scripts/steps/ui/<module>Steps.js       one step-definition file per module
```

Because this stack has no compile step (plain Node/CommonJS), the sibling-folder layout works with ordinary relative `require()` paths — no build-plugin trick is needed here (contrast with the Java/Maven stack, which needed `build-helper-maven-plugin`).

## Prerequisites

- Node.js 20+

## Setup

```bash
cd 07-outputs/Automation/Framework
npm install
npx playwright install
cp src/helper/env/.env.PRD.example src/helper/env/.env.PRD   # defaults already work — SauceDemo is a live public site
```

## Run

```bash
ENV=PRD npm test                  # full suite
npm run test:smoke                # @smoke only
npm run test:regression           # @regression only
npm run test:failed               # rerun last failures (@rerun.txt)
npm run allure:serve              # view the Allure report
```

## Adding a New Page Object

1. Create `<name>Page.js` under `../PageObjects/`, composing `this.base = new PlaywrightWrapper(page, logger)` — never extend a base class.
2. Register it in `src/support/hooks.js`'s `Before` hook: `fixture.pages.<name>Page = new <Name>Page(fixture.page, fixture.logger);`

## Adding a New Feature File / Step Definition

1. Create `<module>.feature` under `../Scripts/features/ui/`, tagged `@smoke`/`@regression` and `@Key:<id>`.
2. Create `<module>Steps.js` under `../Scripts/steps/ui/`, importing `{ fixture }` from `../../../Framework/src/support/pageFixture`.

## Known Blockers / Things to Verify

- **`problem_user` conflict** (`inventorySteps.js`, `cartSteps.js`, `checkoutSteps.js`): the two source PRDs disagree on this persona's exact defect (see `GapAnalysis.md` Conflicting Requirements). Those scenarios are tagged `@manual` (not `@smoke`/`@regression`) pending live confirmation.
- **`error_user`'s errors are documented as "random"** — non-deterministic, kept as `@manual` observational scenarios.
- **Session timeout (10 minutes)** — a real-time wait is impractical for CI — `@manual`.
- **"About" menu destination** — undocumented in either source PRD — `@manual`.
- Checkout error text and tax formula are undocumented — assertions check structure (error visible, grand total = item total + tax) rather than exact strings/values.
