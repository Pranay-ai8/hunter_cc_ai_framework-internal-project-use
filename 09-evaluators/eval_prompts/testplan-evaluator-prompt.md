# Test Plan Evaluator Prompt

## Objective

Evaluate a previously generated Test Plan for completeness, structure, risk coverage, and standards compliance — and produce a scored Evaluation Report.

This is an evaluation activity, not a generation activity. Do not regenerate the test plan; assess what already exists.

---

# Input Sources

## Primary Inputs

- Generated Test Plan (.md / .pdf) under evaluation
- Requirement Summary
- Business Workflow
- Requirement Gap Report
- Test Strategy (if separately produced)

## Existing Framework Outputs

- RequirementAnalysis.md
- GapAnalysis.md
- RTM.md (if available)

---

# Evaluation Scope

Evaluate the test plan against:

- 03-standards/testplan-standards.md
- 09-evaluators/eval_standards/evaluation-standards.md
- 02-templates/ test plan template structure

---

# Evaluation Dimensions

Score each dimension:

- Scope Completeness (in-scope, out-of-scope clarity and coverage of requirements)
- Test Strategy Adequacy (approach per test type: functional, API, UI, security, accessibility, performance)
- Risk Coverage (risks, assumptions, dependencies identified)
- Resource & Schedule Realism (roles, environments, entry/exit criteria)
- Traceability (linkage to requirements and RTM)
- Structural Compliance (template sections present and complete)
- Clarity (unambiguous, review-ready language)

---

# Findings to Detect

Identify and classify by severity (Critical / Major / Minor / Observation):

- Missing or incomplete scope sections
- Missing entry/exit criteria
- Missing environment or test data strategy
- Missing risk/assumption/dependency sections
- Missing coverage for a required test type (security, accessibility, performance, etc.)
- Inconsistencies between test plan and RTM or requirement documents
- Template section deviations

---

# Deliverables

Generate a Test Plan Evaluation Report containing:

- Overall Score (/100) and Score Band
- Score Breakdown by Dimension
- Findings Table (Section, Severity, Description, Reference)
- Missing Coverage Summary
- Recommendations
- Approval Status (Approved / Approved with Comments / Rejected)

---

# Output

Save the generated report into:

09-evaluators/eval_outputs/TestPlanEvaluation.md 09-evaluators/eval_outputs/TestPlanEvaluation.xlsx

---

# Output Quality

The evaluation report shall be:

- Evidence Based
- Standards Referenced
- Objective
- Actionable
- Review Ready
