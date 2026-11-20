const { expect } = require("@playwright/test");

/**
 * Hard assertion helpers — throw immediately on failure (fail-fast).
 */
async function assertEquals(actual, expected, message = "") {
    expect(actual, message).toEqual(expected);
}

async function assertTrue(condition, message = "") {
    expect(condition, message).toBeTruthy();
}

async function assertContains(actual, substring, message = "") {
    expect(actual, message).toContain(substring);
}

/**
 * Soft assertion helper — logs a failure without throwing, collecting into `errors`.
 * Call `flush()` at the end of a scenario/step to raise a combined error if any were recorded.
 */
class SoftAssert {
    constructor() {
        this.errors = [];
    }

    equals(actual, expected, message = "") {
        if (actual !== expected) {
            this.errors.push(`${message || "Values not equal"}: expected "${expected}", got "${actual}"`);
        }
    }

    isTrue(condition, message = "") {
        if (!condition) {
            this.errors.push(message || "Expected condition to be true");
        }
    }

    flush() {
        if (this.errors.length > 0) {
            const combined = this.errors.join("\n");
            this.errors = [];
            throw new Error(`Soft assertion failures:\n${combined}`);
        }
    }
}

module.exports = { assertEquals, assertTrue, assertContains, SoftAssert };
