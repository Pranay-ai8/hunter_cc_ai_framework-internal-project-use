# RTM Evaluator Prompt

## Objective

Evaluate a previously generated Requirements Traceability Matrix (RTM) for completeness, traceability integrity, and standards compliance — and produce a scored Evaluation Report.

This is an evaluation activity, not a generation activity. Do not regenerate the RTM; assess what already exists.

---

# Input Sources

## Primary Inputs

- Generated RTM (.md / .xlsx) under evaluation
- Requirement Summary
- Business Workflow
- Requirement Gap Report

## Existing Framework Outputs

- TestPlan.md
- FunctionalTestCases.md / .xlsx
- APITestCases.md / .xlsx
- UITestCases.md / .xlsx
- SecurityTestCases.md / .xlsx
- AccessibilityTestCases.md / .xlsx
- Automation Scripts / Execution Reports (if available)

---

# Evaluation Scope

Evaluate the RTM against:

- 03-standards/rtm-standards.md
- 09-evaluators/eval_standards/evaluation-standards.md
- 02-templates/RTM.md

---

# Evaluation Dimensions

Score each dimension:

- Requirement Coverage (every requirement mapped end-to-end)
- Test Case Linkage (no orphan requirements or orphan test cases)
- Automation Traceability (where applicable)
- Execution & Defect Traceability (where applicable)
- Consistency (IDs match across requirement, test case, and automation artifacts)
- Structural Compliance (RTM template columns present and complete)
- Change Impact Reflection (if this RTM follows a change impact update)

---

# Findings to Detect

Identify and classify by severity (Critical / Major / Minor / Observation):

- Uncovered or partially covered requirements
- Orphan test cases (no requirement reference)
- Orphan requirements (no test case reference)
- Duplicate mappings
- Inconsistent or mismatched IDs across artifacts
- Missing automation status where automation exists
- Stale traceability after a known requirement change

---

# Deliverables

Generate an RTM Evaluation Report containing:

- Overall Score (/100) and Score Band
- Score Breakdown by Dimension
- Findings Table (Requirement ID / Test Case ID, Severity, Description)
- Coverage Metrics (Requirement %, Automation %, Execution %)
- Recommendations
- Approval Status (Approved / Approved with Comments / Rejected)

---

# Output

Save the generated report into:

09-evaluators/eval_outputs/RTMEvaluation.md 09-evaluators/eval_outputs/RTMEvaluation.xlsx

---

# Output Quality

The evaluation report shall be:

- Evidence Based
- Standards Referenced
- Objective
- Actionable
- Review Ready
