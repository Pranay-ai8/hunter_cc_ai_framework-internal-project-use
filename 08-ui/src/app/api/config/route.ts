import { NextResponse } from 'next/server'
import { AGENTS, STANDARDS, PROMPTS } from '@/config/framework'

export async function GET() {
  return NextResponse.json({
    agents: AGENTS,
    standards: STANDARDS,
    prompts: PROMPTS,
  })
}
