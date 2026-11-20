import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { Agent, Standard, Prompt, ExecutionResult, AgentConfig } from '@/types'
import { AGENTS, STANDARDS, PROMPTS } from '@/config/framework'

interface AgentStore {
  agents: Agent[]
  standards: Standard[]
  prompts: Prompt[]
  executionHistory: ExecutionResult[]
  currentExecution: ExecutionResult | null
  additionalOptions: {
    generateRTM: boolean
    generateRegressionSuite: boolean
    generateSmokeSuite: boolean
  }

  toggleAgent: (id: string) => void
  toggleStandard: (id: string) => void
  togglePrompt: (id: string) => void
  toggleOption: (key: keyof AgentStore['additionalOptions']) => void

  selectAllAgents: () => void
  selectAllStandards: () => void
  selectAllPrompts: () => void
  deselectAllAgents: () => void
  deselectAllStandards: () => void
  deselectAllPrompts: () => void

  getSelectedConfig: () => AgentConfig
  setCurrentExecution: (execution: ExecutionResult | null) => void
  addExecutionToHistory: (execution: ExecutionResult) => void
  resetSelections: () => void
}

const useAgentStore = create<AgentStore>()(
  persist(
    (set, get) => ({
      agents: AGENTS,
      standards: STANDARDS,
      prompts: PROMPTS,
      executionHistory: [],
      currentExecution: null,
      additionalOptions: {
        generateRTM: true,
        generateRegressionSuite: true,
        generateSmokeSuite: true,
      },

      toggleAgent: (id: string) =>
        set((state) => ({
          agents: state.agents.map((agent) =>
            agent.id === id ? { ...agent, enabled: !agent.enabled } : agent
          ),
        })),

      toggleStandard: (id: string) =>
        set((state) => ({
          standards: state.standards.map((standard) =>
            standard.id === id
              ? { ...standard, enabled: !standard.enabled }
              : standard
          ),
        })),

      togglePrompt: (id: string) =>
        set((state) => ({
          prompts: state.prompts.map((prompt) =>
            prompt.id === id ? { ...prompt, enabled: !prompt.enabled } : prompt
          ),
        })),

      toggleOption: (key: keyof AgentStore['additionalOptions']) =>
        set((state) => ({
          additionalOptions: {
            ...state.additionalOptions,
            [key]: !state.additionalOptions[key],
          },
        })),

      selectAllAgents: () =>
        set((state) => ({
          agents: state.agents.map((agent) => ({ ...agent, enabled: true })),
        })),

      selectAllStandards: () =>
        set((state) => ({
          standards: state.standards.map((standard) => ({
            ...standard,
            enabled: true,
          })),
        })),

      selectAllPrompts: () =>
        set((state) => ({
          prompts: state.prompts.map((prompt) => ({ ...prompt, enabled: true })),
        })),

      deselectAllAgents: () =>
        set((state) => ({
          agents: state.agents.map((agent) => ({ ...agent, enabled: false })),
        })),

      deselectAllStandards: () =>
        set((state) => ({
          standards: state.standards.map((standard) => ({
            ...standard,
            enabled: false,
          })),
        })),

      deselectAllPrompts: () =>
        set((state) => ({
          prompts: state.prompts.map((prompt) => ({ ...prompt, enabled: false })),
        })),

      getSelectedConfig: () => {
        const state = get()
        return {
          agents: state.agents.filter((a) => a.enabled),
          standards: state.standards.filter((s) => s.enabled),
          prompts: state.prompts.filter((p) => p.enabled),
          additionalOptions: state.additionalOptions,
        }
      },

      setCurrentExecution: (execution: ExecutionResult | null) =>
        set({ currentExecution: execution }),

      addExecutionToHistory: (execution: ExecutionResult) =>
        set((state) => ({
          executionHistory: [execution, ...state.executionHistory],
        })),

      resetSelections: () =>
        set((state) => ({
          agents: state.agents.map((agent) => ({ ...agent, enabled: true })),
          standards: state.standards.map((standard) => ({
            ...standard,
            enabled: true,
          })),
          prompts: state.prompts.map((prompt) => ({ ...prompt, enabled: true })),
        })),
    }),
    {
      name: 'agent-store',
    }
  )
)

export default useAgentStore
