package pages;

import base.DriverFactory;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.DateExpressionResolver;
import utils.WebDriverCommonLibrary;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

/*
 * Copyright © 2026 Invensoft.
 * ERP Automation Framework
 */

public class CommodityContractManagementPage{

    static WebDriver driver;
    static WebDriverCommonLibrary webDriverCommonLibrary;

    @FindBy(xpath = "//span[contains(text(),'Commodity Contract Management - Create')]")
    private WebElement label_CreateGeneralPopupTitle;

    @FindBy(xpath = "//span[@role='combobox' and @aria-label='Class']")
    private WebElement dropdown_Class;

    public CommodityContractManagementPage(WebDriver initDriver) {
        this.driver = initDriver;
        this.webDriverCommonLibrary = new WebDriverCommonLibrary(driver);
        PageFactory.initElements(driver, this);
    }

    public void getCreateGeneralPopupTitle() {
        webDriverCommonLibrary.highLightTheElement(driver, label_CreateGeneralPopupTitle);
    }

    public void selectClassDropdown(String filterText, String optionText) {
        webDriverCommonLibrary.explicitWait(driver, dropdown_Class);
        dropdown_Class.click();

        By filterInputLocator = By.xpath("//div[contains(@class,'p-dropdown-panel')]//input[contains(@class,'p-dropdown-filter')]");
        WebElement filterInput = driver.findElement(filterInputLocator);
        webDriverCommonLibrary.explicitWait(driver, filterInput);
        filterInput.sendKeys(filterText);

        String optionXpath = "//div[contains(@class,'p-dropdown-panel')]//li[contains(@class,'p-dropdown-item') and normalize-space(text())='" + optionText + "']";
        WebElement option = driver.findElement(By.xpath(optionXpath));
        webDriverCommonLibrary.explicitWait(driver, option);
        option.click();
    }

    public void selectDropdownValue(String section, String field, String value) {
        WebElement dropdown = null;

        // ============CASE 1: Product Details -> Product============ //
        if (section.equalsIgnoreCase("Product Details") && field.equalsIgnoreCase("Product")) {
            dropdown = driver.findElement(By.xpath("//div[@formarrayname='item_details']" +
                            "//tbody/tr[1]" + "/td[2]" + "//p-dropdown" + "//span[@role='combobox']"));
            System.out.println("Dropdown found using Product Details -> Product locator.");
        } else {
            // ===========Case 2 : Normal dropdown floating table inside the field ============= //
            By insideFieldLocator = By.xpath("//p-card[@class='p-element']" + "[.//span[contains(text(),'" + section + "')]]" + "//span[contains(@aria-label,'" + field + "')]");

            List<WebElement> insideFieldDropdown = driver.findElements(insideFieldLocator);
            System.out.println("Inside-field elements found: " + insideFieldDropdown.size());

            // Find FIRST VISIBLE element
            for (WebElement element : insideFieldDropdown) {
                if (element.isDisplayed()) {
                    dropdown = element;
                    System.out.println("Dropdown found using INSIDE-FIELD locator: " + section + " -> " + field);
                    break;
                }
            }
            // ============ CASE 3: Floating label ABOVE the field ============ //
            if (dropdown == null) {
                By aboveFieldLocator = By.xpath(
                        "//label[.//span[normalize-space()='" + field + "']]" +
                                "/..//div[@aria-label='dropdown trigger']"
                );
                try {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    dropdown = wait.until(ExpectedConditions.elementToBeClickable(aboveFieldLocator));
                    System.out.println("Dropdown found using ABOVE-FIELD locator: " + section + " -> " + field);
                } catch (TimeoutException e) {
                    throw new NoSuchElementException("Unable to locate clickable dropdown. " + "Section: [" + section + "] " + "Field: [" + field + "]");
                }
            }
            // ========== NO VISIBLE DROPDOWN FOUND===========  //
            if (dropdown == null) {
                throw new NoSuchElementException(
                        "Unable to locate a VISIBLE dropdown." +
                                " Section: [" + section + "]" +
                                " Field: [" + field + "]" +
                                " Tried: INSIDE-FIELD and ABOVE-FIELD locators.");
            }
        }
        // ============CLICK DROPDOWN=============  //
        webDriverCommonLibrary.explicitWait(driver, dropdown);
        webDriverCommonLibrary.scrollIntoTheView(driver, dropdown);
        dropdown.click();

        // =========SEARCH BOX ============== //
        WebElement searchboxoption = driver.findElement(By.xpath("//input[@role='searchbox']"));
        System.out.println("Search box located successfully.");
        webDriverCommonLibrary.explicitWait(driver, searchboxoption);
        searchboxoption.clear();
        searchboxoption.sendKeys(value);
        webDriverCommonLibrary.scrollIntoTheView(driver, searchboxoption);

        // ======== SELECT DROPDOWN OPTION ========= //
        WebElement selectOption = driver.findElement(By.xpath("//li[@role='option' and normalize-space()='" + value + "']"));
        webDriverCommonLibrary.explicitWait(driver, selectOption);
        webDriverCommonLibrary.scrollIntoTheView(driver, selectOption);
        webDriverCommonLibrary.clickUsingActionClass(driver, selectOption);
    }

//        public void selectDropdownValue(String section, String field, String value) {
//        Duration originalImplicit = driver.manage().timeouts().getImplicitWaitTimeout();
//        driver.manage().timeouts().implicitlyWait(Duration.ZERO); // explicit waits only
//        try {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//            wait.pollingEvery(Duration.ofMillis(200));
//
//            // 1. Locate the dropdown: all candidates polled together, first visible wins
//            List<By> candidates = new ArrayList<>();
//            if (section.equalsIgnoreCase("Product Details") && field.equalsIgnoreCase("Product")) {
//                candidates.add(By.xpath("//div[@formarrayname='item_details']//tbody/tr[1]/td[2]//p-dropdown//span[@role='combobox']"));
//            } else {
//                candidates.add(By.xpath("//p-card[.//span[contains(text(),'" + section + "')]]//span[contains(@aria-label,'" + field + "')]"));
//                candidates.add(By.xpath("//label[.//span[normalize-space()='" + field + "']]/..//div[@aria-label='dropdown trigger']"));
//            }
//
//            WebElement dropdown = wait.until(d -> {
//                for (By by : candidates) {
//                    for (WebElement e : d.findElements(by)) {
//                        try {
//                            if (e.isDisplayed() && e.isEnabled()) return e;
//                        } catch (StaleElementReferenceException ignored) { }
//                    }
//                }
//                return null;
//            });
//
//            // 2. Open it
//            webDriverCommonLibrary.scrollIntoTheView(driver, dropdown);
//            try {
//                wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();
//            } catch (ElementClickInterceptedException e) {
//                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
//            }
//
//            // 3. Search inside the open overlay panel only
//            String panel = "//div[contains(@class,'p-dropdown-panel')]";
//            WebElement searchBox = wait.until(
//                    ExpectedConditions.visibilityOfElementLocated(By.xpath(panel + "//input[@role='searchbox']")));
//            searchBox.sendKeys(value);
//
//            // 4. Wait for the filtered option, then click it
//            By optionLocator = By.xpath(panel + "//li[@role='option' and normalize-space()='" + value + "']");
//            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
//            try {
//                option.click();
//            } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
//                webDriverCommonLibrary.clickUsingActionClass(driver, driver.findElement(optionLocator));
//            }
//        } catch (TimeoutException e) {
//            throw new NoSuchElementException("Unable to select [" + value + "] in Section: [" + section + "] Field: [" + field + "]", e);
//        } finally {
//            driver.manage().timeouts().implicitlyWait(originalImplicit);
//        }
//    }

    public void selectModifiedDropdownValue(String section, String field, String value) {
        WebElement dropdown;

        if (section.equalsIgnoreCase("Product Details") && field.equalsIgnoreCase("Product")) {
            dropdown = driver.findElement(By.xpath("//div[@formarrayname='item_details']"
                    + "//tbody/tr[1]/td[2]//p-dropdown//span[@role='combobox']"));
        } else {
            dropdown = findNormalDropdown(section, field);
        }
        webDriverCommonLibrary.scrollIntoTheView(driver, dropdown);
        webDriverCommonLibrary.clickWithOverlayRetry(dropdown);

        // Scope to the open panel so old/hidden overlays are not picked up
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'p-dropdown-panel')]//input[@role='searchbox']")));
        searchBox.click();
        searchBox.clear();
        searchBox.sendKeys(value);

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'p-dropdown-panel')]"
                        + "//li[@role='option' and normalize-space()='" + value + "']")));
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, option);
    }

    private WebElement findNormalDropdown(String section, String field) {
        // Works for both empty and already-filled dropdowns (label does not change)
        By aboveField = By.xpath("//p-card[.//span[contains(text(),'" + section + "')]]"
                + "//label[.//span[normalize-space()='" + field + "']]/..//div[@aria-label='dropdown trigger']");
        // Fallback for fields where the trigger is inside the field (only matches when empty)
        By insideField = By.xpath("//p-card[@class='p-element'][.//span[contains(text(),'" + section + "')]]"
                + "//span[contains(@aria-label,'" + field + "')]");

        Duration originalImplicit = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> {
                        for (By by : new By[]{aboveField, insideField}) {
                            for (WebElement e : d.findElements(by)) {
                                if (e.isDisplayed() && e.isEnabled()) {
                                    return e;
                                }
                            }
                        }
                        return null;   // null makes the wait keep polling
                    });
        } catch (TimeoutException e) {
            throw new NoSuchElementException("Unable to locate a visible dropdown. Section: ["
                    + section + "] Field: [" + field + "]");
        } finally {
            driver.manage().timeouts().implicitlyWait(originalImplicit);
        }
    }

    public void selectDropdownValueupdated(String section, String field, String value) {
        By dropdownLocator;
        if (section.equalsIgnoreCase("Product Details")
                && field.equalsIgnoreCase("Product")) {

            dropdownLocator = By.xpath(
                    "//div[@formarrayname='item_details']" +
                            "//tbody/tr[1]/td[2]//p-dropdown//span[@role='combobox']"
            );

        } else {

            // First: dropdown inside the field
            By insideField = By.xpath(
                    "//p-card[@class='p-element']" +
                            "[.//span[contains(text(),'" + section + "')]]" +
                            "//span[contains(@aria-label,'" + field + "')]"
            );

            // Second: dropdown trigger above/associated with the field
            By aboveField = By.xpath(
                    "//p-card[.//span[contains(text(),'" + section + "')]]" +
                            "//label[.//span[normalize-space()='" + field + "']]" +
                            "/..//div[@aria-label='dropdown trigger']"
            );

            Duration originalImplicit =
                    driver.manage().timeouts().getImplicitWaitTimeout();

            driver.manage().timeouts().implicitlyWait(Duration.ZERO);

            try {

                dropdownLocator = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .ignoring(StaleElementReferenceException.class)
                        .until(d -> {

                            // 1. First try inside-field dropdown
                            for (WebElement e : d.findElements(insideField)) {
                                if (e.isDisplayed() && e.isEnabled()) {
                                    return insideField;
                                }
                            }

                            // 2. If not found, try above-field dropdown
                            for (WebElement e : d.findElements(aboveField)) {
                                if (e.isDisplayed() && e.isEnabled()) {
                                    return aboveField;
                                }
                            }

                            return null;
                        });

            } finally {
                driver.manage().timeouts().implicitlyWait(originalImplicit);
            }
        }

        By panel = By.xpath(
                "//div[contains(@class,'p-dropdown-panel')]"
        );

        By searchBox = By.xpath(
                "//div[contains(@class,'p-dropdown-panel')]" +
                        "//input[@role='searchbox']"
        );

        By option = By.xpath(
                "//div[contains(@class,'p-dropdown-panel')]" +
                        "//li[@role='option' and normalize-space()='" + value + "']"
        );

        // 1. Open dropdown
        webDriverCommonLibrary.staleTolerantWait(driver).until(d -> {

            WebElement dd = d.findElement(dropdownLocator);

            if (!"true".equals(dd.getAttribute("aria-expanded"))) {
                dd.click();
            }

            return webDriverCommonLibrary.isDisplayedNow(d, searchBox);
        });

        // 2. Type into filter
        webDriverCommonLibrary.typeWhenReady(
                driver,
                searchBox,
                value
        );

        // 3. Select option
        webDriverCommonLibrary.clickWhenReady(
                driver,
                option
        );
    }

    public void selectTableDropdownValue(String value, String field, String section) {

        //Find dropdown element
        WebElement dropdown = driver.findElement(By.xpath("//h3[normalize-space()='"+section+"']/following::table[1]//tbody/tr[1]/td[count(//h3[normalize-space()='"+section+"']/following::table[1]//thead/tr/th[.//span[normalize-space()='"+field+"']]/preceding-sibling::th)+1]//div[contains(@class,'p-dropdown-trigger')]"));
//        webDriverCommonLibrary.explicitWait(driver,dropdown);
        webDriverCommonLibrary.scrollIntoTheView(driver,dropdown);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,dropdown);

        //search required options
        WebElement selectSearchOption = driver.findElement(By.xpath("//span[@role='combobox' and @aria-haspopup='listbox' and @aria-expanded='true']"));
        webDriverCommonLibrary.scrollIntoTheView(driver,selectSearchOption);
        webDriverCommonLibrary.explicitWait(driver,selectSearchOption);
        webDriverCommonLibrary.explicitWaitVisibility(driver,selectSearchOption);
        selectSearchOption.sendKeys(value);

        //Click on required option
        WebElement selectOption = driver.findElement(By.xpath("//ul[contains(@role,'listbox')]//li[@role='option' and normalize-space(@aria-label)='"+value+"']"));
        webDriverCommonLibrary.explicitWait(driver,selectOption);
        webDriverCommonLibrary.scrollIntoTheView(driver,selectOption);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,selectOption);
    }

    public void selectTableType2DropdownValue(String value, String field, String section) {

        //Find field element
        WebElement dropdown = driver.findElement(By.xpath("//h3[normalize-space()='"+section+"']/following::table[1]//tbody/tr[1]/td[count(//h3[normalize-space()='"+section+"']/following::table[1]//thead/tr/th[.//span[normalize-space()='"+field+"']]/preceding-sibling::th)+1]//div[contains(@class,'p-dropdown-trigger')]"));
        webDriverCommonLibrary.explicitWait(driver,dropdown);
        webDriverCommonLibrary.scrollIntoTheView(driver,dropdown);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,dropdown);

        //search required options
        WebElement selectSearchOption = driver.findElement(By.xpath("//input[@role='searchbox' and @type='text']"));
        webDriverCommonLibrary.scrollIntoTheView(driver,selectSearchOption);
        webDriverCommonLibrary.explicitWait(driver,selectSearchOption);
        webDriverCommonLibrary.explicitWaitVisibility(driver,selectSearchOption);
        selectSearchOption.sendKeys(value);

        //Click on required option
        WebElement selectOption = driver.findElement(By.xpath("//ul[contains(@role,'listbox')]//li[@role='option' and normalize-space(@aria-label)='"+value+"']"));
        webDriverCommonLibrary.explicitWait(driver,selectOption);
        webDriverCommonLibrary.scrollIntoTheView(driver,selectOption);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,selectOption);
    }

    public void
    selectTableType2DropdownValues(String section, String field, String value) {

        //Find field element
        WebElement dropdown = driver.findElement(By.xpath("//h3[normalize-space()='"+section+"']/following::table[1]//tbody/tr[1]/td[count(//h3[normalize-space()='"+section+"']/following::table[1]//thead/tr/th[.//span[normalize-space()='"+field+"']]/preceding-sibling::th)+1]//div[contains(@class,'p-dropdown-trigger')]"));
        webDriverCommonLibrary.scrollIntoTheView(driver,dropdown);
        webDriverCommonLibrary.explicitWait(driver,dropdown);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,dropdown);

        //search required options
        WebElement selectSearchOption = driver.findElement(By.xpath("//input[@role='searchbox' and @type='text']"));
        webDriverCommonLibrary.scrollIntoTheView(driver,selectSearchOption);
        webDriverCommonLibrary.explicitWait(driver,selectSearchOption);
        webDriverCommonLibrary.explicitWaitVisibility(driver,selectSearchOption);
        selectSearchOption.sendKeys(value);

        //Click on required option
        WebElement selectOption = driver.findElement(By.xpath("//ul[contains(@role,'listbox')]//li[@role='option' and normalize-space(@aria-label)='"+value+"']"));
        webDriverCommonLibrary.explicitWait(driver,selectOption);
        webDriverCommonLibrary.scrollIntoTheView(driver,selectOption);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,selectOption);
        // After clicking selectOption in the previous dropdown call, before returning:
        webDriverCommonLibrary.explicitWaitInvisibility(driver, By.cssSelector(".p-overlay-mask, .p-dropdown-panel"));
    }

//    public void selectLiteralDropdownValue(String section, String field, String value) {
//        //Find class element
//        WebElement dropdown = driver.findElement(By.xpath("//p-card[@class='p-element'][.//span[contains(text()," + xpathLiteral(section) + ")]]//span[contains(@aria-label," + xpathLiteral(field) + ")]"));
//        dropdown.click();
//        webDriverCommonLibrary.waitForSetTime(200);
//        //search required options
//        WebElement searchboxoption = driver.findElement(By.xpath("//span[@aria-label=" + xpathLiteral(field) + " and @aria-expanded='true']/following::input[@role='searchbox']"));
//        webDriverCommonLibrary.explicitWait(driver, searchboxoption);
//        searchboxoption.clear();
//        searchboxoption.sendKeys(value);
//        //Click on required option
//        WebElement selectOption = driver.findElement(By.xpath("//ul[contains(@class,'p-dropdown-items')]//span[normalize-space(text())=" + xpathLiteral(value) + "]"));
//        webDriverCommonLibrary.explicitWait(driver, selectOption);
//        webDriverCommonLibrary.scrollIntoTheView(driver, selectOption);
//        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, selectOption);
//    }
//
//    /**
//     * Safely wraps a raw string as an XPath string literal, handling values
//     * that contain single quotes, double quotes, or both — via concat()
//     * when necessary. Prevents malformed XPath like:
//     *   @aria-label='BUYER'S CALL'   (breaks on the embedded apostrophe)
//     */
//    String xpathLiteral(String value) {
//        if (!value.contains("'")) {
//            return "'" + value + "'";
//        }
//        if (!value.contains("\"")) {
//            return "\"" + value + "\"";
//        }
//        // Contains BOTH quote types — split on ' and rebuild with concat()
//        String[] parts = value.split("'", -1);
//        StringBuilder sb = new StringBuilder("concat(");
//        for (int i = 0; i < parts.length; i++) {
//            sb.append("'").append(parts[i]).append("'");
//            if (i < parts.length - 1) {
//                sb.append(", \"'\", ");
//            }
//        }
//        sb.append(")");
//        return sb.toString();
//    }


    public void selectLiteralDropdownValue(String section, String field, String value) {
        WebElement dropdown;

        if (section.equalsIgnoreCase("Product Details") && field.equalsIgnoreCase("Product")) {
            dropdown = driver.findElement(By.xpath("//div[@formarrayname='item_details']"
                    + "//tbody/tr[1]/td[2]//p-dropdown//span[@role='combobox']"));
        } else {
            dropdown = findLiteralNormalDropdown(section, field);
        }
        webDriverCommonLibrary.scrollIntoTheView(driver, dropdown);
        webDriverCommonLibrary.clickWithOverlayRetry(dropdown);

        // Scope to the open panel so old/hidden overlays are not picked up
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'p-dropdown-panel')]//input[@role='searchbox']")));
        searchBox.click();
        searchBox.clear();
        searchBox.sendKeys(value);

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'p-dropdown-panel')]"
                        + "//li[@role='option' and normalize-space()=" + xpathLiteral(value) + "]")));
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, option);
    }

    /**
     * Literal-safe version of findNormalDropdown: section and field are wrapped
     * with xpathLiteral(), so names like BUYER'S CALL don't break the XPath.
     */
    private WebElement findLiteralNormalDropdown(String section, String field) {
        // Works for both empty and already-filled dropdowns (label does not change)
        By aboveField = By.xpath("//p-card[.//span[contains(text()," + xpathLiteral(section) + ")]]"
                + "//label[.//span[normalize-space()=" + xpathLiteral(field) + "]]/..//div[@aria-label='dropdown trigger']");
        // Fallback for fields where the trigger is inside the field (only matches when empty)
        By insideField = By.xpath("//p-card[@class='p-element'][.//span[contains(text()," + xpathLiteral(section) + ")]]"
                + "//span[contains(@aria-label," + xpathLiteral(field) + ")]");

        Duration originalImplicit = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> {
                        for (By by : new By[]{aboveField, insideField}) {
                            for (WebElement e : d.findElements(by)) {
                                if (e.isDisplayed() && e.isEnabled()) {
                                    return e;
                                }
                            }
                        }
                        return null;   // null makes the wait keep polling
                    });
        } catch (TimeoutException e) {
            throw new NoSuchElementException("Unable to locate a visible dropdown. Section: ["
                    + section + "] Field: [" + field + "]");
        } finally {
            driver.manage().timeouts().implicitlyWait(originalImplicit);
        }
    }

    /**
     * Safely wraps a raw string as an XPath string literal, handling values
     * that contain single quotes, double quotes, or both, via concat()
     * when necessary. Prevents malformed XPath like:
     *   @aria-label='BUYER'S CALL'   (breaks on the embedded apostrophe)
     */
    String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        // Contains BOTH quote types: split on ' and rebuild with concat()
        String[] parts = value.split("'", -1);
        StringBuilder sb = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            sb.append("'").append(parts[i]).append("'");
            if (i < parts.length - 1) {
                sb.append(", \"'\", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @When("user enter {string} in the {string} number field in the {string} table")
    public void userEnterInTheNumberFieldInTheTable(String value, String field, String section) {
        WebElement numberField = driver.findElement(By.xpath("//h3[normalize-space()='"+section+"']/following::table[1]//tbody/tr[1]/td[count(//h3[normalize-space()='"+section+"']/following::table[1]//thead/tr/th[.//span[normalize-space()='"+field+"']]/preceding-sibling::th)+1]//.//input"));
        webDriverCommonLibrary.explicitWait(driver,numberField);
        webDriverCommonLibrary.scrollIntoTheView(driver,numberField);
        numberField.sendKeys(value);
    }

    public void selectTablePicklistType2DropdownValue(String section, String field, String value) {

        // Step 1: resolve the table ONCE, reuse the same reference for both header and body
        WebElement table = driver.findElement(By.xpath(
                "//h3[normalize-space()='" + section + "']/following::table[1]"
        ));

        // Step 2: find the header <th> for this field WITHIN that exact table
        WebElement headerCell = table.findElement(By.xpath(
                ".//thead/tr/th[.//span[normalize-space()='" + field + "']]"
        ));

        // Step 3: compute its column index via JS — reliable even with colspan/frozen quirks,
        // since it reads the browser's own cellIndex rather than recomputing via preceding-sibling count
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long columnIndex = (Long) js.executeScript("return arguments[0].cellIndex;", headerCell);

        // Step 4: find the matching <td> in row 1 of the SAME table, using that index (0-based + 1 for XPath)
        WebElement dropdown = table.findElement(By.xpath(
                ".//tbody/tr[1]/td[" + (columnIndex + 1) + "]//div[contains(@class,'p-dropdown-trigger')]"));



        webDriverCommonLibrary.scrollIntoTheViewNearest(driver, dropdown);
        webDriverCommonLibrary.waitForSetTime(300);
        webDriverCommonLibrary.explicitWait(driver, dropdown);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, dropdown);

        WebElement selectSearchOption = driver.findElement(By.xpath("//input[@role='searchbox' and @type='text']"));
        webDriverCommonLibrary.scrollIntoTheView(driver, selectSearchOption);
        webDriverCommonLibrary.explicitWaitVisibility(driver, selectSearchOption);
        selectSearchOption.sendKeys(value);

        WebElement selectOption = driver.findElement(By.xpath("//ul[contains(@role,'listbox')]//li[@role='option' and normalize-space(@aria-label)='" + value + "']"));
        webDriverCommonLibrary.explicitWait(driver, selectOption);
        webDriverCommonLibrary.scrollIntoTheView(driver, selectOption);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, selectOption);
        webDriverCommonLibrary.explicitWaitInvisibility(driver, By.cssSelector(".p-overlay-mask, .p-dropdown-panel"));
    }

    public void userSelectsTargetDateRangesFromToForRowFieldInTheTable(String startDateStr, String endDateStr, int rowIndex, String field, String table) {
        LocalDate startDate = DateExpressionResolver.resolve(startDateStr);
        LocalDate endDate = DateExpressionResolver.resolve(endDateStr);
        PageObjectManager.getCreateCommodityContractPage().setTargetDateRange(rowIndex, startDate, endDate);
    }


    public WebElement getVisibleCellByHeaderText(
            WebDriver driver,
            String sectionName,
            String columnHeaderText,
            int rowIndex) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        Object result = js.executeScript(

                "function normalize(s) {                                   " +
                        "  return (s || '')                                        " +
                        "      .replace(/\\*/g,'')                                 " +
                        "      .replace(/\\s+/g,' ')                               " +
                        "      .trim();                                            " +
                        "}                                                        " +

                        "let sectionText = arguments[0];                          " +
                        "let columnHeaderText = normalize(arguments[1]);          " +
                        "let rowIndex = arguments[2];                             " +

                        "let containers =                                         " +
                        " [...document.querySelectorAll('fieldset,.p-panel,section,div')] " +
                        " .filter(el => {                                         " +
                        "      let heading = el.querySelector(                    " +
                        "          'legend,.p-panel-title,h2,h3,h4');             " +
                        "      return heading &&                                 " +
                        "             normalize(heading.textContent) ===          " +
                        "             normalize(sectionText);                     " +
                        " });                                                     " +

                        /*
                         * IMPORTANT FIX:
                         * Collect tables from ALL matching containers,
                         * not just containers[0].
                         */
                        "let tables = [];                                         " +

                        "if (containers.length > 0) {                             " +
                        "    containers.forEach(c => {                            " +
                        "        tables.push(...c.querySelectorAll('table'));      " +
                        "    });                                                  " +
                        "} else {                                                 " +
                        "    tables = [...document.querySelectorAll('table')];     " +
                        "}                                                        " +

                        /*
                         * Find all tables containing the requested column.
                         */
                        "let matchingTables = tables.filter(t =>                  " +
                        "    [...t.querySelectorAll('thead th')]                  " +
                        "      .some(th =>                                        " +
                        "          normalize(th.textContent) ===                 " +
                        "          columnHeaderText));                            " +

                        "if (matchingTables.length === 0) return null;            " +

                        /*
                         * Header lookup from first matching table.
                         */
                        "let targetTh = [...matchingTables[0]                     " +
                        "       .querySelectorAll('thead th')]                    " +
                        "       .find(th =>                                       " +
                        "          normalize(th.textContent) ===                 " +
                        "          columnHeaderText);                             " +

                        "if (!targetTh) return null;                              " +

                        "let thRect = targetTh.getBoundingClientRect();           " +
                        "let targetX = thRect.left + (thRect.width / 2);          " +

                        /*
                         * IMPORTANT FIX:
                         * Collect ALL rows across ALL matching tables.
                         */
                        "let allRows = [];                                        " +

                        "matchingTables.forEach(t => {                            " +
                        "    allRows.push(...t.querySelectorAll('tbody tr'));      " +
                        "});                                                      " +

                        "console.log('Total rows collected = ' + allRows.length);" +
                        "console.log('Requested rowIndex = ' + rowIndex);         " +

                        "let row = allRows[rowIndex];                             " +

                        "if (!row) return null;                                   " +

                        "let cells = [...row.querySelectorAll('td')]              " +
                        " .filter(td => {                                         " +
                        "     let style = td.getAttribute('style') || '';         " +
                        "     return !style.includes('display: none')             " +
                        "            && td.getBoundingClientRect().width > 0;     " +
                        " });                                                     " +

                        "let best = null;                                         " +
                        "let bestDist = Infinity;                                 " +

                        "cells.forEach(td => {                                    " +
                        "     let rect = td.getBoundingClientRect();              " +
                        "     let center = rect.left + rect.width / 2;            " +
                        "     let dist = Math.abs(center - targetX);              " +

                        "     if (dist < bestDist) {                              " +
                        "         bestDist = dist;                                " +
                        "         best = td;                                      " +
                        "     }                                                   " +
                        "});                                                      " +

                        "return best;",

                sectionName,
                columnHeaderText,
                rowIndex
        );

        if (result == null) {

            String diagnostic =
                    runDiagnostics(
                            driver,
                            sectionName,
                            columnHeaderText,
                            rowIndex);

            throw new NoSuchElementException(
                    "Could not resolve grid cell for section '"
                            + sectionName
                            + "', column '"
                            + columnHeaderText
                            + "', row "
                            + rowIndex
                            + ".\n"
                            + diagnostic
            );
        }

        return (WebElement) result;
    }

    // ------------------------------------------------------------------
    // Diagnostic dump used ONLY when the primary lookup fails, so the
    // exception tells you exactly which assumption broke instead of a
    // bare "not found". Run this once, read the output, then we know
    // whether it's a row-count problem, a header-text mismatch, or a
    // section-scoping problem — no more guessing.
    // ------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private String runDiagnostics(
            WebDriver driver,
            String sectionName,
            String columnHeaderText,
            int rowIndex) {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        Map<String, Object> diag =
                (Map<String, Object>) js.executeScript(

                        "function normalize(s) {                                " +
                                "  return (s || '')                                     " +
                                "        .replace(/\\*/g,'')                            " +
                                "        .replace(/\\s+/g,' ')                          " +
                                "        .trim();                                       " +
                                "}                                                      " +

                                "let sectionText = arguments[0];                        " +
                                "let columnHeaderText = normalize(arguments[1]);        " +

                                "let containers =                                       " +
                                " [...document.querySelectorAll('fieldset,.p-panel,section,div')]" +
                                " .filter(el => {                                       " +
                                "      let heading = el.querySelector(                  " +
                                "          'legend,.p-panel-title,h2,h3,h4');           " +
                                "      return heading &&                                " +
                                "          normalize(heading.textContent) ===           " +
                                "          normalize(sectionText);                      " +
                                " });                                                   " +

                                "let allHeadings =                                      " +
                                " [...document.querySelectorAll(                        " +
                                "   'legend,.p-panel-title,h2,h3,h4')]                 " +
                                " .map(h => normalize(h.textContent))                   " +
                                " .filter(t => t.length > 0);                           " +

                                /*
                                 * FIX:
                                 * Collect tables from ALL matching containers.
                                 */
                                "let tables = [];                                       " +

                                "if (containers.length > 0) {                           " +
                                "   containers.forEach(c => {                           " +
                                "      tables.push(...c.querySelectorAll('table'));      " +
                                "   });                                                 " +
                                "} else {                                               " +
                                "   tables = [...document.querySelectorAll('table')];    " +
                                "}                                                      " +

                                "let tableHeaderSets = tables.map(t =>                  " +
                                "   [...t.querySelectorAll('thead th')]                " +
                                "      .map(th => normalize(th.textContent)));          " +

                                /*
                                 * FIX:
                                 * Find ALL matching tables.
                                 */
                                "let matchingTables = tables.filter(t =>                " +
                                "   [...t.querySelectorAll('thead th')]                " +
                                "      .some(th =>                                      " +
                                "          normalize(th.textContent) ===               " +
                                "          columnHeaderText));                          " +

                                /*
                                 * FIX:
                                 * Total rows across all matching tables.
                                 */
                                "let totalRows = 0;                                     " +

                                "matchingTables.forEach(t => {                          " +
                                "   totalRows += t.querySelectorAll('tbody tr').length; " +
                                "});                                                    " +

                                "return {                                               " +
                                "   sectionContainersFound: containers.length,          " +
                                "   allHeadingsOnPage: allHeadings,                    " +
                                "   scopedToDocument: containers.length === 0,         " +
                                "   tablesInScope: tables.length,                      " +
                                "   headersPerTable: tableHeaderSets,                  " +
                                "   matchingTableCount: matchingTables.length,         " +
                                "   totalRowsAcrossMatchingTables: totalRows           " +
                                "};",

                        sectionName,
                        columnHeaderText
                );

        StringBuilder sb = new StringBuilder();

        sb.append("Diagnostic:\n");

        sb.append(
                "  Section containers matching '"
                        + sectionName
                        + "': "
                        + diag.get("sectionContainersFound")
                        + "\n"
        );

        if (Boolean.TRUE.equals(diag.get("scopedToDocument"))) {

            sb.append(
                    "  -> No section container matched by heading text; " +
                            "fell back to whole-document search.\n"
            );

            sb.append(
                    "     All heading texts found on page: "
                            + diag.get("allHeadingsOnPage")
                            + "\n"
            );
        }

        sb.append(
                "  Tables in scope: "
                        + diag.get("tablesInScope")
                        + "\n"
        );

        sb.append(
                "  Header rows per table found: "
                        + diag.get("headersPerTable")
                        + "\n"
        );

        sb.append(
                "  Matching tables found: "
                        + diag.get("matchingTableCount")
                        + "\n"
        );

        sb.append(
                "  Total rows across matching tables: "
                        + diag.get("totalRowsAcrossMatchingTables")
                        + " (requested rowIndex="
                        + rowIndex
                        + ")\n"
        );

        int totalRows =
                ((Number) diag.get("totalRowsAcrossMatchingTables"))
                        .intValue();

        if (totalRows == 0) {

            sb.append(
                    "  -> LIKELY ROOT CAUSE: No rows exist in the matching grid.\n"
            );

        } else if (rowIndex >= totalRows) {

            sb.append(
                    "  -> LIKELY ROOT CAUSE: Requested rowIndex "
                            + rowIndex
                            + " exceeds available rows "
                            + totalRows
                            + ".\n"
            );
        }

        return sb.toString();
    }

    public void clickGridColumnDropdown(WebDriver driver, String sectionName,String columnHeaderText, int rowIndex) {
        WebElement cell = getVisibleCellByHeaderText(driver, sectionName, columnHeaderText, rowIndex);

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);
        WebElement trigger = cell.findElement(By.cssSelector("div.p-dropdown-trigger"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(trigger));
        trigger.click();
    }

    public void selectProductDetailValue(String sectionName, String columnName, int rowIndex, String value) {
        clickGridColumnDropdown(driver, sectionName, columnName, rowIndex);

        WebElement option = driver.findElement(By.xpath("//ul[contains(@class,'p-dropdown-items')]//li[@aria-label='"+value+"']"));
        try{
            webDriverCommonLibrary.clickUsingActionClass(driver,option);
        }catch (Exception e){
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,option);
        }
    }

    // Generic dropdown selector (Product, Trade Month, UOM, etc.)
    public void selectDropdownValue(String sectionName, String columnName, int rowIndex, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        selectProductDetailValue(sectionName, columnName, rowIndex, value);
    }

    public void selectMultipleDropdownValue(String sectionName, String columnName, int rowIndex, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        selectMultipleProductDetailValue(sectionName, columnName, rowIndex, value);
    }

    public void createCertificationPremium(String sectionName, String columnName, int rowIndex, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        selectCertificationAndPremium(sectionName, columnName, rowIndex, value);
    }

    public void selectMultipleCommissionDropdownValue(String sectionName, String columnName, int rowIndex, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        selectMultipleCommissionDetailValue(sectionName, columnName, rowIndex, value);
    }

    public void selectMultipleCommissionDropdownValueValidation(String sectionName, String columnName, int rowIndex, String value) {
        if (value == null || value.trim().isEmpty())
            return;
        selectMultipleCommissionDetailValueValidation(sectionName, columnName, rowIndex, value);
    }

    // For PrimeNG multi-select / secondary dropdown style columns (e.g. Packing)
    public void selectTableType2DropdownValues(String sectionName, String columnName,
                                               int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;
        selectProductDetailValue(sectionName, columnName, rowIndex, value);
    }

    public void selectMultipleTableType2DropdownValues(String sectionName, String columnName,
                                                       int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;
        selectMultipleProductDetailValue(sectionName, columnName, rowIndex, value);
    }

    // Numeric input cells (Units, Qty., No Of Lots., Price)
    public void userEnterInTheNumberFieldInTheTable(String columnName, String sectionName,
                                                    int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getVisibleCellByHeaderText(driver, sectionName, columnName, rowIndex);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell
        );

        WebElement input = cell.findElement(By.cssSelector("input"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    // Plain text cells (Position Month, Quality) — kept separate from the
    // numeric helper above so callers are explicit about intent, but they no
    // longer both fire for the same column.


    public void enterFieldValue(String columnName, String sectionName,
                                int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getVisibleCellByHeaderText(driver, sectionName, columnName, rowIndex);
        WebElement input = cell.findElement(By.cssSelector("input"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
    }


    public void enterMultipleFieldValue(String columnName, String sectionName,
                                        int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getProductDetailCell(sectionName, columnName, rowIndex);
        WebElement input = cell.findElement(By.cssSelector("textarea"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
    }


    public void enterMultipleCommissionFieldValue(String columnName, String sectionName,
                                                  int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getCommissionDetailCell(sectionName, columnName, rowIndex);
        WebElement input = cell.findElement(By.cssSelector("input"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
    }

    public void addProductDetailRow(String sectionName) {

        int rowCountBefore = countProductDetailRows();
        System.out.println("========================================");
        System.out.println("ADD PRODUCT DETAIL ROW");
        System.out.println("Section        : " + sectionName);
        System.out.println("Rows before    : " + rowCountBefore);
        System.out.println("========================================");

        By addButtonLocator = By.xpath("//div[@formarrayname='item_details']" + "//button[" + ".//span[contains(@class,'pi-plus')]" + " or .//i[contains(@class,'pi-plus')]" + " or contains(@class,'pi-plus')" + "]");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {

            System.out.println("Searching Add (+) button...");

            WebElement addButton = wait.until(ExpectedConditions.presenceOfElementLocated(addButtonLocator));

            System.out.println("Add (+) button FOUND.");
            System.out.println("Button HTML:");
            System.out.println(addButton.getAttribute("outerHTML"));

            wait.until(ExpectedConditions.elementToBeClickable(addButtonLocator));

            WebElement freshButton = wait.until(ExpectedConditions.elementToBeClickable(addButtonLocator));

            System.out.println("Button displayed = " + freshButton.isDisplayed());
            System.out.println("Button enabled   = " + freshButton.isEnabled());

            System.out.println("Button class     = " +
                    freshButton.getAttribute("class"));

            System.out.println("Button HTML BEFORE CLICK:");
            System.out.println(freshButton.getAttribute("outerHTML"));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                    freshButton
            );

            webDriverCommonLibrary.waitForSetTime(300);
            System.out.println("Clicking Add (+) button using Selenium click...");
            freshButton.click();

//            closeAnyOpenOverlay(driver);
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> debugProductDetailRowCountBroad() > rowCountBefore);

            System.out.println("Selenium click completed.");

            webDriverCommonLibrary.waitForSetTime(100);
            System.out.println("========================================");
            System.out.println("AFTER ADD BUTTON CLICK");
            System.out.println("========================================");

            System.out.println(
                    "Existing row locator count = "
                            + countProductDetailRows()
            );

            debugProductDetailRowCountBroad();

            System.out.println("Rows before = " + rowCountBefore);

            System.out.println("STRICT COUNT = "
                    + countProductDetailRows());

            System.out.println("BROAD COUNT = "
                    + debugProductDetailRowCountBroad());

            System.out.println("getProductDetailRows() = "
                    + getProductDetailRows().size());

            boolean rowAdded = waitForRowCountToExceed(
                    rowCountBefore,
                    Duration.ofSeconds(8)
            );


            if (!rowAdded) {
                System.out.println("Row count did NOT increase.");
                debugRowValidity(rowCountBefore - 1);
                throw new IllegalStateException(
                        "Add (+) button was clicked, but Product Details row count " +
                                "did not increase. Previous row count = " +
                                rowCountBefore);
            }

            int rowCountAfter = countProductDetailRows();
            System.out.println("========================================");
            System.out.println("ROW ADDED SUCCESSFULLY");
            System.out.println("Rows before : " + rowCountBefore);
            System.out.println("Rows after  : " + rowCountAfter);
            System.out.println("========================================");

        } catch (TimeoutException e) {
            System.out.println("========================================");
            System.out.println("ADD BUTTON NOT FOUND");
            System.out.println("Section : " + sectionName);
            System.out.println("Rows   : " + rowCountBefore);
            System.out.println("========================================");
            debugProductDetailActionButtons(rowCountBefore);

            throw new IllegalStateException(
                    "Add button for section '" + sectionName + "' could not be found within the timeout. " + "The current locator may not match the actual Add button location.", e);
        }
    }
    private int countProductDetailRows() {
        return driver.findElements(productDetailRows).size();
    }
    private boolean waitForRowCountToExceed(
            int previousCount,
            Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(d -> {

                        int count = countProductDetailRows();

                        System.out.println(
                                "Current row count = " + count
                        );
                        return count > previousCount;
                    });

            return true;

        } catch (TimeoutException e) {

            System.out.println(
                    "Timed out waiting for row count > "
                            + previousCount
            );
            return false;
        }
    }

    public void closeAnyOpenOverlay(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("document.body.click();");
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(d ->
                    (Boolean) ((JavascriptExecutor) d).executeScript(
                            "return document.querySelectorAll('.p-overlay-panel, .p-dropdown-panel, .p-datepicker, .cdk-overlay-container .cdk-overlay-pane').length === 0;"));
        } catch (TimeoutException ignored) {}

        // NEW: wait for any blur-triggered Angular change detection / re-render to finish
        ((JavascriptExecutor) driver).executeScript(
                "return new Promise(resolve => setTimeout(() => requestAnimationFrame(() => requestAnimationFrame(resolve)), 150));");
    }
    private final By productDetailRows = By.xpath( "//div[@formarrayname='item_details']//tbody/tr");

    public List<WebElement> getProductDetailRows() {
        return driver.findElements(productDetailRows);
    }

    public WebElement getProductDetailRow(int rowNumber) {

        List<WebElement> rows = getProductDetailRows();

        if (rows.isEmpty()) {
            throw new NoSuchElementException("No Product Detail rows found.");}

        if (rowNumber < 1 || rowNumber > rows.size()) {
            throw new NoSuchElementException("Requested row " + rowNumber + " " + "but only " + rows.size() + " rows exist.");
        }
        return rows.get(rowNumber - 1);
    }
    // Temporary button
    public void printProductDetailRowCount() {
        List<WebElement> rows = getProductDetailRows();
        System.out.println("================================");
        System.out.println("PRODUCT DETAIL ROW COUNT = " + rows.size());
        System.out.println("================================");
    }
    // Temporary button
    public void debugProductDetailActionButtons(int rowNumber) {
        WebElement row = getProductDetailRow(rowNumber);
        List<WebElement> buttons = row.findElements(By.xpath(".//button"));
        System.out.println("========================================");
        System.out.println("ROW NUMBER       = " + rowNumber);
        System.out.println("BUTTON COUNT     = " + buttons.size());
        System.out.println("========================================");
        for (int i = 0; i < buttons.size(); i++) {
            WebElement button = buttons.get(i);
            System.out.println("----------------------------------------");
            System.out.println("BUTTON " + (i + 1));
            System.out.println("text      = [" + button.getText() + "]");
            System.out.println("aria-label= [" + button.getAttribute("aria-label") + "]");
            System.out.println("title     = [" + button.getAttribute("title") + "]");
            System.out.println("class     = [" + button.getAttribute("class") + "]");
            System.out.println("outerHTML = ");
            System.out.println(button.getAttribute("outerHTML"));
            System.out.println("----------------------------------------");
        }
    }

    public void debugProductDetailAddButtons(String sectionName) {
        System.out.println("========================================");
        System.out.println("PRODUCT DETAILS ADD BUTTON DEBUG");
        System.out.println("Section = " + sectionName);
        System.out.println("========================================");
        By sectionLocator = By.xpath("//div[@formarrayname='item_details']" + "//xbs-dcloud-caption[.//h3[normalize-space()='" + sectionName + "']][1]" + "/following-sibling::div[1]");
        List<WebElement> sections = driver.findElements(sectionLocator);
        System.out.println("Section containers found = " + sections.size());
        if (sections.isEmpty()) {
            System.out.println(
                    "ERROR: Product Details section container was NOT found."
            );return;
        }
        WebElement section = sections.get(0);
        List<WebElement> buttons = section.findElements(
                By.xpath(".//button | .//*[@role='button']"));

        System.out.println("Buttons / role=button found = " + buttons.size());
        for (int i = 0; i < buttons.size(); i++) {
            WebElement button = buttons.get(i);
            System.out.println("----------------------------------------");
            System.out.println("BUTTON " + (i + 1));
            System.out.println("Text       = [" + button.getText() + "]");
            System.out.println("aria-label = [" + button.getAttribute("aria-label") + "]");
            System.out.println("title      = [" + button.getAttribute("title") + "]");
            System.out.println("class      = [" + button.getAttribute("class") + "]");
            System.out.println("displayed  = " + button.isDisplayed());
            System.out.println("enabled    = " + button.isEnabled());
            System.out.println("OUTER HTML:");
            System.out.println(button.getAttribute("outerHTML"));
            System.out.println("----------------------------------------");
        }
        System.out.println("END PRODUCT DETAILS ADD BUTTON DEBUG");
        System.out.println("========================================");
    }

    public void debugLastProductDetailRow() {

        List<WebElement> rows = getProductDetailRows();

        System.out.println("========================================");
        System.out.println("TOTAL ROWS = " + rows.size());
        System.out.println("========================================");

        WebElement lastRow = rows.get(rows.size() - 1);

        System.out.println("LAST ROW HTML:");
        System.out.println(lastRow.getAttribute("outerHTML"));
    }

    /**
     * Confirms a numeric table field's value actually committed to Angular's
     * reactive form model, by re-reading it back from the DOM, before we
     * trust the row is in a stable state.
     */
    public boolean verifyTableNumberFieldCommitted(String fieldLabel, String section, int rowIndex, String expectedValue, Duration timeout) {
        int xpathRow = rowIndex + 1; // convert 0-based loop index to 1-based XPath predicate

        By fieldLocator = By.xpath(
                "(//div[@formarrayname='item_details']" +
                        "//xbs-dcloud-caption[.//h3[normalize-space()='" + section + "']][1]" +
                        "/following-sibling::div[1]//table//tbody/tr)[" + xpathRow + "]" +
                        "//xbs-dcloud-input-number[.//label[normalize-space()='" + fieldLabel + "']" +
                        " or ancestor::td[.//label[normalize-space()='" + fieldLabel + "']]]" +
                        "//input");

        try {
            new WebDriverWait(driver, timeout).until(d -> {
                List<WebElement> els = d.findElements(fieldLocator);
                if (els.isEmpty()) return false;
                String actual = els.get(0).getAttribute("value");
                return actual != null && actual.trim().equals(expectedValue.trim());
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void debugRowValidity(int rowIndex) {
        int xpathRow = rowIndex + 1;
        List<WebElement> controls = driver.findElements(By.xpath(
                "(//div[@formarrayname='item_details']" +
                        "//xbs-dcloud-caption[.//h3[normalize-space()='Product Details']][1]" +
                        "/following-sibling::div[1]//table//tbody/tr)[" + xpathRow + "]" +
                        "//input | (//div[@formarrayname='item_details']" +
                        "//xbs-dcloud-caption[.//h3[normalize-space()='Product Details']][1]" +
                        "/following-sibling::div[1]//table//tbody/tr)[" + xpathRow + "]//textarea"));
        System.out.println("=== ROW " + rowIndex + " FIELD VALIDITY ===");
        for (WebElement c : controls) {
            String cls = c.getAttribute("class");
            System.out.println(
                    "id=" + c.getAttribute("id") +
                            " value=[" + c.getAttribute("value") + "]" +
                            " invalid=" + (cls != null && cls.contains("ng-invalid")) +
                            " untouched=" + (cls != null && cls.contains("ng-untouched")));
        }
    }

    public String getUomFieldValue(String section, int rowIndex) {
        int xpathRow = rowIndex + 1;
        List<WebElement> els = driver.findElements(By.xpath(
                "(//div[@formarrayname='item_details']" +
                        "//xbs-dcloud-caption[.//h3[normalize-space()='" + section + "']][1]" +
                        "/following-sibling::div[1]//table//tbody/tr)[" + xpathRow + "]" +
                        "//xbs-dcloud-input-text[.//label[normalize-space()='UOM']]//input"
        ));
        return els.isEmpty() ? "" : els.get(0).getAttribute("value");
    }

    public void debugAllProductDetailTables() {

        System.out.println("========================================");
        System.out.println("PRODUCT DETAIL TABLE DOM DEBUG");
        System.out.println("========================================");

        List<WebElement> tables = driver.findElements(
                By.xpath(
                        "//div[@formarrayname='item_details']" +
                                "//table"
                )
        );

        System.out.println("Product Details tables found = " + tables.size());

        for (int i = 0; i < tables.size(); i++) {

            WebElement table = tables.get(i);

            List<WebElement> rows = table.findElements(
                    By.xpath(".//tbody/tr")
            );

            System.out.println("----------------------------------------");
            System.out.println("TABLE " + (i + 1));
            System.out.println("Rows = " + rows.size());

            for (int r = 0; r < rows.size(); r++) {

                System.out.println("ROW " + (r + 1) + " displayed=" + rows.get(r).isDisplayed());
            }
            System.out.println("----------------------------------------");
        }
        System.out.println("========================================");
    }

    public int debugProductDetailRowCountBroad() {

        By locator = By.xpath("//div[@formarrayname='item_details']" + "//h3[normalize-space()='Product Details']" + "/ancestor::*[self::div or self::section][1]" + "//table//tbody/tr");

        int count= driver.findElements(locator).size();
        System.out.println("BROAD PRODUCT DETAIL ROW COUNT = " + count);
        return count;
    }

    public WebElement getProductDetailCell(String sectionName, String columnName, int rowIndex) {
        System.out.println("========================================");
        System.out.println("RESOLVING PRODUCT DETAIL CELL");
        System.out.println("Section   = " + sectionName);
        System.out.println("Column    = " + columnName);
        System.out.println("Row Index = " + rowIndex);
        System.out.println("UI Row    = " + (rowIndex + 1));
        System.out.println("========================================");
        // ---------------------------------------------------------
        // 1. Locate Product Details container
        // ---------------------------------------------------------
        WebElement productDetailsContainer = driver. findElement(
//                By.xpath("//div[@formarrayname='item_details']" + "[.//xbs-dcloud-caption//h3[normalize-space()='" +sectionName + "']]"));
                By.xpath("//div[@formarrayname='item_details'][preceding::span[contains(@class,'form-header')][1][normalize-space()='"+sectionName+"']]"));
        // ---------------------------------------------------------
        // 2. Find ALL headers
        // ---------------------------------------------------------
        List<WebElement> allHeaders = productDetailsContainer.findElements(By.xpath(".//thead/tr/th"));
        List<WebElement> visibleHeaders = allHeaders.stream().filter(WebElement::isDisplayed).toList();

        int visibleColumnIndex = -1;
        for (int i = 0; i < visibleHeaders.size(); i++) {
            String headerText = visibleHeaders.get(i)
                    .getText()
                    .replace("*", "")
                    .replace("\u00A0", " ")
                    .trim();
            System.out.println("Visible Header [" + i + "] = [" + headerText + "]");

            if (headerText.equalsIgnoreCase(columnName.trim())) {
                visibleColumnIndex = i;
                System.out.println("Resolved visible column index = " + visibleColumnIndex);
                break;
            }
        }

        if (visibleColumnIndex == -1) {
            throw new IllegalArgumentException("Column '" + columnName + "' was not found among visible headers.");
        }
        // ---------------------------------------------------------
        // 3. IMPORTANT:
        //    Find rows from Product Details container itself
        // ---------------------------------------------------------

        List<WebElement> allRows = productDetailsContainer.findElements(
                By.xpath(".//tbody/tr")
        );

        System.out.println("Total rows found in Product Details container = " + allRows.size());

        // ---------------------------------------------------------
        // 4. Validate requested row
        // ---------------------------------------------------------

        if (rowIndex < 0 || rowIndex >= allRows.size()) {

            throw new IllegalArgumentException("Invalid rowIndex = " + rowIndex +
                            ". Available rows = " + allRows.size()
            );
        }

        WebElement requestedRow = allRows.get(rowIndex);

        // ---------------------------------------------------------
        // 5. Get ALL cells from requested row
        // ---------------------------------------------------------

        List<WebElement> allCells = requestedRow.findElements(
                By.xpath("./td")
        );

        // ---------------------------------------------------------
        // 6. Keep ONLY visible cells
        // ---------------------------------------------------------

        List<WebElement> visibleCells = allCells.stream().filter(WebElement::isDisplayed).toList();

        System.out.println("Total cells in row = " + allCells.size());
        System.out.println("Visible cells in row = " + visibleCells.size());

        // ---------------------------------------------------------
        // 7. Validate visible column index
        // ---------------------------------------------------------

        if (visibleColumnIndex >= visibleCells.size()) {
            throw new IllegalStateException(
                    "Column '" + columnName +
                            "' resolved to visible index " +
                            visibleColumnIndex +
                            ", but row " + (rowIndex + 1) +
                            " has only " +
                            visibleCells.size() +
                            " visible cells.");
        }

        // ---------------------------------------------------------
        // 8. Resolve final cell
        // ---------------------------------------------------------

        WebElement cell = visibleCells.get(visibleColumnIndex);
        System.out.println("SUCCESS: Cell resolved | Row = " + (rowIndex + 1) + " | Column = " + columnName + " | Visible Column Index = " + visibleColumnIndex);
        return cell;
    }

    public WebElement getCommissionDetailCell(String sectionName, String columnName, int rowIndex) {
        System.out.println("========================================");
        System.out.println("RESOLVING COMMISSION DETAIL CELL");
        System.out.println("Section   = " + sectionName);
        System.out.println("Column    = " + columnName);
        System.out.println("Row Index = " + rowIndex);
        System.out.println("UI Row    = " + (rowIndex + 1));
        System.out.println("========================================");

        // 1. Locate Commission Details container
        WebElement commissionDetailsContainer = driver.findElement(
                By.xpath("//div[@formarrayname='commision_details']"
                        + "[preceding::span[contains(@class,'form-header')][1]"
                        + "[normalize-space()='" + sectionName + "']]"));
        // 2. Find ALL headers
        // 2. Find ALL headers       // 2. Find ALL headers
        // 2. Find ALL headers
        // 2. Find ALL headers       // 2. Find ALL headerss

        List<WebElement> allHeaders = commissionDetailsContainer.findElements(By.xpath(".//thead/tr/th"));
        List<WebElement> visibleHeaders = allHeaders.stream().filter(WebElement::isDisplayed).toList();

        int visibleColumnIndex = -1;

        for (int i = 0; i < visibleHeaders.size(); i++) {

            String headerText = visibleHeaders.get(i)
                    .getText()
                    .replace("*", "")
                    .replace("\u00A0", " ")
                    .trim();

            System.out.println("Visible Header [" + i + "] = [" + headerText + "]");

            if (headerText.equalsIgnoreCase(columnName.trim())) {
                visibleColumnIndex = i;
                System.out.println("Resolved visible column index = " + visibleColumnIndex);
                break;
            }
        }

        if (visibleColumnIndex == -1) {
            throw new IllegalArgumentException("Column '" + columnName + "' was not found among visible headers.");
        }

        //    Find rows from Product Details container itself
        List<WebElement> allRows = commissionDetailsContainer.findElements(
                By.xpath(".//tbody/tr")
        );

        System.out.println("Total rows found in Product Details container = " + allRows.size());


        // 4. Validate requested row

        if (rowIndex < 0 || rowIndex >= allRows.size()) {

            throw new IllegalArgumentException(
                    "Invalid rowIndex = " + rowIndex +
                            ". Available rows = " + allRows.size()
            );
        }

        WebElement requestedRow = allRows.get(rowIndex);

        // 5. Get ALL cells from requested row

        List<WebElement> allCells = requestedRow.findElements(
                By.xpath("./td")
        );

        // 6. Keep ONLY visible cells

        List<WebElement> visibleCells = allCells.stream().filter(WebElement::isDisplayed).toList();

        System.out.println("Total cells in row = " + allCells.size());
        System.out.println("Visible cells in row = " + visibleCells.size());

        // 7. Validate visible column index

        if (visibleColumnIndex >= visibleCells.size()) {
            throw new IllegalStateException(
                    "Column '" + columnName +
                            "' resolved to visible index " +
                            visibleColumnIndex +
                            ", but row " + (rowIndex + 1) +
                            " has only " +
                            visibleCells.size() +
                            " visible cells.");
        }

        // 8. Resolve final cell
        WebElement cell = visibleCells.get(visibleColumnIndex);
        System.out.println("SUCCESS: Cell resolved | Row = " + (rowIndex + 1) + " | Column = " + columnName + " | Visible Column Index = " + visibleColumnIndex);
        return cell;
    }

    public WebElement getCommissionDetailCellValidation(
            String sectionName,
            String columnName,
            int rowIndex) {

        System.out.println("========================================");
        System.out.println("RESOLVING COMMISSION DETAIL CELL");
        System.out.println("Section   = " + sectionName);
        System.out.println("Column    = " + columnName);
        System.out.println("Row Index = " + rowIndex);
        System.out.println("UI Row    = " + (rowIndex + 1));
        System.out.println("========================================");

        WebElement commissionDetailsContainer = driver.findElement(
                By.xpath("//div[@formarrayname='commision_details']"
                        + "[preceding::span[contains(@class,'form-header')][1]"
                        + "[normalize-space()='" + sectionName + "']]"));

        List<WebElement> visibleHeaders =
                commissionDetailsContainer.findElements(
                                By.xpath(".//thead/tr/th"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .toList();

        String requestedHeader = normalizeCommissionHeader(columnName);
        int visibleColumnIndex = -1;

        for (int i = 0; i < visibleHeaders.size(); i++) {
            String headerText = normalizeCommissionHeader(
                    visibleHeaders.get(i).getText());

            System.out.println(
                    "Visible Header [" + i + "] = [" + headerText + "]");

            if (headerText.equalsIgnoreCase(requestedHeader)) {
                visibleColumnIndex = i;
                System.out.println(
                        "Resolved visible column index = " + visibleColumnIndex);
                break;
            }
        }

        if (visibleColumnIndex < 0) {
            throw new IllegalArgumentException(
                    "Column '" + columnName
                            + "' was not found among visible headers.");
        }

        List<WebElement> allRows =
                commissionDetailsContainer.findElements(
                        By.xpath(".//tbody/tr"));

        System.out.println("Total rows found = " + allRows.size());

        if (rowIndex < 0 || rowIndex >= allRows.size()) {
            throw new IllegalArgumentException(
                    "Invalid rowIndex = " + rowIndex
                            + ". Available rows = " + allRows.size());
        }

        WebElement requestedRow = allRows.get(rowIndex);

        // The Value header covers both the amount input and currency dropdown.
        // Return the cell containing the last dropdown in that row.
        if ("Value".equalsIgnoreCase(requestedHeader)) {
            List<WebElement> dropdowns =
                    requestedRow.findElements(By.cssSelector("p-dropdown"));

            if (dropdowns.isEmpty()) {
                throw new IllegalStateException(
                        "No dropdown found in Commission Details row "
                                + (rowIndex + 1));
            }

            WebElement unitDropdown = dropdowns.get(dropdowns.size() - 1);
            WebElement unitCell =
                    unitDropdown.findElement(By.xpath("./ancestor::td[1]"));

            System.out.println(
                    "SUCCESS: Value unit dropdown cell resolved | Row = "
                            + (rowIndex + 1));

            return unitCell;
        }

        List<WebElement> visibleCells =
                requestedRow.findElements(By.xpath("./td"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .toList();

        System.out.println("Visible cells in row = " + visibleCells.size());

        if (visibleColumnIndex >= visibleCells.size()) {
            throw new IllegalStateException(
                    "Column '" + columnName
                            + "' resolved to visible index " + visibleColumnIndex
                            + ", but row " + (rowIndex + 1)
                            + " has only " + visibleCells.size()
                            + " visible cells.");
        }

        WebElement cell = visibleCells.get(visibleColumnIndex);

        System.out.println(
                "SUCCESS: Cell resolved | Row = " + (rowIndex + 1)
                        + " | Column = " + columnName
                        + " | Visible Column Index = " + visibleColumnIndex);

        return cell;
    }
    private String normalizeCommissionHeader(String text) {
        return text
                .replace("*", "")
                .replace("\u00A0", " ")
                .trim();
    }
    // ---- Step 2: new helpers ----
    public WebElement getCommissionValueAmountField(String sectionName, int rowIndex) {
        WebElement valueCell = getCommissionDetailCell(sectionName, "Value", rowIndex);
        return valueCell.findElement(By.xpath(".//input[contains(@class,'p-inputnumber-input')]"));
    }

    public WebElement getCommissionValueUnitDropdownTrigger(String sectionName, int rowIndex) {
        WebElement valueCell = getCommissionDetailCell(sectionName, "Value", rowIndex);
        WebElement nextCell = valueCell.findElement(By.xpath("following-sibling::td[1]"));
        return nextCell.findElement(By.xpath(".//p-dropdown[contains(@class,'p-inputwrapper')]//div[contains(@class,'p-dropdown-trigger')]"));
    }

    // ---- Step 3: add HERE ----
    public void selectValueUnitOption(WebElement dropdownTrigger, String visibleText) {
        dropdownTrigger.click();

        // Locate the filter input inside the opened panel and type into it
        By filterInputLocator = By.xpath(
                "//div[contains(@class,'p-dropdown-panel')]//input[@type='text']"
        );
        WebElement filterInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(filterInputLocator));
        filterInput.sendKeys(visibleText);

        // Use contains() instead of exact text() match — safer against whitespace/casing differences
        By optionLocator = By.xpath("//div[contains(@class,'p-dropdown-panel')]" + "//li[contains(@class,'p-dropdown-item')]" + "[contains(normalize-space(.), '" + visibleText + "')]");
        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(optionLocator));
        option.click();
    }

    // ---- Step 4: add HERE ----
    public void enterCommissionValueAmount(WebElement amountField, String value) {
        amountField.click();
        amountField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        amountField.sendKeys(Keys.DELETE);
        amountField.sendKeys(value);
        amountField.sendKeys(Keys.TAB);
    }

    // ---- Step 5: rewritten method ----
    public void selectMultipleCommissionDetailValue(String sectionName, int rowIndex, String value, String valueUnit) {
        WebElement amountField = getCommissionValueAmountField(sectionName, rowIndex);
        enterCommissionValueAmount(amountField, value);

        WebElement unitDropdown = getCommissionValueUnitDropdownTrigger(sectionName, rowIndex);
        selectValueUnitOption(unitDropdown, valueUnit);
    }

    // Numeric input cells (Units, Qty., No Of Lots., Price)
    public void userEnterInTheMultipleNumberFieldInTheTable(String columnName, String sectionName,
                                                            int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getProductDetailCell(sectionName, columnName, rowIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);
        WebElement input = cell.findElement(By.cssSelector("input"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    public void userEnterInTheMultipleQualityTextFieldInTheTable(String columnName, String sectionName, int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getProductDetailCell(sectionName, columnName, rowIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);
        WebElement input = cell.findElement(By.cssSelector("textarea"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    public void userEnterInTheMultipleCommissionNumberFieldInTheTable(String columnName, String sectionName,
                                                                      int rowIndex, String value) {
        if (value == null || value.trim().isEmpty()) return;

        WebElement cell = getCommissionDetailCell(sectionName, columnName, rowIndex);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);

        WebElement input = cell.findElement(By.cssSelector("input"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(input));
        input.clear();
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    public void selectCommissionValueUnit(String sectionName, int rowIndex, String valueUnit) {
        WebElement unitDropdown = getCommissionValueUnitDropdownTrigger(sectionName, rowIndex);
        selectValueUnitOption(unitDropdown, valueUnit);
    }



    public void clickMultipleGridColumnDropdown(WebDriver driver, String sectionName,String columnHeaderText, int rowIndex) {
        WebElement cell = getProductDetailCell(sectionName, columnHeaderText, rowIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);
        WebElement trigger = cell.findElement(By.cssSelector("div.p-dropdown-trigger"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(trigger));
        System.out.println("OPEN DROPDOWN | ROW = " + rowIndex + " | FIELD = " + columnHeaderText);
        trigger.click();
    }

    public void clickMultipleCommissionGridColumnDropdown(WebDriver driver, String sectionName,String columnHeaderText, int rowIndex) {
        WebElement cell = getCommissionDetailCell(sectionName, columnHeaderText, rowIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", cell);
        WebElement trigger = cell.findElement(By.cssSelector("div.p-dropdown-trigger"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(trigger));
        System.out.println("OPEN DROPDOWN | ROW = " + rowIndex + " | FIELD = " + columnHeaderText);
        trigger.click();
    }

    public void clickMultipleCommissionGridColumnDropdownValidation(WebDriver driver, String sectionName, String columnHeaderText, int rowIndex) {

        WebElement cell = getCommissionDetailCellValidation(sectionName, columnHeaderText, rowIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cell);
        WebElement dropdown = cell.findElement(By.cssSelector("p-dropdown"));
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.elementToBeClickable(dropdown));
        dropdown.click();
    }



    public void selectMultipleProductDetailValue(String sectionName, String columnName, int rowIndex, String value) {
        clickMultipleGridColumnDropdown(driver, sectionName, columnName, rowIndex);

        WebElement option = driver.findElement(By.xpath("//ul[contains(@class,'p-dropdown-items')]//li[@aria-label='"+value+"']"));
        try{
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,option);
        }catch (Exception e){
            webDriverCommonLibrary.clickUsingActionClass(driver,option);
        }
    }


    public void selectCertificationAndPremium(String sectionName, String columnName, int rowIndex, String value) {
//        clickMultipleGridColumnDropdown(driver, sectionName, columnName, rowIndex);

        webDriverCommonLibrary.highLightTheElement(driver,driver.findElement(By.xpath("//span[normalize-space(.)='No Certificates']")));
        WebElement bipencil = driver.findElement(By.xpath("//span[normalize-space()='No Certificates']/following::i[contains(@class,'bi-pencil')]"));
        bipencil.click();

        webDriverCommonLibrary.waitForSetTime(200);
        webDriverCommonLibrary.highLightTheElement(driver,driver.findElement(By.xpath("//span[contains(text(),'Certification Details')]")));

        String xpath = "//th[.//span[normalize-space()='Certification']]/ancestor::table//tbody/tr[1]//td[count(//th[.//span[normalize-space()='Certification']]/preceding-sibling::th) + 1]//*[contains(@class,'dropdown') or contains(@class,'chevron')]";
        WebElement certificateDropdown = driver.findElement(By.xpath(xpath));
        webDriverCommonLibrary.explicitWait(driver,certificateDropdown);
        webDriverCommonLibrary.scrollIntoTheView(driver,certificateDropdown);
        certificateDropdown.click();

        WebElement option = driver.findElement(By.xpath("//ul[contains(@class,'p-dropdown-items')]//li[@aria-label='"+value+"']"));
        try{
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,option);
        }catch (Exception e){
            webDriverCommonLibrary.clickUsingActionClass(driver,option);
        }


        String premium = "//th[.//span[normalize-space()='Premium USC/LB']]/ancestor::table//tbody/tr[1]//td[count(//th[.//span[normalize-space()='Premium USC/LB']]/preceding-sibling::th)+1]//*[contains(@class,'p-inputtext')]";
        WebElement premiumNumberField = driver.findElement(By.xpath(premium));
        webDriverCommonLibrary.explicitWait(driver,premiumNumberField);
        webDriverCommonLibrary.scrollIntoTheView(driver,premiumNumberField);
        premiumNumberField.click();
        premiumNumberField.sendKeys("21");

        WebElement saveButton = driver.findElement(with(By.tagName("button")).toLeftOf(By.xpath("//span[normalize-space()='Cancel']")));
        webDriverCommonLibrary.explicitWait(driver,saveButton);
        saveButton.click();
    }

    public void selectPositionMonthValue(String sectionName, String columnName, int rowIndex, String value) {
        clickMultipleGridColumnDropdown(driver, sectionName, columnName, rowIndex);

        WebElement option = driver.findElement(By.xpath("//p-calendar[@view='month' and @dateformat ='M-yy']//span[contains(@class,'p-calendar')]"));
        try{
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,option);
        }catch (Exception e){
            webDriverCommonLibrary.clickUsingActionClass(driver,option);
        }
    }

    public void selectMultipleCommissionDetailValue(String sectionName, String columnName, int rowIndex, String value) {
        clickMultipleCommissionGridColumnDropdown(driver, sectionName, columnName, rowIndex);
        WebElement option = driver.findElement(By.xpath("//ul[contains(@class,'p-dropdown-items')]//li[@aria-label='"+value+"']"));
        try{
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,option);
        }catch (Exception e){
            webDriverCommonLibrary.clickUsingActionClass(driver,option);
        }
    }

    public void selectMultipleCommissionDetailValueValidation(String sectionName, String columnName, int rowIndex, String value) {
        clickMultipleCommissionGridColumnDropdownValidation(driver, sectionName, columnName, rowIndex);
        WebElement option = driver.findElement(By.xpath("//ul[contains(@class,'p-dropdown-items')]//li[@aria-label='"+value+"']"));
        try{
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver,option);
        }catch (Exception e){
            webDriverCommonLibrary.clickUsingActionClass(driver,option);
        }
    }

    public static String getCreatedContractNumber() {

        String contractNumberLocator =
                "//tbody[contains(@class,'p-datatable-tbody')]" +
                        "/tr/td[count(" +
                        "//th[contains(normalize-space(.),'Contract No.')]" +
                        "/preceding-sibling::th)+1]";

        WebElement element = webDriverCommonLibrary.explicitWaitVisibilityByLocator(driver, By.xpath(contractNumberLocator));
        String contractNumber = element.getText().trim();
        if (contractNumber.isEmpty()) {
            throw new IllegalStateException("Created contract number is empty.");
        }
        webDriverCommonLibrary.highLightTheElement(driver, element);
        System.out.println("CURRENT CREATED CONTRACT NUMBER = " + contractNumber);
        return contractNumber;
    }

    //***********************  Commission Details ********************//

    private final By commissionDetailRows = By.xpath("//div[@formarrayname='commision_details']//tbody/tr");

    public List<WebElement> getCommissionDetailRows() {
        return driver.findElements(commissionDetailRows);
    }

    private int countCommissionDetailRows() {
        return getCommissionDetailRows().size();
    }

    public WebElement getCommissionDetailRow(int rowNumber) {

        List<WebElement> rows = getCommissionDetailRows();

        if (rows.isEmpty()) {
            throw new NoSuchElementException("No Commission Detail rows found.");
        }

        if (rowNumber < 1 || rowNumber > rows.size()) {
            throw new NoSuchElementException("Requested row " + rowNumber + " but only " + rows.size() + " rows exist.");
        }
        return rows.get(rowNumber - 1);
    }

    /**
     * Clicks the "+" button that lives INSIDE the Action column of the
     * CURRENT LAST row.
     *
     * IMPORTANT ARCHITECTURAL DIFFERENCE vs. Product Details:
     * Product Details has one static "+" button OUTSIDE the table, so the
     * same locator is valid for every add. Commission Details (per the
     * screenshot) renders its "+" inside each row's own Action cell, so
     * the underlying WebElement is destroyed and re-created every time a
     * row is added. We MUST re-query the rows and grab the button off
     * whatever is currently the last row — never cache this locator.
     */
    public void addCommissionDetailRow(String sectionName) {

        int rowsBefore = countCommissionDetailRows();
        System.out.println("========================================");
        System.out.println("ADD COMMISSION DETAIL ROW");
        System.out.println("Section     : " + sectionName);
        System.out.println("Rows before : " + rowsBefore);
        System.out.println("========================================");

        List<WebElement> rows = getCommissionDetailRows();
        if (rows.isEmpty()) {
            throw new IllegalStateException("No Commission Detail rows found — cannot locate the '+' button.");
        }

        WebElement lastRow = rows.get(rows.size() - 1);

        // TODO: confirm the actual icon class from Step 1 (pi-plus assumed,

        By plusInRow = By.xpath(
                ".//button[.//span[contains(@class,'pi-plus')]"
                        + " or .//i[contains(@class,'pi-plus')]"
                        + " or contains(@class,'pi-plus')]");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement plusButton;
        try {
            plusButton = wait.until(ExpectedConditions.elementToBeClickable(lastRow.findElement(plusInRow)));
        } catch (NoSuchElementException | TimeoutException e) {
            throw new IllegalStateException("Could not find a clickable '+' button in the last Commission Details row. " + "Check the icon class / markup assumed in plusInRow.", e);
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", plusButton);

        webDriverCommonLibrary.waitForSetTime(300);

        System.out.println("Clicking Commission Details '+' button...");
        plusButton.click();

        boolean rowAdded = waitForCommissionRowCountToExceed(rowsBefore, Duration.ofSeconds(8));

        if (!rowAdded) {
            throw new IllegalStateException(
                    "'+' button was clicked, but Commission Details row count did not increase. "
                            + "Previous row count = " + rowsBefore);
        }

        int rowsAfter = countCommissionDetailRows();
        System.out.println("========================================");
        System.out.println("COMMISSION ROW ADDED SUCCESSFULLY");
        System.out.println("Rows before : " + rowsBefore);
        System.out.println("Rows after  : " + rowsAfter);
        System.out.println("========================================");
    }

    private boolean waitForCommissionRowCountToExceed(int previousCount, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(d -> {
                int count = countCommissionDetailRows();
                System.out.println("Current commission row count = " + count);
                return count > previousCount;
            });
            return true;
        } catch (TimeoutException e) {
            System.out.println("Timed out waiting for commission row count > " + previousCount);
            return false;
        }
    }

    /**
     * Clicks the "x" (remove) button for a specific 0-based row index.
     * Handy for negative tests or cleaning up a row you added by mistake.
     */
    public void removeCommissionDetailRow(int rowIndex) {

        List<WebElement> rows = getCommissionDetailRows();

        if (rowIndex < 0 || rowIndex >= rows.size()) {
            throw new NoSuchElementException(
                    "Requested row index " + rowIndex + " but only " + rows.size() + " rows exist.");
        }

        WebElement row = rows.get(rowIndex);

        // TODO: confirm the actual icon class from Step 1 (pi-times assumed).
        By xInRow = By.xpath(
                ".//button[.//span[contains(@class,'pi-times')]"
                        + " or .//i[contains(@class,'pi-times')]"
                        + " or contains(@class,'pi-times')]");

        WebElement xButton = row.findElement(xInRow);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", xButton);
        xButton.click();
    }

    // Temporary debug button — same pattern as printProductDetailRowCount()
    public void printCommissionDetailRowCount() {
        List<WebElement> rows = getCommissionDetailRows();
        System.out.println("================================");
        System.out.println("COMMISSION DETAIL ROW COUNT = " + rows.size());
        System.out.println("================================");
    }

    // =====================================================================
    // Phase 4b - actions moved here from StepsContainer so locators live only in the page object.
    // The bodies are the original step bodies (same locators, same waits, same order).
    // Assertions stay in the steps: these methods locate / act / highlight and return what the step checks.
    // =====================================================================

    private static By dashboardContentLocator(String text) {
        return By.xpath("//div[contains(@class,'content') and contains(@class,'sidenav-content')]//*[contains(text(),'" + text + "')]");
    }

    private static By sidebarMenuLocator(String id) {
        return By.xpath("//li[@routerlinkactive='active' and contains(@id,'" + id + "')]");
    }

    private static By primeIconButtonLocator(String icon) {
        return By.xpath("//p-button[contains(@icon,'pi pi-" + icon + "')]//button");
    }

    /** The button in the list-view row under the given column header (e.g. the Contract No. actions button). */
    private static By listRowActionButtonLocator(String columnName) {
        return By.xpath("//tbody[contains(@class,'p-datatable-tbody')]" + "/tr/td["
                + "count(//th[contains(normalize-space(),'" + columnName + "')]/preceding-sibling::th) + 1" + "]//button");
    }

    private static By listRowCellLocator(String columnName) {
        return By.xpath("//tbody[contains(@class,'p-datatable-tbody')]/tr/td[count(//th[contains(normalize-space(.),'"
                + columnName + "')]/preceding-sibling::th)+1]");
    }

    private static By menuItemLocator(String label) {
        return By.xpath(
                "//*[self::li or self::a or self::span or self::button]" +
                        "[normalize-space(.)='" + label + "' or @aria-label='" + label + "']");
    }
    // ---- dashboard / navigation ----

    public void clickSidebarMenu(String id) {
        driver.findElement(sidebarMenuLocator(id)).click();
    }

    public void clickDashboardSection(String text) {
        driver.findElement(dashboardContentLocator(text)).click();
        webDriverCommonLibrary.waitForSetTime(200);
    }

    public void clickPrimeIconButton(String icon) {
        WebElement element = driver.findElement(primeIconButtonLocator(icon));
        webDriverCommonLibrary.explicitWait(driver, element);
        webDriverCommonLibrary.scrollIntoTheView(driver, element);
        element.click();
    }

    public void highlightPrimeIconButton(String icon) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(primeIconButtonLocator(icon)));
    }

    public void highlightSpanContaining(String text) {
        WebElement element = driver.findElement(By.xpath("//span[contains(normalize-space(),'" + text + "')]"));
        webDriverCommonLibrary.highLightTheElement(driver, element);
    }

    public void highlightTableSection(String heading) {
        WebElement element = driver.findElement(By.xpath("//*[normalize-space()='" + heading + "']"));
        webDriverCommonLibrary.highLightTheElement(driver, element);
    }

    // ---- buttons ----

    /** Plain click on a button whose label is a span with exactly this text (Save, Yes ...). */
    public void clickSpanButton(String buttonText) {
        By button = By.xpath("//button[@type='button' and .//span[normalize-space()='"+buttonText+"']]");
        WebElement element = driver.findElement(button);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            webDriverCommonLibrary.waitForLoadingOverlay();
            element = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(button));
            element.click();
        }
    }

    /** JavaScript click on a button whose label is a span with this text (Approve ...). */
    public void clickSpanButtonWithJs(String label) {
        WebElement button = driver.findElement(By.xpath("//span[normalize-space()='" + label + "']"));
        webDriverCommonLibrary.explicitWait(driver, button);
        webDriverCommonLibrary.scrollIntoTheView(driver, button);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, button);
    }

    // ---- form fields ----

    public void enterTextInSectionField(String section, String field, String value) {
        WebElement textField = driver.findElement(By.xpath("//span[text()='" + section + "']/following::span[@class='p-float-label'][.//span[text()='" + field + "']]//input"));
        webDriverCommonLibrary.explicitWait(driver, textField);
        webDriverCommonLibrary.scrollIntoTheView(driver, textField);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, textField);
        textField.clear();
        textField.sendKeys(value);
    }

    public void enterTextInSectionTextArea(String section, String field, String value) {
        WebElement longTextArea = driver.findElement(By.xpath("//span[text()='" + section + "']/following::span[@class='p-float-label'][.//span[text()='" + field + "']]/textarea"));
        webDriverCommonLibrary.explicitWait(driver, longTextArea);
        webDriverCommonLibrary.scrollIntoTheView(driver, longTextArea);
        webDriverCommonLibrary.explicitWaitVisibility(driver, longTextArea);
        longTextArea.click();
        longTextArea.clear();
        longTextArea.sendKeys(value);
    }

    public void enterPopupTextTextArea(String label, String value) {
        By loc = By.xpath("//span[normalize-space()='" + label + "']/following::textarea");
        WebElement popupTextArea = webDriverCommonLibrary.waitForFirstVisible(loc, 15);
        webDriverCommonLibrary.scrollIntoTheView(driver, popupTextArea);
        popupTextArea.click();
        popupTextArea.clear();
        popupTextArea.sendKeys(value);
    }

    /** Ticks a checkbox whose label text is exactly this text. */
    public void selectCheckbox(String label) {
        WebElement checkbox = driver.findElement(By.xpath("//label[contains(@class,'doc-check')][normalize-space(span)='"+label+"']/input[@type='checkbox']"));
        webDriverCommonLibrary.explicitWait(driver, checkbox);
        webDriverCommonLibrary.scrollIntoTheView(driver, checkbox);
        webDriverCommonLibrary.highLightTheElement(driver, checkbox);
        checkbox.click();
    }

    /** Clicks a checkbox whose label text contains this text (kept as the original: contains, not exact). */
    public void deselectCheckbox(String label) {
        WebElement checkbox = driver.findElement(By.xpath("//label[contains(@class,'doc-check')][normalize-space(span)='"+label+"']/input[@type='checkbox']"));
        webDriverCommonLibrary.explicitWait(driver, checkbox);
        webDriverCommonLibrary.scrollIntoTheView(driver, checkbox);
        webDriverCommonLibrary.highLightTheElement(driver, checkbox);
        checkbox.click();
    }

    /** Trader-style field: returns the selected-value span inside the card of the given section. */
    public WebElement locateLoggedInUserField(String section, String user) {
        String dropdown = "//p-card[@class='p-element']" + "[.//span[contains(text(),'" + section + "')]]" + "//span[contains(@aria-label,'" + user + "')]";
        WebElement field = driver.findElement(By.xpath(dropdown));
        webDriverCommonLibrary.scrollIntoTheView(driver, field);
        webDriverCommonLibrary.explicitWait(driver, field);
        return field;
    }

    /** Read-only text input that sits under a span label (e.g. Contract No.). */
    public WebElement locateInputByLabel(String label) {
        return driver.findElement(By.xpath("//span[normalize-space()='" + label + "']/../../input[contains(@class,'p-inputtext')]"));
    }

    public WebElement locateContractDateField() {
        return driver.findElement(By.id("contract_date"));
    }

    /** Finds a selected-value span by aria-label, brings it into view and highlights it (e.g. Price Currency / UOM). */
    public void highlightSpanByAriaLabel(String ariaLabel) {
        WebElement element = driver.findElement(By.xpath("//span[@aria-label='" + ariaLabel + "']"));
        webDriverCommonLibrary.scrollIntoTheView(driver, element);
        webDriverCommonLibrary.explicitWait(driver, element);
        webDriverCommonLibrary.highLightTheElement(driver, element);
        webDriverCommonLibrary.waitForSetTime(2000);
    }

    public void highlightDisabledSpanByAriaLabel(String ariaLabel) {
        WebElement element = driver.findElement(By.xpath("//div[contains(@class,'p-disabled')]/..//span[@aria-label='"+ariaLabel+"']"));
        webDriverCommonLibrary.scrollIntoTheView(driver, element);
        webDriverCommonLibrary.explicitWait(driver, element);
        webDriverCommonLibrary.highLightTheElement(driver, element);
        webDriverCommonLibrary.waitForSetTime(2000);
    }

    /** Class field on the amendment screen: shows the chosen class value, enabled or disabled. */
    public void highlightClassField(boolean disabled, String classValue) {
        String stateCondition = disabled ? "@aria-disabled='true'" : "not(@aria-disabled='true')";
        WebElement classField = driver.findElement(By.xpath("//span[" + stateCondition + " and @aria-label='" + classValue + "']"));
        webDriverCommonLibrary.explicitWait(driver, classField);
        webDriverCommonLibrary.scrollIntoTheView(driver, classField);
        webDriverCommonLibrary.highLightTheElement(driver, classField);
    }

    // ---- messages and dialogs ----

    public void verifyMessageVisible(String text) {
        By messageLocator = By.xpath("//*[contains(@aria-label, '" + text + "')]");
        webDriverCommonLibrary.explicitWaitVisibilityByLocator(driver, messageLocator);
        WebElement message = driver.findElement(messageLocator);
        webDriverCommonLibrary.highLightTheElement(driver, message);
    }

    /** Finds a dialog span by its exact text, waits for it and highlights it. Optionally scrolls to it first. */
    public WebElement locateDialogText(String text, boolean scrollIntoView) {
        WebElement element = driver.findElement(By.xpath("//*[normalize-space(.)='" + text + "']"));
        webDriverCommonLibrary.explicitWait(driver, element);
        if (scrollIntoView) {
            webDriverCommonLibrary.scrollIntoTheView(driver, element);
        }
        webDriverCommonLibrary.highLightTheElement(driver, element);
        return element;
    }

    public WebElement locateConfirmDialogText(String text, boolean scrollIntoView) {
        WebElement element = driver.findElement(By.xpath("//span[normalize-space(.)='" + text + "']"));
        webDriverCommonLibrary.explicitWait(driver, element);
        if (scrollIntoView) {
            webDriverCommonLibrary.scrollIntoTheView(driver, element);
        }
        webDriverCommonLibrary.highLightTheElement(driver, element);
        return element;
    }

    // ---- list view (dashboard grid) ----

    /** Text of the first list row under the given column header (e.g. Contract No.). */
    public String getListColumnText(String columnHeader) {
        return driver.findElement(listRowCellLocator(columnHeader)).getText();
    }

    public void verifyRecordAndStatus(String columnHeader, String status) {
        By record = listRowCellLocator(columnHeader);
        By statusTag = By.xpath("//span[@class='p-tag-value' and text()='" + status + "']");

        WebElement recno = driver.findElement(record);
        WebElement statusElement = driver.findElement(statusTag);

        webDriverCommonLibrary.explicitWaitVisibilityByLocator(driver, record);
        webDriverCommonLibrary.explicitWaitVisibilityByLocator(driver, statusTag);

        webDriverCommonLibrary.highLightTheElement(driver, recno);
        webDriverCommonLibrary.highLightTheElement(driver, statusElement);
    }

    public void searchContractNo(String contractNumber) {
        WebElement searchbox = driver.findElement(By.xpath("//input[@placeholder='Search Contract No.']"));
        searchbox.click();
        webDriverCommonLibrary.waitForSetTime(200);
        searchbox.clear();
        searchbox.sendKeys(contractNumber);
        webDriverCommonLibrary.waitForSetTime(200);
    }

//    public void searchContractNo(String contractNumber) {
//        By searchLocator = By.xpath("//input[@placeholder='Search Contract No.']");
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//
//        webDriverCommonLibrary.waitForLoadingOverlay();
//
//        WebElement searchbox = wait.ignoring(StaleElementReferenceException.class)
//                .until(ExpectedConditions.elementToBeClickable(searchLocator));
//
//        // Set the full value in one go so only one search request fires
//        ((JavascriptExecutor) driver).executeScript(
//                "const el = arguments[0]; el.focus(); el.value = '';" +
//                        "el.dispatchEvent(new Event('input', {bubbles: true}));" +
//                        "el.value = arguments[1];" +
//                        "el.dispatchEvent(new Event('input', {bubbles: true}));" +
//                        "el.dispatchEvent(new Event('change', {bubbles: true}));",
//                searchbox, contractNumber);
//
//        // Wait for the filtered row instead of a fixed sleep
//        By resultRow = By.xpath("//tbody[contains(@class,'p-datatable-tbody')]"
//                + "/tr[td[contains(normalize-space(), '" + contractNumber + "')]]");
//        new WebDriverWait(driver, Duration.ofSeconds(20))
//                .ignoring(StaleElementReferenceException.class)
//                .until(ExpectedConditions.visibilityOfElementLocated(resultRow));
//
//        webDriverCommonLibrary.waitForLoadingOverlay();
//    }

    public void selectPurchaseOrderCheckbox(String checkboxName) {
//        WebElement checkboxRoot = driver.findElement(By.xpath("//span[normalize-space()='Purchase']/../..//div[contains(@data-pc-name,'" + checkboxName + "')]"));
        WebElement checkboxRoot = driver.findElement(By.xpath( "//span[normalize-space()='"+checkboxName+"']/preceding::div[@data-pc-name='checkbox']"));
        webDriverCommonLibrary.explicitWait(driver, checkboxRoot);
        webDriverCommonLibrary.scrollIntoTheView(driver, checkboxRoot);

        // Drill down to the actual clickable box
        WebElement checkboxBox = checkboxRoot.findElement(By.cssSelector("div.p-checkbox-box"));
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, checkboxBox);
    }

//    public void verifyRowMenuItems(String menuItemsText, String columnName) {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        wait.pollingEvery(Duration.ofMillis(100))
//                .ignoring(StaleElementReferenceException.class)
//                .ignoring(ElementClickInterceptedException.class)
//                .ignoring(ElementNotInteractableException.class);
//
//        webDriverCommonLibrary.waitForLoadingOverlay();
//
//        // Find fresh, scroll and click atomically; any stale/intercepted failure re-runs all three
//        wait.until(d -> {
//            WebElement button = d.findElement(listRowActionButtonLocator(columnName));
//            ((JavascriptExecutor) d).executeScript(
//                    "arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
//            button.click();
//            return true;
//        });
//
//        for (String menuItem : menuItemsText.split(",")) {
//            WebElement menuElement = wait.until(
//                    ExpectedConditions.visibilityOfElementLocated(menuItemLocator(menuItem.trim())));
//            webDriverCommonLibrary.explicitWait(driver, menuElement);
//            webDriverCommonLibrary.highLightTheElement(driver, menuElement);
//        }
//    }


    public void verifyRowMenuItems(String menuItemsText, String columnName) {
        List<String> items = Arrays.stream(menuItemsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                    "No menu items provided for column: " + columnName);
        }

        By buttonLocator = listRowActionButtonLocator(columnName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.pollingEvery(Duration.ofMillis(100))
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class)
                .ignoring(ElementNotInteractableException.class);

        WebDriverWait openWait =
                new WebDriverWait(driver, Duration.ofSeconds(3));
        openWait.pollingEvery(Duration.ofMillis(100))
                .ignoring(StaleElementReferenceException.class);

        long deadline =
                System.nanoTime() + Duration.ofSeconds(20).toNanos();

        while (true) {

            webDriverCommonLibrary.waitForLoadingOverlay();

            // Open the row-action menu using a fresh button reference
            wait.until(d -> {
                WebElement button = d.findElement(buttonLocator);

                ((JavascriptExecutor) d).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                        button);

                button = d.findElement(buttonLocator);
                button.click();

                return true;
            });

            // Wait until the first expected menu item is visible
            try {
                openWait.until(ExpectedConditions.visibilityOfElementLocated(
                        menuItemLocator(items.get(0))));
            } catch (TimeoutException ignored) {
                // Continue checking and retry if the menu did not open
            }

            // Verify every expected menu item
            List<WebElement> found = new ArrayList<>();

            for (String item : items) {
                WebElement element = firstDisplayed(menuItemLocator(item));

                if (element == null) {
                    break;
                }

                found.add(element);
            }

            if (found.size() == items.size()) {

                // Highlight all verified menu items
                for (WebElement menuElement : found) {
                    webDriverCommonLibrary.explicitWait(driver, menuElement);
                    webDriverCommonLibrary.highLightTheElement(
                            driver, menuElement);
                }

                // Close the menu by clicking the same row-action button again
                wait.until(d -> {
                    WebElement button = d.findElement(buttonLocator);

                    ((JavascriptExecutor) d).executeScript(
                            "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                            button);

                    button = d.findElement(buttonLocator);
                    button.click();

                    return true;
                });

                System.out.println(
                        "Verified menu items and closed the menu for column: "
                                + columnName);

                return;
            }

            // Fail with details if the expected items are not found
            if (System.nanoTime() > deadline) {

                List<String> visible = driver.findElements(
                                By.cssSelector(
                                        "[role='menuitem'], .p-menuitem-text"))
                        .stream()
                        .filter(WebElement::isDisplayed)
                        .map(el -> "[" + el.getText() + "]")
                        .collect(Collectors.toList());

                throw new AssertionError(
                        "Expected menu items " + items
                                + " for '" + columnName
                                + "' but menu showed: " + visible);
            }

            // Close an incomplete menu before retrying
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        }
    }


    private WebElement firstDisplayed(By locator) {
        try {
            for (WebElement el : driver.findElements(locator)) {
                if (el.isDisplayed()) {
                    return el;
                }
            }
        } catch (StaleElementReferenceException ignored) {
            // menu re-rendered between find and check; treat as not found, caller re-opens
        }
        return null;
    }
    /** Opens the row actions menu under the column and clicks the given menu item. */
    public void selectRowMenuItem(String item, String columnName) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        By buttonLocator = listRowActionButtonLocator(columnName);

        wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
        try {
            WebElement button = driver.findElement(buttonLocator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
            // Re-find the button after scroll because the table can re-render
            button = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
            System.out.println("Button text = [" + button.getText() + "]");
            button.click();
        } catch (StaleElementReferenceException e) {
            System.out.println("Row action button became stale. Re-locating...");
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
            button.click();
        }
        // Wait for requested menu item
        By menuLocator = menuItemLocator(item);
        WebElement menuElement = wait.until(ExpectedConditions.elementToBeClickable(menuLocator));
        menuElement.click();
       }

//    public void selectRowMenuItem(String item, String columnName) {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        wait.pollingEvery(Duration.ofMillis(100))
//                .ignoring(StaleElementReferenceException.class)
//                .ignoring(ElementClickInterceptedException.class);
//
//        By buttonLocator = listRowActionButtonLocator(columnName);
//        By menuLocator = menuItemLocator(item);
//
//        // Only open the menu if it isn't already open (avoids toggling it closed)
//        if (driver.findElements(menuLocator).isEmpty()) {
//            wait.until(d -> {
//                WebElement button = d.findElement(buttonLocator);
//                ((JavascriptExecutor) d).executeScript(
//                        "arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
//                button.click();
//                return true;
//            });
//        }
//
//        try {
//            wait.until(ExpectedConditions.elementToBeClickable(menuLocator)).click();
//        } catch (TimeoutException e) {
//            List<String> visible = driver.findElements(By.cssSelector("[role='menuitem'], .p-menuitem-text"))
//                    .stream().map(el -> "[" + el.getText() + "]").collect(Collectors.toList());
//            throw new AssertionError("Menu item '" + item + "' not found. Menu items currently in DOM: " + visible, e);
//        }
//    }


    /********************overlau
    /** Waits only while the loading overlay is present; returns instantly if it isn't. */
//    private void waitForLoadingOverlayGone() {
//        new WebDriverWait(driver, Duration.ofSeconds(15)).until(
//                ExpectedConditions.invisibilityOfElementLocated(
//                        By.xpath("//div[contains(@class,'loading-overlay')]")));
//    }
//
//    /** Opens the row actions menu under the column and clicks the given menu item. */
//    public void selectRowMenuItem(String item, String columnName) {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        By buttonLocator = listRowActionButtonLocator(columnName);
//
//        // Re-locate on every attempt, so stale or intercepted clicks are retried once
//        for (int attempt = 1; attempt <= 2; attempt++) {
//            try {
//                waitForLoadingOverlayGone();
//                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(buttonLocator));
//                ((JavascriptExecutor) driver).executeScript(
//                        "arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
//                button.click();
//                break;
//            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
//                if (attempt == 2) {
//                    throw e;   // second failure is a real problem, so report it
//                }
//                // loop again: the overlay wait at the top handles the timing
//            }
//        }
//
//        // Menu item (the overlay can reappear after the menu opens)
//        waitForLoadingOverlayGone();
//        WebElement menuElement = wait.until(ExpectedConditions.elementToBeClickable(menuItemLocator(item)));
//        menuElement.click();
//    }

//    public void selectRowMenuItem(String item, String columnName) {
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        By buttonLocator = listRowActionButtonLocator(columnName);
//        By menuLocator = menuItemLocator(item);
//
//        // 1. Wait for the page/table to finish loading
//        waitForLoadingToFinish(driver);
//
//        // 2. Open the row action menu (re-find + retry on stale/intercepted)
//        wait.ignoring(StaleElementReferenceException.class, ElementClickInterceptedException.class)
//                .until(d -> {
//                    waitForLoadingToFinish(d);
//                    WebElement button = d.findElement(buttonLocator);
//                    ((JavascriptExecutor) d).executeScript(
//                            "arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
//                    button.click();
//                    return true;
//                });
//
//        // 3. Click the requested menu item (same retry protection)
//        new WebDriverWait(driver, Duration.ofSeconds(20))
//                .ignoring(StaleElementReferenceException.class, ElementClickInterceptedException.class)
//                .until(d -> {
//                    d.findElement(menuLocator).click();
//                    return true;
//                });
//
//        // 4. Menu action usually triggers a load (navigation/modal), wait for it
//        waitForLoadingToFinish(driver);
//    }

    public void waitForLoadingToFinish(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-overlay")));
    }

    public void logout() {
        By userMenu = By.xpath("//div[@class='p-toolbar-group-end']//i[contains(@class,'bi-person-circle')]");
        By logoutButton = By.xpath("//span[normalize-space()='Logout']");

        WebElement userMenuIcon = driver.findElement(userMenu);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, userMenuIcon);
        WebElement logoutButtonIcon = driver.findElement(logoutButton);

        webDriverCommonLibrary.explicitWait(driver, logoutButtonIcon);
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, logoutButtonIcon);
    }

    public void verifyFieldState(
            WebElement field,
            String fieldName,
            String expectedState) {

        boolean actualDisabled = isFieldDisabled(field);

        boolean expectedDisabled;

        if ("disabled".equalsIgnoreCase(expectedState)) {
            expectedDisabled = true;

        } else if ("enabled".equalsIgnoreCase(expectedState)) {
            expectedDisabled = false;

        } else {
            Assert.fail("Invalid expected field state '" + expectedState + "' for field '" + fieldName + "'. Expected 'enabled' or 'disabled'.");
            return;
        }

        Assert.assertEquals(actualDisabled, expectedDisabled, "Field state mismatch for '" + fieldName +"'. Expected: " + expectedState + ", Actual: " + (actualDisabled ? "disabled" : "enabled")
        );
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
        if (classAttribute != null && classAttribute.contains("p-disabled")) {
            return true;
        }
        return !field.isEnabled();
    }


    public void verifyField(String section, String fieldName, String ariaLabel, String expectedState, String expectedValue) {
        WebElement field = getField(section, fieldName, ariaLabel);
        webDriverCommonLibrary.waitForSetTime(2000);
        webDriverCommonLibrary.verifyFieldState(field, fieldName, expectedState);
        webDriverCommonLibrary.highLightTheElement(driver, field);
        verifyFieldValue(field, fieldName, expectedValue);
    }

    public WebElement getField(String section, String field, String ariaLabel) {
        By locator = By.xpath("//p-card[.//span[contains(text(),'" + section + "')]]" + "//label[.//span[normalize-space()='" + field + "']]/.." + "//*[@aria-label='" + ariaLabel + "']");
        return webDriverCommonLibrary.waitForVisibility(locator);
    }

    public void verifyFieldValue(WebElement field, String fieldName, String expectedValue) {
        String actualValue = field.getAttribute("value");
        if (actualValue == null || actualValue.trim().isEmpty()) {
            actualValue = field.getText();
        }
        Assert.assertEquals(actualValue.trim(), expectedValue.trim(), "Field value mismatch for '" + fieldName + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
    }

    public void verifyMultipleCommissionValueUnit(String section, String columnName, int rowIndex, String expectedUnit) {
        String actualUnit = getCommissionDetailDisplayedValue(section, columnName, rowIndex);
        Assert.assertEquals(
                actualUnit,
                expectedUnit, "Commission Details value mismatch in column '" + columnName + "', row " + (rowIndex + 1) + ". Expected: " + expectedUnit + ", Actual: " + actualUnit);
    }

    public void verifyMultipleCommissionValueUnitValidation(String section, String columnName, int rowIndex, String expectedUnit) {
        String actualUnit = getCommissionDetailDisplayedValue(section, columnName, rowIndex);
        WebElement element = getCommissionDetailCellValidation(section, columnName, rowIndex);
        Assert.assertEquals(actualUnit, expectedUnit, "Commission Details value mismatch in column '" + columnName + "', row " + (rowIndex + 1) + ". Expected: " + expectedUnit + ", Actual: " + actualUnit);
        highlightElement(element);
    }
    private void highlightElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String originalStyle = element.getAttribute("style") == null
                ? ""
                : element.getAttribute("style");

        js.executeScript(
                "arguments[0].style.setProperty("
                        + "'background-color', '#C8E6C9', 'important');"   // light green
                        + "arguments[0].style.setProperty("
                        + "'outline', '3px solid #4CAF50', 'important');", // medium green border
                element);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        js.executeScript(
                "arguments[0].setAttribute('style', arguments[1]);",
                element,
                originalStyle);
    }

    public String getCommissionDetailDisplayedValue(String sectionName, String columnName, int rowIndex) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cell = wait.until(d -> {
            WebElement currentCell = getCommissionDetailCellValidation(sectionName, columnName, rowIndex);
            return currentCell.isDisplayed() ? currentCell : null;
        });

        WebElement selectedLabel = wait.until(d -> {
            List<WebElement> labels = cell.findElements(By.cssSelector(".p-dropdown-label"));

            for (WebElement label : labels) {
                if (label.isDisplayed()) {
                    String text = label.getText().replace("\u00A0", " ").trim();
                    if (!text.isEmpty()) {
                        return label;
                    }
                }
            }
            return null;
        });

        return selectedLabel.getText().replace("\u00A0", " ").trim();
    }
    public String getCommissionDetailDisplayedValueValidation(String sectionName, String columnName, int rowIndex) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cell = wait.until(d -> {WebElement currentCell = getCommissionDetailCell(sectionName, columnName, rowIndex);
            return currentCell.isDisplayed() ? currentCell : null;
        });
        return cell.getText().replace("\u00A0", " ").trim();
    }
}