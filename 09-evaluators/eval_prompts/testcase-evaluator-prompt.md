# Test Case Evaluator Prompt

## Objective

Evaluate previously generated Test Cases (Functional, UI, API, Boundary, Negative, Database, Accessibility, Security) for completeness, correctness, traceability, and standards compliance — and produce a scored Evaluation Report.

This is an evaluation activity, not a generation activity. Do not regenerate test cases; assess what already exists.

---

# Input Sources

## Primary Inputs

- Generated Test Cases (.md / .xlsx) under evaluation
- Requirement Summary
- Business Workflow
- Requirement Gap Report
- Acceptance Criteria
- Figma Designs / UI Screenshots (where applicable)
- Swagger/OpenAPI Specifications (for API test cases)
- JIRA Stories/Epics/Tickets

## Existing Framework Outputs

- RequirementAnalysis.md
- GapAnalysis.md
- TestPlan.md
- RTM.md (if available, to cross-check traceability)

---

# Evaluation Scope

Evaluate test cases against:

- 03-standards/testcase-standards.md
- 03-standards/naming-conventions-standards.md
- 09-evaluators/eval_standards/evaluation-standards.md

---

# Evaluation Dimensions

Score each dimension:

- Requirement Coverage (are all in-scope requirements and acceptance criteria represented?)
- Correctness (do steps and expected results match the source requirement?)
- Traceability (does every test case reference a requirement/user story ID?)
- Boundary & Negative Coverage
- Naming Convention Compliance
- Structural Compliance (test case ID, preconditions, steps, expected result, priority)
- Clarity & Testability (unambiguous, executable steps)
- Duplication (redundant or overlapping test cases)

---

# Findings to Detect

Identify and classify by severity (Critical / Major / Minor / Observation):

- Missing test cases for stated requirements or acceptance criteria
- Missing negative/boundary/security/accessibility coverage where applicable
- Orphan test cases with no requirement linkage
- Ambiguous steps or expected results
- Naming convention violations
- Duplicate or near-duplicate test cases — including content duplicates that carry different, unique Test Case IDs across Functional/Generic/UI documents (per `09-evaluators/eval_standards/evaluation-standards.md` section 14d); an ID-uniqueness check alone does not satisfy this
- Incorrect expected results relative to source requirements
- Invalid Automation Candidate values (`Partial` or anything other than `YES`/`NO`/`TBD`), missing/non-evidential Automation Candidate Rationale, or a `TBD` with no corresponding HITL question
- Functional test cases misclassified as `UI` (or vice versa) relative to their actual testing objective
- Generic Test Cases whose Generic Applicability Decision is not `APPLICABLE`, or whose content still reads as unadapted library text

---


# Deliverables

Generate a Test Case Evaluation Report containing:

- Overall Score (/100) and Score Band
- Score Breakdown by Dimension
- Findings Table (ID, Severity, Description, Requirement Reference)
- Coverage Gap Summary
- Duplicate/Redundant Test Case List
- Recommendations
- Approval Status (Approved / Approved with Comments / Rejected)

Evaluate the generated Functional Test Cases.

Calculate:

- Overall Quality Score
- Requirement Coverage
- Business Rule Coverage
- Boundary Coverage
- Negative Coverage
- Workflow Coverage
- Exception Coverage

---

# Output

Save the generated report into:

09-evaluators/eval_outputs/TestCoverageReport.md 09-evaluators/eval_outputs/TestCoverageReport.xlsx

---

# Output Quality

The evaluation report shall be:

- Evidence Based
- Standards Referenced
- Objective
- Actionable
- Review Ready
