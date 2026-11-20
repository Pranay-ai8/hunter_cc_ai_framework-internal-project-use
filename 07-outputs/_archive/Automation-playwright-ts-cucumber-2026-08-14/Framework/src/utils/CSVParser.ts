import * as fs from 'fs';
import { parse } from 'csv-parse/sync';

/**
 * Single loader for the framework's pipe-delimited CSV test-data format
 * (`resources/data/*.csv`). Do not introduce a second CSV parsing utility.
 */
export class CSVParser {
  static loadRows(filePath: string): Record<string, string>[] {
    const content = fs.readFileSync(filePath, 'utf-8');
    return parse(content, {
      columns: true,
      delimiter: '|',
      skip_empty_lines: true,
      trim: true,
    }) as Record<string, string>[];
  }

  /**
   * Load a single row matching the given `Key` column value.
   */
  static loadRowByKey(filePath: string, key: string): Record<string, string> {
    const rows = CSVParser.loadRows(filePath);
    const row = rows.find((r) => r.Key === key);
    if (!row) {
      throw new Error(`CSVParser: no row found with Key="${key}" in ${filePath}`);
    }
    return row;
  }
}
