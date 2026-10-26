package utils;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.openqa.selenium.*;
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
import java.util.LinkedHashMap;
import java.util.Map;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class WebDriverCommonLibrary {

    private final WebDriver driver;
    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final ObjectMapper MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final By LOADING_OVERLAY = By.cssSelector(".loading-overlay");

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

    public void addWatermark(WebDriver driver, String watermarkText) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = "var watermark = document.createElement('div');" +
                "watermark.innerHTML = '" + watermarkText + "';" +
                "watermark.style.position = 'fixed';" +
                "watermark.style.bottom = '20px';" +
                "watermark.style.right = '20px';" +
                "watermark.style.zIndex = '999999';" +
                "watermark.style.opacity = '0.6';" +
                "watermark.style.fontSize = '14px';" +
                "watermark.style.fontWeight = 'bold';" +
                "watermark.style.pointerEvents = 'none';" +
                "document.body.appendChild(watermark);";
        js.executeScript(script);
    }

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
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .pollingEvery(Duration.ofMillis(50))
                    .until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                            "const o=document.querySelector('div.loading-overlay');" +
                                    "if(!o) return true;" +
                                    "const s=getComputedStyle(o), r=o.getBoundingClientRect();" +
                                    "return s.display==='none' || s.visibility==='hidden' || s.opacity==='0'" +
                                    " || s.pointerEvents==='none' || r.width===0 || r.height===0;"));
        } catch (TimeoutException e) {
            System.out.println("WARN: loading overlay still present after 10s");
        }
    }

    public void clickWithOverlayRetry(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("Click intercepted. Waiting for loading overlay...");
            waitForLoadingOverlay();
            try {
                element.click();
            } catch (StaleElementReferenceException stale) {
                throw new StaleElementReferenceException("Element became stale after loading overlay disappeared.", stale);
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

}
