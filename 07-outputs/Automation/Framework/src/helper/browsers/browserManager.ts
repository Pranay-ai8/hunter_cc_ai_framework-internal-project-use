import { LaunchOptions, chromium, firefox, webkit } from "@playwright/test";

const options: LaunchOptions = {
    headless: false,            // must be false to see window
    args: [
        "--start-maximized",    // maximizes the OS window
        // The regression suite runs headed and unattended for hours. Chromium throttles JS timers
        // on a backgrounded/occluded window - if the app's SPA relies on a background timer to
        // silently refresh its auth token before it expires, that throttling stops it firing on
        // schedule, and the session dies on a clock that has nothing to do with the real backend
        // token TTL. These flags disable that throttling so timers keep firing normally regardless
        // of window focus/occlusion.
        "--disable-backgrounding-occluded-windows",
        "--disable-renderer-backgrounding",
        "--disable-background-timer-throttling",
    ]
};
export const invokeBrowser = () => {
    const browserType = process.env.npm_config_BROWSER || "chrome";
    switch (browserType) {
        case "chrome":
            return chromium.launch(options);
        case "firefox":
            return firefox.launch(options);
        case "webkit":
            return webkit.launch(options);
        case "edge":
            return chromium.launch({ ...options, channel: "msedge" });
        default:
            throw new Error("Please set the proper browser!")
    }

}