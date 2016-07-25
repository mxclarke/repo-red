package mxc.demo.campus;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FFStudentIntegrationTest extends AbstractStudentWebDriverTest {
	
	protected WebDriver createWebDriver() {
		return new FirefoxDriver();
	}
}