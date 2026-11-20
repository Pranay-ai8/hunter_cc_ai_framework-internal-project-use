# Complete Setup Guide

Everything you need to get the QA Framework UI running.

## What Was Created

A complete React + Next.js web application with:

✅ **Frontend UI**
- Agent selection with checkboxes
- Standards and prompts selection
- Real-time execution logs
- Execution history tracking
- File download support
- Responsive design
- Dark/light theme ready

✅ **Backend API**
- Configuration endpoint
- Agent execution endpoint
- File download endpoint
- Error handling

✅ **State Management**
- Zustand store with persistence
- Local storage for user selections
- Execution history tracking

✅ **Documentation**
- README.md - Feature overview
- QUICKSTART.md - Get running in 5 minutes
- INTEGRATION.md - Connect to master agent
- ARCHITECTURE.md - Technical deep dive
- This guide - Setup and deployment

## Directory Structure

```
08-ui/
├── src/
│   ├── app/
│   │   ├── api/
│   │   │   ├── config/route.ts           # Get available agents/standards
│   │   │   ├── execute/route.ts          # Execute master agent
│   │   │   └── download/route.ts         # Download files
│   │   ├── layout.tsx                    # Root layout
│   │   ├── page.tsx                      # Main page (1000+ lines)
│   │   └── globals.css                   # Global styles
│   ├── components/
│   │   ├── SelectionPanel.tsx            # Agent/Standard/Prompt selection
│   │   ├── AdditionalOptions.tsx         # Optional output controls
│   │   ├── ExecutionPanel.tsx            # Real-time log display
│   │   └── HistoryPanel.tsx              # Execution history
│   ├── config/
│   │   └── framework.ts                  # Agent/Standard/Prompt definitions
│   ├── store/
│   │   └── agentStore.ts                 # Zustand state management
│   └── types/
│       └── index.ts                      # TypeScript interfaces
├── public/
│   └── outputs/                          # Generated artifacts (created at runtime)
├── package.json                          # Dependencies
├── tsconfig.json                         # TypeScript config
├── tsconfig.node.json                    # Node config
├── next.config.js                        # Next.js config
├── tailwind.config.js                    # Tailwind config
├── postcss.config.js                     # PostCSS config
├── .gitignore                            # Git ignore rules
├── .env.example                          # Environment template
├── README.md                             # Full documentation
├── QUICKSTART.md                         # 5-minute guide
├── INTEGRATION.md                        # Backend setup
├── ARCHITECTURE.md                       # Technical overview
└── SETUP.md                              # This file
```

## Installation Steps

### Step 1: Navigate to UI Directory

```bash
cd /path/to/CC-QA-AI-Framework/08-ui
```

### Step 2: Install Dependencies

```bash
npm install
```

This installs:
- `next` - React framework
- `react` - UI library
- `react-dom` - DOM binding
- `axios` - HTTP client
- `zustand` - State management
- `react-toastify` - Toast notifications
- `date-fns` - Date formatting
- `tailwindcss` - CSS framework
- `typescript` - Type checking

**Installation time:** ~2-3 minutes on first run **Disk space:** ~500MB

### Step 3: Start Development Server

```bash
npm run dev
```

Expected output:
```
> qa-framework-ui@1.0.0 dev
> next dev

  ▲ Next.js 14.1.0
  - Local:        http://localhost:3000
  - Environments: .env.local

✓ Ready in 1.2s
```

### Step 4: Open in Browser

Navigate to: **http://localhost:3000**

You should see:
- Header: "QA Framework Master Agent"
- Tab navigation: Configuration | Execution | History
- 9 agents with checkboxes
- 6 standards with checkboxes
- 9 prompts with checkboxes
- Blue "Run Agent" button at bottom

## First Run - Demo Walkthrough

### 1. Configuration (Default Tab)

```
You see:
├─ Agents
│  ├─ ☐ Requirement Analysis
│  ├─ ☐ Gap Analysis
│  ├─ ☐ Test Plan Generation
│  ├─ ☐ Functional Testing
│  ├─ ☐ UI Testing
│  ├─ ☐ API Testing
│  ├─ ☐ Security Testing
│  ├─ ☐ Accessibility Testing
│  └─ ☐ Automation Scripts
├─ Standards
│  ├─ ☐ Test Case Standards
│  ├─ ☐ API Standards
│  ├─ ☐ Automation Guidelines
│  ├─ ☐ Naming Conventions
│  ├─ ☐ Testing Standards
│  └─ ☐ UI Standards
├─ Prompts
│  ├─ ☐ Requirement Analysis
│  ├─ ☐ Gap Analysis
│  ├─ ☐ Test Plan Generation
│  ├─ ☐ Functional Test Cases
│  ├─ ☐ UI Test Cases
│  ├─ ☐ API Test Cases
│  ├─ ☐ Security Test Cases
│  ├─ ☐ Accessibility Test Cases
│  └─ ☐ Automation Scripts
├─ Additional Options
│  ├─ ☐ Generate RTM
│  ├─ ☐ Generate Regression Suite
│  └─ ☐ Generate Smoke Suite
└─ Run Agent Button
```

### 2. Select Some Items

Click checkboxes:
- ☑ Functional Testing
- ☑ API Testing
- ☑ Security Testing

All standards are checked by default. Keep them.

### 3. Click "Run Agent"

Button changes to "Running..." with spinner.

### 4. Watch Execution Tab

Automatically switches to "Execution" tab. You see:
- Status: "Running" (blue badge)
- Live logs streaming
- Animation showing activity
- Timestamp for each log entry

Sample logs appear:
```
10:00:00 [INFO] Agent execution started
10:00:01 [SUCCESS] Loading agents: Functional Testing, API Testing, Security Testing
10:00:02 [SUCCESS] Applying standards: Test Case Standards, API Standards, Testing Standards
10:00:03 [SUCCESS] Analyzing requirements...
...continues for ~10-15 seconds...
```

### 5. Execution Completes

Status changes to "Completed" (green badge).

Green "Generated Files" section appears:
```
Generated Files
├─ ↓ TestCases.md
├─ ↓ RTM.xlsx
└─ ↓ AutomationScripts.ts
```

Click download icon to download each file.

### 6. Check History Tab

"Execution #abc12345" appears with:
- Status: Completed
- 3 agents, 6 standards selected
- "10 seconds ago"

Click to view its logs again.

## Next Steps - Connect Your Master Agent

The demo uses simulated execution. To connect your real master agent:

### Quick Test (10 minutes)

1. Read: [INTEGRATION.md](INTEGRATION.md)
2. Modify: `/src/app/api/execute/route.ts`
3. Replace mock with API call to your agent
4. Test with one simple agent first

### Full Integration (1-2 hours)

1. Implement backend according to INTEGRATION.md
2. Connect Claude API or your agent server
3. Test file generation
4. Deploy to production

See [INTEGRATION.md](INTEGRATION.md) for detailed walkthrough.

## Common Commands

### Development

```bash
# Start dev server (with hot reload)
npm run dev

# Visit http://localhost:3000

# Stop server
Ctrl + C
```

### Building

```bash
# Create production build
npm run build

# This optimizes and compiles the app
# Creates .next/ directory with optimized code

# Start production server
npm start

# Visit http://localhost:3000 (production mode)
```

### Maintenance

```bash
# Clear build cache
rm -rf .next

# Reinstall dependencies
rm -rf node_modules
npm install

# Check for TypeScript errors
npx tsc --noEmit

# Format code
npx prettier --write .
```

## Configuration

### Changing Agents

Edit `/src/config/framework.ts`:

```typescript
export const AGENTS: Agent[] = [
  {
    id: 'functional',
    name: 'Functional Testing',
    description: 'Generate functional test cases',
    enabled: true,  // ← Show by default
  },
  // Add your agents here
]
```

### Changing Standards

Edit `/src/config/framework.ts`:

```typescript
export const STANDARDS: Standard[] = [
  {
    id: 'testcase-standards',
    name: 'Test Case Standards',
    description: 'Apply standard test case format',
    enabled: true,
  },
  // Add your standards here
]
```

### Changing Colors

Edit component files and modify Tailwind classes:

```typescript
// In SelectionPanel.tsx
<div className="bg-gradient-to-r from-blue-500 to-blue-600">
//                                  ^^^^^^^^  change colors
```

Tailwind colors: blue, green, red, purple, amber, pink, etc.

### Changing Port

```bash
# Run on port 3001 instead of 3000
npm run dev -- -p 3001
```

## Troubleshooting

### Issue: "Port 3000 already in use"

**Solution:**
```bash
# Use different port
npm run dev -- -p 3001

# Or kill process using 3000
lsof -i :3000
kill -9 <PID>
```

### Issue: "Module not found" errors

**Solution:**
```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install

# Clear build cache
rm -rf .next
npm run dev
```

### Issue: "Styles not loading"

**Solution:**
```bash
# Force rebuild
rm -rf .next
npm run build
npm run dev

# Hard refresh browser (Ctrl+Shift+R)
```

### Issue: "localStorage is not defined"

**Solution:** This happens in server-side rendering.
- It's already handled in the code
- Just refresh the page
- Should work on second load

### Issue: "Agents not showing"

**Solution:**
1. Check `/src/config/framework.ts` has agents
2. Hard refresh browser (Ctrl+Shift+R)
3. Check browser console for errors (F12)
4. Check localStorage:
   ```javascript
   localStorage.removeItem('agent-store')
   ```

### Issue: "Cannot find module '@/...'"

**Solution:** TypeScript path aliases
```bash
# Make sure tsconfig.json has:
"paths": {
  "@/*": ["./src/*"]
}

# Then restart dev server
npm run dev
```

## Environment Variables

### Optional Configuration

Create `.env.local`:

```bash
# Copy from template
cp .env.example .env.local

# Edit with your settings
cat .env.local
```

Available variables:

```bash
# Master Agent API (if using HTTP backend)
MASTER_AGENT_API_URL=http://localhost:5000
MASTER_AGENT_API_KEY=your-key-here

# Claude API (if calling Claude directly)
ANTHROPIC_API_KEY=sk-ant-...

# File storage
OUTPUT_DIR=/path/to/outputs
PUBLIC_URL=http://localhost:3000

# Database (optional, for production)
DATABASE_URL=postgresql://user:pass@localhost:5432/qa_framework
```

## Browser Requirements

### Minimum Requirements
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### Features Used
- ES2020 JavaScript
- CSS Grid/Flexbox
- localStorage
- Fetch API
- EventSource (for SSE)

### Recommended Setup
- Chrome or Firefox (best developer tools)
- 1920x1080 resolution (responsive down to 768px)
- 4GB RAM (for agent execution)
- 10GB disk space (for artifacts)

## Production Deployment

### Before Deploying

```bash
# Build for production
npm run build

# Test production build locally
npm start

# Check for build errors
npm run build 2>&1 | grep -i error

# Check TypeScript errors
npx tsc --noEmit
```

### Deploy to Vercel (Easiest)

```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
vercel

# Provide Vercel credentials
# Select project folder
# Confirm build settings
# Done!
```

Your app is now at: `https://your-project.vercel.app`

### Deploy to AWS

See: AWS Lambda + API Gateway + S3 guide

### Deploy to Docker

Create `Dockerfile`:
```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

Then:
```bash
docker build -t qa-framework-ui .
docker run -p 3000:3000 qa-framework-ui
```

### Environment for Production

```bash
# In production environment
NODE_ENV=production

# Set your master agent URL
MASTER_AGENT_API_URL=https://agent.yourcompany.com

# Set Claude API key
ANTHROPIC_API_KEY=sk-ant-...
```

## Performance Optimization

### Frontend

```bash
# Build size check
npm run build
# Check .next folder size

# Analyze bundle
npx next/bundle-analyzer
```

### Server

```bash
# Monitor performance
npm run dev

# Watch for slow routes
# Check terminal output
```

## Monitoring

### Development

Watch for:
- TypeScript errors (red text)
- Hot reload working (✓)
- No console errors (F12)

### Production

Monitor:
- Response times (should be <100ms)
- Error rates (should be ~0%)
- Execution success rate
- Agent execution times

## Getting Help

1. **Check Documentation**
   - README.md (features)
   - QUICKSTART.md (5-min start)
   - INTEGRATION.md (backend setup)
   - ARCHITECTURE.md (technical details)

2. **Check Browser Console**
   - F12 → Console tab
   - Look for red errors
   - Copy error message

3. **Check Logs**
   - Terminal where `npm run dev` is running
   - Look for stack traces

4. **Reset Everything**
   ```bash
   # Clear all data
   localStorage.removeItem('agent-store')
   
   # Clear all files
   rm -rf .next node_modules
   
   # Reinstall
   npm install
   npm run dev
   ```

## What's Next?

### Immediate (Today)
- [ ] Run `npm install`
- [ ] Run `npm run dev`
- [ ] Visit http://localhost:3000
- [ ] Try selecting agents and clicking Run
- [ ] Watch execution logs

### Short Term (This Week)
- [ ] Read INTEGRATION.md
- [ ] Connect your master agent
- [ ] Test with real agents
- [ ] Test file generation

### Medium Term (This Month)
- [ ] Deploy to production
- [ ] Set up monitoring
- [ ] Add authentication (if needed)
- [ ] Database persistence for history

### Long Term
- [ ] Add more agents
- [ ] Custom standards
- [ ] Performance optimizations
- [ ] Multi-user support

## File Sizes Reference

For reference, after `npm install`:

```
node_modules/         ~700MB
.next/ (after build)  ~100MB
src/                  ~50KB
public/               ~1MB
```

**Total:** ~800MB on disk

## Support Resources

- **React:** https://react.dev
- **Next.js:** https://nextjs.org
- **Tailwind:** https://tailwindcss.com
- **TypeScript:** https://www.typescriptlang.org
- **Zustand:** https://github.com/pmndrs/zustand

## License

This UI is part of the QA Framework. See main project LICENSE.

---

**Ready to start?**

```bash
cd 08-ui
npm install
npm run dev
```

Then visit: http://localhost:3000

**Questions?** See [README.md](README.md) or [INTEGRATION.md](INTEGRATION.md)
