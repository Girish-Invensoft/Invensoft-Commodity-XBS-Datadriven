package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import org.testng.annotations.Test;
import utils.RetryAnalyzer;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps","hooks"},
        tags = "@XBS_Commodity_Contract_Management_Flow_With_Approver_Modify_Amendment",
        plugin = {"pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @Test(groups = "cucumber",
            description = "Runs Cucumber Scenarios",
            dataProvider = "scenarios",
            retryAnalyzer = RetryAnalyzer.class)
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) {
        super.runScenario(pickleWrapper, featureWrapper);
    }
}