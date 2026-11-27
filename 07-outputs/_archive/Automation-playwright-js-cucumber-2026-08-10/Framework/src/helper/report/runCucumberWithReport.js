const { spawnSync } = require("child_process");

/**
 * test:* npm scripts entry point — runs cucumber-js with the forwarded CLI args,
 * then always runs the posttest reporter regardless of pass/fail.
 */
const args = process.argv.slice(2);
require("./init.js");

const result = spawnSync("npx", ["cucumber-js", ...args], { stdio: "inherit", shell: true });

require("./report.js");

process.exit(result.status ?? 1);
