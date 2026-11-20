package com.codecraft.saucedemo.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Captures failure-time evidence per 03-standards/selenium-standards.md "Reporting &amp;
 * Logging" — screenshots saved under {@code test-output/screenshots/}.
 */
public final class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final Path SCREENSHOT_DIR = Paths.get("test-output", "screenshots");

    private ScreenshotUtils() {
    }

    public static String capture(WebDriver driver, String testName) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fileName = testName + "_" + Instant.now().toEpochMilli() + ".png";
            Path dest = SCREENSHOT_DIR.resolve(fileName);
            Files.copy(src.toPath(), dest);
            log.info("Screenshot captured: {}", dest);
            return dest.toString();
        } catch (IOException | ClassCastException e) {
            log.warn("Failed to capture screenshot for {}: {}", testName, e.getMessage());
            return null;
        }
    }
}
