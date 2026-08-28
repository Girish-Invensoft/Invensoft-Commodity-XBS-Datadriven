package utils;

import io.cucumber.core.options.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.cucumber.core.options.Constants.*;

public class WebDriverCommonLibrary {

    private final WebDriver driver;

    public WebDriverCommonLibrary(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement explicitWait(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
        //elementtobeclickable covers both isdisplayed() and isenabled().
    }

    public void scrollIntoTheView(final WebDriver driver, final WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"nearest\"});", element);
    }

    public void highLightTheElement(final WebDriver driver, final WebElement ele) {
        JavascriptExecutor jsExe = (JavascriptExecutor) driver;
        scrollIntoTheView(driver, ele);
        String originalStyle = ele.getAttribute("style");
        jsExe.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", ele);
        dynamicWait(ConfigReader.getHighlightWaitMillis());
        jsExe.executeScript("arguments[0].setAttribute('style', arguments[1]);", ele, originalStyle == null ? "" : originalStyle);
    }

    private void dynamicWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickWithJavaScriptExecutor(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public void clickUsingActionClass(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        scrollIntoTheView(driver,element);
        actions.click(element).build().perform();
    }

//    public void scrollToViewInLightningPage(final WebDriver driver, final WebElement element) {
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"nearest\"});", element);
//    }
}