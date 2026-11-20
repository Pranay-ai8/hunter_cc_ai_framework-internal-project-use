package com.cresanmanagement.eyeq.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

/**
 * Minimal JSON test-data loader for any ad-hoc JSON fixture a Page Object/test needs beyond the
 * primary CSV + {@code @TestDataSource} pattern (see 03-standards/appium-java-standards.md's "Test
 * Data Management" section) - e.g. a nested structure CSV rows can't express cleanly.
 */
public final class ReadJson {

    private static final Logger logger = LogManager.getLogger(ReadJson.class.getName());

    private ReadJson() {
    }

    public static JSONObject readJsonObject(String filePath) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + filePath);
        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(reader);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.error("Failed to read/parse JSON file: " + filePath, e);
            throw new RuntimeException("Failed to read/parse JSON file: " + filePath, e);
        }
    }

    public static JSONArray readJsonArray(String filePath) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + filePath);
        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(reader);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.error("Failed to read/parse JSON file: " + filePath, e);
            throw new RuntimeException("Failed to read/parse JSON file: " + filePath, e);
        }
    }

    public static String getValue(String filePath, String key) {
        JSONObject obj = readJsonObject(filePath);
        Object value = obj.get(key);
        return value == null ? null : value.toString();
    }
}
