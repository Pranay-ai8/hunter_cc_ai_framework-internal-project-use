# Impact Analysis Report — IGZ Marquis Hunting App (RanchOps Pro)

## Document Information

| Field | Value |
|--------|-------|
| Project Name | IGZ Marquis Hunting App ("RanchOps Pro") |
| Application | IMHA |
| Release | Initial |
| Version | 1 |
| Change Request ID | N/A — initial generation |
| Requirement Version | PRD_IMHA_2026-11-02 (first version) |
| Prepared By | AI Change Impact Agent |
| Generated Date | 2026-11-02 |
| Status | Review |

---

# Executive Summary

This is the **first Change Impact Analysis run for this requirement source**. No previous baseline exists for the IMHA / RanchOps Pro project in `07-outputs/` (no prior `RequirementAnalysis.md`, `GapAnalysis.md`, `TestPlan.md`, Functional/API/UI/Security/Accessibility Test Cases, RTM, or Automation Framework/Scripts were found for this project — the only prior artifacts under `07-outputs/_archive/` belong to unrelated projects/demos, e.g. SauceDemo automation samples).

Per `05-agents/change-impact-agent.md`'s Constraints ("Always prefer incremental updates over complete regeneration") and its design as an incremental-comparison tool, there is no prior version of this project's requirements to diff against. Change Impact Analysis is therefore not applicable in its comparative form for this run — there is nothing to classify as "changed." All downstream artifacts for this project must be treated as **initial generation**, not regeneration.

- Total requirements analyzed: 44 active Functional Requirements (46 authored; FR-AUTH-001/FR-AUTH-002 excluded pending engineering review)
- New requirements: 44 (all — first baseline)
- Modified requirements: 0 (no prior version exists)
- Deleted requirements: 0
- Overall impact level: **N/A (Initial Baseline)**
- Recommendation: Proceed with full initial generation of Test Plan, Functional Test Cases, and all other downstream artifacts for this project. This Impact Analysis itself becomes the baseline for any future incremental run against this requirement source.

---

# Change Summary

| Change Type | Count |
|------------|------:|
| New Requirements | 44 |
| Modified Requirements | 0 |
| Deleted Requirements | 0 |
| Clarified Requirements | 0 |
| Business Rule Changes | 0 (all rules are new) |
| UI Changes | 0 (all UI behavior is new) |
| API Changes | 0 (all integrations are new) |
| Workflow Changes | 0 (all workflows are new) |

---

# Requirement Change Details

## Newly Added Requirements

All 44 active Functional Requirements across all 12 modules (Authentication & Access [partial — 2 excluded], Super Admin & Ranch Admin Management, Ranch Configuration, Booking Management, Guest/Hunter Directory, Staff & Guide Management, Harvest Log & Compliance, Invoicing, Waivers & Document Workflow, Dashboard & Reporting, Guide Portal & Role-Based Visibility, Platform/Cross-Cutting Technical) are new — see `07-outputs/RequirementAnalysis.md` and `07-outputs/GeneratedPRD(JIRA)/PRD_IMHA_DRAFT_2026-11-02.md` Section 8 and Section 17.1 for the full Requirement ID → JIRA Issue Key map. Not re-listed row-by-row here to avoid duplicating that traceability table.

## Modified Requirements

None — no prior baseline exists.

## Deleted Requirements

None — no prior baseline exists.

---

# Business Rule Changes

None — all business rules documented in `RequirementAnalysis.md` Section 9 (Business Rules) are new for this initial baseline.

---

# Workflow Changes

None — all end-to-end workflows are new for this initial baseline.

---

# UI Changes

None — all UI behavior described in the PRD/Requirement Analysis is new for this initial baseline. (Note: no Figma/screenshot design source exists for this project, per the earlier UI Test Case input decision — this is an existing, separately-tracked gap, not a "change.")

---

# API Changes

None — all integrations (AWS Cognito/SES, Docuseal, AWS S3, Stripe-referenced-but-out-of-scope) are new for this initial baseline.

---

# Database Changes

None — no prior schema baseline exists in this framework's records for this project.

---

# Requirement Traceability Impact

| Requirement | Previous Status | New Status | Action Required |
|------------|----------------|-----------|----------------|
| All 44 active FRs | N/A (no prior baseline) | New | Carry into Test Plan and Test Case generation |
| FR-AUTH-001 / FR-AUTH-002 | N/A | Excluded (pending engineering review) | Hold out of this generation pass; do not test an unconfirmed mechanism |

---

# Test Plan Impact

## Impact Assessment

| Section | Impact | Action |
|---------|--------|-------|
| Test Strategy | N/A (new) | Author from scratch |
| Scope | N/A (new) | Author from scratch, excluding FR-AUTH-001/002 pending review |
| Risks | N/A (new) | Carry forward GapAnalysis.md's Critical/High items |
| Automation Scope | N/A (new) | To be defined in Test Plan generation |

---

# Functional Test Case Impact

| Module | Existing TC | Impacted TC | New TC Required | Update Required |
|---------|------------:|------------:|----------------:|----------------:|
| All 12 modules | 0 | 0 | All (full initial set) | 0 |

---

# API Test Case Impact

| API | Existing TC | Impact |
|-----|------------:|-------|
| All integrations | 0 | New generation required if API Test Cases (capability 13) is selected in a future run |

---

# UI Test Case Impact

| Screen | Existing TC | Impact |
|--------|------------:|-------|
| All screens | 0 | New generation required (from PRD text only — no design source exists) |

---

# Security Test Case Impact

| Module | Existing TC | Impact |
|--------|------------:|-------|
| All modules | 0 | New generation required if Security Test Cases (capability 12) is selected |

---

# Accessibility Test Case Impact

| Module | Existing TC | Impact |
|--------|------------:|-------|
| All modules | 0 | New generation required if Accessibility Test Cases (capability 14) is selected |

---

# Automation Impact

## Framework Impact

| Component | Impact | Action |
|----------|--------|-------|
| Automation Framework | N/A (none exists for this project) | To be established if an Automation capability (7-11) is selected |

## Automation Script Impact

| Script | Action |
|--------|-------|
| Update Required | None (nothing exists) |
| New Script Required | All, if an Automation capability is selected downstream |
| Delete Script | None |
| No Change | None |

---

# Page Object Impact

| Page Object | Action |
|-------------|-------|
| None exist for this project | New, if Automation is selected |

---

# Test Data Impact

| Test Data | Action |
|-----------|-------|
| None exists for this project | New, to be defined during Test Plan/Test Case generation |

---

# Execution Impact

| Suite | Impact |
|------|--------|
| Smoke | New — to be defined |
| Regression | New — to be defined |
| Sanity | New — to be defined |
| Integration | New — to be defined |
| E2E | New — to be defined |

---

# Reporting Impact

| Report | Impact |
|--------|-------|
| All reports | New — no prior reporting baseline exists |

---

# RTM Impact

| Requirement | Existing Mapping | Updated Mapping |
|------------|-----------------|----------------|
| All 44 active FRs | None | To be created in RTM generation (capability 6), if selected |

---

# Documentation Impact

| Document | Action |
|----------|-------|
| Requirement Analysis | Already generated this run (`07-outputs/RequirementAnalysis.md`) |
| Gap Analysis | Already generated this run (`07-outputs/GapAnalysis.md`) |
| Test Plan | To be generated next (this run) |
| Test Cases | To be generated next (this run) |
| Automation Framework | Not in scope for this run |
| User Guide | Not in scope |
| Release Notes | Not in scope |

---

# Risk Assessment

## Business Risks

| Risk | Severity | Recommendation |
|------|----------|---------------|
| Authentication mechanism unresolved (password vs. OTP) could delay or misdirect release-readiness testing | Critical | Resolve before Test Plan locks scope for Authentication module (carried from GapAnalysis.md) |
| No measurable NFR targets (performance/security/accessibility/availability) | High | Product Owner/Engineering to define before NFR test design |

## Technical Risks

| Risk | Severity | Recommendation |
|------|----------|---------------|
| No prior automation framework exists for this project | Medium | Establish framework from scratch if automation capability is selected |

## Testing Risks

| Risk | Severity | Recommendation |
|------|----------|---------------|
| No UI design source (Figma/screenshots) exists | High | UI Test Cases this run will be generated from PRD text only, at lower fidelity — flagged as a documented limitation |
| IMHA-93 TestLink content inaccessible | Medium | Retrieve TestLink content in a future pass to corroborate already-tested Super Admin/Ranch Admin behavior |

---

# Dependency Analysis

| Dependency | Impact |
|-----------|-------|
| Requirement Analysis → Gap Analysis → Test Plan → Functional/UI Test Cases | Sequential, all new this run — no incremental shortcuts apply |

---

# Recommended Actions

- Proceed directly to **Test Plan Generation** (this run) — no incremental shortcut applies since this is the initial baseline.
- Proceed to **Functional + Generic + UI Test Case Generation** (this run) per the user's selected mode.
- Treat this Impact Analysis Report as the **baseline** for any future Change Impact Analysis run against this project (e.g. if JIRA stories are added/modified and a new PRD version is generated later).

---

# Regeneration Matrix

| Artifact | Regenerate | Update | No Action |
|----------|:----------:|:------:|:---------:|
| Requirement Analysis | ✅ (done this run) | | |
| Gap Analysis | ✅ (done this run) | | |
| Test Plan | ✅ (next) | | |
| Functional Test Cases | ✅ (next) | | |
| API Test Cases | | | ✅ (not selected this run) |
| UI Test Cases | ✅ (next, from PRD text only) | | |
| Security Test Cases | | | ✅ (not selected this run) |
| Accessibility Test Cases | | | ✅ (not selected this run) |
| RTM | | | ✅ (not selected this run) |
| Automation Framework | | | ✅ (not selected this run) |
| Automation Scripts | | | ✅ (not selected this run) |
| Execution Suite | | | ✅ (not selected this run) |

---

# Overall Impact Rating

**N/A — Initial Baseline** (not Low/Medium/High/Critical in the comparative sense; treat as full initial generation)

---

# AI Recommendations

- Lock the Authentication requirement gap (FR-AUTH-001/002) with engineering before any Authentication-specific test design is attempted, per GapAnalysis.md's Critical finding.
- Proceed with Test Plan and Functional+Generic+UI Test Case generation now, excluding Authentication-mechanism-specific test cases until that gap is resolved (Test Plan should note this as an explicit scope exclusion, not a silent omission).
- Re-run Change Impact Analysis the next time this project's JIRA scope changes or a new PRD version is generated, using this report as the comparison baseline.

---

# Conclusion

This is the initial Change Impact Analysis for the IMHA / RanchOps Pro project — there is no prior baseline, so no comparative impact exists. All downstream artifacts (Test Plan, Test Cases, RTM, Automation) are new generations, not updates. The project is ready to proceed to Test Plan Generation, with the Authentication mechanism gap and absence of a UI design source carried forward as known, documented limitations rather than blockers.

---

# Approval

| Role | Name | Date |
|------|------|------|
| Business Analyst | | |
| Product Owner | | |
| QA Lead | | |
| QA Manager | | |

---

# Output Quality Standards

This report is Requirement Traceable, Actionable, Risk Based, Review Ready, and suitable for downstream AI agents (Test Plan Orchestrator, Functional Test Case Orchestrator).
