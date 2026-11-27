package com.cresanmanagement.eyeq.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp3-based REST helpers for backend calls a test may need alongside UI actions (e.g. seeding a
 * test ticket via API rather than waiting for one to be system-generated, or polling device-health
 * state for Site Pulse assertions). Not wired into any Page Object by this scaffold - that is
 * 05-agents/appium-java-agent.md's job once real endpoints are confirmed.
 */
public class APIBase {

    private static final Logger logger = LogManager.getLogger(APIBase.class.getName());
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    protected final OkHttpClient client;

    public APIBase() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public Response get(String url, Map<String, String> headers) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: GET " + url);
        Request.Builder builder = new Request.Builder().url(url).get();
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    public Response post(String url, String jsonBody, Map<String, String> headers) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: POST " + url);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request.Builder builder = new Request.Builder().url(url).post(body);
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    public Response put(String url, String jsonBody, Map<String, String> headers) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: PUT " + url);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request.Builder builder = new Request.Builder().url(url).put(body);
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    public Response delete(String url, Map<String, String> headers) {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + " is executed: DELETE " + url);
        Request.Builder builder = new Request.Builder().url(url).delete();
        addHeaders(builder, headers);
        return execute(builder.build());
    }

    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
    }

    private Response execute(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            logger.error("API call failed: " + request.method() + " " + request.url(), e);
            throw new RuntimeException("API call failed: " + request.method() + " " + request.url(), e);
        }
    }
}
