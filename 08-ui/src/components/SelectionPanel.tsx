'use client'

import { useState } from 'react'
import { Agent, Standard, Prompt } from '@/types'

interface SelectionPanelProps {
  title: string
  items: (Agent | Standard | Prompt)[]
  onToggle: (id: string) => void
  onSelectAll: () => void
  onDeselectAll: () => void
}

export default function SelectionPanel({
  title,
  items,
  onToggle,
  onSelectAll,
  onDeselectAll,
}: SelectionPanelProps) {
  const [expanded, setExpanded] = useState(true)
  const selectedCount = items.filter((item) => item.enabled).length

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
              <h3 className="text-blue-900 text-sm font-semibold">{title}</h3>
              <p className="text-blue-600 text-xs">
                {selectedCount} of {items.length} selected
              </p>
            </div>
          </div>
          <div className="flex gap-2">
            <button
              onClick={onSelectAll}
              className="px-2 py-1 text-xs bg-blue-50 border border-blue-200 hover:bg-blue-100 text-blue-700 rounded transition"
            >
              All
            </button>
            <button
              onClick={onDeselectAll}
              className="px-2 py-1 text-xs bg-blue-50 border border-blue-200 hover:bg-blue-100 text-blue-700 rounded transition"
            >
              None
            </button>
          </div>
        </div>
      </div>

      {expanded && (
        <div className="p-3 space-y-1 max-h-96 overflow-y-auto">
          {items.map((item) => (
            <label
              key={item.id}
              className="flex items-start gap-2 p-2 hover:bg-blue-100 rounded cursor-pointer transition"
            >
              <input
                type="checkbox"
                checked={item.enabled}
                onChange={() => onToggle(item.id)}
                className="w-4 h-4 mt-0.5 cursor-pointer text-blue-700 rounded focus:ring-1 focus:ring-blue-500"
              />
              <div className="flex-1">
                <div className="text-sm font-medium text-blue-900">{item.name}</div>
                <div className="text-xs text-blue-600">{item.description}</div>
              </div>
            </label>
          ))}
        </div>
      )}
    </div>
  )
}
