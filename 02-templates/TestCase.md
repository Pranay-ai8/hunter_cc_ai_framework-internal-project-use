# Test Case Template

## Objective

Generate comprehensive, production-ready, reusable, and automation-friendly test cases with complete requirement traceability.

Every test case must be independent, easy to understand, and executable by both Manual QA Engineers and Automation Engineers.

---

# Standard Test Case Columns

Every test case, regardless of type (Functional, UI, API, Security, Accessibility, Performance), must use exactly these 17 columns — no more, no fewer:

1. Test Case ID
2. Requirement ID
3. Module
4. Feature
5. Test Case Type — `FUNCTIONAL` / `UI` / `API` / `SECURITY` / `ACCESSIBILITY` / `PERFORMANCE` / `GENERIC`
6. Test Scenario
7. Preconditions
8. Test Data
9. Test Steps
10. Expected Result
11. Priority
12. Severity
13. Test Design Technique
14. Automation Candidate — `YES` / `NO` / `TBD`
15. Automation Candidate Rationale
16. Suggested Framework
17. Tags

Requirement ID traceability may originate from a BRD, PRD, FRD, Jira Story, Acceptance Criteria, Figma Screen, or API Endpoint — but it is always recorded in the single **Requirement ID** column above. No test case should exist without requirement traceability.

**Never invent a Requirement ID.** Populate this column only with the ID exactly as assigned in `RequirementAnalysis.md` (or the Jira issue key, when one exists), per `05-agents/requirement-analysis-agent.md`'s Requirement ID Assignment rule (original ID → Jira key → ask the user). Do not fabricate a new ID or numbering scheme at test-case-generation time, even to fill a gap. If a test case's requirement has no ID anywhere in `RequirementAnalysis.md` and no Jira key, either ask the user for the feature name/number to use, or record the Requirement ID as `TBD` and log the gap in `GapAnalysis.md` — never silently invent one.

**Automation Candidate Rationale** must cite the concrete evidence behind the decision (which steps/assertions are automatable, what is missing, or why it requires manual judgment) — never a generic restatement like "standard scenario". See "Automation Candidate Classification" below for the full decision rule.

## Generic Test Case Columns (Conditional)

These 5 additional columns are included **only** when Generic Test Case generation is explicitly invoked (`test_case_generation_mode = FUNCTIONAL_AND_GENERIC`) and the row's Test Case Type is `GENERIC`. They do not apply to Functional, UI, API, Security, Accessibility, or Performance test cases:

- Generic Test Case ID
- Generic Test Case Source Worksheet
- Generic Test Case Source Reference
- Generic Applicability Decision
- Generic Applicability Rationale

For a project-specific test case (Test Case Type = `FUNCTIONAL`, `UI`, `API`, `SECURITY`, `ACCESSIBILITY`, or `PERFORMANCE`), these 5 columns are omitted entirely rather than filled with `N/A`.

For a Generic Test Case (Test Case Type = `GENERIC`), preserve the original Generic Test Case ID, source worksheet name, and source/reference from `010-GenericTestCases/GenericTestCases.xlsx` (read-only — never modify the library). Only a generic case whose `Generic Applicability Decision = APPLICABLE` is ever written as a row here; `NOT_APPLICABLE` and `TBD` generic cases are excluded from this document and recorded instead in `GenericApplicabilityReview.md` (see `05-agents/functional-agent.md`).

---

# Automation Candidate Classification

Applies to every test case, project-specific or generic. Evaluate the **complete** test case — preconditions, every step, test data/dependencies, expected result(s), and verification method — never a single step in isolation, and never the test case's category or origin (a generic/standard scenario is not automatically `YES`).

## YES

Assign only when there is sufficient evidence that:

- Every execution step can be automated with the intended application interfaces (UI, API, or the supported automation environment).
- Every expected result can be asserted automatically (no subjective/visual-only judgment call).
- Required test data, accounts, services, permissions, and environment dependencies can be provided or controlled by the automation environment.
- Required UI elements/API operations are sufficiently identifiable for the intended implementation.
- No human action or manual judgment is required at any point during execution or verification.
- The test can run end-to-end within the defined automation scope.

A required Human-in-the-Loop approval of generated test cases or automation code **before a run starts** is not manual intervention **during** the test and must never by itself cause a `NO`.

## NO

Assign only when there is a clear, documented reason the complete test cannot reasonably be automated within the supported scope — e.g. inherently subjective human judgment, a mandatory physical interaction the automation environment cannot control, or a verification that genuinely requires a human decision. Never assign `NO` solely because the prompt currently lacks information — that is `TBD`.

## TBD

Assign whenever there is insufficient evidence to confidently decide full automation feasibility — e.g. unknown application interfaces, missing test accounts/environment details, unavailable verification mechanisms, unclear steps, or an unresolved manual dependency. For every `TBD` case, the Automation Candidate Rationale must record what is missing, and the agent must raise a specific Human-in-the-Loop question (see `05-agents/functional-agent.md` / `05-agents/ui-agent.md`) identifying the information needed to resolve it. Never silently default an unresolved `TBD` to `YES` or `NO`.

**Environment/tooling capability assumptions never justify `YES` on their own.** A step that depends on something the automation environment must provide — a controllable test mailbox (API/IMAP access) to retrieve a value like an OTP, a backend bypass/test-only endpoint, a device-farm feature (biometric injection, network/battery/storage simulation), a specific pre-provisioned test account or role — is not automatically assumed available just because it's a common pattern in other projects. Per the standing protocol: (1) ask the user whether that capability exists or can be provisioned; (2) if not answered or not available, assign `TBD`, state exactly which capability is unconfirmed in the Rationale, and log it as a dependency/gap in `GapAnalysis.md` (not just a HITL question in the test case document) so it has a durable, traceable record. Only assign `YES` once that capability has actually been confirmed, not by analogy to what a "typical" automation environment usually supports.

`PARTIAL` (or any equivalent partial-automation value) is never a valid value anywhere in the framework.

## Automation Eligibility

Only test cases with `Automation Candidate = YES` are eligible to be routed into automation script generation (`05-agents/playwright-*-agent.md`, `selenium-agent.md`, `cypress-javascript-agent.md`, `webdriverio-javascript-agent.md`, `appium-java-agent.md`). An explicit, traceable user override that promotes a `NO`/`TBD` case must re-evaluate the case against this same rule, update the Automation Candidate value and Rationale to reflect the override (what changed, who approved it, why), and must never silently bypass this gate.

---

# Preconditions

Include:

- Environment
- User Role
- Required Configuration
- Required Test Data
- Browser / Device
- Login State

---

# Test Data

Provide:

- Valid Data
- Invalid Data
- Boundary Data
- Negative Data
- Dynamic Data
- Mock Data (if applicable)

---

# Test Steps

Every step should:

- Be clear
- Be executable
- Validate one business action
- Avoid ambiguity

---

# Expected Result

Expected results must be:

- Measurable
- Observable
- Business-oriented
- Verifiable

Never use vague statements such as:

❌ Should work

Instead use:

✅ User is redirected to Dashboard page.

---

# Priority

Use:

- P1 – Critical
- P2 – High
- P3 – Medium
- P4 – Low

---

# Severity

Use:

- Critical
- High
- Medium
- Low

---

# Test Design Technique

Mention technique used:

- Boundary Value Analysis
- Equivalence Partitioning
- Decision Table
- State Transition
- Error Guessing
- Pairwise Testing
- Risk-Based Testing

---

# Validation Coverage

Guidance for populating the **Test Steps** / **Expected Result** columns — not a separate column. Where applicable include:

- Business Rules
- Mandatory Fields
- Optional Fields
- Input Validation
- Output Validation
- Error Messages
- Navigation
- Session Management
- Authorization
- Authentication
- Audit Logs
- Notifications

---

# Edge Case Coverage

Guidance for populating the **Test Data** / **Test Steps** columns — not a separate column. Always consider:

- Null
- Blank
- Empty
- Invalid Data
- Boundary Values
- Duplicate Records
- Concurrent Users
- Session Timeout
- Network Failure
- Browser Refresh

---

# Automation Candidate / Suggested Framework

Guidance for the **Automation Candidate**, **Automation Candidate Rationale**, and **Suggested Framework** columns:

- Automation Candidate: `YES` / `NO` / `TBD` — see "Automation Candidate Classification" above. `Partial` is never valid.
- Automation Candidate Rationale: the evidence behind the decision (what makes it fully automatable, why it cannot be, or what is missing).
- Suggested Framework: Playwright / Selenium / Cypress / WebdriverIO / Appium / n/a

---

# Tags

Guidance for the **Tags** column — also carries any test-type/classification nuance not captured by Test Case Type (e.g. Database, Regression, Smoke, Sanity, Responsive, Cross-Browser):

- Smoke
- Regression
- Critical Path
- Responsive
- Cross-Browser
- Mobile
- Database

---

# Output Quality Standards

Every generated test case must be:

- Requirement Traceable
- Business Focused
- Reusable
- Independent
- Non-duplicative
- Automation Friendly
- Review Ready
- Production Ready