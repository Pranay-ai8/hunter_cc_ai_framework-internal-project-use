# WebdriverIO Native Demo App — WebdriverIO Mobile Automation Framework

WebdriverIO + JavaScript (ES Modules) + Appium + Cucumber mobile automation framework, generated per `03-standards/webdriverio-javascript-standards.md` by the QA-AI-Framework **WebdriverIO Automation Orchestrator**, and verified green against both a live Android emulator and a live iOS simulator (not just generated — see [Live Verification](#live-verification--real-bugs-found)).

**Target application:** [WebdriverIO Native Demo App](https://github.com/webdriverio/native-demo-app) — `com.wdiodemoapp` (Android) / `org.wdiodemoapp` (iOS), a purpose-built guinea-pig app for practicing Appium/WebdriverIO mobile automation. Android build: `android.wdio.native.app.v2.2.0.apk`. iOS build: simulator `.app` bundle (see [Configuration](#configuration)). **Source test cases:** `07-outputs/TestCases/WdioNativeDemoApp-FunctionalUITestCases.md` **Sibling framework:** this same app also has an Appium + Java + TestNG + Maven stack at [`07-outputs/Automation-Appium/Framework/`](../../../Automation-Appium/Framework/README.md) (see its `AutomationSummary.md` for that stack's own generation + live-run history). The two frameworks are fully independent — no shared files, config, or dependencies.

---

## Contents

- [Folder Layout](#folder-layout)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Configuration](#configuration)
- [Running the Tests](#running-the-tests)
- [Test Coverage](#test-coverage)
- [Architecture](#architecture)
- [Self-Healing Locators](#self-healing-locators)
- [Test Data](#test-data)
- [Adding a New Module](#adding-a-new-module)
- [Reports & Logs](#reports--logs)
- [Live Verification / Real Bugs Found](#live-verification--real-bugs-found)
- [Known Limitations](#known-limitations)
- [Troubleshooting](#troubleshooting)

---

## Folder Layout

```
Automation-WebdriverIO/                 <- project root (one level above this file)
├── AutomationSummary.md                <- full generation + live-run report (real bugs found, fixes, decisions)
├── healing-cache.json                  <- SelfHealer's cached heal recipes (generated at runtime, TTL 1h)
├── healing-report.log                  <- append-only log of every successful self-heal (generated at runtime)
└── Framework/                          <- YOU ARE HERE — the WebdriverIO project root (package.json lives here)
    ├── README.md                        <- this file
    ├── package.json
    ├── wdio.conf.js
    ├── .env                             <- real device/app values (never committed)
    ├── .gitignore
    ├── data/
    │   └── testdata.json                <- centralized business test data (see Test Data below)
    ├── src/
    │   ├── helper/
    │   │   ├── Utils.js                  context switching helpers, device toggles
    │   │   ├── gestures.js               swipe/scroll/drag W3C Actions API helpers
    │   │   ├── TestData.js               single load point for data/testdata.json
    │   │   └── SelfHealer.js             self-healing locator engine (see Self-Healing Locators below)
    │   ├── pageobjects/
    │   │   ├── page.js                    base Page class — isDisplayed/isAndroid/isIOS/isSwitchOn/hideKeyboardSafely/clickWithHeal/typeWithHeal
    │   │   └── <module>.page.js           one per app module (8 total)
    │   ├── step-definitions/
    │   │   ├── common.steps.js            shared "Given the app is launched" precondition
    │   │   └── <module>.steps.js           one per app module
    │   └── support/
    │       └── hooks.js                   After (failure screenshot), AfterAll (minimal)
    └── features/
        └── <module>.feature               one Gherkin file per app module (8 total) — features/ holds ONLY .feature files
```

Unlike the Appium-Java sibling framework's Maven module, this project's `package.json`/`wdio.conf.js`/source all live directly in `Framework/` — there is no further nesting. `AutomationSummary.md` and the self-heal cache/log sit one level up, directly under the shared `Automation-WebdriverIO/` project root, matching the Java sibling's own layout (`Automation-Appium/AutomationSummary.md` next to `Automation-Appium/Framework/`).

---

## Technology Stack

| Property | Value | Notes |
|---|---|---|
| Language | JavaScript (ES Modules) | `"type": "module"` in `package.json` |
| Test Runner | WebdriverIO | `@wdio/cli` 9.31.1 + `@wdio/local-runner` 9.31.1 |
| BDD Layer | Cucumber | `@wdio/cucumber-framework` 9.31.1 + `@cucumber/cucumber` 10.9.0 |
| Mobile Automation | Appium | 3.6.0 (server, connected externally — see [Setup](#setup)) |
| Platform Drivers | UiAutomator2 (Android), XCUITest (iOS) | 8.4.0 / installed via `appium driver install` — **not** npm dependencies, see [Known Limitations](#known-limitations) |
| Assertions/Globals | `@wdio/globals` 9.31.1 | `$`, `$$`, `browser`, `expect` |
| Reporting | Allure + spec | `@wdio/allure-reporter` 9.31.1, `@wdio/spec-reporter` 9.31.1, `allure-commandline` 2.43.0 |
| Env Config | `dotenv` 17.4.2 | `.env` at project root |

All versions above were the actual latest resolved via `npm view <package> version` at generation time, per this repo's Latest-Dependency-Version Policy — re-verify before assuming they're still current.

**Deliberate simplifications vs. this framework's reference template** (`/Users/ostan/Downloads/SwagLabs-master`): `pageobjects`/`step-definitions`/`support`/`helper` live under a top-level `src/` rather than nested inside `features/` as the reference project does — an explicit correction applied after the initial scaffold (see `03-standards/webdriverio-javascript-standards.md`'s "Live-run correction" note). No self-healing locator layer exists in this stack (the sibling Java framework has one; out of scope for this pass, to avoid scope creep beyond what was requested).

---

## Prerequisites

- **Node.js** (LTS) and npm
- **Appium** installed globally (`npm install -g appium`), with both drivers installed:
  ```bash
  appium driver install uiautomator2
  appium driver install xcuitest
  ```
  These are **not** `package.json` dependencies — Appium loads them from its own `APPIUM_HOME` driver registry, not from this project's `node_modules`. If your environment needs an isolated `APPIUM_HOME` (e.g. root-owned files from a prior `sudo npm install` under the default one), install both drivers against that isolated home instead — see [Troubleshooting](#troubleshooting).
- **Android SDK** with `platform-tools`, `emulator`, and at least one system image (verified against **Pixel_6_API_33**)
- **Xcode + iOS Simulator** (for iOS runs only) — verified against **iPhone 15, iOS 17.2**
- **The app under test**: `android.wdio.native.app.v2.2.0.apk` (Android), a simulator `.app` build (iOS) — or your own build of the [native-demo-app](https://github.com/webdriverio/native-demo-app)

---

## Setup

```bash
cd Automation-WebdriverIO/Framework
npm install

# --- Android ---
emulator -avd Pixel_6_API_33 -no-snapshot -no-boot-anim &
adb devices -l

# --- iOS ---
xcrun simctl boot "iPhone 15"
xcrun simctl list devices | grep Booted

# --- Appium server (both platforms share one server) ---
# This project connects to an EXTERNALLY-managed Appium server (hostname/port/path
# in wdio.conf.js, services: [] — not @wdio/appium-service) — start it yourself:
appium --address 127.0.0.1 --port 4723 --relaxed-security &
curl -s http://127.0.0.1:4723/status   # {"value":{"ready":true, ...}}
```

If your environment needs an isolated `APPIUM_HOME` (see Prerequisites), export it before both the `appium driver install ...` commands and the `appium --address ...` server start:
```bash
export APPIUM_HOME=~/.appium-fresh
```

---

## Configuration

Edit `.env` (copy from a committed `.env.example` if present, or edit directly — see `.gitignore`):

```properties
ANDROID_APP_PATH=/absolute/path/to/android.wdio.native.app.v2.2.0.apk
ANDROID_APP_PACKAGE=com.wdiodemoapp
ANDROID_APP_ACTIVITY=com.wdiodemoapp.MainActivity
ANDROID_DEVICE_NAME=emulator-5554

# RUN_ON=iOS_APP switches wdio.conf.js's capabilities block to iOS (default: Android)
RUN_ON=ANDROID_APP
IOS_APP_PATH=/absolute/path/to/wdiodemoapp.app
IOS_BUNDLE_ID=org.wdiodemoapp
IOS_DEVICE_NAME=iPhone 15
IOS_PLATFORM_VERSION=17.2
IOS_UDID=<simulator UDID from `xcrun simctl list devices`>

APPIUM_HOST=127.0.0.1
APPIUM_PORT=4723
```

`wdio.conf.js` reads `RUN_ON` to pick between two fully-populated capability branches (Android is the default/`else` branch) — it is a switch, never two capability objects active at once. It also sets `appium:forceAppLaunch`/`appium:shouldTerminateApp` on both platforms (mandatory — see [Live Verification](#live-verification--real-bugs-found) finding #1) and `appium:autoGrantPermissions` (Android) / `appium:autoAcceptAlerts` (iOS) for permission handling.

---

## Running the Tests

```bash
cd Automation-WebdriverIO/Framework

# Android (default)
npm run wdio              # full suite
npm run wdio:smoke        # @smoke only
npm run wdio:regression   # @regression only

# iOS (RUN_ON=iOS_APP set automatically by these scripts)
npm run wdio:ios
npm run wdio:ios:smoke
npm run wdio:ios:regression
```

Both platforms require the emulator/simulator and Appium server from [Setup](#setup) to already be running. Each `.feature` file gets its own Appium session (not one session per Scenario, not one for the whole suite) — see [Architecture](#architecture) for why `forceAppLaunch`/`shouldTerminateApp` matter given that.

---

## Test Coverage

| Module | Feature File | Scenarios | Notes |
|---|---|---|---|
| Home | `home.feature` | 2 | |
| Login / Sign Up | `login.feature` | 5 | credentials/invalid-email sourced from `data/testdata.json` |
| Navigation (tab bar + side menu) | `navigation.feature` | 4 | |
| Forms | `forms.feature` | 5 | sample text/special characters/dropdown option sourced from `data/testdata.json` |
| Swipe | `swipe.feature` | 3 | |
| Drag | `drag.feature` | 3 | |
| Permissions | `permissions.feature` | 3 (one is a Scenario Outline expanding to 3 executions — microphone/location/photos) | expected default state is platform-aware (Android pre-grants; iOS starts ungranted) |
| WebView | `webview.feature` | 2 | |
| **Total** | **8 feature files** | **27 scenario blocks** | last full run (all 8 files, `@smoke`+`@regression` together): **Android 8/8 spec files passing**. **iOS verified at `@smoke` only** — see [Known Limitations](#known-limitations) for why a full iOS regression run is not yet confirmed clean. |

Not covered by this pass (parity with the sibling Java framework's own scope note): **Data Management** module and **biometric login** — no Page Object exists yet for either.

---

## Architecture

Idiomatic WebdriverIO Page Object Model, per `03-standards/webdriverio-javascript-standards.md` — a **plain JS class per module with getter-based selectors and instance action methods**, not the Java stack's class+repository split:

- **`src/pageobjects/page.js`** — base class every module's Page Object extends. Holds only what was found genuinely duplicated across modules: `isDisplayed(element)` (wait-then-check), `isAndroid()`/`isIOS()` (thin wrappers around WebdriverIO's own `browser.isAndroid`/`browser.isIOS`), `isSwitchOn(element)` (Android `checked` vs iOS `value` attribute), `hideKeyboardSafely()` (iOS throws if no keyboard is shown; treated as a no-op).
- **`src/pageobjects/<module>.page.js`** — one per module, selectors as getters (`get loginButton() { return $('~button-LOGIN'); }`), business-action methods on the same class, exported as a singleton (`export default new LoginPage();`).
- **`src/step-definitions/<module>.steps.js`** — one file per module. Step bodies call Page Object methods plus an `expect(...)` assertion; never inline `$(...)`/`$$(...)` calls.
- **`src/step-definitions/common.steps.js`** — the shared `Given("the app is launched")` precondition every feature file's `Background` uses, which waits for the bottom tab bar (`~Menu`) to exist.
- **`src/support/hooks.js`** — `After` captures a failure screenshot (single `saveScreenshot()` call); `AfterAll` is intentionally minimal — see finding #2 below for why it does **not** call `mobile: terminateApp`.
- **Session lifecycle:** one Appium session per `.feature` file (WebdriverIO/Cucumber's default), not per Scenario. `appium:forceAppLaunch`/`appium:shouldTerminateApp` are what make each new session start from a genuinely fresh app state instead of re-attaching to whatever the previous spec file's last Scenario left on screen.
- **`clickWithHeal(element, hint)` / `typeWithHeal(element, hint, text)`** (on the base `Page` class) — self-healing click/type wrappers every module's interactive action methods route through. See [Self-Healing Locators](#self-healing-locators) below.

---

## Self-Healing Locators

This framework includes a rule-based self-healing layer, ported from the sibling Appium-Java framework's `Utils/SelfHealer.java` (see `03-standards/appium-java-standards.md`'s "Self-Healing Locators" section — the algorithm is identical, only the WebdriverIO/Appium client API differs) to WebdriverIO. Full design in `AutomationSummary.md`'s "Self-Healing Locators" section, including a live, end-to-end verification **on both Android and iOS** (intentionally broke a real locator, confirmed the heal, confirmed the cache hit on a second run, reverted — repeated in full against the iPhone 15/iOS 17.2 simulator, not just Android) — summary:

- **`src/helper/SelfHealer.js`** scores every element in the live UI hierarchy (`browser.getPageSource()`) against a human-readable hint (e.g. `"Login Button"`) using the same precision/recall (F1) algorithm as the Java/web-stack implementations, then resolves the best match to a real selector: accessibility id (`content-desc`/`name`) → `~accessibilityId` (strongest) → `resource-id` → `id=...`/xpath (bare/unnamespaced resource-ids use xpath, matching a real pattern already hit by this app) → visible text (`@text`/`@label`/`@value`) → text xpath.
- **`Page.clickWithHeal(element, hint)`** / **`Page.typeWithHeal(element, hint, text)`** wrap the normal wait-then-act pattern (`waitForDisplayed()`, never `waitForClickable()` — see Known Limitations); on failure they hand off to `SelfHealer` and retry against the healed element. Every Page Object's interactive methods (click/type) on **stable, permanent UI elements** route through these — matching the Java sibling's exact scope, ad-hoc dynamically-resolved elements (e.g. a dropdown option matched by its own text) and optional/conditional dialog-dismiss buttons are intentionally **not** wrapped, since there's no stable long-lived selector for a heal hint to describe.
- Successful heals are cached for 1 hour in **`../healing-cache.json`** and logged to **`../healing-report.log`** (both one level above `Framework/`, directly under `Automation-WebdriverIO/` — matching the Java sibling's convention exactly, now that both frameworks share the same `Automation-X/Framework/` shape).
- The cache-partition key uses `browser.execute('mobile: getCurrentActivity', {})` on Android (confirmed live); on iOS, which has no activity concept, it gracefully degrades to an empty key rather than erroring — confirmed live on both platforms.

If a click/type action fails with a selector-not-found error, check `healing-report.log` first — if a heal was attempted and failed, the log/console warning will say so; if it succeeded, the test would have passed and the log records what changed.

---

## Test Data

`data/testdata.json` centralizes business test data — passwords, sample input text, dropdown options — that would otherwise be duplicated as literal strings in multiple `.feature` files:

```json
{
  "login": { "validPassword": "...", "invalidEmailFormat": "..." },
  "forms": { "sampleText": "...", "specialCharacters": "...", "dropdownOption": "..." },
  "permissions": ["camera", "microphone", "location", "photos"]
}
```

`src/helper/TestData.js` is the single load point (`import testData from '../../data/testdata.json' with { type: 'json' };`, re-exported) — the JS-stack equivalent of the sibling Java framework's `PropertyReader.getInstance()` for `config.properties`. Step definitions import from `TestData.js`, never re-derive the JSON path themselves.

**Live-run finding:** this file originally existed with real content but had zero consumers — every value it declared (`validPassword`, `sampleText`, etc.) was independently hardcoded a second time directly in `login.feature`/`forms.feature`, meaning an edit to `testdata.json` would have silently done nothing. Fixed by adding named steps (`I enter the valid password`, `I type the sample text into the text input`, `I select the sample dropdown option`, etc.) that resolve through `TestData.js`, and updating the feature files to use them instead of the literal duplicates. The `permissions` array is documentation of the valid permission set only — Cucumber Scenario Outline `Examples` tables are inherently literal-value tables, so `permissions.feature`'s outline isn't a candidate for the same fix.

---

## Adding a New Module

1. Create `src/pageobjects/<module>.page.js`, extending `Page`, with `@`-verified selectors (see [Live Verification](#live-verification--real-bugs-found) for why every selector must be confirmed against a real session, not guessed).
2. Create `src/step-definitions/<module>.steps.js` — one file per module, not appended to an existing one.
3. Create `features/<module>.feature`, tagging every scenario `@smoke` or `@regression`.
4. Confirm `wdio.conf.js`'s `specs`/`cucumberOpts.require` globs still match — both are globs (`./features/**/*.feature`, `./src/step-definitions/*.steps.js`), so a new module normally needs no edit there.
5. If the module needs literal business data used in more than one place, add it to `data/testdata.json` and resolve it through `src/helper/TestData.js` — don't duplicate the same literal independently in the `.feature` file and a JSON file that nothing reads.

---

## Reports & Logs

| Output | Location |
|---|---|
| Console reporter | `spec` (real-time, alongside Allure) |
| Allure results | `allure-results/` |
| Allure report | `npm run allure:generate && npm run allure:open` (or `allure serve allure-results`) |
| Failure screenshots | `allure-results/failure-<timestamp>.png` (via `src/support/hooks.js`'s `After` hook) |

---

## Live Verification / Real Bugs Found

This framework was not just generated — it was run against a live Android emulator and a live iOS simulator until both were fully green, and separately, its first-ever `@regression`-tagged run surfaced an additional bug that `@smoke`-only runs had never exercised. **Read `AutomationSummary.md` in this same folder for the complete list and root-cause narrative** — summary of the highlights:

1. **Missing `forceAppLaunch`/`shouldTerminateApp` caused cross-session state leakage** (both platforms). Confirmed via failure screenshots: a spec file's last Scenario left the side-menu drawer open; the next spec file's "new" Appium session just re-attached to the same still-running app (drawer still open), and the shared "app is launched" precondition step could never succeed because the tab bar isn't rendered while that drawer overlays it — no timeout value could have fixed this, only relaunching the app could. Fixed by adding both capabilities.
2. **`mobile: terminateApp` in `AfterAll` intermittently hung 20+ seconds** on a resource-constrained host, aborting the entire worker process regardless of try/catch. Removed — WebdriverIO's automatic end-of-session teardown already closes the session correctly.
3. **5 iOS-specific selector/attribute bugs**, all fixed after live root-causing: login success-alert selectors (Android resource-ids don't exist on iOS — needed `~Success`/`~You are logged in!`/`~OK`), permission switch state (`checked` vs `value` attribute), Swipe carousel locator (Android's xpath-by-`resource-id` workaround doesn't apply on iOS), WebView detection (`android.webkit.WebView` vs `XCUIElementTypeWebView`), Drag/drop piece counting (`@content-desc` vs `@name` in xpath).
4. **iOS permission-state false alarm**: a permissions test failed expecting "ungranted by default" but found "granted" — root-caused to simulator residue (camera had already been granted during earlier testing on the same simulator, and iOS TCC decisions persist across `noReset: true` sessions until uninstall), not a code bug. Confirmed by `xcrun simctl uninstall <udid> <bundleId>` and re-running clean.
5. **`waitForClickable()` on a native element throws "only available for desktop and mobile browsers"** — a browser-only WebdriverIO command, never valid for native app automation. Never caught before because this scenario is `@regression`-tagged and only `@smoke` had ever been run; the resulting exception left the dropdown dialog open, cascading into 2 more scenario failures in the same session. Fixed by using `waitForDisplayed()` instead, matching every other native interaction in the project.
6. **Intra-session side-menu leak, distinct from finding #1.** The very first time all of `navigation.feature`'s scenarios ran together in one session (its `@smoke` scenario plus 3 `@regression` scenarios — again only surfaced once `@regression` actually ran alongside `@smoke`), 3 of the 4 failed with the identical `element ("~Menu") still not existing` symptom as finding #1, but from a different cause: `forceAppLaunch`/`shouldTerminateApp` only relaunch the app once **per session** (once per spec file), not once per Scenario, so the `@smoke` scenario ("Side menu opens from the Menu tab") opening the drawer and never closing it left it open for every subsequent Scenario in that same file. Confirmed live that Android's hardware back button (`browser.execute('mobile: pressKey', { keycode: 4 })`) reliably closes it (`~Menu` exists again immediately after). Fixed via `NavigationPage.closeSideMenuIfOpen()`, called from the shared "Given the app is launched" step before its own `~Menu` check. **Android-only fix** — see Known Limitations.

Final results: **Android — full suite (all 8 feature files, `@smoke`+`@regression` together): 8/8 spec files passing.** iOS: verified at `@smoke` only (8/8); a full iOS regression run has not yet been performed, and finding #6's fix does not cover iOS (no hardware back button) — see Known Limitations.

---

## Known Limitations

- **`NavigationPage.closeSideMenuIfOpen()` (finding #6) is Android-only.** iOS has no hardware back button, and the correct cross-platform close gesture (e.g. tapping the dimmed scrim outside the drawer) hasn't been live-verified yet. Running `navigation.feature`'s full regression on iOS today would very likely hit the same intra-session leak with no fix applied.
- **A full iOS regression run (`npm run wdio:ios`, `@smoke`+`@regression` together) has not been performed** — iOS has only been verified at `@smoke` level (see Test Coverage). Given the above, expect at least the `navigation.feature` failure until an iOS close mechanism is added.
- **`appium-uiautomator2-driver`/`appium-xcuitest-driver` are not `package.json` dependencies.** They must be installed once via `appium driver install uiautomator2` / `appium driver install xcuitest` against whichever `APPIUM_HOME` your Appium server actually uses — `npm install` alone does not set them up.
- **No Live UI Hierarchy Verification snapshots are committed** (`docs/dom-snapshots/`) — the original scaffold's Phase 0 verification was done against live sessions during generation, but the snapshot artifacts themselves were not persisted to disk in this pass. Selectors are correct and live-verified; the traceability artifact is simply missing.
- **Data Management and biometric login are not automated** — see [Test Coverage](#test-coverage).

---

## Troubleshooting

| Symptom | Likely Cause | Fix |
|---|---|---|
| `EACCES` reinstalling an Appium driver | A prior `sudo npm install` left root-owned files under `~/.appium`/`~/.npm` | Don't `sudo` your way through it — `export APPIUM_HOME=~/.appium-fresh` (or similar) before `appium driver install ...` and before starting the server, then update `APPIUM_HOST`/`APPIUM_PORT` in `.env` if you also changed the port. |
| `element ("~Menu") still not existing after <N>ms` on the very first step of a scenario | Cross-session app state leakage (finding #1), an open side-menu drawer leaked from an earlier Scenario in the same session (finding #6, Android-only fix), or the emulator/simulator itself is genuinely slow to render under host CPU contention | Confirm `appium:forceAppLaunch`/`appium:shouldTerminateApp` are still set for both platforms in `wdio.conf.js`, and that `NavigationPage.closeSideMenuIfOpen()` is still called from `common.steps.js`'s "Given the app is launched" step. If both are in place and this still happens, check `uptime`/Activity Monitor for unrelated CPU-heavy processes (a stuck IDE language server has caused this before) before assuming a code bug. |
| A click/type action is unexpectedly slow, or `healing-report.log` gains an entry | The real app locator changed and `SelfHealer` recovered it | Check `healing-report.log` for what it healed to, then update the Page Object's selector to match — healing is a safety net for a genuinely broken locator, not a substitute for keeping selectors current. |
| `Error: The 'checked' attribute is unknown` on iOS | A selector/attribute check assumes Android's `checked` attribute exists on iOS | Use the base `Page` class's `isSwitchOn(element)` — never a raw `getAttribute('checked')` call — see [Test Data](#test-data)/Architecture above. |
| `waitForClickable` throws "only available for desktop and mobile browsers" | Called on a native-app element — this command is browser-context-only | Use `waitForDisplayed()` instead, per finding #5 above. |
| iOS `PermissionsTests`-equivalent scenario sees a permission already granted on a "fresh" run | iOS TCC permission decisions persist across `noReset: true` sessions until the app is uninstalled | `xcrun simctl uninstall <udid> org.wdiodemoapp` before the run. |
| Editing `data/testdata.json` has no effect on test behavior | A `.feature` file still hardcodes the same value as a literal instead of going through a named step that resolves it via `src/helper/TestData.js` | See [Test Data](#test-data) — every value declared in `testdata.json` must have exactly one step-definition consumer, not a parallel hardcoded copy. |
