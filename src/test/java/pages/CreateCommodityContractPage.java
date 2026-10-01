package pages;

import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.PrimeNgCalendarRangeHelper;
import utils.WebDriverCommonLibrary;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Page Object for the Create Commodity Contract Management page.
 * Owns the row/column XPath scoping for the repeating "Product Details"
 * blocks and delegates the actual calendar mechanics to
  PrimeNgCalendarRangeHelper (which knows nothing about this page).
 */
public class CreateCommodityContractPage {

    private final WebDriver driver;
    private final WebDriverCommonLibrary webDriverCommonLibrary;
    private final PrimeNgCalendarRangeHelper calendarHelper;

    private static final String SECTION_CAPTION = "Product Details";
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public CreateCommodityContractPage(WebDriver driver, WebDriverCommonLibrary webDriverCommonLibrary) {
        this.driver = driver;
        this.webDriverCommonLibrary = webDriverCommonLibrary;
        this.calendarHelper = new PrimeNgCalendarRangeHelper(driver, webDriverCommonLibrary);
    }

    /**
     * Public entry point. rowIndex is 1-based (matches XPath predicate
     * convention), corresponding to the Nth "Product Details" block on
     * the page — each block is its own <table>, not a <tr> within one table.
     */
    public void setTargetDateRange(int rowIndex, LocalDate startDate, LocalDate endDate) {
        By iconLocator = buildDateRangeLocator(rowIndex, "calendaricon[contains(@class,'p-datepicker-icon')]");
        calendarHelper.openCalendar(iconLocator);
        calendarHelper.selectDateRange(startDate, endDate);
        verifyTargetDateRange(rowIndex, startDate, endDate);
    }

    /**
     * Verifies against the bound input's value attribute, not the popup DOM —
     * this PrimeNG version has no distinct range-start/range-end/in-range
     * class, so the input value is the only reliable source of truth.
     */
    private void verifyTargetDateRange(int rowIndex, LocalDate startDate, LocalDate endDate) {
        By inputLocator = buildDateRangeLocator(rowIndex, "input[@role='combobox']");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputLocator));

        String actualValue = input.getAttribute("value");
        String expectedValue = startDate.format(INPUT_DATE_FORMAT) + " - " + endDate.format(INPUT_DATE_FORMAT);

        Assert.assertEquals(actualValue, expectedValue,
                "Target Date Range value mismatch for row " + rowIndex +
                        ". Expected: " + expectedValue + ", Actual: " + actualValue);
    }

    /**
      leafXPathFragment swaps in either the calendaricon (to open) or the
      input[@role='combobox'] (to verify), reusing the same row/column scope.
     **/
    private By buildDateRangeLocator(int rowIndex, String leafXPathFragment) {
        String xpath =
                "//div[@formarrayname='item_details']" +
                        "//xbs-dcloud-caption[.//h3[normalize-space()='" + SECTION_CAPTION + "']][1]" +
                        "/following-sibling::div[" + rowIndex + "]//table" +
                        "//p-calendar[@selectionmode='range']//" + leafXPathFragment;

        return By.xpath(xpath);
    }


}