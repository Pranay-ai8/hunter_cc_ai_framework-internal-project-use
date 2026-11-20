# 00-framework

Governance and self-documentation scaffolding for the QA-AI-Framework itself — describes how the framework's own files relate to each other, separate from the requirement/test artifacts it produces in `07-outputs/`.

## Files

| File | Purpose |
|---|---|
| `FrameworkManifest.md` | Single top-level manifest listing every agent, prompt, standard, and template in the framework, and whether each is currently wired into a working orchestrator. |
| `DependencyMap.md` | Documents which files depend on which — the agent → prompt → standard → template chains used by each orchestrator. |
| `WorkflowManifest.md` | Lists every root orchestrator workflow under `MasterQAOrchestrator/` and its trigger conditions. |

## Status

All three files are currently scaffolding only — created as placeholders, with no content written yet.


# Framework Architecture Documentation

## Purpose

The `00-framework` folder contains the architectural documentation required to understand, maintain, extend, and govern the AI QA Framework.

These documents provide a structured understanding of:

- What the framework is
- What components exist
- How components are connected
- How new capabilities should be introduced

The documentation in this folder is intended to be consumed by:

- QA Engineers
- QA Architects
- Automation Engineers
- Framework Developers
- Engineering Managers
- AI/LLM Agents
- New contributors

---

# Architecture Documentation

The framework architecture is documented through three primary files.

| File | Purpose | Primary Question |
|---|---|---|
| `FrameworkManifest.md` | Defines the framework architecture, principles, scope, and lifecycle | **What is the framework?** |
| `ComponentCatalog.md` | Maintains the inventory of framework components | **What components exist?** |
| `DependencyMap.md` | Defines relationships and dependencies between components | **How are components connected?** |

---

# 1. FrameworkManifest.md

## Purpose

`FrameworkManifest.md` is the authoritative definition of the AI QA Framework.

It provides the high-level architecture, objectives, principles, execution lifecycle, folder responsibilities, and framework governance rules.

## Use this file when

- Understanding the overall framework
- Onboarding a new team member
- Understanding framework principles
- Adding a new capability
- Understanding the QA lifecycle
- Understanding the responsibilities of major framework layers

## Key Question

> What is this framework and how is it intended to operate?

---

# 2. ComponentCatalog.md

## Purpose

`ComponentCatalog.md` is the inventory of the framework.

It maintains information about the components currently implemented in the framework.

Components include:

- Orchestrators
- Agents
- Prompts
- Standards
- Templates
- Evaluators
- Integrations
- Outputs
- LLM components

## Use this file when

- Finding an existing component
- Checking whether a capability already exists
- Understanding an agent's purpose
- Finding the location of a component
- Understanding component inputs and outputs
- Adding a new component

## Key Question

> What components currently exist in the framework?

---

# 3. DependencyMap.md

## Purpose

`DependencyMap.md` defines how framework components interact with each other.

It documents relationships such as:

- Orchestrator → Agent
- Agent → Prompt
- Agent → Standards
- Agent → Template
- Agent → Output
- Output → Evaluator
- Evaluator → Next Workflow
- Agent → External Integration

## Use this file when

- Changing an existing component
- Adding a new agent
- Modifying an orchestrator
- Adding an evaluator
- Adding an integration
- Assessing change impact
- Understanding downstream dependencies

## Key Question

> If I change this component, what other components are affected?

---

# How the Three Documents Work Together

The recommended reading order is:

```text
FrameworkManifest.md
        ↓
Understand the Framework
        ↓
ComponentCatalog.md
        ↓
Identify Required Components
        ↓
DependencyMap.md
        ↓
Understand Component Relationships
        ↓
Implement / Modify Capability