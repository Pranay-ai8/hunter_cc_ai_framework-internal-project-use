# Test Plan — IGZ Marquis Hunting App ("RanchOps Pro")

**STATUS: APPROVED (Step 7.5 Human Approval Gate — approved by user on 2026-11-02)**

**Revision Note (2026-11-02):** Authentication mechanism confirmed password-based post-approval; Sections 4, 8, and 14 updated accordingly. This is a factual correction reflecting newly confirmed facts (not a scope change), and does not re-trigger the full approval gate — the document's APPROVED status above is retained.

**Revision Note 2 (2026-11-03):** Role terminology correction — "Admin" (sourced from IMHA-91/IMHA-92) was mistakenly treated as a role distinct from Ranch Admin in Section 8's Authentication & Access row and Super Admin & Ranch Admin Management row. The user confirmed on 2026-11-03 that there are exactly **3 real login roles: Super Admin, Ranch Admin, and Guide** — "Admin" is the same role as Ranch Admin, not a 4th role. Section 8 updated accordingly; this is a factual/terminology correction, not a scope change, and does not re-trigger the full approval gate.

**Revision History**

| **Version** | **Date** | **Author** | **Reviewed By** | **Authored By** | **Description** |
|:---|:---|:---|:---|:---|:---|
| **1.0** | 2026-11-02 | Pranay | Not yet assigned / TBD | Pranay | Initial Draft |

**Table of Contents**

1. Introduction
2. References
3. Terms and Abbreviations
4. Testing Objectives
5. Testing Process
6. Planning, Implementation, & Release
7. Test Documentation
8. Test Strategy
9. Test Tools
10. Web Services Testing
11. Build Process
12. Test Deliverables
13. Exit/Entry Criteria
14. Risks and Assumptions
15. Resources
16. Appendix

---

## 1. Introduction

This document prescribes the scope, approach, resources and schedule of the testing CodeCraft QA will perform on the **IGZ Marquis Hunting App ("RanchOps Pro")** project. This document will identify the items and features to be tested, any testing tasks that must be performed, who is responsible for these tasks and any risks that are associated with this plan.

RanchOps Pro is a multi-tenant SaaS platform for hunting-ranch operators, covering authentication & access, ranch configuration, booking management, guest/hunter directory, staff/guide management, harvest log & compliance, invoicing, waivers/e-signature workflow, dashboard & reporting, and role-based (Guide) visibility restrictions — 46 active Functional Requirements across 12 modules, traced to 54 in-scope JIRA issues (project IMHA). (Revised 2026-11-02: was 44 active; FR-AUTH-001/FR-AUTH-002 confirmed active, password-based mechanism.)

---

## 2. References

| **Document Title** | **Description** | **Version** |
|:---|:---|:---|
| `07-outputs/GeneratedPRD(JIRA)/PRD_IMHA_DRAFT_2026-11-02.md` (approved content; same as the final `.docx`) | Product Requirement Document for RanchOps Pro, derived from 54 in-scope JIRA stories (project IMHA) plus IMHA-71 supporting context | 2026-11-02 |
| `07-outputs/RequirementAnalysis.md` | Requirement Analysis Report — 46 active FRs across 12 modules, business workflows, roles, rules, risks (revised 2026-11-02) | 1.0 |
| `07-outputs/GapAnalysis.md` | Requirement Gap Report — missing FRs, business rules, validations, workflows, NFR gaps, clarification questions | 1.0 |
| `07-outputs/ImpactAnalysis.md` | Change Impact Analysis — confirms this is an initial baseline run (no prior artifacts exist for this project) | 1.0 |
| `07-outputs/TestPlan/TestPlanIntake.md` | Test Plan Intake Questionnaire answers (tools, scope, environment, data, team, criteria, deliverables, risks) | 1.0 |
| JIRA Project IMHA | System of record; 54 in-scope issues (traceability only, not independently re-queried this run) | N/A |

*Table 1.1: References*

---

## 3. Terms and Abbreviations

| **Term or Acronym** | **Description** |
|:---|:---|
| **IGZ Marquis Hunting App ("RanchOps Pro")** | Project name — multi-tenant ranch/hunting-property operations platform. |
| **Smoke Test** | Test of a product build to verify that core functionality is present and testing of the build can proceed. Designed to catch blocking issues and reject builds which are significantly untestable. |
| **Code Complete** | When the product or specific feature area meets all related functional requirements in the Design documents, and is ready for debugging. |
| **Sprint Complete** | When development is complete, and QA has tested all features of that sprint. |
| **Feature Complete (Beta)** | When the development of the product or specific feature area has implemented all functional requirements in the Design documents, no further functionality is yet to be implemented and there is no opened Priority 1 or Severity 1 issues. |
| **Test Area** | Functional groups of requirements related to one area of product functionality. E.g. "Booking Management" would be a Test Area that tests all requirements and use case scenarios related to creating/editing/managing a booking. |
| **Final Candidate (FC)** | A build of the application that QA has certified to meet the contractual obligations of the project. |
| **JIRA** | The task tracking, defect tracking and Test Case Management tool used by CodeCraft QA for task/defect tracking (Intake B.6/B.7). Test Case Management for this project specifically uses **TestLink** (Intake B.8). |
| **Ranch** | A tenant property within the platform, configured and managed by a Ranch Admin (or Super Admin). |
| **Harvest Log** | A compliance record capturing a hunter's harvested animal against the ranch's tag allocation. |
| **Docuseal** | Self-hosted e-signature integration used to dispatch, track, and resend liability waivers. |

---

## 4. Testing Objectives

The objective of Codecraft QA during the **IGZ Marquis Hunting App ("RanchOps Pro")** project is primarily to perform testing of the application flow, such that it can be determined that the requirements as outlined in the requirements (documented as epics and stories in Jira project IMHA, and formalized in `RequirementAnalysis.md`) have been met.

QA is also responsible for the following:

* Deployment of versions tested to the **Dev** environment (the only environment currently in scope — Intake D.15; `https://dev-dashboard.hunter.igoalzero.com/login`).
* Creating and maintaining test case suites and defect reports in **Jira** (B.6/B.7), with the test case repository maintained in **TestLink** (B.8).

### Areas in Scope

All features and functionalities mentioned in the requirements in the form of epics and stories in Jira (project IMHA) of **RanchOps Pro** are considered IN scope for testing, restricted to the testing types the Intake Questionnaire marked in scope: **Functional** and **UI** (Intake C.12). API, Database, Security, Accessibility, and Performance testing are explicitly **not** selected as in-scope testing types for this cycle (see Areas Out of Scope).

Proceeding from the requirements, the list of features deemed to be In Scope for verification in **RanchOps Pro** includes all 12 functional modules and their 46 active Functional Requirements:

1. Authentication & Access (FR-AUTH-001/002/003) — confirmed password-based login, session establishment, and password reset
2. Super Admin & Ranch Admin Management (FR-SA-001/002)
3. Ranch Configuration (FR-RANCH-001/002/003)
4. Booking Management (FR-BOOK-001 through 008)
5. Guest/Hunter Directory (FR-GUEST-001 through 005)
6. Staff & Guide Management (FR-STAFF-001/002/003)
7. Harvest Log & Compliance (FR-HARVEST-001 through 008)
8. Invoicing (FR-INVOICE-001/002/003)
9. Waivers & Document Workflow (FR-WAIVER-001 through 004)
10. Dashboard & Reporting (FR-DASH-001)
11. Guide Portal & Role-Based Visibility (FR-GUIDE-001/002)
12. Platform/Cross-Cutting Technical (FR-PLATFORM-001/002/003) — functional-surface behavior only (pagination), not the underlying SQLite→PostgreSQL migration as an infrastructure activity

Test levels/approach in scope (Intake C.13): Smoke, Sanity, Integration, System, Regression, and Automation.

#### Browser & Operating System Support

RanchOps Pro is a **Web** application (Intake C.0). The developed product will be tested on the latest versions of **Chrome, Firefox, Safari, and Edge**, on **Windows and macOS** (Intake D.16). No mobile device/OS testing applies to this project (Intake D.17 — "Not applicable per C.0, Web only").

#### GUI

No Figma file or visual design document was supplied as an input to this project (`RequirementAnalysis.md` Section 2/12; `GapAnalysis.md` "Missing UI Requirements"). GUI verification will therefore be performed against the PRD's own narrative UI description (`PRD_IMHA_DRAFT_2026-11-02.md` Sections 7–9) rather than against a Figma/visual design document — this is an explicit, documented limitation, not an assumption that a design source exists. UI test cases derived under this constraint will validate structural/behavioral UI elements (forms, wizards, tables, modals, tabs) described in the PRD, not pixel-level layout fidelity.

### Areas Out of Scope

Per Intake C.14 ("None beyond what's already excluded in the PRD/Requirement Analysis") plus the following exclusions already documented in Requirement Analysis / Gap Analysis / the PRD itself:

* **API Testing** — not selected as an in-scope testing type (Intake C.12 selected only Functional and UI).
* **Database Testing** — not selected as an in-scope testing type (Intake D.18 — "Not applicable").
* **Security Testing** — not selected as an in-scope testing type (Intake C.12).
* **Accessibility Testing** — not selected as an in-scope testing type (Intake C.12). (No WCAG target exists in source material regardless — `RequirementAnalysis.md` Section 17.)
* **Performance Testing** — not selected as an in-scope testing type (Intake C.12). (No performance target exists in source material regardless — `GapAnalysis.md` "Missing Non-Functional Requirements".)
* **Mobile Testing** — not applicable; RanchOps Pro is Web-only (Intake C.0/D.17).
* **Guest Portal** and **Payment Processing/Stripe** — explicitly confirmed Out of Scope for V1.0 by the PRD itself (PRD Section 5, citing IMHA-71 OOS-02/OOS-03).
* **IMHA-2, IMHA-106, IMHA-141, IMHA-148** — excluded JIRA issues, not carried into any requirement (PRD Section 5; `RequirementAnalysis.md` Section 23).
* Session-management behavior (timeout, concurrent sessions, token refresh), account-lockout/rate-limiting, and any audit-trail requirement for Super Admin destructive Harvest edits are **not** defined anywhere in source material and are therefore out of scope for test design until a requirement exists (`GapAnalysis.md` GAP-FR-02/03, GAP-FR-05).
* Only the stories accepted into the 54 in-scope JIRA issues for project IMHA will be considered for testing; no additional scope beyond what Requirement Analysis documents is assumed.

---

## 5. Testing Process

This section describes how the testing portion of the project will be structured: what documents will be produced, what phases testing goes through, how results are recorded and issues logged.

Testing will proceed in alignment with the AI QA Framework's own artifact chain: Requirement Analysis → Gap Analysis → Change Impact Analysis → this Test Plan → Functional/UI Test Case generation → Requirement Traceability Matrix → (where selected) Automation script generation → Execution → Defect logging and triage (see Sections 7, 9, and 16 below for the specific documents, tools, and triage process). Test cases and defects will be tracked in Jira; the test case repository itself is maintained in TestLink (Intake B.8).

---

## 6. Planning, Implementation, & Release

**Planning:** During this phase, Codecraft QA is responsible for gathering the necessary details regarding the application requirements as well as any other details needed for the implementation phase. This has been completed for RanchOps Pro via the Requirement Analysis, Gap Analysis, and Change Impact Analysis reports, plus the Test Plan Intake Questionnaire (`TestPlanIntake.md`), which captured the testing types performed (Functional, UI), the environment requirements (Dev only, `https://dev-dashboard.hunter.igoalzero.com/login`), and the necessary tools (Jira, TestLink, Playwright).

**Implementation** – Once planning is completed, Codecraft QA will proceed to set up the test environment (Dev), create the test data, and write the test cases. Test data will be **manually created** by the QA team, which also provisions its own test accounts (Intake E.20/E.21); no refresh cadence is required since a static dataset is used (Intake E.22). **Data privacy note:** the booking/guest registration flow collects PII (Full Name, Home Address, Phone Number, Date of Birth, State Hunting License Number) — all test data for these fields must use synthetic/fake values only, never real user PII (Intake E.23, confirmed during the Step 6.6 Intake Consistency Check). Jira will be used by Codecraft QA to document all test cases and defects, with the test case repository maintained in TestLink. Test execution and defect reporting will proceed per feature/module as they are considered code complete and become available from development. Regular test status reports will be issued during this phase via the Jira-native dashboard (Intake B.11); no separate reporting tool (Excel/PowerBI/email) is used for this engagement.

**Sprints** – Codecraft QA will start testing as soon as a development Sprint begins. At every Sprint end, QA will have verified all features of that sprint, executed all applicable test cases, and reported bugs found during that sprint. **Schedule note:** no key schedule dates/milestones (Planning, Test Design, Environment Setup, Automation, Execution, Regression, UAT, Closure) have been defined yet for this engagement (Intake F.26 — "Not yet defined / TBD"); this is recorded here as an explicit open item, not a silent omission — see Section 14 (Risks).

**Release** – Once the application is declared *Feature Complete*, Codecraft QA will begin a full regression cycle, which executes the full test case suite (Test Levels in scope: Smoke, Sanity, Integration, System, Regression, Automation — Intake C.13). There may be multiple versions in each cycle, so that defects are verified as they are fixed, but the version that is sent for sign-off must complete a full regression against the Dev environment (the only environment currently provisioned for this project).

---

## 7. Test Documentation

As well as this Test Plan, a Test Requirements document (the Requirement Traceability Matrix, RTM) will be created. It is the cross-reference document that will link all Test Cases back to the source requirements (`RequirementAnalysis.md`'s 46 active Functional Requirements (revised 2026-11-02)). Upon project completion, CodeCraft QA will deliver these documents along with all test suites and test results, per the deliverables listed in Section 12.

---

## 8. Test Strategy

The strategy being employed during the testing of Project **IGZ Marquis Hunting App ("RanchOps Pro")** is as follows:

### Overview

This section lists the detail necessary to describe the levels and types of testing that Codecraft QA will perform during the course of the project.

New test cases will be written for RanchOps Pro-specific features. All test cases and defects will be tracked in **Jira** (Intake B.6/B.7); the complete Test Case suite is additionally maintained in **TestLink** (Intake B.8).

It is assumed that before any code is submitted for RanchOps Pro, it has been unit tested by the submitting developer.

### Test Method

Testing methods being employed in Project RanchOps Pro are scoped to the test levels/approach actually selected in Intake C.13 (Smoke, Sanity, Integration, System, Regression, Automation):

1. When validating UI, Codecraft QA will use the PRD's own narrative UI description as the available reference, since no Figma/visual design document exists for this project (see Section 4, GUI) — UI verification fidelity is therefore structural/behavioral, not pixel-level.
2. Codecraft QA works alongside the Development Team and, when needed, will make requests for order of features to enable the quickest possible turn-around of issues as they arise.
3. Functional testing will be based on the requirements provided in `RequirementAnalysis.md` and the PRD. Where additional detail is required (e.g., the Booking status × field permission matrix, FR-GUEST-004's exact fields/trigger timing, the Docuseal webhook-vs-polling mechanism, or Guide dashboard visibility scope — all flagged in `GapAnalysis.md`), Codecraft QA will work closely with the client to resolve these before authoring the affected test cases, rather than guessing.
4. Regression testing will commence once the application is declared *Feature Complete*. At this point, an end-to-end execution approach will be adopted to ensure application integrity is maintained and to bear out any possible risk areas.
5. Critical Path testing will target features and functionality the user sees and uses every day (the Booking lifecycle: create → waiver dispatch → check-in → harvest log → check-out → invoice). Critical Path test cases must pass by the end of every test cycle and must all be executed at least once during each iteration cycle, and once during the final candidate cycle.

Since **Automation** is an in-scope test level (Intake C.13) with **Playwright** confirmed as the automation tool (Intake B.9), automation-driven regression execution is also part of the Test Method for this project — see Section 9 for current Automation Strategy status.

### Testing Approach Per Feature

#### RanchOps Pro Flow

| **Feature** | **Testing Approach** |
|:---|:---|
| Authentication & Access | Functional + UI validation of Login (FR-AUTH-001: valid/invalid credentials, role validation — username exists, password correct, role matches — per role) and Session Establishment (FR-AUTH-002: session persists across navigation/refresh, logout terminates session, unauthenticated access to protected routes redirects to Login), plus Password Reset (FR-AUTH-003: forgot-password from login page; authenticated reset-password from profile) across Super Admin, Ranch Admin, and Guide roles (there are exactly 3 real login roles — "Admin" in IMHA-91/IMHA-92 is the same role as Ranch Admin, not a distinct 4th role, confirmed by the user on 2026-11-03). Session-timeout/idle-expiry duration and account-lockout/rate-limiting remain out of scope pending clarification (`GapAnalysis.md` GAP-FR-02/GAP-FR-03). |
| Super Admin & Ranch Admin Management | Functional validation of Create Ranch Admin (mandatory fields, password policy as stated, optional welcome-email toggle) and the Ranch Admin role's creation restriction (barred from Ranch/other Ranch Admin creation; Full-Name-only edit on another Ranch Admin's record) — UI-level only, since server-side enforcement of this boundary is an open gap (`GapAnalysis.md` GAP-BR-05). |
| Ranch Configuration | Functional + UI validation of the 5-step Ranch Create/Edit Wizard (per-step required-field gating, cross-step value persistence, edit-mode pre-population), Turnover Day booking-block enforcement, and generic field-level validation on Ranch forms. |
| Booking Management | Functional + UI validation of booking creation (Ranch, dates, rooms, hunter/guest details, guide assignment up to 4), mandatory Address, Booking Classification default/edit, Guide Booking Request + approval workflow, and auto check-in/out with manual-override interaction. **Status-conditional edit/view behavior (Pending/Approved/Cancelled) is scoped to happy-path only** pending the status × field permission matrix (`GapAnalysis.md` GAP-BR-01) — this is an explicit, documented scope limitation, not a silent gap. |
| Guest/Hunter Directory | Functional + UI validation of auto-population from booking create/edit, pagination, relaxed-optional fields (Age, Mobile, Address, City, State, Zip), and the personal-details email form happy path (FR-GUEST-004's non-submission behavior is out of scope pending clarification, `GapAnalysis.md` GAP-FR-04). |
| Staff & Guide Management | Functional + UI validation of Create/Edit Guide (mandatory fields, email/phone format, default Active status) and Guide email update with Cognito-sync notification. |
| Harvest Log & Compliance | Functional + UI validation of Harvest Log create/edit, tag allocation/consumption and exhaustion blocking, category-conditional field display ("Other" replicating Deer), animal-name display, and photo capture/gallery-select. Super Admin destructive edit/delete on past bookings is tested at the happy-path/confirmation-prompt level only — no audit-trail verification is possible since none exists (`GapAnalysis.md` GAP-FR-05). |
| Invoicing | Functional + UI validation of invoice generation/preview/download (PDF/Excel)/deletion, discard-and-replace, and the one-invoice-per-booking rule including Invoiced-tag/gating UI behavior. |
| Waivers & Document Workflow | Functional + UI validation of Docuseal dispatch at booking creation, signed-document display, resend post check-in, and mandatory signing extended to non-hunter guests. **Exact timing/latency of "signed document displayed"** is scoped as best-effort/observed-behavior only, since the webhook-vs-polling mechanism is unresolved (`GapAnalysis.md` GAP-BR-02). |
| Dashboard & Reporting | Functional + UI validation of the Booking Calendar, Recent Bookings (last 6 days), summary metric cards, and Active Ranches section. |
| Guide Portal & Role-Based Visibility | Functional + UI validation that pricing is hidden from the Guide role (UI-level only this pass, since API/data-layer testing is out of scope per C.12) and that Whitetail Deer tag counts are visible. **Guide dashboard visibility scope (assigned-only vs. all-ranch) is scoped as an open item** pending clarification (`GapAnalysis.md` GAP-BR-03) — test cases will assume the PRD's stated behavior and flag the ambiguity rather than guess. |
| Platform/Cross-Cutting Technical | Functional validation of pagination across Bookings, Ranches, Guides/Staff, Harvest logs, Invoices, and Guests list views. The SQLite→PostgreSQL migration itself (FR-PLATFORM-002) is an infrastructure/technical change validated only indirectly, through functional testing of the affected modules — not tested as a standalone DB migration activity, since Database Testing is out of scope (Intake D.18). |
*Table 8.1: Testing Approach Per Feature*

---

## 9. Test Tools

Codecraft will use the following tools, confirmed via the Test Plan Intake Questionnaire, during the testing process for RanchOps Pro:

* **Jira** — task/project tracking tool (Intake B.6) and defect tracking tool (Intake B.7), to track project progress, test cases, and bugs.
* **TestLink** — Test Case Management tool (Intake B.8), storing the complete Test Case suite for the project.
* **Playwright** — confirmed automation tool (Intake B.9). This is also where Automation Strategy content lives in this template (no separate Automation Strategy section exists): Automation is an in-scope test level (Intake C.13), and Playwright scripts will be authored for the Automation-Candidate-marked test cases once Functional/UI Test Case generation (and its own Automation Candidate classification per `03-standards/testing-standards.md`) is complete. **No CI/CD tool is in use for this engagement** (Intake B.10 — "None"); automation execution is therefore manually triggered until/unless a CI/CD tool is adopted.
* **Jira-native dashboard** — reporting channel for test status (Intake B.11); no separate Excel/PowerBI/email reporting channel is used.

No third-party web proxy (e.g. Charles) or API client (e.g. Postman) tooling has been confirmed for this engagement, since API Testing is not an in-scope testing type (Intake C.12) and no API tooling was named in the Intake Questionnaire.

---

## 10. Web Services Testing

**Not applicable.** API/Web Services testing was not selected as an in-scope testing type (Intake C.12). This is consistent with `RequirementAnalysis.md`/`GapAnalysis.md`'s own finding that no Swagger/OpenAPI specification, full endpoint inventory, or error-response format catalog exists for RanchOps Pro — only 3 API-relevant Functional Requirements are documented at all (FR-BOOK-003, FR-HARVEST-002, FR-GUIDE-001), and even those would require black-box observation only, not endpoint-level contract testing, if API testing were in scope. Should API Testing be added to scope in a future cycle, this section will be revised and `05-agents/api-agent.md` engaged accordingly.

---

## 11. Build Process

No CI/CD tool is confirmed for this engagement (Intake B.10 — "None"). The build/test/release process for RanchOps Pro is therefore **manual**: Codecraft Developers commit code to a branch in Git; QA manually pulls/deploys the relevant branch to the Dev environment (`https://dev-dashboard.hunter.igoalzero.com/login`) to test new features; once features have been tested and bugs fixed, the build is promoted manually. Should a CI/CD tool (e.g. Jenkins, GitHub Actions, GitLab CI, Azure Pipelines) be adopted in a later cycle, this section will be revised to reflect automated build/deploy/test-trigger integration.

---

## 12. Test Deliverables

Per Intake G.31 ("All standard deliverables"), Codecraft QA will deliver the following materials upon Release acceptance:

* Current, updated version of this Test Plan document.
* Functional and UI Test Cases (`07-outputs/TestCases/`), maintained in TestLink.
* Requirement Traceability Matrix (RTM), linking all 46 active Functional Requirements to their Test Cases (revised 2026-11-02).
* Test Execution Reports.
* Defect Reports (tracked in Jira).
* Automation Scripts (Playwright), covering the test cases marked Automation Candidate = YES.
* Test Summary Report upon completion of each regression cycle.

---

## 13. Exit/Entry Criteria

For the **IGZ Marquis Hunting App ("RanchOps Pro")** project, the following criteria are outlined below and will be adhered to throughout. Per Intake G.27/G.28/G.29, these use the **framework standard** definitions (`03-standards/testplan-standards.md`), with no project-specific customization.

**Entry Criteria** (framework standard, Intake G.27): Testing shall begin only when requirements are approved, the test environment (Dev) is available, test data is ready, the build is stable, and required integrations are available.

**Exit Criteria** (framework standard, Intake G.28): Testing shall complete only when planned testing is completed, critical defects are resolved, regression testing is completed, test coverage objectives are achieved, and exit criteria are approved.

**Suspension/Resumption Criteria** (framework standard, Intake G.29): Testing shall be suspended under conditions such as environment failure, build instability, or critical blocker defects, and may resume once the suspending condition is resolved.

Defect severity/priority gating below uses the **framework default** scheme (Intake G.30), which for this template is this document's own Section 16.3 Issue Priority table (1 Immediate / 2 High / 3 Normal / 4 Low) — no customization was requested.

### Implementation Phase Exit

Codecraft QA will test features at every sprint and validate them. QA will verify and validate all bugs fixed from the previous sprint. This phase is marked as complete once QA has verified all features of the project in each consecutive sprint.

### Beta Phase Exit

A full regression sweep will be performed during the Beta 1 cycle. If no Priority 1 (Immediate) or Priority 2 (High) issues remain open by the end of the Beta 1 milestone, the build is released for the next stage and the Beta 1 cycle is considered complete. If **new** Priority 1 or 2 issues are reported, they are marked as highest priority for immediate resolution and the new build is released for testing as Beta 2. These standards are maintained for each subsequent beta release cycle.

### Final Candidate Entry

At this point, when regression testing is complete there should be no more than 10 open Priority 3 (Normal) issues left in Jira, and no Priority 1 or 2 issues open. If this is the case, the *Final Candidate* build can be declared. If defects are discovered that keep the quality level from reaching *Final Candidate* standard, more builds are made until enough defect fixes have been performed to reach *Final Candidate* release quality.

For the RanchOps Pro project, the quality requirements are:

* No known Priority 1 (Immediate) defects caused by application code.
* No known Priority 2 (High) defects caused by application code.

---

## 14. Risks and Assumptions

### Assumptions

Carried forward verbatim from `RequirementAnalysis.md` Section 19 and `GapAnalysis.md` Section "Assumptions" — no new assumption is invented here:

* ASM-1: IMHA-93 remains non-functional QA/process tracking, not a numbered business Functional Requirement.
* ASM-2: IMHA-8 (DB migration) and IMHA-5 (API technical change) remain non-functional/technical requirements, not business-facing FRs.
* ASM-3: IMHA-71's uncorroborated supplementary detail (e.g., AWS S3 storage backend) remains corroborating/background context only, never promoted to a confirmed standalone requirement.
* ASM-4: The four excluded issues (IMHA-2, IMHA-106, IMHA-141, IMHA-148) remain confirmed out of scope, not merely undocumented.
* Priority values (P1–P4) assigned to Functional Requirements and Gaps in `RequirementAnalysis.md`/`GapAnalysis.md` are derived judgment (no explicit priority scale exists in source material) and should be reviewed/confirmed by a QA Architect or Product Owner before being used to sequence test execution.
* It is assumed that before any code is submitted to Git for RanchOps Pro, it has been unit tested by the submitting developer.
* All builds delivered to QA will have been unit tested by Development, and no Release build will be made for QA testing if unit tests are failing.

### Risks

| **#** | **Description of risk** | **Contingency Plan** | **Impact** | **Probability** |
|:---|:---|:---|:---|:---|
| **1** | **CLOSED/RESOLVED 2026-11-02.** ~~Authentication mechanism remains entirely unresolved~~ — password-based mechanism confirmed by the user, post-PRD-approval (`RequirementAnalysis.md` RISK-2/CONF-1 resolved, `GapAnalysis.md` GAP-FR-01 resolved). FR-AUTH-001 (Login) and FR-AUTH-002 (Session Establishment) are now authored and tested (Section 8). | No longer applicable — resolved. | Closed (was Critical) | Closed (was High) |
| **2** | No measurable Non-Functional targets exist (performance, accessibility/WCAG, availability/SLA, compliance) (`GapAnalysis.md` "Missing Non-Functional Requirements") | Not a blocker this cycle since NFR testing types are not in scope (Intake C.12); flagged so that if NFR testing types are added in a future cycle, targets must be obtained first — do not claim NFR coverage without them. | High | High (if NFR scope is ever added without targets) |
| **3** | Booking status × field permission matrix (Pending/Approved/Cancelled) is undefined (`GapAnalysis.md` GAP-BR-01) | Scope Booking edit/view test cases to happy-path only until the matrix is obtained from Business Analyst/Product Owner; track as an open item, not a silent assumption. | Medium | Medium |
| **4** | Docuseal signature-notification mechanism (webhook vs. polling) is unresolved (`GapAnalysis.md` GAP-BR-02) | Test "signed document displayed" behavior as best-effort/observed timing only; obtain engineering confirmation before asserting a specific latency expectation. | Medium | Medium |
| **5** | Guide dashboard visibility scope (assigned-only vs. all-ranch) is unresolved (`GapAnalysis.md` GAP-BR-03) | Author test cases against the PRD's stated behavior and flag the ambiguity explicitly in the test case's own notes; do not guess a scope and present it as confirmed. | Medium | Medium |
| **6** | No key schedule dates/milestones have been defined yet (Intake F.26 — TBD) | Treat schedule as an open item to be resolved before Test Plan execution sequencing is finalized; update this Test Plan's metadata once dates are confirmed. | Medium | Medium |
| **7** | QA team composition is a single generalist covering all in-scope testing types (Functional, UI) with no dedicated Automation Engineer role confirmed (Intake F.24), despite Automation being an in-scope test level and deliverable | Monitor whether a single generalist can realistically deliver Functional + UI manual testing and Playwright automation scripting within the (currently undefined) schedule; escalate a staffing request if the schedule, once defined, makes this infeasible. | Medium | Medium |
| **8** | No audit trail exists for Super Admin destructive edits on compliance-relevant Harvest Log records (`GapAnalysis.md` GAP-FR-05) | Confirm with Product Owner/Compliance whether this is an accepted risk for V1.0; test only the confirmation-prompt behavior, not any audit record (since none exists). | Medium | Low |
| **9** | PII is collected during booking/guest registration (Full Name, Address, Phone, DOB, Hunting License Number) (Intake E.23) | All test data must use synthetic/fake values only; QA team provisioning its own test accounts/data must enforce this at data-creation time, not rely on post-hoc scrubbing. | High | Low (if synthetic-data discipline is maintained) |

*Table 14.1: Risks and Contingencies*

---

## 15. Resources

### QA Staffing & Responsibilities

Per Intake F.24, QA team composition for RanchOps Pro is a **single QA generalist** covering all in-scope testing types (Functional, UI). RACI ownership for Development, Business/Product Owner, and DevOps roles is **not yet assigned / TBD** (Intake F.25).

| **Position** | **Person** | **Responsibilities** |
|:---|:---|:---|
| **QA Generalist** | TBD (Intake F.24: "Single QA generalist covering all in-scope types") | - Manage QA effort for Functional and UI testing<br>- Manage defect process in Jira<br>- Plan and execute test cases (manual and Playwright automation)<br>- Maintain the TestLink test case repository<br>- Monitor software quality against milestone criteria<br>- Write defect reports and verify defect fixes |
| **Development Team** | TBD (Intake F.25 — not yet assigned) | - Unit-test code before submission to Git<br>- Fix defects reported by QA<br>- Confirm other open clarification items (Section 14) — Authentication mechanism resolved 2026-11-02 |
| **Business/Product Owner** | TBD (Intake F.25 — not yet assigned) | - Approve requirements<br>- Resolve open clarification items (Booking status matrix, FR-GUEST-004 behavior, Guide dashboard visibility scope, audit-trail decision)<br>- Provide sign-off at exit criteria |
| **DevOps** | TBD (Intake F.25 — not yet assigned) | - Not currently applicable; no CI/CD tool is in use (Intake B.10) |

*Table 15.1: Staffing & Responsibilities*

### Test Environments

Per Intake D.15, only the **Dev** environment is currently provisioned for testing: `https://dev-dashboard.hunter.igoalzero.com/login`. No QA/Test, Staging, UAT, or Production environment has been confirmed in scope for this cycle. RanchOps Pro is a **Web** application (Intake C.0); testing will be performed only on desktop web browsers — there is no mobile phone/tablet surface to this application, unlike the generic framework template's mobile-device default framing.

### OS Platforms

The following Operating Systems are supported for RanchOps Pro (per Intake D.16):

* Windows
* macOS

No Android/iOS platform testing applies, since RanchOps Pro is Web-only (Intake D.17 — "Not applicable per C.0").

### Version Requirements

Per Intake D.16, RanchOps Pro will be tested on the **latest versions of Chrome, Firefox, Safari, and Edge**, on Windows and macOS. No broader browser-version compatibility matrix (e.g., specific minimum version numbers, older-version support) was provided in the Intake Questionnaire; this is recorded as an **open item** — a defined minimum-supported-version policy is undefined beyond "latest" — rather than fabricating a version matrix the way a generic template example might.

---

## 16. Appendix

### Test Cases

All test cases will be stored in **TestLink** (Intake B.8), with task/defect cross-references maintained in **Jira**. Test cases will be prioritized according to the Functional Test Level described by the table below. These levels do not mean that some tests will not be executed.

| **Level** | **Description** |
|:---|:---|
| **Smoke** | Subset of all tests used to determine if a build is suitable for testing |
| **1: Critical** | Tests main required functionality |
| **2: High** | Tests ancillary required functionality |
| **3: Medium** | Tests edge cases |
| **4: Low** | Tests usability requirements |

*Table 16.1: Functional Test Level*

### Test Pass Results

The **QA Generalist** (the single staffing role confirmed for this project, Intake F.24 — named owner TBD per F.25) will compile summarized results of test passes. Codecraft will only consider the product complete once QA gives final sign-off on the commercially ready state of the product and the number and quality of remaining bugs meets contracted deliverable status.

### Issues

Any defect found in or other issue raised against RanchOps Pro will be logged and tracked in **Jira** (Intake B.7).

Each issue will be assigned a Priority based on the following table (framework default, Intake G.30 — no customization requested). These criteria determine how or when the issue will be addressed, but do not mean that any issue will be ignored.

| **Priority** | **Description** |
|:---|:---|
| **1 (Immediate)** | Crash/data loss and/or barrier to testing, or any major issue that impedes the users' ability to progress through the application or greatly damages user experience/brand fidelity. For example: the application crashes when attempting to access an area. |
| **2 (High)** | A loss of functionality with no workaround, or any major issue that does not impede the users' ability to progress through the application yet still damages user experience significantly. |
| **3 (Normal)** | Loss of functionality with workaround, or any issue which cannot be classed as major but still creates a negative user experience. |
| **4 (Low)** | Usability issues or any defect involving enhancements and design issues, or minor irregularities that do not affect the software's functionality. |

*Table 16.2: Issue Priority Definitions*

### Escalation

If a Blocking issue that significantly prevents testing is logged, notification will be sent to the RanchOps Pro Project Manager, Lead Developer, and Lead QA with a link to the issue in Jira. As with RACI ownership (Section 15), these named individuals are **TBD** (Intake F.25 — not yet assigned); this section will be updated once staffing is confirmed. Likewise, as the product nears *Feature Complete* status, the logging of new Priority 1/2 issues will be escalated to the project team.

### Triage

During the Beta phase, daily triage meetings with the RanchOps Pro Project Manager, Lead Developer, and Lead QA (names TBD, see above) will be held. Together they will decide the resolution schedule for each issue. Typically the priority of issues entered into Jira determines the order in which they are addressed; highest-priority defects are tackled first.

### Test Cycles

Each phase will consist of a series of test cycles, scoped to the test levels selected in Intake C.13 (Smoke, Sanity, Integration, System, Regression, Automation). In general, a test cycle will consist of the following components:

* Perform smoke testing of a representative subset of functionality.
* Test fixes of any defects that blocked testing in previous cycles.
* Test all other defect fixes.
* Perform regression tests (the full set of applicable test cases, executed as often as possible — including Playwright-automated regression once automation scripts are authored for Automation-Candidate test cases).

---

**End of Test Plan (Draft).**

Per `04-prompts/testplan-gen-prompt.md` Constraints: this document contains no individual test cases, Requirement Traceability Matrix, or automation scripts — those are separate downstream deliverables (Functional/UI Test Case Generation, RTM Generation, Automation Script Generation).
