import { Page } from "@playwright/test";
import * as path from "path";
import LoginPage from "../pages/loginPage";
import { fixture } from "./pageFixture";

// Kept short on purpose: during a long regression run the shared session can die repeatedly
// (short backend token TTL), and a long cooldown here turns each death into a multi-minute
// cascade of instantly-failing scenarios (every scenario hits the cooldown guard below until
// it expires) instead of a single quick, guarded re-login.
const RELOGIN_COOLDOWN_MS = 60 * 1000;
let lastReloginAt = 0;

export async function ensureSuperAdminSession(page: Page): Promise<void> {
    const onLoginScreen = page.url().includes("/login");
    if (!onLoginScreen) {
        return;
    }

    const now = Date.now();
    if (now - lastReloginAt < RELOGIN_COOLDOWN_MS) {
        const waitMin = Math.ceil((RELOGIN_COOLDOWN_MS - (now - lastReloginAt)) / 60000);
        throw new Error(
            `Shared Super Admin session is gone and a guarded re-login already happened within the last ` +
            `${RELOGIN_COOLDOWN_MS / 60000} minutes (${waitMin}m remaining on cooldown). Refusing another ` +
            `automatic re-login to avoid hammering the backend's rate-limit/account-lock. Investigate why the ` +
            `session keeps dying, then restart the run.`
        );
    }

    fixture.logger?.info("[Session Guard] Shared session expired mid-run. Performing one guarded re-login (cooldown protected)...");
    console.log("[Session Guard] Shared session expired mid-run. Performing one guarded re-login (cooldown protected)...");

    const loginPage = new LoginPage(page);
    await loginPage.navigateToLogin();
    const adminEmail = process.env.SUPER_ADMIN_EMAIL || process.env.SUPERADMIN_USER;
    const adminPassword = process.env.SUPER_ADMIN_PASSWORD || process.env.SUPERADMIN_PASSWORD;
    if (!adminEmail || !adminPassword) {
        throw new Error("SUPER_ADMIN_EMAIL/SUPER_ADMIN_PASSWORD must be set in the environment - no hardcoded fallback credentials.");
    }
    await loginPage.enterCredentials(adminEmail, adminPassword);
    await loginPage.clickSignIn();
    await page.waitForURL(/.*\/dashboard/, { timeout: 30000 });

    lastReloginAt = Date.now();

    const authFile = path.join(__dirname, "../../.auth/admin.json");
    try {
        await page.context().storageState({ path: authFile });
    } catch (_) {
        // Non-fatal: the in-memory page session is already restored even if the snapshot write fails.
    }

    fixture.logger?.info("[Session Guard] Re-login successful. Session refreshed.");
    console.log("[Session Guard] Re-login successful. Session refreshed.");
}
