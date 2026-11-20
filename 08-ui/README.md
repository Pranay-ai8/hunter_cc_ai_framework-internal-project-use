# QA Framework Master Agent UI

A modern web-based control panel for managing and executing QA testing agents with support for real-time logging, execution history, and artifact generation.

## Features

- **Agent Selection**: Select from 9 specialized testing agents
- **Standards Management**: Choose applicable testing standards
- **Prompt Selection**: Pick specific prompts for customized execution
- **Real-time Logging**: Stream execution logs as they happen
- **Execution History**: Track and review past agent runs
- **File Downloads**: Download generated test artifacts
- **Document Upload**: Drag-and-drop document upload with automatic categorization
- **Smart Categorization**: Automatically places documents in correct folders (PDFs, PRD, BRD, Screenshots, Figma, API Docs)
- **Responsive Design**: Works on desktop and tablet
- **Persistent State**: Configuration persists across sessions

## Tech Stack

- **Frontend**: React 18 + TypeScript
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **Framework**: Next.js 14
- **Date Formatting**: date-fns

## Getting Started

### Prerequisites

- Node.js 18+ or 20+
- npm or yarn

### Installation

```bash
cd 08-ui
npm install
```

### Development

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

### Production Build

```bash
npm run build
npm start
```

## Project Structure

```
08-ui/
├── src/
│   ├── app/
│   │   ├── api/              # API routes
│   │   ├── layout.tsx        # Root layout
│   │   ├── page.tsx          # Main page
│   │   └── globals.css       # Global styles
│   ├── components/           # React components
│   │   ├── SelectionPanel.tsx
│   │   ├── AdditionalOptions.tsx
│   │   ├── ExecutionPanel.tsx
│   │   └── HistoryPanel.tsx
│   ├── config/
│   │   └── framework.ts      # Agent, standard, prompt definitions
│   ├── store/
│   │   └── agentStore.ts     # Zustand store
│   └── types/
│       └── index.ts          # TypeScript types
├── next.config.js
├── tailwind.config.js
├── postcss.config.js
└── tsconfig.json
```

## Available Agents

1. **Requirement Analysis** - Analyze requirements from multiple sources
2. **Gap Analysis** - Identify missing requirements and gaps
3. **Test Plan Generation** - Generate comprehensive test plans
4. **Functional Testing** - Create functional test cases
5. **UI Testing** - Generate UI test cases
6. **API Testing** - Create API test cases
7. **Security Testing** - Generate security test cases
8. **Accessibility Testing** - Create accessibility test cases
9. **Automation Scripts** - Generate automation scripts with POM pattern

## Available Standards

- Test Case Standards
- API Standards
- Automation Guidelines
- Naming Conventions
- Testing Standards
- UI Standards

## Document Upload

The UI includes a powerful document upload feature with automatic categorization:

**Features:**
- Drag-and-drop interface for easy uploads
- Automatic file type detection and categorization
- Smart folder placement based on filename patterns and extensions
- Real-time upload status feedback
- Recent uploads tracking

**Categorization Rules:**
- `.pdf` → PDFs folder
- Filename contains "prd" → PRD folder
- Filename contains "brd" → BRD folder
- `.png`, `.jpg`, `.jpeg`, `.heic` → Screenshots folder
- `.figma` or "figma" in filename → Figma/figma-exports folder
- "api" in filename → API Docs folder
- Everything else → Other Requirement Docs folder

**How to Use:**
1. Click the "📤 Upload Docs" tab
2. Drag files or click "Browse Files"
3. System automatically places documents in correct folders
4. View recent uploads in the same tab

See [UPLOAD-DOCS.md](UPLOAD-DOCS.md) for detailed documentation and examples.

## Available Prompts

- Requirement Analysis
- Gap Analysis
- Test Plan Generation
- Functional Test Cases
- UI Test Cases
- API Test Cases
- Security Test Cases
- Accessibility Test Cases
- Automation Scripts

## Usage

### Running Agents

1. **Navigate to Configuration Tab**
   - Click the "Configuration" tab if not already there

2. **Select Agents**
   - Check/uncheck individual agents
   - Use "All" to select all agents
   - Use "None" to deselect all agents

3. **Select Standards**
   - Choose which standards to apply
   - Standards customize the output format and rules

4. **Select Prompts**
   - Pick specific prompts for the execution
   - Prompts guide the agent behavior

5. **Additional Options**
   - Enable/disable optional outputs like RTM, Regression Suite, Smoke Suite

6. **Run Agent**
   - Click the "▶ Run Agent" button
   - The execution automatically switches to the Execution tab

### Monitoring Execution

- **Real-time Logs**: Watch logs stream in real-time
- **Status Badge**: See current execution status (Running/Completed/Failed)
- **Download Files**: Get generated artifacts once execution completes

### Reviewing History

- **Execution History Tab**: View all past executions
- **Execution Details**: Click any history item to view its logs and artifacts
- **Re-run Configurations**: Select a past execution to see exactly what was run

## API Routes

### GET /api/config

Returns available agents, standards, and prompts.

**Response:**
```json
{
  "agents": [...],
  "standards": [...],
  "prompts": [...]
}
```

### POST /api/execute

Executes the agent with given configuration.

**Request:**
```json
{
  "config": {
    "agents": [...],
    "standards": [...],
    "prompts": [...],
    "additionalOptions": {
      "generateRTM": true,
      "generateRegressionSuite": true,
      "generateSmokeSuite": true
    }
  }
}
```

**Response:**
```json
{
  "executionId": "abc12345",
  "startTime": "2024-07-20T10:00:00.000Z",
  "config": {...},
  "status": "queued"
}
```

## Backend Integration

### Connecting to Your Master Agent

To integrate with your actual master agent:

1. **Modify `/src/app/api/execute/route.ts`**
   - Replace mock execution with actual agent calls
   - Build the master prompt from selected agents, standards, and prompts
   - Call your Claude API endpoint

2. **Implement Streaming Logs**
   - Use Server-Sent Events (SSE) for real-time log streaming
   - Stream logs from agent execution to frontend

3. **Handle File Generation**
   - Save generated files to `/public/outputs/`
   - Return file paths in execution completion response

4. **Error Handling**
   - Catch agent failures and return error messages
   - Log errors on server side

### Example Integration Pattern

```typescript
// In /src/app/api/execute/route.ts

export async function POST(request: NextRequest) {
  const body = await request.json()
  const { config } = body

  // 1. Build master prompt
  const masterPrompt = buildMasterPrompt(config)

  // 2. Call Claude API
  const response = await callClaudeAPI(masterPrompt)

  // 3. Stream logs
  const logs = await streamExecutionLogs(response)

  // 4. Generate artifacts
  const files = await generateArtifacts(logs, config)

  return NextResponse.json({
    executionId,
    status: 'completed',
    logs,
    outputFiles: files,
  })
}
```

## Customization

### Adding New Agents

Edit `/src/config/framework.ts`:

```typescript
export const AGENTS: Agent[] = [
  // ... existing agents
  {
    id: 'my-new-agent',
    name: 'My New Agent',
    description: 'Description of my new agent',
    enabled: true,
  },
]
```

### Adding New Standards

Edit `/src/config/framework.ts`:

```typescript
export const STANDARDS: Standard[] = [
  // ... existing standards
  {
    id: 'my-standard',
    name: 'My Standard',
    description: 'Description of my standard',
    enabled: true,
  },
]
```

### Styling

The UI uses Tailwind CSS. Customize colors and theme in:
- `tailwind.config.js` - Theme configuration
- `src/app/globals.css` - Global styles
- Component files - Inline Tailwind classes

## Troubleshooting

### Port Already in Use

If port 3000 is already in use:

```bash
npm run dev -- -p 3001
```

### Zustand Store Not Persisting

The store persists to localStorage. Clear it with:

```javascript
localStorage.removeItem('agent-store')
```

### Build Errors

Clear the build cache:

```bash
rm -rf .next
npm run build
```

## Performance Tips

- The UI can handle 100+ agents/standards/prompts
- Real-time logs update efficiently with React reconciliation
- Execution history is stored in memory (implement DB for larger scale)

## Future Enhancements

- Database persistence for execution history
- Real-time WebSocket streaming for logs
- Agent status and health monitoring
- Scheduled agent execution
- Custom agent templates
- Multi-user support with authentication
- Export execution reports
- Compare past executions

## Support

For issues or questions about this UI, refer to:
- Framework documentation in `../CLAUDE.md`
- Master agent documentation in `../05-agents/master-agent.md`
- Template examples in `../02-templates/`

## License

This project is part of the QA Framework and follows the same license terms.
