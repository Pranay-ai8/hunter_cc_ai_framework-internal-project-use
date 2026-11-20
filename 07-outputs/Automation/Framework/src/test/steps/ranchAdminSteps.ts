import { Given, When, Then } from "@cucumber/cucumber";
import { expect, devices } from "@playwright/test";
import { fixture } from "../../support/pageFixture";
import { RanchAdminPage } from "../../pages/ranchAdminPage";
import { LoginPage } from "../../pages/loginPage";
import { HarvestLogsPage } from "../../pages/harvestLogsPage";
import { ensureSuperAdminSession } from "../../support/sessionGuard";

function getRanchAdminPage(): RanchAdminPage {
  if (!fixture.pages.ranchAdminPage) {
    fixture.pages.ranchAdminPage = new RanchAdminPage(fixture.page);
  }
  return fixture.pages.ranchAdminPage;
}

function getLoginPage(): LoginPage {
  if (!fixture.pages.loginPage) {
    fixture.pages.loginPage = new LoginPage(fixture.page);
  }
  return fixture.pages.loginPage;
}

function getHarvestLogsPage(): HarvestLogsPage {
  if (!fixture.pages.harvestLogsPage) {
    fixture.pages.harvestLogsPage = new HarvestLogsPage(fixture.page);
  }
  return fixture.pages.harvestLogsPage;
}

/** Opens the collapsed mobile/narrow-viewport sidebar drawer if the nav items aren't visible yet. */
async function ensureSidebarOpen(page: any, navItem: any) {
  if (await navItem.isVisible().catch(() => false)) return;

  const mobileTrigger = page
    .locator([
      'button[data-sidebar="trigger"]',
      'header button:has(svg)',
      'button[aria-label*="menu" i]',
      'button[aria-label*="sidebar" i]',
      'button[aria-label*="Toggle" i]',
      'button:has(.lucide-menu)',
      'button:has(.lucide-panel-left)'
    ].join(', '))
    .first();

  if (await mobileTrigger.isVisible().catch(() => false)) {
    await mobileTrigger.click();
    await page.waitForTimeout(300);
  }
}

let lastCreatedAdminName = "";
let lastUpdatedAdminName = "";
let capturedGeneratedName = "";

// --- Navigation & Auth ---
Given("User navigates to the login page for Ranch Admin module", async function () {
  await getLoginPage().navigateToLogin();
});

When("User logs in with valid credentials", async function () {
  const email = fixture.testData?.username || process.env.SUPER_ADMIN_EMAIL || "superadmin@buckhouse.com";
  const password = fixture.testData?.password || process.env.SUPER_ADMIN_PASSWORD || "AdminUser123!";
  await getLoginPage().login(email, password);
});

Given("User logged  in as Super Admin", async function () {
  // Suite-level login happens exactly once (see hooks.ts BeforeAll) and every scenario shares that same
  // session/browser page. This step used to unconditionally perform a fresh real login on every single
  // invocation (40 scenarios in ranchAdmin.feature) with no check at all - across a long run that's 40 extra
  // real backend login attempts on top of the one legitimate one, which is what drove the backend's own
  // rate-limit/account-lock. Now it only verifies the shared session is still there.
  await ensureSuperAdminSession(fixture.page);
});

When("User navigates to the Ranch Admin module from the left navigation menu", async function () {
  const page = this.page || fixture.page || (getLoginPage() as any).page;

  if (!page) {
    throw new Error("❌ Playwright Page instance is undefined in step definition context.");
  }

  // The shared page is reused across scenarios. A prior (possibly failed) scenario can leave a modal
  // overlay open, which intercepts pointer events on the whole page and hangs this click for the full
  // 30s timeout. Clear any leftover overlay before navigating.
  await page.keyboard.press('Escape').catch(() => {});

  const ranchAdminsNav = page.locator('a, button, div').filter({ hasText: /^Ranch Admins$/i }).first();
  await ranchAdminsNav.waitFor({ state: 'visible', timeout: 10000 });

  // Already on the Ranch Admins page (e.g. left over from a prior scenario) - nothing to click.
  const alreadyActive = await ranchAdminsNav.getAttribute('aria-current').catch(() => null);
  if (alreadyActive !== 'page') {
    // The blocking overlay can appear/reappear right at click time rather than being present when the
    // Escape above ran, so retry once by dismissing again and forcing the click through.
    try {
      await ranchAdminsNav.click({ timeout: 8000 });
    } catch {
      await page.keyboard.press('Escape').catch(() => {});
      await ranchAdminsNav.click({ force: true, timeout: 5000 }).catch(async () => {
        // A prior scenario's interaction can have thrown inside the React app and tripped its error
        // boundary ("Something went wrong!"), which has no nav links at all to click. hooks.ts only
        // checks for this at the very start of the scenario, before this step runs its own navigation -
        // check again here and recover by reloading, then retry the click once more.
        const crashed = await page.locator('text=/Something went wrong!/i').first().isVisible({ timeout: 1000 }).catch(() => false);
        if (crashed) {
          await page.reload().catch(() => {});
          await page.waitForLoadState('networkidle').catch(() => {});
          await page.locator('a, button, div').filter({ hasText: /^Ranch Admins$/i }).first().click({ timeout: 8000 });
        } else {
          throw new Error('Could not click the "Ranch Admins" nav link - not a detected app crash.');
        }
      });
    }
    await page.waitForURL('**/admin**', { timeout: 10000 }).catch(() => {});
  }

  // The grid/search are on a shared page reused across scenarios - a prior scenario (e.g. a SQL-injection
  // negative test) can leave a search filter value behind, which silently filters this scenario's grid
  // down to "No admins found" / "Showing 0 to 0" instead of the real data. Reset it on every fresh landing.
  const searchInput = page.locator('input[placeholder*="search" i], input[type="search"]').first();
  if (await searchInput.isVisible().catch(() => false)) {
    const currentValue = await searchInput.inputValue().catch(() => '');
    if (currentValue) {
      await searchInput.fill('');
      await searchInput.press('Enter').catch(() => {});
    }
  }
});

When("User navigates to the {string} module from the left navigation menu", async function (menuName: string) {
  if (menuName === "Ranch Admins") {
    await getRanchAdminPage().navigateToRanchAdmins();
  } else if (menuName === "Harvest Logs") {
    await getHarvestLogsPage().navigateToHarvestLogsFromSidebar();
  } else {
    const page = this.page || fixture.page || (getLoginPage() as any).page;
    const navItem = page.locator('a, button, div').filter({ hasText: new RegExp(`^${menuName}$`, 'i') }).first();
    await ensureSidebarOpen(page, navItem);
    await navItem.waitFor({ state: 'visible', timeout: 10000 });
    await navItem.click();
  }
});

// --- Modal Buttons & Form Actions ---
When("User clicks the {string} button for ranch admin", async function (buttonName: string) {
  switch (buttonName) {
    case "+ Create New Admin":
    case "Create New Admin":
      await getRanchAdminPage().openCreateAdminModal();
      break;
    case "Create Admin":
      await getRanchAdminPage().clickCreateAdmin();
      break;
    case "Update Admin":
      await getRanchAdminPage().clickUpdateAdmin();
      break;
    case "Cancel":
      await getRanchAdminPage().clickCancelButton();
      break;
    default:
      await getRanchAdminPage().clickButtonByName(buttonName);
  }
});

When("User fills the admin form with Name {string}, Email {string}, Password {string}, and Ranch {string}", async function (name: string, email: string, pass: string, ranch: string) {
  // Dynamically append unique timestamps for creation scenarios to prevent duplicate record collisions
  if (["Race Condition", "John Doe", "Sanity Admin", "EditTarget", "Robert Sterling", "SearchTarget", "<script>alert('XSS')</script>"].some(target => name.includes(target))) {
    const timestamp = Date.now();
    lastCreatedAdminName = `${name} ${timestamp}`;
    const dynamicEmail = `test.${timestamp}@ranch.com`;
    await getRanchAdminPage().fillAdminForm(lastCreatedAdminName, dynamicEmail, pass, ranch);
  } else if (name === "Dupe User") {
    const existingEmail = await getRanchAdminPage().getFirstExistingAdminEmail().catch(() => "superadmin@buckhouse.com");
    lastCreatedAdminName = name;
    await getRanchAdminPage().fillAdminForm(name, existingEmail, pass, ranch);
  } else {
    lastCreatedAdminName = name;
    await getRanchAdminPage().fillAdminForm(name, email, pass, ranch);
  }
});

Then("The modal should close and a success toast {string} should appear", async function (message: string) {
  await getRanchAdminPage().verifyModalClosed();
  await getRanchAdminPage().verifySuccessToast(message);
});

Then("The grid should display a new row containing {string}", async function (expectedName: string) {
  // A rename ("User changes Full Name to...") makes lastUpdatedAdminName the real expected value -
  // it must win over the original creation name, which this check used to fall back to instead.
  const targetName = lastUpdatedAdminName || lastCreatedAdminName || expectedName;
  await getRanchAdminPage().verifyGridContainsAdmin(targetName);
  lastUpdatedAdminName = "";
});
Then("The grid should display the trimmed name {string}", async function (trimmedName: string) {
  await getRanchAdminPage().verifyGridDisplaysTrimmedName(trimmedName);
});
// --- Mandatory & Password Rules ---
When("User leaves all fields empty and submits the form", async function () {
  await getRanchAdminPage().submitEmptyForm();
});

Then("Form submission should be blocked with inline errors under all mandatory fields", async function () {
  await getRanchAdminPage().verifyMandatoryFieldErrors();
});

When("User enters password {string} in the Password field", async function (password: string) {
  await getRanchAdminPage().enterPassword(password);
});

Then("A password length error should display", async function () {
  await getRanchAdminPage().verifyPasswordLengthError();
});

Then("No password length error should display", async function () {
  await getRanchAdminPage().verifyNoPasswordLengthError();
});

Then("A password complexity error should display", async function () {
  await getRanchAdminPage().verifyPasswordComplexityError();
});

Then("Password validation should clear completely", async function () {
  await getRanchAdminPage().verifyPasswordValidationCleared();
});

Then("The Password field type should be {string}", async function (type: "password" | "text") {
  await getRanchAdminPage().verifyPasswordFieldType(type);
});

// --- Email Validations ---
Then("Submission should fail with a duplicate email error", async function () {
  await getRanchAdminPage().verifyDuplicateEmailError();
});

Then('An invalid email format error should display', async function () {
  await getRanchAdminPage().verifyInvalidEmailSyntaxError();
});

When("User enters email {string} in the Email field", async function (email: string) {
  await getRanchAdminPage().fillEmailField(email);
});

Then("Inline errors {string} and {string} should display", async function (nameError: string, emailError: string) {
  await getRanchAdminPage().verifyInlineValidationErrors(nameError, emailError);
});

// --- Focus Order & Accessibility ---
When('User presses Tab repeatedly from modal launch', async function () {
  await getRanchAdminPage().recordTabFocusSequence();
});

Then('Focus should move logically through Close, Full Name, Email, Password, Show Password toggle, Ranch Dropdown, Cancel, and Create Admin', async function () {
  await getRanchAdminPage().verifyKeyboardTabOrder();
});

Then("Action buttons should expose accessible aria-labels", async function () {
  await getRanchAdminPage().verifyAriaLabelsPresent();
});

// --- XSS & Payloads ---
When("User enters payload {string} in the Full Name field", async function (payload: string) {
  await getRanchAdminPage().verifyNoAlertDialogTriggered();
  await getRanchAdminPage().submitFullNameWithPayload(payload);
});

Then("The input should be sanitized and rendered as plain text in the grid", async function () {
  const payload = fixture.testData?.xssPayload || "<script>alert('XSS')</script>";
  await getRanchAdminPage().verifyGridRendersPlainText(payload);
});

When("User enters SQL or regex payload {string} in the search field", async function (payload: string) {
  await getRanchAdminPage().submitPayloadInSearch(payload);
});

When("User pastes payload {string} into the Full Name field", async function (payload: string) {
  await getRanchAdminPage().pasteIntoFullName(payload);
});

Then("The field should remain responsive and not freeze", async function () {
  await getRanchAdminPage().verifyFieldNotFrozen('input[name="fullName"], input[placeholder*="full name" i]');
});

// --- Modal Dismissal & Reset ---
When("User presses the ESC key", async function () {
  await getRanchAdminPage().pressEscapeKey();
});

When("User clicks the modal backdrop outside the modal", async function () {
  await getRanchAdminPage().clickModalBackdrop();
});

Then("The modal should close without saving data", async function () {
  await getRanchAdminPage().verifyModalClosed();
});

Then("The form fields should reset to empty on reopen", async function () {
  await getRanchAdminPage().verifyFormFieldsEmpty();
});

Then("The modal should open clean with empty fields and zero validation errors", async function () {
  await getRanchAdminPage().verifyFormFieldsEmpty();
  await getRanchAdminPage().verifyZeroValidationErrors();
});

// --- Search & Grid Filtering ---
When("User searches for the created admin in the search bar", async function () {
  await getRanchAdminPage().typeInSearchBar(lastCreatedAdminName);
});

When("User types {string} in the ranch admin search bar", async function (searchText: string) {
  await getRanchAdminPage().typeInSearchBar(searchText);
});

Then('The grid should filter to {int} row\\(s)', async function (expectedCount: number) {
  await getRanchAdminPage().verifyGridRowCount(expectedCount);
});

Then("Only one record for {string} should be created", async function (expectedName: string) {
  const targetName = lastCreatedAdminName || expectedName;
  await getRanchAdminPage().verifyRecordCountInGrid(targetName, 1);
});

Then('The grid should display a {string} empty state', async function (expectedMessage: string) {
  await getRanchAdminPage().verifyEmptyStateMessage(expectedMessage);
});

Then("The table should update dynamically without a full page reload", async function () {
  await getRanchAdminPage().verifyGridUpdatesWithoutReload("");
});

Then("The search should handle the input literally without server errors", async function () {
  await getRanchAdminPage().verifyNoServerErrorDisplayed();
});

// --- Pagination & Summary ---
When('User changes the results per page dropdown to {string} for ranch admin', async function (pageSize: string) {
  await getRanchAdminPage().changePageSize(pageSize);
});

Then("The results summary text should display {string} for ranch admin", async function (summaryText: string) {
  await getRanchAdminPage().verifyPaginationSummary(summaryText);
});

When("User navigates to page {int}", async function (pageNum: number) {
  await getRanchAdminPage().navigateToPage(pageNum);
});

When("User deletes the last record on the current page", async function () {
  await getRanchAdminPage().deleteLastRecordOnCurrentPage();
});

Then("The view should automatically redirect back to page 1", async function () {
  await getRanchAdminPage().verifyRedirectedToPageOne();
});

When("User records the initial total result count", async function () {
  await getRanchAdminPage().recordInitialTotalCount();
});

When("User deletes an available record on the current page", async function () {
  await getRanchAdminPage().deleteAvailableRecordOnCurrentPage();
});

Then("The results summary total count should decrease by 1", async function () {
  await getRanchAdminPage().verifyTotalCountDecreasedByOne();
});

// --- Layout Toggles & Mobile Responsiveness ---
Given("User sets the viewport size to {int} pixels by {int} pixels for ranch admin", async function (width: number, height: number) {
  await getRanchAdminPage().setViewportSize(width, height);
});

When("User taps the hamburger menu and selects {string} for ranch admin", async function (moduleName: string) {
  const page = this.page || fixture.page || (getLoginPage() as any).page;

  if (page.url().includes('/login')) {
    const adminEmail = process.env.SUPER_ADMIN_EMAIL || process.env.SUPERADMIN_USER || "superadmin@buckhouse.com";
    const adminPassword = process.env.SUPER_ADMIN_PASSWORD || process.env.SUPERADMIN_PASSWORD || "Admin@123!";
    await getLoginPage().login(adminEmail, adminPassword);
  }

  const hamburgerBtn = page.locator('button[data-sidebar="trigger"], button[aria-label*="sidebar" i], button[aria-label*="menu" i], button:has(svg)').first();
  if (await hamburgerBtn.isVisible({ timeout: 5000 }).catch(() => false)) {
    await hamburgerBtn.click();
  }

  const mobileDrawer = page.locator('[role="dialog"], [data-state="open"], div[class*="sheet-content"], nav').last();
  const navItem = mobileDrawer.locator('a, button, span, div').filter({ hasText: new RegExp(`^${moduleName}$`, 'i') }).first();

  const finalNavItem = (await navItem.isVisible().catch(() => false))
    ? navItem
    : page.locator('a, button').filter({ hasText: new RegExp(`^${moduleName}$`, 'i') }).locator('visible=true').first();

  await finalNavItem.waitFor({ state: 'visible', timeout: 8000 });
  await finalNavItem.click();
});

Then("The sidebar should collapse into a hamburger menu for ranch admin", async function () {
  await getRanchAdminPage().verifyMobileSidebarCollapsed();
});

Then("The modal should fit the screen without clipping", async function () {
  await getRanchAdminPage().verifyModalFitsViewport();
});

Given("User emulates mobile device {string} for ranch admin", async function (deviceName: string) {
  const deviceConfig = devices[deviceName];
  if (!deviceConfig) {
    throw new Error(`Device profile "${deviceName}" not recognized.`);
  }

  const page = this.page || fixture.page || (getLoginPage() as any).page;
  if (page && deviceConfig.viewport) {
    await page.setViewportSize(deviceConfig.viewport);
  }

  await getLoginPage().navigateToLogin();
});

Then("Touch action targets and form inputs should be fully interactive", async function () {
  await getRanchAdminPage().verifyTouchTargetsAndInputsInteractive();
});

When("User clicks the Card View toggle icon", async function () {
  await getRanchAdminPage().clickCardViewToggle();
});

When("User clicks the Table View toggle icon", async function () {
  await getRanchAdminPage().clickTableViewToggle();
});

Then("The view should display in Card layout", async function () {
  await getRanchAdminPage().verifyCardLayoutVisible();
});

Then("The view should display in Table layout", async function () {
  await getRanchAdminPage().verifyTableLayoutVisible();
});

// --- Edit & Update Flow ---
When("User clicks {string} for an available admin row", async function (actionName: string) {
  await getRanchAdminPage().clickActionForAvailableRow(actionName);
});

When("User clicks {string} for admin row {string}", async function (actionName: string, targetName: string) {
  const actualTarget = lastCreatedAdminName || targetName;
  await getRanchAdminPage().clickActionForNamedRow(actionName, actualTarget);
});

When("User changes Full Name to {string}", async function (newName: string) {
  const timestamp = Date.now();
  lastUpdatedAdminName = `${newName} ${timestamp}`;
  await getRanchAdminPage().fillFullNameInput(lastUpdatedAdminName);
});

Then("The edit modal should be pre-filled with the selected admin details", async function () {
  await getRanchAdminPage().verifyEditModalPreFilledWithSelectedDetails();
});

Then("The Updating loader should display before the modal closes", async function () {
  await getRanchAdminPage().verifyUpdatingLoader();
});

When("User clears the Full Name field", async function () {
  await getRanchAdminPage().clearFullNameField();
});

Then("The original values for the selected row should remain unchanged in the grid", async function () {
  await getRanchAdminPage().verifyOriginalRowValuesUnchanged();
});

// --- Delete Flow ---
Then("The delete confirmation modal should display", async function () {
  await getRanchAdminPage().verifyDeleteConfirmationModalVisible();
});

When("User confirms the deletion", async function () {
  await getRanchAdminPage().confirmDeletion();
});

Then("The modal should close and the record should be removed from the grid", async function () {
  await getRanchAdminPage().verifyRecordRemovedFromGrid();
});

When("User cancels the deletion", async function () {
  await getRanchAdminPage().cancelDeletionModal();
});

Then("The record should remain visible in the grid", async function () {
  await getRanchAdminPage().verifyRecordStillVisibleInGrid();
});

Then("The delete button for the Super Admin row should be disabled", async function () {
  await getRanchAdminPage().verifySuperAdminDeleteDisabled();
});

// --- Edge Cases, Network & System Tests ---
When("User rapidly double-clicks the Create Admin button", async function () {
  await getRanchAdminPage().rapidlyDoubleClickSubmit();
});

Then("The submit button should disable immediately with a loading state", async function () {
  await getRanchAdminPage().verifySubmitButtonDisabledWithLoader();
});

When("User enters a Full Name of {int} characters and submits", async function (length: number) {
  capturedGeneratedName = await getRanchAdminPage().enterFullNameOfLength(length);
  await getRanchAdminPage().clickCreateAdmin();
});

Then("The Full Name should be accepted or a max-length error should display", async function () {
  await getRanchAdminPage().verifyNameAcceptedOrTruncationError();
});

When("User enters Unicode Full Name {string}", async function (name: string) {
  await getRanchAdminPage().submitFullNameWithPayload(name);
});

Then("The grid should render the Unicode name {string} correctly", async function (name: string) {
  await getRanchAdminPage().verifyGridDisplaysUnicodeNameCorrectly(name);
});

When("User opens the Assign to Ranch dropdown", async function () {
  await getRanchAdminPage().verifyNoActiveRanchesDisabledState();
});

Then("The dropdown should display a disabled {string} state", async function (_label: string) {
  await getRanchAdminPage().verifyNoRanchesDisabledStateMessage();
});

When("The auth session token expires", async function () {
  await getRanchAdminPage().simulateSessionTokenExpiry();
});

Then("The application should redirect to the Login page without console errors", async function () {
  await getRanchAdminPage().verifyRedirectedToLoginOn401();
});

When("Another user deletes the same admin record in a separate session", async function () {
  await getRanchAdminPage().simulateRecordDeletedByOtherUser();
});

Then("The UI should notify the user that the record no longer exists", async function () {
  await getRanchAdminPage().verifyRecordConflictNotification();
});

Then("The toast notification should automatically fade out", async function () {
  await getRanchAdminPage().verifyToastAutoDismisses();
});

Then("The Email field editable state should be recorded for review", async function () {
  const isDisabled = await getRanchAdminPage().verifyEmailFieldEditableState();
  fixture.testData._emailFieldDisabled = isDisabled;
});

When("The network is throttled to Slow 3G", async function () {
  await getRanchAdminPage().enableSlow3GThrottling();
});

Then("A loading spinner or skeleton state should display", async function () {
  await getRanchAdminPage().verifyLoadingSkeletonVisible();
});

When("The network connection is severed before the API response resolves", async function () {
  await getRanchAdminPage().goOfflineDuringSubmission();
});

Then("An offline alert should display without losing the entered form draft {string}", async function (fieldValue: string) {
  await getRanchAdminPage().verifyOfflineAlertWithoutDataLoss(fieldValue);
});

Given("The backend API is mocked to return HTTP 500", async function () {
  await getRanchAdminPage().mockServerErrorOnSubmit();
});

Then("A user-friendly server error banner should display", async function () {
  await getRanchAdminPage().verifyServerErrorBanner();
});

Then("Core Ranch Admin elements should render without visual bugs", async function () {
  await getRanchAdminPage().verifyCoreRanchAdminElementsRender();
});

When("User toggles the theme switch to Dark Mode", async function () {
  await getRanchAdminPage().toggleDarkMode();
});

Then("Dark theme should apply uniformly with legible contrast", async function () {
  await getRanchAdminPage().verifyDarkModeAppliedAcrossComponents();
});

// --- RBAC Execution ---
When("User logs in with Ranch Admin credentials for scenario {string}", async function (scenarioKey: string) {
  const loginPage = getLoginPage();
  const page = this.page || fixture.page || (loginPage as any).page;

  if (page) {
    // 1. Navigate to application domain first to establish origin context
    await page.goto('https://dev-dashboard.hunter.igoalzero.com/login', { waitUntil: 'domcontentloaded' });

    // 2. Wipe cookies and domain-specific storage state
    const context = page.context();
    if (context) {
      await context.clearCookies();
    }

    await page.evaluate(() => {
      try {
        localStorage.clear();
        sessionStorage.clear();
      } catch (e) {}
    });

    // 3. Force page reload to clear in-memory single-page app (SPA) auth state
    await page.reload({ waitUntil: 'networkidle' });
  }

  // 4. Authenticate cleanly as Ranch Admin
  await loginPage.login("jony54@gmail.com", "Jony@54!");
});
Then("The grid or dashboard should display only {string}", async function (expectedRanch: string) {
  const targetRanch = this.csvData?.assigned_ranch || expectedRanch;
  await getRanchAdminPage().verifyOnlyAssignedRanchInGrid(targetRanch);
});

Then("The ranch filter selector should be locked or contain only {string}", async function (expectedRanch: string) {
  const targetRanch = this.csvData?.assigned_ranch || expectedRanch;
  await getRanchAdminPage().verifyRanchSelectorRestricted(targetRanch);
});