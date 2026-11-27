import { Page, BrowserContext } from "@playwright/test";

export default class DocusealPage {
    /**
     * Opens the DocuSeal signing link in its own page (it's a public, unauthenticated hunter-facing
     * page unrelated to the admin session on `fixture.page`), types `signerName` as the signature,
     * and completes the signing. Returns the page so the caller can close it afterward.
     */
    async signAs(context: BrowserContext, signingUrl: string, signerName: string): Promise<Page> {
        const page = await context.newPage();
        // networkidle can take a while here (the signing page keeps a background poll alive), so give
        // it room, then fall back to just waiting for the Sign Now control instead of failing the goto.
        await page.goto(signingUrl, { waitUntil: "domcontentloaded", timeout: 30000 });

        const signNowBtn = page.getByText('SIGN NOW', { exact: false }).first();
        await signNowBtn.waitFor({ state: "visible", timeout: 30000 });
        await signNowBtn.click();
        await page.locator('span', { hasText: /^TYPE$/i }).first().click({ force: true });
        await page.locator('input[placeholder="Type signature here..."]').fill(signerName);

        const completeBtn = page.locator('button:has-text("SIGN AND COMPLETE")');
        await completeBtn.waitFor({ state: "visible", timeout: 10000 });
        await completeBtn.click();
        await page.waitForTimeout(2000);

        return page;
    }
}
