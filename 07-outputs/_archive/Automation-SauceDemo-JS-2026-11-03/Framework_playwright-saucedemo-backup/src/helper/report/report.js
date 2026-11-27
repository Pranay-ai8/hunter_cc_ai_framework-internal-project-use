const report = require("multiple-cucumber-html-reporter");
const path = require("path");
const fs = require("fs-extra");

/**
 * posttest hook — generates the multi-cucumber-html-report from the Cucumber JSON output.
 */
const jsonDir = path.resolve(__dirname, "..", "..", "..", "test-results");
const jsonFile = path.join(jsonDir, "cucumber-report.json");

if (!fs.existsSync(jsonFile)) {
    console.warn(`[report.js] No cucumber-report.json found at ${jsonFile} — skipping HTML report generation.`);
    process.exit(0);
}

report.generate({
    jsonDir,
    reportPath: path.join(jsonDir, "html-report"),
    metadata: {
        browser: { name: process.env.BROWSER || "chromium", version: "latest" },
        device: "Local test machine",
        platform: { name: process.platform, version: "latest" }
    },
    customData: {
        title: "Run Info",
        data: [
            { label: "Project", value: "SauceDemo Playwright JS Cucumber Suite" },
            { label: "Environment", value: process.env.ENV || "PRD" },
            { label: "Execution Start Time", value: new Date().toString() }
        ]
    }
});

console.log("[report.js] HTML report generated at test-results/html-report/index.html");
