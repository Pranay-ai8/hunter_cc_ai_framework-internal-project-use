export interface Agent {
  id: string
  name: string
  description: string
  enabled: boolean
}

export interface Standard {
  id: string
  name: string
  description: string
  enabled: boolean
}

export interface Prompt {
  id: string
  name: string
  description: string
  filePath: string
  enabled: boolean
}

export interface AgentConfig {
  agents: Agent[]
  standards: Standard[]
  prompts: Prompt[]
  additionalOptions?: {
    generateRTM?: boolean
    generateRegressionSuite?: boolean
    generateSmokeSuite?: boolean
  }
}

export interface ExecutionLog {
  timestamp: string
  level: 'info' | 'success' | 'error' | 'warning'
  message: string
}

export interface ExecutionResult {
  id: string
  config: AgentConfig
  status: 'running' | 'completed' | 'failed'
  logs: ExecutionLog[]
  startTime: string
  endTime?: string
  outputFiles?: {
    name: string
    path: string
    downloadUrl: string
  }[]
  error?: string
}

export interface ExecutionHistory {
  results: ExecutionResult[]
  total: number
  page: number
}
