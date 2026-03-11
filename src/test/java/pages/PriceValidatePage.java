package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.ByteArrayInputStream;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

/**
 * PriceValidatePage - Fetches prices using multiple CSS/XPath fallback selectors,
 * cleans the price strings, converts to double, and validates ascending sort order.
 */
public class PriceValidatePage extends Base {

    public PriceValidatePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    /**
     * Fetches the prices of the first N products using multiple fallback selectors.
     * Amazon frequently changes its DOM structure, so we try several known selectors.
     *
     * Fallback order:
     *  1. span.a-price-whole          — whole number part (most common, always visible)
     *  2. span.a-offscreen            — screen reader span with full ₹ price
     *  3. span[data-a-color='price']  — alternate price container
     *
     * @param numberOfProducts Number of prices to capture
     * @return List of prices as Double values
     */
    public List<Double> fetchProductPrices(int numberOfProducts) {
        List<Double> priceList = new ArrayList<>();

        // ── SELECTOR FALLBACK LIST ──────────────────────────────────────────
        // Try each selector in order until we get enough prices
        String[] priceSelectors = {
            "span.a-price-whole",                                      // Most reliable — whole number part
            "span.a-offscreen",                                        // Screen reader full price
            ".s-price-instructions-style span.a-offscreen",           // Scoped offscreen
            "span[data-a-color='price'] span.a-offscreen",            // Color-tagged price
            ".a-section.a-spacing-none.aok-align-center span.a-price-whole", // Centered price block
            "div.s-widget-container span.a-price-whole"               // Widget-scoped
        };

        for (String selector : priceSelectors) {
            System.out.println("🔍 Trying price selector: " + selector);
            try {
                // Wait up to 10s for at least one element with this selector
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));

                List<WebElement> priceElements = driver.findElements(By.cssSelector(selector));
                System.out.println("   Found " + priceElements.size() + " elements.");

                if (priceElements.isEmpty()) continue;

                // Scroll page to ensure price elements are in view
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", priceElements.get(0)
                );

                List<Double> tempList = new ArrayList<>();
                for (WebElement el : priceElements) {
                    if (tempList.size() >= numberOfProducts) break;

                    // Try both innerText and innerHTML to get price text
                    String rawPrice = el.getText().trim();
                    if (rawPrice.isEmpty()) {
                        rawPrice = el.getAttribute("innerHTML").trim();
                    }
                    if (rawPrice.isEmpty()) continue;

                    System.out.println("   Raw price text: [" + rawPrice + "]");

                    double price = cleanAndConvertPrice(rawPrice);
                    if (price > 0) {
                        tempList.add(price);
                        System.out.println("   ✅ Parsed: " + price);
                    }
                }

                // If we collected enough prices, use this selector and stop
                if (tempList.size() >= numberOfProducts) {
                    priceList = tempList.subList(0, numberOfProducts);
                    System.out.println("✅ Prices captured using selector: " + selector);
                    System.out.println("📋 Captured Prices: " + priceList);
                    return new ArrayList<>(priceList);
                }

                // Partial match — keep best result so far and try next selector
                if (tempList.size() > priceList.size()) {
                    priceList = new ArrayList<>(tempList);
                }

            } catch (Exception e) {
                System.out.println("   ⚠️ Selector failed: " + e.getMessage().split("\n")[0]);
            }
        }

        // ── FINAL FALLBACK: XPath with visible text containing ₹ ──────────
        if (priceList.size() < numberOfProducts) {
            System.out.println("🔍 Trying XPath fallback for ₹ prices...");
            try {
                List<WebElement> xpathPrices = driver.findElements(
                    By.xpath("//*[contains(@class,'a-price-whole') and not(contains(@class,'a-text-strike'))]")
                );
                System.out.println("   XPath found: " + xpathPrices.size() + " elements.");

                List<Double> tempList = new ArrayList<>();
                for (WebElement el : xpathPrices) {
                    if (tempList.size() >= numberOfProducts) break;
                    String raw = el.getText().trim().replace(",", "").replace(".", "");
                    if (!raw.isEmpty()) {
                        try {
                            double val = Double.parseDouble(raw);
                            if (val > 0) {
                                tempList.add(val);
                                System.out.println("   ✅ XPath parsed: " + val);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }

                if (!tempList.isEmpty() && tempList.size() > priceList.size()) {
                    priceList = tempList;
                }

            } catch (Exception e) {
                System.out.println("   ⚠️ XPath fallback also failed: " + e.getMessage().split("\n")[0]);
            }
        }

        if (priceList.isEmpty()) {
            throw new RuntimeException(
                "❌ Could not find any price elements on the page. " +
                "Amazon may have changed its DOM. " +
                "Please inspect the page manually and update the selector."
            );
        }

        System.out.println("📋 Final Prices List: " + priceList);
        return priceList;
    }

    /**
     * Cleans raw price string and converts to double.
     *
     * Handles formats:
     *  - "₹79,999"       → 79999.0
     *  - "₹1,25,000"     → 125000.0
     *  - "79,999"        → 79999.0
     *  - "79999"         → 79999.0
     *  - "79,999.00"     → 79999.0
     *
     * @param rawPrice Raw price string from DOM
     * @return Numeric double value
     */
    public double cleanAndConvertPrice(String rawPrice) {
        try {
            // Step 1: Remove ₹ Rupee symbol (multiple encodings)
            String cleaned = rawPrice
                .replace("₹", "")
                .replace("\u20B9", "")
                .replace("Rs.", "")
                .replace("Rs", "")
                .trim();

            // Step 2: Remove commas (Indian number format: 1,25,000)
            cleaned = cleaned.replace(",", "");

            // Step 3: Remove any stray HTML tags if innerHTML was used
            cleaned = cleaned.replaceAll("<[^>]+>", "").trim();

            // Step 4: Keep only digits and decimal point
            cleaned = cleaned.replaceAll("[^0-9.]", "");

            if (cleaned.isEmpty()) return 0.0;

            // Step 5: Handle multiple decimal points — keep only first
            int firstDot = cleaned.indexOf('.');
            if (firstDot != -1) {
                String beforeDot = cleaned.substring(0, firstDot + 1);
                String afterDot  = cleaned.substring(firstDot + 1).replace(".", "");
                cleaned = beforeDot + afterDot;
            }

            return Double.parseDouble(cleaned);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Could not parse price: '" + rawPrice + "'");
            return 0.0;
        }
    }

    /**
     * Validates that the given price list is sorted in ascending order.
     *
     * @param actualPrices List of prices captured from the UI
     */
    @Step("Validate products are sorted by Price: Low to High")
    public void validateAscendingSort(List<Double> actualPrices) {
        System.out.println("\n========== PRICE SORT VALIDATION ==========");
        System.out.println("📋 Actual Price List   : " + actualPrices);

        // Create sorted copy
        List<Double> sortedPrices = new ArrayList<>(actualPrices);
        Collections.sort(sortedPrices);

        System.out.println("📋 Expected Sorted List: " + sortedPrices);

        // ✅ Write price lists as Allure step details (visible in Steps section)
        Allure.step("📋 Actual Prices from UI   : " + actualPrices);
        Allure.step("📋 Expected Sorted Prices  : " + sortedPrices);

        // ✅ Attach full price comparison as a text file in Allure
        String priceReport =
            "PRICE SORT VALIDATION REPORT\n" +
            "==============================\n" +
            "Actual Prices (from UI) : " + actualPrices + "\n" +
            "Expected Sorted Prices  : " + sortedPrices + "\n" +
            "==============================\n";

        boolean isSorted = actualPrices.equals(sortedPrices);

        if (isSorted) {
            priceReport += "RESULT : ✅ PASS — Products are correctly sorted by Price: Low to High!\n";
            Allure.addAttachment(
                "✅ Price Validation Result",
                "text/plain",
                new ByteArrayInputStream(priceReport.getBytes()),
                "txt"
            );
            Allure.step("✅ PASS — Products are correctly sorted by Price: Low to High!");
        } else {
            priceReport += "RESULT : ❌ FAIL — Products are NOT sorted correctly!\n";
            Allure.addAttachment(
                "❌ Price Validation Result",
                "text/plain",
                new ByteArrayInputStream(priceReport.getBytes()),
                "txt"
            );
            Allure.step("❌ FAIL — Prices are NOT in ascending order!");
        }

        System.out.println(priceReport);

        // TestNG assertion — fails the test if not sorted
        Assert.assertEquals(
            actualPrices,
            sortedPrices,
            "\n❌ FAIL: Products are NOT sorted by Price: Low to High!\n" +
            "   Actual  : " + actualPrices + "\n" +
            "   Expected: " + sortedPrices + "\n"
        );

        System.out.println("✅ PASS: Products are correctly sorted by Price: Low to High!");
        System.out.println("===========================================\n");
    }
}
