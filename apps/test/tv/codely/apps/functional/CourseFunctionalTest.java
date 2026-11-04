package tv.codely.apps.functional;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseFunctionalTest {

    WebDriver driver;

    @BeforeEach
    void setup() {
        driver = new FirefoxDriver();
    }

    @Test
    void create_valid_course() {
        driver.get("http://localhost:8041/courses");

        int coursesBefore =
            getCoursesCount(driver);

        WebElement name =
            driver.findElement(By.name("name"));

        WebElement duration =
            driver.findElement(By.name("duration"));

        WebElement createButton =
            driver.findElement(
                By.cssSelector("button[type='submit']")
            );

        name.sendKeys("Curso Selenium");
        duration.sendKeys("5 days");

        createButton.click();

        WebDriverWait wait =
            new WebDriverWait(
                driver,
                Duration.ofSeconds(5)
            );

        wait.until(d ->
            getCoursesCount(d) == coursesBefore + 1
        );

        int coursesAfter =
            getCoursesCount(driver);

        assertEquals(
            coursesBefore + 1,
            coursesAfter
        );
    }

    @Test
    void not_create_course_with_empty_name() {
        driver.get("http://localhost:8041/courses");

        int coursesBefore =
            getCoursesCount(driver);

        WebElement duration =
            driver.findElement(By.name("duration"));

        WebElement createButton =
            driver.findElement(
                By.cssSelector("button[type='submit']")
            );

        duration.sendKeys("5 days");

        createButton.click();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int coursesAfter =
            getCoursesCount(driver);

        assertEquals(
            coursesBefore,
            coursesAfter
        );
    }

    @Test
    void filter_existing_course_by_name() {
        driver.get("http://localhost:8041/courses");

        driver.findElement(
            By.id("add-field-button")
        ).click();

        Select field =
            new Select(
                driver.findElement(
                    By.name("filters[0][field]")
                )
            );

        Select operator =
            new Select(
                driver.findElement(
                    By.name("filters[0][operator]")
                )
            );

        WebElement value =
            driver.findElement(
                By.name("filters[0][value]")
            );

        field.selectByValue("name");
        operator.selectByValue("=");

        value.sendKeys("Java");

        driver.findElement(
            By.id("filter-button")
        ).click();

        WebDriverWait wait =
            new WebDriverWait(
                driver,
                Duration.ofSeconds(5)
            );

        wait.until(d -> {
            List<WebElement> rows =
                d.findElements(
                    By.cssSelector("#courses-list tr")
                );

            return rows.stream()
                .anyMatch(row ->
                    !row.getText().isBlank()
                );
        });

        List<WebElement> rows =
            driver.findElements(
                By.cssSelector("#courses-list tr")
            );

        boolean foundJava = false;

        for (WebElement row : rows) {
            String text = row.getText();

            if (!text.isBlank()) {
                assertTrue(
                    text.contains("Java")
                );

                foundJava = true;
            }
        }

        assertTrue(foundJava);
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    private int getCoursesCount(
        WebDriver driver
    ) {
        String text =
            driver.findElement(By.tagName("body"))
                  .getText();

        Pattern pattern =
            Pattern.compile(
                "cuenta con (\\d+) cursos"
            );

        Matcher matcher =
            pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(
                matcher.group(1)
            );
        }

        return -1;
    }
}
