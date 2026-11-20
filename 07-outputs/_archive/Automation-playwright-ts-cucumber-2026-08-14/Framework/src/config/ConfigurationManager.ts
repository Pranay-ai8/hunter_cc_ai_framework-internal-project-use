import * as fs from 'fs';
import * as path from 'path';
import * as dotenv from 'dotenv';
import { getActiveEnv } from './env';

export interface AppConfig {
  env: string;
  baseUrl: string;
  browser: 'chromium' | 'chrome' | 'edge' | 'firefox' | 'webkit';
  headless: boolean;
  defaultTimeout: number;
  navigationTimeout: number;
  slowMo: number;
}

/**
 * Singleton configuration loader.
 *
 * Loads `resources/.env.<ENV>` (selected via the `ENV` process variable, default STG)
 * merged with `resources/configuration.json` defaults. Never hardcode base URLs,
 * tokens, or browser choices in page objects or steps — always read from here.
 */
class ConfigurationManagerImpl {
  private static instance: ConfigurationManagerImpl;
  private appConfig: AppConfig;

  private constructor() {
    const env = getActiveEnv();
    const envFile = path.resolve(process.cwd(), `resources/.env.${env}`);
    if (fs.existsSync(envFile)) {
      dotenv.config({ path: envFile });
    }

    const defaultsPath = path.resolve(process.cwd(), 'resources/configuration.json');
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    let defaults: Record<string, any> = {};
    if (fs.existsSync(defaultsPath)) {
      defaults = JSON.parse(fs.readFileSync(defaultsPath, 'utf-8'));
    }

    this.appConfig = {
      env,
      baseUrl: process.env.baseUrl ?? process.env.BASE_URL ?? defaults.baseUrl ?? 'https://www.saucedemo.com/',
      browser: (process.env.BROWSER ?? defaults.browser ?? 'chromium') as AppConfig['browser'],
      headless: process.env.HEADLESS ? process.env.HEADLESS === 'true' : (defaults.headless ?? true),
      defaultTimeout: Number(process.env.DEFAULT_TIMEOUT ?? defaults.defaultTimeout ?? 15000),
      navigationTimeout: Number(process.env.NAVIGATION_TIMEOUT ?? defaults.navigationTimeout ?? 30000),
      slowMo: Number(process.env.SLOW_MO ?? defaults.slowMo ?? 0),
    };
  }

  public static config(): AppConfig {
    if (!ConfigurationManagerImpl.instance) {
      ConfigurationManagerImpl.instance = new ConfigurationManagerImpl();
    }
    return ConfigurationManagerImpl.instance.appConfig;
  }
}

export const ConfigurationManager = {
  config: (): AppConfig => ConfigurationManagerImpl.config(),
};
