package com.cresanmanagement.eyeq.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Singleton device-readiness/identity resolver for a LOCAL run (SETUP=Local). Shells out to {@code adb}
 * to discover whichever Android device/emulator is already connected and booted - it has no ability to
 * boot one itself (that is a generation-time/pre-run prerequisite, see
 * 03-standards/appium-java-standards.md's "Device Readiness" section).
 *
 * This project is Android-only; the iOS branches (idevice_id/idevicename) from
 * 02-templates/AppiumFramework.md are intentionally omitted since RUN_ON is always ANDROID_APP here -
 * add them back if iOS support is ever requested for this project.
 */
public final class DeviceConfig {

    private static final Logger logger = LogManager.getLogger(DeviceConfig.class.getName());
    private static DeviceConfig instance;

    private DeviceConfig() {
    }

    public static synchronized DeviceConfig getInstance() {
        if (instance == null) {
            instance = new DeviceConfig();
        }
        return instance;
    }

    public String getDeviceID(String runOn) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if ("ANDROID_APP".equalsIgnoreCase(runOn)) {
            return getAndroidUDID();
        }
        throw new IllegalArgumentException("Unsupported RUN_ON for this Android-only project: " + runOn);
    }

    public String getPlatformVersion(String runOn) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if ("ANDROID_APP".equalsIgnoreCase(runOn)) {
            return getAndroidOsVersion();
        }
        throw new IllegalArgumentException("Unsupported RUN_ON for this Android-only project: " + runOn);
    }

    public String getDeviceName(String runOn) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        if ("ANDROID_APP".equalsIgnoreCase(runOn)) {
            return getAndroidDeviceName();
        }
        throw new IllegalArgumentException("Unsupported RUN_ON for this Android-only project: " + runOn);
    }

    /**
     * True when the resolved Android target is an AVD emulator rather than real hardware. Every AVD
     * emulator's ADB serial is assigned by the emulator console itself and always has the form
     * {@code emulator-NNNN} (e.g. {@code emulator-5554}) - a real device's serial is its own
     * manufacturer-assigned alphanumeric ID (e.g. {@code 8AHY0LBTH}) and never starts with that prefix.
     * Reuses {@link #getDeviceID(String)}'s already-resolved serial rather than re-querying adb
     * separately. Added for real-hardware-only UI differences (e.g. the "Fingerprint Available"
     * biometric-enrollment screen confirmed live on a Pixel 3 XL - see
     * {@code OnboardingAuthenticationPage#login}) that an AVD emulator will never show.
     */
    public boolean isRunningOnEmulator(String runOn) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed");
        String deviceId = getDeviceID(runOn);
        return deviceId != null && deviceId.startsWith("emulator-");
    }

    /**
     * Returns the connected device/emulator serial to target. If {@code config.properties}' optional
     * {@code AndroidUDID} key is set, that explicit value wins outright (no auto-detection at all -
     * use this when more than one device/emulator may be attached). Otherwise scans {@code adb devices}
     * for every serial in "device" state (not "unauthorized"/"offline"):
     * <ul>
     *   <li>exactly one ready device -&gt; use it (the common case, unchanged from before);</li>
     *   <li>zero ready devices -&gt; fail fast with a clear message (unchanged from before);</li>
     *   <li>more than one ready device -&gt; fail fast rather than arbitrarily picking the first one -
     *       this is the live-run correction: a prior version of this method always returned the first
     *       match, which would silently target the wrong device (emulator vs. real hardware) the moment
     *       both happened to be attached at once. Set {@code AndroidUDID} in config.properties, or
     *       disconnect all but one device/emulator, to resolve.</li>
     * </ul>
     */
    private String getAndroidUDID() {
        String explicitUdid = PropertyReader.getInstance().getPropValues("config.properties", "AndroidUDID");
        if (explicitUdid != null && !explicitUdid.isBlank()) {
            logger.debug("Using explicit AndroidUDID from config.properties: " + explicitUdid);
            return explicitUdid.trim();
        }

        java.util.List<String> readySerials = new java.util.ArrayList<>();
        for (String line : runAdb("devices")) {
            String trimmed = line.trim();
            if (trimmed.endsWith("\tdevice") || trimmed.matches("^\\S+\\s+device$")) {
                readySerials.add(trimmed.split("\\s+")[0]);
            }
        }

        if (readySerials.isEmpty()) {
            throw new RuntimeException(
                    "No ready Android device/emulator found via 'adb devices'. "
                            + "Confirm a device is connected and booted (state 'device', not 'unauthorized'/'offline') "
                            + "before starting a test run - see 03-standards/appium-java-standards.md's Device Readiness section.");
        }
        if (readySerials.size() > 1) {
            throw new RuntimeException(
                    "Multiple ready Android devices/emulators found via 'adb devices': " + readySerials
                            + " - refusing to arbitrarily pick one. Either disconnect all but the intended "
                            + "target before running, or set config.properties' AndroidUDID key to the exact "
                            + "serial to use (see 03-standards/appium-java-standards.md's Device Readiness section).");
        }
        return readySerials.get(0);
    }

    private String getAndroidOsVersion() {
        String udid = getAndroidUDID();
        for (String line : runAdb("-s", udid, "shell", "getprop", "ro.build.version.release")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        throw new RuntimeException("Could not read ro.build.version.release from device " + udid);
    }

    private String getAndroidDeviceName() {
        String udid = getAndroidUDID();
        for (String line : runAdb("-s", udid, "shell", "getprop", "ro.product.model")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        // Fall back to the serial itself (e.g. "emulator-5554") if the model prop is unavailable.
        return udid;
    }

    private java.util.List<String> runAdb(String... args) {
        java.util.List<String> output = new java.util.ArrayList<>();
        try {
            String[] command = new String[args.length + 1];
            command[0] = "adb";
            System.arraycopy(args, 0, command, 1, args.length);
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to execute adb command: " + String.join(" ", args), e);
            throw new RuntimeException("Failed to execute adb command: " + String.join(" ", args), e);
        }
        return output;
    }
}
