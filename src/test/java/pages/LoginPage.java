package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WebDriverCommonLibrary;

import java.time.Duration;

public class LoginPage {

    private static final Duration LOGIN_WAIT = Duration.ofSeconds(45);

    private final WebDriverCommonLibrary webDriverCommonLibrary;
    private final WebDriver driver;

    @FindBy(id = "username")
    private WebElement txt_Username;
    @FindBy(id = "password")
    private WebElement txt_Password;
    @FindBy(xpath = "//button[normalize-space()='Sign in']")
    private WebElement btn_Login;
    @FindBy(xpath = "//span[normalize-space()='Search']/preceding::span[normalize-space()='Dashboard'][1]")
    private WebElement field_dashboardHeader;

    public LoginPage(WebDriver loginDriver) {
        this.driver = loginDriver;
        this.webDriverCommonLibrary = new WebDriverCommonLibrary(driver);
        PageFactory.initElements(driver, this);
    }


    public void enterUsername(String username) {
        enterTextWhenAvailable(By.id("username"), username, "username");
    }

    public void enterPassword(String password) {
        By passwordLocator = By.cssSelector(
                "input#password, input[name='password'], input[type='password'], input[autocomplete='current-password']");
        enterTextWhenAvailable(passwordLocator, password, "password");
    }

    private void enterTextWhenAvailable(By locator, String value, String fieldName) {
        WebDriverWait wait = new WebDriverWait(driver, LOGIN_WAIT);
        wait.ignoring(StaleElementReferenceException.class);
        wait.ignoring(ElementNotInteractableException.class);
        wait.withMessage("Timed out entering the " + fieldName + " on the login page.");

        try {
            wait.until(webDriver -> {
                for (WebElement candidate : webDriver.findElements(locator)) {
                    if (candidate.isDisplayed() && candidate.isEnabled()) {
                        candidate.clear();
                        candidate.sendKeys(value);
                        return true;
                    }
                }
                return false;
            });
        } catch (TimeoutException e) {
            throw new TimeoutException(
                    "Could not enter the " + fieldName + " within " + LOGIN_WAIT.toSeconds()
                            + " seconds. Current URL: " + driver.getCurrentUrl()
                            + ", page title: " + driver.getTitle()
                            + ". Check whether the login form is ready or the field is inside an iframe.",
                    e);
        }
    }

    public void clickLogin() {
        By loginButton = By.xpath("//button[normalize-space()='Sign in']");
        WebDriverWait wait = new WebDriverWait(driver, LOGIN_WAIT);
        wait.ignoring(StaleElementReferenceException.class);
        wait.ignoring(ElementNotInteractableException.class);
        wait.withMessage("Timed out waiting to click the Sign in button.");

        try {
            wait.until(webDriver -> {
                WebElement button = ExpectedConditions.elementToBeClickable(loginButton).apply(webDriver);
                if (button == null) {
                    return false;
                }
                ((JavascriptExecutor) webDriver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'center'});", button);
                button.click();
                return true;
            });
        } catch (TimeoutException e) {
            throw new TimeoutException(
                    "Could not click the Sign in button within " + LOGIN_WAIT.toSeconds()
                            + " seconds. Current URL: " + driver.getCurrentUrl()
                            + ", page title: " + driver.getTitle(),
                    e);
        }
    }

    public boolean isDashboardDisplayed() {
        try {
            return webDriverCommonLibrary.explicitWait(driver,field_dashboardHeader).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}