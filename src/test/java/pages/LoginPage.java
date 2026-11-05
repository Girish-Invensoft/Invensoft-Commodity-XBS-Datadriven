package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.WebDriverCommonLibrary;

import java.time.Duration;

public class LoginPage {

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
        By usernameLocator = By.id("username");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement usernameField = wait.until(webDriver -> {
                    for (WebElement candidate : webDriver.findElements(usernameLocator)) {
                        if (candidate.isDisplayed() && candidate.isEnabled()) {
                            return candidate;
                        }
                    }
                    return null;
                });
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", usernameField);
                usernameField.click();
                usernameField.clear();
                usernameField.sendKeys(username);
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                if (attempt == 3) {
                    throw e;
                }
            }
        }
    }

    public void enterPassword(String password) {
        By passwordLocator = By.id("password");
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                webDriverCommonLibrary.explicitWait(driver, txt_Password);
                WebElement passwordField = driver.findElement(passwordLocator);
                passwordField.clear();
                passwordField.sendKeys(password);
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == 3) {
                    throw e;
                }
            }
        }
    }


    public void clickLogin() {
        By loginButton = By.xpath("//button[@type='submit' and @label='Sign in']");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        // 1. Wait until the page is fully loaded
        wait.until(webDriver -> "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
        // 2. Wait until the login button is visible
        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));

        // 3. Scroll button into the center of the viewport
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", button);

        // 4. Wait until the button is enabled
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));

        // 5. Check whether another element is covering the button
        wait.until(webDriver -> {WebElement element = webDriver.findElement(loginButton);

            return (Boolean) ((JavascriptExecutor) webDriver).executeScript(
                    "const rect = arguments[0].getBoundingClientRect();" +
                            "const x = rect.left + rect.width / 2;" +
                            "const y = rect.top + rect.height / 2;" +
                            "const topElement = document.elementFromPoint(x, y);" +
                            "return topElement === arguments[0] || " +
                            "arguments[0].contains(topElement);",
                    element);
        });

        // 6. Perform normal Selenium click
        button = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        button.click();
    }

    public boolean isDashboardDisplayed() {
        try {
            return webDriverCommonLibrary.explicitWait(driver,field_dashboardHeader).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}