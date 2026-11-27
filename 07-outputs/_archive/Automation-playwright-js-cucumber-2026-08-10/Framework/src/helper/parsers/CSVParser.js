const fs = require("fs");
const { parse } = require("csv-parse/sync");

/**
 * Pipe-delimited CSV loader. Per playwright-javascript-standards.md "Test Data
 * Standards" — default separator is "|".
 */
class CSVParser {
    static parseData(filePath, separator = "|") {
        const content = fs.readFileSync(filePath, "utf-8");
        return parse(content, {
            columns: true,
            delimiter: separator,
            skip_empty_lines: true,
            trim: true,
        });
    }
}

module.exports = CSVParser;
