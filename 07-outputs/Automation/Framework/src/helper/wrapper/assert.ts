import { expect, Page } from "@playwright/test";

export default class Assert {
    constructor(private page: Page) { }

    async assertTitleContains(title: string) {
        console.log(`Asserting page title contains: ${title}`);
        await expect(this.page).toHaveTitle(new RegExp(title));
    }

    async assertElementVisible(locator: string, timeoutMs: number = 15000) {
        console.log(`Asserting element is visible: ${locator}`);
        await expect(this.page.locator(locator)).toBeVisible({ timeout: timeoutMs });
    }

    async assertTextContains(locator: string, expectedText: string) {
        console.log(`Asserting element ${locator} contains text: ${expectedText}`);
        const text = await this.page.locator(locator).textContent();
        expect(text?.toLowerCase()).toContain(expectedText.toLowerCase());
    }

    // Soft assertion won't stop the test if it fails
    async softAssertTextContains(locator: string, expectedText: string) {
        console.log(`Soft asserting element ${locator} contains text: ${expectedText}`);
        const text = await this.page.locator(locator).textContent();
        expect.soft(text?.toLowerCase()).toContain(expectedText.toLowerCase());
    }

    // Checking arrays of text (useful for search results)
    async assertArrayContains(textArray: string[], expectedText: string) {
        console.log(`Asserting at least one result contains text: ${expectedText}`);
        expect(textArray.length).toBeGreaterThan(0);
        
        // Check the first element for example
        const firstElementText = textArray[0].toLowerCase();
        expect(firstElementText).toContain(expectedText.toLowerCase());
    }
}