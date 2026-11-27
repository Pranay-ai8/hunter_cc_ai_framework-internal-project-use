# WebdriverIO Automation Generation Complete — WebdriverIO Native Demo App

**Mode:** A — New Framework **Platform:** Android (emulator: Pixel_6_API_33) — full suite (`@smoke`+`@regression`) verified green. **iOS** (simulator: iPhone 15, iOS 17.2) — supported and verified green at `@smoke` only; full iOS regression not yet run (see Known Limitations). **Context:** NATIVE **Output Location:** `07-outputs/Automation-WebdriverIO/Framework/` **Source test cases:** `07-outputs/TestCases/WdioNativeDemoApp-FunctionalUITestCases.md` **Sibling framework:** `07-outputs/Automation-Appium/Framework/` — an Appium + Java + TestNG + Maven stack for the same app, generated and verified independently (see its own `AutomationSummary.md`). The two frameworks share no files, config, or dependencies.

---

## Stack (actual latest versions, verified via `npm view` at generation time)

| Property | Value |
|---|---|
| Language | JavaScript (ES Modules) |
| Test Runner | WebdriverIO — `@wdio/cli` 9.31.1, `@wdio/local-runner` 9.31.1 |
| BDD Layer | `@wdio/cucumber-framework` 9.31.1, `@cucumber/cucumber` 10.9.0 |
| Mobile Automation | Appium 3.6.0 (server; connected externally, not service-managed) |
| Platform Drivers | UiAutomator2 + XCUITest, 8.4.0 (installed via `appium driver install`, not npm dependencies) |
| Globals/Assertions | `@wdio/globals` 9.31.1 |
| Reporting | `@wdio/allure-reporter` 9.31.1, `@wdio/spec-reporter` 9.31.1, `allure-commandline` 2.43.0 |
| Env Config | `dotenv` 17.4.2 |

---

## Generated Files

| Module | Page Object | Step Definitions | Feature File |
|--------|-------------|-------------------|---------------|
| Home | ✅ | ✅ | ✅ |
| Login / Sign Up | ✅ | ✅ | ✅ |
| Navigation | ✅ | ✅ | ✅ |
| Forms | ✅ | ✅ | ✅ |
| Swipe | ✅ | ✅ | ✅ |
| Drag | ✅ | ✅ | ✅ |
| Permissions | ✅ | ✅ | ✅ |
| WebView | ✅ | ✅ | ✅ |

Plus: `src/pageobjects/page.js` (base class), `src/support/hooks.js`, `src/helper/Utils.js`, `src/helper/gestures.js`, `src/helper/TestData.js`, `data/testdata.json`, `wdio.conf.js`, `package.json`, `.env`.

### Suites

- 27 scenario blocks across 8 feature files (one Scenario Outline in `permissions.feature` expands to 3 executions) — every scenario tagged `@smoke` or `@regression`.

### Run Commands

```bash
# Android (default)
npm run wdio:smoke
npm run wdio:regression

# iOS (RUN_ON=iOS_APP set automatically by these scripts)
npm run wdio:ios:smoke
npm run wdio:ios:regression
```

---

## Directory Restructuring (post-scaffold correction)

The initial scaffold followed the SwagLabs reference project's layout literally — `pageobjects`/`step-definitions`/`support`/`helper` nested inside `features/`. Per explicit direction, this was corrected to a top-level `src/` holding those four directories, with `features/` reduced to holding only `.feature` files. All internal relative imports between the four moved directories were unaffected (same relative depth to each other and to `data/`), and `wdio.conf.js`'s `cucumberOpts.require` glob was updated to `./src/step-definitions/*.steps.js` / `./src/support/hooks.js`. This is now the standing convention for regenerating this stack — see `03-standards/webdriverio-javascript-standards.md`.

The project's own physical location was later separated from a shared `Automation-Mobile/` parent (which held both stacks as sibling folders) into a fully independent top-level `Automation-WebdriverIO/` directory, with the Java stack similarly separated into `Automation-Appium/`.

---

## Live UI Hierarchy Verification

Selectors were confirmed against real Appium sessions (Android emulator, then cross-checked on the iOS simulator) rather than transcribed from documentation — the sibling Java framework's already-verified accessibility ids/resource-ids were used as a starting reference, then re-confirmed live for this stack (identifiers proved to map 1:1 across the two client libraries, as expected — same app, same build). No `docs/dom-snapshots/` artifacts were persisted to disk in this pass, unlike the standard this repo's templates call for; selectors themselves are correct and live-verified, but the traceability snapshot is a known gap — see the README's Known Limitations.

---

## Final Test Results

- **Android: 8/8 feature files passing — full suite, all `@smoke`+`@regression` scenarios run together in one execution** (`npm run wdio`, no tag filter).
- **iOS: 8/8 feature files passing at `@smoke` only** (`npm run wdio:ios:smoke`). A full iOS regression run has not been performed — see Known Limitations for why it would currently fail on `navigation.feature`.
- **Self-healing: live-verified** — see "Self-Healing Locators" below.

Reached after four separate rounds of live debugging: one for the initial Android generation (session-state leakage, plus the `mobile: terminateApp` hang), one for adding iOS support (5 selector/attribute bugs, one environment false-alarm), a third triggered by wiring up test data that incidentally ran `@regression` scenarios for the first time and surfaced one more real bug (`waitForClickable` on native elements), and a fourth while adding and verifying self-healing, which — for the first time — ran **all 8 feature files together in one single execution** and surfaced one more real bug (intra-session side-menu leak, finding #6 below) that per-file/partial runs had never combined in a way that could expose it.

---

## Real Bugs Found and Fixed — Android

1. **Missing `appium:forceAppLaunch`/`appium:shouldTerminateApp` caused cross-spec-file state leakage.** Each `.feature` file gets its own Appium session, but without these two capabilities the underlying app process is never relaunched — a "new" session just re-attaches to whatever the previous spec file left running. Confirmed via failure screenshots: `home.feature`'s failure showed the **Forms screen** (left over from `forms.feature`'s last action) instead of Home; `permissions.feature`/`swipe.feature`/`webview.feature` all failed identically on `element ("~Menu") still not existing` because `navigation.feature`'s last scenario had opened the side-menu drawer and never closed it — the bottom tab bar genuinely isn't rendered while that drawer overlays it, so the shared "app is launched" precondition could never succeed at any timeout. Fixed by adding both capabilities; full run time also dropped from ~7m28s to ~1m15s as a side effect (sessions no longer piling up retries against a stuck app).
2. **`mobile: terminateApp` in `AfterAll` intermittently hung 20+ seconds** on this resource-constrained host — Cucumber's own hook-level timeout aborted the entire worker process regardless of any try/catch wrapped around the call. Removed; WebdriverIO's automatic `deleteSession()` (run after every spec file) already closes the session correctly.
3. **Redundant double screenshot capture in the `After` hook** — an earlier version called both `browser.takeScreenshot()` (result discarded) and `browser.saveScreenshot()`, doubling the device round-trip cost on every failure for no benefit. Reduced to a single `saveScreenshot()` call.
4. **`waitForClickable()` called on a native Android element** inside `FormsPage.selectDropdownOption()` — this WebdriverIO command is browser-context-only and throws `"only available for desktop and mobile browsers"` on a native app element, regardless of platform. Never caught during initial generation because `"Dropdown opens and allows a selection"` is `@regression`-tagged and only `@smoke` had ever been executed. The exception left the dropdown's option dialog open on screen, cascading into 2 further scenario failures within the same session (`"Active and Inactive buttons..."` and `"Special character input..."`, both failing on the same `~Menu`-not-found symptom as finding #1, this time caused by the dropdown overlay rather than the side menu). Fixed by using `waitForDisplayed()`, matching every other native interaction already in this project.
5. **`data/testdata.json` existed with real content but had zero consumers.** Every value it declared (`login.validPassword`, `login.invalidEmailFormat`, `forms.sampleText`, `forms.specialCharacters`, `forms.dropdownOption`) was independently hardcoded a second time directly in `login.feature`/`forms.feature` — editing the JSON file would have silently changed nothing. Fixed by adding `src/helper/TestData.js` as a single load point (mirroring the sibling Java framework's `PropertyReader.getInstance()` pattern for `config.properties`) and new named steps (`I enter the valid password`, `I enter an invalid-format email`, `I type the sample text into the text input`, `I type the special-character sample into the text input`, `I select the sample dropdown option`) that resolve through it, replacing the literal duplicates in both feature files.
6. **Intra-session side-menu leak — distinct from finding #1, same symptom.** The first time all 8 feature files ran together in one execution (`npm run wdio`, prompted by wiring up self-healing and wanting a genuine full-suite confirmation), `navigation.feature` failed 3 of its 4 scenarios on the identical `element ("~Menu") still not existing` symptom as finding #1 — but from a different root cause. `forceAppLaunch`/`shouldTerminateApp` relaunch the app once **per session** (once per spec file), not once per Scenario; `navigation.feature`'s own `@smoke` scenario ("Side menu opens from the Menu tab") opens the drawer and never closes it, so every subsequent Scenario **in that same file/session** inherited the still-open drawer, which hides the bottom tab bar the shared "app is launched" precondition checks for. Confirmed live (standalone script against a real session) that Android's hardware back button closes it: `browser.execute('mobile: pressKey', { keycode: 4 })` — `~Menu` exists again immediately after. Fixed via a new `NavigationPage.closeSideMenuIfOpen()`, called at the top of `common.steps.js`'s "Given the app is launched" step. **This fix is Android-only** — iOS has no hardware back button, and the correct close gesture for that platform (e.g. tapping the dimmed scrim outside the drawer) has not been live-verified.

---

## Self-Healing Locators

Ported from the sibling Appium-Java framework's `Utils/SelfHealer.java` (see `03-standards/appium-java-standards.md`'s "Self-Healing Locators" section) — identical algorithm, adapted to WebdriverIO's `browser.getPageSource()`/`browser.$()` API instead of Appium-Java's `WebElement`/`By`.

- **`src/helper/SelfHealer.js`** — snapshots the live UI hierarchy, scores every candidate element by precision/recall (F1) against a human-readable hint's keywords (accessibility id / resource-id / text, excluding tag/class as structural noise), and resolves the best match (score ≥ 0.75) to a real selector: accessibility id → resource-id (`id=` shorthand if namespaced, else xpath) → text xpath.
- **`Page.clickWithHeal(element, hint)` / `Page.typeWithHeal(element, hint, text)`** — added to the base `Page` class; every module's interactive methods on stable, permanent UI elements route through these (login buttons, tab navigation, form controls, dialog OK button, permission switches, drag/retry buttons) — matching the Java sibling's exact scope: ad-hoc dynamically-resolved elements and optional dialog-dismiss buttons stay as plain `.click()`, since there's no stable selector for a heal hint to describe.
- Cache (`healing-cache.json`, 1h TTL) and log (`healing-report.log`) persist one level above `Framework/`, directly under `Automation-WebdriverIO/` — matching the Java sibling's convention now that both frameworks share the same `Automation-X/Framework/` shape (this only became possible after the most recent directory flattening).
- `safeScreenKey()` (cache-partition key) uses `browser.execute('mobile: getCurrentActivity', {})`, confirmed live to work on Android (returned `.MainActivity`); on iOS it falls through to `""` (no activity concept) as designed — confirmed live (cache key recorded as `"|Login Button"`, empty prefix), same graceful-degradation behavior as the Java sibling's own `safeScreenKey()`.
- **Found and fixed one real, pre-existing bug while wiring this in:** `FormsPage.isSwitchOn()` (zero-arity) was silently shadowing the base `Page.isSwitchOn(element)` method — JS has no method overloading, so the subclass's own zero-arg version completely replaced the platform-aware base implementation, meaning Forms' switch-state check always used Android's `checked` attribute even when running on iOS. Never caught before because the consuming scenario (`"Switch toggles state on tap"`) is `@regression`-tagged and iOS had only ever been run at `@smoke`. Fixed by delegating via `super.isSwitchOn(this.switchControl)`.

### Live Verification (not just generated — actually broken and healed, on both platforms)

**Android:**

1. Intentionally broke `LoginPage.loginButton`'s selector (`~button-LOGIN` → `~button-LOGIN-INTENTIONALLY-BROKEN-FOR-SELFHEAL-TEST`).
2. Ran `login.feature`'s `@smoke` scenario live: `tapLoginButton()` failed its normal wait, `SelfHealer` scored all live candidates, found the real `button-LOGIN` element via hint `"Login Button"` at confidence 1.00, clicked it, and the scenario **passed**. Console: `Self-healed [Login Button] with confidence 1`.
3. Confirmed `healing-cache.json`/`healing-report.log` were created with the correct recovered value and the original error message intact (cache key `.MainActivity|Login Button` — confirms `mobile: getCurrentActivity` works as the screen-key partition on Android).
4. Re-ran the identical (still-broken) test a second time: `SelfHealer: cache hit for hint [Login Button] (confidence 1)` — cache-hit path confirmed working, scenario passed again.
5. Reverted the intentional break, cleaned up the generated cache/log files, and re-ran the full Android suite (8/8) to confirm the happy path is unaffected and healing stays silent when nothing is actually broken.

**iOS:** identical procedure repeated end-to-end against the iPhone 15/iOS 17.2 simulator, confirming the algorithm and wiring are genuinely cross-platform, not Android-specific:

1. Same intentional break on `LoginPage.loginButton` (shared by both platforms — one Page Object, one selector).
2. Ran `login.feature`'s `@smoke` scenario with `RUN_ON=iOS_APP`: healed at confidence 1.00, recovered `button-LOGIN`, scenario **passed** (9/9 steps). Console: `Self-healed [Login Button] with confidence 1`.
3. Confirmed `healing-cache.json`/`healing-report.log` — cache key `|Login Button` (empty screen-key prefix, since `mobile: getCurrentActivity` correctly has no iOS equivalent and `safeScreenKey()` degrades to `""` as designed, not an error).
4. Re-ran the identical (still-broken) test: `SelfHealer: cache hit for hint [Login Button] (confidence 1)` — cache-hit path confirmed on iOS too.
5. Reverted the break, cleaned up cache/log, and re-ran the full iOS `@smoke` suite (8/8) to confirm the happy path is unaffected.

One unrelated environment flake surfaced and was ruled out along the way: the very first iOS session after a fresh simulator boot failed with `Unable to start WebDriverAgent session... ECONNREFUSED 127.0.0.1:8100` — a known WDA cold-start race, not a code or self-healing defect. A clean re-run immediately after (WDA now warm) passed 8/8 with no further occurrences.

---

## Real Bugs Found and Fixed — iOS

Adding iOS support surfaced 5 more real bugs, none hypothetical — all confirmed via live Appium sessions against the iPhone 15/iOS 17.2 simulator:

1. **Login success-alert selectors were Android-only.** `id=com.wdiodemoapp:id/alert_title` etc. don't exist on iOS — there is no resource-id concept at all. Fixed with iOS-specific accessibility-id selectors keyed to the alert's own rendered text: `~Success`, `~You are logged in!`, `~OK`.
2. **Permission switch state read the wrong attribute on iOS.** Android exposes on/off via `checked` (`"true"`/`"false"`); XCUITest has no such attribute — iOS exposes it via `value` (`"1"`/`"0"`). Confirmed live: `getAttribute('checked')` on iOS threw `WebDriverError: The attribute 'checked' is unknown`. Fixed via a shared `isSwitchOn(element)` helper on the base `Page` class that branches by platform.
3. **Swipe carousel locator used Android's xpath-by-`resource-id` workaround, which iOS doesn't need or support.** This carousel's resource-ids have no colon (auto-prefixing quirk on Android only), requiring an xpath workaround there; iOS exposes the same identifiers directly as accessibility ids. Fixed with iOS-specific `~Carousel`/`~__CAROUSEL_ITEM_0__`/`~__CAROUSEL_ITEM_1__` selectors.
4. **WebView detection used a hardcoded Android class name.** `$('android.webkit.WebView')` never matches on iOS. Fixed by branching to `$('XCUIElementTypeWebView')` on iOS — both are bare "class name" strategy strings WebdriverIO auto-detects.
5. **Drag/drop piece-counting xpath used Android-only `@content-desc`.** iOS has no such attribute; the equivalent is `@name`. Fixed by extending the xpath to `starts-with(@content-desc,"drag-") or starts-with(@name,"drag-")` (and the `"drop-"` equivalent).

**Environment false-alarm (not a code bug):** a permissions test expected "ungranted by default" on iOS but observed "granted" — root-caused to simulator residue, not a defect. This exact simulator had already had Camera granted during earlier manual/automated testing in the same environment, and iOS TCC permission decisions persist across `noReset: true` sessions until the app is uninstalled (unlike Android, which resets via `autoGrantPermissions` every session). Confirmed by running `xcrun simctl uninstall <udid> org.wdiodemoapp` and re-testing clean — passed immediately (`value: "0"`, correctly ungranted).

Also made platform-aware rather than forced to a single expectation: the Permissions `"granted by default"`/`"remain granted"` assertions now branch on `PermissionsPage.isAndroid()` for the default-state checks (Android pre-grants via `autoGrantPermissions`; iOS starts ungranted — a genuine OS security-model difference, not a bug to paper over). The Android-only `"Toggling an already-granted permission is blocked in-app"` regression scenario was deliberately **left** Android-only rather than force-fit to iOS's different first-toggle-grants behavior — a blanket platform-aware assertion there would have been actively wrong, not just incomplete.

---

## Known Limitations

- **`NavigationPage.closeSideMenuIfOpen()` (finding #6) is Android-only.** No cross-platform close gesture is implemented for iOS yet.
- **A full iOS regression run has not been performed.** iOS is verified at `@smoke` only; running `npm run wdio:ios` (full suite) today would very likely reproduce finding #6 on `navigation.feature`, since the fix doesn't cover iOS.
- **`appium-uiautomator2-driver`/`appium-xcuitest-driver` are not `package.json` dependencies** — they're Appium server-side driver installs (`appium driver install <name>`), not consumed by any generated code; `npm install` alone does not configure them.
- **No `docs/dom-snapshots/` artifacts persisted** — selectors were live-verified during generation, but the snapshot files themselves weren't saved to disk.
- **Data Management and biometric login are not automated** — no Page Object exists yet for either module.

---

## Scope Note

This pass covered the same 8 modules as the sibling Java framework (Home, Login/Sign Up, Navigation, Forms, Swipe, Drag, Permissions, WebView) for coverage parity, prioritizing a genuinely green full suite over exact 1:1 scenario-count matching with the Java stack. Self-healing locators were added in a later pass (see "Self-Healing Locators" above), ported from the Java sibling's `SelfHealer.java`. Not built: Data Management module, biometric login — see Known Limitations above.

### Next Steps

1. Populate `docs/dom-snapshots/` retroactively if strict selector-traceability is required.
2. Implement an iOS close mechanism for `NavigationPage.closeSideMenuIfOpen()` (e.g. tap the dimmed scrim outside the drawer — needs live verification), then run a full `npm run wdio:ios` (not just `:smoke`) to confirm iOS parity with Android's fully-green full suite.
3. Re-run the **complete** suite (`npm run wdio` / `npm run wdio:ios`, no tag filter, all 8 feature files in one execution) after any future change — both this pass's `waitForClickable` bug and finding #6 (intra-session side-menu leak) went undetected for as long as they did specifically because no earlier run had combined `@smoke`+`@regression` across every feature file in a single execution.
