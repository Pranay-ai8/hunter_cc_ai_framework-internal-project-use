import fs from 'fs/promises'
import path from 'path'
import Anthropic from '@anthropic-ai/sdk'
import mammoth from 'mammoth'
import ExcelJS from 'exceljs'

// Root of the QA framework (one level up from 08-ui)
const ROOT = path.join(process.cwd(), '..')

export type FileRole = 'input' | 'loaded' | 'written'

export interface FlowFile {
  path: string
  role: FileRole
  note?: string
}

export interface StepEvent {
  type: 'step' | 'log' | 'file' | 'done' | 'error'
  step?: number
  title?: string
  message?: string
  level?: 'info' | 'success' | 'warning' | 'error'
  file?: FlowFile
  files?: FlowFile[]
  error?: string
}

async function readIfExists(p: string): Promise<string | null> {
  try {
    return await fs.readFile(p, 'utf-8')
  } catch {
    return null
  }
}

async function extractDocx(p: string): Promise<string> {
  const buffer = await fs.readFile(p)
  const result = await mammoth.extractRawText({ buffer })
  return result.value
}

async function listDocs(dir: string): Promise<string[]> {
  try {
    const entries = await fs.readdir(dir, { withFileTypes: true })
    return entries.filter((e) => e.isFile() && !e.name.startsWith('.')).map((e) => e.name)
  } catch {
    return []
  }
}

interface SheetSpec {
  name: string
  headers: string[]
  rows: string[][]
}

interface StructuredResult {
  markdown: string
  sheets?: SheetSpec[]
}

const EMIT_TOOL = {
  name: 'emit_report',
  description:
    'Emit the generated report as markdown, plus optional structured spreadsheet data.',
  input_schema: {
    type: 'object' as const,
    properties: {
      markdown: {
        type: 'string',
        description: 'The full report content in Markdown format.',
      },
      sheets: {
        type: 'array',
        description:
          'Optional structured tabular data to also save as an Excel workbook (one entry per sheet).',
        items: {
          type: 'object',
          properties: {
            name: { type: 'string', description: 'Sheet name (max 31 chars).' },
            headers: { type: 'array', items: { type: 'string' } },
            rows: {
              type: 'array',
              items: { type: 'array', items: { type: 'string' } },
            },
          },
          required: ['name', 'headers', 'rows'],
        },
      },
    },
    required: ['markdown'],
  },
}

async function callClaude(
  client: Anthropic,
  systemPrompt: string,
  userPrompt: string
): Promise<StructuredResult> {
  const response = await client.messages.create({
    model: 'claude-sonnet-5',
    max_tokens: 16000,
    system: systemPrompt,
    tools: [EMIT_TOOL],
    tool_choice: { type: 'tool', name: 'emit_report' },
    messages: [{ role: 'user', content: userPrompt }],
  })

  const toolUse = response.content.find((b) => b.type === 'tool_use')
  if (!toolUse || toolUse.type !== 'tool_use') {
    throw new Error('Claude did not return the expected structured report.')
  }
  return toolUse.input as StructuredResult
}

async function writeSheetsToExcel(sheets: SheetSpec[], outPath: string) {
  const wb = new ExcelJS.Workbook()
  for (const sheet of sheets) {
    const ws = wb.addWorksheet(sheet.name.slice(0, 31))
    ws.addRow(sheet.headers)
    ws.getRow(1).font = { bold: true }
    ws.getRow(1).fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: 'FFDDDDDD' },
    }
    for (const row of sheet.rows) ws.addRow(row)
    sheet.headers.forEach((h, i) => {
      const colValues = sheet.rows.map((r) => (r[i] || '').length)
      ws.getColumn(i + 1).width = Math.min(
        Math.max(h.length, ...colValues, 10) + 2,
        60
      )
    })
    ws.views = [{ state: 'frozen', ySplit: 1 }]
  }
  await wb.xlsx.writeFile(outPath)
}

const outRoot = path.join(ROOT, '07-outputs')

interface CommonContext {
  claudeMd: string
  requirementAnalysisMd: string
  gapAnalysisMd: string
}

/**
 * Steps 1-4, shared by every orchestrator: load CLAUDE.md, validate the PRD
 * input, and produce (or reuse) Requirement Analysis + Gap Analysis — every
 * downstream report is built on top of these two.
 */
async function* loadCommonContext(
  client: Anthropic
): AsyncGenerator<StepEvent, CommonContext, void> {
  // ---------- Step 1: Load Global Instructions ----------
  yield { type: 'step', step: 1, title: 'Load Global Instructions' }
  const claudeMd = await readIfExists(path.join(ROOT, 'CLAUDE.md'))
  if (!claudeMd) {
    throw new Error('CLAUDE.md not found at project root.')
  }
  yield { type: 'file', file: { path: 'CLAUDE.md', role: 'loaded' } }
  yield { type: 'log', level: 'success', message: 'Loaded CLAUDE.md' }

  // ---------- Step 2: Validate Inputs ----------
  yield { type: 'step', step: 2, title: 'Validate Inputs under 01-docs' }
  const docsRoot = path.join(ROOT, '01-docs')
  const prdDir = path.join(docsRoot, 'PRD')
  const prdFiles = await listDocs(prdDir)
  if (prdFiles.length === 0) {
    throw new Error('No PRD documents found under 01-docs/PRD.')
  }

  let requirementText = ''
  for (const f of prdFiles) {
    const full = path.join(prdDir, f)
    yield { type: 'file', file: { path: `01-docs/PRD/${f}`, role: 'input' } }
    if (f.toLowerCase().endsWith('.docx')) {
      requirementText += `\n\n--- ${f} ---\n` + (await extractDocx(full))
    } else {
      const txt = await readIfExists(full)
      if (txt) requirementText += `\n\n--- ${f} ---\n` + txt
    }
  }
  yield {
    type: 'log',
    level: 'success',
    message: `Found ${prdFiles.length} PRD document(s); extracted requirement text.`,
  }

  // ---------- Step 3: Requirement Analysis ----------
  yield { type: 'step', step: 3, title: 'Requirement Analysis' }
  const reqOutPath = path.join(outRoot, 'RequirementAnalysis', 'RequirementAnalysis.md')
  let requirementAnalysisMd = await readIfExists(reqOutPath)

  if (requirementAnalysisMd) {
    yield {
      type: 'log',
      level: 'info',
      message: 'RequirementAnalysis.md already exists — reusing existing output.',
    }
    yield {
      type: 'file',
      file: { path: '07-outputs/RequirementAnalysis/RequirementAnalysis.md', role: 'loaded' },
    }
  } else {
    const agentMd = await readIfExists(path.join(ROOT, '05-agents', 'requirement-analysis-agent.md'))
    const promptMd = await readIfExists(path.join(ROOT, '04-prompts', 'requirement-analysis-prompt.md'))
    const templateMd = await readIfExists(path.join(ROOT, '02-templates', 'RequirementAnalysis.md'))
    yield { type: 'file', file: { path: '05-agents/requirement-analysis-agent.md', role: 'loaded' } }
    yield { type: 'file', file: { path: '04-prompts/requirement-analysis-prompt.md', role: 'loaded' } }
    yield { type: 'file', file: { path: '02-templates/RequirementAnalysis.md', role: 'loaded' } }
    yield { type: 'log', level: 'info', message: 'Calling Claude to generate Requirement Analysis...' }

    const result = await callClaude(
      client,
      `${claudeMd}\n\n${agentMd}`,
      `${promptMd}\n\nUse this template structure:\n${templateMd}\n\nRequirement source documents:\n${requirementText}\n\nReturn the completed Requirement Analysis Report as markdown via the emit_report tool. No sheets needed for this report.`
    )
    requirementAnalysisMd = result.markdown
    await fs.mkdir(path.dirname(reqOutPath), { recursive: true })
    await fs.writeFile(reqOutPath, requirementAnalysisMd, 'utf-8')
    yield { type: 'file', file: { path: '07-outputs/RequirementAnalysis/RequirementAnalysis.md', role: 'written' } }
    yield { type: 'log', level: 'success', message: 'Requirement Analysis generated and saved.' }
  }

  // ---------- Step 4: Gap Analysis ----------
  yield { type: 'step', step: 4, title: 'Gap Analysis' }
  const gapOutPath = path.join(outRoot, 'GapAnalysis', 'GapAnalysis.md')
  const gapXlsxPath = path.join(outRoot, 'GapAnalysis', 'GapAnalysis.xlsx')
  let gapAnalysisMd = await readIfExists(gapOutPath)

  if (gapAnalysisMd) {
    yield { type: 'log', level: 'info', message: 'GapAnalysis.md already exists — reusing existing output.' }
    yield { type: 'file', file: { path: '07-outputs/GapAnalysis/GapAnalysis.md', role: 'loaded' } }
  } else {
    const agentMd = await readIfExists(path.join(ROOT, '05-agents', 'gap-analysis-agent.md'))
    const promptMd = await readIfExists(path.join(ROOT, '04-prompts', 'gap-analysis-prompt.md'))
    const templateMd = await readIfExists(path.join(ROOT, '02-templates', 'GapAnalysis.md'))
    yield { type: 'file', file: { path: '05-agents/gap-analysis-agent.md', role: 'loaded' } }
    yield { type: 'file', file: { path: '04-prompts/gap-analysis-prompt.md', role: 'loaded' } }
    yield { type: 'file', file: { path: '02-templates/GapAnalysis.md', role: 'loaded' } }
    yield { type: 'log', level: 'info', message: 'Calling Claude to generate Gap Analysis...' }

    const result = await callClaude(
      client,
      `${claudeMd}\n\n${agentMd}`,
      `${promptMd}\n\nUse this template structure:\n${templateMd}\n\nPrimary input — Requirement Analysis:\n${requirementAnalysisMd}\n\nReturn the Gap Analysis Report as markdown via the emit_report tool, AND also include a "sheets" array so it can be saved as Excel: include at least a "Missing Functional Requirements" sheet, a "Missing Business Rules" sheet, and a "Clarification Questions" sheet, each with headers and rows.`
    )
    gapAnalysisMd = result.markdown
    await fs.mkdir(path.dirname(gapOutPath), { recursive: true })
    await fs.writeFile(gapOutPath, gapAnalysisMd, 'utf-8')
    yield { type: 'file', file: { path: '07-outputs/GapAnalysis/GapAnalysis.md', role: 'written' } }

    if (result.sheets && result.sheets.length > 0) {
      await writeSheetsToExcel(result.sheets, gapXlsxPath)
      yield { type: 'file', file: { path: '07-outputs/GapAnalysis/GapAnalysis.xlsx', role: 'written' } }
    }
    yield { type: 'log', level: 'success', message: 'Gap Analysis generated and saved.' }
  }

  return { claudeMd, requirementAnalysisMd, gapAnalysisMd }
}

function getClient(): Anthropic {
  const apiKey = process.env.ANTHROPIC_API_KEY
  if (!apiKey) {
    throw new Error(
      'ANTHROPIC_API_KEY is not set. Add it to 08-ui/.env.local and restart the dev server.'
    )
  }
  return new Anthropic({ apiKey })
}

/**
 * Functional Test Case Orchestrator
 * (mirrors MasterQAOrchestrator/FunctionalTestCaseOrchestrator.md)
 */
export async function* runFunctionalTestCaseOrchestrator(): AsyncGenerator<StepEvent> {
  let client: Anthropic
  try {
    client = getClient()
  } catch (err) {
    yield { type: 'error', error: err instanceof Error ? err.message : String(err) }
    return
  }

  let ctx: CommonContext
  try {
    ctx = yield* loadCommonContext(client)
  } catch (err) {
    yield { type: 'error', error: err instanceof Error ? err.message : String(err) }
    return
  }
  const { claudeMd, requirementAnalysisMd, gapAnalysisMd } = ctx

  // ---------- Step 5: Load Framework Resources ----------
  yield { type: 'step', step: 5, title: 'Load Framework Resources' }
  const testCaseTemplate = await readIfExists(path.join(ROOT, '02-templates', 'TestCase.md'))
  const testingStandards = await readIfExists(path.join(ROOT, '03-standards', 'testing-standards.md'))
  const testcaseStandards = await readIfExists(path.join(ROOT, '03-standards', 'testcase-standards.md'))
  const namingConventions = await readIfExists(
    path.join(ROOT, '03-standards', 'naming-conventions-standards.md')
  )
  for (const f of [
    '02-templates/TestCase.md',
    '02-templates/GapAnalysis.md',
    '03-standards/testing-standards.md',
    '03-standards/testcase-standards.md',
    '03-standards/naming-conventions-standards.md',
  ]) {
    yield { type: 'file', file: { path: f, role: 'loaded' } }
  }
  yield { type: 'log', level: 'success', message: 'Framework templates and standards loaded.' }

  // ---------- Step 6: Execute Functional Agent ----------
  yield { type: 'step', step: 6, title: 'Execute Functional Agent' }
  const agentMd = await readIfExists(path.join(ROOT, '05-agents', 'functional-agent.md'))
  const promptMd = await readIfExists(path.join(ROOT, '04-prompts', 'functional-testcase-prompt.md'))
  yield { type: 'file', file: { path: '05-agents/functional-agent.md', role: 'loaded' } }
  yield { type: 'file', file: { path: '04-prompts/functional-testcase-prompt.md', role: 'loaded' } }
  yield { type: 'log', level: 'info', message: 'Calling Claude to generate Functional Test Cases...' }

  let tcResult: StructuredResult
  try {
    tcResult = await callClaude(
      client,
      `${claudeMd}\n\n${agentMd}\n\nApply these standards:\n${testingStandards}\n\n${testcaseStandards}\n\n${namingConventions || ''}`,
      `${promptMd}\n\nUse this test case template structure:\n${testCaseTemplate}\n\nConsume the following as context (requirement analysis and gap analysis) so identified gaps are reflected as clarifications/assumptions in the generated test cases:\n\n--- RequirementAnalysis.md ---\n${requirementAnalysisMd}\n\n--- GapAnalysis.md ---\n${gapAnalysisMd}\n\nReturn the Functional Test Cases as markdown via the emit_report tool, AND include a "sheets" array with one sheet named "Functional Test Cases" containing headers: Test Case ID, Module, Scenario, Objective, Preconditions, Test Data, Test Steps, Expected Result, Priority, Severity, Automation Candidate — and one row per test case.`
    )
  } catch (err) {
    yield { type: 'error', error: err instanceof Error ? err.message : String(err) }
    return
  }

  const tcOutPath = path.join(outRoot, 'TestCases', 'FunctionalTestCases.md')
  const tcXlsxPath = path.join(outRoot, 'TestCases', 'FunctionalTestCases.xlsx')
  await fs.mkdir(path.dirname(tcOutPath), { recursive: true })
  await fs.writeFile(tcOutPath, tcResult.markdown, 'utf-8')
  yield { type: 'file', file: { path: '07-outputs/TestCases/FunctionalTestCases.md', role: 'written' } }

  if (tcResult.sheets && tcResult.sheets.length > 0) {
    await writeSheetsToExcel(tcResult.sheets, tcXlsxPath)
    yield { type: 'file', file: { path: '07-outputs/TestCases/FunctionalTestCases.xlsx', role: 'written' } }
  }
  yield { type: 'log', level: 'success', message: 'Functional Test Cases generated and saved.' }

  // ---------- Step 7: Save Outputs ----------
  yield { type: 'step', step: 7, title: 'Save Outputs' }
  yield { type: 'log', level: 'success', message: 'All artifacts saved under 07-outputs/.' }

  yield { type: 'done' }
}

/**
 * Test Plan Orchestrator
 * (mirrors MasterQAOrchestrator/TestPlanOrchestrator.md)
 */
export async function* runTestPlanOrchestrator(): AsyncGenerator<StepEvent> {
  let client: Anthropic
  try {
    client = getClient()
  } catch (err) {
    yield { type: 'error', error: err instanceof Error ? err.message : String(err) }
    return
  }

  let ctx: CommonContext
  try {
    ctx = yield* loadCommonContext(client)
  } catch (err) {
    yield { type: 'error', error: err instanceof Error ? err.message : String(err) }
    return
  }
  const { claudeMd, requirementAnalysisMd, gapAnalysisMd } = ctx

  // ---------- Step 5: Load Framework Resources ----------
  yield { type: 'step', step: 5, title: 'Load Framework Resources' }
  const testPlanTemplate = await readIfExists(path.join(ROOT, '02-templates', 'TestPlan.md'))
  const testingStandards = await readIfExists(path.join(ROOT, '03-standards', 'testing-standards.md'))
  for (const f of ['02-templates/TestPlan.md', '03-standards/testing-standards.md']) {
    yield { type: 'file', file: { path: f, role: 'loaded' } }
  }
  yield { type: 'log', level: 'success', message: 'Framework template and standards loaded.' }

  // ---------- Step 6: Execute Test Plan Agent ----------
  yield { type: 'step', step: 6, title: 'Execute Test Plan Agent' }
  const agentMd = await readIfExists(path.join(ROOT, '05-agents', 'testplan-gen-agent.md'))
  const promptMd = await readIfExists(path.join(ROOT, '04-prompts', 'testplan-gen-prompt.md'))
  yield { type: 'file', file: { path: '05-agents/testplan-gen-agent.md', role: 'loaded' } }
  yield { type: 'file', file: { path: '04-prompts/testplan-gen-prompt.md', role: 'loaded' } }
  yield { type: 'log', level: 'info', message: 'Calling Claude to generate the Test Plan...' }

  let tpResult: StructuredResult
  try {
    tpResult = await callClaude(
      client,
      `${claudeMd}\n\n${agentMd || ''}\n\nApply these standards:\n${testingStandards}`,
      `${promptMd}\n\nUse this test plan template structure:\n${testPlanTemplate}\n\nConsume the following as context (requirement analysis and gap analysis):\n\n--- RequirementAnalysis.md ---\n${requirementAnalysisMd}\n\n--- GapAnalysis.md ---\n${gapAnalysisMd}\n\nReturn the completed Test Plan as markdown via the emit_report tool. No sheets are required for this report.`
    )
  } catch (err) {
    yield { type: 'error', error: err instanceof Error ? err.message : String(err) }
    return
  }

  const tpOutPath = path.join(outRoot, 'TestPlan', 'TestPlan.md')
  await fs.mkdir(path.dirname(tpOutPath), { recursive: true })
  await fs.writeFile(tpOutPath, tpResult.markdown, 'utf-8')
  yield { type: 'file', file: { path: '07-outputs/TestPlan/TestPlan.md', role: 'written' } }
  yield { type: 'log', level: 'success', message: 'Test Plan generated and saved.' }

  // ---------- Step 7: Save Outputs ----------
  yield { type: 'step', step: 7, title: 'Save Outputs' }
  yield { type: 'log', level: 'success', message: 'All artifacts saved under 07-outputs/.' }

  yield { type: 'done' }
}
