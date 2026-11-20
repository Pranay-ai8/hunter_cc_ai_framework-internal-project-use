import { NextResponse } from 'next/server'
import fs from 'fs/promises'
import path from 'path'

export const runtime = 'nodejs'

// Root of the QA framework (one level up from 08-ui)
const ROOT = path.join(process.cwd(), '..')

// Only these known generated-report files are removed.
// Their parent folders are never touched, and no other file in
// the repository is ever deleted by this route.
const TARGETS = [
  '07-outputs/RequirementAnalysis/RequirementAnalysis.md',
  '07-outputs/GapAnalysis/GapAnalysis.md',
  '07-outputs/GapAnalysis/GapAnalysis.xlsx',
  '07-outputs/TestCases/FunctionalTestCases.md',
  '07-outputs/TestCases/FunctionalTestCases.xlsx',
  '07-outputs/RequirementTraceabilityMatrix/RTM.md',
  '07-outputs/TestPlan/TestPlan.md',
]

export async function POST() {
  const deleted: string[] = []
  const skipped: string[] = []

  for (const relPath of TARGETS) {
    const fullPath = path.join(ROOT, relPath)
    try {
      await fs.unlink(fullPath)
      deleted.push(relPath)
    } catch {
      // File didn't exist — nothing to clear, not an error.
      skipped.push(relPath)
    }
  }

  return NextResponse.json({ success: true, deleted, skipped })
}
