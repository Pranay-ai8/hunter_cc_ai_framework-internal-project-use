package com.codecraft.saucedemo.driver;

import com.codecraft.saucedemo.config.ConfigurationManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

/**
 * Creates a browser instance per {@link BrowserType}, resolving driver binaries via
 * WebDriverManager (never a manually downloaded/committed binary — per
 * 03-standards/selenium-standards.md). Headless mode and sandbox flags are driven by
 * config so the suite can run in a display-less CI/sandbox environment.
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createDriver(BrowserType browserType) {
        boolean headless = ConfigurationManager.headless();
        log.info("Creating {} driver (headless={})", browserType, headless);

        WebDriver driver;
        switch (browserType) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
                }
                driver = new EdgeDriver(edgeOptions);
                break;
            case SAFARI:
                driver = new SafariDriver();
                break;
            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    // --headless=new + sandbox flags: required for headless Chrome to launch
                    // reliably in a display-less/sandboxed CI environment (no X server).
                    chromeOptions.addArguments(
                            "--headless=new",
                            "--no-sandbox",
                            "--disable-dev-shm-usage",
                            "--disable-gpu",
                            "--window-size=1920,1080");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigurationManager.pageLoadTimeoutSeconds()));
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            // Headless/sandboxed environments without a display can reject maximize();
            // --window-size is already set via ChromeOptions above, so this is safe to skip.
            log.warn("Window maximize() skipped: {}", e.getMessage());
        }
        return driver;
    }
}
