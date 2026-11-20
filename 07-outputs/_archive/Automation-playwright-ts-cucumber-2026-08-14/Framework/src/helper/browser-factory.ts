import { Browser, chromium, firefox, webkit } from '@playwright/test';
import { ConfigurationManager } from '../config/ConfigurationManager';
import { logger } from '../utils/logger';

/**
 * Launches the browser named in `ConfigurationManager.config().browser`.
 * Supports chromium, chrome, edge, firefox, webkit.
 */
export class BrowserFactory {
  static async launch(): Promise<Browser> {
    const { browser, headless, slowMo } = ConfigurationManager.config();
    logger.info(`Launching browser: ${browser} (headless=${headless})`);

    switch (browser) {
      case 'firefox':
        return firefox.launch({ headless, slowMo });
      case 'webkit':
        return webkit.launch({ headless, slowMo });
      case 'chrome':
        return chromium.launch({ headless, slowMo, channel: 'chrome' });
      case 'edge':
        return chromium.launch({ headless, slowMo, channel: 'msedge' });
      case 'chromium':
      default:
        return chromium.launch({ headless, slowMo });
    }
  }
}
