package com.cresanmanagement.eyeq.utils.MailApi;

import com.cresanmanagement.eyeq.utils.APIBase;
import com.cresanmanagement.eyeq.utils.PropertyReader;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for the Gmail REST API (https://developers.google.com/gmail/api) - used when the OTP/account
 * emails this app sends (Forgot Password, Activate Account) are read back from a real Gmail test
 * mailbox rather than a disposable inbox provider.
 *
 * Requires a pre-obtained OAuth2 access token for the test mailbox, read from config.properties key
 * {@code GmailAccessToken} (token refresh/credential management is out of scope for this scaffold -
 * wire in a real OAuth2 client if this path is actually used). This capability was not confirmed
 * during Requirement Analysis (see FunctionalTestCases.md Section 4.3 / GapAnalysis.md GAP-ENV-01) -
 * do not assume it is available without first confirming a real test mailbox and token exist.
 */
public class Gmail extends APIBase {

    private static final Logger logger = LogManager.getLogger(Gmail.class.getName());
    private static final String BASE_URL = "https://gmail.googleapis.com/gmail/v1/users/me";

    private final String accessToken;

    public Gmail() {
        super();
        Map<String, String> config = PropertyReader.getInstance().getPropValues("config.properties");
        this.accessToken = config.get("GmailAccessToken");
    }

    /**
     * Lists message IDs matching a Gmail search query (e.g. {@code "from:noreply@eyeq.com newer_than:1h"}).
     */
    public JSONArray listMessages(String query) {
        requireConfig();
        String url = BASE_URL + "/messages?q=" + java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        Map<String, String> headers = authHeader();
        try (Response response = get(url, headers)) {
            String body = response.body() != null ? response.body().string() : "{}";
            JSONObject json = (JSONObject) new JSONParser().parse(body);
            Object messages = json.get("messages");
            return messages instanceof JSONArray ? (JSONArray) messages : new JSONArray();
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.error("Failed to list Gmail messages for query: " + query, e);
            throw new RuntimeException("Failed to list Gmail messages for query: " + query, e);
        }
    }

    public JSONObject getMessage(String messageId) {
        requireConfig();
        String url = BASE_URL + "/messages/" + messageId + "?format=full";
        try (Response response = get(url, authHeader())) {
            String body = response.body() != null ? response.body().string() : "{}";
            return (JSONObject) new JSONParser().parse(body);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.error("Failed to fetch Gmail message: " + messageId, e);
            throw new RuntimeException("Failed to fetch Gmail message: " + messageId, e);
        }
    }

    private Map<String, String> authHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private void requireConfig() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "Gmail is not configured (missing GmailAccessToken in config.properties). Per "
                            + "GapAnalysis.md GAP-ENV-01, no test-mailbox capability was confirmed for this project - "
                            + "do not hardcode a token here; confirm the real capability (mailbox + OAuth2 credentials) first.");
        }
    }
}
