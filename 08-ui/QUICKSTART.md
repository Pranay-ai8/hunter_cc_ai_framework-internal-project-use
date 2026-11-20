# Quick Start Guide

Get the QA Framework UI up and running in 5 minutes.

## 1. Installation

```bash
cd 08-ui
npm install
```

This installs all dependencies including React, Next.js, Tailwind CSS, and Zustand.

## 2. Start Development Server

```bash
npm run dev
```

Output:
```
- Local:        http://localhost:3000
- Environments: .env.local
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

## 3. See the Demo

The UI comes with **mock execution** that simulates agent runs:

### Demo Walkthrough

1. **Configuration Tab** (default)
   - See 9 agents: Requirement Analysis, Gap Analysis, Test Plan, etc.
   - See 6 standards: Test Case, API, Automation, etc.
   - See 9 prompts ready to use

2. **Select Some Agents**
   - Click "Functional Testing" checkbox
   - Click "UI Testing" checkbox
   - Click "Execution" button

3. **Watch Execution Tab**
   - Logs stream in real-time (simulated)
   - Status shows "Running"
   - Progress indicator at the top

4. **Execution Completes**
   - Status changes to "Completed"
   - Files appear ready to download
   - Execution appears in History tab

5. **Check History Tab**
   - Click any past execution
   - View its logs and artifacts
   - Download previously generated files

## 4. Key Features to Try

### Bulk Selection
```
- Click "All" to select all agents/standards/prompts
- Click "None" to deselect all
- Use checkboxes for fine-grained control
```

### Additional Options
```
- Scroll down to see additional output options
- Toggle RTM generation
- Toggle Regression Suite
- Toggle Smoke Suite
```

### Real-Time Monitoring
```
- Click Run Agent
- Watch logs update in real-time
- Files download link appears when complete
```

### Execution History
```
- All executions are saved
- Click history item to re-view its details
- Status badges show at-a-glance results
```

## 5. File Structure

```
08-ui/
├── src/
│   ├── app/page.tsx           ← Main UI
│   ├── components/            ← React components
│   ├── store/agentStore.ts   ← State management
│   └── config/framework.ts    ← Agents/Standards/Prompts
├── package.json               ← Dependencies
├── tsconfig.json              ← TypeScript config
├── tailwind.config.js         ← Styles
└── README.md                  ← Full documentation
```

## 6. Connect Your Real Master Agent

The mock execution is just for demo. To connect your real agent:

### Quick Connection (5 lines of code)

Edit `/src/app/api/execute/route.ts`:

```typescript
// Replace this mock call:
// await simulateAgentExecution(execution)

// With your agent call:
const result = await fetch('http://your-master-agent:5000/execute', {
  method: 'POST',
  body: JSON.stringify({ config }),
})
const data = await result.json()
```

See **INTEGRATION.md** for full backend setup.

## 7. Build for Production

```bash
npm run build
npm start
```

Server runs on `http://localhost:3000` in production mode.

## 8. Customization

### Add New Agent

Edit `src/config/framework.ts`:

```typescript
export const AGENTS: Agent[] = [
  // ... existing
  {
    id: 'my-agent',
    name: 'My New Agent',
    description: 'What it does',
    enabled: true,
  },
]
```

### Add New Standard

Edit `src/config/framework.ts`:

```typescript
export const STANDARDS: Standard[] = [
  // ... existing
  {
    id: 'my-standard',
    name: 'My Standard',
    description: 'Standard details',
    enabled: true,
  },
]
```

### Change Colors

Edit `tailwind.config.js` or modify component Tailwind classes.

## 9. Common Tasks

### View Real-Time Logs
- Go to Execution tab
- Logs stream from top to bottom
- Colors: green=success, red=error, yellow=warning

### Download Files
- After execution completes
- Green "Generated Files" section appears
- Click download icon next to file name

### Re-Run Same Configuration
- Go to History tab
- Click past execution
- Files and settings appear
- Modify if needed and click Run again

### Clear History
```javascript
// In browser console
localStorage.removeItem('agent-store')
```

Then refresh the page.

## 10. Troubleshooting

### Port 3000 Already in Use
```bash
npm run dev -- -p 3001
```

### Styles Not Loading
```bash
rm -rf .next
npm run build
npm run dev
```

### Agents/Standards Not Showing
- Check `/src/config/framework.ts` has entries
- Refresh browser (Ctrl+Shift+R for hard refresh)

### History Not Saving
- Check localStorage is enabled (not private browsing)
- Check browser console for errors (F12)

## 11. Next Steps

1. **Review Full Documentation**
   - Read [README.md](README.md) for complete feature list
   - Read [INTEGRATION.md](INTEGRATION.md) for backend setup

2. **Connect Master Agent**
   - Follow INTEGRATION.md guide
   - Test with one agent first
   - Scale up gradually

3. **Customize for Your Needs**
   - Add your agents
   - Add your standards
   - Adjust styles/colors

4. **Deploy**
   - Test locally (npm run dev)
   - Build for production (npm run build)
   - Deploy to Vercel, AWS, or your hosting

## 12. File Structure Reference

```
Project Layout:
├── UI (React + Next.js)
│   └── 08-ui/  ← You are here
├── Agents (Python/Node)
│   └── 05-agents/
├── Prompts (Markdown)
│   └── 04-prompts/
├── Standards (Markdown)
│   └── 03-standards/
├── Templates (Markdown)
│   └── 02-templates/
└── Documentation
    └── 01-docs/
```

## 13. API Endpoints

These are available from the UI:

```
GET  /api/config              → Get agents/standards/prompts
POST /api/execute             → Run agents
GET  /api/download?file=...   → Download artifact
```

## 14. Environment Setup

Optional - for backend integration:

```bash
# Create .env.local
cp .env.example .env.local
```

Edit `.env.local` with your settings:
```
ANTHROPIC_API_KEY=sk-ant-...
MASTER_AGENT_API_URL=http://localhost:5000
```

## 15. Performance Notes

- UI handles 100+ agents smoothly
- Real-time logs update at 60fps
- History saved in browser memory
- Consider database for 1000+ executions

## Quick Reference

| Action | How |
|--------|-----|
| Start | `npm run dev` |
| Build | `npm run build` |
| Run Production | `npm start` |
| Select All | Click "All" button |
| Run Agent | Click "▶ Run Agent" button |
| View Logs | Go to "Execution" tab |
| View History | Go to "History" tab |
| Download File | Click download icon in completed execution |
| Clear Data | `localStorage.removeItem('agent-store')` |
| Add Agent | Edit `src/config/framework.ts` |
| Change Colors | Edit component Tailwind classes |

---

**That's it!** Your QA Framework UI is ready to use. 

Next: Read [INTEGRATION.md](INTEGRATION.md) to connect your master agent.
