# WebdriverIO Native Demo App — Appium Mobile Automation Framework

Appium + Java + TestNG + Maven mobile automation framework, generated per `03-standards/appium-java-standards.md` by the QA-AI-Framework **Appium Automation Orchestrator** (Mode A — new framework), and verified green against both a live Android emulator and a live iOS simulator (not just generated — see [Live Verification](#live-verification--real-bugs-found)).

**Target application:** [WebdriverIO Native Demo App](https://github.com/webdriverio/native-demo-app) — `com.wdiodemoapp` / `org.wdiodemoapp`, a purpose-built guinea-pig app for practicing Appium/WebdriverIO mobile automation. Android build: `android.wdio.native.app.v2.2.0.apk`. iOS build: simulator `.app` bundle (see [Configuration](#configuration)). **Source test cases:** `07-outputs/TestCases/WdioNativeDemoApp-FunctionalUITestCases.md` **Full generation + live-run report:** [`../AutomationSummary.md`](../AutomationSummary.md) (read this for the complete list of real bugs found and fixed during live execution) **Sibling framework:** this app also has a WebdriverIO + JavaScript + Cucumber automation stack at [`07-outputs/Automation-WebdriverIO/Framework/`](../../Automation-WebdriverIO/Framework/README.md) — a separate, independently-runnable project, not a variant of this one. Never merge dependency manifests, config, or source files between the two.

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
- [Adding a New Page Object](#adding-a-new-page-object)
- [Adding a New Test Class](#adding-a-new-test-class)
- [Reports & Logs](#reports--logs)
- [Live Verification / Real Bugs Found](#live-verification--real-bugs-found)
- [Known Limitations](#known-limitations)
- [Troubleshooting](#troubleshooting)

---

## Folder Layout

This README lives inside `framework/`, the Maven module itself. Its parent, `Automation-Appium/`, is the project root shared with a couple of generation-time siblings:

```
Automation-Appium/                  <- project root (one level above this file)
├── AutomationSummary.md            <- full generation + live-run report (real bugs found, fixes, decisions)
├── healing-cache.json              <- SelfHealer's cached heal recipes (generated at runtime, TTL 1h)
├── healing-report.log              <- append-only log of every successful self-heal (generated at runtime)
└── framework/                      <- YOU ARE HERE — the Maven module (pom.xml lives here)
    ├── README.md                   <- this file
    ├── pom.xml
    ├── .gitignore
    ├── src/main/java/com/codecraft/wdiodemoapp/
    │   ├── utils/                        DriverFactory, Base, Object_Base, InitiateDriver, PropertyReader, SelfHealer
    │   ├── annotations/                  TestDataSource
    │   ├── Pages.java                    static registry: Pages.LoginPage(), Pages.FormsPage(), ...
    │   └── pageobjects/                  one flatcase subfolder per module, e.g. login/LoginPage.java, login/LoginPageObjectRepository.java
    │       ├── home/, navigation/, webview/, login/, forms/, swipe/, drag/, permissions/
    ├── src/main/resources/
    │   ├── config.properties             AppPath/AndroidAppPackage/AndroidAppActivity + IOSAppPath/BundleId/IOSDeviceName/..., SeverAddress, RUN_ON (filtered from Maven profile)
    │   └── log4j2.xml
    └── src/test/java/com/codecraft/wdiodemoapp/
        ├── framework/TestBase.java       @BeforeMethod/@AfterMethod: creates + quits an Appium session per test
        ├── testng/TestListener.java      screenshots on failure -> framework/screenshots/
        ├── <module>tests/<Module>Tests.java   one TestNG class per module (8 total), flatcase package/PascalCase class
        ├── CC_TestNG.xml                 regression suite — all 32 active tests
        └── CC_Sample_TestNG.xml          smoke suite — 9 tests, group="smoke"
```

All package-level folders under `com.codecraft.wdiodemoapp` follow flatcase (`utils`, `annotations`, `pageobjects`, `pageobjects/login`, `dragtests`, `testng`, etc.) — only the `.java` file names and class names themselves stay PascalCase (`LoginPage.java`, `class LoginPage`). This was a deliberate correction from the original per-module PascalCase folders (`Login/`, `Utils/`, `TestNG/`, ...).

Unlike this framework's web-stack siblings (which split `Framework/`, `PageObjects/`, `Scripts/` into sibling directories), this project keeps everything inside one Maven module — the simpler, single-app, local-only scope of this engagement didn't need the split.

---

## Technology Stack

| Property | Value | Notes |
|---|---|---|
| Language | Java 17 | |
| Build | Maven | tested with both `apache-maven-3.9.9` and Homebrew `mvn` |
| Test Framework | TestNG | **7.11.0** (latest) |
| Appium Client | `io.appium:java-client` | **9.4.0** (latest) — see [Known Limitations](#known-limitations) for why this needs Selenium pinned alongside it, not downgraded |
| Selenium | **4.26.0**, pinned via `dependencyManagement` | see [Known Limitations](#known-limitations) |
| Automation Driver | UiAutomator2 (Android) **+ XCUITest (iOS)** | both platforms fully supported |
| Logging | Log4j2 | **2.26.0**, console + `logs/automation.log` |
| Reporting | TestNG native + failure screenshots | `Screenshots/<testMethodName>.png` |
| JSON (SelfHealer cache) | Jackson `jackson-databind` **2.19.0** | |
| Other | Commons Lang3 **3.20.0** | |

**Deliberate simplifications vs. this framework's reference template** (`Mobile_App_Automation_Template`): no BrowserStack/Flutter support paths, no Allure reporting, no TestNG priority/retry listeners — that machinery would add risk without value for this single-app engagement. iOS was added properly (Maven `android`/`ios` profiles, per-platform driver bootstrap) once a simulator build became available, not simplified away. The core package convention (`utils/DriverFactory`, `Base`, `Object_Base`, `InitiateDriver`, `Pages` registry, Page Action + Page Object Repository per module, `TestBase`, `CC_TestNG.xml`/`CC_Sample_TestNG.xml`) was kept faithful to the template.

---

## Prerequisites

- **JDK 17+**
- **Maven 3.9+** (`mvn -version`)
- **Android SDK** with `platform-tools`, `emulator`, and at least one system image installed (`$ANDROID_HOME` set)
- **Xcode + iOS Simulator** (for iOS runs only) — `xcrun simctl list devices` should show a bootable simulator; verified against **iPhone 15, iOS 17.2**
- **Node.js + Appium** (`npm install -g appium`) with both drivers installed: `appium driver install uiautomator2` (Android) and `appium driver install xcuitest` (iOS)
- **The app under test**: `android.wdio.native.app.v2.2.0.apk` for Android, a simulator `.app` build for iOS (or your own build of the [native-demo-app](https://github.com/webdriverio/native-demo-app))
- An Android emulator/device **or** iOS simulator/device (this project was verified against a **Pixel_6_API_33** Android emulator and an **iPhone 15 / iOS 17.2** simulator)

---

## Setup

```bash
# --- Android ---
# 1. Start an Android emulator (or connect a real device) and confirm it's visible
emulator -avd Pixel_6_API_33 -no-snapshot -no-boot-anim &
adb devices -l

# --- iOS ---
# 1. Boot the simulator (or confirm one is already booted) and note its UDID
xcrun simctl boot "iPhone 15"
xcrun simctl list devices | grep Booted

# --- Both platforms ---
# 2. Start the Appium server (loads both uiautomator2 and xcuitest drivers)
appium --address 127.0.0.1 --port 4723 --relaxed-security &
curl -s http://127.0.0.1:4723/status   # {"value":{"ready":true, ...}}

# 3. Point config.properties at your app builds (see Configuration below), then compile
cd 07-outputs/Automation-Appium/framework
mvn -q test-compile
```

## Configuration

Edit `src/main/resources/config.properties`:

```properties
AppPath=/absolute/path/to/android.wdio.native.app.v2.2.0.apk
AndroidAppPackage=com.wdiodemoapp
AndroidAppActivity=com.wdiodemoapp.MainActivity
DeviceName=emulator-5554

IOSAppPath=/absolute/path/to/wdiodemoapp.app
BundleId=org.wdiodemoapp
IOSDeviceName=iPhone 15
IOSPlatformVersion=17.2
IOSUdid=<simulator UDID from `xcrun simctl list devices`>

SeverAddress=http://127.0.0.1:4723
```

`RUN_ON` (Android vs. iOS) is **not** set by hand in this file — it's filtered in from the active Maven profile (`android`/`ios`, see [Running the Tests](#running-the-tests)) at build time via `${RunOn.value}`.

`InitiateDriver` reads this file to build either `UiAutomator2Options` or `XCUITestOptions` depending on `RUN_ON`. It also sets platform-appropriate session-hygiene capabilities in code (not via properties): `appium:forceAppLaunch=true` and `appium:shouldTerminateApp=true` on both platforms so every test starts from a fully-relaunched app state (see `../AutomationSummary.md` finding #3), plus `appium:autoGrantPermissions=true` (Android) or `appium:autoAcceptAlerts=true` (iOS, needed for real system permission alerts — see Known Limitations for the alert-racing gotcha this introduces).

---

## Running the Tests

```bash
cd 07-outputs/Automation-Appium/framework

# Android — smoke suite (9 tests) and full regression (32 tests)
mvn test -P android -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_Sample_TestNG.xml
mvn test -P android -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_TestNG.xml

# iOS — same suites, -P ios activates the iOS profile (RunOn=iOS_APP)
mvn test -P ios -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_Sample_TestNG.xml
mvn test -P ios -DsuiteFile=src/test/java/com/codecraft/wdiodemoapp/CC_TestNG.xml
```

`android` is the default Maven profile — omitting `-P` runs Android. Both platforms require their respective emulator/simulator plus the shared Appium server from [Setup](#setup) to already be running. Each `@Test` method creates and tears down its own Appium session (see [Architecture](#architecture)), so the app cold-launches once per test — this trades some wall-clock time for full test isolation.

**iOS note:** permission decisions (Camera/Microphone/Location/Photos) persist on the simulator across `noReset=true` sessions until the app is uninstalled (unlike Android, which starts fresh via `autoGrantPermissions`). Run `xcrun simctl uninstall <udid> org.wdiodemoapp` before a `PermissionsTests` run if you need default (ungranted) state.

---

## Test Coverage

| Module | Test Class | Active Tests | Notes |
|---|---|---|---|
| Home | `HomeTests` | 3 | |
| Navigation (tab bar + side menu) | `NavigationTests` | 4 | |
| WebView | `WebviewTests` | 2 (+1 disabled) | context-switch test disabled on **Android only** — see [Known Limitations](#known-limitations); iOS WebView automation doesn't use chromedriver and is unaffected |
| Login / Sign Up | `LoginTests` | 5 | biometric sub-flows not automated (no enrolled biometric on emulator/simulator) |
| Forms | `FormsTests` | 6 | dropdown interaction is platform-specific (Android option list vs. iOS `XCUIElementTypePickerWheel`) — same test, branching Page Action code |
| Swipe | `SwipeTests` | 4 | |
| Drag | `DragTests` | 3 | full 9-piece puzzle completion verified live on both platforms |
| Permissions | `PermissionsTests` | 5 | expected default/toggle state is platform-aware (Android: granted-by-default via `autoGrantPermissions`; iOS: ungranted-by-default, first toggle triggers a real system alert) |
| **Total** | **8 classes** | **32 active, 1 disabled (Android only)** | last full run: **Android 32/32, iOS 32/32 — 64/64 total** |

Not covered by this pass (see `../AutomationSummary.md` "Scope Note" for why): **Data Management** (the app's 4 local-storage tiers — no Page Object exists for this module yet) and **biometric login** (TC-AUTH-008–011 — needs a biometric-enrolled emulator/device/simulator).

---

## Architecture

Standard Appium/Java Page Object Model, per `03-standards/appium-java-standards.md`:

- **`utils/DriverFactory`** — `ThreadLocal<AppiumDriver>` holder (platform-generic — holds either an `AndroidDriver` or `IOSDriver`, both of which extend `AppiumDriver`).
- **`utils/Base`** — shared action primitives every Page Action class extends: `waitUntilElementIsVisible`/`Clickable`, `swipe`/`dragAndDrop` (W3C pointer actions, platform-agnostic), context switching (via the `SupportsContextSwitching` interface both drivers implement), `clickWithHeal`/`typeWithHeal` (see [Self-Healing Locators](#self-healing-locators)), plus `isAndroid()`/`isIOS()` helpers (read `platformName` off the session capabilities) that back every platform branch in the Page Action classes. `driver` is an **instance field set in the constructor**, not a static field — see `../AutomationSummary.md` finding #2 for why a static field is a real, live-discovered bug in this pattern.
- **`utils/Object_Base`** — `PageFactory`/`AppiumFieldDecorator` initialization for the dual `@AndroidFindBy`/`@iOSXCUITFindBy`-annotated repository fields; the decorator resolves whichever annotation matches the live session's platform.
- **`utils/InitiateDriver`** — reads `RUN_ON` from `config.properties` (set by the active Maven profile) and branches: builds `UiAutomator2Options` + `AndroidDriver` for Android, `XCUITestOptions` + `IOSDriver` for iOS. This is the one class that "knows" about both platforms — everything downstream works against the common `AppiumDriver` type.
- **`<Module>PageObjectRepository`** — one per module, fields carry **both** `@AndroidFindBy` and `@iOSXCUITFindBy` annotations. Every locator on both platforms is a **real value captured from a live Appium session** (see [Live Verification](#live-verification--real-bugs-found)) — none are placeholders. Live exploration found this app's testID props map 1:1 across platforms in almost every case; the handful of exceptions (alert buttons, the WebView element, an iOS-only dropdown chevron) are called out in `../AutomationSummary.md`.
- **`<Module>Page`** — one per module, extends `Base`, exposes business-action methods (`enterEmail(...)`, `tapLoginButton()`, ...) that the repository's elements back. Where a widget behaves fundamentally differently per platform (e.g. Forms' dropdown — Android's tappable option list vs. iOS's native `XCUIElementTypePickerWheel`), the method branches internally on `isAndroid()`/`isIOS()` so callers and tests stay platform-agnostic.
- **`Pages`** — static registry (`Pages.LoginPage()`, `Pages.FormsPage()`, ...) — test classes must go through this, never `new LoginPage()` or `DriverFactory` directly.
- **`framework/TestBase`** — `@BeforeMethod` creates a session via `InitiateDriver`, `@AfterMethod` quits it. One session per test method, by design (see [Running the Tests](#running-the-tests)).
- **`<Module>Tests`** — one TestNG class per module, extends `TestBase`. A few classes (e.g. `PermissionsTests`) assert platform-appropriate expected states rather than one hardcoded expectation, since Android and iOS genuinely differ in permission/security default behavior (not a test smell — a real OS difference, documented in `../AutomationSummary.md`).

---

## Self-Healing Locators

This framework includes a rule-based self-healing layer, ported from this repo's Playwright-Java `SelfHealer` (`07-outputs/_archive/Automation-java-2026-08-05/.../SelfHealer.java`, standardized in `03-standards/playwright-java-standards.md`) to Appium/Android. Full design + a live, end-to-end verification (intentionally broke a locator, confirmed the heal, confirmed the cache hit, reverted) is in `../AutomationSummary.md`'s "Self-Healing Locators" section — summary:

- **`utils/SelfHealer.java`** scores every element in the live UI hierarchy (`driver.getPageSource()`) against a human-readable hint (e.g. `"Login Button"`) using the same precision/recall (F1) algorithm as the web-stack implementations, then resolves the best match to a real locator: accessibility id (`content-desc` on Android / `name` on iOS) → `AppiumBy.accessibilityId` → `resource-id` (Android only) → `By.id`/`By.xpath` → visible text (`@text` on Android / `@label`/`@value` on iOS). Fully cross-platform — the same healer runs unmodified against either platform's UI hierarchy XML.
- **`Base.clickWithHeal(element, hint)`** / **`Base.typeWithHeal(element, hint, text)`** wrap the normal wait-then-act pattern; on failure they hand off to `SelfHealer` and retry against the healed element. Every Page Action class's interactive methods (click/type) route through these instead of calling `element.click()`/`sendKeys()` directly. READ-only checks and gesture (swipe/drag) endpoints are intentionally **not** wrapped — see the standards doc for why.
- Successful heals are cached for 1 hour in **`../healing-cache.json`** and logged to **`../healing-report.log`** (both one level above `Framework/`, i.e. directly under `Automation-Appium/`).

If a test fails with `NoSuchElementException`/`TimeoutException` on a click or type action, check `healing-report.log` first — if a heal was attempted and failed, the log/warning will say so; if it succeeded, the test would have passed and the log records what changed.

---

## Adding a New Page Object

1. Create `<Module>PageObjectRepository.java` under `src/main/java/com/codecraft/wdiodemoapp/<Module>/`, extending `Object_Base`, with `@AndroidFindBy`-annotated fields — **verify every locator against a live Appium session first** (`driver.getPageSource()`), never guess from static analysis of the APK.
2. Create `<Module>Page.java` in the same package, extending `Base`, exposing business-action methods. Route click/type actions through `clickWithHeal`/`typeWithHeal` with a descriptive hint (see [Self-Healing Locators](#self-healing-locators)).
3. Register a static accessor in `Pages.java`:
   ```java
   public static <Module>Page <Module>Page() {
       return getPage(<Module>Page.class);
   }
   ```

## Adding a New Test Class

1. Create `<Module>Tests.java` under `src/test/java/com/codecraft/wdiodemoapp/<Module>Tests/`, extending `TestBase`.
2. Use `groups = {"smoke", "regression"}` for one representative test per module, `groups = {"regression"}` for the rest.
3. Add the class to `<classes>` in `CC_TestNG.xml` (and `CC_Sample_TestNG.xml` if it has a smoke test).

---

## Reports & Logs

| Output | Location |
|---|---|
| TestNG console/XML report | `target/surefire-reports/` |
| Failure screenshots | `Screenshots/<testMethodName>.png` |
| Application log | `logs/automation.log` |
| Self-heal cache | `../healing-cache.json` |
| Self-heal report | `../healing-report.log` |

---

## Live Verification / Real Bugs Found

This framework was not just generated — it was run against a live Android emulator and a live iOS simulator until both were green. The Android pass surfaced 7 real, non-obvious bugs (a stale-static-driver-field bug present in the framework's own reference template, an Appium locator-strategy quirk with unnamespaced `resource-id`s, two genuinely wrong assumptions in the source test cases about app/OS behavior, and more). Adding iOS support surfaced 7 more, entirely different bugs — a session-state-leakage gap in the iOS driver options, iOS permission persistence across `noReset=true` sessions, `hideKeyboard()` corrupting subsequent iOS field input, `sendKeys()` not reliably focusing an iOS `SecureTextField`, a character-drop bug when batch-typing on iOS, the Forms dropdown being a fundamentally different native widget on iOS (`XCUIElementTypePickerWheel` vs. Android's tappable list), and `autoAcceptAlerts` racing the framework's own explicit alert-dismissal code. **Read `../AutomationSummary.md` for the full list on both platforms** — it's the most valuable part of this generation and is not duplicated here.

---

## Known Limitations

- **`WebviewTests` context-switch test is disabled on Android only.** Switching into the app's WebView needs a Chromedriver matching the emulator's System WebView (109.0.5414.x); Appium's auto-provisioning hung ~10 minutes in this environment before timing out. Mitigated with `appium:chromedriverAutodownload=false` (fail fast instead of hanging) and a lighter replacement test that confirms the `WEBVIEW_*` context is detected without performing the full switch. **iOS is unaffected** — iOS WebView automation goes through Safari/WebKit remote debugging, not chromedriver, so the equivalent iOS test runs the full switch. See `../AutomationSummary.md` for the Android re-enable path.
- **`java-client` is on the latest 9.4.0, with Selenium pinned to 4.26.0 via `dependencyManagement`.** java-client 9.4.0's own POM declares an *open* version range (`[4.26.0, 5.0)`) for `selenium-api`/`-remote-driver`/`-support`, which resolves to whatever the newest Selenium release is at build time — 4.47.0 as of this writing, which removed `org.openqa.selenium.ContextAware`/`html5.LocationContext` that Appium's driver class hierarchy still references, a hard compile failure. Pinning via `dependencyManagement` to the tested floor (4.26.0) fixes this **without** downgrading java-client. Do not remove the `dependencyManagement` block, and re-verify it if `java.client.appium` is bumped again in future.
- **iOS permission state persists across test runs** (`noReset=true` + simulator TCC privacy decisions survive between sessions, unlike Android's `autoGrantPermissions` which resets per session). Run `xcrun simctl uninstall <udid> org.wdiodemoapp` before a clean `PermissionsTests` run — see [Running the Tests](#running-the-tests).
- **Data Management and biometric login are not automated** — see [Test Coverage](#test-coverage).
- **Drag/drop and swipe gesture endpoints do not self-heal** — only single-element click/type actions do (see [Self-Healing Locators](#self-healing-locators)).

---

## Troubleshooting

| Symptom | Likely Cause | Fix |
|---|---|---|
| `NoSuchSessionException: Session ID is null` | Stale driver reference somewhere still calling a dead session | Should not recur — this was a real bug, fixed in `Base.java` (instance field, not static). If it reappears, check for a new direct `driver.findElement(...)` call bypassing `Base`. |
| Element not found immediately after a menu/tab tap | Previous test's screen state leaked into this session | Confirm `appium:forceAppLaunch`/`shouldTerminateApp` are still set in `InitiateDriver` for both `buildAndroidOptions()` and `buildIOSOptions()`. |
| `mvn test-compile` fails on `ContextAware`/`LocationContext` | The Selenium `dependencyManagement` pin in `pom.xml` was removed or java-client was bumped past a version whose floor no longer resolves cleanly | Restore/adjust the `dependencyManagement` block pinning `selenium-*` artifacts to a version that still has these classes (4.26.0 confirmed working). |
| A click/type action is slow or logs `[self-heal]` warnings | The real app locator changed | Check `healing-report.log` for what it healed to, then update the Page Object Repository's `@AndroidFindBy`/`@iOSXCUITFindBy` value to match — don't rely on healing permanently, it's a safety net, not a substitute for correct locators. |
| Appium server takes 30–60s to report `ready` | It's loading all installed drivers (uiautomator2, xcuitest, flutter, ...) on startup | Normal for this environment's Appium install — wait for `curl http://127.0.0.1:4723/status` to return `"ready":true` before running tests. |
| `EACCES`/permission errors reinstalling an Appium driver | A prior `sudo npm install` left root-owned files under `~/.appium` or `~/.npm` | Don't `sudo` your way through it — point Appium at an isolated home instead: `export APPIUM_HOME=~/.appium-fresh` before `appium driver install ...` and before starting the server. |
| iOS: typed text into email/password field is empty or missing characters | `hideKeyboard()` was called on iOS (WDA doesn't support programmatic keyboard dismissal and corrupts the next field's input), or characters were sent in one batched `sendKeys()` call (`@` and other symbols can drop) | `Base.hideKeyboard()` is already a no-op on iOS — don't add a new direct keyboard-dismiss call. `Base.typeText()` already types character-by-character on iOS — route all typing through `typeWithHeal()`, not a raw `sendKeys()`. |
| iOS: Forms dropdown tap does nothing | The dropdown's tappable container is a silent no-op on iOS — only the chevron icon opens the native `XCUIElementTypePickerWheel` | Use `FormsPage.selectDropdownOption()`, which already branches for this; don't tap `repo.dropdown` directly on iOS. |
| iOS: `PermissionsTests` sees a permission as already granted on a fresh-looking run | iOS TCC permission decisions persist across `noReset=true` sessions until the app is uninstalled | `xcrun simctl uninstall <udid> org.wdiodemoapp` before the run. |
| iOS: login/button-state alert click times out or hits a stale-element exception | `appium:autoAcceptAlerts` auto-tapped the alert before the framework's own explicit dismiss code ran | Expected and already handled — `LoginPage.dismissAlert()` / `FormsPage.dismissButtonStateDialogIfPresent()` check for button presence first and treat "already gone" as success. If a *new* dialog exhibits this, apply the same pattern rather than a hard wait-then-click. |
