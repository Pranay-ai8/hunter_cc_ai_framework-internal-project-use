import dotenv from 'dotenv';
dotenv.config();

export const config = {
    //
    // ====================
    // Runner Configuration
    // ====================
    runner: 'local',

    //
    // ==================
    // Specify Test Files
    // ==================
    specs: [
        './features/**/*.feature',
    ],
    exclude: [],

    //
    // ============
    // Capabilities
    // ============
    maxInstances: 1,

    // Connect directly to an already-running Appium server rather than having
    // @wdio/appium-service spawn its own. This environment intentionally runs
    // Appium with an isolated APPIUM_HOME (see README "Prerequisites") to avoid
    // root-owned files left by an earlier `sudo npm install` — letting a second,
    // service-managed Appium process spin up under the default APPIUM_HOME would
    // not see the already-installed uiautomator2 driver and would either fail or
    // try to reinstall it. `@wdio/appium-service` is still kept as a
    // devDependency (see package.json) for teams that DO want wdio to manage the
    // Appium lifecycle itself — just add `services: ['appium']` back and remove
    // the hostname/port/path block below to opt into that instead.
    hostname: process.env.APPIUM_HOST || '127.0.0.1',
    port: Number(process.env.APPIUM_PORT) || 4723,
    path: '/',

    // Live-run finding (same bug class already fixed in the sibling
    // Appium-Java framework's InitiateDriver): each spec file gets its own
    // Appium session, but without forceAppLaunch/shouldTerminateApp the app
    // process itself is never relaunched — Appium just re-attaches to
    // whatever the PREVIOUS spec file left running (including any open
    // dialog/drawer). Confirmed via failure screenshots: a spec's "Given the
    // app is launched" step (which only checks that the bottom tab bar
    // exists) was seeing a side-menu drawer left open by a prior spec's last
    // scenario — the tab bar isn't rendered while that drawer is open, so the
    // step could never succeed regardless of timeout. Applies to both
    // platforms below, not just Android.
    capabilities: [
        process.env.RUN_ON === 'iOS_APP'
            ? {
                platformName: 'iOS',
                'appium:deviceName': process.env.IOS_DEVICE_NAME || 'iPhone 15',
                'appium:platformVersion': process.env.IOS_PLATFORM_VERSION || '17.2',
                'appium:udid': process.env.IOS_UDID,
                'appium:automationName': 'XCUITest',
                'appium:bundleId': process.env.IOS_BUNDLE_ID || 'org.wdiodemoapp',
                'appium:app': process.env.IOS_APP_PATH,
                'appium:noReset': true,
                'appium:newCommandTimeout': 240,
                'appium:fullReset': false,
                'appium:forceAppLaunch': true,
                'appium:shouldTerminateApp': true,
                // iOS has no autoGrantPermissions equivalent; permission
                // alerts are real system alerts outside the app's own a11y
                // tree — autoAcceptAlerts (below) is what handles those.
                'appium:autoAcceptAlerts': true,
            }
            : {
                platformName: 'Android',
                'appium:deviceName': process.env.ANDROID_DEVICE_NAME || 'emulator-5554',
                'appium:automationName': 'UiAutomator2',
                'appium:appPackage': process.env.ANDROID_APP_PACKAGE || 'com.wdiodemoapp',
                'appium:appActivity': process.env.ANDROID_APP_ACTIVITY || 'com.wdiodemoapp.MainActivity',
                'appium:app': process.env.ANDROID_APP_PATH,
                'appium:noReset': true,
                'appium:newCommandTimeout': 240,
                'appium:fullReset': false,
                'appium:forceAppLaunch': true,
                'appium:shouldTerminateApp': true,
                'appium:autoGrantPermissions': true,
                // Live-run finding: this sandboxed emulator host runs under heavy,
                // variable CPU contention (observed `uptime` load averages of
                // 15-23 even at "idle") — likely several concurrent sandboxed
                // sessions sharing the same machine. Under that load, session
                // bootstrap (installing/verifying the UiAutomator2 server APKs,
                // starting its instrumentation process, adb port-forwarding) can
                // take several minutes instead of the normal ~10-20s, so every
                // adb-touching Appium capability timeout below is raised well
                // above its default to avoid a false "hung" failure.
                'appium:adbExecTimeout': 90000,
                'appium:androidInstallTimeout': 180000,
                'appium:uiautomator2ServerLaunchTimeout': 90000,
                'appium:uiautomator2ServerInstallTimeout': 90000,
            },
    ],

    //
    // ===================
    // Test Configurations
    // ===================
    logLevel: 'info',
    bail: 0,
    waitforTimeout: 15000,
    // Live-run finding: with the default connectionRetryTimeout (120s) and
    // connectionRetryCount (3), a slow-but-eventually-successful session
    // creation on this contended host got retried by the CLIENT before the
    // server had finished the previous attempt — Appium was left processing
    // several overlapping POST /session calls at once, each one competing for
    // the same adb/instrumentation resources and making every attempt slower
    // still. A single, much longer-lived attempt (no client-side retry)
    // avoids that pileup and lets the one real attempt actually finish.
    connectionRetryTimeout: 300000,
    connectionRetryCount: 0,

    // No 'services' entry — see the capabilities comment above for why this
    // project connects to an externally-managed Appium server instead.
    services: [],

    framework: 'cucumber',

    reporters: [
        'spec',
        ['allure', {
            outputDir: 'allure-results',
            disableWebdriverStepsReporting: true,
            disableWebdriverScreenshotsReporting: false,
            disableMochaHooks: true,
        }],
    ],

    cucumberOpts: {
        require: [
            './src/step-definitions/*.steps.js',
            './src/support/hooks.js',
        ],
        backtrace: false,
        requireModule: [],
        dryRun: false,
        failFast: false,
        name: [],
        snippets: true,
        source: true,
        strict: false,
        tags: '',
        timeout: 120000,
        ignoreUndefinedDefinitions: false,
    },
};
