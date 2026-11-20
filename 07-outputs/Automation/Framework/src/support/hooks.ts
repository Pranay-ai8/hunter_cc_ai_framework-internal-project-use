import {
    BeforeAll,
    AfterAll,
    Before,
    After,
    BeforeStep,
    AfterStep,
    Status,
    setDefaultTimeout
} from "@cucumber/cucumber";
import { Browser, BrowserContext, Page } from "@playwright/test";

// Set default timeout to 1 minute (60,000 ms)
setDefaultTimeout(60 * 1000);
import { fixture } from "./pageFixture";
import { invokeBrowser } from "../helper/browsers/browserManager";
import { createLogger } from "winston";
import { options } from "../helper/util/logger";
import { CSVParser } from "../helper/parsers/CSVParser";
import * as fs from "fs-extra";
import * as path from "path";
import { getEnv } from "../helper/env/env";
import { HTMLSubStepLogger } from "./htmllSubStepLogger";
import LoginPage from "../pages/loginPage";

setDefaultTimeout(60 * 1000);

// ----------------- Global Variables -----------------
let browser: Browser;
let context: BrowserContext;
let allData: Record<string, string>[] = [];

// ================================================================
// 🧩 BEFORE ALL — Launch Browser once before all features
// ================================================================
BeforeAll(async function () {
    getEnv();
    browser = await invokeBrowser();
    console.log("✅ Browser launched for all tests.");

    // Load all CSV files in the test-data folder starting from project root or helper directory
    const candidateDirs = [
        path.join(process.cwd(), "src/helper/util/test-data"),
        path.join(__dirname, "../helper/util/test-data"),
        path.join(process.cwd(), "src/util/test-data")
    ];

    allData = [];
    const loadedFiles = new Set<string>();

    for (const testDataDir of candidateDirs) {
        if (fs.existsSync(testDataDir)) {
            const files = await fs.readdir(testDataDir);
            for (const file of files) {
                if (file.endsWith(".csv") && !loadedFiles.has(file)) {
                    const filePath = path.join(testDataDir, file);
                    const fileData = await CSVParser.parseData(filePath, '|');
                    allData.push(...fileData);
                    loadedFiles.add(file);
                }
            }
        }
    }
    console.log(`✅ Loaded ${allData.length} test data rows from test-data directories.`);
    // ---- PERFORM ONE-TIME LOGIN TO SAVE STATE & INITIALIZE MAIN PAGE ----
    const authDir = path.join(__dirname, "../../.auth");
    fs.ensureDirSync(authDir);
    const authFile = path.join(authDir, "admin.json");
    
    context = await browser.newContext({
        viewport: null,
        ignoreHTTPSErrors: true,
        recordVideo: { dir: "test-results/videos" },
        permissions: ["clipboard-read", "clipboard-write"],
    });
    const mainPage = await context.newPage();
    fixture.page = mainPage;

    const loginPage = new LoginPage(mainPage);
    try {
        console.log("🔐 Authenticating once for all tests...");
        await loginPage.navigateToLogin();
        const adminEmail = process.env.SUPER_ADMIN_EMAIL || process.env.SUPERADMIN_USER || "superadmin@buckhouse.com";
        const adminPassword = process.env.SUPER_ADMIN_PASSWORD || process.env.SUPERADMIN_PASSWORD || "Admin@123!";
        await loginPage.enterCredentials(adminEmail, adminPassword);
        await loginPage.clickSignIn();
        await mainPage.waitForURL(/.*\/dashboard/, { timeout: 30000 });
        await context.storageState({ path: authFile });
        console.log("✅ Authentication state saved. Browser is on Dashboard.");
    } catch (error) {
        console.error("❌ Failed to authenticate during setup:", error);
    }
    // ---------------------------------------------------------------------

    // Load all CSV files in the test-data directory
    const testDataDir = path.join(__dirname, "../helper/util/test-data");
    const fsFiles = fs.readdirSync(testDataDir).filter((f: string) => f.endsWith('.csv'));
    
    allData = [];
    for (const file of fsFiles) {
        const filePath = path.join(testDataDir, file);
        const fileData = await CSVParser.parseData(filePath, '|');
        allData = [...allData, ...fileData];
    }
});

// ================================================================
// 🧩 BEFORE SCENARIO — Setup context, page, and data per scenario
// ================================================================
Before(async function ({ pickle }) {
    const scenarioName = `${pickle.name}_${pickle.id}`;
    const env = process.env.ENV || "STG";
    fixture.env = env;

    // Extract Key from scenario tag @Key:XYZ
    const keyTag = pickle.tags.find(tag => tag.name.startsWith("@Key:"));
    const key = keyTag ? keyTag.name.replace("@Key:", "").trim() : null;

    if (key) {
        // Find the row matching Key + Env
        const testData = allData.find((row: any) => {
            const csvKey = row.Key ?? row.TestCaseID ?? row.key ?? row.testcaseid;
            const csvEnv = row.Env ?? row.env;
            return csvKey === key && csvEnv?.toLowerCase() === env.toLowerCase();
        });

        if (!testData) {
            throw new Error(`❌ No CSV data found for Key=${key} in Env=${env}. Ensure a row with Key '${key}' exists in your CSV files.`);
        }

        // Parse Group field as JSON if present
        if (testData.Group) {
            try {
                testData.Group = JSON.parse(testData.Group);
            } catch (e) {
                fixture.logger?.error(`Failed to parse Group field as JSON: ${testData.Group}`);
            }
        }
        fixture.testData = testData;

        // Replace parameters in steps with actual values (supports nested keys like <Group.name>)
        pickle.steps.forEach((step, index) => {
            pickle.steps[index].text = step.text.replace(/<([\w.]+)>/g, (_, paramKey) => {
                const keys = paramKey.split('.');
                let value: any = testData;
                for (const k of keys) {
                    value = value?.[k];
                }
                return Array.isArray(value) ? value[0] : value || `<${paramKey}>`;
            });
        });

        // Create stepArgsMap for dynamic step arguments
        // @ts-ignore
        this.stepArgsMap = new Proxy({}, {
            get: (_, prop: string) => testData[prop] ?? testData[prop.toLowerCase()] ?? `<${prop}>`
        });

        fixture.logger?.info(`🔹 Loaded scenario data → Key: ${key}, Env: ${env}`);
    } else {
        fixture.logger?.info("⚠️ No @Key tag found — skipping CSV data load");
    }



    // Check for @login tag to avoid using saved state for login tests
    const isLoginTest = pickle.tags.some(tag => tag.name.toLowerCase() === "@login");
    const authFile = path.join(__dirname, "../../.auth/admin.json");
    const useStorageState = !isLoginTest && fs.existsSync(authFile) ? authFile : undefined;

    if (!context) {
        context = await browser.newContext({
            viewport: null,
            ignoreHTTPSErrors: true,
            recordVideo: { dir: "test-results/videos" },
            storageState: useStorageState,
            permissions: ["clipboard-read", "clipboard-write"],
        });
    }

    if (!fixture.page || fixture.page.isClosed()) {
        fixture.page = await context.newPage();
    }

    // Reset to the standard desktop viewport before every scenario. The context is shared across the
    // whole suite run, so a responsiveness/mobile-viewport scenario (e.g. setViewportSize({width: 375, ...}))
    // would otherwise leave the browser mobile-sized for every scenario that runs after it - not just
    // visually, but causing the app's own real mobile/collapsed nav to render for unrelated scenarios.
    await fixture.page.setViewportSize({ width: 1280, height: 800 }).catch(() => {});

    // The page/context is shared across the whole suite. A prior (possibly failed) scenario can leave a
    // modal dialog open, whose backdrop overlay intercepts pointer events on the entire page and hangs
    // the next scenario's first click for the full actionability timeout. Escape alone isn't reliable -
    // it depends on focus having stayed on the dialog, which a failed step can leave in an odd state -
    // and even a "successful" force-click on the real target can silently land on the overlay instead,
    // since force only skips Playwright's actionability checks, not the real browser hit-test. Forcibly
    // remove any stray open overlay/dialog from the DOM so the next scenario starts on a clean page.
    await fixture.page.keyboard.press('Escape').catch(() => {});
    await fixture.page.evaluate(() => {
        try {
            const doc = (globalThis as any).document;
            doc?.querySelectorAll('div[data-state="open"].fixed.inset-0, div[role="dialog"][data-state="open"], div[role="alertdialog"][data-state="open"]')
                .forEach((el: any) => el.remove());
        } catch (_) { /* no-op */ }
    }).catch(() => {});

    // A prior scenario's interaction can throw inside the React app and trip its error boundary
    // ("Something went wrong!"), which renders in place of the whole UI (no sidebar/nav at all) and,
    // since the page is shared and never reloaded between scenarios, persists for every scenario that
    // runs after it. Detect that crashed state and reload to recover before this scenario starts.
    const crashed = await fixture.page.locator('text=/Something went wrong!/i').first().isVisible({ timeout: 1000 }).catch(() => false);
    if (crashed) {
        fixture.logger?.warn?.('[Crash Recovery] Error boundary detected from a prior scenario - reloading.');
        console.log('[Crash Recovery] Error boundary detected from a prior scenario - reloading.');
        await fixture.page.reload().catch(() => {});
        await fixture.page.waitForLoadState('networkidle').catch(() => {});
    }

    if (isLoginTest) {
        // Clear cookies and storage so login test runs unauthenticated without destroying the window
        await context.clearCookies().catch(() => {});
        await fixture.page.evaluate(() => {
            try {
                localStorage.clear();
                sessionStorage.clear();
            } catch (_) {}
        }).catch(() => {});

        // Pre-navigate to base URL so about:blank never shows
        const origin = (process.env.BASEURL || "").replace(/\/dashboard\/?$/, '').replace(/\/$/, '');
        if (fixture.page.url() === 'about:blank' || fixture.page.url().includes('/dashboard')) {
            await fixture.page.goto(`${origin}/`);
        }
    } else {
        fixture.logger?.info("🔹 Reusing existing browser page on Dashboard (no reload)");
    }

    try {
        await context.tracing.start({
            name: scenarioName,
            title: pickle.name,
            screenshots: true,
            snapshots: true,
            sources: true,
        });
    } catch (_) {}

    fixture.logger = createLogger(options(scenarioName));
    fixture.logger.info(`🚀 Starting Scenario: ${pickle.name}`);

    // Initialize sub-step logger
    // @ts-ignore
    this.subStepLogger = new HTMLSubStepLogger(this.attach.bind(this));
    fixture.subStepLogger = this.subStepLogger;
    fixture.logger?.info('Sub-step logger initialized for scenario.');

    fixture.pages = {};
    fixture.logger?.info('Page objects initialized for scenario.');
});

// ================================================================
// 🧩 STEP HOOKS — Step-wise logging
// ================================================================
BeforeStep(async function ({ pickleStep }) {
    fixture.logger?.info(`🟡 Step started → ${pickleStep.text}`);
});

AfterStep(async function ({ pickleStep, result }) {
    const stepText = pickleStep.text;
    const status = result?.status || "UNKNOWN";

    if (status === Status.PASSED) {
        fixture.logger?.info(`✅ Step passed → ${stepText}`);
    } else if (status === Status.FAILED) {
        fixture.logger?.error(`❌ Step failed → ${stepText}`);
        if (fixture.page) {
            const screenshot = await fixture.page.screenshot({
                path: `./test-results/screenshots/${Date.now()}_${stepText.replace(/[^a-zA-Z0-9]/g, "_")}.png`,
                type: "png"
            });
            await this.attach(screenshot, "image/png");
        }
    }
});

// ================================================================
// 🧩 AFTER SCENARIO — Attach trace, video, screenshot
// ================================================================
After(async function ({ pickle, result }) {
    // Only proceed if context was successfully initialized in Before hook
    if (context) {
        const scenarioPath = `./test-results/trace/${pickle.id}.zip`;
        await context.tracing.stop({ path: scenarioPath }).catch(() => {});

        if (result?.status === Status.PASSED && fixture.page) {
            const sanitizedName = pickle.name.replace(/[<>:"/\\|?*]/g, '_');
            const img = await fixture.page.screenshot({ 
                path: `./test-results/screenshots/${sanitizedName}.png`, 
                type: "png" 
            });
            await this.attach(img, "image/png");

            const videoPath = await fixture.page.video()?.path();
            if (videoPath && fs.existsSync(videoPath)) {
                const relPath = path.relative(process.cwd(), videoPath).replace(/\\/g, '/');
                await this.attach(`<video controls width="480"><source src="${relPath}" type="video/webm">Video: <a href="${relPath}" target="_blank">${relPath}</a></video>`, "text/html");
            }

            await this.attach(`<a href="https://trace.playwright.dev/" target="_blank">Open Trace</a>`, "text/html");
        } else if (result?.status === Status.FAILED) {
            fixture.logger?.error(`❌ Scenario failed: ${pickle.name}`);
        }
    }
});

// ================================================================
// 🧩 AFTER (tagged) — Restore the shared Super Admin session after any
// scenario that deliberately switches the shared page to a different
// user/role, so later scenarios (which only verify a session exists,
// not which user it belongs to - see sessionGuard.ts) don't silently
// keep running as the wrong role for the rest of the suite.
// ================================================================
After({ tags: "@Key:TC_RAM_026" }, async function () {
    const page = fixture.page;
    if (!page) {
        return;
    }
    try {
        await page.goto("https://dev-dashboard.hunter.igoalzero.com/login", { waitUntil: "domcontentloaded" });
        await page.context().clearCookies();
        await page.evaluate(() => {
            try {
                localStorage.clear();
                sessionStorage.clear();
            } catch (_) { /* no-op */ }
        });
        await page.reload({ waitUntil: "networkidle" });

        const loginPage = new LoginPage(page);
        const adminEmail = process.env.SUPER_ADMIN_EMAIL || process.env.SUPERADMIN_USER || "superadmin@buckhouse.com";
        const adminPassword = process.env.SUPER_ADMIN_PASSWORD || process.env.SUPERADMIN_PASSWORD || "Admin@123!";
        await loginPage.login(adminEmail, adminPassword);
        await page.waitForURL(/.*\/dashboard/, { timeout: 30000 });

        const authFile = path.join(__dirname, "../../.auth/admin.json");
        await page.context().storageState({ path: authFile }).catch(() => {});

        fixture.logger?.info("[TC_RAM_026 cleanup] Restored shared Super Admin session after Ranch Admin role switch.");
    } catch (err) {
        fixture.logger?.error(`[TC_RAM_026 cleanup] Failed to restore Super Admin session: ${err}`);
    }
});

After({ tags: "@Key:SGC081" }, async function () {
    const page = fixture.page;
    if (!page) {
        return;
    }
    try {
        await page.goto("https://dev-dashboard.hunter.igoalzero.com/login", { waitUntil: "domcontentloaded" });
        await page.context().clearCookies();
        await page.evaluate(() => {
            try {
                localStorage.clear();
                sessionStorage.clear();
            } catch (_) { /* no-op */ }
        });
        await page.reload({ waitUntil: "networkidle" });

        const loginPage = new LoginPage(page);
        const adminEmail = process.env.SUPER_ADMIN_EMAIL || process.env.SUPERADMIN_USER || "superadmin@buckhouse.com";
        const adminPassword = process.env.SUPER_ADMIN_PASSWORD || process.env.SUPERADMIN_PASSWORD || "Admin@123!";
        await loginPage.login(adminEmail, adminPassword);
        await page.waitForURL(/.*\/dashboard/, { timeout: 30000 });

        const authFile = path.join(__dirname, "../../.auth/admin.json");
        await page.context().storageState({ path: authFile }).catch(() => {});

        fixture.logger?.info("[SGC081 cleanup] Restored shared Super Admin session after Guide Portal role switch.");
    } catch (err) {
        fixture.logger?.error(`[SGC081 cleanup] Failed to restore Super Admin session: ${err}`);
    }
});

// ================================================================
// 🧩 AFTER ALL — Close browser
// ================================================================
AfterAll(async function () {
    if (fixture.page && !fixture.page.isClosed()) {
        await fixture.page.close().catch(() => {});
    }
    if (context) {
        await context.close().catch(() => {});
    }
    if (browser) {
        await browser.close();
        console.log("🧹 Browser closed after all scenarios.");
    }
});