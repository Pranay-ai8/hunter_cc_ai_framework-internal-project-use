import * as fs from 'fs';
import * as path from 'path';

let cache: Record<string, string> | null = null;

function loadPasswordMap(): Record<string, string> {
  if (cache) return cache;
  const file = path.resolve(process.cwd(), 'resources/data/.env.passwords');
  const map: Record<string, string> = {};
  if (fs.existsSync(file)) {
    const lines = fs.readFileSync(file, 'utf-8').split('\n');
    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) continue;
      const [key, ...rest] = trimmed.split('=');
      if (key) map[key.trim()] = rest.join('=').trim();
    }
  }
  cache = map;
  return map;
}

/**
 * Resolves a password value referenced by test data (e.g. `td.password.standard`)
 * from `resources/data/.env.passwords` — never place real passwords inline in CSV data.
 * Falls back to returning the input unchanged if it does not match a known key
 * (covers cases where a CSV column already contains a literal, non-sensitive value).
 */
export function resolvePassword(reference: string): string {
  const map = loadPasswordMap();
  return map[reference] ?? reference;
}
