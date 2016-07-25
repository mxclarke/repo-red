package mxc.demo.campus;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FFLoginAccessIntegrationTest extends AbstractCampusWebDriverTest {

	protected WebDriver createWebDriver() {
		return new FirefoxDriver();
	}
}
