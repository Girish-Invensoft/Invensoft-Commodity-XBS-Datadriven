package pages;

import base.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.WebDriverCommonLibrary;

import static base.DriverFactory.driver;

public class PageObjectManager {
    private static CreateCommodityContractPage createCommodityContractPage;
    private static CommodityContractManagementPage commodityContractManagementPage;

    public static CreateCommodityContractPage getCreateCommodityContractPage() {
        if (createCommodityContractPage == null) {
            createCommodityContractPage = new CreateCommodityContractPage(
                    DriverFactory.getDriver(),
                    new WebDriverCommonLibrary(DriverFactory.getDriver())
            );
        }
        return createCommodityContractPage;
    }

    public static CommodityContractManagementPage getCommodityContractManagementPage() {
        if (commodityContractManagementPage == null) {
            commodityContractManagementPage = new CommodityContractManagementPage(DriverFactory.getDriver());
        }
        return commodityContractManagementPage;
    }

}