import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || 'https://www.saucedemo.com/',
    specPattern: 'cypress/e2e/**/*.cy.js',
    supportFile: 'cypress/support/e2e.js',
    fixturesFolder: 'cypress/fixtures',
    viewportWidth: 1366,
    viewportHeight: 768,
    defaultCommandTimeout: 10000,
    requestTimeout: 15000,
    video: true,
    screenshotOnRunFailure: true,
    retries: { runMode: 2, openMode: 0 },
    env: {
      grepFilterSpecs: true,
      grepOmitFiltered: true,
    },
    async setupNodeEvents(on, config) {
      const { default: registerMochawesome } = await import('cypress-mochawesome-reporter/plugin.js');
      registerMochawesome(on);

      const grepPlugin = await import('@cypress/grep/src/plugin.js');
      (grepPlugin.default || grepPlugin)(config);

      on('task', {
        log(message) {
          console.log(message);
          return null;
        },
      });

      return config;
    },
  },
  reporter: 'cypress-mochawesome-reporter',
  reporterOptions: {
    reportDir: 'cypress/reports/mochawesome',
    charts: true,
    overwrite: false,
    html: true,
    json: true,
  },
});
