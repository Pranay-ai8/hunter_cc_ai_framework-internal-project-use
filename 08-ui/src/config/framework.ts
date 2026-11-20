import { Agent, Standard, Prompt } from '@/types'

export const AGENTS: Agent[] = [
  {
    id: 'requirement-analysis',
    name: 'Requirement Analysis',
    description: 'Analyze requirements and generate requirement summary',
    enabled: true,
  },
  {
    id: 'gap-analysis',
    name: 'Gap Analysis',
    description: 'Identify missing requirements and gaps',
    enabled: true,
  },
  {
    id: 'testplan-gen',
    name: 'Test Plan Generation',
    description: 'Generate comprehensive test plan',
    enabled: true,
  },
  {
    id: 'functional',
    name: 'Functional Testing',
    description: 'Generate functional test cases',
    enabled: true,
  },
  {
    id: 'ui',
    name: 'UI Testing',
    description: 'Generate UI test cases',
    enabled: true,
  },
  {
    id: 'api',
    name: 'API Testing',
    description: 'Generate API test cases',
    enabled: true,
  },
  {
    id: 'security',
    name: 'Security Testing',
    description: 'Generate security test cases',
    enabled: true,
  },
  {
    id: 'accessibility',
    name: 'Accessibility Testing',
    description: 'Generate accessibility test cases',
    enabled: true,
  },
  {
    id: 'automation',
    name: 'Automation Scripts',
    description: 'Generate automation scripts and POM',
    enabled: true,
  },
]

export const STANDARDS: Standard[] = [
  {
    id: 'testcase-standards',
    name: 'Test Case Standards',
    description: 'Apply standard test case format and naming conventions',
    enabled: true,
  },
  {
    id: 'api-standards',
    name: 'API Standards',
    description: 'Apply API testing standards and best practices',
    enabled: true,
  },
  {
    id: 'automation-guidelines',
    name: 'Automation Guidelines',
    description: 'Apply automation scripting guidelines and POM patterns',
    enabled: true,
  },
  {
    id: 'naming-conventions',
    name: 'Naming Conventions',
    description: 'Apply consistent naming conventions across artifacts',
    enabled: true,
  },
  {
    id: 'testing-standards',
    name: 'Testing Standards',
    description: 'Apply general testing best practices and standards',
    enabled: true,
  },
  {
    id: 'ui-standards',
    name: 'UI Standards',
    description: 'Apply UI testing standards and accessibility guidelines',
    enabled: true,
  },
]

export const PROMPTS: Prompt[] = [
  {
    id: 'requirement-analysis',
    name: 'Requirement Analysis',
    description: 'Analyze requirements from multiple sources',
    filePath: '../04-prompts/requirement-analysis-prompt.md',
    enabled: true,
  },
  {
    id: 'gap-analysis',
    name: 'Gap Analysis',
    description: 'Identify gaps in requirements',
    filePath: '../04-prompts/gap-analysis-prompt.md',
    enabled: true,
  },
  {
    id: 'testplan-gen',
    name: 'Test Plan Generation',
    description: 'Generate comprehensive test plan',
    filePath: '../04-prompts/testplan-gen-prompt.md',
    enabled: true,
  },
  {
    id: 'functional-testcase',
    name: 'Functional Test Cases',
    description: 'Generate functional test cases',
    filePath: '../04-prompts/functional-testcase-prompt.md',
    enabled: true,
  },
  {
    id: 'ui-testcase',
    name: 'UI Test Cases',
    description: 'Generate UI test cases',
    filePath: '../04-prompts/ui-testcase-prompt.md',
    enabled: true,
  },
  {
    id: 'api',
    name: 'API Test Cases',
    description: 'Generate API test cases',
    filePath: '../04-prompts/api-prompt.md',
    enabled: true,
  },
  {
    id: 'security',
    name: 'Security Test Cases',
    description: 'Generate security test cases',
    filePath: '../04-prompts/security-prompt.md',
    enabled: true,
  },
  {
    id: 'accessibility',
    name: 'Accessibility Test Cases',
    description: 'Generate accessibility test cases',
    filePath: '../04-prompts/accessibility-prompt.md',
    enabled: true,
  },
  {
    id: 'automation',
    name: 'Automation Scripts',
    description: 'Generate automation scripts',
    filePath: '../04-prompts/automation-prompt.md',
    enabled: true,
  },
]
