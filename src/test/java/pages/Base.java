package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class - Initializes WebDriver, ChromeOptions, and WebDriverWait.
 * All page classes extend this class to share the driver instance.
 */
public class Base {

    // Static WebDriver instance shared across all page classes
    public static WebDriver driver;

    // Explicit wait instance for dynamic element waiting
    public static WebDriverWait wait;

    // Default wait timeout in seconds
    private static final int WAIT_TIMEOUT = 15;

    /**
     * Initializes ChromeDriver with options and sets up WebDriverWait.
     * Called from Hooks @Before method.
     */
    public static void initDriver() {
        // Configure ChromeOptions for stable automation
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");           // Start browser maximized
        options.addArguments("--disable-notifications");     // Disable browser notifications
        options.addArguments("--disable-popup-blocking");    // Disable popup blocking
        options.addArguments("--disable-infobars");          // Remove 'Chrome is being controlled' bar
        options.addArguments("--remote-allow-origins=*");    // Allow remote origins (ChromeDriver compatibility)

        // Initialize ChromeDriver (WebDriverManager or local driver path required)
        // If using WebDriverManager, add: WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        // Set implicit wait as fallback (optional, explicit wait preferred)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Initialize explicit WebDriverWait with timeout
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));

        System.out.println("✅ ChromeDriver initialized successfully.");
    }

    /**
     * Quits the WebDriver and closes all browser windows.
     * Called from Hooks @After method.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("✅ Browser closed and WebDriver session ended.");
        }
    }

    /**
     * Returns the current WebDriver instance.
     */
    public static WebDriver getDriver() {
        return driver;
    }

    /**
     * Returns the WebDriverWait instance.
     */
    public static WebDriverWait getWait() {
        return wait;
    }
}
