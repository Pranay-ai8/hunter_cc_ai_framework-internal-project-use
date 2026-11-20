# Naming Conventions Standard

## Objective

Establish a consistent naming convention across all QA artifacts to improve readability, traceability, maintainability, and automation readiness.

These standards apply to all AI-generated artifacts within the QA Framework.

---

# General Naming Principles

Always:

- Use meaningful names.
- Keep names concise and descriptive.
- Use business terminology.
- Maintain consistency across all artifacts.
- Avoid abbreviations unless widely accepted.
- Use English for all artifact names.

---

# Test Case ID

Format:

TC_<Module>_<Feature>_<Sequence>

Examples:

TC_LOGIN_001

TC_CHECKOUT_015

TC_PROFILE_028

---

# Requirement ID

Use the original Requirement ID wherever available.

Examples:

REQ-101

BRD-205

PRD-015

US-456

---

# Test Scenario Name

Format:

<Module> - <Feature> - <Scenario>

Examples:

Login - Valid Login

Checkout - Apply Discount Coupon

Profile - Change Password

---

# Test Case Title

Use action-oriented names.

Examples:

Verify successful login with valid credentials.

Verify user cannot login with an invalid password.

Verify password reset email is sent successfully.

Avoid:

Login Test

Password Test

Scenario 1

---

# Module Naming

Use business module names.

Examples:

Authentication

Dashboard

Checkout

Orders

Inventory

Payments

Reports

Settings

---

# Feature Naming

Use functional feature names.

Examples:

Login

Registration

Forgot Password

Search Products

Create Order

Cancel Booking

---

# Test Data Naming

Format:

<Module>_<Purpose>

Examples:

Login_ValidUser

Login_InvalidPassword

Checkout_ValidCard

Checkout_ExpiredCard

---

# Environment Naming

Allowed values:

DEV

QA

SIT

UAT

STAGE

PROD

---

# Priority Naming

Allowed values:

P1 - Critical

P2 - High

P3 - Medium

P4 - Low

---

# Severity Naming

Allowed values:

Critical

High

Medium

Low

---

# Automation Candidate

Allowed values:

YES

NO

TBD

`Partial` (or any equivalent partial-automation value) is never a valid Automation Candidate value anywhere in the framework. See `02-templates/TestCase.md`'s "Automation Candidate Classification" for the decision rule.

---

# Test Type Naming

Allowed values:

Functional

UI

API

Regression

Smoke

Sanity

Integration

System

End-to-End

Security

Accessibility

Performance

Compatibility

Database

---

# Requirement Traceability

Every test case must reference:

- Requirement ID
- User Story ID (if applicable)
- Module
- Feature

---

# File Naming

Use PascalCase.

Examples:

RequirementAnalysis.md

GapAnalysis.md

TestPlan.md

FunctionalTestCases.xlsx

APITestCases.xlsx

AutomationFramework.md

AutomationScript.md

ExecutionReport.md

DefectReport.md

---

# Folder Naming

Use lowercase with hyphens.

Examples:

01-docs

02-templates

03-standards

04-prompts

05-agents

07-outputs

---

# Sheet Naming (Excel)

Examples:

Functional Test Cases

API Test Cases

UI Test Cases

Security Test Cases

Accessibility Test Cases

RTM

---

# Defect Naming

Format:

<Module> - <Issue Summary>

Examples:

Login - Invalid Password Validation Missing

Checkout - Order Created Twice

Profile - Save Button Disabled

---

# Automation Script Naming

Format:

<Feature>.spec.ts

Examples:

login.spec.ts

checkout.spec.ts

payment.spec.ts

---

# Page Object Naming

Format:

<PageName>Page

Examples:

LoginPage

DashboardPage

CheckoutPage

OrderSummaryPage

---

# API Test Naming

Format:

<HTTP Method>_<Resource>_<Scenario>

Examples:

POST_Login_ValidCredentials

GET_Orders_ValidUser

DELETE_User_AdminAccess

---

# Output File Naming

Examples:

RequirementAnalysis.md

GapAnalysis.md

TestPlan.md

FunctionalTestCases.md

UITestCases.md

APITestCases.md

SecurityTestCases.md

AccessibilityTestCases.md

AutomationFramework.md

AutomationScripts.zip

ExecutionReport.md

FinalSummary.md

---

# Naming Quality Rules

Ensure every generated name is:

- Unique
- Meaningful
- Business-focused
- Consistent
- Traceable
- Easy to understand
- Automation-friendly

Avoid:

- Test1
- Sample
- Temp
- New Test
- Scenario A
- Untitled

---

# Compliance

All AI-generated artifacts must comply with these naming standards before being saved to the framework outputs.