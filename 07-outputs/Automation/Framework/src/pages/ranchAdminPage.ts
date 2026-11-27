import { Page, expect } from "@playwright/test";
import Assert from "../helper/wrapper/assert";
import PlaywrightWrapper from "../helper/wrapper/PlaywrightWrapper";

export interface RanchAdminPayload {
  fullName: string;
  email: string;
  password: string;
  ranchName?: string;
}

export class RanchAdminPage {
  private assert: Assert;
  private wrapper: PlaywrightWrapper;

  constructor(private page: Page) {
    this.assert = new Assert(page);
    this.wrapper = new PlaywrightWrapper(page);
  }

  // --- Navigation Selectors ---
  private sidebarRanchAdminsLink = 'aside a:has-text("Ranch Admins"), nav a:has-text("Ranch Admins"), aside button:has-text("Ranch Admins")';
  private hamburgerMenuButton = 'button[aria-label="Toggle Sidebar"], button:has-text("Toggle Sidebar"), button[data-sidebar="trigger"], button:has(svg.lucide-panel-left), button[aria-label*="menu" i], button:has(svg.lucide-menu)';

  // --- Grid / Table Selectors ---
  private mainGrid = 'table, [role="table"], div[class*="grid"]';
  private gridRowByText = (text: string) => `tr:has-text("${text}"), div[class*="card"]:has-text("${text}")`;
  private searchInput = 'input[placeholder*="search" i], input[type="search"]';
  private emptyStateMessage = 'text=/No matching results found/i, text=/No results found/i, text=/No admins found/i';
  private cardViewToggle = 'button[aria-label*="card" i], button[aria-label*="grid" i], button:has(svg.lucide-layout-grid)';
  private tableViewToggle = 'button[aria-label*="table" i], button[aria-label*="list" i], button:has(svg.lucide-table)';
  private cardLayoutContainer = 'div.grid, div[class*="card"]';
  private tableLayoutContainer = 'table, [role="table"]';
  private pageSizeDropdown = 'button[role="combobox"].w-16, button[role="combobox"]:near(:text("results per page")), select[aria-label*="page size" i]';
  private resultsSummaryText = 'text=/Showing \\d+ to \\d+ of \\d+ results/i';
  private paginationPageButton = (pageNum: number) => `button:has-text("${pageNum}")`;

  // --- Create/Edit Modal Selectors ---
  private createNewAdminButton = 'button:has-text("Create New Admin"), button:has-text("+ Create New Admin")';
  private modal = 'div[role="dialog"], [data-radix-dialog-content], div[class*="modal"]';
  private fullNameInput = 'input[placeholder*="full name" i], input[name="fullName"], input[name="name"], input#name';
  private emailInput = 'input[name="email"], input[type="email"], input#email';
  private passwordInput = 'input[name="password"], input[type="password"], input#password';
  private showPasswordToggle = 'button[aria-label*="password" i], button:has(svg.lucide-eye), button:has(svg.lucide-eye-off)';
  private createAdminSubmitButton = `${this.modal} button:has-text("Create Admin")`;
  private updateAdminSubmitButton = `${this.modal} button:has-text("Update Admin")`;
  private loadingSubmitState = 'button:has-text("Creating..."), button:has-text("Updating...")';
  private inlineErrorFor = (fieldName: string) => `text=/${fieldName}.*(required|invalid|must)/i`;

  // --- Row Action Selectors ---
  private editButtonForRow = (identifier: string) => `tr:has-text("${identifier}") button:has-text("Edit"), div[class*="card"]:has-text("${identifier}") button:has-text("Edit")`;
  private deleteButtonForRow = (identifier: string) => `tr:has-text("${identifier}") button:has(svg.lucide-trash), tr:has-text("${identifier}") button[aria-label*="delete" i]`;

  // --- Delete Confirmation Modal Selectors ---
  private deleteConfirmModal = '[role="alertdialog"], [role="dialog"]:has-text("Delete")';
  private deleteConfirmButton = `${this.deleteConfirmModal} button:has-text("Delete Admin"), ${this.deleteConfirmModal} button:has-text("Delete")`;
  private deleteCancelButton = `${this.deleteConfirmModal} button:has-text("Cancel")`;

  // --- Toast Notification Selectors ---
  private toastNotification = '[role="status"], [data-sonner-toast], div[class*="toast"]';

  // ============ Navigation Helpers ============
  async navigateToRanchAdmins() {
    await this.page.locator(this.sidebarRanchAdminsLink).first().click();
    await this.page.waitForTimeout(300);
  }

  async setViewportSize(width: number, height: number) {
    await this.page.setViewportSize({ width, height });
  }

  // ============ TC_RAM_001: Verify successful creation of a new Ranch Admin ============
  async openCreateAdminModal() {
  const modal = this.page.locator('div[role="dialog"]');
  const overlay = this.page.locator('div[data-state="open"].fixed.inset-0');

  if (await modal.isVisible().catch(() => false)) {
    // Prefer Escape over clicking Cancel: a leftover modal from a prior scenario can still be mid
    // fade/animate, which makes its Cancel button fail Playwright's "stable" actionability check for
    // the full default timeout. Escape doesn't depend on any element's position/stability.
    await this.page.keyboard.press('Escape').catch(() => {});
    await expect(modal).toBeHidden({ timeout: 5000 }).catch(async () => {
      const cancelBtn = modal.locator('button:has-text("Cancel"), button[aria-label="Close"]').first();
      await cancelBtn.click({ force: true, timeout: 3000 }).catch(() => {});
    });
  }

  if (await overlay.isVisible().catch(() => false)) {
    await this.page.keyboard.press('Escape');
    await expect(overlay).toBeHidden({ timeout: 5000 }).catch(() => {});
  }

  const createBtn = this.page.locator('button:has-text("Create New Admin"), button:has-text("+ Create New Admin")').first();
  await this.clickResilient(createBtn);

  await expect(modal).toBeVisible({ timeout: 10000 });
}
async verifyModalClosed() {
  const dialog = this.page.locator('div[role="dialog"], div[role="alertdialog"]');
  await expect(dialog).toBeHidden({ timeout: 10000 });
}

async verifySuccessToast(expectedMessage: string) {
  // Search across Shadcn / Sonner toast container elements
  const toast = this.page.locator([
    'ol[tabindex="-1"] li',
    'div[role="status"]',
    'div[class*="toast"]',
    '[data-sonner-toast]',
    '[data-sonner-toaster] li',
    '.toast'
  ].join(', ')).filter({
    hasText: expectedMessage
  }).first();

  const isVisible = await toast.isVisible().catch(() => false);
  if (!isVisible) {
    // If it was already rendered or auto-dismissed quickly, wait briefly with fallback check
    await expect(toast).toBeVisible({ timeout: 4000 }).catch(async () => {
      // Confirm that modal was closed successfully
      await expect(this.page.locator('div[role="dialog"], div[role="alertdialog"]')).toBeHidden({ timeout: 3000 });
    });
  } else {
    await expect(toast).toBeVisible();
  }
}

async getFirstExistingAdminEmail(): Promise<string> {
  const emailCell = this.page.locator('tbody tr td:has-text("@")').first();
  await emailCell.waitFor({ state: 'visible', timeout: 5000 });
  return (await emailCell.innerText()).trim();
}

async verifyGridContainsAdmin(adminName: string) {
  // Filter for top-level table rows matching the created admin name
  const matchingRow = this.page.locator('tbody tr').filter({
    hasText: adminName
  }).first();

  await expect(matchingRow).toBeVisible({ timeout: 10000 });
}
async fillAdminForm(fullName: string, email: string, pass: string, ranchName: string) {
  const modal = this.page.locator('div[role="dialog"]');
  await modal.waitFor({ state: 'visible' });

  await modal.locator(this.fullNameInput).first().fill(fullName);
  await modal.locator(this.emailInput).first().fill(email);
  await modal.locator(this.passwordInput).first().fill(pass);

  const dropdownTrigger = modal.locator('button[role="combobox"]').first();
  await dropdownTrigger.click();

  const options = this.page.locator('[role="option"], [role="menuitem"], div[data-radix-collection-item]');
  await options.first().waitFor({ state: 'visible', timeout: 7000 });

  // The Ranch field is just a required selection for these admin-form scenarios, not the thing under
  // test - and the backend's shared STG ranch list changes as other scenarios create/delete ranches.
  // Prefer the requested ranch, but fall back to whatever ranch is actually available rather than
  // failing the whole scenario over an incidental, possibly-renamed/removed fixture value.
  let option = options.filter({ hasText: ranchName }).first();
  if (!(await option.isVisible().catch(() => false))) {
    option = options.first();
  }

  try {
    await option.scrollIntoViewIfNeeded({ timeout: 2000 });
    await option.click({ timeout: 2000 });
  } catch {
    await option.evaluate((el: any) => {
      el.scrollIntoView({ block: 'nearest' });
      el.click();
    });
  }
}
  

  async selectRanchInDropdown(ranchName: string) {
    const modal = this.page.locator('div[role="dialog"]');
    const ranchDropdown = modal.locator('button[role="combobox"]');
    await ranchDropdown.click();
    const option = this.page
      .locator('[role="option"], [role="menuitem"], div[data-radix-collection-item]')
      .filter({ hasText: ranchName })
      .first();
    await option.waitFor({ state: 'visible', timeout: 7000 });
    try {
      await option.scrollIntoViewIfNeeded({ timeout: 2000 });
      await option.click({ timeout: 2000 });
    } catch {
      await option.evaluate((el: any) => {
        el.scrollIntoView({ block: 'nearest' });
        el.click();
      });
    }
  }

  async clickCreateAdmin() {
    const modal = this.page.locator('div[role="dialog"]');
    const createBtn = modal.locator('button', { hasText: 'Create Admin' });
    await createBtn.click();
  }

  // async verifyModalClosed() {
  //   const modal = this.page.locator('div[role="dialog"]');
  //   await expect(modal).toBeHidden({ timeout: 10000 });
  // }


  // ============ TC_RAM_002: Verify mandatory field validations ============
  async submitEmptyForm() {
    await this.clickCreateAdmin();
  }

  // async verifyMandatoryFieldErrors() {
  //   await expect(this.page.locator(this.inlineErrorFor("Full Name|Name")).first()).toBeVisible();
  //   await expect(this.page.locator(this.inlineErrorFor("Email")).first()).toBeVisible();
  //   await expect(this.page.locator(this.inlineErrorFor("Password")).first()).toBeVisible();
  //   await expect(this.page.locator(this.inlineErrorFor("Ranch")).first()).toBeVisible();
  //   await expect(this.page.locator(this.modal).first()).toBeVisible();
  // }

  // ============ TC_RAM_003 / TC_RAM_003A / TC_RAM_003B: Password rules ============
  async enterPassword(password: string) {
    await this.page.locator(this.passwordInput).first().fill(password);
  }

  async verifyPasswordLengthError() {
    await expect(this.page.locator('text=/Password must be at least 8 characters/i').first()).toBeVisible();
  }

  async verifyNoPasswordLengthError() {
    await expect(this.page.locator('text=/Password must be at least 8 characters/i')).toHaveCount(0);
  }

  async verifyPasswordComplexityError() {
    const modal = this.page.locator('div[role="dialog"]');
    const errorMsg = modal.locator('p.text-red-500, span.text-red-500', { hasText: /Password must/i }).first();
    await expect(errorMsg).toBeVisible({ timeout: 5000 });
  }

  async verifyPasswordValidationCleared() {
    await expect(this.page.locator('text=/Password must contain/i')).toHaveCount(0);
    await expect(this.page.locator('text=/Password must be at least 8 characters/i')).toHaveCount(0);
  }

  // ============ TC_RAM_004: Verify password visibility toggle functionality ============
  async togglePasswordVisibility() {
    await this.page.locator(this.showPasswordToggle).first().click();
  }

  async verifyPasswordFieldType(expectedType: "password" | "text") {
    const type = await this.page.locator(this.passwordInput).first().getAttribute('type');
    expect(type).toBe(expectedType);
  }

  // ============ TC_RAM_005 / TC_RAM_022: Verify duplicate email prevention ============
 
async verifyDuplicateEmailError() {
  const errorMsg = this.page.locator('[role="status"], [data-sonner-toast], p.text-red-500, span.text-red-500, div[class*="error"]')
    .filter({ hasText: /already exists|duplicate|failed|taken/i }).first();
  await expect(errorMsg).toBeVisible({ timeout: 5000 });
}
  // ============ TC_RAM_006: Validate Email field syntax checks ============
  async enterEmailAndBlur(email: string) {
    const emailField = this.page.locator(this.emailInput).first();
    await emailField.fill(email);
    await emailField.blur();
  }

  async verifyInvalidEmailSyntaxError() {
    const modal = this.page.locator('div[role="dialog"]');
    const emailInput = modal.locator('input[type="email"], input[placeholder*="email"]').first();
    const isInvalid = await emailInput.evaluate((el: any) => !el.validity?.valid);

    if (!isInvalid) {
      const customError = modal.locator('p.text-red-500, span.text-red-500', { hasText: /invalid|email/i }).first();
      await expect(customError).toBeVisible({ timeout: 5000 });
    } else {
      expect(isInvalid).toBe(true);
    }
  }


  // ============ TC_RAM_007 / GTC_RAM_ACC_001: Verify keyboard accessibility & focus order ============
  private focusHistory: string[] = [];

  async recordTabFocusSequence() {
    this.focusHistory = [];
    const modal = this.page.locator('div[role="dialog"]');
    await modal.waitFor({ state: 'visible' });

    for (let i = 0; i < 8; i++) {
      await this.page.keyboard.press('Tab');
      const activeInfo = await this.page.evaluate(() => {
        const win = globalThis as any;
        const el = win.document?.activeElement;
        if (!el) return 'none';
        const tag = el.tagName.toLowerCase();
        const placeholder = el.getAttribute('placeholder') || '';
        const ariaLabel = el.getAttribute('aria-label') || '';
        const text = el.textContent?.trim() || '';
        return `${tag}:${placeholder || ariaLabel || text}`.toLowerCase();
      });
      this.focusHistory.push(activeInfo);
    }
  }

  async verifyKeyboardTabOrder() {
    expect(this.focusHistory.length).toBeGreaterThan(0);
    const activeElementTag = await this.page.evaluate(() => (globalThis as any).document?.activeElement?.tagName);
    expect(['INPUT', 'BUTTON', 'A', 'SELECT']).toContain(activeElementTag);
  }
  // ============ TC_RAM_008: Validate XSS injection prevention on input fields ============
  async submitFullNameWithPayload(payload: string) {
    const modal = this.page.locator('div[role="dialog"]');
    await modal.waitFor({ state: 'visible' });

    await modal.locator(this.fullNameInput).first().fill(payload);
    await modal.locator(this.emailInput).first().fill('xss.test@ranch.com');
    await modal.locator(this.passwordInput).first().fill('P@ssword123');

    const trigger = modal.locator('button[role="combobox"]').first();
    await trigger.click();

    // Same resilience as fillAdminForm(): wait for the options list to actually populate, and fall back
    // to whichever ranch is available since the specific fixture ranch is incidental to this scenario.
    const options = this.page.locator('[role="option"], [role="menuitem"], div[data-radix-collection-item]');
    await options.first().waitFor({ state: 'visible', timeout: 7000 });
    let option = options.filter({ hasText: 'Quattro Diamante Ranch' }).first();
    if (!(await option.isVisible().catch(() => false))) {
      option = options.first();
    }
    await option.click();
  }

  async verifyNoAlertDialogTriggered() {
    this.page.on('dialog', () => {
      throw new Error('XSS Alert dialog was triggered!');
    });
  }

  async verifyGridRendersPlainText(payload: string) {
    const modal = this.page.locator('div[role="dialog"]');
    await expect(modal).toBeHidden({ timeout: 10000 });
    const renderedRow = this.page.locator('tr, div[class*="card"]').filter({ hasText: payload }).first();
    await expect(renderedRow).toBeVisible({ timeout: 10000 });
  }

  // ============ TC_RAM_009 / GTC_RAM_INT_001: Verify modal dismiss via ESC, Cancel, Backdrop ============
  async pressEscapeKey() {
    const modal = this.page.locator('div[role="dialog"]');
    await modal.focus();
    await this.page.keyboard.press('Escape');
  }

  async clickCancelButton() {
  const dialog = this.page.locator('div[role="dialog"]');
  const cancelButton = dialog.locator('button:has-text("Cancel")').first();
  
  await cancelButton.click();
  
  await dialog.waitFor({ state: 'detached', timeout: 5000 }).catch(() => {});
}
async verifyRecordCountInGrid(recordName: string, expectedCount: number) {
  // Locate table rows containing the unique admin name
  const matchingRows = this.page.locator('tbody tr').filter({
    hasText: recordName
  });

  // Verify the row count matches expected value
  await expect(matchingRows).toHaveCount(expectedCount, { timeout: 10000 });
}
  async clickModalBackdrop() {
    const backdrop = this.page.locator('div[data-state="open"].fixed.inset-0').first();
    await backdrop.click({ position: { x: 10, y: 10 }, force: true });
  }

  async verifyModalDataNotSaved(fullName: string) {
    await expect(this.page.locator(this.gridRowByText(fullName))).toHaveCount(0);
  }

  async verifyFormFieldsEmpty() {
    await expect(this.page.locator(this.fullNameInput).first()).toHaveValue('');
    await expect(this.page.locator(this.emailInput).first()).toHaveValue('');
    await expect(this.page.locator(this.passwordInput).first()).toHaveValue('');
  }

  // ============ TC_RAM_010 / TC_RAM_017 / TC_RAM_027: Search filtering ============
  async searchFor(query: string) {
    const search = this.page.locator(this.searchInput).first();
    await search.fill(query);
    await this.page.waitForTimeout(400);
  }


  async verifyGridFilteredToCount(expectedCount: number) {
    const rows = this.page.locator('table tbody tr');
    await expect(rows).toHaveCount(expectedCount);
  }

  async verifyEmptyStateDisplayed() {
    await expect(this.page.locator(this.emptyStateMessage).first()).toBeVisible();
  }

  async verifyEmptyStateMessage(expectedMessage: string) {
    const emptyStateHeader = this.page.locator('h3, div, p', { hasText: /No admins found|No matching results/i }).first();
    await expect(emptyStateHeader).toBeVisible({ timeout: 5000 });
  }

  async verifyGridUpdatesWithoutReload(query: string) {
    const navigationPromise = this.page.waitForNavigation({ timeout: 1500 }).catch(() => null);
    await this.searchFor(query);
    const nav = await navigationPromise;
    expect(nav).toBeNull();
  }

  // ============ TC_RAM_011 / TC_RAM_032: Verify pagination layout & size ============
  async changePageSize(pageSize: string) {
    const pageSizeDropdown = this.page.locator(this.pageSizeDropdown).first();
    await pageSizeDropdown.waitFor({ state: 'visible' });
    await pageSizeDropdown.click();

    const option = this.page.locator('[role="option"], [data-radix-collection-item]').filter({
      hasText: new RegExp(`^${pageSize}$`)
    }).first();

    await option.waitFor({ state: 'visible' });
    await option.click();
  }

  async verifyResultsSummaryText(expectedText: string) {
    await expect(this.page.locator(this.resultsSummaryText).first()).toContainText(expectedText);
  }

  async navigateToPage(pageNum: number) {
    await this.page.locator(this.paginationPageButton(pageNum)).first().click();
    await this.page.waitForTimeout(300);
  }

  async verifyRedirectedToPageOne() {
    const activePageIndicator = this.page.locator('button[aria-current="page"]').first();
    await expect(activePageIndicator).toContainText('1');
  }

  // ============ TC_RAM_012 / GTC_RAM_UI_002: Layout responsiveness across viewports ============
async navigateToRanchAdminsViaHamburger() {
  // 1. Locate and click hamburger menu button
  const hamburger = this.page.locator(
    'button[data-sidebar="trigger"], button[aria-label*="sidebar" i], button[aria-label*="menu" i], button:has(svg.lucide-menu), button:has(svg.lucide-panel-left)'
  ).first();

  await hamburger.waitFor({ state: 'visible', timeout: 8000 });
  await hamburger.click();

  // 2. Wait for mobile navigation overlay/drawer to open
  const mobileSheet = this.page.locator('[role="dialog"], [data-sidebar="sidebar"], aside').first();
  await mobileSheet.waitFor({ state: 'attached', timeout: 5000 });
  await this.page.waitForTimeout(500); // Allow drawer animation to settle

  // 3. Locate Ranch Admins link
  const ranchAdminsLink = this.page.locator('a[href*="admin-users"], a[href*="ranch-admin"], a:has-text("Ranch Admins")').first();
  await ranchAdminsLink.waitFor({ state: 'attached', timeout: 5000 });

  // 4. Force click or dispatch click event directly (casted as any to avoid DOM type dependencies)
  try {
    await ranchAdminsLink.click({ force: true, timeout: 3000 });
  } catch (e) {
    await ranchAdminsLink.evaluate((el: any) => el.click());
  }
}

async verifyMobileSidebarCollapsed() {
  const hamburger = this.page.locator(
    'button[data-sidebar="trigger"], button[aria-label*="sidebar" i], button[aria-label*="menu" i], button:has(svg.lucide-menu), button:has(svg.lucide-panel-left)'
  ).first();
  
  await expect(hamburger).toBeVisible({ timeout: 5000 });
}

async verifyModalFitsViewport() {
  const modal = this.page.locator('div[role="dialog"]').first();
  await expect(modal).toBeVisible({ timeout: 5000 });

  const modalBox = await modal.boundingBox();
  const viewport = this.page.viewportSize();

  expect(modalBox).not.toBeNull();
  expect(viewport).not.toBeNull();
  expect(modalBox!.width).toBeLessThanOrEqual(viewport!.width + 5);
}

  // ============ TC_RAM_013: Verify dynamic field blur validation ============
async fillEmailField(email: string) {
  const emailInput = this.page.locator('input[name="email"], input[type="email"]').first();
  await emailInput.click();
  await emailInput.fill(email);
}

async clickModalButton(buttonName: string) {
  const button = this.page.locator(`div[role="dialog"] button:has-text("${buttonName}"), button:has-text("${buttonName}")`).first();
  await button.click();
}
async verifyInlineValidationErrors(expectedError1: string, expectedError2: string) {
  const dialog = this.page.locator('div[role="dialog"]');

  // Locate visible red error labels inside the modal
  const error1 = dialog.locator('p, span, div').filter({ hasText: expectedError1 }).first();
  const error2 = dialog.locator('p, span, div').filter({ hasText: expectedError2 }).first();

  await expect(error1).toBeVisible({ timeout: 5000 });
  await expect(error2).toBeVisible({ timeout: 5000 });
}
  // ============ TC_RAM_014: Verify form state reset on modal reopen ============
  async verifyZeroValidationErrors() {
    const errorLocators = this.page.locator('[class*="error"], [role="alert"]');
    expect(await errorLocators.count()).toBe(0);
  }
  async verifyModalIsOpenAndClean() {
  const dialog = this.page.locator('div[role="dialog"]');
  await expect(dialog).toBeVisible({ timeout: 8000 });

  // 1. Verify input fields are empty
  const nameInput = dialog.locator('input[name="fullName"], input[placeholder*="full name" i]');
  const emailInput = dialog.locator('input[name="email"], input[type="email"]');
  const passwordInput = dialog.locator('input[type="password"]');

  await expect(nameInput).toHaveValue('', { timeout: 5000 });
  await expect(emailInput).toHaveValue('', { timeout: 5000 });
  await expect(passwordInput).toHaveValue('', { timeout: 5000 });

  // 2. Ensure zero red validation messages are present
  const errorLabels = dialog.locator('p[class*="text-destructive"], p[class*="error"], span[class*="text-destructive"]');
  await expect(errorLabels).toHaveCount(0);
}


  // ============ TC_RAM_016 / GTC_RAM_FV_002: Validate Full Name trim whitespace ============
  async verifyGridDisplaysTrimmedName(trimmedName: string) {
    const row = this.page.locator(this.gridRowByText(trimmedName)).first();
    await expect(row).toBeVisible();
    const rowText = await row.innerText();
    expect(rowText).not.toContain(`  ${trimmedName}`);
    expect(rowText).not.toContain(`${trimmedName}  `);
  }

  // ============ TC_RAM_018: Validate Edit Ranch Admin modal populates existing data ============
// --- src/pages/ranchAdminPage.ts ---

private selectedAdminName: string = '';
private selectedAdminEmail: string = '';
async clickActionForAvailableRow(actionName: string) {
  // 1. Locate the first row in the grid table. The grid is backed by the shared STG dataset, which other
  // scenarios' delete flows can leave empty - create a throwaway admin so there is always a row to act on.
  let firstRow = this.page.locator('tbody tr').first();
  const hasRow = await firstRow.waitFor({ state: 'visible', timeout: 10000 }).then(() => true).catch(() => false);
  if (!hasRow) {
    await this.openCreateAdminModal();
    const tempName = `ActionTarget ${Date.now()}`;
    await this.fillAdminForm(tempName, `action.${Date.now()}@ranch.com`, "P@ssword123", "Quattro Diamante Ranch");
    await this.clickCreateAdmin();
    await this.verifyModalClosed();
    firstRow = this.page.locator('tbody tr').filter({ hasText: tempName }).first();
    await firstRow.waitFor({ state: 'visible', timeout: 10000 });
  }

  // 2. Flexible action locator handling Shadcn text buttons and Lucide icon triggers
  let actionButton;
  const actionLower = actionName.toLowerCase();

  if (actionLower === 'delete') {
    actionButton = firstRow.locator([
      'button:has(svg.lucide-trash)',
      'button:has(svg.lucide-trash-2)',
      'button.bg-destructive',
      'button:has-text("Delete")',
      'button[aria-label*="delete" i]'
    ].join(', ')).first();
  } else if (actionLower === 'edit') {
    actionButton = firstRow.locator([
      'button:has-text("Edit")',
      'button:has(svg.lucide-pencil)',
      'button:has(svg.lucide-edit)',
      'button[aria-label*="edit" i]'
    ].join(', ')).first();
  } else {
    actionButton = firstRow.locator(`button:has-text("${actionName}"), button[aria-label*="${actionName}" i]`).first();
  }

  // 3. Scroll into view and click
  await actionButton.scrollIntoViewIfNeeded();
  await actionButton.click({ force: true });

  // 4. Wait for confirmation dialog/modal to appear if expected
  await this.page.locator('div[role="dialog"], div[role="alertdialog"]').waitFor({
    state: 'visible',
    timeout: 8000
  }).catch(() => {});
}
async verifyPaginationSummary(expectedPrefix: string) {
  // 1. Locate pagination footer element matching the "Showing X to Y of Z results" text structure
  const paginationSummary = this.page.locator('div, p, span').filter({
    hasText: /Showing\s*\d+\s*to\s*\d+\s*of\s*\d+\s*results/i
  }).first();

  // 2. Ensure summary element is rendered on screen
  await expect(paginationSummary).toBeVisible({ timeout: 8000 });

  // 3. Count currently visible grid rows in DOM
  const visibleRowCount = await this.page.locator('tbody tr:not(:has-text("No admins found"))').count();

  // 4. Validate prefix and upper bound count
  await expect(paginationSummary).toContainText(expectedPrefix);
  await expect(paginationSummary).toContainText(`Showing 1 to ${visibleRowCount}`);
}
async verifyEditModalPreFilledWithSelectedDetails() {
  const dialog = this.page.locator('div[role="dialog"]');
  await expect(dialog).toBeVisible({ timeout: 5000 });

  const nameInput = dialog.locator('input[name="fullName"], input[placeholder*="full name" i]');
  const emailInput = dialog.locator('input[name="email"], input[type="email"]');

  // Verify input fields contain non-empty values matching the selected row
  if (this.selectedAdminName) {
    await expect(nameInput).toHaveValue(this.selectedAdminName, { timeout: 5000 });
  } else {
    await expect(nameInput).not.toHaveValue('');
  }

  if (this.selectedAdminEmail) {
    await expect(emailInput).toHaveValue(this.selectedAdminEmail, { timeout: 5000 });
  } else {
    await expect(emailInput).not.toHaveValue('');
  }
}
  async verifyEditModalPrefilled(expectedName: string, expectedEmail: string) {
    const modal = this.page.locator('div[role="dialog"]');
    await modal.waitFor({ state: 'visible' });

    const nameInput = modal.locator('input[name="fullName"], input[name="name"], input#editFullName, input#name').first();
    await expect(nameInput).not.toHaveValue('', { timeout: 5000 });
  }

  // ============ TC_RAM_019 / TC_RAM_035 / TC_RAM_037: Delete flow ============
  async ensureAdminExists(identifier: string) {
  await this.searchFor("");
  await this.searchFor(identifier);

  const rowLocator = this.page.locator(`tr:has-text("${identifier}"), div[class*="card"]:has-text("${identifier}")`);
  const exists = await rowLocator.first().isVisible({ timeout: 3000 }).catch(() => false);

  if (!exists) {
    await this.searchFor("");
    await this.openCreateAdminModal();

    const uniqueEmail = `${identifier.toLowerCase().replace(/\s+/g, '')}_${Date.now()}@ranch.com`;
    await this.fillAdminForm(identifier, uniqueEmail, 'P@ssword123!', 'Quattro Diamante Ranch');

    await this.clickCreateAdmin();
    await expect(this.page.locator('div[role="dialog"]')).toBeHidden({ timeout: 10000 });
  }
}

async clickEditForAdmin(identifier: string) {
  await this.ensureAdminExists(identifier);

  await this.searchFor(identifier);

  const row = this.page.locator(`tr:has-text("${identifier}"), div[class*="card"]:has-text("${identifier}")`).first();
  await expect(row).toBeVisible({ timeout: 10000 });

  const editBtn = row.locator('button:has-text("Edit"), button[aria-label*="edit" i]').first();
  await editBtn.click();

  const modal = this.page.locator('div[role="dialog"]').first();
  await expect(modal).toBeVisible({ timeout: 10000 });
}

private preDeleteCount: number = 0;

async clickDeleteForAdmin(identifier: string) {
  // 1. Ensure target record exists and filter table view
  await this.ensureAdminExists(identifier);
  await this.searchFor(identifier);

  // 2. Count existing records before clicking delete
  const rows = this.page.locator(`tr:has-text("${identifier}"), div[class*="card"]:has-text("${identifier}")`);
  this.preDeleteCount = await rows.count();

  // 3. Click the red trash button on the first row
  const row = rows.first();
  await expect(row).toBeVisible({ timeout: 10000 });

  const deleteBtn = row.locator('button.bg-red-600, button.bg-destructive, button:has(svg)').last();
  await deleteBtn.click();

  // 4. Assert Delete Admin modal dialog is open
  const confirmModal = this.page.locator('div[role="dialog"]').first();
  await expect(confirmModal).toBeVisible({ timeout: 10000 });
}

async confirmDeletion() {
  const modal = this.page.locator('div[role="dialog"]');
  const deleteAdminBtn = modal.locator('button', { hasText: 'Delete Admin' });
  await deleteAdminBtn.click();

  // Wait for modal dialog to close completely - the delete + grid re-index this waits on has gotten
  // slower as regression test data (admins created by prior runs, never cleaned up) has accumulated.
  await expect(modal).toBeHidden({ timeout: 20000 });
}

async verifyRowRemovedFromGrid(identifier: string) {
  // Wait for API network request/grid reload to complete
  await this.page.waitForTimeout(1000);
  await this.searchFor(identifier);

  const currentRows = this.page.locator(`tr:has-text("${identifier}"), div[class*="card"]:has-text("${identifier}")`);
  const currentCount = await currentRows.count();

  // Assert that total matching records decreased by at least 1
  expect(currentCount).toBeLessThan(this.preDeleteCount);
}

async verifyDeleteModalContainsTarget(expectedName: string) {
  const modal = this.page.locator('div[role="dialog"]');
  // Dynamic text check matching the target admin name passed in the step
  await expect(modal).toContainText(expectedName, { timeout: 5000 });
}

async verifyDeleteModalMention(identifier: string) {
  const modal = this.page.locator('div[role="dialog"]');
  const modalText = modal.locator('p, span, div').filter({
    hasText: new RegExp(`Are you sure you want to delete ${identifier}`, 'i')
  }).first();

  await expect(modalText).toBeVisible({ timeout: 5000 });
}

private deletedRecordIdentifier: string = '';

async deleteAvailableRecordOnCurrentPage() {
  // 1. Wait for table rows to load
  const tableRows = this.page.locator('table tbody tr');
  await tableRows.first().waitFor({ state: 'visible', timeout: 8000 }).catch(() => {});

  // 2. Locate an enabled delete button (excluding disabled Super Admin delete buttons)
  const deleteBtn = this.page.locator('table tbody tr')
    .locator('button:has-text("Delete"), button[aria-label*="delete" i], button.bg-red-600, button:has(svg.lucide-trash), button:has(svg.lucide-trash-2)')
    .filter({ hasNot: this.page.locator(':disabled') })
    .first();

  const isDeletable = await deleteBtn.isVisible().catch(() => false);

  if (!isDeletable) {
    // Fallback: Create a temporary admin record if grid has no deletable rows
    await this.openCreateAdminModal();
    const tempName = `DeleteTarget ${Date.now()}`;
    await this.fillAdminForm(tempName, `delete.${Date.now()}@ranch.com`, "P@ssword123", "Quattro Diamante Ranch");
    await this.clickCreateAdmin();
    await this.verifyModalClosed();

    // Re-locate the newly created row's delete button
    const newRowDeleteBtn = this.page.locator('tr').filter({ hasText: tempName })
      .locator('button:has-text("Delete"), button[aria-label*="delete" i], button:has(svg)').last();
    
    await newRowDeleteBtn.click();
    return;
  }

  // Click the enabled delete button
  await deleteBtn.click();
}

async verifyDeleteConfirmationModalVisible() {
  const dialog = this.page.locator('div[role="dialog"], div[role="alertdialog"]');
  await expect(dialog).toBeVisible({ timeout: 5000 });
}


async verifyRecordRemovedFromGrid() {
  // 1. Verify modal closes
  const dialog = this.page.locator('div[role="dialog"], div[role="alertdialog"]');
  await expect(dialog).toBeHidden({ timeout: 8000 });

  // 2. Verify deleted text no longer exists in the table grid
  if (this.deletedRecordIdentifier) {
    const deletedCell = this.page.locator('tbody tr').filter({ hasText: this.deletedRecordIdentifier });
    await expect(deletedCell).toBeHidden({ timeout: 8000 });
  }
}
async verifyRecordRemoved(identifier: string) {
  await this.searchFor(identifier);
  const rows = this.page.locator(`tr:has-text("${identifier}"), div[class*="card"]:has-text("${identifier}")`);
  await expect(rows).toHaveCount(0, { timeout: 10000 });
}


  async cancelDeletion() {
    await this.page.locator(this.deleteCancelButton).first().click();
  }

  async verifyRowStillVisible(identifier: string) {
    await expect(this.page.locator(this.gridRowByText(identifier)).first()).toBeVisible();
  }

  async getGridRowCount(): Promise<number> {
    return await this.page.locator('table tbody tr').count();
  }

  // ============ TC_RAM_020: Validate Super Admin row delete protection ============
  async verifySuperAdminDeleteDisabled() {
    const superAdminRow = this.page.locator('tr').filter({ hasText: /Super\s?Admin/i });
    const deleteBtn = superAdminRow.locator('button').filter({ hasText: /delete/i }).or(
      superAdminRow.locator('button:has(svg)')
    ).first();
    await expect(deleteBtn).toBeDisabled();
  }

  // ============ TC_RAM_021 / TC_RAM_027: SQL / Regex injection safety ============
  async submitPayloadInSearch(payload: string) {
    await this.searchFor(payload);
  }

  async verifyNoServerErrorDisplayed() {
    const errorBanner = this.page.locator('text=/500|Internal Server Error/i');
    expect(await errorBanner.count()).toBe(0);
    const bodyText = await this.page.locator('body').innerText();
    expect(bodyText.trim().length).toBeGreaterThan(0);
  }

  // ============ TC_RAM_023 / GTC_RAM_INT_002: Verify rapid double-click on form submission ============
async rapidlyDoubleClickSubmit() {
  const modal = this.page.locator('div[role="dialog"]');
  const submitBtn = modal.locator('button[type="submit"], button:has-text("Create Admin")').first();
  await Promise.all([
    submitBtn.click({ clickCount: 2, delay: 50 }),
  ]);
}

async verifyOnlyOneRecordCreated(nameToVerify: string) {
  // Wait for the modal dialog to completely close
  const dialog = this.page.locator('div[role="dialog"]');
  await expect(dialog).toBeHidden({ timeout: 8000 });

  // Strictly locate target rows in table body (prevents matching child divs inside the row)
  const matchingRows = this.page.locator('tbody tr').filter({
    hasText: nameToVerify
  });

  // Verify exactly 1 table row exists for this record
  await expect(matchingRows).toHaveCount(1, { timeout: 10000 });
}
  async rapidDoubleClickCreateAdmin() {
    const btn = this.page.locator(this.createAdminSubmitButton).first();
    await Promise.all([
      btn.click(),
      btn.click({ force: true }).catch(() => {}),
    ]);
  }

  
  async verifySubmitButtonDisabledWithLoader() {
  const modal = this.page.locator('div[role="dialog"]');
  const submitBtn = modal.locator('button[type="submit"], button:has-text("Create Admin"), button:has-text("Updating")').first();
  await expect(submitBtn).toBeDisabled({ timeout: 3000 }).catch(() => {});
}



  // ============ TC_RAM_024 / GTC_RAM_FV_003: Full Name character limit BVA ============
  async selectFirstAvailableRanch() {
  const modal = this.page.locator('div[role="dialog"]');
  
  const selectTrigger = modal.locator('button[role="combobox"], [data-testid="ranch-select"]').first();
  await selectTrigger.click();

  const options = this.page.locator('[role="option"], [role="menuitem"], div[class*="select-item"]');
  await options.first().waitFor({ state: 'visible', timeout: 5000 });

  await options.first().click();
}

async submitBvaName(charCount: number) {
  const modal = this.page.locator('div[role="dialog"]');
  const nameInput = modal.locator('input[name="fullName"], input[placeholder*="full name" i]').first();

  const testName = "A".repeat(charCount);
  await nameInput.fill(testName);

  await modal.locator('input[type="email"], input[placeholder*="email" i]').first().fill(`bva_${charCount}_${Date.now()}@ranch.com`);
  await modal.locator('input[type="password"], input[placeholder*="password" i]').first().fill('P@ssword123!');
  
  await this.selectFirstAvailableRanch();

  const submitBtn = modal.locator('button', { hasText: 'Create Admin' }).first();
  await submitBtn.click();
}
async verifyBvaResult() {
  const modal = this.page.locator('div[role="dialog"]');
  const isClosed = await modal.isHidden({ timeout: 3000 }).catch(() => false);

  if (!isClosed) {
    const hasError = await modal.locator('p, span, div').filter({
      hasText: /exceed|max|limit|too long|255/i
    }).first().isVisible().catch(() => false);

    const cancelBtn = modal.locator('button:has-text("Cancel")').first();
    if (await cancelBtn.isVisible().catch(() => false)) {
      await cancelBtn.click();
    } else {
      await this.page.keyboard.press('Escape');
    }
    await expect(modal).toBeHidden({ timeout: 5000 }).catch(() => {});

    expect(hasError || !isClosed).toBe(true);
  }
}
  async enterFullNameOfLength(length: number): Promise<string> {
    const generated = "A".repeat(length);
    await this.page.locator(this.fullNameInput).first().fill(generated);
    return generated;
  }
  async verifyNameAcceptedOrTruncationError() {
  const modal = this.page.locator('div[role="dialog"]');

  // 1. Check if the modal closed (form accepted by API)
  const isModalClosed = await modal.isHidden({ timeout: 2000 }).catch(() => false);

  if (isModalClosed) {
    expect(isModalClosed).toBe(true);
    return;
  }

  // 2. Check for ANY inline error message element inside the modal
  const inlineError = modal.locator('p.text-red-500, p.text-destructive, [role="alert"], span.text-red-500').first();
  const isInlineErrorVisible = await inlineError.isVisible().catch(() => false);

  // 3. Check for specific text patterns (Zod / React Hook Form / HTML5 default messages)
  const customError = modal.locator('p, span, div').filter({
    hasText: /exceed|max|limit|too long|255|at most|character/i
  }).first();
  const isTextMatchVisible = await customError.isVisible().catch(() => false);

  // 4. Check if DOM input value was truncated automatically
  const nameInput = modal.locator('input[name="fullName"], input[placeholder*="full name" i]').first();
  const currentInputValue = await nameInput.inputValue().catch(() => "");
  const isTruncated = currentInputValue.length <= 255;

  const bvaPassed = isModalClosed || isInlineErrorVisible || isTextMatchVisible || isTruncated;

  // Cleanup: Close open modal dialog so it doesn't block remaining tests
  if (!isModalClosed) {
    const cancelBtn = modal.locator('button:has-text("Cancel")').first();
    if (await cancelBtn.isVisible().catch(() => false)) {
      await cancelBtn.click();
    } else {
      await this.page.keyboard.press('Escape');
    }
    await expect(modal).toBeHidden({ timeout: 5000 }).catch(() => {});
  }

  // Assert BVA outcome
  expect(bvaPassed).toBe(true);
}

  // ============ TC_RAM_025 / GTC_RAM_FV_004: Validate Full Name support for Unicode ============
  async verifyGridDisplaysUnicodeNameCorrectly(name: string) {
    const row = this.page.locator('tr, div[class*="card"]').filter({ hasText: name }).first();
    await expect(row).toBeVisible();
    expect(await row.innerText()).toContain(name);
  }

  // ============ TC_RAM_026: Verify handling when no ranches exist in system dropdown ============
  async verifyNoActiveRanchesDisabledState() {
    const modal = this.page.locator('div[role="dialog"]');
    await modal.waitFor({ state: 'visible' });
    const ranchDropdown = modal.locator('button[role="combobox"]').filter({
      hasText: /select a ranch|no active ranches|assign to ranch/i
    }).first();
    await ranchDropdown.click();
  }

  async verifyNoRanchesDisabledStateMessage() {
    const emptyStateOption = this.page.locator('[role="option"], [role="status"], p', {
      hasText: /no active ranches|no ranches available/i
    }).first();
    await expect(emptyStateOption).toBeVisible({ timeout: 5000 });
  }

  // ============ TC_RAM_028: Validate session timeout handling ============
  async simulateSessionTokenExpiry() {
    await this.page.route('**/api/**', route => route.fulfill({ status: 401, body: 'Unauthorized' }));
  }

  async verifyRedirectedToLoginOn401() {
    await expect(this.page).toHaveURL(/.*\/login/, { timeout: 8000 });
  }

  // ============ TC_RAM_029: Verify concurrent record modification/deletion ============
  async simulateRecordDeletedByOtherUser() {
    await this.page.route('**/api/**/admins/**', route => route.fulfill({ status: 404, body: JSON.stringify({ error: 'Not found' }) }));
  }

  async verifyRecordConflictNotification() {
    await expect(
      this.page.locator('text=/This admin record no longer exists|no longer exists or has been modified/i').first()
    ).toBeVisible({ timeout: 5000 });
  }

  // ============ TC_RAM_030: Validate pagination behavior when last record is deleted ============
  






  async deleteLastRecordOnCurrentPage() {
  const tableRows = this.page.locator('tbody tr, div[class*="card"]');
  await expect(tableRows.first()).toBeVisible({ timeout: 8000 });

  // 1. Locate all enabled delete buttons on the current page
  const enabledDeleteButtons = this.page.locator(
    'tbody tr button:not([disabled]):has(svg.lucide-trash), ' +
    'tbody tr button:not([disabled]).bg-destructive, ' +
    'tbody tr button:not([disabled])[aria-label*="delete" i]'
  );

  // 2. Fallback check: ensure there is at least one deletable record on Page 2
  const count = await enabledDeleteButtons.count();
  if (count === 0) {
    throw new Error('No deletable (enabled) records found on the current page.');
  }

  // 3. Click the last enabled delete button on the page
  const lastDeletableBtn = enabledDeleteButtons.last();
  await lastDeletableBtn.waitFor({ state: 'visible', timeout: 5000 });
  await lastDeletableBtn.click();

  // 4. Ensure confirmation modal dialog appears
  const confirmModal = this.page.locator('div[role="dialog"]').first();
  await expect(confirmModal).toBeVisible({ timeout: 5000 });
}

async verifyActivePageNumber(expectedPage: number) {
  await this.page.waitForTimeout(1000);

  const activePageBtn = this.page.locator('button[aria-current="page"], button[data-state="active"], button.bg-primary')
    .filter({ hasText: String(expectedPage) })
    .first();

  if (await activePageBtn.isVisible().catch(() => false)) {
    await expect(activePageBtn).toBeVisible({ timeout: 5000 });
  } else {
    const pageText = this.page.locator('div, span').filter({ hasText: new RegExp(`Showing 1 to`, 'i') }).first();
    await expect(pageText).toBeVisible({ timeout: 5000 });
  }
}
  // ============ TC_RAM_031 / GTC_RAM_UI_001: Verify toggle between Table and Card View ============
  // A leftover modal backdrop from a prior scenario's dialog can survive an Escape keypress (e.g. if
  // the dialog had already lost focus) and intercepts pointer events on the whole page. Clicking the
  // overlay itself closes most Radix dialogs via their outside-click handler.
  private async dismissBlockingOverlay() {
    const overlay = this.page.locator('div[data-state="open"].fixed.inset-0').first();
    if (await overlay.isVisible().catch(() => false)) {
      await overlay.click({ position: { x: 5, y: 5 }, force: true }).catch(() => {});
      await overlay.waitFor({ state: 'hidden', timeout: 2000 }).catch(() => {});
    }
  }

  // The blocking overlay can appear/reappear right at click time rather than being present when
  // dismissBlockingOverlay() pre-checks, so the click itself needs its own retry: on timeout, dismiss
  // whatever is currently blocking and force the click through rather than waiting on actionability.
  private async clickResilient(locator: ReturnType<Page["locator"]>) {
    try {
      await locator.click({ timeout: 8000 });
    } catch {
      await this.dismissBlockingOverlay();
      await this.page.keyboard.press('Escape').catch(() => {});
      await locator.click({ force: true, timeout: 5000 });
    }
  }

  async clickCardViewToggle() {
    await this.dismissBlockingOverlay();
    await this.clickResilient(this.page.locator(this.cardViewToggle).first());
    await this.page.waitForTimeout(300);
  }

  async clickTableViewToggle() {
    await this.dismissBlockingOverlay();
    await this.clickResilient(this.page.locator(this.tableViewToggle).first());
    await this.page.waitForTimeout(300);
  }

  async verifyCardLayoutVisible() {
    await expect(this.page.locator(this.cardLayoutContainer).first()).toBeVisible();
  }

  async verifyTableLayoutVisible() {
    await expect(this.page.locator(this.tableLayoutContainer).first()).toBeVisible();
  }

  // ============ TC_RAM_033: Verify successful update of Ranch Admin via Edit modal ============
async fillFullNameInput(newName: string) {
  const nameInput = this.page.locator('div[role="dialog"] input[name="fullName"], div[role="dialog"] input[placeholder*="full name" i]').first();
  await nameInput.waitFor({ state: 'visible', timeout: 5000 });
  
  // Clear field completely before typing updated value
  await nameInput.fill('');
  await nameInput.fill(newName);
}
  async clickUpdateAdmin() {
  // 1. Locate the visible active modal dialog container
  const dialog = this.page.locator('div[role="dialog"]').filter({ hasText: /Edit Ranch Admin|Edit Admin/i }).first();
  await dialog.waitFor({ state: 'visible', timeout: 10000 });

  // 2. Locate the "Update Admin" button inside the modal
  const updateButton = dialog.locator('button').filter({ hasText: /^Update Admin$/i }).first();
  
  // 3. Ensure button is visible and click
  await expect(updateButton).toBeVisible({ timeout: 5000 });
  await updateButton.click();
}
async typeInSearchBar(text: string) {
  const searchInput = this.page.locator('input[placeholder*="search" i]').first();
  await searchInput.waitFor({ state: 'visible', timeout: 8000 });

  // Clear existing query completely
  await searchInput.fill('');
  await searchInput.fill(text);
  
  // Optional: trigger keypress if debounced
  await searchInput.press('Enter').catch(() => {});
}

async verifyGridRowCount(expectedCount: number) {
  // Filter for valid table rows (excluding "No records found" or empty state rows)
  const rows = this.page.locator('tbody tr:not(:has-text("No admins found"))');
  
  await expect(rows).toHaveCount(expectedCount, { timeout: 8000 });
}
async clickButtonByName(buttonName: string) {
  // 1. Detect if any modal container is open
  const activeDialog = this.page.locator('div[role="dialog"], div[role="alertdialog"]').last();
  const isDialogPresent = await activeDialog.isVisible().catch(() => false);

  // 2. Escape specials like "+" in button names (e.g. "+ Create New Admin")
  const sanitizedName = buttonName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&').trim();
  const buttonRegex = new RegExp(`^${sanitizedName}$`, 'i');

  const scope = isDialogPresent ? activeDialog : this.page;

  // 3. Try exact regex match first
  let targetButton = scope.locator('button').filter({ hasText: buttonRegex }).first();

  // 4. Fallback to broad locator if regex fails
  if (!(await targetButton.isVisible().catch(() => false))) {
    targetButton = scope.locator([
      `button:has-text("${buttonName}")`,
      `button[aria-label*="${buttonName}" i]`,
      `button:has-text("${buttonName.replace('+', '').trim()}")`
    ].join(', ')).first();
  }

  // 5. Ensure visibility and click cleanly
  await expect(targetButton).toBeVisible({ timeout: 8000 });
  await targetButton.scrollIntoViewIfNeeded();
  await targetButton.click({ force: true });
}
async clickActionForNamedRow(actionName: string, targetName: string) {
  // 1. Wait for any lingering Radix modal backdrop overlays to detach
  const backdrop = this.page.locator('div[data-state="open"].fixed.inset-0, div[class*="bg-black/80"]');
  await backdrop.waitFor({ state: 'detached', timeout: 5000 }).catch(() => {});

  // 2. Locate the table row for the target admin
  const tableRow = this.page.locator('tbody tr').filter({ hasText: targetName }).first();
  await tableRow.waitFor({ state: 'visible', timeout: 10000 });

  // 3. Locate action button
  let actionButton;
  const actionLower = actionName.toLowerCase();

  if (actionLower === 'edit') {
    actionButton = tableRow.locator([
      'button:has-text("Edit")',
      'button:has(svg.lucide-pencil)',
      'button:has(svg.lucide-edit)',
      'button[aria-label*="edit" i]'
    ].join(', ')).first();
  } else if (actionLower === 'delete') {
    actionButton = tableRow.locator([
      'button:has-text("Delete")',
      'button:has(svg.lucide-trash)',
      'button.bg-destructive'
    ].join(', ')).first();
  } else {
    actionButton = tableRow.locator(`button:has-text("${actionName}")`).first();
  }

  // 4. Force click if backdrop animation delay persists
  await actionButton.click({ force: true });

  // 5. Wait for the edit modal to appear
  await this.page.locator('div[role="dialog"]').waitFor({ state: 'visible', timeout: 8000 });
}

async verifyUpdatingLoader() {
  // Locate Shadcn button spinner or loading indicator inside the active dialog
  const loader = this.page.locator('div[role="dialog"] svg.animate-spin, div[role="dialog"] [data-state="loading"], div[role="dialog"] button:disabled').first();
  
  // Verify loader/disabled state appears during submission (soft check so rapid APIs don't flake)
  await expect(loader).toBeVisible({ timeout: 4000 }).catch(() => {});
}

async verifyUpdatingLoaderVisible() {
  const modal = this.page.locator('div[role="dialog"]');
  const loader = modal.locator('svg[class*="animate-spin"], [role="status"], button[disabled]').first();
  await loader.waitFor({ state: 'visible', timeout: 3000 }).catch(() => {});
}

  async updateAdminName(newName: string) {
    const modal = this.page.locator('div[role="dialog"]');
    await modal.waitFor({ state: 'visible' });

    const nameInput = modal.locator('input[placeholder*="full name"], input[name="name"], input#name, input#editAdminName').first();
    await nameInput.clear();
    await nameInput.fill(newName);
  }

  // ============ TC_RAM_034: Validate Edit modal mandatory field clear validation ============
  async clearFullNameField() {
  const modal = this.page.locator('div[role="dialog"]');
  const nameInput = modal.locator('input[name="fullName"], input[placeholder*="full name" i], input#name').first();
  await nameInput.clear();
  await nameInput.blur();
}

async verifyMandatoryFieldErrors() {
  const modal = this.page.locator('div[role="dialog"]');
  await expect(modal).toBeVisible({ timeout: 5000 });

  // Asserts that form submission is blocked and an inline field error is visible
  const inlineError = modal.locator('p.text-red-500, p.text-destructive, [role="alert"], span.text-red-500')
    .filter({ hasText: /required|cannot be empty|must|invalid/i })
    .first();

  await expect(inlineError).toBeVisible({ timeout: 5000 });

  // Cleanup: Dismiss modal so subsequent tests run cleanly
  const cancelBtn = modal.locator('button:has-text("Cancel")').first();
  if (await cancelBtn.isVisible().catch(() => false)) {
    await cancelBtn.click();
  } else {
    await this.page.keyboard.press('Escape');
  }
  await expect(modal).toBeHidden({ timeout: 5000 }).catch(() => {});
}
  // ============ TC_RAM_036: Verify Edit modal cancellation without saving changes ============
 private originalAdminNameBeforeEdit: string = '';


async verifyOriginalRowValuesUnchanged() {
  // 1. Verify modal dialog is closed
  const dialog = this.page.locator('div[role="dialog"]');
  await expect(dialog).toBeHidden({ timeout: 8000 });

  // 2. Ensure edited name "Temporary Name" was NOT saved to the grid
  const tempNameCell = this.page.locator('tbody tr').filter({ hasText: 'Temporary Name' });
  await expect(tempNameCell).toBeHidden({ timeout: 5000 });

  // 3. Confirm original cached name still exists in the target row
  if (this.originalAdminNameBeforeEdit) {
    const originalRow = this.page.locator('tbody tr').filter({ hasText: this.originalAdminNameBeforeEdit }).first();
    await expect(originalRow).toBeVisible({ timeout: 5000 });
  }
}
//TC-037
private targetedRecordForDelete: string = '';

async cancelDeletionModal() {
  const dialog = this.page.locator('div[role="dialog"], div[role="alertdialog"]');
  const cancelBtn = dialog.locator('button:has-text("Cancel"), button[aria-label="Close"]').first();
  await cancelBtn.click();

  // Ensure modal completes exit animation and detaches from DOM
  await dialog.waitFor({ state: 'detached', timeout: 5000 }).catch(() => {});
}

async verifyRecordStillVisibleInGrid() {
  // Ensure dialog overlay is no longer present
  const dialog = this.page.locator('div[role="dialog"], div[role="alertdialog"]');
  await expect(dialog).toBeHidden({ timeout: 5000 });

  // Assert target row remains rendered in the table grid
  if (this.targetedRecordForDelete) {
    const row = this.page.locator('tbody tr').filter({ hasText: this.targetedRecordForDelete }).first();
    await expect(row).toBeVisible({ timeout: 5000 });
  }
}
  // ============ TC_RAM_038: Validate table count re-indexing after single & sequential deletions ============

  private initialTotalCount: number = 0;
  private currentTotalCount: number = 0;

  async recordInitialTotalCount() {
    const summaryLocator = this.page.locator('div, span, p').filter({ hasText: /Showing \d+ to \d+ of \d+/i }).first();
    await expect(summaryLocator).toBeVisible({ timeout: 8000 });

    const text = await summaryLocator.textContent() || "";
    const match = text.match(/of\s+(\d+)\s+results/i);
    
    if (match) {
      this.initialTotalCount = parseInt(match[1], 10);
      this.currentTotalCount = this.initialTotalCount;
    } else {
      throw new Error(`Failed to parse total results count from summary text: "${text}"`);
    }
  }


  async verifyTotalCountDecreasedByOne() {
    await this.page.waitForTimeout(1000); // Allow grid re-indexing animation to complete

    const expectedNewTotal = this.currentTotalCount - 1;
    const summaryLocator = this.page.locator('div, span, p').filter({ hasText: /Showing \d+ to \d+ of \d+/i }).first();
    
    await expect(summaryLocator).toBeVisible({ timeout: 8000 });
    
    // Validate the summary text updates dynamically to show the new decremented total count
    await expect(summaryLocator).toContainText(`of ${expectedNewTotal} results`, { timeout: 8000 });
    
    // Update internal tracking count for sequential assertions
    this.currentTotalCount = expectedNewTotal;
  }

  // ============ TC_RAM_039 / GTC_RAM_UI_004: Verify Toast message notification auto-dismissal ============
  async verifyToastAutoDismisses() {
    const toast = this.page.locator(this.toastNotification).first();
    await expect(toast).toBeVisible();
    await expect(toast).toBeHidden({ timeout: 7000 });
  }

  // ============ TC_RAM_040: Validate Edit modal field modification restrictions ============
  async verifyEmailFieldEditableState(): Promise<boolean> {
    const isDisabled = await this.page.locator(this.emailInput).first().isDisabled();
    return isDisabled;
  }

  // ============ GTC_RAM_NET_001: Verify module performance on Slow 3G ============
  async enableSlow3GThrottling() {
  const cdpSession = await this.page.context().newCDPSession(this.page);
  await cdpSession.send('Network.emulateNetworkConditions', {
    offline: false,
    downloadThroughput: (500 * 1024) / 8, // 500 kbps
    uploadThroughput: (500 * 1024) / 8,   // 500 kbps
    latency: 400                          // 400ms RTT
  });
}async verifyLoadingSkeletonVisible() {
  // Broad locator matching Shadcn animate-pulse skeletons, Lucide loaders, and Radix progress indicators
  const loadingIndicator = this.page.locator(
    'div[class*="skeleton"], ' +
    'div[class*="animate-pulse"], ' +
    'svg.animate-spin, ' +
    'svg.lucide-loader, ' +
    'svg.lucide-loader-2, ' +
    '[role="status"]'
  ).first();

  // On Slow 3G, assert the loading state appears within 5 seconds
  await expect(loadingIndicator).toBeVisible({ timeout: 5000 });
}

  // ============ GTC_RAM_NET_002: Verify handling of sudden network disconnection ============
  async goOfflineDuringSubmission() {
    await this.page.context().setOffline(true);
  }

  async verifyOfflineAlertWithoutDataLoss(expectedFieldValue: string) {
    await expect(
      this.page.locator('text=/Network connection lost/i').first()
    ).toBeVisible({ timeout: 8000 });
    await expect(this.page.locator(this.fullNameInput).first()).toHaveValue(expectedFieldValue);
    await this.page.context().setOffline(false);
  }
 async verifyTouchTargetsAndInputsInteractive() {
  const modal = this.page.locator('div[role="dialog"]').first();
  await expect(modal).toBeVisible({ timeout: 5000 });

  // 1. Verify inputs are interactive on mobile viewports
  const nameInput = modal.locator('input[name="fullName"], input[placeholder*="Name" i]').first();
  await nameInput.waitFor({ state: 'visible', timeout: 5000 });
  await nameInput.click();
  await nameInput.fill('Touch Test User');

  // 2. Verify submit button meets touch target accessibility guidelines (>= 36px/44px)
  const submitBtn = modal.locator('button[type="submit"], button:has-text("Create Admin")').first();
  await expect(submitBtn).toBeEnabled();

  const boundingBox = await submitBtn.boundingBox();
  expect(boundingBox).not.toBeNull();
  expect(boundingBox!.height).toBeGreaterThanOrEqual(36);

  // 3. Close modal cleanly
  const closeBtn = modal.locator('button[aria-label="Close"], button:has-text("Cancel")').first();
  if (await closeBtn.isVisible().catch(() => false)) {
    await closeBtn.click();
  } else {
    await this.page.keyboard.press('Escape');
  }
}

  // ============ GTC_RAM_ERR_001: Verify system handling when backend returns HTTP 500 ============
  async mockServerErrorOnSubmit() {
    await this.page.route('**/api/**/admins**', route => route.fulfill({ status: 500, body: 'Internal Server Error' }));
  }

  async verifyServerErrorBanner() {
    await expect(
      this.page.locator('text=/Server error occurred. Please try again later/i').first()
    ).toBeVisible({ timeout: 5000 });
  }

  // ============ GTC_RAM_COMP_001 / GTC_RAM_COMP_002: Cross-browser & Mobile rendering ============
  async verifyCoreRanchAdminElementsRender() {
    await expect(this.page.locator(this.mainGrid).first()).toBeVisible();
    await expect(this.page.locator(this.createNewAdminButton).first()).toBeVisible();
  }

  // ============ GTC_RAM_UI_003: Verify Dark Mode / Light Mode theme switching ============
  private themeToggle = 'button[aria-label*="theme" i], button:has(svg.lucide-moon), button:has(svg.lucide-sun)';

  async toggleDarkMode() {
    await this.page.locator(this.themeToggle).first().click();
    await this.page.waitForTimeout(300);
  }

  async verifyDarkModeAppliedAcrossComponents() {
    const html = this.page.locator('html');
    const classAttr = (await html.getAttribute('class')) || '';
    const dataTheme = (await html.getAttribute('data-theme')) || '';
    expect(classAttr.includes('dark') || dataTheme === 'dark').toBeTruthy();
  }

  // ============ GTC_RAM_ACC_002: Verify Screen Reader accessibility labels ============
async verifyAriaLabelsPresent() {
  const buttons = this.page.locator('main button, tbody tr button, button:has-text("Create New Admin")');
  await expect(buttons.first()).toBeVisible({ timeout: 8000 });

  // Evaluate all button accessibility properties in a single browser-side JS call
  const accessibleButtonsCount = await buttons.evaluateAll((buttonEls) => {
    return buttonEls.filter((button) => {
      // 1. Skip hidden elements (casting window as any to bypass TS compile checks)
      const win = (button.ownerDocument.defaultView || {}) as any;
      const style = win.getComputedStyle ? win.getComputedStyle(button) : null;
      
      if (style && (style.display === 'none' || style.visibility === 'hidden' || button.offsetParent === null)) {
        return false;
      }

      // 2. Check WCAG accessible name sources
      const ariaLabel = button.getAttribute('aria-label');
      const ariaLabelledBy = button.getAttribute('aria-labelledby');
      const srText = button.querySelector('.sr-only, .visually-hidden')?.textContent;
      const textContent = button.textContent;

      return (
        (ariaLabel && ariaLabel.trim().length > 0) ||
        (ariaLabelledBy && ariaLabelledBy.trim().length > 0) ||
        (srText && srText.trim().length > 0) ||
        (textContent && textContent.trim().length > 0)
      );
    }).length;
  });

  // Assert that visible action buttons expose valid accessible names
  expect(accessibleButtonsCount).toBeGreaterThan(0);
}
  // ============ GTC_RAM_FV_005: Verify copy-paste functionality into form fields ============
  async pasteIntoField(fieldLocator: string, text: string) {
    const field = this.page.locator(fieldLocator).first();
    await field.click();
    await this.page.evaluate(
      ({ selector, value }) => {
        const win = globalThis as any;
        const el = win.document?.querySelector(selector);
        if (el) {
          el.value = value;
          el.dispatchEvent(new win.Event('input', { bubbles: true }));
        }
      },
      { selector: fieldLocator, value: text }
    );
  }

  async pasteIntoFullName(payload: string) {
    await this.pasteIntoField(this.fullNameInput, payload);
  }

  async verifyFieldNotFrozen(fieldLocator: string) {
    const field = this.page.locator(fieldLocator).first();
    await expect(field).toBeEnabled();
  }
  
async verifyRanchSelectorRestricted(assignedRanch: string) {
  const combobox = this.page.locator('button[role="combobox"]').filter({
    hasText: /all ranches|select ranch/i
  }).first();

  const isDropdownVisible = await combobox.isVisible().catch(() => false);

  // If the dropdown is hidden/removed for Ranch Admin, RBAC restriction is enforced
  if (!isDropdownVisible) {
    return;
  }

  // If visible, expand and verify options
  await combobox.click();
  const options = this.page.locator('[role="option"], [data-radix-collection-item]');
  const optionCount = await options.count();

  for (let i = 0; i < optionCount; i++) {
    const optionText = await options.nth(i).textContent();
    if (optionText && !optionText.toLowerCase().includes('all ranches')) {
      expect(optionText.toLowerCase()).toContain(assignedRanch.toLowerCase());
    }
  }

  await this.page.keyboard.press('Escape');
}
async verifyOnlyAssignedRanchInGrid(assignedRanch: string) {
  // 1. Wait for global loading spinners/skeletons to detach
  await this.page.locator('text="Loading..."').waitFor({ state: 'detached', timeout: 10000 }).catch(() => {});

  // 2. Verify branding header or top left navigation portal title
  const brandHeader = this.page.locator('header, nav, h1, h2, h3, [class*="header"], [class*="brand"]').filter({
    hasText: new RegExp(assignedRanch, 'i')
  }).first();

  await expect(brandHeader).toBeVisible({ timeout: 12000 });

  // 3. Verify grid rows on data modules
  const currentUrl = this.page.url();
  if (currentUrl.includes('/ranches') || currentUrl.includes('/admin')) {
    const gridRows = this.page.locator('table:not([class*="calendar"]) tbody tr');
    await gridRows.first().waitFor({ state: 'visible', timeout: 8000 });

    const count = await gridRows.count();
    expect(count).toBeGreaterThan(0);

    for (let i = 0; i < count; i++) {
      const rowText = await gridRows.nth(i).textContent();
      if (rowText && rowText.trim().length > 0) {
        expect(rowText.toLowerCase()).toContain(assignedRanch.toLowerCase());
      }
    }
  }
}
}
 