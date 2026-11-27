package com.codecraft.saucedemo.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Comma-delimited CSV loader (header row + a {@code Key} column), per
 * 03-standards/selenium-standards.md "Test Data" — CSV via Apache Commons CSV.
 * A row is selected by its value in the "Key" column.
 */
public final class CSVLoader {

    private CSVLoader() {
    }

    public static Map<String, String> loadRowByKey(String path, String key) {
        try (Reader reader = new FileReader(path)) {
            for (CSVRecord record : CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
                if (key.equalsIgnoreCase(record.get("Key"))) {
                    Map<String, String> row = new LinkedHashMap<>();
                    record.toMap().forEach(row::put);
                    return row;
                }
            }
            throw new IllegalArgumentException("Key not found in " + path + ": " + key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV test data: " + path, e);
        }
    }
}
