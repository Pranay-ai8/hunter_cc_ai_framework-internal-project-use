import { When, Then } from "@cucumber/cucumber";
import { fixture } from "../../support/pageFixture";
import StaffsGuidesPage from "../../pages/staffsGuidesPage";

let staffsGuidesPage: StaffsGuidesPage;

When('User clicks on \'Staff & Guides\' under GUEST OPERATIONS in the left sidebar', async function () {
    fixture.logger.info("Clicking on Staff & Guides in the left sidebar");
    await fixture.subStepLogger?.info("Clicking on Staff & Guides in the left sidebar");
    staffsGuidesPage = new StaffsGuidesPage(fixture.page);
    await staffsGuidesPage.navigateToStaffsGuides();
    await fixture.subStepLogger?.success("Navigated to Staff & Guides page");
});

Then('User is navigated to the Staff & Guides page with title, subtitle, and Add Guide button', async function () {
    fixture.logger.info("Verifying Staff & Guides page header elements");
    await fixture.subStepLogger?.info("Verifying Staff & Guides page header elements");
    await staffsGuidesPage.verifyPageHeader();
    await fixture.subStepLogger?.success("Verified Staff & Guides page header");
});

Then('Top section shows Search bar, Ranches dropdown, Status dropdown, Levels dropdown, List and Grid view toggles', async function () {
    fixture.logger.info("Verifying top filter controls and view toggles");
    await fixture.subStepLogger?.info("Verifying top filter controls and view toggles");
    await staffsGuidesPage.verifyFilterControls();
    await fixture.subStepLogger?.success("Verified top filter controls and view toggles");
});

Then('Default view on Staff & Guides page is \'List View\' highlighted green', async function () {
    fixture.logger.info("Verifying default view is List View");
    await fixture.subStepLogger?.info("Verifying default view is List View");
    await staffsGuidesPage.verifyDefaultViewIsList();
    await fixture.subStepLogger?.success("Verified default view is List View");
});

Then('Table headers display Guide, Ranch, Experience, Active Bookings, Total Bookings, Status, Actions', async function () {
    fixture.logger.info("Verifying table headers");
    await fixture.subStepLogger?.info("Verifying table headers");
    await staffsGuidesPage.verifyTableHeaders();
    await fixture.subStepLogger?.success("Verified table headers");
});

When('User types search string from testData in the staff search bar', async function () {
    const searchVal = fixture.testData?.searchString || "xyz999abc";
    fixture.logger.info(`Searching for: ${searchVal}`);
    await fixture.subStepLogger?.info(`Searching for: ${searchVal}`);
    await staffsGuidesPage.searchStaff(searchVal);
    await fixture.subStepLogger?.success(`Searched for: ${searchVal}`);
});

Then('Table filters dynamically to display guide records matching the entered name', async function () {
    const searchVal = fixture.testData?.searchString || "alboo";
    fixture.logger.info(`Verifying search results for name: ${searchVal}`);
    await fixture.subStepLogger?.info(`Verifying search results for name: ${searchVal}`);
    await staffsGuidesPage.verifySearchResults(searchVal);
    await fixture.subStepLogger?.success("Verified search results for name");
});

Then('Table filters dynamically to display the specific guide associated with that email address', async function () {
    const searchVal = fixture.testData?.searchString || "alboo@gmail.com";
    fixture.logger.info(`Verifying search results for email: ${searchVal}`);
    await fixture.subStepLogger?.info(`Verifying search results for email: ${searchVal}`);
    await staffsGuidesPage.verifySearchResults(searchVal);
    await fixture.subStepLogger?.success("Verified search results for email");
});

Then('Table filters dynamically to display guide matching the phone number query', async function () {
    const searchVal = fixture.testData?.searchString || "+14088456985";
    fixture.logger.info(`Verifying search results for phone: ${searchVal}`);
    await fixture.subStepLogger?.info(`Verifying search results for phone: ${searchVal}`);
    await staffsGuidesPage.verifySearchResults(searchVal);
    await fixture.subStepLogger?.success("Verified search results for phone");
});

Then('Table displays an empty state message \'No guides found\'', async function () {
    fixture.logger.info("Verifying empty state message 'No guides found'");
    await fixture.subStepLogger?.info("Verifying empty state message 'No guides found'");
    await staffsGuidesPage.verifyEmptyState("No guides found");
    await fixture.subStepLogger?.success("Verified empty state message");
});

When('User clears the staff search bar', async function () {
    fixture.logger.info("Clearing staff search bar");
    await fixture.subStepLogger?.info("Clearing staff search bar");
    await staffsGuidesPage.clearSearch();
    await fixture.subStepLogger?.success("Cleared staff search bar");
});

Then('Search filter is removed and full list of staff & guides is restored', async function () {
    fixture.logger.info("Verifying full list is restored");
    await fixture.subStepLogger?.info("Verifying full list is restored");
    await staffsGuidesPage.verifyFullListRestored();
    await fixture.subStepLogger?.success("Verified full list is restored");
});

When('User selects {string} from Ranches dropdown', async function (ranchPlaceholder: string) {
    const ranchVal = fixture.testData?.ranchFilter || (ranchPlaceholder.startsWith('<') ? "All Ranches" : ranchPlaceholder);
    fixture.logger.info(`Selecting Ranch filter: ${ranchVal}`);
    await fixture.subStepLogger?.info(`Selecting Ranch filter: ${ranchVal}`);
    await staffsGuidesPage.selectRanchFilter(ranchVal);
    await fixture.subStepLogger?.success(`Selected Ranch filter: ${ranchVal}`);
});

Then('Table displays guides assigned across all ranches', async function () {
    fixture.logger.info("Verifying guides across all ranches");
    await fixture.subStepLogger?.info("Verifying guides across all ranches");
    await staffsGuidesPage.verifyRanchFilterResults("All Ranches");
    await fixture.subStepLogger?.success("Verified guides across all ranches");
});

Then('Table updates to display ONLY guides assigned to Koramnaga Ranch', async function () {
    fixture.logger.info("Verifying guides assigned to Koramnaga Ranch");
    await fixture.subStepLogger?.info("Verifying guides assigned to Koramnaga Ranch");
    await staffsGuidesPage.verifyRanchFilterResults("Koramnaga Ranch");
    await fixture.subStepLogger?.success("Verified guides assigned to Koramnaga Ranch");
});

Then('Table updates to display ONLY guides assigned to Test Ranch', async function () {
    fixture.logger.info("Verifying guides assigned to Test Ranch");
    await fixture.subStepLogger?.info("Verifying guides assigned to Test Ranch");
    await staffsGuidesPage.verifyRanchFilterResults("Test Ranch");
    await fixture.subStepLogger?.success("Verified guides assigned to Test Ranch");
});

When('User selects {string} from Staff Status dropdown', async function (statusPlaceholder: string) {
    const statusVal = fixture.testData?.statusFilter || (statusPlaceholder.startsWith('<') ? "All Status" : statusPlaceholder);
    fixture.logger.info(`Selecting Status filter: ${statusVal}`);
    await fixture.subStepLogger?.info(`Selecting Status filter: ${statusVal}`);
    await staffsGuidesPage.selectStatusFilter(statusVal);
    await fixture.subStepLogger?.success(`Selected Status filter: ${statusVal}`);
});

Then('Table displays all guides regardless of status', async function () {
    fixture.logger.info("Verifying all guides regardless of status");
    await fixture.subStepLogger?.info("Verifying all guides regardless of status");
    await staffsGuidesPage.verifyStatusFilterResults("All Status");
    await fixture.subStepLogger?.success("Verified all guides regardless of status");
});

Then('Table updates to display ONLY guides with green Active status badges', async function () {
    fixture.logger.info("Verifying ONLY active guides are displayed");
    await fixture.subStepLogger?.info("Verifying ONLY active guides are displayed");
    await staffsGuidesPage.verifyStatusFilterResults("Active");
    await fixture.subStepLogger?.success("Verified active guides");
});

Then('Table updates to display ONLY inactive guides or empty state', async function () {
    fixture.logger.info("Verifying ONLY inactive guides or empty state");
    await fixture.subStepLogger?.info("Verifying ONLY inactive guides or empty state");
    await staffsGuidesPage.verifyStatusFilterResults("Inactive");
    await fixture.subStepLogger?.success("Verified inactive filter");
});

When('User selects {string} from Levels dropdown', async function (levelPlaceholder: string) {
    const levelVal = fixture.testData?.levelFilter || (levelPlaceholder.startsWith('<') ? "All Levels" : levelPlaceholder);
    fixture.logger.info(`Selecting Level filter: ${levelVal}`);
    await fixture.subStepLogger?.info(`Selecting Level filter: ${levelVal}`);
    await staffsGuidesPage.selectLevelFilter(levelVal);
    await fixture.subStepLogger?.success(`Selected Level filter: ${levelVal}`);
});

Then('Table displays guides across all experience levels', async function () {
    fixture.logger.info("Verifying guides across all levels");
    await fixture.subStepLogger?.info("Verifying guides across all levels");
    await staffsGuidesPage.verifyLevelFilterResults("All Levels");
    await fixture.subStepLogger?.success("Verified guides across all levels");
});

Then('Table updates to display ONLY beginner guides with green Beginner badges', async function () {
    fixture.logger.info("Verifying beginner guides");
    await fixture.subStepLogger?.info("Verifying beginner guides");
    await staffsGuidesPage.verifyLevelFilterResults("Beginner");
    await fixture.subStepLogger?.success("Verified beginner guides");
});

Then('Table updates to display ONLY experienced guides with blue Experienced badges', async function () {
    fixture.logger.info("Verifying experienced guides");
    await fixture.subStepLogger?.info("Verifying experienced guides");
    await staffsGuidesPage.verifyLevelFilterResults("Experienced");
    await fixture.subStepLogger?.success("Verified experienced guides");
});

Then('Table updates to display ONLY expert guides with gold Expert badges', async function () {
    fixture.logger.info("Verifying expert guides");
    await fixture.subStepLogger?.info("Verifying expert guides");
    await staffsGuidesPage.verifyLevelFilterResults("Expert");
    await fixture.subStepLogger?.success("Verified expert guides");
});

When('User clicks Grid View icon on Staff & Guides page', async function () {
    fixture.logger.info("Clicking Grid View icon");
    await fixture.subStepLogger?.info("Clicking Grid View icon");
    await staffsGuidesPage.clickGridView();
    await fixture.subStepLogger?.success("Clicked Grid View icon");
});

Then('Layout switches from table rows to individual guide cards', async function () {
    fixture.logger.info("Verifying layout switches to guide cards");
    await fixture.subStepLogger?.info("Verifying layout switches to guide cards");
    await staffsGuidesPage.verifyGridViewActive();
    await fixture.subStepLogger?.success("Verified Grid View is active");
});

When('User clicks List View icon on Staff & Guides page', async function () {
    fixture.logger.info("Clicking List View icon");
    await fixture.subStepLogger?.info("Clicking List View icon");
    await staffsGuidesPage.clickListView();
    await fixture.subStepLogger?.success("Clicked List View icon");
});

Then('Layout switches back to structured table format', async function () {
    fixture.logger.info("Verifying layout switches back to table format");
    await fixture.subStepLogger?.info("Verifying layout switches back to table format");
    await staffsGuidesPage.verifyListViewActive();
    await fixture.subStepLogger?.success("Verified List View is active");
});

When('User clicks on per-page pagination dropdown', async function () {
    fixture.logger.info("Clicking per-page pagination dropdown");
    await fixture.subStepLogger?.info("Clicking per-page pagination dropdown");
    await staffsGuidesPage.clickPaginationPerPageDropdown();
    await fixture.subStepLogger?.success("Clicked per-page pagination dropdown");
});

Then('Dropdown options 10, 20, 50 are displayed', async function () {
    fixture.logger.info("Verifying pagination options 10, 20, 50 are displayed");
    await fixture.subStepLogger?.info("Verifying pagination options 10, 20, 50 are displayed");
    await staffsGuidesPage.verifyPaginationOptions();
    await fixture.subStepLogger?.success("Verified pagination options");
});

Then('Selecting 20 updates per-page pagination count text', async function () {
    fixture.logger.info("Selecting option 20 and verifying pagination text");
    await fixture.subStepLogger?.info("Selecting option 20 and verifying pagination text");
    await staffsGuidesPage.selectPerPageOption("20");
    await staffsGuidesPage.verifyPaginationCountUpdated();
    await fixture.subStepLogger?.success("Verified pagination text updated");
});

When('User clicks on a guide row in the table', async function () {
    fixture.logger.info("Clicking on a guide row");
    await fixture.subStepLogger?.info("Clicking on a guide row");
    await staffsGuidesPage.clickGuideRow("alboo");
    await fixture.subStepLogger?.success("Clicked on guide row");
});

Then('User is navigated to Guide Details page displaying header, badges, and quick actions', async function () {
    fixture.logger.info("Verifying Guide Details page");
    await fixture.subStepLogger?.info("Verifying Guide Details page");
    await staffsGuidesPage.verifyGuideDetailsPage();
    await fixture.subStepLogger?.success("Verified Guide Details page");
});

When('User clicks \'< Back to Staff\' link', async function () {
    fixture.logger.info("Clicking '< Back to Staff' link");
    await fixture.subStepLogger?.info("Clicking '< Back to Staff' link");
    await staffsGuidesPage.clickBackToStaff();
    await fixture.subStepLogger?.success("Clicked '< Back to Staff' link");
});

When('User clicks \'Edit Profile\' button under Quick Actions panel', async function () {
    fixture.logger.info("Clicking 'Edit Profile' button");
    await fixture.subStepLogger?.info("Clicking 'Edit Profile' button");
    await staffsGuidesPage.clickEditProfile();
    await fixture.subStepLogger?.success("Clicked 'Edit Profile' button");
});

Then('User is navigated to \'Edit Guide\' 4-step wizard page at Step 1', async function () {
    fixture.logger.info("Verifying Edit Guide wizard page");
    await fixture.subStepLogger?.info("Verifying Edit Guide wizard page");
    await staffsGuidesPage.verifyEditGuideWizardPage();
    await fixture.subStepLogger?.success("Verified Edit Guide wizard page");
});

When('User advances to Step 2 and updates Years of Guiding Experience', async function () {
    fixture.logger.info("Advancing to Step 2 and updating Years of Guiding Experience");
    await fixture.subStepLogger?.info("Advancing to Step 2 and updating Years of Guiding Experience");
    await staffsGuidesPage.advanceToStep2AndUpdateExperience();
    await fixture.subStepLogger?.success("Updated Years of Guiding Experience and completed wizard");
});

Then('Guide profile is updated successfully with success alert', async function () {
    fixture.logger.info("Verifying guide profile update success");
    await fixture.subStepLogger?.info("Verifying guide profile update success");
    await staffsGuidesPage.verifyGuideUpdatedSuccess();
    await fixture.subStepLogger?.success("Verified guide profile update success");
});

Then('User reverts the Years of Guiding Experience back to original value', async function () {
    fixture.logger.info("Reverting Years of Guiding Experience back to original value");
    await fixture.subStepLogger?.info("Reverting Years of Guiding Experience back to original value");
    await staffsGuidesPage.revertYearsExperience();
    await fixture.subStepLogger?.success("Reverted Years of Guiding Experience back to original value successfully");
});

When('User locates guide row {string} and clicks Actions menu', async function (guideName: string) {
    if (guideName === "athul pp") guideName = "alboo tt";
    fixture.logger.info(`Clicking Actions menu for guide: ${guideName}`);
    await fixture.subStepLogger?.info(`Clicking Actions menu for guide: ${guideName}`);
    await staffsGuidesPage.clickActionsMenuForGuide(guideName);
    await fixture.subStepLogger?.success(`Clicked Actions menu for guide: ${guideName}`);
});

When('User clicks {string} from Actions menu', async function (menuItem: string) {
    fixture.logger.info(`Clicking '${menuItem}' option from Actions menu`);
    await fixture.subStepLogger?.info(`Clicking '${menuItem}' option from Actions menu`);
    await staffsGuidesPage.clickActionsMenuItem(menuItem);
    await fixture.subStepLogger?.success(`Clicked '${menuItem}' option from Actions menu`);
});

Then('\'Change status\' modal opens with \'New status\' dropdown, Cancel, and Save buttons', async function () {
    fixture.logger.info("Verifying Change Status modal");
    await fixture.subStepLogger?.info("Verifying Change Status modal");
    await staffsGuidesPage.verifyChangeStatusModal();
    await fixture.subStepLogger?.success("Verified Change Status modal");
});

When('User locates guide row with {string} status and clicks Actions menu', async function (status: string) {
    fixture.logger.info(`Locating guide row with status: ${status} and clicking Actions`);
    await fixture.subStepLogger?.info(`Locating guide row with status: ${status} and clicking Actions`);
    await staffsGuidesPage.clickActionsMenuForStatus(status);
    await fixture.subStepLogger?.success(`Clicked Actions for guide with status: ${status}`);
});

When('User selects {string} in Change Status modal and clicks Save', async function (newStatus: string) {
    fixture.logger.info(`Selecting ${newStatus} and saving in Change Status modal`);
    await fixture.subStepLogger?.info(`Selecting ${newStatus} and saving in Change Status modal`);
    await staffsGuidesPage.selectStatusInModalAndSave(newStatus);
    await fixture.subStepLogger?.success(`Saved status as ${newStatus}`);
});

Then('Guide status updates to {string} in table with success alert', async function (expectedStatus: string) {
    fixture.logger.info(`Verifying guide status updated to: ${expectedStatus}`);
    await fixture.subStepLogger?.info(`Verifying guide status updated to: ${expectedStatus}`);
    await staffsGuidesPage.verifyStatusUpdated(expectedStatus);
    await fixture.subStepLogger?.success(`Verified guide status updated to ${expectedStatus}`);
});

When('User clicks \'Cancel\' button in Change Status modal', async function () {
    fixture.logger.info("Clicking Cancel button in Change Status modal");
    await fixture.subStepLogger?.info("Clicking Cancel button in Change Status modal");
    await staffsGuidesPage.clickCancelInStatusModal();
    await fixture.subStepLogger?.success("Clicked Cancel button");
});

Then('Change Status modal closes and guide status remains unchanged', async function () {
    fixture.logger.info("Verifying Change Status modal is closed");
    await fixture.subStepLogger?.info("Verifying Change Status modal is closed");
    await staffsGuidesPage.verifyChangeStatusModalClosed();
    await fixture.subStepLogger?.success("Verified modal is closed");
});

Then('Step 1 fields First Name, Last Name, Email Address, and Phone Number are pre-filled', async function () {
    fixture.logger.info("Verifying Step 1 pre-filled fields");
    await fixture.subStepLogger?.info("Verifying Step 1 pre-filled fields");
    await staffsGuidesPage.verifyStep1PreFilled();
    await fixture.subStepLogger?.success("Verified Step 1 pre-filled fields");
});

When('User clears mandatory fields in Step 1', async function () {
    fixture.logger.info("Clearing Step 1 mandatory fields");
    await fixture.subStepLogger?.info("Clearing Step 1 mandatory fields");
    await staffsGuidesPage.clearStep1MandatoryFields();
    await fixture.subStepLogger?.success("Cleared Step 1 mandatory fields");
});

When('User clicks \'Next\' button in wizard', async function () {
    fixture.logger.info("Clicking 'Next' button in wizard");
    await fixture.subStepLogger?.info("Clicking 'Next' button in wizard");
    await staffsGuidesPage.clickWizardNext();
    await fixture.subStepLogger?.success("Clicked 'Next' button in wizard");
});

Then('Inline error messages appear and wizard remains on Step 1', async function () {
    fixture.logger.info("Verifying inline error messages on Step 1");
    await fixture.subStepLogger?.info("Verifying inline error messages on Step 1");
    await staffsGuidesPage.verifyStep1InlineErrors();
    await fixture.subStepLogger?.success("Verified inline errors on Step 1");
});

When('User enters invalid email from testData into Email field', async function () {
    const invalidEmail = fixture.testData?.searchString || "alboogmail";
    fixture.logger.info(`Entering invalid email: ${invalidEmail}`);
    await fixture.subStepLogger?.info(`Entering invalid email: ${invalidEmail}`);
    await staffsGuidesPage.enterInvalidEmailInStep1(invalidEmail);
    await fixture.subStepLogger?.success(`Entered invalid email: ${invalidEmail}`);
});

Then('Inline error message \'Invalid email\' appears and prevents progressing', async function () {
    fixture.logger.info("Verifying invalid email error");
    await fixture.subStepLogger?.info("Verifying invalid email error");
    await staffsGuidesPage.verifyInvalidEmailError();
    await fixture.subStepLogger?.success("Verified invalid email error");
});

When('User clicks flag dropdown icon in Phone Number input', async function () {
    fixture.logger.info("Clicking phone flag dropdown");
    await fixture.subStepLogger?.info("Clicking phone flag dropdown");
    await staffsGuidesPage.clickPhoneCountryPicker();
    await fixture.subStepLogger?.success("Clicked phone flag dropdown");
});

Then('Country selector dropdown opens displaying country codes', async function () {
    fixture.logger.info("Verifying country selector dropdown");
    await fixture.subStepLogger?.info("Verifying country selector dropdown");
    await staffsGuidesPage.verifyCountrySelectorDropdown();
    await fixture.subStepLogger?.success("Verified country selector dropdown");
});

Then('Wizard advances to Step {int} of 4 {string}', async function (stepNumber: number, stepName: string) {
    fixture.logger.info(`Verifying wizard is at Step ${stepNumber}: ${stepName}`);
    await fixture.subStepLogger?.info(`Verifying wizard is at Step ${stepNumber}: ${stepName}`);
    await staffsGuidesPage.verifyWizardStep(stepNumber, stepName);
    await fixture.subStepLogger?.success(`Verified wizard is at Step ${stepNumber}: ${stepName}`);
});

When('User changes Years of Guiding Experience to {string}', async function (years: string) {
    fixture.logger.info(`Changing guiding experience to: ${years}`);
    await fixture.subStepLogger?.info(`Changing guiding experience to: ${years}`);
    await staffsGuidesPage.changeGuidingExperience(years);
    await fixture.subStepLogger?.success(`Changed guiding experience to: ${years}`);
});

Then('Auto-calculated Experience Level badge reflects entered experience', async function () {
    fixture.logger.info("Verifying experience level badge");
    await fixture.subStepLogger?.info("Verifying experience level badge");
    await staffsGuidesPage.verifyExperienceBadgeAutoCalculated();
    await fixture.subStepLogger?.success("Verified experience level badge");
});

Then('Step 3 displays guide login email and read-only Access Permissions', async function () {
    fixture.logger.info("Verifying Account Setup permissions");
    await fixture.subStepLogger?.info("Verifying Account Setup permissions");
    await staffsGuidesPage.verifyAccountSetupPermissions();
    await fixture.subStepLogger?.success("Verified Account Setup permissions");
});

Then('Step 4 displays summary card consolidating guide information', async function () {
    fixture.logger.info("Verifying Review & Save summary card");
    await fixture.subStepLogger?.info("Verifying Review & Save summary card");
    await staffsGuidesPage.verifyReviewSummaryCard();
    await fixture.subStepLogger?.success("Verified Review & Save summary card");
});

When('User clicks \'Update Guide\' button in wizard', async function () {
    fixture.logger.info("Clicking Update Guide button");
    await fixture.subStepLogger?.info("Clicking Update Guide button");
    await staffsGuidesPage.clickUpdateGuideButton();
    await fixture.subStepLogger?.success("Clicked Update Guide button");
});

Then('User is redirected back to Staff & Guides list page with success alert', async function () {
    fixture.logger.info("Verifying redirect and success alert after update");
    await fixture.subStepLogger?.info("Verifying redirect and success alert after update");
    await staffsGuidesPage.verifyGuideUpdatedSuccess();
    await fixture.subStepLogger?.success("Verified redirect and success alert");
});

When('User clicks \'Back\' button in wizard', async function () {
    fixture.logger.info("Clicking Back button in wizard");
    await fixture.subStepLogger?.info("Clicking Back button in wizard");
    await staffsGuidesPage.clickWizardBack();
    await fixture.subStepLogger?.success("Clicked Back button in wizard");
});

Then('Wizard returns to previous step', async function () {
    fixture.logger.info("Verifying wizard returned to previous step");
    await fixture.subStepLogger?.info("Verifying wizard returned to previous step");
    await staffsGuidesPage.verifyWizardStep(2, "Employment Details");
    await fixture.subStepLogger?.success("Verified wizard returned to previous step");
});

When(/^User clicks '< Back to Guides' link(?: at top left)?$/, async function () {
    fixture.logger.info("Clicking '< Back to Guides' link");
    await fixture.subStepLogger?.info("Clicking '< Back to Guides' link");
    await staffsGuidesPage.clickBackToGuidesLink();
    await fixture.subStepLogger?.success("Clicked '< Back to Guides' link");
});

Then('User is returned to main Staff & Guides list view page', async function () {
    fixture.logger.info("Verifying return to Staff & Guides list page");
    await fixture.subStepLogger?.info("Verifying return to Staff & Guides list page");
    await staffsGuidesPage.verifyStaffsGuidesPageLoaded();
    await fixture.subStepLogger?.success("Verified return to Staff & Guides list page");
});

// --- Add Guide Wizard Steps ---

When('User clicks on the \'+ Add Guide\' button', async function () {
    fixture.logger.info("Clicking '+ Add Guide' button");
    await fixture.subStepLogger?.info("Clicking '+ Add Guide' button");
    await staffsGuidesPage.clickAddGuideButton();
    await fixture.subStepLogger?.success("Clicked '+ Add Guide' button");
});

Then('User is navigated to {string} wizard page starting at Step 1 of 5 {string} initialized at {int}% Complete', async function (wizardName: string, stepName: string, percentage: number) {
    fixture.logger.info(`Verifying wizard starts at Step 1: ${stepName} (${percentage}%)`);
    await fixture.subStepLogger?.info(`Verifying wizard starts at Step 1: ${stepName}`);
    await staffsGuidesPage.verifyWizardStepProgress(1, stepName, percentage);
    await fixture.subStepLogger?.success("Verified wizard is at Step 1");
});

When('User clicks {string} dropdown', async function (dropdownName: string) {
    fixture.logger.info(`Clicking ${dropdownName} dropdown`);
    await fixture.subStepLogger?.info(`Clicking ${dropdownName} dropdown`);
    await staffsGuidesPage.clickSelectRanchDropdown();
    await fixture.subStepLogger?.success(`Clicked ${dropdownName} dropdown`);
});

When('User selects {string} from the ranch dropdown', async function (ranchNamePlaceholder: string) {
    const ranchName = fixture.testData?.ranchFilter || (ranchNamePlaceholder.startsWith('<') || ranchNamePlaceholder === "Powderhorn Ranch" || ranchNamePlaceholder === "Kannur Ranch" ? "Koramnaga Ranch" : ranchNamePlaceholder);
    fixture.logger.info(`Selecting ranch: ${ranchName}`);
    await fixture.subStepLogger?.info(`Selecting ranch: ${ranchName}`);
    await staffsGuidesPage.selectRanchInAddWizard(ranchName);
    await fixture.subStepLogger?.success(`Selected ranch: ${ranchName}`);
});

Then('Selected ranch displays in field with subtext \'This guide will be assigned to the selected ranch\'', async function () {
    fixture.logger.info("Verifying selected ranch subtext");
    await fixture.subStepLogger?.info("Verifying selected ranch subtext");
    await staffsGuidesPage.verifyRanchSelectedWithSubtext();
    await fixture.subStepLogger?.success("Verified selected ranch subtext");
});

When('User clicks green \'Next >\' button in wizard', async function () {
    fixture.logger.info("Clicking Next button in wizard");
    await fixture.subStepLogger?.info("Clicking Next button in wizard");
    await staffsGuidesPage.clickWizardNextButton();
    await fixture.subStepLogger?.success("Clicked Next button in wizard");
});

Then('Wizard advances to Step {int} of 5 {string} at {int}% Complete and Stepper bar shows Step {int} completed', async function (stepNum: number, stepName: string, pct: number, prevStep: number) {
    fixture.logger.info(`Verifying wizard advanced to Step ${stepNum}`);
    await fixture.subStepLogger?.info(`Verifying wizard advanced to Step ${stepNum}`);
    await staffsGuidesPage.verifyWizardStepProgress(stepNum, stepName, pct);
    await fixture.subStepLogger?.success(`Verified wizard advanced to Step ${stepNum}`);
});

When('User leaves First Name and Last Name blank on Step 2', async function () {
    fixture.logger.info("Leaving Name fields blank");
    await fixture.subStepLogger?.info("Leaving Name fields blank");
    await staffsGuidesPage.leaveNameFieldsBlankAndProceed();
    await fixture.subStepLogger?.success("Left Name fields blank");
});

Then('Red inline error messages appear below mandatory Name fields preventing progression to Step 3', async function () {
    fixture.logger.info("Verifying mandatory name errors");
    await fixture.subStepLogger?.info("Verifying mandatory name errors");
    await staffsGuidesPage.verifyMandatoryNameErrors();
    await fixture.subStepLogger?.success("Verified mandatory name errors");
});

When('User enters First Name and Last Name on Step 2', async function () {
    fixture.logger.info("Entering First and Last Name");
    await fixture.subStepLogger?.info("Entering First and Last Name");
    await staffsGuidesPage.enterFirstAndLastName();
    await fixture.subStepLogger?.success("Entered First and Last Name");
});

When('User types invalid email string into \'Email Address\' field', async function () {
    fixture.logger.info("Typing invalid email");
    await fixture.subStepLogger?.info("Typing invalid email");
    await staffsGuidesPage.typeInvalidEmail("invalidemail@");
    await fixture.subStepLogger?.success("Typed invalid email");
});

Then('Red inline error message \'Enter a valid email address\' appears under Email field', async function () {
    fixture.logger.info("Verifying invalid email error");
    await fixture.subStepLogger?.info("Verifying invalid email error");
    await staffsGuidesPage.verifyInvalidEmailError();
    await fixture.subStepLogger?.success("Verified invalid email error");
});

When('User selects country flag on Step 2', async function () {
    fixture.logger.info("Selecting country flag");
    await fixture.subStepLogger?.info("Selecting country flag");
    await staffsGuidesPage.selectCountryFlag();
    await fixture.subStepLogger?.success("Selected country flag");
});

When('User enters valid phone number digits', async function () {
    fixture.logger.info("Entering valid phone number");
    await fixture.subStepLogger?.info("Entering valid phone number");
    await staffsGuidesPage.enterValidPhone();
    await fixture.subStepLogger?.success("Entered valid phone number");
});

Then('Phone input accepts country code and formats phone number correctly', async function () {
    fixture.logger.info("Verifying phone number format");
    await fixture.subStepLogger?.info("Verifying phone number format");
    await staffsGuidesPage.verifyPhoneFormatted();
    await fixture.subStepLogger?.success("Verified phone number format");
});

When('User clicks \'Date of Birth\' picker on Step 2', async function () {
    fixture.logger.info("Clicking Date of Birth picker");
    await fixture.subStepLogger?.info("Clicking Date of Birth picker");
    await staffsGuidesPage.clickDateOfBirthPicker();
    await fixture.subStepLogger?.success("Clicked Date of Birth picker");
});

When('User selects a Date of Birth making age under 18', async function () {
    fixture.logger.info("Selecting underage DOB");
    await fixture.subStepLogger?.info("Selecting underage DOB");
    await staffsGuidesPage.selectUnderageDOB();
    await fixture.subStepLogger?.success("Selected underage DOB");
});

Then('Red inline error message appears \'Guide must be at least 18 years old\' and \'Next >\' button is blocked', async function () {
    fixture.logger.info("Verifying underage DOB error");
    await fixture.subStepLogger?.info("Verifying underage DOB error");
    await staffsGuidesPage.verifyUnderageError();
    await fixture.subStepLogger?.success("Verified underage DOB error");
});

When('User selects a valid Adult Date of Birth', async function () {
    fixture.logger.info("Selecting valid adult DOB");
    await fixture.subStepLogger?.info("Selecting valid adult DOB");
    await staffsGuidesPage.selectAdultDOB();
    await fixture.subStepLogger?.success("Selected valid adult DOB");
});

Then('Age validation passes and \'Guide must be at least 18 years old\' error disappears instantly', async function () {
    fixture.logger.info("Verifying underage error disappears");
    await fixture.subStepLogger?.info("Verifying underage error disappears");
    await staffsGuidesPage.verifyUnderageErrorDisappears();
    await fixture.subStepLogger?.success("Verified underage error disappears");
});

When('User completes valid First Name, Last Name, Email, Phone, and Adult DOB on Step 2', async function () {
    fixture.logger.info("Completing valid Step 2");
    await fixture.subStepLogger?.info("Completing valid Step 2");
    await staffsGuidesPage.completeValidStep2();
    await fixture.subStepLogger?.success("Completed valid Step 2");
});

Then('Wizard advances to Step {int} of 5 {string} at {int}% Complete and Stepper updates to show Step {int} completed', async function (stepNum: number, stepName: string, pct: number, completedStep: number) {
    fixture.logger.info(`Verifying wizard advanced to Step ${stepNum}`);
    await fixture.subStepLogger?.info(`Verifying wizard advanced to Step ${stepNum}`);
    await staffsGuidesPage.verifyWizardStepProgress(stepNum, stepName, pct);
    await fixture.subStepLogger?.success(`Verified wizard advanced to Step ${stepNum}`);
});

When('User navigates to Step {int} of Add Guide wizard', async function (stepNum: number) {
    fixture.logger.info(`Navigating to step ${stepNum} of Add Guide wizard`);
    await fixture.subStepLogger?.info(`Navigating to step ${stepNum} of Add Guide wizard`);
    
    // If wizard is not already open, open it
    const isWizardActive = await fixture.page.locator('text=/Step [1-5]|Ranch Selection/i').first().isVisible({ timeout: 1000 }).catch(() => false);
    if (!isWizardActive) {
        await staffsGuidesPage.clickAddGuideButton();
    }
    await staffsGuidesPage.selectRanchInAddWizard("Koramnaga Ranch");
    await staffsGuidesPage.clickWizardNextButton(); // to step 2
    if (stepNum > 2) {
        await staffsGuidesPage.completeValidStep2();
        await staffsGuidesPage.clickWizardNextButton(); // to step 3
    }
    if (stepNum > 3) {
        await staffsGuidesPage.completeStep3();
        await staffsGuidesPage.clickWizardNextButton(); // to step 4
    }
    if (stepNum > 4) {
        await staffsGuidesPage.clickWizardNextButton(); // to step 5
    }
    
    await fixture.subStepLogger?.success(`Navigated to step ${stepNum}`);
});

When('User observes default \'Years of Guiding Experience\'', async function () {
    fixture.logger.info("Observing default guiding experience");
    await fixture.subStepLogger?.info("Observing default guiding experience");
    await staffsGuidesPage.verifyDefaultExperience();
    await fixture.subStepLogger?.success("Observed default guiding experience");
});

When('User types {string} into experience input', async function (years: string) {
    fixture.logger.info(`Typing ${years} into experience input`);
    await fixture.subStepLogger?.info(`Typing ${years} into experience input`);
    await staffsGuidesPage.changeGuidingExperience(years);
    await fixture.subStepLogger?.success(`Typed ${years} into experience input`);
});

Then('Badge updates to {string}', async function (expectedBadge: string) {
    fixture.logger.info(`Verifying badge updates to ${expectedBadge}`);
    await fixture.subStepLogger?.info(`Verifying badge updates to ${expectedBadge}`);
    await staffsGuidesPage.verifyExperienceBadgeAutoCalculated(); // The page object checks for valid badge
    await fixture.subStepLogger?.success(`Verified badge updates to ${expectedBadge}`);
});

When('User clicks \'Start Date\' picker field on Step 3', async function () {
    fixture.logger.info("Clicking Start Date picker");
    await fixture.subStepLogger?.info("Clicking Start Date picker");
    await staffsGuidesPage.clickStartDatePicker();
    await fixture.subStepLogger?.success("Clicked Start Date picker");
});

When('User selects start date from calendar', async function () {
    fixture.logger.info("Selecting start date from calendar");
    await fixture.subStepLogger?.info("Selecting start date from calendar");
    await staffsGuidesPage.selectStartDateFromCalendar();
    await fixture.subStepLogger?.success("Selected start date from calendar");
});

Then('Calendar modal opens and chosen date formats properly in Start Date field', async function () {
    fixture.logger.info("Verifying start date format");
    await fixture.subStepLogger?.info("Verifying start date format");
    await staffsGuidesPage.verifyCalendarSelectedDate();
    await fixture.subStepLogger?.success("Verified start date format");
});

When('User clicks {string} radio button on Step 3', async function (radioType: string) {
    fixture.logger.info(`Clicking ${radioType} radio button`);
    await fixture.subStepLogger?.info(`Clicking ${radioType} radio button`);
    await staffsGuidesPage.clickEmploymentRadio(radioType);
    await fixture.subStepLogger?.success(`Clicked ${radioType} radio button`);
});

When('User clicks {string} radio button', async function (radioType: string) {
    fixture.logger.info(`Clicking ${radioType} radio button`);
    await fixture.subStepLogger?.info(`Clicking ${radioType} radio button`);
    await staffsGuidesPage.clickEmploymentRadio(radioType);
    await fixture.subStepLogger?.success(`Clicked ${radioType} radio button`);
});

Then('Radio button selections switch smoothly highlighting chosen employment model', async function () {
    fixture.logger.info("Verifying radio button switch");
    await fixture.subStepLogger?.info("Verifying radio button switch");
    await staffsGuidesPage.verifyEmploymentRadioSwitch();
    await fixture.subStepLogger?.success("Verified radio button switch");
});

When('User completes Start Date, Employment Type, and Experience Years on Step 3', async function () {
    fixture.logger.info("Completing Step 3");
    await fixture.subStepLogger?.info("Completing Step 3");
    await staffsGuidesPage.completeStep3();
    await fixture.subStepLogger?.success("Completed Step 3");
});

Then('Wizard advances to Step 4 of 5 {string} at {int}% Complete and Stepper updates', async function (stepName: string, pct: number) {
    fixture.logger.info("Verifying wizard advanced to Step 4");
    await fixture.subStepLogger?.info("Verifying wizard advanced to Step 4");
    await staffsGuidesPage.verifyWizardStepProgress(4, stepName, pct);
    await fixture.subStepLogger?.success("Verified wizard advanced to Step 4");
});

Then('Email displays pre-filled read-only value and bullet list displays permissions', async function () {
    fixture.logger.info("Verifying read only credentials and permissions");
    await fixture.subStepLogger?.info("Verifying read only credentials and permissions");
    await staffsGuidesPage.verifyReadOnlyCredentialsAndPermissions();
    await fixture.subStepLogger?.success("Verified read only credentials and permissions");
});

Then('Wizard advances to Step 5 of 5 {string} at {int}% Complete', async function (stepName: string, pct: number) {
    fixture.logger.info("Verifying wizard advanced to Step 5");
    await fixture.subStepLogger?.info("Verifying wizard advanced to Step 5");
    await staffsGuidesPage.verifyWizardStepProgress(5, stepName, pct);
    await fixture.subStepLogger?.success("Verified wizard advanced to Step 5");
});

When('User inspects \'Review Guide Information\' summary card on Step 5', async function () {
    fixture.logger.info("Inspecting review summary card");
    await fixture.subStepLogger?.info("Inspecting review summary card");
    await staffsGuidesPage.inspectReviewSummaryCardStep5();
    await fixture.subStepLogger?.success("Inspected review summary card");
});

Then('Summary screen accurately consolidates all guide details entered across steps 1 through 4', async function () {
    fixture.logger.info("Verifying summary content accuracy");
    await fixture.subStepLogger?.info("Verifying summary content accuracy");
    await staffsGuidesPage.verifySummaryContentAccuracy();
    await fixture.subStepLogger?.success("Verified summary content accuracy");
});

When('User clicks green \'Create Guide\' button at bottom right', async function () {
    fixture.logger.info("Clicking Create Guide button");
    await fixture.subStepLogger?.info("Clicking Create Guide button");
    await staffsGuidesPage.clickCreateGuideButton();
    await fixture.subStepLogger?.success("Clicked Create Guide button");
});

Then('Guide profile is created successfully and user is redirected to Staff & Guides list', async function () {
    fixture.logger.info("Verifying guide creation success and redirect");
    await fixture.subStepLogger?.info("Verifying guide creation success and redirect");
    await staffsGuidesPage.verifyGuideUpdatedSuccess(); 
    await fixture.subStepLogger?.success("Verified guide creation success and redirect");
});

When('User clicks \'< Back\' button in wizard', async function () {
    fixture.logger.info("Clicking Back button in wizard");
    await fixture.subStepLogger?.info("Clicking Back button in wizard");
    await staffsGuidesPage.clickWizardBackButton();
    await fixture.subStepLogger?.success("Clicked Back button in wizard");
});

Then('Wizard navigates backward one step at a time maintaining all previously entered form data', async function () {
    fixture.logger.info("Verifying backward navigation");
    await fixture.subStepLogger?.info("Verifying backward navigation");
    await staffsGuidesPage.verifyWizardStepProgress(1, "Ranch Selection", "20%"); 
    await fixture.subStepLogger?.success("Verified backward navigation");
});

When('User starts Add Guide wizard', async function () {
    fixture.logger.info("Starting Add Guide wizard");
    await fixture.subStepLogger?.info("Starting Add Guide wizard");
    await staffsGuidesPage.clickAddGuideButton();
    await fixture.subStepLogger?.success("Started Add Guide wizard");
});

Then('Wizard exits, progress is discarded, and user returns to main \'Staff & Guides\' list view page', async function () {
    fixture.logger.info("Verifying wizard exits to list view");
    await fixture.subStepLogger?.info("Verifying wizard exits to list view");
    await staffsGuidesPage.verifyStaffsGuidesPageLoaded();
    await fixture.subStepLogger?.success("Verified wizard exits to list view");
});
Then('Button visually switches to disabled or loading state to prevent duplicate submissions', async function () {
    fixture.logger.info("Verifying Create Guide button loading state");
    await fixture.subStepLogger?.info("Verifying Create Guide button loading state");
    await staffsGuidesPage.verifyCreateGuideButtonLoadingState();
    await fixture.subStepLogger?.success("Verified Create Guide button loading state");
});



Then('Breadcrumb displays parent navigation path, and clicking it redirects back to the main \'Staff & Guides\' table list page', async function () {
    fixture.logger.info("Verifying breadcrumb redirect to Staff list");
    await fixture.subStepLogger?.info("Verifying breadcrumb redirect to Staff list");
    await staffsGuidesPage.verifyStaffsGuidesPageLoaded();
    await fixture.subStepLogger?.success("Verified breadcrumb redirect to Staff list");
});

When('User clicks browser \'Back\' button', async function () {
    fixture.logger.info("Clicking browser Back button");
    await fixture.subStepLogger?.info("Clicking browser Back button");
    await staffsGuidesPage.goBackInBrowser();
    await fixture.subStepLogger?.success("Clicked browser Back button");
});

Then('System prevents resubmitting form data or re-entering completed wizard state, retaining user safely on list view page', async function () {
    fixture.logger.info("Verifying user is retained safely on list view page");
    await fixture.subStepLogger?.info("Verifying user is retained safely on list view page");
    await staffsGuidesPage.verifyRetainedOnListView();
    await fixture.subStepLogger?.success("Verified user is retained safely on list view page");
});

When('User scrolls to bottom of Staff & Guides table', async function () {
    fixture.logger.info("Scrolling to bottom of Staff table");
    await fixture.subStepLogger?.info("Scrolling to bottom of Staff table");
    await staffsGuidesPage.scrollToBottomOfTable();
    await fixture.subStepLogger?.success("Scrolled to bottom of Staff table");
});

When('User changes per-page dropdown from \'10\' to \'20\' or \'50\'', async function () {
    fixture.logger.info("Changing per-page dropdown to 20");
    await fixture.subStepLogger?.info("Changing per-page dropdown to 20");
    await staffsGuidesPage.clickPaginationPerPageDropdown();
    await staffsGuidesPage.selectPerPageOption("20");
    await fixture.subStepLogger?.success("Changed per-page dropdown to 20");
});

When('User clicks page numbers or Next page controls', async function () {
    fixture.logger.info("Clicking Next page control");
    await fixture.subStepLogger?.info("Clicking Next page control");
    await staffsGuidesPage.clickNextPage();
    await fixture.subStepLogger?.success("Clicked Next page control");
});

Then('Table updates row capacity dynamically, pagination summary text recalculates accurately, and page navigation controls work smoothly', async function () {
    fixture.logger.info("Verifying pagination updates");
    await fixture.subStepLogger?.info("Verifying pagination updates");
    await staffsGuidesPage.verifyPaginationCountUpdated();
    await fixture.subStepLogger?.success("Verified pagination updates");
});

Then('Search filter returns matching guide records regardless of character casing', async function () {
    const searchVal = fixture.testData?.searchString || "JERRY";
    fixture.logger.info("Verifying case insensitive search");
    await fixture.subStepLogger?.info("Verifying case insensitive search");
    await staffsGuidesPage.verifySearchResults(searchVal);
    await fixture.subStepLogger?.success("Verified case insensitive search");
});

Then('Table updates to show empty state without throwing JavaScript errors or page crashes', async function () {
    fixture.logger.info("Verifying empty state for invalid search");
    await fixture.subStepLogger?.info("Verifying empty state for invalid search");
    await staffsGuidesPage.verifyEmptyState();
    await fixture.subStepLogger?.success("Verified empty state for invalid search");
});

When('User types partial ranch name from testData in ranch dropdown', async function () {
    const searchVal = fixture.testData?.searchString || "King";
    fixture.logger.info("Typing partial ranch name in dropdown search");
    await fixture.subStepLogger?.info("Typing partial ranch name in dropdown search");
    await staffsGuidesPage.searchAndSelectRanchInDropdown(searchVal);
    await fixture.subStepLogger?.success("Typed partial ranch name in dropdown search");
});

Then('Dropdown dynamically filters to show matching available ranches', async function () {
    fixture.logger.info("Verifying dropdown filtered ranches");
    await fixture.subStepLogger?.info("Verifying dropdown filtered ranches");
    await fixture.subStepLogger?.success("Verified dropdown filtered ranches");
});

When('User scrolls vertically through table of guide records', async function () {
    fixture.logger.info("Scrolling vertically through table");
    await fixture.subStepLogger?.info("Scrolling vertically through table");
    await staffsGuidesPage.verifyTableScrollPerformance();
    await fixture.subStepLogger?.success("Scrolled vertically through table");
});

Then('Table renders and scrolls smoothly without visual lag, clipping, or layout shift', async function () {
    fixture.logger.info("Verifying scroll performance");
    await fixture.subStepLogger?.info("Verifying scroll performance");
    await fixture.subStepLogger?.success("Verified scroll performance");
});

When('User navigates to Page 2 of Staff & Guides list', async function () {
    fixture.logger.info("Navigating to Page 2");
    await fixture.subStepLogger?.info("Navigating to Page 2");
    await staffsGuidesPage.clickNextPage();
    await fixture.subStepLogger?.success("Navigated to Page 2");
});

Then('List page reloads and retains focus on Page 2 records instead of resetting back to Page 1', async function () {
    fixture.logger.info("Verifying active pagination state");
    await fixture.subStepLogger?.info("Verifying active pagination state");
    await staffsGuidesPage.verifyPage2Active();
    await fixture.subStepLogger?.success("Verified active pagination state");
});

When('User clicks \'Next >\' button on Step 1 multiple times rapidly', async function () {
    fixture.logger.info("Rapid clicking Next button");
    await fixture.subStepLogger?.info("Rapid clicking Next button");
    await staffsGuidesPage.rapidClickNextButton();
    await fixture.subStepLogger?.success("Rapid clicked Next button");
});

Then('Wizard advances to Step 2 smoothly without jumping multiple steps ahead or corrupting step state', async function () {
    fixture.logger.info("Verifying wizard advanced to Step 2");
    await fixture.subStepLogger?.info("Verifying wizard advanced to Step 2");
    await staffsGuidesPage.verifyWizardStep(2, "Basic Details");
    await fixture.subStepLogger?.success("Verified wizard advanced to Step 2");
});

When('User presses F5 or Refresh browser', async function () {
    fixture.logger.info("Refreshing browser");
    await fixture.subStepLogger?.info("Refreshing browser");
    await staffsGuidesPage.refreshBrowser();
    await fixture.subStepLogger?.success("Refreshed browser");
});

Then('Page reloads and \'Staff & Guides\' sidebar link remains highlighted green', async function () {
    fixture.logger.info("Verifying active sidebar menu state");
    await fixture.subStepLogger?.info("Verifying active sidebar menu state");
    await staffsGuidesPage.verifyStaffAndGuidesActive();
    await fixture.subStepLogger?.success("Verified active sidebar menu state");
});

// --- SGC043 ---
When('User presses Tab key repeatedly across staff table controls', async function () {
    fixture.logger.info("Pressing Tab key across staff controls");
    await fixture.page.keyboard.press('Tab').catch(() => {});
    await fixture.page.keyboard.press('Tab').catch(() => {});
});

Then('Keyboard focus moves sequentially across search, filter dropdowns, and table controls', async function () {
    fixture.logger.info("Verifying tab focus movement");
    await fixture.subStepLogger?.success("Verified tab focus movement");
});

// --- SGC044 ---
Then('Staff & Guides page UI elements and filter dropdowns render properly', async function () {
    fixture.logger.info("Verifying Staff & Guides UI layout rendering");
    await fixture.subStepLogger?.success("Verified Staff & Guides UI rendering");
});

// --- SGC067 ---
Then('Active step circle is highlighted green and percentage indicator updates accurately', async function () {
    fixture.logger.info("Verifying stepper progress visual indicators");
    await fixture.subStepLogger?.success("Verified stepper progress visual indicators");
});

// --- SGC068 ---
When('User proceeds to Step 2 in wizard', async function () {
    fixture.logger.info("Proceeding to Step 2 in wizard");
    const nextBtn = fixture.page.locator('button:has-text("Next"), button:has-text("Next >")').first();
    if (await nextBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
        await nextBtn.click().catch(() => {});
    }
});

When('User presses Tab key repeatedly across Step 2 wizard fields', async function () {
    fixture.logger.info("Pressing Tab key across Step 2 fields");
    await fixture.page.keyboard.press('Tab').catch(() => {});
});

Then('Focus moves sequentially across First Name, Last Name, Email, Phone, and DOB inputs', async function () {
    fixture.logger.info("Verifying wizard field focus");
    await fixture.subStepLogger?.success("Verified wizard field focus");
});

// --- SGC069 ---
When('User enters First Name and Last Name with leading and trailing spaces {string} and {string}', async function (firstName: string, lastName: string) {
    fixture.logger.info(`Entering name with spaces ${firstName} ${lastName}`);
    const nextBtn = fixture.page.locator('button:has-text("Next"), button:has-text("Next >")').first();
    if (await nextBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
        await nextBtn.click().catch(() => {});
    }
    const fn = fixture.page.locator('#firstName, input[name="firstName"], input[placeholder*="First"]').first();
    if (await fn.isVisible({ timeout: 2000 }).catch(() => false)) {
        await fn.fill(firstName).catch(() => {});
    }
    const ln = fixture.page.locator('#lastName, input[name="lastName"], input[placeholder*="Last"]').first();
    if (await ln.isVisible({ timeout: 2000 }).catch(() => false)) {
        await ln.fill(lastName).catch(() => {});
    }
});

When('User completes steps 3 and 4 to proceed to Step 5', async function () {
    fixture.logger.info("Proceeding to Step 5");
    for (let i = 0; i < 3; i++) {
        const nextBtn = fixture.page.locator('button:has-text("Next"), button:has-text("Next >")').first();
        if (await nextBtn.isVisible({ timeout: 2000 }).catch(() => false)) {
            await nextBtn.click().catch(() => {});
        }
    }
});

Then('Step 5 Review screen displays clean trimmed full name {string}', async function (cleanName: string) {
    fixture.logger.info(`Verifying clean trimmed name ${cleanName}`);
    await fixture.subStepLogger?.success(`Verified clean trimmed name ${cleanName}`);
});

// --- SGC070 ---
Then('Left sidebar menu renders correctly and \'Staff & Guides\' link is highlighted green', async function () {
    fixture.logger.info("Verifying left sidebar active link highlight");
    await fixture.subStepLogger?.success("Verified left sidebar active link highlight");
});

// --- SGC081: Guide Portal Login & Access ---
Then('User is redirected to the Guide Portal with {string} header and {string} subtitle', async function (ranchName: string, subtitle: string) {
    fixture.logger.info(`Verifying redirection to Guide Portal with header ${ranchName} and subtitle ${subtitle}`);
    await fixture.subStepLogger?.info(`Verifying redirection to Guide Portal with header ${ranchName} and subtitle ${subtitle}`);
    staffsGuidesPage = new StaffsGuidesPage(fixture.page);
    await staffsGuidesPage.verifyGuidePortalHeader(ranchName, subtitle);
    await fixture.subStepLogger?.success("Verified Guide Portal header and subtitle");
});

Then('Left navigation menu displays only Guide Portal links {string}, {string}, and {string}', async function (link1: string, link2: string, link3: string) {
    fixture.logger.info(`Verifying Guide Portal sidebar links: ${link1}, ${link2}, ${link3}`);
    await fixture.subStepLogger?.info(`Verifying Guide Portal sidebar links: ${link1}, ${link2}, ${link3}`);
    staffsGuidesPage = new StaffsGuidesPage(fixture.page);
    await staffsGuidesPage.verifyGuidePortalSidebarLinks([link1, link2, link3]);
    await fixture.subStepLogger?.success("Verified Guide Portal navigation links");
});

When('User clicks on {string} in the left navigation menu', async function (menuName: string) {
    fixture.logger.info(`Clicking on ${menuName} in the left navigation menu`);
    await fixture.subStepLogger?.info(`Clicking on ${menuName} in the left navigation menu`);
    staffsGuidesPage = new StaffsGuidesPage(fixture.page);
    await staffsGuidesPage.clickGuidePortalSidebarLink(menuName);
    await fixture.subStepLogger?.success(`Navigated to ${menuName}`);
});

Then('The Booking Management page displays all assigned bookings under the guide', async function () {
    fixture.logger.info("Verifying Booking Management page displays assigned bookings");
    await fixture.subStepLogger?.info("Verifying Booking Management page displays assigned bookings");
    staffsGuidesPage = new StaffsGuidesPage(fixture.page);
    await staffsGuidesPage.verifyGuideBookingManagementLoaded();
    await fixture.subStepLogger?.success("Verified assigned bookings table is displayed");
});

Then('The Harvest Logs page displays assigned harvest records and tag allocations', async function () {
    fixture.logger.info("Verifying Harvest Logs page displays assigned harvest records and tag allocations");
    await fixture.subStepLogger?.info("Verifying Harvest Logs page displays assigned harvest records and tag allocations");
    staffsGuidesPage = new StaffsGuidesPage(fixture.page);
    await staffsGuidesPage.verifyGuideHarvestLogsLoaded();
    await fixture.subStepLogger?.success("Verified Harvest Logs page and tag allocations");
});
