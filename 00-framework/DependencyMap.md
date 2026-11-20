# Dependency Map

Version: 1.0

Framework: Enterprise AI QA Framework

---

# Purpose

This document defines the dependency relationships between Orchestrators, Agents, Prompts, Standards, Templates, Integrations, Evaluators and Outputs.

The purpose is to ensure every framework component follows a consistent execution model and dependency chain.

---

# Framework Dependency Hierarchy

```
User
    │
    ▼
Master QA Orchestrator
    │
    ▼
Specialized Orchestrator
    │
    ▼
AI Agent
    │
    ▼
Prompt
    │
    ├───────────────┐
    ▼               ▼
Standards       Templates
    │               │
    └──────┬────────┘
           ▼
      Generated Output
           │
           ▼
       Evaluator
           │
           ▼
 Downstream Orchestrator
```

---

# Component Dependency Rules

## Master QA Orchestrator

Depends On

- CLAUDE.md (or active LLM instruction)
- Framework Manifest
- Project Context

Invokes

- Specialized Orchestrators

Never Depends On

- Prompts
- Templates
- Standards

---

## Specialized Orchestrators

Examples

- FunctionalTestCaseOrchestrator
- TestPlanOrchestrator
- RTMOrchestrator
- PlaywrightAutomationOrchestrator

Depends On

- Project Context
- Previous Outputs
- Assigned Agent

Loads

- Standards
- Templates

Executes

- AI Agent

Triggers

- Evaluator

Produces

- Output Artifact

---

## Agents

Every Agent depends on

- Prompt
- Standards
- Templates
- Project Context
- Required Input Documents

Agents never depend on other Agents.

---

## Prompts

Prompts depend on

- User Objective
- Project Inputs

Prompts never depend on Templates.

Prompts never depend on Standards.

---

## Standards

Standards are independent.

They shall never depend on

- Prompts
- Agents
- Outputs

---

## Templates

Templates are independent.

Templates shall define document structure only.

---

## Evaluators

Evaluators depend on

- Generated Output
- Standards
- Templates (if required)

Evaluators never regenerate artifacts.

---

## Integrations

Integrations depend on

- Generated Outputs
- Project Context

Examples

- TestLink
- TestRail
- Jira
- GitHub
- BrowserStack
- Appium

---

# Functional Test Case Dependencies

```
Requirement Documents
        │
        ▼
Requirement Analysis
        │
        ▼
Gap Analysis
        │
        ▼
Test Plan
        │
        ▼
FunctionalTestCaseOrchestrator
        │
        ▼
Functional Agent
        │
        ├──────────────┐
        ▼              ▼
Prompt         Standards
        │              │
        └──────┬───────┘
               ▼
Template
               │
               ▼
Functional Test Cases
               │
               ▼
Test Case Evaluator
```

---

# RTM Dependencies

```
Requirement Analysis
        │
        ▼
Functional Test Cases
        │
        ▼
RTM Agent
        │
        ▼
RTM Prompt
        │
        ▼
RTM Template
        │
        ▼
RTM Output
        │
        ▼
RTM Evaluator
```

---

# Automation Dependencies

```
Functional Test Cases
        │
        ▼
Automation Framework Agent
        │
        ▼
Playwright Agent
        │
        ▼
Code Review
        │
        ▼
Execution
```

---

# Test Management Dependencies

```
Functional Test Cases
        │
        ▼
TestLink Sync Agent
        │
        ▼
TestLink MCP
        │
        ▼
TestLink
```

```
Functional Test Cases
        │
        ▼
TestRail Sync Agent
        │
        ▼
TestRail MCP
        │
        ▼
TestRail
```

```
Functional Test Cases
        │
        ▼
Jira Sync Agent
        │
        ▼
Jira MCP
        │
        ▼
Jira
```

---

# Output Dependencies

Requirement Analysis
↓
Gap Analysis
↓
Test Plan
↓
Functional Test Cases
↓
RTM
↓
Automation
↓
Execution

Every downstream component shall consume the latest validated output.

---

# Evaluation Dependencies

Every major artifact shall be evaluated before it becomes an input to the next stage.

Examples

- Functional Test Cases
- RTM
- Automation Scripts

---

# General Dependency Rules

Always

- Consume previous validated output.
- Load standards before generation.
- Use templates for output structure.
- Execute evaluators after generation.
- Preserve traceability.

Never

- Skip mandatory dependencies.
- Execute downstream agents without validated upstream outputs.
- Create circular dependencies.
- Modify upstream outputs directly.

---

# Dependency Principle

The framework follows a unidirectional dependency model.

```
Inputs
↓
Orchestrator
↓
Agent
↓
Prompt
↓
Standards
↓
Template
↓
Output
↓
Evaluator
↓
Next Orchestrator
```

No component shall introduce reverse or circular dependencies.