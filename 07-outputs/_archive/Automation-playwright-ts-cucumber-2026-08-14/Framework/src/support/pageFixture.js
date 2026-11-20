/**
 * The shared fixture object — page, logger, testData, pages registry.
 * This stack does NOT use a World/ScenarioContext class.
 * Per playwright-javascript-standards.md "Shared Fixture (fixture) Standards".
 */
const fixture = {
    page: undefined,
    browser: undefined,
    context: undefined,
    logger: undefined,
    subStepLogger: undefined,
    testData: {},
    allTestData: [],
    env: "",
    pages: {},
};

module.exports = { fixture };
