/**
 * API path constants.
 *
 * SauceDemo is a stateless React SPA with no publicly documented backend API
 * (see 07-outputs/RequirementAnalysis/RequirementAnalysis.md §13 — "No API endpoints
 * are publicly documented; testing is UI-layer only"). This module exists to satisfy
 * the standard scaffold shape and to centralize any future API path constants if this
 * suite is ever extended with API-level checks. No page object currently depends on it
 * for `page.waitForResponse` matching because there is no network call to match against.
 */
export const ApiEndpoints = {
  // Intentionally empty — no API surface documented for SauceDemo.
} as const;
