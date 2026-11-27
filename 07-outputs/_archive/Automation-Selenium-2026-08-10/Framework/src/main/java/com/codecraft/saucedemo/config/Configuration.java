package com.codecraft.saucedemo.config;

import org.aeonbits.owner.Config;

/**
 * Owner-backed configuration interface, file/classpath sources only.
 *
 * Note: "system:properties" is deliberately NOT listed as a source here. Owner
 * 1.0.12 has a real bug where declaring "system:properties" as a source ahead of
 * file:/classpath: sources breaks fallthrough for any key absent from the actual
 * system properties (verified directly: with it first, {@code baseUrl()} returns
 * null even though config-prd.properties defines it, because Owner never falls
 * through to the file source once "system:properties" doesn't contain the key —
 * see git history for the reproduction). Every accessor here therefore also has
 * no built-in override capability; system-property overrides (-Dbrowser=chrome,
 * -Dheadless=false, etc.) are applied explicitly in {@link ConfigurationManager}
 * instead, which is the single supported entry point for reading config.
 *
 * Per 03-standards/selenium-standards.md "Config" — never hardcode credentials or
 * URLs directly in Page Objects / test classes; always read through
 * {@link ConfigurationManager}, not this interface directly.
 */
@Config.Sources({
        "file:src/main/resources/config-${env}.properties",
        "classpath:config-${env}.properties",
        "file:src/main/resources/config.properties",
        "classpath:config.properties"
})
public interface Configuration extends Config {

    @Key("baseUrl")
    String baseUrl();

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("headless")
    @DefaultValue("true")
    boolean headless();

    @Key("explicitWaitSeconds")
    @DefaultValue("15")
    int explicitWaitSeconds();

    @Key("pageLoadTimeoutSeconds")
    @DefaultValue("30")
    int pageLoadTimeoutSeconds();

    @Key("username")
    @DefaultValue("standard_user")
    String username();

    @Key("password")
    @DefaultValue("secret_sauce")
    String password();
}
