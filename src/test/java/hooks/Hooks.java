package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import base.DriverFactory;
import utils.WebDriverCommonLibrary;

import static base.DriverFactory.driver;

public class Hooks {

    WebDriverCommonLibrary webDriverCommonLibrary;
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
          webDriverCommonLibrary.addWatermark(driver, "ERP Automationᴳᴷ | © 2026");
        }
    }

    @Attachment(value = "Page source", type = "text/plain")
    public String attachPageSource() {
        return DriverFactory.getDriver().getPageSource();
    }

}
