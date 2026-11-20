# Requirement Analysis Report

---

# 1. Document Information

| Field | Value |
|--------|-------|
| Project Name | IGZ Marquis Hunting App ("RanchOps Pro") |
| Application | RanchOps Pro — multi-tenant ranch/hunting-property operations platform |
| Release | V1.0 (scope per PRD Section 4) |
| Version | 1.0 (derived from PRD_IMHA_2026-11-02) |
| Prepared By | AI Requirement Analysis Agent |
| Generated Date | 2026-11-02 |
| Source Documents | `07-outputs/GeneratedPRD(JIRA)/PRD_IMHA_2026-11-02.docx` (final, approved deliverable) and its markdown source `07-outputs/GeneratedPRD(JIRA)/PRD_IMHA_DRAFT_2026-11-02.md` (same approved content — read directly per run instructions). JIRA project IMHA (54 in-scope issues; traceability only, not independently re-queried this run). |

**Revision Note (2026-11-02):** CONF-1 (authentication mechanism) and RISK-2 are now RESOLVED — the user confirmed, post-PRD-approval, that the live mechanism is password-based (IMHA-82/IMHA-91's described flow). FR-AUTH-001 (Login) and FR-AUTH-002 (Session) are now confirmed, active requirements. This revision updates Sections 3, 6, 7.1, 7.3, 8, 10.1, 14, 16, 18, 20, 21, 22, and 23 accordingly; all other content is unchanged from the first run.

**Revision Note 2 (2026-11-03):** Role terminology correction applied — "Admin" (as referenced in IMHA-91/IMHA-92, FR-SA-002) was mistakenly documented as a distinct 4th role. The user confirmed on 2026-11-03 that there are exactly 3 real login roles — Super Admin, Ranch Admin, and Guide — and that "Admin" is the same role as Ranch Admin, not separate. All standalone "Admin" references throughout this document have been renamed to "Ranch Admin" (Sections 3, 6, 8, 9, 10.2, 10.4, 16); the User Roles table (Section 8) no longer lists "Admin" as its own row.

**Note on PRD footer text:** The PRD markdown file's own final line reads "Do not promote this document to the downstream Requirement Analysis workflow... until a human has responded to every item." The document's own Resolution Log (top of file) shows all 17 pending items (8 Ambiguities, 1 Conflict, 4 Material Assumptions, 4 Risks) were in fact resolved by the user on 2026-11-02, and this run was explicitly instructed that the PRD is final/approved. This Requirement Analysis treats the PRD as approved per that explicit instruction, but flags the stale footer text as a documentation inconsistency worth correcting in the PRD artifact itself (see Section 20, Ambiguities).

---

# 2. Documents Analysed

## Business Documents
- PRD: `PRD_IMHA_2026-11-02` (docx, final) / `PRD_IMHA_DRAFT_2026-11-02.md` (md source, same content) — primary and sole formal requirement source for this run.
- No BRD exists for this project (PRD Section 3 states "no separate BRD exists for this project").
- No FRD exists separately; the PRD itself serves as the functional specification.

## Design Documents
- No Figma files, screenshots, or UI mockups were supplied as inputs to this run. The PRD references UI structure (forms, wizards, dashboards) narratively but no Figma/screenshot artifacts were analyzed directly.

## Technical Documents
- No Swagger/OpenAPI spec, architecture document, or database schema was supplied as an input to this run. Technical content (API/DB notes) is limited to what the PRD itself states (e.g., FR-BOOK-003 API contract, FR-PLATFORM-002 DB migration).

## Supporting Documents
- Two files were observed as untracked in the repo working tree but were **not** supplied or referenced as inputs for this run: `01-docs/Other Required docs/IGZ - BuckHouse - Feature List (1).xlsx` and `01-docs/Other Required docs/Ranch Booking Platform (1).docx`. These appear to correspond to the "IMHA-71 supplementary content" the PRD already cites as corroborating context (PRD Section 1, "Supporting Inputs" row: "IGZ - BuckHouse - Feature List" xlsx and "Ranch Booking Platform" docx). They were not re-read independently in this run — the PRD's own citations of that content (Sections 2, 3, 6, 8.1–8.3, 8.9, 8.11, 12, 13) are relied upon as already-correlated. Flagged as an open item below (Section 20) since this Requirement Analysis did not independently verify them.

## Project Context
- `01-docs/ProjectContext/` **does not exist** in the repository (confirmed via directory listing). No Project Context documents were available to consult. This is noted as a gap, not fabricated — no background/system/domain context beyond what the PRD itself states was available this run.

## External Sources (JIRA)
- Not independently queried via the Atlassian MCP in this run. All JIRA traceability (issue keys) is taken as already-resolved from the approved PRD's own Section 17 Traceability tables, per the task's explicit instruction that the PRD is the immediate, approved source document for this analysis.

---

# 3. Executive Summary

**Application Purpose:** RanchOps Pro (IGZ Marquis Hunting App) is a multi-tenant SaaS platform for hunting-ranch operators. A Super Admin manages the platform and onboards Ranch Admins (each scoped to one or more ranches/tenants). Within a ranch, Ranch Admin users configure the property, manage bookings for hunters and guests, assign Guides, track harvested animals against a Harvest Log (with tag allocation and regulatory-compliance capture), generate post-stay invoices, and manage liability waivers via a Docuseal e-signature integration.

**Business Goals:** Replace ad hoc/manual ranch operations processes with a single system covering property configuration, bookings, guest/hunter management, staff/guide management, harvest compliance, waivers, and invoicing; support multiple ranches under centralized oversight; automate regulatory/compliance workflows; reduce manual booking-lifecycle overhead; enforce role-appropriate data visibility; and streamline financial close-out.

**Key Functional Areas (12 modules):** Authentication & Access; Super Admin & Ranch Admin Management; Ranch Configuration; Booking Management; Guest/Hunter Directory; Staff & Guide Management; Harvest Log & Compliance; Invoicing; Waivers & Document Workflow; Dashboard & Reporting; Guide Portal & Role-Based Visibility; Platform/Cross-Cutting Technical.

**Primary Users:** Super Admin (platform owner), Ranch Admin (ranch-scoped operator — note: IMHA-91/IMHA-92's "Admin" references are the same role as Ranch Admin, not a 4th role, confirmed by the user on 2026-11-03), Guide (operational, restricted role; "Staff" is the same entity/form as Guide), Hunter/Guest (booking subject, not a system login role).

**High-Level Architecture:** Multi-tenant web application; PostgreSQL datastore (migrated from SQLite); integrations with AWS Cognito/SES (authentication/notifications), Docuseal (self-hosted e-signature), and a cited-but-unconfirmed AWS S3 (harvest-photo storage). No architecture document was supplied to this run — this summary is derived entirely from PRD narrative.

**Scale of this analysis:** 46 active Functional Requirements across 12 modules, traced to 54 in-scope JIRA issues (IMHA project), with 4 JIRA issues excluded from scope entirely (IMHA-2, IMHA-106, IMHA-141, IMHA-148). **(Revised 2026-11-02: FR-AUTH-001/002, previously excluded pending engineering confirmation, are now confirmed active — password-based authentication mechanism confirmed; was 44 active/46 authored/2 excluded.)**

---

# 4. Business Objectives

**Business Problem:** Ranch operators currently rely on ad hoc/manual processes for property configuration, bookings, guest/hunter tracking, harvest compliance, waivers, and invoicing (PRD Section 3).

**Expected Business Outcome:**
- A single system replacing manual processes across all ranch operations (IMHA-1, IMHA-84, IMHA-85, IMHA-86, IMHA-88, IMHA-89, IMHA-90).
- Multi-ranch support under centralized Super Admin oversight with ranch-scoped Ranch Admin access (IMHA-85, IMHA-91, IMHA-92).
- Automated/enforced regulatory workflows: liability waivers, harvest tagging/reporting, mandatory hunter information capture (IMHA-6, IMHA-111, IMHA-153, IMHA-160, IMHA-97, IMHA-139, IMHA-184, IMHA-164).
- Reduced manual overhead via status-aware booking edits, automated check-in/out, auto-populated guest records (IMHA-4, IMHA-9, IMHA-86, IMHA-163, IMHA-87).
- Role-appropriate visibility/restriction (e.g., pricing hidden from Guides) (IMHA-137, IMHA-138).
- Streamlined financial close-out via invoice generation/download and one-invoice-per-booking integrity (IMHA-27, IMHA-56, IMHA-90, IMHA-191).

**Success Criteria / KPIs:** No explicit KPIs, target metrics, or measurable success thresholds are stated anywhere in the PRD or its source issues. **This is a confirmed gap**, not an inferred one — the PRD's own Section 11 (NFR) explicitly states "No stated performance... or availability/SLA... targets exist anywhere in the 54 in-scope issues." Treat as Missing Information (Section 20).

**Business Constraints:**
- No separate BRD exists; business objectives are derived entirely from JIRA story content (PRD Section 3, explicit statement).
- Guest Portal and Payment Processing/Stripe are explicitly confirmed Out of Scope for V1.0 (PRD Section 5, citing IMHA-71 OOS-02/OOS-03).
- ~~Authentication mechanism is unresolved pending engineering review~~ **RESOLVED 2026-11-02:** password-based mechanism confirmed (FR-AUTH-001/FR-AUTH-002); no longer a blocking constraint on downstream planning (see Section 8.1/Section 10.1).

---

# 5. Application Overview

**Modules (12):** Authentication & Access (AUTH); Super Admin & Ranch Admin Management (SA); Ranch Configuration (RANCH); Booking Management (BOOK); Guest/Hunter Directory (GUEST); Staff & Guide Management (STAFF); Harvest Log & Compliance (HARVEST); Invoicing (INVOICE); Waivers & Document Workflow (WAIVER); Dashboard & Reporting (DASH); Guide Portal & Role-Based Visibility (GUIDE); Platform/Cross-Cutting Technical (PLATFORM).

**Features:** See Section 6 (Functional Modules) and Section 10 (Functional Requirements) for the full feature inventory per module.

**Subsystems:** Ranch configuration wizard; booking lifecycle engine (create/edit/status-conditional/auto check-in-out); guest directory auto-population; staff/guide management; harvest log + tag allocation + category-conditional field display; invoicing engine with one-invoice-per-booking enforcement; waiver dispatch/tracking via Docuseal; dashboard/reporting; role-based visibility layer (Guide pricing restriction).

**Integrations (confirmed vs. unconfirmed — see Section 15 for full detail):**
- Confirmed: AWS Cognito/SES (Guide email update + invitation/password-setup emails, IMHA-172); Docuseal self-hosted e-signature (IMHA-6, IMHA-111, IMHA-153).
- Corroborated-but-not-independently-confirmed by in-scope JIRA: AWS S3 (harvest-photo storage) — PRD treats this as supporting context only (ASM-3), not a confirmed integration requirement.
- Explicitly Out of Scope: Stripe/payment processing (per IMHA-71 OOS-03); Guest Portal (OOS-02).
- Referenced but unresolved: external TestLink instance (IMHA-93) — content not retrieved (RISK-4, carried-forward open item).

**External Systems:** AWS Cognito, AWS SES, Docuseal (self-hosted), AWS S3 (unconfirmed), external TestLink instance (unresolved).

---

# 6. Functional Modules

| Module | Features | Priority |
|---|---|---|
| Authentication & Access (AUTH) | Login (password-based, confirmed 2026-11-02), Session Establishment, Password Reset (forgot-password + in-profile reset) | P1 (Critical — gating access to the whole system) |
| Super Admin & Ranch Admin Management (SA) | Create Ranch Admin; Ranch Admin role restriction (barred from Ranch/Ranch Admin creation; Full-Name-only edit) | P1 |
| Ranch Configuration (RANCH) | 5-step Ranch Create/Edit Wizard; Turnover Day booking block; generic form field validation | P1 |
| Booking Management (BOOK) | Create/Edit Booking; status-conditional behavior; Booking API ranch-object exposure; mandatory Address; Booking Classification; multi-guide assignment (up to 4); Guide Booking Request + calendar; auto check-in/out with manual-override interaction | P1 (core workflow) |
| Guest/Hunter Directory (GUEST) | Auto-population from bookings; pagination; mandatory-field relaxation; personal-details email form; technical form migration | P2 |
| Staff & Guide Management (STAFF) | Create/Edit Guide (incl. Staff/Guide CRUD + validation); narrower Create Guide flow (kept separate); Guide email update w/ Cognito sync | P2 |
| Harvest Log & Compliance (HARVEST) | Create/Edit Harvest Log; creation API+frontend integration; Super Admin enhancements (delete/edit past bookings/unlinked harvests); tag allocation & consumption; animal category management; category-conditional field display ("Other" replicates "Deer"); animal-name display; photo upload (camera/gallery) | P1 (compliance-critical) |
| Invoicing (INVOICE) | Generation/preview/download (PDF/Excel)/deletion; discard & replace; one-invoice-per-booking rule + invoiced tags/gating UI | P1 (financial close-out) |
| Waivers & Document Workflow (WAIVER) | Docuseal dispatch at booking creation; signed-document display + per-ranch template config; resend post check-in; mandatory signing extended to non-hunter guests | P1 (compliance-critical) |
| Dashboard & Reporting (DASH) | Booking Calendar + Recent Bookings + summary metric cards + Active Ranches section | P2 |
| Guide Portal & Role-Based Visibility (GUIDE) | Pricing-visibility restriction (UI + API); Whitetail Deer tag-count display | P2 |
| Platform/Cross-Cutting Technical (PLATFORM) | Pagination across 5 entities; SQLite→PostgreSQL migration; generic form validation (cross-module); TestLink test-tracking (non-functional, excluded this pass) | P2 (technical enablers) |

Priority values above are this agent's own derived judgment (PRD does not assign explicit P1–P4 priorities to modules) based on business criticality (compliance, financial, and core-workflow modules rated P1; supporting/reporting/technical modules rated P2) — marked as **Derived Information**, not a confirmed requirement.

---

# 7. Business Workflows

## 7.1 Ranch Onboarding (Super Admin) — Primary Flow
1. Super Admin creates a ranch via the 5-step wizard (FR-RANCH-001: Basic Information → Ranch Details → Rooms → Initial Settings → Review & Submit).
2. Super Admin creates a Ranch Admin and assigns them to the ranch (FR-SA-001).
3. Ranch Admin authenticates via the confirmed password-based login (FR-AUTH-001) and is scoped to that ranch only (FR-AUTH-002 session establishment).

**Alternate Flow:** Edit mode on the wizard pre-populates all 5 steps from the existing ranch record (FR-RANCH-001); shows loading/error state on fetch failure.

**Exception Flow:** Each wizard step enforces required fields before allowing "Next" — incomplete steps block progression (FR-RANCH-001).

## 7.2 Booking Lifecycle — Primary Flow
1. User creates a booking: Ranch, Check-in/out Dates, Available Rooms, Hunter Details, optional Guest Details, Guide assignment (up to 4 guides) (FR-BOOK-001, FR-BOOK-006).
2. Booking creation triggers Docuseal waiver dispatch to every listed hunter's/guest's email (FR-WAIVER-001, FR-WAIVER-004).
3. Hunters/guests may receive a personal-details email form; results reflected in Guest Directory (FR-GUEST-004, FR-GUEST-001).
4. Booking auto-checks-in on start date / auto-checks-out on end date, at the ranch's configured time/time zone (FR-BOOK-008).
5. Signed waivers display in UI; can be resent post check-in if needed (FR-WAIVER-002, FR-WAIVER-003).
6. Once checked in, a Harvest Log can be created/edited, consuming tag allocation and applying category-conditional fields (FR-HARVEST-001, FR-HARVEST-004, FR-HARVEST-006/007).
7. Once checked out, an invoice is generated/previewed/downloaded, subject to one-invoice-per-booking rule and Invoiced-tag UI (FR-INVOICE-001/002/003).

**Alternate Flow:** Booking can be edited at any point prior to/during its lifecycle; edit/view behavior is status-conditional (Pending/Approved/Cancelled) (FR-BOOK-002) — **exact field-level differences per status are an acknowledged residual gap** (PRD's own text, not this agent's inference).

**Exception Flow:** System blocks booking creation on Turnover Days (FR-RANCH-002); blocks harvest log creation once tag allocation for a category is exhausted (FR-HARVEST-004); blocks a second invoice once one exists for a booking/unlinked harvest (FR-INVOICE-003).

## 7.3 Guide Operational Flow — Primary Flow
1. Guide authenticates via the confirmed password-based login (FR-AUTH-001/FR-AUTH-002).
2. Guide sees assigned/ranch bookings on their dashboard — **visibility scope (all ranch bookings vs. only assigned) is an unresolved ambiguity flagged by the PRD's own supporting context (IMHA-71 GUIDE-01), not settled by any in-scope JIRA story.**
3. Guide can submit a new booking request via calendar view (FR-BOOK-007) — requires Ranch Admin/Super Admin approval before becoming a confirmed booking.
4. Guide can access Harvest Log and invoicing for selected bookings (FR-HARVEST-001, FR-INVOICE-001) without seeing pricing data (FR-GUIDE-001), but can see Whitetail Deer tag counts (FR-GUIDE-002).

**Exception Flow:** Guides cannot add/edit/remove guide assignments on a booking themselves — only Super Admin/Ranch Admin can (FR-BOOK-006).

---

# 8. User Roles

**Note (confirmed by the user, 2026-11-03):** "Admin" (as referenced in IMHA-91/IMHA-92) is the same role as Ranch Admin — not a separate 4th role. There are exactly 3 login roles: Super Admin, Ranch Admin, and Guide.

| Role | Permissions / Access Level | Source |
|---|---|---|
| Super Admin | Platform owner; creates/manages Ranches and Ranch Admins; full cross-tenant access; manages Animal Category list and Harvest Log Enhancements | IMHA-1, IMHA-82, IMHA-84, IMHA-85; corroborated IMHA-71 (AUTH-01, SA-01, SA-02) |
| Ranch Admin | Created by Super Admin; scoped to assigned ranch(es); role validated at login. Within the assigned ranch(es): full create/edit on Bookings, Guests, Staff & Guides, Harvest Logs, Invoices, booking adjustments; barred from creating Ranches/other Ranch Admin users; may edit only Full Name on another Ranch Admin's record | IMHA-85, IMHA-91, IMHA-92 |
| Guide | Views assigned/ranch bookings; submits booking requests; accesses Harvest Log/invoicing for selected bookings; pricing hidden; cannot access Super Admin/Config/most Ops modules | IMHA-88, IMHA-135, IMHA-137, IMHA-138, IMHA-172; corroborated IMHA-71 (AUTH-04, GUIDE-01..04) |
| Staff | Confirmed same entity/form as Guide — no distinct Staff-only fields or permission set (resolved per AMB-1) | IMHA-19, IMHA-35, IMHA-88 |
| Hunter / Guest | Booking subject(s); **not a system login role** in any in-scope story; managed via Guest Directory and captured at booking creation | IMHA-49, IMHA-86, IMHA-87, IMHA-107, IMHA-160, IMHA-164 |

**Access Levels** are tenant-scoped: each Ranch Admin/Guide sees only their assigned ranch's data (tenant data isolation — corroborated by IMHA-71 AUTH-03; no in-scope JIRA story contradicts it, but **no in-scope JIRA story independently confirms it either** — this is Derived/Corroborated Information, not a Confirmed Requirement sourced from an in-scope issue).

---

# 9. Business Rules

All extracted verbatim from PRD Section 9, with traceability preserved:

| Rule | Source |
|---|---|
| Tenant data isolation — each Ranch Admin/Guide sees only their assigned ranch's data | Corroborated IMHA-71 AUTH-03 (not independently confirmed by in-scope JIRA) |
| Ranch Admin role restriction — cannot create Ranches/other Ranch Admin users; may edit only Full Name on another Ranch Admin's record | FR-SA-002; IMHA-91, IMHA-92 |
| Turnover-day blocking — bookings cannot be created on a ranch's Turnover Day | FR-RANCH-002; IMHA-72, IMHA-1 |
| One invoice per booking (and per unlinked harvest) | FR-INVOICE-003; IMHA-90, IMHA-191 |
| Pricing hidden from Guide role at both UI and API levels | FR-GUIDE-001; IMHA-137 |
| Mandatory waiver signing extends to non-hunter guests | FR-WAIVER-004; IMHA-160 |
| Address is mandatory in Hunter Information during New Booking | FR-BOOK-004; IMHA-164 |
| Guide assignment capped at 4 per booking; only Super Admin/Ranch Admin can manage assignments | FR-BOOK-006; IMHA-171 |
| Harvest tag consumption — each harvest consumes one allocated tag | FR-HARVEST-004; IMHA-97 |
| Category-conditional harvest field/result display | FR-HARVEST-006/007; IMHA-89, IMHA-136, IMHA-184 |

**Mandatory fields** (by form, per Section 8 detail): Ranch wizard (Ranch Name, Full Address, County, Contact Phone, Contact Email, Property Size, Ranch Description, Huntable Animals, Turnover Days, Waiver Signing Deadline, Waiver Expiration, Harvest Report Email Frequency, Time Zone); Create Ranch Admin (Full Name, Email, Password, Assign to Ranch); Hunter Information Address (FR-BOOK-004); Guest mandatory-field relaxation — Age, Mobile Number, Street Address, City, State, Zip Code now **optional** (FR-GUEST-003, a relaxation, not a tightening).

**Approval Rules:** Guide Booking Requests require Ranch Admin/Super Admin approval before becoming confirmed (FR-BOOK-007).

**Calculation Rules:** Invoice tax/total calculated automatically from selected charges (FR-INVOICE-001). No other calculation rules (e.g., pricing formulas, tag-allocation formulas beyond simple consumption) are specified in source material.

---

# 10. Functional Requirements

Organized by module. Requirement ID = PRD-assigned ID (primary), with source Jira Issue Key(s) retained as the traceability reference, per the Requirement ID Assignment priority (PRD is the immediate source document and already maps its own IDs to Jira).

## 10.1 Authentication & Access (AUTH)

| Requirement ID | Jira Key(s) | Description | Priority | Status |
|---|---|---|---|---|
| FR-AUTH-001 | IMHA-82, IMHA-91, IMHA-71 | Login (password-based): `/login` page, Email + masked Password fields, "Sign In" button; validates username/email exists, password correct, and role matches | P1 | **Confirmed 2026-11-02** — password-based mechanism confirmed by user (post-PRD-approval engineering confirmation); supersedes the prior provisional OTP (Candidate B) selection; resolves CONF-1/RISK-2 |
| FR-AUTH-002 | IMHA-91, IMHA-71 | Session Establishment: a successful FR-AUTH-001 login establishes an active, role-/ranch-scoped session; session persists until logout; logout terminates the session and redirects protected-route access to Login | P1 | **Confirmed 2026-11-02** — resolves CONF-1/RISK-2; exact session-timeout/idle-expiry duration remains unspecified (open item) |
| FR-AUTH-003 | IMHA-98, IMHA-128 | Password Reset via two entry points: unauthenticated "forgot password" (login page) and authenticated "reset password" (profile); confirmed across Super Admin, Ranch Admin, Guide roles | P1 | Confirmed |

## 10.2 Super Admin & Ranch Admin Management (SA)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-SA-001 | IMHA-85 | Create Ranch Admin modal: Full Name, Email, Password (min 8 chars, masked + toggle), Assign to Ranch (all mandatory); optional "Send welcome email" toggle | P1 |
| FR-SA-002 | IMHA-91, IMHA-92 | Ranch Admin barred from Ranch/other Ranch Admin creation; where permitted, may edit only Full Name on another Ranch Admin's record | P1 |

## 10.3 Ranch Configuration (RANCH)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-RANCH-001 | IMHA-1, IMHA-84 | 5-step Ranch Create/Edit Wizard (Basic Info → Ranch Details → Rooms → Initial Settings → Review & Submit), values persist across navigation, per-step required-field validation, edit mode pre-populates | P1 |
| FR-RANCH-002 | IMHA-72 | Prevent booking creation on a Turnover Day (defect fix enforcing existing rule) | P1 |
| FR-RANCH-003 | IMHA-10 | Generic field-level validation on Ranch / Ranch Admin / Booking forms | P2 |

## 10.4 Booking Management (BOOK)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-BOOK-001 | IMHA-86 | Create Booking: Ranch, Check-in/out, Available Rooms, Hunter Details, optional Guest Details, Guide assignment; validates dates/availability/mandatory fields; generates Booking ID | P1 |
| FR-BOOK-002 | IMHA-86, IMHA-4, IMHA-9 | Edit Booking incl. status-conditional (Pending/Approved/Cancelled) edit/view behavior — **exact field-level differences per status not specified (residual gap)** | P1 |
| FR-BOOK-003 | IMHA-5 | Booking API returns `ranch` object `{id,name}` instead of bare `ranchId`; create/update still accepts `ranchId`; hunter/guest suggestions scoped per ranch | P2 (technical, ASM-2) |
| FR-BOOK-004 | IMHA-164 | Address mandatory in Hunter Information section of New Booking | P1 |
| FR-BOOK-005 | IMHA-173 | Booking Classification: Hunting / Recreational / Maintenance; defaults "Hunting"; editable by any create/edit-capable role | P2 |
| FR-BOOK-006 | IMHA-171 | Up to 4 guides per booking via multi-select; only Super Admin/Ranch Admin can manage assignments | P1 |
| FR-BOOK-007 | IMHA-135 | Guide Booking Request via "+ New Booking Request" against availability calendar; requires Ranch Admin/Super Admin approval | P1 |
| FR-BOOK-008 | IMHA-163, IMHA-38 | Auto check-in/out at ranch's configured time/time zone; manual override (Edit Check-in Status) interoperates without being silently overwritten | P1 |

## 10.5 Guest / Hunter Directory (GUEST)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-GUEST-001 | IMHA-87 | Guest Directory auto-populated from booking create/edit (Name, Email/Phone, Ranch, Type, license, waiver status) | P2 |
| FR-GUEST-002 | IMHA-61 | Guests page pagination (confirmed tested on dev) | P2 |
| FR-GUEST-003 | IMHA-49 | Age, Mobile Number, Street Address, City, State, Zip Code become optional for Guests | P2 |
| FR-GUEST-004 | IMHA-107 | Email form to capture hunter/guest personal details; submission updates profile — **exact fields/trigger timing/non-submission behavior not specified** | P2 |
| FR-GUEST-005 | IMHA-7 | GuestDetails form migrated to React Hook Form (technical, no new business behavior) | P3 (technical, ASM-2) |

## 10.6 Staff & Guide Management (STAFF)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-STAFF-001 | IMHA-88, IMHA-19, IMHA-35 | Create/Edit Guide: Ranch, Basic Details, Position, Experience Level, Years of Experience; validates mandatory fields/email/phone; default status Active; edit allows ranch/details/position/experience/status updates | P2 |
| FR-STAFF-002 | IMHA-17 | Create Guide flow renders new guide on guide list (kept separate from FR-STAFF-001 per Duplicate Report F6) | P3 |
| FR-STAFF-003 | IMHA-172 | Update Guide email post-creation + resend account-setup/password-setup email via Cognito; prior email stops receiving comms | P2 |

## 10.7 Harvest Log & Compliance (HARVEST)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-HARVEST-001 | IMHA-89 | Checked-in user creates/edits Harvest Log: Ranch, Harvest Details (date/type/quantity), Regulatory Compliance (free-text) | P1 |
| FR-HARVEST-002 | IMHA-20 | Backend API for harvest-log creation, integrated with frontend form (kept separate per F7) | P2 |
| FR-HARVEST-003 | IMHA-140 | Super Admin-only: delete harvests, add/edit/delete against past bookings, log unlinked harvests; confirmation prompt before delete; no audit trail | P2 |
| FR-HARVEST-004 | IMHA-97 | Tags allocated per ranch; each harvest consumes one tag; blocks creation once a category's allocation is exhausted | P1 |
| FR-HARVEST-005 | IMHA-139 | Super Admin CRUD on custom animal categories; available wherever animal classification is needed (confirmed tested) | P2 |
| FR-HARVEST-006 | IMHA-184 | "Other" category replicates Deer's full field set (Harvest Details + Antler Information) — confirmed general pattern (category-conditional display) | P2 |
| FR-HARVEST-007 | IMHA-136 | Final Harvest Results display animal name instead of species (same category-conditional mechanism as FR-HARVEST-006) | P2 |
| FR-HARVEST-008 | IMHA-185 | Harvest Log photo: capture new photo (camera) **or** select existing photo (camera roll/gallery) | P2 |

## 10.8 Invoicing (INVOICE)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-INVOICE-001 | IMHA-90, IMHA-27, IMHA-56 | Generate/preview invoice for checked-out booking (auto-populated Booking ID, Ranch, dates, duration, Bill To, charges, tax/total); download PDF/Excel matching preview; delete (soft) | P1 |
| FR-INVOICE-002 | IMHA-56 | After discard, create a new invoice for the same booking | P1 |
| FR-INVOICE-003 | IMHA-90, IMHA-191 | One invoice per booking/unlinked harvest; "Invoiced" tag + Booking ID/Invoice #; Select Booking list grays out invoiced items; 2 tabs (Booking / Harvests without booking), live tag updates | P1 |

## 10.9 Waivers & Document Workflow (WAIVER)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-WAIVER-001 | IMHA-6 | Docuseal waiver auto-dispatched at booking creation to every listed hunter's email, using ranch's configured template (detail confirmed via AMB-7, since IMHA-6's own text is generic coding-standards content) | P1 |
| FR-WAIVER-002 | IMHA-111 | Signed document displayed in UI post-signature; per-ranch Docuseal template selection in UI | P1 |
| FR-WAIVER-003 | IMHA-153 | "Send Waiver" button on Guest Details page — resend even after check-in | P2 |
| FR-WAIVER-004 | IMHA-160 | Mandatory waiver signing extended to non-hunter guests (same as hunters) | P1 |

## 10.10 Dashboard & Reporting (DASH)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-DASH-001 | IMHA-3, IMHA-83 | Dashboard: Booking Calendar, Recent Bookings (last 6 days), summary metric cards (Total Ranches, Active Bookings incl. pending, Ranch Admins count, Total Guides, This Month Revenue, Pending Waivers), Active Ranches section | P2 |

## 10.11 Guide Portal & Role-Based Visibility (GUIDE)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-GUIDE-001 | IMHA-137 | Pricing hidden from Guide role at both UI and API/data layer (direct API call as Guide must not return pricing) | P1 (security-relevant) |
| FR-GUIDE-002 | IMHA-138 | Whitetail Deer tag counts (Bucks/Does separately) visible to Guide role | P2 |

## 10.12 Platform / Cross-Cutting Technical (PLATFORM)

| Requirement ID | Jira Key(s) | Description | Priority |
|---|---|---|---|
| FR-PLATFORM-001 | IMHA-57 | Pagination for Bookings, Ranches, Guides/Staff, Harvest logs, Invoices (5-entity scope confirmed via Human Review Resolution) | P2 |
| FR-PLATFORM-002 | IMHA-8 | Migrate data from SQLite to PostgreSQL (technical; containerized, confirmed on dev) | P2 (technical, ASM-2) |
| FR-PLATFORM-003 | IMHA-10 | Cross-module reference to FR-RANCH-003 (same source, IMHA-10 spans Ranch/Ranch Admin/Booking forms) | P2 |
| FR-PLATFORM-004 | IMHA-93 | Test case design/execution tracking in external TestLink instance (non-functional, QA/process); content not independently retrievable — **excluded from active Dependencies/NFR citation this pass** | — (non-functional, ASM-1/RISK-4) |

**Total: 46 active Functional Requirements** (46 authored; 0 excluded — FR-AUTH-001 and FR-AUTH-002 confirmed active 2026-11-02, per PRD Section 1 and Section 17.1, revised). Previously: 44 active, 2 excluded.

---

# 11. Non-Functional Requirements

Only what is actually stated in the PRD (PRD Section 11) — nothing fabricated to fill this section:

- **Data architecture:** Migration from SQLite to PostgreSQL as production datastore (FR-PLATFORM-002; IMHA-8).
- **API contract stability:** Booking API must keep accepting `ranchId` on write while returning a richer `ranch` object on read — backward compatibility for existing clients (FR-BOOK-003; IMHA-5).
- **Security — server-side enforcement of role-based data restriction:** Pricing data must not be returned to a Guide-authenticated API call, not merely hidden in the UI (FR-GUIDE-001; IMHA-137).
- **Scalability/usability — pagination** required across Bookings, Ranches, Guides/Staff, Harvest, Invoices, Guests list views (FR-PLATFORM-001, FR-GUEST-002; IMHA-57, IMHA-61).
- **Code quality standards** (typed code, no `any`, single responsibility, no dead code, explicit error handling, accessible semantic HTML, consistent naming) — stated as engineering rules attached to the Docuseal story (IMHA-6), not as product-facing NFRs; recorded because it is the only concrete NFR-style content that story contains.

**Confirmed gap (not fabricated):** No performance (latency/throughput), availability/SLA, or accessibility-standard (e.g., WCAG level) targets exist anywhere in the 54 in-scope issues. This Requirement Analysis does not invent any such targets. See Section 19 (Risks) and Section 20 (Gaps).

---

# 12. UI Flow

No Figma files, screenshots, or UI mockups were supplied as inputs to this run (Section 2). The following UI-flow understanding is derived entirely from PRD narrative description — **Derived Information, not independently verified against a design artifact**:

- **Ranch Create/Edit:** 5-step wizard with per-step validation gating (FR-RANCH-001); edit mode shows loading/error state on fetch failure.
- **Create Ranch Admin:** modal dialog with 4 fields + optional toggle (FR-SA-001).
- **Booking creation/edit:** form-based flow with date pickers, room selection, hunter/guest detail entry, guide multi-select (FR-BOOK-001/006); status-conditional rendering for Pending/Approved/Cancelled (FR-BOOK-002) — **exact UI differences per status not specified** (confirmed gap).
- **Guide Booking Request:** calendar view showing existing bookings as "Occupied," with a "+ New Booking Request" button (FR-BOOK-007).
- **Harvest Log:** form with category-conditional field display (Deer-specific fields replicated for "Other") and a photo-capture/gallery-select option (FR-HARVEST-001/006/008).
- **Invoicing:** invoice preview before creation; "Invoiced" tags and grayed-out, non-selectable rows for already-invoiced bookings; two tabs ("Booking" / "Harvests without booking") on the Generate Invoice page (FR-INVOICE-001/003).
- **Guest Details page:** "Send Waiver" button (FR-WAIVER-003).
- **Dashboard:** Booking Calendar, Recent Bookings table, summary metric cards, Active Ranches section (FR-DASH-001).

No component library, responsive-behavior spec, or error-message catalog is available from the PRD alone — these would require Figma/screenshot input not supplied this run (see Section 20).

---

# 13. API Dependencies

- **Booking API** (list/detail/create/update): must return a `ranch` object (`{id, name}`) on read; continues accepting `ranchId` on write (FR-BOOK-003; IMHA-5). Hunter/guest search suggestions scoped per selected ranch.
- **Harvest Log Creation API**: backend API integrated with the frontend harvest-log creation form (FR-HARVEST-002; IMHA-20).
- **Guide-authenticated API calls**: must not return pricing data under any circumstance (FR-GUIDE-001; IMHA-137) — a hard, testable API-contract rule.

No Swagger/OpenAPI specification was supplied this run; the above is the full extent of API behavior stated in the PRD. Full endpoint inventory, request/response schemas, authentication headers, and error-response formats are **not available** and constitute a confirmed gap for downstream API Test Case generation (see Section 20).

---

# 14. External Integrations

| Integration | Status | Source |
|---|---|---|
| AWS Cognito / SES | Confirmed in-scope (Guide email update + invitation/password-setup email resend) | IMHA-172 |
| AWS Cognito (passwordless OTP login) | Directionally accepted (CONF-1) as the likely future mechanism, but **not** a confirmed/numbered FR this pass | IMHA-71 AUTH-02; excluded via FR-AUTH-001/002 |
| Docuseal (self-hosted e-signature) | Confirmed in-scope | IMHA-6, IMHA-111, IMHA-153; corroborated IMHA-71 INT-01/INT-02 |
| Docuseal webhook vs. polling for signature notification | **Unresolved** — PRD's own supporting context (IMHA-71) flags this itself; no in-scope JIRA story settles it | IMHA-71 (self-flagged ambiguity, not resolved by this PRD) |
| AWS S3 (harvest-photo storage) | Corroborated by supporting context only (IMHA-71 POST-04); **no in-scope JIRA story confirms S3 specifically**; FR-HARVEST-008 confirms upload behavior, not storage backend | IMHA-71; ASM-3 |
| Stripe (payment processing) | **Confirmed Out of Scope** for V1.0 | IMHA-71 Feature List, OOS-03 |
| External TestLink instance | Referenced (IMHA-93) but content not retrieved; excluded from active citation this pass | IMHA-93; RISK-4 |

---

# 15. Data Validations

Extracted directly from PRD content (no validation invented beyond what is stated):

- **Ranch wizard:** Ranch Name, Full Address, County, Contact Phone, Contact Email (required, Basic Information); Property Size, Ranch Description, Huntable Animals (required, Ranch Details; at least one Huntable Animal required); Turnover Days, Waiver Signing Deadline, Waiver Expiration (default 365), Harvest Report Email Frequency, Time Zone (required, Initial Settings). Each step enforces its own required fields before "Next."
- **Create Ranch Admin:** Full Name (mandatory), Email (mandatory, valid format), Password (mandatory, min 8 characters), Assign to Ranch (mandatory).
- **Booking creation:** check-out date after check-in date; room availability; mandatory fields enforced; Address mandatory in Hunter Information (FR-BOOK-004).
- **Guide creation:** Name, Email, Phone validated for mandatory presence and valid email/phone format (FR-STAFF-001).
- **Guest records:** Age, Mobile Number, Street Address, City, State, Zip Code are **optional** (relaxed from previously mandatory) (FR-GUEST-003).
- **Generic field validation:** stated as applying to Ranch, Ranch Admin, and Booking forms, with no further rule-level detail beyond what each form's own FR already specifies (FR-RANCH-003/FR-PLATFORM-003; IMHA-10) — **this is itself a confirmed gap**: the PRD states the requirement exists but does not enumerate the specific validation rules per field beyond what's already listed elsewhere.

No input-format rules (regex patterns, character limits, special-character handling) are specified beyond "valid email/phone format" — exact format rules are not available in source material (gap, see Section 20).

---

# 16. Security Considerations

- **Role-based data restriction enforced server-side:** Guide-authenticated API calls must not return pricing data — explicitly both a UI and API/data-layer rule (FR-GUIDE-001; IMHA-137).
- **Tenant data isolation:** Ranch Admin/Guide must not see another ranch's data — corroborated by supporting context (IMHA-71 AUTH-03) but **not independently confirmed by any in-scope JIRA story** (flag as Derived/Corroborated, not Confirmed).
- ~~**Authentication mechanism is entirely unresolved**~~ **RESOLVED 2026-11-02:** password-based mechanism confirmed (FR-AUTH-001/FR-AUTH-002), with role validation (username exists, password correct, role matches) and a confirmed 8-character minimum password length (no additional complexity/reuse rule). Session-management detail beyond establishment/termination (exact timeout/idle-expiry duration) and account-lockout behavior remain unspecified and are still open items, but authentication itself is no longer a blocking dependency for security test planning.
- **Ranch Admin role restriction:** Ranch Admin barred from creating Ranches/other Ranch Admin users; limited to Full-Name-only edit on another Ranch Admin's record (FR-SA-002) — an authorization boundary that should be verified server-side, though the PRD does not explicitly state whether this is enforced at the API layer (gap).
- No password-policy detail (beyond "min. 8 characters" for Ranch Admin creation, FR-SA-001), no session-timeout rule, no CSRF/XSS/SQL-injection statement, and no OWASP-Top-10-style requirement appears anywhere in the PRD. This is a confirmed gap, not an omission by this agent (see Section 20).

---

# 17. Accessibility Considerations

No accessibility requirement, WCAG conformance level, or specific accessibility behavior (keyboard navigation, screen-reader support, color contrast, ARIA labeling) is stated anywhere in the PRD, except the single incidental mention of "accessible semantic HTML" listed as an engineering/code-quality rule attached to the Docuseal story (IMHA-6; PRD Section 11). This is a **confirmed gap**, explicitly acknowledged in the PRD's own NFR section ("No stated... accessibility-standard... targets exist anywhere in the 54 in-scope issues"). No accessibility requirement is fabricated here to fill the gap.

---

# 18. Risks

All sourced from PRD Section 16 (already resolved by the user on 2026-11-02), carried forward with status preserved:

| Risk | Status | Detail |
|---|---|---|
| RISK-1 — Staff-requirements coverage gap (IMHA-19, IMHA-35, IMHA-88 — FR-STAFF-001) | **Closed/Mitigated** | AMB-1 resolution (Staff = Guide, no distinct fields) eliminates the gap. |
| RISK-2 — Authentication-mechanism conflict's downstream impact (IMHA-82, IMHA-91, IMHA-71 — FR-AUTH-001/002) | **Resolved/Closed 2026-11-02** | Password-based mechanism confirmed by the user (post-PRD-approval engineering confirmation), superseding the PRD's prior provisional OTP selection. FR-AUTH-001/002 are now confirmed, active requirements. Functional/Security Test Case authoring for the Authentication module can proceed. |
| RISK-3 — Minors/child-hunter handling undocumented (IMHA-106, excluded) | **Closed/Accepted as documented** | No FR exists for this area; consistent with existing exclusion. |
| RISK-4 — External TestLink content inaccessible (IMHA-93) | **Closed by exclusion** | Citation excluded from active Dependencies/NFR content this pass; retained in traceability only. |

**Additional risks identified by this Requirement Analysis (not previously in the PRD's risk list), flagged as Derived Information:**
- **No measurable NFR targets** (performance/availability/accessibility) — risk that downstream Test Plan/Test Case generation cannot produce meaningful NFR-based test scenarios without first obtaining targets from engineering/stakeholders.
- **No Project Context available** (`01-docs/ProjectContext/` does not exist) — risk that broader system/architecture context that could disambiguate some of the above gaps is simply unavailable, not merely unconsulted.
- **Unverified supporting documents** — the two untracked files in `01-docs/Other Required docs/` (IGZ BuckHouse Feature List xlsx, Ranch Booking Platform docx) were not independently re-read this run; if the PRD's own prior correlation of this content is incomplete, this analysis would inherit that gap silently. Recommend an explicit independent read of these two files in a follow-up pass if higher confidence on IMHA-71-sourced corroborating content is needed.

---

# 19. Assumptions

All material assumptions below were already confirmed as final by the user on 2026-11-02, per PRD Section 14 (ASM-1 through ASM-4) — restated here with traceability, as **Confirmed Assumptions** (not Confirmed Requirements):

- **ASM-1:** IMHA-93 remains non-functional QA/process tracking, not a numbered business Functional Requirement.
- **ASM-2:** IMHA-8 (DB migration) and IMHA-5 (API technical change) remain non-functional/technical requirements, not business-facing FRs.
- **ASM-3:** IMHA-71's uncorroborated supplementary detail (e.g., AWS S3 storage backend) remains corroborating/background context only, never promoted to a confirmed standalone requirement.
- **ASM-4:** The four excluded issues (IMHA-2, IMHA-106, IMHA-141, IMHA-148) remain confirmed out of scope, not merely undocumented.

**This Requirement Analysis's own additional assumption (Derived, not PRD-sourced):** Priority values assigned in Sections 6 and 10 (P1–P4) are this agent's own derived judgment based on stated business/compliance/financial criticality, since the PRD itself does not assign explicit priority levels to any requirement. These should be reviewed/confirmed by a QA Architect or product owner before being used to drive Test Plan sequencing.

---

# 20. Dependencies

Extracted directly from PRD Section 12, plus this agent's cross-module observations:

- FR-AUTH-002 (role-validated session establishment per FR-AUTH-001 login) depends on FR-SA-001 (Ranch Admin must exist) — IMHA-91 ↔ IMHA-85. **Note:** RISK-2 is resolved 2026-11-02; this dependency is now active.
- FR-GUEST-001 depends on FR-BOOK-001/002 (booking create/edit data) — IMHA-87 ↔ IMHA-86.
- FR-BOOK-002's status-conditional resolution depends on FR-BOOK-001's base mechanics — IMHA-4, IMHA-9 ↔ IMHA-86.
- FR-HARVEST-004 (tag allocation/consumption) is a dependency for FR-GUIDE-002 (Whitetail Deer tag counts) — IMHA-97 ↔ IMHA-138.
- FR-WAIVER-002 depends on FR-WAIVER-001 having already dispatched — IMHA-6 ↔ IMHA-111.
- FR-RANCH-002 depends on the Turnover Days setting in FR-RANCH-001 — IMHA-72 ↔ IMHA-1.
- FR-HARVEST-002/003 both build on the FR-HARVEST-001 entity — IMHA-20, IMHA-140 ↔ IMHA-89 (overlap accepted as-is, not merged).
- FR-PLATFORM-004 (IMHA-93)'s dependency citation is **excluded** from the active dependency list this pass per RISK-4 (external TestLink content not corroborated) — retained in traceability only.

**Internal:** All 12 modules are interdependent through the Booking entity (central to Guest, Harvest, Invoice, Waiver modules).
**External:** AWS Cognito/SES (confirmed), Docuseal (confirmed), AWS S3 (unconfirmed), Stripe (out of scope), external TestLink (unresolved).
**Infrastructure:** PostgreSQL migration (FR-PLATFORM-002) is itself a dependency for any data-layer testing that assumes the production datastore rather than the legacy SQLite store.

---

# 21. Requirement Conflicts

**CONF-1 — Authentication Mechanism — RESOLVED 2026-11-02:**
- **Candidate A** (IMHA-82, IMHA-91, both JIRA Status: Done): password-based login at `/login` (Email + masked Password + "Sign In"; validates username exists, password correct, role matches).
- **Candidate B** (IMHA-71/AUTH-02, supporting-document source): passwordless OTP login via AWS Cognito (email → allowlist validation → 6-digit emailed OTP; no password field).
- **Original PRD resolution:** User provisionally selected Candidate B as "directionally correct for a future pass," while excluding both candidates as active FRs pending engineering confirmation (RISK-2).
- **Final resolution (this session, 2026-11-02, post-PRD-approval engineering confirmation):** The user confirmed the live system implements **Candidate A (password-based login)**. This supersedes the PRD's original provisional selection of Candidate B. FR-AUTH-001 (Login) and FR-AUTH-002 (Session Establishment) are now confirmed, active requirements using Candidate A's flow (Section 10.1). Downstream Test Plans, Test Cases, and Security Test Cases for the AUTH module should be authored against the password-based mechanism.

No other direct, source-vs-source conflicts were identified in the PRD content reviewed. (The PRD's own Resolution Log states this was the only formal Conflict among its 17 resolved review items — 8 were Ambiguities, 4 Material Assumptions, 4 Risks, 1 Conflict.)

---

# 22. Requirement Gaps

Per this agent's Constraints, a full formal Gap Analysis is explicitly **not** generated here — that is a separate downstream capability (Gap Analysis Agent, capability 2). This section only notes the existence and nature of gaps observed during this analysis, for that later capability to formalize:

- ~~Authentication mechanism entirely unresolved (FR-AUTH-001/002 excluded)~~ **RESOLVED 2026-11-02** — password-based mechanism confirmed; no longer a gap.
- IMHA-93/TestLink content not retrieved — excluded from active citation pending retrieval.
- `01-docs/ProjectContext/` does not exist — no background/system context available.
- No measurable NFR targets (performance, availability/SLA, accessibility/WCAG level) anywhere in source material.
- FR-BOOK-002's per-status (Pending/Approved/Cancelled) field-level edit/view differences not specified.
- FR-GUEST-004's exact fields, trigger timing, and non-submission behavior not specified.
- FR-RANCH-003/FR-PLATFORM-003's "proper field-level validation" not enumerated beyond what other FRs already state.
- Docuseal webhook-vs-polling question (IMHA-71's own self-flagged ambiguity) left unresolved by any in-scope JIRA story.
- Guide dashboard visibility scope (all ranch bookings vs. only assigned) left unresolved by any in-scope JIRA story (IMHA-71 GUIDE-01's own flagged ambiguity).
- Two untracked supporting documents in `01-docs/Other Required docs/` were not independently re-read this run (see Section 18).
- No API specification (Swagger/OpenAPI), architecture document, or database schema was supplied, limiting API/DB-level requirement depth (Sections 13, 14 of template N/A beyond what's in Section 13 above).

These are flagged for the Gap Analysis Agent (capability 2) to formalize into a complete Requirement Gap Report — not resolved or elaborated further here.

---

# 23. Requirement Traceability

Full PRD Requirement ID → JIRA Issue Key map (restated from PRD Section 17.1, with PRD ID as primary and JIRA Key(s) as the traceability reference, per the Requirement ID Assignment priority):

| Requirement ID | JIRA Issue Key(s) | Module | Status |
|---|---|---|---|
| FR-AUTH-001 | IMHA-82, IMHA-91, IMHA-71 | AUTH | Active (Confirmed 2026-11-02; RISK-2/CONF-1 resolved) |
| FR-AUTH-002 | IMHA-91, IMHA-71 | AUTH | Active (Confirmed 2026-11-02; RISK-2/CONF-1 resolved) |
| FR-AUTH-003 | IMHA-98, IMHA-128 | AUTH | Active |
| FR-SA-001 | IMHA-85 | SA | Active |
| FR-SA-002 | IMHA-91, IMHA-92 | SA | Active |
| FR-RANCH-001 | IMHA-1, IMHA-84 | RANCH | Active |
| FR-RANCH-002 | IMHA-72 | RANCH | Active |
| FR-RANCH-003 | IMHA-10 | RANCH | Active |
| FR-BOOK-001 | IMHA-86 | BOOK | Active |
| FR-BOOK-002 | IMHA-86, IMHA-4, IMHA-9 | BOOK | Active |
| FR-BOOK-003 | IMHA-5 | BOOK | Active (technical) |
| FR-BOOK-004 | IMHA-164 | BOOK | Active |
| FR-BOOK-005 | IMHA-173 | BOOK | Active |
| FR-BOOK-006 | IMHA-171 | BOOK | Active |
| FR-BOOK-007 | IMHA-135 | BOOK | Active |
| FR-BOOK-008 | IMHA-163, IMHA-38 | BOOK | Active |
| FR-GUEST-001 | IMHA-87 | GUEST | Active |
| FR-GUEST-002 | IMHA-61 | GUEST | Active |
| FR-GUEST-003 | IMHA-49 | GUEST | Active |
| FR-GUEST-004 | IMHA-107 | GUEST | Active |
| FR-GUEST-005 | IMHA-7 | GUEST | Active (technical) |
| FR-STAFF-001 | IMHA-88, IMHA-19, IMHA-35 | STAFF | Active |
| FR-STAFF-002 | IMHA-17 | STAFF | Active |
| FR-STAFF-003 | IMHA-172 | STAFF | Active |
| FR-HARVEST-001 | IMHA-89 | HARVEST | Active |
| FR-HARVEST-002 | IMHA-20 | HARVEST | Active |
| FR-HARVEST-003 | IMHA-140 | HARVEST | Active |
| FR-HARVEST-004 | IMHA-97 | HARVEST | Active |
| FR-HARVEST-005 | IMHA-139 | HARVEST | Active |
| FR-HARVEST-006 | IMHA-184 | HARVEST | Active |
| FR-HARVEST-007 | IMHA-136 | HARVEST | Active |
| FR-HARVEST-008 | IMHA-185 | HARVEST | Active |
| FR-INVOICE-001 | IMHA-90, IMHA-27, IMHA-56 | INVOICE | Active |
| FR-INVOICE-002 | IMHA-56 | INVOICE | Active |
| FR-INVOICE-003 | IMHA-90, IMHA-191 | INVOICE | Active |
| FR-WAIVER-001 | IMHA-6 | WAIVER | Active |
| FR-WAIVER-002 | IMHA-111 | WAIVER | Active |
| FR-WAIVER-003 | IMHA-153 | WAIVER | Active |
| FR-WAIVER-004 | IMHA-160 | WAIVER | Active |
| FR-DASH-001 | IMHA-3, IMHA-83 | DASH | Active |
| FR-GUIDE-001 | IMHA-137 | GUIDE | Active |
| FR-GUIDE-002 | IMHA-138 | GUIDE | Active |
| FR-PLATFORM-001 | IMHA-57 | PLATFORM | Active |
| FR-PLATFORM-002 | IMHA-8 | PLATFORM | Active (technical) |
| FR-PLATFORM-003 | IMHA-10 | PLATFORM | Active (cross-ref to FR-RANCH-003) |
| FR-PLATFORM-004 | IMHA-93 | PLATFORM | Active (non-functional; Dependencies/NFR citation excluded this pass) |

**Excluded Issues (not carried into any requirement):** IMHA-2, IMHA-106, IMHA-141, IMHA-148 (all excluded by human decision during JIRA ingestion review — see PRD Section 17.2).

**Cross-reference only (not a standalone requirement):** IMHA-71 — cited as supporting/corroborating context throughout Sections 2, 3, 6, 8.1–8.3, 8.9, 8.11, 12, 13, and 5 of the PRD.

**Coverage check:** All 54 in-scope JIRA keys are represented above (directly or via cross-reference), matching PRD Section 17.3's own full coverage check. **46 Requirement IDs are active as of 2026-11-02** (previously 44 active, 2 excluded pending engineering confirmation — FR-AUTH-001/002 now confirmed active).

---

**End of Requirement Analysis Report.**

Per this agent's Constraints: no test cases, test plan, gap analysis, or automation code are generated in this document. This report is prepared as input for the Gap Analysis Agent, Test Plan Agent, Functional/UI/API/Security/Accessibility Test Case Agents, and Automation Framework Agent.
