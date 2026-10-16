package steps;

import io.cucumber.java.PendingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import base.DriverFactory;
import invensoft.testdata.CommissionDetailRow;
import invensoft.testdata.ContractTestData;
import invensoft.testdata.ProductDetailRow;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.CommodityContractManagementPage;
import pages.LoginPage;
import pages.PageObjectManager;
import utils.ConfigReader;
import utils.DateExpressionResolver;
import utils.TestDataReader;
import utils.WebDriverCommonLibrary;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static pages.PageObjectManager.getCommodityContractManagementPage;
import static utils.Constants.ROW_SETTLE_PAUSE_MS;
import static utils.Constants.SETTLE_PAUSE_MS;
import static utils.Constants.WAIT_SHORT_SEC;
import static utils.Constants.setTime;

public class StepsContainer {

    private static final Logger log = LogManager.getLogger(StepsContainer.class);


    private String createdContractNumber;
    private LoginPage loginPage;
    private WebDriver driver;
    private WebDriverCommonLibrary webDriverCommonLibrary;
    private ConfigReader configReader;
    private ContractTestData currentTestData;

    @Given("test data {string} is loaded")
    public void testDataIsLoaded(String testCaseId) throws IOException {
        currentTestData = TestDataReader.getByTestCaseId(testCaseId);
        log.info("Loaded test data: " + testCaseId);
    }
    private String resolve(String raw) {
        if (raw == null || !raw.startsWith("@")) {
            return raw;
        }
        if (currentTestData == null) {
            throw new IllegalStateException("'" + raw + "' needs test data, but no test data is loaded. "
                    + "Add: Given test data \"<TestCaseId>\" is loaded");
        }
        return currentTestData.getValue(raw.substring(1));
    }
    private String resolveLogin(String arg) {
        return (arg != null && arg.startsWith("@")) ? resolve(arg) : ConfigReader.getProperty(arg);
    }

    @Given("open browser")
    public void openBrowser() {
        // Browser, like the url, comes from the test data ("login.browser") when a test case is loaded.
        String browser = (currentTestData != null) ? currentTestData.getValue("login.browser") : ConfigReader.getProperty("browser");
        driver = DriverFactory.initializeDriver(browser);
        // Login url comes from the test data when a test case is loaded ("login.url");
        // features that load no test data keep using values.properties.
        String url = (currentTestData != null) ? currentTestData.getValue("login.url") : ConfigReader.getProperty("url");
        webDriverCommonLibrary = new WebDriverCommonLibrary(driver);
        log.info("================================");
        log.info("Launching application");
        log.info("Browser: " + browser);
        log.info("URL: " + url);
        log.info("================================");
        driver.get(url);
        driver.manage().window().maximize();
        log.info("Current URL: " + driver.getCurrentUrl());
        log.info("Page Title: " + driver.getTitle());
        loginPage = new LoginPage(driver);
    }

    @When("enter the user name as {string}")
    public void enterTheUserNameAs(String arg0) {
        String username = resolveLogin(arg0);
        loginPage.enterUsername(username);
    }

    @When("enter the user password as {string}")
    public void enterTheUserPasswordAs(String arg0) {
        String password = resolveLogin(arg0);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        log.info("After Login URL: " + driver.getCurrentUrl());
        log.info("After Login Title: " + driver.getTitle());

    }

    @Then("the user should be navigated to the dashboard")
    public void theUserShouldBeNavigatedToTheDashboard() {
        boolean isDashboardVisible = loginPage.isDashboardDisplayed();
        Assert.assertTrue(isDashboardVisible, "Login did not land on the dashboard as expected");
    }

    @And("verify {string} section is displayed on the dashboard page")
    public void verifyFieldIsDisplayingInLightningPage(String arg0) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//div[contains(@class,'content') and contains(@class,'sidenav-content')]//*[contains(text(),'" + arg0 + "')]")));
    }

    @Then("verify {string} is displayed in the dashboard sidebar menu")
    public void verifyIsDisplayedInTheDashboardSidebarMenu(String arg0) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//li[@routerlinkactive='active' and contains(@id,'" + arg0 + "')]")));
    }

    @When("user navigates to {string} dashboard sidebar menu")
    public void userNavigatesToDashboardSidebarMenu(String arg0) {
        getCommodityContractManagementPage().clickSidebarMenu(arg0);
    }

    @When("user clicks on {string} section on the dashboard page")
    public void userClicksOnSectionOnTheDashboardPage(String arg0) {
        getCommodityContractManagementPage().clickDashboardSection(arg0);
    }

    @Then("verify {string}{string} button is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyButtonIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//p-button[contains(@icon,'pi pi-" + arg1 + "')]//button")));

    }

    @Then("verify {string}{string} prime icons is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyPrimeIconsIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        arg1 = resolve(arg1);
        getCommodityContractManagementPage().highlightPrimeIconButton(arg1);
    }

    @Then("verify {string}{string} boot strap icon is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyBootStrapIconIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//p-button[contains(@icon,'" + arg1 + "')]//button")));

    }

    @When("clicks on {string} {string} icon on the Commodity Contract Management dashboard")
    public void clicksOnIconOnTheCommodityContractManagementDashboard(String arg0, String arg1) {
        getCommodityContractManagementPage().clickPrimeIconButton(arg1);
    }

    @Then("verifies that the {string} popup is displayed")
    public void verifiesThatThePopupIsDisplayed(String arg0) {
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.getCreateGeneralPopupTitle();
    }

    @Then("verifies that the {string} section is displayed")
    public void verifiesThatTheSectionIsDisplayed(String arg0) {
        getCommodityContractManagementPage().highlightSpanContaining(arg0);
    }

    @When("user selects {string} from the {string} dropdown in the {string} section")
    public void userSelectsFromTheDropdownInTheSection(String value, String field, String section) {
        value = resolve(value);
        field = resolve(field);
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.selectDropdownValue(section, field, value);
    }

    @And("click on {string} button")
    public void clickOnButton(String arg0) {
        arg0 = resolve(arg0);
        getCommodityContractManagementPage().clickSpanButton(arg0);
    }

    @When("user enters {string} in {string} text field in the {string} section")
    public void userEntersInTheTextFieldInTheSection(String arg0, String field, String section) {
        arg0 = resolve(arg0);
        getCommodityContractManagementPage().enterTextInSectionField(section, field, arg0);
    }

    @And("verifies that the {string} table section is displayed")
    public void verifiesThatTheTableSectionIsDisplayed(String label) {
        getCommodityContractManagementPage().highlightTableSection(label);
    }


    @And("verifies that the {string} column is displayed in the {string} section table")
    public void verifiesThatTheColumnIsDisplayedInTheSectionTable(String column, String section) {
        WebElement element = driver.findElement(By.xpath("//h3[normalize-space()='" + section + "']/following::th[.//span[normalize-space()='" + column + "']]"));
        webDriverCommonLibrary.highLightTheElement(driver, element);
    }

    @When("user selects {string} from the {string} dropdown in the {string} table")
    public void userSelectsFromTheDropdownInTheTable(String value, String field, String section) {
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.selectTableDropdownValue(value, field, section);
    }

    @When("user select {string} from the {string} second type dropdown in the {string} table")
    public void userSelectFromTheDropdownInTheTable(String value, String field, String section) {

        // 1. Locate dropdown
        By dropdownLocator = By.xpath(
                "//h3[normalize-space()='" + section + "']" +
                        "/following::table[1]" +
                        "//tbody/tr[1]/td[" +
                        "count(" +
                        "//h3[normalize-space()='" + section + "']" +
                        "/following::table[1]" +
                        "//thead/tr/th[" +
                        ".//span[normalize-space()='" + field + "']" +
                        "]/preceding-sibling::th" +
                        ") + 1" +
                        "]" +
                        "//p-dropdown"
        );

        // 2. Wait for dropdown
        WebElement dropdown = webDriverCommonLibrary.explicitWaitByLocator(driver, dropdownLocator);

        // 3. Scroll
        webDriverCommonLibrary.scrollIntoTheView(driver, dropdown);
        // 4. Click
        dropdown.click();


        // 5. Locate opened PrimeNG combobox
//        By searchOptionLocator = By.xpath("//span[@role='combobox'" + " and @aria-haspopup='listbox'" + " and @aria-expanded='true']"
        By searchOptionLocator = By.xpath("//input[@role='searchbox' and @type='text']");

        WebElement selectSearchOption =
                webDriverCommonLibrary.explicitWaitVisibilityByLocator(
                        driver,
                        searchOptionLocator
                );

        // 6. Enter value
        selectSearchOption.sendKeys(value);


        // 7. Locate required option
        By optionLocator = By.xpath(
                "//ul[@role='listbox']" +
                        "//li[@role='option'" +
                        " and normalize-space(@aria-label)='" + value + "']"
        );

        // 8. Wait for option
        WebElement selectOption =
                webDriverCommonLibrary.explicitWaitByLocator(
                        driver,
                        optionLocator
                );

        // 9. Scroll
        webDriverCommonLibrary.scrollIntoTheView(
                driver,
                selectOption
        );

        // 10. Click option
        selectOption.click();
    }

    @When("user selects {string} from the {string} dropdown type in the {string} table")
    public void userSelectsFromTheDropdownTypeInTheTable(String value, String field, String section) {
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.selectTableType2DropdownValue(value, field, section);
    }

    @When("user enters {string} in the {string} number field in the {string} table")
    public void userEntersInTheNumberFieldInTheTable(String value, String field, String section) {
        WebElement numberField = driver.findElement(By.xpath("//h3[normalize-space()='" + section + "']/following::table[1]//tbody/tr[1]/td[count(//h3[normalize-space()='" + section + "']/following::table[1]//thead/tr/th[.//span[normalize-space()='" + field + "']]/preceding-sibling::th)+1]//.//input"));
        webDriverCommonLibrary.explicitWait(driver, numberField);
        webDriverCommonLibrary.scrollIntoTheView(driver, numberField);
        numberField.sendKeys(value);
    }


    @When("user enters {string} in {string} long text area field in the {string} section")
    public void userEntersInLongTextAreaFieldInTheSection(String value, String field, String section) {
        value = resolve(value);
        getCommodityContractManagementPage().enterTextInSectionTextArea(section, field, value);
    }

    @When("test selects target date range from {string} to {string} in the {string} field in the {string} table")
    public void testSelectsTargetDateRangeFromToInTheFieldInTheTable(String startDateStr, String endDateStr, String field, String table) {
        LocalDate startDate = LocalDate.parse(startDateStr); // ISO format yyyy-MM-dd
        LocalDate endDate = LocalDate.parse(endDateStr);
        webDriverCommonLibrary.testselectTargetDateRangeInTable(field, table, startDate, endDate);
    }

    @When("user selects target date range from {string} to {string} for row {int}  {string} field in the {string} table")
    public void userSelectsTargetDateRangeFromToForRowFieldInTheTable(String startDateStr, String endDateStr, int rowIndex, String field, String table) {
        LocalDate startDate = DateExpressionResolver.resolve(startDateStr);
        LocalDate endDate = DateExpressionResolver.resolve(endDateStr);
        PageObjectManager.getCreateCommodityContractPage().setTargetDateRange(rowIndex, startDate, endDate);
    }

    @And("verify {string} field is displaying in list view")
    public void verifyFieldIsDisplayingInListView(String arg0) {
        WebElement listelement = driver.findElement(By.xpath("//thead[@role='rowgroup']/tr//span[normalize-space()='" + arg0 + "']"));
        webDriverCommonLibrary.scrollIntoTheView(driver, listelement);
        webDriverCommonLibrary.explicitWait(driver, listelement);
        webDriverCommonLibrary.highLightTheElement(driver, listelement);
    }

    @Then("the successful validation message {string} is displayed")
    public void theSuccessfulValidationMessageIsDisplayed(String arg0) {
        By successMsg = By.xpath("//*[contains(normalize-space(.),'Contract created successfully')]");
        webDriverCommonLibrary.explicitWaitVisibilityByLocator(driver, successMsg);
    }

    @Then("capture {string} {string}")
    public void capture(String arg0, String arg1) {
        if (!"Contract No.".equals(arg1)) {
            throw new IllegalArgumentException("capture supports only the 'Contract No.' column but got: '" + arg1 + "'");
        }
        webDriverCommonLibrary.waitForSetTime(setTime / 2);

        String contractNo = getCommodityContractManagementPage().getListColumnText(arg1).trim();
        String key = arg0 + "ContractNumber";

        webDriverCommonLibrary.updateTheValueInDataValuePropertiesFile(key, contractNo);
        webDriverCommonLibrary.updateTheValueInJsonFile(key, contractNo);
    }

    @When("the user selects the {string} checkbox under {string}")
    public void theUserSelectsTheCheckboxUnder(String arg0, String arg1) {
        getCommodityContractManagementPage().selectCheckbox(arg0);
    }

    @Then("verify {string}{string} prime icon button is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyPrimeIconButtonIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//span[contains(normalize-space(.),'" + arg1 + "')]")));
    }

    @When("user selects {string} from the {string} literal dropdown in the {string} section")
    public void userSelectsFromTheLiteralDropdownInTheSection(String value, String field, String section) {
        value = resolve(value);
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.selectLiteralDropdownValue(section, field, value);
    }

    @Given("user fills product details for test case {string}")
    public void userFillsProductDetailsForTestCase(String testCaseId) throws IOException {
        ContractTestData data = TestDataReader.getByTestCaseId(testCaseId);
        List<ProductDetailRow> rows = data.getProductDetails();
        CommodityContractManagementPage page = getCommodityContractManagementPage();

        final String section = "Product Details";

        for (int i = 0; i < rows.size(); i++) {
            ProductDetailRow row = rows.get(i);
            int rowIndex = i;

            log.info("Row " + rowIndex + " -> product = [" + row.getProduct() + "]");

            if (isBlank(row.getProduct())) continue;
            if (i > 0) {

                log.info("========================================");
                log.info("BEFORE ADDING PRODUCT DETAIL ROW");
                log.info("Current row index = " + rowIndex);
                log.info("========================================");

                page.printProductDetailRowCount();
                page.debugLastProductDetailRow();
                page.debugProductDetailAddButtons(section);
                page.debugRowValidity(rowIndex - 1);
                page.addProductDetailRow(section);
                log.info("Product rows after add = " + page.getProductDetailRows().size());
                webDriverCommonLibrary.waitForSetTime(ROW_SETTLE_PAUSE_MS);
                page.debugAllProductDetailTables();

            }
            page.debugProductDetailActionButtons(1);
            page.debugLastProductDetailRow();

            // Dropdowns
            page.selectDropdownValue(section, "Product", rowIndex, row.getProduct());
            page.userEnterInTheNumberFieldInTheTable("Units", section, rowIndex, row.getUnits());
            page.selectTableType2DropdownValues(section, "Packing", rowIndex, row.getPacking());
            // UOM intentionally skipped — auto-selected when Product is chosen (per your note)
            page.userEnterInTheNumberFieldInTheTable("Qty.", section, rowIndex, row.getQty());
            page.userEnterInTheNumberFieldInTheTable("No Of Lots.", section, rowIndex, row.getNoOfLots());
            page.userEnterInTheNumberFieldInTheTable("Price", section, rowIndex, row.getPrice());

            if (isNotBlank(row.getTargetType())) {
                page.selectProductDetailValue(section, "Target Type", rowIndex, row.getTargetType());
            }
            if (isNotBlank(row.getTradeMonth())) {
                page.selectDropdownValue(section, "Trade Month", rowIndex, row.getTradeMonth());
            }

//            // Plain text fields — each written exactly once now
//            if (isNotBlank(row.getPositionMonth())) {
//                page.enterFieldValue("Position Month", section, rowIndex, row.getPositionMonth());
//            }
            if (isNotBlank(row.getQuality())) {
                page.enterFieldValue("Quality", section, rowIndex, row.getQuality());
            }

            // Date range — delegates to your existing, verified setTargetDateRange method
            if (isNotBlank(row.getTargetDateRange())) {
                fillTargetDateRangeFromJson(row.getTargetDateRange(), rowIndex);
            }

            // certificationPremium from your sample JSON isn't wired to any UI call yet —
            // add a line here once you tell me which control it maps to.
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    // Assumed to already exist in your codebase per your comment — signature
    // shown here just for reference/consistency with the rowIndex convention above.
    private void fillTargetDateRangeFromJson(String targetDateRange, int rowIndex) {
        // Split on " - " (space, hyphen, space) specifically — NOT a bare "-" —
        // because a single expression can itself contain arithmetic like
        // "today-2" or "today+4", and a naive split on any hyphen would
        // break those. \\s+ tolerates inconsistent spacing in the JSON.
        String[] parts = targetDateRange.split("\\s+-\\s+", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "targetDateRange '" + targetDateRange + "' for row " + rowIndex +
                            " is not in the expected '<start> - <end>' format (e.g. 'today - today+4')."
            );
        }

        String startExpr = parts[0].trim();
        String endExpr = parts[1].trim();

        LocalDate startDate = DateExpressionResolver.resolve(startExpr);
        LocalDate endDate = DateExpressionResolver.resolve(endExpr);

        // buildDateRangeLocator() uses a 1-based XPath sibling predicate
        // (following-sibling::div[rowIndex]), while this loop's rowIndex is
        // 0-based to match tbody-tr JS array indexing used everywhere else.
        // Convert ONLY at this call site — do not change the verified
        // locator method itself.
        int xpathRowIndex = rowIndex + 1;

        // ASSUMPTION: setTargetDateRange lives on the same page object as
        // everything else in this loop (CommodityContractManagementPage).
        // If it's actually a separate CreateCommodityContractPage class,
        // swap this to PageObjectManager.getCreateCommodityContractPage().
        PageObjectManager.getCreateCommodityContractPage().setTargetDateRange(xpathRowIndex, startDate, endDate);
    }

      @Given("user fills multiple product details for test case {string}")
    public void userFillsMultipleProductDetailsForTestCase(String testCaseId) throws IOException {
        webDriverCommonLibrary.waitForSetTime(SETTLE_PAUSE_MS);
        try {
            ContractTestData data = TestDataReader.getByTestCaseId(testCaseId);
            List<ProductDetailRow> rows = data.getProductDetails();

            if (rows == null || rows.isEmpty()) {
                log.info("No productDetails in test data for '" + testCaseId + "' - skipping Product Details step.");
                return;
            }

            CommodityContractManagementPage page = getCommodityContractManagementPage();

            final String section = "Product Details";

            log.info("========================================");
            log.info("MULTIPLE PRODUCT DETAILS");
            log.info("Test Case = " + testCaseId);
            log.info("JSON Rows = " + rows.size());
            log.info("========================================");

            // Counts only rows actually created in the UI, so skipped (blank) JSON rows never shift the UI row index.
            int uiRowIndex = -1;

            for (int i = 0; i < rows.size(); i++) {

                ProductDetailRow row = rows.get(i);
                // Here using 0-based index as per java -GK
                int rowIndex = i;

                log.info("========================================");
                log.info("PROCESSING PRODUCT ROW");
                log.info("JSON Row Index = " + rowIndex);
                log.info("UI Row Number  = " + (rowIndex + 1));
                log.info("Product        = [" + row.getProduct() + "]");
                log.info("========================================");

                if (isBlank(row.getProduct())) {
                    log.warn("Skipping row " + rowIndex + " because Product is blank.");
                    continue;
                }

                // From here on rowIndex means the UI row (0-based), not the JSON position.
                rowIndex = ++uiRowIndex;
                /* First JSON row already exist  * Row 0 -> existing UI row   * Row 1 -> click +  * Row 2 -> click + * etc.    */
                if (rowIndex > 0) {
                    log.info("Adding UI row for JSON row " + rowIndex);
                    int rowsBefore = page.getProductDetailRows().size();
                    log.info("Rows before Add = " + rowsBefore);
                    page.addProductDetailRow(section);
                    int rowsAfter = page.getProductDetailRows().size();
                    log.info("Rows after Add = " + rowsAfter);
                    if (rowsAfter <= rowsBefore) {
                        throw new IllegalStateException("Product Details row was not added. " + "Expected rows > " + rowsBefore + " but found " + rowsAfter);
                    }
                    webDriverCommonLibrary.waitForSetTime(ROW_SETTLE_PAUSE_MS);
                }
                page.selectMultipleDropdownValue(section, "Product", rowIndex, row.getProduct());
                page.userEnterInTheMultipleNumberFieldInTheTable("Units", section, rowIndex, row.getUnits());
                page.selectMultipleTableType2DropdownValues(section, "Packing", rowIndex, row.getPacking());
                page.userEnterInTheMultipleNumberFieldInTheTable("Qty.", section, rowIndex, row.getQty());
                page.userEnterInTheMultipleNumberFieldInTheTable("No Of Lots.", section, rowIndex, row.getNoOfLots());
                page.userEnterInTheMultipleNumberFieldInTheTable("Price", section, rowIndex, row.getPrice());

                if (isNotBlank(row.getTargetType())) {
                    page.selectMultipleProductDetailValue(section, "Target Type", rowIndex, row.getTargetType());
                }

                if (isNotBlank(row.getTradeMonth())) {
                    page.selectMultipleDropdownValue(section, "Trade Month", rowIndex, row.getTradeMonth());
                }

                if (isNotBlank(row.getTargetDateRange())) {
                    fillTargetDateRangeFromJson(row.getTargetDateRange(), rowIndex);}
                log.info("COMPLETED PRODUCT ROW = " + rowIndex);

                if (isNotBlank(row.getQuality())) {
                    page.enterMultipleFieldValue("Quality", section, rowIndex, row.getQuality());
                }

//                page.selectPositionMonthValueInTheTable("Position Month", section, rowIndex, row.getPositionMonth());
//                page.userEnterInTheMultipleQualityTextFieldInTheTable("Quality", section, rowIndex, row.getQuality());

//                  page.createCertificationPremium(section, "Certification & Premium", rowIndex, row.getCertificationPremium());

            }
            log.info("========================================");
            log.info("ALL PRODUCT DETAILS COMPLETED");
            log.info("Total JSON Rows = " + rows.size());
            log.info("========================================");

        } catch (Exception e) {
            log.error("ERROR while filling multiple Product Details", e);
            throw e;
        }
    }

    @And("verify the message {string} is displayed")
    public void verifyTheMessageIsDisplayed(String arg0) {
        arg0 = resolve(arg0);
        getCommodityContractManagementPage().verifyMessageVisible(arg0);
    }


    @Then("search for the created {string} by {string}")
    public void searchForTheCreatedBy(String arg0, String arg1) {
        String propertyKey = arg0 + arg1;
        String recordnumber = ConfigReader.getProperty(propertyKey);
        getCommodityContractManagementPage().searchContractNo(recordnumber);
    }

    @And("verify {string} is in {string} status")
    public void verifyIsInStatus(String arg0, String arg1) {
        arg1 = resolve(arg1);
        getCommodityContractManagementPage().verifyRecordAndStatus(arg0, arg1);
    }

    @Then("user captures the created contract number")
    public void userCapturesTheCreatedContractNumber() {

        createdContractNumber = CommodityContractManagementPage.getCreatedContractNumber();

        if (createdContractNumber == null || createdContractNumber.trim().isEmpty()) {
            throw new IllegalStateException("Created contract number was not captured from UI.");
        }

        createdContractNumber = createdContractNumber.trim();
        log.info("CURRENT CREATED CONTRACT NUMBER = " + createdContractNumber);

        ConfigReader.setProperty("CommercialCommodityPOContractNumber", createdContractNumber);

        String propertyValue = ConfigReader.getProperty("CommercialCommodityPOContractNumber");
        log.info("PROPERTY FILE CONTRACT NUMBER = " + propertyValue);
    }

    @When("the user selects the {string} for the created Purchase Order")
    public void theUserSelectsTheForTheCreatedPurchaseOrder(String arg0) {
        getCommodityContractManagementPage().selectPurchaseOrderCheckbox(arg0);
    }

    @When("the user clicks the {string} button on the Commercial Commodity Contract Management page")
    public void theUserClicksTheButtonOnTheCommercialCommodityContractManagementPage(String arg0) {
        arg0 = resolve(arg0);
        getCommodityContractManagementPage().clickSpanButtonWithJs(arg0);
    }


    @Then("verify the {string} menu item is displayed for {string}")
    public void verifyTheMenuItemIsDisplayedFor(String arg0, String arg1) throws InterruptedException {
        By contractNoButton = By.xpath("//tbody[contains(@class,'p-datatable-tbody')]" +
                "/tr/td[" +
                "count(//th[contains(normalize-space(),'" + arg1 +
                "')]/preceding-sibling::th) + 1" +
                "]//button");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SHORT_SEC));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(contractNoButton));
        log.info("Button text = [" + button.getText() + "]");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
        wait.until(ExpectedConditions.elementToBeClickable(button));
        button.click();
        log.info("Native Selenium click executed.");


        WebElement menuItems = driver.findElement(By.xpath("//li[@data-pc-section = 'menuitem' and @aria-label = '" + arg0 + "']"));
        webDriverCommonLibrary.explicitWait(driver, menuItems);
        webDriverCommonLibrary.highLightTheElement(driver, menuItems);
    }

    @Then("verify the {string} menu items is displayed for {string}")
    public void verifyTheMenuItemsIsDisplayedFor(String menuItemsText, String columnName) {
        menuItemsText = resolve(menuItemsText);
        getCommodityContractManagementPage().verifyRowMenuItems(menuItemsText, columnName);
    }
    @Then("verify {string} message is displayed with {string} and {string} buttons")
    public void verifyMessageIsDisplayedWithAndButtons(String message, String firstButton, String secondButton) {
        message      = resolve(message);
        firstButton  = resolve(firstButton);
        secondButton = resolve(secondButton);

        CommodityContractManagementPage page = getCommodityContractManagementPage();

        WebElement confirmationMessage = page.locateDialogText(message, false);
        Assert.assertEquals(confirmationMessage.getText().trim(), message,
                "Confirmation message text mismatch");

        if (secondButton == null || secondButton.isBlank()) {
            // Single-button dialog (e.g. reason confirmation): only the confirm button
            WebElement confirmButton = page.locateConfirmDialogText(firstButton, true);
            Assert.assertEquals(confirmButton.getText().trim(), firstButton,
                    "Confirm button label mismatch");
        } else {
            // Two-button dialog: Yes / No
            WebElement yesButton = page.locateDialogText(firstButton, true);
            Assert.assertEquals(yesButton.getText().trim(), firstButton,
                    "Yes button label mismatch");

            WebElement noButton = page.locateDialogText(secondButton, true);
            Assert.assertEquals(noButton.getText().trim(), secondButton,
                    "No button label mismatch");
        }
    }

    @Then("verify that the {string} field is automatically populated based on the logged-in {string}")
    public void verifyThatTheFieldIsAutomaticallyPopulatedBasedOnTheLoggedIn(String section, String user) {
        user = resolve(user);
        // Verification only. The old version re-opened the dropdown and re-selected the user,
        // which changed application data instead of checking it.
        WebElement lookUp = getCommodityContractManagementPage().locateLoggedInUserField(section, user);

        String actualLabel = lookUp.getAttribute("aria-label");
        Assert.assertTrue(actualLabel != null && actualLabel.contains(user),
                "'" + section + "' field is not populated with the logged-in user. Expected to contain: '"
                        + user + "' but found: '" + actualLabel + "'");
        webDriverCommonLibrary.highLightTheElement(driver, lookUp);
    }

    @Then("verify that the {string} field displays {string}.")
    public void verifyThatTheFieldDisplays(String arg0, String arg1) {
        WebElement contractNo = getCommodityContractManagementPage().locateInputByLabel(arg0);

        // Assert the displayed text (arg1 was previously ignored). Falls back to the placeholder
        // in case the app renders "Auto" as a placeholder rather than as the input value.
        String displayed = contractNo.getAttribute("value");
        if (displayed == null || displayed.trim().isEmpty()) {
            displayed = contractNo.getAttribute("placeholder");
        }
        displayed = displayed == null ? "" : displayed.trim();
        Assert.assertEquals(displayed, arg1.trim(),
                "'" + arg0 + "' field text mismatch. Expected: '" + arg1 + "' but found: '" + displayed + "'");
        webDriverCommonLibrary.highLightTheElement(driver,contractNo);
    }

    @And("verify that the {string} field is displayed as today's date by default.")
    public void verifyThatTheFieldIsDisplayedAsTodaySDateByDefault(String arg0) {
        log.info(driver.findElement(By.id("contract_date")).getAttribute("value"));

        By contractDate = By.id("contract_date");
        String actualDate = driver.findElement(contractDate).getAttribute("value");
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Assert.assertEquals(actualDate, expectedDate, "Contract Date mismatch. Expected: " + expectedDate + " but found: " + actualDate);
        // Highlight only when verification passes
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(contractDate));
    }

    @Then("verify that the {string} field is displayed as {string}")
    public void verifyThatTheFieldIsDisplayedAs(String arg0, String arg1) {
        arg1 = resolve(arg1);
        getCommodityContractManagementPage().highlightSpanByAriaLabel(arg1);
    }

    @And("verify that the {string} field is displayed as {string} date by default.")
    public void verifyThatTheFieldIsDisplayedAsDateByDefault(String arg0, String arg1) {
        WebElement contractDate = getCommodityContractManagementPage().locateContractDateField();
        String actualDate = contractDate.getAttribute("value");
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Assert.assertEquals(actualDate, expectedDate, "Contract Date mismatch. Expected: " + expectedDate + " but found: " + actualDate);
        // Highlight only when verification passes
        webDriverCommonLibrary.highLightTheElement(driver, contractDate);
    }

    @Given("user fills multiple commission details for test case {string}")
    public void userFillsMultipleCommissionDetailsForTestCase(String testCaseId) throws IOException {
        webDriverCommonLibrary.waitForSetTime(SETTLE_PAUSE_MS);
        try {
            ContractTestData data = TestDataReader.getByTestCaseId(testCaseId);
            List<CommissionDetailRow> rows = data.getCommissionDetails();

            if (rows == null || rows.isEmpty()) {
                log.info("No commissionDetails in test data for '" + testCaseId + "' - skipping Commission Details step.");
                return;
            }

            CommodityContractManagementPage page = getCommodityContractManagementPage();

            final String section = "Commission Details";
            log.info("========================================");
            log.info("MULTIPLE COMMISSION DETAILS");
            log.info("Test Case = " + testCaseId);
            log.info("JSON Rows = " + rows.size());
            log.info("========================================");

            // Counts only rows actually created in the UI, so skipped (blank) JSON rows never shift the UI row index.
            int uiRowIndex = -1;

            for (int i = 0; i < rows.size(); i++) {
                CommissionDetailRow row = rows.get(i);
                int rowIndex = i;
                log.info("========================================");
                log.info("PROCESSING COMMISSION ROW");
                log.info("JSON Row Index = " + rowIndex);
                log.info("UI Row Number  = " + (rowIndex + 1));
                log.info("Agent/Broker   = [" + row.getAgentBroker() + "]");
                log.info("========================================");

                if (isBlank(row.getAgentBroker())) {
                    log.warn("Skipping row " + rowIndex + " because Agent/Broker is blank.");
                    continue;
                }

                // From here on rowIndex means the UI row (0-based), not the JSON position.
                rowIndex = ++uiRowIndex;

                if (rowIndex > 0) {
                    log.info("Adding UI row for JSON row " + rowIndex);
                    int rowsBefore = page.getCommissionDetailRows().size();
                    log.info("Rows before Add = " + rowsBefore);

                    page.addCommissionDetailRow(section);

                    int rowsAfter = page.getCommissionDetailRows().size();
                    log.info("Rows after Add = " + rowsAfter);

                    if (rowsAfter <= rowsBefore) {throw new IllegalStateException("Commission Details row was not added. "+ "Expected rows > " + rowsBefore + " but found " + rowsAfter);}
                    webDriverCommonLibrary.waitForSetTime(ROW_SETTLE_PAUSE_MS);
                }

                // in Step 1 (watch for the slash/spacing in "Commission Agent / Broker").
                page.selectMultipleCommissionDropdownValue(section, "Commission Agent / Broker", rowIndex, row.getAgentBroker());
                page.enterMultipleCommissionFieldValue("Ref. No.", section, rowIndex, row.getRefNo());

                if (isNotBlank(row.getType())) {
                    page.selectMultipleCommissionDropdownValue(section, "Type", rowIndex, row.getType());
                }

                if (isNotBlank(row.getValue())) {
                    page.userEnterInTheMultipleCommissionNumberFieldInTheTable("Value", section, rowIndex, row.getValue());
                }

                if (isNotBlank(row.getValueUnit())) {
                    page.selectCommissionValueUnit(section, rowIndex, row.getValueUnit());
                }

                log.info("COMPLETED COMMISSION ROW = " + rowIndex);
            }
            log.info("========================================");
            log.info("ALL COMMISSION DETAILS COMPLETED");
            log.info("Total JSON Rows = " + rows.size());
            log.info("========================================");

        } catch (Exception e) {
            log.error("ERROR while filling multiple Commission Details", e);
            throw e;
        }
    }

    @Then("verify the validation message {string} is displayed")
    public void verifyTheValidationMessageIsDisplayed(String arg0) {

        WebElement element = driver.findElement(By.xpath("//div[contains(text(), '"+arg0+"')]"));
        webDriverCommonLibrary.explicitWait(driver,element);
        webDriverCommonLibrary.scrollIntoTheView(driver,element);
        webDriverCommonLibrary.highLightTheElement(driver,element);
    }

    @When("the user selects the checkboxes listed in test data {string} under {string}")
    public void theUserSelectsTheCheckboxesListedInTestDataUnder(String path, String group) {
        requireTestData(path);
        for (String label : currentTestData.getList(path)) {
            theUserSelectsTheCheckboxUnder(label, group);
        }
    }

    @When("the user deselects the checkboxes listed in test data {string} under {string}")
    public void theUserDeselectsTheCheckboxesListedInTestDataUnder(String path, String group) {
        requireTestData(path);
        for (String label : currentTestData.getList(path)) {
            theUserDeselectsTheCheckboxUnder(label, group);
        }
    }

    private void requireTestData(String path) {
        if (currentTestData == null) {
            throw new IllegalStateException("'" + path + "' needs test data, but no test data is loaded. "
                    + "Add: Given test data \"<TestCaseId>\" is loaded");
        }
    }

    @When("the user deselects the {string} checkbox under {string}")
    public void theUserDeselectsTheCheckboxUnder(String arg0, String arg1) {
        getCommodityContractManagementPage().deselectCheckbox(arg0);
    }

    @And("Logout")
    public void logout() {
        /** Temporary logout step.Later,this will be configured in Hooksto execute logout before ending the browser session.*/
        getCommodityContractManagementPage().logout();
        log.info("User logged out successfully.");
    }

    @And("select the {string} menu item for the {string}")
    public void selectTheMenuItemForThe(String item, String columnName) {
        item = resolve(item);
        getCommodityContractManagementPage().selectRowMenuItem(item, columnName);
    }

    @When("user selects {string} from the {string} stale dropdown in the {string} section")
    public void userSelectsFromTheStaledropdownInTheSection(String value, String field, String section) {
        value = resolve(value);
        field = resolve(field);
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.selectDropdownValueupdated(section, field, value);

    }

    @Then("verify the mandatory validation indicator is displayed for fields {string}")
    public void verifyTheMandatoryValidationIndicatorIsDisplayedForFields(String fields) {

            String[] fieldNames = fields.split(",");

            for (String field : fieldNames) {

                String fieldName = field.trim();

                By mandatoryLocator = By.xpath( "//span[contains(@class,'mandatory')]/preceding-sibling::span[normalize-space()='"+fieldName+"']");

                webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(mandatoryLocator));

                Assert.assertTrue(webDriverCommonLibrary.isElementDisplayed(mandatoryLocator), "Mandatory validation is not displayed for field: " + fieldName);
            }
    }

    @Then("apply a static wait for the web element for testing purposes only.")
    public void applyAStaticWaitForTheWebElementForTestingPurposesOnly() {
      webDriverCommonLibrary.waitForSetTime(4000);
    }

    @When("user selects {string} from the {string} modified dropdown in the {string} section")
    public void userSelectsFromTheModifiedDropdownInTheSection(String value, String field, String section) {
        value = resolve(value);
        field = resolve(field);
        CommodityContractManagementPage commodityContractManagementPage = getCommodityContractManagementPage();
        commodityContractManagementPage.selectModifiedDropdownValue(section, field, value);
    }

    @Then("verify that the {string} field is {string}.")
    public void verifyThatTheFieldIs(String arg0, String arg1) {
          WebElement element = driver.findElement(By.xpath("//span[normalize-space()='"+arg0+"']/ancestor::div[1]//input['"+arg1+"']"));
          webDriverCommonLibrary.highLightTheElement(driver,element);
    }


    @Then("verify that the {string} field in {string} section with value {string} is {string} and displays {string}")
    public void verifyFieldStateAndValue(String fieldName, String section, String ariaLabel, String expectedState, String expectedValue) {

            expectedState = resolve(expectedState);
            section = resolve(section);
            ariaLabel = resolve(ariaLabel);
            expectedValue = resolve(expectedValue);

            getCommodityContractManagementPage().verifyField(section, fieldName, ariaLabel, expectedState, expectedValue);
    }

    @When("user navigates to the required page")
    public void userNavigatesToTheRequiredPage() {
        driver.navigate().to("https://wepapp-xbsdcloud-dev-001-dta4d9ahhehvdfeb.centralindia-01.azurewebsites.net/#/11/XT1102801");
    }

    @When("user enters {string} in {string} long text area pop up")
    public void userEntersInLongTextAreaPopUp(String value, String label) {
        value = resolve(value);
        try {
            getCommodityContractManagementPage().enterPopupTextTextArea(label, value);
        } catch (Exception e) {
            System.out.println("WARN: skipped popup text entry: " + e.getMessage());
        }
    }

    @And("close Popup Using X Button")
    public void closePopupUsingXButton() {
            By closeButton = By.xpath("//div[contains(@class,'p-dialog')]"
                    + "//button[contains(@class,'p-dialog-header-close') or @aria-label='Close']");
            WebElement close = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(closeButton));
            webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, close);

            // Confirm the popup is actually gone, returns instantly if it already is
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'p-dialog-mask')]")));
        }

    @And("close pop up using X button")
    public void closePopUpUsingXButton() {
        By closeButton = By.cssSelector("timesicon.p-element");
        WebElement close = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(closeButton));
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, close);

        // Confirm the popup is actually gone, returns instantly if it already is
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("timesicon.p-element")));
    }

    @When("the user selects the {string}{string} for the created Purchase Order")
    public void theUserSelectsTheForTheCreatedPurchaseOrder(String arg0, String arg1) {
        WebElement checkboxRoot = driver.findElement(By.xpath( "//span[normalize-space()='"+arg0+"']/preceding::div[@data-pc-name='"+arg1+"']"));
        webDriverCommonLibrary.explicitWait(driver, checkboxRoot);
        webDriverCommonLibrary.scrollIntoTheView(driver, checkboxRoot);
        WebElement checkboxBox = checkboxRoot.findElement(By.cssSelector("div.p-checkbox-box"));
        webDriverCommonLibrary.clickWithJavaScriptExecutor(driver, checkboxBox);
    }

    @When("the user selects Incoterms {string} one by one and verifies {string} is mandatory")
    public void theUserSelectsIncotermsOneByOneAndVerifiesIsMandatory(String incoterms, String fieldName) {
        CommodityContractManagementPage page = PageObjectManager.getCommodityContractManagementPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        for (String value : incoterms.split(",")) {
            String incoterm = value.trim();
            System.out.println("Selecting INCO Term: " + incoterm + " | Verifying mandatory field: " + fieldName);
            page.selectDropdownValueupdated("Delivery Terms", "INCO Terms", incoterm);
            By mandatoryAsteriskLocator = By.xpath("//span[normalize-space(.)='" + fieldName + "']" + "/following::span[normalize-space(.)='*'][1]/..");
            WebElement mandatoryAsterisk = wait.until(ExpectedConditions.visibilityOfElementLocated(mandatoryAsteriskLocator));
            webDriverCommonLibrary.scrollIntoTheView(driver, mandatoryAsterisk);
            webDriverCommonLibrary.highLightTheElement(driver, mandatoryAsterisk);
            Assert.assertTrue(mandatoryAsterisk.isDisplayed(), "Mandatory indicator missing for " + fieldName + " with INCO Term: " + incoterm);
        }
    }

    @When("user selects {string} verify {string} value is displayed")
    public void userSelectsAndVerifiesValueIsDisplayed(
            String commissionTypeKey,
            String expectedUnitKey) throws IOException {

        CommodityContractManagementPage page = getCommodityContractManagementPage();

        String commissionType = resolve(commissionTypeKey);
        String expectedUnit = resolve(expectedUnitKey);

        page.selectMultipleCommissionDropdownValue(
                "Commission Details",
                "Type",
                0,
                commissionType);

        page.verifyMultipleCommissionValueUnit(
                "Commission Details",
                0,
                expectedUnit);

    }
}