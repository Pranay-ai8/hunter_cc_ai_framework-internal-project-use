package com.codecraft.wdiodemoapp.pageobjects.webview;

import com.codecraft.wdiodemoapp.utils.Base;
import com.codecraft.wdiodemoapp.utils.DriverFactory;

import java.util.List;
import java.util.Set;

public class WebviewPage extends Base {

    private final WebviewPageObjectRepository repo;

    public WebviewPage() {
        repo = new WebviewPageObjectRepository(DriverFactory.getDriverForFramework());
    }

    public boolean isWebViewDisplayed() {
        waitUntilElementIsVisible(repo.webView);
        return isElementVisible(repo.webView);
    }

    /**
     * Polls for a WEBVIEW_* context to register, rather than checking once. Live-run finding:
     * the Android WebView only exposes itself for remote-debugging discovery a moment after
     * navigation completes, so an immediate single check is flaky.
     */
    public boolean webviewContextIsAvailable() {
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            List<String> contexts = getContexts();
            if (contexts.stream().anyMatch(c -> c.startsWith("WEBVIEW"))) {
                return true;
            }
            sleep(500);
        }
        return false;
    }

    public String switchToWebviewContext() {
        String webviewContext = getContexts().stream()
                .filter(c -> c.startsWith("WEBVIEW"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No WEBVIEW context available"));
        switchToContext(webviewContext);
        return webviewContext;
    }

    public void switchToNativeContext() {
        switchToContext("NATIVE_APP");
    }

    public String getCurrentContext() {
        return ((io.appium.java_client.remote.SupportsContextSwitching) driver).getContext();
    }
}
