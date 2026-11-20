# Appium Automation Generation Complete — WebdriverIO Native Demo App

**Mode:** A — New Framework **Platform:** Android (emulator: Pixel_6_API_33, API 33) **and iOS** (simulator: iPhone 15, iOS 17.2) — both fully supported and verified green **Context:** NATIVE **Output Location:** `07-outputs/Automation-Appium/framework/` **Source test cases:** `07-outputs/TestCases/WdioNativeDemoApp-FunctionalUITestCases.md` **Sibling framework:** `07-outputs/Automation-WebdriverIO/Framework/` — a WebdriverIO + JavaScript + Cucumber stack for the same app, generated and verified independently (see its own `AutomationSummary.md`). The two frameworks share no files, config, or dependencies.

---

## Stack (latest verified-compatible library versions; deviates from the framework's default template — see Constraints below)

| Property | Value |
|---|---|
| Language | Java 17 |
| Build | Maven (`apache-maven-3.9.9` / Homebrew `mvn`, either works) |
| Test Framework | TestNG **7.11.0** (latest) |
| Appium Client | `io.appium:java-client` **9.4.0** (latest — see "Library Upgrade" below for why this needed a real fix, not just a version bump) |
| Selenium | **4.26.0**, explicitly pinned via `dependencyManagement` (see "Library Upgrade") |
| Log4j2 | **2.26.0** (latest stable) |
| Commons Lang3 | **3.20.0** (latest) |
| Jackson Databind | **2.19.0** (latest) |
| Automation Driver | UiAutomator2 (Android) **+ XCUITest (iOS)** |
| Reporting | Log4j2 console + file (`logs/automation.log`), screenshots on failure (`Screenshots/`) |

**Deliberate simplifications vs. the reference template** (`Mobile_App_Automation_Template`): dropped BrowserStack/Flutter support paths, Allure reporting, and the TestNG priority/retry listeners. iOS was added properly (not simplified away) once a simulator build was supplied. Package convention (`utils/DriverFactory`, `Base`, `Object_Base`, `InitiateDriver`, `Pages` registry, Page Action + Page Object Repository per module, `TestBase`, `CC_TestNG.xml`/`CC_Sample_TestNG.xml`, Maven `android`/`ios` profiles) was kept faithful to the template.

---

## Library Upgrade — the real fix, not just a version bump

Bumping `java-client` straight to the latest (9.4.0) reproduces the exact compile failure from the original build (`org.openqa.selenium.ContextAware`/`html5.LocationContext` missing) — because **java-client 9.4.0's own POM declares Selenium as the open range `[4.26.0, 5.0)`**, not a fixed version. Left unpinned, Maven resolves that range to whatever Selenium is newest at build time (4.47.0, which removed those interfaces) — a latent fragility in java-client's own dependency declaration, not a mistake in this project. **Fix:** added a `<dependencyManagement>` block pinning `selenium-api`/`-remote-driver`/`-support`/`-http`/`-json`/`-manager`/`-os` to **4.26.0** — the floor of java-client 9.4.0's declared range, i.e. the version it was actually built and tested against — so the project stays on the latest java-client without silently inheriting an untested Selenium. `maven-compiler-plugin` (3.15.0) and `maven-surefire-plugin` (3.5.6) were also bumped to latest GA.

**Environment fix required to even test this locally:** this machine's global Appium install (`~/.appium`) and npm cache (`~/.npm`) were partially root-owned from an earlier `sudo npm install`, which blocked reinstalling/upgrading the `xcuitest` driver entirely (`EACCES`, "cache folder contains root-owned files"). Rather than requesting `sudo` access, installed a second, fully user-owned Appium home (`APPIUM_HOME=~/.appium-fresh`) with fresh driver installs — this incidentally also upgraded `uiautomator2` (3.7.4 → **8.4.0**) and `xcuitest` (7.24.4 → **12.5.0**), both flagged as outdated for the installed Appium server (v3.5.2) before this fix.

---

## Generated Files

| Module | Page Class | Repository | Test Class | Test Count |
|---|---|---|---|---|
| Home | ✅ | ✅ | ✅ | 3 |
| Navigation (tab bar + side menu) | ✅ | ✅ | ✅ | 4 |
| WebView | ✅ | ✅ | ✅ | 2 active, 1 disabled |
| Login/Signup | ✅ | ✅ | ✅ | 5 |
| Forms | ✅ | ✅ | ✅ | 6 |
| Swipe | ✅ | ✅ | ✅ | 4 |
| Drag | ✅ | ✅ | ✅ | 3 |
| Permissions | ✅ | ✅ | ✅ | 5 |
| **Total** | | | | **32 active, 1 disabled (Android)** — same 32 all pass on iOS too |

Every Page Object Repository field carries both `@AndroidFindBy` and `@iOSXCUITFindBy` (confirmed identical accessibility identifiers on both platforms for nearly every element — see "Live UI Hierarchy Verification"), except where a platform genuinely needs a different locator (Login's native alert, Forms' iOS-only dropdown chevron — see "Real Bugs Found — iOS" #6).

Utility classes: `DriverFactory`, `Base` (waits, swipe/drag via W3C pointer actions, context switching, `isAndroid()`/`isIOS()`, cross-platform `isSwitchOn()`), `Object_Base` (PageFactory init), `InitiateDriver` (branches to `UiAutomator2Options`/Android or `XCUITestOptions`/iOS based on `RUN_ON`, set by the active Maven profile), `PropertyReader`, `SelfHealer` (cross-platform).

### TestNG Suites
- Regression suite: `CC_TestNG.xml` — 32 test methods across 8 classes
- Smoke suite: `CC_Sample_TestNG.xml` — 9 `groups="smoke"` methods, one per module

### Run Commands
```
cd 07-outputs/Automation-Appium/framework

# Android (default Maven profile)
mvn test -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_Sample_TestNG.xml   # smoke
mvn test -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_TestNG.xml          # full regression

# iOS (-P ios profile — same test classes, only the driver bootstrap differs)
mvn -P ios test -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_Sample_TestNG.xml
mvn -P ios test -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_TestNG.xml
```
Requires: Android emulator/device running (`adb devices`) **or** a booted iOS simulator (`xcrun simctl list devices`), an Appium server on `127.0.0.1:4723` with both `uiautomator2` and `xcuitest` drivers installed, and `config.properties` pointed at the app binaries (`AppPath`/`AndroidAppPackage`/`AndroidAppActivity` for Android; `IOSAppPath`/`BundleId`/`IOSDeviceName`/`IOSPlatformVersion`/`IOSUdid` for iOS).

---

## Live UI Hierarchy Verification

Performed against a real Appium session on **both** the Pixel_6_API_33 emulator and the iPhone 15 (iOS 17.2) simulator before writing any locator (per `03-standards/locator-standards.md`) — the release APK's Hermes bytecode strips source identifiers, so this was not optional. Every locator in every Page Object Repository is a real, confirmed value captured this way, not a placeholder.

- **Modules verified against a real session, both platforms:** all 8 (Home, Navigation, WebView, Login, Forms, Swipe, Drag, Permissions)
- **Cross-platform identifier parity confirmed live:** this app's `testID` props map 1:1 to Android's `content-desc` and iOS's accessibility `name` for every single element checked across all 8 screens — no exceptions found. This is why most `@AndroidFindBy`/`@iOSXCUITFindBy` pairs on the same field carry the identical string.
- **Discrepancies found vs. the source test case document:** see "Real Bugs Found" below — several expected-result assumptions in `WdioNativeDemoApp-FunctionalUITestCases.md` did not match actual app behavior (on one or both platforms) and were corrected in the test code (not silently ignored)
- **Modules left with `// TODO` placeholders:** none

---

## Final Test Results

```
Android:  Tests run: 32, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
iOS:      Tests run: 32, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

Reached green on Android first (3 full runs plus targeted fixes), then again on iOS after adding platform support (5 more live-discovered fixes), then re-verified Android was still green after every iOS-driven code change — see below for what each round found and fixed.

---

## Real Bugs Found and Fixed — Android (this is the actual value of running live, not just generating code)

1. **`io.appium:java-client:9.4.0`'s own POM under-constrains its Selenium dependency** — see "Library Upgrade" above for the full mechanism and the real fix (pin Selenium to 4.26.0 via `dependencyManagement`, not downgrade java-client).

2. **Stale static driver reference (framework-level bug, present in the reference template too).** `Base.java` originally had `public static AppiumDriver driver = getDriverForFramework();` — a static field initialized once per JVM. Since each `@Test` method creates a brand-new Appium session, this froze onto whichever session was live the *first* time `Base` loaded, then silently referenced a dead session for every later direct `driver.findElement(...)` call (`DragPage`, `LoginPage`, `FormsPage`). Symptom: `NoSuchSessionException: Session ID is null` starting several tests into a run. **Fix:** converted `driver` to an instance field set fresh in `Base()`'s constructor, since a new Page object is constructed on every `Pages.XPage()` call.

3. **Test state leaking across sessions.** `noReset=true` plus a new Appium session per test meant "am start" just brought the *existing* app process forward rather than truly restarting it — so a side menu opened by one test was still open when the next test's session began, hiding the bottom tab bar it needed. **Fix:** added `appium:forceAppLaunch=true` + `appium:shouldTerminateApp=true` so every test starts from a guaranteed-fresh activity state.

4. **Login "Success" alert used the wrong resource-id.** Wrote `android:id/alert_title` (system-framework style) instead of the actual `com.wdiodemoapp:id/alert_title` (this specific dialog is app-themed, unlike its `message`/`button1`, which genuinely are `android:id/*`). Confirmed by screenshot: the alert was rendering the whole time, my locator just never matched it.

5. **Unprefixed `resource-id` values break `AndroidFindBy(id=...)`.** The Swipe carousel (`resource-id="Carousel"`) and Forms dropdown value (`resource-id="text_input"`) both use bare, unnamespaced resource-ids (no `package:id/` prefix) — a valid but unusual React Native pattern. Appium's `id=` locator strategy silently auto-prefixes the app package when no colon is present, so it was looking for `com.wdiodemoapp:id/Carousel`, which never matches. **Fix:** switched both to `xpath="//*[@resource-id='...']"` to match the literal string.

6. **Forms "Active" button triggers an undocumented native confirmation dialog** ("This button is" / "This button is active", ASK ME LATER / CANCEL / OK) that the source test case (TC-FORM-004) didn't anticipate. Tapping "Inactive" immediately after left the dialog blocking the screen, so the next locator lookup timed out. **Fix:** `FormsPage` now defensively dismisses this dialog after either button tap.

7. **Permission toggle test's core assumption was wrong.** TC-PERM-003 assumed tapping a granted permission's switch would flip it off. Live execution showed Android correctly refuses this — since the suite runs with `autoGrantPermissions=true`, the permission is already granted at the OS level, and tapping the switch surfaces a native "Change in Settings" dialog directing the user to system Settings instead of silently revoking access in-app. **Fix:** rewrote the test to assert this actual (and correct, security-appropriate) behavior — the switch must stay granted after the dialog is cancelled — rather than forcing the original, incorrect assumption to "pass."

---

## Real Bugs Found and Fixed — iOS (adding platform support surfaced 7 more, none of them hypothetical)

Went from 2/9 → 8/9 → 27/32 → 30/32 → **32/32** across five live-debugging rounds. Every one of these was found by actually running against the iPhone 15 simulator, not inferred from documentation:

1. **Same session state-leakage bug as Android, unaddressed for iOS.** `InitiateDriver` set `appium:forceAppLaunch`/`shouldTerminateApp` only in `buildAndroidOptions`. These are Appium base-driver capabilities, not UiAutomator2-specific, so they apply to XCUITest too — without them, 7 of 9 smoke tests failed with a plain "element not visible" timeout because the previous test's screen was still showing. **Fix:** added both capabilities to `buildIOSOptions` as well.

2. **iOS permission decisions persist across `noReset=true` sessions.** A `PermissionsTests` assertion expected Camera to start ungranted, but it had already been granted permanently during earlier manual exploration on this exact simulator (iOS TCC privacy decisions survive app relaunches, only clearing on uninstall) — `noReset=true` never resets them. Not a framework bug, but a real environment-state trap worth documenting: **any iOS permission test needs a genuinely fresh install** (`xcrun simctl uninstall`) to be deterministic, not just a relaunched session.

3. **`hideKeyboard()` silently corrupts the next field interaction on iOS.** Calling it after typing into the email field left the password field completely empty on every run — confirmed via a raw session replay that the identical type-email → type-password sequence works perfectly with the `hideKeyboard()` call removed. WebDriverAgent itself throws "Did not know how to dismiss the keyboard" internally; the exception is caught, but something about the attempt still disrupts the next field's focus. **Fix:** `Base.hideKeyboard()` is now a no-op on iOS — dismissing the keyboard isn't actually required for any of this app's flows.

4. **`sendKeys()` doesn't reliably focus an iOS `XCUIElementTypeSecureTextField`.** Related to #3 but independently confirmed: even with `hideKeyboard()` removed, typing into the password field without an explicit `.click()` first was unreliable. **Fix:** `Base.typeWithHeal()` now clicks the element before clearing/typing on every platform (harmless on Android, necessary on iOS).

5. **A single batched `sendKeys()` drops iOS keyboard-page-switch characters.** `"!@#$%^&*()"` arrived as `"!#$%^&*()"` — the `@` specifically, which lives on iOS's secondary symbols keyboard page. Sending the exact same characters one at a time with a ~120ms pause between each preserved all of them. **Fix:** `Base` now types character-by-character on iOS only (Android's single-call `sendKeys` remains unchanged — no such issue there).

6. **Forms dropdown is a completely different native widget per platform.** Android renders it as a tappable `AlertDialog`-style option list (find the option by text, click it) — this app's own React Native dropdown component renders the SAME control as a native spinning `XCUIElementTypePickerWheel` on iOS. There is no per-option element to click at all on iOS. Also: tapping the dropdown's outer container was a silent no-op on iOS (confirmed with both `element.click()` and a raw W3C coordinate tap) — only tapping the chevron icon specifically opened the picker. **Fix:** `FormsPage.selectDropdownOption()` now branches — iOS taps the chevron, sets the `PickerWheel`'s value directly via `sendKeys` (Appium's iOS driver spins the wheel to match), and confirms with "Done"; Android keeps the original tap-the-list-item behavior.

7. **`autoAcceptAlerts` (needed for real iOS system permission alerts) also auto-dismisses this app's own in-app alerts**, racing against this framework's explicit dismiss calls. Two failures traced to the exact same cause: `LoginPage.dismissAlert()` timed out waiting to click "OK" on the Login success alert (already gone), and `FormsPage`'s button-state dialog dismissal hit a stale-element exception — both because the alert was auto-tapped by XCUITest before the explicit dismiss code ran. **Fix:** both methods now check for the button's presence first and treat "already gone" as success rather than racing a hard wait-then-click against the automatic dismissal.

Additionally, the permission-toggle test itself needed a genuine **async-timing fix**, not just a platform branch: after `autoAcceptAlerts` accepts the system alert, the switch's state takes up to ~500ms to reflect the grant (confirmed by polling every 500ms live) — checking immediately after the click reads the stale pre-grant value. `PermissionsPage.togglePermission()` now polls for up to 3s on iOS for the state to actually change, rather than assuming the click is synchronous with the OS-level effect.

---

## Self-Healing Locators

Ported from this framework's Playwright-Java self-healer (`07-outputs/_archive/Automation-java-2026-08-05/.../SelfHealer.java`, documented in `03-standards/playwright-java-standards.md` "Self-Healing Locators") to Appium/Java — same F1 precision/recall scoring algorithm, adapted to read the Android UI hierarchy (`driver.getPageSource()`) instead of the DOM. New standards section added at `03-standards/appium-java-standards.md` "Self-Healing Locators" so future Appium-stack generations include this by default.

**Architecture note:** this stack's canonical Page Object Repository uses `@AndroidFindBy`-annotated fields (no single `Locator`-wrapper class like Playwright-Java's `PlaywrightLocator` to bake healing into implicitly), so healing instead lives in two new `Base` action-wrapper methods — `clickWithHeal(element, hint)` and `typeWithHeal(element, hint, text)` — that every Page Action class's interaction methods now route through instead of calling `element.click()`/`sendKeys()` directly. READ methods (assertions, `getText()`) and gesture endpoints (swipe/drag) are deliberately left unwrapped, matching the reference implementation's own scope.

New file: `src/main/java/.../utils/SelfHealer.java`. Resolution priority: `content-desc` (Android) / `name` (iOS) → `AppiumBy.accessibilityId` (strongest — this app's own "stable test selector" convention, confirmed identical on both platforms) → `resource-id` (Android only) → `By.id` or `By.xpath` (bare/unnamespaced resource-ids, a real pattern hit earlier in this same app, need the xpath form) → visible `text` (Android `@text`, or iOS `@label`/`@value`) → xpath text match. Cache (`healing-cache.json`) and log (`healing-report.log`) live at `07-outputs/Automation-Appium/` — one level above `framework/`, matching the web-stack convention. Candidate collection and resolution are fully cross-platform as of the iOS support work — same algorithm, same code path, no platform branch needed inside `SelfHealer` itself.

### Verified live, end-to-end (not just compiled)

1. Intentionally broke `FormsPageObjectRepository.buttonActive`'s locator (`button-Active` → `button-Active-STALE-DEMO`) and ran `FormsTests.verifyButtonTapTriggersAction`.
2. **Fresh-detection path:** the broken locator timed out as expected (20s), `SelfHealer` scanned the live UI hierarchy, matched the real `button-Active` element against the hint `"Active Button"` with **confidence 1.0**, resolved it via `ACCESSIBILITY_ID`, retried the click — **test passed**. `healing-report.log` and `healing-cache.json` were both written correctly:
   ```
   [2026-08-18T09:13:13.819552Z] HEALED: hint="Active Button" strategy=ACCESSIBILITY_ID value="button-Active" confidence=1.00 originalError="Expected condition failed: waiting for element to be clickable: Located by By.chained({AppiumBy.accessibilityId: button-Active-STALE-DEMO}) (tried for 20 second(s) with 500 milliseconds interval)"
   ```
3. **Cache-hit path:** re-ran the same test — log showed `SelfHealer: cache hit for hint [Active Button] (confidence 1.0)`, skipping a fresh hierarchy scan — **test passed** again.
4. Reverted the intentional break, cleaned the demo cache/log, and reran the full 32-test regression suite: still **32/32 passing**, confirming the healing wrapper is a true no-op when locators are healthy.

---

## Known Limitation (environment, not app or test defect) — 1 test disabled (Android only)

`WebviewTests.verifySwitchToWebviewContext_requiresChromedriver` is disabled (`enabled=false`) — **Android-specific**, since iOS WebView automation uses Safari/WebKit remote debugging, not Chromedriver, and isn't affected by this at all. Switching Appium's context into the app's Android WebView requires a Chromedriver matching the emulator's System WebView build (109.0.5414.x). Live execution showed Appium's chromedriver auto-provisioning hangs for **~10 minutes** before timing out — even though the exact same file downloads directly in ~12 seconds when fetched manually, proving the bottleneck is inside Appium's own resolution logic in this sandbox, not the network or the app. Fixed the failure mode from "hangs 10 minutes" to "fails fast" via `appium:chromedriverAutodownload=false`, and kept a lighter, still-meaningful replacement test (`verifyWebviewContextBecomesAvailable`) that confirms the `WEBVIEW_*` context registers correctly without requiring the full switch — this replacement test passes on **both** platforms. Re-enable the disabled Android test once a matching chromedriver binary is available locally (`appium:chromedriverExecutable`) or Appium's chromedriver module is upgraded.

---

## Scope Note

Per the explicitly supplied source document, automation covers the 8 modules in `WdioNativeDemoApp-FunctionalUITestCases.md` (Home, Navigation, WebView, Login, Forms, Swipe, Drag, Permissions), on **both Android and iOS**. Data Management (the 4 local-storage tiers documented in `WdioNativeDemoApp-DataStorageTestCases.md`) was intentionally not automated in this pass — no Page Object exists for it, on either platform. Biometric login tests (TC-AUTH-008/009/010/011) were not automated on either platform: neither the Android emulator nor the iOS simulator used here has an enrolled fingerprint/Face ID, and the app's own Login screen copy confirms the biometric button only renders "when the device has Touch/FaceID or FingerPrint enabled."

### Next Steps
1. If Data Management or biometric coverage is wanted, that requires either a biometric-configured emulator/simulator or a follow-up generation pass with dedicated Page Objects.
2. For CI, wire `appium:chromedriverExecutable` to a pinned, pre-downloaded chromedriver if Android WebView-context automation becomes a priority.
3. `mvn test -DsuiteFile=.../CC_TestNG.xml` (add `-P ios` for iOS) is ready to run as a release-readiness gate for future app version bumps, on either platform.
4. This project's `~/.appium` / `~/.npm` on this machine still has root-owned files outside `~/.appium-fresh` — if that original Appium home is ever reused, the same `EACCES` install failures will recur until it's `chown`'d (needs `sudo`, not run here — see "Library Upgrade" above) or abandoned in favor of `~/.appium-fresh`.
