import { Page, BrowserContext, expect } from "@playwright/test";

export default class CompleteProfilePage {
    private page!: Page;

    /**
     * Opens the "Complete Your Profile" link in its own page (it's a public, unauthenticated
     * hunter-facing page unrelated to the admin session on `fixture.page`, same as DocusealPage).
     * Returns the page so the caller can close it afterward.
     */
    async open(context: BrowserContext, profileUrl: string): Promise<Page> {
        this.page = await context.newPage();
        await this.page.goto(profileUrl, { waitUntil: "domcontentloaded", timeout: 30000 });
        await expect(this.page.locator('h1', { hasText: /Complete Your Profile/i })).toBeVisible({ timeout: 15000 });
        return this.page;
    }

    async verifyActionRequiredBanner() {
        await expect(this.page.locator('text=/Action Required/i').first()).toBeVisible();
    }

    // Fills whichever top-level fields the "Action Required" banner lists as missing (phone, license
    // number, license expiration) - the rest (name, email, DOB, address) arrive pre-filled from the
    // booking/hunter record and are left untouched.
    async fillRequiredContactAndLicenseFields(phone: string, license: string, licenseExpiration: string) {
        const phoneInput = this.page.locator('input.react-international-phone-input').first();
        await phoneInput.click();
        const cleanPhone = phone.replace(/\D/g, '').replace(/^1/, '');
        await phoneInput.pressSequentially(cleanPhone, { delay: 10 });

        await this.page.locator('input[name="license"]').fill(license);

        const licenseExpBtn = this.page.locator('label').filter({ hasText: /^License Expiration/ }).first()
            .locator('..').locator('button').first();
        await licenseExpBtn.click();
        await this.page.waitForTimeout(500);
        await this.pickDateInOpenCalendar(licenseExpiration);
    }

    async clickAddMinor() {
        await this.page.locator('button', { hasText: 'Add Minor' }).click();
        await this.page.waitForTimeout(500);
    }

    // Fills the most recently added Minor Hunters card (Name and Date of Birth are the only fields
    // required for a minor - License Number/Expiration are optional per the form).
    async fillMinorDetails(name: string, dateOfBirth: string) {
        await this.page.locator('input[placeholder="Minor\'s full name"]').last().fill(name);

        const minorDobBtn = this.page.locator('label').filter({ hasText: /^Date of Birth/ }).last()
            .locator('..').locator('button').first();
        await minorDobBtn.click();
        await this.page.waitForTimeout(500);
        await this.pickDateInOpenCalendar(dateOfBirth);
    }

    async verifyMinorAdded(name: string) {
        await expect(this.page.locator('input[value="' + name + '"]').or(this.page.getByText(name)).first()).toBeVisible();
    }

    async clickSaveAndFinalize() {
        await this.page.locator('button', { hasText: 'Save and Finalize' }).click();
        await this.page.waitForTimeout(2000);
    }

    async verifyProfileCompletedSuccessfully() {
        // The "Action Required" banner (and its field checklist) disappears once every required field
        // is filled and saved - its absence is the clearest confirmation the save actually succeeded,
        // since a validation failure re-shows it with "Please fix the validation errors".
        await expect(this.page.locator('text=/Action Required/i')).toBeHidden({ timeout: 15000 });
    }

    // Shared by both date pickers on this page (License Expiration, Minor Date of Birth). Confirmed live
    // this calendar has no native <select> month/year dropdowns (unlike the admin-side wizard's - see
    // BookingsPage.pickDateInCalendar) - its month/year are button-triggered dropdowns instead, so this
    // navigates via the Previous/Next Month chevrons, reading the visible "Month YYYY" text to compute
    // how many clicks are needed, with a nudge pass afterward in case a click didn't register.
    private async pickDateInOpenCalendar(mmddyyyy: string) {
        const [monthStr, dayStr, yearStr] = mmddyyyy.split('/');
        const targetDay = parseInt(dayStr, 10).toString();
        const targetMonth = parseInt(monthStr, 10) - 1;
        const targetYear = parseInt(yearStr, 10);

        const calendar = this.page.locator('.rdp, [role="dialog"]').last();
        const monthYearPattern = /(January|February|March|April|May|June|July|August|September|October|November|December)\s+(\d{4})/;
        const readMonthYear = async (): Promise<{ year: number; month: number }> => {
            const text = await calendar.innerText().catch(() => '');
            const match = text.match(monthYearPattern);
            if (!match) return { year: NaN, month: NaN };
            return { year: parseInt(match[2], 10), month: new Date(`${match[1]} 1, 2000`).getMonth() };
        };

        const prevBtn = calendar.locator('button[aria-label*="Previous Month" i]').first();
        const nextBtn = calendar.locator('button[aria-label*="Next Month" i]').first();

        const start = await readMonthYear();
        if (!isNaN(start.year)) {
            const monthsAway = (targetYear - start.year) * 12 + (targetMonth - start.month);
            const btn = monthsAway < 0 ? prevBtn : nextBtn;
            for (let i = 0; i < Math.abs(monthsAway); i++) {
                await btn.click();
                await this.page.waitForTimeout(80);
            }
            for (let nudge = 0; nudge < 6; nudge++) {
                const now = await readMonthYear();
                const diff = (now.year - targetYear) * 12 + (now.month - targetMonth);
                if (diff === 0) break;
                await (diff > 0 ? prevBtn : nextBtn).click();
                await this.page.waitForTimeout(100);
            }
        }

        const grid = this.page.locator('table[role="grid"]').last();
        const dayBtn = grid.locator(`td[role="gridcell"]:not(.day-outside) button:text-is("${targetDay}")`).first();
        await dayBtn.click({ force: true });
        await this.page.waitForTimeout(200);
    }
}
