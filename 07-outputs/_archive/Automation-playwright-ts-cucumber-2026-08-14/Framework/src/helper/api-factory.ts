import { APIRequestContext, request } from '@playwright/test';
import { ConfigurationManager } from '../config/ConfigurationManager';

/**
 * Builds a Playwright `APIRequestContext`.
 *
 * SauceDemo has no publicly documented API (UI-layer only — see RequirementAnalysis.md §13),
 * so no auth headers are wired here today. This factory exists to satisfy the standard
 * scaffold shape and to make `ScenarioContext.api`/`apiFactory` available for any future
 * API-level test cases without further scaffolding changes.
 */
export class ApiFactory {
  static async build(): Promise<APIRequestContext> {
    const { baseUrl } = ConfigurationManager.config();
    return request.newContext({ baseURL: baseUrl });
  }
}
