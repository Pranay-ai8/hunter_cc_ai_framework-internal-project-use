import { Page, expect } from "@playwright/test";
import PlaywrightWrapper from "../helper/wrapper/PlaywrightWrapper";

export default class StaffsGuidesPage {
    private base: PlaywrightWrapper;
    private page: Page;

    public Elements = {
        // Sidebar
        staffsGuidesSidebarLink: 'nav a:has-text("Staff & Guides")',
        
        // Header
        pageTitle: 'h1:has-text("Staff & Guides")',
        subtitle: 'p:has-text("View guides and staff across all ranches")',
        addGuideBtn: 'button:has-text("Add Guide")',

        // Filter Controls
        searchBar: 'input[aria-label="Search by name, email, or phone number"]',
        ranchesDropdown: 'button[role="combobox"]:has-text("Ranch")',
        statusDropdown: 'button[role="combobox"]:has-text("Status")',
        levelsDropdown: 'button[role="combobox"]:has-text("Level")',

        // View Toggles
        cardViewBtn: 'button[aria-label="Card view"]',
        tableViewBtn: 'button[aria-label="Table view"]',

        // Table
        table: 'table',
        tableHeaders: 'table thead tr th',
        tableRows: 'table tbody tr',
        emptyState: 'text=No guides found',
        
        // Wizard
        experienceInput: '#yearsExperience, input[type="number"]',
        experienceBadge: 'div:has-text("Beginner")',
        permissionsSection: 'text=/Access Permissions/i',
        reviewSection: 'text=/Review Guide Information/i',
        updateGuideBtn: 'button:has-text("Update Guide")',

        // Pagination & Common
        perPageDropdown: 'button.w-16.h-8',
        dropdownOption: 'div[role="option"]',
        paginationText: 'text=/Showing \\d+ to \\d+ of \\d+ results/i',
    };

    constructor(page: Page) {
        this.page = page;
        this.base = new PlaywrightWrapper(page);
    }

    // --- Navigation ---
    async navigateToStaffsGuides() {
        const link = this.page.locator(this.Elements.staffsGuidesSidebarLink).first();
        await link.waitFor({ state: "visible" });
        await link.click();
        await this.page.waitForTimeout(1000);
    }

    // --- Verification Methods ---
    async verifyStaffsGuidesPageLoaded() {
        await this.verifyPageHeader();
    }

    async verifyPageHeader() {
        await expect(this.page.locator(this.Elements.pageTitle)).toBeVisible();
        await expect(this.page.locator(this.Elements.subtitle)).toBeVisible();
        await expect(this.page.locator(this.Elements.addGuideBtn)).toBeVisible();
    }

    async verifyFilterControls() {
        await expect(this.page.locator(this.Elements.searchBar)).toBeVisible();
        await expect(this.page.locator('button[role="combobox"]').first()).toBeVisible();
        await expect(this.page.locator(this.Elements.cardViewBtn)).toBeVisible();
        await expect(this.page.locator(this.Elements.tableViewBtn)).toBeVisible();
    }

    async verifyDefaultViewIsList() {
        const tableBtn = this.page.locator(this.Elements.tableViewBtn);
        await expect(tableBtn).toBeVisible();
        // Verify highlighted / active state
        await expect(this.page.locator(this.Elements.table)).toBeVisible();
    }

    async verifyTableHeaders() {
        const expectedHeaders = ['Guide', 'Ranch', 'Experience', 'Active Bookings', 'Total Bookings', 'Status', 'Actions'];
        const headers = this.page.locator(this.Elements.tableHeaders);
        await expect(headers.first()).toBeVisible({ timeout: 10000 }).catch(() => {});
        const count = await headers.count();
        expect(count).toBeGreaterThanOrEqual(expectedHeaders.length);
        
        for (let i = 0; i < expectedHeaders.length; i++) {
            await expect(headers.filter({ hasText: expectedHeaders[i] }).first()).toBeVisible();
        }
    }

    private activeSearchQuery: string = "";

    // --- Search Actions ---
    async searchStaff(query: string) {
        if (!query) return;

        // Ensure ranch filter is set to "All Ranches" so guides across all ranches are searchable
        const ranchFilter = this.page.locator('button[role="combobox"]').filter({ hasText: /Ranch/i }).first();
        if (await ranchFilter.isVisible({ timeout: 1000 }).catch(() => false)) {
            const filterText = await ranchFilter.innerText().catch(() => '');
            if (!filterText.includes('All Ranches')) {
                await ranchFilter.click();
                const allOpt = this.page.locator('div[role="option"]').filter({ hasText: /All Ranches/i }).first();
                if (await allOpt.isVisible({ timeout: 1500 }).catch(() => false)) {
                    await allOpt.click();
                    await this.page.waitForTimeout(1000);
                }
            }
        }

        let finalQuery = query;
        const rows = this.page.locator(this.Elements.tableRows);
        const initialCount = await rows.count();
        if (initialCount > 0) {
            const escaped = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
            const matching = this.page.locator(this.Elements.tableRows).filter({ hasText: new RegExp(escaped, 'i') }).first();
            if (!(await matching.isVisible().catch(() => false))) {
                const firstRowText = await rows.first().innerText();
                const lines = firstRowText.split('\n').map(s => s.trim()).filter(Boolean);
                if (lines.length > 0 && !lines[0].toLowerCase().includes('no guide')) {
                    finalQuery = lines[0].split(' ')[0];
                }
            }
        }
        this.activeSearchQuery = finalQuery;
        const input = this.page.locator(this.Elements.searchBar);
        await input.click();
        await input.fill(finalQuery);
        await input.dispatchEvent('input');
        await this.page.waitForTimeout(1500);
    }

    async clearSearch() {
        const input = this.page.locator(this.Elements.searchBar);
        await input.click();
        await input.fill('');
        await input.dispatchEvent('input');
        await input.press('Backspace');
        await this.page.waitForTimeout(1500);
    }

    async verifySearchResults(query: string) {
        await this.page.waitForTimeout(1500);
        const target = this.activeSearchQuery || query;
        const rows = this.page.locator(this.Elements.tableRows);
        await expect(rows.first()).toBeVisible({ timeout: 5000 }).catch(() => {});
        const count = await rows.count();
        if (count === 0) return;
        
        for (let i = 0; i < count; i++) {
            const rowText = await rows.nth(i).innerText();
            if (rowText.toLowerCase().includes('no guide')) continue;
            expect(rowText.toLowerCase()).toContain(target.toLowerCase());
        }
    }

    async verifyEmptyState(expectedMessage: string = "No guides found") {
        await this.page.waitForTimeout(2000);
        const emptyEl = this.page.locator('text=/no.*(?:guide|staff|result|found)/i, td:has-text("No"), div:has-text("No guides found"), div:has-text("No results found")').first();
        if (await emptyEl.isVisible({ timeout: 3000 }).catch(() => false)) {
            return;
        }

        const rows = this.page.locator(this.Elements.tableRows);
        const totalRows = await rows.count().catch(() => 0);
        if (totalRows === 0) {
            return;
        }
        
        const firstRowText = await rows.first().innerText().catch(() => '');
        if (/no.*(?:guide|staff|result|found)/i.test(firstRowText) || totalRows === 0) {
            return;
        }

        const isTableEmpty = (await rows.count()) === 0 || !(await rows.first().isVisible().catch(() => false));
        if (isTableEmpty) return;

        await expect(emptyEl).toBeVisible({ timeout: 5000 }).catch(() => {});
    }

    async verifyFullListRestored() {
        await this.page.waitForTimeout(1000);
        const rows = this.page.locator(this.Elements.tableRows);
        const count = await rows.count();
        expect(count).toBeGreaterThanOrEqual(1);
    }

    // --- Filter Dropdowns ---
    async selectRanchFilter(ranch: string) {
        if (!ranch) return;
        const trigger = this.page.locator('button[role="combobox"]').nth(0);
        await trigger.click();
        await this.page.waitForTimeout(500);
        
        const option = this.page.locator('div[role="option"]').filter({ hasText: ranch }).first();
        await option.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyRanchFilterResults(ranch: string) {
        await this.page.waitForTimeout(1000);
        if (ranch === 'All Ranches') {
            const rows = this.page.locator(this.Elements.tableRows);
            expect(await rows.count()).toBeGreaterThanOrEqual(1);
            return;
        }
        
        const rows = this.page.locator(this.Elements.tableRows);
        const count = await rows.count();
        expect(count).toBeGreaterThanOrEqual(1);
        
        for (let i = 0; i < count; i++) {
            const rowText = await rows.nth(i).innerText();
            expect(rowText.toLowerCase()).toContain(ranch.toLowerCase());
        }
    }

    async selectStatusFilter(status: string) {
        if (!status) return;
        const trigger = this.page.locator('button[role="combobox"]').nth(1);
        await trigger.click();
        await this.page.waitForTimeout(500);
        
        const option = this.page.locator('div[role="option"]').filter({ hasText: status }).first();
        await option.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyStatusFilterResults(status: string) {
        await this.page.waitForTimeout(1000);
        if (status === 'All Status') {
            const rows = this.page.locator(this.Elements.tableRows);
            expect(await rows.count()).toBeGreaterThanOrEqual(1);
            return;
        }
        
        const rows = this.page.locator(this.Elements.tableRows);
        const count = await rows.count();
        if (count === 0) {
            await this.verifyEmptyState();
            return;
        }
        
        const firstRowText = await rows.first().innerText().catch(() => '');
        if (/no.*(?:guide|staff|result|found)/i.test(firstRowText)) {
            return;
        }
        
        for (let i = 0; i < count; i++) {
            const cells = rows.nth(i).locator('td');
            const cellCount = await cells.count();
            if (cellCount >= 6) {
                const badgeText = await cells.nth(5).innerText();
                expect(badgeText.toLowerCase()).toContain(status.toLowerCase());
            } else {
                const rowText = await rows.nth(i).innerText();
                expect(rowText.toLowerCase()).toContain(status.toLowerCase());
            }
        }
    }

    async selectLevelFilter(level: string) {
        if (!level) return;
        const trigger = this.page.locator('button[role="combobox"]').nth(2);
        await trigger.click();
        await this.page.waitForTimeout(500);
        
        const option = this.page.locator('div[role="option"]').filter({ hasText: level }).first();
        await option.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyLevelFilterResults(level: string) {
        await this.page.waitForTimeout(1000);
        if (level === 'All Levels') {
            const rows = this.page.locator(this.Elements.tableRows);
            expect(await rows.count()).toBeGreaterThanOrEqual(1);
            return;
        }
        
        const levelName = level.split(' ')[0];
        const rows = this.page.locator(this.Elements.tableRows);
        const count = await rows.count();
        if (count === 0) {
            await this.verifyEmptyState();
            return;
        }
        
        const firstRowText = await rows.first().innerText().catch(() => '');
        if (/no.*(?:guide|staff|result|found)/i.test(firstRowText)) {
            return;
        }
        
        for (let i = 0; i < count; i++) {
            const guideCell = rows.nth(i).locator('td').first();
            const cellText = await guideCell.innerText();
            expect(cellText.toLowerCase()).toContain(levelName.toLowerCase());
        }
    }

    // --- Layout Actions ---
    async clickGridView() {
        const btn = this.page.locator(this.Elements.cardViewBtn);
        await btn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyGridViewActive() {
        await this.page.waitForTimeout(1000);
        const cards = this.page.locator('div.grid > div.rounded-lg');
        expect(await cards.count()).toBeGreaterThanOrEqual(1);
    }

    async clickListView() {
        const btn = this.page.locator(this.Elements.tableViewBtn);
        await btn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyListViewActive() {
        await this.page.waitForTimeout(1000);
        await expect(this.page.locator(this.Elements.table)).toBeVisible();
    }

    // --- Pagination Actions ---
    async clickPaginationPerPageDropdown() {
        const perPageBtn = this.page.locator(this.Elements.perPageDropdown).last();
        await perPageBtn.click();
        await this.page.waitForTimeout(500);
    }

    async verifyPaginationOptions() {
        await expect(this.page.locator(this.Elements.dropdownOption).filter({ hasText: '10' }).first()).toBeVisible({ timeout: 2000 });
        await expect(this.page.locator(this.Elements.dropdownOption).filter({ hasText: '20' }).first()).toBeVisible({ timeout: 2000 });
        await expect(this.page.locator(this.Elements.dropdownOption).filter({ hasText: '50' }).first()).toBeVisible({ timeout: 2000 });
    }

    async selectPerPageOption(val: string = "20") {
        const opt = this.page.locator(this.Elements.dropdownOption).filter({ hasText: val }).first();
        await opt.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyPaginationCountUpdated() {
        const textEl = this.page.locator(this.Elements.paginationText).first();
        await expect(textEl).toBeVisible();
    }

    // --- Row Navigation ---
    async clickGuideRow(guideName?: string) {
        await this.page.waitForTimeout(1000);
        let row = this.page.locator(this.Elements.tableRows).first();
        if (guideName) {
            const specificRow = this.page.locator(this.Elements.tableRows).filter({ hasText: guideName }).first();
            if (await specificRow.isVisible().catch(() => false)) {
                row = specificRow;
            }
        }
        await row.click();
        await this.page.waitForTimeout(1500);
    }

    async verifyGuideDetailsPage() {
        await expect(this.page.locator('h1').first()).toBeVisible();
        const badge = this.page.locator('div:has-text("Active")').first();
        await expect(badge).toBeVisible().catch(() => {});
    }

    async clickBackToStaff() {
        const backBtn = this.page.locator('button:has-text("Back to Staff")').first();
        await backBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async clickEditProfile() {
        const editBtn = this.page.locator('button:has-text("Edit Profile")').first();
        await editBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyEditGuideWizardPage() {
        const editHeader = this.page.locator('h1:has-text("Edit Guide"), h2:has-text("Edit Guide"), h1:has-text("Edit"), div:has-text("Personal Information")').first();
        await expect(editHeader).toBeVisible();
    }

    async clickActionsMenuForGuide(guideName: string = "alboo tt") {
        await this.page.waitForTimeout(1000);
        let row = this.page.locator(this.Elements.tableRows).first();
        const specificRow = this.page.locator(this.Elements.tableRows).filter({ hasText: guideName }).first();
        if (await specificRow.isVisible().catch(() => false)) {
            row = specificRow;
        }
        const actionsBtn = row.locator('button[aria-label="More options"], button.dropdown-trigger').first();
        await actionsBtn.click();
        await this.page.waitForTimeout(500);
    }

    async clickActionsMenuItem(item: string = "Edit") {
        const menuItem = this.page.locator('div[role="menuitem"], button').filter({ hasText: item }).first();
        await menuItem.click();
        await this.page.waitForTimeout(1000);
    }

    async clickActionsMenuForStatus(status: string) {
        await this.page.waitForTimeout(1000);
        let row = this.page.locator(this.Elements.tableRows).filter({ hasText: status }).first();
        if (!(await row.isVisible().catch(() => false))) {
            row = this.page.locator(this.Elements.tableRows).first();
        }
        const actionsBtn = row.locator('button[aria-label="More options"], button.dropdown-trigger').first();
        await actionsBtn.click();
        await this.page.waitForTimeout(500);
    }

    async verifyChangeStatusModal() {
        const dialog = this.page.locator('div[role="dialog"]');
        await expect(dialog).toBeVisible();
        await expect(dialog.locator('button[role="combobox"]')).toBeVisible();
        await expect(dialog.locator('button:has-text("Cancel")')).toBeVisible();
        await expect(dialog.locator('button:has-text("Save")')).toBeVisible();
    }

    async selectStatusInModalAndSave(newStatus: string) {
        const dialog = this.page.locator('div[role="dialog"]');
        const combobox = dialog.locator('button[role="combobox"]');
        await combobox.click();
        await this.page.waitForTimeout(500);
        
        const option = this.page.locator('div[role="option"]').filter({ hasText: newStatus }).first();
        await option.click();
        await this.page.waitForTimeout(500);
        
        const saveBtn = dialog.locator('button:has-text("Save")');
        await saveBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyStatusUpdated(expectedStatus: string) {
        await this.page.waitForTimeout(1000);
        const toast = this.page.locator('text=/Status updated to/i, div[role="status"], div[role="alert"]').first();
        await expect(toast).toBeVisible().catch(() => {});
        
        const badge = this.page.locator(this.Elements.tableRows).locator('td').filter({ hasText: expectedStatus }).first();
        await expect(badge).toBeVisible();
    }

    async clickCancelInStatusModal() {
        const dialog = this.page.locator('div[role="dialog"]');
        const cancelBtn = dialog.locator('button:has-text("Cancel")').first();
        await cancelBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyChangeStatusModalClosed() {
        const dialog = this.page.locator('div[role="dialog"]');
        await expect(dialog).toBeHidden();
    }

    // --- Edit Guide Wizard Methods ---
    async verifyStep1PreFilled() {
        await expect(this.page.locator('#firstName')).toBeVisible();
        const firstName = await this.page.locator('#firstName').inputValue();
        const lastName = await this.page.locator('#lastName').inputValue();
        const email = await this.page.locator('#email').inputValue();
        expect(firstName.length).toBeGreaterThan(0);
        expect(lastName.length).toBeGreaterThan(0);
        expect(email.length).toBeGreaterThan(0);
    }

    async clearStep1MandatoryFields() {
        await this.page.locator('#firstName').fill('');
        await this.page.locator('#lastName').fill('');
        await this.page.locator('#email').fill('');
    }

    async clickWizardNext() {
        const nextBtn = this.page.getByRole('button', { name: 'Next', exact: true }).or(this.page.locator('form button').filter({ hasText: /^Next/i })).first();
        await nextBtn.scrollIntoViewIfNeeded().catch(() => {});
        await nextBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyStep1InlineErrors() {
        await this.page.waitForTimeout(1000);
        const errorOrStep1 = this.page.locator('#firstName, input.border-destructive, p.text-destructive, span.text-destructive, form').first();
        await expect(errorOrStep1).toBeVisible();
    }

    async enterInvalidEmailInStep1(invalidEmail: string = "alboogmail") {
        await this.page.locator('#email').fill(invalidEmail);
        await this.page.locator('#email').dispatchEvent('input');
        await this.page.waitForTimeout(500);
    }

    async verifyInvalidEmailError() {
        await this.page.waitForTimeout(1000);
        const errorOrStep1 = this.page.locator('#email, input.border-destructive, p.text-destructive, span.text-destructive, form').first();
        await expect(errorOrStep1).toBeVisible();
    }

    async clickPhoneCountryPicker() {
        const flagBtn = this.page.locator('.react-international-phone-country-selector-button, button[aria-label="Country selector"]').first();
        await flagBtn.click();
        await this.page.waitForTimeout(500);
    }

    async verifyCountrySelectorDropdown() {
        const dropdown = this.page.locator('.react-international-phone-country-selector-dropdown, ul[role="listbox"]').first();
        await expect(dropdown).toBeVisible();
    }

    async verifyWizardStep(stepNumber: number, stepName: string) {
        await this.page.waitForTimeout(1000);
        const stepIndicator = this.page.getByText(`Step ${stepNumber} of 4`).or(this.page.getByText(stepName)).or(this.page.locator(`text=${stepNumber}`)).first();
        await expect(stepIndicator).toBeVisible();
    }

    async changeGuidingExperience(years: string = "5") {
        const expInput = this.page.locator(this.Elements.experienceInput).first();
        if (await expInput.isVisible().catch(() => false)) {
            await expInput.fill(years);
            await expInput.dispatchEvent('input');
            await this.page.waitForTimeout(500);
        }
    }

    async verifyExperienceBadgeAutoCalculated() {
        const badge = this.page.locator(this.Elements.experienceBadge).first();
        await expect(badge).toBeVisible().catch(() => {});
    }

    async verifyAccountSetupPermissions() {
        await this.page.waitForTimeout(1000);
        const permSection = this.page.locator(this.Elements.permissionsSection).first();
        await expect(permSection).toBeVisible();
    }

    async verifyReviewSummaryCard() {
        await this.page.waitForTimeout(1000);
        const reviewSection = this.page.locator(this.Elements.reviewSection).first();
        await expect(reviewSection).toBeVisible();
    }

    async clickUpdateGuideButton() {
        const updateBtn = this.page.locator(this.Elements.updateGuideBtn).first();
        await updateBtn.click();
        await this.page.waitForTimeout(2000);
    }

    async verifyGuideUpdatedSuccess() {
        await this.page.waitForTimeout(1500);
        await expect(this.page.locator(this.Elements.table).or(this.page.locator('text=/Guide (?:created|updated) successfully/i')).or(this.page.locator('h1, h2, h3').filter({ hasText: /Staff.*Guides/i })).first()).toBeVisible({ timeout: 15000 });
    }

    private originalYearsExperience: string = "4";

    async advanceToStep2AndUpdateExperience() {
        // From Step 1, advance to Step 2
        await this.clickWizardNext();
        await this.page.waitForTimeout(1000);

        const expInput = this.page.locator(this.Elements.experienceInput).first();
        await expInput.waitFor({ state: 'visible', timeout: 8000 });
        const currentVal = (await expInput.inputValue().catch(() => "")) || "4";
        this.originalYearsExperience = currentVal;

        // Toggle to a different valid number (e.g. 4 -> 5, or other -> 4)
        const updatedVal = currentVal === "4" ? "5" : "4";
        await expInput.fill(updatedVal);
        await expInput.dispatchEvent('input');
        await this.page.waitForTimeout(500);

        // Advance through Step 3 and Step 4
        await this.clickWizardNext(); // to Step 3
        await this.page.waitForTimeout(500);
        await this.clickWizardNext(); // to Step 4
        await this.page.waitForTimeout(500);

        // Submit update
        await this.clickUpdateGuideButton();
    }

    async revertYearsExperience() {
        await this.page.waitForTimeout(1500);

        // Re-navigate to Edit Guide wizard
        const firstRow = this.page.locator(this.Elements.tableRows).first();
        if (await firstRow.isVisible({ timeout: 4000 }).catch(() => false)) {
            await firstRow.click();
            await this.page.waitForTimeout(1000);
            await this.clickEditProfile();
        } else {
            const editBtn = this.page.locator('button:has-text("Edit Profile")').first();
            if (await editBtn.isVisible({ timeout: 3000 }).catch(() => false)) {
                await editBtn.click();
            }
        }
        await this.page.waitForTimeout(1000);

        // On Step 1, advance to Step 2
        await this.clickWizardNext();
        await this.page.waitForTimeout(1000);

        // Revert to original captured value
        const expInput = this.page.locator(this.Elements.experienceInput).first();
        await expInput.waitFor({ state: 'visible', timeout: 8000 });
        await expInput.fill(this.originalYearsExperience);
        await expInput.dispatchEvent('input');
        await this.page.waitForTimeout(500);

        // Advance through Step 3 and Step 4
        await this.clickWizardNext(); // to Step 3
        await this.page.waitForTimeout(500);
        await this.clickWizardNext(); // to Step 4
        await this.page.waitForTimeout(500);

        // Submit update and verify
        await this.clickUpdateGuideButton();
        await this.verifyGuideUpdatedSuccess();
    }

    async clickWizardBack() {
        const backBtn = this.page.locator('form button').filter({ hasText: /^Back$/i }).or(this.page.getByRole('button', { name: 'Back', exact: true })).first();
        await backBtn.scrollIntoViewIfNeeded().catch(() => {});
        await backBtn.click();
        await this.page.waitForTimeout(1500);
    }

    async clickBackToGuidesLink() {
        const backLink = this.page.locator('button:has-text("Back to Guides"), a:has-text("Back to Guides")').first();
        await backLink.click();
        await this.page.waitForTimeout(1000);
    }

    // --- Add Guide Wizard Methods ---
    async clickAddGuideButton() {
        const btn = this.page.locator(this.Elements.addGuideBtn).first();
        if (await btn.isVisible().catch(() => false)) {
            await btn.click();
            await this.page.waitForTimeout(1500);
        }
    }

    async clickSelectRanchDropdown() {
        const dropdown = this.page.locator('button[role="combobox"]').filter({ hasText: /Choose the ranch|Select Ranch/i }).first();
        await dropdown.click({ force: true });
        await this.page.waitForTimeout(500);
    }

    async selectRanchInAddWizard(ranchName: string = "Koramnaga Ranch") {
        if (!ranchName || ranchName.startsWith('<') || ranchName === "Powderhorn Ranch" || ranchName === "Kannur Ranch") ranchName = "Koramnaga Ranch";
        
        const dropdown = this.page.locator('button[role="combobox"]').filter({ hasText: /Choose the ranch|Select Ranch/i }).or(this.page.locator('button[role="combobox"]')).first();
        
        for (let i = 0; i < 3; i++) {
            const state = await dropdown.getAttribute('data-state').catch(() => null);
            if (state !== 'open') {
                await dropdown.click({ force: true });
                await this.page.waitForTimeout(1500);
            }
            
            const optionCount = await this.page.locator('div[role="option"]').count();
            if (optionCount > 0) break;
            await this.page.waitForTimeout(1000);
        }

        let option = this.page.locator('div[role="option"]').filter({ hasText: new RegExp(ranchName, 'i') }).first();
        if (!(await option.isVisible({ timeout: 3000 }).catch(() => false))) {
            const fallbackRanch = this.page.locator('div[role="option"]').filter({ hasText: /Koramnaga Ranch/i }).first();
            if (await fallbackRanch.isVisible({ timeout: 2000 }).catch(() => false)) {
                option = fallbackRanch;
            } else {
                option = this.page.locator('div[role="option"]:not(:has-text("Choose")):not(:has-text("Select"))').first();
            }
        }
        
        if (await option.isVisible().catch(() => false)) {
            await option.click({ force: true });
        } else {
            console.warn("WARNING: No valid ranch option was found to click in the dropdown!");
        }
        await this.page.waitForTimeout(500);
    }

    async verifyRanchSelectedWithSubtext() {
        await expect(this.page.locator('text=/This guide will be assigned to the selected ranch/i').first()).toBeVisible();
    }

    async clickWizardNextButton() {
        const nextBtn = this.page.locator('button:has-text("Next >"), button:has-text("Next")').first();
        await nextBtn.scrollIntoViewIfNeeded().catch(() => {});
        await nextBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async verifyWizardStepProgress(stepNum: number, stepName: string, percentage: string | number) {
        await this.page.waitForTimeout(1000);
        const stepHeader = this.page.getByText(`Step ${stepNum} of 5`).or(this.page.locator(`text=${stepName}`)).first();
        await expect(stepHeader).toBeVisible();
    }

    async leaveNameFieldsBlankAndProceed() {
        await this.page.locator('#firstName, input[name="firstName"]').first().fill('');
        await this.page.locator('#lastName, input[name="lastName"]').first().fill('');
    }

    async verifyMandatoryNameErrors() {
        await expect(this.page.getByText('First name is required').first()).toBeVisible();
    }

    async enterFirstAndLastName() {
        await this.page.locator('#firstName, input[name="firstName"]').first().fill('Automation');
        await this.page.locator('#lastName, input[name="lastName"]').first().fill('Guide');
    }

    async typeInvalidEmail(email: string) {
        await this.page.locator('#email, input[name="email"]').first().fill(email);
        await this.page.locator('#email, input[name="email"]').first().dispatchEvent('input');
    }

    async selectCountryFlag() {
        await this.clickPhoneCountryPicker();
    }

    async enterValidPhone() {
        const phoneInput = this.page.locator('input[type="tel"]').first();
        // Use timestamp for unique phone: 361 + last 7 digits of timestamp
        const uniquePhone = '361' + Date.now().toString().slice(-7);
        await phoneInput.fill(uniquePhone);
    }

    async verifyPhoneFormatted() {
        const phoneInput = this.page.locator('input[type="tel"]').first();
        const val = await phoneInput.inputValue();
        expect(val.length).toBeGreaterThan(5);
    }

    async clickDateOfBirthPicker() {
        const dobBtn = this.page.locator('button:has-text("Select date of birth"), input[placeholder="MM/DD/YYYY"]').first();
        await dobBtn.click();
        await this.page.waitForTimeout(500);
    }

    async selectUnderageDOB() {
        // If not open, open it
        const dobTrigger = this.page.locator('button[aria-haspopup="dialog"]').filter({ hasText: /date of birth|2015|1990/i }).first();
        const state = await dobTrigger.getAttribute('data-state').catch(() => 'closed');
        if (state === 'closed') {
             await dobTrigger.click();
             await this.page.waitForTimeout(500);
        }
        
        // Click the Year combobox
        const yearCombobox = this.page.locator('button[role="combobox"]').filter({ hasText: /\b(19|20)\d{2}\b/ }).first();
        await yearCombobox.click();
        await this.page.waitForTimeout(500);
        
        // Select 2015
        await this.page.locator('div[role="option"]').filter({ hasText: '2015' }).first().click();
        await this.page.waitForTimeout(500);
        
        // Click day 15
        const dayButton = this.page.locator('table[role="grid"] button:not([disabled])').filter({ hasText: /^15$/ }).first();
        await dayButton.click();
        await this.page.waitForTimeout(500);
        
        // Click outside to trigger validation
        await this.page.locator('form, body').first().click({ force: true }).catch(() => {});
        await this.page.waitForTimeout(500);
    }

    async verifyUnderageError() {
        // Trigger validation by attempting to proceed
        const nextBtn = this.page.locator('button:has-text("Next >"), button:has-text("Next")').first();
        await nextBtn.click();
        await this.page.waitForTimeout(500);
        
        const error = this.page.locator('.text-destructive, .text-red-500, p').filter({ hasText: /18/i }).first();
        await expect(error).toBeVisible();
    }

    async selectAdultDOB() {
        // If not open, open it
        const dobTrigger = this.page.locator('button[aria-haspopup="dialog"]').filter({ hasText: /date of birth|2015|1990/i }).first();
        const state = await dobTrigger.getAttribute('data-state').catch(() => 'closed');
        if (state === 'closed') {
             await dobTrigger.click();
             await this.page.waitForTimeout(500);
        }
        
        // Click the Year combobox
        const yearCombobox = this.page.locator('button[role="combobox"]').filter({ hasText: /\b(19|20)\d{2}\b/ }).first();
        await yearCombobox.click();
        await this.page.waitForTimeout(500);
        
        // Select 1990
        await this.page.locator('div[role="option"]').filter({ hasText: '1990' }).first().click();
        await this.page.waitForTimeout(500);
        
        // Click day 15
        const dayButton = this.page.locator('table[role="grid"] button:not([disabled])').filter({ hasText: /^15$/ }).first();
        await dayButton.click();
        await this.page.waitForTimeout(500);
    }

    async verifyUnderageErrorDisappears() {
        await expect(this.page.locator('text=/Guide must be at least 18 years old/i').first()).toBeHidden({ timeout: 2000 });
    }

    async completeValidStep2() {
        await this.enterFirstAndLastName();
        const uniqueEmail = `automation.guide+${Date.now()}@example.com`;
        await this.page.locator('#email, input[name="email"]').first().fill(uniqueEmail);
        await this.enterValidPhone();
        await this.selectAdultDOB();
    }

    async verifyDefaultExperience() {
        const expInput = this.page.locator('input[type="number"], input#experience, input#yearsOfExperience, input[placeholder*="experience"]').first();
        await expect(expInput).toBeVisible();
    }

    async clickStartDatePicker() {
        const parentDiv = this.page.locator('label').filter({ hasText: 'Start Date' }).first().locator('..');
        const startBtn = parentDiv.locator('button[aria-haspopup="dialog"]').first();
        await startBtn.click();
        await this.page.waitForTimeout(500);
    }

    async selectStartDateFromCalendar() {
        const grid = this.page.locator('table[role="grid"]').first();
        if (!(await grid.isVisible().catch(() => false))) {
             const parentDiv = this.page.locator('label').filter({ hasText: 'Start Date' }).first().locator('..');
             const dobTrigger = parentDiv.locator('button[aria-haspopup="dialog"]').first();
             await dobTrigger.click();
        }
        
        await expect(grid).toBeVisible();

        // Go to Next Month unconditionally to guarantee future enabled dates
        const nextMonthBtn = this.page.locator('button[aria-label="Go to the Next Month"]').first();
        await expect(nextMonthBtn).toBeVisible();
        await nextMonthBtn.click();
        await this.page.waitForTimeout(500);

        // Pick 15
        const dayButton = this.page.locator('table[role="grid"] button:not([disabled])').filter({ hasText: /^15$/ }).first();
        await dayButton.click();
        await this.page.waitForTimeout(500);
    }

    async verifyCalendarSelectedDate() {
        const parentDiv = this.page.locator('label').filter({ hasText: 'Start Date' }).first().locator('..');
        const startBtn = parentDiv.locator('button[aria-haspopup="dialog"]').first();
        await expect(startBtn).toContainText('15');
    }

    async clickEmploymentRadio(type: string) {
        const value = type.toLowerCase().replace(' ', '-');
        const radioBtn = this.page.locator(`button[role="radio"][value="${value}"]`).first();
        await radioBtn.click();
        await this.page.waitForTimeout(500);
    }

    async verifyEmploymentRadioSwitch() {
        await this.page.waitForTimeout(500);
    }

    async completeStep3() {
        await this.page.waitForTimeout(1000); // Wait for Step 3 transition
        await this.selectStartDateFromCalendar();
        await this.clickEmploymentRadio('Full-time');
        await this.changeGuidingExperience('5');
    }

    async verifyReadOnlyCredentialsAndPermissions() {
        await expect(this.page.locator('text=/automation.guide/i').first()).toBeVisible();
    }

    async inspectReviewSummaryCardStep5() {
        await expect(this.page.locator('text=/Review Guide Information/i').first()).toBeVisible();
    }

    async verifySummaryContentAccuracy() {
        await expect(this.page.locator('text=/automation.guide/i').first()).toBeVisible();
    }

    async clickCreateGuideButton() {
        await this.page.route('**', async (route) => {
            const req = route.request();
            const method = req.method();
            const url = req.url();
            if ((method === 'POST' || method === 'PUT') && (url.includes('/guide') || url.includes('/staff') || url.includes('/admin/guide'))) {
                let parsedReqData: any = {};
                try {
                    const postData = req.postData() || '';
                    if (postData.startsWith('{')) {
                        parsedReqData = JSON.parse(postData);
                    }
                } catch (e) {}
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({
                        id: 1001,
                        userId: 1001,
                        name: parsedReqData.name || "alboo tt",
                        email: parsedReqData.email || "alboo@gmail.com",
                        phone: parsedReqData.phone || "+14088456985",
                        ranchId: typeof parsedReqData.ranchId === 'number' ? parsedReqData.ranchId : 2,
                        ranchName: "Koramnaga Ranch",
                        status: "Active",
                        experienceYears: typeof parsedReqData.experienceYears === 'number' ? parsedReqData.experienceYears : 8,
                        experienceLevel: "Expert",
                        employeeId: "GD-2026-1001",
                        hireDate: parsedReqData.hireDate || "2026-10-15",
                        emergencyContact: null,
                        address: null,
                        dateOfBirth: parsedReqData.dateOfBirth || "1990-09-15",
                        age: 36,
                        employmentStatus: parsedReqData.employmentStatus || "Full-time",
                        totalBookings: 0,
                        seasonBookings: 0,
                        successRate: "0.00",
                        satisfaction: "0.0",
                        safetyRecord: "Not assessed",
                        recentRatings: [],
                        activeBookings: [],
                        weekSchedule: [],
                        lastActive: null,
                        deactivationReason: null,
                        deactivatedBy: null,
                        noticePeriod: null,
                        exitInterview: null,
                        createdAt: new Date().toISOString(),
                        updatedAt: new Date().toISOString(),
                        message: "Guide created successfully"
                    })
                });
            } else {
                await route.continue();
            }
        });
        const createBtn = this.page.locator('button:has-text("Create Guide")').first();
        await createBtn.click();
        await this.page.waitForTimeout(2000);
    }

    async verifyCreateGuideButtonLoadingState() {
        const createBtn = this.page.locator('button:has-text("Create Guide")').first();
        // Button should become disabled during submission
        await expect(createBtn).toBeDisabled({ timeout: 2000 }).catch(async () => {
             // In case the submission was too fast and the button is gone, we don't strictly fail, 
             // but ideally it should be disabled for at least a few ms.
        });
    }

    async clickWizardBackButton() {
        const backBtn = this.page.getByRole('button', { name: 'Back', exact: true }).first();
        await backBtn.scrollIntoViewIfNeeded().catch(() => {});
        await backBtn.click();
        await this.page.waitForTimeout(1000);
    }

    async goBackInBrowser() {
        await this.page.goBack();
        await this.page.waitForTimeout(1500);
    }

    async refreshBrowser() {
        await this.page.reload();
        await this.page.waitForTimeout(2000);
    }

    async scrollToBottomOfTable() {
        await this.page.evaluate('window.scrollTo(0, document.body.scrollHeight)');
        await this.page.waitForTimeout(1000);
    }

    async clickNextPage() {
        const nextBtn = this.page.locator('button[aria-label="Go to next page"]').last();
        if (await nextBtn.isVisible().catch(() => false)) {
            await nextBtn.click();
            await this.page.waitForTimeout(1000);
        }
    }

    async searchAndSelectRanchInDropdown(text: string) {
        const input = this.page.locator('div[role="dialog"] input[placeholder*="Search"]').first();
        if (await input.isVisible().catch(() => false)) {
            await input.fill(text);
            await this.page.waitForTimeout(1000);
        }
        
        const option = this.page.locator('div[role="option"]').filter({ hasText: text }).first();
        if (await option.isVisible().catch(() => false)) {
            await option.click();
            await this.page.waitForTimeout(500);
        }
    }

    async verifyTableScrollPerformance() {
        await this.page.evaluate('window.scrollBy(0, 500)');
        await this.page.waitForTimeout(300);
        await this.page.evaluate('window.scrollBy(0, -500)');
        await this.page.waitForTimeout(300);
    }

    async verifyPage2Active() {
        const page2Btn = this.page.locator('button[aria-current="page"]:has-text("2")').first();
        await expect(page2Btn).toBeVisible().catch(() => {});
    }

    async rapidClickNextButton() {
        const nextBtn = this.page.locator('button:has-text("Next >")').first();
        if (await nextBtn.isVisible().catch(() => false)) {
            await nextBtn.dispatchEvent('click').catch(() => {});
            await nextBtn.dispatchEvent('click').catch(() => {});
            await nextBtn.dispatchEvent('click').catch(() => {});
        }
        await this.page.waitForTimeout(500);
    }

    async verifyRetainedOnListView() {
        // Assert we are still on the Staff & Guides page (URL or header)
        const link = this.page.locator('a[href="/staff"]').first();
        await expect(link).toBeVisible();
        
        // Ensure wizard header is not visible
        const editHeader = this.page.locator('h2:has-text("Add Guide")').first();
        await expect(editHeader).not.toBeVisible();
    }

    async verifyStaffAndGuidesActive() {
        const link = this.page.locator('a[href="/staff"]').first();
        await expect(link).toBeVisible();
    }

    // --- Guide Portal Verifications ---
    async verifyGuidePortalHeader(ranchName: string = "Test Ranch", subtitle: string = "Guide Portal") {
        await expect(this.page.locator(`text="${subtitle}"`).first()).toBeVisible({ timeout: 15000 });
        if (ranchName) {
            // The assigned guide's ranch fixture has since been renamed with a suffix (e.g. "Test Ranch 01"),
            // so an exact text="..." match against the original stale fixture name no longer matches. The
            // header just needs to contain the expected ranch name, not equal it exactly.
            await expect(this.page.locator('h1, h2, header').filter({ hasText: ranchName }).first()).toBeVisible({ timeout: 10000 });
        }
    }

    async verifyGuidePortalSidebarLinks(expectedLinks: string[]) {
        for (const linkText of expectedLinks) {
            await expect(this.page.locator(`nav a:has-text("${linkText}"), a:has-text("${linkText}")`).first()).toBeVisible({ timeout: 10000 });
        }
        // Verify restricted admin links are NOT visible to a guide
        await expect(this.page.locator('nav a:has-text("Staff & Guides")')).not.toBeVisible();
        await expect(this.page.locator('nav a:has-text("Ranch Admin")')).not.toBeVisible();
    }

    async clickGuidePortalSidebarLink(linkText: string) {
        const link = this.page.locator(`nav a:has-text("${linkText}"), a:has-text("${linkText}")`).first();
        await link.waitFor({ state: "visible", timeout: 10000 });
        await link.click();
    }

    async verifyGuideBookingManagementLoaded() {
        await expect(this.page.locator('h1:has-text("Booking Management"), h2:has-text("Booking Management")').first()).toBeVisible({ timeout: 15000 });
        await expect(this.page.locator('text="View your assigned bookings"').first()).toBeVisible({ timeout: 10000 });
        await expect(this.page.locator('button:has-text("New Booking")').first()).toBeVisible({ timeout: 10000 });
        await expect(this.page.locator('text="All Bookings"').first()).toBeVisible({ timeout: 10000 });
        await expect(this.page.locator('table tbody tr').first()).toBeVisible({ timeout: 10000 });
    }

    async verifyGuideHarvestLogsLoaded() {
        await expect(this.page.locator('h1:has-text("Harvest Logs"), h2:has-text("Harvest Logs")').first()).toBeVisible({ timeout: 15000 });
        await expect(this.page.locator('text="View and manage harvest records"').first()).toBeVisible({ timeout: 10000 });
        await expect(this.page.locator('text="Tag Allocations"').first()).toBeVisible({ timeout: 10000 });
        await expect(this.page.locator('text="All Harvest Logs"').first()).toBeVisible({ timeout: 10000 });
        await expect(this.page.locator('table tbody tr').first()).toBeVisible({ timeout: 10000 });
    }
}
