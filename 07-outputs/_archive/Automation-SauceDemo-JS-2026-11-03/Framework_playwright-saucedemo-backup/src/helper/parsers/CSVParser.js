const fs = require("fs");
const { parse } = require("csv-parse/sync");

class CSVParser {
    /**
     * Parses a delimited CSV file into an array of row objects keyed by header.
     * @param {string} filePath absolute or relative path to the CSV file
     * @param {string} separator field delimiter (SauceDemo suite uses "|")
     * @returns {Array<Object>}
     */
    static parseData(filePath, separator = "|") {
        if (!fs.existsSync(filePath)) {
            throw new Error(`[CSVParser] Test data file not found: ${filePath}`);
        }
        const raw = fs.readFileSync(filePath, "utf8");
        return parse(raw, {
            columns: true,
            delimiter: separator,
            skip_empty_lines: true,
            trim: true
        });
    }
}

module.exports = CSVParser;
