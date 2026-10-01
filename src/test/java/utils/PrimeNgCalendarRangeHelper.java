package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Locale;

/**
 * Reusable helper for interacting with PrimeNG range-mode calendars
 * (p-calendar[selectionmode="range"], single-month-panel variant, appendTo="body").

 * This class is intentionally generic: it has no knowledge of tables, rows,
 * or field names like "Target Date Range". The calling Page Object supplies
 * the icon locator that opens the calendar; this class only handles what
 * happens once the popup is open (navigation, day-click, close).

 * Reuse this for ANY PrimeNG range calendar field elsewhere in the app —
 * do not fork it per-page.
 */
public class PrimeNgCalendarRangeHelper {

    private static final By POPUP_CONTAINER = By.cssSelector("div.p-datepicker.p-component");
    private static final By MONTH_BUTTON   = By.cssSelector("button.p-datepicker-month");
    private static final By YEAR_BUTTON    = By.cssSelector("button.p-datepicker-year");
    private static final By PREV_BUTTON    = By.cssSelector("button.p-datepicker-prev");
    private static final By NEXT_BUTTON    = By.cssSelector("button.p-datepicker-next");

    private static final int MAX_MONTH_NAVIGATION_ITERATIONS = 60; // 5-year safety cap, prevents infinite loop on nav failure
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(15);

    private final WebDriver driver;
    private final WebDriverCommonLibrary webDriverCommonLibrary;
    private final WebDriverWait wait;

    public PrimeNgCalendarRangeHelper(WebDriver driver, WebDriverCommonLibrary webDriverCommonLibrary) {
        this.driver = driver;
        this.webDriverCommonLibrary = webDriverCommonLibrary;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void openCalendar(By calendarTriggerLocator) {
        WebElement trigger = wait.until(ExpectedConditions.presenceOfElementLocated(calendarTriggerLocator));
        webDriverCommonLibrary.scrollIntoTheView(driver, trigger);

        // Re-wait for clickable AFTER scroll — scroll can trigger Angular layout recalculation
        WebElement clickableTrigger = wait.until(ExpectedConditions.elementToBeClickable(calendarTriggerLocator));
        try {
            clickableTrigger.click();
        } catch (ElementClickInterceptedException e) {
            // Narrow, documented fallback for the confirmed overflow-x:auto risk only —
            // native click is always attempted first.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableTrigger);
        }

        waitForPopupVisible();
    }

    /**
     * Selects both ends of the range. Always re-navigates before the end-date
     * click, regardless of whether the panel auto-advances after the start
     * click — this is a harmless no-op if it already advanced, and correct
     * if it didn't. Do not "optimize" this away without confirming
     * auto-advance behavior first.
     */
    public void selectDateRange(LocalDate startDate, LocalDate endDate) {
        selectStartDate(startDate);
        selectEndDate(endDate);
    }

    private void waitForPopupVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(POPUP_CONTAINER));
    }

    private void selectStartDate(LocalDate startDate) {
        navigateToMonth(YearMonth.from(startDate));
        clickDay(startDate);
    }

    private void selectEndDate(LocalDate endDate) {
        navigateToMonth(YearMonth.from(endDate));
        clickDay(endDate);
    }

    private void clickDay(LocalDate date) {
        By locator = dayCellLocator(date);
        WebElement dayCell = wait.until(ExpectedConditions.elementToBeClickable(locator));
        dayCell.click();
    }

    /**
     * data-date is zero-indexed on month (confirmed: September = "8").
     * Uses data-date instead of visible day text because adjacent-month
     * overflow days repeat day numbers, making text-based locators ambiguous.
     */
    private By dayCellLocator(LocalDate date) {
        String dataDateValue = date.getYear() + "-" + (date.getMonthValue() - 1) + "-" + date.getDayOfMonth();
        return By.xpath(
                "//td[not(contains(@class,'p-datepicker-other-month'))]" +
                        "//span[@data-date='" + dataDateValue + "']"
        );
    }

    private void navigateToMonth(YearMonth target) {
        int iterations = 0;
        while (iterations < MAX_MONTH_NAVIGATION_ITERATIONS) {
            YearMonth displayed = getDisplayedMonthYear();
            if (displayed.equals(target)) return;

            By navButton = displayed.isBefore(target) ? NEXT_BUTTON : PREV_BUTTON;
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(navButton));
            btn.click();

            // Wait for header to actually change — prevents reading a stale value mid re-render
            wait.until(d -> !getDisplayedMonthYear().equals(displayed));
            iterations++;
        }
        throw new NoSuchElementException(
                "Could not navigate PrimeNG calendar to target month/year: " + target
        );
    }

    private YearMonth getDisplayedMonthYear() {
        WebElement monthBtn = driver.findElement(MONTH_BUTTON);
        WebElement yearBtn = driver.findElement(YEAR_BUTTON);
        Month month = Month.valueOf(monthBtn.getText().trim().toUpperCase(Locale.ENGLISH));
        int year = Integer.parseInt(yearBtn.getText().trim());
        return YearMonth.of(year, month);
    }
}