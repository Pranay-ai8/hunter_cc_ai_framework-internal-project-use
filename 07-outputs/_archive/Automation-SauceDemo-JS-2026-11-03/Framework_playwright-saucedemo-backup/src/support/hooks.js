const { Before, After, BeforeAll, AfterAll, BeforeStep, AfterStep, Status, setDefaultTimeout } = require("@cucumber/cucumber");
const fs = require("fs-extra");
const path = require("path");

const { getEnv } = require("../helper/env/env");
const { invokeBrowser } = require("../helper/browsers/browserManager");
const CSVParser = require("../helper/parsers/CSVParser");
const { getLogger } = require("../helper/util/logger");
const HTMLSubStepLogger = require("./htmllSubStepLogger");
const { fixture } = require("./pageFixture");

const LoginPage = require("../../../PageObjects/loginPage");
const InventoryPage = require("../../../PageObjects/inventoryPage");
const ProductDetailPage = require("../../../PageObjects/productDetailPage");
const CartPage = require("../../../PageObjects/cartPage");
const CheckoutPage = require("../../../PageObjects/checkoutPage");
const OrderConfirmationPage = require("../../../PageObjects/orderConfirmationPage");
const NavigationPage = require("../../../PageObjects/navigationPage");

const TEST_DATA_PATH = path.resolve(__dirname, "..", "helper", "util", "test-data", "testData.csv");
const SCREENSHOT_DIR = path.resolve(__dirname, "..", "..", "test-results", "screenshots");
const TRACE_DIR = path.resolve(__dirname, "..", "..", "test-results", "trace");

// Real bug found while running the suite live: the default Cucumber step timeout (5000ms) is
// shorter than performance_glitch_user's own documented ~5s artificial login delay (TC_LOGIN_006),
// causing that step to time out before Playwright's own (longer) waits ever got a chance to
// resolve. Raised to 30s so genuinely slow-but-correct steps aren't treated as failures.
setDefaultTimeout(30 * 1000);

let browser;
let allTestData = [];

/**
 * Replaces <placeholder> tokens in step text using the resolved data row.
 * Unresolved placeholders are left untouched (never silently blank) so a
 * mismatch is visible in the report rather than hidden.
 */
function replaceStepPlaceholders(stepText, dataRow) {
    if (!stepText || !dataRow) return stepText;
    return stepText.replace(/<([\w.]+)>/g, (match, key) => {
        const value = key.split(".").reduce((acc, part) => (acc && acc[part] !== undefined ? acc[part] : undefined), dataRow);
        return value !== undefined ? value : match;
    });
}

function findDataByKeyAndEnv(key, env) {
    if (!key) return {};
    return (
        allTestData.find((row) => (row.Key || row.key) === key && String(row.Env || row.env || "").toUpperCase() === String(env || "").toUpperCase()) || {}
    );
}

BeforeAll(async function () {
    getEnv();
    fixture.env = process.env.ENV || "PRD";
    try {
        allTestData = CSVParser.parseData(TEST_DATA_PATH, "|");
    } catch (err) {
        console.error(`[hooks] Failed to load test data: ${err.message}`);
        allTestData = [];
    }
    fs.ensureDirSync(SCREENSHOT_DIR);
    fs.ensureDirSync(TRACE_DIR);
    browser = await invokeBrowser();
});

Before(async function (scenario) {
    const scenarioName = scenario.pickle.name;
    fixture.logger = getLogger(scenarioName);
    fixture.subStepLogger = new HTMLSubStepLogger((data, mediaType) => this.attach(data, mediaType));

    fixture.context = await browser.newContext({ viewport: { width: 1366, height: 900 } });
    await fixture.context.tracing.start({ screenshots: true, snapshots: true, sources: false });
    fixture.page = await fixture.context.newPage();

    fixture.pages.loginPage = new LoginPage(fixture.page, fixture.logger);
    fixture.pages.inventoryPage = new InventoryPage(fixture.page, fixture.logger);
    fixture.pages.productDetailPage = new ProductDetailPage(fixture.page, fixture.logger);
    fixture.pages.cartPage = new CartPage(fixture.page, fixture.logger);
    fixture.pages.checkoutPage = new CheckoutPage(fixture.page, fixture.logger);
    fixture.pages.orderConfirmationPage = new OrderConfirmationPage(fixture.page, fixture.logger);
    fixture.pages.navigationPage = new NavigationPage(fixture.page, fixture.logger);

    const tags = scenario.pickle.tags.map((t) => t.name);
    const keyTag = tags.find((t) => t.startsWith("@Key:"));
    const dataKey = keyTag ? keyTag.replace("@Key:", "") : null;
    fixture.testData = findDataByKeyAndEnv(dataKey, fixture.env);

    fixture.logger.info(`===== Starting scenario: ${scenarioName} (Key=${dataKey || "none"}) =====`);
});

BeforeStep(async function (step) {
    const stepText = step.pickleStep ? step.pickleStep.text : "";
    if (fixture.logger) fixture.logger.info(`STEP: ${stepText}`);
    if (fixture.subStepLogger) fixture.subStepLogger.info(stepText);
});

AfterStep(async function (step) {
    if (step.result && step.result.status === Status.FAILED) {
        if (fixture.logger) fixture.logger.error(`STEP FAILED: ${step.pickleStep ? step.pickleStep.text : ""}`);
        if (fixture.subStepLogger) fixture.subStepLogger.failure(step.pickleStep ? step.pickleStep.text : "");
        if (fixture.page) {
            try {
                const screenshot = await fixture.page.screenshot();
                this.attach(screenshot, "image/png");
            } catch (err) {
                fixture.logger && fixture.logger.warn(`Could not capture failure screenshot: ${err.message}`);
            }
        }
    } else if (fixture.subStepLogger) {
        fixture.subStepLogger.success(step.pickleStep ? step.pickleStep.text : "");
    }
});

After(async function (scenario) {
    const scenarioName = scenario.pickle.name.replace(/[^a-z0-9_-]/gi, "_").slice(0, 80);

    if (fixture.context) {
        const tracePath = path.join(TRACE_DIR, `${scenarioName}.zip`);
        try {
            await fixture.context.tracing.stop({ path: tracePath });
        } catch {
            // Tracing may already be stopped on a hard crash — non-fatal.
        }
    }

    if (scenario.result && scenario.result.status === Status.FAILED) {
        fixture.logger && fixture.logger.error(`===== Scenario FAILED: ${scenario.pickle.name} =====`);
    } else {
        fixture.logger && fixture.logger.info(`===== Scenario PASSED: ${scenario.pickle.name} =====`);
    }

    if (fixture.page) await fixture.page.close();
    if (fixture.context) await fixture.context.close();
});

AfterAll(async function () {
    if (browser) await browser.close();
});
