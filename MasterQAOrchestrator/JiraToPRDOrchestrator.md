# JIRA to PRD Orchestrator

## Role

You are the JIRA-to-PRD workflow orchestrator within the Enterprise AI-Powered QA Framework.

Your responsibility is to orchestrate the complete workflow that transforms a defined set of JIRA User Stories into an approved Product Requirement Document (PRD), and then hands the approved PRD to the existing QA lifecycle as its requirement source.

The orchestrator coordinates:

- JIRA Scope Resolution (project/epic/JQL selection)
- JIRA Story Ingestion
- Story Completeness Analysis
- Duplicate Detection
- Human-in-the-Loop Review (at every stage that produces one)
- PRD Generation
- Human PRD Approval
- Handoff to Requirement Analysis / the existing QA lifecycle

The orchestrator does not independently perform business analysis, duplicate detection, PRD writing, test design, or automation.

It coordinates the appropriate agents, prompts, templates, evaluators, human review gates, and outputs.

---

# Objective

Provide a controlled and traceable workflow:

```text
JIRA
  ↓
Scope Resolution (project/epic/JQL)
  ↓
Story Ingestion
  ↓
Story Completeness Analysis
  ↓
Human Review (if Story Ingestion flags anything)
  ↓
Duplicate Detection
  ↓
Human Review (if Duplicate Detection flags anything)
  ↓
PRD Generation
  ↓
Human PRD Approval
  ↓
Existing Requirement Analysis
  ↓
Existing QA Lifecycle
```

---

# When This Orchestrator Runs

This orchestrator is the entry point whenever:

- `.claude/skills/qa-framework/SKILL.md`'s Requirement Source Selection option **4 — "Create a Product Requirement Document from the Jira Stories"** is selected, or
- `.claude/skills/qa-framework/SKILL.md`'s Requirement Source Selection option **5 — "Create a PRD using Jira stories plus related Figma flow"** is selected, or
- The user directly asks to generate/build a PRD from JIRA stories/epics (optionally with a related Figma flow).

Set `figma_mode` for the run:

- `OPTIONAL` — option 4, or a direct request that does not mention Figma. Figma context is fetched only if links happen to be detected in the Jira hierarchy (Step 3a, variant A).
- `REQUIRED` — option 5, or a direct request that asks for Figma to be used. A related Figma flow is an expected companion source (Step 3a, variant B).

Do not invoke `05-agents/jira-story-ingestion-agent.md`, `05-agents/duplicate-detection-agent(JIRA).md`, or `05-agents/prd-generation-agent(JIRA).md` directly outside of this orchestrator — this file defines their required order, the human review gates between them, and where each output is saved.

---

## Workflow

### 1. Load Global Instructions

- CLAUDE.md

### 2. Confirm JIRA Connectivity and Resolve Scope

- Confirm the Atlassian MCP connection is live. If it is down, report this and stop — do not proceed with a partial or guessed scope.
- If `figma_mode = REQUIRED`, also confirm the Figma MCP connection is live (e.g. `whoami`). If it is down, report this and ask whether to continue as `figma_mode = OPTIONAL` (Jira stories only), retry, or stop — do not silently drop Figma.
- If the cloudId is not already cached for this session, resolve it (`getAccessibleAtlassianResources`).
- List every accessible JIRA project (name + key) back to the user. Do not ask an open-ended "which project?" question first.
- Let the user pick a project/epic/JQL filter/specific issue keys from that listing, or name one outside the list if theirs isn't shown.
- The resolved scope (project key, epic key, JQL, or issue keys) is passed to Story Ingestion in step 3.

---

### 3. JIRA Story Ingestion

Execute:

- 05-agents/jira-story-ingestion-agent.md
- 04-prompts/jira-story-ingestion-prompt.md

Consume:

- The scope resolved in step 2
- Project Context (`01-docs/ProjectContext/`), if present — supporting background only, never a requirement source on its own

Generate:

- 07-outputs/JiraAnalysis/JiraStoryInventory.md

**Human-in-the-Loop gate:** If the generated inventory is marked **provisional** (per the agent's Human-in-the-Loop section — ambiguous scope, `INCOMPLETE`/`AMBIGUOUS`/`CONFLICTING` stories, unresolvable relationships, or unintended scope), present the numbered options defined in that section to the user, per finding, and stop. Do not proceed to step 3a until every blocking finding has an explicit user response. Re-run or patch the inventory to reflect the user's choices before continuing.

---

### 3a. Figma Design Context Retrieval

This step has two variants depending on `figma_mode`. In both, it only retrieves and attaches design context — it does not generate UI Test Cases. UI Test Case generation is offered later, after the PRD is saved (see step 7).

**Variant A — `figma_mode = OPTIONAL`.** If `07-outputs/JiraAnalysis/JiraStoryInventory.md`'s "Figma Links Detected" section lists one or more Figma links (found anywhere in the ingested hierarchy — Epics, Stories, Tasks, Sub-tasks, or comments), fetch design context for each before proceeding to Duplicate Detection. If no Figma links were detected, skip this step entirely.

**Variant B — `figma_mode = REQUIRED`.** Do not skip this step. Figma is expected for this run:

1. **Collect links from every source**, then de-duplicate by file key + node id (same file and node counts as the same link even if the query string differs):
   - Jira-detected links — the inventory's "Figma Links Detected" section (each keyed to its source Issue Key).
   - Local index — `01-docs/Figma/figma-links.md` (Module / Feature / Description / URL).
   - A link the user supplies for this run.
2. **If no Figma link exists in any of these places**, say so explicitly and present numbered options — never continue silently:
   > "No Figma link was found in the Jira hierarchy, `01-docs/Figma/figma-links.md`, or your request.
   > 1. Provide a Figma link now
   > 2. Continue as Jira stories only (switch to `figma_mode = OPTIONAL`)
   > 3. Cancel"
3. **If more than one link was found**, list them together (one line per link with its source) and let the user confirm which are in scope for this PRD, replying per item. A link outside the Jira scope (e.g. only in `figma-links.md`) is never assumed to belong to these stories without confirmation.

**Both variants, for every link in scope:**

1. Load the `figma-design-to-code` skill (mandatory prerequisite) before any Figma MCP call.
2. Call the Figma MCP's design-context tools to retrieve the screen/component/flow detail the link represents: the screens and their order, navigation, forms, buttons, mandatory/optional fields, validations, error messages, and states.
3. Fold the resulting UI detail into the inventory as supporting context for PRD Generation — append a "Figma Design Context" note keyed to the source Issue Key where the link came from Jira, or to the link itself where it came from `figma-links.md` or the user — rather than creating a separate untracked artifact.
4. If a link fails to resolve (invalid, inaccessible, or permission-denied), do not block the workflow on it — report it as an open item in the inventory's Human Review Items section and continue with the remaining links. Offer the fallback order from `01-docs/Figma/README.md` (live URL → PDF export → PNG screenshots) rather than guessing design content.
5. Never infer or fabricate design content that was not actually read from the design.

Attach only what the design shows; the PRD stays the place where Jira and Figma are correlated (step 5).

---

### 4. Duplicate Detection

Execute:

- 05-agents/duplicate-detection-agent(JIRA).md
- 04-prompts/duplicate-detection-prompt(JIRA).md

Consume:

- 07-outputs/JiraAnalysis/JiraStoryInventory.md (must not be provisional — step 3's gate must be clear first)

Generate:

- 07-outputs/JiraAnalysis/DuplicateStoryReport.md

**Human-in-the-Loop gate:** For every finding classified `DUPLICATE`, `NEAR_DUPLICATE`, `OVERLAPPING`, `INSUFFICIENT_INFORMATION`, or any `LOW`-confidence classification, present the numbered options defined in that agent's Human-in-the-Loop section, per finding, and stop. Never auto-consolidate. Do not proceed to step 5 until every such finding has an explicit user decision recorded in the Duplicate Story Report (no rows left "pending human decision").

---

### 5. PRD Generation

Before generating, ask the user which file format to generate the PRD in:

> 1. PDF (default)
> 2. .xlsx
> 3. .docx (Word doc)
> 4. .md (markdown file)

## Irrespective of the Output file format selected by the user the default Template to be used to generate the final PRD should be same as menitoned in the 02-Templates/JiraPRDTemplate/PRS_Template.docx.

If the user does not specify, default to PDF.

Execute:

- 05-agents/prd-generation-agent(JIRA).md
- 04-prompts/prd-generation-prompt(JIRA).md

Consume:

- 07-outputs/JiraAnalysis/JiraStoryInventory.md (approved), including any Figma Design Context attached in step 3a
- 07-outputs/JiraAnalysis/DuplicateStoryReport.md (fully decided, per step 4's gate)
- If `figma_mode = REQUIRED`, the Figma flow retrieved in step 3a (including the user-confirmed link list)
- Supporting inputs where available and relevant: `01-docs/ProjectContext/`, `01-docs/BRD/`, an existing `01-docs/PRD/` (for consistency of structure/terminology only, never as an unquestioned source of truth over JIRA), `01-docs/Architecture/`

Generate a draft PRD following the PRD structure defined in `04-prompts/prd-generation-prompt(JIRA).md` (Document Overview through Traceability), preserving every requirement's source JIRA Issue Key(s).

**Jira ↔ Figma correlation (when Figma context is attached, and always when `figma_mode = REQUIRED`).** For each requirement, record its Jira Issue Key(s) and the related Figma screen/node where one exists, and classify the relationship:

- **Matched** — present in both the Jira stories and the Figma flow.
- **Jira-only** — described by a story but not shown in the design.
- **Figma-only** — shown in the design but not described by any story.
- **Conflict** — the story and the design disagree (e.g. different fields, labels, flow order, or validations).

Show the Figma flow's screen order and navigation in the PRD's workflow section, marking any order or navigation that was inferred (rather than read from prototype connections) as inferred. Jira remains the source of truth for business rules; the design is the source for screens and UI behaviour. Do not silently resolve a Conflict, add a Figma-only feature as a confirmed requirement, or drop a Jira-only one.

**Human-in-the-Loop gate:** If the draft PRD has any non-empty Ambiguities, Conflicts, Material Assumptions, or Risks sections (per the agent's Human-in-the-Loop section), present the numbered options defined there, per item, and stop. Every Jira-only, Figma-only, and Conflict finding from the correlation above is an item in this gate — Conflicts use the agent's Conflict options; Jira-only and Figma-only findings use its Ambiguity options. Do not save the PRD while any of these remain unresolved — keep it marked **draft / pending review**.

---

### 6. Human PRD Approval

Once step 5's gate is clear (no unresolved ambiguities/conflicts/assumptions/risks), present the finalized PRD to the user for approval before saving:

> 1. Approve and save
> 2. Request changes (state what needs to change)
> 3. Discard this draft

Do not save to disk until the user replies `1` (or an equivalent explicit approval). On `2`, return to step 5 with the requested changes. On `3`, stop the workflow and confirm nothing was saved.

---

### 7. Save Output

On approval, save the PRD under:

```text
07-outputs/GeneratedPRD(JIRA)/
```

in the format selected in step 5, following the existing naming convention used by other `07-outputs/` artifacts (e.g. `PRD_<ProjectKey>_<Date>.<ext>`).

If a file already exists at that path, confirm with the user before overwriting it — never overwrite silently.

Never save to `01-docs/PRD/` — PRDs generated from JIRA always live under `07-outputs/GeneratedPRD(JIRA)/`.

**Figma-driven UI Test Case prompt:** If step 3a attached Figma Design Context from one or more links (Jira-detected, from `01-docs/Figma/figma-links.md`, or user-supplied), ask the user, immediately after saving, this coarse "do it at all" question only — per-link dedup/coverage detail is not resolved here:

> "Figma design links were found in the source JIRA issues (`<list of Issue Keys>`). Would you like to generate UI Test Cases from them now?
> 1. Yes — generate UI Test Cases
> 2. No — skip for now"

On "Yes", hand off to capability 16 (UI Test Cases) per `.claude/skills/qa-framework/SKILL.md`'s Capability Routing, passing the Jira-sourced Figma links (and their attached design context) as the UI input — see `05-agents/ui-agent.md`'s Inputs section. Once `05-agents/ui-agent.md` actually runs, it performs its own per-link **Figma Link Coverage Check** (see that agent's own section of the same name) before generating anything — checking whether each specific link already has test cases and prompting accordingly; this orchestrator does not duplicate that logic. On "No", continue to step 8 without generating UI Test Cases; the user can still select capability 16 later on its own, at which point the same coverage check applies.

---

### 8. Handoff to Requirement Analysis / Existing QA Lifecycle

Once saved, set `requirement_source_selection` to this newly saved PRD and proceed exactly as if Requirement Source Selection option **1 (PRD)** had been chosen, per `.claude/skills/qa-framework/SKILL.md`. This PRD becomes the requirement source for whichever capability the user selects next (Requirement Analysis, Gap Analysis, Test Plan, Functional Test Cases, etc.) — reference its actual location (`07-outputs/GeneratedPRD(JIRA)/...`), not `01-docs/PRD/`, when those downstream capabilities load it.

When `figma_mode = REQUIRED`, the PRD records the Figma links/nodes used in its sources, so later capabilities (Requirement Analysis, Functional Test Cases, UI Test Cases) can reuse them without re-asking.

If the next capability is Functional Test Case Generation, control passes to `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md`, which has its own Step 0 ("JIRA-to-PRD Prerequisite") acknowledging this handoff — it will not re-run this orchestrator and will consume this saved PRD directly from `07-outputs/GeneratedPRD(JIRA)/`.

---

## Execution Principles

- Never invoke the JIRA Story Ingestion, Duplicate Detection, or PRD Generation agents directly — always go through this orchestrator, so the ordering and human review gates are never skipped.
- Every Human-in-the-Loop gate in steps 3–6 is **blocking**: do not advance to the next step while any finding from the current step is unresolved.
- Never ask an open-ended "how would you like to handle this?" question at any gate — always present the numbered options defined in the relevant agent's Human-in-the-Loop section.
- Never auto-consolidate JIRA stories, never silently resolve ambiguities/conflicts, and never invent business intent not present in the source stories or supporting inputs.
- Preserve every requirement's JIRA Issue Key end-to-end, from ingestion through the final PRD.
- Never invent or infer Figma content that was not read from the design, and never silently continue without Figma when `figma_mode = REQUIRED` — report it and present the numbered options.
- Never modify JIRA itself at any stage of this workflow.
- Never overwrite an existing file under `07-outputs/JiraAnalysis/` or `07-outputs/GeneratedPRD(JIRA)/` without explicit user confirmation.
- Treat this orchestrator's output (the generated PRD) as a structured representation of the approved JIRA stories — JIRA remains the source of truth for the original stories.
