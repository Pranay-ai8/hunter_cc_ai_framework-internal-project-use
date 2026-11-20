const fs = require("fs-extra");
const path = require("path");

/**
 * pretest hook — resets/prepares report output directories before a run.
 */
const dirs = [
    path.resolve(__dirname, "..", "..", "..", "test-results"),
    path.resolve(__dirname, "..", "..", "..", "test-results", "screenshots"),
    path.resolve(__dirname, "..", "..", "..", "test-results", "trace"),
    path.resolve(__dirname, "..", "..", "..", "test-results", "logs"),
    path.resolve(__dirname, "..", "..", "..", "allure-results")
];

dirs.forEach((dir) => {
    fs.ensureDirSync(dir);
});

console.log("[report/init.js] Report output directories are ready.");
