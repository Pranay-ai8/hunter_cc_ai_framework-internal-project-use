// pages/base.page.ts
// Mirror of Framework/src/pages/base.page.ts, re-exported here per Phase 2 of
// 05-agents/playwright-typescript-agent.md ("Generate one additional file: base.page.ts
// in 07-outputs/Automation/PageObjects/"). The Framework copy at
// 07-outputs/Automation/Framework/src/pages/base.page.ts remains the canonical/compiled
// source that ScenarioContext and the framework build actually consume — this file is
// kept identical to it for reviewers browsing PageObjects/ in isolation. Do not diverge
// the two; if BasePage changes, update Framework/src/pages/base.page.ts first and mirror here.

import { Locator, Page } from '@playwright/test';
import { HTMLSubStepLogger } from '../Framework/src/utils/htmlSubStepLogger';
import { findHealedLocator } from '../Framework/src/utils/self-healer';

export default class BasePage {
  constructor(
    protected page: Page,
    protected subStepLogger?: HTMLSubStepLogger,
  ) {}

  async waitForLoadingToComplete(): Promise<void> {
    await this.page.waitForLoadState('domcontentloaded');
  }

  protected async logInfo(message: string): Promise<void> {
    await this.subStepLogger?.info(message);
  }

  protected async logSuccess(message: string): Promise<void> {
    await this.subStepLogger?.success(message);
  }

  protected async logWarning(message: string): Promise<void> {
    await this.subStepLogger?.warning(message);
  }

  protected async logFailure(message: string): Promise<void> {
    await this.subStepLogger?.logFailure(message);
  }

  /**
   * Wraps an interaction with self-healing recovery per
   * 03-standards/playwright-typescript-standards.md's "Self-Healing Locators" section.
   */
  protected async withHeal<T>(locator: Locator, action: (l: Locator) => Promise<T>, hint: string): Promise<T> {
    try {
      await locator.waitFor({ state: 'attached', timeout: 3000 });
      return await action(locator);
    } catch (error) {
      await this.logWarning(`Primary locator failed for "${hint}" — attempting self-heal.`);
      const healResult = await findHealedLocator(this.page, hint, error as Error);

      if (healResult.success && healResult.locator) {
        await this.logWarning(`Self-heal succeeded for "${hint}" (confidence=${healResult.confidence.toFixed(2)}).`);
        return action(healResult.locator);
      }

      await this.logFailure(`Self-heal failed for "${hint}" — rethrowing original error.`);
      throw error;
    }
  }
}
