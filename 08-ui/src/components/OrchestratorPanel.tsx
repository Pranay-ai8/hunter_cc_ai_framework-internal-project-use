'use client'

import { useCallback, useRef, useState } from 'react'

interface FlowFile {
  path: string
  role: 'input' | 'loaded' | 'written'
  note?: string
}

interface LogLine {
  level: 'info' | 'success' | 'warning' | 'error'
  message: string
  time: string
}

interface StepState {
  step: number
  title: string
  status: 'pending' | 'active' | 'done'
}

export type OrchestratorType = 'functional' | 'testplan'

interface OrchestratorPanelProps {
  type: OrchestratorType
  title: string
  description: string
  stepTitles: string[]
  runLabel: string
}

export default function OrchestratorPanel({
  type,
  title,
  description,
  stepTitles,
  runLabel,
}: OrchestratorPanelProps) {
  const [isRunning, setIsRunning] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [steps, setSteps] = useState<StepState[]>(
    stepTitles.map((t, i) => ({ step: i + 1, title: t, status: 'pending' }))
  )
  const [logs, setLogs] = useState<LogLine[]>([])
  const [files, setFiles] = useState<FlowFile[]>([])
  const [error, setError] = useState<string>('')
  const [finished, setFinished] = useState(false)
  const logsEndRef = useRef<HTMLDivElement>(null)

  const reset = () => {
    setSteps(stepTitles.map((t, i) => ({ step: i + 1, title: t, status: 'pending' })))
    setLogs([])
    setFiles([])
    setError('')
    setFinished(false)
  }

  const executeRun = useCallback(async () => {
    reset()
    setIsRunning(true)

    try {
      const response = await fetch('/api/orchestrate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ type }),
      })
      if (!response.body) throw new Error('No response stream from server')

      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        buffer += decoder.decode(value, { stream: true })

        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          if (!line.trim()) continue
          const event = JSON.parse(line)
          const time = new Date().toLocaleTimeString()

          if (event.type === 'step') {
            setSteps((prev) =>
              prev.map((s) =>
                s.step === event.step
                  ? { ...s, status: 'active' }
                  : s.step < event.step
                  ? { ...s, status: 'done' }
                  : s
              )
            )
            setLogs((prev) => [
              ...prev,
              { level: 'info', message: `Step ${event.step}: ${event.title}`, time },
            ])
          } else if (event.type === 'log') {
            setLogs((prev) => [...prev, { level: event.level, message: event.message, time }])
          } else if (event.type === 'file') {
            setFiles((prev) => [...prev, event.file])
          } else if (event.type === 'error') {
            setError(event.error)
            setLogs((prev) => [...prev, { level: 'error', message: event.error, time }])
          } else if (event.type === 'done') {
            setSteps((prev) => prev.map((s) => ({ ...s, status: 'done' })))
            setFinished(true)
          }

          logsEndRef.current?.scrollIntoView({ behavior: 'smooth' })
        }
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error')
    } finally {
      setIsRunning(false)
    }
  }, [type])

  const handleRunClick = useCallback(() => {
    setShowConfirm(true)
  }, [])

  const handleConfirmRun = useCallback(() => {
    setShowConfirm(false)
    executeRun()
  }, [executeRun])

  const handleCancelRun = useCallback(() => {
    setShowConfirm(false)
  }, [])

  const grouped = {
    loaded: files.filter((f) => f.role === 'loaded'),
    input: files.filter((f) => f.role === 'input'),
    written: files.filter((f) => f.role === 'written'),
  }

  return (
    <div className="relative border border-blue-200 rounded bg-blue-50 flex flex-col h-full">
      <div className="border-b border-blue-200 px-4 py-3">
        <h3 className="text-sm font-semibold text-blue-900">{title}</h3>
        <p className="text-xs text-blue-700 mt-0.5">{description}</p>
        <button
          onClick={handleRunClick}
          disabled={isRunning}
          className={`mt-3 w-full text-xs font-medium px-4 py-2 rounded border ${
            isRunning
              ? 'bg-blue-100 text-blue-300 border-blue-200 cursor-not-allowed'
              : 'bg-blue-900 text-white border-blue-900 hover:bg-blue-950'
          }`}
        >
          {isRunning ? 'Running…' : runLabel}
        </button>
      </div>

      {/* Confirmation dialog */}
      {showConfirm && (
        <div className="absolute inset-0 z-10 flex items-center justify-center bg-blue-950/60 rounded">
          <div className="bg-blue-50 border border-blue-300 rounded shadow-lg w-11/12 max-w-sm p-4">
            <h4 className="text-sm font-semibold text-blue-900">Run {title}?</h4>
            <p className="text-xs text-blue-700 mt-2">
              This will call the Anthropic API and write generated report(s) to{' '}
              <span className="font-mono">07-outputs/</span>, reusing any existing
              Requirement Analysis / Gap Analysis if already present. Do you want to
              continue?
            </p>
            <div className="mt-4 flex justify-end gap-2">
              <button
                onClick={handleCancelRun}
                className="text-xs font-medium px-3 py-1.5 rounded border border-blue-300 text-blue-800 bg-blue-50 hover:bg-blue-100"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmRun}
                className="text-xs font-medium px-3 py-1.5 rounded border border-blue-900 bg-blue-900 text-white hover:bg-blue-950"
              >
                Run
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Step tracker */}
      <div className="px-4 py-3 border-b border-blue-200 flex flex-wrap gap-1.5">
        {steps.map((s) => (
          <span
            key={s.step}
            className={`text-xs px-2 py-1 rounded border ${
              s.status === 'done'
                ? 'bg-blue-200 text-blue-800 border-blue-300'
                : s.status === 'active'
                ? 'bg-blue-700 text-white border-blue-700'
                : 'bg-blue-50 text-blue-400 border-blue-200'
            }`}
          >
            {s.step}. {s.title}
          </span>
        ))}
      </div>

      {/* Log output */}
      <div className="h-56 overflow-y-auto bg-blue-100 font-mono text-xs p-3 space-y-1 border-b border-blue-200">
        {logs.length === 0 ? (
          <div className="text-blue-400">No run yet. Click &quot;{runLabel}&quot; to start.</div>
        ) : (
          logs.map((log, i) => (
            <div key={i} className="text-blue-800 flex gap-2">
              <span className="text-blue-400 whitespace-nowrap">{log.time}</span>
              <span className="uppercase text-blue-600">[{log.level}]</span>
              <span>{log.message}</span>
            </div>
          ))
        )}
        <div ref={logsEndRef} />
      </div>

      {error && (
        <div className="px-4 py-3 border-b border-blue-200 text-xs text-blue-900 bg-blue-200">
          <strong>Error:</strong> {error}
        </div>
      )}

      {/* Flow diagram: files loaded / input / written */}
      {files.length > 0 && (
        <div className="px-4 py-3 grid grid-cols-3 gap-3 text-xs">
          <div>
            <h4 className="font-semibold text-blue-800 mb-1">Input</h4>
            <ul className="space-y-0.5 text-blue-700">
              {grouped.input.map((f, i) => (
                <li key={i} className="truncate" title={f.path}>{f.path}</li>
              ))}
            </ul>
          </div>
          <div>
            <h4 className="font-semibold text-blue-800 mb-1">Loaded</h4>
            <ul className="space-y-0.5 text-blue-700">
              {grouped.loaded.map((f, i) => (
                <li key={i} className="truncate" title={f.path}>{f.path}</li>
              ))}
            </ul>
          </div>
          <div>
            <h4 className="font-semibold text-blue-800 mb-1">Written</h4>
            <ul className="space-y-0.5 text-blue-700">
              {grouped.written.map((f, i) => (
                <li key={i} className="truncate" title={f.path}>{f.path}</li>
              ))}
            </ul>
          </div>
        </div>
      )}

      {finished && !error && (
        <div className="px-4 py-2 text-xs text-blue-700 border-t border-blue-200 mt-auto">
          Done. Outputs saved under 07-outputs/.
        </div>
      )}
    </div>
  )
}
