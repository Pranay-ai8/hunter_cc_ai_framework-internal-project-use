const { spawnSync } = require("child_process");
const path = require("path");

/**
 * Entry point for npm run test:smoke / test:regression / test:sanity / test:failed.
 * Runs `init.js` -> cucumber-js (forwarding all CLI args) -> `report.js`, in sequence,
 * and exits with cucumber-js's own exit code so CI correctly reports failures.
 */
const root = path.resolve(__dirname, "..", "..", "..");
const args = process.argv.slice(2);

function run(command, commandArgs) {
    const result = spawnSync(command, commandArgs, { stdio: "inherit", cwd: root, shell: process.platform === "win32" });
    return result.status === null ? 1 : result.status;
}

run(process.execPath, [path.join(__dirname, "init.js")]);

const cucumberBin = path.join(root, "node_modules", ".bin", process.platform === "win32" ? "cucumber-js.cmd" : "cucumber-js");
const exitCode = run(cucumberBin, args);

run(process.execPath, [path.join(__dirname, "report.js")]);

process.exit(exitCode);
