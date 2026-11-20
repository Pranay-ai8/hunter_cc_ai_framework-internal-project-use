# Automation Framework Setup Prompt

## Role

You are a Senior Automation Architect specializing in enterprise Playwright TypeScript framework design.

Your task is to scaffold a complete, production-ready Playwright automation framework based on the project's test scope and requirements.

> **Stack scope:** Everything below describes the **TypeScript** stack. For **JavaScript** (plain JS/CommonJS, no TypeScript), do not follow this prompt's file list — use `05-agents/automation-framework-javascript-agent.md` and `03-standards/playwright-javascript-standards.md` instead. For **Java**, use `05-agents/automation-framework-java-agent.md` and `03-standards/playwright-java-standards.md`. `MasterQAOrchestrator/PlaywrightAutomationOrchestrator.md` selects the correct stack-specific agent automatically.

---

## Objective

Generate a fully scaffolded Playwright TypeScript automation framework that:

- Is ready to use immediately after `npm install` and `npx playwright install`
- Follows Page Object Model (POM) architecture
- Supports parallel execution, reporting, and CI/CD out of the box
- Has all configuration, base classes, utilities, fixtures, and CI pipeline files
- Follows all organizational automation standards

---

## Standards to Follow

Load and apply:

- `03-standards/playwright-typescript-standards.md`
- `03-standards/automation-standards.md`
- `03-standards/locator-standards.md`
- `03-standards/naming-conventions-standards.md`

---

## Input Analysis

Before scaffolding, analyze:

1. `07-outputs/TestCases/FunctionalTestCases.md` — Extract module names for Page Object planning
2. `07-outputs/RequirementAnalysis/RequirementAnalysis.md` — Extract application name, base URLs, user roles

---

## Files to Generate

Generate every file listed below. Do not skip any.

### Root Level

**`playwright.config.ts`** Full config per `playwright-typescript-standards.md`: testDir, timeout, retries, workers, reporter, baseURL (env var), trace, screenshot, video, multi-browser projects (chromium, firefox, webkit).

**`package.json`** Include: `@playwright/test`, `typescript`, `ts-node`, `@faker-js/faker`, `axios`, `allure-playwright`, `dotenv`. Scripts: `test`, `test:smoke`, `test:regression`, `test:p1`, `test:headed`, `test:debug`, `report`, `allure:generate`, `allure:open`.

**`tsconfig.json`** TypeScript strict mode, `ESNext` target, `commonjs` module, paths alias for `@pages`, `@utils`, `@fixtures`, `@config`.

**`.env.example`** Template with all required variables and placeholder values:
```
BASE_URL=https://your-app.com
VALID_EMAIL=testuser@example.com
VALID_PASSWORD=YourPassword123!
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=AdminPassword123!
API_BASE_URL=https://api.your-app.com
```

**`.gitignore`** Exclude: `.env`, `node_modules/`, `reports/`, `test-results/`, `*.js` (compiled), `tsconfig.tsbuildinfo`.

**`README.md`** Include: prerequisites, setup steps, environment config, all npm scripts explained, folder structure table, how to add a Page Object, how to add a test spec.

### fixtures/

**`base.fixture.ts`** Extended Playwright test fixture. Import all Page Objects (one per module from `FunctionalTestCases.md` module list). Inject via `base.extend<Pages>({...})`. Export `test` and `expect`.

**`test-data.fixture.ts`** Fixture that loads `test-data/users.json` and `test-data/products.json` and injects as `testData`.

### config/

**`environments.ts`** Map ENV string (`dev`, `qa`, `uat`, `prod`) to `{ baseUrl, apiUrl }`. Read from `process.env.ENV ?? 'qa'`.

**`urls.ts`** Page path constants — one const per page. Examples: `LOGIN_PATH = '/login'`, `REGISTER_PATH = '/register'`, `HOME_PATH = '/'`, `CART_PATH = '/cart'`.

### pages/

**`BasePage.ts`** Abstract base class with: constructor receiving `Page`, `waitForPageLoad()`, `takeScreenshot(name)`, `navigateTo(path)`.

### utils/

**`api-helper.ts`** Axios-based utility with: `get(path)`, `post(path, body)`, `put(path, body)`, `delete(path)`. Reads base URL from config. Used for test data seeding in `beforeAll`.

**`random-data.ts`** `@faker-js/faker` wrapper with: `generateEmail()`, `generateName()`, `generatePhone()`, `generatePassword()`, `generateProductName()`.

**`date-helper.ts`** Utilities: `today()`, `futureDate(days)`, `pastDate(days)`, `formatDate(date, format)`.

### test-data/

**`users.json`** Structured test data with roles: `{ "validUser": {...}, "adminUser": {...}, "lockedUser": {...} }`.

**`products.json`** Structured product test data: `{ "availableProduct": {...}, "outOfStockProduct": {...} }`.

### CI/CD

**`.github/workflows/playwright.yml`** GitHub Actions: trigger on push/PR, Node 20, `npm ci`, `npx playwright install --with-deps`, `npx playwright test` with CI env, upload `reports/` artifact on failure.

**`Jenkinsfile`** Declarative Jenkins pipeline: stages for Checkout, Install, Install Playwright Browsers, Run Tests, Publish Report. Environment variables from Jenkins credentials.

---

## Output Location

Save all files to `07-outputs/Automation/Framework/` preserving full folder hierarchy.

---

## Output Summary

After generating all files, print:

```
=== Automation Framework Scaffold Summary ===
Framework       : Playwright TypeScript
Pattern         : Page Object Model (POM)
Modules Planned : <list from FunctionalTestCases.md>
Files Generated : <count>
Output Location : 07-outputs/Automation/Framework/
Next Step       : Run Playwright Agent to generate Page Objects and spec files
=============================================
```
