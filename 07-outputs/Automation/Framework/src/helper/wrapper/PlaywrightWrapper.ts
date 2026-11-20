import { Page } from "@playwright/test";

export default class PlaywrightWrapper {
  constructor(private page: Page) {}

  async goto(url: string, options?: any) {
    console.log(`Navigating to URL: ${url}`);
    for (let attempt = 1; attempt <= 3; attempt++) {
      try {
        await this.page.goto(url, options);
        return;
      } catch (error: any) {
        if (attempt < 3 && (error?.message?.includes('ERR_NAME_NOT_RESOLVED') || error?.message?.includes('ERR_CONNECTION') || error?.message?.includes('Timeout'))) {
          console.warn(`Navigation to ${url} failed (attempt ${attempt}/3). Retrying in 1s... Error: ${error.message}`);
          await this.page.waitForTimeout(1000);
        } else {
          throw error;
        }
      }
    }
  }

  async click(locator: string) {
    console.log(`Waiting for element ${locator} and clicking it`);
    await this.page.locator(locator).waitFor({ state: "visible" });
    await this.page.locator(locator).click();
  }

  async type(locator: string, text: string) {
    console.log(`Typing "${text}" into element ${locator}`);
    await this.page.locator(locator).waitFor({ state: "visible" });
    await this.page.locator(locator).fill(text);
  }

  async pressKey(key: string) {
    console.log(`Pressing keyboard key: ${key}`);
    await this.page.keyboard.press(key);
  }

  async getElementsText(locator: string): Promise<string[]> {
    console.log(`Getting text contents from all elements matching: ${locator}`);
    await this.page.waitForSelector(locator);
    return await this.page.locator(locator).allTextContents();
  }
}
