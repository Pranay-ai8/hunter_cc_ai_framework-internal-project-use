import * as fs from 'fs';
import * as path from 'path';
import { Browser } from '@playwright/test';
import { ConfigurationManager } from '../config/ConfigurationManager';
import { resolvePassword } from '../utils/passwordResolver';
import { logger } from '../utils/logger';

const AUTH_DIR = path.resolve(process.cwd(), '.auth');
const AUTH_FILE = path.join(AUTH_DIR, 'user.json');

export function getAuthStatePath(): string {
  return AUTH_FILE;
}

/**
 * One-time headless login for `standard_user`, persisting storage state to `.auth/user.json`
 * so scenarios tagged `@authenticated` can skip the login steps entirely.
 *
 * No scenario in this suite currently uses `@authenticated` (every login persona is itself
 * under test), but this hook is wired per the standard so it is available without further
 * scaffolding work if a future scenario needs a pre-authenticated session.
 */
export async function saveAuthState(browser: Browser): Promise<void> {
  const { baseUrl } = ConfigurationManager.config();
  const username = process.env.ADMIN_USERNAME ?? 'standard_user';
  const password = resolvePassword('standard_user');

  if (!fs.existsSync(AUTH_DIR)) {
    fs.mkdirSync(AUTH_DIR, { recursive: true });
  }

  const context = await browser.newContext();
  const page = await context.newPage();
  try {
    await page.goto(baseUrl);
    await page.locator('#user-name').fill(username);
    await page.locator('#password').fill(password);
    await page.locator('#login-button').click();
    await page.waitForURL(/inventory\.html/, { timeout: 15000 });
    await context.storageState({ path: AUTH_FILE });
    logger.info(`Saved auth state to ${AUTH_FILE}`);
  } catch (err) {
    logger.warn(`saveAuthState skipped — login precheck failed: ${(err as Error).message}`);
  } finally {
    await context.close();
  }
}
