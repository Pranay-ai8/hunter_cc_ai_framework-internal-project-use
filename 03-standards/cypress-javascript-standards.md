# Cypress JavaScript Standards

## Scope

This standard governs the **Cypress + plain JavaScript (ES Modules)** automation stack — the second supported UI automation tool alongside Playwright (TypeScript / JavaScript / Java, see `03-standards/playwright-typescript-standards.md`, `03-standards/playwright-javascript-standards.md`, `03-standards/playwright-java-standards.md`).

This stack is **JavaScript-only** — Cypress is never paired with TypeScript or Java in this framework. There is no Cucumber/Gherkin layer: Cypress specs are native `describe`/`it` (Mocha) files.

Reference implementation this standard is modeled on: `/Users/ostan/Documents/Automation/cypress`.

---

## Module System

- **ES Modules** (`import`/`export default`), not CommonJS. `package.json` may need `"type": "module"` or the project's own transpilation (Cypress supports ESM in spec/support files out of the box via its bundler) — do not use `require()`/`module.exports` in new files.
- Every class/module uses a single `export default` — named exports only for the `Verify`-style barrel file (`utils/assertions/index.js` re-exporting `{ default as Verify } from './verify'`).

---

## Folder Structure (Mode A — new framework scaffold)

```
cypress.config.js
package.json
.gitignore
README.md
cypress/
  e2e/
    smokeSuites/
      <area>.cy.js
    regressionSuites/
      desktop/
        <module>.cy.js
        <domain>/
          <module>.cy.js
      mobile/
        <module>.cy.js
        <domain>/
          <module>.cy.js
  fixtures/
    <domain>/
      <dataFile>.json
    SignIn/
      user.json
  pageObjects/
    signIn/
      siginPage.js
    <domain>/
      <module>.js
  support/
    commands.js
    e2e.js
  utils/
    assertions/
      index.js
      verify.js
    constants/
      commandOptions.js
      globalConst.js
      <domain>/
        <module>.js
    helpers/
      crossModuleFunctions.js
    Interactions/
      click.js
      type.js
      check.js
      wait.js
      Intercept.js
    selectors/
      globalSels.js
      <domain>.js
.github/
  workflows/
    ci.yml
```

- `cypress/e2e/smokeSuites/` — flat, one file per major area (e.g., `admin.cy.js`, `IAP.cy.js`), tagged `@smoke`.
- `cypress/e2e/regressionSuites/desktop/` and `.../mobile/` — mirror the same domain subfolders; a domain with a single spec may sit flat (e.g., `accountSettings.cy.js`), a domain with many modules gets its own subfolder (e.g., `prosightAssets/assetsManagement.cy.js`).
- Keep desktop and mobile regression suites separate even when they cover the same feature — mobile specs assert responsive/mobile-specific behavior, not a duplicate of desktop.
- `pageObjects/<domain>/<module>.js` — one file per module, domain-grouped exactly like the spec/selectors/constants trees so a module's page object, selectors, constants, and specs all share the same subfolder name.
- `utils/selectors/` and `utils/constants/` mirror the domain folders under `pageObjects/`; `globalSels.js` and `globalConst.js`/`commandOptions.js` hold cross-domain shared values only.

---

## Page Objects

- **Static classes** — never instantiated. All methods are `static` fields assigned to arrow functions (`static doUserLogin = (username, password) => { ... }`), not `static methodName() {}` declarations, to keep `this`-free and tree-shakeable.
- Import selectors from `utils/selectors/<domain>.js`, shared selectors from `utils/selectors/globalSels.js`, and constants from `utils/constants/<domain>/<module>.js`.
- Every public method has a JSDoc block: one-line summary, `@param` for every argument, `@returns` when non-void.
- Compose Cypress commands through the `utils/Interactions/*` and `utils/assertions/*` abstraction classes wherever an equivalent exists (`Click.on(sel)`, `Type.theText(text).into(sel)`, `Verify.theElement(sel).contains(text)`) rather than calling `cy.get(sel).click()`/`cy.get(sel).type()` directly in a page object — this keeps assertions/interactions centrally maintainable. Direct `cy.get()`/`cy.contains()` calls are acceptable only for one-off page-specific flows that don't fit the shared abstraction.
- A `static toVisit = (path) => cy.visit(path)` convention is used for navigation, one per page object that owns a route.
- API-backed setup helpers (e.g., `loginToApplication`) may use `cy.api(...)` (via the `cypress-plugin-api` plugin) directly and return a `Cypress.Promise` for chaining — see `pageObjects/signIn/siginPage.js`.
- File naming: camelCase file name matching the module (`accountSettings.js`, `assetsManagement.js`); class naming: PascalCase (`AccountSettings`, `LoginPage`).

---

## Selectors (`utils/selectors/`)

- One default-exported object per domain file, keyed by page/section name, then by element name: `{ accountSettings: { nameInput: '...', phoneNumberInput: '...' } }`.
- Follow `03-standards/locator-standards.md` priority order adapted to Cypress string selectors:
  1. `[data-testid="..."]` / `[data-cy="..."]` when present in the app
  2. Stable `id`/`name` attributes
  3. ARIA role/label text (`cy.contains('button', 'Save')` pattern via `Click.onButtonByInnerText`)
  4. Attribute-substring CSS (`button[class*="topNavBar"]`) when class names are hashed/dynamic but contain a stable substring
  5. Visible text via `:contains()` — acceptable and common in this stack for buttons/links
  6. XPath — last resort, avoid unless no CSS/text alternative exists
- `globalSels.js` holds only selectors/labels reused across 3+ domains (nav bar, dialog buttons, table cells, generic Save/Cancel/Clear buttons).

---

## Live DOM Verification

See `03-standards/locator-standards.md`'s "Live DOM Verification" section for the full rationale and cross-stack process — this section covers only this stack's mechanics.

Before writing a module's selector file for the first time, run a **temporary** spec (`cypress/e2e/_domSnapshot.cy.js`, deleted before finalizing the suite — it is a one-off tool, not part of the generated framework) that visits the module's screen (performing any required setup, e.g. login) and dumps the real DOM instead of trusting the documented locator table:

```javascript
it('captures a DOM snapshot for <module>', () => {
  cy.visit('/<module-path>');
  cy.window().then((win) => {
    const selectors = ['button', 'a', 'input', 'select', 'textarea', '[role]', '[data-testid]', '[data-cy]', 'label'];
    const elements = Array.from(win.document.querySelectorAll(selectors.join(','))).slice(0, 150);
    const snapshot = elements.map((el) => ({
      tag: el.tagName.toLowerCase(),
      id: el.id || '', class: el.className || '', name: el.getAttribute('name') || '',
      role: el.getAttribute('role') || '', dataTestId: el.getAttribute('data-testid') || el.getAttribute('data-cy') || '',
      placeholder: el.getAttribute('placeholder') || '', text: (el.textContent || '').trim().replace(/\s+/g, ' '),
    }));
    cy.writeFile('docs/dom-snapshots/<module>.json', snapshot);
  });
});
```

Cross-check every selector you're about to put in `utils/selectors/<domain>.js` against this snapshot before writing it — not after the fact. Keep the generated `docs/dom-snapshots/<module>.json` for traceability, and note any documented-vs-actual discrepancy directly in the generation summary.

---

## Constants (`utils/constants/`)

- One file per module under `utils/constants/<domain>/<module>.js`, grouped into semantic sub-objects: `urlText`, `toastMessages`, `buttonsInnerText`, form labels, toggle labels, popup text, etc. — never a single flat object of unrelated strings.
- `commandOptions.js` — shared Cypress command option objects (`{ force: true }`, assertion-chainer strings like `'have.text'`/`'be.visible'`) and HTTP method constants (`requestMethod.post`, `.get`, ...). Reuse these instead of inlining literal option objects or HTTP verb strings in specs/page objects.
- `globalConst.js` — cross-domain constant values (application keys, shared action names) used by `utils/helpers/crossModuleFunctions.js` and multiple page objects.

---

## Interactions (`utils/Interactions/`)

Thin static wrapper classes around raw Cypress commands, one responsibility per file:

- `click.js` (`Click`) — `on`, `onContainText`, `onFirst`, `onLast`, `onIndexNo`, `onMultiple`, `forcefullyOn`, and named convenience methods for common dialog buttons (`onCancelButton`, `onDeleteButton`, `onContinueButton`, `onDoneButton`).
- `type.js` (`Type`) — fluent `theText(text).into(selector)` / `theTextInIndex(text, index).into(selector)` pattern; always `.clear({ force: true }).type(text, { force: true })`.
- `check.js` (`Check`) — checkbox/toggle interactions.
- `wait.js` (`AliasWait`) — `static wait = (alias) => cy.wait(`@${alias}`, { requestTimeout: 15000 })`; always wait on a named intercept alias, never a bare `cy.wait(<ms>)`.
- `Intercept.js` (`Intercept`) — `static theApi = (method, url, alias) => cy.intercept(method, url).as(alias)`.

New interaction helpers follow the same static-class, single-responsibility pattern — do not add ad hoc one-off wrapper classes; extend an existing file if the action is a variant of an existing responsibility (e.g., a new click variant goes in `click.js`).

---

## Assertions (`utils/assertions/`)

- `verify.js` exports a default `Verify` class exposing a fluent, curried API: `Verify.theElement(sel, alias).contains(text)`, `.hasText(text)`, `.hasValue(text)`, `.isVisible()`, `.isDisabled()`, `.appearsNTimes(n)`, `.doesNotContain(text)`, etc. Each method returns the underlying Cypress chainable.
- `index.js` re-exports named: `export { default as Verify } from './verify'`. Specs and page objects import `{ Verify }` from `utils/assertions` (barrel), not `verify.js` directly.
- Every assertion carries a human-readable Cypress `.should(...)`/message argument where the chainer supports it — never a bare `.should('exist')` without message context when a more specific chainer is available.
- Extend `verify.js` with new fluent methods for new assertion needs rather than asserting inline with raw `cy.get(sel).should(...)` in specs/page objects.

---

## Helpers (`utils/helpers/crossModuleFunctions.js`)

- A single default-exported `HelperFunction` class holding **cross-module** static methods only (global intercept setup, department/hospital lookups, shared teardown/reset logic) — module-specific logic belongs in that module's page object instead.
- `globalIntercept()`-style methods centralize `cy.intercept` registration used by multiple specs' `beforeEach` hooks.

---

## Fixtures (`cypress/fixtures/`)

- One JSON file per data concern, domain-grouped exactly like `pageObjects`/`utils/selectors` (`fixtures/prosightAssets/accountSettings.json`, `fixtures/SignIn/user.json`).
- Bulk/tabular data may be CSV (`fixtures/prosightCore/usersBulk.csv`) when the volume or shape doesn't fit JSON naturally.
- Import fixtures directly via ES `import` in specs/page objects (`import userData from '../../fixtures/SignIn/user.json'`) rather than `cy.fixture()` for data needed at module-load time; use `cy.fixture()` inside a test body only when the value is needed lazily inside a Cypress command chain.

---

## Specs (`cypress/e2e/**/*.cy.js`)

- `/// <reference types="cypress" />` as the first line of every spec.
- Import order: page objects → global selectors → assertions (`Verify`) → interactions (`Click`/`Type`/...) → constants → fixtures.
- Destructure only the fields actually used from each imported selectors/constants/fixture object at the top of the file — do not reference `constants.foo.bar` inline throughout the spec.
- `describe('<Suite description>', { tags: '@smoke' | '@regression' | '@sanity' }, () => { ... })` — tag at the `describe` level via the second-argument options object (consumed by `@cypress/grep`), not a string prefix in the title.
- `beforeEach` (or `before`) blocks handle login/navigation, typically via `cy.session('<sessionName>', () => { ... }, { validate() { ... } })` to cache authenticated sessions across tests and speed up runs — always pair `cy.session` with a `validate()` callback that re-confirms the session is still authenticated (e.g., re-checking a dashboard API call), and always follow it with a fresh `LoginPage.toVisit(...)` + assertion so the browser actually lands on the right page in every test's `beforeEach`, not just the one that created the session.
- Reference the source TC ID in a leading comment or the `it(...)` title so every generated test remains traceable to `FunctionalTestCases.md`.
- Skip test cases where Automation Candidate is `NO` or `TBD` — do not generate stub scripts for them; a `TBD` case must be re-evaluated and re-classified `YES` before automation. Never delete or silently skip a `YES` scenario.
- No hardcoded waits (`cy.wait(<ms>)`) — wait on intercept aliases via `AliasWait.wait(alias)` or Cypress's built-in retry-ability.

---

## Support Files (`cypress/support/`)

- `e2e.js` — global support entrypoint: import `./commands`, register cross-cutting plugins (`cypress-plugin-api`, `cypress-mochawesome-reporter/register`, `@cypress/grep` via `registerCypressGrep()`, `cy-verify-downloads`'s `addCustomCommand()`, `cypress-mailosaur`), a global `Cypress.on('uncaught:exception', ...)` handler returning `false` to prevent app errors from failing tests, and any global `before(...)` hook needed for cross-suite setup (e.g., resetting a shared counter via `cy.task`).
- `commands.js` — custom Cypress commands via `Cypress.Commands.add(...)`. Keep the stock scaffold comments as documentation until real custom commands are added.

---

## Configuration (`cypress.config.js`, Mode A scaffold)

Generate an `e2e`-focused config using the `defineConfig` API:

```javascript
const { defineConfig } = require('cypress')

module.exports = defineConfig({
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || 'https://your-app.example.com',
    specPattern: 'cypress/e2e/**/*.cy.js',
    supportFile: 'cypress/support/e2e.js',
    fixturesFolder: 'cypress/fixtures',
    viewportWidth: 1366,
    viewportHeight: 768,
    defaultCommandTimeout: 10000,
    requestTimeout: 15000,
    video: true,
    screenshotOnRunFailure: true,
    retries: { runMode: 2, openMode: 0 },
    env: {
      API_BaseUrl: process.env.CYPRESS_API_BASE_URL,
      grepFilterSpecs: true,
      grepOmitFiltered: true,
    },
    setupNodeEvents(on, config) {
      require('cypress-mochawesome-reporter/plugin')(on)
      require('@cypress/grep/src/plugin')(config)
      on('task', {
        setAuthToken(token) {
          process.env.AUTH_TOKEN = token
          return null
        },
      })
      return config
    },
  },
  reporter: 'cypress-mochawesome-reporter',
  reporterOptions: {
    reportDir: 'cypress/reports/mochawesome',
    charts: true,
    overwrite: false,
    html: true,
    json: true,
  },
})
```

Mobile regression specs (`cypress/e2e/regressionSuites/mobile/`) run against a separate mobile viewport, either via a dedicated npm script that overrides `viewportWidth`/`viewportHeight` (e.g., `--config viewportWidth=390,viewportHeight=844`) or `cy.viewport(...)` inside the mobile spec's `beforeEach` — pick one approach and apply it consistently across all mobile specs in a given run.

---

## package.json (Mode A scaffold)

```json
{
  "devDependencies": {
    "cypress": "^13.0.0",
    "@cypress/grep": "^4.1.0",
    "cypress-mochawesome-reporter": "^3.8.0",
    "cypress-plugin-api": "^2.11.0",
    "cy-verify-downloads": "^0.2.7",
    "cypress-mailosaur": "^2.0.0",
    "mochawesome": "^7.1.3",
    "mochawesome-merge": "^4.3.0",
    "mochawesome-report-generator": "^6.2.0"
  },
  "scripts": {
    "cy:open": "cypress open",
    "cy:run": "cypress run",
    "test:smoke": "cypress run --env grepTags=@smoke",
    "test:regression": "cypress run --spec \"cypress/e2e/regressionSuites/**/*.cy.js\"",
    "test:regression:desktop": "cypress run --spec \"cypress/e2e/regressionSuites/desktop/**/*.cy.js\"",
    "test:regression:mobile": "cypress run --spec \"cypress/e2e/regressionSuites/mobile/**/*.cy.js\"",
    "test:sanity": "cypress run --env grepTags=@sanity",
    "report:merge": "mochawesome-merge cypress/reports/mochawesome/*.json > cypress/reports/mochawesome/report.json",
    "report:generate": "marge cypress/reports/mochawesome/report.json -f report -o cypress/reports/mochawesome"
  }
}
```

Add `axios`/`cypress-plugin-api` usage only when API test cases exist for the module set; keep `cypress-mailosaur` only if email verification scenarios are in scope, otherwise omit it from the scaffold.

---

## Tagging & Suite Convention

| Tag | Meaning | Applied via |
|---|---|---|
| `@smoke` | Critical-path smoke checks | `describe(..., { tags: '@smoke' }, ...)` |
| `@regression` | Full regression coverage | `describe(..., { tags: '@regression' }, ...)` |
| `@sanity` | Post-deploy sanity checks | `describe(..., { tags: '@sanity' }, ...)` |

Run subsets with `cypress run --env grepTags=@smoke` (via `@cypress/grep`) rather than maintaining separate spec-pattern globs per tag.

---

## Constraints

- Never introduce a `BasePage` class, Playwright `Locator` fields, or Cucumber `.feature`/step-definition files into this stack — those belong to the Playwright stacks.
- Never mix Cypress and Playwright test runners in the same generated project.
- Never call `cy.wait(<literal ms>)` — always alias-based waits or rely on Cypress's built-in retryability.
- Never hardcode credentials/base URLs in specs or page objects — source them from `cypress.config.js` `env`/`baseUrl`, `Cypress.env(...)`, or fixtures.
- Keep specs thin: business logic and selectors live in page objects/selectors/constants; specs orchestrate calls to those layers plus `Verify` assertions.
