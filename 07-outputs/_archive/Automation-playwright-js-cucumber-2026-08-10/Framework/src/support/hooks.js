const { BeforeAll, Before, BeforeStep, AfterStep, After, AfterAll, Status, setDefaultTimeout } = require("@cucumber/cucumber");
const fs = require("fs-extra");

// There is no config-file equivalent for this — cucumber.js's IConfiguration schema has
// no "timeout" key. The default (5000ms) is too tight for a real Playwright browser
// launch in the Before hook (cold start) or for personas like performance_glitch_user
// that add several seconds of real latency to a single UI action.
setDefaultTimeout(30 * 1000);
const { fixture } = require("./pageFixture");
const { invokeBrowser } = require("../helper/browsers/browserManager");
const { getEnv } = require("../helper/env/env");
const { getLogger } = require("../helper/util/logger");
const CSVParser = require("../helper/parsers/CSVParser");
const HTMLSubStepLogger = require("./htmllSubStepLogger");

// Page Objects live in the sibling ../../../PageObjects/ folder — see readme.md.
const LoginPage = require("../../../PageObjects/loginPage");
const InventoryPage = require("../../../PageObjects/inventoryPage");
const ProductDetailPage = require("../../../PageObjects/productDetailPage");
const CartPage = require("../../../PageObjects/cartPage");
const CheckoutPage = require("../../../PageObjects/checkoutPage");
const OrderConfirmationPage = require("../../../PageObjects/orderConfirmationPage");
const MenuPage = require("../../../PageObjects/menuPage");

let browser;
let allTestData = [];

BeforeAll(async function () {
    getEnv();
    fs.ensureDirSync("test-results/screenshots");
    allTestData = CSVParser.parseData("src/helper/util/test-data/testData.csv", "|");
    fixture.allTestData = allTestData;
});

Before(async function (scenario) {
    const tags = scenario.pickle.tags.map((t) => t.name);
    const scenarioName = scenario.pickle.name;

    fixture.logger = getLogger(scenarioName);
    fixture.subStepLogger = new HTMLSubStepLogger(this);

    if (tags.includes("@api")) {
        // No API test cases exist for this PRD (no public API documented) — this
        // branch exists for standards compliance / future extension only.
        fixture.logger.info(`Scenario '${scenarioName}' tagged @api — skipping browser setup.`);
        return;
    }

    browser = await invokeBrowser();
    fixture.browser = browser;
    fixture.context = await browser.newContext();
    fixture.page = await fixture.context.newPage();

    fixture.pages.loginPage = new LoginPage(fixture.page, fixture.logger);
    fixture.pages.inventoryPage = new InventoryPage(fixture.page, fixture.logger);
    fixture.pages.productDetailPage = new ProductDetailPage(fixture.page, fixture.logger);
    fixture.pages.cartPage = new CartPage(fixture.page, fixture.logger);
    fixture.pages.checkoutPage = new CheckoutPage(fixture.page, fixture.logger);
    fixture.pages.orderConfirmationPage = new OrderConfirmationPage(fixture.page, fixture.logger);
    fixture.pages.menuPage = new MenuPage(fixture.page, fixture.logger);

    const keyTag = tags.find((t) => t.startsWith("@Key:"));
    if (keyTag) {
        const key = keyTag.replace("@Key:", "");
        fixture.testData = findDataByKeyAndEnv(key, process.env.ENV);
    }
});

function findDataByKeyAndEnv(key, env) {
    const row = allTestData.find(
        (r) => (r.Key || r.key) === key && (r.Env || r.env || "").toUpperCase() === (env || "").toUpperCase()
    );
    if (!row) {
        fixture.logger.info(`No test data row found for Key='${key}' Env='${env}'`);
        return {};
    }
    return row;
}

/**
 * Resolves <placeholder> tokens in step text against fixture.testData.
 * Unresolved placeholders are left untouched (never silently blanked) so a
 * mismatch is visible in the report. "{{BLANK}}" resolves to an empty string
 * by design, for blank-field negative test data rows.
 */
function replaceStepPlaceholders(text) {
    return text.replace(/<([\w.]+)>/g, (match, key) => {
        if (!(key in fixture.testData)) {
            return match;
        }
        const value = fixture.testData[key];
        return value === "{{BLANK}}" ? "" : value;
    });
}

BeforeStep(async function (step) {
    if (fixture.subStepLogger) {
        await fixture.subStepLogger.info(`Step starting: ${step.pickleStep.text}`);
    }
});

AfterStep(async function (step) {
    if (step.result.status === Status.FAILED && fixture.page) {
        const path = `test-results/screenshots/${step.pickleStep.text.replace(/[^a-z0-9]/gi, "_")}.png`;
        await fixture.page.screenshot({ path });
        if (fixture.subStepLogger) {
            await fixture.subStepLogger.failure(`Step failed: ${step.pickleStep.text}`);
        }
    } else if (fixture.subStepLogger) {
        await fixture.subStepLogger.success(`Step passed: ${step.pickleStep.text}`);
    }
});

After(async function (scenario) {
    if (scenario.result?.status === Status.FAILED && fixture.page) {
        const screenshot = await fixture.page.screenshot();
        await this.attach(screenshot, "image/png");
    }
    if (fixture.context) {
        await fixture.context.close();
    }
    if (browser) {
        await browser.close();
    }
    fixture.pages = {};
    fixture.testData = {};
});

AfterAll(async function () {
    // Shared browser (if any long-lived instance were introduced) would close here.
});

module.exports = { replaceStepPlaceholders, findDataByKeyAndEnv };
