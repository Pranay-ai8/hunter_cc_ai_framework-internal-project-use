package com.cresanmanagement.eyeq.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Shell-command execution and small common utilities shared across Page Objects/tests that don't
 * belong in a single-purpose class (Base/DeviceConfig/etc.).
 */
public final class ReusuableUtility {

    private static final Logger logger = LogManager.getLogger(ReusuableUtility.class.getName());

    private ReusuableUtility() {
    }

    /**
     * Runs an arbitrary shell command (e.g. an {@code adb} invocation not already wrapped by
     * {@link DeviceConfig}) and returns its combined stdout/stderr, line by line.
     */
    public static List<String> runShellCommand(String... command) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: " + String.join(" ", command));
        List<String> output = new ArrayList<>();
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            logger.error("Shell command failed: " + String.join(" ", command), e);
        }
        return output;
    }

    public static String currentTimestamp(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * True when neither collection is null/empty and every element of {@code subset} is present in
     * {@code superset} - handy for asserting a filtered list (e.g. Tickets "Open" toggle) is a proper
     * subset of the unfiltered list.
     */
    public static boolean isSubsetOf(List<?> subset, List<?> superset) {
        if (CollectionUtils.isEmpty(subset) || CollectionUtils.isEmpty(superset)) {
            return false;
        }
        return CollectionUtils.isSubCollection(subset, superset);
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
