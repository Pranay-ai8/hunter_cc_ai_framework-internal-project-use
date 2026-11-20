# Integration Guide: Connecting to Master Agent

This guide explains how to integrate the UI with your actual master agent implementation.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                          React UI (Browser)                     │
│  - Agent/Standard/Prompt Selection                              │
│  - Real-time Log Display                                        │
│  - Execution History                                            │
│  - File Downloads                                               │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/WebSocket
                             ↓
┌─────────────────────────────────────────────────────────────────┐
│                  Next.js API Routes (Node.js)                   │
│  - /api/config - Get available agents                           │
│  - /api/execute - Execute agents                                │
│  - /api/download - Download files                               │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────┴────────┐
                    ↓                 ↓
           ┌──────────────────┐  ┌──────────────────┐
           │  Master Agent    │  │  File Storage    │
           │  (Python/Node)   │  │  (/public/out)   │
           └──────────────────┘  └──────────────────┘
```

## Step 1: Update API Execute Endpoint

Replace `/src/app/api/execute/route.ts` with actual master agent execution:

```typescript
import { NextRequest, NextResponse } from 'next/server'
import { AgentConfig, ExecutionResult } from '@/types'
import path from 'path'
import fs from 'fs/promises'

// Import your master agent module
// import { MasterAgent } from '../../../path/to/master-agent'

interface ExecuteRequest {
  config: AgentConfig
}

function buildMasterPrompt(config: AgentConfig): string {
  // Build a comprehensive prompt that includes:
  // 1. Selected agents and their context
  // 2. Standards to apply
  // 3. Prompts to use
  // 4. Any additional instructions

  const agentsList = config.agents.map((a) => `- ${a.name}`).join('\n')
  const standardsList = config.standards.map((s) => `- ${s.name}`).join('\n')
  const promptsList = config.prompts.map((p) => `- ${p.name}`).join('\n')

  return `
You are the QA Framework Master Agent. Execute the following:

AGENTS TO RUN:
${agentsList}

APPLY THESE STANDARDS:
${standardsList}

USE THESE PROMPTS:
${promptsList}

ADDITIONAL OPTIONS:
- Generate RTM: ${config.additionalOptions?.generateRTM ? 'Yes' : 'No'}
- Generate Regression Suite: ${config.additionalOptions?.generateRegressionSuite ? 'Yes' : 'No'}
- Generate Smoke Suite: ${config.additionalOptions?.generateSmokeSuite ? 'Yes' : 'No'}

Execute all agents and provide comprehensive test artifacts.
  `.trim()
}

async function callMasterAgent(prompt: string): Promise<string> {
  // Option 1: Call your Python master agent via subprocess
  // const { spawn } = require('child_process')
  // const agent = spawn('python', ['path/to/master_agent.py'])

  // Option 2: Call Claude API directly
  // const response = await fetch('https://api.anthropic.com/v1/messages', {
  //   method: 'POST',
  //   headers: {
  //     'x-api-key': process.env.ANTHROPIC_API_KEY,
  //     'content-type': 'application/json',
  //   },
  //   body: JSON.stringify({
  //     model: 'claude-opus-4-8',
  //     max_tokens: 8000,
  //     messages: [{ role: 'user', content: prompt }],
  //   }),
  // })
  // const data = await response.json()
  // return data.content[0].text

  // Option 3: Call existing agent API
  // const response = await fetch('http://localhost:5000/execute', {
  //   method: 'POST',
  //   headers: { 'content-type': 'application/json' },
  //   body: JSON.stringify({ prompt }),
  // })
  // return await response.text()

  // Placeholder
  throw new Error('Implement master agent call')
}

async function parseAgentOutput(
  output: string
): Promise<{ logs: string[]; files: string[] }> {
  // Parse agent output to extract:
  // 1. Generated file paths
  // 2. Execution logs
  // 3. Any errors or warnings

  const logs = output.split('\n').filter((line) => line.trim())
  const files: string[] = []

  // Look for file generation messages
  // Example: "Generated file: /outputs/test-cases.md"
  const filePattern = /Generated file: (.+)/g
  let match
  while ((match = filePattern.exec(output)) !== null) {
    files.push(match[1])
  }

  return { logs, files }
}

export async function POST(request: NextRequest) {
  try {
    const body: ExecuteRequest = await request.json()
    const { config } = body

    if (!config || config.agents.length === 0) {
      return NextResponse.json(
        { error: 'No agents selected' },
        { status: 400 }
      )
    }

    const executionId = Date.now().toString(36)
    const startTime = new Date().toISOString()

    try {
      // Step 1: Build master prompt
      const masterPrompt = buildMasterPrompt(config)

      // Step 2: Call master agent
      const output = await callMasterAgent(masterPrompt)

      // Step 3: Parse output
      const { logs, files } = await parseAgentOutput(output)

      // Step 4: Build file metadata
      const outputFiles = files.map((filePath) => ({
        name: path.basename(filePath),
        path: filePath,
        downloadUrl: `/api/download?file=${encodeURIComponent(filePath)}`,
      }))

      const result: ExecutionResult = {
        id: executionId,
        config,
        status: 'completed',
        logs: logs.map((message, index) => ({
          timestamp: new Date().toISOString(),
          level: message.includes('error')
            ? 'error'
            : message.includes('warning')
              ? 'warning'
              : message.includes('✓') || message.includes('Success')
                ? 'success'
                : 'info',
          message,
        })),
        startTime,
        endTime: new Date().toISOString(),
        outputFiles,
      }

      return NextResponse.json(result)
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : 'Unknown error'

      const result: ExecutionResult = {
        id: executionId,
        config,
        status: 'failed',
        logs: [
          {
            timestamp: new Date().toISOString(),
            level: 'error',
            message: `Execution failed: ${errorMessage}`,
          },
        ],
        startTime,
        endTime: new Date().toISOString(),
        error: errorMessage,
      }

      return NextResponse.json(result, { status: 500 })
    }
  } catch (error) {
    console.error('API error:', error)
    return NextResponse.json(
      { error: 'Failed to process request' },
      { status: 500 }
    )
  }
}
```

## Step 2: Implement File Download Endpoint

Create `/src/app/api/download/route.ts`:

```typescript
import { NextRequest, NextResponse } from 'next/server'
import path from 'path'
import fs from 'fs/promises'

export async function GET(request: NextRequest) {
  try {
    const file = request.nextUrl.searchParams.get('file')

    if (!file) {
      return NextResponse.json(
        { error: 'File path required' },
        { status: 400 }
      )
    }

    // Security: Validate file path to prevent directory traversal
    const outputDir = path.join(process.cwd(), 'public', 'outputs')
    const filePath = path.resolve(outputDir, file)

    if (!filePath.startsWith(outputDir)) {
      return NextResponse.json(
        { error: 'Invalid file path' },
        { status: 403 }
      )
    }

    const fileContent = await fs.readFile(filePath)
    const filename = path.basename(filePath)

    return new NextResponse(fileContent, {
      headers: {
        'Content-Disposition': `attachment; filename="${filename}"`,
        'Content-Type': 'application/octet-stream',
      },
    })
  } catch (error) {
    console.error('Download error:', error)
    return NextResponse.json(
      { error: 'File not found' },
      { status: 404 }
    )
  }
}
```

## Step 3: Implement WebSocket Streaming (Optional)

For real-time log streaming without polling, implement WebSocket:

```typescript
// /src/app/api/execute-stream/route.ts
import { NextRequest } from 'next/server'

export async function POST(request: NextRequest) {
  const body = await request.json()
  const { config } = body

  // Use ReadableStream for Server-Sent Events (SSE)
  const stream = new ReadableStream({
    async start(controller) {
      try {
        // Build prompt
        const prompt = buildMasterPrompt(config)

        // Start agent execution
        const agentStream = await startAgentExecution(prompt)

        // Stream logs back to client
        for await (const chunk of agentStream) {
          controller.enqueue(
            new TextEncoder().encode(`data: ${JSON.stringify(chunk)}\n\n`)
          )
        }

        controller.close()
      } catch (error) {
        controller.error(error)
      }
    },
  })

  return new Response(stream, {
    headers: {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      Connection: 'keep-alive',
    },
  })
}
```

## Step 4: Update UI to Use Real Backend

In `/src/app/page.tsx`, replace the mock execution with API calls:

```typescript
const handleRunAgent = async () => {
  const config = getSelectedConfig()

  if (config.agents.length === 0) {
    alert('Please select at least one agent')
    return
  }

  setIsRunning(true)

  try {
    // Call your API
    const response = await fetch('/api/execute', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ config }),
    })

    if (!response.ok) {
      throw new Error('Execution failed')
    }

    const result: ExecutionResult = await response.json()
    addExecutionToHistory(result)
    setCurrentExecution(result)
  } catch (error) {
    alert(`Error: ${error instanceof Error ? error.message : 'Unknown error'}`)
  } finally {
    setIsRunning(false)
  }
}
```

## Step 5: Connect to Master Agent

Your master agent should:

1. **Accept Configuration**
   - List of agents to execute
   - Standards to apply
   - Prompts to use

2. **Execute Agents**
   - Run each selected agent in sequence or parallel
   - Apply standards to output
   - Combine results

3. **Generate Output**
   - Test cases (Markdown/Excel)
   - RTM (Excel)
   - Automation scripts (TypeScript/Python)
   - Report summaries

4. **Report Progress**
   - Log each step
   - Report file generation
   - Handle errors gracefully

### Example Master Agent Structure (Python)

```python
# master_agent.py
import json
from typing import List

class MasterAgent:
    def __init__(self, config: dict):
        self.agents = config['agents']
        self.standards = config['standards']
        self.prompts = config['prompts']

    def execute(self):
        logs = []

        for agent in self.agents:
            logs.append(f"Executing {agent['name']}...")

            # Load prompt
            prompt_path = agent.get('promptPath')
            prompt = self.load_prompt(prompt_path)

            # Execute via Claude API or local logic
            output = self.run_agent(prompt)

            # Apply standards
            formatted_output = self.apply_standards(output)

            # Save to file
            output_file = self.save_output(formatted_output, agent['id'])
            logs.append(f"Generated file: {output_file}")

        return logs

    def load_prompt(self, path):
        with open(path) as f:
            return f.read()

    def run_agent(self, prompt):
        # Call Claude API or your agent logic
        pass

    def apply_standards(self, output):
        # Format according to selected standards
        pass

    def save_output(self, output, agent_id):
        # Save to /outputs directory
        pass

if __name__ == '__main__':
    config = json.loads(input())
    agent = MasterAgent(config)
    logs = agent.execute()
    for log in logs:
        print(log)
```

## Step 6: Configuration Management

Create environment variables for your integration:

```bash
# .env.local
ANTHROPIC_API_KEY=sk-ant-...
MASTER_AGENT_PYTHON_PATH=/path/to/master_agent.py
OUTPUT_DIR=/path/to/outputs
```

## Testing the Integration

1. **Start the UI**
   ```bash
   npm run dev
   ```

2. **Select agents and standards**
   - Choose at least one agent

3. **Click Run Agent**
   - Watch real-time logs
   - Verify files are generated

4. **Download artifacts**
   - Test file downloads work

5. **Check execution history**
   - Verify history is saved

## Troubleshooting

### Execution Times Out
- Increase Next.js timeout: `vercel.json` maxDuration
- Implement streaming for long operations

### Files Not Downloading
- Verify file path is correct
- Check `/public/outputs/` permissions
- Ensure file exists after generation

### Logs Not Showing
- Verify logs are being captured
- Check format matches ExecutionLog type
- Use timestamps for each log entry

### Agent Execution Fails
- Check master agent logs
- Verify Claude API key is set
- Test agent locally first

## Performance Considerations

1. **Parallel Agent Execution**
   - Run agents in parallel to reduce total time
   - Use Promise.all() for non-dependent agents

2. **Streaming Large Outputs**
   - Use Server-Sent Events for real-time logs
   - Don't load entire output into memory

3. **File Storage**
   - Implement cleanup for old output files
   - Consider using cloud storage (S3, GCS)

4. **Caching**
   - Cache standard definitions
   - Cache prompt templates
   - Avoid reloading on every execution

## Production Deployment

When deploying to production:

1. **Security**
   - Validate all inputs
   - Sanitize file paths
   - Use authentication/authorization
   - Rate limit API calls

2. **Scalability**
   - Use message queue (Redis, RabbitMQ) for async execution
   - Store execution history in database
   - Implement pagination for history
   - Use CDN for file downloads

3. **Monitoring**
   - Log all executions
   - Track execution times
   - Monitor error rates
   - Alert on failures

4. **Database Schema** (for history persistence)
   ```sql
   CREATE TABLE executions (
     id VARCHAR PRIMARY KEY,
     config JSON,
     status VARCHAR,
     logs JSON,
     startTime TIMESTAMP,
     endTime TIMESTAMP,
     error VARCHAR,
     created_at TIMESTAMP DEFAULT NOW()
   );

   CREATE TABLE output_files (
     id VARCHAR PRIMARY KEY,
     executionId VARCHAR,
     fileName VARCHAR,
     filePath VARCHAR,
     FOREIGN KEY (executionId) REFERENCES executions(id)
   );
   ```

## Next Steps

1. Implement master agent call in `/src/app/api/execute/route.ts`
2. Test with your actual agents and standards
3. Add database persistence for history
4. Deploy to production
5. Monitor execution metrics

For questions or issues, refer to the main README.md.
