const { chromium, firefox, webkit } = require("@playwright/test");

/**
 * Launches a browser instance based on the BROWSER env var.
 * Supported values: chrome, chromium, firefox, webkit, safari.
 * Defaults to chromium if BROWSER is unset.
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
        case "chrome": {
            const launchOptions = { headless, channel: "chrome" };
            if (process.env.CHROME_EXECUTABLE) {
                launchOptions.executablePath = process.env.CHROME_EXECUTABLE;
                delete launchOptions.channel;
            }
            try {
                return await chromium.launch(launchOptions);
            } catch (err) {
                // Fall back to bundled Chromium if a system Chrome channel isn't installed.
                console.log(`[browserManager] Could not launch 'chrome' channel (${err.message}). Falling back to bundled Chromium.`);
                return chromium.launch({ headless });
            }
        }
        case "chromium":
        default:
            return chromium.launch({ headless });
    }
}

module.exports = { invokeBrowser };
