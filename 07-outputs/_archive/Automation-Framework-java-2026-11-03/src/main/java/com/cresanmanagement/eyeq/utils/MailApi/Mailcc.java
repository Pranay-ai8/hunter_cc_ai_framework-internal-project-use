package com.cresanmanagement.eyeq.utils.MailApi;

import com.cresanmanagement.eyeq.utils.APIBase;
import com.cresanmanagement.eyeq.utils.PropertyReader;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.Map;

/**
 * Client for a self-hosted mail-catcher service (MailHog/MailCatcher-style REST API: {@code GET
 * /api/v2/messages} returning a JSON array of captured messages) used when the test environment's
 * outbound mail is routed to an internal catch-all SMTP sink rather than a real mailbox provider like
 * Mailinator.
 *
 * The base URL is read from config.properties key {@code MailCatcherUrl} - never hardcoded, since the
 * real test-environment mail-catcher endpoint (if one exists at all for this project) was not
 * confirmed during Requirement Analysis (see FunctionalTestCases.md Section 4.3 / GapAnalysis.md
 * GAP-ENV-01, the same unconfirmed-mailbox-capability gap {@link Gmail} and {@link Mailinator} note).
 */
public class Mailcc extends APIBase {

    private static final Logger logger = LogManager.getLogger(Mailcc.class.getName());

    private final String baseUrl;

    public Mailcc() {
        super();
        Map<String, String> config = PropertyReader.getInstance().getPropValues("config.properties");
        this.baseUrl = config.get("MailCatcherUrl");
    }

    /**
     * Returns every captured message as a raw JSON array (caller filters by recipient/subject).
     */
    public JSONArray getAllMessages() {
        requireConfig();
        try (Response response = get(baseUrl + "/api/v2/messages", null)) {
            String body = response.body() != null ? response.body().string() : "[]";
            return (JSONArray) new JSONParser().parse(body);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.error("Failed to fetch mail-catcher messages from " + baseUrl, e);
            throw new RuntimeException("Failed to fetch mail-catcher messages from " + baseUrl, e);
        }
    }

    public void deleteAllMessages() {
        requireConfig();
        try (Response response = delete(baseUrl + "/api/v1/messages", null)) {
            if (!response.isSuccessful()) {
                logger.warn("Mail-catcher delete-all returned HTTP " + response.code());
            }
        }
    }

    private void requireConfig() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "Mailcc is not configured (missing MailCatcherUrl in config.properties). Per "
                            + "GapAnalysis.md GAP-ENV-01, no test-mailbox/mail-catcher capability was confirmed for "
                            + "this project - do not hardcode an endpoint here; confirm the real capability first.");
        }
    }
}
