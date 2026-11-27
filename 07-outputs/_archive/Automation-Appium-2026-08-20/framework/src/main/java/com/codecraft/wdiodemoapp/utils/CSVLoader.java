package com.codecraft.wdiodemoapp.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Comma-delimited CSV loader. A row is selected by its value in the "Key" column.
 */
public final class CSVLoader {

    private CSVLoader() {
    }

    public static Map<String, String> loadRowByKey(String path, String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String[] header = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                // limit=-1 preserves trailing empty fields (e.g. "Jane,Doe," for a blank
                // last column) — the default split(",") silently drops them, leaving the
                // last CSV column entirely absent from the row map instead of "".
                String[] cols = line.split(",", -1);
                if (header == null || "Key".equalsIgnoreCase(cols[0].trim())) {
                    header = cols;
                    continue;
                }
                if (cols.length > 0 && cols[0].trim().equalsIgnoreCase(key)) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < header.length && i < cols.length; i++) {
                        row.put(header[i].trim(), cols[i].trim());
                    }
                    return row;
                }
            }
            throw new IllegalArgumentException("Key not found in " + path + ": " + key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV test data: " + path, e);
        }
    }
}
