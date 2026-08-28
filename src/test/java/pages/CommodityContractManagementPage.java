package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WebDriverCommonLibrary;

public class CommodityContractManagementPage {

    WebDriver driver;
    WebDriverCommonLibrary webDriverCommonLibrary;

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
}