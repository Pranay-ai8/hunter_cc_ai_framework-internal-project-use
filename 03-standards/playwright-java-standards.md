# Playwright Java Automation Standards

## Objective

Establish enterprise-grade standards for **Playwright with Java + TestNG** automation, so AI-generated frameworks and test artifacts for this stack are maintainable, scalable, reusable, and CI/CD-ready.

These standards apply whenever the user selects **Java** as the automation language for Playwright automation, and are modeled on the reference implementation at `Covacsis_web`.

This is a **sibling stack** to `03-standards/playwright-typescript-standards.md` (Cucumber + Playwright + TypeScript). The two are never mixed within one project — a project is either the TypeScript/BDD stack or this Java/TestNG stack, selected explicitly by the user.

---

## Technology Stack

| Layer | Choice |
|---|---|
| Automation Tool | Playwright for Java (`com.microsoft.playwright:playwright`) |
| Language | Java 17 |
| Build Tool | Maven (`pom.xml`) |
| Test Runner | TestNG (`config/testng_config.xml` suite file, run via `maven-surefire-plugin`) |
| Design Pattern | Page Object Model, fluent locators via a lock-in `ILocator` wrapper |
| Assertions | Centralized `AssertionUtil` (hard + thread-safe soft assertions over TestNG `Assert`/`SoftAssert`) |
| Reporting | ExtentReports + custom HTML email report + optional ReportPortal (`com.epam.reportportal`) |
| Logging | Log4j2 (`org.apache.logging.log4j`) |
| Config | `owner` library (`org.aeonbits.owner`) with environment-specific `.properties` files |
| Test Data | CSV (comma-delimited, header + `Key` column) loaded via a custom `TestDataSource` annotation + provider |
| Boilerplate reduction | Lombok (`@Getter`, `@Accessors(fluent = true)`, `@Slf4j`) |
| CI/CD | Jenkins (`Jenkinsfile`, parameterized by `ENV`) |

---

## Project Structure

```
<project-root>/
├── pom.xml                                    # Maven build, Java 17, Playwright/TestNG/ExtentReports/etc.
├── Jenkinsfile                                 # Parameterized pipeline (ENV choice, credentials binding)
├── config/
│   └── testng_config.xml                       # TestNG suite — one <test> block per module, groups for include/exclude
├── src/
│   ├── main/
│   │   ├── java/com/<org>/<product>/
│   │   │   ├── annotations/
│   │   │   │   ├── Smoke.java                  # @Retention(RUNTIME) @Target(METHOD) marker annotations
│   │   │   │   ├── Regression.java
│   │   │   │   └── TestDataSource.java          # value=path, type=csv|json, key=<row key>
│   │   │   ├── api/
│   │   │   │   ├── IApiClient.java
│   │   │   │   ├── ApiFactory.java
│   │   │   │   └── PlaywrightApiClient.java
│   │   │   ├── base/
│   │   │   │   ├── BasePage.java                # protected Page page; $() locator wrapper; navigation/wait/screenshot helpers
│   │   │   │   ├── BaseTest.java                # TestNG @BeforeSuite/@BeforeTest/@AfterTest lifecycle
│   │   │   │   ├── ApiTest.java
│   │   │   │   ├── ILocator.java                # Locator abstraction interface
│   │   │   │   └── PlaywrightLocator.java        # ILocator impl wrapping com.microsoft.playwright.Locator
│   │   │   ├── browser/
│   │   │   │   ├── BrowserName.java
│   │   │   │   ├── ChromeBrowser.java / EdgeBrowser.java / FirefoxBrowser.java / WebkitBrowser.java
│   │   │   ├── config/
│   │   │   │   ├── Configuration.java            # Owner @Config interface — env(), org(), baseUrl(), browser(), runMode(), timeouts
│   │   │   │   └── ConfigurationManager.java     # Loads config.properties + config-<env>.properties, returns Configuration
│   │   │   ├── core/
│   │   │   │   ├── BrowserFactory.java / IBrowser.java
│   │   │   │   ├── BrowserManager.java / IBrowserManager.java   # Creates/destroys BrowserContext + Page per test thread
│   │   │   │   └── PageManager.java              # ThreadLocal current-Page registry (supports popup/new-tab pages)
│   │   │   ├── listeners/
│   │   │   │   ├── TestListener.java
│   │   │   │   ├── WebListener.java
│   │   │   │   ├── WebAnnotationTransformer.java  # Maps @Smoke/@Regression to TestNG groups
│   │   │   │   └── WebEmailReport.java
│   │   │   ├── reports/
│   │   │   │   └── ExtentReportManager.java       # Static ExtentReports instance + ThreadLocal suite/case nodes
│   │   │   └── utils/
│   │   │       ├── AssertionUtil.java             # Hard + ThreadLocal SoftAssert helpers
│   │   │       ├── CSVLoader.java / JsonLoader.java
│   │   │       ├── TestDataArgumentsProvider.java # TestNG DataProvider resolving @TestDataSource
│   │   │       ├── CdpUtils.java / PerformanceUtils.java / WaitUtils.java / ScreenshotUtils.java / Result.java
│   │   │       ├── SelfHealer.java                # findHealedLocator() — see "Self-Healing Locators" below
│   │   └── resources/
│   │       ├── config.properties                  # Shared defaults
│   │       └── config-<env>.properties             # Per-environment overrides (sit, uat-new, prod, ...)
│   └── test/
│       ├── java/com/<org>/<product>/
│       │   ├── components/                        # Reusable UI components (Table, Tab, Form, DropdownSelect, Calendar, Widget, ...)
│       │   ├── pages/
│       │   │   ├── PageFactory.java                # Static factory: `public static LoginPage loginPage() { return getPage(LoginPage.class); }`
│       │   │   └── <area>/<module>/<Module>Page.java   # One Page Object per module, grouped by app area
│       │   └── tests/
│       │       └── <module>/<Module>Test.java      # One TestNG test class per module
│       └── resources/
│           ├── log4j2.xml
│           ├── reportportal.properties
│           └── testdata/
│               └── <module>.csv                    # Comma-delimited, header row + `Key` column selecting a data row
├── logs/
│   └── automation.log
├── screenshots/ / target/screenshots/
└── test-output/                                    # TestNG + email report HTML output
```

---

## pom.xml Standards

Every project must declare, at minimum:

```xml
<properties>
  <maven.compiler.source>17</maven.compiler.source>
  <maven.compiler.target>17</maven.compiler.target>
</properties>

<dependencies>
  <dependency><groupId>com.microsoft.playwright</groupId><artifactId>playwright</artifactId><version>1.57.0</version></dependency>
  <dependency><groupId>org.testng</groupId><artifactId>testng</artifactId><version>7.12.0</version></dependency>
  <dependency><groupId>com.aventstack</groupId><artifactId>extentreports</artifactId><version>5.1.2</version></dependency>
  <dependency><groupId>org.apache.logging.log4j</groupId><artifactId>log4j-core</artifactId><version>2.25.3</version></dependency>
  <dependency><groupId>org.apache.logging.log4j</groupId><artifactId>log4j-api</artifactId><version>2.25.1</version></dependency>
  <dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId><version>2.17.0</version></dependency>
  <dependency><groupId>org.aeonbits.owner</groupId><artifactId>owner</artifactId><version>1.0.12</version></dependency>
  <dependency><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><version>1.18.30</version><scope>provided</scope></dependency>
  <dependency><groupId>com.github.javafaker</groupId><artifactId>javafaker</artifactId><version>1.0.2</version></dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <configuration>
        <suiteXmlFiles><suiteXmlFile>config/testng_config.xml</suiteXmlFile></suiteXmlFiles>
        <testFailureIgnore>true</testFailureIgnore>
        <properties>
          <property><name>listener</name><value>
            com.<org>.<product>.listeners.WebListener,
            com.<org>.<product>.listeners.WebEmailReport,
            com.<org>.<product>.listeners.TestListener
          </value></property>
        </properties>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Optional add-ons only when the project needs them: `com.epam.reportportal:agent-java-testng` (ReportPortal), `com.rabbitmq:amqp-client` (messaging integrations), `commons-io`.

---

## BasePage Standards

All Page Objects extend `BasePage` (`src/main/java/.../base/BasePage.java`):

```java
public abstract class BasePage {
    protected final Page page;
    private static final Logger log = LogManager.getLogger(BasePage.class);

    protected BasePage(Page page) {
        this.page = page;
    }

    protected ILocator $(String name, Locator locator) {
        return new PlaywrightLocator(name, locator);
    }

    protected void navigate(String url) {
        log.info("Navigating to {}", url);
        page.navigate(url);
    }

    protected void screenshot(String name) { /* target/screenshots/<name>_<timestamp>.png */ }
}
```

- Constructor signature is always `protected BasePage(Page page)`; concrete Page Objects declare `public <Module>Page(Page page) { super(page); }`.
- `$(name, locator)` is the standard way to wrap a raw Playwright `Locator` into the framework's `ILocator` abstraction — never expose a raw `Locator` field directly from a Page Object.
- Common helpers live on `BasePage`: dialog handling (`acceptDialog`, `dismissDialog`), frame handling (`frameLocator`, `getFrame`, `frameElement`), attribute reads, navigation, wait helpers, screenshot, keyboard/mouse helpers.

---

## Page Object Standards

### File & Package Naming

- Package: `pages.<area>.<module>` (e.g., `pages.plantOS.login`, `pages.admin.areas`) — `<area>` groups pages by application section.
- Class: `<Module>Page.java` (or `<Module>Admin.java` for admin-area pages) — PascalCase, one file per class.

### Locators — Lombok fluent getters over `ILocator`

```java
public class LoginPage extends BasePage {

    public LoginPage(Page page) {
        super(page);
    }

    /*
     * Locators
     */
    @Getter
    @Accessors(fluent = true)
    private final ILocator usernameInput = $("Username Input",
            page.getByPlaceholder("daphnesmith@gmail.com"));

    @Getter
    @Accessors(fluent = true)
    private final ILocator loginButton = $("Login Button",
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")));

    /*
     * Page Actions
     */

    public LoginPage enterUsername(String username) {
        log.info("Entering username: {}", username);
        usernameInput().fill(username);
        return this;
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public boolean isErrorVisible() {
        return errorMessage().isVisible();
    }
}
```

- Locators are declared as `private final ILocator` fields, initialized inline in the field declaration (not in the constructor body), each wrapped via `$("Descriptive Name", page.<locatorMethod>(...))`.
- Every locator field is annotated `@Getter @Accessors(fluent = true)` (Lombok) so it is accessed as a fluent method: `usernameInput()`, not `getUsernameInput()`.
- Action methods that chain naturally return `this` (fluent builder style: `enterUsername(...).clickLogin()`), others return `void` or a result type.
- Required section comment banners in order: `/* Locators */` → `/* Page Actions */` (add `/* Validations */` when the page exposes boolean/state-check methods, e.g. `isErrorVisible()`).
- Prefer Playwright semantic locators (`getByRole`, `getByPlaceholder`, `getByText`, `getByLabel`) — see `03-standards/locator-standards.md` for full priority order; the same priority order applies here, just wrapped through `$()`.

### PageFactory

- `src/test/java/.../pages/PageFactory.java` is the single access point for obtaining a Page Object instance — Page Objects are **never** `new`'d directly in test classes.
- Pattern: `public static <T extends BasePage> T getPage(Class<T> clazz)` uses reflection (`clazz.getDeclaredConstructor(Page.class).newInstance(page)`) against the current thread's `Page` from `PageManager`.
- For every new Page Object, add a corresponding static accessor:

```java
public static LoginPage loginPage() {
    return getPage(LoginPage.class);
}
```

- Test classes access pages via a static import: `import static com.<org>.<product>.pages.PageFactory.loginPage;` then call `loginPage().login(...)`.
- `PageFactory.clickAndSwitchToNewPage(locator, clazz, pageName)` is the standard pattern for popup/new-tab navigation — register the new page with `PageManager.addPage(pageName, newPage)`.

---

## Live DOM Verification

See `03-standards/locator-standards.md`'s "Live DOM Verification" section for the full rationale and cross-stack process — this section covers only this stack's mechanics.

Before writing a module's Page Object for the first time, run a throwaway `main()` (a one-off class, e.g. `DomSnapshotTool.java`, deleted after use — not part of the generated framework) that launches Playwright for Java directly, navigates to the module's screen (performing any required setup, e.g. login), and collects the same candidate shape `SelfHealer.java` gathers at runtime via `page.evaluate(String, Object)` — reuse that collection script verbatim rather than writing a second version. Cross-check every locator you're about to wrap via `$("Descriptive Name", ...)` against this snapshot before writing it — not after the fact. Save the raw snapshot to `docs/dom-snapshots/<module>.json` (relative to the project root, create the folder if absent) so the verification is auditable later, and note any documented-vs-actual discrepancy directly in the generation summary.

---

## Self-Healing Locators

Every project generated for this stack must include a self-healing layer so a stale/renamed locator degrades to an automatic, logged recovery instead of an immediate hard failure. This is **mandatory scaffold**, not optional.

This stack has a structural advantage over the JavaScript/TypeScript stacks: every locator is already constructed through `$("Descriptive Name", page.<locatorMethod>(...))` (see "BasePage Standards" / "Page Object Standards" above), so the human-readable hint healing needs is already captured at the point of declaration. Healing therefore lives entirely **inside `PlaywrightLocator`'s action methods** — no changes are needed at Page Object or test-class call sites; `usernameInput().fill(username)` gains healing for free once `PlaywrightLocator` is fixed correctly.

### Module: `src/main/java/.../utils/SelfHealer.java`

```java
public final class SelfHealer {
    private static final long CACHE_TTL_MS = 60 * 60 * 1000; // 1 hour
    private static final double CONFIDENCE_THRESHOLD = 0.75;
    private static final String[] INTERACTIVE_SELECTORS = {"button", "a", "input", "select", "textarea", "[role]", "[data-testid]", "label"};
    private static final int MAX_DOM_SNAPSHOT_ELEMENTS = 150;

    public record HealResult(boolean success, Locator locator, double confidence) {}

    public static HealResult findHealedLocator(Page page, String hint, Exception originalError) { /* ... */ }
}
```

**Algorithm** (identical scoring model to `03-standards/playwright-javascript-standards.md`'s "Self-Healing Locators" section — read that section for the full rationale; only the Java-specific deltas are called out here):

1. Tokenize `hint` — the `name` string already passed to `$(name, locator)` at declaration, e.g. `"Login Button"` — into a keyword word-SET (strip punctuation, lowercase, split on whitespace, keep tokens ≥3 chars, dedupe). There is no separate locator-string keyword pass here (Playwright Java's `Locator` exposes no public selector-introspection API) — the descriptive name is the query, which is exactly why every `$()` call requiring healing coverage must carry a genuinely descriptive name, not a generic placeholder like `"Element"` or `"Button1"`.
2. Collect DOM candidates via **`page.evaluate(String, Object)` — exactly one `arg` parameter**, same as every other language binding of Playwright. Passing extra arguments is a JavaScript/Puppeteer habit that does not carry over; bundle everything the evaluated function needs into a single JSON-serializable object (a `Map<String,Object>` or a small record) if more than one value must cross into the browser context.
3. Score each candidate by precision/recall (F1) over a word set built from `id`, `name`, `role`, `aria-label`, `data-testid`, `placeholder`, `for`, visible text — **excluding `class`** (styling noise, not semantic identity; see the JavaScript standard's rule 3 for the concrete failure this caused: an otherwise-correct candidate scored 0.13 below threshold purely because its CSS class list diluted precision). Add `+0.15`/`+0.1` bonuses when a matched keyword is directly contained in `data-testid`/`id`.
4. Resolve the best-scoring candidate (score ≥ `CONFIDENCE_THRESHOLD`) to an actual `com.microsoft.playwright.Locator` via an explicit chain of `if`/`else` on the candidate's fields (`getByRole`, `getByTestId`, `getByLabel`, `getByText`, or a CSS `#id`/`[name=...]`/`.class` fallback) — build the real Playwright object directly, never a string to be interpreted later.
5. **Validate** the resolved locator actually matches something (`locator.count() > 0`) before returning success.
6. **Cache** successful heals for `CACHE_TTL_MS`, keyed by `page.url() + "|" + hint`, in a `healing-cache.json` file resolved relative to the project root (a shared ancestor of `PageObjects`/`Scripts` in Mode A's split layout, exactly as in the JavaScript stack).
7. **Log** every successful heal to `healing-report.log`: the hint, the confidence score, and the first line of the original exception's message.

### Wiring into `PlaywrightLocator`

`PlaywrightLocator implements ILocator`, constructed as `new PlaywrightLocator(name, locator)` inside every `$()` call — it already stores both the human-readable `name` and the underlying `Locator`. Wrap the **action** methods (`click()`, `fill(String)`, `selectOption(String)`, `check()`, `uncheck()`) with a short fast-attempt/fallback pattern:

```java
@Override
public void click() {
    try {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(3000));
        locator.click();
    } catch (PlaywrightException originalError) {
        SelfHealer.HealResult healed = SelfHealer.findHealedLocator(locator.page(), name, originalError);
        if (!healed.success()) {
            throw originalError;
        }
        healed.locator().click();
    }
}
```

Apply the identical try/fast-wait/catch-and-heal shape to `fill(String)`, `selectOption(String)`, `check()`, and `uncheck()`. **Read methods must NOT gain this wrapping**: `isVisible()`, `isEnabled()`, `textContent()`, `count()`, and similar state-reading methods on `ILocator` call straight through to the underlying `Locator` with no healing attempt — silently healing an assertion's target risks pointing it at the wrong element and turning a real failure into a false pass, a materially different risk than healing a click. See the JavaScript standard's "Self-Healing Locators" section for the full rationale; it applies identically here.

### Constraints

- Every `$(name, locator)` call must pass a genuinely descriptive `name` — it is the sole healing signal in this stack.
- Never call `page.evaluate(...)` with more than one data argument — bundle multiple values into one object.
- Never include `class` in the candidate scoring word set.
- Never wrap `isVisible()`/`isEnabled()`/`textContent()`/`count()` (or other `ILocator` read methods) with healing.
- Never build a healed locator from a string to be parsed/evaluated later — resolve it directly to a `Locator` object (step 4 above).

---

## Test Class Standards

```java
@Listeners({WebAnnotationTransformer.class})
public class LoginTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(LoginTest.class);

    @Test
    @Smoke
    @TestDataSource(value = "src/test/resources/testdata/login.csv", type = "csv", key = "ValidLogin")
    public void loginSuccessful(Map<String, String> data) {
        log.info("Starting Login Test: {}", data.get("Username"));
        loginPage().login(data.get("Username"), data.get("Password"));
        AssertionUtil.softAssertFalse(loginPage().isErrorVisible(), "User Logged in successfully");
        AssertionUtil.assertAll();
        ExtentReportManager.pass("Login successful");
    }

    @Test
    @Regression
    @TestDataSource(value = "src/test/resources/testdata/login.csv", type = "csv", key = "FailedLogin")
    public void loginFail(Map<String, String> data) {
        loginPage().login(data.get("Username"), data.get("Password"));
        AssertionUtil.softAssertTrue(loginPage().isErrorVisible(), "Login failed with error");
        AssertionUtil.assertAll();
    }
}
```

- Test class: package `tests.<module>`, class name `<Module>Test.java`, extends `BaseTest`.
- Always annotate `@Listeners({WebAnnotationTransformer.class})` at the class level.
- Every `@Test` method is tagged with exactly one severity/suite marker annotation: `@Smoke` or `@Regression` (add project-specific ones the same way, e.g. `@Sanity`, `@E2E`).
- Every data-driven test uses `@TestDataSource(value = "<csv path>", type = "csv", key = "<RowKey>")` — the framework's `TestDataArgumentsProvider` resolves this into a `Map<String,String> data` method parameter matching the CSV header row.
- Use `Map<String, String> data` as the test method parameter when a `@TestDataSource` is present; read fields via `data.get("<ColumnName>")`.
- Access Page Objects only via the static `PageFactory` accessors (e.g., `loginPage()`), imported statically — never instantiate directly.
- Use `AssertionUtil` for all assertions: `softAssertXxx(...)` collects failures, `AssertionUtil.assertAll()` at the end of the test raises them; use `assertXxx(...)` (hard) only when a failure should stop the test immediately.
- Use `ExtentReportManager.info/pass/fail(...)` to annotate report entries at key checkpoints.
- Delete/cleanup-only test methods (or entire test classes) that must run separately are tagged with a TestNG **group** (e.g., `groups = {"delete"}`) and wired into a dedicated `<test>` block in `testng_config.xml` with `<include name="delete"/>`; all other suites `<exclude name="delete"/>`.

---

## Test Data Standards

- Format: comma-delimited CSV under `src/test/resources/testdata/<module>.csv`.
- Header row defines the field names (`Key,Env,Org,Username,Password,...`); a `Key` column selects the specific row via `@TestDataSource(..., key = "ValidLogin")`.
- Multiple logical blocks may repeat the header row within the same file when grouping different scenarios (see reference: `login.csv` has two header blocks — `ValidLogin` rows then `FailedLogin` rows) — the loader must tolerate repeated header lines.
- Never hardcode credentials or environment values inside test classes — always source them from the CSV (or `Configuration`/environment properties for shared secrets).
- `CSVLoader` / `JsonLoader` are the single utilities for reading test data — do not introduce a second parser.

---

## Configuration Standards

- `Configuration.java` (Owner `@Config` interface) declares typed accessors: `env()`, `org()`, `baseUrl()`, `browser()`, `runMode()`, `screenWidth()`, `screenHeight()`, timeouts, etc., each with `@Key` and `@DefaultValue`.
- `@Config.Sources({"system:properties", "classpath:config.properties", "classpath:config-${env}.properties"})` — system properties win, then the active environment file, then the shared default file.
- `ConfigurationManager.config()` is the single entry point — reads `System.getProperty("env")` (default `"sit"`), loads `config.properties` + `config-<env>.properties`, and returns a cached `Configuration` via `ConfigCache.getOrCreate(...)`.
- Environment/user/password are typically also passed in via TestNG `<parameter>` elements in `testng_config.xml` and read with `@Parameters({"env","username","password"})` on a `@BeforeSuite` method, then pushed into `System.setProperty(...)` for `Configuration` to pick up.
- Never hardcode `baseUrl`, `browser`, or credentials in Java source — always go through `ConfigurationManager.config()`.

---

## Browser & Page Lifecycle Standards

- `BrowserFactory`/`IBrowser` + per-browser classes (`ChromeBrowser`, `EdgeBrowser`, `FirefoxBrowser`, `WebkitBrowser`) select and launch the configured browser.
- `BrowserManager` (implements `IBrowserManager<Page>`) creates one `BrowserContext`/`Page` per test thread (`ThreadLocal<BrowserContext>`, `ThreadLocal<Browser>`), enabling tracing when `Configuration.traceEnabled()` is true, and tears it down in `destroyBrowserPage`.
- `PageManager` is a `ThreadLocal` registry for "the current page" (supports adding/removing pages for popups/new tabs) — `PageFactory` always resolves the active page through `PageManager.getPage()`, never a field passed around manually.
- `BaseTest` wires the full lifecycle: `@BeforeSuite` (params + `ExtentReportManager.initReport()`), `@BeforeTest` (create `Playwright`, `BrowserManager`, `Page`; `PageManager.setMainPage(page)`; navigate to `ConfigurationManager.config().baseUrl()`), `@AfterTest` (`browserManager.destroyBrowserPage(page)`, close `Playwright`).

---

## Reporting Standards

Every execution must produce:

- **ExtentReports HTML** via `ExtentReportManager` — per-suite/per-case nodes created in `BaseTest`, `info/pass/fail(...)` calls from test methods and page objects.
- **Custom HTML email report** via `WebEmailReport` (TestNG listener), summarizing pass/fail counts, sent/saved after the run.
- **TestNG native reports** under `test-output/`.
- **Screenshots** captured via `BasePage.screenshot(name)` (saved to `target/screenshots/` or `screenshots/`), and automatically on failure via `TestListener`/`WebListener`.
- **Logs** via Log4j2 to `logs/automation.log`.
- Optional: **ReportPortal** integration (`agent-java-testng`, `logger-java-log4j`) when the project has ReportPortal configured (`reportportal.properties`).

---

## TestNG Suite Standards

- One `<test>` block per module in `config/testng_config.xml`, each referencing a single `<class name="com.<org>.<product>.tests.<module>.<Module>Test"/>`.
- Suite-level `<listeners>` always includes: `WebAnnotationTransformer`, `TestListener`, `WebListener`, `WebEmailReport` (add `ReportPortalTestNGListener` only when ReportPortal is enabled).
- Suite-level `<parameter>` elements provide `env`, `username`, `password` defaults, overridable from the CI pipeline (`-Denv=... -Dusername=... -Dpassword=...` via Maven `-D` system properties, or TestNG parameter overrides).
- Cleanup/deletion-only test groups are isolated into their own `<test>` blocks with `<include name="delete"/>`, while all standard test blocks `<exclude name="delete"/>`.
- Register a new module by adding both a new `<test>` block for its main scenarios and, if it creates data needing teardown, a corresponding `<test>` block with the `delete` group include.

---

## Code Quality Standards

- Java 17 language level, compiled via `maven-compiler-plugin`.
- Use Lombok (`@Getter`, `@Accessors(fluent = true)`, `@Slf4j`) to remove boilerplate — do not hand-write getters for locator fields.
- Every class that logs uses a `private static final Logger log = LogManager.getLogger(<ClassName>.class);` (Log4j2), not `System.out`/`e.printStackTrace()` in new code.
- Favor fluent, chainable action methods on Page Objects (`return this;`) for methods that are commonly composed.
- Extract any logic reused across 2+ Page Objects into `BasePage` or a shared component under `src/test/java/.../components/`.
- Avoid raw `com.microsoft.playwright.Locator` fields on Page Objects — always wrap with `$()`/`ILocator`.

---

## Security Standards

- Never hardcode credentials in Java source, `testng_config.xml`, or committed `config-<env>.properties` — use CI credential bindings (e.g., Jenkins `withCredentials`) and pass as `-D`/TestNG parameters.
- `.gitignore` must exclude `target/`, `logs/`, `screenshots/`, `test-output/`, and any file containing real secrets.

---

## CI/CD Standards

- `Jenkinsfile` — parameterized (`choice(name: 'ENV', choices: [...])`), binds credentials via `withCredentials([usernamePassword(credentialsId: ..., usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')])`, and runs:
  ```bash
  mvn clean test -Dsurefire.suiteXmlFiles=config/testng_config.xml -Denv=${params.ENV} -Dusername=${USERNAME} -Dpassword=${PASSWORD}
  ```
- Archive `test-output/`, `screenshots/`, and `logs/automation.log` as build artifacts.

---

## AI Generation Compliance

AI-generated Playwright + Java artifacts must:

- Follow all standards in this document — Maven + TestNG + Page Object Model with `ILocator`/`$()` wrapping, never a bare `Locator` field.
- Map every generated `@Test` method to a Test Case ID (in a `//` comment above the method or the method's Javadoc).
- Access Page Objects exclusively through `PageFactory` static accessors — never `new <Module>Page(page)` inside a test class.
- Externalize all test data to `src/test/resources/testdata/<module>.csv`, referenced via `@TestDataSource`.
- Tag every test with the correct suite annotation (`@Smoke`, `@Regression`, or project-specific equivalents) and register its class in `config/testng_config.xml`.
- Use `AssertionUtil` for all assertions — never call TestNG `Assert`/`SoftAssert` directly from a test or page object.
- Output Page Objects under `src/test/java/.../pages/<area>/<module>/`, test classes under `src/test/java/.../tests/<module>/`.

---

## Deliverables Checklist

Before finalizing, verify:

- [ ] Page Object extends `BasePage`, constructor is `public <Module>Page(Page page) { super(page); }`
- [ ] Locators declared as `private final ILocator` fields wrapped via `$()`, annotated `@Getter @Accessors(fluent = true)`
- [ ] Every `$(name, locator)` call passes a genuinely descriptive `name` — the sole self-healing signal for that locator
- [ ] `PlaywrightLocator`'s action methods (`click()`, `fill()`, `selectOption()`, `check()`, `uncheck()`) attempt-then-heal via `SelfHealer`; read methods (`isVisible()`, `textContent()`, `count()`) do NOT
- [ ] Section banners present: `/* Locators */` → (`/* Validations */` if applicable) → `/* Page Actions */`
- [ ] New Page Object registered with a static accessor in `PageFactory`
- [ ] Test class extends `BaseTest`, annotated `@Listeners({WebAnnotationTransformer.class})`
- [ ] Every `@Test` tagged with exactly one suite annotation (`@Smoke`/`@Regression`/etc.)
- [ ] Data-driven tests use `@TestDataSource` with a valid CSV path and `key`
- [ ] Assertions go through `AssertionUtil`, ending with `assertAll()` when soft asserts were used
- [ ] Test class registered in `config/testng_config.xml` in an appropriate `<test>` block
- [ ] No hardcoded credentials, URLs, or fixed `Thread.sleep()` waits
- [ ] Logging via Log4j2 `Logger`, not `System.out`/stack traces
- [ ] `pom.xml` dependencies unchanged unless a new capability genuinely requires an addition
