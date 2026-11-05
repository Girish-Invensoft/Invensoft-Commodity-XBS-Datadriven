package hooks;

import base.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.WebDriverCommonLibrary;
import io.cucumber.java.Before;
import utils.TestContext;
import io.cucumber.java.Before;
import utils.TestContext;

public class Hooks {

    // The test data validator (@BeforeSuite) now lives in runners.TestRunner,
    // because TestNG does not run @BeforeSuite from a Cucumber glue class.

    private static final String WATERMARK_TEXT = "Automation ᴳᴷ | © 2026";
    public static final String TEST_CASE_ID_KEY = "currentTestCaseId";

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

    @Before(order = 0)
    public void captureTestCaseId(Scenario scenario) {
        System.out.println("Scenario tags: " + scenario.getSourceTagNames());

        TestContext.clear();   // clear first, then set, in the same method
        scenario.getSourceTagNames().stream()
                .filter(tag -> tag.startsWith("@TC_"))
                .findFirst()
                .ifPresent(tag -> TestContext.set(TEST_CASE_ID_KEY, tag.substring(1)));
    }
}