package mxc.demo.campus;

import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Functional tests using Selenium.
 * TODO: extend this class for all your tests, then further extend as concrete classes for
 * each of the browsers you want to support.
 * 
 * @WebIntegrationTest enables this test class to launch the app, so you don't
 * have to run it separately. See here: 
 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications
 *
 *Also if you want to use mockito
 *http://stackoverflow.com/questions/24431427/multiple-runwith-statements-in-junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CampusApplication.class)
@WebIntegrationTest(value = "server.port=9000")
@TestPropertySource(locations="classpath:test.properties")
public abstract class AbstractWebDriverTest {
	
	protected abstract WebDriver getWebDriver();
}
