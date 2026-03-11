// Step definitions for price validation
package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import pages.Base;
import pages.PriceValidatePage;
import pages.ProductPage;

import java.util.List;

/**
 * ProductPriceValidateSteps - Step definitions for price sorting validation.
 *
 * FIX: clickSortDropdown() and selectPriceLowToHigh() are now called
 * in ONE combined step to avoid state-sharing issues between steps.
 * The dropdown span click + Select.selectByValue() happen in sequence.
 */
public class ProductPriceValidateSteps {

    private ProductPage productPage;
    private PriceValidatePage priceValidatePage;
    private List<Double> capturedPrices;

    /**
     * Step: "I click on the Sort By dropdown"
     * Clicks the visible span overlay that represents the Sort By dropdown face.
     */
    @And("I click on the Sort By dropdown")
    public void i_click_on_the_sort_by_dropdown() {
        // Re-use the ProductPage instance (driver is static in Base)
        productPage = new ProductPage(Base.driver);
        productPage.clickSortDropdown();
    }

    /**
     * Step: "I select {string} from the sort options"
     * Uses Select class to set value on the <select> element directly.
     * This is the FIX — Select.selectByValue() bypasses the click intercept.
     *
     * @param sortOption Sort option label from feature file
     */
    @And("I select {string} from the sort options")
    public void i_select_from_the_sort_options(String sortOption) {
        if (sortOption.equalsIgnoreCase("Price: Low to High")) {
            productPage.selectPriceLowToHigh();
        } else {
            throw new IllegalArgumentException("Sort option not implemented: " + sortOption);
        }
    }

    /**
     * Step: "the first 3 product prices should be sorted in ascending order"
     * Captures first 3 prices and validates ascending order.
     */
    @Then("the first 3 product prices should be sorted in ascending order")
    public void the_first_3_product_prices_should_be_sorted_in_ascending_order() {
        priceValidatePage = new PriceValidatePage(Base.driver);

        // Fetch the first 3 product prices from the sorted results
        capturedPrices = priceValidatePage.fetchProductPrices(3);

        // Assert prices are in ascending order
        priceValidatePage.validateAscendingSort(capturedPrices);
    }
}
