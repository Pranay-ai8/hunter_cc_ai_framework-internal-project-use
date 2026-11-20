import * as fs from 'fs';
import * as path from 'path';
const report = require('multiple-cucumber-html-reporter');
import { ConfigurationManager } from '../../config/ConfigurationManager';

/**
 * Posttest hook — drives the enhanced multi-report from the raw Cucumber JSON output.
 */
const jsonDir = path.resolve(process.cwd(), 'test-results');
const jsonReport = path.join(jsonDir, 'cucumber-report.json');

if (!fs.existsSync(jsonReport)) {
  // eslint-disable-next-line no-console
  console.warn('[report] No cucumber-report.json found — skipping multi-report generation.');
} else {
  report.generate({
    jsonDir,
    reportPath: path.join(jsonDir, 'multi-report'),
    metadata: {
      browser: { name: ConfigurationManager.config().browser, version: 'latest' },
      device: 'Local Test Machine',
      platform: { name: process.platform, version: process.version },
    },
    customData: {
      title: 'Run Info',
      data: [
        { label: 'Project', value: 'SauceDemo Playwright + Cucumber (TypeScript)' },
        { label: 'Environment', value: ConfigurationManager.config().env },
        { label: 'Execution Start Time', value: new Date().toISOString() },
      ],
    },
  });
  // eslint-disable-next-line no-console
  console.log(`[report] multi-report generated at ${path.join(jsonDir, 'multi-report')}`);
}
