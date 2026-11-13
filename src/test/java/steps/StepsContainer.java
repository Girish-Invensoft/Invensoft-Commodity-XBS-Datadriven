package steps;

import hooks.Hooks;
import invensoft.testdata.*;
import io.cucumber.java.PendingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import base.DriverFactory;
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
import utils.*;
import java.io.IOException;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static pages.PageObjectManager.getCommodityContractManagementPage;
import static utils.Constants.ROW_SETTLE_PAUSE_MS;
import static utils.Constants.SETTLE_PAUSE_MS;
import static utils.Constants.WAIT_SHORT_SEC;
import static utils.Constants.setTime;

public class StepsContainer {

    private static final Logger log = LogManager.getLogger(StepsContainer.class);

    private static final String TEST_DATA_JSON = "src/test/resources/testdata/contractTestData.json";

    private String createdContractNumber;
    private LoginPage loginPage;
    private WebDriver driver;
    private WebDriverCommonLibrary webDriverCommonLibrary;
    private ConfigReader configReader;
    private ContractTestData currentTestData;
    private NPEContractTestData npecurrentTestData;

    @Given("test data {string} is loaded")
    public void testDataIsLoaded(String testCaseId) throws IOException {
        TestContext.set(Hooks.TEST_CASE_ID_KEY, testCaseId);
        TestDataReader.reload();
        currentTestData = TestDataReader.getByTestCaseId(testCaseId);
        log.info("Loaded test data: " + testCaseId);
    }

    @Given("NPE test data {string} is loaded")
    public void npeTestDataIsLoaded(String testCaseId) throws IOException {
        npecurrentTestData = TestDataReader.getNpeByTestCaseId(testCaseId);
        log.info("Loaded NPE test data: " + testCaseId);
    }

    // Shared steps (resolve, open browser, checkbox lists) work for both normal and NPE scenarios.
    private boolean hasTestData() {
        return currentTestData != null || npecurrentTestData != null;
    }

    private String dataValue(String path) {
        return (npecurrentTestData != null)
                ? npecurrentTestData.getValue(path)
                : currentTestData.getValue(path);
    }

    // Commission rows for the loaded scenario: NPE data when this test case was loaded with the NPE step,
    // otherwise the normal test data looked up by id.
    private List<CommissionDetailRow> commissionRowsFor(String testCaseId) throws IOException {
        if (npecurrentTestData != null && testCaseId != null
                && testCaseId.equals(npecurrentTestData.getTestCaseId())) {
            return npecurrentTestData.getCommissionDetails();
        }
        return TestDataReader.getByTestCaseId(testCaseId).getCommissionDetails();
    }

    private List<String> dataList(String path) {
        return (npecurrentTestData != null)
                ? npecurrentTestData.getList(path)
                : currentTestData.getList(path);
    }

    private String resolve(String raw) {
        if (raw == null) return null;

        if (raw.startsWith("#")) {
            // "#key" -> properties / runtime value
            String key = raw.substring(1);
            return TestContext.has(key)
                    ? TestContext.get(key)
                    : ConfigReader.getProperty(key);
        }
        if (raw.startsWith("*")) {
            // "*section.field" -> JSON test data for the active test case
            String testCaseId = TestContext.get(Hooks.TEST_CASE_ID_KEY);

            if (testCaseId == null || testCaseId.isBlank()) {
                throw new IllegalStateException(
                        "No test case id set. Use 'test data \"<id>\" is loaded' first.");
            }

            TestDataReader.reload();

            String path = raw.substring(1);
            String value;

            try {
                value = TestDataReader.getValue(testCaseId, path);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Could not read test data for test case '" + testCaseId + "'", e);
            }

            System.out.println("testCaseId = " + testCaseId);
            System.out.println("JSON path = " + path + ", value = " + value);

            if (value == null) {
                throw new IllegalStateException(
                        "No value in JSON for '" + path + "' in test case '"
                                + testCaseId + "'.");
            }

            return value;
        }

        if (!raw.startsWith("@")) return raw;

        if (!hasTestData()) {
            /* existing exception */
        }

        return dataValue(raw.substring(1));
    }

    private String resolveLogin(String arg) {
        if (arg == null) return null;

        // plain key -> value.properties
        if (!arg.startsWith("@") && !arg.startsWith("*")) {
            return ConfigReader.getProperty(arg);
        }

        String key = arg.substring(1);                       // "login.password"
        String shortKey = key.substring(key.lastIndexOf('.') + 1);   // "password"

        // 1. test data first
        try {
            String value = resolve(arg);
            if (value != null && !value.isBlank()) {
                return value;
            }
        } catch (IllegalStateException ignored) {
            // missing/blank/non-text in test data -> fall back
        }

        // 2. value.properties: full key, then last segment
        for (String k : new String[] { key, shortKey }) {
            try {
                String fallback = ConfigReader.getProperty(k);
                if (fallback != null && !fallback.isBlank()) {
                    return fallback;
                }
            } catch (RuntimeException ignored) {
                // key not in properties -> try next
            }
        }

        throw new IllegalStateException("Login credential '" + key
                + "' not found in test data or value.properties");
    }

    @Given("open browser")
    public void openBrowser() {
        // Browser, like the url, comes from the test data ("login.browser") when a test case is loaded.
        String browser = hasTestData() ? dataValue("login.browser") : ConfigReader.getProperty("browser");
        driver = DriverFactory.initializeDriver(browser);
        // Login url comes from the test data when a test case is loaded ("login.url");
        // features that load no test data keep using values.properties.
        String url = hasTestData() ? dataValue("login.url") : ConfigReader.getProperty("url");
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
        log.info("Password entered successfully");

        loginPage.clickLogin();
        log.info("Login button clicked successfully");

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
        System.out.println("its an logistic dashboard");
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

        if ("Product Details".equalsIgnoreCase(arg0)) {
            getCommodityContractManagementPage().highlighth3Containing(arg0);
        } else {
            getCommodityContractManagementPage().highlightSpanContaining(arg0);
        }
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

    @When("use click on {string} button in the {string} section")
    public void clickSectionButton(String buttonText, String section) {
        getCommodityContractManagementPage().clickSectionSearchButton(section, buttonText);
    }

    @When("user enters {string} in {string} text field in the {string} section")
    public void userEntersInTheTextFieldInTheSection(String arg0, String field, String section) {
        arg0 = resolve(arg0);
        getCommodityContractManagementPage().enterTextInSectionField(section, field, arg0);
    }

    @Then("verify that the {string} unit is displayed for {string} in the {string} section")
    public void verifyUnitDisplayedForField(String unit, String field, String section) {
        getCommodityContractManagementPage().verifyTruckTareWeightUnit(section, field, unit);
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
        if (!"Contract No.".equals(arg1) && !"SI No.".equals(arg1) && !"GRN No.".equals(arg1)) {
            throw new IllegalArgumentException(
                    "capture supports 'Contract No.', 'SI No.' and 'GRN No.' columns but got: '" + arg1 + "'");
        }
        webDriverCommonLibrary.waitForSetTime(setTime / 2);

        String contractNo = getCommodityContractManagementPage().getListColumnText(arg1).trim();
        String key = buildKey(arg0, arg1);

        webDriverCommonLibrary.updateTheValueInDataValuePropertiesFile(key, contractNo);
        webDriverCommonLibrary.updateTheValueInJsonFile(key, contractNo);
    }

    @When("the user selects the {string} checkbox under {string}")
    public void theUserSelectsTheCheckboxUnder(String arg0, String arg1) {
        getCommodityContractManagementPage().selectCheckbox(arg0);
    }

    @When("the user selects the SI {string} checkbox under {string}")
    public void theUserSelectsTheSICheckboxUnder(String arg0, String arg1) {
        getCommodityContractManagementPage().selectmodCheckbox(arg0);
    }

    @When("the user Deselects the SI {string} checkbox under {string}")
    public void theUserDeSelectsTheSICheckboxUnder(String arg0, String arg1) {
        getCommodityContractManagementPage().deselectSICheckbox(arg0);
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

    @Given("user fills multiple product details for test case {string}")
    public void userFillsMultipleCcmProductDetailsForTestCase(String testCaseId) throws IOException {
        webDriverCommonLibrary.waitForSetTime(SETTLE_PAUSE_MS);

        ContractTestData data = TestDataReader.getByTestCaseId(testCaseId);
        List<ProductDetailRow> rows = data.getProductDetails();
        if (rows == null || rows.isEmpty()) {
            log.info("No productDetails in test data for '" + testCaseId + "' - skipping Product Details step.");
            return;
        }

        CommodityContractManagementPage page = getCommodityContractManagementPage();
        final String section = "Product Details";
        int uiRowIndex = 0;

        for (int dataRowIndex = 0; dataRowIndex < rows.size(); dataRowIndex++) {
            ProductDetailRow row = rows.get(dataRowIndex);
            if (row == null) {
                throw new IllegalStateException("Product Details test data contains a null row at index " + dataRowIndex + ".");
            }
            if (isBlank(row.getProduct())) {
                log.warn("Skipping product detail row " + dataRowIndex + " because Product is blank.");
                continue;
            }

            if (uiRowIndex > 0) {
                int rowsBefore = page.getProductDetailRows().size();
                page.addProductDetailRow(section);
                int rowsAfter = page.getProductDetailRows().size();
                if (rowsAfter <= rowsBefore) {
                    throw new IllegalStateException("Product Details row was not added. "
                            + "Expected rows > " + rowsBefore + " but found " + rowsAfter);
                }
                webDriverCommonLibrary.waitForSetTime(ROW_SETTLE_PAUSE_MS);
            }

            page.selectMultipleDropdownValue(section, "Product", uiRowIndex, row.getProduct());
            page.userEnterInTheMultipleNumberFieldInTheTable("Units", section, uiRowIndex, row.getUnits());
            page.selectMultipleTableType2DropdownValues(section, "Packing", uiRowIndex, row.getPacking());
            page.userEnterInTheMultipleNumberFieldInTheTable("Qty.", section, uiRowIndex, row.getQty());
            page.selectMultipleDropdownValue(section, "UOM", uiRowIndex, row.getUom());
            page.userEnterInTheMultipleNumberFieldInTheTable("No Of Lots.", section, uiRowIndex, row.getNoOfLots());
            page.userEnterInTheMultipleNumberFieldInTheTable("Price", section, uiRowIndex, row.getPrice());

            if (isNotBlank(row.getTargetType())) {
                page.selectMultipleDropdownValue(section, "Target Type", uiRowIndex, row.getTargetType());
            }
            if (isNotBlank(row.getTargetDateRange())) {
                fillTargetDateRangeFromJson(row.getTargetDateRange(), uiRowIndex);
            }
            if (isNotBlank(row.getTradeMonth())) {
                page.selectMultipleDropdownValue(section, "Trade Month", uiRowIndex, row.getTradeMonth());
            }
            if (isNotBlank(row.getPositionMonth())) {
                page.selectMultipleDropdownValue(section, "Position Month", uiRowIndex, row.getPositionMonth());
            }
            if (isNotBlank(row.getQuality())) {
                page.enterMultipleFieldValue("Quality", section, uiRowIndex, row.getQuality());
            }
//            if (isNotBlank(row.getCertificationPremium())) {
//                page.createCertificationPremium(
//                        section, "Certification & Premium", uiRowIndex,
//                        row.getCertificationPremium(), row.getPremiumAmount());
//            }

            log.info("Completed Product Details row " + uiRowIndex + " for test case " + testCaseId);
            uiRowIndex++;
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

    @Given("user fills multiple grn product details for test case {string}")
    public void userFillsMultipleGRNProductDetailsForTestCase(String testCaseId) throws IOException {
        webDriverCommonLibrary.waitForSetTime(SETTLE_PAUSE_MS);
        try {
            ContractTestData data = TestDataReader.getByTestCaseId(testCaseId);
            List<Map<String, Object>> grnRows = data.getBatchDetails();
            if (grnRows == null || grnRows.isEmpty()) {
                grnRows = data.getGrnProductDetails();
            }
            if (grnRows != null && !grnRows.isEmpty()) {
                fillGrnProductDetails(getCommodityContractManagementPage(), grnRows);
                return;
            }
            throw new IllegalStateException("No GRN product rows were configured for test case '"
                    + testCaseId + "'. Add at least one row to batchDetails.");
        } catch (Exception e) {
            throw e;
        }
    }


    private void fillGrnProductDetails(
            CommodityContractManagementPage page, List<Map<String, Object>> rows) {
        int uiRowIndex = 0;
        for (Map<String, Object> row : rows) {
            if (row == null) {
                throw new IllegalStateException("GRN product details contain a null row.");
            }
            Map<String, Object> rowValues = row;
            String contractSI = grnProductValue(row, "contractSI");
            if (contractSI != null && contractSI.startsWith("#")) {
                rowValues = new java.util.LinkedHashMap<>(row);
                rowValues.put("contractSI", resolve(contractSI));
            }
            for (String requiredField : List.of(
                    "contractSI", "product", "season", "warehouseZone", "units", "packing", "grossQty", "netQty")) {
                if (isBlank(grnProductValue(rowValues, requiredField))) {
                    throw new IllegalStateException("GRN product row " + uiRowIndex
                            + " is missing required test-data value '" + requiredField + "'.");
                }
            }

            if (uiRowIndex > 0) {
                page.addGrnProductDetailRow(uiRowIndex);
            }
            page.fillGrnProductDetailRow(uiRowIndex, rowValues);
            log.info("Completed GRN Product Details row " + uiRowIndex);
            uiRowIndex++;
        }
    }

    private void fillGrnSecondProductDetails(
            CommodityContractManagementPage page, List<Map<String, Object>> rows) {
        int uiRowIndex = 0;
        for (Map<String, Object> row : rows) {
            if (row == null) {
                throw new IllegalStateException("GRN product details contain a null row.");
            }
            Map<String, Object> rowValues = row;
            String contractSI = grnProductValue(row, "contractSI");
            if (contractSI != null && contractSI.startsWith("#")) {
                rowValues = new java.util.LinkedHashMap<>(row);
                rowValues.put("contractSI", resolve(contractSI));
            }
            for (String requiredField : List.of(
                    "contractSI", "product", "season", "warehouseZone", "units", "packing", "grossQty", "netQty")) {
                if (isBlank(grnProductValue(rowValues, requiredField))) {
                    throw new IllegalStateException("GRN product row " + uiRowIndex
                            + " is missing required test-data value '" + requiredField + "'.");
                }
            }

            if (uiRowIndex > 0) {
                page.addGrnProductDetailRow(uiRowIndex);
            }
            page.fillGrnSecondProductDetailRow(uiRowIndex, rowValues);
            log.info("Completed GRN Product Details row " + uiRowIndex);
            uiRowIndex++;
        }
    }

    private String grnProductValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : String.valueOf(value).trim();
    }


    @Given("user fills multiple NPE product details for test case {string}")
    public void userFillsMultipleNPEProductDetailsForTestCase(String testCaseId) throws IOException {
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

            int uiRowIndex = -1;

            for (int i = 0; i < rows.size(); i++) {

                ProductDetailRow row = rows.get(i);
                // Here using 0-based index as per java -GK
                int rowIndex = i;

                if (isBlank(row.getProduct())) {
                    log.warn("Skipping row " + rowIndex + " because Product is blank.");
                    continue;
                }

                // From here on rowIndex means the UI row (0-based), not the JSON position.
                rowIndex = ++uiRowIndex;
                /* First JSON row already exist  * Row 0 -> existing UI row   * Row 1 -> click +  * Row 2 -> click + * etc.    */
                if (rowIndex > 0) {
                    int rowsBefore = page.getProductDetailRows().size();
                    page.addProductDetailRow(section);
                    int rowsAfter = page.getProductDetailRows().size();
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
//                page.userEnterInTheMultipleNumberFieldInTheTable("Price / Differential", section, rowIndex, row.getPrice());

                if (isNotBlank(row.getTargetType())) {
                    page.selectMultipleProductDetailValue(section, "Target Type", rowIndex, row.getTargetType());
                }

//                if (isNotBlank(row.getTradeMonth())) {
//                    page.selectMultipleDropdownValue(section, "Trade Month", rowIndex, row.getTradeMonth());
//                }

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
        } catch (Exception e) {
            throw e;
        }
    }

    @Given("user fills multiple PTBF product details for test case {string}")
    public void userFillsMultiplePTBFProductDetailsForTestCase(String testCaseId) throws IOException {
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
            int uiRowIndex = -1;
            for (int i = 0; i < rows.size(); i++) {
                ProductDetailRow row = rows.get(i);
                // Here using 0-based index as per java -GK
                int rowIndex = i;
                if (isBlank(row.getProduct())) {
                    log.warn("Skipping row " + rowIndex + " because Product is blank.");
                    continue;
                }

                // From here on rowIndex means the UI row (0-based), not the JSON position.
                rowIndex = ++uiRowIndex;
                /* First JSON row already exist  * Row 0 -> existing UI row   * Row 1 -> click +  * Row 2 -> click + * etc.    */
                if (rowIndex > 0) {
                    int rowsBefore = page.getProductDetailRows().size();
                    page.addProductDetailRow(section);
                    int rowsAfter = page.getProductDetailRows().size();
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
                page.userEnterInTheMultipleNumberFieldInTheTable("Differential", section, rowIndex, row.getPrice());

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
        } catch (Exception e) {
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
        getCommodityContractManagementPage().searchRecord("Contract No.", recordnumber);
    }

//    @Then("search for the json created {string} by {string}")
//    public void searchForJsonTheCreatedBy(String arg0, String arg1) {
//        String key = arg0 + arg1;                       // e.g. CommercialCommodityPOContractNumber
//        String recordNumber = TestContext.has(key)
//                ? TestContext.get(key)                  // captured earlier in this run
//                : dataValue("contractDetails.contractNo");   // fall back to the JSON for this TC
//        getCommodityContractManagementPage().searchContractNo(recordNumber);
//    }

    @Then("search for the json created {string} by {string}")
    public void searchForJsonTheCreatedBy(String arg0, String arg1) {
        String normalized = arg1.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        String section = isCaptureDataSection(arg0) ? arg0 : null;
        String column;
        String field;
        switch (normalized) {
            case "contractno":
            case "contractnumber":
                column = "Contract No.";
                field = "contractNo";
                section = section == null ? "contractDetails" : section;
                break;
            case "shippinginstruction":
            case "approvedshippinginstruction":
            case "sino":
            case "sinumber":
                column = "SI No.";
                field = normalized.equals("approvedshippinginstruction")
                        || arg0.toLowerCase().contains("approvedshippinginstruction")
                        ? "approvedShippingInstruction"
                        : "shippingInstruction";
                section = section == null ? "contractShippingDetails" : section;
                break;
            case "grnno":
            case "grnnumber":
            case "grnrecordnumber":
                column = "GRN No.";
                field = normalized.equals("grnno") || normalized.equals("grnnumber")
                        ? (section != null && section.equals("grnBasicDetails") ? "grnNo" : "grnRecordNumber")
                        : "grnRecordNumber";
                section = section == null ? "contractDetails" : section;
                break;
            default:
                column = arg1.trim();
                field = toCamelCaseField(arg1);
                section = section == null ? "contractDetails" : section;
        }

        String key = arg0 + field;
        String recordNumber = TestContext.has(key)
                ? TestContext.get(key)
                : TestContext.has(section + field)
                        ? TestContext.get(section + field)
                        : dataValue(section + "." + field);
        if (recordNumber == null || recordNumber.trim().isEmpty()) {
            throw new IllegalStateException("No value found for '" + section + "." + field
                    + "' while searching the '" + column + "' list column.");
        }

        getCommodityContractManagementPage().searchRecord(column, recordNumber.trim());
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
        switch (arg0.trim().toLowerCase()) {
            case "contract no. & date", "billing location", "counterparty name", "counterparty address" ->
                    getCommodityContractManagementPage().highlightSpanByAriaLabel(arg0.trim(), arg1);
            default ->
                    getCommodityContractManagementPage().highlightSpanByAriaLabel(arg1);
        }
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
            List<CommissionDetailRow> rows = commissionRowsFor(testCaseId);

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

        WebElement element = driver.findElement(By.xpath("//*[contains(text(), '"+arg0+"')]"));
        webDriverCommonLibrary.explicitWait(driver,element);
        webDriverCommonLibrary.scrollIntoTheView(driver,element);
        webDriverCommonLibrary.highLightTheElement(driver,element);
    }

    @When("the user selects the checkboxes listed in test data {string} under {string}")
    public void theUserSelectsTheCheckboxesListedInTestDataUnder(String path, String group) {
        requireTestData(path);
        for (String label : dataList(path)) {
            theUserSelectsTheCheckboxUnder(label, group);
        }
    }

    @When("the user deselects the checkboxes listed in test data {string} under {string}")
    public void theUserDeselectsTheCheckboxesListedInTestDataUnder(String path, String group) {
        requireTestData(path);
        for (String label : dataList(path)) {
            theUserDeselectsTheCheckboxUnder(label, group);
        }
    }

    private void requireTestData(String path) {
        if (!hasTestData()) {
            throw new IllegalStateException("'" + path + "' needs test data, but no test data is loaded. "
                    + "Add: Given test data \"<TestCaseId>\" is loaded (or: Given NPE test data \"<TestCaseId>\" is loaded)");
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
        webDriverCommonLibrary.waitForSetTime(3000);
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
        WebElement checkboxRoot = driver.findElement(By.xpath( "//*[normalize-space()='"+arg0+"']/preceding::div[@data-pc-name='"+arg1+"']"));
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
    public void userSelectsAndVerifiesValueIsDisplayed(String commissionTypeKey, String expectedUnitKey) throws IOException {
        CommodityContractManagementPage page = getCommodityContractManagementPage();
        String agentBroker = resolve("@commissionDetailsValidation.agentBroker");
        String commissionType = resolve(commissionTypeKey);
        String expectedUnit = resolve(expectedUnitKey);

        page.selectMultipleCommissionDropdownValueValidation("Commission Details", "Commission Agent / Broker", 0, agentBroker);
        page.selectMultipleCommissionDropdownValueValidation("Commission Details", "Type", 0, commissionType);

        if (!"Flat Amount".equalsIgnoreCase(commissionType)
                && !"Value".equalsIgnoreCase(commissionType)) {
            throw new IllegalArgumentException("No currency expectation is defined for commission type: " + commissionType);
        }
        page.verifyMultipleCommissionValueUnitValidation("Commission Details", "Value", 0, expectedUnit);
    }

    @Given("user fills {string} multiple product details for test case {string}")
    public void userFillsMultipleProductDetailsForTestCase(String arg0, String testCaseId) throws IOException {
        webDriverCommonLibrary.waitForSetTime(SETTLE_PAUSE_MS);

        NPEContractTestData data = TestDataReader.getNpeByTestCaseId(testCaseId);
        List<NPEProductDetailRow> rows = data.getNPEProductDetails();

        if (rows == null || rows.isEmpty()) {
            return;
        }
        CommodityContractManagementPage page = getCommodityContractManagementPage();
        final String section = "Product Details";
        int uiRowIndex = -1;

        for (int i = 0; i < rows.size(); i++) {
            NPEProductDetailRow row = rows.get(i);

            if (isBlank(row.getProduct())) {
                continue;
            }
            int rowIndex = ++uiRowIndex;
            if (rowIndex > 0) {
                int rowsBefore = page.getProductDetailRows().size();
                page.addProductDetailRow(section);
                int rowsAfter = page.getProductDetailRows().size();
                if (rowsAfter <= rowsBefore) {
                    throw new IllegalStateException("Product Details row was not added. "
                            + "Expected rows > " + rowsBefore + " but found " + rowsAfter);
                }
                webDriverCommonLibrary.waitForSetTime(ROW_SETTLE_PAUSE_MS);
            }

            page.selectMultipleDropdownValue(section, "Product", rowIndex, row.getProduct());
            if (isNotBlank(row.getUnits())) {
                page.userEnterInTheMultipleNumberFieldInTheTable("Units", section, rowIndex, row.getUnits());
            }
            if (isNotBlank(row.getPacking())) {
                page.selectMultipleTableType2DropdownValues(section, "Packing", rowIndex, row.getPacking());
            }
            if (isNotBlank(row.getQty())) {
                page.userEnterInTheMultipleNumberFieldInTheTable("Qty.", section, rowIndex, row.getQty());
            }
            if (isNotBlank(row.getNoOfLots())) {
                page.userEnterInTheMultipleNumberFieldInTheTable("No Of Lots.", section, rowIndex, row.getNoOfLots());
            }

            if (isNotBlank(row.getTargetDateRange())) {
                fillTargetDateRangeFromJson(row.getTargetDateRange(), rowIndex);
            }
            if (isNotBlank(row.getQuality())) {
                page.enterMultipleFieldValue("Quality", section, rowIndex, row.getQuality());
            }
        }
    }
    @Then("Verify step number {string} section {string} displays the mandatory red badge")
    public void verifyMandatoryRedBadge(String stepNo, String sectionName) {
        By expander = By.cssSelector("nav.section-nav button.nav-toggle");
        WebElement toggle = driver.findElement(expander);
        if ("false".equals(toggle.getAttribute("aria-expanded"))) {
            toggle.click();
        }

        By badge = By.xpath("//span[contains(@class,'nav-label') and normalize-space()='" + sectionName + "']"
                + "/parent::*[.//span[contains(@class,'step-no') and normalize-space()='" + stepNo + "']]"
                + "//span[contains(@class,'step-badge')]");

        WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(badge));

        String bg = el.getCssValue("background-color");
        String fg = el.getCssValue("color");
        System.out.println("badge bg=" + bg + ", text color=" + fg + ", text=" + el.getText());

        Assert.assertTrue(isRedHue(bg) || isRedHue(fg),
                "Badge is not red for step " + stepNo + ". bg=" + bg + ", color=" + fg);
        webDriverCommonLibrary.highLightTheElementRed(driver,el);
    }

    // Accepts any red-dominant shade, including light tints like rgb(254,226,226)
    private boolean isRedHue(String rgba) {
        String[] p = rgba.replaceAll("[^0-9,]", "").split(",");
        int r = Integer.parseInt(p[0].trim());
        int g = Integer.parseInt(p[1].trim());
        int b = Integer.parseInt(p[2].trim());
        return r >= 150 && r - g >= 20 && r - b >= 20;
    }
    private boolean isRed(String rgba) {
        String[] p = rgba.replaceAll("[^0-9,]", "").split(",");
        int r = Integer.parseInt(p[0]), g = Integer.parseInt(p[1]), b = Integer.parseInt(p[2]);
        return r > 150 && g < 100 && b < 100;
    }
    @Then("Verify step number {string} section {string} displays the green completed icon")
    public void verifyStepNumberSectionDisplaysTheGreenCompletedIcon(String stepNo, String sectionName) {
        By navItem = By.xpath("//button[contains(@class,'nav-item')]"
                + "[.//span[contains(@class,'step-no') and normalize-space()='" + stepNo + "']]"
                + "[.//span[contains(@class,'nav-label') and normalize-space()='" + sectionName + "']]");

        Duration originalImplicit = driver.manage().timeouts().getImplicitWaitTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement item = wait.until(ExpectedConditions.visibilityOfElementLocated(navItem));

            WebElement icon = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                    item, By.cssSelector("i.step-ok"))).get(0);

            // With implicit wait at zero, these absence checks return instantly
            Assert.assertTrue(item.findElements(By.cssSelector("span.step-badge")).isEmpty(),
                    "Error badge still present for step " + stepNo + " - " + sectionName);
            Assert.assertFalse(item.getAttribute("class").contains("has-error"),
                    "Step " + stepNo + " still has error state");

            String fg = icon.getCssValue("color");
            Assert.assertTrue(isGreenHue(fg), "Icon is not green for step " + stepNo + ". color=" + fg);

            // Instant highlight: one JS call, no sleep, no revert
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.outline='2px solid green';" +
                            "arguments[0].style.backgroundColor='rgba(0,200,0,0.15)';", item);
        } finally {
            driver.manage().timeouts().implicitlyWait(originalImplicit);
        }
    }

    private boolean isGreenHue(String rgba) {
        String[] p = rgba.replaceAll("[^0-9,]", "").split(",");
        int r = Integer.parseInt(p[0].trim());
        int g = Integer.parseInt(p[1].trim());
        int b = Integer.parseInt(p[2].trim());
        return g >= 100 && g - r >= 20 && g - b >= 20;
    }

    @Then("verify that the {string} field is displayed as {string} and disabled")
    public void verifyThatTheFieldIsDisplayedAsAndDisabled(String arg0, String arg1) {
        arg1 = resolve(arg1);
        getCommodityContractManagementPage().highlightSpanByAriaLabel(arg1);
    }

    @Then("verify the {string} mandatory validation indicator is displayed for fields {string}")
    public void verifyTheMandatoryValidationIndicatorIsDisplayedForFields(String arg0, String fields) {
        String[] fieldNames = fields.split(",");
        for (String field : fieldNames) {
            String fieldName = field.trim();
            By mandatoryLocator = By.xpath( "(//span[contains(@class,'mandatory')]/preceding-sibling::span[normalize-space()='"+fieldName+"'])[2]");
            webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(mandatoryLocator));
            Assert.assertTrue(webDriverCommonLibrary.isElementDisplayed(mandatoryLocator), "Mandatory validation is not displayed for field: " + fieldName);
        }
    }

    @Then("capture {string} {string} in json test data")
    public void captureInJsonTestData(String arg0, String arg1) {
        if (!TestContext.has(Hooks.TEST_CASE_ID_KEY)) {
            throw new IllegalStateException("No test case id set. Load test data before capturing a list value.");
        }

        String normalized = arg1.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        String column;
        String section;
        String field;
        switch (normalized) {
            case "contractno":
            case "contractnumber":
                column = "Contract No.";
                field = "contractNo";
                section = "contractDetails";
                break;
            case "shippinginstruction":
            case "approvedshippinginstruction":
            case "sino":
            case "sinumber":
                column = "SI No.";
                field = normalized.equals("approvedshippinginstruction")
                        || arg0.toLowerCase().contains("approvedshippinginstruction")
                        ? "approvedShippingInstruction"
                        : "shippingInstruction";
                section = "contractShippingDetails";
                break;
            case "grnno":
            case "grnnumber":
            case "grnrecordnumber":
                column = "GRN No.";
                field = "grnRecordNumber";
                section = "contractDetails";
                break;
            default:
                column = arg1.trim();
                field = toCamelCaseField(arg1);
                if (field.isEmpty()) {
                    throw new IllegalArgumentException("List column must not be blank.");
                }
                section = "contractDetails";
        }

        if (isCaptureDataSection(arg0)) {
            section = arg0;
        }
        if ((normalized.equals("grnno") || normalized.equals("grnnumber"))
                && section.equals("grnBasicDetails")) {
            field = "grnNo";
        }

        webDriverCommonLibrary.waitForSetTime(setTime / 2);
        String value = getCommodityContractManagementPage().getListColumnText(column).trim();
        if (value.isEmpty()) {
            throw new IllegalStateException("The '" + column + "' list value is blank; nothing was captured.");
        }

        TestContext.set(buildKey(arg0, column), value);
        TestContext.set(section + field, value);
        TestContext.set(arg0 + field, value);

        JsonDataWriter.captureValue(
                TEST_DATA_JSON,
                TestContext.get(Hooks.TEST_CASE_ID_KEY),
                section,
                field,
                value);
        TestDataReader.reload();
        log.info("Captured " + column + " = " + value + " -> " + section + "." + field);
    }

    private boolean isCaptureDataSection(String value) {
        return List.of(
                "contractDetails", "contractShippingDetails", "contractRecords",
                "grnBasicDetails", "grnTruckDetails", "grnProductDetails")
                .contains(value);
    }

    private String toCamelCaseField(String value) {
        StringBuilder field = new StringBuilder();
        boolean capitalizeNext = false;
        for (char character : value.trim().toCharArray()) {
            if (!Character.isLetterOrDigit(character)) {
                capitalizeNext = field.length() > 0;
            } else {
                field.append(capitalizeNext ? Character.toUpperCase(character) : character);
                capitalizeNext = false;
            }
        }
        return field.toString();
    }

    @Then("the {string} {string} should be {string} date")
    public void theShouldBeDate(String section, String field, String dateExpression) {
            webDriverCommonLibrary.verifyDateField(section, field, dateExpression);
    }

    @Then("verify that the {string} field in {string} section with value {string}")
    public void verifyThatTheFieldInSectionWithValue(String fieldName, String section, String expectedValue) {

        section = resolve(section);
        expectedValue = resolve(expectedValue);
        getCommodityContractManagementPage().verifyField(section,fieldName,expectedValue);
    }

    @Then("verify that the checklist {string} is selected")
    public void verifyThatTheChecklistIsSelected(String names) {
      webDriverCommonLibrary.verifyChecklistState(resolve(names), true);
    }

    @Then("verify that the checklist {string} is not selected")
    public void verifyThatTheChecklistIsNotSelected(String names) {
        webDriverCommonLibrary.verifyChecklistState(resolve(names), false);
    }

    @Then("user enters {string} in {string} long text field in the {string} section")
    public void userEntersInLongTextFieldInTheSection(String arg0, String field, String section) {
        arg0 = resolve(arg0);
        getCommodityContractManagementPage().enterTextInSectionLongTextArea(section, field, arg0);

    }

    @When("the user enters {string} in row {string} of the {string} table")
    public void theUserEntersInRowOfTheTable(String arg0, String row, String table) {
        arg0 = resolve(arg0);
        WebElement element = driver.findElement(By.xpath("//table[.//th[normalize-space()='"+table+"']]//tr[td[1][normalize-space()='"+row+"']]//input[@type='text']"));
        webDriverCommonLibrary.scrollIntoTheView(driver,element);
        webDriverCommonLibrary.explicitWait(driver,element);
        element.clear();
        element.sendKeys(arg0);

    }

    @When("the user selects the SI checkboxes listed in test data {string} under {string}")
    public void theUserSelectsTheSICheckboxesListedInTestDataUnder(String path, String group) {
        requireTestData(path);
        for (String label : dataList(path)) {
            theUserSelectsTheSICheckboxUnder(label, group);
        }
    }

    @When("the user deselects the SI checkboxes listed in test data {string} under {string}")
    public void theUserDeselectsTheSICheckboxesListedInTestDataUnder(String path, String group) {
        requireTestData(path);
        for (String label : dataList(path)) {
            theUserDeSelectsTheSICheckboxUnder(label, group);
        }
    }

    @When("the user selects the {string} {string} as {string}")
    public void theUserSelectsTheAs(String section, String field, String dateExpression) {
        webDriverCommonLibrary.selectDate(section, field, dateExpression);
    }

    @When("the user selects {string} {string} as {string}")
    public void theUserSelectsAs(String section, String field, String dateExpression) {
        webDriverCommonLibrary.selectDates(section, field, dateExpression);
    }

    @When("the user selects {string} {string} {string} as {string}")
    public void theUserSelectsReferenceDateAs(
            String section, String referenceField, String dateLabel, String dateExpression) {
        if (!"Date".equalsIgnoreCase(dateLabel)) {
            throw new IllegalArgumentException("Expected date label 'Date' but got '" + dateLabel + "'");
        }
        webDriverCommonLibrary.selectReferenceDate(section, referenceField, dateExpression);
    }



    @When("the user selects the type {string} {string} as {string}")
    public void theUserSelectsTheTypeAs(String section, String field, String dateExpression) {
        webDriverCommonLibrary.selectTheDate(section, field, dateExpression);
    }

    @Then("captures the {string} {string}")
    public void capturesThe(String arg0, String arg1) {
        if (!"+arg1+".equals(arg1)) {
            throw new IllegalArgumentException("capture supports only the 'Contract No.' column but got: '" + arg1 + "'");
        }
        webDriverCommonLibrary.waitForSetTime(setTime / 2);

        String contractNo = getCommodityContractManagementPage().getListColumnText(arg1).trim();
        String key = arg0 + arg1;

        webDriverCommonLibrary.updateTheValueInDataValuePropertiesFile(key, contractNo);
        webDriverCommonLibrary.updateTheValueInJsonFile(key, arg1);
    }

    @Then("captures the {string} {string} into properties and json {string} {string}")
    public void capturesThe(String keyPrefix, String column, String jsonSection, String jsonField) {
        if (!TestContext.has(Hooks.TEST_CASE_ID_KEY)) {
            throw new IllegalStateException("No test case id set. Use 'test data \"<id>\" is loaded' first.");
        }
        webDriverCommonLibrary.waitForSetTime(setTime / 2);

        String value = getCommodityContractManagementPage().getListColumnText(column).trim();
        String key = buildKey(keyPrefix, column);

        // 1) TestContext, so later steps in this run can read it directly
        TestContext.set(key, value);
        TestContext.set(jsonSection + jsonField, value);

        // 2) properties file
        webDriverCommonLibrary.updateTheValueInDataValuePropertiesFile(key, value);

        // 3) JSON test data, at the section and field given in the step
        JsonDataWriter.captureValue(
                TEST_DATA_JSON,
                TestContext.get(Hooks.TEST_CASE_ID_KEY),
                jsonSection,
                jsonField,
                value);
        TestDataReader.reload();   // needs the reload() method in TestDataReader

        log.info("Captured " + key + " = " + value + " -> properties + " + jsonSection + "." + jsonField);
    }

    // "CommercialCommodityPO" + "Contract No." -> "CommercialCommodityPOContractNumber"
    private String buildKey(String prefix, String column) {
        String suffix = column.trim()
                .replaceAll("(?i)\\bNo\\.?$", "Number")
                .replaceAll("[^A-Za-z0-9]", "");
        return prefix + suffix;
    }

    @When("user selects {string} custom radio in {string} section")
    public void userSelectsCustomRadioInSection(String arg0, String arg1) {
     WebElement radio = driver.findElement(By.xpath("//span[normalize-space()='"+arg0+"']/ancestor::label[contains(@class,'custom-radio')]/preceding-sibling::input[@type='radio']"));
     webDriverCommonLibrary.scrollIntoTheView(driver,radio);
     webDriverCommonLibrary.explicitWait(driver,radio);
     radio.click();
    }

    @Given("user fills multiple grn second product details for test case {string}")
    public void userFillsMultipleGrnSecondProductDetailsForTestCase(String testCaseId) throws IOException {
        webDriverCommonLibrary.waitForSetTime(SETTLE_PAUSE_MS);
        try {
            ContractTestData data = TestDataReader.getByTestCaseId(testCaseId);
            List<Map<String, Object>> grnRows = data.getBatchDetails();
            if (grnRows == null || grnRows.isEmpty()) {
                grnRows = data.getGrnProductDetails();
            }
            if (grnRows != null && !grnRows.isEmpty()) {
                fillGrnSecondProductDetails(getCommodityContractManagementPage(), grnRows);
                return;
            }
            throw new IllegalStateException("No GRN product rows were configured for test case '"
                    + testCaseId + "'. Add at least one row to batchDetails.");
        } catch (Exception e) {
            throw e;
        }
    }

    @Then("Click on remove row buttton in the {string} section")
    public void clickOnRemoveRowButttonInTheSection(String arg0) {
      WebElement button = driver.findElement(By.xpath("(//button[contains(@class,'p-button-danger')][.//span[contains(@class,'pi-times')]])[2]"));
      button.click();
    }
}
