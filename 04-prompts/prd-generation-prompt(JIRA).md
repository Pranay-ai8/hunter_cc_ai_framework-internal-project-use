# PRD Generation Prompt

## Objective

Generate a comprehensive, structured, and traceable Product Requirement Document from approved JIRA stories and relevant project context.

The generated PRD will be consumed by the existing Requirement Analysis workflow.

JIRA remains the source of truth for the original User Stories.

---

# Inputs

## Primary Inputs

- Approved JIRA Story Inventory
- Duplicate Story Report
- Human duplicate-review decisions

## Supporting Inputs

- Project Context
- BRD
- Existing PRD
- Business Rules
- Architecture Documents
- Other relevant project documentation

Use only relevant and available information.

---

# JIRA Story Analysis

For each approved JIRA story:

- Understand business intent.
- Identify user or actor.
- Identify capability.
- Identify feature.
- Identify workflow.
- Identify acceptance criteria.
- Identify business rules.
- Identify dependencies.
- Identify relationships.

Preserve the original JIRA Issue Key.

---

# Duplicate Handling

Respect the Duplicate Story Report and explicit human decisions.

## If Consolidation Is Approved

- Consolidate the common business intent.
- Preserve all source JIRA Issue Keys.
- Create a single PRD requirement where appropriate.

## If Consolidation Is Not Approved

- Keep requirements separate.
- Preserve individual JIRA traceability.

## If Human Decision Is Pending

- Do not consolidate.
- Preserve affected stories separately.
- Flag the PRD for human review.

Never silently merge requirements.

---

# Project Context

Use Project Context to understand:

- Product background
- Business/domain context
- Product scope
- System boundaries
- Major modules
- Terminology
- Technology/platform context
- Dependencies
- Constraints

Project Context is supporting context.

Do not convert contextual information into confirmed requirements unless explicitly defined as a requirement.

---

# PRD Structure

Generate the PRD using the approved organizational PRD structure.

Where supported, include:

1. Document Overview
2. Product Overview
3. Business Objectives
4. Scope
5. Out of Scope
6. User Roles
7. Product Features
8. Functional Requirements
9. Business Rules
10. End-to-End Workflows
11. Non-Functional Requirements
12. Dependencies
13. Integrations
14. Assumptions
15. Ambiguities
16. Risks
17. Traceability

Do not fabricate content merely to complete a section.

---

# Functional Requirements

Assign unique PRD Requirement IDs.

Example:

```text
FR-AUTH-001
FR-AUTH-002
FR-PAY-001
```

Every requirement must carry its source JIRA Issue Key(s).

---

# Human-in-the-Loop

This is a blocking checkpoint, not an advisory note — it applies beyond pending duplicate decisions.

Pause and request explicit human review before finalizing and saving the PRD when any of the following is non-empty:

- Pending duplicate-consolidation decisions (per the Duplicate Handling section above).
- Ambiguities — any story or requirement whose business intent could not be reliably determined.
- Conflicts — contradictory information between stories, or between JIRA and a supporting input (BRD, existing PRD, Architecture, etc.).
- Material Assumptions — anything the PRD had to assume because the source stories were incomplete.
- Risks identified during PRD authoring that materially affect scope.

For each item requiring review, include:

- Affected JIRA Issue Key(s) / PRD Requirement ID
- Finding
- Evidence
- Impact on the PRD
- Recommended action

Never ask an open-ended "how would you like to handle this?" question. Always present the user a concrete, numbered set of options scoped to the finding type, so they can respond with a number per item:

**Pending duplicate-consolidation decision carried over from Duplicate Detection:**
1. Apply the recommended consolidation/separation from the Duplicate Story Report
2. Override it (state the alternative decision)
3. Hold this requirement out of this PRD draft until resolved

**Ambiguity (business intent unclear):**
1. Accept the stated interpretation (state it) and proceed
2. Provide the correct/clarified intent now
3. Include the requirement with the ambiguity explicitly flagged in the PRD, unresolved
4. Exclude this requirement from the PRD

**Conflict (contradictory sources):**
1. Accept JIRA's version as authoritative and proceed
2. Accept the other source's version (state which) and proceed
3. Provide the correct resolution now
4. Include both versions in the PRD with the conflict explicitly flagged, unresolved

**Material Assumption (PRD had to assume due to incomplete stories):**
1. Accept the stated assumption and proceed
2. Provide the correct information now
3. Include the requirement with the assumption explicitly flagged in the PRD, unresolved

**Risk identified during authoring:**
1. Accept the risk as documented and proceed
2. Provide mitigating information that changes the risk assessment
3. Exclude the affected requirement from this PRD pass pending further review

Present a separate numbered list per item (not merged across items), so the user can reply per item. A custom typed instruction is always accepted in place of a number.

Do not save a PRD to `07-outputs/GeneratedPRD(JIRA)/` while any of the above remain unresolved. Mark the PRD as **draft / pending review** until the user responds, and clearly surface the pending items at the top of the document rather than burying them in the Ambiguities/Risks sections only.

---

# Output

Save the generated PRD under:

```text
07-outputs/GeneratedPRD(JIRA)/
```

in the format the user selected (PDF, .xlsx, .doc, or .md — PDF by default), following the existing naming convention for that folder. Confirm with the user before overwriting an existing file there. Once saved, this PRD becomes the run's requirement source for downstream Requirement Analysis.