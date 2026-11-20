# Appium Java Automation Standards

## Stack

- **Language:** Java 17+
- **Test Framework:** TestNG — always verify the actual latest version at generation time (see "Dependency Version Policy" below); never hardcode a number from this doc
- **Build Tool:** Maven
- **Appium Client:** io.appium:java-client — always verify the actual latest version at generation time
- **Selenium:** individually pinned via `dependencyManagement` (never `selenium-java`) to the floor of java-client's own declared Selenium range — see "Dependency Version Policy" below; this is mandatory, not optional
- **Reporting:** ExtentReports 5.x (`ExtentReportManager`) + a custom TestNG-suite-level HTML summary (`MobileEmailReport`) — see "Reporting" below for why this replaced an earlier, never-actually-implemented Allure design
- **Logging:** Log4j2
- **Utilities:** Apache Commons Lang3, OkHttp3, JSON Simple
- **Flutter Support:** Kotlin-based FlutterFinder (optional, when CONTEXT=FLUTTER)

---

## Dependency Version Policy

**Mandatory, not optional.** Every version number anywhere in this stack's docs/templates is illustrative, not current — re-verify via Maven Central at generation time, the same discipline `03-standards/webdriverio-javascript-standards.md`'s "Latest-Dependency-Version Policy" requires for the JS sibling stack (`curl -s "https://search.maven.org/solrsearch/select?q=g:%22<groupId>%22+AND+a:%22<artifactId>%22&core=gav&rows=1&wt=json"`).

**This stack has one additional, mandatory step the JS sibling does not need: pinning Selenium.** `io.appium:java-client` declares its own Selenium dependency as an **open-ended version range** (e.g. `[4.26.0, 5.0)`), not a fixed version. Left unpinned, Maven resolves that range to whatever the newest Selenium release is *at build time* — which routinely breaks the specific `java-client` release you're using, since Selenium refactors internals between releases that `java-client` hasn't caught up to yet. **Live-run confirmed failure modes:**

- `NoClassDefFoundError: org/openqa/selenium/ContextAware` (or `org.openqa.selenium.html5.LocationContext`) — Selenium removed these classes in a recent release; `AndroidDriver`/`IOSDriver`'s class hierarchy still references them, so merely *constructing* either driver throws this at runtime (not a compile-time symptom — `mvn compile`/`mvn test-compile` succeed cleanly; only an actual session-creation call fails).
- `IllegalAccessError` inside `AppiumCommandExecutor` reflectively touching a Selenium-internal field (e.g. `HttpCommandExecutor.commandCodec`) that a *different* Selenium release made inaccessible in an incompatible way — confirmed live on a Selenium version one below the floor described next, even though that version still contained `ContextAware`.

**The fix:** find the exact floor of the target `java-client` version's declared Selenium range (`curl -s "https://repo1.maven.org/maven2/io/appium/java-client/<version>/java-client-<version>.pom" | grep -A2 "selenium-api\|selenium-remote-driver"`), then pin every Selenium sub-artifact to **exactly that floor** — not one version below it, not left unpinned — via `dependencyManagement`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-api</artifactId><version>${selenium.version}</version></dependency>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-remote-driver</artifactId><version>${selenium.version}</version></dependency>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-support</artifactId><version>${selenium.version}</version></dependency>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-http</artifactId><version>${selenium.version}</version></dependency>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-json</artifactId><version>${selenium.version}</version></dependency>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-manager</artifactId><version>${selenium.version}</version></dependency>
        <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-os</artifactId><version>${selenium.version}</version></dependency>
    </dependencies>
</dependencyManagement>
```

A plain sibling `<dependency>` entry does **not** win against java-client's version range (ranges resolve to the newest matching version from any repository regardless of declaration order) — only `dependencyManagement` pins deterministically. Pin all seven artifacts, not a subset — pinning only `selenium-api`/`-remote-driver`/`-support` while leaving `-http`/`-json`/`-manager`/`-os` to resolve transitively reproduces the same mismatch. Never declare `selenium-java` in this stack at all: it pulls unrelated desktop browser drivers and its own independently-versioned `selenium-api`/`-support`, conflicting with the pin above the same way an unpinned range does.

**Verify twice, not once:**
1. `mvn dependency:tree | grep -i selenium` — confirm every `selenium-*` line resolves to the same pinned version.
2. A live driver-construction test (Phase 0's Live UI Hierarchy Verification, or a throwaway smoke test) — a clean `mvn compile`/`mvn test-compile` is **not** sufficient evidence the pairing works, since the `IllegalAccessError` failure mode above only surfaces at actual runtime session creation.

---

## Project Structure

```
<project-root>/
├── pom.xml
├── Jenkinsfile
├── ReadMe.md
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/com/<org>/<product>/
│   │   │   ├── Pages.java                        ← Central page registry
│   │   │   ├── utils/
│   │   │   │   ├── DriverFactory.java             ← ThreadLocal<AppiumDriver>
│   │   │   │   ├── InitiateDriver.java            ← Driver creation (Local/BrowserStack)
│   │   │   │   ├── Base.java                     ← Extends DriverFactory, common mobile actions
│   │   │   │   ├── FlutterBase.java              ← Flutter-specific gestures and waits
│   │   │   │   ├── Object_Base.java              ← AppiumFieldDecorator page factory base
│   │   │   │   ├── DeviceConfig.java             ← ADB/idevice_id UDID/OS version detection
│   │   │   │   ├── AppiumServer.java             ← Programmatic Appium server management
│   │   │   │   ├── PropertyReader.java           ← .properties file reader (singleton)
│   │   │   │   ├── ReadJson.java                 ← JSON test data loader
│   │   │   │   ├── ReusuableUtility.java         ← Common utility methods
│   │   │   │   ├── Stream.java                   ← Stream utilities
│   │   │   │   ├── Direction.java                ← Enum: UP, DOWN, LEFT, RIGHT
│   │   │   │   ├── APIBase.java                  ← REST API helpers (OkHttp3)
│   │   │   │   ├── SelfHealer.java               ← F1-scored locator self-healing
│   │   │   │   ├── CSVLoader.java                ← Test-data CSV reader (see Test Data Management)
│   │   │   │   ├── TestDataArgumentsProvider.java ← Resolves @TestDataSource on a @Test method
│   │   │   │   ├── BrowserStackConfig.java       ← device_conf.json reader (server/user/key + named environment lookup) — BrowserStack only, see "BrowserStack Integration"
│   │   │   │   ├── BrowserStackAppUploader.java  ← Uploads/caches the bs:// app URL for the selected environment's platform — BrowserStack only
│   │   │   │   ├── MailApi/
│   │   │   │   │   ├── Gmail.java
│   │   │   │   │   ├── Mailcc.java
│   │   │   │   │   └── Mailinator.java
│   │   │   │   └── kotlin/
│   │   │   │       ├── FlutterFinder.kt
│   │   │   │       └── finder/
│   │   │   │           ├── FlutterElement.kt
│   │   │   │           ├── text.kt
│   │   │   │           ├── bySemanticsLabel.kt
│   │   │   │           ├── byValueKey.kt
│   │   │   │           ├── byType.kt
│   │   │   │           ├── byButton.kt
│   │   │   │           ├── byTooltip.kt
│   │   │   │           ├── ancestor.kt
│   │   │   │           ├── descendant.kt
│   │   │   │           ├── pageback.kt
│   │   │   │           └── serializer.kt
│   │   │   ├── annotations/
│   │   │   │   └── TestDataSource.java           ← @Retention(RUNTIME) @Target(METHOD): value/type/key
│   │   │   ├── reports/
│   │   │   │   └── ExtentReportManager.java      ← Static ExtentReports instance + ThreadLocal<ExtentTest> (see Reporting)
│   │   │   └── pageobjects/
│   │   │       └── <modulename>/
│   │   │           ├── <ModuleName>Page.java         ← Page action class (extends FlutterBase or Base)
│   │   │           └── <ModuleName>PageObjectRepository.java  ← Locators only
│   │   └── resources/
│   │       ├── config.properties
│   │       ├── device_conf.json                  ← BrowserStack only — omit entirely when SETUP is never BrowserStack for this project
│   │       └── Log4j2.xml
│   └── test/
│       ├── java/com/<org>/<product>/
│       │   ├── framework/
│       │   │   └── TestBase.java                 ← @BeforeMethod/@AfterMethod (live-verified; one Appium session per @Test method)
│       │   ├── testng/
│       │   │   ├── TestListener.java
│       │   │   ├── RetryAnalyzer.java
│       │   │   ├── EventListener.java
│       │   │   ├── INewListener.java
│       │   │   ├── PriorityInterceptor.java
│       │   │   ├── SuiteCreationListener.java
│       │   │   ├── TestAnnotation.java
│       │   │   └── MobileEmailReport.java        ← ISuiteListener: ExtentReportManager init/flush + custom HTML suite summary
│       │   └── <modulename>tests/
│       │       └── <ModuleName>Tests.java        ← extends TestBase, uses Pages.<Module>()
│       ├── resources/
│       │   └── testdata/
│       │       └── <modulename>.csv              ← Header row + one row per data key (see Test Data Management)
│       └── java/com/<org>/<product>/
│           ├── CC_TestNG.xml                     ← Full regression suite
│           └── CC_Sample_TestNG.xml              ← Smoke/sample suite
```

> **Naming convention:** every package-level folder under `com.<org>.<product>` is **flatcase** (all-lowercase, no separators — `utils`, `annotations`, `pageobjects`, `pageobjects/<modulename>`, `<modulename>tests`, `framework`, `testng`). Only `.java` file names and class names stay PascalCase (`DriverFactory.java`, `class DriverFactory`, `LoginPage.java`, `class LoginPage`). This was a deliberate, live-verified correction from an earlier per-module PascalCase layout (`Login/`, `Utils/`, `TestNG/`, ...) — see the Naming Conventions section below.

> **Live-run correction:** `credentials.properties`, `SampleTestData.json`, `testrail.properties`, and `zephyr.properties` are **no longer part of the standard `src/main/resources/` scaffold.** A real generated framework was found with all four present and zero code anywhere referencing any of them — dead scaffolding indistinguishable from real configuration until someone actually greps for a consumer. `credentials.properties` is superseded by the CSV + `@TestDataSource` pattern (see "Test Data Management" below) for a fresh Mode A scaffold; it remains legitimate only in Mode B, and only when the target project's Framework Profile already reads credentials that way. TestRail/Zephyr integration and a generic `SampleTestData.json` are speculative scaffolding for integrations/data no test case asks for — add them later, specifically, if a real requirement calls for them, rather than by default.

---

## Execution Modes (Maven Profiles)

| Profile ID | `-Ddevice=` | `RUN_ON` | `SETUP` |
|---|---|---|---|
| `android` (default) | `android` | `ANDROID_APP` | `Local` |
| `iOS` | `iOS` | `iOS_APP` | `Local` |
| `bsAndroid` | `bsAndroid` | `ANDROID_APP` | `BrowserStack` |
| `bsiOS` | `bsiOS` | `iOS_APP` | `BrowserStack` |

- `CONTEXT`: `NATIVE` (standard Appium) or `FLUTTER` (Flutter driver via Kotlin FlutterFinder)
- Run command: `mvn clean test -Ddevice=android` or `mvn clean test -Ddevice=iOS`

---

## Driver Management

- Use `DriverFactory` with `ThreadLocal<AppiumDriver>` for thread-safe parallel execution.
- `InitiateDriver` is a singleton (`getInstance()`) that reads `config.properties` and creates `AndroidDriver` or `IOSDriver` based on `RUN_ON`.
- `TestBase.setup()` calls `InitiateDriver.getInstance().createDriver()` then `setDriverForFramework(...)`.
- Never instantiate `AppiumDriver` directly in test or page classes — always call `DriverFactory.getDriverForFramework()`.

---

## BrowserStack Integration

**Generate only when the user asked for BrowserStack remote execution** (the scaffold agent's "BrowserStack" input question) — `device_conf.json`, `BrowserStackConfig.java`, and `BrowserStackAppUploader.java` have no purpose in a project that only ever runs locally. **Live-run correction:** an earlier framework shipped `device_conf.json` and a `SETUP=BrowserStack` capability description in this doc, but `InitiateDriver` only ever implemented the `Local` branch — selecting `SETUP=BrowserStack` did nothing. The mechanism below is what was actually built and wired end-to-end to close that gap.

`InitiateDriver.createDriver()` branches on `config.properties`' `SETUP`:

- **`Local`** (default): unchanged — connects to the local Appium server at `SeverAddress`, using `AppPath`/`DeviceConfig`-detected device info, exactly as documented in "Driver Management" above.
- **`BrowserStack`**: routes through `createBrowserStackDriver()`, which:
  1. Reads `config.properties`' `BSEnvironment` (mandatory when `SETUP=BrowserStack` — fail fast with a clear message if unset, don't silently fall back to `Local` behavior) and looks it up in `device_conf.json`'s `environments` map via `BrowserStackConfig.getEnvironment(key)`, yielding `device`, `os`, `runon`, and optional `deviceid`.
  2. Resolves `runon` (`ANDROID_APP`/`iOS_APP`) to decide which of `config.properties`' `AndroidAppPath`/`iOSAppPath` to upload — **not** the same `AppPath` key `Local` mode uses, since a BrowserStack run may target a different platform than whatever's configured for local runs.
  3. Calls `BrowserStackAppUploader.getOrUploadAppUrl(path, user, key)`, which uploads the binary to BrowserStack App Automate (`https://api-cloud.browserstack.com/app-automate/upload`, Basic Auth with `device_conf.json`'s `user`/`key`, multipart `file` field) and returns a `bs://<hash>` app URL. **Cached** in a local file keyed by the app binary's own last-modified timestamp, so an unchanged binary isn't re-uploaded on every run — only a changed/rebuilt app triggers a fresh upload.
  4. Builds capabilities: `platformName`/`appium:automationName` from `runon`, `appium:app` = the `bs://` URL, `appium:deviceName`/`appium:platformVersion` = the resolved `device`/`os`, and a `bstack:options` map carrying `userName`/`accessKey`/`deviceName`/`osVersion`/`projectName`/`buildName`/`sessionName` (and `deviceUuid` when `deviceid` is non-blank — BrowserStack's Dedicated/Private Device pinning; omitted for a shared-pool device, which is what every environment in this project's `device_conf.json` is by default).
  5. Connects to `https://<user>:<key>@<device_conf.json's server>/wd/hub` instead of the local `SeverAddress`.

**Platform limitation — not a bug in this uploader:** BrowserStack's real-device cloud requires a signed `.ipa` for iOS. An unsigned Simulator `.app` bundle (the shape Xcode produces for local Simulator testing) will be rejected by the upload endpoint. Document this plainly in the generation summary/README when a project's iOS build is Simulator-only — BrowserStack iOS coverage needs a separate signed build, which is outside this framework's own build pipeline. Android `.apk` files upload and run normally.

---

## Device Readiness (Emulator/Simulator Auto-Launch)

`DeviceConfig` (via `adb devices` / `idevice_id -l`) auto-detects whichever device is **already connected and booted** when the framework runs — it has no ability to boot one itself. Neither `AppiumServer`'s startup nor `InitiateDriver.createDriver()` blocks on device availability; they simply fail (or hang) if nothing answers. This means device readiness is a **generation-time and pre-run prerequisite**, checked and satisfied by `MasterQAOrchestrator/AppiumAutomationOrchestrator.md`'s Step 3b before either Phase 0 Live UI Hierarchy Verification or the first real test run — not something `DeviceConfig` itself is responsible for.

**Live-run correction:** earlier versions of this workflow simply told the user to "start an emulator/simulator" in the post-generation Next Steps and moved on — Phase 0 would then either hang against a dead Appium connection or fail outright the first time a project without a pre-existing running device was generated. The check-then-boot sequence below closes that gap.

### Android

1. **Check:** `adb devices` — any line ending in `\tdevice` (not `unauthorized`/`offline`) counts as ready. Capture its serial (e.g. `emulator-5554`) — this is the exact value `DeviceConfig.getAndroidUDID()` would itself discover at runtime, so confirming it here is just doing that check early enough to fail fast.
2. **If none ready:** `emulator -list-avds` to enumerate installed AVDs.
   - Exactly one AVD → boot it automatically: `emulator -avd <name> -no-snapshot -no-audio &` (background/detached — do not block the generation process on the emulator process itself).
   - More than one AVD and the user hasn't already named a device → ask which AVD to launch; do not guess.
   - Zero AVDs → **stop and ask** the user to create one (via Android Studio's Device Manager or `avdmanager create avd`) — this requires an installed system image this workflow cannot fetch or assume, so it is a hard stop, not a silent skip.
3. **Wait for boot completion** after launching: `adb wait-for-device`, then poll `adb shell getprop sys.boot_completed` every few seconds until it returns `1`, bounded by a timeout (120s is a reasonable default — cold AVD boot is slow). On timeout, stop and report a boot failure rather than proceeding against a half-booted emulator that will only produce confusing downstream errors.
4. Re-run `adb devices` to confirm the now-connected serial before proceeding to Phase 0.

### iOS (macOS host only — XCUITest requires macOS; there is no Linux/Windows equivalent)

1. **Check:** `xcrun simctl list devices booted` — any entry under a "Booted" state heading counts as ready. Capture its UDID, device name, and iOS version from the same listing.
2. **If none booted:** `xcrun simctl list devices available` to enumerate created-but-not-booted simulators.
   - Exactly one match for the required iOS version (or none specified — pick the newest installed iOS runtime's default iPhone) → boot it: `xcrun simctl boot <udid>`.
   - More than one plausible match and the user hasn't already named a device → ask which simulator to boot; do not guess.
   - Zero simulators exist for the required runtime → attempt `xcrun simctl create "<name>" "<device-type>" "<runtime>"` only when exactly one installed runtime and one plausible device type exist (`xcrun simctl list devicetypes`/`list runtimes`); otherwise stop and ask the user to create one via Xcode — auto-creating against an ambiguous runtime/device-type set risks silently picking the wrong OS version.
3. **Wait for boot completion:** poll `xcrun simctl list devices` until the target UDID's state reads `Booted`, bounded by a timeout (60s is a reasonable default — simulator boot is faster than a physical/AVD Android boot). On timeout, stop and report a boot failure.
4. `open -a Simulator` afterward is optional (brings the simulator window to the foreground) — not required for Appium to drive it headlessly, but conventional so the user can visually follow along.

### Scope

This check runs once per generation session (and should be re-run before any subsequent live test execution, not just the original generation) — it is not baked into the generated framework's own code as a runtime dependency. `AppiumServer`/`InitiateDriver` remain exactly as documented above: they assume a ready device and fail fast if one isn't there, matching every other Appium framework's convention. Auto-launch is an **agent-time convenience**, not a feature of the shipped test suite.

---

## Live UI Hierarchy Verification

See `03-standards/locator-standards.md`'s "Live DOM Verification" section for the full rationale and cross-stack process — this section covers only this stack's mechanics. There is no browser DOM in a native mobile app; the equivalent artifact is the UI element hierarchy Appium already exposes via `driver.getPageSource()`. This is the same primitive the Appium MCP Server's "Get Page Source" tool wraps for AI-driven exploration (see the `appium-mcp`/`@gavrix/appium-mcp` reference workflow: start session → launch app → capture page source/screenshot per screen → extract selectors) — this framework does the same thing directly via the Appium Java client, since the deliverable is a self-contained Maven/TestNG project, not an MCP-tool-driven session.

### Prerequisite — this cannot run without a real app to launch

Unlike a web base URL, `AppPath` (APK/IPA) or an installed app's `AndroidAppPackage`+`AndroidAppActivity`/`BundleId` is almost never available from a PRD by default. **This information must be collected during Step 2/3 of `MasterQAOrchestrator/AppiumAutomationOrchestrator.md` — before any scaffolding — not deferred to a post-generation "edit config.properties" instruction.** If it isn't provided by the user and isn't documented anywhere in `RequirementAnalysis.md`, stop and ask; there is no default, no placeholder, and no way to guess a binary's location or an installed package's identifier.

A confirmed app identity is not enough on its own — this process also needs a **ready** (connected and booted, not just installed) emulator/simulator/device to launch it on. See "Device Readiness" above and `MasterQAOrchestrator/AppiumAutomationOrchestrator.md`'s Step 3b — do not attempt this process against a device that hasn't been confirmed ready.

### Process

1. **Start a real Appium session** against a running emulator/simulator or connected device, using the confirmed `AppPath`/`AndroidAppPackage`+`AndroidAppActivity`/`BundleId` from the prerequisite step, and launch the app.
2. **Navigate to the module's screen** — performing whatever setup flow is required (e.g., logging in first, using credentials confirmed per `locator-standards.md`'s prerequisite step and the already-verified locators from a prior, already-verified module — verify modules in dependency order, e.g. splash/login before anything gated behind it, exactly as for web).
3. **Capture the UI hierarchy**: call `driver.getPageSource()` and parse the returned XML into a candidate list:

```java
String pageSourceXml = driver.getPageSource();
DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
Document doc = builder.parse(new InputSource(new StringReader(pageSourceXml)));
NodeList nodes = doc.getElementsByTagName("*");
List<Map<String, String>> candidates = new ArrayList<>();
for (int i = 0; i < nodes.getLength(); i++) {
    Element el = (Element) nodes.item(i);
    Map<String, String> candidate = new LinkedHashMap<>();
    candidate.put("class", el.getAttribute("class"));
    candidate.put("resourceId", el.getAttribute("resource-id"));   // Android
    candidate.put("contentDesc", el.getAttribute("content-desc")); // Android accessibility id
    candidate.put("text", el.getAttribute("text"));                // Android
    candidate.put("name", el.getAttribute("name"));                // iOS accessibility id
    candidate.put("label", el.getAttribute("label"));              // iOS
    candidate.put("value", el.getAttribute("value"));              // iOS
    candidate.put("type", el.getAttribute("type"));                // iOS element type
    candidate.put("clickable", el.getAttribute("clickable"));
    candidates.add(candidate);
}
```

Optionally also capture a screenshot (`driver.getScreenshotAs(OutputType.FILE)`) alongside the page source for the same screen — useful for the generation summary/documentation, though not required for locator matching itself.

4. **For every locator about to be written into a Page Object Repository:** if a documented value (accessibility ID, resource-id) matches a real candidate exactly, use it; if not, use the actual best-matching candidate's real value and record a discrepancy note (module, documented value, actual value, snapshot date) instead of silently substituting; if nothing plausible exists, stop and flag it as a blocking gap rather than emitting a `// TODO: Replace with actual locator` placeholder and moving on. A generated Page Object Repository should only ever contain a `TODO` placeholder when a real device/emulator session genuinely could not be established for that screen (e.g., the screen is behind a gate that couldn't be reached) — and that exception must be explained in the generation summary, not left silent.
5. **Persist both the raw XML and the parsed candidate list** to `docs/dom-snapshots/<module>.xml` and `docs/dom-snapshots/<module>.json` (relative to the project root) for traceability, mirroring the same convention used by every other stack.
6. This runs **once per module** during initial Page Object generation, not repeated on every subsequent run, per `locator-standards.md`'s process step 5.

---

## Page Object Model

### Page Action Classes

- Extend `FlutterBase` when `CONTEXT=FLUTTER`.
- Extend `Base` when `CONTEXT=NATIVE`.
- Constructor calls `super(DriverFactory.getDriverForFramework())` and instantiates the matching `PageObjectRepository`.
- Only business actions here — no raw locator references.

```java
public class LoginPage extends FlutterBase {
    private LoginPageObjectRepository lp;

    public LoginPage() {
        super(DriverFactory.getDriverForFramework());
        lp = new LoginPageObjectRepository(driver);
    }

    public void login(String email, String password) {
        waitUntillPwdFieldIsVisible();
        enterEmail(email);
        enterPassword(password);
        clickSignInButton();
    }
}
```

### Page Object Repository (Locators)

- **Native (non-Flutter):** Use `@AndroidFindBy` + `@iOSXCUITFindBy` annotations with `AppiumFieldDecorator` (via `Object_Base`).
- **Flutter:** Use `FlutterFinder.bySemanticsLabel()`, `.text()`, `.byValueKey()`, `.byType()`, etc.
- Never use raw `driver.findElement()` in repository classes.

```java
// Native example
public class LoginPageObjectRepository extends Object_Base {
    @AndroidFindBy(accessibility = "username_field")
    @iOSXCUITFindBy(accessibility = "username")
    public WebElement usernameField;
}

// Flutter example
public class LoginPageObjectRepository {
    public FlutterElement emailField = find.bySemanticsLabel("textFieldEmailAddress");
    public FlutterElement passwordField = find.bySemanticsLabel("textFieldPassword");
    public FlutterElement signInButton = find.text("Log in");
}
```

---

## Pages Registry

- `Pages.java` at the package root exposes static factory methods for all page classes.
- Test classes use `Pages.LoginPage()` — never `new LoginPage()` directly.

```java
public class Pages {
    private static <T extends FlutterBase> T getPage(Class<T> pageType) {
        try { return pageType.newInstance(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    public static LoginPage LoginPage() { return getPage(LoginPage.class); }
}
```

---

## Test Classes

- Extend `TestBase` (which extends `DriverFactory`).
- Use TestNG's own `@Test(testName=..., description=..., groups={...})` — no `@Description`/`@Epic`/`@Feature` Allure annotations; those were dropped along with Allure itself (see "Reporting" below) after confirming they were never actually present in any live-generated framework either.
- Read credentials/business data via the CSV + `@TestDataSource` pattern (see "Test Data Management" below) for a fresh Mode A scaffold — not `credentials.properties`/`PropertyReader`, which is Mode-B-only (see the Project Structure section's live-run correction note above for why).
- Wrap test body in try/catch; on exception call `Assert.fail(...)`.
- Use `Pages.<Module>()` to get page instances.

```java
@Test(testName = "Login", description = "TC-001: Verify user can log in with valid credentials", groups = {"smoke"})
@TestDataSource(value = "src/test/resources/testdata/login.csv", key = "ValidUser")
public void verifyLogin(Method testMethod) {
    try {
        Map<String, String> data = TestDataArgumentsProvider.resolve(testMethod);
        Pages.LoginPage().login(data.get("Email"), data.get("Password"));
    } catch (Exception e) {
        Assert.fail("Login test failed: " + e.getMessage());
    }
}
```

---

## TestNG XML Suite

- Two XMLs per project: `CC_TestNG.xml` (full regression) and `CC_Sample_TestNG.xml` (smoke/sample).
- Listeners registered: `TestListener`, `MobileEmailReport`.
- Use `parallel="tests"` or `parallel="classes"` for parallel device execution.
- Default suite file: `src/test/java/com/<org>/<product>/CC_TestNG.xml`.

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="MobileRegressionSuite" parallel="tests" thread-count="2">
    <listeners>
        <listener class-name="com.<org>.<product>.testng.TestListener"/>
        <listener class-name="com.<org>.<product>.testng.MobileEmailReport"/>
    </listeners>
    <test name="LoginTests" preserve-order="true">
        <classes>
            <class name="com.<org>.<product>.LoginTests.LoginTests"/>
        </classes>
    </test>
</suite>
```

---

## config.properties

| Key | Description |
|---|---|
| `AppPath` | Absolute path to APK/IPA (no extension) |
| `AndroidAppPackage` | Android app package name |
| `AndroidAppActivity` | Android launcher activity |
| `BundleId` | iOS bundle ID |
| `RUN_ON` | `ANDROID_APP` / `iOS_APP` (set by Maven profile) |
| `CONTEXT` | `NATIVE` / `FLUTTER` (set by Maven profile) |
| `SETUP` | `Local` / `BrowserStack` (set by Maven profile) |
| `SeverAddress` | Appium server URL (default: `http://0.0.0.0:4723`) — unused when `SETUP=BrowserStack` |
| `ENV` | Environment: `dev` / `integration` / `uat` / `preprod` / `prod` |
| `BSEnvironment` | **BrowserStack only.** Names one key from `device_conf.json`'s `environments` map (e.g. `samsung_s10(9.0)`) — see "BrowserStack Integration" below |
| `AndroidAppPath` / `iOSAppPath` | **BrowserStack only.** Real local app binary paths `BrowserStackAppUploader` uploads from, keyed by the selected environment's `runon` — separate from `AppPath` because a `SETUP=BrowserStack` run may target a different platform than the local `SETUP=Local` default |

---

## Test Data Management (data-driven `@Test` methods)

`config.properties` is environment/device config only (app paths, device names, server address) — it is not where business test data (credentials, sample input strings, dropdown selections) belongs. **Live-run correction:** a generated framework was found with every such value hardcoded directly as a string literal inside `@Test` method bodies (`enterPassword("Passw0rd!")`, `typeText("Hello WDIO")`) — functionally working, but meaning the same literal had to be found and edited in Java source whenever test data changed, and offering no separation between "what the test does" and "what data it uses."

The validated fix, ported from a reference Java project that had built this exact mechanism but never actually wired it into a test method — a CSV-backed, annotation-driven data resolver:

- **`annotations/TestDataSource.java`** — a `@Retention(RUNTIME)`, `@Target(METHOD)` annotation: `value` (CSV file path), `type` (default `"csv"`), `key` (the row to select).
- **`utils/CSVLoader.java`** — `loadRowByKey(path, key)` reads a CSV with a header row and returns the matching row as `Map<String, String>`. Tolerant of blank trailing columns (`split(",", -1)`, not the default `split(",")` which silently drops them).
- **`utils/TestDataArgumentsProvider.java`** — `resolve(Method testMethod)` reads the method's `@TestDataSource` annotation and returns `CSVLoader.loadRowByKey(source.value(), source.key())`.
- **Test method wiring** — declare a `java.lang.reflect.Method testMethod` parameter on the `@Test` method; TestNG auto-injects the currently-running method, so the test body needs no reflection lookup of its own:
  ```java
  @Test(description = "...")
  @TestDataSource(value = "src/test/resources/testdata/login.csv", key = "ValidUser")
  public void verifyValidCredentialsLoginSucceeds(Method testMethod) {
      Map<String, String> data = TestDataArgumentsProvider.resolve(testMethod);
      Pages.LoginPage().enterPassword(data.get("Password"));
      ...
  }
  ```
- **CSV files live under `src/test/resources/testdata/`** (test-scoped, not `src/main/resources/`) — one file per module/concern (`login.csv`, `forms.csv`), header row + one row per named data set (`Key` column), e.g.:
  ```csv
  Key,Password
  ValidUser,Passw0rd!
  ```
- Only wire this up for values that are genuinely business/scenario data reused across the test suite (credentials, canonical sample inputs) — an arbitrary one-off literal in a single negative/boundary-case test (e.g. a malformed-input string used exactly once) does not need externalizing.

This is the JS-stack's `data/testdata.json` + `TestData.js` pattern's Java/TestNG equivalent — see `03-standards/webdriverio-javascript-standards.md`'s "Test Data" section for the sibling mechanism, including its own live-run lesson (a data file with no consumer is dead weight, not test-data management).

---

## device_conf.json

**Generate only for a project that asked for BrowserStack** — see "BrowserStack Integration" above for the full mechanism this file feeds.

- Contains BrowserStack device matrix under `"environments"`, keyed by a name referenced from `config.properties`' `BSEnvironment` (e.g. `"samsung_s10(9.0)"`).
- Each entry has: `device`, `os`, `runon` (`ANDROID_APP`/`iOS_APP`), `deviceid` (blank unless pinning a specific Dedicated/Private Device).
- `server`, `user`, `key` at root level for BrowserStack credentials — real values, not the `<bs_username>`/`<bs_access_key>` placeholders, once the user actually supplies them; a live BrowserStack session cannot be established without them.

---

## Logging

- Use `LogManager.getLogger(ClassName.class.getName())` — always Log4j2.
- Log entry at start of each public method: `logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed")`.
- Log errors with full exception: `logger.error("Message", exception)`.
- Log4j2 config: `src/main/resources/Log4j2.xml`.

---

## Reporting

**Live-run correction:** every prior version of this section documented an Allure-based reporting stack (`allure-maven`/`allure-attachments` dependencies, `@Description`/`@Epic`/`@Feature`/`@Story`/`@Step` annotations, `target/allure-results`/`target/allure-reports`, an `EmailableTestNGReport` `IReporter`). None of it was ever actually present in a live-generated framework — the real, live-run-verified `TestListener.java` only ever did log4j2 logging plus a raw screenshot-to-`Screenshots/`, with zero Allure dependency in `pom.xml` and zero Allure annotations anywhere in a generated test class. The documented design and the shipped code had silently diverged. This section now documents the stack that is actually wired end-to-end and produces a real, openable report, ported directly from a reference Java project's proven `ExtentReportManager`/`WebEmailReport` pair (same rationale as the CSV test-data pattern borrowed from that project).

- **`ExtentReportManager`** (`reports/ExtentReportManager.java`) — static `ExtentReports` instance + `ThreadLocal<ExtentTest>`, one entry per `@Test` method. `initReport()` is idempotent (only creates the report once); `startTest`/`pass`/`fail`/`skip`/`endTest` are called by `TestListener`, not by test classes themselves — no per-test-method boilerplate or annotation is needed.
- **Screenshots on failure** — `TestListener.onTestFailure()` keeps its existing raw screenshot capture to `Screenshots/<methodName>.png` (via `((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE)`), and additionally passes that same path into `ExtentReportManager.fail(throwable, screenshotPath)` so the failure screenshot is embedded inline in the HTML report, not only saved separately.
- **`MobileEmailReport`** (`testng/MobileEmailReport.java`, `ISuiteListener`) — owns `ExtentReportManager.initReport()`/`.flush()` (called from `onStart`/`onFinish`, since this stack's `TestBase` lifecycle is per-`@Test`-method, not per-suite — there is no `@BeforeSuite`/`@AfterSuite` hook to put these calls in instead), and writes a lightweight custom HTML summary table (`test-output/email-report.html`) with pass/fail/skip counts per `<test>` block.
- **Output:** `test-output/ExtentReport/<ProjectName>Report.html` (the full interactive Extent report) and `test-output/email-report.html` (the at-a-glance suite summary) — both under `test-output/`, gitignored, generated fresh on every `mvn test` run. No separate CLI, Maven plugin, or Jenkins plugin install is needed to view either file.
- No `@Description`/`@Epic`/`@Feature`/`@Story`/`@Step` annotations on test methods — TestNG's own `@Test(testName=..., description=...)` carries equivalent metadata, and `ExtentReportManager.startTest(className, methodName)` (driven automatically by `TestListener`) is what actually produces the report entry.

---

## Gestures (FlutterBase)

| Method | Usage |
|---|---|
| `tapGesture(element)` | Flutter long tap |
| `scroll(element, Direction, point)` | Directional scroll |
| `scrollToElement(anchor, target, Direction)` | Scroll until target visible |
| `scrollIntoView(element)` | Scroll element into view |
| `DragGesture(from, to)` | Drag from one Flutter element to another |
| `isVisisble(element)` | Flutter waitFor with 5s timeout |

## Mobile Actions (Base)

| Method | Usage |
|---|---|
| `waitUntilElementIsVisible(el)` | WebDriverWait visibilityOf |
| `waitForElementToBeInvisible(el)` | Wait for element to disappear |
| `hideKeyboard()` | Platform-aware keyboard dismiss |
| `tapByCoordinates(x, y)` | TouchAction tap |
| `swipeFromElementToElement(from, to)` | Swipe gesture |
| `minimizeApplicationAndRestore(time)` | Background app for N seconds |
| `isAndroid()` | Returns true if RUN_ON contains ANDROID_APP |
| `navigateBack()` | driver.navigate().back() |

---

## Naming Conventions

| Artifact | Convention |
|---|---|
| Page action class | `<ScreenName>Page.java` (e.g., `LoginPage.java`) |
| Page object repository | `<ScreenName>PageObjectRepository.java` |
| Test class | `<ModuleName>Tests.java` |
| Test method | `verify<Action><Subject>()` (e.g., `verifyLoginWithValidCredentials()`) |
| Package (main, page objects) | `com.<org>.<product>.pageobjects.<modulename>/` (e.g., `pageobjects.login`) |
| Package (main, utils/annotations) | `com.<org>.<product>.utils/`, `com.<org>.<product>.annotations/` |
| Package (test, listeners/base) | `com.<org>.<product>.testng/`, `com.<org>.<product>.framework/` |
| Package (test, test classes) | `com.<org>.<product>.<modulename>tests/` (e.g., `logintests`) |

**Package-level folders vs. file/class names:** every folder under `com.<org>.<product>` is **flatcase** — all-lowercase, no separators, no PascalCase, no underscores (`pageobjects`, `pageobjects/login`, `logintests`, `utils`, `annotations`, `framework`, `testng`). Only the `.java` file name and the class it declares stay PascalCase (`LoginPage.java` / `class LoginPage`, `LoginTests.java` / `class LoginTests`). **Live-run correction:** an earlier generated framework used per-module PascalCase folders directly under the base package (`Login/`, `Utils/`, `TestNG/`, `LoginTests/`) with Page Objects living unnested at that level; this was restructured after review into the `pageobjects/<modulename>/` nesting shown here, then every package-level folder (not just the page-object ones) was renamed to flatcase — a two-step rename is required on case-insensitive filesystems (macOS APFS, Windows) since a same-case-differing `mv`/rename silently no-ops (rename to a temp name first, then to the final lowercase name).

---

## CI — Jenkins

- Parameterized `Jenkinsfile` with `DEVICE` (android/iOS/bsAndroid/bsiOS) and `ENV` parameters.
- Command: `mvn clean test -Ddevice=${DEVICE} -Denv=${ENV}`
- Post-build: archive `target/surefire-reports/**`, `Screenshots/**`, and `test-output/**` (no separate Allure report-generation step or Jenkins plugin — `mvn test` itself already produces the Extent report and HTML summary under `test-output/`, see "Reporting" above).

---

## Retry

- `RetryAnalyzer` registered via `TestListener.transform()` implementing `IAnnotationTransformer`.
- All test methods get retry automatically; max retry count configured in `RetryAnalyzer`.

---

## Self-Healing Locators

Every project generated for this stack must include a self-healing layer so a stale/renamed locator degrades to an automatic, logged recovery instead of an immediate hard failure. This is **mandatory scaffold**, not optional — ported from `playwright-java-standards.md`'s "Self-Healing Locators" (same scoring algorithm, same cache/log convention), adapted for the Android UI hierarchy in place of the DOM.

Unlike the Playwright-Java stack, this stack's canonical Page Object Repository pattern uses `@AndroidFindBy`-annotated fields resolved implicitly by `PageFactory`/`AppiumFieldDecorator` (see "Page Object Repository Pattern — Native" above) — there is no single wrapper object (like Playwright-Java's `PlaywrightLocator`) that every locator is constructed through, so healing cannot be baked in with zero call-site changes. Instead, healing lives in **`Base`'s action-wrapper methods**, and each Page Action class's interaction methods route through them with a short, descriptive hint string instead of calling `element.click()`/`sendKeys()` directly:

```java
// utils/Base.java
public void clickWithHeal(WebElement element, String hint) {
    try {
        waitUntilElementIsClickable(element);
        element.click();
    } catch (RuntimeException originalError) {
        healOrRethrow(hint, originalError).click();
    }
}

public void typeWithHeal(WebElement element, String hint, String text) {
    try {
        waitUntilElementIsVisible(element);
        element.clear();
        element.sendKeys(text);
    } catch (RuntimeException originalError) {
        WebElement healed = healOrRethrow(hint, originalError);
        healed.clear();
        healed.sendKeys(text);
    }
}
```

Page Action classes call these instead of the raw wait-then-act pattern: `clickWithHeal(repo.loginButton, "Login Button")`, `typeWithHeal(repo.inputEmail, "Email Input", email)`. READ methods (`getText()`, `isDisplayed()`-style checks, assertion targets) deliberately do **not** gain this wrapping — silently healing an assertion's target risks turning a real failure into a false pass. Gesture-based interactions (swipe/drag endpoints) are also out of scope for this pass — healing a single element locator is a different problem than healing two coordinates, and the reference Playwright implementations don't address it either.

### Module: `src/main/java/.../utils/SelfHealer.java`

```java
public final class SelfHealer {
    private static final long CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
    private static final double CONFIDENCE_THRESHOLD = 0.75;
    private static final int MAX_SNAPSHOT_ELEMENTS = 150;

    public record HealResult(boolean success, WebElement element, double confidence) {}

    public static HealResult findHealedLocator(AppiumDriver driver, String hint, Exception originalError) { /* ... */ }
}
```

Algorithm (identical scoring model to the web-stack reference implementations, Android-adapted collection/resolution):

1. **Candidate collection** — parse `driver.getPageSource()` (the UiAutomator2 XML hierarchy) with a lightweight regex element/attribute scan instead of a DOM query; keep only elements carrying at least one of `content-desc` / `resource-id` / `text` (skip pure layout containers), capped at `MAX_SNAPSHOT_ELEMENTS`.
2. **Tokenize** the hint into a lower-case word set (≥3 chars, deduped) — hint-only, matching the Playwright-Java rationale (a stable, human-readable name is already the intent behind every element).
3. **Score** each candidate by precision/recall (F1) over `content-desc` + `resource-id` (local name after the last `/`) + `text` — deliberately **excluding** the widget `class` (e.g. `android.widget.Button`): structural noise, not semantic identity, exactly like excluding CSS `class` on the web side. Bonus `+0.15` if `content-desc` directly contains a matched keyword (this app's strongest, most intentional identity signal — the Appium equivalent of `data-testid`/`aria-label`); `+0.10` for `resource-id`.
4. **Resolve** the best candidate (score ≥ `CONFIDENCE_THRESHOLD`) to a real `By`, in priority order: `content-desc` → `AppiumBy.accessibilityId` → `resource-id` → `By.id` (namespaced `pkg:id/name`) or `By.xpath("//*[@resource-id='...']")` (bare/unnamespaced — see the locator-standards.md note on `By.id` silently mis-prefixing unnamespaced resource-ids) → `text` → `By.xpath("//*[@text='...']")`.
5. **Validate** the resolved `By` actually matches something on screen right now (`driver.findElements(by)` non-empty) before trusting it.
6. **Cache** successful heals for `CACHE_TTL_MS`, keyed by `<currentActivity>|<hint>` (the closest native analogue to the web stack's `<pageUrl>|<locator>` key, since a native screen has no URL), in `healing-cache.json` at the project root (one level above `framework/` — the Maven module directory — matching the live `07-outputs/Automation/Framework/` output-location convention in `MasterQAOrchestrator/AppiumAutomationOrchestrator.md`, exactly like the Playwright-Java convention).
7. **Log** every successful heal to `healing-report.log` at the same location.

---

## Prohibited Patterns

- Never hard-code device UDIDs, credentials, or app paths in test or page classes.
- Never use `Thread.sleep()` as a primary wait strategy — use `WebDriverWait`.
- Never mix `FlutterBase` and `Object_Base` in the same page class.
- Never access `driver` directly in test classes — use `Pages` registry only.
- Never duplicate locators across multiple repository classes.
- Never emit a `// TODO: Replace with actual locator` placeholder when a real device/emulator session was available to verify it — see "Live UI Hierarchy Verification" above. A `TODO` is only acceptable when a session genuinely could not be established, and that exception must be explained in the generation summary.
- Never call `element.click()`/`sendKeys()` directly from a Page Action class's interaction methods — route through `Base.clickWithHeal(...)`/`typeWithHeal(...)` so self-healing applies (see "Self-Healing Locators" above). Direct calls are still correct for READ-only checks (`getText()`, visibility assertions) and gesture endpoints.
