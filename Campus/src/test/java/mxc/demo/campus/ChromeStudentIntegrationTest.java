package mxc.demo.campus;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeStudentIntegrationTest extends AbstractStudentWebDriverTest {
	
	protected WebDriver createWebDriver() {
		  // Optional, if not specified, WebDriver will search your path for chromedriver.
		  System.setProperty("webdriver.chrome.driver", ChromeIntegrationTestSuite.PATH_TO_CHROME_DRIVER);

		  WebDriver driver = new ChromeDriver();
		  
		  return driver;

	}
}
