const winston = require("winston");
const fs = require("fs-extra");

fs.ensureDirSync("logs");

/**
 * Winston logger factory — one log file per scenario, per playwright-javascript-standards.md.
 */
function getLogger(scenarioName) {
    const safeName = scenarioName.replace(/[^a-z0-9]/gi, "_").toLowerCase();
    return winston.createLogger({
        level: "info",
        format: winston.format.combine(
            winston.format.timestamp(),
            winston.format.printf(({ timestamp, level, message }) => `${timestamp} [${level.toUpperCase()}] ${message}`)
        ),
        transports: [
            new winston.transports.Console(),
            new winston.transports.File({ filename: `logs/${safeName}.log` }),
        ],
    });
}

module.exports = { getLogger };
