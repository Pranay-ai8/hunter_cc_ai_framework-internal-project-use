package com.codecraft.saucedemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * JSON test-data loader (Jackson-backed) — available alongside {@link CSVLoader} per
 * 03-standards/selenium-standards.md "Test Data". Not required by the current SauceDemo
 * test data (CSV covers all modules) but kept scaffolded for future modules that prefer
 * structured/nested JSON fixtures.
 */
public final class JsonLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonLoader() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadAsMap(String path) {
        try {
            return MAPPER.readValue(new File(path), Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON test data: " + path, e);
        }
    }

    public static <T> T load(String path, Class<T> type) {
        try {
            return MAPPER.readValue(new File(path), type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON test data: " + path, e);
        }
    }
}
