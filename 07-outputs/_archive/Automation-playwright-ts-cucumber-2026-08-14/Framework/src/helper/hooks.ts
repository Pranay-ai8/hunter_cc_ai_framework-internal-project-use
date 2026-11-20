import * as fs from 'fs';
import * as path from 'path';
import {
  AfterAll,
  AfterStep,
  Before,
  BeforeAll,
  BeforeStep,
  After,
  Status,
  setDefaultTimeout,
} from '@cucumber/cucumber';
import { Browser, BrowserContext } from '@playwright/test';
import { BrowserFactory } from './browser-factory';
import { saveAuthState } from './auth-setup';
import { ScenarioContext } from '../utils/ScenarioContext';
import { HTMLSubStepLogger } from '../utils/htmlSubStepLogger';
import { SoftAssert } from '../utils/assert';
import { CSVParser } from '../utils/CSVParser';
import { logger } from '../utils/logger';
import { ResultStore } from '../report/email-html/ResultStore';
import { generateEmailSummary } from '../report/email-html/HtmlEmailReport';

// Default Cucumber step timeout (5000ms) races too closely against Playwright's own
// default 5000ms assertion-retry window when running against the real live network
// (as opposed to a local app) — a slow-but-passing assertion can otherwise be aborted by
// Cucumber's timer first, masking the real Playwright error with a generic "function timed
// out" message. Widen the step budget so Playwright's own timeout/error always surfaces first.
setDefaultTimeout(20000);

let sharedBrowser: Browser;

BeforeAll({ timeout: 60000 }, async function () {
  sharedBrowser = await BrowserFactory.launch();
  await saveAuthState(sharedBrowser);
});

Before({ timeout: 30000 }, async function (this: ScenarioContext, scenario) {
  this.logger = logger;
  this.subStepLogger = new HTMLSubStepLogger();
  this.softAssert = new SoftAssert();
  this.testData = {};

  const tags = scenario.pickle.tags.map((t) => t.name);
  const dataFileTag = tags.find((t) => t.startsWith('@DataFile:'));
  const keyTag = tags.find((t) => t.startsWith('@Key:'));
  const isAuthenticated = tags.includes('@authenticated');

  if (dataFileTag && keyTag) {
    const dataFile = path.resolve(process.cwd(), dataFileTag.replace('@DataFile:', ''));
    const key = keyTag.replace('@Key:', '');
    if (fs.existsSync(dataFile)) {
      try {
        this.testData = CSVParser.loadRowByKey(dataFile, key);
      } catch (err) {
        logger.warn(`Before hook: ${(err as Error).message}`);
      }
    } else {
      logger.warn(`Before hook: data file not found: ${dataFile}`);
    }
  }

  const contextOptions: Parameters<Browser['newContext']>[0] = {};
  if (isAuthenticated && fs.existsSync(path.resolve(process.cwd(), '.auth/user.json'))) {
    contextOptions.storageState = path.resolve(process.cwd(), '.auth/user.json');
  }

  this.context = (await sharedBrowser.newContext(contextOptions)) as BrowserContext;
  this.page = await this.context.newPage();
});

BeforeStep(async function (this: ScenarioContext, { pickleStep }) {
  await this.subStepLogger?.info(`STEP: ${pickleStep.text}`);
});

AfterStep(async function (this: ScenarioContext, { result, pickleStep }) {
  if (result?.status === Status.FAILED) {
    await this.subStepLogger?.logFailure(`FAILED STEP: ${pickleStep.text}`);
    if (this.page) {
      const dir = path.resolve(process.cwd(), 'test-results/screenshots');
      if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
      const file = path.join(dir, `failure-${Date.now()}.png`);
      try {
        await this.page.screenshot({ path: file, fullPage: true });
        this.attach(fs.readFileSync(file), 'image/png');
      } catch (err) {
        logger.warn(`Screenshot capture failed: ${(err as Error).message}`);
      }
    }
  }
});

After({ timeout: 15000 }, async function (this: ScenarioContext, scenario) {
  try {
    if (this.softAssert?.hasFailures()) {
      const failures = this.softAssert.getFailures();
      this.softAssert.reset();
      throw new Error(`Soft-assertion failures accumulated:\n${failures.map((f, i) => `  ${i + 1}. ${f}`).join('\n')}`);
    }
  } finally {
    const status =
      scenario.result?.status === Status.PASSED
        ? 'passed'
        : scenario.result?.status === Status.SKIPPED
          ? 'skipped'
          : 'failed';
    ResultStore.add({ name: scenario.pickle.name, status });
    await this.context?.close().catch(() => undefined);
  }
});

AfterAll(async function () {
  await sharedBrowser?.close();
  const file = generateEmailSummary();
  logger.info(`Email-style run summary written to ${file}`);
});
