const dotenv = require("dotenv");
const path = require("path");
const fs = require("fs");

/**
 * Loads the active environment file (src/helper/env/.env.<ENV>) via dotenv.
 * ENV must be passed explicitly (e.g. `cross-env ENV=PRD ...`) — there is no default,
 * so a missing ENV is logged as an error rather than silently assumed.
 */
function getEnv() {
    const env = process.env.ENV;
    if (!env) {
        console.error("[env.js] ERROR: ENV environment variable is not set. Pass ENV=PRD (or another env) explicitly.");
        return;
    }

    const envFilePath = path.resolve(__dirname, `.env.${env.toUpperCase()}`);
    if (!fs.existsSync(envFilePath)) {
        console.error(`[env.js] ERROR: Env file not found at ${envFilePath}`);
        return;
    }

    dotenv.config({ override: true, path: envFilePath });
    console.log(`[env.js] Loaded environment: ${env.toUpperCase()} (BASEURL=${process.env.BASEURL})`);
}

module.exports = { getEnv };
