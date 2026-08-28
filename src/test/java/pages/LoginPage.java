package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WebDriverCommonLibrary;

public class LoginPage {

    private final WebDriverCommonLibrary webDriverCommonLibrary;
    private final WebDriver driver;

    @FindBy(id = "username")
    private WebElement txt_Username;
    @FindBy(id = "password")
    private WebElement txt_Password;
    @FindBy(xpath = "//button[normalize-space()='Login']")
    private WebElement btn_Login;
    @FindBy(xpath = "//span[normalize-space()='Search']/preceding::span[normalize-space()='Dashboard'][1]")
    private WebElement field_dashboardHeader;

    public LoginPage(WebDriver loginDriver) {
        this.driver = loginDriver;
        this.webDriverCommonLibrary = new WebDriverCommonLibrary(driver);
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        webDriverCommonLibrary.explicitWait(driver,txt_Username);
        txt_Username.clear();
        txt_Username.sendKeys(username);
    }

    public void enterPassword(String password) {
        webDriverCommonLibrary.explicitWait(driver,txt_Password);
        txt_Password.clear();
        txt_Password.sendKeys(password);
    }

    public void clickLogin() {
        webDriverCommonLibrary.explicitWait(driver,btn_Login);
        btn_Login.click();
    }

    public boolean isDashboardDisplayed() {
        try {
            return webDriverCommonLibrary.explicitWait(driver,field_dashboardHeader).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}