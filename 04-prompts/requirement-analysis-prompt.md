# Requirement Analysis Prompt

## Objective

Generate a comprehensive Requirement Analysis Report by analyzing all available project documentation.

## Input Sources

## Input Sources

Analyze all relevant documents available under the **01-docs** folder, including but not limited to:

- Business Requirement Documents (BRD)
- Product Requirement Documents (PRD)
- Functional Requirement Documents (FRD)
- User Stories
- Acceptance Criteria
- Business Rules
- Figma Designs
- Screenshots
- API Documentation
- Swagger / OpenAPI Specifications
- Architecture Documents
- Database Documents
- Release Notes
- Supporting Documents
- Project Context documents under `01-docs/ProjectContext/`

## Project Context Analysis

Review the documents available under:

`01-docs/ProjectContext/`

Use Project Context to establish an overall understanding of:

- Product and system background
- Business/domain context
- Project scope
- Major modules and capabilities
- System boundaries
- Relevant terminology
- Technology/platform context
- Important constraints
- Dependencies and integrations

Use this context when interpreting and correlating information from BRD, PRD, FRD, JIRA, Figma, API, Architecture, and other project sources.

Project Context is a supporting context source.

Do not treat information from Project Context as a confirmed functional requirement unless it is explicitly defined as a requirement.

If Project Context conflicts with a formal requirement source, report the conflict and identify the relevant sources rather than silently resolving it.

## Analysis Scope

Generate a complete understanding of the application by identifying:

## Analysis Scope

Generate a complete understanding of the application by identifying:

- Project Context
- System Scope and Boundaries
- Business Objectives
- Functional Modules
- Features
- User Roles
- Business Workflows
- Business Rules
- Functional Requirements
- Non-Functional Requirements
- UI Components
- API Integrations
- External System Integrations
- Data Validations
- Dependencies
- Risks
- Assumptions
- Ambiguities
- Missing Information

## Deliverable

# Validation

Before generating the Requirement Analysis Report:

- Confirm that Project Context documents have been reviewed.
- Use Project Context to improve understanding of the system.
- Distinguish contextual information from confirmed requirements.
- Preserve conflicts between Project Context and formal requirements.
- Do not invent requirements based on Project Context.
- Preserve source traceability for significant information derived from Project Context.

Generate the Requirement Analysis using the structure defined in:

02-templates/RequirementAnalysis.md

## Output

Save the generated report as:

07-outputs/RequirementAnalysis.md