import { ScenarioContext } from './ScenarioContext';
import { resolvePassword } from './passwordResolver';

/**
 * Resolves a step-text token against the scenario's resolved CSV data row.
 *
 * `td.<column>` tokens are replaced from `this.testData[<column>]` (loaded by
 * `hooks.ts`'s `Before` hook from the `@DataFile:`/`@Key:` tags on the scenario).
 * `td.password.<persona>` tokens resolve through `passwordResolver` instead, so real
 * password values never sit in the CSV. Any value that does not start with `td.` is
 * returned unchanged (a literal step-text value).
 */
export function resolveTestDataToken(context: ScenarioContext, token: string): string {
  if (!token.startsWith('td.')) return token;

  const ref = token.slice(3);
  if (ref.startsWith('password.')) {
    return resolvePassword(ref);
  }

  const value = context.testData[ref];
  if (value === undefined) {
    throw new Error(`resolveTestDataToken: no column "${ref}" found in resolved test data row for this scenario`);
  }
  return String(value);
}
