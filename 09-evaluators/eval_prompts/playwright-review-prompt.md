# Playwright Script Review Prompt

## Objective

Review previously generated Playwright automation scripts for code quality, framework compliance, reliability, and production-readiness — and produce a scored Evaluation Report.

This is a review activity, not a generation activity. Do not regenerate scripts; assess what already exists.

---

# Input Sources

## Primary Inputs

- Generated Playwright Scripts (.ts / .js) under review
- Page Object files
- Test Data files
- Configuration files (playwright.config)

## Existing Framework Outputs

- FunctionalTestCases.md / .xlsx (source test cases the scripts implement)
- AutomationFramework.md (if available)

---

# Review Scope

Review the scripts against:

- 03-standards/automation-standards.md
- 03-standards/code-review-standards.md
- 03-standards/locator-standards.md
- 03-standards/naming-conventions-standards.md
- 03-standards/playwright-standards.md
- 09-evaluators/eval_standards/evaluation-standards.md

---

# Review Dimensions

Score each dimension:

- Test Case Coverage (does automation implement the mapped manual test cases?)
- Page Object Model Compliance
- Locator Quality (test-id / role-based locators over brittle CSS/XPath)
- Synchronization (Playwright auto-waits used correctly, no hard-coded timeouts)
- Assertion Quality (meaningful, business-outcome assertions)
- Test Independence (no inter-test dependency, proper fixtures/setup-teardown)
- Naming Conventions
- Error Handling & Reporting (screenshots/traces on failure)
- CI/CD Readiness

---

# Findings to Detect

Identify and classify by severity (Critical / Major / Minor / Observation):

- Missing coverage for a mapped functional test case
- Business logic embedded inside Page Objects
- Brittle locators (absolute XPath, index-based selectors)
- Hard-coded `waitForTimeout` usage
- Missing or weak assertions
- Hardcoded test data or credentials
- Inter-test dependencies
- Missing failure evidence (screenshot/trace/video config)
- Naming convention violations

---

# Deliverables

Generate a Playwright Review Report containing:

- Overall Score (/100) and Score Band
- Score Breakdown by Dimension
- Findings Table (File, Line/Location, Severity, Description)
- Locator Quality Summary
- Security Findings (hardcoded secrets/credentials)
- Recommendations
- Approval Status (Approved / Approved with Comments / Rejected)

---

# Output

Save the generated report into:

09-evaluators/eval_outputs/PlaywrightReview.md 09-evaluators/eval_outputs/PlaywrightReview.xlsx

---

# Output Quality

The review report shall be:

- Evidence Based
- Standards Referenced
- Objective
- Actionable
- Review Ready
