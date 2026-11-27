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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client for Mailinator's public Email API v2 (https://www.mailinator.com/docs.html) - used to
 * retrieve OTP/verification emails sent to a disposable test mailbox during Forgot Password /
 * Activate Account flows (see RequirementAnalysis.md Section 7.1).
 *
 * Requires a real Mailinator API token (config.properties key {@code MailinatorApiToken}) and a
 * private domain name (config.properties key {@code MailinatorDomain}) - this scaffold does not
 * hardcode either, per this stack's "never hardcode credentials" rule. If this capability is not
 * actually provisioned for this project, OTP test cases should stay marked TBD rather than silently
 * calling this client against a non-existent account (see FunctionalTestCases.md Section 4.3 /
 * GapAnalysis.md GAP-ENV-01, which already flagged this exact assumption as unconfirmed).
 */
public class Mailinator extends APIBase {

    private static final Logger logger = LogManager.getLogger(Mailinator.class.getName());
    private static final String BASE_URL = "https://api.mailinator.com/api/v2";
    private static final Pattern OTP_PATTERN = Pattern.compile("\\b(\\d{6})\\b");

    private final String apiToken;
    private final String domain;

    public Mailinator() {
        super();
        Map<String, String> config = PropertyReader.getInstance().getPropValues("config.properties");
        this.apiToken = config.get("MailinatorApiToken");
        this.domain = config.get("MailinatorDomain");
    }

    /**
     * Returns the most recent message subject lines for {@code inbox} (the local-part before @).
     */
    public JSONArray getInboxMessages(String inbox) {
        requireConfig();
        String url = BASE_URL + "/domains/" + domain + "/inboxes/" + inbox;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", apiToken);
        try (Response response = get(url, headers)) {
            String body = response.body() != null ? response.body().string() : "{}";
            JSONObject json = (JSONObject) new JSONParser().parse(body);
            return (JSONArray) json.get("msgs");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            logger.error("Failed to fetch Mailinator inbox: " + inbox, e);
            throw new RuntimeException("Failed to fetch Mailinator inbox: " + inbox, e);
        }
    }

    public String getMessageBody(String inbox, String messageId) {
        requireConfig();
        String url = BASE_URL + "/domains/" + domain + "/inboxes/" + inbox + "/messages/" + messageId;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", apiToken);
        try (Response response = get(url, headers)) {
            return response.body() != null ? response.body().string() : "";
        } catch (IOException e) {
            logger.error("Failed to fetch Mailinator message: " + messageId, e);
            throw new RuntimeException("Failed to fetch Mailinator message: " + messageId, e);
        }
    }

    /**
     * Convenience helper for the OTP Verification flow: extracts the first 6-digit code found in the
     * given message body text.
     */
    public String extractOtp(String messageBody) {
        Matcher matcher = OTP_PATTERN.matcher(messageBody == null ? "" : messageBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("No 6-digit OTP code found in the supplied message body");
    }

    private void requireConfig() {
        if (apiToken == null || apiToken.isBlank() || domain == null || domain.isBlank()) {
            throw new IllegalStateException(
                    "Mailinator is not configured (missing MailinatorApiToken/MailinatorDomain in config.properties). "
                            + "Per GapAnalysis.md GAP-ENV-01, the test-mailbox/automation-environment capability for OTP "
                            + "flows is unconfirmed for this project - do not hardcode a token/domain here; confirm the "
                            + "real capability first and keep affected test cases TBD until then.");
        }
    }
}
