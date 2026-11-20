// Lives at 07-outputs/Automation/ (one level above Framework/) rather than inside
// Framework/, because ESLint 9's flat config restricts linting to files under the
// config file's own directory tree ("base path") — since this generation's Page
// Objects/Scripts are siblings of Framework/ (per the orchestrator's literal Mode A
// save table), the config must sit at their common ancestor to lint all three.
import tsParser from '@typescript-eslint/parser';
import tsPlugin from '@typescript-eslint/eslint-plugin';
import prettierConfig from 'eslint-config-prettier';

export default [
  {
    ignores: ['**/dist/**', '**/node_modules/**', '**/test-results/**', '**/*.js'],
  },
  {
    files: ['Framework/src/**/*.ts', 'PageObjects/**/*.ts', 'Scripts/**/*.ts'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 2021,
        sourceType: 'module',
      },
    },
    plugins: {
      '@typescript-eslint': tsPlugin,
    },
    rules: {
      '@typescript-eslint/no-explicit-any': 'warn',
      '@typescript-eslint/no-floating-promises': 'off',
      'no-console': 'warn',
    },
  },
  prettierConfig,
];
