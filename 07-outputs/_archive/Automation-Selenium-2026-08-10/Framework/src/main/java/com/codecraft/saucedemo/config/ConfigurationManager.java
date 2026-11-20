package com.codecraft.saucedemo.config;

import org.aeonbits.owner.ConfigCache;

/**
 * Single entry point for reading configuration. Reads {@code System.getProperty("env")}
 * (default "prd" — SauceDemo has one live environment, see TestPlan.md Section 8),
 * then loads config-&lt;env&gt;.properties + config.properties, cached via Owner's ConfigCache.
 *
 * System-property overrides (-Dbrowser=chrome, -Dheadless=false, etc.) are applied
 * explicitly here — via {@code System.getProperty(key, fileValue)} — rather than through
 * Owner's own "system:properties" source mechanism, which {@link Configuration} deliberately
 * does not use (see its Javadoc for why). Every caller should go through these typed
 * accessors instead of calling {@code config().xxx()} directly, so overrides apply uniformly.
 */
public final class ConfigurationManager {

    private ConfigurationManager() {
    }

    public static Configuration config() {
        String env = System.getProperty("env", "prd");
        System.setProperty("env", env);
        return ConfigCache.getOrCreate(Configuration.class);
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", config().baseUrl());
    }

    public static String browser() {
        return System.getProperty("browser", config().browser());
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", String.valueOf(config().headless())));
    }

    public static int explicitWaitSeconds() {
        return Integer.parseInt(System.getProperty("explicitWaitSeconds", String.valueOf(config().explicitWaitSeconds())));
    }

    public static int pageLoadTimeoutSeconds() {
        return Integer.parseInt(System.getProperty("pageLoadTimeoutSeconds", String.valueOf(config().pageLoadTimeoutSeconds())));
    }

    public static String username() {
        return System.getProperty("username", config().username());
    }

    public static String password() {
        return System.getProperty("password", config().password());
    }
}
