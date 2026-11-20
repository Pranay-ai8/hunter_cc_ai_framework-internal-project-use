# Change Impact Analysis Prompt

## Objective

Generate a comprehensive Change Impact Analysis Report by comparing the latest project requirements against the previously approved requirements and identifying all downstream impacts across the AI QA Framework.

The objective is to identify requirement changes, assess their impact, recommend regeneration activities, and maintain complete requirement traceability.

---

# Input Sources

## Primary Inputs

Use the latest project documents available under:

- BRD
- PRD
- FRD
- User Stories
- Business Rules
- API Documentation
- Figma Designs
- UI Screenshots
- Architecture Documents
- Database Documents
- Release Notes

---

## Existing Outputs

Consume existing framework outputs when available:

- RequirementAnalysis.md
- GapAnalysis.md
- TestPlan.md
- FunctionalTestCases.md / .xlsx
- APITestCases.md / .xlsx
- UITestCases.md / .xlsx
- SecurityTestCases.md / .xlsx
- AccessibilityTestCases.md / .xlsx
- RTM.md / .xlsx
- AutomationFramework.md
- Automation Scripts
- Execution Reports

---

# Analysis Scope

Perform a complete comparison between the previous project baseline and the latest requirements.

Identify:

- New Requirements
- Modified Requirements
- Deleted Requirements
- Clarified Requirements
- Updated Business Rules
- Workflow Changes
- UI Changes
- API Changes
- Database Changes
- Integration Changes
- Non-Functional Requirement Changes

---

# Change Categories

Categorize every change into one of the following:

- Functional
- UI
- API
- Business Rule
- Security
- Accessibility
- Performance
- Database
- Integration
- Configuration
- Documentation

---

# Dependency Analysis

Identify downstream impact on:

- Requirement Analysis
- Gap Analysis
- Test Plan
- Functional Test Cases
- API Test Cases
- UI Test Cases
- Security Test Cases
- Accessibility Test Cases
- RTM
- Automation Framework
- Automation Scripts
- Page Objects
- Test Data
- Execution Suites
- Reports

---

# Traceability

Maintain complete traceability between:

Requirement

↓

Business Rule

↓

Workflow

↓

Test Plan

↓

Test Cases

↓

Automation

↓

Execution

↓

Reporting

Every impacted requirement shall identify the affected downstream artifacts.

---

# Impact Assessment

Determine:

- Business Impact
- Technical Impact
- Testing Impact
- Automation Impact
- Execution Impact
- Release Impact

Assign impact severity:

- Low
- Medium
- High
- Critical

---

# Risk Assessment

Identify:

- Business Risks
- Technical Risks
- Testing Risks
- Delivery Risks

Recommend mitigation for each identified risk.

---

# Regeneration Strategy

Determine whether each artifact should be:

- Regenerated
- Updated
- Left Unchanged

Artifacts include:

- Requirement Analysis
- Gap Analysis
- Test Plan
- Functional Test Cases
- API Test Cases
- UI Test Cases
- Security Test Cases
- Accessibility Test Cases
- RTM
- Automation Framework
- Automation Scripts

---

# Recommendations

Provide recommendations for:

- Requirement improvements
- Requirement clarification
- Additional testing
- Regression scope
- Automation updates
- Documentation updates
- Release readiness

---

# Deliverable

Generate the report using:

02-templates/ImpactAnalysis.md

---

# Output

Save the generated report as:

07-outputs/ImpactAnalysis.md

Additionally generate:

- ImpactAnalysis.xlsx

The report shall be suitable for Business Analysts, Product Owners, Developers, QA Leads, Automation Engineers, and Project Managers.

---

# Output Quality

The generated report must be:

- Requirement Traceable
- Risk Based
- Actionable
- Standards Compliant
- Review Ready
- Enterprise Grade
- Suitable for downstream AI agents