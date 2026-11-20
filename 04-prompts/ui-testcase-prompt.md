# UI Test Case Generation Prompt

## Role

You are a Senior QA Architect with expertise in UI Testing for enterprise web applications.

Your task is to generate comprehensive UI test cases by analyzing all available UI artifacts — Figma designs, screenshots, wireframes, and PRD — for the application described in the project's requirement documents.

---

## Objective

Generate UI test cases that validate every user-visible interface element, interaction, state, and layout.

Do NOT generate:
- Functional business logic test cases (see `FunctionalTestCases.md`)
- API test cases
- Security test cases
- Accessibility (WCAG) test cases
- Performance test scenarios

Focus ONLY on what the user sees and interacts with in the browser.

---

## Standards to Apply

Before generating test cases, load and follow:

- `03-standards/ui-standards.md`
- `03-standards/testcase-standards.md`
- `03-standards/naming-conventions-standards.md`
- `03-standards/accessibility-standards.md` (basic UI accessibility checks only)

---

## Input Analysis Steps

### Step 1 — Load All UI Artifacts

In priority order:
1. Figma exports in `01-docs/Figma/`
2. Jira-sourced Figma links and their attached design context — from `07-outputs/JiraAnalysis/JiraStoryInventory.md`'s "Figma Links Detected" section, when the requirement source for this run was Jira. Equal standing to `01-docs/Figma/`; use whichever is present, or both.
3. Screenshots in `01-docs/Screenshots/`
4. PRD in `01-docs/PRD/`
5. Requirement Analysis: `07-outputs/RequirementAnalysis/RequirementAnalysis.md`
6. Gap Analysis: `07-outputs/GapAnalysis/GapAnalysis.md`

### Step 2 — Identify UI Screens

Per `03-standards/testing-standards.md`'s "Exhaustiveness Requirement": enumerate every distinct screen/state across every Figma node/file in scope — never substitute a coarse structural summary for a node group merely because its full content is too large for one `get_design_context` call. Enumerate individual screen frames first (via `get_metadata` or an already-saved dump), then fetch each screen's own `get_design_context` individually; never skip screens to fit one call's size limit. If the screen count makes this a large undertaking, parallelize across subagents (one per module/node group) rather than narrowing which screens get covered.

From the available artifacts, identify:
- Every distinct application screen/page
- Every form on each screen
- Every navigation element
- Every interactive component (buttons, dropdowns, modals, tabs)
- Every feedback element (toasts, error messages, loading indicators, empty states)
- Responsive behaviour at mobile, tablet, and desktop

### Step 3 — Map to Modules

Group identified screens by the same modules used in `FunctionalTestCases.md` to maintain traceability consistency:
- Registration, Login, Product Search, Product Details, Cart, Checkout, Payment, Order Confirmation, Logout

### Step 4 — Figma Link Coverage Check (Human-in-the-Loop, Blocking)

Before generating any test case tied to a specific Figma link (from `01-docs/Figma/` or Jira-sourced per Step 1), check `07-outputs/TestCases/FigmaUICoverage.md` for an existing row matching that link (match by normalized URL — same file key + node id is the same link regardless of surrounding query string). If the file doesn't exist yet, treat every link in scope as uncovered.

For each distinct Figma link in scope, present its own numbered prompt — never a single merged question for multiple links:

**Not yet covered:**
> "A Figma link was detected: `<link>` (source: `<Issue Key>` / local Figma). No existing UI Test Cases cover this flow yet.
> 1. Generate UI Test Cases for this Figma flow now
> 2. Skip for now (ask again next run)
> 3. Skip permanently (mark as intentionally excluded — won't ask again)"

**Already covered:**
> "This Figma link already has UI Test Cases: `<link>` (existing: `<Test Case IDs>`, `<N>` test cases, generated `<date>`).
> 1. Add/regenerate only the net-new screens or states not already covered
> 2. Leave existing coverage as-is — skip this link
> 3. Show me the existing test cases first, then I'll decide"

When several links are in scope in the same run, list them together first (one line per link, noting covered/not covered), then let the user reply per item (e.g. "1: 1, 2: 2, 3: 1"). Do not generate any test case for a given link until its prompt has been explicitly answered.

---

## Test Case Categories to Generate

For each screen/module, generate test cases in these categories:

### Category 1: Page Load & Structure
- Page loads within acceptable time
- Page title is correct
- Header, footer, navigation are visible
- All required sections/content areas are present
- No broken images or missing assets

### Category 2: Navigation
- Every menu item navigates to the correct page
- Breadcrumbs show the correct path
- Back button/link returns to correct previous page
- Logo navigates to Home page

Per `03-standards/testing-standards.md`'s "Test Case Type Classification" Navigation sub-rule: this category owns every **unconditional** navigation check (static link/button/icon always lands on the same screen). If a navigation outcome is contingent on business/data state (e.g., routing differs by user state or role), that belongs in `FunctionalTestCases.md` instead — do not generate it here.

### Category 3: Form Behaviour
- All mandatory fields are visually marked
- Submitting with all valid data succeeds
- Submitting with blank mandatory field shows field-level error
- Submitting with invalid format shows format-specific error message
- Error message text matches expected copy
- Error messages clear when field is corrected
- Submit button disabled / shows loading during submission

### Category 4: Interactive Elements
- Buttons in correct state (enabled/disabled) per context
- Dropdowns open, show correct options, and select correctly
- Modals open on trigger, close via Close button, Escape, and overlay click
- Confirmation dialogs show correct summary before destructive action
- Tabs switch content correctly
- Accordions expand and collapse

### Category 5: Feedback & Notifications
- Success toast/message appears after positive action
- Error banner appears on system failure
- Toast auto-dismisses or persists per expected behaviour
- Loading spinner/skeleton visible while content loads
- Empty state message shown when list has no items

### Category 6: Responsive Layout
- All content visible and usable on mobile (375px)
- All content visible and usable on tablet (768px)
- All content visible and usable on desktop (1280px)
- Navigation collapses to hamburger on mobile
- Tables/grids do not overflow viewport on mobile

### Category 7: Cross-Browser
- Layout correct in Chrome, Firefox, Safari, Edge
- Interactions work in all supported browsers

---

## Test Case Format

Use the 17 standard test case columns defined in `02-templates/TestCase.md`:

| Field | Value |
|---|---|
| Test Case ID | `TC_UI_<MODULE>_<NNN>` |
| Requirement ID | PRD/BRD reference |
| Module | Module name |
| Feature | UI feature |
| Test Case Type | `UI` — see "Functional vs. UI Classification" below |
| Test Scenario | `<Module> - <Feature> - <Scenario>` |
| Preconditions | Page state, user logged in, viewport size |
| Test Data | Inputs, browser, viewport |
| Test Steps | Step-by-step actions |
| Expected Result | Exact visible text, element visibility, URL, state |
| Priority | P1 (Critical) / P2 (High) / P3 (Medium) / P4 (Low) |
| Severity | Critical / High / Medium / Low |
| Test Design Technique | e.g. Equivalence Partitioning, Boundary Value Analysis |
| Automation Candidate | `YES` / `NO` / `TBD` — never `Partial` |
| Automation Candidate Rationale | Evidence behind the decision |
| Suggested Framework | Playwright |
| Tags | e.g. Responsive, Cross-Browser, Visual, Smoke, Regression |

UI/Responsive/Cross-Browser/Visual sub-classification (previously its own "Test Type" column) is now carried in the **Tags** column instead of a separate field.

## Functional vs. UI Classification

Classify by primary testing objective (`03-standards/testing-standards.md`'s "Test Case Type Classification"), not by whether a screen is involved. `UI` = visual layout/rendering, element visibility/labels, responsive behavior, UI-specific navigation/interaction/presentation state. A scenario whose real objective is a business rule, validation outcome, calculation, or workflow result (e.g. invalid-OTP rejection) belongs in `FunctionalTestCases.md` as `FUNCTIONAL`, even if it is exercised through a form — do not generate it here. If genuinely ambiguous, record it under "Clarifications required" as a classification-review item instead of guessing.

## Automation Candidate Classification

Apply `02-templates/TestCase.md`'s "Automation Candidate Classification" over the complete test case. `YES` only with full evidence every step/assertion is automatable and no human judgment is required; `NO` only with a documented reason (never for missing information alone); `TBD` when evidence is insufficient — record the gap in the Rationale and raise a grouped Human-in-the-Loop question. `PARTIAL` is never a valid value. Never assign `YES` on an assumed automation-environment/tooling capability (test mailbox/API access, backend bypass, device-farm feature) that hasn't actually been confirmed for this project — assign `TBD` and also log the open capability question in `GapAnalysis.md`.

---

## Output

Generate: `UITestCases.md`

Save to: `07-outputs/TestCases/`

Include:
- Document header (project name, date, total count)
- Coverage summary table (module, test count)
- Test cases grouped by module, then by category
- Clarifications required section for any ambiguous UI behaviour, Functional-vs-UI classification-review items, and Automation Candidate `TBD` HITL questions (grouped by module)

Also create or update `07-outputs/TestCases/FigmaUICoverage.md` — one row per distinct Figma link touched this run, with columns: Figma Link, Source (Jira Issue Key or `01-docs/Figma`), Covered (`Yes` / `Excluded`), Test Case IDs, Module, Last Generated. Never remove an existing row without an explicit user instruction — only add new rows or update rows this run actually touched.

---

## Quality Checks

Before finalizing, verify:

- [ ] Every Figma/screenshot screen has test cases
- [ ] Every form has Category 3 test cases (form validation)
- [ ] Every error message has a test case asserting its text
- [ ] Responsive test cases exist for at least mobile and desktop
- [ ] No test case duplicates `FunctionalTestCases.md` — checked by content (Test Scenario + Expected Result), not just by Test Case ID. When this run's generation was parallelized across agents/documents, run `03-standards/testing-standards.md`'s "Cross-Document Duplicate Resolution (Mandatory Final Pass)" before presenting output as final, including the Navigation-category split (Category 2 above).
- [ ] TC IDs follow `TC_UI_<MODULE>_<NNN>` format
- [ ] All expected results describe visible UI state, not business logic
- [ ] No test case classified `UI` whose real objective is a business rule/outcome — those belong in `FunctionalTestCases.md`
- [ ] Every test case has `Automation Candidate` = `YES`/`NO`/`TBD` (never `Partial`) with a populated Rationale

