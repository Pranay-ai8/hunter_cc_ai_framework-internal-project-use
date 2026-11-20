const report = require("multiple-cucumber-html-reporter");

/**
 * posttest hook — generates the multi-cucumber-html-report from the JSON output.
 */
report.generate({
    jsonDir: "test-results/",
    reportPath: "test-results/html-report/",
    metadata: {
        browser: { name: process.env.BROWSER || "chromium", version: "latest" },
        device: "CI Runner",
        platform: { name: process.platform, version: "latest" },
    },
    customData: {
        title: "SauceDemo Automation — Run Info",
        data: [
            { label: "Project", value: "SauceDemo (Swag Labs)" },
            { label: "Environment", value: process.env.ENV || "PRD" },
        ],
    },
});
console.log("Multi-Cucumber HTML report generated at test-results/html-report/");
