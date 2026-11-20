import { defineParameterType } from '@cucumber/cucumber';

/**
 * Custom Cucumber parameter types.
 *
 * {jObj} — parses a JSON object literal embedded in step text.
 * {list}  — parses a comma-separated list into string[].
 */
defineParameterType({
  name: 'jObj',
  regexp: /(\{.*\})/,
  transformer: (s: string) => JSON.parse(s),
});

defineParameterType({
  name: 'list',
  regexp: /([^"]+)/,
  transformer: (s: string) => s.split(',').map((v) => v.trim()),
});
