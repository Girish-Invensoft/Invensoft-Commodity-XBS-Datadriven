package hooks;

import base.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.WebDriverCommonLibrary;

public class Hooks {

    // The test data validator (@BeforeSuite) now lives in runners.TestRunner,
    // because TestNG does not run @BeforeSuite from a Cucumber glue class.

    private static final String WATERMARK_TEXT = "XBS Automation ᴳᴷ | © 2026";

    @After
    public void tearDown(Scenario scenario) {
        if (!scenario.isFailed()) {
            return;
        }
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }
        try {
            // Watermark (Invensoft logo + text) first, so it appears in the screenshot
            new WebDriverCommonLibrary(driver).addWatermark(driver, WATERMARK_TEXT);
        } catch (Exception e) {
            System.out.println("Watermark skipped: " + e.getMessage());
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        } catch (Exception e) {
            System.out.println("Screenshot skipped: " + e.getMessage());
        }
    }
}