/**
 * Environment helpers.
 * Resolves the active environment name from the `ENV` process variable.
 * Defaults to STG when unset (per playwright-typescript-standards.md).
 */
export type EnvName = 'DEV' | 'STG' | 'SBX' | 'PRD';

export function getActiveEnv(): EnvName {
  const raw = (process.env.ENV ?? 'STG').toUpperCase();
  const allowed: EnvName[] = ['DEV', 'STG', 'SBX', 'PRD'];
  return (allowed.includes(raw as EnvName) ? raw : 'STG') as EnvName;
}

export function envFilePath(): string {
  return `resources/.env.${getActiveEnv()}`;
}
