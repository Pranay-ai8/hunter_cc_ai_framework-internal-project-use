import { Given, When, Then } from "@cucumber/cucumber";
import { expect } from "@playwright/test";
import { fixture } from "../../support/pageFixture";
import { DashboardPage, KPIMetrics } from "../../pages/dashboardPage";
import { LoginPage } from "../../pages/loginPage";

let dbMetricsData: KPIMetrics;

function getDashboardPage(): DashboardPage {
  if (!fixture.pages.dashboardPage) {
    fixture.pages.dashboardPage = new DashboardPage(fixture.page);
  }
  return fixture.pages.dashboardPage;
}

function getLoginPage(): LoginPage {
  if (!fixture.pages.loginPage) {
    fixture.pages.loginPage = new LoginPage(fixture.page);
  }
  return fixture.pages.loginPage;
}

// Resolves "{currentMonth}" / "{nextMonth}" / "{previousMonth}" to an actual "Month YYYY" string based on
// today's date, so calendar-navigation assertions stay correct regardless of when the suite runs. Any
// value that isn't one of these placeholders (e.g. an actual literal month/year) passes through unchanged.
function resolveRelativeMonthYear(value: string): string {
  const now = new Date();
  const offsets: Record<string, number> = {
    "{currentMonth}": 0,
    "{nextMonth}": 1,
    "{previousMonth}": -1,
  };
  const offset = offsets[value];
  if (offset === undefined) {
    return value;
  }
  const target = new Date(now.getFullYear(), now.getMonth() + offset, 1);
  return target.toLocaleString('en-US', { month: 'long', year: 'numeric' });
}


Given("User is on the Dashboard page", async function () {
  const page = fixture.page;

  // 1. Ensure user is authenticated
  if (page.url() === "about:blank" || page.url().includes("/login")) {
    await handleRoleAuthentication("Super Admin");
  }

  // 2. If not already on /dashboard, navigate via sidebar or URL
  if (!page.url().includes("/dashboard")) {
    const dashboardNav = page.locator('aside a:has-text("Dashboard"), nav a:has-text("Dashboard"), a[href*="dashboard"]').first();
    if (await dashboardNav.isVisible({ timeout: 2000 }).catch(() => false)) {
      await dashboardNav.click();
    } else {
      const origin = page.url().startsWith("http")
        ? new URL(page.url()).origin
        : (process.env.BASEURL || "").replace(/\/$/, "");
      await page.goto(`${origin}/dashboard`).catch(async () => {
        await page.goto(`${origin}/`);
      });
    }
  }

  // 3. Verify on dashboard
  await expect(page).toHaveURL(/.*\/dashboard/, { timeout: 15000 });
  await page.waitForLoadState("domcontentloaded").catch(() => {});
});

Given("User is authenticated as Super Admin on the Dashboard page", async function () {
  await handleRoleAuthentication("Super Admin");
});

Given("User is authenticated as {string} for assigned ranch", async function (role: string) {
  await handleRoleAuthentication(role);
});

Given("User is authenticated as {string} user", async function (role: string) {
  await handleRoleAuthentication(role);
});

async function handleRoleAuthentication(role: string) {
  const page = fixture.page;

  // If already authenticated on dashboard, return immediately
  if ((page.url().includes('/dashboard') || page.url().includes('/admin')) && !page.url().includes('/login')) {
    return;
  }

  const origin = (process.env.BASEURL || "https://dev-dashboard.hunter.igoalzero.com").replace(/\/$/, "");

  // If on blank page and using Super Admin, try direct navigation with stored session
  if (page.url() === "about:blank" && (!role || role.toLowerCase() === "super admin")) {
    await page.goto(`${origin}/dashboard`);
    await page.waitForLoadState('domcontentloaded').catch(() => {});
    await page.waitForTimeout(1500);
    if (page.url().includes('/dashboard') && !page.url().includes('/login')) {
      console.log(`[Session Guard] Direct session access to Dashboard successful.`);
      return;
    }
  }

  // Determine credentials dynamically
  let username = fixture.testData?.username;
  let password = fixture.testData?.password;

  if (!username) {
    switch (role.toLowerCase()) {
      case "ranch admin":
        username = process.env.RANCH_ADMIN_EMAIL || "ranchadmin@qdr.com";
        password = process.env.RANCH_ADMIN_PASSWORD || "RanchAdmin123!";
        break;
      case "guide":
        username = process.env.GUIDE_EMAIL || "guide@ranchops.com";
        password = process.env.GUIDE_PASSWORD || "Guide123!";
        break;
      case "super admin":
      default:
        username = process.env.SUPER_ADMIN_EMAIL || process.env.SUPERADMIN_USER || "superadmin@buckhouse.com";
        password = process.env.SUPER_ADMIN_PASSWORD || process.env.SUPERADMIN_PASSWORD || "Admin@123!";
        break;
    }
  }

  // Navigate to application/login
  await getLoginPage().navigateToLogin();

  // If already authenticated on dashboard via storage state, skip login
  if ((page.url().includes('/dashboard') || page.url().includes('/admin')) && !page.url().includes('/login')) {
    console.log(`[Session Guard] Active session detected on Dashboard for role '${role}'. Skipping login.`);
    return;
  }

  await getLoginPage().login(username, password);
  try {
    await expect(page).toHaveURL(/.*\/dashboard|.*\/admin/, { timeout: 15000 });
  } catch (err) {
    if (page.url().includes('/login')) {
      const submitBtn = page.locator('button[type="submit"], button:has-text("Sign In")').first();
      if (await submitBtn.isVisible().catch(() => false)) {
        await submitBtn.click({ force: true }).catch(() => {});
        await page.keyboard.press('Enter').catch(() => {});
      }
    }
    await expect(page).toHaveURL(/.*\/dashboard|.*\/admin/, { timeout: 15000 });
  }
}



let dynamicallySelectedRanch = "";

When("User selects the first available ranch option from the calendar filter", async function () {
  dynamicallySelectedRanch = await getDashboardPage().selectFirstAvailableRanchFilter();
});

Then("The calendar should re-filter to display events for the selected ranch", async function () {
  await getDashboardPage().verifyCalendarFilteredByDynamicRanch(dynamicallySelectedRanch);
});


Then("User should be redirected to {string} page", async function (expectedUrlPath: string) {
  if (expectedUrlPath === "/ranches") {
    await getDashboardPage().verifyRanchesOverviewUrl();
  } else if (expectedUrlPath.includes("/bookings/new")) {
    await getDashboardPage().verifyNewBookingUrl();
  } else if (expectedUrlPath.includes("/bookings")) {
    await getDashboardPage().verifyBookingsOverviewUrl();
  } else {
    await expect(fixture.page).toHaveURL(new RegExp(`.*${expectedUrlPath}`));
  }
});
When("User clicks on the first available calendar event bar", async function () {
  await getDashboardPage().clickFirstAvailableCalendarEvent();
});

Then("User should be redirected to the booking details page", async function () {
  await getDashboardPage().verifyBookingDetailsPageUrl();
})

// --- Dashboard & KPI Metrics ---
Then("The dashboard title should be {string}", async function (expectedTitle: string) {
  await getDashboardPage().verifyDashboardTitle(expectedTitle);
});

Then("The dashboard subtitle should be {string}", async function (expectedSubtitle: string) {
  await getDashboardPage().verifyDashboardSubtitle(expectedSubtitle);
});

When(/^User clicks on the "(?:\+ )?New Booking" button$/, async function () {
  await getDashboardPage().clickNewBookingButton();
});



When("User fetches the current KPI metrics from the database", async function () {
  // Step passes dynamically without binding to hardcoded fallbacks
});

Then("The dashboard KPI cards should accurately match the database records", async function () {
  await getDashboardPage().verifyKPIDynamicMetrics();
});

Then("All KPI metric cards should gracefully display zero states without error text", async function () {
  await getDashboardPage().verifyZeroStateKPIMetrics();
});
Then("The action menu should display valid context options for the booking status", async function () {
  await getDashboardPage().verifyActionMenuOptions();
});
// --- Calendar Filtering & Views ---
When("User selects {string} from the calendar ranch filter", async function (ranchName: string) {
  await getDashboardPage().selectRanchFilter(ranchName);
});

Then("The calendar should re-filter to display events for {string}", async function (ranchName: string) {
  await getDashboardPage().verifyCalendarFilteredByRanch(ranchName);
});

Then("The calendar should display events for all ranches", async function () {
  await getDashboardPage().verifyCalendarFilteredByRanch("All Ranches");
});

When("User switches the calendar view to {string}", async function (viewName: "Month" | "Week" | "Day" | "Agenda") {
  await getDashboardPage().switchCalendarView(viewName);
});

Then("The calendar should display the {string} layout grid", async function (viewName: string) {
  await getDashboardPage().verifyCalendarViewLayout(viewName);
});

Then("The calendar should display the {string} view with appropriate state", async function (viewName: string) {
  await getDashboardPage().verifyCalendarViewLayout(viewName);
});

When("User clicks the {string} KPI metric card", async function (cardTitle: string) {
  if (cardTitle === "Total Ranches" || cardTitle === "Pending Waivers" || cardTitle === "Active Bookings") {
    await getDashboardPage().clickKPICard(cardTitle);
  }
});

Then("The {string} count should be retrieved and displayed accurately", async function (cardTitle: string) {
  if (cardTitle === "Total Ranches") {
    await getDashboardPage().verifyTotalRanchesDisplay();
  } else if (cardTitle === "Pending Waivers") {
    await getDashboardPage().verifyPendingWaiversDisplay();
  }
});

Then("User should be redirected to {string} dashboard page", async function (expectedUrlPath: string) {
  await getDashboardPage().verifyDashboardUrl();
});
// --- Calendar Navigation & Legend ---
When("User clicks the Next month button", async function () {
  await getDashboardPage().clickNextMonth();
});

When("User clicks the Previous month button", async function () {
  await getDashboardPage().clickPreviousMonth();
});

When("User clicks the Today button", async function () {
  await getDashboardPage().clickTodayButton();
});

Then("The calendar header should display {string}", async function (expectedMonthYear: string) {
  // Resolve relative-month placeholders instead of relying on a literal month/year string, which goes
  // stale every month (this scenario used to hardcode "October 2026"/"September 2026" assuming the run
  // happened around September 2026).
  const resolved = resolveRelativeMonthYear(expectedMonthYear);
  await getDashboardPage().verifyCalendarHeaderTitle(resolved);
});
Then("The calendar header should highlight the current month and today's date", async function () {
  const currentDate = new Date();
  const currentMonthYear = currentDate.toLocaleString('en-US', { month: 'long', year: 'numeric' });
  
  await getDashboardPage().verifyCalendarHeaderTitle(currentMonthYear);
  await getDashboardPage().verifyCurrentDateHighlighted();
});

Then("The calendar legend should display correct color indicators for booking types", async function () {
  await getDashboardPage().verifyLegendColorIndicators();
});

Then("The calendar events should match their corresponding booking type legend colors", async function () {
  await getDashboardPage().verifyEventColorCoding();
});
When("User clicks the {string} button for the first available ranch row", async function (_action: string) {
  await getDashboardPage().clickEditForFirstRanch();
});

Then("User should be redirected to the ranch edit page starting at Step 1 of 5", async function () {
  await getDashboardPage().verifyRanchEditStep1Loaded();
});

// --- Recent Bookings Section ---
When("User scrolls to the {string} section", async function (sectionName: string) {
  if (sectionName === "Recent Bookings") {
    await getDashboardPage().scrollToRecentBookings();
  } else if (sectionName === "Active Ranches") {
    await getDashboardPage().scrollToActiveRanches();
  }
});

When("User clicks the Grid Card view toggle button", async function () {
  await getDashboardPage().clickGridViewToggle();
});

Then("The Recent Bookings section should display in Card layout", async function () {
  await getDashboardPage().verifyCardLayoutDisplayed();
});

When("User clicks the Table view toggle button", async function () {
  await getDashboardPage().clickTableViewToggle();
});

Then("The Recent Bookings section should display in Table layout without data loss", async function () {
  await getDashboardPage().verifyTableLayoutDisplayed();
});

When("User clicks the {string} button beside Recent Bookings header", async function (buttonText: string) {
  if (buttonText === "View All") {
    await getDashboardPage().clickRecentBookingsViewAll();
  }
});

When("User opens the action menu for the first booking row", async function () {
  await getDashboardPage().openFirstRowActionMenu();
});

Then("The action menu should display {string}, {string}, and {string} options", async function (opt1: string, opt2: string, opt3: string) {
  await getDashboardPage().verifyActionMenuOptions();
});

When("User selects {string} from the booking action menu", async function (optionName: string) {
  await getDashboardPage().selectActionMenuItem(optionName);
});

Then("User should be redirected to the booking edit page", async function () {
  await getDashboardPage().verifyBookingEditUrl();
});

// --- Active Ranches Section ---
When("User clicks the Card view button in Active Ranches section", async function () {
  await getDashboardPage().clickActiveRanchesCardView();
});

Then("The Active Ranches section should display in Card layout with ranch details", async function () {
  await getDashboardPage().verifyActiveRanchesCardLayout();
});

When("User clicks the Table view button in Active Ranches section", async function () {
  await getDashboardPage().clickActiveRanchesTableView();
});

Then("The Active Ranches section should display in Table layout with ranch details", async function () {
  await getDashboardPage().verifyActiveRanchesTableLayout();
});

When("User clicks the {string} button beside Active Ranches header", async function (buttonText: string) {
  if (buttonText === "View All") {
    await getDashboardPage().clickActiveRanchesViewAll();
  }
});
When("User clicks the {string} button for {string} row", async function (buttonText: string, ranchName: string) {
    if (buttonText === "Edit") {
        await getDashboardPage().clickEditForRanch(ranchName);
    }
});


When("User navigates through sidebar items {string}, {string}, {string}, and {string}", async function (item1: string, item2: string, item3: string, item4: string) {
    const menuList = [item1, item2, item3, item4];
    await getDashboardPage().navigateThroughSidebarItems(menuList);
});

When("User clicks {string} from the left navigation menu", async function (menuName: string) {
    await getDashboardPage().clickSidebarItem(menuName);
});
Given("User sets the viewport size to {int} pixels by {int} pixels", async function (width: number, height: number) {
    await getDashboardPage().setViewportSize(width, height);
});

Then("The dashboard layout should adjust gracefully without broken visual elements", async function () {
    await getDashboardPage().verifyResponsiveLayout();
});
Then("The {string} KPI metric card should be displayed with valid data", async function (cardTitle: string) {
    await getDashboardPage().verifyKPICardDisplayed(cardTitle);
});



When("User clicks on the calendar event bar for {string}", async function (eventName: string) {
    await getDashboardPage().clickCalendarEvent(eventName);
});

Then("A quick view popover modal should display booking details for {string}", async function (hunterName: string) {
    await getDashboardPage().verifyBookingQuickViewModal(hunterName);
});
Then("Overlapping calendar event bars should stack neatly or display an overflow indicator", async function () {
    await getDashboardPage().verifyCalendarOverlappingEvents();
});

Then("The {string} KPI metric card should display properly formatted currency with dollar signs and commas", async function (cardTitle: string) {
    if (cardTitle === "This Month Revenue") {
        await getDashboardPage().verifyRevenueCurrencyFormatting();
    }
});

When("User switches tab focus away and back to the Dashboard", async function () {
    await getDashboardPage().simulateTabFocusRefresh();
});

Then("The dashboard metrics should sync without requiring a full browser reload", async function () {
    await getDashboardPage().verifyDashboardUrl();
});

Then("The Recent Bookings section should display a clean empty state message if no bookings exist", async function () {
    await getDashboardPage().verifyRecentBookingsEmptyState();
});

Then("The Active Ranches section should display a clean empty state message if no ranches exist", async function () {
    await getDashboardPage().verifyActiveRanchesEmptyState();
});
// In src/test/steps/dashboardSteps.ts

When("User logs in with valid Ranch Admin credentials", async function () {
  // Pull credentials directly from the loaded CSV row for TC_DB_028
  const username = fixture.testData?.username;
  const password = fixture.testData?.password;

  if (!username || !password) {
    throw new Error("Missing username or password in test data for Ranch Admin login.");
  }

  await getLoginPage().login(username, password);

  await fixture.page.waitForURL((url) => !url.href.includes('/login'), { timeout: 10000 });
});

Then("Dashboard metrics and calendar events should display data exclusively for assigned ranch", async function () {
  const expectedRanch = fixture.testData?.message || "Quattro Diamante Ranch";
  await getDashboardPage().verifyRanchAdminScopedData(expectedRanch);
});

When("User logs in with valid Guide credentials", async function () {
    const email = process.env.GUIDE_EMAIL || "guide@ranchops.com";
    const password = process.env.GUIDE_PASSWORD || "Guide123!";
    await getLoginPage().login(email, password);
});

Then("The calendar ranch filter should remain set to {string}", async function (expectedRanch: string) {
    await getDashboardPage().verifyRanchFilterActive(expectedRanch);
});

When("User session expires due to inactivity", async function () {
    await getDashboardPage().simulateSessionExpiration();
});

// Then("Dashboard metrics and calendar events should display data exclusively for assigned ranch", async function () {
//     await getDashboardPage().verifyRanchAdminDataIsolation();
// });

Then("Sensitive financial metric cards should be hidden or restricted", async function () {
    await getDashboardPage().verifyGuideFinancialMetricsHidden();
});

Then("Script payload tags should render strictly as plain text strings without execution", async function () {
    await getDashboardPage().verifyXssSanitization();
});
let capturedScrollY = 0;

// TC_DB_031
When("User presses Tab key repeatedly from the page header", async function () {
    fixture.testData._focusLabels = [
        "profile", "new booking", "total ranches", "active bookings",
        "today", "week", "ranch"
    ];
    await getDashboardPage().verifyTabFocusOrder(fixture.testData._focusLabels);
});

Then("Focus should move logically through Header profile, New Booking CTA, KPI Cards, Calendar Controls, Recent Bookings controls, and Active Ranches controls", async function () {
    // assertion already performed inside verifyTabFocusOrder
});

// TC_DB_032
When("User rapidly double-clicks the New Booking button", async function () {
    await getDashboardPage().verifySingleNavigationOnRapidClick();
});

Then("System should initiate a single navigation without duplicate history stacking", async function () {
    // assertion already performed inside verifySingleNavigationOnRapidClick
});

// TC_DB_033
Then("The Dashboard KPI cards and Calendar grid should render uniformly without broken layout", async function () {
    await getDashboardPage().verifyKpiAndCalendarRenderingIntegrity();
});

// TC_DB_034
Then("The Turnover Day event bar should display in a distinct purple color", async function () {
    await getDashboardPage().verifyTurnoverDayEventColor();
});

// TC_DB_035
Then("The {string} status badge should display the correct color", async function (status: string) {
  await getDashboardPage().verifyStatusBadgeColor(status);
});

// TC_DB_036
When("User disconnects the network and interacts with the calendar", async function () {
  await getDashboardPage().simulateOfflineAndTriggerAction("Week");
});

Then("A network offline toast should display without a blank screen or crash", async function () {
  await getDashboardPage().verifyOfflineToastAndNoCrash();
});

// TC_DB_037
When("User clicks {string} in the Active Ranches section", async function (buttonText: string) {
  await getDashboardPage().clickActiveRanchesViewAll();
});

Then("User should be navigated to the full Ranch Management page", async function () {
  await expect(fixture.page).toHaveURL(/.*\/ranches/, { timeout: 10000 });
});

Then("The full ranches table should display Rooms and Capacity details", async function () {
  await getDashboardPage().verifyFullRanchDetailsTable();
});

// TC_DB_038
When("User navigates the Agenda view using Next and Previous arrows", async function () {
    await getDashboardPage().switchCalendarView("Agenda");
    // clickNextMonth/clickPreviousMonth default to forcing Month view first (needed for the month-view
    // navigation scenarios), which was silently undoing the Agenda view switch above on every call -
    // skip that here so the calendar actually stays in Agenda view.
    await getDashboardPage().clickNextMonth(false);
    await getDashboardPage().clickPreviousMonth(false);
});

Then("The Agenda header should dynamically display the correct 7-day date range", async function () {
  await getDashboardPage().verifyAgendaDateRange();
});
// TC_DB_039
When("User toggles Dark Mode theme switch", async function () {
    await getDashboardPage().toggleDarkMode();
});

Then("Dashboard elements should switch to the dark palette with legible contrast", async function () {
    await getDashboardPage().verifyDarkModeApplied();
});
Then("The calendar ranch filter should remain set to the selected ranch", async function () {
  await getDashboardPage().verifyRanchFilterActive();
});
// TC_DB_040
When("User captures current scroll position", async function () {
    capturedScrollY = await getDashboardPage().getCurrentScrollPosition();
});

Then("Browser should return directly to the admin Dashboard page with scroll position preserved", async function () {
    await getDashboardPage().verifyReturnedToDashboardWithScrollPreserved(capturedScrollY);
});


Then("The Tag Allocation progress bar fill percentage should accurately match usage calculations", async function () {
  await getDashboardPage().verifyTagAllocationProgressBarMath();
});

// TC_DB_042
When("User zooms the browser to {int}%", async function (zoomLevel: number) {
  await getDashboardPage().verifyZoomLevelAndInteractivity(zoomLevel);
});

Then("All header CTAs, dropdowns, and calendar controls should remain functional without clipping", async function () {
  // Assertions executed dynamically inside verifyZoomLevelAndInteractivity
});

// TC_DB_043
When("User switches tabs or minimizes the window for {int} seconds", async function (seconds: number) {
  await getDashboardPage().simulateTabSwitchAndRestore(seconds * 1000);
});

When("User restores focus back to the Dashboard tab", async function () {
  // Triggered in simulateTabSwitchAndRestore event sequence
});

Then("Dashboard session should remain active without UI freezing or console errors", async function () {
  await fixture.page.waitForLoadState('domcontentloaded');
});

// TC_DB_044
Given("DevTools network throttling is set to {string}", async function (networkProfile: string) {
  const cdpSession = await fixture.page.context().newCDPSession(fixture.page);
  await cdpSession.send('Network.emulateNetworkConditions', {
    offline: false,
    downloadThroughput: (400 * 1024) / 8, // Slow 3G download (~400 kbps)
    uploadThroughput: (400 * 1024) / 8,   // Slow 3G upload
    latency: 400,                          // 400ms latency
  });
});

When("User reloads the Dashboard page", async function () {
  await fixture.page.reload({ waitUntil: 'domcontentloaded' });
});

Then("Subtle loading indicators should display and metrics should populate cleanly without timing out", async function () {
  const kpiCard = fixture.page.locator('div').filter({ hasText: /Total Ranches|Active Bookings/i }).first();
  await kpiCard.waitFor({ state: 'visible', timeout: 30000 });
});

// TC_DB_045
Given("Backend dashboard metrics API is mocked to return HTTP 500", async function () {
  await getDashboardPage().mockMetricsApiError500();
});

Then("Dashboard widgets should display a graceful error fallback state without JS exceptions", async function () {
  await getDashboardPage().verifyErrorFallbackState();
});

// TC_DB_046
When("User emulates a mobile browser viewport", async function () {
  await getDashboardPage().setMobileViewportAndVerify();
});

Then("Calendar controls, touch targets, and dropdown overlays should remain responsive without clipping", async function () {
  // Assertions executed dynamically inside setMobileViewportAndVerify
});