package com.cresanmanagement.eyeq.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Real-device battery safety check. Emulators always report a full/unlimited battery, so this is a
 * no-op (never aborts) when running against one - it only matters for real hardware, where an
 * unplanned power-off mid-test can lose device/app state (and, worse, leave the device bricked at a
 * bad moment if it dies while flashing/writing).
 *
 * Added for a real-device run on a Pixel device at ~5% battery (charging via USB, but still able to
 * drain under active test load) - checked once before the suite starts (see
 * testng.SuiteCreationListener) and again before every @Test method (see framework.TestBase), so a run
 * aborts cleanly with a clear report instead of silently losing mid-test state to a power-off.
 */
public final class BatteryGuard {

    private static final Logger logger = LogManager.getLogger(BatteryGuard.class.getName());
    private static final Pattern LEVEL_PATTERN = Pattern.compile("level:\\s*(\\d+)");

    private BatteryGuard() {
    }

    /**
     * Thrown when the device's battery is at or below the configured critical threshold - deliberately
     * a distinct type from a generic RuntimeException so callers/listeners can recognize "stop cleanly,
     * this is expected safety behavior" rather than treating it as an ordinary test defect.
     */
    public static class BatteryCriticalException extends RuntimeException {
        public BatteryCriticalException(String message) {
            super(message);
        }
    }

    /**
     * Reads the given device's battery level via {@code adb shell dumpsys battery} and throws
     * {@link BatteryCriticalException} if it is at or below the configured
     * {@code BatteryCriticalThresholdPercent} (config.properties, default 5). Returns the level
     * otherwise. A level that cannot be determined (e.g. the device disappeared) is treated as
     * critical - fail safe, not silently skip the check.
     */
    public static int checkOrAbort(String deviceSerial) {
        int level = readBatteryLevel(deviceSerial);
        int threshold = readThreshold();

        if (level <= threshold) {
            throw new BatteryCriticalException(
                    "Device " + deviceSerial + " battery is at " + level + "% (threshold " + threshold
                            + "%) - stopping cleanly rather than risking mid-test state loss to a power-off. "
                            + "Charge the device and re-run.");
        }
        logger.debug("Battery check OK for " + deviceSerial + ": " + level + "% (threshold " + threshold + "%)");
        return level;
    }

    private static int readThreshold() {
        try {
            String configured = PropertyReader.getInstance().getPropValues("config.properties", "BatteryCriticalThresholdPercent");
            if (configured != null && !configured.isBlank()) {
                return Integer.parseInt(configured.trim());
            }
        } catch (Exception e) {
            logger.warn("Could not read BatteryCriticalThresholdPercent from config.properties - defaulting to 5", e);
        }
        return 5;
    }

    private static int readBatteryLevel(String deviceSerial) {
        try {
            Process process = new ProcessBuilder("adb", "-s", deviceSerial, "shell", "dumpsys", "battery")
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = LEVEL_PATTERN.matcher(line);
                    if (matcher.find()) {
                        process.waitFor();
                        return Integer.parseInt(matcher.group(1));
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to read battery level for device " + deviceSerial + " - treating as critical (fail safe)", e);
        }
        return 0;
    }
}
