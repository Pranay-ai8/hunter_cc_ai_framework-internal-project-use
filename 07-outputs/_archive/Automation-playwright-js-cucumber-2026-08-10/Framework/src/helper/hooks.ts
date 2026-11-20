import { Before, After, BeforeAll, AfterAll, setDefaultTimeout } from '@cucumber/cucumber';
import { chromium, Browser } from '@playwright/test';
import { ScenarioContext } from '../utils/ScenarioContext';

setDefaultTimeout(30 * 1000);

let browser: Browser;

BeforeAll(async () => {
  browser = await chromium.launch({ headless: process.env.HEADED !== 'true' });
});

Before(async function (this: ScenarioContext) {
  this.browser = browser;
  this.context = await browser.newContext();
  this.page = await this.context.newPage();
});

After(async function (this: ScenarioContext) {
  await this.context?.close();
});

AfterAll(async () => {
  await browser?.close();
});
