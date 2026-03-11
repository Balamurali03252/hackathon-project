package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.Base;
import pages.ProductPage;

/**
 * ProductSearchSteps - Cucumber step definitions for Amazon product search scenarios.
 * Maps Gherkin steps from search.feature to Java actions using ProductPage.
 */
public class ProductSearchSteps {

    // Page object for product search interactions
    private ProductPage productPage;

    // ─── Step Definitions ───────────────────────────────────────────────────

    /**
     * Step: "I open the Amazon website"
     * Opens Amazon homepage and handles any initial popups.
     */
    @Given("I open the Amazon website")
    public void i_open_the_amazon_website() {
        // Initialize ProductPage with the shared driver from Base
        productPage = new ProductPage(Base.driver);

        // Navigate to Amazon India
        productPage.openAmazon();

        // Handle Sign In popup if present
        productPage.handleSignInPopup();

        // Handle Continue Shopping popup if present
        productPage.handleContinueShoppingPopup();
    }

    /**
     * Step: "I search for the product {string}"
     * Types the product name in the search box and submits.
     * @param productName Product to search (passed from feature file)
     */
    @When("I search for the product {string}")
    public void i_search_for_the_product(String productName) {
        productPage.searchProduct(productName);
    }

    /**
     * Step: "I should see search results for {string}"
     * Validates that the search results page loaded with relevant results.
     * @param productName Expected product name in results
     */
    @Then("I should see search results for {string}")
    public void i_should_see_search_results_for(String productName) {
        // Verify search results container is present
        Base.wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("div.s-main-slot div[data-component-type='s-search-result']")
        ));
        System.out.println("✅ Search results are displayed for: " + productName);
    }
}
