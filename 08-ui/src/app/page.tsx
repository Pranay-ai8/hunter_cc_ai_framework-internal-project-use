'use client'

import { useState, useCallback } from 'react'
import useAgentStore from '@/store/agentStore'
import SelectionPanel from '@/components/SelectionPanel'
import AdditionalOptions from '@/components/AdditionalOptions'
import ExecutionPanel from '@/components/ExecutionPanel'
import HistoryPanel from '@/components/HistoryPanel'
import UploadPanel from '@/components/UploadPanel'
import MasterOrchestratorSection from '@/components/MasterOrchestratorSection'
import { ExecutionResult, ExecutionLog } from '@/types'
import { UploadResult } from '@/types/upload'

export default function Home() {
  const {
    agents,
    standards,
    prompts,
    toggleAgent,
    toggleStandard,
    togglePrompt,
    selectAllAgents,
    deselectAllAgents,
    selectAllStandards,
    deselectAllStandards,
    selectAllPrompts,
    deselectAllPrompts,
    getSelectedConfig,
    addExecutionToHistory,
    setCurrentExecution,
    currentExecution,
  } = useAgentStore()

  const [isRunning, setIsRunning] = useState(false)
  const [isClearing, setIsClearing] = useState(false)
  const [selectedTab, setSelectedTab] = useState<
    'orchestrator' | 'config' | 'execution' | 'history' | 'upload'
  >('orchestrator')
  const [uploadedDocs, setUploadedDocs] = useState<UploadResult[]>([])

  const createMockExecution = (config: ReturnType<typeof getSelectedConfig>) => {
    const executionId = Math.random().toString(36).substring(2, 10)
    const execution: ExecutionResult = {
      id: executionId,
      config,
      status: 'running',
      logs: [
        {
          timestamp: new Date().toISOString(),
          level: 'info',
          message: 'Agent execution started',
        },
      ],
      startTime: new Date().toISOString(),
      outputFiles: [],
    }
    return execution
  }

  const simulateAgentExecution = useCallback(
    async (execution: ExecutionResult) => {
      const logs: ExecutionLog[] = [...execution.logs]

      const agentNames = execution.config.agents.map((a) => a.name)
      const standardNames = execution.config.standards.map((s) => s.name)

      const steps = [
        { delay: 500, message: 'Initializing agents...' },
        { delay: 800, message: `Loading agents: ${agentNames.join(', ')}` },
        { delay: 600, message: `Applying standards: ${standardNames.join(', ')}` },
        { delay: 1200, message: 'Analyzing requirements...' },
        { delay: 1000, message: 'Generating test artifacts...' },
        { delay: 800, message: 'Creating test cases...' },
        { delay: 600, message: 'Building automation scripts...' },
        { delay: 900, message: 'Generating RTM...' },
        { delay: 700, message: 'Finalizing outputs...' },
        { delay: 400, message: 'Agent execution completed successfully' },
      ]

      for (const step of steps) {
        await new Promise((resolve) => setTimeout(resolve, step.delay))

        const newLog: ExecutionLog = {
          timestamp: new Date().toISOString(),
          level: Math.random() > 0.9 ? 'warning' : 'success',
          message: step.message,
        }
        logs.push(newLog)

        setCurrentExecution({
          ...execution,
          logs,
        })
      }

      const completedExecution: ExecutionResult = {
        ...execution,
        status: 'completed',
        endTime: new Date().toISOString(),
        logs,
        outputFiles: [
          {
            name: 'TestCases.md',
            path: '/outputs/test-cases.md',
            downloadUrl: '#',
          },
          {
            name: 'RTM.xlsx',
            path: '/outputs/rtm.xlsx',
            downloadUrl: '#',
          },
          {
            name: 'AutomationScripts.ts',
            path: '/outputs/automation-scripts.ts',
            downloadUrl: '#',
          },
        ],
      }

      addExecutionToHistory(completedExecution)
      setCurrentExecution(completedExecution)
      setIsRunning(false)
    },
    [addExecutionToHistory, setCurrentExecution]
  )

  const handleRunAgent = useCallback(async () => {
    const selectedCount =
      agents.filter((a) => a.enabled).length +
      standards.filter((s) => s.enabled).length +
      prompts.filter((p) => p.enabled).length

    if (selectedCount === 0) {
      alert(
        'Please select at least one agent, standard, or prompt before running'
      )
      return
    }

    setIsRunning(true)
    const config = getSelectedConfig()
    const execution = createMockExecution(config)

    setCurrentExecution(execution)
    setSelectedTab('execution')

    await simulateAgentExecution(execution)
  }, [
    agents,
    standards,
    prompts,
    getSelectedConfig,
    simulateAgentExecution,
    setCurrentExecution,
  ])

  const handleSelectExecution = (execution: ExecutionResult) => {
    setCurrentExecution(execution)
    setSelectedTab('execution')
  }

  const handleClearSession = useCallback(async () => {
    const confirmed = window.confirm(
      'This will delete the reports generated in this session (RequirementAnalysis.md, GapAnalysis.md/.xlsx, FunctionalTestCases.md/.xlsx, RTM.md, TestPlan.md) and clear the dashboard\'s cached execution history and context. Folder structure and other files are left untouched. Continue?'
    )
    if (!confirmed) return

    setIsClearing(true)
    try {
      await fetch('/api/clear-session', { method: 'POST' })
    } catch {
      // Even if the file cleanup request fails, still clear local cache below.
    } finally {
      window.localStorage.removeItem('agent-store')
      window.location.reload()
    }
  }, [])

  return (
    <div className="min-h-screen bg-[#050a14] py-6 px-4">
      <div className="max-w-5xl mx-auto">
        {/* Header */}
        <div className="mb-4 flex items-start justify-between gap-4">
          <div>
            <h1 className="text-lg font-semibold text-blue-50">
              QA Framework Master Agent
            </h1>
            <p className="text-blue-300 text-xs">
              Configure and run your testing agents
            </p>
          </div>
          <button
            onClick={handleClearSession}
            disabled={isClearing}
            title="Delete reports generated in this session and clear cached execution history/context. Does not touch folder structure or other files."
            className={`text-xs font-medium px-3 py-2 rounded border whitespace-nowrap ${
              isClearing
                ? 'bg-blue-950 text-blue-700 border-blue-800 cursor-not-allowed'
                : 'bg-blue-900 text-blue-50 border-blue-800 hover:bg-blue-950'
            }`}
          >
            {isClearing ? 'Clearing…' : 'Clear Session'}
          </button>
        </div>

        {/* Tab Navigation */}
        <div className="bg-blue-950 border border-blue-800 rounded mb-4 flex overflow-x-auto">
          {[
            { id: 'orchestrator', label: 'Orchestrator' },
            { id: 'config', label: 'Configuration' },
            { id: 'execution', label: 'Execution' },
            { id: 'history', label: 'History' },
            { id: 'upload', label: 'Upload Docs' },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() =>
                setSelectedTab(
                  tab.id as 'orchestrator' | 'config' | 'execution' | 'history' | 'upload'
                )
              }
              className={`px-4 py-2 text-xs font-medium transition whitespace-nowrap border-r border-blue-800 last:border-r-0 ${
                selectedTab === tab.id
                  ? 'bg-blue-900 text-blue-50'
                  : 'bg-blue-950 text-blue-300 hover:bg-blue-900'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Content Area */}
        <div className="grid gap-4">
          {selectedTab === 'orchestrator' && <MasterOrchestratorSection />}

          {selectedTab === 'config' && (
            <>
              <div className="grid md:grid-cols-2 gap-4">
                <SelectionPanel
                  title="Agents"
                  items={agents}
                  onToggle={toggleAgent}
                  onSelectAll={selectAllAgents}
                  onDeselectAll={deselectAllAgents}
                />
                <SelectionPanel
                  title="Standards"
                  items={standards}
                  onToggle={toggleStandard}
                  onSelectAll={selectAllStandards}
                  onDeselectAll={deselectAllStandards}
                />
              </div>

              <SelectionPanel
                title="Prompts"
                items={prompts}
                onToggle={togglePrompt}
                onSelectAll={selectAllPrompts}
                onDeselectAll={deselectAllPrompts}
              />

              <AdditionalOptions />

              {/* Run Button */}
              <div className="bg-blue-50 border border-blue-200 rounded p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-blue-900 text-sm font-semibold">
                      Ready to run your agents?
                    </h3>
                    <p className="text-blue-600 text-xs mt-1">
                      {agents.filter((a) => a.enabled).length} agents •{' '}
                      {standards.filter((s) => s.enabled).length} standards •{' '}
                      {prompts.filter((p) => p.enabled).length} prompts selected
                    </p>
                  </div>
                  <button
                    onClick={handleRunAgent}
                    disabled={isRunning}
                    className={`px-4 py-2 rounded border text-xs font-medium transition ${
                      isRunning
                        ? 'bg-blue-100 text-blue-400 border-blue-200 cursor-not-allowed'
                        : 'bg-blue-900 text-white border-blue-900 hover:bg-blue-950'
                    }`}
                  >
                    {isRunning ? 'Running…' : 'Run Agent'}
                  </button>
                </div>
              </div>
            </>
          )}

          {selectedTab === 'execution' && (
            <ExecutionPanel execution={currentExecution} isRunning={isRunning} />
          )}

          {selectedTab === 'history' && (
            <HistoryPanel onSelectExecution={handleSelectExecution} />
          )}

          {selectedTab === 'upload' && (
            <div className="space-y-4">
              <UploadPanel
                onUploadSuccess={(result: UploadResult) => {
                  setUploadedDocs([result, ...uploadedDocs.slice(0, 9)])
                }}
              />

              {/* Recently Uploaded Documents Summary */}
              {uploadedDocs.length > 0 && (
                <div className="bg-blue-50 rounded border border-blue-200 overflow-hidden">
                  <div className="border-b border-blue-200 px-4 py-3">
                    <h3 className="text-blue-900 text-sm font-semibold">
                      Recently Uploaded Documents
                    </h3>
                    <p className="text-blue-600 text-xs">
                      {uploadedDocs.length} documents uploaded in this session
                    </p>
                  </div>

                  <div className="p-4">
                    <div className="space-y-2">
                      {uploadedDocs.map((doc, index) => (
                        <div
                          key={index}
                          className="flex items-center justify-between p-2 bg-blue-100 rounded border border-blue-100 text-xs"
                        >
                          <div className="flex-1">
                            <p className="font-medium text-blue-900">
                              {doc.fileName}
                            </p>
                            <p className="text-blue-600 mt-0.5">
                              Category: {doc.category}
                            </p>
                          </div>
                          <div className="flex items-center gap-3">
                            <span className="px-2 py-1 bg-blue-200 text-blue-800 rounded font-medium">
                              {doc.category}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
