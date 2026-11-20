# Appium Java Automation Framework Template

> **Reference implementation:** `/Users/ostan/Documents/Automation/Mobile_App_Automation_Template`
> All generated frameworks must replicate this structure exactly unless the user specifies otherwise.

---

## Framework Identity

| Property | Value |
|---|---|
| Language | Java 17+ |
| Test Framework | TestNG — always verify the actual latest via `npm`-equivalent Maven Central lookup at generation time (see "Dependency Version Policy" below); never copy the number from this row |
| Build | Maven |
| Appium Client | io.appium:java-client — always verify the actual latest at generation time; never copy a number from this row |
| Selenium | Individually pinned via `dependencyManagement` (api/remote-driver/support/http/json/manager/os) to the **floor** of java-client's own declared Selenium range — never `selenium-java`, never left unpinned. See "Dependency Version Policy" below — this is a mandatory, not optional, step. |
| Reporting | ExtentReports 5.1.2 + a custom TestNG-suite-level HTML summary (`MobileEmailReport`) — see "Reporting" in `03-standards/appium-java-standards.md` for why Allure was dropped |
| Logging | Log4j2 — always verify the actual latest at generation time |
| Flutter Support | Kotlin (optional, when CONTEXT=FLUTTER) — always verify the actual latest at generation time |

---

## Dependency Version Policy

**Mandatory, not optional — every dependency version in this template is a placeholder to be re-verified at generation time, never copied as-is.** This mirrors `03-standards/webdriverio-javascript-standards.md`'s "Latest-Dependency-Version Policy" for the JS sibling stack, with one Java-specific addition below that has no JS equivalent.

1. **Verify the actual latest version** of every dependency (`java-client`, `testng`, `extentreports`, `log4j-core`, etc.) via Maven Central before writing `pom.xml` — e.g. `curl -s "https://search.maven.org/solrsearch/select?q=g:%22io.appium%22+AND+a:%22java-client%22&core=gav&rows=1&wt=json"`. Do not copy the version numbers shown elsewhere in this template; they are illustrative, not current.

2. **Pin Selenium explicitly — this step is unique to this stack and is the single most likely thing to break a fresh scaffold if skipped.** `io.appium:java-client` declares its own Selenium dependency as an **open-ended version range** (e.g. `[4.26.0, 5.0)`), not a fixed version. Left unpinned, Maven resolves that range to whatever the newest Selenium release is *at build time* — which is very often a version Selenium has since refactored in ways that break the specific `java-client` release you're using, even though nothing in your own code changed. **Live-run confirmed failure modes on this exact pattern:**
   - `NoClassDefFoundError: org/openqa/selenium/ContextAware` (or `org.openqa.selenium.html5.LocationContext`) — Selenium removed these classes in a recent release; `AndroidDriver`/`IOSDriver`'s class hierarchy still references them.
   - `IllegalAccessError` inside `AppiumCommandExecutor` reflectively touching a Selenium-internal field (e.g. `HttpCommandExecutor.commandCodec`) that a different Selenium release made inaccessible in an incompatible way.

   **The fix:** find the exact **floor** of `java-client`'s declared Selenium range (`curl -s "https://repo1.maven.org/maven2/io/appium/java-client/<version>/java-client-<version>.pom" | grep -A2 "selenium-api\|selenium-remote-driver"`), then pin every Selenium sub-artifact to that floor version explicitly via `dependencyManagement` (not a plain sibling `<dependency>` — a plain dependency does **not** win against a version range; `dependencyManagement` does). Pin all of: `selenium-api`, `selenium-remote-driver`, `selenium-support`, `selenium-http`, `selenium-json`, `selenium-manager`, `selenium-os` — pinning only some of the seven leaves the rest transitively resolved at a mismatched newer version, which reproduces the same class of failure. **The floor itself, not "one version below the floor,"** is what was actually tested by the `java-client` maintainers — one version below still failed live with the `IllegalAccessError` above even though it also contained `ContextAware`. Never declare `selenium-java` as a dependency in this stack at all — it pulls unrelated desktop browser drivers and its own independently-versioned `selenium-api`/`selenium-support`, which conflicts with `java-client`'s pinned versions the same way an unpinned range does.

3. **Verify the resolved versions, not just the declared ones:** run `mvn dependency:tree | grep -i selenium` after writing `pom.xml` and confirm every `selenium-*` artifact resolves to the same pinned version — a partial pin (some artifacts pinned, others still ranged) is worse than no pin, since it's silent until a specific method call touches the mismatched internals.

4. **Verify with a live driver-construction test, not just `mvn compile` / `mvn test-compile`.** The `ContextAware` failure above is a compile-time symptom, but the `IllegalAccessError` only surfaces when a real `AndroidDriver`/`IOSDriver` session is actually constructed at runtime — a clean compile is not sufficient evidence that the dependency pairing works. Run at least one real test through Phase 0's Live UI Hierarchy Verification (or a throwaway smoke test) before considering the scaffold's dependency setup validated.

---

## pom.xml — Key Sections

### Properties

```xml
<properties>
    <defaultSuiteFiles>src/test/java/com/<org>/<product>/CC_TestNG.xml</defaultSuiteFiles>
    <suiteFile>${defaultSuiteFiles}</suiteFile>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <skipTests>false</skipTests>
    <extentreports.version><!-- verify via Maven Central --></extentreports.version>
    <testng.version><!-- verify via Maven Central --></testng.version>
    <aspectj.version><!-- verify via Maven Central --></aspectj.version>
    <java.client.appium><!-- verify via Maven Central --></java.client.appium>
    <selenium.version><!-- the FLOOR of java-client's declared Selenium range — see "Dependency Version Policy" above --></selenium.version>
    <log4j.version><!-- verify via Maven Central --></log4j.version>
    <kotlin.version><!-- verify via Maven Central; Flutter context only --></kotlin.version>
</properties>
```

### Selenium Version Pin (mandatory — see "Dependency Version Policy" above)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-api</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-remote-driver</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-support</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-http</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-json</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-manager</artifactId>
            <version>${selenium.version}</version>
        </dependency>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-os</artifactId>
            <version>${selenium.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Maven Profiles

```xml
<profiles>
    <profile>
        <id>android</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <properties>
            <RunOn.value>ANDROID_APP</RunOn.value>
            <Context.value>NATIVE</Context.value>
            <Setup.value>Local</Setup.value>
        </properties>
    </profile>
    <profile>
        <id>iOS</id>
        <properties>
            <RunOn.value>iOS_APP</RunOn.value>
            <Context.value>NATIVE</Context.value>
            <Setup.value>Local</Setup.value>
        </properties>
    </profile>
    <profile>
        <id>bsAndroid</id>
        <properties>
            <RunOn.value>ANDROID_APP</RunOn.value>
            <Context.value>NATIVE</Context.value>
            <Setup.value>BrowserStack</Setup.value>
        </properties>
    </profile>
    <profile>
        <id>bsiOS</id>
        <properties>
            <RunOn.value>iOS_APP</RunOn.value>
            <Context.value>NATIVE</Context.value>
            <Setup.value>BrowserStack</Setup.value>
        </properties>
    </profile>
</profiles>
```

> **Flutter projects:** Set `<Context.value>FLUTTER</Context.value>` in each profile when the app is built with Flutter.

### Core Dependencies

```xml
<!-- Appium — brings selenium-api/selenium-remote-driver/selenium-support transitively,
     pinned to the dependencyManagement block above. Do NOT also declare selenium-java (see
     "Dependency Version Policy" above for why). -->
<dependency>
    <groupId>io.appium</groupId>
    <artifactId>java-client</artifactId>
    <version>${java.client.appium}</version>
</dependency>
<!-- TestNG -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>${testng.version}</version>
</dependency>
<!-- Reporting: ExtentReports -->
<dependency>
    <groupId>com.aventstack</groupId>
    <artifactId>extentreports</artifactId>
    <version>${extentreports.version}</version>
</dependency>
<!-- Logging -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>${log4j.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>${log4j.version}</version>
</dependency>
<!-- JSON -->
<dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
</dependency>
<!-- HTTP -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.2.0</version>
</dependency>
<!-- Utilities -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
<!-- Kotlin (only for Flutter projects) -->
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jdk8</artifactId>
    <version>${kotlin.version}</version>
</dependency>
<!-- Javassist (for TestNG priority interceptor) -->
<dependency>
    <groupId>javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.12.1.GA</version>
</dependency>
```

---

## Source Files to Generate

### `src/main/java/com/<org>/<product>/utils/DriverFactory.java`

> **Naming convention:** every package-level folder under `com.<org>.<product>` is **flatcase** (all-lowercase, no separators — `utils`, `annotations`, `pageobjects`, `pageobjects/<modulename>`, `<modulename>tests`, `framework`, `testng`). Only `.java` file names and class names stay PascalCase (`DriverFactory.java`, `class DriverFactory`). This was a deliberate, live-verified correction — see `03-standards/appium-java-standards.md`'s Naming Conventions section.

```java
package com.<org>.<product>.utils;
import io.appium.java_client.AppiumDriver;

public class DriverFactory {
    public static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static AppiumDriver getDriverForFramework() { return driver.get(); }
    public static void setDriverForFramework(AppiumDriver localdriver) { driver.set(localdriver); }
}
```

---

### `src/main/java/com/<org>/<product>/utils/Base.java`

Extends `DriverFactory`. Contains:
- `public static AppiumDriver driver = getDriverForFramework()`
- `isElementVisible(WebElement)` — try/catch, returns boolean
- `waitUntilElementIsVisible(WebElement)` — WebDriverWait 90s
- `waitForElementToBeInvisible(WebElement)`
- `waitUntilElementHasText(WebElement, String)`
- `isAndroid()` — reads `RUN_ON` from config.properties
- `hideKeyboard()` — platform-aware (iOS: click AccessibilityId "Done", Android: hideKeyboard())
- `tapByCoordinates(int x, int y)` — TouchAction
- `swipeFromElementToElement(WebElement from, WebElement to)` — TouchAction press+move+release
- `navigateBack()`, `navigateRefresh()`
- `sleep(long)`, `sleep(int)`
- `minimizeApplicationAndRestore(int time)`
- `checkOS()` — returns adb path for Windows/Linux, null for Mac
- `generateRandomString(int)`, `generateAlphaNumericString(int)`, `generateRandomMobileNumber()`, `generateRandomEmail()`, `generateRandomNumber()`
- `executeCommand(String[])`, `turnOffWifi()`, `turnOnWifi()`

---

### `src/main/java/com/<org>/<product>/utils/Object_Base.java`

```java
public class Object_Base {
    public long wait = 10;
    public Object_Base(AppiumDriver driver) {
        FieldDecorator fieldDecorator = new AppiumFieldDecorator(driver, Duration.ofSeconds(wait));
        PageFactory.initElements(fieldDecorator, this);
    }
}
```

---

### `src/main/java/com/<org>/<product>/utils/InitiateDriver.java`

Singleton. `createDriver()` method reads `config.properties`' `SETUP` and branches:

- **`SETUP=Local`** (`createLocalDriver()`):
  1. Calls `DeviceConfig.getInstance().getDeviceID(runON)` / `getDeviceName(runON)` / `getPlatformVersion(runON)`
  2. Sets `automationName` based on CONTEXT (`FLUTTER` → `"FLUTTER"`, Android → `"UiAutomator2"`, iOS → `"XCUITest"`)
  3. Creates `AndroidDriver`/`IOSDriver` against the local `SeverAddress`
- **`SETUP=BrowserStack`** (`createBrowserStackDriver()`) — only present when the project asked for BrowserStack; see `03-standards/appium-java-standards.md`'s "BrowserStack Integration" section for the full mechanism:
  1. Reads `config.properties`' `BSEnvironment`, resolves it via `BrowserStackConfig.getEnvironment(key)` against `device_conf.json`
  2. Uploads/reuses (via `BrowserStackAppUploader`) the platform-matching `AndroidAppPath`/`iOSAppPath` binary, getting back a `bs://` app URL
  3. Builds capabilities including a `bstack:options` map (`userName`/`accessKey`/`deviceName`/`osVersion`/`deviceUuid` when pinned) and connects to `https://<user>:<key>@<server>/wd/hub`

Both branches expose `getAppiumDriver()` to return the created driver.

---

### `src/main/java/com/<org>/<product>/utils/DeviceConfig.java`

Singleton. Methods:
- `getDeviceID(String runON)` — routes to `getAndroidUDID()` or `getiOSUDID()`
- `getPlatformVersion(String runON)` — routes to `getAndroidOsversion()` or `getiOSPlatformVersion()`
- `getDeviceName(String runON)` — routes to `getAndroidDeviceName()` or `getiOSDeviceName()`
- Private methods use `adb devices`, `adb devices -l`, `adb shell getprop ro.build.version.release` for Android
- Private methods use `idevice_id -l`, `idevicename` for iOS

---

### `src/main/java/com/<org>/<product>/utils/AppiumServer.java`

Singleton. `init()` method:
- Calls `dependencyCheck()` to verify Node.js, Carthage (macOS), Appium JS path
- Builds `AppiumDriverLocalService` for Mac or Windows
- `tearDown()` stops the server

---

### `src/main/java/com/<org>/<product>/utils/PropertyReader.java`

Singleton. Methods:
- `getPropValues(String fileName)` → `HashMap<String,String>` — reads from `src/main/resources/`
- `getPropValues(String fileName, String key)` → `String` — reads single key

---

### `src/main/java/com/<org>/<product>/Pages.java`

```java
public class Pages {
    private static <T extends FlutterBase> T getPage(Class<T> pageType) {
        try { return pageType.newInstance(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    // One static method per page:
    public static <ModuleName>Page <ModuleName>Page() { return getPage(<ModuleName>Page.class); }
}
```

---

### Page Action Class Pattern

```java
package com.<org>.<product>.pageobjects.<modulename>;
import com.<org>.<product>.utils.FlutterBase; // or Base for NATIVE
import com.<org>.<product>.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class <ModuleName>Page extends FlutterBase {
    private <ModuleName>PageObjectRepository repo;
    private static final Logger logger = LogManager.getLogger(<ModuleName>Page.class.getName());

    public <ModuleName>Page() {
        super(DriverFactory.getDriverForFramework());
        repo = new <ModuleName>PageObjectRepository(driver);
    }
    // Business action methods only
}
```

---

### Page Object Repository Pattern — Flutter

```java
package com.<org>.<product>.pageobjects.<modulename>;
import com.<org>.<product>.utils.DriverFactory;
import com.<org>.<product>.utils.kotlin.FlutterFinder;
import com.<org>.<product>.utils.kotlin.finder.FlutterElement;
import io.appium.java_client.AppiumDriver;

public class <ModuleName>PageObjectRepository {
    public AppiumDriver driver;
    public FlutterFinder find;

    public <ModuleName>PageObjectRepository(AppiumDriver driver) {
        this.driver = driver;
        this.find = new FlutterFinder(this.driver);
    }
    // Locators using find.bySemanticsLabel(), find.text(), find.byValueKey(), etc.
    protected FlutterElement <elementName> = find.bySemanticsLabel("<semanticsKey>");
}
```

### Page Object Repository Pattern — Native

```java
package com.<org>.<product>.pageobjects.<modulename>;
import com.<org>.<product>.utils.Object_Base;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class <ModuleName>PageObjectRepository extends Object_Base {
    public <ModuleName>PageObjectRepository(AppiumDriver driver) { super(driver); }
    
    @AndroidFindBy(accessibility = "<android-key>")
    @iOSXCUITFindBy(accessibility = "<ios-key>")
    public WebElement <elementName>;
}
```

---

### TestNG Listeners (`src/test/.../testng/`)

| Class | Implements | Responsibility |
|---|---|---|
| `INewListener` | Interface combining `IMethodInterceptor`, `IAnnotationTransformer`, `ITestListener` | Marker interface |
| `TestListener` | `INewListener` extends `TestBase` | Text/log4j2 lifecycle logs; screenshot-on-failure to `Screenshots/`; drives `ExtentReportManager` (`startTest`/`pass`/`fail`-with-screenshot/`skip`) per test method |
| `RetryAnalyzer` | `IRetryAnalyzer` | Configurable retry count (default 1) |
| `PriorityInterceptor` | `IMethodInterceptor` | Orders test methods by source line number via Javassist |
| `EventListener` | `WebDriverEventListener` | Optional WebDriver event logging |
| `SuiteCreationListener` | `ISuiteListener` | Suite-level events |
| `TestAnnotation` | `IAnnotationTransformer` | Annotation transformation |
| `MobileEmailReport` | `ISuiteListener` | Initializes `ExtentReportManager` on `onStart`, writes a lightweight custom HTML suite-summary table (pass/fail/skip counts per `<test>` block) and flushes the Extent report on `onFinish` — see "Reporting" in `03-standards/appium-java-standards.md`. Replaces an earlier, never-actually-implemented `EmailableTestNGReport`/`IReporter` design. |

---

### `src/test/.../framework/TestBase.java`

Extends `DriverFactory`. Lifecycle (live-verified — **not** `@BeforeSuite`/`@BeforeTest`/`@AfterTest`/`@AfterSuite`; an earlier version of this template documented that suite/test-level lifecycle, but the framework that was actually built and live-run uses per-method setup/teardown instead, matching one Appium session per `@Test` method):
- `@BeforeMethod(alwaysRun = true)`: creates driver via `InitiateDriver.getInstance().createDriver()`, calls `setDriverForFramework(...)`
- `@AfterMethod(alwaysRun = true)`: quits driver via `InitiateDriver.getInstance().quitDriver()`

Reads: `config.properties`

---

### `src/main/java/com/<org>/<product>/reports/ExtentReportManager.java`

Ported from the reference `Automation-java` project's proven `ExtentReportManager` (see `03-standards/appium-java-standards.md`'s "Reporting" section) — static `ExtentReports` instance + `ThreadLocal<ExtentTest>` per test method, Appium-adapted with a screenshot-attaching `fail` overload:

```java
package com.<org>.<product>.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestResult;

public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    private ExtentReportManager() { }

    public static synchronized void initReport() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport/<ProjectName>Report.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    public static void startTest(String className, String methodName) {
        currentTest.set(extent.createTest(className + " :: " + methodName));
    }

    public static void pass(String message) {
        if (currentTest.get() != null) currentTest.get().pass(message);
    }

    public static void fail(Throwable t, String screenshotPath) {
        if (currentTest.get() == null) return;
        if (screenshotPath != null) {
            try {
                currentTest.get().fail(t, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                return;
            } catch (Exception ignored) { /* fall through to text-only failure */ }
        }
        currentTest.get().fail(t);
    }

    public static void skip(String message) {
        if (currentTest.get() != null) currentTest.get().skip(message);
    }

    public static void endTest(ITestResult result) {
        currentTest.remove();
    }

    public static synchronized void flush() {
        if (extent != null) extent.flush();
    }
}
```

### `src/test/.../testng/MobileEmailReport.java`

Ported from the reference project's proven `WebEmailReport` (same rationale) — an `ISuiteListener` that both owns the Extent lifecycle (`initReport`/`flush`, since this stack's `TestBase` has no `@BeforeSuite`/`@AfterSuite` hook to put them in — see "Lifecycle" above) and writes a lightweight custom HTML pass/fail/skip summary table per `<test>` block:

```java
package com.<org>.<product>.testng;

import com.<org>.<product>.reports.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class MobileEmailReport implements ISuiteListener {

    private static final Logger log = LogManager.getLogger(MobileEmailReport.class);

    @Override
    public void onStart(ISuite suite) {
        ExtentReportManager.initReport();
    }

    @Override
    public void onFinish(ISuite suite) {
        try {
            Files.createDirectories(Paths.get("test-output"));
            StringBuilder html = new StringBuilder();
            html.append("<html><body><h2><ProjectName> Automation — Suite Summary</h2><table border='1' cellpadding='6'>");
            html.append("<tr><th>Test</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr>");

            int totalPass = 0, totalFail = 0, totalSkip = 0;
            for (Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet()) {
                ITestContext ctx = entry.getValue().getTestContext();
                int pass = ctx.getPassedTests().size();
                int fail = ctx.getFailedTests().size();
                int skip = ctx.getSkippedTests().size();
                totalPass += pass; totalFail += fail; totalSkip += skip;
                html.append("<tr><td>").append(ctx.getName()).append("</td><td>").append(pass)
                        .append("</td><td>").append(fail).append("</td><td>").append(skip).append("</td></tr>");
            }
            html.append("<tr><th>Total</th><th>").append(totalPass).append("</th><th>").append(totalFail)
                    .append("</th><th>").append(totalSkip).append("</th></tr></table></body></html>");

            try (FileWriter writer = new FileWriter("test-output/email-report.html")) {
                writer.write(html.toString());
            }
            log.info("Email-style HTML report written to test-output/email-report.html");
        } catch (IOException e) {
            log.error("Failed to write email report", e);
        } finally {
            ExtentReportManager.flush();
        }
    }
}
```

`TestListener.onTestStart/onTestSuccess/onTestFailure/onTestSkipped` call `ExtentReportManager.startTest(...)`/`.pass(...)`/`.fail(throwable, screenshotPath)`/`.skip(...)` respectively — the existing screenshot-to-`Screenshots/` logic in `onTestFailure` stays exactly as-is, just also passing its saved path into `ExtentReportManager.fail(...)` so the failure screenshot is embedded inline in the HTML report, not only saved to disk separately.

Both listeners are registered in `CC_TestNG.xml`'s `<listeners>` block (see "TestNG XML Suite" below) — no separate Maven Surefire `<properties><property><name>listener</name>...` wiring is needed for this stack, since the existing `<listeners>` mechanism already covers it.

---

### Test Class Pattern

```java
package com.<org>.<product>.<modulename>tests;
import com.<org>.<product>.Pages;
import com.<org>.<product>.framework.TestBase;
import com.<org>.<product>.utils.PropertyReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class <ModuleName>Tests extends TestBase {

    @Test(testName = "<TestName>", description = "<Full test description — JIRA-ID/TC-ID and business intent>", groups = {"smoke"})
    public void verify<Action>() {
        try {
            // Test steps using Pages registry
            Pages.<ModuleName>Page().<action>();
        } catch (Exception e) {
            logger.error("Exception occurred", e);
            Assert.fail("<Failure message>: " + e.getMessage());
        }
    }
}
```

No `@Description`/`@Epic`/`@Feature` annotations — those were Allure-specific and never actually implemented in any live-generated framework (see "Reporting" below). TestNG's own `@Test(testName=..., description=...)` plus `ExtentReportManager.startTest(className, methodName)` (wired automatically via `TestListener`, not per-test-method boilerplate) carry the same metadata with no extra annotations needed on every method.

---

### `src/main/resources/config.properties`

```properties
AppPath=<absolute_path_to_app_without_extension>
AndroidAppPackage=<package.name>
AndroidAppActivity=<package.name.MainActivity>
BundleId=<com.example.app>
TestName=<ProjectName> Tests
ReportTitle=<ProjectName> Test Report
RUN_ON=${RunOn.value}
CONTEXT=${Context.value}
ENV=<dev|integration|uat|preprod|prod>
SETUP=${Setup.value}
BROWSER=Chrome
PROXY=false
URL=http://127.0.0.1:4444/wd/hub
SeverAddress=http://0.0.0.0:4723

# --- BrowserStack (only when the user asked for BrowserStack — see below) ---
# BSEnvironment=<key from device_conf.json's "environments" map>
# AndroidAppPath=<real local .apk path — used only when the selected environment's runon=ANDROID_APP>
# iOSAppPath=<real local .ipa/.app path — used only when the selected environment's runon=iOS_APP>
```

**Live-run correction:** `credentials.properties`, `SampleTestData.json`, `testrail.properties`, and `zephyr.properties` are **no longer generated by default.** A real generated framework was found with all four present and zero code anywhere referencing any of them. Use the CSV + `@TestDataSource` pattern (see `03-standards/appium-java-standards.md`'s "Test Data Management" section) for credentials/business data in a fresh Mode A scaffold instead — `credentials.properties` is Mode-B-only, and only when the target project's Framework Profile already reads credentials that way. Don't scaffold TestRail/Zephyr integration or a generic `SampleTestData.json` speculatively; add them later if a real requirement calls for them.

### `src/main/resources/device_conf.json` (only when the user asked for BrowserStack — see "BrowserStack Integration" in `03-standards/appium-java-standards.md`)

```json
{
  "server": "hub-cloud.browserstack.com",
  "user": "<bs_username>",
  "key": "<bs_access_key>",
  "capabilities": {
    "name": "<ProjectName> Tests",
    "browserstack.debug": true,
    "AndroidID": "",
    "iOSID": ""
  },
  "environments": {
    "samsung_s10(9.0)": {
      "device": "Samsung Galaxy S10",
      "os": "9.0",
      "runon": "ANDROID_APP",
      "deviceid": ""
    }
  }
}
```

`user`/`key` must be the real BrowserStack account credentials the user supplies — a live session cannot be established with the `<bs_username>`/`<bs_access_key>` placeholders shown above. `BSEnvironment` in `config.properties` must name one key from `environments` (e.g. `"samsung_s10(9.0)"`); `InitiateDriver.createBrowserStackDriver()` resolves it, uploads the matching platform's app via `BrowserStackAppUploader`, and connects to `https://<user>:<key>@<server>/wd/hub` — see the standards doc's "BrowserStack Integration" section for the full flow, including the `.ipa`-vs-`.app` limitation for iOS real devices.

### `src/main/java/com/<org>/<product>/utils/BrowserStackConfig.java` / `BrowserStackAppUploader.java` (only when the user asked for BrowserStack)

- `BrowserStackConfig` — reads `device_conf.json` from the classpath (like `PropertyReader` does for `.properties` files) and exposes `getServer()`/`getUser()`/`getKey()`/`getEnvironment(String key)`.
- `BrowserStackAppUploader` — `getOrUploadAppUrl(appPath, user, key)` uploads the local binary to `https://api-cloud.browserstack.com/app-automate/upload` (Basic Auth, multipart `file` field) and returns the `bs://<hash>` app URL, caching it in a local file keyed by the app binary's own last-modified timestamp so an unchanged binary isn't re-uploaded on every run.

---

### `CC_TestNG.xml`

```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="<ProjectName>RegressionSuite" parallel="tests" thread-count="2" verbose="2">
    <listeners>
        <listener class-name="com.<org>.<product>.testng.TestListener"/>
        <listener class-name="com.<org>.<product>.testng.MobileEmailReport"/>
    </listeners>
    <test name="<ModuleName>Tests" preserve-order="true">
        <classes>
            <class name="com.<org>.<product>.<modulename>tests.<ModuleName>Tests"/>
        </classes>
    </test>
</suite>
```

---

### `Log4j2.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <File name="FileAppender" fileName="logs/automation.log" append="true">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

---

### `Jenkinsfile`

**Mandatory scaffold, not optional.** **Live-run correction:** this file was documented in every prior version of this template but was never actually generated for the real Appium-Java framework — the same class of "documented intent vs. shipped code" drift found and fixed for this stack's Reporting setup (see "Reporting" below). Every Mode A generation must produce a working `Jenkinsfile` at the framework's own root, not defer it.

Mirrors the reference `Automation-java` project's proven, actually-working `Jenkinsfile` pattern: full-repo checkout (this is a monorepo — the Jenkins job checks out `cc-qa-ai-framework` as a whole, not just this subfolder) + `dir(...)`-scoped stages using the real output path, plus a `junit` step so Jenkins's own test-result trend graphs pick up the Surefire XML directly instead of only archiving it as an opaque file.

```groovy
pipeline {
    agent any
    parameters {
        choice(name: 'DEVICE', choices: ['android', 'iOS', 'bsAndroid', 'bsiOS'], description: 'Target device platform')
        choice(name: 'ENV', choices: ['dev', 'integration', 'uat', 'preprod', 'prod'], description: 'Test environment')
        string(name: 'SUITE', defaultValue: '', description: 'Override TestNG XML suite (optional)')
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Test') {
            steps {
                dir('07-outputs/Automation/Framework') {
                    script {
                        def suiteArg = params.SUITE ? "-DsuiteFile=${params.SUITE}" : ''
                        sh "mvn clean test -Ddevice=${params.DEVICE} -Denv=${params.ENV} ${suiteArg}"
                    }
                }
            }
        }
    }
    post {
        always {
            junit '07-outputs/Automation/Framework/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: '07-outputs/Automation/Framework/target/surefire-reports/**, 07-outputs/Automation/Framework/Screenshots/**, 07-outputs/Automation/Framework/test-output/**, 07-outputs/Automation/Framework/logs/**', allowEmptyArchive: true
        }
    }
}
```

- `dir('07-outputs/Automation/Framework')` must match the actual output path from `MasterQAOrchestrator/AppiumAutomationOrchestrator.md` Step 5 — substitute the real path at generation time if the user specified a non-default output location, in every stage, not just the first.
- No Allure pipeline stage — `mvn test` itself produces `test-output/ExtentReport/<ProjectName>Report.html` and `test-output/email-report.html` directly (see "Reporting" below), so there is nothing separate to invoke or install a Jenkins plugin for; archiving `test-output/**` is sufficient.

---

### `.gitignore`

```
target/
out/
.idea/
*.class
*.iml
Screenshots/
logs/
test-output/
.DS_Store
```
