import * as fs from 'fs';
import * as path from 'path';
import { ResultStore } from './ResultStore';

/**
 * Generates a self-contained HTML email-style summary of the run, written to
 * `test-results/email-summary.html`. Actual SMTP dispatch is handled by the
 * `.github/actions/email_notification` composite action in CI — this module only
 * produces the HTML body.
 */
export function generateEmailSummary(): string {
  const { total, passed, failed, skipped } = ResultStore.summary();
  const rows = ResultStore.all()
    .map(
      (r) =>
        `<tr><td>${r.tcId ?? ''}</td><td>${r.name}</td><td style="color:${
          r.status === 'passed' ? 'green' : r.status === 'failed' ? 'red' : 'orange'
        }">${r.status.toUpperCase()}</td></tr>`,
    )
    .join('\n');

  const html = `
  <html>
    <body style="font-family: Arial, sans-serif;">
      <h2>SauceDemo Automation Run Summary</h2>
      <p>Total: ${total} | Passed: ${passed} | Failed: ${failed} | Skipped: ${skipped}</p>
      <table border="1" cellpadding="6" cellspacing="0">
        <thead><tr><th>TC ID</th><th>Scenario</th><th>Status</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </body>
  </html>`;

  const outDir = path.resolve(process.cwd(), 'test-results');
  if (!fs.existsSync(outDir)) fs.mkdirSync(outDir, { recursive: true });
  const outFile = path.join(outDir, 'email-summary.html');
  fs.writeFileSync(outFile, html, 'utf-8');
  return outFile;
}
