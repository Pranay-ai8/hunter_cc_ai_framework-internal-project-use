# Integrations & MCPs

This folder contains Model Context Protocol (MCP) configurations and integrations that enhance the QA-AI-Framework with external tool connectivity.

## Available MCPs

### 1. Jira MCP
**File:** [jira-mcp.md](jira-mcp.md)

**Purpose:** Integration with Atlassian Jira for requirement management and issue tracking.

**Use Cases:**
- Read epics, stories, and acceptance criteria from Jira
- Map Jira requirements to test cases
- Track test execution results in Jira
- Link test cases to defects and stories
- Generate traceability between Jira and test artifacts

**Integration Points:**
- Requirement Analysis Agent → Fetches stories and acceptance criteria
- Gap Analysis Agent → Identifies missing AC and requirements
- Test Plan Agent → Maps test strategy to Jira epics

---

### 2. Figma MCP
**File:** [figma-mcp.md](figma-mcp.md)

**Purpose:** Integration with Figma design tool for UI/UX analysis and design-driven testing.

**Use Cases:**
- Extract UI components from Figma designs
- Identify user journeys and navigation flows
- Analyze form validations and field dependencies
- Generate UI test cases from design specifications
- Validate responsive behavior requirements

**Integration Points:**
- UI Test Case Agent → Generates test cases from Figma designs
- Requirement Analysis Agent → Maps design requirements to business rules
- Gap Analysis Agent → Identifies missing UI specifications

---

### 3. GitHub MCP
**File:** [github-mcp.md](github-mcp.md)

**Purpose:** Integration with GitHub for version control and CI/CD workflows.

**Use Cases:**
- Store test artifacts in GitHub repositories
- Track test case versions and changes
- Integrate with GitHub Actions for automated test generation
- Link test cases to code commits and PRs
- Manage test standards and templates in Git

**Integration Points:**
- Test artifact versioning and collaboration
- Automated test case export to GitHub
- CI/CD pipeline integration for test generation

---

### 4. TestRail MCP
**File:** [testrail-mcp.md](testrail-mcp.md)

**Purpose:** Integration with TestRail for test case management and execution tracking.

**Use Cases:**
- Export test cases to TestRail
- Track test execution and results
- Manage test runs and test suites
- Report test metrics and coverage
- Link test cases to requirements and defects

**Integration Points:**
- Test Execution Agent → Tracks test results
- Functional Test Case Agent → Exports test cases
- Reporting → Generates metrics from TestRail data

---

### 5. TestLink MCP
**File:** [testlink-mcp.md](testlink-mcp.md)

**Purpose:** Integration with TestLink open-source test management platform.

**Use Cases:**
- Export test cases to TestLink
- Manage test plans and test suites
- Track test execution results
- Generate test reports and metrics
- Alternative to TestRail for open-source environments

**Integration Points:**
- Test Execution Agent → Records test results
- Test Case Agents → Export cases to TestLink
- Reporting → Generates execution reports

---

### 6. Appium MCP
**File:** [appium.mcp.md](appium.mcp.md)

**Purpose:** Integration with Appium for mobile application testing automation.

**Use Cases:**
- Automate mobile test execution (Android and iOS)
- Generate mobile test case automation scripts
- Support for cross-platform mobile testing
- Simulate user interactions on mobile devices
- Validate mobile-specific requirements

**Integration Points:**
- Mobile Test Case Agent → Generates Appium scripts
- Test Execution Agent → Runs mobile test automation
- Automation Guidelines → Appium best practices and patterns

---

## MCP Registry

The `mcp-registry.json` file maintains a centralized registry of all active MCPs used in the framework. This registry is referenced during:
- Test case generation with external tool integration
- Test execution planning
- Artifact export workflows
- Documentation generation

---

## How to Use MCPs in the Framework

### Step 1: Enable an MCP
1. Review the relevant MCP documentation file
2. Configure connection credentials and API keys
3. Update `mcp-registry.json` to mark the MCP as active

### Step 2: Reference in Agents
Use MCP-enabled agents in your workflow:
- **Requirement Analysis** → Jira MCP for fetching requirements
- **UI Test Cases** → Figma MCP for design analysis
- **Test Execution** → TestRail/TestLink MCPs for result tracking
- **Mobile Testing** → Appium MCP for automation
- **Version Control** → GitHub MCP for artifact storage

### Step 3: Map Requirements to Test Cases
```
Jira (Epics/Stories) 
    ↓
Figma (UI Designs) 
    ↓
Gap Analysis (Identify missing requirements)
    ↓
Test Case Generation (Functional, UI, API)
    ↓
TestRail/TestLink (Manage and execute)
    ↓
GitHub (Version control and artifacts)
```

---

## Supported MCP Workflow

```text
Requirements → Analysis → Planning → Generation → Management → Execution → Reporting
    ↑              ↑          ↑           ↑            ↑           ↑          ↑
   Jira        Figma      Jira      Multiple     TestRail/   Appium    GitHub
               (UI)     Standards   Agents       TestLink   (Mobile)
```

---

## Configuration Guidelines

### API Keys and Credentials
Store MCP credentials in environment variables or secure config files:
```bash
JIRA_API_TOKEN=your_token
FIGMA_API_KEY=your_key
GITHUB_TOKEN=your_token
TESTRAIL_API_KEY=your_key
TESTLINK_API_KEY=your_key
APPIUM_SERVER_URL=http://localhost:4723
```

### Connection Validation
Before using an MCP, validate the connection:
1. Test API credentials
2. Verify network connectivity
3. Confirm required permissions/scopes
4. Check API rate limits

---

## Integration Status

| MCP | Status | Configuration | Last Updated |
|---|---|---|---|
| Jira | - | [jira-mcp.md](jira-mcp.md) | - |
| Figma | - | [figma-mcp.md](figma-mcp.md) | - |
| GitHub | - | [github-mcp.md](github-mcp.md) | - |
| TestRail | - | [testrail-mcp.md](testrail-mcp.md) | - |
| TestLink | - | [testlink-mcp.md](testlink-mcp.md) | - |
| Appium | - | [appium.mcp.md](appium.mcp.md) | - |

---

## Best Practices

1. **Validate Requirements** → Use Jira MCP to fetch source-of-truth requirements
2. **Design-Driven Testing** → Leverage Figma MCP for UI test case generation
3. **Traceability** → Map Jira stories → Test Cases → TestRail runs
4. **Automation** → Use Appium for mobile test automation
5. **Version Control** → Store artifacts in GitHub for collaboration
6. **Execution Tracking** → Use TestRail/TestLink for metrics and reporting

---

## Troubleshooting

### Common Issues

**MCP Connection Fails**
- Verify API credentials and environment variables
- Check network connectivity and firewall rules
- Confirm API endpoint URLs are correct
- Review API rate limits and quota

**Authentication Errors**
- Refresh API tokens if expired
- Verify permission scopes are granted
- Check for IP address whitelisting requirements

**Data Export Issues**
- Validate exported data format compatibility
- Check for special characters or encoding issues
- Confirm API response size limits
- Review MCP transformation logic

---

## Extending MCPs

To add a new MCP:
1. Create a new `{tool-name}-mcp.md` file in this folder
2. Document purpose, use cases, and integration points
3. Add configuration instructions and examples
4. Update `mcp-registry.json` with the new MCP entry
5. Update this README with the new integration

---

## Related Documentation

- [Testing Standards](../03-standards/testing-standards.md) → Best practices for test design
- [Automation Guidelines](../03-standards/automation-guidelines.md) → Framework-specific automation patterns
- [Framework README](../README.md) → Overall framework structure and workflow

---

## Support

For MCP-specific questions or issues:
1. Review the relevant MCP documentation file
2. Check integration status in the table above
3. Verify configuration and credentials
4. Consult framework best practices and troubleshooting guides
