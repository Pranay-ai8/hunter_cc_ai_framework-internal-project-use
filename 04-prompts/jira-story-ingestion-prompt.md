# JIRA Story Ingestion Prompt

## Objective

Retrieve and normalize all relevant JIRA stories within the requested scope and produce a complete, traceable JIRA Story Inventory for downstream duplicate detection and PRD generation.

JIRA is the source of truth for User Stories maintained in JIRA.

Do not modify JIRA.

---

# Scope

Determine the requested JIRA scope from the available input.

The scope may be defined by:

- JIRA Project
- Epic
- Initiative
- Sprint
- Fix Version
- Release
- JQL
- Specific Issue Keys
- User-provided story selection

Do not retrieve unrelated stories.

If the scope cannot be determined reliably, flag the issue for human clarification.

---

# Retrieval

Use the configured JIRA MCP integration to retrieve relevant stories.

For each story retrieve, where available:

- JIRA Issue Key
- Summary
- Description
- Issue Type
- Status
- Acceptance Criteria
- Epic
- Parent
- Child Issues
- Linked Issues
- Dependencies
- Components
- Labels
- Priority
- Assignee
- Reporter
- Fix Version
- Sprint
- Relevant comments
- Relevant relationships
- Created Date
- Updated Date

Do not invent unavailable information.

---

# Figma Link Detection

Scan the Description, Acceptance Criteria, and Comments of every retrieved issue — Epic, Story, Task, and Sub-task alike, anywhere in the hierarchy, not only the top-level issue(s) named in the requested scope — for Figma design links (`figma.com/design/...`, `figma.com/file/...`, `figma.com/proto/...`).

For each one found, record the source Issue Key, Issue Type, and the raw URL. A single issue may carry more than one Figma link; record each separately. Detection and recording only — do not open or fetch the link at this stage. Do not fabricate a link that isn't literally present. If none are found anywhere in scope, state that explicitly in the output.

---

# Normalization

Normalize the retrieved information into a consistent structure without changing the original business intent.

For each story capture:

- JIRA Issue Key
- Summary
- Description
- Acceptance Criteria
- Business Behavior
- User Role
- Module
- Feature
- Dependencies
- Relationships
- Figma Link(s) (if any were detected on this issue; otherwise `None`)
- Priority
- Status
- Source

Clearly distinguish retrieved information from derived information.

---

# Completeness Analysis

Evaluate each story for completeness.

Identify:

- Missing description
- Missing acceptance criteria
- Missing business rules
- Missing user role
- Missing expected behavior
- Missing workflow
- Missing error handling
- Missing dependencies
- Ambiguity
- Contradiction

Use:

- `COMPLETE`
- `INCOMPLETE`
- `AMBIGUOUS`
- `CONFLICTING`

Do not infer missing requirements.

---

# Traceability

Preserve the JIRA Issue Key for every story.

Use:

```text
Source System: JIRA
JIRA Issue Key: AUTH-123
Project: AUTH
Epic: AUTH-100
```

Every story in the output must carry this traceability block.

---

# Human-in-the-Loop

Do not silently proceed past unresolved scope or story issues.

Pause and request explicit human clarification before producing the final JIRA Story Inventory when:

- The requested JIRA scope cannot be determined reliably.
- A story is classified `INCOMPLETE`, `AMBIGUOUS`, or `CONFLICTING` in a way that would materially affect downstream PRD generation.
- JIRA relationships (epic/parent/child/linked/dependency) cannot be reliably interpreted.
- The resolved scope appears to include issues the requester likely did not intend.

For each item requiring clarification, include in the review package:

- JIRA Issue Key
- Finding
- Evidence (the specific field or relationship in question)
- Impact on downstream PRD generation
- Recommended action

Never ask an open-ended "how would you like to handle this?" question. Always present the user with a concrete, numbered set of options to choose from, scoped to the specific finding type:

**Scope cannot be determined reliably:**
1. Confirm the inferred scope (state the inferred scope) and proceed
2. Provide a corrected/narrower scope
3. Cancel ingestion

**Story is `INCOMPLETE` (missing field materially affects PRD generation):**
1. Provide the missing information now
2. Proceed with this story flagged as an assumption in the PRD
3. Exclude this story from scope
4. Pause ingestion entirely until this is resolved

**Story is `AMBIGUOUS` or `CONFLICTING`:**
1. Confirm one interpretation (state it) and proceed
2. Provide the correct/clarified information
3. Exclude this story from scope
4. Pause ingestion entirely until this is resolved

**JIRA relationship cannot be reliably interpreted:**
1. Confirm the inferred relationship (state it) and proceed
2. Provide the correct relationship
3. Treat the story as having no relationship and proceed

**Scope appears to include unintended issues:**
1. Confirm the full scope as retrieved and proceed
2. Remove the specific flagged issue(s) from scope
3. Provide a corrected scope definition

Present each numbered list per finding (not merged across findings), so the user can reply per item (e.g. "AUTH-124: 2, AUTH-125: 1"). The user may also type a custom instruction instead of a number.

Do not pass unresolved, materially-affecting items on to Duplicate Detection or PRD Generation. Treat the inventory as provisional until the human reviewer responds, and clearly mark it as such in the output.

---

# Output

Save the generated inventory as:

```text
07-outputs/JiraAnalysis/JiraStoryInventory.md
```

Include:

1. Scope (project, scope definition, retrieval criteria, retrieval timestamp where available)
2. Story Summary (total / complete / incomplete / ambiguous / conflicting counts)
3. Story Inventory (all normalized story information)
4. Relationships (epic, parent, child, linked, dependency)
5. Figma Links Detected (source Issue Key, Issue Type, and raw URL for every link found anywhere in scope; state explicitly if none were found)
6. Completeness Findings
7. Human Review Items (pending clarifications, if any)
8. Traceability (JIRA Issue Key preserved for every story; source Issue Key preserved for every Figma link)