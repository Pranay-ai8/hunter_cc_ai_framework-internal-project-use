'use client'

import useAgentStore from '@/store/agentStore'
import { useState } from 'react'

export default function AdditionalOptions() {
  const [expanded, setExpanded] = useState(true)
  const { additionalOptions, toggleOption } = useAgentStore()

  const options = [
    {
      key: 'generateRTM' as const,
      label: 'Generate RTM',
      description: 'Generate Requirement Traceability Matrix',
    },
    {
      key: 'generateRegressionSuite' as const,
      label: 'Generate Regression Suite',
      description: 'Generate regression test suite',
    },
    {
      key: 'generateSmokeSuite' as const,
      label: 'Generate Smoke Suite',
      description: 'Generate smoke test suite',
    },
  ]

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
            <h3 className="text-blue-900 text-sm font-semibold">Additional Options</h3>
          </div>
        </div>
      </div>

      {expanded && (
        <div className="p-3 space-y-1">
          {options.map(({ key, label, description }) => (
            <label
              key={key}
              className="flex items-start gap-2 p-2 hover:bg-blue-100 rounded cursor-pointer transition"
            >
              <input
                type="checkbox"
                checked={additionalOptions[key]}
                onChange={() => toggleOption(key)}
                className="w-4 h-4 mt-0.5 cursor-pointer text-blue-700 rounded focus:ring-1 focus:ring-blue-500"
              />
              <div className="flex-1">
                <div className="text-sm font-medium text-blue-900">{label}</div>
                <div className="text-xs text-blue-600">{description}</div>
              </div>
            </label>
          ))}
        </div>
      )}
    </div>
  )
}
