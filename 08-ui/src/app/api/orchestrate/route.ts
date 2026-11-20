import { NextRequest, NextResponse } from 'next/server'
import {
  runFunctionalTestCaseOrchestrator,
  runTestPlanOrchestrator,
} from '@/lib/orchestrator'

export const runtime = 'nodejs'
export const dynamic = 'force-dynamic'

export type OrchestratorType = 'functional' | 'testplan'

export async function POST(request: NextRequest) {
  let type: OrchestratorType = 'functional'
  try {
    const body = await request.json()
    if (body?.type === 'testplan') type = 'testplan'
  } catch {
    // No body / invalid JSON — default to the functional orchestrator.
  }

  const encoder = new TextEncoder()
  const run = type === 'testplan' ? runTestPlanOrchestrator : runFunctionalTestCaseOrchestrator

  const stream = new ReadableStream({
    async start(controller) {
      try {
        for await (const event of run()) {
          controller.enqueue(encoder.encode(JSON.stringify(event) + '\n'))
        }
      } catch (error) {
        const message = error instanceof Error ? error.message : 'Unknown error'
        controller.enqueue(
          encoder.encode(JSON.stringify({ type: 'error', error: message }) + '\n')
        )
      } finally {
        controller.close()
      }
    },
  })

  return new NextResponse(stream, {
    headers: {
      'Content-Type': 'application/x-ndjson',
      'Cache-Control': 'no-cache',
    },
  })
}
