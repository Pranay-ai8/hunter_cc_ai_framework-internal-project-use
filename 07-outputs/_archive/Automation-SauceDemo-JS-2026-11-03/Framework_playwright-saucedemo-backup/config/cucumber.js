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
            "./src/support/allureReporter.js",
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
            "./src/support/allureReporter.js",
            "rerun:@rerun.txt"
        ],
        parallel: 1
    }
};
