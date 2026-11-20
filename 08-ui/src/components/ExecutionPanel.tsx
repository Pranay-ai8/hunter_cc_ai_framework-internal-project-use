'use client'

import { useEffect, useRef } from 'react'
import { ExecutionResult } from '@/types'
import { formatDistanceToNow } from 'date-fns'

interface ExecutionPanelProps {
  execution: ExecutionResult | null
  isRunning: boolean
}

export default function ExecutionPanel({
  execution,
  isRunning,
}: ExecutionPanelProps) {
  const logsEndRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    logsEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [execution?.logs])

  if (!execution) {
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
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
          />
        </svg>
        <p className="text-blue-600 text-sm">No agent execution in progress</p>
      </div>
    )
  }

  const statusColors = {
    running: 'bg-blue-100 text-blue-700',
    completed: 'bg-blue-200 text-blue-800',
    failed: 'bg-blue-300 text-blue-900',
  }

  const logColors = {
    info: 'text-blue-700',
    success: 'text-blue-800',
    error: 'text-blue-900',
    warning: 'text-blue-700',
  }

  return (
    <div className="bg-blue-50 rounded border border-blue-200 overflow-hidden">
      <div className="bg-blue-100 border-b border-blue-200 px-4 py-2.5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="relative w-2.5 h-2.5">
              {isRunning && (
                <div className="absolute inset-0 bg-blue-700 rounded-full animate-ping"></div>
              )}
              <div
                className={`absolute inset-0 bg-blue-700 rounded-full ${
                  isRunning ? 'animate-pulse' : ''
                }`}
              ></div>
            </div>
            <div>
              <h3 className="text-blue-900 text-sm font-semibold">Execution Log</h3>
              <p className="text-blue-600 text-xs">
                Started {formatDistanceToNow(new Date(execution.startTime), { addSuffix: true })}
              </p>
            </div>
          </div>
          <span
            className={`px-2 py-0.5 rounded text-xs font-medium ${
              statusColors[execution.status]
            }`}
          >
            {execution.status.charAt(0).toUpperCase() + execution.status.slice(1)}
          </span>
        </div>
      </div>

      <div className="h-96 overflow-y-auto bg-blue-100 font-mono text-xs p-3 space-y-1">
        {execution.logs.length === 0 ? (
          <div className="text-blue-400">Waiting for logs...</div>
        ) : (
          execution.logs.map((log, index) => (
            <div key={index} className={`${logColors[log.level]} flex gap-2`}>
              <span className="text-blue-400 whitespace-nowrap">
                {new Date(log.timestamp).toLocaleTimeString()}
              </span>
              <span className="font-semibold">[{log.level.toUpperCase()}]</span>
              <span>{log.message}</span>
            </div>
          ))
        )}
        <div ref={logsEndRef} />
      </div>

      {execution.error && (
        <div className="bg-blue-100 border-t border-blue-200 p-3">
          <div className="text-blue-900 text-xs">
            <strong>Error:</strong> {execution.error}
          </div>
        </div>
      )}

      {execution.outputFiles && execution.outputFiles.length > 0 && (
        <div className="bg-blue-100 border-t border-blue-200 p-3">
          <h4 className="font-semibold text-blue-800 text-sm mb-2">Generated Files</h4>
          <div className="space-y-1">
            {execution.outputFiles.map((file, index) => (
              <a
                key={index}
                href={file.downloadUrl}
                download={file.name}
                className="flex items-center gap-2 text-blue-700 hover:text-blue-900 transition text-xs"
              >
                <svg
                  className="w-3.5 h-3.5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                  />
                </svg>
                <span>{file.name}</span>
              </a>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
