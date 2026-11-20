# Testing Standards

## Objective
Generate comprehensive, reusable, business-focused, and automation-ready test cases with maximum requirement coverage.

---
# General Principles

- Every requirement must be testable.
- Every critical business flow must be covered.
- Cover positive, negative, alternate, and exception scenarios.
- Prefer reusable test cases.
- Avoid duplicate scenarios.
- One objective per test case.
- Ensure complete traceability.
- Test from an end-user perspective.
- Validate business requirements.
- Verify business workflows.
- Ensure complete requirement coverage.
- Prevent defect leakage.
- Promote early defect detection.
- Prefer reusable test assets.
- Maintain end-to-end traceability.

---

# Exhaustiveness Requirement (Default Behavior — Do Not Self-Scope)

"Maximize business coverage" and "comprehensive" mean **exhaustive by default**, not a curated or sampled subset chosen to keep output manageable. Every agent in this framework must apply this without being separately asked, every run:

- **Never sample.** When evaluating a source with many items (e.g. every worksheet/row of `010-GenericTestCases/GenericTestCases.xlsx`, every screen in a Figma file, every field on a form), evaluate every individual item. A worksheet, screen, or field that looks obviously irrelevant still gets its own row-level/item-level decision and a one-line reason — it is never excluded in bulk without that per-item record, because experience has shown that a "clearly irrelevant" group can still contain a few genuinely applicable items once checked individually.
- **Never cap coverage per requirement/screen to a small fixed number** (e.g. "1-3 cases per requirement") when the applicable test-design techniques and coverage categories (positive, negative, boundary, alternate, exception, role-based) each support their own case. Generate the full set those categories call for, per requirement and per field/rule/sub-behavior, not one bundled case per requirement.
- **Never substitute a coarse structural summary for full-fidelity detail** merely because the full detail is large to fetch or process (e.g. a Figma node group too large for one call) — break the fetch into smaller individual pieces (e.g. per-screen calls) instead of narrowing what gets analyzed.
- **If the resulting scale is large, parallelize the work (e.g. via subagents, one per module/cluster) rather than narrowing scope to fit a single pass.** Scale is a execution-strategy problem to solve, not a reason to generate less.
- **Do not wait to be asked twice.** If a prior run on the same project used a narrower scope, do not repeat that narrower scope by default on a later run — apply full exhaustiveness every time, and only narrow scope when the user explicitly asks for a smaller/sampled run for that specific request.

---

# Requirement Review Checklist

Always verify:

- Functional Requirements
- Business Rules
- User Roles
- Permissions
- Mandatory Fields
- Optional Fields
- Input Validation
- Output Validation
- Workflow Transitions
- Notifications
- APIs
- Database Updates
- Integrations
- Error Handling
- API Contracts

Raise clarification questions where required.

---

# Test Case Standard

Each test case must contain exactly these 17 columns (see `02-templates/TestCase.md`):

- Test Case ID
- Requirement ID
- Module
- Feature
- Test Case Type
- Test Scenario
- Preconditions
- Test Data
- Test Steps
- Expected Result
- Priority
- Severity
- Test Design Technique
- Automation Candidate (`YES`/`NO`/`TBD` only — `Partial` is never valid)
- Automation Candidate Rationale
- Suggested Framework
- Tags

When Generic Test Case generation is enabled (`FUNCTIONAL_AND_GENERIC` mode), a Generic Test Case additionally carries 5 conditional columns: Generic Test Case ID, Generic Test Case Source Worksheet, Generic Test Case Source Reference, Generic Applicability Decision, Generic Applicability Rationale.

---

# Test Case Type Classification (FUNCTIONAL vs. UI)

`FUNCTIONAL` and `UI` are mutually exclusive. Classify by the test case's **primary testing objective**, never by whether it happens to use the interface — a scenario driven through clicks, forms, or screen navigation is not automatically `UI`.

Use `FUNCTIONAL` when the objective validates business rules/outcomes, calculations, data processing, positive/negative/boundary/exception behavior, input validation and business constraints, authentication/authorization outcomes, state transitions, end-to-end business workflows, or data persistence/integrity — even when the test is performed through the UI.

Use `UI` when the objective validates visual layout/alignment/spacing/rendering, element visibility/labels/icons/presentation, responsive behavior, UI-specific navigation/interaction, screen transitions/dialogs/presentation states, or UI-specific usability/visual behavior.

Rules:

- Assign exactly one primary type per test case. Do not duplicate the same scenario across Functional and UI outputs merely because it touches the UI.
- Split a scenario into two test cases only when it genuinely has two independent objectives, each with its own independently verifiable expected result.
- If the primary objective is genuinely ambiguous, do not guess — raise it as a classification-review item under "Clarifications Required" (per `04-prompts/functional-testcase-prompt.md` / `04-prompts/ui-testcase-prompt.md`) instead of assigning a type.
- Apply this rule identically to project-specific and Generic Test Cases — adapting a Generic Test Case does not change how it is classified.
- **Navigation specifically** (the most common source of Functional/UI overlap, since both agents' coverage categories include "Navigation"): an **unconditional** navigation check — tapping a static link/button/icon/menu item always lands on the same screen, with no business rule, permission, or data state deciding the outcome — is `UI` only. `FUNCTIONAL` covers navigation only when the destination or routing outcome is **contingent on business/data state** (e.g., post-login routing differs for a first-run vs. returning user, a guarded screen redirects based on role/entitlement, or a wizard's next step depends on prior input). When in doubt whether a navigation check is conditional, treat it as `UI`.

Examples: "An invalid OTP is rejected and the user cannot authenticate" → `FUNCTIONAL`. "OTP input fields are aligned and visible on a mobile screen" → `UI`. "A user without the required role cannot perform a restricted business operation" → `FUNCTIONAL`. "A modal is positioned correctly and its close control is visible" → `UI`. "Tapping 'Forgot Password' navigates to the Reset Password screen" → `UI` (unconditional). "A first-time user is routed to onboarding while a returning user goes straight to the dashboard after login" → `FUNCTIONAL` (conditional on state).

---

# Cross-Document Duplicate Resolution (Mandatory Final Pass)

Generation for a single run is often parallelized — across subagents, across the Functional/Generic/UI documents, or both — because the Exhaustiveness Requirement above means the per-document output is too large for one pass. Parallel agents cannot see each other's in-progress output, so the same real-world scenario can be generated twice under two different IDs (e.g. a Login blank-field-validation case written independently by the Functional pass and by the Generic-adaptation pass, or a static navigation check written independently by the Functional pass and the UI pass). A Test Case ID-uniqueness check does **not** catch this — the IDs are unique; the *content* is duplicated.

Whenever generation for a run spans more than one agent and/or more than one output document, a final consolidation pass is mandatory **before presenting the output as final**, not after the user notices:

1. Group all generated rows (across Functional, Generic, and UI) by shared Requirement ID (or shared screen/module for UI-only clusters).
2. Within each group, compare Test Scenario + Expected Result content for near-equivalent assertions — not just matching Test Case IDs. Two rows that assert the same outcome through the same action are a duplicate even if their wording, module grouping, or Test Case Type differs.
3. For every confirmed duplicate, keep exactly one row and remove the other(s) — apply the Navigation rule above to decide which survives when it's a Functional/UI split, and prefer the row with the more specific/accurate evidence (e.g. a later per-screen Figma fetch that confirms something an earlier pass had marked `TBD`) when evidence quality differs.
4. Never silently delete a removed row's decision record: if it was an adapted Generic Test Case, reclassify its `GenericApplicabilityReview.md` entry from `APPLICABLE` to `NOT_APPLICABLE` with a redundancy reason naming the surviving row. If it was a project-specific row, record the removal and its surviving counterpart in the output document's Clarifications/Resolution section.
5. Recompute every count (coverage summary tables, grand totals, Automation Candidate breakdowns) from the post-removal row set — never leave stale pre-removal counts in the document.

This is a standing requirement for every run that uses parallel generation, not a one-off cleanup task.

---

# Priority Matrix

| Priority | Meaning |
|----------|---------|
| P1 | Business Critical |
| P2 | High |
| P3 | Medium |
| P4 | Low |

---

# Severity Matrix

| Severity | Meaning |
|----------|---------|
| Critical | System unusable, security issue, data corruption |
| High | Major functionality broken |
| Medium | Partial functionality impacted |
| Low | Cosmetic or usability issue |

---

# Test Coverage Standards

Every feature should include:

- Positive Testing
- Negative Testing
- Boundary Testing
- Alternate Flow Testing
- Exception Handling
- Workflow Validation
- Role-Based Testing
- Error Handling
- End-to-End Testing

---

# Test Design Techniques

Always apply where appropriate:

- Boundary Value Analysis
- Equivalence Partitioning
- Decision Table Testing
- State Transition Testing
- Error Guessing
- Pairwise Testing
- Risk-Based Testing

---

# Functional Testing

Generate scenarios for:

- CRUD Operations
- Business Rules
- Field Validations
- Navigation
- Mandatory Fields
- Optional Fields
- Search
- Sort
- Filter
- Pagination
- Role-Based Access
- Session Management
- Error Handling
- Workflow Transitions
- User Permissions

---

# UI Testing

Validate:

- Layout
- Alignment
- Labels
- Buttons
- Forms
- Tables
- Menus
- Dialogs
- Tooltips
- Responsive Design
- Browser Compatibility
- Mobile Compatibility

---

# API Testing

Generate:

- GET
- POST
- PUT
- PATCH
- DELETE
- Positive tests
- Negative tests
- Invalid Payload
- Missing Headers
- Authentication
- Authorization
- Invalid Token
- Expired Token
- Schema Validation
- Response Validation
- Status Code Validation
- Query Parameters
- Pagination
- Error Responses

---

# Database Testing

Validate:

- CRUD Operations
- Stored Procedures
- Constraints
- Data Integrity
- Duplicate Records
- Null Handling
- Referential Integrity

---

# Accessibility Testing

Follow WCAG.

Validate:

- Keyboard Navigation
- Focus Order
- Screen Reader
- Color Contrast
- ARIA Labels
- Form Labels
- Alternative Text

---

# Security Testing

Validate:

- Authentication
- Authorization
- Session Management
- SQL Injection
- XSS
- CSRF
- Input Validation
- Password Policy
- Secure Headers

Reference OWASP Top 10 where applicable.

---

# Performance Testing

Recommend scenarios for:

- Load
- Stress
- Spike
- Soak
- Volume
- Scalability

---

# Mobile Testing

Validate:

- Installation
- Upgrade
- Permissions
- Orientation
- Notifications
- Offline Mode
- Network Switching
- Camera
- GPS
- Biometrics

---

# Edge Cases

Always include:

- Null
- Empty
- Blank
- Boundary Values
- Invalid Data
- Maximum Length
- Minimum Length
- Special Characters
- Unicode
- Emoji
- Duplicate Data
- Concurrent Users
- Session Timeout
- Browser Refresh   
- Network Failure

---

# Defect Standards

Every defect must include:

- Title
- Description
- Steps to Reproduce
- Expected Result
- Actual Result
- Severity
- Priority
- Screenshots
- Logs
- Environment

---

# Reporting Standards

Track:

- Requirement Coverage
- Test Coverage
- Execution Status
- Pass Rate
- Fail Rate
- Defect Density
- Defect Leakage
- Automation Coverage

---

# Regression Classification

Categorize test cases into:

- Smoke
- Sanity
- Regression
- Critical Path
- Business Critical

---

# Automation Recommendation

For every test case specify:

- Automation Candidate (`YES`/`NO`/`TBD` — see "Test Case Standard" above)
- Recommended Framework
- Automation Priority
- Estimated Complexity

---

# Requirement Traceability

Map every:

Requirement

↓

Feature

↓

Test Case

↓

Automation Candidate

No requirement should remain untested.

---

# Output Quality Standards

Every generated artifact must be:

- Accurate
- Complete
- Traceable
- Review Ready
- Business Focused
- Automation Friendly
- Easy to Maintain

---

# Never

- Invent business rules.
- Skip negative testing.
- Ignore edge cases.
- Ignore accessibility.
- Ignore security.
- Produce duplicate test cases.
- Combine multiple objectives into one test case.