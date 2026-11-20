import * as fs from 'fs/promises';
import * as path from 'path';
import { existsSync } from 'fs';

export class DataProviderError extends Error {
    constructor(message: string, public readonly cause?: unknown) {
        super(message);
        this.name = 'DataProviderError';
    }
}

export class CSVParser {
    static async parseData(
        filePath: string,
        separator: string = '|'
    ): Promise<Record<string, string>[]> {
        if (!filePath || filePath.trim().length === 0) {
            throw new DataProviderError('filePath must be a non-empty string.');
        }

        let fileContent: string;
        try {
            fileContent = await fs.readFile(filePath, 'utf8');
            fileContent = fileContent.replace(/^\uFEFF/, ''); // remove BOM
        } catch (err) {
            throw new DataProviderError(`Error reading CSV file at ${filePath}`, err);
        }

        const data: Record<string, string>[] = [];
        const lines = fileContent
            .split(/\r?\n/)
            .map(l => l.trim())
            .filter(l => l.length > 0);

        let headers: string[] = [];

        for (const line of lines) {
            if (line.startsWith('#') || line.startsWith('!')) continue;

            const cols = line.split(separator).map(c => c.trim());

            if (cols.some(c => c.toLowerCase() === 'key') && cols.some(c => c.toLowerCase() === 'env')) {
                headers = cols;
                continue;
            }

            if (!headers.length) continue;

            const row: Record<string, string> = {};
            headers.forEach((h, i) => row[h] = cols[i] ?? '');
            data.push(row);
        }

        return data;
    }

    /**
     * Dynamically locates and fetches test data row by scenario key and environment.
     */
    static async getTestDataByKey(
        testCaseKey: string,
        fileName: string = 'guests_testData.csv'
    ): Promise<Record<string, string>> {
        const env = process.env.ENV || 'STG';
        
        // Candidate paths to check in order
        const possiblePaths = [
            path.resolve(process.cwd(), 'src/helper/util/test-data', fileName),
            path.resolve(__dirname, '../util/test-data', fileName),
            path.resolve(process.cwd(), 'src/util/test-data', fileName),
            path.resolve(__dirname, '../../util/test-data', fileName),
            path.resolve(__dirname, '../../../util/test-data', fileName)
        ];

        const csvPath = possiblePaths.find(p => existsSync(p));

        if (!csvPath) {
            throw new DataProviderError(
                `CSV file '${fileName}' not found. Checked locations:\n - ${possiblePaths.join('\n - ')}`
            );
        }

        const records = await CSVParser.parseData(csvPath);

        const matchedRow = records.find(
            row => row['Key'] === testCaseKey && row['Env'] === env
        );

        if (!matchedRow) {
            throw new DataProviderError(
                `Test data record not found for Key: '${testCaseKey}' and Env: '${env}' in ${csvPath}`
            );
        }

        return matchedRow;
    }
}

export const getTestDataByKey = CSVParser.getTestDataByKey;