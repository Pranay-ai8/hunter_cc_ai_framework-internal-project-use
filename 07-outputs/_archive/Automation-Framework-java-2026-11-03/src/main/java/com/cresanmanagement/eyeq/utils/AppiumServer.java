package com.cresanmanagement.eyeq.utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Singleton wrapper around Appium's {@link AppiumDriverLocalService} for programmatically
 * starting/stopping a local Appium server, as an alternative to running {@code appium} manually
 * before a test run.
 *
 * Windows is this project's primary dev/CI target (per the generation environment); the Mac branch is
 * kept for portability but is not exercised here.
 */
public final class AppiumServer {

    private static final Logger logger = LogManager.getLogger(AppiumServer.class.getName());
    private static AppiumServer instance;
    private AppiumDriverLocalService service;

    private AppiumServer() {
    }

    public static synchronized AppiumServer getInstance() {
        if (instance == null) {
            instance = new AppiumServer();
        }
        return instance;
    }

    /**
     * Performs a best-effort dependency check (Node.js on PATH) before attempting to build the
     * service. Appium itself will fail fast with a clear error if Node/Appium are missing, but this
     * surfaces the same problem earlier with a friendlier message.
     */
    private void dependencyCheck() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        try {
            Process node = new ProcessBuilder("node", "-v").redirectErrorStream(true).start();
            node.waitFor();
            if (node.exitValue() != 0) {
                logger.warn("Node.js was found on PATH but returned a non-zero exit code when checking its version.");
            }
        } catch (Exception e) {
            logger.warn("Node.js does not appear to be on PATH - 'appium' (and therefore AppiumServer.init()) requires it. "
                    + "Install Node.js, or run a local Appium server manually instead of using this programmatic path.", e);
        }
    }

    public void init() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        dependencyCheck();

        String os = System.getProperty("os.name").toLowerCase();
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("0.0.0.0")
                .usingPort(4723)
                .withTimeout(Duration.ofSeconds(60));

        if (os.contains("win") || os.contains("nux") || os.contains("nix")) {
            // Default executable discovery (node_modules/.bin/appium or a global npm install) works for
            // both Windows and Linux; override with .usingDriverExecutable(new File(...)) if Appium is
            // installed somewhere non-standard.
            service = AppiumDriverLocalService.buildService(builder);
        } else {
            // Mac: identical builder; kept as a separate branch per the template's documented contract
            // in case Mac-specific flags (e.g. Carthage-built WebDriverAgent paths) are needed later.
            service = AppiumDriverLocalService.buildService(builder);
        }

        service.start();
        logger.info("Appium server started at " + service.getUrl());
    }

    public boolean isRunning() {
        return service != null && service.isRunning();
    }

    public void tearDown() {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if (service != null) {
            service.stop();
            logger.info("Appium server stopped.");
        }
    }
}
