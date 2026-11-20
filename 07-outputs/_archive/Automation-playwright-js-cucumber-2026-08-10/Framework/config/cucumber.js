// Cucumber config. Paths/requires are relative to the process cwd, which is this
// Framework/ directory when `npm test`/`npm run test:*` runs (package.json lives here).
// Per MasterQAOrchestrator/PlaywrightAutomationOrchestrator.md Mode A output table,
// feature files and steps live in the SIBLING ../Scripts/ folder, not nested under
// this project — Node's relative-require model makes that a normal, working
// cross-folder reference (no compile step, unlike the Java stack's Maven wiring).
//
// NOTE: allure-cucumberjs v3's formatter entry point is the "allure-cucumberjs/reporter"
// subpath export (a class extending Cucumber's own Formatter) — NOT a custom wrapper
// class, and NOT the package's main "./index.js" (that auto-registers BeforeAll/Before
// hooks for the runtime/world integration instead). resultsDir is passed via the shared
// formatOptions object, which Cucumber forwards to every formatter as parsedArgvOptions.
module.exports = {
    default: {
        tags: process.env.npm_config_TAGS || "",
        formatOptions: { snippetInterface: "async-await", resultsDir: "allure-results" },
        paths: ["../Scripts/features/**/*.feature"],
        publishQuiet: true,
        dryRun: false,
        require: [
            "src/support/customTypes.js",
            "src/support/hooks.js",
            "../Scripts/steps/**/*.js"
        ],
        format: [
            "progress-bar",
            "json:test-results/cucumber-report.json",
            "allure-cucumberjs/reporter",
            "rerun:@rerun.txt"
        ],
        parallel: 1
    },
    rerun: {
        formatOptions: { snippetInterface: "async-await", resultsDir: "allure-results" },
        publishQuiet: true,
        dryRun: false,
        require: [
            "src/support/customTypes.js",
            "src/support/hooks.js",
            "../Scripts/steps/**/*.js"
        ],
        format: [
            "progress-bar",
            "json:test-results/cucumber-report.json",
            "allure-cucumberjs/reporter",
            "rerun:@rerun.txt"
        ],
        parallel: 1
    }
};
