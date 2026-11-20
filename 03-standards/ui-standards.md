# UI Testing Standards

## Objective

Establish enterprise-grade UI testing standards to ensure all AI-generated UI test cases and automation scripts comprehensively validate user interface behaviour, visual correctness, accessibility, and cross-browser compatibility.

---

## Scope

These standards apply to:

- Web Application UI Testing
- Responsive / Mobile Web Testing
- Cross-Browser Testing
- Component-Level Testing
- End-to-End User Journey Testing
- Visual Regression Testing

---

## UI Testing Principles

Every UI test must:

- Validate user-visible behaviour — not internal implementation.
- Be written from the user's perspective.
- Cover both happy path and error/edge states.
- Be independent and repeatable.
- Include meaningful assertions on UI state.
- Validate presentation (layout, visibility, labels, responsive/visual behaviour, UI-specific navigation), not business outcome. A scenario whose objective is a business rule, calculation, validation outcome, or workflow result is `FUNCTIONAL` even when driven through the UI — see `03-standards/testing-standards.md`'s "Test Case Type Classification" for the full rule and examples.

---

## UI Test Coverage Areas

### 1. Page Layout and Structure

- Page title is correct.
- All major sections are present (header, footer, main content area, navigation).
- No broken images (all images load and are visible).
- No overlapping or hidden required content.

### 2. Navigation

- Menu items navigate to the correct pages.
- Breadcrumbs reflect current location.
- Back-navigation returns to the correct previous page.
- Deep links load the correct page directly.
- 404 page is displayed for invalid URLs.

### 3. Forms

- All mandatory fields are marked (e.g. asterisk or visual indicator).
- All optional fields are clearly distinguishable.
- Inline validation fires at the correct trigger (on-blur or on-submit).
- Validation messages are visible, specific, and user-friendly.
- Form retains entered values on validation failure (no unintended clearing).
- Successful submission produces a clear success indicator.
- Submit button is disabled or shows a loading state while processing.

### 4. Buttons and Interactive Elements

- All buttons are visible, labeled, and clickable in expected state.
- Buttons show correct disabled state when action is not permitted.
- Hover/focus states are visible.
- Destructive actions (delete, cancel order) require confirmation.

### 5. Error Messages

- Error messages appear in close proximity to the triggering field.
- Error messages use plain language — no technical codes exposed.
- Error messages disappear when the error is corrected.
- Global error banners are displayed for system-level failures.

### 6. Modals and Dialogs

- Modal opens on the correct trigger.
- Modal can be closed via Close button, Escape key, and clicking outside.
- Background content is not interactable while modal is open.
- Confirmation dialogs display the correct action summary before confirming.

### 7. Tables and Lists

- Column headers match expected data labels.
- Rows display correct data.
- Empty state message is shown when no data exists.
- Pagination controls are functional and display correct page counts.
- Sorting changes row order correctly.

### 8. Notifications and Toast Messages

- Success/error toasts appear after the triggering action.
- Toast messages contain the correct text.
- Toasts auto-dismiss after the expected timeout or persist when required.

---

## Cross-Browser Standards

Test on the following browsers as a minimum:

| Browser | Engine | Desktop | Mobile Web |
|---|---|---|---|
| Chrome (latest) | Chromium | ✅ | ✅ |
| Firefox (latest) | Gecko | ✅ | Optional |
| Safari (latest) | WebKit | ✅ | ✅ |
| Edge (latest) | Chromium | ✅ | Optional |

Minimum supported browser versions must be defined per project in the Test Plan.

---

## Responsive Testing Breakpoints

Test at the following viewport sizes:

| Category | Width |
|---|---|
| Mobile S | 320px |
| Mobile L | 425px |
| Tablet | 768px |
| Desktop | 1280px |
| Wide Desktop | 1440px |

For Playwright:

```typescript
// Set viewport in test or config
await page.setViewportSize({ width: 768, height: 1024 });
```

---

## Visual Regression Standards

When visual regression testing is required:

- Use Playwright's built-in `expect(page).toHaveScreenshot()` for baseline comparisons.
- Store baseline screenshots in `tests/__snapshots__/`.
- Run baseline capture on a clean, stable build before enabling diff checks.
- Define a pixel-difference threshold per component (e.g., `maxDiffPixels: 50`).
- Visual regression tests are a separate suite, not part of smoke/regression.

```typescript
await expect(page).toHaveScreenshot('login-page.png', { maxDiffPixels: 50 });
```

---

## Accessibility (Basic) in UI Tests

Every UI test must also verify:

- Page has a visible, descriptive `<title>`.
- Form fields have associated `<label>` elements.
- Images have `alt` text.
- Interactive elements are keyboard-reachable (Tab key navigates to them).
- Error messages are associated with their fields via `aria-describedby`.

Full accessibility testing is covered separately in `03-standards/accessibility-standards.md`.

---

## UI Test Case Structure

Every UI test case must include:

| Field | Description |
|---|---|
| TC ID | From naming conventions: `TC_<MODULE>_<NNN>` |
| Requirement ID | Traced to PRD/BRD requirement |
| Module | Business module name |
| Feature | UI feature under test |
| Preconditions | Required state before test execution |
| Test Data | Input values, user roles, environment |
| Test Steps | Step-by-step user actions |
| Expected UI Result | Exact visible outcome: text, element state, URL, toast |
| Priority | P1–P4 |
| Tags | UI sub-classification (Smoke, Regression, Visual, Responsive, Cross-Browser) — carried in the Tags column, not a separate field |
| Automation Candidate | `YES` / `NO` / `TBD` — see `02-templates/TestCase.md`'s "Automation Candidate Classification"; `Partial` is never valid |
| Automation Candidate Rationale | Evidence behind the Automation Candidate decision |

---

## UI Assertion Standards

Every UI test step that changes state must be followed by an assertion.

Assert:

- Correct page URL after navigation.
- Correct page title after navigation.
- Visibility of expected elements after interaction.
- Correct text content for labels, headings, messages.
- Correct disabled/enabled state of buttons.
- Form field values after auto-population.
- Toast/notification text after action.
- Modal open/close state.

Never assert:

- CSS property values directly (fragile).
- Auto-generated class names.
- Internal application state not visible to the user.

---

## UI Environment Standards

- UI tests must run against a stable, seeded QA or UAT environment.
- Test data must be pre-seeded or created via API in `beforeEach`/`beforeAll`.
- Never run UI tests against Production unless explicitly approved.
- Base URL must be injected via environment variable — never hardcoded.

---

## Reporting Standards

Every UI test execution must generate:

- HTML Report with pass/fail per test case.
- Screenshots on failure.
- Video on failure retention.
- Trace files for first-retry failures (Playwright `trace: 'on-first-retry'`).
- Mapped Test Case ID visible in report.

---

## UI Testing Anti-Patterns (Forbidden)

- Do not validate CSS colors or fonts in functional UI tests (belongs to visual regression).
- Do not use `waitForTimeout` / `Thread.sleep` — use smart waits.
- Do not test multiple unrelated user journeys in one test spec.
- Do not rely on hardcoded element positions or coordinates.
- Do not duplicate assertions that are already covered by API/unit tests.
- Do not test third-party components beyond their integration point.

---

## Compliance

All UI test cases and automation scripts must comply with:

- `03-standards/playwright-typescript-standards.md`
- `03-standards/locator-standards.md`
- `03-standards/naming-conventions-standards.md`
- `03-standards/accessibility-standards.md`
- `03-standards/automation-standards.md`
