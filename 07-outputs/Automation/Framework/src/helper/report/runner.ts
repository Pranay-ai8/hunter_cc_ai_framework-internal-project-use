import { spawnSync } from "child_process";
import * as path from "path";

// 1. Run init.ts to initialize/clean test-results directory
const initScript = path.resolve(__dirname, "init.ts");
const npxCmd = process.platform === "win32" ? "npx.cmd" : "npx";

spawnSync(npxCmd, ["ts-node", initScript], {
    stdio: "inherit",
    shell: true
});

// 2. Forward any CLI arguments to cucumber-js (e.g. --tags=@Key:SGC024)
const cliArgs = process.argv.slice(2);

let exitCode = 0;
try {
    const cucumberResult = spawnSync(npxCmd, ["cucumber-js", "--config=config/cucumber.js", ...cliArgs], {
        stdio: "inherit",
        shell: true
    });
    exitCode = cucumberResult.status ?? 0;
} finally {
    // 3. ALWAYS generate the HTML report, whether tests pass, fail, or are stopped!
    const reportScript = path.resolve(__dirname, "report.ts");
    spawnSync(npxCmd, ["ts-node", reportScript], {
        stdio: "inherit",
        shell: true
    });

    // 4. Exit with Cucumber's actual exit status
    process.exit(exitCode);
}
