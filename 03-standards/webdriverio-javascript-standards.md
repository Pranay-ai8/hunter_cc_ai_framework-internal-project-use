# WebdriverIO JavaScript Automation Standards

## Scope

This standard governs the **WebdriverIO + JavaScript (ES Modules) + Appium + Cucumber** mobile automation stack — the JavaScript-stack sibling of `03-standards/appium-java-standards.md` (Java/TestNG/Maven). Both target the same class of app (native Android/iOS via Appium); this standard is JavaScript-only and always Cucumber-BDD (Gherkin `.feature` files + step definitions), never a plain `mocha`/`jasmine` runner and never TypeScript in this framework.

Reference implementation this standard is modeled on: `/Users/ostan/Downloads/SwagLabs-master` (WebdriverIO + Appium + Cucumber against the Sauce Labs mobile sample app).

There is no browser DOM in a native mobile app and no Cypress/Selenium/Playwright construct belongs in this stack — see "Prohibited Patterns" at the end.

---

## Stack

- **Language:** JavaScript (ES Modules — `"type": "module"` in `package.json`)
- **Automation Driver:** WebdriverIO (`webdriverio` via `@wdio/cli` + `@wdio/local-runner`)
- **Mobile Driver:** Appium 2.x, via `@wdio/appium-service` (the service starts/stops the Appium server around the WDIO run — never spawn Appium manually in a generated project)
- **Platform Drivers:** `appium-uiautomator2-driver` (Android), `appium-xcuitest-driver` (iOS)
- **BDD Layer:** `@wdio/cucumber-framework` + `@cucumber/cucumber` — Gherkin `.feature` files, one step-definition module per app module (see "Step Definitions" below)
- **Reporting:** `@wdio/allure-reporter` (primary) — `allure-results/` → `allure serve` / `allure generate`
- **Assertions:** WebdriverIO's built-in `expect` (`@wdio/globals`) — never a separate assertion library (`chai`, `assert`) in this stack
- **Env/config:** `dotenv` — `.env` at the project root, never committed with real values

---

## Module System

- **ES Modules only** (`import`/`export default`, `import`/`export`), never CommonJS (`require`/`module.exports`). `package.json` must declare `"type": "module"`.
- Every Page Object module has a single `export default` — an instance (`export default new LoginPage()`), not the bare class — see "Page Object Model" below.
- Step-definition and hook files use named imports from `@wdio/cucumber-framework` (`Given`, `When`, `Then`) and `@cucumber/cucumber` (`Before`, `After`, `BeforeAll`, `AfterAll`), plus `browser`/`$`/`$$`/`expect` from `@wdio/globals`.

---

## Project Structure (Mode A — new framework scaffold)

```
<project-root>/
├── package.json
├── wdio.conf.js
├── .env
├── .env.example
├── .gitignore
├── README.md
├── data/
│   └── testdata.json
├── docs/
│   └── dom-snapshots/
│       └── <module>.json
├── src/
│   ├── helper/
│   │   ├── Utils.js               ← context switching (hybrid), wifi toggles, misc device helpers
│   │   ├── gestures.js             ← swipe/scroll/drag/pinch/long-press via W3C Actions API
│   │   └── SelfHealer.js           ← F1-scored locator self-healing (see "Self-Healing Locators" below)
│   ├── pageobjects/
│   │   ├── page.js                 ← base Page class — shared cross-page helpers (see "Page Object Model" below)
│   │   └── <module>.page.js        ← one per app module (e.g. login.page.js, dashboard.page.js)
│   ├── step-definitions/
│   │   └── <module>.steps.js       ← one per app module (see "Step Definitions" below)
│   └── support/
│       └── hooks.js                ← Before/After/AfterAll — app relaunch/teardown hygiene
└── features/
    └── <module>.feature            ← one Gherkin file per app module — `features/` holds ONLY .feature files
```

**Live-run correction: `pageobjects`/`step-definitions`/`support`/`helper` live under a top-level `src/`, not nested inside `features/`.** The reference SwagLabs project nests them inside `features/`; this repo's actual generated framework was corrected to the `src/` layout above after explicit user direction, applied post-scaffold. Follow the `src/` layout for every new scaffold — do not revert to nesting these four directories inside `features/`, which now holds only `.feature` files. Do not split `pageobjects`/`step-definitions` by platform (Android/iOS differences live inside a single Page Object's selectors, not in parallel folder trees).

---

## Session & App Lifecycle

WebdriverIO + `@wdio/appium-service` starts **one Appium/WebDriver session per worker process**, and by default a Cucumber run assigns one worker per spec file. Since `specs` in `wdio.conf.js` glob whole `.feature` files, the practical unit of session lifetime is **one Appium session per feature file**, not one session per Scenario, and not one long-lived session for the whole suite. Do not fight this by scripting manual `driver.deleteSession()`/re-`createSession()` calls in step definitions or hooks — that duplicates what the runner and the `appium` service already manage.

What *does* need explicit hygiene between Scenarios sharing a session is **app state**, not the WebDriver session itself:

- Set `appium:forceAppLaunch: true` — forces a fresh launch of the AUT even if it's already running, so a Scenario never silently inherits another Scenario's leftover screen state. **Non-negotiable, not optional:** a live run without this capability produced a confirmed real bug — a spec file's last scenario left a side-menu drawer open, the next spec file's new Appium session simply re-attached to that same still-running app instance (drawer still open), and every subsequent spec's shared "app is launched" precondition step (which only checked that the bottom tab bar existed) hung/failed indefinitely, because the tab bar genuinely isn't rendered while that drawer overlays it — no timeout value fixes this, only relaunching the app does.
- Set `appium:shouldTerminateApp: true` — ensures the AUT is actually terminated when the session ends (or when `driver.terminateApp()`/`driver.closeApp()` is called), instead of being left backgrounded for the next Scenario to stumble into. Set alongside `forceAppLaunch` above, not in isolation — together they are what prevent the cross-spec-file state leakage described above.
- Prefer capability-level hygiene (`forceAppLaunch`/`shouldTerminateApp`) over ad hoc `driver.reset()` calls in step definitions — `driver.reset()` (equivalent to `fullReset`) is slow (full app data wipe + reinstall) and is a last resort for state-corruption scenarios only, not a per-Scenario default.
- Where a Scenario must guarantee a truly clean start (e.g., first-launch onboarding, a scenario that depends on no prior login session), do it explicitly and visibly in that feature's `Before`/`Given` step (e.g., `await driver.terminateApp('<bundleId/package>'); await driver.activateApp('<bundleId/package>');`) rather than relying on a global capability that silently affects every other Scenario in the file.
- **Live-run correction — do not call `mobile: terminateApp` in `AfterAll`.** An earlier version of this guidance recommended an `AfterAll` hook that force-stops the app via `browser.execute('mobile: terminateApp', ...)`. In practice, on a resource-constrained host the underlying `adb shell am force-stop`/simulator-terminate call intermittently hung for 20+ seconds, and Cucumber's own hook-level timeout aborted the entire worker process regardless of any try/catch around the call — a "cosmetic" cleanup call taking down a whole spec file's results. WebdriverIO's automatic end-of-session teardown (`deleteSession`, run after every spec file) already closes the Appium session correctly. Leave `AfterAll` for genuinely necessary end-of-run bookkeeping only (e.g., a log line) — do not add an app-termination call there. See "Hooks" below for the validated `hooks.js` shape.

### Intra-Session State Leakage (Same Spec File, Across Scenarios)

`forceAppLaunch`/`shouldTerminateApp` above only guard the boundary **between spec files** (each gets its own session). They do nothing for two Scenarios inside the **same** `.feature` file, which share one live session and therefore one continuous app state. **Confirmed live bug:** running the full suite (`@smoke` + `@regression` together) for the first time — not just `@smoke` alone — surfaced a case where a `navigation.feature` Scenario left the side-menu drawer open, and the next Scenario in that same file inherited it, hiding the bottom tab bar its own `Given the app is launched` precondition step depends on.

Fix pattern: a defensive `closeSideMenuIfOpen()` on the relevant Page Object, called from the shared launch precondition step *before* it asserts anything else exists:

```javascript
// src/pageobjects/navigation.page.js
async closeSideMenuIfOpen() {
    const isOpen = await this.sideMenuPanel.isExisting().catch(() => false);
    if (!isOpen) {
        return;
    }
    if (this.isAndroid()) {
        await browser.execute('mobile: pressKey', { keycode: 4 }); // hardware back button
    }
    // No iOS equivalent implemented — confirmed Android-only; iOS full regression
    // (as opposed to @smoke) was never run against this scenario ordering, so an
    // iOS-side leak of the same shape remains an open, undemonstrated gap.
}
```

```javascript
// src/step-definitions/common.steps.js
Given(/^the app is launched$/, async () => {
    await NavigationPage.closeSideMenuIfOpen();
    await NavigationPage.tabMenu.waitForExist();
});
```

This class of bug is invisible when only `@smoke` scenarios run (each spec file's first scenario always starts clean) — it only shows up once `@regression` scenarios run in sequence after `@smoke` ones in the same file. **Always run the full suite, not just `@smoke`, at least once before declaring a generated framework's suite complete** — the same lesson as the `waitForClickable()` bug documented in "Prohibited Patterns" below.

---

## Device Readiness (Emulator/Simulator Auto-Launch)

Unlike the Java sibling stack, this stack has **no runtime auto-detection at all** — `wdio.conf.js`'s Android capabilities read `appium:deviceName` directly from `process.env.ANDROID_DEVICE_NAME` (an ADB serial, e.g. `emulator-5554`), and iOS reads `IOS_DEVICE_NAME`/`IOS_PLATFORM_VERSION`/`IOS_UDID` the same way — there is no `DeviceConfig`-equivalent that discovers these at test-run time. That makes device readiness a **harder requirement here than for the Java stack**: not only must a device be connected/booted before any session can start, its identifiers must also be captured into `.env` during generation, or every generated capability object is left pointing at empty strings.

**Live-run correction:** earlier versions of this workflow only collected app path/package/bundle id (Step 3a) and left device identifiers to be filled in by the user post-generation, based on a Next Steps instruction to "start an emulator/simulator" — this produced a `.env` that could not actually start a session until the user manually ran `adb devices`/`xcrun simctl list` themselves and pasted the result in. `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md`'s Step 3b closes this by performing the check-then-boot sequence below and writing its result directly into `.env`, so generation output is immediately runnable per the "Scaffold... immediately usable" rule already stated elsewhere in this doc.

### Android

1. **Check:** `adb devices` — any line ending in `\tdevice` (not `unauthorized`/`offline`) counts as ready. Its serial (e.g. `emulator-5554`) becomes `ANDROID_DEVICE_NAME` directly — this stack's convention treats the ADB serial itself as the capability value, not a human device model name (see "`wdio.conf.js` Capability Conventions" below).
2. **If none ready:** `emulator -list-avds` to enumerate installed AVDs.
   - Exactly one AVD → boot it automatically: `emulator -avd <name> -no-snapshot -no-audio &` (background/detached).
   - More than one AVD and the user hasn't already named a device → ask which AVD to launch; do not guess.
   - Zero AVDs → **stop and ask** the user to create one — this requires an installed system image this workflow cannot fetch or assume.
3. **Wait for boot completion:** `adb wait-for-device`, then poll `adb shell getprop sys.boot_completed` until it returns `1`, bounded by a timeout (120s default). On timeout, stop and report a boot failure.
4. Re-run `adb devices` to confirm the connected serial, then write it to `.env`'s `ANDROID_DEVICE_NAME`.

### iOS (macOS host only)

1. **Check:** `xcrun simctl list devices booted` — any entry under a "Booted" heading counts as ready. Capture its UDID, device name, and iOS version from the listing.
2. **If none booted:** `xcrun simctl list devices available` to enumerate created-but-not-booted simulators.
   - Exactly one match for the required iOS version (or none specified — pick the newest installed runtime's default iPhone) → boot it: `xcrun simctl boot <udid>`.
   - More than one plausible match and the user hasn't already named a device → ask which simulator to boot; do not guess.
   - Zero simulators exist for the required runtime → attempt `xcrun simctl create` only when exactly one installed runtime and device type exist; otherwise stop and ask the user to create one via Xcode.
3. **Wait for boot completion:** poll `xcrun simctl list devices` until the target UDID reads `Booted`, bounded by a timeout (60s default). On timeout, stop and report a boot failure.
4. Write the resulting UDID/name/version to `.env`'s `IOS_UDID`/`IOS_DEVICE_NAME`/`IOS_PLATFORM_VERSION`. `open -a Simulator` afterward is optional (foregrounds the simulator window) — not required for Appium to drive it.

### Scope

This check runs once per generation session (and should be re-run, not assumed still valid, before any subsequent live test execution — a device can be closed/rebooted between sessions) — it is not baked into the generated framework's own runtime code. `wdio.conf.js` remains exactly as documented below: it reads whatever `.env` says and fails fast if a session can't be established, matching every other WebdriverIO project's convention. Auto-launch is an **agent-time convenience**, not a feature of the shipped test suite.

---

## Live UI Hierarchy Verification (Mandatory for First-Time Generation)

See `03-standards/locator-standards.md`'s "Live DOM Verification" section for the full cross-stack rationale, and `03-standards/appium-java-standards.md`'s "Live UI Hierarchy Verification" section for the sibling Java-stack mechanics this section mirrors. There is no browser DOM in a native mobile app; the equivalent artifact is the UI element hierarchy Appium exposes via `driver.getPageSource()` (WebdriverIO exposes the same call as `browser.getPageSource()`).

**Never fabricate a selector from static APK/IPA inspection, a design spec, or training-data assumptions about "what Appium usually calls this."** Every selector written into a Page Object module, the first time that module is automated, must be confirmed against a real, live snapshot of the app's UI hierarchy captured during an actual Appium session — not transcribed from a document on trust.

### Prerequisite

Exactly as for the Java stack: `AppPath`/`AndroidAppPackage`+`AndroidAppActivity` (Android) or `BundleId` (iOS) must be confirmed **before** any scaffolding or verification — there is no default, no placeholder, and no way to guess a binary's location or an installed package's identifier. This is collected in Step 3a of `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md`, never deferred to a post-generation ".env edit" step.

A confirmed app identity is not enough on its own — this stack additionally needs `.env`'s device identifiers (`ANDROID_DEVICE_NAME` / `IOS_UDID`+`IOS_DEVICE_NAME`+`IOS_PLATFORM_VERSION`) populated from a **ready**, not just installed, emulator/simulator. See "Device Readiness" above and `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md`'s Step 3b — do not attempt this mechanism against a device that hasn't been confirmed ready, and do not proceed with an empty device identifier in `.env`.

### Mechanism

Because this generation activity happens **outside** a running Cucumber suite (there is no test yet to attach to — the Page Object doesn't exist), do not try to drive the snapshot through `wdio.conf.js`/Cucumber. Use WebdriverIO's **standalone mode** to open a throwaway session directly:

```javascript
// Temporary, one-off script — never committed, never part of the generated framework.
// Deleted immediately after the snapshot is captured, mirroring the Cypress-stack
// convention of a temporary _domSnapshot.cy.js spec that is not part of the final suite.
import { remote } from 'webdriverio';
import fs from 'node:fs';

const driver = await remote({
    hostname: '127.0.0.1',
    port: 4723,
    capabilities: {
        platformName: 'Android',
        'appium:deviceName': process.env.ANDROID_DEVICE_NAME,
        'appium:platformVersion': process.env.ANDROID_DEVICE_VERSION,
        'appium:automationName': 'UiAutomator2',
        'appium:app': process.env.ANDROID_APP_PATH,
        'appium:appPackage': process.env.ANDROID_APP_PACKAGE,
        'appium:appActivity': process.env.ANDROID_APP_ACTIVITY,
        'appium:autoGrantPermissions': true,
        'appium:noReset': true,
    },
});

// Navigate to the module's screen first (e.g., perform login using
// already-verified locators from a prior module, per dependency order below).

const pageSourceXml = await driver.getPageSource();
fs.writeFileSync('docs/dom-snapshots/<module>.xml', pageSourceXml);

// Parse into a flat candidate list — regex-based, no DOM/XML library dependency
// needed for a one-off script. Capture the same attribute set as the Java stack:
// class, resource-id, content-desc (Android a11y id), text, name/label/value/type (iOS).
const candidates = [...pageSourceXml.matchAll(/<(\S+)\s+([^>]*)\/?>/g)].map(([, tag, attrsRaw]) => {
    const attrs = Object.fromEntries(
        [...attrsRaw.matchAll(/([\w-]+)="([^"]*)"/g)].map(([, k, v]) => [k, v]),
    );
    return { tag, ...attrs };
});
fs.writeFileSync('docs/dom-snapshots/<module>.json', JSON.stringify(candidates, null, 2));

await driver.deleteSession();
```

An equivalent iOS capability block substitutes `platformName: 'iOS'`, `'appium:automationName': 'XCUITest'`, `'appium:bundleId'`/`'appium:app'`, and reads `name`/`label`/`type`/`value` from the XML instead of `resource-id`/`content-desc`/`text`.

### Process

1. **Start a real Appium session** via the standalone script above (or, if a suite already exists for this project, by temporarily driving through it) against a running emulator/simulator or connected device, using the confirmed app path/package/activity/bundle ID, and launch the app.
2. **Navigate to the module's screen**, performing whatever setup flow is required (e.g., logging in first, using credentials confirmed per `locator-standards.md`'s prerequisite step). Verify modules in dependency order — splash/login before anything gated behind it — reusing already-verified locators from prior modules to drive the navigation.
3. **Capture the UI hierarchy** via `driver.getPageSource()` and parse it into a candidate list, as shown above.
4. **For every locator about to be written into a Page Object module:** if a documented value (accessibility id, resource-id) matches a real candidate exactly, use it. If not, use the actual best-matching candidate's real value and record a discrepancy note (module, documented value, actual value, snapshot date) in the generation summary — never silently substitute. If nothing plausible exists, stop and flag it as a blocking gap rather than emitting a placeholder selector.
5. **Persist both the raw XML and the parsed candidate list** to `docs/dom-snapshots/<module>.xml` / `.json` (relative to the project root) for traceability.
6. This runs **once per module** during initial Page Object generation, not repeated on every subsequent run.

A generated Page Object module should only ever contain a `// TODO: verify selector — live session unavailable` placeholder when a real device/emulator session genuinely could not be established for that screen, and that exception must be explained in the generation summary — never left silent, and never used as a shortcut when a session was in fact available.

---

## Locator Priority (Appium + WebdriverIO)

Selectors are plain strings passed to WebdriverIO's `$()`/`$$()` — there is no annotation-based decorator layer like the Java stack's `@AndroidFindBy`/`@iOSXCUITFindBy`. Always select in this priority order, adapted from `03-standards/locator-standards.md`'s cross-stack priority for mobile:

| Priority | Strategy | WebdriverIO syntax | When to Use |
|---|---|---|---|
| **1** | Accessibility ID | `$('~loginButton')` | Preferred on both platforms — maps to Android `content-desc` / iOS `accessibility identifier`. Cross-platform when the app assigns the same a11y id on both builds. |
| **2** | Android `UiSelector` | `$('android=new UiAutomator2.UiSelector().resourceId("com.pkg:id/login_button")')` | Android-only, when no accessibility id exists — resource-id, text, class-based `UiSelector` chains |
| **2** | iOS predicate string / class chain | `$('-ios predicate string:name == "loginButton"')` / `$('-ios class chain:**/XCUIElementTypeButton[\`name == "loginButton"\`]')` | iOS-only, when no accessibility id exists |
| **3** | XPath | `$('//android.widget.Button[@text="LOGIN"]')` | Last resort only — document why in a comment; never a deep layout-dependent path (`//*/android.view.ViewGroup[3]/...`) |

Never use plain `class name` selectors (`$('android.widget.Button')`) or index-based selection (`$$('android.widget.Button')[2]`) without a stable scoping ancestor — both are exactly as fragile here as their web-stack equivalents in `03-standards/locator-standards.md`'s Anti-Patterns section.

### Examples

```javascript
// Priority 1 — accessibility id (preferred, cross-platform)
get inputUsername() { return $('~test-Username'); }
get btnSubmit()     { return $('~test-LOGIN'); }

// Priority 2 — Android UiSelector (no accessibility id available)
get productSortDropdown() {
    return $('android=new UiAutomator2.UiSelector().resourceId("com.swaglabsmobileapp:id/spinner_container")');
}

// Priority 2 — iOS predicate string (no accessibility id available)
get productSortDropdown() {
    return $('-ios predicate string:name == "spinner_container"');
}

// Priority 3 — XPath (last resort, documented)
// No accessibility id or resource-id on this legacy screen; text is the only stable anchor.
get flashAlert() {
    return $("//android.view.ViewGroup[@content-desc='test-Error message']/android.widget.TextView");
}
```

---

## Cross-Platform Selector & Attribute Reference

Confirmed live against the same target app on both platforms — these are the concrete attribute/selector differences that come up whenever a Page Object supports both Android and iOS, not theoretical guidance. Branch on `this.isAndroid()`/`this.isIOS()` (see the base `Page` class above) rather than writing one selector/xpath that tries to serve both.

| Concern | Android | iOS | Notes |
|---|---|---|---|
| Switch/toggle on-state | `getAttribute('checked')` → `"true"`/`"false"` | `getAttribute('value')` → `"1"`/`"0"` | Use the base class's `isSwitchOn(element)` — never inline the raw attribute check per module |
| Text content in an xpath | `@text` | `@label` / `@name` / `@value` (no single attribute is authoritative — check all three) | A cross-platform "contains text" xpath needs a 4-way `or`: `contains(@text,"...") or contains(@label,"...") or contains(@name,"...") or contains(@value,"...")` |
| Cross-platform identifier in an xpath | `@content-desc` | `@name` | Same shape as above — e.g. `starts-with(@content-desc,"drag-") or starts-with(@name,"drag-")` |
| Unnamespaced `resource-id` | Needs an xpath workaround (`//*[@resource-id="foo"]`) — a plain `id=foo` lookup auto-prefixes the current app package (`com.pkg:id/foo`) and never matches a colon-less id | No workaround needed — the same identifier is exposed directly as an accessibility id (`~foo`) | Confirmed on this app's Swipe carousel and Drag/Drop puzzle pieces on both stacks (Java and this one) |
| WebView / hybrid content view | `$('android.webkit.WebView')` (bare class-name string) | `$('XCUIElementTypeWebView')` (bare class-name string) | WebdriverIO auto-detects the "class name" locator strategy for both bare-string shapes — no explicit strategy prefix needed |
| Native alert/dialog (app-presented, not OS-level) | Resource-id based: `id=android:id/button1` (OK), `id=android:id/message`, `id=<pkg>:id/alert_title` | Accessibility-id based, keyed to the alert's own text: `~OK`, `~<exact message text>`, `~<exact title text>` | iOS has no resource-id concept at all — there is nothing to fall back to except the alert's own rendered text |
| Permission grant model | `appium:autoGrantPermissions` pre-grants everything before the app is even reached — permissions read as already-granted from the first screen | No equivalent capability — every permission starts **ungranted**; the first toggle triggers a real system alert outside the app's own accessibility tree, handled by `appium:autoAcceptAlerts` | An assertion like "permission X is granted by default" must branch its *expected* value on platform — this is a genuine OS security-model difference, not a bug to paper over |
| iOS permission state across runs | N/A | Persists across `noReset: true` sessions until the app is uninstalled (unlike Android, which resets via `autoGrantPermissions` every session) | Run `xcrun simctl uninstall <udid> <bundleId>` before a test run that depends on a clean permission baseline |
| Keyboard dismissal | `browser.hideKeyboard()` generally safe | Frequently throws ("Did not know how to dismiss the keyboard") when called with no field focused/keyboard shown | Always route through the base class's `hideKeyboardSafely()`, never a raw `browser.hideKeyboard()` call in a Page Object |

---

## Page Object Model

This stack's idiomatic pattern is a **plain JS class with getter-based selectors and instance action methods**, exported as a singleton instance — this is WebdriverIO's own convention (per the WDIO Cucumber boilerplate and the reference SwagLabs project), **not** the Java stack's class+repository split (`<Module>Page` + `<Module>PageObjectRepository`). Do not introduce a separate locator-repository class in this stack.

### Base `Page` class (`src/pageobjects/page.js`)

Keep this file thin — module-specific logic belongs in the module's own page object — but do include the following, all confirmed as genuine cross-module duplication once a real multi-module suite existed (each was found copy-pasted verbatim into 2+ page objects before being pulled up here):

```javascript
import { findHealedLocator } from '../helper/SelfHealer.js';

export default class Page {
    /** Wait for an element to be displayed, then return whether it is — the
     * "waitForDisplayed() + isDisplayed()" pair was found duplicated verbatim
     * across every module's "is<Module>ScreenDisplayed()" method. */
    async isDisplayed(element) {
        await element.waitForDisplayed();
        return element.isDisplayed();
    }

    /** Prefer WebdriverIO's own `browser.isAndroid`/`browser.isIOS` booleans
     * (auto-derived from session capabilities) directly where a single
     * expression suffices. These wrappers exist only for readability inside a
     * Page Object's own methods (`this.isAndroid()` reads slightly better than
     * `browser.isAndroid` scattered through business-action bodies) — they are
     * a style convenience, not new capability detection logic. */
    isAndroid() {
        return browser.isAndroid;
    }

    isIOS() {
        return browser.isIOS;
    }

    /** Android exposes a switch/toggle's on-state via the `checked` attribute
     * ("true"/"false"); XCUITest has no such attribute — iOS switches expose
     * it via `value` ("1"/"0"). See the "Cross-Platform Selector & Attribute
     * Reference" section below. */
    async isSwitchOn(element) {
        if (this.isIOS()) {
            return (await element.getAttribute('value')) === '1';
        }
        return (await element.getAttribute('checked')) === 'true';
    }

    /** Some keyboard/field combinations throw if the keyboard isn't currently
     * shown (confirmed live on iOS — WDA errors "Did not know how to dismiss
     * the keyboard" whenever it's called with no keyboard visible) — treat
     * that as a no-op rather than a real failure. */
    async hideKeyboardSafely() {
        try {
            await browser.hideKeyboard();
        } catch (e) {
            // No-op if the keyboard was already hidden/never shown.
        }
    }

    /** Self-healing click — see "Self-Healing Locators" below for the full
     * mechanism. Waits and clicks normally; on failure, hands `hint` (a
     * human-readable name, e.g. "Login Button") to `SelfHealer.findHealedLocator`
     * and retries against the healed element. Rethrows the original error
     * unchanged if healing fails, so a genuine failure is never silently lost.
     * Uses `waitForDisplayed()`, not `waitForClickable()` — see "Prohibited
     * Patterns" below for why the latter is never valid on a native element. */
    async clickWithHeal(element, hint) {
        try {
            await element.waitForDisplayed();
            await element.click();
        } catch (originalError) {
            const healed = await this.healOrRethrow(hint, originalError);
            await healed.click();
        }
    }

    /** Self-healing type-into-field — see `clickWithHeal` above. */
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

Import `browser` from `@wdio/globals` at the top of this file for the methods above to resolve.

### Module Page Object (`src/pageobjects/<module>.page.js`)

```javascript
import { $ } from '@wdio/globals';
import Page from './page.js';

/**
 * Page object for the Login screen.
 */
class LoginPage extends Page {
    /**
     * Selectors declared as getter methods — evaluated fresh on every access
     * (never cached in a constructor field, since native screens are frequently
     * torn down/rebuilt and a stale cached element reference will go stale).
     */
    get inputUsername() { return $('~test-Username'); }
    get inputPassword() { return $('~test-Password'); }
    get btnSubmit()     { return $('~test-LOGIN'); }
    get flashAlert()    { return $("//android.view.ViewGroup[@content-desc='test-Error message']/android.widget.TextView"); }

    /**
     * Business action — composes the selectors above. Step definitions call this,
     * never the getters directly.
     */
    async login(username, password) {
        await this.inputUsername.setValue(username);
        await this.inputPassword.setValue(password);
        await this.btnSubmit.click();
    }
}

export default new LoginPage();
```

Rules:

- **File naming:** `<module>.page.js`, camelCase/lowercase matching the screen (e.g., `login.page.js`, `dashboard.page.js`, `productDetails.page.js`).
- **Class naming:** PascalCase + `Page` suffix (`LoginPage`, `DashboardPage`).
- **Export convention:** `export default new <Name>Page();` — a singleton instance, not the class itself. Step definitions `import LoginPage from '../pageobjects/login.page.js'` and call methods directly on it — never `new LoginPage()` in a step file.
- **Selectors are getters, never constructor fields** — a constructor-assigned `this.inputUsername = $('~test-Username')` resolves once and can go stale; a getter re-resolves on every access, which is the safe default for a native UI that rebuilds screens on navigation.
- **Never call `$()`/`$$()` inline inside an action method or step definition** — every selector is declared as a getter first, exactly as `03-standards/locator-standards.md`'s "Locator Declaration Rules" mandates for the Cucumber+web-stack convention.
- Business-action methods (`login()`, `addToCart()`, `logOutClick()`) live on the same class as the selectors — there is no separate "action" layer to keep in sync, unlike the Java stack's Page Action Class / Page Object Repository split.
- Dynamic locators (e.g., a product row keyed by name) are parameterized getter-returning methods: `getProductRow(name) { return $(\`//android.widget.TextView[@text="${name}"]\`); }`.

---

## Self-Healing Locators

Every project generated for this stack must include a self-healing layer so a stale/renamed selector degrades to an automatic, logged recovery instead of an immediate hard failure. This is **mandatory scaffold**, not optional — ported from `03-standards/appium-java-standards.md`'s "Self-Healing Locators" (itself ported from the Playwright-Java reference implementation): same F1 scoring model, same cache/log convention, adapted for WebdriverIO's `browser.getPageSource()`/`browser.$()` in place of a `WebElement`/`By` API.

Healing lives in the base `Page` class's `clickWithHeal(element, hint)` / `typeWithHeal(element, hint, text)` wrapper methods (see the base `Page` class code block above) — module Page Objects route stable, permanent-element interactions through these instead of calling `.click()`/`.setValue()` directly, passing a short human-readable hint string:

```javascript
// src/pageobjects/login.page.js
async login(username, password) {
    await this.typeWithHeal(this.inputUsername, 'Username Input', username);
    await this.typeWithHeal(this.inputPassword, 'Password Input', password);
    await this.clickWithHeal(this.btnSubmit, 'Login Button');
}
```

**Only wrap stable, always-present elements** — a login button, a tab, a permanent form field. Deliberately do **not** wrap: READ methods (`getText()`, `isDisplayed()`-style assertion targets — silently healing an assertion's target risks turning a real failure into a false pass), ad-hoc/dynamically-resolved elements (e.g. a dropdown option selected by its own text), or optional/conditional elements (a dialog's dismiss/cancel button that may not even be on screen) — same scope discipline as the Java sibling framework.

### Module: `src/helper/SelfHealer.js`

```javascript
export async function findHealedLocator(hint, originalError) { /* returns { success, element, confidence } — never throws */ }
```

Algorithm (identical scoring model to the Java/Playwright reference implementations, WebdriverIO-adapted collection/resolution):

1. **Candidate collection** — parse `browser.getPageSource()` (the UiAutomator2/XCUITest XML hierarchy) with a lightweight regex element/attribute scan; keep only elements carrying at least one of `content-desc`/`name` / `resource-id` / `text`/`label`/`value`, capped at `MAX_SNAPSHOT_ELEMENTS` (150).
2. **Tokenize** the hint into a lower-case word set (≥3 chars, deduped) — hint-only, same rationale as the Java/Playwright implementations.
3. **Score** each candidate by precision/recall (F1) over content-desc/name + resource-id (local name after the last `/`) + text/label/value — deliberately **excluding** the element's tag/class: structural noise, not semantic identity.
4. **Resolve** the best candidate (score ≥ 0.75) to a real WebdriverIO selector, in priority order: accessibility id (`~<value>`) → resource-id (`id=<name>` shorthand if namespaced, else an xpath — an unprefixed resource-id breaks WebdriverIO's `id=` strategy the same way it breaks Java's `By.id`) → text xpath.
5. **Validate** the resolved selector actually matches something on screen right now (`browser.$(selector).isExisting()`) before trusting it.
6. **Cache** successful heals for 1 hour, keyed by `<screenKey>|<hint>` — `screenKey` comes from `browser.execute('mobile: getCurrentActivity', {})` wrapped in try/catch, an **Android-only** signal that gracefully degrades to `""` on iOS (there is no activity-equivalent concept to key on) rather than throwing — in `healing-cache.json` at the project root (one level above this framework's own folder, matching the Java sibling's convention).
7. **Log** every successful heal to `healing-report.log` at the same location.

**Live-verified on both platforms** — a run that deliberately renamed a stable selector was confirmed to trigger a heal (visible in `healing-report.log`) and still pass, on both an Android emulator and an iOS simulator session, not just Android.

---

## Step Definitions

- **One step-definition file per app module** (`src/step-definitions/<module>.steps.js`), not one shared `steps.js` for the whole suite. The reference project's single `steps.js` is acceptable only for a tiny single-feature proof of concept; any real generated framework (more than one or two modules) must split by module so step files stay reviewable and merge conflicts stay localized. `wdio.conf.js`'s `cucumberOpts.require` lists every module's step file explicitly (or a glob, e.g. `./src/step-definitions/**/*.steps.js`).
- Import `Given`/`When`/`Then` from `@wdio/cucumber-framework` (not `@cucumber/cucumber` — the WDIO adapter re-exports the compatible versions already wired into the runner).
- Import `expect`/`$`/`browser` from `@wdio/globals` when a step needs them directly (most assertions should still go through the Page Object where practical).
- Step definitions **orchestrate calls into a Page Object's action/business methods** — never inline raw `$(...)`/`$$(...)` selector calls or raw multi-step interaction logic directly in a step file. A step body should read as a short sequence of Page Object method calls plus an `expect(...)` assertion, not a re-implementation of the interaction.
- Reference the source TC ID in a leading comment or the step text so every generated step remains traceable to `FunctionalTestCases.md`.
- Use Allure's `allure.addStep(...)`/`allure.addAttachment(...)` (via `@wdio/allure-reporter`) for readable step-by-step reporting and failure screenshots, per the reference project's pattern.
- No hardcoded `browser.pause(<ms>)` as a wait strategy — use `browser.waitUntil(...)`, an element's built-in retry-ability (`expect(el).toBeDisplayed()`), or an explicit `waitForDisplayed()`/`waitForExist()` call with a documented timeout.

---

## `wdio.conf.js` Capability Conventions

Every generated `wdio.conf.js` capabilities block must set the following mobile-hygiene capabilities explicitly — do not rely on Appium/driver defaults, which vary by version:

| Capability | Purpose | Default in generated scaffold |
|---|---|---|
| `appium:autoGrantPermissions` | Android only — auto-grants all normal runtime permissions on install, so a permissions dialog never blocks the first screen a test needs. iOS has no equivalent capability; it starts every permission ungranted by design (see "Cross-Platform Selector & Attribute Reference" below) | `true` (Android) |
| `appium:autoAcceptAlerts` | iOS only — accepts real system permission alerts automatically (there is no `autoGrantPermissions` equivalent on iOS). **Caution:** this also auto-taps app-level `UIAlertController`s the app itself presents, before a step's own explicit dismiss code runs — write alert-dismissal steps defensively (check the button exists first, treat "already gone" as success) rather than racing a hard wait-then-click against it | `true` (iOS) |
| `appium:forceAppLaunch` | Forces a fresh app launch even if the session's app is already running — see "Session & App Lifecycle" above. **Mandatory on both platforms**, not a nice-to-have | `true` |
| `appium:shouldTerminateApp` | Ensures the AUT is actually terminated when told to (session end / explicit `terminateApp()`), not just backgrounded | `true` |
| `appium:newCommandTimeout` | How long Appium waits for the next WebdriverIO command before killing the session — keep generous for interactive debugging, tight for CI | `240` (seconds) |
| `appium:noReset` | Skip full app data reset between sessions (fast) — pair with `forceAppLaunch`/`shouldTerminateApp` for state hygiene instead of `fullReset` | `true` |
| `appium:fullReset` | Full uninstall/reinstall — reserved for explicit "clean install" scenarios only, never the suite default | `false` |

### Platform selection: a `RUN_ON`-switched single capability, not two simultaneously-active ones

**Live-run correction:** an earlier version of this guidance recommended keeping both an Android and an iOS capability object simultaneously active in the `capabilities` array when platform = "both" (WebdriverIO would fan out one worker per entry, running the same specs against both in parallel). This was never actually validated end-to-end and conflicts with the pattern this repo's sibling Appium-Java framework already uses successfully (a single active platform selected via a build-time switch — Maven profiles there, an env var here). The validated approach: **one active capabilities entry, chosen at invocation time by a `RUN_ON` environment variable**, mirroring the Java stack's `RunOn.value` Maven-profile substitution:

```javascript
capabilities: [
    process.env.RUN_ON === 'iOS_APP'
        ? {
            platformName: 'iOS',
            'appium:deviceName': process.env.IOS_DEVICE_NAME || 'iPhone 15',
            'appium:platformVersion': process.env.IOS_PLATFORM_VERSION || '17.2',
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
```

- `RUN_ON` defaults to Android when unset (the ternary's `else` branch) — set `RUN_ON=iOS_APP` (as a shell-level env var, which `dotenv.config()` will not override — dotenv never overwrites an already-set variable) to switch to iOS.
- Expose this as dedicated npm scripts rather than asking users to export the var by hand: `"wdio:ios": "RUN_ON=iOS_APP wdio run ./wdio.conf.js"`, `"wdio:ios:smoke": "RUN_ON=iOS_APP wdio run ./wdio.conf.js --cucumberOpts.tags=@smoke"`, etc., alongside the plain (Android-default) `wdio`/`wdio:smoke`/`wdio:regression` scripts.
- Platform = "both" means: generate/verify both branches above, ready to run either way — not "run both at once." A single Cucumber run still targets one platform at a time, exactly like the sibling Java framework's `mvn test -P android` / `mvn test -P ios`.
- iOS additionally needs `appium:udid` when more than one simulator of the same device model/OS version exists on the host — Android's `appium:deviceName` (an ADB device id, e.g. `emulator-5554`) is already unambiguous without an equivalent field.

### Connecting to Appium: external server vs. `@wdio/appium-service`

The template defaults to `services: ['appium']`, which is the right default for most environments — `@wdio/appium-service` starts/stops Appium automatically around the WDIO run. **Live-run finding:** in an environment where a prior `sudo npm install` has left root-owned files under the default `~/.appium`/`~/.npm`, a service-managed Appium process spawned under that same default `APPIUM_HOME` will hit `EACCES` trying to verify/reinstall drivers. Do not "fix" this with `sudo` inside a generated project. Instead, connect to an **externally-managed** Appium server started with an isolated `APPIUM_HOME`:

```javascript
// hostname/port/path connect to an already-running Appium server instead of
// having @wdio/appium-service spawn its own — use when the environment needs
// an isolated APPIUM_HOME (see README "Prerequisites"/"Troubleshooting").
hostname: process.env.APPIUM_HOST || '127.0.0.1',
port: Number(process.env.APPIUM_PORT) || 4723,
path: '/',
services: [], // no 'appium' entry when connecting externally
```

Keep `@wdio/appium-service` in `package.json`'s `devDependencies` regardless of which connection mode is active, so switching back to service-managed later is a config change, not a reinstall.

---

## Gestures (`src/helper/gestures.js`)

Swipe, scroll, drag, pinch, and long-press are implemented via the **W3C Actions API** (`driver.performActions([...])`), never via the deprecated JSONWP `TouchAction` class (removed from modern Appium clients). Follow the reference project's pattern:

- Percentage-based swipe helpers (`swipeUp(percentage)`, `swipeDown`, `swipeLeft`, `swipeRight`) that resolve against `driver.getWindowRect()` so gestures scale correctly across device screen sizes — never hardcode pixel coordinates for a swipe.
- `dragElementTo(elementOne, elementTwo)` — drag via `pointerMove` with `origin: element.elementId` so Appium resolves the element's on-screen position, instead of manually computing coordinates.
- `longPress(element, duration)`, `pinchZoomIn(element)`, `pinchZoomOut(element)` — same `performActions` primitive, different action sequences.
- `Page.checkIfDisplayedWithSwipeUp(element, maxScrolls)` — a recursive "scroll until visible, else fail after N attempts" helper for long lists, called from a Page Object's action methods rather than duplicated per module.

---

## Helper Utilities (`src/helper/Utils.js`)

- Hybrid/WebView context switching (`switchToContext`, `getCurrentContexts`, `waitForWebViewContextLoaded`) — only needed and only scaffolded when the app context is `HYBRID`. Never mix WebView context-switching helpers into a `NATIVE`-only project.
- Device-level toggles (`turnOnWifi`/`turnOffWifi`) and similar `driver.*` device-control passthroughs.
- Keep this file to genuinely cross-module device/session utilities — module-specific logic belongs in that module's Page Object.

---

## Test Data (`data/testdata.json`)

- **`src/helper/TestData.js` is the single load point** — `import testData from '../../data/testdata.json' with { type: 'json' }; export default testData;` — every step-definition file that needs test data imports from `TestData.js`, never re-derives the JSON path/import-assertion syntax itself. This is the JS-stack equivalent of the sibling Java framework's `PropertyReader.getInstance()` for `config.properties`. (Use a `createRequire`-based JSON import instead if the pinned Node version doesn't support import assertions — verify the correct syntax for the Node version in use at generation time, since the ESM JSON-import spec has changed across Node LTS releases.)
- Grouped into semantic top-level keys mirroring the reference project (`login_credentials`, etc.) — one key namespace per module's data needs, not a single flat object of unrelated values.
- Never hardcode credentials directly in a step definition or Page Object — always resolve through `TestData.js` (or `.env` for anything environment-specific like device names/app paths).
- **Every value declared in `testdata.json` must have exactly one step-definition consumer — not a parallel hardcoded copy in the `.feature` file.** Live-run correction: a generated `testdata.json` was found with real, well-structured content that had zero consumers — every value it declared was independently hardcoded a second time directly in the `.feature` files, meaning an edit to the JSON file would have silently changed nothing. When a value is reused across scenarios or represents semantic business data (a credential, a canonical sample input), give it a named step (`I enter the valid password`, `I type the sample text into the text input`) whose step-definition body resolves the value through `TestData.js` — don't leave the JSON file's content duplicated as a literal in Gherkin text.
- This does **not** mean every literal in a `.feature` file must be routed through `testdata.json` — an arbitrary one-off input for a boundary/edge-case scenario (e.g. testing that a specific malformed string doesn't crash the app) is normal, idiomatic Gherkin and does not need centralizing. Centralize a value when and only when `testdata.json` already declares it, or when the same literal is duplicated across more than one scenario.
- Cucumber `Scenario Outline` `Examples` tables are inherently literal-value tables — a `testdata.json` array that happens to enumerate the same values (e.g. a list of permission names) is fine as documentation, but is not a bug if no `Examples` table "reads" from it; there is no standard Cucumber mechanism to populate an `Examples` table from external JSON at scenario-definition time, so don't force one.

---

## Hooks (`src/support/hooks.js`)

- `Before`/`BeforeAll` — session-level or scenario-level setup that isn't already handled by capability hygiene (see "Session & App Lifecycle").
- `After` — capture a failure screenshot, keyed off `scenario.pickle.name`/`scenario.result.status`. Call **`browser.saveScreenshot(path)` only** — it both captures and writes the file in one call. An earlier version of this pattern also called `browser.takeScreenshot()` first and discarded its result; that's a real, confirmed inefficiency (two full screenshot round-trips to the device for every failure instead of one) — do not resurrect it.
  ```javascript
  After(async function (scenario) {
      if (scenario.result.status === 'FAILED') {
          try {
              await browser.saveScreenshot(`./allure-results/failure-${Date.now()}.png`);
          } catch (e) {
              console.error('Could not capture failure screenshot:', e.message);
          }
      }
  });
  ```
- **Live-run correction — `AfterAll` should NOT call `mobile: terminateApp`.** See "Session & App Lifecycle" above for the confirmed failure mode (an intermittently-hanging force-stop call aborting the entire worker under host load). Keep `AfterAll` minimal — a log line is enough; WebdriverIO's automatic `deleteSession()` (run after every spec file) already tears down the Appium session correctly.
- Import `After`/`AfterAll`/`Before`/`BeforeAll` from `@cucumber/cucumber` (the hooks file, unlike step-definition files, uses the base Cucumber package directly, matching the reference project); import `browser` from `@wdio/globals`.

---

## Tagging & Suite Convention

| Tag | Meaning | Applied via |
|---|---|---|
| `@smoke` | Critical-path smoke checks | `@smoke` above the `Scenario`/`Scenario Outline` line in the `.feature` file |
| `@regression` | Full regression coverage | `@regression` above the `Scenario`/`Scenario Outline` line |

Run subsets via `cucumberOpts.tags` passed as a CLI override or a dedicated npm script — do not maintain separate spec-pattern globs per tag; Cucumber tag expressions are the standard mechanism in this stack, exactly as `@cypress/grep` fills the equivalent role for the Cypress stack.

**Platform (via `RUN_ON`, see the capability-conventions section above) and suite subset (via tags) are two independent, orthogonal npm-script axes** — the validated convention pairs them as:

| Script | Platform | Subset |
|---|---|---|
| `wdio` | Android (default) | all |
| `wdio:smoke` | Android | `@smoke` |
| `wdio:regression` | Android | `@regression` |
| `wdio:ios` | iOS | all |
| `wdio:ios:smoke` | iOS | `@smoke` |
| `wdio:ios:regression` | iOS | `@regression` |

e.g. `"wdio:ios:smoke": "RUN_ON=iOS_APP wdio run ./wdio.conf.js --cucumberOpts.tags=@smoke"`.

---

## Latest-Dependency-Version Policy

Every scaffold (Mode A) pins dependency versions to **the actual latest release at generation time**, verified by querying the registry — `npm view <package> version` — for every package before writing `package.json`, not assumed from memory, copied from the SwagLabs reference project's `package.json`, or inferred from training data. Reference-project versions and training-data versions both go stale; this is the same discipline the Appium-Java stack's `02-templates/AppiumFramework.md`/`03-standards/appium-java-standards.md` apply to pinning `selenium-java` and `java-client` via Maven's `dependencyManagement` rather than guessing a version number.

If a just-verified latest version of one package introduces a real breaking change against another pinned dependency in the same scaffold (the WebdriverIO/Appium equivalent of the Java stack's Selenium open-range issue — e.g., a `@wdio/*` major bump that drops support for the Appium major version pinned elsewhere in the same `package.json`), resolve it with an explicit, compatible version pin — never silently downgrade the package that prompted the check without documenting why. Document the resolution in the generated `AutomationSummary.md`, the same way the Appium-Java stack's `AutomationSummary.md` documents its "Library Upgrade" section.

Never hand-write a version number into `package.json` without having run `npm view` for it in the same generation pass — including for a Mode B "add a dependency to an existing project" change.

---

## Naming Conventions

| Artifact | Convention |
|---|---|
| Page Object file | `<module>.page.js` (e.g., `login.page.js`) |
| Page Object class | `<Module>Page` (e.g., `LoginPage`) |
| Step definition file | `<module>.steps.js` (e.g., `login.steps.js`) |
| Feature file | `<module>.feature` (e.g., `login.feature`) |
| Selector getter | camelCase, prefixed by element type (`inputUsername`, `btnSubmit`, `flashAlert`) |
| Data key namespace | `<module>_<concern>` (e.g., `login_credentials`) in `data/testdata.json` |
| Tag | `@smoke`, `@regression` |

---

## Reporting

- `@wdio/allure-reporter` — `allure.addStep(...)` for readable step logs, `allure.addAttachment('Screenshot on Failure', ..., 'image/png')` on failure (see reference `steps.js` pattern).
- Results: `allure-results/` at the project root; generate/view via `allure generate allure-results --clean -o allure-report && allure open allure-report`, or `allure serve allure-results` for an ephemeral view.
- `disableWebdriverStepsReporting: true` in the reporter config keeps the report focused on Cucumber steps and `allure.addStep` calls rather than every low-level WebdriverIO command.

---

## CI — Jenkins

**Live-run correction:** no `Jenkinsfile` was documented anywhere in this stack's standards/template, and none was ever generated for the real WebdriverIO framework — a gap only discovered when the user checked the generated framework's root directory directly, the same class of drift found and fixed for the Appium-Java sibling's Reporting setup. A `Jenkinsfile` at the framework's own root is now **mandatory scaffold, not optional**.

- Parameterized `Jenkinsfile` with `PLATFORM` (android/ios) and `SUITE` (smoke/regression) parameters, mapping to the validated npm script set (`wdio:smoke`/`wdio:regression` for Android, `wdio:ios:smoke`/`wdio:ios:regression` for iOS).
- `dir('<output_path>')`-scoped stages (`Checkout` → `Install Dependencies` → `Test` → `Generate Allure Report`) since this is a monorepo — the Jenkins job checks out `cc-qa-ai-framework` as a whole, not just this subfolder. Mirrors the reference `Automation-java` project's proven `Jenkinsfile` pattern (see `03-standards/appium-java-standards.md`'s "CI — Jenkins" section for the sibling stack's identical rationale).
- Post-build: archive `allure-results/**`, `allure-report/**`, and `*.log` — no Jenkins Allure plugin install is assumed; `allure-report/` is a plain static HTML directory from the `allure-commandline` npm dependency, viewable as an archived artifact like any other file.
- See `02-templates/WebdriverIOFramework.md`'s "Jenkinsfile" section for the exact, copy-ready content.

---

## Prohibited Patterns

- Never use CommonJS (`require`/`module.exports`) in a new file — this stack is ES Modules only.
- Never introduce a separate `<Module>PageObjectRepository`-style locator class — selectors and actions live together on one Page Object class, per "Page Object Model" above.
- Never call `$(...)`/`$$(...)` inline inside a step definition or action method body — declare it as a getter on the Page Object first.
- Never use the deprecated `TouchAction` class for gestures — use the W3C Actions API (`driver.performActions([...])`) via `src/helper/gestures.js`.
- Never use `browser.pause(<literal ms>)` as a wait strategy — use `waitUntil`/`waitForDisplayed`/`waitForExist` or WebdriverIO's built-in assertion retry-ability.
- **Never call `element.waitForClickable()` on a native mobile element.** Confirmed live: it throws `"only available for desktop and mobile browsers"` — this command is browser-context-only, not valid for native app automation on either platform, regardless of what error message implies. Use `waitForDisplayed()` before `.click()` instead, matching every other native interaction in this stack. This bug went undetected for an entire generation cycle because it was only exercised by an `@regression`-tagged scenario while every prior run was `@smoke`-only — always run the full suite (including `@regression`) at least once before declaring a generated framework complete, not just `@smoke`.
- Never hardcode device names, app paths, package names, bundle IDs, or credentials directly in `wdio.conf.js`, a Page Object, or a step definition — source them from `.env` (via `dotenv`) or `data/testdata.json`.
- Never emit a placeholder selector when a real device/emulator session was available to verify it — see "Live UI Hierarchy Verification" above. A placeholder is only acceptable when a session genuinely could not be established, and that exception must be explained in the generation summary.
- Never introduce Selenium/Playwright/Cypress constructs (`WebDriver`, `page.locator()`, `cy.get()`) into this stack's files.
- Never mix WebView/hybrid context-switching helpers into a project scoped as `NATIVE`-only.
- Never hand-write a dependency version into `package.json` without verifying it via `npm view` in the same generation pass — see "Latest-Dependency-Version Policy" above.
- **Never declare a zero-arity override of a base-class method that takes a parameter (e.g. `isSwitchOn()` with no args in a module Page Object, shadowing the base `Page.isSwitchOn(element)`).** JavaScript has no method overloading by arity — the subclass method fully replaces the base one regardless of parameter count, so this silently breaks the base implementation for that one class instead of producing a compile error. Confirmed live: a module's own zero-arg `isSwitchOn()` shadowed the base class's platform-aware `isSwitchOn(element)`, and the fix was `return super.isSwitchOn(this.switchControl);`, not a rename — call `super.<method>()` explicitly when a subclass genuinely needs to specialize a base method with a different signature.
- Never route an interaction through `clickWithHeal`/`typeWithHeal` for an assertion's read target, an ad-hoc dynamically-resolved element, or an optional/conditional dialog control — see "Self-Healing Locators" above for the exact scope.
