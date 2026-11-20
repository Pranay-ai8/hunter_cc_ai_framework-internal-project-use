package com.cresanmanagement.eyeq.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads a header-row CSV (see src/test/resources/testdata/*.csv) and returns the row matching a given
 * key (matched against the CSV's "Key" column) as a {@code Map<String, String>}.
 *
 * Tolerant of blank trailing columns: {@link #parseCsvLine} always returns as many fields as the line has
 * commas + 1, never silently dropping trailing empty fields the way the default {@code String.split(",")}
 * does.
 *
 * <p><b>Fixed 2026-10-27</b> after a confirmed live failure: this previously did a naive
 * {@code line.split(",", -1)} with no quote-awareness, so an RFC4180-quoted value containing its own comma
 * (e.g. global.csv's {@code KnownTicketCreatedOn}, {@code "18 Mar 2024, 9:18 PM"}) was silently split into
 * two separate fields at that embedded comma, truncating the actual "Value" column to
 * {@code "18 Mar 2024} (with the stray leading quote left in, since {@code trim()} only strips whitespace)
 * - confirmed via {@code verifyTicketDetailsDisplaysTitleNumberAndTimestamps}'s real failure message
 * ("expected [\"18 Mar 2024] but found [18 Mar 2024, 9:18 PM]" - the real UI value was actually correct;
 * the CSV-sourced expected value was the one that was broken). Replaced with a proper quote-aware parser.
 */
public final class CSVLoader {

    private static final Logger logger = LogManager.getLogger(CSVLoader.class.getName());

    private CSVLoader() {
    }

    public static Map<String, String> loadRowByKey(String csvPath, String key) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + csvPath + " / key=" + key);
        Path path = Paths.get(csvPath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new RuntimeException("CSV file is empty: " + csvPath);
            }
            String[] headers = parseCsvLine(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] values = parseCsvLine(line);
                if (values.length > 0 && values[0].trim().equals(key)) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        String value = i < values.length ? values[i].trim() : "";
                        row.put(headers[i].trim(), value);
                    }
                    return row;
                }
            }
            throw new RuntimeException("Key '" + key + "' not found in CSV file: " + csvPath);
        } catch (IOException e) {
            logger.error("Failed to read CSV file: " + csvPath, e);
            throw new RuntimeException("Failed to read CSV file: " + csvPath, e);
        }
    }

    /**
     * RFC4180-aware single-line CSV splitter: a comma inside a double-quoted field does not end the field,
     * a doubled quote ({@code ""}) inside a quoted field is an escaped literal quote, and the surrounding
     * quotes of a quoted field are stripped from the returned value (unlike a plain {@code split(",")}).
     */
    private static String[] parseCsvLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
