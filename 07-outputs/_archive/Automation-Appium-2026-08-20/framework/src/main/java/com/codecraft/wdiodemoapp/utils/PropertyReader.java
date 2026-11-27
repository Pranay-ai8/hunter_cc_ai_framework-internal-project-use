package com.codecraft.wdiodemoapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyReader {

    private static PropertyReader instance;
    private final Map<String, Properties> cache = new HashMap<>();

    private PropertyReader() {
    }

    public static PropertyReader getInstance() {
        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }

    private Properties load(String fileName) {
        if (cache.containsKey(fileName)) {
            return cache.get(fileName);
        }
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Unable to locate " + fileName + " on the classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + fileName, e);
        }
        cache.put(fileName, props);
        return props;
    }

    public HashMap<String, String> getPropValues(String fileName) {
        Properties props = load(fileName);
        HashMap<String, String> map = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        return map;
    }

    public String getPropValues(String fileName, String key) {
        return load(fileName).getProperty(key);
    }
}
