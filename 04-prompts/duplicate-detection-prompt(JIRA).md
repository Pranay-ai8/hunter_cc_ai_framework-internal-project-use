# Duplicate Detection Prompt

## Objective

Analyze the JIRA Story Inventory and identify duplicate, near-duplicate, overlapping, related, and insufficiently defined stories.

The objective is to reduce redundant requirements while preserving legitimate business behavior.

Do not modify JIRA.

---

# Inputs

Primary input:

```text
07-outputs/JiraAnalysis/JiraStoryInventory.md
```

---

# Comparison Dimensions

Compare each story against every other story in scope across:

- Summary
- Description
- Acceptance Criteria
- Business Intent
- User Role
- Module / Feature
- Workflow
- Business Rules
- Dependencies
- Inputs and Outputs

---

# Classification

For each comparison, assign one of:

- `DUPLICATE` — same business intent and behavior, different story only in wording.
- `NEAR_DUPLICATE` — materially overlapping intent, minor meaningful differences.
- `OVERLAPPING` — partial functional overlap, each story also covers distinct behavior.
- `RELATED` — connected (e.g. shared dependency, same workflow) but not redundant.
- `UNIQUE` — no meaningful overlap with other stories in scope.
- `INSUFFICIENT_INFORMATION` — cannot be reliably classified given current story content.

Assign a confidence level (`HIGH`, `MEDIUM`, `LOW`) to every classification and provide the specific evidence (matching fields/phrases) that supports it.

Never infer a classification from assumed intent not present in the stories. Preserve each story's JIRA Issue Key in every finding.

---

# Human-in-the-Loop

This is a blocking checkpoint, not an advisory note.

Do not auto-consolidate anything. Every `DUPLICATE` or `NEAR_DUPLICATE` finding is a **recommendation only** until a human explicitly approves it.

Pause and request an explicit decision for every finding classified as:

- `DUPLICATE`
- `NEAR_DUPLICATE`
- `OVERLAPPING`
- `INSUFFICIENT_INFORMATION`
- Any classification assigned with `LOW` confidence

Never ask an open-ended "how would you like to handle this?" question. Always present the user a concrete, numbered set of options scoped to the classification, so they can respond with a number per finding:

**`DUPLICATE` or `NEAR_DUPLICATE`:**
1. Consolidate into a single requirement (merging the listed JIRA Issue Keys)
2. Keep as separate requirements
3. Need more information — hold this finding and continue reviewing others

**`OVERLAPPING`:**
1. Consolidate the overlapping portion only, keep the distinct portions separate
2. Keep fully separate (treat overlap as acceptable)
3. Need more information — hold this finding and continue reviewing others

**`INSUFFICIENT_INFORMATION` or any `LOW`-confidence classification:**
1. Reclassify as `UNIQUE` and proceed (state the reason)
2. Provide the missing/clarifying information now
3. Treat as `NEAR_DUPLICATE`/`OVERLAPPING` pending (state which) and consolidate per that option
4. Hold this finding for later — exclude from this PRD generation pass

Present a separate numbered list per finding (not merged across findings), so the user can reply per finding (e.g. "AUTH-123/AUTH-130: 1, AUTH-124/AUTH-140: 2"). A custom typed instruction is always accepted in place of a number.

Do not pass an unresolved finding to PRD Generation as if it were decided. Mark the Duplicate Story Report's affected rows as **pending human decision** until the user responds, and carry that pending status forward rather than guessing a default.

---

# Output

Save the generated report as:

```text
07-outputs/JiraAnalysis/DuplicateStoryReport.md
```

Include:

1. Comparison Summary (total stories compared, counts per classification)
2. Findings (per finding: JIRA Issue Keys involved, classification, confidence, evidence)
3. Pending Human Decisions (every finding awaiting explicit approval)
4. Approved Decisions (once provided by the user — consolidate / keep separate)
5. Traceability (JIRA Issue Keys preserved for every finding)