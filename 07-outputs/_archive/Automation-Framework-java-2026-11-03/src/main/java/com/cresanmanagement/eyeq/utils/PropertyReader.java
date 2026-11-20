package com.cresanmanagement.eyeq.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton .properties file reader. Reads from the classpath root (src/main/resources), which is
 * where config.properties lives after the Maven build places it on the test classpath.
 */
public final class PropertyReader {

    private static final Logger logger = LogManager.getLogger(PropertyReader.class.getName());
    private static PropertyReader instance;

    private PropertyReader() {
    }

    public static synchronized PropertyReader getInstance() {
        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }

    /**
     * Reads every key/value pair out of {@code fileName} (e.g. "config.properties") from the classpath.
     */
    public HashMap<String, String> getPropValues(String fileName) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        HashMap<String, String> propMap = new HashMap<>();
        Properties properties = loadProperties(fileName);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            propMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return propMap;
    }

    /**
     * Reads a single {@code key} out of {@code fileName} from the classpath.
     */
    public String getPropValues(String fileName, String key) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        Properties properties = loadProperties(fileName);
        return properties.getProperty(key);
    }

    private Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = PropertyReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + fileName + " on the classpath (expected under src/main/resources/)");
            }
            properties.load(input);
        } catch (IOException e) {
            logger.error("Failed to load properties file: " + fileName, e);
            throw new RuntimeException("Failed to load properties file: " + fileName, e);
        }
        return properties;
    }
}
