const { chromium, firefox, webkit } = require("@playwright/test");

/**
 * Launches the configured browser. Reads BROWSER (chrome/chromium/firefox/webkit),
 * with an optional CHROME_EXECUTABLE override. Defaults to chromium headless.
 */
async function invokeBrowser() {
    const browserName = (process.env.BROWSER || "chromium").toLowerCase();
    const headless = process.env.HEADLESS !== "false";

    switch (browserName) {
        case "firefox":
            return firefox.launch({ headless });
        case "webkit":
        case "safari":
            return webkit.launch({ headless });
        case "chrome":
            return chromium.launch({
                headless,
                channel: "chrome",
                executablePath: process.env.CHROME_EXECUTABLE || undefined,
            });
        case "chromium":
        default:
            return chromium.launch({ headless });
    }
}

module.exports = { invokeBrowser };
