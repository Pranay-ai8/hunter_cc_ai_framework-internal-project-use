import { Locator, Page } from '@playwright/test';
import { HTMLSubStepLogger } from '../utils/htmlSubStepLogger';
import { findHealedLocator } from '../utils/self-healer';

/**
 * BasePage — every Page Object in this suite extends this class.
 * Constructor signature is always (page, subStepLogger?): `page` is required,
 * `subStepLogger` is optional. Never redeclare `page`/`subStepLogger` in a subclass —
 * they are inherited `protected` fields.
 */
export default class BasePage {
  constructor(
    protected page: Page,
    protected subStepLogger?: HTMLSubStepLogger,
  ) {}

  async waitForLoadingToComplete(): Promise<void> {
    await this.page.waitForLoadState('domcontentloaded');
  }

  /**
   * Self-healing wrapper for state-changing actions (fill/click/selectOption).
   * Attempts the action against the original locator; on failure, asks
   * `findHealedLocator` to find a replacement using `hint` as the keyword query and
   * retries the action against the healed locator if one was found above the
   * confidence threshold. Rethrows the original error otherwise.
   *
   * By design, this must NOT be used for reads/assertions (`isVisible`, `toBeVisible`
   * checks, `assert*` methods, `textContent`, `count`) — silently healing an
   * assertion's target risks turning a real failure into a false pass.
   */
  protected async withHeal<T>(locator: Locator, action: (l: Locator) => Promise<T>, hint: string): Promise<T> {
    try {
      await locator.waitFor({ state: 'attached', timeout: 3000 });
      return await action(locator);
    } catch (error) {
      const healResult = await findHealedLocator(this.page, hint, error as Error);
      if (healResult.success && healResult.locator) {
        await this.logWarning(
          `Locator for "${hint}" failed — healed with confidence ${healResult.confidence.toFixed(2)}`,
        );
        return action(healResult.locator);
      }
      throw error;
    }
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
}
