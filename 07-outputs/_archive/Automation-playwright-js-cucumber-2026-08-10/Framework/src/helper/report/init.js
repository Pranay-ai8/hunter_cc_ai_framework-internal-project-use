const fs = require("fs-extra");

/**
 * pretest hook — resets/prepares report output directories.
 */
fs.emptyDirSync("test-results");
fs.emptyDirSync("allure-results");
fs.ensureDirSync("logs");
console.log("Report/log directories reset.");
