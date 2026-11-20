const { expect } = require("@playwright/test");

/**
 * Soft/hard assertion helpers. Per playwright-javascript-standards.md "Assertions".
 */
class SoftAssert {
    constructor() {
        this.errors = [];
    }

    assertTrue(condition, message) {
        if (!condition) this.errors.push(message);
    }

    assertFalse(condition, message) {
        if (condition) this.errors.push(message);
    }

    assertEqual(actual, expected, message) {
        if (actual !== expected) this.errors.push(`${message} — expected: ${expected}, actual: ${actual}`);
    }

    assertAll() {
        if (this.errors.length > 0) {
            throw new Error(`Soft assertion failures:\n- ${this.errors.join("\n- ")}`);
        }
    }
}

function assertTrue(condition, message) {
    expect(condition, message).toBeTruthy();
}

function assertFalse(condition, message) {
    expect(condition, message).toBeFalsy();
}

function assertEqual(actual, expected, message) {
    expect(actual, message).toBe(expected);
}

module.exports = { SoftAssert, assertTrue, assertFalse, assertEqual };
