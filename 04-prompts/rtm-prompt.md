# Requirements Traceability Matrix (RTM) Prompt

## Objective

Generate a comprehensive Requirements Traceability Matrix (RTM) by establishing end-to-end traceability between business requirements, test planning, test design, automation, execution, and reporting.

The RTM shall serve as the single source of truth for requirement coverage throughout the QA lifecycle.

---

# Input Sources

## Primary Inputs

Consume the following project documents:

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

---

## Existing Framework Outputs

Consume existing outputs whenever available:

- RequirementAnalysis.md
- GapAnalysis.md
- TestPlan.md
- FunctionalTestCases.md / .xlsx
- APITestCases.md / .xlsx
- UITestCases.md / .xlsx
- SecurityTestCases.md / .xlsx
- AccessibilityTestCases.md / .xlsx
- AutomationFramework.md
- Automation Scripts
- Execution Reports
- Defect Reports

---

# Analysis Scope

Generate complete traceability for:

- Requirements
- Business Rules
- User Stories
- Functional Test Cases
- API Test Cases
- UI Test Cases
- Security Test Cases
- Accessibility Test Cases
- Automation Coverage
- Execution Coverage
- Defect Traceability

---

# Traceability Rules

Every requirement shall be mapped to:

Requirement

↓

Business Rule

↓

User Story

↓

Test Plan

↓

Functional Test Case

↓

API Test Case (where applicable)

↓

UI Test Case (where applicable)

↓

Security Test Case (where applicable)

↓

Accessibility Test Case (where applicable)

↓

Automation Script

↓

Execution Result

↓

Defect

↓

Release Readiness

---

# Coverage Analysis

Identify:

- Covered Requirements
- Partially Covered Requirements
- Uncovered Requirements
- Duplicate Test Coverage
- Missing Traceability
- Missing Automation
- Missing Execution

---

# Automation Mapping

Identify:

- Automation Candidate
- Automation Status
- Framework
- Script Name
- Execution Suite

---

# Risk Analysis

Highlight:

- Missing Coverage
- High Risk Requirements
- Missing Automation
- Missing Regression Coverage
- Missing Security Validation
- Missing Accessibility Validation

---

# Deliverables

Generate:

- RTM.md
- RTM.xlsx

Use:

02-templates/RTM.md

Follow:

03-standards/rtm-standards.md

---

# Output

Save the generated RTM into:

07-outputs/

---

# Output Quality

The generated RTM shall be:

- Requirement Traceable
- Standards Compliant
- Business Focused
- Automation Ready
- Review Ready
- Audit Ready
- Enterprise Grade