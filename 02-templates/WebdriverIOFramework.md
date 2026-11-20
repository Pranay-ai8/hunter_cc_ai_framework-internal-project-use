# WebdriverIO JavaScript Automation Framework Template

> **Reference implementation:** `/Users/ostan/Downloads/SwagLabs-master`, with one corrected deviation — see "Live-run correction" under Project Structure below (`pageobjects`/`step-definitions`/`support`/`helper` live under a top-level `src/`, not nested inside `features/` as SwagLabs itself does). Follow this template's layout, not the reference project's, when the two differ. This is the JavaScript-stack sibling of `02-templates/AppiumFramework.md` (Java) — same target app class (native Android/iOS via Appium), different language/runner/BDD binding.

---

## Framework Identity

| Property | Value |
|---|---|
| Language | JavaScript (ES Modules) |
| Test Runner | WebdriverIO (`@wdio/cli` + `@wdio/local-runner`) |
| BDD Layer | Cucumber (`@wdio/cucumber-framework`) |
| Mobile Driver | Appium (`@wdio/appium-service` as devDependency; connect to an externally-managed server by default — see "Connecting to Appium" in the standards doc) — always the actual latest major at generation time, verify via `npm view appium version`, don't assume 2.x |
| Platform Drivers | `uiautomator2` (Android), `xcuitest` (iOS) — installed into Appium's own `APPIUM_HOME` via `appium driver install <name>`, **not** npm dependencies of this project (see the package.json section's note) |
| Reporting | Allure (`@wdio/allure-reporter`) |
| Env Config | `dotenv` |
| Dependency Versions | **Always `<LATEST — verify via npm view <package> version>` at generation time** — never hardcode a number from this template or a reference project. See `03-standards/webdriverio-javascript-standards.md`'s "Latest-Dependency-Version Policy". |

---

## Project Structure

```
<project-root>/
├── package.json
├── wdio.conf.js
├── Jenkinsfile                     ← mandatory scaffold, not optional — see "Jenkinsfile" below
├── .env
├── .env.example
├── .gitignore
├── README.md                       ← lives HERE, inside this framework's own folder — never at a shared parent level
├── data/
│   └── testdata.json
├── docs/
│   └── dom-snapshots/               ← Live UI Hierarchy Verification artifacts, one <module>.xml + <module>.json per module
├── src/
│   ├── helper/
│   │   ├── Utils.js
│   │   ├── gestures.js
│   │   ├── TestData.js               ← single load point for data/testdata.json
│   │   └── SelfHealer.js             ← F1-scored locator self-healing (see standards' "Self-Healing Locators")
│   ├── pageobjects/
│   │   ├── page.js
│   │   └── <module>.page.js         ← one per app module, created by 05-agents/webdriverio-javascript-agent.md, NOT this scaffold
│   ├── step-definitions/
│   │   └── <module>.steps.js        ← one per app module, created by 05-agents/webdriverio-javascript-agent.md, NOT this scaffold
│   └── support/
│       └── hooks.js
└── features/
    └── <module>.feature             ← created by 05-agents/webdriverio-javascript-agent.md, NOT this scaffold — features/ holds ONLY .feature files
```

**Live-run correction:** `pageobjects`/`step-definitions`/`support`/`helper` live under a top-level `src/`, not nested inside `features/` as an earlier version of this template showed (mirroring the SwagLabs reference project too literally) — this repo's actual generated framework was corrected to the `src/` layout above after explicit user direction. Follow `src/` for every new scaffold.

**Scaffold boundary:** `05-agents/automation-framework-webdriverio-javascript-agent.md` generates every file above **except** `src/pageobjects/<module>.page.js`, `src/step-definitions/<module>.steps.js`, and `features/<module>.feature` — those three are `05-agents/webdriverio-javascript-agent.md`'s responsibility, generated only after Live UI Hierarchy Verification confirms real selectors. The scaffold agent may create the `features/`, `src/pageobjects/`, `src/step-definitions/` directories themselves (empty, ready for population) but must not populate them with module content.

---

## `package.json`

```json
{
  "name": "<app-name>-webdriverio-mobile",
  "version": "1.0.0",
  "type": "module",
  "description": "WebdriverIO + Appium + Cucumber mobile automation framework for <AppName>",
  "scripts": {
    "wdio": "wdio run ./wdio.conf.js",
    "wdio:smoke": "wdio run ./wdio.conf.js --cucumberOpts.tags=@smoke",
    "wdio:regression": "wdio run ./wdio.conf.js --cucumberOpts.tags=@regression",
    "wdio:ios": "RUN_ON=iOS_APP wdio run ./wdio.conf.js",
    "wdio:ios:smoke": "RUN_ON=iOS_APP wdio run ./wdio.conf.js --cucumberOpts.tags=@smoke",
    "wdio:ios:regression": "RUN_ON=iOS_APP wdio run ./wdio.conf.js --cucumberOpts.tags=@regression",
    "allure:generate": "allure generate allure-results --clean -o allure-report",
    "allure:open": "allure open allure-report"
  },
  "devDependencies": {
    "@wdio/cli": "<LATEST — verify via npm view @wdio/cli version>",
    "@wdio/local-runner": "<LATEST — verify via npm view @wdio/local-runner version>",
    "@wdio/cucumber-framework": "<LATEST — verify via npm view @wdio/cucumber-framework version>",
    "@wdio/globals": "<LATEST — verify via npm view @wdio/globals version>",
    "@wdio/appium-service": "<LATEST — verify via npm view @wdio/appium-service version>",
    "@wdio/allure-reporter": "<LATEST — verify via npm view @wdio/allure-reporter version>",
    "@wdio/spec-reporter": "<LATEST — verify via npm view @wdio/spec-reporter version>",
    "@cucumber/cucumber": "<LATEST — verify via npm view @cucumber/cucumber version>",
    "appium": "<LATEST — verify via npm view appium version>",
    "allure-commandline": "<LATEST — verify via npm view allure-commandline version>"
  },
  "dependencies": {
    "dotenv": "<LATEST — verify via npm view dotenv version>"
  }
}
```

Notes:

- **`wdio:ios*` scripts switch platform via the `RUN_ON` env var** (see `03-standards/webdriverio-javascript-standards.md`'s "Platform selection" section) — plain `wdio`/`wdio:smoke`/`wdio:regression` default to Android.
- **Live-run correction — `appium-uiautomator2-driver`/`appium-xcuitest-driver` are NOT npm dependencies of this project.** An earlier version of this template listed both in `devDependencies`, but neither is imported by any generated code, and Appium loads its drivers from its own `APPIUM_HOME` driver registry (via `appium driver install uiautomator2` / `appium driver install xcuitest`), not from this project's `node_modules` — adding them here has no functional effect. Install both drivers once against whatever `APPIUM_HOME` the Appium server actually uses (document the exact commands in the generated `README.md`'s Prerequisites section), and don't list them in `package.json`.
- **`@wdio/globals` and `@wdio/spec-reporter` are required, not optional** — an earlier version of this template omitted both, but every generated Page Object/step-definition file imports `$`/`$$`/`browser`/`expect` from `@wdio/globals`, and the validated `wdio.conf.js` reporter config uses both `'spec'` and `'allure'` together (fast console feedback plus the full Allure report).
- **Every version above is a placeholder.** Before writing this file, run `npm view <package> version` for each package listed and substitute the real, current value. Do not copy the SwagLabs reference project's `package.json` versions — they are already stale relative to whatever is latest at generation time.
- If a just-verified latest version introduces an incompatibility between two of these packages (e.g., a `@wdio/*` major that has dropped support for the `appium` major pinned above), resolve it with an explicit compatible pin and document the resolution in `AutomationSummary.md` — see the standards doc's "Latest-Dependency-Version Policy".

---

## `wdio.conf.js`

```javascript
import dotenv from 'dotenv';
dotenv.config();

export const config = {
    runner: 'local',

    specs: [
        './features/**/*.feature'
    ],
    exclude: [],

    maxInstances: 1,

    // Connect to an already-running, externally-managed Appium server rather
    // than having @wdio/appium-service spawn its own — see
    // 03-standards/webdriverio-javascript-standards.md's "Connecting to
    // Appium" section for why (root-owned files from a prior `sudo npm
    // install` under the default APPIUM_HOME). Switch back to
    // services: ['appium'] and drop this block if the environment doesn't
    // need an isolated APPIUM_HOME.
    hostname: process.env.APPIUM_HOST || '127.0.0.1',
    port: Number(process.env.APPIUM_PORT) || 4723,
    path: '/',

    // Platform is a RUN_ON switch, not two simultaneously-active capability
    // entries — see the standards doc's "Platform selection" section for why.
    capabilities: [
        process.env.RUN_ON === 'iOS_APP'
            ? {
                platformName: 'iOS',
                'appium:deviceName': process.env.IOS_DEVICE_NAME,
                'appium:platformVersion': process.env.IOS_PLATFORM_VERSION,
                'appium:udid': process.env.IOS_UDID,
                'appium:automationName': 'XCUITest',
                'appium:bundleId': process.env.IOS_BUNDLE_ID,
                'appium:app': process.env.IOS_APP_PATH,
                'appium:noReset': true,
                'appium:fullReset': false,
                'appium:forceAppLaunch': true,
                'appium:shouldTerminateApp': true,
                'appium:autoAcceptAlerts': true,
                'appium:newCommandTimeout': 240,
            }
            : {
                platformName: 'Android',
                'appium:deviceName': process.env.ANDROID_DEVICE_NAME,
                'appium:automationName': 'UiAutomator2',
                'appium:appPackage': process.env.ANDROID_APP_PACKAGE,
                'appium:appActivity': process.env.ANDROID_APP_ACTIVITY,
                'appium:app': process.env.ANDROID_APP_PATH,
                'appium:noReset': true,
                'appium:fullReset': false,
                'appium:autoGrantPermissions': true,
                'appium:forceAppLaunch': true,
                'appium:shouldTerminateApp': true,
                'appium:newCommandTimeout': 240,
            },
    ],

    logLevel: 'info',
    bail: 0,
    waitforTimeout: 15000,
    connectionRetryTimeout: 30000,
    connectionRetryCount: 3,

    services: [],

    framework: 'cucumber',

    reporters: [
        'spec',
        ['allure', {
            outputDir: 'allure-results',
            disableWebdriverStepsReporting: true,
            disableWebdriverScreenshotsReporting: false,
            disableMochaHooks: true,
        }],
    ],

    cucumberOpts: {
        require: ['./src/step-definitions/*.steps.js', './src/support/hooks.js'],
        backtrace: false,
        requireModule: [],
        dryRun: false,
        failFast: false,
        name: [],
        snippets: true,
        source: true,
        strict: false,
        tags: '',
        timeout: 120000,
        ignoreUndefinedDefinitions: false,
    },
};
```

- `cucumberOpts.require` uses a glob (`./src/step-definitions/*.steps.js`) rather than an explicit file list, so `05-agents/webdriverio-javascript-agent.md` adding a new module's step file never requires a manual edit here — confirm the glob still matches after each new module is added rather than assuming.
- `specs` stays a single glob for the same reason — new `.feature` files are picked up automatically.
- Bump `cucumberOpts.timeout` well above the WDIO default (30000ms) — 120000ms proved necessary for mobile; cold app launches and gesture-heavy steps routinely exceed the web-stack default.
- `waitforTimeout: 15000` is a reasonable generation-time default; raise it (with a documented reason, e.g. confirmed host resource contention) rather than silently — see the standards doc's "Session & App Lifecycle" section for the difference between a genuine timing issue and a `forceAppLaunch`/`shouldTerminateApp` state-leakage bug that no timeout value fixes.
- `reporters: ['spec', ['allure', {...}]]` — pair the `spec` reporter with Allure for immediate console feedback alongside the full report; `@wdio/spec-reporter` must be a `package.json` dependency for this (see the package.json section's note above).

---

## `src/pageobjects/page.js` (base class)

```javascript
import { browser } from '@wdio/globals';
import { findHealedLocator } from '../helper/SelfHealer.js';

export default class Page {
    /** Wait for an element to be displayed, then return whether it is —
     * found duplicated verbatim across every module's "is<Module>ScreenDisplayed()"
     * before being pulled up here. */
    async isDisplayed(element) {
        await element.waitForDisplayed();
        return element.isDisplayed();
    }

    isAndroid() {
        return browser.isAndroid;
    }

    isIOS() {
        return browser.isIOS;
    }

    /** Android: `checked` attribute ("true"/"false"). iOS: `value` ("1"/"0"). */
    async isSwitchOn(element) {
        if (this.isIOS()) {
            return (await element.getAttribute('value')) === '1';
        }
        return (await element.getAttribute('checked')) === 'true';
    }

    /** Some keyboard/field combinations throw if no keyboard is currently
     * shown (confirmed live on iOS) — treat as a no-op, not a real failure. */
    async hideKeyboardSafely() {
        try {
            await browser.hideKeyboard();
        } catch (e) {
            // No-op if the keyboard was already hidden/never shown.
        }
    }

    /** Self-healing click/type — see standards' "Self-Healing Locators" section.
     * `hint` is a short human-readable name (e.g. "Login Button"); only wrap
     * stable, permanent elements — never a READ/assertion target, an ad-hoc
     * dynamically-resolved element, or an optional/conditional dialog control. */
    async clickWithHeal(element, hint) {
        try {
            await element.waitForDisplayed();
            await element.click();
        } catch (originalError) {
            const healed = await this.healOrRethrow(hint, originalError);
            await healed.click();
        }
    }

    async typeWithHeal(element, hint, text) {
        try {
            await element.waitForDisplayed();
            await element.setValue(text);
        } catch (originalError) {
            const healed = await this.healOrRethrow(hint, originalError);
            await healed.setValue(text);
        }
    }

    async healOrRethrow(hint, originalError) {
        const healed = await findHealedLocator(hint, originalError);
        if (!healed.success) {
            throw originalError;
        }
        console.info(`Self-healed [${hint}] with confidence ${healed.confidence}`);
        return healed.element;
    }
}
```

This is the validated shape, not a placeholder stub — see `03-standards/webdriverio-javascript-standards.md`'s "Page Object Model" and "Self-Healing Locators" sections for why each method exists.

## `src/pageobjects/<module>.page.js` (pattern — populated by the test-generation agent, not the scaffold agent)

```javascript
import { $ } from '@wdio/globals';
import Page from './page.js';

class <Module>Page extends Page {
    get <elementName>() { return $('~<accessibility-id>'); }

    async <businessAction>(...args) {
        // compose the getters above, routing stable elements through
        // this.clickWithHeal(element, hint) / this.typeWithHeal(element, hint, text)
    }
}

export default new <Module>Page();
```

## `src/step-definitions/<module>.steps.js` (pattern — populated by the test-generation agent, not the scaffold agent)

```javascript
import { Given, When, Then } from '@wdio/cucumber-framework';
import { expect } from '@wdio/globals';
import <Module>Page from '../pageobjects/<module>.page.js';
import testData from '../helper/TestData.js';

Given(/^.../, async () => { /* ... */ });
When(/^.../, async () => { /* ... */ });
// Give a business-data value (a credential, a canonical sample input) its own
// named step that resolves through testData — never re-duplicate the same
// literal directly in the .feature file. See "data/testdata.json" below.
Then(/^.../, async () => { /* ... */ });
```

Per `03-standards/webdriverio-javascript-standards.md`'s "Step Definitions" section — **one step-definition file per app module**, not a single shared `steps.js`, once the project has more than one or two modules. Import `testData` from `../helper/TestData.js`, never re-derive the `../../data/testdata.json` relative path and import-assertion syntax in every step-definition file.

---

## `src/support/hooks.js`

```javascript
import { After, AfterAll } from '@cucumber/cucumber';
import { browser } from '@wdio/globals';

After(async function (scenario) {
    const scenarioName = scenario.pickle.name;
    const scenarioStatus = scenario.result.status;

    if (scenarioStatus === 'FAILED') {
        console.error(`Scenario "${scenarioName}" failed!`);
        try {
            // saveScreenshot() both captures and writes the file — do not also
            // call takeScreenshot() first, that's a confirmed real inefficiency
            // (two device round-trips for one screenshot).
            await browser.saveScreenshot(`./allure-results/failure-${Date.now()}.png`);
        } catch (e) {
            console.error('Could not capture failure screenshot:', e.message);
        }
    }
    // Add module-specific post-scenario cleanup here (e.g. logging out) as modules are added.
});

AfterAll(async function () {
    // Live-run correction: do NOT call `mobile: terminateApp` here. On a
    // resource-constrained host the underlying force-stop call intermittently
    // hung 20+ seconds, and Cucumber's own hook timeout aborted the entire
    // worker process regardless of try/catch. WebdriverIO's automatic
    // deleteSession() (run after every spec file) already tears the session
    // down correctly — keep this hook to genuinely necessary bookkeeping only.
    console.log('All scenarios completed for this spec file.');
});
```

---

## `src/helper/Utils.js`

```javascript
export const CONTEXT_REF = {
    NATIVE: 'native',
    WEBVIEW: 'webview',
};

export default class Utils {
    // Hybrid/WebView context switching — scaffold these three methods only when context = HYBRID.
    async waitForWebViewContextLoaded() { /* ... */ }
    async switchToContext(context) { /* ... */ }
    async getCurrentContexts() { return driver.getContexts(); }

    // Device-level toggles usable regardless of context.
    async turnOnWifi() { driver.turnOnWifi(); }
    async turnOffWifi() { driver.turnOffWifi(); }
}
```

Omit `waitForWebViewContextLoaded`/`switchToContext`/`getCurrentContexts` entirely when the app context is `NATIVE` — do not scaffold unused hybrid plumbing into a native-only project.

---

## `src/helper/gestures.js`

Scaffold the full W3C Actions API gesture set — `swipeUp`/`swipeDown`/`swipeLeft`/`swipeRight` (percentage-based, resolved against `driver.getWindowRect()`), `swipe(from, to)`, `dragElementTo(elementOne, elementTwo)`, `longPress(element, duration)`, `pinchZoomIn(element)`, `pinchZoomOut(element)`, and `Page.checkIfDisplayedWithSwipeUp(element, maxScrolls)` — exactly per the reference implementation's `src/helper/gestures.js`. This file is generic (no module-specific logic) and is created in full by the scaffold agent, not incrementally by the test-generation agent.

---

## `data/testdata.json`

```json
{
  "login_credentials": {
    "valid_user": "<placeholder — populate from RequirementAnalysis.md/FunctionalTestCases.md test data>",
    "valid_password": "<placeholder>"
  }
}
```

Scaffold with an empty/placeholder shape only — real per-module data keys are added by `05-agents/webdriverio-javascript-agent.md` as each module is generated, sourced from already-confirmed credentials (never invented).

## `src/helper/TestData.js`

```javascript
import testData from '../../data/testdata.json' with { type: 'json' };

/**
 * Single, centralized read of data/testdata.json — the JS-stack equivalent of
 * the Appium-Java sibling framework's PropertyReader.getInstance() for
 * config.properties. Step definitions import from here, never re-derive the
 * relative path/import-assertion syntax themselves.
 */
export default testData;
```

**Live-run correction: a value declared in `testdata.json` is not "test data management" until something actually consumes it.** A generated framework was found with a fully-populated `testdata.json` that had zero consumers — every value was independently hardcoded a second time as a literal directly in the `.feature` files, so editing the JSON silently changed nothing. When `05-agents/webdriverio-javascript-agent.md` adds a key to `testdata.json` for a value reused across scenarios (a credential, a canonical sample input), it must also add a named step (`I enter the valid password`, not a generic `I enter the password "(.*)"` fed a literal from the feature file) whose step-definition body resolves the value through `TestData.js`. See `03-standards/webdriverio-javascript-standards.md`'s "Test Data" section for the full rule, including when a literal in a `.feature` file is fine as-is (an arbitrary one-off boundary-case input never needs externalizing).

---

## `.env` / `.env.example`

```
# .env.example — commit this file; copy to .env locally and fill in real values. Never commit .env itself.
ANDROID_APP_PATH=
ANDROID_APP_PACKAGE=
ANDROID_APP_ACTIVITY=
ANDROID_DEVICE_NAME=

# RUN_ON=iOS_APP switches wdio.conf.js's capabilities block to iOS (default: Android)
RUN_ON=ANDROID_APP
IOS_APP_PATH=
IOS_BUNDLE_ID=
IOS_DEVICE_NAME=
IOS_PLATFORM_VERSION=
IOS_UDID=

APPIUM_HOST=127.0.0.1
APPIUM_PORT=4723
```

`.env` itself is generated with the **real, confirmed values** from Step 3a of `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md` — this file must be immediately usable to start a real Appium session for Live UI Hierarchy Verification, not require a manual edit first. `.env.example` ships with empty values (except `RUN_ON`, which defaults to Android) as a template for anyone re-cloning the project. Populate the iOS keys even when platform = Android-only (harmless if unused) so adding iOS later is a `RUN_ON` flip, not a fresh round of collecting values. `IOS_UDID` matters once more than one simulator of the same device/OS version exists on the host; leave empty otherwise. `APPIUM_HOST`/`APPIUM_PORT` back the external-Appium-server connection in `wdio.conf.js` — see that section above.

---

## `Jenkinsfile`

**Live-run correction: this file was documented nowhere in this template and was never actually generated for the real WebdriverIO framework** — a gap discovered only when the user checked the generated framework's root directory directly (the same class of "documented intent vs. shipped code" drift found and fixed for the Appium-Java sibling's reporting setup — see `03-standards/appium-java-standards.md`'s "Reporting" section). This is now **mandatory scaffold, not optional** — every Mode A generation must produce a working `Jenkinsfile` at the framework's own root, not defer it.

Mirrors the reference `Automation-java` project's proven, actually-working `Jenkinsfile` pattern: full-repo checkout (this is a monorepo — the Jenkins job checks out `cc-qa-ai-framework` as a whole, not just this subfolder) + `dir(...)`-scoped stages using the real output path, so the pipeline works regardless of where in the repo the Jenkins job's "Pipeline script from SCM" happens to find this file.

```groovy
pipeline {
    agent any
    parameters {
        choice(name: 'PLATFORM', choices: ['android', 'ios'], description: 'Target platform (android = default npm scripts, ios = RUN_ON=iOS_APP scripts)')
        choice(name: 'SUITE', choices: ['smoke', 'regression'], description: 'Cucumber tag suite to run')
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Install Dependencies') {
            steps {
                dir('07-outputs/Automation/Framework') {
                    sh 'npm ci'
                }
            }
        }
        stage('Test') {
            steps {
                dir('07-outputs/Automation/Framework') {
                    script {
                        def npmScript = params.PLATFORM == 'ios' ? "wdio:ios:${params.SUITE}" : "wdio:${params.SUITE}"
                        sh "npm run ${npmScript}"
                    }
                }
            }
        }
        stage('Generate Allure Report') {
            steps {
                dir('07-outputs/Automation/Framework') {
                    sh 'npm run allure:generate || true'
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '07-outputs/Automation/Framework/allure-results/**, 07-outputs/Automation/Framework/allure-report/**, 07-outputs/Automation/Framework/*.log', allowEmptyArchive: true
        }
    }
}
```

- `dir('07-outputs/Automation/Framework')` must match the actual output path from `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md` Step 5 — substitute the real path at generation time if the user specified a non-default output location, in every stage, not just the first.
- `npm run allure:generate` is wrapped with `|| true` because it can legitimately fail when a run produces zero `allure-results` (e.g., an empty smoke tag match on a brand-new scaffold) — a missing report in that case is expected, not a pipeline failure.
- No separate Jenkins Allure plugin install/stage is assumed — `allure-report/` is a plain static HTML directory produced by the `allure-commandline` npm dependency already in `package.json`, archived as a build artifact like any other file. Add a Jenkins-side Allure plugin integration later only if the team specifically wants Jenkins's own Allure trend graphs; it is not required to view the report.

---

## `README.md`

**Lives inside this framework's own scaffolded folder** (`07-outputs/Automation/Framework/README.md`) — this repo's hard rule is that a generated framework's README never lives outside its own project folder. Note that `07-outputs/Automation/Framework/` is the same unified output path every tool/stack scaffolds to (see `MasterQAOrchestrator/AutomationOrchestrator.md`'s Constraints) — an Appium-Java framework previously generated for this app does not coexist here once this WebdriverIO scaffold has overwritten it.

Include:

- Prerequisites (Node.js version, Appium, Android Studio/Xcode as applicable, and the exact `appium driver install uiautomator2`/`appium driver install xcuitest` commands against whatever `APPIUM_HOME` the project's Appium server uses — see the package.json section's note on why these aren't npm dependencies)
- Setup: `npm install`, copy `.env.example` to `.env` and fill in real device/app values (both platforms' keys, even if only targeting one today)
- Starting an emulator/simulator and the Appium server: this template connects to an **externally-managed** Appium server (`hostname`/`port`/`path` in `wdio.conf.js`, not `services: ['appium']`) — document the exact `appium --address ... --port ...` invocation, including the isolated-`APPIUM_HOME` pattern if the environment needs one (see the standards doc's "Connecting to Appium" section)
- Execution commands, matching the validated script set: `npm run wdio`, `npm run wdio:smoke`, `npm run wdio:regression` (Android, default), `npm run wdio:ios`, `npm run wdio:ios:smoke`, `npm run wdio:ios:regression` (iOS, via `RUN_ON`)
- Allure report commands: `npm run allure:generate`, `npm run allure:open`
- CI: the `Jenkinsfile` at this project's root (parameterized by `PLATFORM`/`SUITE`) — see "Jenkinsfile" above
- Folder structure explanation (link back to this template's structure section — `src/` holds `pageobjects`/`step-definitions`/`support`/`helper`; `features/` holds only `.feature` files)
- How to add a new module (Page Object → step-definition file → feature file → confirm `wdio.conf.js` globs still match — normally no edit needed since both `specs` and `cucumberOpts.require` are globs)
- Note in the README if an Appium-Java framework for this app was overwritten to generate this WebdriverIO scaffold (see "AutomationSummary.md Cross-Reference" below), so the reader understands why one doesn't exist alongside this one

---

## `AutomationSummary.md` Cross-Reference

Since `07-outputs/Automation/Framework/` is the single shared output path every tool/stack scaffolds to, an Appium-Java framework and a WebdriverIO framework for the same app cannot coexist on disk — generating one overwrites the other (see `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md`'s Execution Modes). If this scaffold overwrote a prior Appium-Java framework, note that plainly in `AutomationSummary.md` (what was there before, and that it's no longer present) rather than silently proceeding as if nothing had been there.
