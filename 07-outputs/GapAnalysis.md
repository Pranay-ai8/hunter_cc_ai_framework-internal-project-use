# Gap Analysis Report

**Revision Note (2026-11-02):** GAP-FR-01 (authentication mechanism entirely unresolved — the Critical, release-blocking gap) is now RESOLVED. The user confirmed, post-PRD-approval, that the live mechanism is password-based (FR-AUTH-001/FR-AUTH-002, per IMHA-82/IMHA-91's described flow). This closes GAP-FR-01 and the corresponding Authentication risk/dependency entries below; GAP-FR-02 (session-timeout/idle-expiry duration) and GAP-FR-03 (account-lockout/rate-limiting) remain open, as they were not addressed by this confirmation.

**Revision Note 2 (2026-11-03):** Role terminology correction applied — "Admin" (sourced from IMHA-91/IMHA-92, FR-SA-002) was mistakenly treated as a distinct 4th role in the Missing User Roles & Permissions section and Open Questions. The user confirmed on 2026-11-03 that there are exactly 3 real login roles — Super Admin, Ranch Admin, and Guide — and "Admin" is the same role as Ranch Admin. All standalone "Admin" references have been renamed to "Ranch Admin" below.

---

# Document Information

| Field | Value |
|--------|-------|
| Project Name | IGZ Marquis Hunting App ("RanchOps Pro") |
| Application | RanchOps Pro — multi-tenant ranch/hunting-property operations platform |
| Release | V1.0 (scope per PRD Section 4) |
| Version | 1.0 |
| Prepared By | AI Gap Analysis Agent |
| Generated Date | 2026-11-02 |
| Source Documents | `07-outputs/RequirementAnalysis.md` (primary input, read in full); `07-outputs/GeneratedPRD(JIRA)/PRD_IMHA_DRAFT_2026-11-02.md` (consulted for requirement-level detail); JIRA project IMHA (54 in-scope issues, traceability only — not independently re-queried this run) |

---

# Executive Summary

This Gap Analysis formalizes the requirement-quality issues surfaced during Requirement Analysis of 46 active Functional Requirements (46 authored, 0 excluded — revised 2026-11-02; previously 44 active/2 excluded) across 12 modules, plus additional gaps independently identified by applying this agent's full Validation Checklist against every FR.

**Overall requirement quality:** Moderate. The PRD is unusually well-traced (every FR maps to a JIRA key) and most core workflows (booking lifecycle, harvest compliance, invoicing, waivers) have clear happy-path definitions. However, the PRD carries one unresolved conflict that blocks an entire module (Authentication), several FRs with materially incomplete field/trigger-timing detail, and a near-total absence of Non-Functional targets (performance, accessibility, availability, compliance).

**Overall completeness:** 46/46 authored FRs are active as of 2026-11-02 (FR-AUTH-001/002 confirmed active, password-based mechanism) — previously 44/46 active with Authentication untestable as written; that is now resolved.

**High-level observations:**
- No measurable NFR targets exist anywhere in source material (performance, availability/SLA, accessibility/WCAG, compliance/security-standard).
- ~~The single highest-severity gap is the unresolved authentication mechanism~~ **RESOLVED 2026-11-02** — password-based mechanism confirmed; Functional, Security, and Session-Management test design for the AUTH module can proceed.
- Several FRs (FR-BOOK-002, FR-GUEST-004) describe a mechanism's existence but not its precise business rules, which prevents writing deterministic expected results.
- No Project Context exists, and one supporting JIRA reference (IMHA-93/TestLink) remains inaccessible.

**Major gaps identified:** 1 Requirement Gap (Authentication), 2 Business Rule Gaps (status-conditional edit/view; webhook vs. polling), 2 Workflow Gaps (GUEST-004 trigger timing; Guide dashboard visibility scope), several Validation/UI/API Gaps (format rules, error-message catalog, audit trail, API contract depth), and 1 pervasive Non-Functional Gap spanning five NFR categories.

**Overall business impact:** ~~Authentication is release-blocking from a test-design perspective.~~ **Resolved 2026-11-02 — Authentication is no longer release-blocking.** The remaining gaps are individually medium/low severity but collectively create ambiguity risk for Booking, Guest, Waiver, and Guide-dashboard test design, and leave no basis for NFR test design at all.

---

# Requirement Coverage Summary

| Category | Status | Comments |
|----------|--------|----------|
| Functional Requirements | Good | 46/46 active (revised 2026-11-02 — FR-AUTH-001/002 confirmed active, closing GAP-FR-01; AUTH module now has all 3 intended FRs active) |
| Business Rules | Partial | Core rules documented (Section 9 of RequirementAnalysis.md); 2 rules (status-conditional edit/view, webhook/polling) left as open decisions rather than stated rules |
| User Stories | N/A | No separate User Story artifact; JIRA stories are the PRD's own source, already correlated |
| Acceptance Criteria | Partial | Most FRs have implicit AC embedded in their description; none are written as formal Given/When/Then AC, and several (FR-GUEST-004, FR-BOOK-002) lack AC for edge/alternate cases |
| UI Requirements | Weak | No Figma/screenshot artifacts supplied; UI understanding is narrative-only, no component spec, no error-message catalog, no responsive-behavior spec |
| API Requirements | Weak | Only 3 API-relevant FRs exist (FR-BOOK-003, FR-HARVEST-002, FR-GUIDE-001); no Swagger/OpenAPI spec, no endpoint inventory, no auth header/error-format spec |
| Non-Functional Requirements | Critical Gap | No performance, availability/SLA, accessibility (WCAG level), or compliance target stated anywhere in source |

---

# Missing Functional Requirements

| ID | Module | Description | Impact | Priority |
|----|--------|-------------|--------|----------|
| GAP-FR-01 | AUTH | ~~No active, numbered FR defines how any user authenticates~~ **RESOLVED 2026-11-02:** password-based mechanism confirmed (FR-AUTH-001 Login, FR-AUTH-002 Session Establishment) | No longer blocking — Functional, Security, and Session-Management test design for the AUTH module can proceed | **Closed** (was P1 — Critical) |
| GAP-FR-02 | AUTH | No FR defines session-management behavior (timeout, concurrent-session handling, token refresh/expiry) regardless of which mechanism (password/OTP) is eventually confirmed | Cannot write session-timeout, "remember me," or concurrent-login test cases | P2 — High |
| GAP-FR-03 | AUTH | No FR defines account-lockout / rate-limiting behavior after repeated failed login or OTP attempts | Cannot write brute-force / rate-limiting security test cases | P2 — High |
| GAP-FR-04 | GUEST | No FR defines what happens to a Guest/Hunter record if the FR-GUEST-004 personal-details email form is never submitted (no stated fallback/default state) | Guest Directory record completeness and downstream Waiver/Harvest flows cannot be deterministically tested for the non-submission path | P3 — Medium |
| GAP-FR-05 | HARVEST | FR-HARVEST-003 (Super Admin delete/edit of harvests against past bookings) explicitly states "no audit trail" — no FR exists for *any* audit/history tracking of destructive Super Admin actions on compliance-relevant records | Regulatory/compliance risk — harvest data is tag/compliance-relevant, and silent destructive edits have no recorded history | P2 — High |

---

# Missing Business Rules

| ID | Module | Missing Rule | Business Impact | Recommendation |
|----|--------|--------------|-----------------|----------------|
| GAP-BR-01 | BOOK | FR-BOOK-002's exact field-level edit/view differences per booking status (Pending/Approved/Cancelled) are not specified — which fields are editable, read-only, or hidden in each status | Cannot write deterministic Functional or UI test cases for booking edit; risk of under- or over-testing status transitions | Obtain a status × field permission matrix from Product Owner/BA before test design begins |
| GAP-BR-02 | WAIVER | Docuseal signature-notification mechanism (webhook vs. polling) is explicitly self-flagged as unresolved by the PRD's own supporting context (IMHA-71) and not settled by any in-scope JIRA story | Cannot write deterministic test cases for "signed document displayed in UI" (FR-WAIVER-002) — timing/latency expectations differ materially between webhook (near-real-time) and polling (interval-dependent) | Obtain engineering confirmation of the integration mechanism; define expected latency/refresh behavior once confirmed |
| GAP-BR-03 | GUIDE / DASH | Guide dashboard visibility scope (all-ranch bookings vs. assigned-bookings-only) is left unresolved by any in-scope JIRA story (IMHA-71 GUIDE-01's own flagged ambiguity) | Cannot write deterministic Functional or Security (data-isolation) test cases for the Guide dashboard — a wrong assumption here directly risks either over-permissive or over-restrictive test coverage | Obtain explicit confirmation of visibility scope from Product Owner before Functional/Security test design for GUIDE/DASH modules |
| GAP-BR-04 | RANCH / BOOK | FR-RANCH-003 / FR-PLATFORM-003 ("generic field-level validation") is stated to exist but no field-by-field validation rule (required/optional, format, length, allowed characters) is enumerated beyond what other FRs already state | Cannot design boundary/negative test cases for fields not otherwise covered elsewhere | Request an explicit field-validation specification (per field: type, mandatory/optional, min/max length, format/regex) for Ranch, Ranch Admin, and Booking forms |
| GAP-BR-05 | SA | FR-SA-002 states Ranch Admin is barred from creating Ranches/other Ranch Admins and may edit only Full Name on another Ranch Admin's record, but the PRD does not state whether this authorization boundary is enforced server-side (API layer) or only in the UI | Cannot confirm whether a direct API call as Ranch Admin would also be blocked — a potential authorization-bypass risk if server-side enforcement is assumed but not actually implemented | Confirm with engineering whether FR-SA-002 is enforced at the API/data layer, consistent with how FR-GUIDE-001 explicitly requires API-level enforcement |

---

# Missing Validations

| Module | Validation | Impact | Recommendation |
|--------|------------|--------|----------------|
| RANCH / SA / BOOK / STAFF | No input-format rules (regex patterns, character limits, special-character/Unicode/emoji handling) are specified beyond "valid email/phone format" for any form field | Cannot design meaningful boundary/negative/edge-case test cases (max length, special characters, Unicode, emoji) per the framework's mandatory Edge Cases list | Obtain a field-level format specification (regex/length/allowed character set) per form before Boundary/Negative test design |
| All forms | No standardized error-message catalog exists (exact wording/placement of validation error messages) | UI test cases cannot assert exact expected error text; only presence/absence of an error state can be tested | Request an error-message catalog or confirm Figma/UI designs as the authoritative source once available |
| SA (Create Ranch Admin) | Password validation is stated only as "min 8 characters" — no complexity rule (uppercase/lowercase/digit/special character), no maximum length, no reuse/history rule | Cannot design a complete password-policy negative/boundary test suite | Obtain full password policy from engineering/security team |
| GUEST | FR-GUEST-003 relaxes several fields to optional, but does not state whether any format validation still applies to those fields *when provided* (e.g., is Zip Code format-checked if the user chooses to enter it) | Ambiguous whether an optional field, once populated, is still subject to format validation | Confirm whether optional fields retain format validation when non-empty |

---

# Missing Business Workflows

| Workflow | Gap | Impact | Recommendation |
|----------|-----|--------|----------------|
| BOOK — Cancellation | No FR describes a Cancel Booking workflow (what triggers cancellation, who can cancel, downstream effects on Harvest/Invoice/Waiver records for a cancelled booking) despite "Cancelled" being a named status in FR-BOOK-002 | Cannot test the exception/recovery flow for a cancelled booking's effect on already-dispatched waivers, in-progress harvest logs, or invoices | Confirm whether a dedicated Cancel Booking FR exists in JIRA but was out of the 54 in-scope issues, or whether this is a genuine scope gap |
| GUEST — Non-submission recovery | FR-GUEST-004's personal-details email form has no stated recovery/reminder flow if the hunter/guest never responds | Cannot test whether/how the system follows up, or whether booking/check-in proceeds regardless | Obtain the intended behavior for non-response (blocking vs. non-blocking) |
| HARVEST — Tag-exhaustion recovery | FR-HARVEST-004 blocks harvest-log creation once a category's tag allocation is exhausted, but no alternate/recovery flow is defined (e.g., can Super Admin manually add tags mid-season, and does this require workflow approval) | Cannot test the recovery path once the exception (blocked creation) is confirmed | Confirm the tag-replenishment workflow, if one exists |
| WAIVER — Expired/unsigned waiver at check-in | FR-WAIVER-001–004 describe dispatch, display, and resend, but no FR states what happens operationally if a waiver remains unsigned at scheduled check-in time (is check-in blocked, is it flagged only, does auto check-in still proceed per FR-BOOK-008) | Cannot test the intersection of the auto check-in workflow (FR-BOOK-008) and an outstanding/unsigned waiver — a compliance-relevant edge case | Obtain the business rule for unsigned-waiver handling at check-in time |

---

# Missing User Roles & Permissions

| Role | Missing Requirement | Impact |
|------|---------------------|--------|
| Guide | Exact dashboard data-visibility scope (assigned-only vs. all-ranch) is unresolved (see GAP-BR-03) | Cannot finalize role-based data-isolation test cases for the Guide role on the Dashboard module |
| Hunter / Guest | Confirmed not a system login role in any in-scope story — but no FR explicitly states this is an intentional design decision (vs. an omission), and no FR describes what a Hunter/Guest *can* access without a login (e.g., can they view/sign their own waiver link without authenticating) | Cannot test the Hunter/Guest's actual interaction surface (e.g., the Docuseal signing link flow) without knowing whether any unauthenticated access exists |
| Ranch Admin | Server-side enforcement of the Full-Name-only edit restriction on another Ranch Admin's record is unconfirmed (see GAP-BR-05). Note: this permission boundary was originally sourced from IMHA-91/IMHA-92, which referred to it as the "Admin" role — confirmed by the user on 2026-11-03 to be the same role as Ranch Admin, not a distinct 4th role. | Cannot confirm whether an Authorization/Security test case for this boundary should target the API layer, UI layer, or both |

---

# Missing UI Requirements

| Screen | Missing Requirement | Impact |
|--------|---------------------|--------|
| All screens | No Figma file, screenshot, or wireframe was supplied; no component library, responsive-behavior spec, or breakpoint definition exists | UI Test Cases cannot be generated with layout/alignment/responsive precision — only narrative-derived structural checks are possible |
| Booking Edit (status-conditional) | No UI spec for which fields render as read-only vs. editable vs. hidden per status (tied to GAP-BR-01) | UI test cases for booking-status-conditional rendering cannot be written deterministically |
| All forms | No error-message catalog or inline-validation UI behavior spec (e.g., on-blur vs. on-submit validation timing) | Cannot test validation-timing UI behavior with precision |
| Login screen | Entire screen layout unknown pending AUTH mechanism resolution (GAP-FR-01) | UI test cases for Login cannot be authored at all until the mechanism is confirmed |

---

# Missing API Requirements

| API | Missing Information | Impact |
|-----|---------------------|--------|
| All APIs | No Swagger/OpenAPI specification, full endpoint inventory, authentication header scheme, or error-response format catalog exists | API Test Case generation is limited to the 3 explicitly-stated API rules (FR-BOOK-003, FR-HARVEST-002, FR-GUIDE-001); no schema-validation, status-code, or negative-payload test cases can be written for any other endpoint |
| Authentication API | No endpoint, request/response schema, or token format is defined, pending GAP-FR-01 | Cannot write any API-level Authentication test case (login, token refresh, logout) |
| Booking API | FR-BOOK-003 defines the `ranch` object read/write contract but does not state full response schema, pagination parameters, or error-response format for invalid `ranchId` | Cannot write complete schema-validation or negative-payload API test cases beyond the one stated contract rule |
| Guide-authenticated APIs | FR-GUIDE-001 states pricing must not be returned, but does not enumerate which specific endpoints/fields are in scope of this restriction (e.g., does it apply to Booking detail, Invoice detail, or both) | Cannot write an exhaustive negative-API security test suite without knowing the full set of affected endpoints |

---

# Missing Non-Functional Requirements

| Category | Gap | Impact |
|----------|-----|--------|
| Performance | No latency, throughput, concurrent-user, or response-time target stated anywhere in the 54 in-scope issues | Performance Test Scenarios cannot be generated with any target threshold — only generic scenario categories (load/stress/spike/soak) can be recommended without pass/fail criteria |
| Security | No password-complexity policy (beyond 8-char minimum), session-timeout rule, CSRF/XSS/SQL-injection statement, or OWASP-Top-10-style requirement exists | Security Test Cases can be designed against general OWASP guidance but not against any project-specific acceptance threshold |
| Accessibility | No WCAG conformance level or specific accessibility behavior (keyboard nav, screen-reader, color contrast, ARIA) is stated, apart from one incidental "accessible semantic HTML" code-quality note on IMHA-6 | Accessibility Test Cases cannot be scoped to a target WCAG level (A/AA/AAA) — testing would default to general best practice only, with no defined pass/fail bar |
| Availability / Compliance | No SLA, uptime target, disaster-recovery, backup, or regulatory-compliance (e.g., data retention for harvest/compliance records) requirement exists, despite the Harvest Log module being explicitly compliance-critical | Cannot test or validate any availability/compliance acceptance criteria; regulatory risk is notable given the domain (hunting-tag compliance data) |

**Severity:** High — this is a pervasive gap spanning all five NFR categories, not an isolated item. **Recommendation:** Before NFR-driven test design (Performance, Security edge-cases beyond functional rules, Accessibility, Availability/Compliance) can proceed meaningfully, Product Owner/Engineering must supply at minimum: (1) target response times for key transactions (login, booking creation, invoice generation), (2) a stated password/session security policy, (3) a target WCAG conformance level, and (4) any regulatory data-retention requirement applicable to harvest/compliance records.

---

# Ambiguous Requirements

| Requirement | Observation | Clarification Required |
|------------|-------------|------------------------|
| FR-BOOK-002 | States status-conditional edit/view behavior exists but not its field-level detail | What exact fields are editable/read-only/hidden in each of Pending/Approved/Cancelled? |
| FR-GUEST-004 | States an email form exists to capture personal details but not its field list, trigger timing (immediately at booking creation vs. some other point), or non-submission behavior | What fields does the form contain, when exactly is it sent, and what happens if never completed? |
| FR-WAIVER-002 (notification mechanism) | PRD's own supporting context flags webhook vs. polling as unresolved | Which mechanism does Docuseal integration actually use, and what latency should the UI "signed document displayed" behavior assume? |
| Guide dashboard visibility (ties to FR-DASH-001 / GUIDE module) | "Assigned bookings" vs. "all ranch bookings" is unresolved per IMHA-71 GUIDE-01 | Which visibility scope is correct for the Guide role? |
| FR-HARVEST-003 ("no audit trail") | States explicitly no audit trail exists for Super Admin destructive edits on compliance data, without clarifying whether this is an accepted risk or an oversight | Is the absence of an audit trail an intentional, accepted design decision, or should one be added before release? |

---

# Conflicting Requirements

| Requirement | Conflict | Recommendation |
|------------|----------|----------------|
| FR-AUTH-001 vs. FR-AUTH-002 | Candidate A (password-based login, IMHA-82/IMHA-91, both JIRA Status: Done) vs. Candidate B (passwordless OTP via AWS Cognito, IMHA-71/AUTH-02). The PRD's own Resolution Log records this as its one formal Conflict (CONF-1); the user selected Candidate B as "directionally correct for a future pass" but neither candidate was authored as an active FR this pass. | Obtain explicit engineering confirmation of which mechanism the live system actually implements (Candidate A appears already built per JIRA status "Done," which is itself worth flagging back to the Product Owner as a potential signal). Until resolved, treat Authentication as a blocking dependency for all downstream test design — do not assume either mechanism. |

No other direct source-vs-source conflicts were identified, consistent with RequirementAnalysis.md's own finding that CONF-1 was the PRD's only formal Conflict among its 17 resolved review items.

---

# Duplicate Requirements

| Requirement | Duplicate Location | Recommendation |
|------------|-------------------|----------------|
| FR-STAFF-001 / FR-STAFF-002 | Both relate to Guide/Staff creation (IMHA-88/19/35 vs. IMHA-17); RequirementAnalysis.md records these as intentionally kept separate per the PRD's own Duplicate Report item F6 | No action required — already resolved and documented as a deliberate non-merge; retained here only for completeness of this Gap Analysis's duplicate check, not as a new finding |
| FR-HARVEST-001 / FR-HARVEST-002 / FR-HARVEST-003 | All three build on the same Harvest Log entity (IMHA-89, IMHA-20, IMHA-140); RequirementAnalysis.md records this overlap as "accepted as-is, not merged" | No action required — already resolved; retained for completeness |

No previously unrecorded duplicate requirements were identified during this Gap Analysis pass.

---

# Assumptions

| Assumption | Reason | Validation Required |
|-----------|--------|---------------------|
| This Gap Analysis treats RequirementAnalysis.md's "Confirmed Assumptions" (ASM-1 through ASM-4) as already validated and does not re-litigate them | They were explicitly confirmed final by the user on 2026-11-02 per PRD Section 14 | None — carried forward as settled |
| The two untracked supporting documents (`IGZ - BuckHouse - Feature List (1).xlsx`, `Ranch Booking Platform (1).docx`) were not independently re-read for this Gap Analysis, consistent with RequirementAnalysis.md's own treatment | Out of scope for this capability per the task's instruction to treat RequirementAnalysis.md as the primary input; re-reading them was flagged as a follow-up item in RequirementAnalysis.md itself, not this agent's responsibility to resolve | Yes — if higher confidence on IMHA-71-sourced corroborating content is needed, an independent read of these two files is recommended as a separate follow-up, not fabricated here |
| Priority values (P1–P4) assigned to gaps in this report follow the same business/compliance/financial-criticality judgment RequirementAnalysis.md used for FR priorities | No explicit severity/priority scale for gaps was supplied by the PRD or agent spec beyond the Severity Matrix in `03-standards/testing-standards.md` | Should be reviewed/confirmed by a QA Architect or Product Owner before being used to sequence remediation work |

---

# Dependencies

| Dependency | Type | Impact |
|-----------|------|--------|
| Authentication resolution (GAP-FR-01) → all AUTH-module, Security, and precondition-dependent test design | **Resolved 2026-11-02** | Password-based mechanism confirmed; no longer blocking. AUTH-module, Security, and Session test design can proceed. |
| FR-BOOK-002 status-matrix clarification (GAP-BR-01) → Booking Functional/UI test design | Blocking (partial) | Booking happy-path test cases can proceed; status-conditional edit/view test cases cannot be finalized until clarified |
| Docuseal webhook/polling confirmation (GAP-BR-02) → Waiver Functional test design | Blocking (partial) | Waiver dispatch/resend test cases can proceed; "signed document displayed" timing-dependent test cases cannot be finalized until clarified |
| Guide dashboard visibility scope (GAP-BR-03) → Guide/Dashboard Functional and Security test design | Blocking (partial) | Role-based data-isolation test cases for the Guide dashboard cannot be finalized until scope is confirmed |
| NFR targets (Non-Functional Gap) → Performance, Accessibility, Security-threshold, Availability/Compliance test design | Blocking (full, for those categories only) | Scenario categories can be recommended, but no target-bound test case (pass/fail criteria) can be authored until targets are supplied |

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|-----------|
~~Authentication mechanism remains unresolved~~ **RESOLVED 2026-11-02** | No longer release-blocking — password-based mechanism confirmed; Login/Session test design can proceed | Closed; TestPlan.md and FunctionalTestCases.md updated accordingly |
| No measurable NFR targets exist | Performance, Security-threshold, Accessibility, and Availability/Compliance test coverage will be shallow (scenario-only, no pass/fail bar), creating a false sense of coverage if not explicitly caveated | Require Product Owner/Engineering to supply targets before claiming NFR test coverage is complete; explicitly caveat NFR Test Scenarios as "target-pending" in the Test Plan |
| Harvest Log destructive edits have no audit trail (FR-HARVEST-003) | Regulatory/compliance risk — hunting-tag and harvest data is compliance-relevant; an unaudited edit/delete capability for Super Admin creates a data-integrity and auditability gap | Confirm with Product Owner/Compliance whether this is an accepted risk for V1.0 or whether an audit trail must be added before release |
| IMHA-93 (TestLink) content remains inaccessible | Any existing legacy test case content that could inform coverage or reveal previously-known defects remains unavailable | Retry access to the TestLink instance in a follow-up pass; do not block current-pass deliverables on it (consistent with RequirementAnalysis.md's RISK-4 closure-by-exclusion) |
| No Project Context exists (`01-docs/ProjectContext/`) | Broader system/architecture context that could disambiguate several of the above gaps (e.g., session management, API authentication scheme) is simply unavailable | Recommend creating a Project Context document as a standing framework improvement, independent of this specific release |

---

# Clarification Questions

| Question | Raised To | Priority |
|----------|-----------|----------|
| ~~Which authentication mechanism does the live system actually implement~~ | — | **RESOLVED 2026-11-02: password-based (Candidate A), confirmed by user (post-PRD-approval engineering confirmation).** |
| What are the exact field-level edit/view differences for a Booking in Pending, Approved, and Cancelled status? | Business Analyst / Product Owner | P1 — High |
| What fields does the FR-GUEST-004 personal-details email form contain, when exactly is it triggered, and what happens if the hunter/guest never submits it? | Business Analyst / Product Owner | P2 — Medium |
| Does the Docuseal integration use a webhook or polling mechanism to detect a signed waiver, and what latency should the "signed document displayed" UI behavior assume? | Development Team | P2 — Medium |
| Does the Guide dashboard show only bookings assigned to that Guide, or all bookings at their ranch(es)? | Product Owner / Stakeholders | P1 — High |
| Is the absence of an audit trail for Super Admin harvest-log edits/deletes (FR-HARVEST-003) an accepted risk for V1.0, or should an audit trail be added before release? | Product Owner / Compliance Stakeholder | P2 — High |
| What are the target performance (response-time/throughput), accessibility (WCAG level), and availability (SLA/uptime) requirements for V1.0, if any? | Product Owner / Engineering | P1 — High |
| Is the Ranch Admin role's Full-Name-only edit restriction on another Ranch Admin's record (FR-SA-002) enforced server-side (API layer), or only in the UI? | Development Team | P2 — Medium |
| Is there a Cancel Booking workflow, and if so, what happens to associated Harvest Logs, Invoices, and Waivers for a cancelled booking? | Business Analyst / Product Owner | P2 — Medium |
| What is the full password-complexity policy beyond the stated 8-character minimum (uppercase/lowercase/digit/special character, max length, reuse rules)? | Development Team / Security | P2 — Medium |

---

# Recommendations

- ~~Treat Authentication (GAP-FR-01/02/03) as a release-blocking dependency~~ **GAP-FR-01 resolved 2026-11-02** (mechanism confirmed password-based) — Test Plan and Functional/Security Test Case generation may proceed on the AUTH module. GAP-FR-02 (session-timeout duration) and GAP-FR-03 (account-lockout/rate-limiting) remain open and should still be tracked.
- Obtain a Booking status × field permission matrix before generating Booking edit/view test cases (GAP-BR-01).
- Obtain FR-GUEST-004's field list, trigger timing, and non-submission behavior before generating Guest-directory-update test cases (GAP-FR-04 / Ambiguous Requirements).
- Confirm the Docuseal notification mechanism (webhook vs. polling) before generating waiver-display timing test cases (GAP-BR-02).
- Confirm Guide dashboard visibility scope before generating Guide/Dashboard Functional and Security (data-isolation) test cases (GAP-BR-03).
- Request a complete field-level validation specification (format/length/allowed characters) for all forms before Boundary/Negative test design (GAP-BR-04).
- Request at least minimal NFR targets (performance, security policy, WCAG level, availability/compliance) before claiming any NFR-category test coverage is complete; explicitly label NFR Test Scenarios as scenario-only/target-pending until then.
- Confirm whether FR-HARVEST-003's stated absence of an audit trail is an accepted risk or a requirement gap needing remediation, given the compliance-critical nature of harvest/tag data.
- Confirm server-side enforcement of the FR-SA-002 Ranch Admin authorization boundary, consistent with how FR-GUIDE-001 already requires API-level enforcement.
- Recommend (as a standing framework improvement, not specific to this release) establishing `01-docs/ProjectContext/` to reduce future ambiguity resolution time.

These are requirement-clarification recommendations only; no implementation changes are recommended, per this agent's Constraints.

---

# Overall Assessment

| Area | Rating |
|------|--------|
| Requirement Completeness | Good — 46/46 FRs active (revised 2026-11-02); AUTH module complete |
| Requirement Clarity | Moderate — most FRs clear; 5 ambiguous requirements identified |
| Business Rule Coverage | Moderate — core rules documented; 2 significant rule gaps (status matrix, notification mechanism) |
| Workflow Coverage | Moderate — happy paths well defined; several alternate/exception/recovery flows missing (cancellation, non-response, tag-exhaustion recovery, unsigned-waiver-at-checkin) |
| Testability | Moderate — AUTH is now testable (password-based mechanism confirmed 2026-11-02); NFR categories remain not testable as written (no targets); other modules are testable with noted caveats |
| Overall Quality | Moderate — strong traceability and happy-path definition, offset by one release-blocking gap (Authentication) and a pervasive NFR gap |

---

# Conclusion

The PRD and its Requirement Analysis provide a solid, well-traced foundation for most of RanchOps Pro's 12 modules, with clear happy-path workflows for Booking, Harvest, Invoicing, and Waivers. This Gap Analysis originally identified one release-blocking gap (Authentication mechanism entirely unresolved); **that gap is now resolved (2026-11-02 — password-based mechanism confirmed)**. Three business-rule gaps still block deterministic test design in their respective modules (Booking status matrix, Docuseal notification mechanism, Guide dashboard visibility), and a pervasive absence of Non-Functional targets across Performance, Security, Accessibility, and Availability/Compliance remains.

**Recommendation:** Test Plan generation and Test Design **can now proceed for all 12 modules, including Authentication**. For Booking (status matrix), Guest (FR-GUEST-004), Waiver (notification mechanism), and Guide/Dashboard (visibility scope), continue tracking the identified clarification questions as open items with explicit assumptions documented in the Test Plan until answered. NFR-driven test design should proceed only as scenario recommendations (no pass/fail targets) until targets are supplied.

---

# Sign-off

| Role | Name | Date |
|------|------|------|
| Business Analyst | | |
| Product Owner | | |
| QA Lead | | |
| QA Manager | | |

---

# Output Quality Standards

This Gap Analysis Report is:

- Complete — all 44 active FRs reviewed against the full Validation Checklist (missing requirements, business rules, validations, workflows, UI/API behavior, error handling, acceptance criteria, conflicting/duplicate/ambiguous requirements, undefined dependencies, missing integrations, missing data requirements).
- Requirement Traceable — every gap references its source Requirement ID(s)/JIRA key(s) via RequirementAnalysis.md.
- Actionable — every gap carries Impact, Severity, Recommendation, Clarification Question, and Suggested Owner (per Recommendations table / inline per-category tables).
- Business Focused and Technology Agnostic — no implementation changes recommended, only requirement-clarification actions.
- Review Ready and Stakeholder Ready — suitable as input for Test Plan Agent, Functional/UI/API/Security/Accessibility Test Design Agents.

No test cases, test plan, or requirement modifications are included in this document, per this agent's Constraints.
