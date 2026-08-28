package invensoft;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.ConfigReader;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println(ConfigReader.getProperty("browser"));
        System.out.println(ConfigReader.getProperty("url"));

    }
}
