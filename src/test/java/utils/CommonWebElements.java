package utils;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CommonWebElements {

    @FindBy(xpath = "//span[normalize-space(.)='Yes']")
    private WebElement btn_Yes;
}
