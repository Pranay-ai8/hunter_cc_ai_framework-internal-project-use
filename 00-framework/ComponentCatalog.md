## Components Catalog

| Component Name                   | Type                | Folder               | Purpose                        | Inputs                                              | Loads                           | Executes                   | Produces               | Next Component                 |
| -------------------------------- | ------------------- | -------------------- | ------------------------------ | --------------------------------------------------- | ------------------------------- | -------------------------- | ---------------------- | ------------------------------ |
| Master QA Orchestrator           | Master Orchestrator | MasterQAOrchestrator | Entry point of the framework   | User Request                                        | CLAUDE.md, Framework Manifest   | Specialized Orchestrator   | Execution Flow         | Specialized Orchestrator       |
| RequirementAnalysisOrchestrator  | Orchestrator        | MasterQAOrchestrator | Requirement Analysis Workflow  | PRD, BRD, User Stories                              | Requirement Template, Standards | Requirement Analysis Agent | RequirementAnalysis.md | GapAnalysisOrchestrator        |
| GapAnalysisOrchestrator          | Orchestrator        | MasterQAOrchestrator | Gap Analysis Workflow          | RequirementAnalysis.md                              | Gap Template, Standards         | Gap Analysis Agent         | GapAnalysis.md         | TestPlanOrchestrator           |
| TestPlanOrchestrator             | Orchestrator        | MasterQAOrchestrator | Generate Test Plan             | RequirementAnalysis.md, GapAnalysis.md              | Test Plan Template, Standards   | Test Plan Agent            | TestPlan.md            | FunctionalTestCaseOrchestrator |
| FunctionalTestCaseOrchestrator   | Orchestrator        | MasterQAOrchestrator | Generate Functional Test Cases | RequirementAnalysis.md, GapAnalysis.md, TestPlan.md | Test Case Template, Standards   | Functional Agent           | FunctionalTestCases.md | Test Case Evaluator            |
| RTMOrchestrator                  | Orchestrator        | MasterQAOrchestrator | Generate RTM                   | RequirementAnalysis.md, FunctionalTestCases.md      | RTM Template, Standards         | RTM Agent                  | RTM.md                 | RTM Evaluator                  |
| PlaywrightAutomationOrchestrator | Orchestrator        | MasterQAOrchestrator | Generate Playwright Automation | FunctionalTestCases.md                              | Automation Template, Standards  | Playwright Agent           | Playwright Scripts     | Playwright Evaluator           |

## Agents catalog

| Agent                      | Folder    | Purpose                                  | Inputs                                              | Loads                           | Produces                  |
| -------------------------- | --------- | ---------------------------------------- | --------------------------------------------------- | ------------------------------- | ------------------------- |
| Requirement Analysis Agent | 05-agents | Analyze Requirements                     | PRD, BRD                                            | Requirement Prompt, Standards   | RequirementAnalysis.md    |
| Gap Analysis Agent         | 05-agents | Identify Requirement Gaps                | RequirementAnalysis.md                              | Gap Prompt, Standards           | GapAnalysis.md            |
| Test Plan Agent            | 05-agents | Generate Test Plan                       | RequirementAnalysis.md, GapAnalysis.md              | Test Plan Prompt, Standards     | TestPlan.md               |
| Functional Agent           | 05-agents | Generate Functional Test Cases           | RequirementAnalysis.md, GapAnalysis.md, TestPlan.md | Functional Prompt, Standards    | FunctionalTestCases.md, GenericApplicabilityReview.md (FUNCTIONAL_AND_GENERIC mode only) |
| API Agent                  | 05-agents | Generate API Test Cases                  | API Docs                                            | API Prompt, Standards           | APITestCases.md           |
| UI Agent                   | 05-agents | Generate UI Test Cases                   | Figma, Screenshots                                  | UI Prompt, Standards            | UITestCases.md            |
| Accessibility Agent        | 05-agents | Generate Accessibility Test Cases        | FunctionalTestCases.md                              | Accessibility Prompt, Standards | AccessibilityTestCases.md |
| Security Agent             | 05-agents | Generate Security Test Cases             | FunctionalTestCases.md                              | Security Prompt, Standards      | SecurityTestCases.md      |
| RTM Agent                  | 05-agents | Generate Requirement Traceability Matrix | RequirementAnalysis.md, FunctionalTestCases.md      | RTM Prompt, Standards           | RTM.md                    |
| Playwright Agent           | 05-agents | Generate Playwright Scripts              | FunctionalTestCases.md                              | Playwright Prompt, Standards    | Playwright Scripts        |
| Selenium Agent             | 05-agents | Generate Selenium Scripts                | FunctionalTestCases.md                              | Selenium Prompt, Standards      | Selenium Scripts          |
| Code Review Agent          | 05-agents | Review Automation Code                   | Automation Scripts                                  | Code Review Standards           | Review Report             |
| Execution Agent            | 05-agents | Execute Automation                       | Automation Suite                                    | Execution Standards             | Execution Report          |
| TestLink Sync Agent        | 05-agents | Synchronize Test Cases with TestLink     | FunctionalTestCases.md, project-context.md          | TestLink Prompt, Sync Standards | Sync Report               |
| TestRail Sync Agent        | 05-agents | Synchronize Test Cases with TestRail     | FunctionalTestCases.md, project-context.md          | TestRail Prompt, Sync Standards | Sync Report               |
| Jira Sync Agent            | 05-agents | Synchronize Test Cases with Jira         | FunctionalTestCases.md, project-context.md          | Jira Prompt, Sync Standards     | Sync Report               |

## Prompts Catalog

| Prompt                      | Used By                    | Purpose                           |
| --------------------------- | -------------------------- | --------------------------------- |
| Requirement Analysis Prompt | Requirement Analysis Agent | Analyze Requirements              |
| Gap Analysis Prompt         | Gap Analysis Agent         | Identify Requirement Gaps         |
| Test Plan Prompt            | Test Plan Agent            | Generate Test Plan                |
| Functional Prompt           | Functional Agent           | Generate Functional Test Cases    |
| API Prompt                  | API Agent                  | Generate API Test Cases           |
| UI Prompt                   | UI Agent                   | Generate UI Test Cases            |
| Accessibility Prompt        | Accessibility Agent        | Generate Accessibility Test Cases |
| Security Prompt             | Security Agent             | Generate Security Test Cases      |
| RTM Prompt                  | RTM Agent                  | Generate RTM                      |
| Playwright Prompt           | Playwright Agent           | Generate Playwright Scripts       |
| Selenium Prompt             | Selenium Agent             | Generate Selenium Scripts         |
| TestLink Sync Prompt        | TestLink Sync Agent        | Synchronize with TestLink         |
| TestRail Sync Prompt        | TestRail Sync Agent        | Synchronize with TestRail         |
| Jira Sync Prompt            | Jira Sync Agent            | Synchronize with Jira             |

Standards catalog

| Standard                       | Used By                            |
| ------------------------------ | ---------------------------------- |
| Testing Standards              | All Agents                         |
| Test Plan Standards            | Test Plan Agent                    |
| Functional Standards           | Functional Agent                   |
| API Standards                  | API Agent                          |
| Accessibility Standards        | Accessibility Agent                |
| Security Standards             | Security Agent                     |
| RTM Standards                  | RTM Agent                          |
| Automation Standards           | Automation Agents                  |
| Code Review Standards          | Code Review Agent                  |
| Test Management Sync Standards | TestLink/TestRail/Jira Sync Agents |

## Template catalog

| Template                    | Used By                    | Output                 |
| --------------------------- | -------------------------- | ---------------------- |
| RequirementAnalysis.md      | Requirement Analysis Agent | RequirementAnalysis.md |
| GapAnalysis.md              | Gap Analysis Agent         | GapAnalysis.md         |
| TestPlan.md                 | Test Plan Agent            | TestPlan.md            |
| FunctionalTestCase.md       | Functional Agent           | FunctionalTestCases.md |
| RTM.md                      | RTM Agent                  | RTM.md                 |
| ImpactAnalysis.md           | Change Impact Agent        | ImpactAnalysis.md      |
| TestManagementSyncReport.md | Sync Agents                | Sync Report            |

## Evaluator Catalog

| Evaluator            | Evaluates             | Output              |
| -------------------- | --------------------- | ------------------- |
| Test Case Evaluator  | Functional Test Cases | Coverage Report     |
| Test Plan Evaluator  | Test Plan             | Quality Report      |
| RTM Evaluator        | RTM                   | Traceability Report |
| Playwright Evaluator | Playwright Scripts    | Automation Review   |
| Selenium Evaluator   | Selenium Scripts      | Automation Review   |

## Integration Catalog

| Integration  | Purpose                      | Triggered By            |
| ------------ | ---------------------------- | ----------------------- |
| TestLink MCP | Publish Test Cases           | TestLink Sync Agent     |
| TestRail MCP | Publish Test Cases           | TestRail Sync Agent     |
| Jira MCP     | Create/Update Test Artifacts | Jira Sync Agent         |
| GitHub MCP   | Push Automation Code         | GitHub Agent            |
| Figma MCP    | Read UI Designs              | UI Agent                |
| BrowserStack | Execute Tests                | Execution Agent         |
| Appium MCP   | Mobile Automation            | Mobile Automation Agent |
