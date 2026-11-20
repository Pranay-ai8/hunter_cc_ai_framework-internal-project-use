---
name: qa-framework
description: >
  Run selected QA activities using the CC-QA-AI Framework and route them
  to the appropriate Master Orchestrators.
---

# QA Framework Skill

## Purpose

Provide the user with a capability selection checklist.

Allow the user to select one or more QA activities.

Execute the corresponding Master Orchestrator for each selected capability.

---

# Requirement Source Selection

Every time this skill is invoked, ask this question first — before the capability checklist, and before building any execution plan. This runs even when the user's invocation already states a capability (e.g. "generate a test plan"), unless the invocation also already states the source (see Skip Condition below).

Present this as a single numbered list, same style as the capability checklist (plain markdown, not `AskUserQuestion` — these 9 options exceed its 4-option cap):

1. Requirements are in PRD
2. Requirements are in BRD
3. Requirements are in Figma Flow
4. Create a Product Requirement Document from the Jira Stories
5. Create a PRD using Jira stories plus related Figma flow
6. Requirements in Screenshots
7. API Requirements and API Documentation
8. Requirements in Project Architecture
9. Other location

Ask the user:

> Where are the requirements for this run coming from? You can Select one or more.

The user may select one option, several (e.g. `1, 5` — a PRD plus Screenshots), or name them directly. Multiple sources are expected and encouraged: `CLAUDE.md`'s Requirement Correlation principle ("Never rely on a single document if multiple sources are available") applies here, so do not force a single choice.

**9 / Other location** — if selected, ask the user to name the location (a folder under `01-docs/` not otherwise listed, e.g. "Other Requirement Docs", "Release Notes", or a specific file/path), then route to that source by name rather than treating it as a cancellation.

## Source → Location Mapping

| # | Source | Location |
|---|---|---|
| 1 | PRD | `01-docs/PRD/` |
| 2 | BRD | `01-docs/BRD/` |
| 3 | Figma Flow | `01-docs/Figma/` (`figma-links.md`) + live Figma MCP for the linked file(s), per the `figma-use` / `figma-design-to-code` skills |
| 4 | Create a PRD from Jira Stories | Driven entirely by `MasterQAOrchestrator/JiraToPRDOrchestrator.md` — load and follow that file rather than invoking `05-agents/jira-story-ingestion-agent.md`, `05-agents/duplicate-detection-agent(JIRA).md`, or `05-agents/prd-generation-agent(JIRA).md` directly. It resolves the Jira scope, ingests stories, runs duplicate detection, generates a PRD (format chosen by the user: PDF/.xlsx/.doc/.md, PDF by default) with blocking human-review gates at every stage, and saves the approved PRD under `07-outputs/GeneratedPRD(JIRA)/` — never `01-docs/PRD/`. That generated PRD then becomes this run's requirement source, as if option 1 had been selected. See "PRD-from-Jira" below. |
| 5 | Create a PRD from Jira Stories + related Figma flow | Driven by `MasterQAOrchestrator/JiraToPRDOrchestrator.md` exactly as option 4 is, with the Figma flow treated as a required companion source rather than optional: Jira stories (via the Atlassian MCP) **plus** the related Figma flow, taken from (a) Figma links detected anywhere in the Jira hierarchy (`07-outputs/JiraAnalysis/JiraStoryInventory.md`'s "Figma Links Detected" section), (b) `01-docs/Figma/` (`figma-links.md`), and/or (c) a link the user supplies — read through the live Figma MCP per the `figma-use` / `figma-design-to-code` skills (fallback order per `01-docs/Figma/README.md`: live URL → PDF export → PNG screenshots). The PRD is saved under `07-outputs/GeneratedPRD(JIRA)/` — never `01-docs/PRD/` — and then becomes this run's requirement source, as if option 1 had been selected. See "PRD-from-Jira + Figma" below. |
| 6 | Requirements in Screenshots | `01-docs/Screenshots/`, treated as a Design Document input per `05-agents/requirement-analysis-agent.md` |
| 7 | API Requirements and API Documentation | `01-docs/API Docs/` (Swagger/OpenAPI, if present) |
| 8 | Requirements in Project Architecture | `01-docs/Architecture/` |
| 9 | Other location | User-specified — e.g. `01-docs/ReleaseNotes/`, `01-docs/Other Requirement Docs/`, or any other path/document the user names |

`01-docs/ProjectContext/` sits outside this list and is always consulted regardless of the selected source(s), per `requirement-analysis-agent.md`'s existing Project Context rule (background/context only, never a requirement source on its own). If the user names a source not covered by 1–8 (e.g. "Release Notes" or "Other Requirement Docs"), treat it as option 9 and route it to its real folder — the 8 named options are the common cases, not an exhaustive restriction.

## PRD-from-Jira (Option 4)

When option 4 is selected, the entry point is `MasterQAOrchestrator/JiraToPRDOrchestrator.md` — read and follow it, not this summary. Its workflow, in order:

1. Confirm the Atlassian MCP connection is live — if it is down, report this instead of silently skipping, per `CLAUDE.md`'s Jira Analysis section. If the cloudId is not already cached for this session, call `getAccessibleAtlassianResources`, then list every accessible Jira project (name + key) back to the user — do not ask an open-ended "which project?" question first. Let the user pick a project/epic/JQL filter/specific issue keys from that listing, or name one outside the list if theirs isn't shown.
2. **JIRA Story Ingestion** — `05-agents/jira-story-ingestion-agent.md` + `04-prompts/jira-story-ingestion-prompt.md` retrieve and normalize the stories into `07-outputs/JiraAnalysis/JiraStoryInventory.md`, with a blocking human-review gate (numbered options, never open-ended) for any ambiguous scope, incomplete/ambiguous/conflicting story, or unresolvable relationship. This step also detects Figma design links anywhere in the retrieved hierarchy (Epics, Stories, Tasks, Sub-tasks, comments) and records them in the inventory's "Figma Links Detected" section.
3. **Figma Design Context Retrieval** (Step 3a, only if Figma links were detected) — fetches design context for each detected link via the Figma MCP and attaches it to the inventory, keyed to its source Issue Key. Does not generate UI Test Cases yet.
4. **Duplicate Detection** — `05-agents/duplicate-detection-agent(JIRA).md` + `04-prompts/duplicate-detection-prompt(JIRA).md` classify story overlap into `07-outputs/JiraAnalysis/DuplicateStoryReport.md`, with a blocking human-review gate for every duplicate/near-duplicate/overlapping/insufficient-information/low-confidence finding. Never auto-consolidated.
5. **PRD Generation** — first asks the user which format to generate (PDF/.xlsx/.docx/.md, PDF by default), then `05-agents/prd-generation-agent(JIRA).md` + `04-prompts/prd-generation-prompt(JIRA).md` author the PRD from the approved inventory (including any attached Figma design context) and resolved duplicates, preserving every requirement's Jira key. A blocking human-review gate applies to any ambiguity, conflict, material assumption, or risk the PRD itself surfaces.
6. **Human PRD Approval** — the finalized draft is presented for explicit approve / request-changes / discard before anything is saved.
7. **Save** — the approved PRD is saved under `07-outputs/GeneratedPRD(JIRA)/` — never `01-docs/PRD/` — following the existing `07-outputs/` naming convention, confirming before overwriting an existing file there. If Figma links were detected, immediately asks whether to also generate UI Test Cases (16) now from them, before moving on.
8. **Handoff** — `requirement_source_selection` is set to this newly saved PRD (i.e., proceed exactly as option 1 would from this point forward) for the rest of the run, referencing its actual location (`07-outputs/GeneratedPRD(JIRA)/`), not `01-docs/PRD/`.

## PRD-from-Jira + Figma (Option 5)

When option 5 is selected, the entry point is still `MasterQAOrchestrator/JiraToPRDOrchestrator.md` — read and follow it, not this summary. The workflow, gates, human approval, save location, and handoff are the same as "PRD-from-Jira (Option 4)" above. The differences are:

1. **Figma is expected, not optional.** Step 3a (Figma Design Context Retrieval) is not skipped. Before it runs, collect Figma links from all three places: Jira-detected links (the inventory's "Figma Links Detected" section), `01-docs/Figma/figma-links.md`, and any link the user supplies. De-duplicate by file key + node id.
2. **If no Figma link is found anywhere**, say so explicitly and present numbered options instead of silently continuing: 1. Provide a Figma link now, 2. Continue as option 4 (Jira stories only), 3. Cancel.
3. **Figma connectivity.** Confirm the Figma MCP connection is live before fetching; if it is down or a link is inaccessible, report it as an open item and ask how to proceed (retry / use PDF export or screenshots per `01-docs/Figma/README.md` / continue without that link). Never guess design content.
4. **Load the `figma-design-to-code` skill** before any Figma MCP design-context call, as option 4's Step 3a already requires.
5. **PRD generation correlates Jira and Figma.** The PRD must show, for each requirement, its Jira Issue Key(s) and the related Figma screen/node where one exists. Features present in Jira but not in the Figma flow, or in the Figma flow but not in any Jira story, are reported as gaps or conflicts through the PRD's existing human-review gate — never silently merged or invented.
6. **Save and handoff** are unchanged: the approved PRD goes under `07-outputs/GeneratedPRD(JIRA)/`, and the Figma links/nodes used are recorded in the PRD's sources so later capabilities can reuse them.

## What the Selection Does

- Record the selection as `requirement_source_selection` for this run.
- Pass it to every capability that consumes requirements — Requirement Analysis (1), Gap Analysis (2), Change Impact Analysis (3), Test Plan (4), Functional Test Cases (5), UI Test Cases (16), API Test Cases (13), Security Test Cases (12), Accessibility Test Cases (14), RTM (6) — so each agent reads only the mapped location(s) above instead of scanning all of `01-docs/` blindly.
- If a mapped folder is empty or missing for a selected source, say so before proceeding — do not silently continue as if that source were absent, and do not substitute a different source without asking.
- If Create a PRD from Jira Stories (4) or Create a PRD using Jira stories plus related Figma flow (5) is selected, confirm the Atlassian MCP connection is live before proceeding; if it is down, report it and ask whether to continue with the remaining selected sources or stop entirely. See "PRD-from-Jira (Option 4)" above for the full project/epic/JQL resolution flow, and "PRD-from-Jira + Figma (Option 5)" for the Figma-specific differences.
- This selection does not replace `05-agents/requirement-analysis-agent.md`'s own Input Sources list — it scopes which of those sources are actually in play for this run, it does not narrow what that agent is capable of reading.

## Skip Condition

If the user's invocation already states the source (e.g. "generate a test plan from the PRD", "use the Jira stories for the AUTH project"), skip this question and infer the source directly from the wording.

---

# Capability Selection

When the user invokes this skill without specifying an activity, present all capabilities together as a single numbered list (one place, no tabs/pages, no checkboxes):

1. Analyze Product Requirements
2. Identify Gaps in Requirements
3. Perform Change Impact Analysis
4. Generate Test Plan as per CodeCraft Template
5. Generate Functional Test Cases
6. Generate Requirement Traceability Matrix (RTM)
7. Generate Playwright Automation Scripts
8. Generate Selenium Automation Scripts
9. Generate Cypress Automation Scripts
10. Generate WebdriverIO Automation Scripts
11. Generate Appium Automation Scripts
12. Generate Security Test Cases
13. Generate API Test Cases
14. Generate Accessibility Test Cases
15. Generate Performance Test Cases
16. Evaluate Test Cases
17. Evaluate Test Plan
18. Evaluate Requirement Traceability Matrix (RTM)
19. Publish Test Cases to TestLink
20. Publish Test Cases to TestRail
22. Publish Artifacts to Jira
23. Stay Idle and Do Nothing (Cancel / None)

Ask the user:

> Select the activities you want to execute.

The user may select:

- One activity
- Multiple activities
- A combination such as `4, 5, 6`
- Activities by name
- **23 / None** — stop here and perform no action. If 23 is selected alongside other numbers, None wins: treat it as a cancellation of the whole request, not as one more item in the list. Do not execute anything, do not ask a follow-up, just confirm nothing was run.

This is plain markdown — a static numbered list, not a clickable control. The user replies by typing their selection back as text (numbers or names). Do **not** use `AskUserQuestion` for this: it caps out at 4 options per question, so fitting all 23 items would require splitting them across multiple questions/tabs, which is explicitly not wanted here — the single numbered list above is the intended mechanism.

If the user's invocation already states the activity (e.g. "generate a test plan"), skip the checklist and go straight to Selection Handling with that activity pre-selected.

---

# Test Case Generation Mode

When capability **5 (Generate Functional Test Cases)** is among the selected capabilities, after identifying the selection but before building the execution plan, ask the mode question that `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` Step 3 defines:

> Select a test case generation mode:
> 1. Functional Test Cases Only
> 2. Functional + Applicable Generic Test Cases (from `010-GenericTestCases/GenericTestCases.xlsx`)
> 3. Functional + Applicable Generic Test Cases + UI Test Cases (from `05-agents/ui-agent.md`)
> 4. Only UI Test Cases (from `05-agents/ui-agent.md`)
>
> Also: would you like UI Test Cases generated in this same run? (Yes/No)

- Options 1/2 set `test_case_generation_mode` exactly as that Orchestrator's Step 3 describes, and that variable is passed to `05-agents/functional-agent.md` as usual.
- A "Yes" to the UI question adds capability **16 (Generate UI Test Cases)** to the execution plan, scheduled to run immediately after Functional Test Cases complete — so `05-agents/ui-agent.md`'s own duplicate-avoidance check (see its Constraints) has a `FunctionalTestCases.md` to compare against.
- A "No" (or capability 5 not selected at all) leaves UI Test Cases out of the plan; the user can still select capability 16 on its own from the main checklist at any time, independent of this question.
- Do not ask this question when capability 5 is not selected. Capability 16 selected on its own runs directly via its own Capability Routing entry, with no mode question.

---

# Selection Handling

After the user replies to the checklist (or the activity was already stated):

0. If the reply is 23 / None (alone or combined with any other selection), stop immediately: perform no action, execute nothing, and confirm that nothing was run. Skip all steps below.
1. Identify all selected capabilities from the user's reply.
2. Map each capability to its corresponding Master Orchestrator.
3. Determine dependencies between selected capabilities.
4. Execute them in the required dependency order.
5. Do not execute unselected capabilities.
6. Show the user the execution plan before starting when multiple capabilities are selected.
7. Once every selected capability (and any prerequisite the Dependency Handling rules inserted) has finished, always generate and publish the Execution Trace Artifact described below — for every capability 1–22 that actually ran, not only test-case generation. Skip this step only when 23/None was selected (step 0 already stopped execution) or when a capability was skipped/blocked (e.g. an MCP connection was down) with nothing to report.

---

# Execution Trace Artifact

After a run completes, always publish an HTML Artifact (via the Artifact tool) that traces exactly what happened, structured the same way every time:

1. **Header** — the feature/subject name, which capabilities ran, the mode/options selected (e.g. `FUNCTIONAL_AND_GENERIC`), and the run date.
2. **Summary band** — a row of key counts pulled from what was actually generated (e.g. requirement count, test case count, coverage %, evaluation scores) — never fabricated placeholder numbers, and never copied uncritically from a generated document's own "Summary Counts" section — independently recount the real artifact (table rows, sheet rows, unique IDs) before putting any number in this band. See the count-verification rule under Execution Rules.
3. **One stage block per capability that ran**, in execution order, each broken into exactly four lanes:
   - **Loaded** — templates (`02-templates/`) and standards (`03-standards/`, `09-evaluators/eval_standards/`) the orchestrator/agent loaded for that stage.
   - **Consumed** — input documents (`01-docs/...`) and/or prior-stage outputs (`07-outputs/...`) it read.
   - **Executed** — the exact agent + prompt (and evaluator agent + prompt, if one ran immediately after) file paths from `05-agents/`, `04-prompts/`, `09-evaluators/eval_agents/`, `09-evaluators/eval_prompts/`.
   - **Written** — the exact output file(s) saved, with full paths.

   Pull every path and number in these lanes from what was actually read/run/saved in this session — do not invent or generalize them from the routing table.
4. **Narrative detail** — for each stage, a short summary of real content produced (counts, key findings), and a call-out for every conflict, ambiguity, or gap that was reported instead of guessed (per `CLAUDE.md`'s Quality Rules). Do not stop at naming the item — for each one, explain in plain language: what the issue actually was (in terms of the real content, not just a category label), what question or options were presented to the user, and what the user decided plus how that decision was carried into the output (e.g. "accepted as a documented gap" vs. "fixed inline" vs. "re-fetched from source"). A one-line tally of category labels (e.g. "5 ambiguities surfaced and resolved") is not sufficient on its own — it must be followed by this per-item breakdown so the user can see the actual reasoning without having to ask for it separately.
5. **Evaluation dimension tables** — if an evaluator ran (test case, test plan, RTM, Playwright/Selenium review), include its full per-dimension score breakdown, not just the overall score.
6. **Open items before sign-off** — any blocking conflict, missing information, or follow-up flagged during the run.
7. **All artifacts written** — a final table listing every output file path produced across all stages of the run.

For a single-capability run (e.g. only Gap Analysis, or only publishing to TestLink), the same structure still applies — just with one stage block instead of several, and sections that don't apply (e.g. evaluation dimension tables, when no evaluator ran) omitted.

When updating a prior run's artifact in the same conversation (e.g. re-running a capability, or continuing to the next one), republish to the same file path so the Artifact tool updates the existing page in place rather than creating a new one, unless the user asks for a separate artifact.

---

# Capability Routing

| # | Capability | Execute | Status |
|---|---|---|---|
| 1 | Requirement Analysis | `04-prompts/requirement-analysis-prompt.md` + `05-agents/requirement-analysis-agent.md`, following the same standards-loading behavior as `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` Step 4 (Requirement Analysis) and Step 6 (Load Framework Resources) | No dedicated orchestrator yet — run agent+prompt directly, but still load `03-standards/testing-standards.md` + `03-standards/naming-conventions-standards.md` per that orchestrator's Step 6, even standalone |
| 2 | Gap Analysis | `04-prompts/gap-analysis-prompt.md` + `05-agents/gap-analysis-agent.md`, following `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` Step 5 (Gap Analysis) and Step 6 (Load Framework Resources) | No dedicated orchestrator yet — run agent+prompt directly, but still load `03-standards/testing-standards.md` + `03-standards/naming-conventions-standards.md` per that orchestrator's Step 6, even standalone |
| 3 | Change Impact Analysis | `04-prompts/change-impact-prompt.md` + `05-agents/change-impact-agent.md`, following `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` Step 3 (Change Detection Check) for when this fires and what it consumes (`02-templates/ImpactAnalysis.md`) | No dedicated orchestrator yet; incremental runs only |
| 4 | Test Plan Generation | `MasterQAOrchestrator/TestPlanOrchestrator.md` (its own Steps 4-5 embed the equivalent Requirement/Gap Analysis sub-steps; its Step 6 loads `03-standards/testing-standards.md` + `03-standards/testplan-standards.md` + `03-standards/naming-conventions-standards.md` — the Test Plan-specific standard in place of `FunctionalTestCaseOrchestrator.md`'s `testcase-standards.md`) | Orchestrator exists |
| 5 | Functional Test Case Generation | `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` | Orchestrator exists |
| 6 | RTM Generation | `MasterQAOrchestrator/RTMOrchestrator.md` | Orchestrator exists |
| 7 | Playwright Automation | `MasterQAOrchestrator/AutomationOrchestrator.md` → `MasterQAOrchestrator/PlaywrightAutomationOrchestrator.md` | Orchestrator exists |
| 8 | Selenium Automation | `MasterQAOrchestrator/AutomationOrchestrator.md` → `MasterQAOrchestrator/SeleniumAutomationOrchestrator.md` | Orchestrator exists |
| 9 | Cypress Automation | `MasterQAOrchestrator/AutomationOrchestrator.md` → `MasterQAOrchestrator/CypressAutomationOrchestrator.md` | Orchestrator exists |
| 10 | WebdriverIO Automation | `MasterQAOrchestrator/AutomationOrchestrator.md` → `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md` | Orchestrator exists |
| 11 | Appium Automation | `MasterQAOrchestrator/AutomationOrchestrator.md` → `MasterQAOrchestrator/AppiumAutomationOrchestrator.md` | Orchestrator exists |
| 12 | Security Test Cases | `05-agents/security-agent.md` + `04-prompts/security-prompt.md`; no dedicated `03-standards/security-standards.md` exists — apply the Security Testing section of `03-standards/testing-standards.md` + `03-standards/naming-conventions-standards.md` | No orchestrator; no dedicated standards file — flag as a partial gap |
| 13 | API Test Cases | `05-agents/api-agent.md` + `04-prompts/api-prompt.md`, loading `03-standards/api-standards.md` + `03-standards/testcase-standards.md` + `03-standards/naming-conventions-standards.md` | No orchestrator; agent+prompt+standards exist |
| 14 | Accessibility Test Cases | `05-agents/accessibility-agent.md` + `04-prompts/accessibility-prompt.md`, loading `03-standards/accessibility-standards.md` + `03-standards/testcase-standards.md` + `03-standards/naming-conventions-standards.md` | No orchestrator; agent+prompt+standards exist |
| 15 | Performance Test Cases | n/a | Not implemented — no agent, prompt, or standards file exists yet. Report this gap to the user instead of silently skipping. |
| 16 | UI Test Cases | `05-agents/ui-agent.md` + `04-prompts/ui-testcase-prompt.md`, loading `03-standards/ui-standards.md` + `03-standards/testcase-standards.md` + `03-standards/naming-conventions-standards.md` + `03-standards/accessibility-standards.md` (basic checks only, per ui-agent.md's own Standards list) | No orchestrator; agent+prompt+standards exist. See the Test Case Generation Mode section for how this combines with capability 5. |
| 17 | Test Case Evaluation | `09-evaluators/` (matching evaluator, if present) | Not yet populated — flag gap, offer to proceed without formal evaluation |
| 18 | Test Plan Evaluation | `09-evaluators/` (matching evaluator, if present) | Not yet populated — flag gap |
| 19 | RTM Evaluation | `09-evaluators/` (matching evaluator, if present) | Not yet populated — flag gap |
| 20 | TestLink Publishing | `05-agents/testlink-sync-agent.md` + `04-prompts/testlink-sync-prompt.md` (MCP: `testlink`) | Not orchestrator-wired yet; also check the `testlink` MCP connection is up before starting |
| 21 | TestRail Publishing | `05-agents/testrail-sync-agent.md` + `04-prompts/testrail-sync-prompt.md` | Not orchestrator-wired yet |
| 22 | Jira Publishing | `05-agents/jira-sync-agent.md` + `04-prompts/jira-sync-prompt.md` | Not orchestrator-wired yet |
| 23 | None | n/a — stops execution, nothing to run | Not a real capability; a cancel signal, handled in Selection Handling step 0 |

This table is a map, not the source of truth — re-verify these paths against the current files in `05-agents/`, `04-prompts/`, `09-evaluators/`, and `MasterQAOrchestrator/` before executing, since the framework evolves.

---

# Dependency Handling

Before execution:

1. Check whether required input artifacts already exist (look in `07-outputs/`).
2. If an artifact exists, reuse it — do not regenerate unless the user explicitly asks.
3. If an artifact is missing and its producer is selected, execute the producer first.
4. If an artifact is missing and its producer is not selected, ask the user whether to execute the required prerequisite.

Known dependencies in this framework:

- Test Plan (4) depends on Requirement Analysis (1) / Gap Analysis (2) output.
- Functional Test Cases (5) always require, and automatically run as part of the same request, Requirement Analysis (1), Gap Analysis (2), and Change Impact Analysis (3) — in that order — before generating test cases, then a Test Plan (4). These three are not optional pre-checks here; they run every time Functional Test Case Generation is selected, even if the user did not check them explicitly. If usable outputs for 1/2/3 already exist in `07-outputs/` for the same requirement input, reuse them per the Dependency Handling rules above instead of regenerating.
- UI Test Cases (16) depends on Requirement Analysis (1) and Gap Analysis (2) only, per `ui-agent.md`'s own Inputs list — it does not require Functional Test Cases (5) to exist first. If `FunctionalTestCases.md` already exists in `07-outputs/TestCases/`, it must still be read to avoid duplicate coverage, per `ui-agent.md`'s own constraint. See the Test Case Generation Mode section for how capability 5 can pull capability 16 into the same run. Its UI input is not limited to `01-docs/Figma/` — Figma links detected anywhere in a Jira hierarchy during `MasterQAOrchestrator/JiraToPRDOrchestrator.md` (recorded in `JiraStoryInventory.md`'s "Figma Links Detected" section, with design context attached in that orchestrator's Step 3a) carry equal standing, per `ui-agent.md`'s Inputs list. When the requirement source for this run was Jira (option 4 or 5), check for that section before concluding no Figma input exists. Before generating anything, `ui-agent.md` runs its own **Figma Link Coverage Check** — a blocking, per-link prompt (see that agent's section of the same name) that checks `07-outputs/TestCases/FigmaUICoverage.md` for whether each detected Figma link already has test cases, and asks the user how to proceed either way (generate/skip/skip-permanently if uncovered; add-net-new/leave-as-is/show-existing-first if already covered). This runs every time capability 16 executes, regardless of whether it was reached directly from the main checklist or via the Jira PRD flow.
- RTM (6) depends on Functional Test Cases (5) (and API/UI/Security/Accessibility test cases where present).
- Automation generation (7-11: Playwright, Selenium, Cypress, WebdriverIO, Appium) depends on Functional Test Cases (5) (and UI Test Cases, if available). **This is not a silent, automatic hand-off:** when a single execution plan includes both Functional Test Cases (5) and any Automation capability (7-11), execution pauses after `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` finishes. `MasterQAOrchestrator/AutomationOrchestrator.md`'s own Step 0 is a blocking human-in-the-loop gate that presents the generated test cases for explicit approve / request-changes / cancel before any automation tool selection or script generation begins — even within the same run and the same user request.
- Evaluations (17, 18, 19) depend on the artifact they evaluate already existing.
- Publishing (20, 21, 22) depends on the artifact being published already existing.

Example:

```text
User Selection: 5, 6 (Generate Functional Test Cases, Generate RTM)

Execution Plan:
1. Analyze Requirements             (requirement-analysis-agent + prompt)
2. Identify Requirement Gaps        (gap-analysis-agent + prompt)
3. Perform Change Impact Analysis   (change-impact-agent + prompt)
4. Generate Test Plan               (MasterQAOrchestrator/TestPlanOrchestrator.md)
5. Generate Functional Test Cases   (MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md)
6. Generate RTM                     (MasterQAOrchestrator/RTMOrchestrator.md)

Functional Test Case Generation always runs Requirement Analysis, Gap Analysis,
and Change Impact Analysis first, then requires a Test Plan — none were found in
07-outputs/, so all four run before Functional Test Cases and RTM.
```

---

# Execution Rules

- If the Capability Routing table lists an Orchestrator for the capability, that Orchestrator file is the entry point — load and follow it, not the agent/prompt directly. Read the Orchestrator file itself and do exactly what its own workflow section says, in its own order (typically: load CLAUDE.md → validate inputs → its own prerequisite/change-detection check → load standards (`03-standards/`) → load templates (`02-templates/`) → invoke the agent (`05-agents/`) with the prompt → save outputs). Do not substitute your own judgment about what order to load standards/templates/agent in when an Orchestrator exists — that ordering is the Orchestrator's job, not the skill's.
- Only for a capability with **no** Orchestrator (per the routing table, e.g. Requirement Analysis, Gap Analysis, Change Impact Analysis today) load directly in this order: Inputs → Standards (`03-standards/`, if relevant) → Templates (`02-templates/`, if relevant) → Agent (`05-agents/`) + its prompt (`04-prompts/`). For Requirement Analysis (1) and Gap Analysis (2) specifically, "if relevant" is resolved by `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` Step 6 (Load Framework Resources): always load `03-standards/testing-standards.md` and `03-standards/naming-conventions-standards.md` before invoking the agent, even when the capability is run standalone and not as part of Functional Test Case or Test Plan generation. For Change Impact Analysis (3), follow that same Orchestrator's Step 3 (Change Detection Check) for when it fires and load `02-templates/ImpactAnalysis.md`. Running these standalone must not mean running them with fewer standards than when they run as part of capability 4 or 5 — the standards apply regardless of which capability triggered the analysis.
- When delegating execution (e.g. to a subagent), name the exact Orchestrator file path in the instructions for every capability that has one, and require it to be read and followed — do not describe the task only in terms of the agent+prompt pair, even as shorthand.
- Do requirement understanding, correlation, and gap analysis before generating any test artifact (per `CLAUDE.md`).
- Never invent business functionality; report ambiguities and conflicts instead of guessing.
- Preserve Requirement IDs and Test Case IDs across phases for traceability.
- Save every generated artifact under `07-outputs/`, following the folder/naming convention already in use there. Never overwrite existing outputs without explicit user confirmation.
- If a selected capability has no orchestrator or evaluator yet, say so explicitly and proceed with the agent + prompt directly rather than silently skipping it.
- Always finish a run with the Execution Trace Artifact (see that section above) — this is a standard part of completing any capability 1–22, not an optional extra to be generated only on request.
- **Never state a generated count without independently recounting the actual artifact.** Any agent that writes a "Summary Counts"/coverage-count section into its own output (RequirementAnalysis.md, GapAnalysis.md, FunctionalTestCases.md/.xlsx, RTM.md/.xlsx, evaluator reports, etc.) must verify that count against the real content before writing it — e.g. count actual table rows (`grep -c "^| TC_"` or equivalent), check `.xlsx` sheet row counts, and confirm no duplicate IDs — rather than computing or estimating the total separately from what was actually generated. This applies whether the count is written by the generating agent itself or relayed afterward (to the user, into an evaluator's report, or into the Execution Trace Artifact) — a self-reported count is a claim to verify, not a fact to relay. A prior run generated 102 real test case rows but its own Summary Counts table said 81 — the error propagated into the evaluator's report and the trace artifact before being caught only because the user asked why the count looked low. Do not let that recur on any future run.

---

# Example Runs

## Example 1 — invoked with no activity stated

```text
User: /qa-framework

Claude: Where are the requirements for this run coming from? Select one or more.

1. Requirements are in PRD
2. Requirements are in BRD
3. Requirements are in Figma Flow
4. Create a Product Requirement Document from the Jira Stories
5. Create a PRD using Jira stories plus related Figma flow
6. Requirements in Screenshots
7. API Requirements and API Documentation
8. Requirements in Project Architecture
9. Other location

User: 1

Claude: What would you like to do?

1. Analyze Product Requirements
2. Identify Gaps in Requirements
3. Perform Change Impact Analysis
4. Generate Test Plan as per CodeCraft Template
5. Generate Functional Test Cases
6. Generate Requirement Traceability Matrix (RTM)
7. Generate Playwright Automation Scripts
8. Generate Selenium Automation Scripts
9. Generate Cypress Automation Scripts
10. Generate WebdriverIO Automation Scripts
11. Generate Appium Automation Scripts
12. Generate Security Test Cases
13. Generate API Test Cases
14. Generate Accessibility Test Cases
15. Generate Performance Test Cases
16. Generate UI Test Cases
17. Evaluate Test Cases
18. Evaluate Test Plan
19. Evaluate Requirement Traceability Matrix (RTM)
20. Publish Test Cases to TestLink
21. Publish Test Cases to TestRail
22. Publish Artifacts to Jira
23. Stay Idle and Do Nothing (Cancel / None)

Select the activities you want to execute (e.g. `4, 5, 6`).

User: 5, 6

Claude: Execution Plan:
1. Generate Functional Test Cases  (MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md)
2. Generate RTM                    (MasterQAOrchestrator/RTMOrchestrator.md)

No Test Plan found in 07-outputs/ — Functional Test Case generation requires one.
Proceeding to generate a Test Plan first (MasterQAOrchestrator/TestPlanOrchestrator.md),
then continuing with the plan above.

Proceed?
```

Claude then executes Test Plan → Functional Test Cases → RTM in order, saving each artifact under `07-outputs/`, and finishes by publishing the Execution Trace Artifact — one Loaded/Consumed/Executed/Written stage block per capability that ran (Requirement Analysis, Gap Analysis, Test Plan, Functional Test Cases, RTM), plus the narrative detail, evaluation scores, open items, and a final list of every file written.

## Example 2 — activity already stated in the invocation

```text
User: /qa-framework generate a test plan for the login PRD
```

Skip the checklist entirely; go straight to `MasterQAOrchestrator/TestPlanOrchestrator.md` with the supplied PRD as input.

## Example 3 — user selects None

```text
User: 23
```

Claude performs no action, executes nothing, and simply confirms nothing was run. The same applies if 23 is combined with other numbers (e.g. `4, 23`) — None overrides the rest of the selection.
