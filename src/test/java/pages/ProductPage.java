package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * ProductPage - Handles Amazon homepage, search, popup handling,
 * Sort By dropdown interaction using Page Object Model + PageFactory.
 */
public class ProductPage extends Base {

    // ─── Page Elements via @FindBy (PageFactory) ───────────────────────────

    /** Amazon search input box */
    @FindBy(id = "twotabsearchtextbox")
    private WebElement searchBox;

    // ─── Constructor ────────────────────────────────────────────────────────

    public ProductPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // ─── Methods ────────────────────────────────────────────────────────────

    /**
     * Navigates to the Amazon India homepage.
     */
    public void openAmazon() {
        driver.get("https://www.amazon.in");
        System.out.println("🌐 Navigated to Amazon India.");
    }

    /**
     * Handles the Sign In popup if it appears after loading Amazon.
     */
    public void handleSignInPopup() {
        try {
            WebElement continueShoppingBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(text(),'Continue shopping') or contains(text(),'Continue Shopping')]")
                )
            );
            continueShoppingBtn.click();
            System.out.println("✅ Sign In / Continue Shopping popup handled.");
        } catch (Exception e) {
            System.out.println("ℹ️ No Sign In popup detected. Continuing...");
        }
    }

    /**
     * Handles any overlay or modal popup that may block interaction.
     */
    public void handleContinueShoppingPopup() {
        try {
            WebElement closeBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@data-action='a-popover-close'] | //i[@class='a-icon a-icon-popover-close']")
                )
            );
            closeBtn.click();
            System.out.println("✅ Continue Shopping popup closed.");
        } catch (Exception e) {
            System.out.println("ℹ️ No additional popup detected. Continuing...");
        }
    }

    /**
     * Searches for a given product on Amazon.
     * FIX: Uses Keys.RETURN instead of clicking search button — faster & more reliable.
     *
     * @param productName The product to search for
     */
    public void searchProduct(String productName) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(searchBox));
            searchBox.clear();

            // Send the entire string at once — no character-by-character delay
            // This is the fastest possible input method
            searchBox.sendKeys(productName);
            searchBox.sendKeys(Keys.RETURN);
            System.out.println("🔍 Searched for: " + productName + " (via ENTER key)");

            // Wait for first product card — faster than waiting for full slot container
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div[data-component-type='s-search-result']")
            ));
            System.out.println("✅ Search results page loaded.");

        } catch (Exception e) {
            System.out.println("❌ Error during product search: " + e.getMessage());
            throw new RuntimeException("Product search failed: " + e.getMessage());
        }
    }

    /**
     * Clicks the Sort By dropdown on the search results page.
     *
     * FIX: Amazon renders a <span class="a-dropdown-prompt"> OVER the hidden <select>.
     * Clicking the <select> directly causes "element click intercepted" error.
     *
     * Strategy:
     *  1. First try clicking the visible <span> prompt (the styled dropdown face)
     *  2. If intercepted, fall back to Select class (works on native <select> without clicking)
     *  3. Last resort: JavascriptExecutor click to bypass any overlay
     */
    public void clickSortDropdown() {
        try {
            // APPROACH 1: Click the visible styled span prompt that overlays the select
            // This is what the user sees visually — the styled dropdown button
            WebElement sortPromptSpan = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("span.a-dropdown-prompt")
                )
            );
            sortPromptSpan.click();
            System.out.println("✅ Sort By dropdown span clicked successfully.");

        } catch (Exception e1) {
            System.out.println("⚠️ Span click failed, trying JavascriptExecutor: " + e1.getMessage());
            try {
                // APPROACH 2: Use JavascriptExecutor to force-click the select element
                WebElement sortSelect = driver.findElement(By.id("s-result-sort-select"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sortSelect);
                System.out.println("✅ Sort By dropdown clicked via JavascriptExecutor.");

            } catch (Exception e2) {
                System.out.println("❌ All click approaches failed: " + e2.getMessage());
                throw new RuntimeException("Could not click Sort dropdown: " + e2.getMessage());
            }
        }
    }

    /**
     * Selects "Price: Low to High" from the Sort By dropdown.
     *
     * FIX: Uses Selenium's Select class with selectByValue() — this directly
     * sets the <select> value WITHOUT needing to click it first.
     * This bypasses the overlay/intercept issue entirely.
     */
    public void selectPriceLowToHigh() {
        try {
            // Wait for the <select> element to be present in DOM
            WebElement sortSelectElement = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("s-result-sort-select"))
            );

            // Use Selenium Select class — works directly on <select> elements
            // selectByValue() matches the option's 'value' attribute: price-asc-rank
            Select sortSelect = new Select(sortSelectElement);
            sortSelect.selectByValue("price-asc-rank");
            System.out.println("✅ Selected 'Price: Low to High' via Select class.");

            // Wait for page to re-render sorted results
            wait.until(ExpectedConditions.stalenessOf(sortSelectElement));
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div[data-component-type='s-search-result']")
            ));
            System.out.println("✅ Sorted results page loaded.");

        } catch (Exception e) {
            System.out.println("❌ Error selecting Price: Low to High: " + e.getMessage());
            throw new RuntimeException("Could not select sort option: " + e.getMessage());
        }
    }
}
