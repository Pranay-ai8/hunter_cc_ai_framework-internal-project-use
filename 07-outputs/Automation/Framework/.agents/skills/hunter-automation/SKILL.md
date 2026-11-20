---
name: Hunter Automation Workflow
description: Guidelines and exact steps for automating Playwright + Cucumber tests in the HUNTER framework, including test-case traceability, framework conventions, validation, execution, and mandatory per-file user approval.
---

# Hunter Automation Workflow

When asked to automate, modify, extend, debug, or implement a test case in the HUNTER Playwright + Cucumber framework, **STRICTLY follow this workflow and architecture**.

Solve the very comman error the ranches is not loading solve that 1st if you encounter any as we solved in earlier testcases(manually the ranches will load in default in list view).

The goal is to create automation that is:

- Maintainable
- Reusable
- Consistent with the existing HUNTER framework
- Traceable to the source test case
- Properly logged
- Properly asserted
- Driven by test data
- Environment-aware
- Independently executable where possible
- In the feature file also mention the tags for the types(@ui..) and the priority(@high...).
- Generate all the steps based on the previous modules structure and notify me once done i will run the test command
- Also in the steps you should never use the data or any playwright operations like .isVisible... or other things in the steps all of that should be included in the page itself and not in the steps step file just use it from the page.

---

# 1. Mandatory Workflow

Before making any code changes, follow this sequence:

1. Identify the exact test case(s) being automated.
2. Read and understand:
   - Test Case ID
   - Scenario
   - Preconditions
   - Steps
   - Test Data
   - Expected Result
   - Priority
   - Severity
   - Type
3. Inspect the repository for existing:
   - Feature files
   - Step definitions
   - Page objects
   - Locators
   - Playwright wrapper methods
   - Assert wrapper methods
   - Fixtures
   - Hooks
   - Utilities
   - Test data
4. Reuse existing implementations whenever possible.
5. Determine exactly which files need to be created or modified.
6. **DO NOT modify any file yet.**
7. Present the proposed changes **one file at a time**.
8. For **EVERY file**, ask the user to:
   - **Accept**
   - **Reject**
9. Apply only changes explicitly accepted by the user.
10. Validate the implementation.
11. Run the relevant test.
12. Report the result and any remaining issues.

---

# 2. CRITICAL: Per-File Accept/Reject Approval

**This is a mandatory rule and must never be skipped.**

Every time code is added, modified, deleted, or updated, the user must receive an explicit **Accept / Reject** choice for **each individual file**.

For example, if a test requires changes to:

```text
src/test/features/invoice.feature
src/test/steps/invoiceSteps.ts
src/pages/invoicePage.ts
src/helper/util/test-data/testDataInvoice.csv

for the sanity testcases do this when i tell you to do
src/test/features/sanity/invoice.feature


```

---

# 3. Page Object Model (POM) & Step Definition Flow

To maintain a clean and reliable test architecture, strictly adhere to the following division of responsibilities between Step Definitions and Page Objects:

## Page Object Responsibilities (`*Page.ts`)

1. **Encapsulate ALL Locators:** Keep all locators private in an `Elements` object at the top of the class. Never expose raw string locators or Playwright `Locator` objects to the step definitions.
2. **Perform ALL Playwright Actions:** Any `click`, `fill`, `type`, `focus`, `keyboard.press`, etc., must happen inside a Page method.
3. **Perform ALL Assertions:** Step definitions should not use Playwright's `expect` or wrapper assert classes. Instead, the Page Object should have specific `verifyX` methods (e.g., `verifyToastMessageVisible(message: string)`).
4. **SPA Navigation Handling:** When asserting URLs in a Single Page Application, prefer Playwright's polling `expect(page).toHaveURL(/.*pattern/)` over `waitForURL()`, as `waitForURL()` can hang if the navigation doesn't trigger a full network load event.
5. **Exact Text Matching:** When asserting toast/error messages, use exact text selectors with quotes (e.g., `text="${message}"`) to avoid flakiness with spaces or partial matches.

## Step Definition Responsibilities (`*Steps.ts`)

1. **Keep it Clean:** Step definitions should act purely as translators between Gherkin English and Page Object methods.
2. **No Playwright Imports:** Step files should ideally not import or use `expect`, `locator`, or Playwright action methods directly.
3. **Delegation:** If a step says "Verify an error message is displayed", the step definition must simply call `await pageObj.verifyErrorMessageVisible();`.
4. **Substep Logging:** Always wrap Page Object calls with `fixture.logger` and `fixture.subStepLogger` to ensure execution trace logs are captured perfectly.

---

# 4. Sanity Suite Standards

When creating or maintaining Sanity test suites in `src/test/features/sanity/`:

1. **File Structure & Naming:**
   - Feature file location: `src/test/features/sanity/<module>.feature` (e.g., `login.feature`, `ranches.feature`, `bookings.feature`, `staffsGuides.feature`).
   - Feature tags: `@sanity @<module>` at the feature header.
   - Scenario tags: `@high`, `@critical`, `@navigation`, `@ui`, `@functional`, and `@Key:<KEY>` on every scenario.

2. **Sanity Scope & Flow Strategy:**
   - **Entry Point:** Module navigation and primary header verification.
   - **View Toggles & Filtering:** List vs Grid views, status/role filtering, and search retrieval.
   - **Deep Read Path:** Navigating directly to detailed item view.
   - **Step-by-Step Creation Traversal:** Multi-step wizard/modal flows must be broken down sequentially step-by-step (e.g., Step 1, Step 2, Step 3, Step 4, Step 5) filling only mandatory/required fields to verify end-to-end record creation and redirection cleanly.
   - **State Transitions / Quick Actions:** Approvals, Status changes, and Edit transitions.

3. **Execution Commands:**
   - Single module sanity: `npm run test -- --tags="@sanity and @<module>"`
   - Complete application sanity: `npm run test -- --tags="@sanity"`

---

# 5. Staffs & Guides Module (`staffsGuides`) Conventions

When automating or extending the **Staffs & Guides** module, follow these framework conventions:

1. **File Naming & Locations:**
   - **Sanity Feature:** `src/test/features/sanity/staffsGuides.feature`
   - **Regression Feature:** `src/test/features/regression/staffsGuides.feature`
   - **Step Definitions:** `src/test/steps/staffsGuidesSteps.ts`
   - **Page Object:** `src/pages/staffsGuidesPage.ts`
   - **Test Data:** `src/helper/util/test-data/testDataStaffsGuides.csv` (Keys: `SGC001`, `SGC002`, ...)

2. **Core Capabilities & Selectors:**
   - **Sidebar Navigation:** Locate under management/operations: `nav a[href="/staff"]`, `nav a[href="/guides"]`, or `nav a[href*="staff"]`.
   - **Role & Type Filtering:** Tabs or dropdown filters for `All`, `Staff`, `Guide`, `Admin`, `Ranch Manager`, `Active`, `Inactive`.
   - **Creation / Add Wizard or Dialog:**
     - Basic Info: Full Name, Email, Phone Number, Role dropdown.
     - Ranch Assignment: Multi-select dropdown or checkboxes for assigned ranches (e.g., Powderhorn Ranch, King Ranch).
     - Guide Credentials: License Number, Expiration Date, Specialization tags (Hunting, Fishing, Tracking).
     - Form submission, validation alerts, and successful toast confirmation.
   - **Quick Actions & Edit:**
     - Actions menu (`...` button): View Details, Edit, Deactivate/Activate, Assign to Ranch, Remove.
     - Role modification and dynamic update of staff/guide chip states.
   - **Dynamic Data Resiliency:** Always support dynamic table/card lookups and fallback to first available active row if hardcoded test data IDs vary across test environments.
