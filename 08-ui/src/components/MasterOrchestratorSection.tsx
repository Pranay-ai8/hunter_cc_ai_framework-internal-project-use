'use client'

import OrchestratorPanel from '@/components/OrchestratorPanel'

const FUNCTIONAL_STEPS = [
  'Load Global Instructions',
  'Validate Inputs',
  'Requirement Analysis',
  'Gap Analysis',
  'Load Framework Resources',
  'Execute Functional Agent',
  'Save Outputs',
]

const TESTPLAN_STEPS = [
  'Load Global Instructions',
  'Validate Inputs',
  'Requirement Analysis',
  'Gap Analysis',
  'Load Framework Resources',
  'Execute Test Plan Agent',
  'Save Outputs',
]

export default function MasterOrchestratorSection() {
  return (
    <div className="border border-blue-800 rounded bg-blue-950">
      {/* Master QA Orchestrator — parent header */}
      <div className="border-b border-blue-800 px-4 py-3 bg-blue-900 rounded-t">
        <h2 className="text-sm font-semibold text-blue-50">Master QA Orchestrator</h2>
        <p className="text-xs text-blue-200 mt-0.5">
          Select which orchestration pipeline to run. Both share the same Requirement
          Analysis and Gap Analysis foundation before generating their own artifact.
        </p>
      </div>

      {/* Aligned sub-orchestrators */}
      <div className="p-4 grid md:grid-cols-2 gap-4 items-start">
        <OrchestratorPanel
          type="functional"
          title="Functional Test Orchestrator"
          description="Generates Requirement Analysis → Gap Analysis → Functional Test Cases from 01-docs."
          stepTitles={FUNCTIONAL_STEPS}
          runLabel="Run Functional Test Orchestrator"
        />
        <OrchestratorPanel
          type="testplan"
          title="Test Plan Orchestrator"
          description="Generates Requirement Analysis → Gap Analysis → Test Plan from 01-docs."
          stepTitles={TESTPLAN_STEPS}
          runLabel="Run Test Plan Orchestrator"
        />
      </div>
    </div>
  )
}
