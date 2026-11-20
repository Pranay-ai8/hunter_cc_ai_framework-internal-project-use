import { logger } from './logger';

export interface SubStepBadge {
  type: 'info' | 'success' | 'warning' | 'failure' | 'softFailure';
  message: string;
  timestamp: string;
}

/**
 * Collects per-step HTML "badges" (info/success/warning/failure) so the Cucumber HTML
 * report can render a readable sub-step timeline for each scenario, and mirrors every
 * badge to the winston logger.
 */
export class HTMLSubStepLogger {
  private badges: SubStepBadge[] = [];

  private push(type: SubStepBadge['type'], message: string): void {
    this.badges.push({ type, message, timestamp: new Date().toISOString() });
  }

  async info(message: string): Promise<void> {
    logger.info(message);
    this.push('info', message);
  }

  async success(message: string): Promise<void> {
    logger.info(`SUCCESS: ${message}`);
    this.push('success', message);
  }

  async warning(message: string): Promise<void> {
    logger.warn(message);
    this.push('warning', message);
  }

  async logFailure(message: string): Promise<void> {
    logger.error(message);
    this.push('failure', message);
  }

  async softFailure(message: string): Promise<void> {
    logger.warn(`SOFT-FAIL: ${message}`);
    this.push('softFailure', message);
  }

  getBadgesAsHtml(): string {
    return this.badges
      .map((b) => `<span class="badge badge-${b.type}" data-ts="${b.timestamp}">${b.message}</span>`)
      .join('\n');
  }

  reset(): void {
    this.badges = [];
  }
}
