package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunner - Entry point for Cucumber + TestNG + Allure.
 *
 * KEY FIX: "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
 * must be in the plugin list — this is what writes allure-results JSON files.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"stepdefinitions"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-report.html",
        "json:target/cucumber-reports/cucumber-report.json",

        // ✅ THIS IS THE CRITICAL LINE — writes to allure-results folder
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * FIX: Override dataProvider to ensure TestNG picks up all scenarios.
     * parallel = false keeps scenarios sequential (safer for shared WebDriver).
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
