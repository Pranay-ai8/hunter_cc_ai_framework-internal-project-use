# QA-AI-Framework

## Folder Overview

| Folder | Purpose |
|---|---|
| **00-framework** | Orchestration contract / governance layer — manifests describing what components exist and how they connect (self-documentation of the framework, not test artifacts) |
| **01-docs** | Input Knowledge (BRD, PRD, API Docs, Figma, Screenshots, PDFs, Architecture, ReleaseNotes, Other Requirement Docs) |
| **02-templates** | Reusable output formats and templates for all test artifacts |
| **03-standards** | QA standards and coding guidelines, including per-automation-stack standards |
| **04-prompts** | AI prompt definitions (what to think) |
| **05-agents** | Agent execution logic and workflows (what to do), including per-framework automation agents |
| **06-integrations** | Adapters, providers, and configs for external system integrations (Jira, GitHub, TestLink) and the MCP registry |
| **07-outputs** | Generated test artifacts and deliverables, including automation frameworks and dated archives |
| **08-ui** | Next.js-based web UI for interacting with the framework |
| **09-evaluators** | Self-review / quality-gate layer — agents that score and critique already-generated artifacts |
| **LLMs** | Placeholder folders for LLM-specific setup/config notes (claude, gemini, github-copilot, openai, other llms) |
| **MasterQAOrchestrator** | Root-level orchestrator definitions that drive end-to-end agent execution for a given request type, including a full family of per-framework automation orchestrators |

## Agent Structure
Every agent follows this exact structure:

- Purpose
- Responsibilities
- Execution Workflow
- MCP Usage
- Validation
- Error Handling
- Constraints
- Completion Criteria

## Master QA Orchestrator - Orchestration Hub
Orchestration is driven by files under **MasterQAOrchestrator/** at the repository root. Ten orchestrator files are currently defined:

- `MasterQAOrchestrator/JiraToPRDOrchestrator.md` — drives the end-to-end JIRA-to-PRD workflow: scope resolution → story ingestion → (Figma design context, when Figma is expected or detected) → duplicate detection → PRD generation, with a blocking human-in-the-loop review gate after every step that can flag issues, before handing the approved PRD to the rest of the QA lifecycle as its requirement source. Serves Requirement Source Selection option 4 (Jira stories) and option 5 (Jira stories plus related Figma flow)
- `MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md` — drives Functional Test Case generation
- `MasterQAOrchestrator/TestPlanOrchestrator.md` — drives Test Plan generation
- `MasterQAOrchestrator/RTMOrchestrator.md` — drives Requirements Traceability Matrix (RTM) generation and maintenance
- `MasterQAOrchestrator/AutomationOrchestrator.md` — top-level automation tool router; determines web vs. mobile and which tool (Playwright, Cypress, Selenium, WebdriverIO, or Appium) the user wants, then hands off entirely to the matching orchestrator below
- `MasterQAOrchestrator/PlaywrightAutomationOrchestrator.md` — drives Playwright automation script generation (TypeScript + Cucumber, JavaScript + Cucumber, or Java + TestNG/Maven) from existing test cases
- `MasterQAOrchestrator/CypressAutomationOrchestrator.md` — drives Cypress automation script generation (plain JavaScript / ES Modules only) from existing test cases
- `MasterQAOrchestrator/SeleniumAutomationOrchestrator.md` — drives Selenium automation script generation (Java + Selenium WebDriver 4.x + TestNG + Maven, Page Object Model only) from existing test cases
- `MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md` — drives WebdriverIO (JavaScript) automation script generation from existing test cases
- `MasterQAOrchestrator/AppiumAutomationOrchestrator.md` — drives Appium (Java) mobile automation script generation (Android/iOS) from existing test cases

Each orchestrator file defines the exact execution sequence for its request type:
- Loads global instructions (CLAUDE.md)
- Validates required inputs under 01-docs
- **Change Detection Check** — checks whether outputs from a prior run already exist (`FunctionalTestCases.md` / `TestPlan.md`) and whether requirement documents have changed since. If nothing changed, reuses existing outputs as-is. If requirements changed, invokes `05-agents/change-impact-agent.md` + `04-prompts/change-impact-prompt.md` (using `02-templates/ImpactAnalysis.md`) to generate `ImpactAnalysis.md`, driving the rest of the run as an **incremental run**
- Runs Requirement Analysis (reusing `07-outputs/RequirementAnalysis.md` if it already exists and unchanged; on an incremental run, updates it with only the new/modified/deleted requirements from `ImpactAnalysis.md`)
- Runs Gap Analysis (reusing `07-outputs/GapAnalysis.md` if it already exists and unchanged; updated incrementally when `ImpactAnalysis.md` flags it)
- Loads the relevant templates and standards for the target artifact
- Executes the corresponding agent + prompt pair (e.g., functional-agent.md + functional-testcase-prompt.md, or the Test Plan equivalent). On an incremental run, the agent additionally consumes the existing output plus `ImpactAnalysis.md`'s impact section, and is instructed to add **only new/changed content** (new Functional Test Cases continue the existing ID sequence; the Test Plan updates only the impacted sections) rather than regenerating from scratch
- Saves outputs into `07-outputs/` — appending/updating in place on an incremental run instead of overwriting

The Test Plan Orchestrator reuses an `ImpactAnalysis.md` already produced by a same-cycle Functional Test Case Orchestrator run instead of re-invoking the change-impact agent a second time.

The **RTM Orchestrator** follows a stricter create-once / update-only rule:
- If `07-outputs/RequirementTraceabilityMatrix/RTM.md` does not exist yet, it is generated in full (first-time run).
- If it already exists, the orchestrator checks for requirement/test-artifact changes (reusing a same-cycle `ImpactAnalysis.md` if available, or running the change-impact agent otherwise). If nothing changed, the RTM is left untouched entirely.
- If something changed, only the rows tied to new/modified/deleted requirements or test cases are added, updated, or removed — the RTM is **never regenerated or renumbered from scratch**, and no other folder or file is modified without explicit user confirmation first.

The **JIRA-to-PRD Orchestrator** coordinates three new agents in strict sequence — `05-agents/jira-story-ingestion-agent.md`, `05-agents/duplicate-detection-agent(JIRA).md`, and `05-agents/prd-generation-agent(JIRA).md` — never invoked directly outside of it. It never auto-consolidates stories or silently resolves ambiguities/conflicts: every flagged finding (provisional story inventory, duplicate/near-duplicate/overlapping stories, unresolved PRD ambiguities/conflicts/assumptions/risks) stops the workflow and presents numbered options to the user. The resulting PRD is saved only on explicit approval, always to `07-outputs/GeneratedPRD(JIRA)/` (never `01-docs/PRD/`), in the user's chosen format (PDF by default, or `.xlsx`/`.doc`/`.md`), and then becomes the requirement source for the rest of the QA lifecycle exactly as if a PRD had been selected directly. With option 5 (Jira stories plus related Figma flow), Figma is treated as a required companion source: links are collected from the Jira hierarchy, `01-docs/Figma/figma-links.md`, and the user, read through the Figma MCP, and correlated to the stories in the PRD (Matched / Jira-only / Figma-only / Conflict, never silently merged).

The **Automation Orchestrator** is a pure router: it asks (or infers) web vs. mobile and which tool, then delegates entirely to `PlaywrightAutomationOrchestrator.md`, `CypressAutomationOrchestrator.md`, `SeleniumAutomationOrchestrator.md`, `WebdriverIOAutomationOrchestrator.md`, or `AppiumAutomationOrchestrator.md`. Each of those five automation orchestrators supports two mutually exclusive modes: **(A) Generate New Framework** — full scaffold in `07-outputs/Automation/` — or **(B) Add To Existing Framework** — automate directly into a user-supplied `<target_path>` following that project's own detected conventions (via `05-agents/framework-detection-agent.md`, 344 lines). Artifacts from different tools/stacks are never mixed within the same generation run or target project.

Note: `04-prompts/master-prompt.md` exists as a prompt definition (422 lines), but there is currently no `05-agents/master-agent.md` file in the repository — orchestration is implemented per-workflow under `MasterQAOrchestrator/` rather than through a single master agent file. Standalone Gap Analysis, UI, API, Security, and Accessibility request types do not yet have a dedicated root orchestrator file.

## Project Overview
QA-AI-Framework is an enterprise-grade quality assurance framework designed for AI-powered test case generation, requirement analysis, comprehensive test planning, and multi-stack automation script generation. It provides a structured approach to correlating requirements from multiple sources (BRD, PRD, Figma, APIs, Jira) and generating traceable, automation-ready test artifacts — plus a self-evaluation layer that scores its own outputs.

The framework is built for:
- **QA Architects** performing requirement analysis before development
- **Test Engineers** generating functional, UI, API, and security test cases
- **Automation Engineers** designing and generating automation scripts across Playwright, Cypress, Selenium, WebdriverIO, and Appium
- **Product Teams** ensuring comprehensive coverage and requirement traceability

The framework acts as both a starter kit and a production-ready system, providing guidance, standards, templates, agent prompts, orchestrators, and evaluators for end-to-end QA workflows.

## Purpose and Goals
The framework aims to:
- **Centralize QA Inputs** → Organize PRDs, BRDs, API docs, Figma designs, Jira epics/stories, and screenshots in a structured format
- **Maximize Test Coverage** → Correlate requirements across sources and identify comprehensive testing scenarios
- **Minimize Duplication** → Use requirement analysis and gap identification to avoid duplicate test cases
- **Standardize Deliverables** → Provide reusable standards, templates, and agent prompts for consistent outputs
- **Enable Scalability** → Support automation script generation across five different frameworks/tools (Playwright, Cypress, Selenium, WebdriverIO, Appium) in addition to functional/UI/API/security testing
- **Improve Traceability** → Generate RTM and requirement correlation documents for audit and compliance
- **Facilitate Automation** → Generate production-ready automation code (Page Objects, spec/test files, CI/CD pipelines) directly from test cases
- **Self-Govern Quality** → Evaluate its own generated Test Cases, Test Plans, RTM, and automation code against the framework's own standards before hand-off

## Features
- **Comprehensive Requirement Analysis** → Requirement Analysis Agent correlates requirements across BRD, PRD, Figma, APIs, Jira, and screenshots
- **Change Impact Analysis** → A dedicated Change Impact Agent (`05-agents/change-impact-agent.md`, 432 lines) compares the latest requirements against the previous baseline, classifies new/modified/deleted/clarified requirements, and drives incremental (append-only) regeneration of Functional Test Cases and targeted updates to the Test Plan instead of full regeneration
- **Multi-dimensional Testing** → Dedicated, populated agents for Functional and UI testing; Gap Analysis and RTM agents are fully built out; API, Security, and Accessibility agent files exist but are currently empty placeholders (their prompts/standards are populated)
- **Ten Orchestrators** → JIRA-to-PRD, Functional Test Case, Test Plan, RTM, plus an Automation router and five per-tool automation orchestrators (Playwright, Cypress, Selenium, WebdriverIO, Appium)
- **JIRA-to-PRD Workflow** → A dedicated orchestrator (`MasterQAOrchestrator/JiraToPRDOrchestrator.md`) turns a chosen set of JIRA stories into an approved PRD (optionally correlated with a related Figma flow) via story ingestion, duplicate detection, and PRD generation agents, with blocking human-in-the-loop review gates at every stage that can flag an issue, before handing the result off as the requirement source for the rest of the lifecycle
- **Multi-Framework Automation Generation** → Populated automation agents for Playwright (TS/JS/Java), Cypress (JS), Selenium (Java), WebdriverIO (JS), and Appium (Java), each with a matching framework-scaffolding agent and stack-specific standards document
- **Framework Detection** → `05-agents/framework-detection-agent.md` (344 lines) reverse-engineers an existing automation project's conventions so new automation can be added indistinguishably from hand-written code
- **Self-Evaluation Layer** → `09-evaluators/` scores and critiques already-generated Test Cases, Test Plans, RTM, and Playwright/Selenium code against the framework's own standards, independent of the generation agents
- **Orchestration Contract Layer** → `00-framework/` documents what components exist (`ComponentCatalog.md`), how they depend on each other (`DependencyMap.md`), and the overall framework architecture (`FrameworkManifest.md`); `WorkflowManifest.md` is currently an empty placeholder
- **Complete Quality Standards** → 21 standards documents in `03-standards/`, including five populated automation-stack standards (Appium Java, Cypress JavaScript, Playwright Java/JavaScript/TypeScript, WebdriverIO JavaScript); a handful of general standards (api, execution, performance, reporting) remain empty placeholders
- **18 Reusable Templates** → Test Plans, Test Cases, Security/Accessibility Test Cases, Gap Analysis, RTM, Defect Reports, Automation Scripts, Automation/Appium/WebdriverIO Framework scaffolds, a JIRA PRD Word template, and more
- **External Integrations** → Adapters and providers for Jira, GitHub, and TestLink under `06-integrations/`, plus a live TestLink MCP server wired up in `.mcp.json`
- **Generic Test Case Library** → Functional Test Case generation asks the user to choose between `FUNCTIONAL_ONLY` and `FUNCTIONAL_AND_GENERIC` modes; the latter evaluates the reusable, read-only `010-GenericTestCases/GenericTestCases.xlsx` library (e.g. Bluetooth, Network, Interrupts worksheets) against project requirements, classifying each Generic Test Case as `APPLICABLE`, `NOT_APPLICABLE`, or `INSUFFICIENT_INFORMATION` — only `APPLICABLE` cases are included in the executable suite, preserving their source worksheet/ID/reference for traceability. On incremental runs, Project Context changes can also trigger re-evaluation of previously-selected Generic Test Cases without regenerating the whole library
- **Live Jira Requirement Source** → When the Atlassian MCP is connected, Requirement Analysis treats Jira as the source of truth for User Stories (Epics, Stories, Acceptance Criteria, linked issues, comments), retaining each requirement's Jira issue key for traceability instead of duplicating it locally; conflicts between Jira and other sources are reported, never silently resolved
- **Project Context Analysis** → `04-prompts/requirement-analysis-prompt.md` now has the Requirement Analysis Agent review Project Context documents first (product/system background, domain, scope, modules, boundaries, terminology, tech/platform context, constraints, dependencies) to inform how it interprets BRD/PRD/FRD/Jira/Figma/API/Architecture sources; Project Context is treated as supporting context, never as a confirmed requirement, and conflicts with formal requirement sources are reported rather than silently resolved. The prompt and the actual folder now agree on `01-docs/ProjectContext/`.
- **Multiple Input Sources** → Support for BRD, PRD, Figma designs, API documentation, screenshots, project context documents, and other requirement documents, plus live Jira via Atlassian MCP
- **Test Artifact Generation** → Markdown, Excel outputs for test cases, plans, gap analysis, and RTM
- **Traceability Matrix** → RTM for mapping test cases to requirements and ensuring complete coverage
- **Best Practices Guidance** → Comprehensive standards for consistent, automation-friendly, and industry-ready test design
- **Live Orchestrator Dashboard** → The 08-ui web app includes a "Master QA Orchestrator" panel with one-click **Functional Test Orchestrator** and **Test Plan Orchestrator** buttons that call the Anthropic API directly, stream each execution step live, show a real-time Input/Loaded/Written file flow diagram, and write generated `.md`/`.xlsx` artifacts straight into `07-outputs/` — with a confirmation dialog before each run and a "Clear Session" control to reset generated reports and dashboard cache without touching folder structure
- **Web UI** → A Next.js-based web application (08-ui/) for interacting with the framework; see UI-GETTING-STARTED.md and UI-SUMMARY.md at the repository root, and 08-ui/README.md, QUICKSTART.md, SETUP.md, ARCHITECTURE.md, and INTEGRATION.md for details

## Project Structure

```text
QA-AI-Framework/
├── CLAUDE.md                          # Project-specific guidance and rules
├── README.md                          # This file
├── UI-GETTING-STARTED.md              # Getting-started guide for the 08-ui web app
├── UI-SUMMARY.md                      # Summary/overview of the 08-ui web app
├── .mcp.json                          # Configures the TestLink MCP server (docker-based stdio server)
│
├── .claude/skills/                    # Claude Code skills
│   └── qa-framework/
│       └── SKILL.md                   # /qa-framework — capability checklist + dependency-aware routing to orchestrators/agents
│
├── 00-framework/                      # Orchestration contract / governance layer
│   ├── README.md                      # Explains how the three manifest files relate
│   ├── FrameworkManifest.md           # Framework architecture, principles, scope, lifecycle
│   ├── ComponentCatalog.md            # Inventory of every component (orchestrators/agents/prompts/standards/templates/evaluators/integrations/outputs)
│   ├── DependencyMap.md               # Orchestrator → Agent → Prompt → Standard → Template → Output chains
│   └── WorkflowManifest.md            # Empty placeholder — intended to list orchestrator trigger conditions
│
├── MasterQAOrchestrator/              # Root-level orchestrator definitions
│   ├── JiraToPRDOrchestrator.md            # Drives the JIRA-to-PRD workflow (ingestion → duplicate detection → PRD generation) with human review gates
│   ├── FunctionalTestCaseOrchestrator.md  # Drives the Functional Test Case workflow end-to-end
│   ├── TestPlanOrchestrator.md            # Drives the Test Plan workflow end-to-end
│   ├── RTMOrchestrator.md                 # Drives RTM creation (once) and incremental, row-level updates thereafter
│   ├── AutomationOrchestrator.md          # Top-level router: web vs. mobile, then which automation tool
│   ├── PlaywrightAutomationOrchestrator.md    # Playwright TS/JS/Java automation generation
│   ├── CypressAutomationOrchestrator.md       # Cypress JavaScript automation generation
│   ├── SeleniumAutomationOrchestrator.md      # Selenium Java automation generation
│   ├── WebdriverIOAutomationOrchestrator.md   # WebdriverIO JavaScript automation generation
│   └── AppiumAutomationOrchestrator.md        # Appium Java mobile automation generation
│
├── 01-docs/                           # Input Knowledge
│   ├── BRD/                           # Business Requirements Documents
│   ├── PRD/                           # Product Requirements Documents
│   ├── Figma/                         # Figma design references
│   ├── API Docs/                      # API specifications and documentation
│   ├── Screenshots/                   # UI screenshots and visual references
│   ├── PDFs/                          # Supporting PDF documents
│   ├── Architecture/                  # Architecture and system design documents
│   ├── ReleaseNotes/                  # Release notes and change logs
│   ├── ProjectContext/             # Supplementary project/system context documents
│   └── Other Requirement Docs/        # Additional requirement documentation
│
├── 010-GenericTestCases/              # Reusable, read-only Generic Test Case library
│   └── GenericTestCases.xlsx          # Multi-worksheet library (e.g. Bluetooth, Network, Interrupts)
│
├── 02-templates/                      # Reusable Output Formats (17 templates)
│   ├── TestPlan.md
│   ├── TestCase.md
│   ├── SecurityTestcase.md
│   ├── AccessibilityTestcase.md
│   ├── RequirementAnalysis.md
│   ├── GapAnalysis.md
│   ├── ImpactAnalysis.md              # Change Impact Analysis report template
│   ├── RTM.md
│   ├── DefectReport.md
│   ├── AutomationScript.md
│   ├── AutomationFramework.md
│   ├── AppiumFramework.md             # Appium mobile framework scaffold template
│   ├── WebdriverIOFramework.md        # WebdriverIO framework scaffold template
│   ├── PageObject.md
│   ├── TestData.md
│   ├── FinalSummary.md
│   ├── _README_Template.md
│   └── JiraPRDTemplate/
│       └── PRD_Template.docx          # Word template used as the structural/terminology reference for JIRA-sourced PRDs
│
├── 03-standards/                      # QA & Coding Standards (21 documents)
│   ├── testing-standards.md
│   ├── testcase-standards.md
│   ├── testplan-standards.md
│   ├── rtm-standards.md
│   ├── code-review-standards.md
│   ├── naming-conventions-standards.md
│   ├── readme-standards.md
│   ├── accessibility-standards.md
│   ├── automation-standards.md
│   ├── locator-standards.md
│   ├── ui-standards.md
│   ├── selenium-standards.md
│   ├── appium-java-standards.md               # Populated
│   ├── cypress-javascript-standards.md        # Populated
│   ├── playwright-java-standards.md           # Populated
│   ├── playwright-javascript-standards.md     # Populated
│   ├── playwright-typescript-standards.md     # Populated
│   ├── webdriverio-javascript-standards.md    # Populated
│   ├── api-standards.md               # Empty placeholder
│   ├── performance-standards.md       # Empty placeholder
│   ├── execution-standards.md         # Empty placeholder
│   └── reporting-standards.md         # Empty placeholder
│
├── 04-prompts/                        # AI Behavior Definitions (22 files)
│   ├── jira-story-ingestion-prompt.md
│   ├── duplicate-detection-prompt(JIRA).md
│   ├── prd-generation-prompt(JIRA).md
│   ├── master-prompt.md
│   ├── requirement-analysis-prompt.md
│   ├── functional-testcase-prompt.md
│   ├── testplan-gen-prompt.md
│   ├── gap-analysis-prompt.md
│   ├── change-impact-prompt.md        # Change Impact Analysis prompt
│   ├── rtm-prompt.md
│   ├── ui-testcase-prompt.md
│   ├── playwright-script-prompt.md
│   ├── selenium-script-prompt.md
│   ├── automation-prompt.md
│   ├── api-prompt.md                  # Minimal (1 line) placeholder
│   ├── security-prompt.md             # Minimal (1 line) placeholder
│   ├── accessibility-prompt.md        # Empty placeholder
│   ├── execution-prompt.md            # Empty placeholder
│   ├── code-review-prompt.md          # Empty placeholder
│   ├── README_Prompt.md               # Empty placeholder
│   ├── jira-sync-prompt.md            # Empty placeholder
│   ├── testlink-sync-prompt.md        # Empty placeholder
│   └── testrail-sync-prompt.md        # Empty placeholder
│
├── 05-agents/                         # Execution Logic (32 agents)
│   ├── jira-story-ingestion-agent.md
│   ├── duplicate-detection-agent(JIRA).md
│   ├── prd-generation-agent(JIRA).md
│   ├── requirement-analysis-agent.md
│   ├── gap-analysis-agent.md
│   ├── functional-agent.md
│   ├── ui-agent.md
│   ├── change-impact-agent.md
│   ├── rtm-agent.md
│   ├── framework-detection-agent.md   # Reverse-engineers an existing automation project's conventions
│   ├── playwright-typescript-agent.md
│   ├── playwright-javascript-agent.md
│   ├── playwright-java-agent.md
│   ├── cypress-javascript-agent.md
│   ├── selenium-agent.md
│   ├── webdriverio-javascript-agent.md
│   ├── appium-java-agent.md
│   ├── automation-framework-typescript-agent.md
│   ├── automation-framework-javascript-agent.md
│   ├── automation-framework-java-agent.md
│   ├── automation-framework-cypress-javascript-agent.md
│   ├── automation-framework-selenium-java-agent.md
│   ├── automation-framework-webdriverio-javascript-agent.md
│   ├── automation-framework-appium-java-agent.md
│   ├── api-agent.md                   # Empty placeholder
│   ├── security-agent.md              # Empty placeholder
│   ├── accessibility-agent.md         # Empty placeholder
│   ├── testplan-gen-agent.md          # Empty placeholder
│   ├── code-review-agent.md           # Empty placeholder
│   ├── execution-agent.md             # Empty placeholder
│   ├── jira-sync-agent.md             # Empty placeholder
│   ├── testlink-sync-agent.md         # Empty placeholder
│   └── testrail-sync-agent.md         # Empty placeholder
│
├── 06-integrations/                   # External system integrations (adapters/providers/configs)
│   ├── README.md
│   ├── adapters/
│   │   ├── jira-adapter.md
│   │   ├── github-adapter.md
│   │   ├── testlink-adapter.md
│   │   └── testlink_export.py
│   ├── configs/
│   │   └── mcp-registry.json
│   └── providers/
│       ├── jira.md
│       ├── github.md
│       └── testlink.md
│
├── 07-outputs/                        # Generated Deliverables (session-specific; not version-controlled)
│   ├── RequirementAnalysis/           # Generated requirement analysis reports
│   ├── TestPlan/                      # Generated test plans
│   ├── TestCases/                     # Generated test cases (functional, UI, API, etc.)
│   ├── GapAnalysis/                   # Gap analysis reports
│   ├── ChangeImpact/ / ImpactAnalysis/  # Change Impact Analysis reports (incremental runs)
│   ├── RTM/ / RequirementTraceabilityMatrix/  # RTM — created once, updated incrementally thereafter
│   ├── GeneratedPRD(JIRA)/            # PRDs authored from Jira Stories (Requirement Source Selection options 4 and 5) — PDF/.xlsx/.doc/.md, PDF by default
│   ├── Reports/                       # Test reports and metrics
│   ├── Automation/                    # Current-run automation output
│   │   ├── Framework/                 # Scaffolded project (config, src/helper, src/support, .github/workflows)
│   │   ├── PageObjects/
│   │   ├── Scripts/                   # features/steps (BDD) or plain spec files
│   │   └── docs/                      # Includes dom-snapshots/
│   └── _archive/                      # Dated snapshots of prior automation runs, per framework
│       ├── Automation-Appium-2026-08-20/
│       ├── Automation-Cypress-2026-08-10/
│       ├── Automation-Selenium-2026-08-10/
│       ├── Automation-WebdriverIO-2026-08-20/
│       ├── Automation-java-2026-08-05/
│       ├── Automation-playwright-js-cucumber-2026-08-10/
│       └── Automation-playwright-ts-cucumber-2026-08-14/
│
├── 08-ui/                              # Next.js web UI for the framework (live orchestrator dashboard)
│   ├── README.md / QUICKSTART.md / SETUP.md / ARCHITECTURE.md / INTEGRATION.md
│   ├── UPLOAD-DOCS.md / UPLOAD-QUICK-START.md
│   ├── .env.local                      # ANTHROPIC_API_KEY (git-ignored; required for live orchestrator runs)
│   ├── src/
│   │   ├── app/                        # page.tsx, layout.tsx, api/ (orchestrate, clear-session, upload, config, execute)
│   │   ├── components/                 # MasterOrchestratorSection, OrchestratorPanel, SelectionPanel, AdditionalOptions, ExecutionPanel, HistoryPanel, UploadPanel
│   │   ├── lib/orchestrator.ts         # Server-side pipeline: loads CLAUDE.md + agents/prompts/templates, calls Claude, writes 07-outputs
│   │   ├── store/agentStore.ts         # Zustand store (persisted execution history/config)
│   │   └── config/framework.ts         # Agent/Standard/Prompt catalog for the Configuration tab
│   └── package.json                    # Includes @anthropic-ai/sdk, exceljs, mammoth
│
├── 09-evaluators/                      # Self-review / quality-gate layer
│   ├── eval_agents/
│   │   ├── testcase-evaluator-agent.md    # Scores/critiques generated Test Cases
│   │   ├── testplan-evaluator-agent.md    # Scores/critiques generated Test Plans
│   │   ├── rtm-evaluator-agent.md         # Scores/critiques the RTM
│   │   ├── playwright-review-agent.md     # Reviews generated Playwright automation code
│   │   └── selenium-review-agent.md       # Reviews generated Selenium automation code
│   ├── eval_prompts/                   # One prompt per evaluator agent above
│   ├── eval_standards/
│   │   └── evaluation-standards.md     # Scoring rubric and approval-recommendation criteria
│   ├── eval_outputs/                   # Generated evaluation reports (MissingScenarios, TestCoverageReport, PlaywrightReview, SeleniumReview, RTMEvaluation, TestPlanEvaluation)
│   └── eval_templates/
│
├── LLMs/                                # Placeholder folders for LLM-specific setup/config docs
│   ├── claude/                          # Empty
│   ├── gemini/                          # Empty
│   ├── github-copilot/                  # Empty
│   ├── openai/                          # Empty
│   └── other llms/                      # Empty
│
└── .venv/                              # Python virtual environment
```

### Major Folders

| Folder | Purpose |
|---|---|
| 00-framework | Self-documentation of the framework's own architecture, component inventory, and dependency graph. |
| 01-docs | Stores business and technical input documents including BRDs, PRDs, API specs, Figma designs, screenshots, and other requirement documentation. |
| 02-templates | Contains reusable templates for all test artifacts (test cases, plans, gap analysis, RTM, automation scripts, etc.). |
| 03-standards | Holds testing standards, QA guidelines, per-stack automation standards, and naming conventions for consistent outputs. |
| 04-prompts | Contains AI prompt definitions that guide agent behavior and reasoning for each testing discipline. |
| 05-agents | Contains executable agent workflows that orchestrate test generation, analysis, planning, and automation-script generation tasks. |
| 06-integrations | Provides adapters and providers for external tools (Jira, GitHub, TestLink) plus the MCP registry. |
| 07-outputs | Stores generated artifacts including test plans, test cases, gap analysis reports, and automation frameworks/scripts. |
| 09-evaluators | Stores the evaluator agents and reports that independently score already-generated artifacts. |

## Technology Stack
The repository is primarily document- and workflow-oriented, with a live Next.js dashboard layered on top. Current elements observed in the repository include:
- Markdown for documentation and generated reports
- Python virtual environment for local execution, `.docx` parsing, TestLink export (`06-integrations/adapters/testlink_export.py`), and Excel/PDF export tasks
- Next.js 14 + React 18 + TypeScript + Tailwind CSS for the 08-ui web dashboard
- `@anthropic-ai/sdk` for live, server-side Claude API calls that drive the Orchestrator dashboard buttons
- `mammoth` for extracting text from `.docx` requirement documents
- `exceljs` for generating `.xlsx` outputs (Gap Analysis, Functional Test Cases) directly from the dashboard
- Zustand for client-side dashboard state (persisted execution history/config)
- A TestLink MCP server (Docker-based, stdio) configured in `.mcp.json`
- Excel-compatible output generation for test case tables
- VS Code workspace-based project structure

## Prerequisites
Before using the framework, ensure you have:
- A local copy of this repository
- Python 3.x installed
- A virtual environment (the repository already includes a local .venv folder)
- Access to relevant input documents in the docs folders
- Docker (only if you want to run the TestLink MCP server configured in `.mcp.json`)

## Installation

```bash
cd /path/to/CC-QA-AI-Framework
python3 -m venv .venv
source .venv/bin/activate
```

## Configuration
- Place requirement artifacts in the appropriate subfolder under 01-docs (BRD, PRD, Figma, API Docs, Screenshots, PDFs, Architecture, ReleaseNotes, Other Requirement Docs).
- Update or create prompt files in 04-prompts as needed for specialized workflows.
- Configure agent behavior in 05-agents based on your project requirements.
- Keep generated deliverables in 07-outputs organized by artifact type.
- Set up integrations in 06-integrations (adapters/providers) and the MCP registry for external tool connectivity.
- Follow the guidance in 03-standards/testing-standards.md (and the relevant stack-specific standard) for consistent test/automation design.

## How to Use the Framework

### Fastest Start — the `/qa-framework` skill
Rather than picking orchestrator/agent files manually, run `/qa-framework` (see [.claude/skills/ — QA Framework Slash-Command Skill](#claudeskills--qa-framework-slash-command-skill) above). It first asks where this run's requirements come from (PRD, BRD, Figma Flow, create a PRD from Jira Stories, create a PRD from Jira stories plus the related Figma flow, Screenshots, API Requirements/Docs, Project Architecture, or Other location), then presents the 23 available capabilities as a numbered checklist, resolves dependencies between whatever you select (e.g. picking Functional Test Cases automatically runs Requirement Analysis → Gap Analysis → Change Impact Analysis → Test Plan first if needed), and executes them in order.

### Quick Start (5 Steps) — manual, file-by-file
1. **Prepare Inputs** → Place requirement documents in 01-docs/BRD, 01-docs/PRD, 01-docs/Figma, 01-docs/API Docs folders
2. **Run Requirement Analysis** → Use 05-agents/requirement-analysis-agent.md to correlate all inputs and identify business flows
3. **Identify Gaps** → Use 05-agents/gap-analysis-agent.md + 04-prompts/gap-analysis-prompt.md to find missing requirements and coverage areas
4. **Generate Test Plan** → Use MasterQAOrchestrator/TestPlanOrchestrator.md (which invokes 05-agents/testplan-gen-agent.md) to create comprehensive test strategy
5. **Generate Test Cases** → Use 05-agents/functional-agent.md, 05-agents/ui-agent.md based on input type (API/Security/Accessibility agents are currently empty placeholders)

### Extended Workflow
1. **Requirement Analysis** (05-agents/requirement-analysis-agent.md)
   - Correlate BRD, PRD, Jira stories, acceptance criteria
   - Identify business rules, user journeys, and validations

2. **Gap Analysis** (05-agents/gap-analysis-agent.md, 04-prompts/gap-analysis-prompt.md)
   - Identify missing requirements and business rules
   - Report missing validations, permissions, exception handling
   - Generate Requirement Gap Report

2a. **Change Impact Analysis** (05-agents/change-impact-agent.md, on repeat runs only)
   - Compare the latest requirement documents against the previous baseline
   - Classify new, modified, deleted, and clarified requirements
   - Determine which downstream artifacts (Test Plan, Functional/API/UI Test Cases, RTM, Automation) need regeneration vs. update vs. no action
   - Generate ImpactAnalysis.md to drive incremental (append-only) regeneration instead of starting over

3. **Test Planning** (MasterQAOrchestrator/TestPlanOrchestrator.md)
   - Define test strategy and scope
   - Identify test types (functional, UI, API, security, performance, accessibility)
   - Create smoke and regression suites

4. **Test Case Generation**
   - **Functional Cases** → Use 05-agents/functional-agent.md for business logic validation
   - **UI Cases** → Use 05-agents/ui-agent.md for form validation, navigation, responsive design
   - **API/Security/Accessibility Cases** → 05-agents/api-agent.md, security-agent.md, accessibility-agent.md exist as empty placeholders today; their prompts (api-prompt.md, security-prompt.md) and standards (accessibility-standards.md) are populated

5. **Automation Script Generation**
   - Use MasterQAOrchestrator/AutomationOrchestrator.md to route to the tool-specific orchestrator (Playwright, Cypress, Selenium, WebdriverIO, Appium)
   - Generates Page Objects, spec/test files, test data/fixtures, and a CI/CD pipeline into `07-outputs/Automation/` (or a supplied `<target_path>`)

6. **Traceability & Documentation**
   - Generate RTM mapping test cases to requirements (05-agents/rtm-agent.md, MasterQAOrchestrator/RTMOrchestrator.md)
   - Create test documentation using 02-templates templates
   - Export test cases to Excel for wider distribution

7. **Self-Evaluation** (09-evaluators/)
   - Run the relevant evaluator agent (testcase, testplan, RTM, Playwright review, Selenium review) against already-generated artifacts
   - Review the scored report in `09-evaluators/eval_outputs/` before sign-off

8. **Test Execution** (04-prompts/execution-prompt.md — empty placeholder; 05-agents/execution-agent.md — empty placeholder)
   - Track test execution and results
   - Report defects with evidence using 02-templates/DefectReport.md
   - Generate test reports and metrics

## Available Agents and Responsibilities

| Agent | Agent File | Prompt File | Responsibility |
|---|---|---|---|
| JIRA-to-PRD Orchestrator | MasterQAOrchestrator/JiraToPRDOrchestrator.md | 04-prompts/jira-story-ingestion-prompt.md, duplicate-detection-prompt(JIRA).md, prd-generation-prompt(JIRA).md | Orchestrates JIRA scope resolution → story ingestion → Figma design context (optional for option 4, required for option 5) → duplicate detection → PRD generation (with Jira ↔ Figma correlation when Figma is attached), with a blocking human review gate after each step, then hands the approved PRD off as the requirement source for the rest of the lifecycle. |
| JIRA Story Ingestion Agent | 05-agents/jira-story-ingestion-agent.md (302 lines) | 04-prompts/jira-story-ingestion-prompt.md (206) | Pulls stories/epics/acceptance criteria for a resolved JIRA scope and produces `JiraStoryInventory.md`, flagging incomplete/ambiguous/conflicting stories for human review. |
| Duplicate Detection Agent (JIRA) | 05-agents/duplicate-detection-agent(JIRA).md (147 lines) | 04-prompts/duplicate-detection-prompt(JIRA).md (108) | Classifies stories in the inventory as DUPLICATE/NEAR_DUPLICATE/OVERLAPPING/INSUFFICIENT_INFORMATION and produces `DuplicateStoryReport.md`; never auto-consolidates. |
| PRD Generation Agent (JIRA) | 05-agents/prd-generation-agent(JIRA).md (170 lines) | 04-prompts/prd-generation-prompt(JIRA).md (203) | Drafts a PRD from the approved story inventory and duplicate report, preserving JIRA issue keys; flags ambiguities/conflicts/assumptions/risks for human review before save. |
| Functional Test Case Orchestrator | MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md | 04-prompts/master-prompt.md | Orchestrates the end-to-end Functional Test Case workflow. Also runnable as a one-click button from the 08-ui dashboard. |
| Test Plan Orchestrator | MasterQAOrchestrator/TestPlanOrchestrator.md | 04-prompts/testplan-gen-prompt.md | Orchestrates the end-to-end Test Plan workflow (Requirement Analysis → Gap Analysis → Test Plan). Also runnable as a one-click button from the 08-ui dashboard; generates both `TestPlan.md` and `TestPlan.pdf`. |
| RTM Orchestrator | MasterQAOrchestrator/RTMOrchestrator.md | 04-prompts/rtm-prompt.md | Generates the RTM once, then only adds/updates/removes the specific rows affected by requirement or test-case changes. |
| Automation Orchestrator (router) | MasterQAOrchestrator/AutomationOrchestrator.md | — | Determines web vs. mobile and which automation tool, then hands off entirely to the matching tool orchestrator below. |
| Playwright Automation Orchestrator | MasterQAOrchestrator/PlaywrightAutomationOrchestrator.md | 04-prompts/playwright-script-prompt.md | Drives Playwright automation generation (TypeScript/JavaScript/Java). |
| Cypress Automation Orchestrator | MasterQAOrchestrator/CypressAutomationOrchestrator.md | — | Drives Cypress (plain JavaScript) automation generation. |
| Selenium Automation Orchestrator | MasterQAOrchestrator/SeleniumAutomationOrchestrator.md | 04-prompts/selenium-script-prompt.md | Drives Selenium (Java + TestNG, POM) automation generation. |
| WebdriverIO Automation Orchestrator | MasterQAOrchestrator/WebdriverIOAutomationOrchestrator.md | — | Drives WebdriverIO (JavaScript) automation generation. |
| Appium Automation Orchestrator | MasterQAOrchestrator/AppiumAutomationOrchestrator.md | — | Drives Appium (Java) mobile automation generation. |
| Requirement Analysis Agent | 05-agents/requirement-analysis-agent.md (137 lines) | 04-prompts/requirement-analysis-prompt.md | Analyzes and correlates requirements across all input sources (BRD, PRD, Jira, Figma), first reviewing Project Context documents (`01-docs/ProjectContext/`) as supporting background before interpreting formal requirement sources. |
| Gap Analysis Agent | 05-agents/gap-analysis-agent.md (239 lines) | 04-prompts/gap-analysis-prompt.md | Identifies missing requirements, business rules, validations, and coverage gaps. |
| Change Impact Analysis Agent | 05-agents/change-impact-agent.md (432 lines) | 04-prompts/change-impact-prompt.md | Compares latest requirements against the previous baseline and drives incremental updates to Test Cases, Test Plan, and RTM. |
| RTM Agent | 05-agents/rtm-agent.md (301 lines) | 04-prompts/rtm-prompt.md | Builds end-to-end requirement-to-defect traceability on first run; incremental row-level updates thereafter. |
| Functional Test Case Agent | 05-agents/functional-agent.md (156 lines) | 04-prompts/functional-testcase-prompt.md | Generates comprehensive functional test cases including positive, negative, boundary, and exception scenarios. Also accepts a `test_case_generation_mode` (`FUNCTIONAL_ONLY` or `FUNCTIONAL_AND_GENERIC`) from the orchestrator; in the latter mode it also evaluates `010-GenericTestCases/GenericTestCases.xlsx` (read-only) against project evidence, classifies each entry `APPLICABLE`/`NOT_APPLICABLE`/`INSUFFICIENT_INFORMATION`, and includes only `APPLICABLE` cases, tagged with their source worksheet/ID/reference. |
| UI Test Case Agent | 05-agents/ui-agent.md (172 lines) | 04-prompts/ui-testcase-prompt.md | UI and user-journey validation, form validations, and responsive behavior from Figma designs. |
| Framework Detection Agent | 05-agents/framework-detection-agent.md (344 lines) | — | Reverse-engineers an existing automation project's conventions for "add to existing framework" mode. |
| Playwright Agents | playwright-typescript-agent.md (239), playwright-javascript-agent.md (209), playwright-java-agent.md (220) | 04-prompts/playwright-script-prompt.md (218) | Generate Playwright Page Objects, feature files/step definitions or test classes, and spec/test files per stack. |
| Cypress Agent | 05-agents/cypress-javascript-agent.md (207 lines) | — | Generates Cypress (plain JavaScript, ES Modules) selectors, constants, static-class Page Objects, fixtures, and spec files. |
| Selenium Agent | 05-agents/selenium-agent.md (214 lines) | 04-prompts/selenium-script-prompt.md (71) | Generates Selenium Page Objects (`@FindBy`/`PageFactory.initElements()`) and TestNG test classes. |
| WebdriverIO Agent | 05-agents/webdriverio-javascript-agent.md (219 lines) | — | Generates WebdriverIO (JavaScript) Page Objects and spec files. |
| Appium Agent | 05-agents/appium-java-agent.md (242 lines) | — | Generates Appium (Java) mobile Page Objects and TestNG test classes for Android/iOS. |
| Automation Framework Agents (6) | automation-framework-typescript-agent.md (215), -javascript-agent.md (183), -java-agent.md (141), -cypress-javascript-agent.md (151), -selenium-java-agent.md (145), -webdriverio-javascript-agent.md (199), -appium-java-agent.md (189) | — | Scaffold a brand-new automation framework per stack, using `02-templates/AutomationFramework.md` / `AppiumFramework.md` / `WebdriverIOFramework.md` and the matching stack standard. |
| API Agent | 05-agents/api-agent.md — **empty placeholder** | 04-prompts/api-prompt.md — minimal (1 line) placeholder | Reserved for API validation, request/response behavior, status codes, integration testing. |
| Security Agent | 05-agents/security-agent.md — **empty placeholder** | 04-prompts/security-prompt.md — minimal (1 line) placeholder | Reserved for security-focused test scenarios, vulnerability testing, compliance validation. |
| Accessibility Agent | 05-agents/accessibility-agent.md — **empty placeholder** | 04-prompts/accessibility-prompt.md — **empty placeholder** | Reserved for WCAG compliance and accessibility test scenarios (03-standards/accessibility-standards.md, 552 lines, is fully populated). |
| Test Plan Agent | 05-agents/testplan-gen-agent.md (149 lines, populated) | 04-prompts/testplan-gen-prompt.md (528 lines, populated) | Transforms `RequirementAnalysis.md` + the Requirement Gap Report into a Test Strategy and Test Plan (scope, approach, entry/exit criteria, smoke/regression suite composition, risks/assumptions), per `testplan-standards.md`/`testing-standards.md`/`naming-conventions-standards.md` and the `TestPlan.md` template. Explicitly out of scope: individual test cases, RTM, and automation scripts. Invoked by `MasterQAOrchestrator/TestPlanOrchestrator.md` rather than run standalone. |
| Code Review Agent | 05-agents/code-review-agent.md — **empty placeholder** | 04-prompts/code-review-prompt.md — **empty placeholder** | Reserved for automated code review against 03-standards/code-review-standards.md (351 lines, populated). |
| Execution Agent | 05-agents/execution-agent.md — **empty placeholder** | 04-prompts/execution-prompt.md — **empty placeholder** | Reserved for test execution, results tracking, and defect reporting. |
| Jira / TestLink / TestRail Sync Agents | jira-sync-agent.md, testlink-sync-agent.md, testrail-sync-agent.md — all **empty placeholders** | jira-sync-prompt.md, testlink-sync-prompt.md, testrail-sync-prompt.md — all **empty placeholders** | Reserved for syncing generated artifacts to external test-management tools. |

## .claude/skills/ — QA Framework Slash-Command Skill
A new Claude Code skill, `.claude/skills/qa-framework/SKILL.md`, gives users a single entry point (`/qa-framework`) for choosing what to run, instead of naming an orchestrator/agent file directly:

- **Requirement Source Selection (asked first, every run)** — before the capability checklist, the skill always asks where this run's requirements come from, as a 9-item numbered list: 1 PRD, 2 BRD, 3 Figma Flow, 4 **Create a Product Requirement Document from the Jira Stories**, 5 **Create a PRD using Jira stories plus related Figma flow**, 6 Requirements in Screenshots, 7 API Requirements and API Documentation, 8 Project Architecture, and 9 Other location (e.g. Release Notes). Each option maps to a real `01-docs/` subfolder or a live source, and multiple sources can be selected together (e.g. `1, 6`). Selecting either PRD-from-Jira option (4 or 5) first confirms the Atlassian MCP connection is live (and, for option 5, the Figma MCP connection), then lists every accessible Jira project (name + key) for the user to pick from — it never asks an open-ended "which project?" question first. The PRD-from-Jira options additionally ask which file format to generate the PRD in (**PDF, .xlsx, .doc, or .md** — PDF by default) and always saves the result to `07-outputs/GeneratedPRD(JIRA)/`, never into `01-docs/PRD/`; that generated file then becomes the run's requirement source, as if PRD had been selected directly. If the user's invocation already states the source, this question is skipped.
- **Capability checklist** — when invoked with no activity stated, it presents all 23 capabilities (Requirement Analysis, Gap Analysis, Change Impact Analysis, Test Plan, Functional Test Cases, RTM, five automation-stack generators, Security/API/Accessibility/Performance/UI Test Case generation, three evaluators, three publish-to-Jira/TestLink/TestRail actions, plus a "None" cancel option) as one numbered list, and the user replies with the numbers/names they want (e.g. `4, 5, 6`).
- **Test Case Generation Mode** — selecting Functional Test Cases (5) first asks Functional-Only vs. Functional + Generic (from `010-GenericTestCases/GenericTestCases.xlsx`), plus whether to also generate UI Test Cases (16) in the same run. UI Test Cases can also be selected on its own from the main checklist; it only requires Requirement Analysis and Gap Analysis to exist, not Functional Test Cases.
- **Direct routing** — if the invocation already names the activity (e.g. `/qa-framework generate a test plan for the login PRD`), it skips the checklist and goes straight to execution.
- **Capability → Orchestrator/Agent routing table** — maps each of the 23 items to its exact entry point (an `MasterQAOrchestrator/*.md` file where one exists, or the raw agent + prompt pair where it doesn't yet), and states plainly which capabilities have no orchestrator or evaluator wired up yet — including UI Test Cases (16), which is fully wired to `05-agents/ui-agent.md` + `04-prompts/ui-testcase-prompt.md` + `03-standards/ui-standards.md`, and Performance Test Cases (15), which has no agent, prompt, or standards file at all yet.
- **Dependency resolution** — before running anything, it checks `07-outputs/` for reusable prior artifacts, and automatically inserts required prerequisite steps (e.g. selecting Functional Test Cases always runs Requirement Analysis → Gap Analysis → Change Impact Analysis → Test Plan first if their outputs don't already exist), showing the resulting execution plan before starting when multiple capabilities are selected.
- **Guardrails** — never invents business functionality, always reports ambiguity instead of guessing, preserves Requirement/Test Case IDs across phases, and never overwrites existing `07-outputs/` artifacts without explicit confirmation.

This is the recommended way to interact with the framework going forward — it removes the need to know which orchestrator or agent file corresponds to a given request.

**Execution Trace Artifact** — every `/qa-framework` run (any of capabilities 1–22, not just test-case generation) now always finishes by publishing an HTML Artifact tracing what happened: one **Loaded → Consumed → Executed → Written** stage block per capability that ran (exact template/standard, input/prior-output, agent+prompt(+evaluator), and output file paths — pulled from what was actually read/run/saved, never fabricated), plus real narrative detail, full evaluator dimension-score tables where an evaluator ran, any conflicts/ambiguities/gaps flagged during the run, and a final table of every file written. A single-capability run still gets this artifact, just with one stage block instead of several.

## 00-framework — Orchestration Contract Layer
`00-framework/` is the framework's self-documentation layer, separate from the requirement/test artifacts it produces in `07-outputs/`:

| File | Status | Purpose |
|---|---|---|
| `FrameworkManifest.md` | Populated (6.8 KB) | Authoritative definition of the framework's architecture, objectives, principles, execution lifecycle, and folder responsibilities. |
| `ComponentCatalog.md` | Populated (10.8 KB) | Inventory of every component currently implemented — orchestrators, agents, prompts, standards, templates, evaluators, integrations, outputs, LLM components. |
| `DependencyMap.md` | Populated (4.9 KB) | Documents Orchestrator → Agent → Prompt → Standard → Template → Output → Evaluator dependency chains. |
| `WorkflowManifest.md` | **Empty placeholder** | Intended to list every root orchestrator workflow and its trigger conditions; not yet written. |
| `README.md` | Populated | Explains the reading order and relationship between the three manifest files above. Note: this file's own "Status" note (calling all three manifests placeholders) is stale relative to `FrameworkManifest.md`/`ComponentCatalog.md`/`DependencyMap.md`, which are in fact populated; only `WorkflowManifest.md` remains empty. |

## 09-evaluators — Self-Evaluation / Quality-Gate Layer
`09-evaluators/` is a new, fully populated subsystem that independently scores and critiques artifacts already produced elsewhere in the framework — it never generates new test cases, plans, or code itself.

| Evaluator Agent | Lines | Checks |
|---|---|---|
| `eval_agents/testcase-evaluator-agent.md` | 139 | Scores coverage, correctness, traceability, and standards compliance of generated Test Cases; detects missing, duplicate, orphan, or ambiguous cases and naming-convention violations. Produces a scored evaluation report with an approval recommendation. |
| `eval_agents/testplan-evaluator-agent.md` | 137 | Scores generated Test Plans against `03-standards/testplan-standards.md` and `02-templates/TestPlan.md`. |
| `eval_agents/rtm-evaluator-agent.md` | 141 | Scores the RTM for completeness and correctness of requirement-to-test-case-to-defect traceability. |
| `eval_agents/playwright-review-agent.md` | 141 | Reviews generated Playwright automation code against `03-standards/playwright-*-standards.md`. |
| `eval_agents/selenium-review-agent.md` | 141 | Reviews generated Selenium automation code against `03-standards/selenium-standards.md`. |

Supporting folders: `eval_prompts/` (one populated prompt per evaluator agent, ~96–115 lines each), `eval_standards/evaluation-standards.md` (487 lines — the shared scoring rubric and approval criteria), `eval_outputs/` (generated reports, each as both `.md` and `.xlsx`: `TestCoverageReport`, `MissingScenarios`, `TestPlanEvaluation`, `RTMEvaluation`, `PlaywrightReview`, `SeleniumReview`), and an empty `eval_templates/` folder.

## LLMs/
A new top-level folder with subfolders for `claude/`, `gemini/`, `github-copilot/`, `openai/`, and `other llms/`, intended to hold LLM-specific setup/config notes. All five subfolders are currently empty — no content has been added yet.

## Folder Structure

```text
QA-AI-Framework/
├── .claude/skills/
│   └── qa-framework/SKILL.md
│
├── 00-framework/
│   ├── FrameworkManifest.md
│   ├── ComponentCatalog.md
│   ├── DependencyMap.md
│   ├── WorkflowManifest.md
│   └── README.md
│
├── MasterQAOrchestrator/
│   ├── JiraToPRDOrchestrator.md
│   ├── FunctionalTestCaseOrchestrator.md
│   ├── TestPlanOrchestrator.md
│   ├── RTMOrchestrator.md
│   ├── AutomationOrchestrator.md
│   ├── PlaywrightAutomationOrchestrator.md
│   ├── CypressAutomationOrchestrator.md
│   ├── SeleniumAutomationOrchestrator.md
│   ├── WebdriverIOAutomationOrchestrator.md
│   └── AppiumAutomationOrchestrator.md
│
├── 01-docs/
│   ├── BRD/
│   ├── PRD/
│   ├── Figma/
│   ├── API Docs/
│   ├── Screenshots/
│   ├── PDFs/
│   ├── Architecture/
│   ├── ReleaseNotes/
│   ├── ProjectContext/
│   └── Other Requirement Docs/
│
├── 010-GenericTestCases/
│   └── GenericTestCases.xlsx
│
├── 02-templates/                      # 18 files
│   ├── TestPlan.md
│   ├── TestCase.md
│   ├── SecurityTestcase.md
│   ├── AccessibilityTestcase.md
│   ├── RequirementAnalysis.md
│   ├── GapAnalysis.md
│   ├── ImpactAnalysis.md
│   ├── RTM.md
│   ├── DefectReport.md
│   ├── AutomationScript.md
│   ├── AutomationFramework.md
│   ├── AppiumFramework.md
│   ├── WebdriverIOFramework.md
│   ├── PageObject.md
│   ├── TestData.md
│   ├── FinalSummary.md
│   ├── _README_Template.md
│   └── JiraPRDTemplate/PRD_Template.docx
│
├── 03-standards/                      # 21 files — see Coding and Testing Standards
│
├── 04-prompts/                        # 22 files — see Available Agents
│
├── 05-agents/                         # 32 files — see Available Agents
│
├── 06-integrations/
│   ├── README.md
│   ├── adapters/
│   │   ├── jira-adapter.md
│   │   ├── github-adapter.md
│   │   ├── testlink-adapter.md
│   │   └── testlink_export.py
│   ├── configs/
│   │   └── mcp-registry.json
│   └── providers/
│       ├── jira.md
│       ├── github.md
│       └── testlink.md
│
├── 07-outputs/
│   ├── RequirementAnalysis/
│   ├── TestPlan/
│   ├── TestCases/
│   ├── GapAnalysis/
│   ├── ChangeImpact/ / ImpactAnalysis/
│   ├── RTM/ / RequirementTraceabilityMatrix/
│   ├── GeneratedPRD(JIRA)/
│   ├── Reports/
│   ├── Automation/
│   │   ├── Framework/
│   │   ├── PageObjects/
│   │   ├── Scripts/
│   │   └── docs/
│   └── _archive/                      # Dated per-framework snapshots
│
├── 08-ui/                              # Live orchestrator dashboard (see Web UI section)
│
├── 09-evaluators/
│   ├── eval_agents/
│   ├── eval_prompts/
│   ├── eval_standards/
│   ├── eval_outputs/
│   └── eval_templates/
│
├── LLMs/
│   ├── claude/ / gemini/ / github-copilot/ / openai/ / other llms/
│
├── CLAUDE.md
├── README.md
├── UI-GETTING-STARTED.md
└── UI-SUMMARY.md
```

## Input Documents
The framework consumes the following types of input documents:

| Input Type | Folder |
|---|---|
| Business Requirements Documents | 01-docs/BRD |
| Product Requirements Documents | 01-docs/PRD |
| Figma design references | 01-docs/Figma |
| API documentation | 01-docs/API Docs |
| Screenshots and visual references | 01-docs/Screenshots |
| Supporting PDF documents | 01-docs/PDFs |
| Architecture and system design documents | 01-docs/Architecture |
| Release notes and change logs | 01-docs/ReleaseNotes |
| Project/system context documents | 01-docs/ProjectContext |
| Other requirement documents | 01-docs/Other Requirement Docs |
| Generic Test Case library (reusable, read-only) | 010-GenericTestCases |

## Integrations
`06-integrations/` was restructured from a single flat `mcp-integration/` folder into adapters, providers, and configs:

| Integration | Adapter | Provider Doc | Purpose |
|---|---|---|---|
| Jira | 06-integrations/adapters/jira-adapter.md | 06-integrations/providers/jira.md | Read epics/stories/acceptance criteria; map to test cases and traceability. Also usable live via the Atlassian MCP connector during Requirement Analysis (see below). |
| GitHub | 06-integrations/adapters/github-adapter.md | 06-integrations/providers/github.md | Track test cases and automation code in GitHub repositories. |
| TestLink | 06-integrations/adapters/testlink-adapter.md, adapters/testlink_export.py | 06-integrations/providers/testlink.md | Manage test plans/cases with TestLink; export helper script. |

`06-integrations/configs/mcp-registry.json` holds the MCP registry configuration. The old `mcp-integration/` subfolder (with files like `jira-mcp.md`, `figma-mcp.md`, `appium.mcp.md`, `testrail-mcp.md`) no longer exists — Figma, Appium, and TestRail no longer have dedicated adapter/provider files, though a live TestLink MCP server is configured directly in the repo root's `.mcp.json` (Docker-based stdio server).

### Live Jira via Atlassian MCP
When an Atlassian MCP connector is authorized for the session, `05-agents/requirement-analysis-agent.md` pulls Epics, Stories, Acceptance Criteria, linked issues, and relevant comments directly from Jira as part of Requirement Analysis, instead of relying solely on exported/local copies:
- Jira is treated as the source of truth for User Stories it maintains — the agent does not create or keep duplicate local copies.
- Every requirement sourced from Jira retains its Jira issue key for traceability (e.g. `User login requirement — Source: JIRA: AUTH-123`).
- Conflicts between Jira and other sources (PRD, Figma, etc.) are reported, not silently resolved.

For detailed integration setup instructions, see [06-integrations/README.md](06-integrations/README.md).

## Output Artifacts

| Artifact Type | Output Folder |
|---|---|
| Requirement analysis reports | 07-outputs/RequirementAnalysis |
| Test plans | 07-outputs/TestPlan |
| Test cases (functional, UI, API, security, accessibility) | 07-outputs/TestCases |
| Generic Test Case library (source, read-only) | 010-GenericTestCases |
| Gap analysis reports | 07-outputs/GapAnalysis |
| Change Impact Analysis reports (incremental runs) | 07-outputs/ChangeImpact, 07-outputs/ImpactAnalysis |
| Requirement Traceability Matrix (created once, updated incrementally) | 07-outputs/RTM, 07-outputs/RequirementTraceabilityMatrix |
| Generated PRD from Jira Stories (PDF/.xlsx/.doc/.md, PDF by default) | 07-outputs/GeneratedPRD(JIRA) |
| Test reports and metrics | 07-outputs/Reports |
| Automation framework, Page Objects, scripts, docs | 07-outputs/Automation (Framework/, PageObjects/, Scripts/, docs/) |
| Archived automation runs (dated, per framework) | 07-outputs/_archive |
| Evaluation reports (test case coverage, test plan, RTM, Playwright/Selenium review) | 09-evaluators/eval_outputs |

## Reusable Templates
The framework includes ready-to-use templates for all testing artifacts:

| Template | File | Purpose |
|---|---|---|
| Test Plan | 02-templates/TestPlan.md | Structured test planning and strategy documentation. |
| Test Case | 02-templates/TestCase.md | Standardized test case format with step-by-step execution. |
| Functional Test Cases | 02-templates/TestCase.md | Template for functional testing scenarios. |
| UI Test Cases | 02-templates/TestCase.md | UI-specific test case format with navigation and validation steps. |
| API Test Cases | 02-templates/TestCase.md | API testing template with request/response validation. |
| Security Test Cases | 02-templates/SecurityTestcase.md | Security-focused test case template for vulnerability and compliance testing. |
| Accessibility Test Cases | 02-templates/AccessibilityTestcase.md | WCAG compliance and accessibility testing scenarios. |
| Gap Analysis | 02-templates/GapAnalysis.md | Requirement gap identification and reporting template. |
| Impact Analysis | 02-templates/ImpactAnalysis.md | Change Impact Analysis template covering requirement changes, downstream dependency impact, regeneration decision matrix, and risk assessment. |
| Requirement Analysis | 02-templates/RequirementAnalysis.md | Comprehensive requirement analysis and correlation template. |
| RTM | 02-templates/RTM.md | Requirement Traceability Matrix for coverage tracking. |
| Defect Report | 02-templates/DefectReport.md | Bug/defect documentation and tracking template. |
| Automation Script | 02-templates/AutomationScript.md | Template for automation test script design and structure. |
| Automation Framework | 02-templates/AutomationFramework.md | Automation framework setup and best practices template (web: Playwright/Cypress/Selenium/WebdriverIO). |
| Appium Framework | 02-templates/AppiumFramework.md | Mobile automation framework scaffold template. |
| WebdriverIO Framework | 02-templates/WebdriverIOFramework.md | WebdriverIO framework scaffold template. |
| Page Object | 02-templates/PageObject.md | Template for Page Object Model classes used by automation scripts. |
| Test Data | 02-templates/TestData.md | Template for structuring reusable test data sets across test cases. |
| Final Summary | 02-templates/FinalSummary.md | Test execution summary and project completion report template. |
| README Template | 02-templates/_README_Template.md | Documentation standard for project readmes. |

## Commands and Workflow
Typical workflow:

```bash
cd CC-QA-AI-Framework
# List available input sources
ls 01-docs/BRD 01-docs/PRD 01-docs/Figma 01-docs/"API Docs" 01-docs/Screenshots 01-docs/"Other Requirement Docs"

# View available agents
ls 05-agents/

# View available prompts
ls 04-prompts/

# View evaluators
ls 09-evaluators/eval_agents/

# Check output artifacts
ls 07-outputs/TestCases 07-outputs/TestPlan 07-outputs/Reports 07-outputs/Automation 07-outputs/GapAnalysis
```

Complete QA workflow:

1. **Requirement Analysis** → Use 05-agents/requirement-analysis-agent.md to correlate inputs
2. **Gap Identification** → Use 05-agents/gap-analysis-agent.md + 04-prompts/gap-analysis-prompt.md to identify missing coverage
3. **Test Planning** → Use MasterQAOrchestrator/TestPlanOrchestrator.md to create strategy and test plan
4. **Test Case Generation** → Use available agents (05-agents/functional-agent.md, 05-agents/ui-agent.md); API/Security/Accessibility agents are placeholders today
5. **Automation Generation** → Use MasterQAOrchestrator/AutomationOrchestrator.md to route to Playwright/Cypress/Selenium/WebdriverIO/Appium
6. **Traceability** → Generate RTM using MasterQAOrchestrator/RTMOrchestrator.md to map test cases to requirements
7. **Self-Evaluation** → Run the relevant agent in 09-evaluators/eval_agents/ against already-generated artifacts
8. **Test Execution** → 04-prompts/execution-prompt.md and 05-agents/execution-agent.md are reserved (currently empty placeholders) for driving testing and reporting results

Example export workflow for generated test case tables:

```bash
source .venv/bin/activate
python3 - <<'PY'
# Example for local export tasks
# Transform generated markdown tables into Excel or other formats
import pandas as pd

# Read markdown test cases and convert to Excel
df = pd.read_csv('outputs/TestCases/Functional_Test_Cases.md', sep='|')
df.to_excel('outputs/TestCases/Functional_Test_Cases.xlsx', index=False)
print('Export completed successfully')
PY
```

## Coding and Testing Standards
The repository includes comprehensive standards guides:

| Standard | File | Focus |
|---|---|---|
| Testing Standards | 03-standards/testing-standards.md | Traceable and reusable test cases, positive/negative/alternate/exception coverage. |
| Test Case Standards | 03-standards/testcase-standards.md | Test case design, structure, and quality criteria. |
| Test Plan Standards | 03-standards/testplan-standards.md | Test strategy and planning document structure. |
| RTM Standards | 03-standards/rtm-standards.md | Traceability matrix structure and update rules. |
| Code Review Standards | 03-standards/code-review-standards.md | Code review checklist and criteria for generated automation code. |
| UI Standards | 03-standards/ui-standards.md | UI testing, form validation, responsive behavior. |
| Automation Standards | 03-standards/automation-standards.md | Cross-cutting best practices for test automation, frameworks, and scripting. |
| Locator Standards | 03-standards/locator-standards.md | Element locator strategy conventions used by automation agents. |
| Selenium Standards | 03-standards/selenium-standards.md | Selenium/Java Page Object Model and TestNG conventions. |
| Appium (Java) Standards | 03-standards/appium-java-standards.md | Mobile automation (Android/iOS) conventions for Appium + Java. |
| Cypress (JavaScript) Standards | 03-standards/cypress-javascript-standards.md | Cypress plain-JavaScript/ES-Modules conventions. |
| Playwright (Java) Standards | 03-standards/playwright-java-standards.md | Playwright Java/TestNG conventions. |
| Playwright (JavaScript) Standards | 03-standards/playwright-javascript-standards.md | Playwright JavaScript conventions. |
| Playwright (TypeScript) Standards | 03-standards/playwright-typescript-standards.md | Playwright TypeScript/Cucumber conventions. |
| WebdriverIO (JavaScript) Standards | 03-standards/webdriverio-javascript-standards.md | WebdriverIO JavaScript conventions. |
| Accessibility Standards | 03-standards/accessibility-standards.md | WCAG compliance and accessibility testing guidelines. |
| Naming Conventions | 03-standards/naming-conventions-standards.md | Standardized naming for test cases, requirement IDs, files, folders, and artifacts (e.g., `TC_<Module>_<Feature>_<Sequence>`). |
| Readme Standards | 03-standards/readme-standards.md | Documentation and readme formatting guidelines. |
| API Standards | 03-standards/api-standards.md | **Empty placeholder.** |
| Performance Standards | 03-standards/performance-standards.md | **Empty placeholder.** |
| Execution Standards | 03-standards/execution-standards.md | **Empty placeholder.** |
| Reporting Standards | 03-standards/reporting-standards.md | **Empty placeholder.** |

Key emphasis areas:
- Traceable and reusable test cases
- Positive, negative, alternate, and exception coverage
- Clear priority and severity classification
- Boundary and edge-case testing
- Automation readiness across five automation stacks
- Accessibility compliance
- Security test coverage
- Independent self-evaluation before sign-off (09-evaluators/)

## Best Practices
- Keep prompts (04-prompts/) focused and reusable across multiple projects.
- Store input documents in the correct 01-docs subfolder (BRD, PRD, Figma, API Docs, Screenshots, PDFs, Architecture, ReleaseNotes, ProjectContext, Other Requirement Docs).
- Treat `010-GenericTestCases/GenericTestCases.xlsx` as a read-only reference library — never edit it directly; it is only consumed when `FUNCTIONAL_AND_GENERIC` mode is selected during Functional Test Case generation.
- Organize generated outputs in the appropriate 07-outputs folders by artifact type.
- Use 02-templates templates for consistent output formatting across test artifacts.
- Correlate requirements across multiple sources before generating test cases to minimize duplication.
- Document assumptions and clearly identify gaps when requirements are incomplete.
- Configure external integrations (06-integrations/) early for seamless tool connectivity.
- Follow 03-standards guidelines (general and stack-specific) for naming conventions and test/automation design consistency.
- Review and customize agent workflows (05-agents/) for your specific project context.
- Run the relevant evaluator in 09-evaluators/ before treating generated artifacts as final.

## Extending the Framework
To extend the framework:
1. Create a new prompt file in 04-prompts/ with clear instructions and examples.
2. Create a corresponding agent file in 05-agents/ to execute the prompt logic.
3. Add supporting templates in 02-templates/ for consistent output format.
4. Define the expected output location under 07-outputs/ organized by artifact type.
5. Add adapter/provider files under 06-integrations/adapters and 06-integrations/providers if connecting an external tool, and register it in 06-integrations/configs/mcp-registry.json.
6. Update relevant standards in 03-standards/ if introducing new testing methodologies or a new automation stack.
7. Add a matching evaluator agent/prompt under 09-evaluators/ if the new artifact type should be independently reviewed.
8. Update 00-framework/ComponentCatalog.md and DependencyMap.md to register the new component and its dependencies.
9. Update this README if the workflow or folder structure changes.

## Troubleshooting
- If a folder is missing, create it manually or re-run the repository scaffold steps.
- If generated outputs are not visible, confirm the output path and file naming.
- If Python dependencies are missing, activate the virtual environment and install them as needed.
- If the TestLink MCP server in `.mcp.json` fails to start, confirm Docker is running and the `TESTLINK_URL` / `TESTLINK_API_KEY` values are current.

## Known Limitations
- API, Security, Accessibility, Code Review, Execution, and all three sync agents (Jira/TestLink/TestRail) in 05-agents/ are currently empty placeholder files, even though several of their prompts/standards are populated. (Test Plan generation is no longer on this list — `05-agents/testplan-gen-agent.md` is now populated.)
- 04-prompts/accessibility-prompt.md, execution-prompt.md, code-review-prompt.md, README_Prompt.md, and the three sync prompts are empty placeholders; api-prompt.md and security-prompt.md are minimal 1-line placeholders
- 03-standards/api-standards.md, performance-standards.md, execution-standards.md, and reporting-standards.md remain empty placeholders
- 00-framework/WorkflowManifest.md is an empty placeholder (FrameworkManifest.md, ComponentCatalog.md, and DependencyMap.md are populated)
- LLMs/ subfolders (claude, gemini, github-copilot, openai, other llms) exist but currently have no content
- 09-evaluators/eval_templates/ exists but is currently empty
- No `05-agents/master-agent.md` file currently exists; orchestration is instead handled per-workflow via `MasterQAOrchestrator/*Orchestrator.md` files — standalone Gap Analysis, UI, API, Security, and Accessibility request types are not yet wired in as separate orchestrator files
- Change Detection in the orchestrators currently relies on presence/absence of prior output files and requirement-document changes under 01-docs; there is no automated diff/versioning mechanism, so change detection depends on the executing agent correctly comparing document state
- The RTM Orchestrator's incremental, row-level update behavior depends on the rtm-agent correctly interpreting ImpactAnalysis.md's impact sections; there is no automated row-diffing mechanism enforcing this at the tooling level
- Figma analysis has no dedicated adapter/provider file since the 06-integrations restructure (the old figma-mcp.md is gone); UI test case generation from Figma still relies on manual input under 01-docs/Figma
- MCP integrations require proper authentication setup before use (the TestLink MCP server in `.mcp.json` needs valid `TESTLINK_URL`/`TESTLINK_API_KEY`)
- No integrated test execution engine; execution tracking is currently manual or external tool-dependent
- 07-outputs is not version-controlled by the framework itself; generated artifacts can be lost if removed outside of a session with no backing history (though 07-outputs/_archive retains dated snapshots of past automation runs)

## Future Enhancements
Potential improvements include:
- **Fill Remaining Placeholders** → Populate the API, Security, Accessibility, Code Review, Execution, and sync agents/prompts listed under Known Limitations
- **WorkflowManifest.md** → Complete the empty 00-framework/WorkflowManifest.md with orchestrator trigger conditions
- **Extended MCP Integrations** → Additional integrations with Figma, Azure DevOps, and other test management platforms beyond the now-live Atlassian (Jira) MCP
- **Figma Automation** → Automated Figma design extraction and design-to-test-case mapping
- **Performance Testing** → Dedicated performance testing agent and stress testing scenarios (03-standards/performance-standards.md is currently empty)
- **Mobile-Specific Testing** → Further mobile testing enhancements building on the new Appium agent/orchestrator
- **CI/CD Integration** → GitHub Actions / GitLab CI workflows for automated test generation and result tracking
- **Dashboard & Reporting** → HTML dashboard for test metrics, coverage trends, and RTM visualization
- **Test Execution Engine** → Integrated test execution framework with cross-platform support
- **Prompt versioning** → Version control and rollback for agent prompts and templates
- **LLMs/ content** → Populate the currently-empty claude/gemini/github-copilot/openai/other-llms subfolders

## Contributing Guidelines
Contributions are welcome. Suggested approach:
1. Keep changes focused and well documented.
2. Update the relevant prompt, standards, or output documentation.
3. Preserve the existing folder structure and naming conventions.
4. Add or update this README when significant workflow changes are introduced.

## License
No license file was found in the repository. Please add a license before distributing or reusing the framework publicly.

## TODO
- Add a formal license file.
- Add example workflows or automation scripts.
- Add more detailed contribution and release guidance.
</content>
