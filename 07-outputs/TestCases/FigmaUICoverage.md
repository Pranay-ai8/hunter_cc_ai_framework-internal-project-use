# Figma UI Coverage Map — IGZ Marquis Hunting App ("RanchOps Pro")

| Field | Value |
|---|---|
| Project | IGZ Marquis Hunting App ("RanchOps Pro") — JIRA project IMHA |
| Generated Date | 2026-11-03 |
| Purpose | Tracks, per distinct Figma link, whether UI Test Cases already exist for it (per `05-agents/ui-agent.md`'s "Figma Link Coverage Check"), so future runs of UI Test Case Generation (capability 16) know what has already been covered. |

## Status

**No Figma links detected in scope for this project as of 2026-11-03; file created for future use per `ui-agent.md`'s Outputs section.**

Confirmed during this run:
- 0 Figma links were detected anywhere in the Jira hierarchy (Epics, Stories, Tasks, Sub-tasks, comments) for JIRA project IMHA.
- `01-docs/Figma/` exists only as a `figma-links.md` file containing a single entry, which belongs to an unrelated application — not RanchOps Pro / IMHA.
- `01-docs/Screenshots/` and `01-docs/PDFs/` are both empty.

Because no Figma link is in scope, `ui-agent.md`'s mandatory "Figma Link Coverage Check" (the blocking, per-link human-in-the-loop prompt) was **not triggered** for this run — there is nothing to check coverage against. UI Test Cases for this run (`UITestCases.md`) were instead generated from the PRD's own narrative UI description (`07-outputs/GeneratedPRD(JIRA)/PRD_IMHA_DRAFT_2026-11-02.md`, Sections 7-9), per the explicit, already-made decision to accept lower fidelity (structural/behavioral coverage only, not pixel-level layout) as a documented limitation.

## Coverage Table

| Figma Link | Source (Jira Issue Key / `01-docs/Figma`) | Covered | Test Case IDs | Module | Last Generated |
|---|---|---|---|---|---|
| *(none — no Figma links in scope)* | | | | | |

No rows exist yet. If a Figma link is added to this project in a future run (either directly under `01-docs/Figma/` or detected in the Jira hierarchy during a `JiraToPRDOrchestrator.md` run), this table will be populated per the coverage-tracking rules in `05-agents/ui-agent.md`'s Outputs section: one row per distinct Figma link, columns Figma Link / Source / Covered (`Yes`/`Excluded`) / Test Case IDs / Module / Last Generated.
