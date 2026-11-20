'use client'

import { useState } from 'react'
import useAgentStore from '@/store/agentStore'
import { ExecutionResult } from '@/types'
import { formatDistanceToNow } from 'date-fns'

interface HistoryPanelProps {
  onSelectExecution: (execution: ExecutionResult) => void
}

export default function HistoryPanel({
  onSelectExecution,
}: HistoryPanelProps) {
  const { executionHistory } = useAgentStore()
  const [expanded, setExpanded] = useState(true)

  if (executionHistory.length === 0) {
    return (
      <div className="bg-blue-50 rounded border border-blue-200 p-6 text-center">
        <svg
          className="w-10 h-10 mx-auto text-blue-300 mb-2"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
        <p className="text-blue-600 text-sm">No execution history yet</p>
      </div>
    )
  }

  const statusBadgeColor = (status: string) => {
    switch (status) {
      case 'completed':
        return 'bg-blue-200 text-blue-800'
      case 'failed':
        return 'bg-blue-300 text-blue-900'
      case 'running':
        return 'bg-blue-100 text-blue-700'
      default:
        return 'bg-blue-100 text-blue-900'
    }
  }

  return (
    <div className="bg-blue-50 rounded border border-blue-200 overflow-hidden">
      <div className="bg-blue-100 border-b border-blue-200 px-4 py-2.5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <button
              onClick={() => setExpanded(!expanded)}
              className="text-blue-600 hover:bg-blue-200 p-1 rounded transition"
            >
              <svg
                className={`w-4 h-4 transform transition ${
                  expanded ? 'rotate-180' : ''
                }`}
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M19 14l-7 7m0 0l-7-7m7 7V3"
                />
              </svg>
            </button>
            <div>
              <h3 className="text-blue-900 text-sm font-semibold">Execution History</h3>
              <p className="text-blue-600 text-xs">
                {executionHistory.length} executions
              </p>
            </div>
          </div>
        </div>
      </div>

      {expanded && (
        <div className="max-h-96 overflow-y-auto">
          <div className="divide-y">
            {executionHistory.map((execution) => (
              <div
                key={execution.id}
                onClick={() => onSelectExecution(execution)}
                className="p-3 hover:bg-blue-100 cursor-pointer transition border-l-2 border-blue-100"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <h4 className="text-sm font-semibold text-blue-900">
                        Execution #{execution.id.slice(0, 8)}
                      </h4>
                      <span
                        className={`px-2 py-0.5 rounded text-xs font-medium ${statusBadgeColor(
                          execution.status
                        )}`}
                      >
                        {execution.status}
                      </span>
                    </div>
                    <p className="text-xs text-blue-700">
                      {execution.config.agents.length} agents •{' '}
                      {execution.config.standards.length} standards
                    </p>
                    <p className="text-xs text-blue-400 mt-1">
                      {formatDistanceToNow(new Date(execution.startTime), {
                        addSuffix: true,
                      })}
                    </p>
                  </div>
                  <svg
                    className="w-4 h-4 text-blue-400"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
