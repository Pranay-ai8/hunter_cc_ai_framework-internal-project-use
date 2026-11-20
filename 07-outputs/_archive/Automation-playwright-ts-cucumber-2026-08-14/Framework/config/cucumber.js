const common = {
  require: [
    'src/utils/ScenarioContext.ts',
    'src/helper/hooks.ts',
    'src/utils/customTypes.ts',
    '../Scripts/steps/**/*.ts',
  ],
  requireModule: ['ts-node/register'],
  format: [
    'progress-bar',
    'html:test-results/cucumber-report.html',
    'json:test-results/cucumber-report.json',
    'junit:test-results/cucumber-report.xml',
  ],
  formatOptions: { snippetInterface: 'async-await' },
  dryRun: false,
  parallel: 1,
};

module.exports = {
  default: {
    ...common,
    paths: ['../Scripts/features/**/*.feature'],
    strict: true,
    retry: 0,
  },
  rerun: {
    ...common,
    paths: ['@rerun.txt'],
  },
};
