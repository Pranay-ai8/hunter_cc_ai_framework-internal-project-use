# Test Case Evaluator Standards

## Objective

Establish enterprise standards for evaluating AI-generated Functional Test Cases to ensure they are complete, accurate, traceable, standards-compliant, and production-ready before they are consumed by downstream framework components.

The Test Case Evaluator shall validate the quality and completeness of generated test cases. It shall not generate or modify test cases.

---

# Purpose

The evaluator shall verify:

- Requirement Coverage
- Business Rule Coverage
- Workflow Coverage
- Scenario Completeness
- Test Design Quality
- Traceability
- Test Case Quality
- Standards Compliance
- Automation Readiness

---

# Evaluation Inputs

The evaluator shall consume:

- RequirementAnalysis.md
- GapAnalysis.md
- TestPlan.md
- FunctionalTestCases.md
- TestCase.md (Template)
- testing-standards.md
- testcase-standards.md

---

# Evaluation Outputs

Generate:

- TestCoverageReport.md
- TestCoverageReport.xlsx
- MissingScenarios.md
- MissingScenarios.xlsx

---

# Evaluation Categories

The evaluator shall assess the following categories.

---

# 1. Requirement Coverage

Verify every approved requirement has at least one mapped functional test case.

Validation Rules

- Every Requirement ID shall be covered.
- No orphan requirements.
- No duplicate mappings.
- Critical requirements shall have multiple scenarios.

Output

- Requirement Coverage %

---

# 2. Business Rule Coverage

Verify every documented business rule has corresponding validation.

Examples

- Limits
- Validations
- Calculations
- Conditional logic
- Approval flows

Output

- Business Rule Coverage %

---

# 3. Positive Scenario Coverage

Verify positive workflows exist for every feature.

Examples

- Successful Login
- Successful Registration
- Successful Payment
- Successful Search

Output

- Positive Scenario Coverage %

---

# 4. Negative Scenario Coverage

Verify negative scenarios exist.

Examples

- Invalid Inputs
- Missing Inputs
- Duplicate Data
- Invalid Credentials
- Unauthorized Access
- Invalid Formats

Output

- Negative Scenario Coverage %

---

# 5. Boundary Coverage

Verify Boundary Value Analysis has been applied.

Examples

- Minimum values
- Maximum values
- Below minimum
- Above maximum
- Empty values
- Maximum length
- Minimum length

Output

- Boundary Coverage %

---

# 6. Alternate Flow Coverage

Verify alternate business flows.

Examples

- Cancel
- Retry
- Resend OTP
- Edit
- Skip
- Save Draft

Output

- Alternate Flow Coverage %

---

# 7. Exception Coverage

Verify exception handling.

Examples

- Network Failure
- Timeout
- Server Error
- Database Error
- Session Expiry

Output

- Exception Coverage %

---

# 8. Validation Coverage

Verify field validations.

Check:

- Mandatory Fields
- Length Validation
- Format Validation
- Character Validation
- Date Validation
- Numeric Validation

Output

- Validation Coverage %

---

# 9. Workflow Coverage

Ensure complete business workflow coverage.

Examples

Registration

↓

Login

↓

Dashboard

↓

Transaction

↓

Logout

Partial workflows shall be reported.

Output

- Workflow Coverage %

---

# 10. Decision Coverage

Verify decision logic.

Examples

IF

ELSE

Nested Conditions

Approval Decisions

Output

- Decision Coverage %

---

# 11. Error Handling Coverage

Verify system behaviour during failures.

Examples

- Invalid Request
- Server Failure
- Service Unavailable
- Timeout
- Retry Logic

Output

- Error Handling Coverage %

---

# 12. Test Design Techniques

Verify appropriate test design techniques have been applied.

Required Techniques

- Equivalence Partitioning
- Boundary Value Analysis
- Decision Table Testing
- State Transition Testing
- Error Guessing
- Use Case Testing
- Risk-Based Testing

Report missing techniques.

---

# 13. Traceability

Verify traceability between:

Requirement

↓

Test Plan

↓

Functional Test Cases

↓

RTM

Every test case shall map back to a requirement.

---

# 14. Test Case Quality

Verify every test case contains exactly the 17 standard columns (see `02-templates/TestCase.md`):

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
- Automation Candidate
- Automation Candidate Rationale
- Suggested Framework
- Tags

When the test case's Test Case Type is `GENERIC`, also verify the 5 conditional columns are present: Generic Test Case ID, Generic Test Case Source Worksheet, Generic Test Case Source Reference, Generic Applicability Decision, Generic Applicability Rationale. A `FUNCTIONAL`/`UI`/`API`/`SECURITY`/`ACCESSIBILITY`/`PERFORMANCE` test case should NOT carry these 5 columns.

Reject incomplete test cases, and reject test cases carrying columns outside this standard set.

## 14a. Automation Candidate Compliance

- Reject any test case whose Automation Candidate value is anything other than `YES`, `NO`, or `TBD` — `Partial`/`PARTIAL` (or any equivalent partial-automation value) is a hard rejection, not a warning.
- Reject any test case with a non-`TBD` Automation Candidate but a missing/empty or non-evidential Automation Candidate Rationale (e.g. "standard case", "looks automatable").
- Flag `YES` as a finding when the test case's own steps/expected result plainly require manual/subjective judgment or an uncontrollable physical action — that evidence contradicts a `YES` classification.
- Flag `NO` as a finding when the stated rationale is really a missing-information gap (that should be `TBD`, not `NO`).
- Verify every `TBD` test case has a corresponding Human-in-the-Loop question recorded in the source document's Clarifications/Review section.
- Verify no test case with Automation Candidate other than `YES` has been carried into an automation script generation output, unless the source document records an explicit, traceable user override.
- Flag any `YES` test case whose steps depend on an automation-environment/tooling capability (a controllable test mailbox/API access to retrieve a value, a backend bypass endpoint, a device-farm feature, a specific pre-provisioned test account) that is not confirmed anywhere in `GapAnalysis.md`, `TestPlan.md`, or `RequirementAnalysis.md` — that `YES` rests on an unverified assumption and should be `TBD` with the gap logged, per `02-templates/TestCase.md`'s TBD rule.

## 14b. Test Case Type Compliance

- Reject a `UI` test case whose Test Scenario/Expected Result is actually validating a business rule, calculation, authorization outcome, or workflow result rather than presentation — per `03-standards/testing-standards.md`'s "Test Case Type Classification".
- Reject a `FUNCTIONAL` test case whose Expected Result only asserts visual layout/rendering/presentation with no business outcome.
- Flag duplicated coverage of the same scenario under both `FUNCTIONAL` and `UI`.

## 14c. Generic Test Case Compliance

- Verify every `GENERIC` row's Generic Applicability Decision is `APPLICABLE` — a `NOT_APPLICABLE`/`TBD` generic case must not appear as an executable row in `FunctionalTestCases.md`.
- Verify a corresponding `GenericApplicabilityReview.md` exists and accounts for every Generic Test Case evaluated this run (including excluded/`TBD` ones), when `FUNCTIONAL_AND_GENERIC` mode was used.
- Reject a `GENERIC` row whose Preconditions/Test Steps/Expected Result still read as the unadapted library scenario (generic placeholder wording, no application-specific terminology) rather than an adaptation.

## 14d. Cross-Document Content-Duplication Compliance

A Test Case ID-uniqueness check (Section 16) does not catch a duplicate that has a unique ID — this check targets content duplicated under different IDs, which is the likelier failure mode whenever generation was parallelized across subagents and/or across the Functional/Generic/UI documents (see `03-standards/testing-standards.md`'s "Cross-Document Duplicate Resolution").

- Group all rows across Functional, Generic, and UI by shared Requirement ID (or shared screen/module for UI-only clusters); within each group, compare Test Scenario + Expected Result content, not just the Test Case ID.
- Flag a pair as a content duplicate when both assert the same outcome through the same action, even if wording, module grouping, or Test Case Type (Functional vs. UI) differs.
- Specifically check every Functional/UI pair sharing a Requirement ID for the Navigation-category overlap (`03-standards/testing-standards.md`'s Navigation sub-rule): an unconditional navigation check duplicated in both documents is always a finding.
- This is a mandatory check on every evaluation run, not only when the user reports duplicates — report it as a Major finding (resolved or open) rather than silently factoring it into the Duplication score with no finding entry.
- Verify that any content duplicate the generation agents already resolved left an audit trail (a Clarifications/Resolution section entry, and for Generic rows a `NOT_APPLICABLE` reclassification in `GenericApplicabilityReview.md` naming the surviving row) rather than a silent deletion.

---

# 15. Naming Standards

Verify compliance with:

- testcase-standards.md
- naming-conventions-standards.md

---

# 16. Duplicate Detection

Detect:

- Duplicate Test Cases
- Duplicate Validations
- Duplicate Scenarios

Duplicates shall be reported.

---

# 17. Missing Scenario Detection

Identify missing:

- Functional Scenarios
- Validation Scenarios
- Boundary Scenarios
- Negative Scenarios
- Alternate Flows
- Exception Flows
- Business Rules

Generate a Missing Scenario Report.

---

# 18. Risk Assessment

Assign risk based on uncovered functionality.

Risk Levels

- Low
- Medium
- High
- Critical

---

# Coverage Calculation

Calculate the following metrics.

| Metric | Formula |
|---------|---------|
| Requirement Coverage | Covered Requirements / Total Requirements × 100 |
| Business Rule Coverage | Covered Business Rules / Total Business Rules × 100 |
| Positive Coverage | Positive Scenarios / Expected Positive Scenarios × 100 |
| Negative Coverage | Negative Scenarios / Expected Negative Scenarios × 100 |
| Boundary Coverage | Boundary Scenarios / Expected Boundary Scenarios × 100 |
| Workflow Coverage | Covered Workflows / Total Workflows × 100 |

---

# Overall Quality Score

Calculate an overall score.

| Score | Grade |
|---------|-------|
| 95–100 | A+ |
| 90–94 | A |
| 80–89 | B |
| 70–79 | C |
| Below 70 | Improvement Required |

---

# Evaluation Report

The report shall include:

- Overall Quality Score
- Overall Grade
- Requirement Coverage
- Business Rule Coverage
- Positive Coverage
- Negative Coverage
- Boundary Coverage
- Workflow Coverage
- Exception Coverage
- Validation Coverage
- Missing Scenarios
- Risks
- Recommendations

---

# Pass Criteria

Minimum acceptable values.

| Category | Minimum |
|-----------|---------|
| Requirement Coverage | 100% |
| Business Rule Coverage | 95% |
| Positive Coverage | 100% |
| Negative Coverage | 90% |
| Boundary Coverage | 90% |
| Workflow Coverage | 95% |
| Traceability | 100% |
| Overall Quality Score | 90% |

If any mandatory threshold is not met, the evaluator shall recommend additional test design before proceeding.

---

# Recommendations

The evaluator shall provide recommendations for:

- Missing Test Cases
- Missing Business Rules
- Missing Boundary Conditions
- Missing Negative Scenarios
- Missing Workflows
- Missing Validations
- Test Case Improvements

Recommendations shall be actionable.

---

# Compliance

The evaluator shall comply with:

- ISO/IEC/IEEE 29119
- ISTQB Test Design Best Practices
- Organizational QA Standards
- Framework Test Case Standards

---

# Output Quality Standards

Every evaluation report shall be:

- Accurate
- Objective
- Requirement Traceable
- Standards Compliant
- Actionable
- Review Ready
- Enterprise Grade
- Production Ready