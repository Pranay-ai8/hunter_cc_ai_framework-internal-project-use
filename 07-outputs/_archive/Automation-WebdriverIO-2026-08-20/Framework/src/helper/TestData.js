import testData from '../../data/testdata.json' with { type: 'json' };

/**
 * Single, centralized read of data/testdata.json — the JS-stack equivalent of
 * the Appium-Java sibling framework's PropertyReader.getInstance() for
 * config.properties. Step definitions import from here instead of each
 * duplicating the relative path/JSON-import-assertion syntax, and instead of
 * hardcoding the same values a second time directly in a .feature file.
 */
export default testData;
