package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pages.Base;

import java.io.ByteArrayInputStream;

/**
 * Hooks - Cucumber lifecycle with full Allure integration.
 *
 * ON PASS: Writes "Products are correctly sorted by Price: Low to High!" to Allure
 * ON FAIL: Attaches screenshot PNG to Allure report
 */
public class Hooks {

    /**
     * @Before — Initialize WebDriver before each scenario.
     */
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("\n========== SCENARIO SETUP ==========");
        Base.initDriver();

        // ✅ Log scenario start in Allure Steps
        Allure.step("🚀 Browser launched | Scenario: " + scenario.getName());
        System.out.println("✅ WebDriver initialized.");
        System.out.println("====================================\n");
    }

    /**
     * @After — Teardown with Allure pass/fail handling.
     */
    @After
    public void tearDown(Scenario scenario) {
        System.out.println("\n========== SCENARIO TEARDOWN ==========");

        if (scenario.isFailed()) {
            handleFailure(scenario);
        } else {
            handlePass(scenario);
        }

        Base.quitDriver();
        System.out.println("=======================================\n");
    }

    // ─── FAILURE HANDLER ────────────────────────────────────────────────────

    /**
     * Captures screenshot and attaches it to Allure on failure.
     */
    private void handleFailure(Scenario scenario) {
        System.out.println("❌ Scenario FAILED: " + scenario.getName());

        try {
            byte[] screenshot = ((TakesScreenshot) Base.driver)
                .getScreenshotAs(OutputType.BYTES);

            // ✅ Attach PNG screenshot to Allure report
            Allure.addAttachment(
                "❌ Failure Screenshot — " + scenario.getName(),
                "image/png",
                new ByteArrayInputStream(screenshot),
                "png"
            );

            // Also attach to Cucumber HTML report
            scenario.attach(screenshot, "image/png",
                "Failure Screenshot - " + scenario.getName());

            // ✅ Log failure step in Allure
            Allure.step("❌ FAILED — Screenshot attached to report.");
            System.out.println("✅ Screenshot attached to Allure report.");

        } catch (Exception e) {
            System.out.println("⚠️ Screenshot capture failed: " + e.getMessage());
        }
    }

    // ─── PASS HANDLER ───────────────────────────────────────────────────────

    /**
     * Writes pass result message to Allure on success.
     * Shows "Products are correctly sorted by Price: Low to High!" in Allure.
     */
    private void handlePass(Scenario scenario) {
        System.out.println("✅ Scenario PASSED: " + scenario.getName());

        // ✅ Build the pass message shown in Allure report
        String passMessage = resolvePassMessage(scenario.getName());

        // ✅ Add as Allure step (visible in Steps section of the report)
        Allure.step("✅ RESULT: " + passMessage);

        // ✅ Attach as a text file in Allure Attachments section
        String reportText =
            "==============================================\n" +
            "  TEST RESULT : PASSED ✅\n" +
            "==============================================\n" +
            "  Scenario    : " + scenario.getName() + "\n" +
            "  Result      : " + passMessage + "\n" +
            "==============================================\n";

        Allure.addAttachment(
            "✅ Test Result",
            "text/plain",
            new ByteArrayInputStream(reportText.getBytes()),
            "txt"
        );

        System.out.println("📋 Allure pass message: " + passMessage);
    }

    /**
     * Returns the specific result message based on scenario name.
     * This is the text that appears in the Allure report.
     */
    private String resolvePassMessage(String scenarioName) {
        String name = scenarioName.toLowerCase();

        if (name.contains("sorted") || name.contains("price") || name.contains("low to high")) {
            // ✅ YOUR REQUIRED MESSAGE for the sorting scenario
            return "Products are correctly sorted by Price: Low to High!";
        } else if (name.contains("search")) {
            return "Product search for 'iphone 17 pro' completed successfully!";
        } else {
            return "Scenario passed successfully: " + scenarioName;
        }
    }
}
