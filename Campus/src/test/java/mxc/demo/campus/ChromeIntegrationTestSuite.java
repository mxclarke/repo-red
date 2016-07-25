package mxc.demo.campus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all integration tests with Chrome.
 * WARNING: you need to have Chromium's bridging driver downloaded for this, and also
 * set its path. I've hard-coded the path for convenience, but you could set it to
 * be on the system path instead.
 * @see CampusTestSuite
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	ChromeLoginAccessIntegrationTest.class
	, ChromeStudentIntegrationTest.class
})
public class ChromeIntegrationTestSuite {
	
	public static final String PATH_TO_CHROME_DRIVER = "c://Apps/chromium/chromedriver.exe";
}
