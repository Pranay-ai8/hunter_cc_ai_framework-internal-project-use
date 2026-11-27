import { Given, When, Then } from "@cucumber/cucumber";
import { expect } from "@playwright/test";
import { fixture } from "../../support/pageFixture";
import { GuestsPage } from "../../pages/guestsPage";
import { LoginPage } from "../../pages/loginPage";
import { getTestDataByKey } from '../../helper/parsers/CSVParser';
import { ensureSuperAdminSession } from "../../support/sessionGuard";

function getGuestsPage(): GuestsPage {
  if (!fixture.pages.guestsPage) {
    fixture.pages.guestsPage = new GuestsPage(fixture.page);
  }
  return fixture.pages.guestsPage;
}

function getLoginPage(): LoginPage {
  if (!fixture.pages.loginPage) {
    fixture.pages.loginPage = new LoginPage(fixture.page);
  }
  return fixture.pages.loginPage;
}

function getTestCaseKey(worldContext: any): string {
  // 1. Check if hooks already assigned testData
  if (fixture.testData?.Key) {
    return fixture.testData.Key;
  }

  // 2. Extract from world context pickle or testCaseStarted tags
  const pickleTags = worldContext.pickle?.tags || [];
  const testCaseTags = worldContext.testCaseStarted?.tags || [];
  const combined = [...pickleTags, ...testCaseTags];

  const keyTag = combined.find((t: any) => {
    const tagName = typeof t === 'string' ? t : (t.name || t.name);
    return tagName && tagName.startsWith('@Key:');
  });

  if (keyTag) {
    const fullTag = typeof keyTag === 'string' ? keyTag : keyTag.name;
    return fullTag.replace('@Key:', '').trim();
  }

  throw new Error('❌ Missing @Key:TC_GST_XXX tag on scenario execution.');
}
let capturedDownload: any = null;

// --- Navigation & Auth (Scoped to Guests Module) ---
Given('User logged in as Super Admin', async function () {
  // Suite-level login happens exactly once (see hooks.ts BeforeAll) and every scenario shares that same
  // session/browser page. This step must only VERIFY that session is still there, never silently trigger a
  // fresh real login - repeatedly re-authenticating here (previously triggered whenever the current page's
  // URL wasn't literally "/dashboard" or "/guests", which was most other module pages) is what drove enough
  // real backend login attempts over a long run to trip the backend's own rate-limit/account-lock.
  await ensureSuperAdminSession(fixture.page);
});


Given("User navigates to the login page for Guests module", async function () {
  await getLoginPage().navigateToLogin();
});

When("User logs into Guests module with valid credentials", async function () {
  const email = fixture.testData?.username || process.env.SUPER_ADMIN_EMAIL || "superadmin@buckhouse.com";
  const password = fixture.testData?.password || process.env.SUPER_ADMIN_PASSWORD || "AdminUser123!";
  await getLoginPage().login(email, password);
});

When("User logs into Guests module as Super Admin", async function () {
  const email = fixture.testData?.username || "superadmin@buckhouse.com";
  const password = fixture.testData?.password || "AdminUser123!";
  await getLoginPage().login(email, password);
});

When("User navigates to the {string} module from the sidebar", async function (menuName: string) {
  if (menuName === "Guests") {
    await getGuestsPage().navigateToGuests();
  }
});
When('User navigates to the Guests module from the left navigation menu', async function () {
  await getGuestsPage().navigateToGuests();
});
When("User taps the hamburger menu and selects {string}", async function (menuName: string) {
  if (menuName === "Guests") {
    await getGuestsPage().navigateToGuestsViaHamburger();
  }
});

// Given("User sets the viewport size to {int} pixels by {int} pixels", async function (width: number, height: number) {
//   await getGuestsPage().setViewportSize(width, height);
// });

When("User clicks on guest row {string}", async function (identifier: string) {
  let target = identifier;
  if (identifier === "CONFIGURED_GUEST") {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    target = testData['firstName'] || testData['guest_name'] || "Test Hunter";
  }
  await getGuestsPage().clickGuestRow(target);
});
Then("The table should update to display guests of type {string}", async function (expectedType: string) {
  await getGuestsPage().verifyGuestTypeInTable(expectedType);
});
When('User clicks {string} on guest row {string}', async function (actionInput: string, guestInput: string) {
  let guestName = guestInput;
  if (guestInput === 'CONFIGURED_GUEST' || guestInput === 'CONFIGURED_HUNTER' || guestInput === 'CONFIGURED_GUEST_TO_DELETE' || guestInput === 'CONFIGURED_NON_HUNTER_GUEST') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    guestName = testData['guest_name'] ?? testData['firstName'] ?? 'chethan';
  }

  const isHunterRequired = actionInput.toLowerCase().includes('hunter') || guestInput === 'CONFIGURED_HUNTER';
  const isNonHunterRequired = guestInput === 'CONFIGURED_NON_HUNTER_GUEST';

  if (actionInput.toLowerCase().includes('edit')) {
    await getGuestsPage().clickEditForGuest(guestName, isHunterRequired, isNonHunterRequired);
  } else if (actionInput.toLowerCase().includes('delete')) {
    this.deletedGuestName = await getGuestsPage().clickDeleteForGuest(guestName);
  }
});
// --- TC_GST_001: Guest Type filter ---
When("User selects {string} from the Guest Type dropdown", async function (type: "Hunters" | "Non-Hunter Guests" | "All Types") {
  await getGuestsPage().selectGuestTypeFilter(type);
});



// --- TC_GST_002: Ranch filter ---
When('User selects {string} from the Ranch dropdown', async function (ranchInput: string) {
  let targetRanch = ranchInput;

  if (ranchInput === 'CONFIGURED_RANCH') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    targetRanch = testData['ranch_name'] ?? testData['ranch'] ?? 'QUATTRO DIAMANTE RANCH';
  }

  await getGuestsPage().selectRanchFilter(targetRanch);
});

Then('The grid should filter to guests belonging to {string}', async function (ranchInput: string) {
  let targetRanch = ranchInput;

  if (ranchInput === 'CONFIGURED_RANCH') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    targetRanch = testData['ranch_name'] ?? testData['ranch'] ?? 'QUATTRO DIAMANTE RANCH';
  }

  await getGuestsPage().verifyRanchFilterActive(targetRanch);
});
// ✅ Added missing step definition
Then('Selecting {string} should reset the ranch filter', async function (_resetText: string) {
  await getGuestsPage().verifyRanchFilterReset();
});

// --- TC_GST_003: Layout view toggle ---
When("User clicks the Card View toggle icon on Guest Directory", async function () {
  await getGuestsPage().clickCardViewToggle();
});

When("User clicks the Table View toggle icon on Guest Directory", async function () {
  await getGuestsPage().clickTableViewToggle();
});

Then("The Guest Directory should display in Card layout", async function () {
  await getGuestsPage().verifyCardLayoutVisible();
});

Then("The Guest Directory should display in Table layout", async function () {
  await getGuestsPage().verifyTableLayoutVisible();
});

// --- TC_GST_004: Mandatory field validation ---
When("User clears the Full Name input field", async function () {
  await getGuestsPage().clearFullNameField();
});

When("User clears the Email Address input field", async function () {
  await getGuestsPage().clearEmailField();
});

When("User clicks the guest form {string} button", async function (buttonName: string) {
  if (buttonName === "Save Changes") {
    await getGuestsPage().clickSaveChanges();
  } else if (buttonName === "Cancel") {
    await getGuestsPage().clickCancelButton();
  }
});


Then("Submission should be blocked with an inline error under Full Name", async function () {
  await getGuestsPage().verifyMandatoryFieldErrors();
});

// --- TC_GST_005: Successful profile update ---
When("User modifies Date of Birth and Address with {string}", async function (dataKey: string) {
  const dob = this.csvData?.dob || "01/15/1990";
  const address = this.csvData?.address || this.csvData?.temp_address || "123 Ranch Blvd, Suite 100";

  await getGuestsPage().fillDobAndAddress(dob, address);
});
// --- Step: Success Toast ---
Then("A success toast {string} should display", async function (expectedMsg: string) {
  // Priority: 1. CSV message column -> 2. Expected step parameter -> 3. Default Toast String
  const targetMessage = (this.csvData?.message && !this.csvData.message.includes("CONFIGURED")) 
    ? this.csvData.message 
    : (expectedMsg && !expectedMsg.includes("CONFIGURED")) 
      ? expectedMsg 
      : "Guest profile updated successfully!";

  await getGuestsPage().verifySuccessToast(targetMessage);
});

Then('The profile summary should display the trimmed name {string}', async function (expectedNameInput: string) {
  let expectedName = expectedNameInput;

  if (expectedNameInput === 'CONFIGURED_TRIMMED_NAME') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    expectedName = testData['trimmed_name'] ?? testData['guest_name'] ?? 'John Hunter Smith';
  }

  const cleanExpectedName = typeof expectedName === 'string' ? expectedName.trim() : String(expectedName);
  await getGuestsPage().verifyTrimmedNameOnSummary(cleanExpectedName);
});
// --- UI Assertion Steps ---
Then('The Save button should show a Saving state', async function () {
  await getGuestsPage().verifySavingLoaderVisible();
});

Then('User should be redirected to the Guest Profile summary page', async function () {
  await getGuestsPage().verifyRedirectedToProfileSummary();
});
// --- TC_GST_006: Document upload validation ---
When("User selects Document Type {string}", async function (type: string) {
  await getGuestsPage().selectDocumentType(type);
});

When("User uploads file {string}", async function (fileName: string) {
  await getGuestsPage().uploadDocument(fileName);
});

Then("The document {string} should attach successfully", async function (fileName: string) {
  await getGuestsPage().verifyDocumentAttachedSuccessfully(fileName);
});

When(/^User clicks (?:the )?"Save Changes" button$/, async function () {
  await getGuestsPage().clickSaveChangesButton();
});

Then("Guest profile update success message should display", async function () {
  await getGuestsPage().verifyProfileUpdateSuccessToast();
});
// --- TC_GST_007: SMS consent persistence ---

When("User toggles the SMS consent checkbox on guest edit form", async function () {
  const phone = this.csvData?.phone || "5550192834";
  await getGuestsPage().toggleSmsConsentCheckbox(phone);
});

Then("The SMS consent checkbox state should persist as updated", async function () {
  await getGuestsPage().verifySmsConsentCheckboxPersisted();
});
// --- TC_GST_008: Hunter License Information ---
When("User enters License Number {string} and License Expiration Date {string}", async function (
  licenseNumber: string,
  expDate: string
) {
  await getGuestsPage().fillGuestProfileForm({ licenseNumber, licenseExpirationDate: expDate });
});

Then("The Hunter License Information should display License {string} and Expiration {string} on the profile summary", async function (
  licenseNumber: string,
  expDate: string
) {
  await getGuestsPage().verifyHunterLicenseSummaryDisplays(licenseNumber, expDate);
});



// --- Step: Click Edit for Hunter Guest (TC_GST_008) ---
When('User clicks "Edit" on hunter guest row {string}', async function (guestInput: string) {
  let guestName = guestInput;
  if (guestInput === 'CONFIGURED_HUNTER') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    guestName = testData['guest_name'] ?? 'chethan';
  }
  await getGuestsPage().clickEditForGuest(guestName, true);
});



// --- Step: Enter Hunter License Information (TC_GST_008) ---
When('User clicks {string} on the guest summary page', async function (buttonName: string) {
  if (/edit profile/i.test(buttonName)) {
    await getGuestsPage().clickEditProfileOnSummaryPage();
  } else {
    await getGuestsPage().clickQuickActionsEditProfile();
  }
});
// --- TC_GST_009: Breadcrumb navigation ---
When("User clicks the {string} breadcrumb link", async function (linkText: string) {
  if (linkText === "Back to Guest Details") {
    await getGuestsPage().clickBackToGuestDetails();
  } else if (linkText === "Back to Guests") {
    await getGuestsPage().clickBackToGuests();
  }
});
Then("User should be on the Guest Profile summary view", async function () {
  await getGuestsPage().verifyOnGuestProfileSummary();
});

Then("User should be on the main Guest Directory page", async function () {
  await getGuestsPage().verifyOnGuestDirectory();
});



// --- TC_GST_010: Emergency contact persistence ---
When("User fills Contact Name {string}, Contact Phone {string}, and Relationship {string}", async function (
  name: string,
  phone: string,
  relationship: string
) {
  await getGuestsPage().fillGuestProfileForm({
    emergencyContactName: name,
    emergencyContactPhone: phone,
    emergencyContactRelationship: relationship,
  });
});



// --- TC_GST_011: Real-time search ---

When("User types {string} in the guest search bar", async function (query: string) {
  await getGuestsPage().searchFor(query);
});
Then("The table grid should update dynamically without a full page reload", async function () {
  await getGuestsPage().verifyGridUpdatesWithoutReload("");
});

// --- TC_GST_012: SQL / XSS search safety ---
When("User enters payload {string} in the search input box", async function (payload: string) {
  await getGuestsPage().submitPayloadInSearch(payload);
});

Then("The search should handle the input safely without JS execution or a server error", async function () {
  await getGuestsPage().verifyNoServerErrorDisplayed();
});

// --- TC_GST_013: Email syntax ---

When('User enters guest email {string}', async function (email: string) {
  await getGuestsPage().enterGuestEmail(email);
});
Then("An invalid email format error should display under the email field", async function () {
  await getGuestsPage().verifyInvalidEmailError();
});

// --- TC_GST_014: Duplicate email prevention ---

When('User enters duplicate email {string} on guest form', async function (emailInput: string) {
  let email = emailInput;

  if (emailInput === 'CONFIGURED_EMAIL') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    email = testData['duplicate_email'] ?? testData['email'];
  }

  await getGuestsPage().enterEmailAndBlur(email);
});
Then("Submission should fail with a duplicate email error for guest module", async function () {
  await getGuestsPage().verifyDuplicateEmailError();
});

// --- TC_GST_015: Phone format ---
When("User attempts to enter non-numeric phone number {string}", async function (invalidPhone: string) {
  await getGuestsPage().enterPhoneNumber(invalidPhone);
});

Then("The phone number input field should reject non-numeric characters and remain empty", async function () {
  await getGuestsPage().verifyPhoneNumberInputRejectsNonNumeric();
});

// --- TC_GST_016: DOB future date restriction ---
When("User enters Date of Birth {string}", async function (dob: string) {
  await getGuestsPage().enterDateOfBirth(dob);
});

Then("The future date of birth should be blocked", async function () {
  await getGuestsPage().verifyFutureDobBlocked();
});

// --- TC_GST_017: ZIP code BVA ---
When("User enters {int}-digit ZIP code {string} on guest form", async function (digitCount: number, zip: string) {
  await getGuestsPage().enterZipCode(zip);
});

Then("The ZIP code field should accept {string} as valid", async function (expectedZip: string) {
  await getGuestsPage().verifyZipAccepted(expectedZip);
});

When("User enters invalid ZIP code {string} on guest form", async function (zip: string) {
  await getGuestsPage().enterZipCode(zip);
});

Then("An invalid ZIP code error {string} should display under the ZIP field", async function (expectedMsg: string) {
  await getGuestsPage().verifyInvalidZipError(expectedMsg);
});

// --- TC_GST_018: License expiration past date ---
When("User enters License Expiration Date {string}", async function (date: string) {
  await getGuestsPage().enterLicenseExpirationDate(date);
});

Then("A License Expired warning should display", async function () {
  await getGuestsPage().verifyLicenseExpiredWarning();
});

// --- TC_GST_019: Drag and drop upload ---
When("User drags file {string} into the upload dropzone", async function (fileName: string) {
  await getGuestsPage().uploadDocument(fileName);
});


// --- TC_GST_020: Duplicate file prevention ---
Then("A duplicate document attachment error should display", async function () {
  await getGuestsPage().verifyDuplicateFileError();
});

// --- TC_GST_021: Cancel discards changes ---


When('User changes Full Name and Address to {string}', async function (inputFlag: string) {
  let tempName = 'Temporary Name';
  let tempAddress = '100 Fake St';

  if (inputFlag === 'CONFIGURED_DATA') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    tempName = testData['temp_name'] ?? 'Temporary Name';
    tempAddress = testData['temp_address'] ?? '100 Fake St';
  } else if (inputFlag.includes('|')) {
    const parts = inputFlag.split('|');
    tempName = parts[0];
    tempAddress = parts[1];
  }

  await getGuestsPage().modifyFullNameAndAddress(tempName, tempAddress);
});

Then(
  'The form should discard changes and return to the Guest Profile summary with original data {string}',
  async function (guestInput: string) {
    let originalName = guestInput;

    if (guestInput === 'CONFIGURED_GUEST') {
      const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
      originalName = testData['guest_name'];
    }

    await getGuestsPage().verifyChangesDiscardedAndReturnedToSummary(originalName);
  }
);

// --- TC_GST_022: Rapid double-click ---
When('User enters phone number {string}', async function (phoneInput: string) {
  let phone = phoneInput;

  if (phoneInput === 'CONFIGURED_PHONE') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    phone = testData['phone'] ?? testData['Phone'] ?? '+1 555-111-2222';
  }

  await getGuestsPage().fillGuestProfileForm({ phone });
});

When('User rapidly double-clicks the Save Changes button', async function () {
  await getGuestsPage().rapidDoubleClickSaveChanges();
});

Then('The Save Changes button should disable immediately with a Saving state', async function () {
  await getGuestsPage().verifySaveButtonDisabledWithLoader();
});


// --- TC_GST_023: Pagination ---
When("User changes the results per page dropdown to {string}", async function (size: "10" | "20" | "50") {
  await getGuestsPage().changePageSize(size);
});



Then("The results summary text should display {string}", async function (text: string) {
  await getGuestsPage().verifyPaginationSummaryText(text);
});

// --- TC_GST_024: Whitespace trim ---


// --- TC_GST_025: Unicode name ---

When('User enters Full Name {string}', async function (nameInput: string) {
  let nameToEnter = nameInput;

  if (nameInput === 'CONFIGURED_UNICODE_NAME') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    nameToEnter = testData['unicode_name'] ?? testData['untrimmed_name'] ?? "José-María O'Connor";
  } else if (nameInput === 'CONFIGURED_UNTRIMMED_NAME') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    nameToEnter = testData['untrimmed_name'] ?? '   John Hunter Smith   ';
  }

  await getGuestsPage().enterFullName(nameToEnter);
});

Then('The profile summary should render the Unicode name {string} correctly', async function (nameInput: string) {
  let expectedUnicodeName = nameInput;

  if (nameInput === 'CONFIGURED_UNICODE_NAME') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    expectedUnicodeName = testData['unicode_name'] ?? "José-María O'Connor";
  }

  await getGuestsPage().verifyProfileDisplaysUnicodeName(expectedUnicodeName);
});
// --- TC_GST_026: XSS sanitization ---
When("User enters payload {string} in Address, Emergency Contact Name, or License fields", async function (payload: string) {
  await getGuestsPage().verifyNoAlertDialogTriggered();
  await getGuestsPage().fillGuestProfileForm({ address: payload, emergencyContactName: payload });
});

Then("The script payload should render as sanitized plain text on the profile summary", async function () {
  const payload = fixture.testData?.xssPayload || "<script>alert('XSS')</script>";
  await getGuestsPage().verifySummaryRendersPlainText(payload);
});

// --- TC_GST_027: Waiver status badge ---

Then('The {string} waiver status badge should display the correct color', async function (status: string) {
  await getGuestsPage().verifyWaiverBadgeColor(status);
});
// --- TC_GST_028: Signed waiver download ---

When('User clicks on completed guest row {string}', async function (guestInput: string) {
  let guestName = guestInput;

  if (guestInput === 'CONFIGURED_GUEST') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    guestName = testData['guest_name'] ?? 'radii';
  }

  await getGuestsPage().clickCompletedGuestRow(guestName);
});

When('User clicks the signed waiver PDF preview button', async function () {
  await getGuestsPage().clickSignedWaiverPdfButton();
});

Then('The signed waiver PDF should open or download successfully', async function () {
  await getGuestsPage().verifySignedWaiverOpenedOrDownloaded();
});
// --- TC_GST_029: Missing waiver empty state ---
When('User clicks on pending guest row {string}', async function (guestInput: string) {
  let guestName = guestInput;

  if (guestInput === 'CONFIGURED_GUEST') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    guestName = testData['guest_name'] ?? 'test9';
  }

  await getGuestsPage().clickPendingGuestRow(guestName);
});

Then(
  'The Signed Waiver card should display a not-available message {string}',
  async function (messageInput: string) {
    let expectedMsg = messageInput;

    if (messageInput === 'CONFIGURED_EMPTY_MESSAGE') {
      const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
      expectedMsg = testData['expected_empty_message'] ?? 'Signed waiver PDF is not available yet.';
    }

    await getGuestsPage().verifySignedWaiverEmptyStateMessage(expectedMsg);
  }
);

// --- TC_GST_030: Session expiration ---
When("The auth session token expires for guest", async function () {
  await getGuestsPage().expireSessionToken();
});

Then("The application should redirect to the login page without console errors", async function () {
  await getGuestsPage().verifyRedirectToLogin();
});

When("User presses Tab key repeatedly from the page top", async function () {
  await getGuestsPage().tabThroughFormFields();
});

Then("Focus should move logically through all Edit Guest Profile form fields", async function () {
  await getGuestsPage().verifyLogicalFocusOrder();
});
/// --- TC_GST_032: //

Then('The sidebar should collapse into a hamburger menu', async function () {
  await getGuestsPage().verifySidebarCollapsed();
});

Then('The form grids should collapse cleanly into a single column layout', async function () {
  await getGuestsPage().verifySingleColumnLayoutOnMobile();
});

// --- TC_GST_033: Emergency contact phone formatting ---
When('User fills Emergency Contact Name {string}', async function (nameInput: string) {
  let emergencyName = nameInput;

  if (nameInput === 'CONFIGURED_EMERGENCY_NAME') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    emergencyName = testData['emergency_name'] ?? 'Jane Doe';
  }

  await getGuestsPage().fillEmergencyContactName(emergencyName);
});

When('User enters emergency phone number {string}', async function (phoneInput: string) {
  let phone = phoneInput;

  if (phoneInput === 'CONFIGURED_PHONE') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    phone = testData['phone'] ?? '555-000-0003';
  }

  await getGuestsPage().enterEmergencyPhone(phone);
});
When('User selects country code {string}', async function (codeInput: string) {
  let countryCode = codeInput;

  if (codeInput === 'CONFIGURED_COUNTRY_CODE') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    countryCode = testData['country_code'] ?? 'United States +1';
  }

  await getGuestsPage().selectCountryCode(countryCode);
});
Then('The Emergency Contact phone should display formatted as {string}', async function (expectedPhoneInput: string) {
  let expectedPhone = expectedPhoneInput;

  if (expectedPhoneInput === 'CONFIGURED_FORMATTED_PHONE') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    expectedPhone = testData['formatted_phone'] ?? '+1 555-000-0003';
  }

  await getGuestsPage().verifyFormattedEmergencyPhone(expectedPhone);
});
// --- TC_GST_034: State field length validation ---

When("User enters State value {string}", async function (stateValue: string) {
  await getGuestsPage().enterStateValue(stateValue);
});

Then("The State field value should not exceed {int} characters", async function (maxLength: number) {
  await getGuestsPage().verifyStateFieldLengthLimit(maxLength);
});

Then("A toast error {string} should display", async function (expectedErrorMessage: string) {
  await getGuestsPage().verifyToastErrorMessage(expectedErrorMessage);
});


// --- TC_GST_035: Concurrent edit conflict ---
When("Another admin updates the same guest profile concurrently", async function () {
  await getGuestsPage().simulateConflictingUpdate();
});

Then("The system should display a conflict notification without silently overwriting data", async function () {
  await getGuestsPage().verifyConflictNotificationDisplayed();
});

// --- TC_GST_036: Document removal ---

When("User clicks the delete icon beside document {string}", async function (fileName: string) {
  await getGuestsPage().clickDeleteIconForDocument(fileName);
});

When("User confirms the document deletion popup", async function () {
  await getGuestsPage().confirmDocumentDeletion();
});

Then("The document {string} should be removed from the list", async function (fileName: string) {
  await getGuestsPage().verifyDocumentRemovedFromList(fileName);
});
// --- TC_GST_037: Non-Hunter license section hidden ---
Then("The Hunter License Information section should be hidden or disabled", async function () {
  await getGuestsPage().verifyHunterLicenseSectionHiddenOrDisabled();
});

// --- TC_GST_038: Quick Actions CTA ---
When("User clicks the {string} button on the Quick Actions card", async function (buttonName: string) {
  if (buttonName === "Edit Profile") {
    await getGuestsPage().clickQuickActionsEditProfile();
  }
});

Then("User should be redirected to the guest edit profile URL", async function () {
  await getGuestsPage().verifyRedirectedToEditProfileUrl();
});

// --- TC_GST_039: Cross-browser consistency ---
Then("Core Guest Directory elements should render without visual bugs", async function () {
  await getGuestsPage().verifyCoreGuestElementsRender();
});

Then("Guest Profile summary elements should render without visual bugs", async function () {
  await getGuestsPage().verifyProfilePageElementsRender();
});

// --- TC_GST_040: Zero-record empty state ---
Then('The Guest Directory grid should display a clean empty state without console errors', async function () {
  await getGuestsPage().verifyEmptyStateAndNoConsoleErrors();
});When('User fills in new guest details {string}', async function (guestInput: string) {
  let firstName = '';
  let lastName = '';
  let email = '';
  let phone = '';

  if (guestInput === 'CONFIGURED_GUEST') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    firstName = testData['first_name'] ?? testData['firstName'];
    lastName = testData['last_name'] ?? testData['lastName'];
    email = testData['email'];
    phone = testData['phone'];
  }

  await getGuestsPage().fillNewGuestForm({ firstName, lastName, email, phone });
});

Then('The new guest {string} should appear in the directory', async function (guestInput: string) {
  let expectedName = guestInput;

  if (guestInput === 'CONFIGURED_GUEST') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    expectedName = `${testData['first_name']} ${testData['last_name']}`.trim();
  }

  await getGuestsPage().verifyGuestInDirectory(expectedName);
});

When('User confirms the guest deletion popup', async function () {
  await getGuestsPage().confirmGuestDeletionPopup();
});

Then('The guest {string} should be removed from the directory', async function (guestInput: string) {
  let targetName = this.deletedGuestName;

  if (!targetName && guestInput === 'CONFIGURED_GUEST_TO_DELETE') {
    const testData = fixture.testData || await getTestDataByKey(getTestCaseKey(this));
    targetName = testData['guest_name'];
  }

  await getGuestsPage().verifyGuestRemovedFromDirectory(targetName);
});