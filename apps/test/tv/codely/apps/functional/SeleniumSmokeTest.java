package tv.codely.apps.functional;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumSmokeTest {

    @Test
    void open_backoffice_home_page() {
        WebDriver driver = new FirefoxDriver();

        try {
            driver.get("http://localhost:8041");

            System.out.println(
                "TITULO REAL: " + driver.getTitle()
            );
        } finally {
            driver.quit();
        }
    }
}
