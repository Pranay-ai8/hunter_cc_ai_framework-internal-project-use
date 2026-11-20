import * as fs from 'fs';
import * as path from 'path';

/**
 * Pretest hook — clears stale results from the previous run so the HTML/JSON/JUnit
 * reporters, the multi-report, and the rerun file always reflect only the current run.
 */
const testResultsDir = path.resolve(process.cwd(), 'test-results');

function clean(dir: string): void {
  if (fs.existsSync(dir)) {
    fs.rmSync(dir, { recursive: true, force: true });
  }
  fs.mkdirSync(dir, { recursive: true });
}

clean(testResultsDir);
clean(path.join(testResultsDir, 'logs'));
clean(path.join(testResultsDir, 'screenshots'));

const rerunFile = path.resolve(process.cwd(), '@rerun.txt');
if (fs.existsSync(rerunFile)) {
  fs.rmSync(rerunFile);
}

// eslint-disable-next-line no-console
console.log('[init] test-results/ cleared, ready for a fresh run.');
