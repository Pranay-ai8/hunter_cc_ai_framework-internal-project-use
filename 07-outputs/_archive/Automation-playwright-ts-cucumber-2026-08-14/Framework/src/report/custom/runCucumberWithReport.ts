import { execSync } from 'child_process';

/**
 * Runs pretest -> cucumber-js (with the tag expression from the TAGS env var) -> posttest,
 * and propagates cucumber-js's real exit code so CI / npm correctly reports pass vs fail.
 * This is the entry point for `npm run test:smoke|test:regression|test:e2e|test:sanity`.
 */
const tags = process.env.TAGS ?? '';

function run(command: string): void {
  // eslint-disable-next-line no-console
  console.log(`> ${command}`);
  execSync(command, { stdio: 'inherit' });
}

run('ts-node src/report/custom/init.ts');

let exitCode = 0;
try {
  run(`cucumber-js --config=config/cucumber.js ${tags}`.trim());
} catch (err) {
  exitCode = (err as { status?: number }).status ?? 1;
}

try {
  run('ts-node src/report/custom/report.ts');
} catch {
  // Report generation failures should not mask the real test result.
}

process.exit(exitCode);
