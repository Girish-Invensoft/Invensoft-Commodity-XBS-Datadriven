package utils;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import java.util.function.Function;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class WebDriverCommonLibrary {

    private final WebDriver driver;
    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final ObjectMapper MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final By LOADING_OVERLAY = By.cssSelector(".loading-overlay");
    private final By contractDateInput = By.xpath(
            "//label[starts-with(normalize-space(.),'Date')]/following::input[@role='combobox'][1]");

    private static final DateTimeFormatter APP_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static String xp(String s) {   // safe XPath literal, handles apostrophes
        return s.contains("'") ? "concat('" + s.replace("'", "', \"'\", '") + "')" : "'" + s + "'";
    }

   public By dateFieldLocator(String section, String field) {
        return By.xpath("//label[@class='selected-label' and normalize-space(translate(.,'*',''))='"+field+"']/preceding-sibling::p-calendar//input");
    }

    public By dateCCMFieldLocator(String section, String field) {
        return By.xpath("//*[contains(normalize-space(translate(.,'*','')),'"+field+"')]/preceding-sibling::p-calendar//input");
    }

    public By dateFieldLocatorSet(String section, String field) {
        return By.xpath("//span[contains(@class,'p-float-label')][label[normalize-space(translate(.,'*',''))='"+field+"']]//input");
    }
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Kolkata");
    public WebDriverCommonLibrary(WebDriver driver) {
        this.driver = driver;
    }


    public WebElement explicitWait(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
        //elementtobeclickable covers both isdisplayed() and isenabled().
    }

    public WebElement explicitWaitByLocator(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement explicitWaitVisibilityByLocator(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    public WebElement explicitWaitVisibility(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void scrollIntoTheView(final WebDriver driver, final WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'nearest'});", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    public void scrollIntoTheViewNearest(final WebDriver driver, final WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'nearest', inline: 'center'});", element);
    }


    public void highLightTheElement(final WebDriver driver, final WebElement ele) {
        try {
            JavascriptExecutor jsExe = (JavascriptExecutor) driver;
            scrollIntoTheView(driver, ele);
            String originalStyle = ele.getAttribute("style");
            jsExe.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", ele);
            dynamicWait(ConfigReader.getHighlightWaitMillis());
            jsExe.executeScript("arguments[0].setAttribute('style', arguments[1]);", ele, originalStyle == null ? "" : originalStyle);
        } catch (StaleElementReferenceException e) {
            // Element was re-rendered (Angular/PrimeNG refresh); highlight is cosmetic, so skip it
        } catch (WebDriverException e) {
            // Any other driver hiccup during highlighting should not fail the test
        }
    }

    public void highLightTheElementRed(final WebDriver driver, final WebElement ele) {
        try {
            JavascriptExecutor jsExe = (JavascriptExecutor) driver;
            scrollIntoTheView(driver, ele);
            String originalStyle = ele.getAttribute("style");
            jsExe.executeScript("arguments[0].setAttribute('style', 'background: red; border: 2px solid blue;');", ele);
            dynamicWait(ConfigReader.getHighlightWaitMillis());
            jsExe.executeScript("arguments[0].setAttribute('style', arguments[1]);", ele, originalStyle == null ? "" : originalStyle);
        } catch (StaleElementReferenceException e) {
            // Element was re-rendered (Angular/PrimeNG refresh); highlight is cosmetic, so skip it
        } catch (WebDriverException e) {
            // Any other driver hiccup during highlighting should not fail the test
        }
    }

    public void highLightTheElementGreen(final WebDriver driver, final WebElement ele) {
        try {
            JavascriptExecutor jsExe = (JavascriptExecutor) driver;
            scrollIntoTheView(driver, ele);
            String originalStyle = ele.getAttribute("style");
            jsExe.executeScript("arguments[0].setAttribute('style', 'background: green; border: 2px solid blue;');", ele);
            dynamicWait(ConfigReader.getHighlightWaitMillis());
            jsExe.executeScript("arguments[0].setAttribute('style', arguments[1]);", ele, originalStyle == null ? "" : originalStyle);
        } catch (StaleElementReferenceException e) {
            // Element was re-rendered (Angular/PrimeNG refresh); highlight is cosmetic, so skip it
        } catch (WebDriverException e) {
            // Any other driver hiccup during highlighting should not fail the test
        }
    }

    public enum HighlightType {
        ACTION("#1E88E5", "rgba(30,136,229,0.15)", "solid"),
        PASS("#2E7D32", "rgba(46,125,50,0.15)", "solid"),
        FAIL("#D32F2F", "rgba(211,47,47,0.20)", "dashed"),
        WARN("#F57C00", "rgba(245,124,0,0.15)", "solid"),
        INFO("#7B1FA2", "rgba(123,31,162,0.12)", "solid");

        private final String outline;
        private final String background;
        private final String lineStyle;

        HighlightType(String outline, String background, String lineStyle) {
            this.outline = outline;
            this.background = background;
            this.lineStyle = lineStyle;
        }
    }

//    // Existing calls continue to use the blue ACTION highlight.
//    public void highLightTheElement(
//            final WebDriver driver,
//            final WebElement ele) {
//
//        highLightTheElement(driver, ele, HighlightType.ACTION);
//    }

//    public void highLightTheElement(
//            final WebDriver driver,
//            final WebElement ele,
//            final HighlightType type) {
//
//        try {
//            scrollIntoTheView(driver, ele);
//            JavascriptExecutor jsExe = (JavascriptExecutor) driver;
//            jsExe.executeScript(
//                    "var e = arguments[0];"
//                            + "var color = arguments[1];"
//                            + "var background = arguments[2];"
//                            + "var lineStyle = arguments[3];"
//                            + "var duration = arguments[4];"
//
//                            + "var oldOutline = e.style.getPropertyValue('outline');"
//                            + "var oldOutlinePriority = e.style.getPropertyPriority('outline');"
//                            + "var oldBackground = e.style.getPropertyValue('background-color');"
//                            + "var oldBackgroundPriority = e.style.getPropertyPriority('background-color');"
//                            + "var oldOffset = e.style.getPropertyValue('outline-offset');"
//                            + "var oldOffsetPriority = e.style.getPropertyPriority('outline-offset');"
//
//                            + "e.style.setProperty("
//                            + "  'outline', '3px ' + lineStyle + ' ' + color, 'important');"
//                            + "e.style.setProperty("
//                            + "  'outline-offset', '1px', 'important');"
//                            + "e.style.setProperty("
//                            + "  'background-color', background, 'important');"
//
//                            + "window.setTimeout(function() {"
//                            + "  if (!e.isConnected) return;"
//                            + "  e.style.setProperty('outline', oldOutline, oldOutlinePriority);"
//                            + "  e.style.setProperty('outline-offset', oldOffset, oldOffsetPriority);"
//                            + "  e.style.setProperty("
//                            + "      'background-color', oldBackground, oldBackgroundPriority);"
//                            + "}, duration);",
//                    ele,
//                    type.outline,
//                    type.background,
//                    type.lineStyle,
//                    ConfigReader.getHighlightWaitMillis());
//
//        } catch (StaleElementReferenceException e) {
//            // The page re-rendered the element; highlighting is cosmetic.
//        } catch (WebDriverException e) {
//            // A highlighting failure should not fail the test.
//        }
//    }

    private void dynamicWait(long millis) {
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }

    public void clickWithJavaScriptExecutor(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void clickUsingActionClass(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        scrollIntoTheView(driver, element);
        actions.click(element).build().perform();
    }

    public void waitForOverlayToClose(WebDriver driver, WebElement triggerElement) {
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);

        // 1. Wait for the trigger's aria-expanded to go back to false
        fluentWait.until((Function<WebDriver, Boolean>) drv -> {
            try {
                String expanded = triggerElement.getAttribute("aria-expanded");
                return "false".equals(expanded);
            } catch (StaleElementReferenceException e) {
                return true; // element was replaced/detached — treat as closed
            }
        });

        // 2. Wait for the listbox overlay to detach/hide from the DOM
        fluentWait.until((Function<WebDriver, Boolean>) drv -> {
            java.util.List<WebElement> panels = drv.findElements(
                    By.xpath("//ul[contains(@role,'listbox')]"));
            return panels.isEmpty() || panels.stream().noneMatch(WebElement::isDisplayed);
        });
    }

    public void selectTargetDateRangeInTable(String fieldLabel, String tableName, LocalDate startDate, LocalDate endDate) {

        WebDriverCommonLibrary webDriverCommonLibrary = new WebDriverCommonLibrary(driver);
        // Locate & open the date-range input (reuses your section/field counting pattern)
        By inputLocator = By.xpath(buildTableFieldInputXpath(tableName, fieldLabel));
        WebElement dateRangeInput = webDriverCommonLibrary.explicitWaitByLocator(driver, inputLocator);
        webDriverCommonLibrary.scrollIntoTheView(driver, dateRangeInput);
        dateRangeInput.click();

        // Read the dynamically generated overlay panel id — never hardcode pn_id_*
        String panelId = dateRangeInput.getAttribute("aria-controls");
        By panelLocator = By.id(panelId);
        webDriverCommonLibrary.explicitWaitVisibilityByLocator(driver, panelLocator);

        // Navigate + click start date
        navigateCalendarToMonth(panelId, startDate);
        clickCalendarDate(panelId, startDate);

        // Navigate + click end date (panel stays open in range selectionMode)
        navigateCalendarToMonth(panelId, endDate);
        clickCalendarDate(panelId, endDate);

        closeCalendarIfOpen(panelLocator);
    }

    private String buildTableFieldInputXpath(String tableName, String fieldLabel) {
        return "//h3[normalize-space()='" + tableName + "']/following::table[1]//tbody/tr[1]/td["
                + "count(//h3[normalize-space()='" + tableName + "']/following::table[1]//thead/tr/th"
                + "[.//span[normalize-space()='" + fieldLabel + "']]/preceding-sibling::th)+1]"
                + "//input[@role='combobox']";
    }

    private void navigateCalendarToMonth(String panelId, LocalDate targetDate) {
        WebDriverCommonLibrary webDriverCommonLibrary = new WebDriverCommonLibrary(driver);

        YearMonth target = YearMonth.of(targetDate.getYear(), targetDate.getMonth());
        int maxAttempts = 24; // safety guard against infinite loop

        for (int i = 0; i < maxAttempts; i++) {
            WebElement monthEl = driver.findElement(By.xpath("//*[@id='" + panelId + "']//span[contains(@class,'p-datepicker-month')]"));
            WebElement yearEl = driver.findElement(By.xpath("//*[@id='" + panelId + "']//span[contains(@class,'p-datepicker-year')]"));

            YearMonth current = YearMonth.of(
                    Integer.parseInt(yearEl.getText().trim()),
                    Month.valueOf(monthEl.getText().trim().toUpperCase())
            );

            if (current.equals(target)) return;

            String navLabel = current.isBefore(target) ? "Next Month" : "Previous Month";
            By navBtn = By.xpath("//*[@id='" + panelId + "']//button[@aria-label='" + navLabel + "']");
            webDriverCommonLibrary.explicitWaitByLocator(driver, navBtn).click();
        }
        throw new RuntimeException("Could not navigate calendar to " + target);
    }

    private void clickCalendarDate(String panelId, LocalDate date) {
        WebDriverCommonLibrary webDriverCommonLibrary = new WebDriverCommonLibrary(driver);

        int primeNgMonthIndex = date.getMonthValue() - 1; // zero-indexed, per data-date attribute
        String dataDateValue = date.getYear() + "-" + primeNgMonthIndex + "-" + date.getDayOfMonth();

        By dateLocator = By.xpath(
                "//*[@id='" + panelId + "']//span[@data-date='" + dataDateValue + "'"
                        + " and not(ancestor::td[contains(@class,'p-datepicker-other-month')])]"
        );
        webDriverCommonLibrary.explicitWaitByLocator(driver, dateLocator).click();
    }

    private void closeCalendarIfOpen(By panelLocator) {
        List<WebElement> panels = driver.findElements(panelLocator);
        if (!panels.isEmpty() && panels.get(0).isDisplayed()) {
            driver.findElement(By.tagName("body")).click(); // click outside to force-close
        }
    }

    public void testselectTargetDateRangeInTable(String fieldLabel, String tableName, LocalDate startDate, LocalDate endDate) {

        WebDriverCommonLibrary webDriverCommonLibrary = new WebDriverCommonLibrary(driver);
        By targetDateRange = By.xpath("//p-calendar[@selectionmode='range']" + "//input[@role='combobox' and @aria-haspopup='dialog']");
        WebElement targetDateRangeElement = webDriverCommonLibrary.explicitWaitByLocator(driver, targetDateRange);
        webDriverCommonLibrary.scrollIntoTheView(driver, targetDateRangeElement);
//        webDriverCommonLibrary.clickUsingActionClass(driver,targetDateRangeElement);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, targetDateRangeElement);

    }


    public void waitForSetTime(int tt) {
        try {
            Thread.sleep(tt);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void updateTheValueInDataValuePropertiesFile(String key, String value) {
        String resourcesDir = System.getProperty("resources", "src/test/resources");
        String fileName = resourcesDir + File.separator + "config" + File.separator + "values.properties";

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(params.properties().setFileName(fileName));

        try {
            PropertiesConfiguration config = builder.getConfiguration();
            config.setProperty(key, value.trim());
            builder.save();
        } catch (ConfigurationException e) {
            System.out.println("Failed to update " + key + " in " + fileName + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void explicitWaitInvisibility(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement getVisibleCellByHeaderText(WebDriver driver, String columnHeaderText, int rowIndex) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        Object result = js.executeScript(
                "let tables = [...document.querySelectorAll('table')];" +
                        "let table = tables.find(t => [...t.querySelectorAll('thead th')]" +
                        "  .some(th => th.textContent.trim() === arguments[0]));" +
                        "if (!table) return null;" +
                        "let targetTh = [...table.querySelectorAll('thead th')]" +
                        "  .find(th => th.textContent.trim() === arguments[0]);" +
                        "let thRect = targetTh.getBoundingClientRect();" +
                        "let targetX = thRect.left + thRect.width / 2;" +
                        "let row = table.querySelectorAll('tbody tr')[arguments[1]];" +
                        "let cells = [...row.querySelectorAll('td')].filter(td => {" +
                        "  let style = td.getAttribute('style') || '';" +
                        "  return !style.includes('display: none') && td.getBoundingClientRect().width > 0;" +
                        "});" +
                        "let best = null, bestDist = Infinity;" +
                        "cells.forEach(td => {" +
                        "  let r = td.getBoundingClientRect();" +
                        "  let center = r.left + r.width / 2;" +
                        "  let dist = Math.abs(center - targetX);" +
                        "  if (dist < bestDist) { bestDist = dist; best = td; }" +
                        "});" +
                        "return best;",
                columnHeaderText, rowIndex
        );

        if (result == null) {
            throw new NoSuchElementException(
                    "Could not resolve grid cell for column header: '" + columnHeaderText + "' at row " + rowIndex
            );
        }
        return (WebElement) result;
    }

    public void clickGridColumnDropdown(WebDriver driver, String columnHeaderText, int rowIndex) {
        WebDriverCommonLibrary webDriverCommonLibrary;
        WebElement cell = getVisibleCellByHeaderText(driver, columnHeaderText, rowIndex);

        // scroll into view first — PrimeNG grids with horizontal scroll can have the
        // matched cell technically "visible" per getBoundingClientRect but outside the
        // current scrollable viewport
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);

        WebElement trigger = cell.findElement(By.cssSelector("div.p-dropdown-trigger"));

        // reuse your existing WaitUtils instead of a raw wait here
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(trigger));
        trigger.click();
    }

//    public void addWatermark(WebDriver driver, String watermarkText) {
//
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//
//        String script = "var watermark = document.createElement('div');" +
//                "watermark.innerHTML = '" + watermarkText + "';" +
//                "watermark.style.position = 'fixed';" +
//                "watermark.style.bottom = '20px';" +
//                "watermark.style.right = '20px';" +
//                "watermark.style.zIndex = '999999';" +
//                "watermark.style.opacity = '0.6';" +
//                "watermark.style.fontSize = '14px';" +
//                "watermark.style.fontWeight = 'bold';" +
//                "watermark.style.pointerEvents = 'none';" +
//                "document.body.appendChild(watermark);";
//        js.executeScript(script);
//    }

    public WebElement waitForElementToBeClickable(WebDriver driver, By locator) {
        WebElement ele = new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(locator));
        scrollIntoTheView(driver, ele);
        highLightTheElement(driver, ele);
        return ele;
    }

    public WebElement waitForVisibility(By locator) {
        WebElement ele = new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOfElementLocated(locator));
        scrollIntoTheView(driver, ele);
        highLightTheElement(driver, ele);
        return ele;
    }

    public void waitForInvisibility(WebDriver driver, By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement waitForElementToBeClickableIgnoringStale(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public FluentWait<WebDriver> staleTolerantWait(WebDriver driver) {
        return new FluentWait<>(driver)
                .withTimeout(TIMEOUT)
                .pollingEvery(Duration.ofMillis(250))
                .ignoreAll(List.of(
                        StaleElementReferenceException.class,
                        NoSuchElementException.class,
                        ElementClickInterceptedException.class,
                        ElementNotInteractableException.class));
    }

    /**
     * Re-finds the element on every attempt, so a stale reference can never stick.
     */
    public void clickWhenReady(WebDriver driver, By locator) {
        staleTolerantWait(driver).until(d -> {
            WebElement el = d.findElement(locator);
            if (!el.isDisplayed() || !el.isEnabled()) return false;
            el.click();
            return true;
        });
    }

    public void typeWhenReady(WebDriver driver, By locator, String text) {
        staleTolerantWait(driver).until(d -> {
            WebElement el = d.findElement(locator);
            if (!el.isDisplayed()) return false;
            el.clear();
            el.sendKeys(text);
            return true;
        });
    }

    public boolean isDisplayedNow(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public synchronized void updateTheValueInJsonFile(String key, String value) {
        String resourcesDir = System.getProperty("resources", "src/test/resources");
        File file = new File(resourcesDir + File.separator + "config" + File.separator + "capturedValues.json");

        try {
            Map<String, Object> data = new LinkedHashMap<>();
            if (file.exists() && file.length() > 0) {
                data = MAPPER.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<LinkedHashMap<String, Object>>() {});
            } else if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            data.put(key, value.trim());
            MAPPER.writeValue(file, data);
        } catch (IOException e) {
            System.out.println("Failed to update " + key + " in " + file.getPath() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    public void waitForLoadingOverlay() {

        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofMillis(100))
                    .until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                            "const o = document.querySelector('div.loading-overlay');" +
                                    "if (!o) return true;" +
                                    "const s = getComputedStyle(o);" +
                                    "const r = o.getBoundingClientRect();" +
                                    "return s.display === 'none' ||" +
                                    "s.visibility === 'hidden' ||" +
                                    "s.opacity === '0' ||" +
                                    "s.pointerEvents === 'none' ||" +
                                    "r.width === 0 || r.height === 0;"
                    ));

            System.out.println("Loading overlay disappeared successfully.");

        } catch (TimeoutException e) {

            throw new TimeoutException(
                    "Loading overlay is still active after 30 seconds. " +
                            "Dropdown interaction cancelled.",
                    e
            );
        }
    }



    public void clickWithOverlayRetry(WebElement element) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait before the first click
        waitForLoadingOverlay();

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();

        } catch (ElementClickInterceptedException e) {

            System.out.println("Click intercepted. Retrying after overlay wait...");
            debugClickInterceptedElement(element);
            waitForLoadingOverlay();

            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                element.click();

            } catch (StaleElementReferenceException stale) {

                throw new StaleElementReferenceException(
                        "Dropdown became stale during retry.",
                        stale
                );
            }
        }
    }


    public void waitForOverlayToDisappear(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
                d.findElements(LOADING_OVERLAY).stream().noneMatch(WebElement::isDisplayed));
    }


    public void verifyFieldValue(
            WebElement field,
            String fieldName,
            String expectedValue) {

        String actualValue = field.getAttribute("value");

        if (actualValue == null || actualValue.isBlank()) {
            actualValue = field.getText().trim();
        }

        if (!actualValue.equals(expectedValue)) {
            throw new AssertionError(
                    "Field '" + fieldName +
                            "' expected value: '" + expectedValue +
                            "' but actual value: '" + actualValue + "'"
            );
        }
    }

    public void verifyFieldState(WebElement field, String fieldName, String expectedState) {

        boolean actualDisabled = isFieldDisabled(field);
        boolean expectedDisabled;
        if ("disabled".equalsIgnoreCase(expectedState)) {
            expectedDisabled = true;
        } else if ("enabled".equalsIgnoreCase(expectedState)) {
            expectedDisabled = false;
        } else {
            Assert.fail(String.format("Invalid expected state '%s' for field '%s'. Expected 'enabled' or 'disabled'.", expectedState, fieldName));            return;
        }
        Assert.assertEquals(actualDisabled, expectedDisabled, "Field state mismatch for '" + fieldName + "'. Expected: " + expectedState + ", Actual: " + (actualDisabled ? "disabled" : "enabled"));
    }

    private boolean isFieldDisabled(WebElement field) {

        String ariaDisabled = field.getAttribute("aria-disabled");

        if ("true".equalsIgnoreCase(ariaDisabled)) {
            return true;
        }

        String disabled = field.getAttribute("disabled");

        if (disabled != null) {
            return true;
        }

        String classAttribute = field.getAttribute("class");

        if (classAttribute != null &&
                classAttribute.contains("p-disabled")) {
            return true;
        }

        return !field.isEnabled();
    }

    public WebElement waitForFirstVisible(By locator, int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    for (WebElement e : d.findElements(locator)) {
                        if (e.isDisplayed()) return e;
                    }
                    return null;
                });
    }

    public void addWatermark(WebDriver driver, String text) {
        String logoBase64 = loadLogoBase64("/images/xbs-logo.png");

        String script =
                "var old = document.getElementById('qa-watermark'); if (old) old.remove();" +
                        "var w = document.createElement('div'); w.id = 'qa-watermark';" +
                        "w.style.cssText = 'position:fixed;right:16px;bottom:16px;z-index:2147483647;" +
                        "pointer-events:none;display:flex;align-items:center;gap:8px;opacity:0.6;" +
                        "background:rgba(255,255,255,0.9);padding:6px 10px;border-radius:6px;" +
                        "font:12px Arial,sans-serif;color:#333;';" +
                        "var img = document.createElement('img');" +
                        "img.src = 'data:image/png;base64,' + arguments[0];" +
                        "img.style.height = '28px';" +
                        "var t = document.createElement('span'); t.textContent = arguments[1];" +
                        "w.appendChild(img); w.appendChild(t); document.body.appendChild(w);";

        ((JavascriptExecutor) driver).executeScript(script, logoBase64, text);
    }

    private String loadLogoBase64(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Logo not found on classpath: " + resourcePath);
            }
            return Base64.getEncoder().encodeToString(is.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void verifyDateField(String section, String field, String dateExpression) {
        By locator = dateFieldLocator(section, field);

        LocalDate expectedBefore = DateExpressionResolver.resolve(dateExpression);

        String shown = "";
        try {
            shown = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                String v = d.findElement(locator).getAttribute("value");
                return (v != null && !v.isBlank()) ? v.trim() : null;
            });
        } catch (TimeoutException ignored) { }

        WebElement dateInput = driver.findElement(dateFieldLocator(section, field));
        highLightTheElement(driver,dateInput);
        LocalDate expectedAfter = DateExpressionResolver.resolve(dateExpression);

        Assert.assertFalse(shown.isEmpty(), "[" + section + "] '" + field + "' is empty");

        LocalDate actual;
        try {
            actual = LocalDate.parse(shown, APP_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new AssertionError("[" + section + "] '" + field + "' shows '" + shown
                    + "', expected format dd-MM-yyyy");
        }

        Assert.assertTrue(actual.equals(expectedBefore) || actual.equals(expectedAfter),
                "[" + section + "] '" + field + "' shows " + actual + " but '" + dateExpression
                        + "' resolves to " + expectedAfter);
    }

    private By checklistInputLocator(String name) {
        return By.xpath("//div[normalize-space(translate(., '\u00a0', ' '))=" + xp(name) + "]/input[@type='checkbox']");
    }

    public void verifyChecklistState(String names, boolean expectedSelected) {

        List<String> failures = new ArrayList<>();

        for (String name : names.split(",")) {

            String item = name.trim();

            if (item.isEmpty()) {
                continue;
            }

            By locator = checklistInputLocator(item);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(locator));

            } catch (TimeoutException e) {

                failures.add(
                        "'" + item + "' not found on the page. "
                                + "Checkboxes present: " + listCheckboxLabels()
                );

                continue;
            }

            List<WebElement> matches = driver.findElements(locator);

            if (matches.isEmpty()) {
                failures.add("'" + item + "' checkbox was not found.");
                continue;
            }

            WebElement checkbox = matches.get(0);

            if (!checkbox.isDisplayed()) {
                failures.add(
                        "'" + item + "' is present but hidden. "
                                + "Check whether the correct tab or panel is open."
                );
                continue;
            }

            boolean selected = checkbox.isSelected();

            // Green = selected; red = not selected.
            if (selected) {
                highLightCheckboxAndLabel(driver, checkbox);
            } else {
                highLightUncheckedCheckboxAndLabel(driver, checkbox);
            }

            // Verify the actual checkbox state against the expected state.
            if (selected != expectedSelected) {
                failures.add(
                        "'" + item + "' is "
                                + (selected ? "selected" : "not selected")
                                + ", expected "
                                + (expectedSelected ? "selected" : "not selected")
                );
            }
        }

        Assert.assertTrue(
                failures.isEmpty(),
                "Checklist verification failed: " + String.join("; ", failures)
        );
    }

    /**
     * Green highlight for a selected checkbox.
     */
    public void highLightCheckboxAndLabel(
            WebDriver driver, WebElement checkbox) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});"
//                        + "arguments[0].style.outline='3px solid #00C853';"
                        + "arguments[0].style.outlineOffset='3px';"
                        + "arguments[0].style.boxShadow='0 0 8px #00C853';"
                        + "var p = arguments[0].parentElement;"
                        + "if (p) {"
//                        + "p.style.outline='2px solid #00C853';"
                        + "p.style.outlineOffset='3px';"
                        + "p.style.backgroundColor='rgba(0,200,83,0.15)';"
                        + "}",
                checkbox
        );
    }

    /**
     * Red highlight for a checkbox that is not selected.
     */
    public void highLightUncheckedCheckboxAndLabel(
            WebDriver driver, WebElement checkbox) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});"
//                        + "arguments[0].style.outline='3px solid #FF1744';"
                        + "arguments[0].style.outlineOffset='3px';"
                        + "arguments[0].style.boxShadow='0 0 8px #FF1744';"
                        + "var p = arguments[0].parentElement;"
                        + "if (p) {"
//                        + "p.style.outline='2px solid #FF1744';"
                        + "p.style.outlineOffset='3px';"
                        + "p.style.backgroundColor='rgba(255,23,68,0.15)';"
                        + "}",
                checkbox
        );
    }

    /**
     * Lists checkbox parent text for failure messages.
     */
    private String listCheckboxLabels() {

        List<String> labels = new ArrayList<>();

        for (WebElement element : driver.findElements(
                By.xpath("//input[@type='checkbox']/parent::div"))) {

            if (labels.size() == 15) {
                labels.add("...");
                break;
            }

            try {
                String text = element.getAttribute("textContent");

                if (text == null) {
                    text = "";
                }

                labels.add(
                        "[" + text.replace('\u00a0', '~').trim()
                                + (element.isDisplayed() ? "" : " (hidden)") + "]"
                );

            } catch (StaleElementReferenceException ignored) {
                // Ignore elements that disappear during inspection.
            }
        }

        return labels.size() + " listed: " + labels;
    }

    public void selectDate(String section, String field, String dateExpression) {
        By locator = dateFieldLocator(section, field);

        LocalDate target = DateExpressionResolver.resolve(dateExpression);
        String text = target.format(APP_DATE_FORMAT);

        WebElement input = findDisplayed(locator, Duration.ofSeconds(10));

        // If this datepicker is already open, dismiss it before clicking the input.
        if ("true".equals(input.getAttribute("aria-expanded"))) {
            input.sendKeys(Keys.ESCAPE);
        }

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
        input.sendKeys(Keys.ESCAPE);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2), Duration.ofMillis(100))
                    .until(d -> text.equals(input.getAttribute("value").trim()));
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "[" + section + "] '" + field + "' should be " + text
                            + " ('" + dateExpression + "') but shows '"
                            + input.getAttribute("value") + "'",
                    e);
        }
    }

    public void selectDates(String section, String field, String dateExpression) {
        By locator = dateCCMFieldLocator(section, field);

        LocalDate target = DateExpressionResolver.resolve(dateExpression);
        String text = target.format(APP_DATE_FORMAT);

        WebElement input = findDisplayed(locator, Duration.ofSeconds(10));

        // If this datepicker is already open, dismiss it before clicking the input.
        if ("true".equals(input.getAttribute("aria-expanded"))) {
            input.sendKeys(Keys.ESCAPE);
        }

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
        input.sendKeys(Keys.ESCAPE);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2), Duration.ofMillis(100))
                    .until(d -> text.equals(input.getAttribute("value").trim()));
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "[" + section + "] '" + field + "' should be " + text
                            + " ('" + dateExpression + "') but shows '"
                            + input.getAttribute("value") + "'",
                    e);
        }
    }
    private WebElement findDisplayed(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(d -> {
            for (WebElement el : d.findElements(locator)) {
                try {
                    if (el.isDisplayed() && el.isEnabled()) {
                        return el;
                    }
                } catch (StaleElementReferenceException ignored) { }
            }
            return null;   // keep polling
        });
    }

    public void selectTheDate(String section, String field, String dateExpression) {

        By locator = dateFieldLocatorSet(section, field);
        LocalDate target = DateExpressionResolver.resolve(dateExpression);
        String text = target.format(APP_DATE_FORMAT);

        long start = System.currentTimeMillis();

        WebElement input = findDisplayed(locator, Duration.ofSeconds(3));
        highLightTheElement(driver, input);

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
        input.sendKeys(Keys.ENTER);          // commits typed value, closes overlay in PrimeNG

        closeCalendarPopup();                // guaranteed close, selector-independent

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2), Duration.ofMillis(100))
                    .until(d -> text.equals(input.getAttribute("value").trim()));
        } catch (TimeoutException e) {
            throw new AssertionError("[" + section + "] '" + field + "' should be " + text
                    + " ('" + dateExpression + "') but shows '" + input.getAttribute("value") + "'", e);
        }
        System.out.println("Date verified in " + (System.currentTimeMillis() - start) + " ms");
    }

    private void closeCalendarPopup() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Visible check using any common PrimeNG overlay class
        String isOpenJs =
                "return Array.from(document.querySelectorAll("
                        + "'.p-datepicker, .p-datepicker-panel, p-calendar .p-overlay'))"
                        + ".some(e => e.offsetParent !== null);";

        if (Boolean.TRUE.equals(js.executeScript(isOpenJs))) {
            // Outside click: PrimeNG listens for document clicks and hides the overlay
            js.executeScript("document.activeElement && document.activeElement.blur();"
                    + "document.body.click();");
        }

        // Short poll, no Selenium element lookup
        long end = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < end
                && Boolean.TRUE.equals(js.executeScript(isOpenJs))) {
            try { Thread.sleep(100); } catch (InterruptedException ignored) { }
        }
    }

    public void debugClickInterceptedElement(WebElement element) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        Object result = js.executeScript(
                "const r = arguments[0].getBoundingClientRect();" +
                        "const x = r.left + r.width / 2;" +
                        "const y = r.top + r.height / 2;" +
                        "const e = document.elementFromPoint(x, y);" +
                        "return {" +
                        "target: arguments[0].outerHTML," +
                        "receiver: e ? e.outerHTML : 'NULL'," +
                        "x: x, y: y" +
                        "};",
                element
        );

        System.out.println("CLICK INTERCEPTION DEBUG: " + result);
    }

}
