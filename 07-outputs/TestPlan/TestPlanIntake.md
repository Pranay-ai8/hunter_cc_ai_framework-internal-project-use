# Test Plan Intake Questionnaire — IGZ Marquis Hunting App (RanchOps Pro)

Captured per `MasterQAOrchestrator/TestPlanOrchestrator.md` Step 6.5. Answers below are authoritative for Test Plan generation (Step 7) — never overridden by agent inference.

## A. Document & Stakeholders

- **A.1 — Project Name:** IGZ Marquis Hunting App ("RanchOps Pro")
- **A.2 — Application Name:** Same as Project Name
- **A.3 — Test Plan version/revision:** 1.0
- **A.4 — Prepared By:** Pranay
- **A.5 — Reviewed By / approver:** Not yet assigned / TBD

## B. Tools & Process

- **B.6 — Task/project tracking tool:** Jira
- **B.7 — Defect tracking tool:** Jira
- **B.8 — Test case management tool:** TestLink
- **B.9 — Automation tool(s):** Playwright
- **B.10 — CI/CD tool:** None
- **B.11 — Reporting channel/tool:** Tool-native dashboard (Jira dashboard)

## C. Testing Scope & Approach

- **C.0 — Application Type:** Web
- **C.12 — Testing types in scope:** Functional, UI
- **C.13 — Test levels/approach:** Smoke, Sanity, Integration, System, Regression, Automation (added during Step 6.6 Intake Consistency Check — see below)
- **C.14 — Additional Out of Scope areas:** None beyond what's already excluded in the PRD/Requirement Analysis

## D. Test Environment

- **D.15 — Environments in scope:** Dev
- **D.15a — Dev environment URL:** https://dev-dashboard.hunter.igoalzero.com/login
- **D.16 — Browser/OS support (Web in scope):** Latest Chrome/Firefox/Safari/Edge on Windows + macOS
- **D.17 — Mobile devices/OS:** Not applicable per C.0 (Web only)
- **D.18 — Database testing:** Not applicable (no Database testing in scope)
- **D.19 — Third-party/external test-environment dependencies:** None beyond the application's own backend API

## E. Test Data

- **E.20 — Test data sourcing:** Manually created
- **E.21 — Test account provisioning:** QA team provisions its own
- **E.22 — Test data refresh cadence:** No refresh needed (static dataset)
- **E.23 — Data privacy/compliance constraints:** PII/personal data applies — booking/guest registration collects Full Name, Home Address, Phone Number, Date of Birth, and State Hunting License Number; test data must use synthetic/fake values only, never real user PII (corrected during Step 6.6 Intake Consistency Check — see below)

## F. Team, Schedule & Responsibilities

- **F.24 — QA team composition:** Single QA generalist covering all in-scope types (Functional, UI)
- **F.25 — RACI ownership:** Not yet assigned / TBD
- **F.26 — Key schedule dates/milestones:** Not yet defined / TBD

## G. Criteria, Deliverables & Risk Process

- **G.27 — Entry Criteria:** Framework standard (Requirements approved / Build available / Environment ready / Test data available / Smoke passed)
- **G.28 — Exit Criteria:** Framework standard (Critical defects closed / Regression complete / Execution complete / Coverage achieved / Sign-off received)
- **G.29 — Suspension/Resumption Criteria:** Framework standard
- **G.30 — Defect severity/priority scheme & SLA:** Framework default (`03-standards/testplan-standards.md` Defect Management)
- **G.31 — Deliverables required:** All standard (Test Plan / Test Cases / RTM / Execution Reports / Defect Reports / Automation Scripts / Summary Report)
- **G.32 — Additional known risks/dependencies:** None additional beyond Gap Analysis

---

## Step 6.6 — Intake Consistency Check (2026-11-02)

Two contradictions were found and resolved with the user before Test Plan generation:

1. **Automation scope mismatch:** G.31 selected all standard deliverables (including Automation Scripts) and B.9 confirmed Playwright as the planned automation tool, but C.13 initially omitted "Automation" as an in-scope test level. **Resolved:** Automation added to C.13's test levels.
2. **Data privacy mismatch:** E.23 initially stated no privacy/compliance constraints, but the PRD's booking/guest registration flow collects PII (Full Name, Address, Phone, DOB, Hunting License Number). **Resolved:** E.23 updated to confirm PII applies; test data must be synthetic only.

No further contradictions found. Proceeding to Step 7 (Execute Agent).
