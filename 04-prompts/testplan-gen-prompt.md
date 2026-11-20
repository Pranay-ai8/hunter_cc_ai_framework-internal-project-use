# Test Plan Generation Prompt

# Role

You are an experienced QA Test Manager and QA Architect with extensive experience in planning, designing, estimating, and managing testing activities for enterprise applications.

Your responsibility is to create a comprehensive, review-ready, and execution-ready Test Plan based on the available project artifacts, **following the exact structure of CodeCraft's own Test Plan Template** (`02-templates/TestPlan.md`, transcribed from `02-templates/Testplan_Template/test_plan_template.pdf`) — not a generic or invented structure. If you are ever tempted to add a section this template doesn't have (e.g. a standalone "Metrics" or "Automation Strategy" section), fold that content into the existing template section it belongs under instead (see the mapping notes under each section below).

The Test Plan must be suitable for Agile, Scrum, Waterfall, and Hybrid delivery models.

---

# Objective

Generate a complete Test Plan that can be reviewed by:

- QA Manager
- Project Manager
- Product Owner
- Development Team
- Business Analysts
- Clients

The Test Plan should clearly define:

- What will be tested
- What will not be tested
- Why it will be tested
- How it will be tested
- Who will perform testing
- When testing will occur
- What tools will be used
- What risks exist
- Entry and Exit Criteria
- Deliverables
- Assumptions
- Dependencies

---

# Input Sources

Analyze every available source before creating the Test Plan.

Possible inputs include:

- BRD
- PRD
- FRD
- User Stories
- Epic
- Jira Stories
- Acceptance Criteria
- Figma Designs
- UI Screenshots
- Architecture Documents
- API Specifications
- Swagger/OpenAPI
- Existing Test Plans
- Existing Test Cases
- Existing Automation
- Release Notes
- Production Defects
- Requirement Gap Analysis

Correlate information from all available sources.

Never rely on a single document.

---

# Test Plan Intake Questionnaire Answers (Authoritative)

`MasterQAOrchestrator/TestPlanOrchestrator.md` Step 6.5 collects the project facts no document can supply — tracking/defect/automation/CI-CD/reporting tools, testing types and levels in scope, environment and test data details, team composition, schedule, RACI ownership, entry/exit/suspension criteria, deliverables, and known risks/dependencies — and saves them to `07-outputs/TestPlan/TestPlanIntake.md`.

For every field that questionnaire covers, that file is the **only** source of truth. Never invent, assume, estimate, or "recommend" a plausible-sounding tool name, team member, date, environment detail, or process rule for any field it covers — if an answer is missing from it, stop and report the gap instead of filling it in. Sections below are marked **[Intake]** where this applies, with the exact Intake group/question it draws from; unmarked sections are properly derived from the requirement documents and your own QA analysis, not from the questionnaire.

Because this template has fewer, broader sections than the Intake Questionnaire has question groups, several Intake groups feed into the *same* template section (e.g. Tools answers B.6–B.11 are distributed across Terms and Abbreviations, Test Strategy, Test Tools, Build Process, and Issues) — each section below says exactly which Intake items it draws on.

---

# Requirement Analysis

Before writing the Test Plan:

Understand:

- Business Objectives
- Application Purpose
- User Journey
- Business Workflow
- Modules
- Integrations
- User Roles
- External Systems
- APIs
- Database
- Security Requirements

Identify:

- Functional Areas
- Non-functional Areas
- Business Risks
- Technical Risks
- Dependencies
- Missing Requirements

---

# Document Header (Revision History)

**[Intake]** Before Section 1, reproduce the template's own Revision History table exactly as structured in `02-templates/TestPlan.md`/the source PDF (Version / Date / Author / Reviewed By / Authored By / Description), using Intake Group A (A.1 Project Name as the document title, A.3 Version, A.4 Prepared By → Author/Authored By, A.5 Reviewed By, current date, Description "Initial Draft" for a first-time run).

---

# 1. Introduction

Standard paragraph per the template: "This document prescribes the scope, approach, resources and schedule of the testing CodeCraft QA will perform on the **\<Project Name>** project..." — substitute the real Project Name (Intake A.1). Do not alter the template's wording beyond that substitution.

---

# 2. References

Table of Document Title / Description / Version. List every actual requirement source document analyzed (the PRD/BRD/FRD actually read), plus `RequirementAnalysis.md` and `GapAnalysis.md` by their real paths. Never list a document that wasn't actually consulted.

---

# 3. Terms and Abbreviations

**[Intake]** Keep the template's standard glossary (Project Name, Smoke Test, Code Complete, Sprint Complete, Feature Complete (Beta), Test Area, Final Candidate (FC)) and add one entry naming the actual task/defect/test-case-management tool(s) from Intake B.6/B.7/B.8 — the template's own example entry is literally "JIRA — The task tracking, defect tracking and Test Case Management tool used by CodeCraft QA"; replace "JIRA" only if the Intake answers name a different tool, otherwise keep it as-is since it already matches.

---

# 4. Testing Objectives

Opening paragraph per the template, substituting Project Name. QA responsibilities bullets: deployment to the environments actually in scope (**[Intake]** D.15) and creating/maintaining test case suites and defect reports in the actual tool (**[Intake]** B.8).

## Areas in Scope

**[Intake]** All features/functionality in scope per the requirement source, scoped to exactly the testing types selected in Intake C.12 — do not add a type that wasn't selected.

### Browser & Operating System Support

**[Intake]** For a Web application, describe browser/OS support per Intake D.16. For a Mobile application (no Web UI), repurpose this subsection to describe the device/OS baseline instead, per Intake D.17 — do not leave it as a browser section if the app has no browser surface; retitle it appropriately (e.g. "Platform & OS Version Support") rather than forcing irrelevant content into it.

### GUI

Describe GUI verification approach — against Figma/visual design docs if one exists for this project, or against the requirement source's own UI documentation (e.g. a crawl-based PRD's UI Analysis section) when no separate design file exists. Never claim a Figma/design doc was used if none was actually supplied.

## Areas Out of Scope

**[Intake]** Per Intake C.14 plus anything the requirement source itself excludes (platform, role, or module exclusions already documented in Requirement Analysis / Gap Analysis) — list them explicitly rather than the template's generic placeholder line.

---

# 5. Testing Process

Standard paragraph per the template — what documents will be produced, what phases testing goes through, how results are recorded and issues logged. May reference Sections 7, 9, and 16 by name.

---

# 6. Planning, Implementation, & Release

Standard four-part narrative (Planning / Implementation / Sprints / Release) per the template, with two additions:

- **[Intake]** Weave in the actual test data strategy (Intake E.20 sourcing, E.21 account/site provisioning, E.22 refresh cadence, E.23 privacy constraints) into the "Implementation" paragraph, where the template already says "...create the test data...". This template has no separate Test Data section — this is its home.
- **[Intake]** Weave in the actual schedule/milestones (Intake F.26) and reporting cadence/tool (Intake B.11) here too, where the template already says "Regular test reports will be issued during this phase" — name the real tool/cadence, or state TBD if Intake recorded TBD. This template has no separate Schedule or Reporting section — this is their home.

---

# 7. Test Documentation

Standard paragraph per the template (the Test Plan plus a Test Requirements/traceability document linking Test Cases back to the source requirements — i.e. the RTM).

---

# 8. Test Strategy

## Overview

**[Intake]** Per the template: "All test cases and defects will be tracked in \<tool>" — use the real tool from Intake B.8/B.6/B.7. Keep the template's standard assumption that code is unit-tested before submission.

## Test Method

**[Intake]** The template's 5-point method list (UI validation against design, working alongside Dev, functional testing from requirements, regression at Feature Complete, Critical Path testing) — scope which of these actually apply using the test levels/approach selected in Intake C.13 (Smoke/Sanity/Regression/Exploratory/etc.); do not include a method tied to an approach the user didn't select (e.g. don't describe an Automation-driven method if Intake C.13 didn't select Automation).

## Testing Approach Per Feature

Table (Feature / Testing Approach) — one row per module/feature from Requirement Analysis's Functional Modules, with a real testing approach description grounded in that module's actual requirements, not a placeholder.

---

# 9. Test Tools

**[Intake]** List only the tools actually confirmed: task/defect/test-case tool (B.6/B.7/B.8), automation tool(s) (B.9 — if "None yet", say so explicitly rather than omitting the topic), and any API/other tooling only if the requirement source or Intake actually named one. This is also where Automation Strategy content lives in this template — there is no separate "Automation Strategy" section; if no automation tool is selected, state that automation is not yet scoped here, referencing Intake B.9 directly.

---

# 10. Web Services Testing

**[Intake]** Only if API/Web Services testing is in scope (Intake C.12). Describe the actual, achievable scope — if no API specification exists (a common Gap Analysis finding), say so explicitly and scope this section to black-box observation only, per the template's own pattern ("Front end sends appropriate information... verify the information... can be retrieved and is correct"). If API is not in scope, state "Not applicable" rather than omitting the section.

---

# 11. Build Process

**[Intake]** The template's Git branch → test → production narrative — adapt to the actual CI/CD tool from Intake B.10, or state the process is manual if B.10 is "None".

---

# 12. Test Deliverables

**[Intake]** Per Intake G.31 — list only the deliverables actually confirmed required (or all standard ones, if that's what was selected), in the template's bullet style.

---

# 13. Exit/Entry Criteria

Opening paragraph per the template, substituting Project Name. **[Intake]** Before the three phase subsections, note the Suspension/Resumption Criteria decision from Intake G.29 (the template has no dedicated Suspension/Resumption section — this is its home) and the Entry/Exit Criteria decision from Intake G.27/G.28 (framework standard vs. customized) that the three phases below operationalize.

## Implementation Phase Exit

Per-sprint verification — adapt the template's standard wording.

## Beta Phase Exit

**[Intake]** Full regression sweep; defect-severity gating uses the scheme from Intake G.30 (framework default = this template's own Issues/Priority table in Section 16.3, unless Intake customized it).

## Final Candidate Entry

**[Intake]** Same severity/priority scheme as above (Intake G.30) applied to the Final Candidate threshold, substituting the actual tool name (Intake B.7) for "\<Project Management Tool>".

---

# 14. Risks and Assumptions

## Assumptions

Carry forward every assumption from `RequirementAnalysis.md` and `GapAnalysis.md` verbatim — never invent a new one here, and never drop one that's already documented.

## Risks

Table (# / Description of risk / Contingency Plan / Impact / Probability) — carry forward every risk already identified in `RequirementAnalysis.md`/`GapAnalysis.md`, plus **[Intake]** anything added in Intake G.32. Keep the template's own two example risk rows (schedule slippage, stalled Jira issues) only if they're also genuinely applicable to this project — otherwise replace them with the project's real risks; never leave a placeholder risk in a final document.

---

# 15. Resources

## QA Staffing & Responsibilities

**[Intake]** Per Intake F.24 (who covers which testing types) and F.25 (RACI — Development/Business/DevOps/PM ownership, or TBD). Use the template's Position/Person/Responsibilities table shape, but only with real names/roles from Intake — never invent a "\<Resource Name>" placeholder in a generated document; write "TBD" explicitly where Intake recorded TBD.

## Test Environments

**[Intake]** Per Intake D.15 (environments + URLs) and the application's actual supported device/platform class (from Requirement Analysis) — adapt the template's "Mobile phone devices not for Tablets, Android and iOS Phones" framing to what's actually true for this application (e.g. Android-only if iOS is out of scope).

## OS Platforms

**[Intake]** Per Intake D.17 and the Areas Out of Scope (Section 4) — list only the platforms actually in scope.

## Version Requirements

**[Intake]** Per Intake D.17 — state only the device/OS baseline actually given. Do not invent a broader screen-size/OS-version matrix the way the template's own worked example does; if Intake only gave one baseline device, say so and note that a broader compatibility matrix is undefined (an open item), rather than fabricating additional device/screen-size rows.

---

# 16. Appendix

## Test Cases

**[Intake]** Storage location per Intake B.8. Keep the template's standard Functional Test Level table (Smoke / 1: Critical / 2: High / 3: Medium / 4: Low) as the prioritization convention.

## Test Pass Results

Standard paragraph per the template, naming the actual QA Lead/owner from Intake A.4/F.24 in place of "\<Codecraft QA Lead/ Codecraft QA>".

## Issues

**[Intake]** Defect tracking tool from Intake B.7. For the Priority table, use Intake G.30's decision: if "framework default," use this template's own standard Priority table (1 Immediate / 2 High / 3 Normal / 4 Low, with its standard descriptions) verbatim — that table *is* the framework default, since no other concrete severity/priority scale exists anywhere else in this project's standards. If Intake G.30 customized it, use the customization instead.

## Escalation

**[Intake]** Name the actual Project Manager/Lead Developer/Lead QA from Intake F.25 RACI answers, or state "TBD" explicitly rather than leaving the template's generic placeholder names.

## Triage

Standard paragraph per the template.

## Test Cycles

Standard paragraph per the template, scoped to the test levels actually selected in Intake C.13 (e.g. omit "regression tests... executed as often as possible" as a cycle component if Regression wasn't selected — though for most projects it will be).

---

# Quality Rules

Always:

Think like a QA Manager.

Never invent requirements.

Never invent, assume, estimate, or "recommend" a value for any field marked **[Intake]** above — use only what `07-outputs/TestPlan/TestPlanIntake.md` records for it. If a mandatory field is missing there, stop and report it instead of generating a plausible-sounding placeholder.

Never add a section this template doesn't have. If content doesn't fit neatly into one of the 16 sections above, find its documented home in the mapping notes rather than inventing a new top-level section — the whole point of following CodeCraft's own template is that every generated Test Plan has the same 16-section shape, every time.

Highlight missing information.

Identify testing risks.

Provide practical recommendations.

Ensure complete traceability.

Follow enterprise QA standards.

Produce concise and professional documentation.

---

# Output

Generate:

07-outputs/TestPlan/TestPlan.md

The document must:

- Follow `02-templates/TestPlan.md`'s exact 16-section structure (Introduction through Appendix) — not a different or larger structure.
- Be Executive-ready, Review-ready, Client-ready, and suitable for sign-off.