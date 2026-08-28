package steps;

import base.DriverFactory;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import pages.CommodityContractManagementPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.WebDriverCommonLibrary;

public class StepsContainer {

    private LoginPage loginPage;
    private WebDriver driver;
    private final WebDriverCommonLibrary webDriverCommonLibrary = new WebDriverCommonLibrary(driver);

    @Given("open browser")
    public void openBrowser() {
        driver = DriverFactory.initializeDriver(ConfigReader.getProperty("browser"));
        String url = ConfigReader.getProperty("url");
        System.out.println("================================");
        System.out.println("Launching application");
        System.out.println("Browser: " + ConfigReader.getProperty("browser"));
        System.out.println("URL: " + url);
        System.out.println("================================");
        driver.get(url);
        driver.manage().window().maximize();
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        loginPage = new LoginPage(driver);
    }

    @When("enter the user name as {string}")
    public void enterTheUserNameAs(String arg0) {
        String username = ConfigReader.getProperty(arg0);
        loginPage.enterUsername(username);
    }

    @When("enter the user password as {string}")
    public void enterTheUserPasswordAs(String arg0) {
        String password = ConfigReader.getProperty(arg0);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        System.out.println("After Login URL: " + driver.getCurrentUrl());
        System.out.println("After Login Title: " +driver.getTitle());

    }

    @Then("the user should be navigated to the dashboard")
    public void theUserShouldBeNavigatedToTheDashboard() {
            boolean isDashboardVisible = loginPage.isDashboardDisplayed();
            Assert.assertTrue(isDashboardVisible, "Login did not land on the dashboard as expected");
    }

    @And("verify {string} section is displayed on the dashboard page")
    public void verifyFieldIsDisplayingInLightningPage(String arg0) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//div[contains(@class,'content') and contains(@class,'sidenav-content')]//*[contains(text(),'" +arg0+ "')]")));
    }

    @Then("verify {string} is displayed in the dashboard sidebar menu")
    public void verifyIsDisplayedInTheDashboardSidebarMenu(String arg0) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//li[@routerlinkactive='active' and contains(@id,'"+arg0+"')]")));
    }

    @When("user navigates to {string} dashboard sidebar menu")
    public void userNavigatesToDashboardSidebarMenu(String arg0) {
        WebElement menuelement = driver.findElement(By.xpath("//li[@routerlinkactive='active' and contains(@id,'"+arg0+"')]"));
        menuelement.click();
    }

    @When("user clicks on {string} section on the dashboard page")
    public void userClicksOnSectionOnTheDashboardPage(String arg0) {
        WebElement element =driver.findElement(By.xpath("//div[contains(@class,'content') and contains(@class,'sidenav-content')]//*[contains(text(),'"+arg0+"')]"));
        element.click();
    }

    @Then("verify {string}{string} button is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyButtonIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//p-button[contains(@icon,'pi pi-" + arg1 + "')]//button")));

    }

    @Then("verify {string}{string} prime icons is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyPrimeIconsIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//p-button[contains(@icon,'pi pi-" + arg1 + "')]//button")));

    }

    @Then("verify {string}{string} boot strap icon is displayed on the Commercial Commodity Contract Management dashboard")
    public void verifyBootStrapIconIsDisplayedOnTheCommercialCommodityContractManagementDashboard(String arg0, String arg1) {
        webDriverCommonLibrary.highLightTheElement(driver, driver.findElement(By.xpath("//p-button[contains(@icon,'" + arg1 + "')]//button")));

    }

    @When("clicks on {string} {string} icon on the Commodity Contract Management dashboard")
    public void clicksOnIconOnTheCommodityContractManagementDashboard(String arg0, String arg1) {
    WebElement element = driver.findElement(By.xpath("//p-button[contains(@icon,'pi pi-" + arg1 + "')]//button"));
    element.click();
    }

    @Then("verifies that the {string} popup is displayed")
    public void verifiesThatThePopupIsDisplayed(String arg0) {
        CommodityContractManagementPage commodityContractManagementPage = new CommodityContractManagementPage(DriverFactory.getDriver());
         commodityContractManagementPage.getCreateGeneralPopupTitle();
    }

    @Then("verifies that the {string} section is displayed")
    public void verifiesThatTheSectionIsDisplayed(String arg0) {
        WebElement element = driver.findElement(By.xpath("//span[contains(normalize-space(),'" + arg0 + "')]"));
        webDriverCommonLibrary.highLightTheElement(driver, element);    }

    @And("select {string} dropdown value as {string}")
    public void selectDropdownValueAs(String arg0, String arg1) {
        CommodityContractManagementPage commodityContractManagementPage = new CommodityContractManagementPage(DriverFactory.getDriver());
        commodityContractManagementPage.selectClassDropdown(arg0,arg1);
    }
}
