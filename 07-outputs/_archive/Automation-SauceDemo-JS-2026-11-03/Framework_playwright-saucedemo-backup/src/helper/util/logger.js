const winston = require("winston");
const fs = require("fs-extra");
const path = require("path");

const LOG_DIR = path.resolve(__dirname, "..", "..", "..", "test-results", "logs");
fs.ensureDirSync(LOG_DIR);

/**
 * Creates a per-scenario Winston logger writing to test-results/logs/<scenarioName>.log
 * and echoing to the console.
 */
function getLogger(scenarioName = "scenario") {
    const safeName = String(scenarioName).replace(/[^a-z0-9_-]/gi, "_").slice(0, 100);
    const logFile = path.join(LOG_DIR, `${safeName}.log`);

    return winston.createLogger({
        level: "info",
        format: winston.format.combine(
            winston.format.timestamp({ format: "YYYY-MM-DD HH:mm:ss" }),
            winston.format.printf(({ timestamp, level, message }) => `[${timestamp}] [${level.toUpperCase()}] ${message}`)
        ),
        transports: [
            new winston.transports.File({ filename: logFile }),
            new winston.transports.Console({ silent: process.env.CI === "true" })
        ]
    });
}

module.exports = { getLogger };
