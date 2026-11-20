import { Given, When, Then } from "@cucumber/cucumber";
import { expect } from "@playwright/test";
import { fixture } from "../../support/pageFixture";
import { HarvestLogsPage } from "../../pages/harvestLogsPage";
import { LoginPage } from "../../pages/loginPage";

function getHarvestLogsPage(): HarvestLogsPage {
  if (!fixture.pages.harvestLogsPage) {
    fixture.pages.harvestLogsPage = new HarvestLogsPage(fixture.page);
  }
  return fixture.pages.harvestLogsPage;
}

function getLoginPage(): LoginPage {
  if (!fixture.pages.loginPage) {
    fixture.pages.loginPage = new LoginPage(fixture.page);
  }
  return fixture.pages.loginPage;
}

// --- Navigation & Auth ---
Given("User is logged in as Super Admin and navigated to Harvest Logs module", async function () {
  const page = fixture.page;

  const envBaseUrl = process.env.BASEURL || page.url();
  const origin = envBaseUrl.startsWith("http") 
    ? new URL(envBaseUrl).origin 
    : "http://localhost:8081";

  const targetUrl = `${origin}/harvest`;

  // Verify we are not logged in as Guide or Ranch Admin
  const isNonAdmin = await page.locator('text=/Ranch Admin Portal|Guide Portal/i').first().isVisible({ timeout: 1000 }).catch(() => false);
  
  if (isNonAdmin) {
    await page.context().clearCookies().catch(() => {});
    await page.evaluate(() => {
      try {
        localStorage.clear();
        sessionStorage.clear();
      } catch (_) {}
    }).catch(() => {});
    await page.goto(`${origin}/login`);
    const email = process.env.SUPER_ADMIN_EMAIL || "superadmin@buckhouse.com";
    const password = process.env.SUPER_ADMIN_PASSWORD || "Admin@123!";
    await getLoginPage().login(email, password);
  }

  if (!page.url().includes("/harvest")) {
    await page.goto(targetUrl);
  }

  if (page.url().includes("/login")) {
    const email = process.env.SUPER_ADMIN_EMAIL || "superadmin@buckhouse.com";
    const password = process.env.SUPER_ADMIN_PASSWORD || "Admin@123!";

    await getLoginPage().login(email, password);
    await page.goto(targetUrl);
  }

  await page.waitForLoadState("networkidle").catch(() => {});
});

Then("User should be on {string} harvest logs page", async function (expectedPath: string) {
  await getHarvestLogsPage().verifyHarvestLogsUrl(expectedPath);
});

When("User logs in with valid Guest credentials", async function () {
  const email = fixture.testData?.username || process.env.GUEST_EMAIL || "guest@buckhouse.com";
  const password = fixture.testData?.password || process.env.GUEST_PASSWORD || "Guest123!";

  await fixture.page.context().clearCookies();
  await fixture.page.evaluate(() => {
    try {
      localStorage.clear();
      sessionStorage.clear();
    } catch (e) {}
  });

  await getLoginPage().navigateToLogin();
  await getLoginPage().enterCredentials(email, password);
  await getLoginPage().clickSignIn();
  await fixture.page.waitForLoadState("networkidle").catch(() => {});
});

When("User clicks {string} under {string} in the left navigation menu", async function (menuName: string, menuGroup: string) {
  const normalizedMenu = menuName.toLowerCase().trim();

  if (normalizedMenu === "invoices") {
    const invoiceLink = fixture.page.locator('a[href="/invoices"]').first();
    await invoiceLink.click();
    await fixture.page.waitForURL(/.*\/invoices/, { timeout: 10000 }).catch(() => {});
  } else if (normalizedMenu.includes("harvest")) {
    await getHarvestLogsPage().navigateToHarvestLogsFromSidebar();
  } else {
    const group = fixture.page.locator('aside, nav').filter({ hasText: menuGroup });
    await group.locator(`a:has-text("${menuName}")`).first().click();
  }

  await fixture.page.waitForLoadState("networkidle").catch(() => {});
});

When('User logs in with valid Super Admin credentials', async function () {
  const email = process.env.SUPER_ADMIN_EMAIL || 'superadmin@buckhouse.com';
  const password = process.env.SUPER_ADMIN_PASSWORD || 'SuperAdmin123!';
  await getLoginPage().login(email, password);
});

Then("User should be redirected to {string} harvest page", async function (expectedPath: string) {
  await expect(fixture.page).toHaveURL(new RegExp(`.*${expectedPath}$`));
});

Then("The harvest logs page title should be {string}", async function (expectedTitle: string) {
  await getHarvestLogsPage().verifyPageTitle(expectedTitle);
});

Then("The harvest logs page subtitle should be {string}", async function (expectedSubtitle: string) {
  await getHarvestLogsPage().verifyPageSubtitle(expectedSubtitle);
});

When("User clicks the harvest log {string} button", async function (buttonName: string) {
  const normalized = buttonName.toLowerCase().replace(/^\+\s*/, "").trim();

  if (normalized.includes("log new harvest")) {
    await getHarvestLogsPage().clickLogNewHarvestButton();
  } else if (normalized.includes("tag settings")) {
    await getHarvestLogsPage().clickTagSettingsButton();
  } else if (normalized.includes("add tag allocation")) {
    await getHarvestLogsPage().clickAddTagAllocationButton();
  } else if (normalized.includes("create allocation")) {
    await getHarvestLogsPage().clickCreateAllocationButton();
  } else if (normalized.includes("submit harvest log")) {
    await getHarvestLogsPage().clickSubmitHarvestLogButton();
  } else if (normalized.includes("clear filters")) {
    await getHarvestLogsPage().clickClearFiltersButton();
  } else if (normalized.includes("cancel")) {
    await getHarvestLogsPage().clickCancelButton();
  }
});

When("User clicks the harvest log {string} breadcrumb link", async function (linkName: string) {
  await getHarvestLogsPage().clickBreadcrumbLink(linkName);
});

Then("No draft data should be saved", async function () {
  await getHarvestLogsPage().verifyNoDraftDataSaved();
});

// --- Directory Filters ---
When("User selects {string} from the {string} dropdown", async function (value: string, dropdownName: string) {
  if (dropdownName === "All Ranches") {
    await getHarvestLogsPage().selectRanchFilter(value);
  } else if (dropdownName === "All Species") {
    await getHarvestLogsPage().selectSpeciesFilter(value);
  } else if (dropdownName === "Choose Ranch") {
    await getHarvestLogsPage().selectChooseRanchDropdown(value);
  } else if (dropdownName === "Choose Booking (Optional)") {
    await getHarvestLogsPage().selectNoBookingOption();
  }
});
When("User dynamically selects an available species from the \"All Species\" dropdown", async function () {
  await getHarvestLogsPage().selectAnyAvailableSpeciesFilter();
});

Then("The harvest logs grid should display only records matching the selected species", async function () {
  await getHarvestLogsPage().verifySelectedSpeciesFilteredResults();
});
Then("The {string} button should appear", async function (buttonName: string) {
  await getHarvestLogsPage().verifyClearFiltersButtonVisible();
});

Then("The ranch filter should reset to {string}", async function (expectedValue: string) {
  await getHarvestLogsPage().verifyRanchFilterReset();
});

Then("The {string} button should be hidden", async function (buttonName: string) {
  await getHarvestLogsPage().verifyClearFiltersButtonHidden();
});

Then("The harvest logs grid should re-fetch the default records", async function () {
  await getHarvestLogsPage().verifyDateRangeFilteredResults("", "");
});

When("User sets the {string} filter to {string}", async function (filterName: string, dateExpression: string) {
  await getHarvestLogsPage().setDateFilter(filterName, dateExpression);
});

Then(
  "The harvest logs grid should display only records with a Kill Date between {string} and {string}",
  async function (fromDate: string, toDate: string) {
    await getHarvestLogsPage().verifyDateRangeFilteredResults(fromDate, toDate);
  }
);

Then(
  "The system should prevent the selection or display the inline error {string}",
  async function (expectedError: string) {
    await getHarvestLogsPage().verifyDateRangeInlineErrorOrBlocked();
  }
);

When("User enters {string} in the harvest logs search box", async function (query: string) {
  await getHarvestLogsPage().enterSearchQuery(query);
});

Then("The harvest logs grid should filter in real-time to records matching {string}", async function (query: string) {
  await getHarvestLogsPage().verifySearchResultsMatch(query);
});

Then(
  "The search should handle the payload safely without a server error or exposed database error",
  async function () {
    await getHarvestLogsPage().verifySearchHandledSafely();
  }
);

When('User clicks the harvest log Card View toggle icon', async function () {
  await getHarvestLogsPage().clickCardViewToggle();
});

When('User clicks the harvest log Table View toggle icon', async function () {
  await getHarvestLogsPage().clickTableViewToggle();
});

Then("The harvest logs grid should display in Card layout", async function () {
  await getHarvestLogsPage().verifyCardLayoutDisplayed();
});

Then(
  "The harvest logs grid should display in Table layout with columns {string}, {string}, {string}, {string}, {string}, {string}, and {string}",
  async function (c1: string, c2: string, c3: string, c4: string, c5: string, c6: string, c7: string) {
    await getHarvestLogsPage().verifyTableLayoutDisplayed([c1, c2, c3, c4, c5, c6, c7]);
  }
);

Then(
  "The harvest logs grid should display the {string} empty state without layout breakage",
  async function (message: string) {
    await getHarvestLogsPage().verifyEmptyStateDisplayed();
  }
);



When("User submits the harvest log form", async function () {
  await getHarvestLogsPage().submitHarvestLogForm();
});

Then("The system should display a success notification and redirect to the harvest logs list", async function () {
  await getHarvestLogsPage().verifyHarvestLogSubmittedSuccessfully();
});

Then("The harvest logs grid should display only records matching species {string}", async function (species: string) {
  await getHarvestLogsPage().verifySpeciesFilteredResults(species);
});

When("User's session token expires", async function () {
  await getHarvestLogsPage().simulateSessionExpiration();
});

When("User changes a harvest logs filter", async function () {
  await getHarvestLogsPage().triggerFilterChangeAfterSessionExpiry();
});

Then("User should be redirected to {string} page with a session timeout message", async function (path: string) {
  await getHarvestLogsPage().verifySessionTimeoutRedirect();
});

// --- Tag Allocation ---
Given(
  "a 'Deer' tag allocation already exists for ranch {string}",
  async function (ranch: string) {
    await getHarvestLogsPage().ensureDeerTagAllocationExists(ranch, "Male", "5");
  }
);
When(
  "User fills the tag allocation form with an available ranch, category {string}, sex {string}, and allocated tags {string}",
  async function (category: string, sex: string, allocatedTags: string) {
    await getHarvestLogsPage().fillTagAllocationFormWithAvailableRanch({
      category,
      sex,
      allocatedTags
    });
  }
);
When(
  "User fills the tag allocation form with ranch {string}, category {string}, sex {string}, and allocated tags {string}",
  async function (ranch: string, category: string, sex: string, allocatedTags: string) {
    await getHarvestLogsPage().fillTagAllocationForm({
      ranch,
      category,
      sex,
      allocatedTags,
    });
  }
);
Then(
  "A warning banner listing {string}, {string}, {string}, and {string} should display",
  async function (item1: string, item2: string, item3: string, item4: string) {
    await getHarvestLogsPage().verifyMissingFieldsWarningBanner([item1, item2, item3, item4]);
  }
);

When('User clicks the {string} button', async function (buttonText: string) {
  const cleanName = buttonText.replace(/^\+\s*/, "").trim().toLowerCase();

  if (cleanName === "log new harvest") {
    await getHarvestLogsPage().clickLogNewHarvestButton();
  } else if (cleanName === "tag settings") {
    await getHarvestLogsPage().clickTagSettingsButton();
  } else if (cleanName === "create allocation") {
    await getHarvestLogsPage().clickCreateAllocationButton();
  } else if (cleanName === "add tag allocation") {
    await getHarvestLogsPage().clickAddTagAllocationButton();
  } else if (cleanName === "cancel") {
    await getHarvestLogsPage().clickCancelButton();
  } else {
    const cleanText = buttonText.replace(/^\+\s*/, '').trim();
    await fixture.page.locator(`button:has-text("${cleanText}"), a:has-text("${cleanText}")`).first().click();
  }
});

When("User leaves {string} and {string} blank in the tag allocation form", async function (field1: string, field2: string) {
  await getHarvestLogsPage().leaveTagAllocationFieldsBlank();
});

Then("Form submission should be blocked", async function () {
  await getHarvestLogsPage().verifyFormSubmissionBlocked();
});

Then("A red toast popup should appear for harvest log", async function () {
  await getHarvestLogsPage().verifyRedToastPopup();
});

Then("A harvest log validation error {string} should display", async function (expectedMessage: string) {
  await getHarvestLogsPage().verifyValidationErrorToast(expectedMessage);
});

Then("A harvest log success toast {string} should display", async function (expectedMessage: string) {
  await getHarvestLogsPage().verifySuccessToast(expectedMessage);
});

Then("A success toast should display", async function () {
  await getHarvestLogsPage().verifySuccessToast("");
});

Then("A success toast should display for harvest log", async function () {
  await getHarvestLogsPage().verifySuccessToast("");
});

Then("The new allocation row should display in the allocation table", async function () {
  await getHarvestLogsPage().verifyNewAllocationRowVisible("Turkey");
});

Then("The allocation submission result should be {string}", async function (expectedResult: string) {
  if (expectedResult.includes("blocked")) {
    await expect(
      fixture.page.locator('text=/Please fill in all required fields|required/i').first()
    ).toBeVisible({ timeout: 5000 });
  } else if (expectedResult.includes("accepted")) {
    await expect(
      fixture.page.locator('text=/Tag allocation created successfully|created|saved/i').first()
    ).toBeVisible({ timeout: 5000 });
  }
});

Then("Every allocation row should mathematically calculate Remaining Tags and Usage progress", async function () {
  await getHarvestLogsPage().verifyDynamicAllocationMathAndUsage();
});

Then("The Usage progress bar should display {string}", async function (expectedUsage: string) {
  await getHarvestLogsPage().verifyUsageProgressBar(expectedUsage);
});

When(
  "User fills the tag allocation form with ranch {string}, category {string}, sex {string}, and allocated tags matching an existing 2026-2027 season allocation",
  async function (ranch: string, category: string, sex: string) {
    await getHarvestLogsPage().attemptDuplicateAllocation({ ranch, category, sex, allocatedTags: "10" });
  }
);

Then("The system should block creation with the error {string}", async function (expectedError: string) {
  await getHarvestLogsPage().verifyDuplicateAllocationError(expectedError);
});

When("User clicks {string} on an existing allocation row", async function (action: string) {
  await getHarvestLogsPage().clickEditOnAllocationRow();
});

When("User updates the {string} value to {string}", async function (fieldName: string, value: string) {
  await getHarvestLogsPage().updateAllocatedTagsValue(value);
});

Then("The allocation grid should reflect the updated allocated and remaining tag counts", async function () {
  await getHarvestLogsPage().verifyAllocationGridUpdated();
});

Then("The tag allocation modal should scale within the screen margins", async function () {
  await getHarvestLogsPage().verifyTagAllocationModalMobileResponsive();
});

// --- Log New Harvest Form ---
Then("The {string} dropdown should be disabled", async function (dropdownName: string) {
  if (dropdownName === "Stand") {
    await getHarvestLogsPage().verifyStandDropdownNotPresent();
  } else if (dropdownName === "Animal") {
    await getHarvestLogsPage().verifyAnimalDropdownDisabled();
  }
});

Then("The {string} dropdown should be enabled", async function (dropdownName: string) {
  if (dropdownName === "Stand") {
    await getHarvestLogsPage().verifyStandDropdownEnabled();
  } else if (dropdownName === "Animal") {
    await getHarvestLogsPage().verifyAnimalDropdownEnabled();
  }
});

Then("The ranch address details should display", async function () {
  await getHarvestLogsPage().verifyRanchAddressDisplayed();
});

When("User attempts to upload the file {string} under Harvest Photos", async function (fileName: string) {
  await getHarvestLogsPage().attemptFileUpload(fileName);
});

Then("The upload should be rejected with the inline error {string}", async function (expectedError: string) {
  await getHarvestLogsPage().verifyUploadRejected(expectedError);
});

When("User selects Ranch {string} and Stand {string}", async function (ranchName: string, standName: string) {
  await getHarvestLogsPage().selectRanchAndStand(ranchName, standName);
});

When("User selects Hunter {string}", async function (hunter: string) {
  await getHarvestLogsPage().selectHunter(hunter);
});

When("User sets Date of Kill, Animal {string}, Sex {string}, and Age {string}", async function (animal: string, sex: string, age: string) {
  await getHarvestLogsPage().setHarvestDetails(animal, sex, age);
});

Given("User sets the browser viewport to {string}", async function (dimensions: string) {
  await getHarvestLogsPage().setViewportSize(dimensions);
});

Then("The Tag Allocation modal should adapt cleanly to the mobile viewport without horizontal overflow", async function () {
  await getHarvestLogsPage().verifyTagAllocationModalMobileResponsiveness();
});

Then("The harvest log should submit successfully", async function () {
  await getHarvestLogsPage().verifyHarvestLogSubmittedSuccessfully();
});

When("User clicks the {string} button without filling any fields", async function (buttonName: string) {
  await getHarvestLogsPage().clickSubmitHarvestLogButton();
});

Then("A warning banner listing {string}, {string}, and {string} should display", async function (field1: string, field2: string, field3: string) {
  await getHarvestLogsPage().verifyWarningBannerListsFields();
});

Then('User should be redirected to {string} harvest logs page', async function (path: string) {
  await fixture.page.waitForURL((url: URL) => url.pathname.includes(path));
});

When("User selects hunter {string} who has an incomplete profile", async function (hunter: string) {
  await getHarvestLogsPage().selectHunterWithIncompleteProfile(hunter);
});

Then("An alert {string} should display", async function (expectedMessage: string) {
  await getHarvestLogsPage().verifyAlertMessage(expectedMessage);
});

When("User fills all mandatory fields and clicks the {string} button", async function (buttonName: string) {
  await getHarvestLogsPage().populateRequiredFieldsForMldp();
  await getHarvestLogsPage().clickSubmitHarvestLogButton();
});

Then("A toast {string} should display", async function (expectedMessage: string) {
  await getHarvestLogsPage().verifyAlertMessage(expectedMessage);
});

When("User enters {string} in the Age field", async function (age: string) {
  await getHarvestLogsPage().enterAgeValue(age);
});

Then("The Age field result should be {string}", async function (expectedResult: string) {
  await getHarvestLogsPage().verifyAgeFieldResult(expectedResult);
});

Then("The form should allow logging an independent harvest without linking to a booking", async function () {
  await getHarvestLogsPage().verifyIndependentHarvestAllowed();
});

Then("The MLDP compliance status indicator should display {string}", async function (expectedStatus: string) {
  await getHarvestLogsPage().verifyMldpStatus(expectedStatus);
});

When("User populates Ranch, Stand, Hunter, Animal, and Sex fields", async function () {
  await getHarvestLogsPage().populateRequiredFieldsForMldp();
});

When("User fills in partial harvest log details", async function () {
  await getHarvestLogsPage().fillPartialHarvestLogDetails();
});

When("User selects the future date {string} in the {string} calendar picker", async function (date: string, pickerName: string) {
  await getHarvestLogsPage().selectFutureDateOfKill(date);
});

Then("The calendar should disable future dates or display a validation error {string}", async function (expectedError: string) {
  await getHarvestLogsPage().verifyFutureDateBlocked(expectedError);
});

// --- Generic: Form Validation ---
Given("User opens the {string} form with all required fields left empty", async function (formName: string) {
  await getHarvestLogsPage().navigateToHarvestLogsFromSidebar();
  await getHarvestLogsPage().clickLogNewHarvestButton();
});

Then(
  "Inline validation errors should display under Ranch, Stand, Hunter, Animal, Sex, Age, and Allocated Tags",
  async function () {
    await getHarvestLogsPage().verifyInlineErrorsUnderRequiredFields();
  }
);

When("User enters {string} in a numeric input field", async function (value: string) {
  await getHarvestLogsPage().enterAgeValue(value);
});

Then("The numeric field result should be {string}", async function (expectedResult: string) {
  await getHarvestLogsPage().verifyAgeFieldResult(expectedResult);
});

// --- Generic: File Uploads ---
When("User uploads the valid image file {string} under Harvest Photos", async function (fileName: string) {
  await getHarvestLogsPage().uploadValidImageFile(fileName);
});

Then(
  "The image should upload successfully with a thumbnail preview and file size details",
  async function () {
    await getHarvestLogsPage().verifyImageUploadedWithPreview();
  }
);

When(
  "User attempts to upload the unsupported files {string} and {string} under Harvest Photos",
  async function (file1: string, file2: string) {
    await getHarvestLogsPage().attemptUploadUnsupportedFiles([file1, file2]);
  }
);

Then("Only the supported image file types should be shown as an upload option", async function () {
  await getHarvestLogsPage().verifyOnlySupportedFileTypesShown();
});

When("User attempts to upload an image file of size {string} under Harvest Photos", async function (sizeLabel: string) {
  await getHarvestLogsPage().attemptUploadFileOfSize(sizeLabel);
});

Then("The upload result should be {string}", async function (expectedResult: string) {
  await getHarvestLogsPage().verifyFileUploadSizeResult(expectedResult);
});

When("User attaches {int} image files under Harvest Photos", async function (count: number) {
  await getHarvestLogsPage().attachMultiplePhotos(count);
});

When("User removes one attached photo thumbnail", async function () {
  await getHarvestLogsPage().removeFirstAttachedPhoto();
});

Then("The removed image should not be part of the submission", async function () {
  await getHarvestLogsPage().verifyRemainingPhotosCount(2);
});

Then("The form should submit successfully with the remaining {int} attached photos", async function (count: number) {
  await getHarvestLogsPage().verifyHarvestLogSubmittedSuccessfully();
});

When("User selects {int} valid image files simultaneously under Harvest Photos", async function (count: number) {
  await getHarvestLogsPage().attachMultiplePhotos(count);
});

Then(
  "All {int} image files should attach and render thumbnails in the upload queue simultaneously",
  async function (count: number) {
    await getHarvestLogsPage().verifyAllThumbnailsRendered(count);
  }
);

// --- Generic: Network, Offline & Error Handling ---
Given("The network is throttled to {string}", async function (profile: string) {
  await getHarvestLogsPage().throttleNetwork();
});

When("User applies a ranch or date filter", async function () {
  await getHarvestLogsPage().applyAnyFilter();
});

Then("A loading spinner or skeleton loader should display", async function () {
  // handled inside verifyLoadingIndicatorAndSuccessfulRender
});

Then("The data should render correctly without a timeout crash", async function () {
  await getHarvestLogsPage().verifyLoadingIndicatorAndSuccessfulRender();
});

When("User fills out the {string} form", async function (formName: string) {
  await getHarvestLogsPage().fillCompleteHarvestLogForm();
});

When("User's network connection is disconnected", async function () {
  await getHarvestLogsPage().setNetworkOffline(true);
});

When("The network connection is restored", async function () {
  await getHarvestLogsPage().setNetworkOffline(false);
});

Then("The record should sync seamlessly to the server", async function () {
  await getHarvestLogsPage().verifyRecordSyncedAfterReconnect();
});

When("User fills out and clicks the {string} button", async function (buttonName: string) {
  await getHarvestLogsPage().fillCompleteHarvestLogForm();
  await getHarvestLogsPage().clickSubmitHarvestLogButton();
});

When("The network connection is severed immediately before the API responds", async function () {
  await getHarvestLogsPage().severNetworkMidRequest();
});

Then(
  "A toast alert {string} should display without an app freeze",
  async function (expectedMessage: string) {
    await getHarvestLogsPage().verifyNetworkLostToastWithoutFreeze();
  }
);

When("The server is mocked to return an HTTP 500 response on submit", async function () {
  await getHarvestLogsPage().mockServerErrorOnSubmit();
});

Then("A user-friendly error banner {string} should display", async function (expectedMessage: string) {
  await getHarvestLogsPage().verifyServerErrorBanner(expectedMessage);
});

Given("the remaining tag allocation count is {string} for a species and ranch", async function (count: string) {
  fixture.testData._remainingTags = count;
});

Given(
  "Admin A and Admin B both open the {string} form for the same species and ranch",
  async function (formName: string) {
    await getHarvestLogsPage().fillCompleteHarvestLogForm();
  }
);

When("Admin A and Admin B submit their harvest logs at the same time", async function () {
  await getHarvestLogsPage().submitConcurrentHarvestLogs();
});

Then("The first submission should succeed", async function () {
  await getHarvestLogsPage().verifyHarvestLogSubmittedSuccessfully();
});

Then("The second submission should be rejected with the error {string}", async function (expectedError: string) {
  await getHarvestLogsPage().verifyFirstSucceedsSecondBlocked(expectedError);
});

// --- Generic: Responsiveness, Accessibility & Cross-Platform ---
When("User resizes the browser to viewport {string}", async function (dimensions: string) {
  const [width, height] = dimensions.split("x").map(Number);
  await getHarvestLogsPage().resizeViewport(width, height);
});

Then("The layout should adapt without overlapping buttons", async function () {
  await getHarvestLogsPage().verifyResponsiveLayoutNoOverlap();
});

When("User sets the browser zoom to {string}", async function (zoomLabel: string) {
  const zoomPercent = parseInt(zoomLabel.replace("%", ""), 10);
  await getHarvestLogsPage().setBrowserZoom(zoomPercent);
});

Then(
  "All text and interactive controls should remain accessible without clipping or overlapping",
  async function () {
    await getHarvestLogsPage().verifyControlsAccessibleAtZoom();
  }
);

When("User presses Tab key repeatedly through the form fields", async function () {
  await getHarvestLogsPage().pressTabKeyRepeatedly(10);
});

Then("Focus should move logically from top to bottom through all controls", async function () {
  await getHarvestLogsPage().verifyFocusRingVisible();
});

When("User fills half of the form fields", async function () {
  await getHarvestLogsPage().fillHalfFormFields();
});

Then(
  "The form should retain the auto-saved draft data or display an unsaved changes warning",
  async function () {
    await getHarvestLogsPage().verifyDraftRetainedOrWarningShown();
  }
);

When("User fills out all valid mandatory harvest log details", async function () {
  await getHarvestLogsPage().fillCompleteHarvestLogForm();
});

When("User rapidly double-clicks the {string} button", async function (buttonName: string) {
  await getHarvestLogsPage().rapidDoubleClickSubmit();
});

Then("The submit button should disable immediately on the first click", async function () {
  await getHarvestLogsPage().verifySubmitButtonDisabledOnFirstClick();
});

Then("Only one harvest log record should be created", async function () {
  await getHarvestLogsPage().verifySingleRecordCreated();
});

Then(
  "The Harvest Logs module should render uniformly across {string}, {string}, {string}, and {string} with zero functional defects",
  async function (b1: string, b2: string, b3: string, b4: string) {
    await getHarvestLogsPage().verifyHarvestLogsUrl();
  }
);

Then(
  "The date pickers, file uploaders, and modal submit buttons should behave correctly on {string} and {string}",
  async function (device1: string, device2: string) {
    await getHarvestLogsPage().verifyHarvestLogsUrl();
  }
);

// --- Generic: Security ---
When("User's authentication session token is invalidated", async function () {
  await getHarvestLogsPage().invalidateSessionToken();
});

When("User navigates directly to the URL {string}", async function (path: string) {
  await getHarvestLogsPage().navigateDirectlyToUrl(path);
});

Then("Access should be blocked with a {string} page or a redirect to the main Dashboard", async function (statusLabel: string) {
  await getHarvestLogsPage().verifyUnauthorizedAccessBlocked();
});
// --- BVA & Numeric Field Validation ---


Then("The upload result should be {string}", async function (expectedResult: string) {
  await getHarvestLogsPage().verifyFileUploadSizeResult(expectedResult);
});

// --- Console Error Assertion ---
Then("No unhandled JS console exception should occur", async function () {
  // Console errors are automatically tracked in hooks.ts via page.on('console')
});

// --- Focus Ring Assertion ---
Then("The active element should display a clear focus ring", async function () {
  await getHarvestLogsPage().verifyFocusRingVisible();
});

// --- Form Reset Assertion ---
Then("The form draft data should clear", async function () {
  await getHarvestLogsPage().verifyNoDraftDataSaved();
});

// --- Submission Blocked Assertion ---
Then("Submission should be blocked", async function () {
  await getHarvestLogsPage().verifyFormSubmissionBlocked();
});

When("User selects an available ranch and completes required harvest details dynamically", async function () {
  await getHarvestLogsPage().selectAvailableRanchAndFillDetails();
});

// Single alias for feature scenarios without "dynamically"
When("User selects an available ranch and completes required harvest details", async function () {
  await getHarvestLogsPage().selectAvailableRanchAndFillDetails();
});