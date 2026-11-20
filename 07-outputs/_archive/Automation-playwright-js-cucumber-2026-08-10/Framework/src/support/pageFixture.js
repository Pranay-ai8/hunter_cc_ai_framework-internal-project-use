/**
 * The shared fixture object — page, logger, testData, pages registry.
 * This stack does NOT use a World/ScenarioContext class.
 * Per playwright-javascript-standards.md "Shared Fixture (fixture) Standards".
 */


const { When, Then } = require("@cucumber/cucumber");
const { expect } = require("@playwright/test");

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

module.exports = { fixture, When, Then, expect };
