const dotenv = require("dotenv");
const path = require("path");

/**
 * Loads the active src/helper/env/.env.<ENV> file. ENV must be passed explicitly
 * (e.g. `ENV=PRD npm test`) — SauceDemo has a single live public environment
 * (see TestPlan.md Section 8), so PRD is the only file provided.
 */
function getEnv() {
    const env = process.env.ENV;
    if (!env) {
        console.error("ENV is not set. Pass it explicitly, e.g. `ENV=PRD npm test`.");
        return;
    }
    dotenv.config({ override: true, path: path.resolve(__dirname, `.env.${env}`) });
}

module.exports = { getEnv };
