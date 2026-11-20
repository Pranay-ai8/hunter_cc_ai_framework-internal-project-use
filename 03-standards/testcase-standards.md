# Test Case Standards

## Objective

Define the organization standard for creating high-quality, reusable, business-focused, and automation-ready test cases.

---

# General Guidelines

Every test case must:

- Validate a single objective.
- Be independent.
- Be reusable.
- Be easy to understand.
- Be executable by any tester.
- Be traceable to one or more requirements.

---

# Mandatory Fields

Every test case shall include exactly these 17 columns (see `02-templates/TestCase.md`) — no more, no fewer:

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
- Automation Candidate (`YES`/`NO`/`TBD` — `Partial` is not a valid value; see `02-templates/TestCase.md`'s "Automation Candidate Classification")
- Automation Candidate Rationale
- Suggested Framework
- Tags

Epic ID and User Story ID are not separate columns — when relevant, record them as part of the Requirement ID value (e.g. the Jira key) rather than as additional columns.

When Generic Test Case generation is enabled (`FUNCTIONAL_AND_GENERIC` mode), a Generic Test Case additionally carries 5 conditional columns: Generic Test Case ID, Generic Test Case Source Worksheet, Generic Test Case Source Reference, Generic Applicability Decision, Generic Applicability Rationale.

---

# Naming Standard

Use:

<Module> - <Feature> - <Scenario>

Example:

Login - Valid Login

Checkout - Invalid Card

Profile - Change Password

---

# Writing Standards

Use:

- Clear language
- Action-oriented steps
- Business terminology
- Numbered steps
- Measurable expected results

Avoid:

- Ambiguous wording
- Technical implementation details
- Multiple validations in one step

---

# Test Step Standards

Each step should:

- Perform one action.
- Be independent.
- Be repeatable.
- Be executable.

---

# Expected Result Standards

Expected results should:

- Be observable.
- Be measurable.
- Describe business behavior.
- Never be vague.

Good Example:

User is redirected to Dashboard page.

Poor Example:

System works correctly.

---

# Test Data Standards

Include:

- Valid Data
- Invalid Data
- Boundary Data
- Null Values
- Blank Values
- Special Characters
- Duplicate Data

---

# Coverage Standards

Every feature must include:

- Positive Scenario
- Negative Scenario
- Boundary Scenario
- Alternate Flow
- Exception Flow
- Role-Based Scenario

---

# Edge Case Standards

Always consider:

- Null
- Empty
- Blank
- Maximum Length
- Minimum Length
- Invalid Format
- Unicode
- Emoji
- Concurrent Users
- Session Timeout
- Network Failure

---

# Priority Standards

P1 – Business Critical

P2 – High

P3 – Medium

P4 – Low

---

# Severity Standards

Critical

High

Medium

Low

---

# Automation Standards

For every test case specify:

- Automation Candidate (`YES`/`NO`/`TBD` — see `02-templates/TestCase.md`'s "Automation Candidate Classification")
- Automation Candidate Rationale
- Automation Priority
- Suggested Framework
- Complexity

---

# Traceability Standards

Every test case must map to:

Requirement

↓

Feature

↓

Test Case

↓

Automation Script

No orphan test cases.

---

# Duplication Standards

Avoid:

- Duplicate scenarios
- Repeated validations
- Overlapping objectives

Reuse common scenarios where possible.

---

# Review Checklist

Before finalizing:

✓ Requirement covered

✓ Business rule validated

✓ Test data included

✓ Preconditions defined

✓ Expected result measurable

✓ Automation feasibility identified

✓ No duplicate coverage

---

# Quality Standards

Every generated test case must be:

- Business Focused
- Requirement Traceable
- Easy to Execute
- Easy to Automate
- Independent
- Maintainable
- Review Ready
- Production Ready