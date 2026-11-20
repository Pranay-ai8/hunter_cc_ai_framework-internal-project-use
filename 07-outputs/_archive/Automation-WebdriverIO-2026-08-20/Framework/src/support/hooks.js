import { After, AfterAll } from '@cucumber/cucumber';
import { browser } from '@wdio/globals';

After(async function (scenario) {
    const scenarioName = scenario.pickle.name;
    const scenarioStatus = scenario.result.status;

    if (scenarioStatus === 'FAILED') {
        console.error(`Scenario "${scenarioName}" failed!`);
        try {
            await browser.saveScreenshot(`./allure-results/failure-${Date.now()}.png`);
        } catch (e) {
            console.error('Could not capture failure screenshot:', e.message);
        }
    }
});

// Live-run finding: an explicit `mobile: terminateApp` AfterAll cleanup call
// was tried here and removed. On this sandboxed emulator, the underlying
// `adb shell am force-stop ...` intermittently hangs for 20s+ per attempt
// (observed 3 consecutive 20s timeouts/retries under multi-worker load).
// Cucumber enforces its own hook-level timeout independently of any
// try/catch wrapped around the awaited call, so a slow/hanging call here
// aborts the whole worker process with "an AfterAll hook errored, process
// exiting" regardless of error handling inside the hook body. WebdriverIO's
// own end-of-session teardown (`deleteSession`, run automatically after each
// spec file) already closes the Appium session correctly; explicitly
// force-stopping the app first was cosmetic, not functionally required, so
// it was removed rather than chasing a longer timeout budget.
AfterAll(async function () {
    console.log('All scenarios completed for this spec file.');
});
