package com.cresanmanagement.eyeq.testng;

import com.cresanmanagement.eyeq.reports.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Owns the {@link ExtentReportManager} lifecycle ({@code initReport}/{@code flush}, called from
 * {@code onStart}/{@code onFinish} since this stack's {@code TestBase} lifecycle is per-@Test-method,
 * not per-suite - there is no {@code @BeforeSuite}/{@code @AfterSuite} hook to put these calls in
 * instead) and writes a lightweight custom HTML pass/fail/skip summary table per {@code <test>} block.
 */
public class MobileEmailReport implements ISuiteListener {

    private static final Logger log = LogManager.getLogger(MobileEmailReport.class);

    @Override
    public void onStart(ISuite suite) {
        ExtentReportManager.initReport();
    }

    @Override
    public void onFinish(ISuite suite) {
        try {
            Files.createDirectories(Paths.get("test-output"));
            StringBuilder html = new StringBuilder();
            html.append("<html><body><h2>EyeQ Monitoring Automation - Suite Summary</h2><table border='1' cellpadding='6'>");
            html.append("<tr><th>Test</th><th>Passed</th><th>Failed</th><th>Skipped</th></tr>");

            int totalPass = 0, totalFail = 0, totalSkip = 0;
            for (Map.Entry<String, ISuiteResult> entry : suite.getResults().entrySet()) {
                ITestContext ctx = entry.getValue().getTestContext();
                int pass = ctx.getPassedTests().size();
                int fail = ctx.getFailedTests().size();
                int skip = ctx.getSkippedTests().size();
                totalPass += pass;
                totalFail += fail;
                totalSkip += skip;
                html.append("<tr><td>").append(ctx.getName()).append("</td><td>").append(pass)
                        .append("</td><td>").append(fail).append("</td><td>").append(skip).append("</td></tr>");
            }
            html.append("<tr><th>Total</th><th>").append(totalPass).append("</th><th>").append(totalFail)
                    .append("</th><th>").append(totalSkip).append("</th></tr></table></body></html>");

            try (FileWriter writer = new FileWriter("test-output/email-report.html")) {
                writer.write(html.toString());
            }
            log.info("Email-style HTML report written to test-output/email-report.html");
        } catch (IOException e) {
            log.error("Failed to write email report", e);
        } finally {
            ExtentReportManager.flush();
        }
    }
}
