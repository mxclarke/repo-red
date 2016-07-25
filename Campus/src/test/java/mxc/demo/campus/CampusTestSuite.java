package mxc.demo.campus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import mxc.demo.campus.repositories.TestUserRepo;
import mxc.demo.campus.validator.PatternTest;

/**
 * Unit and integration test suites.
 * 
 * Test suite runs Firefox integration tests only, since other browsers require
 * bridging drivers to be downloaded.
 * 
 * @see ChromeIntegrationTestSuite
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	PatternTest.class
	, TestUserRepo.class
	, FFLoginAccessIntegrationTest.class
	, FFStudentIntegrationTest.class
})
public class CampusTestSuite {
}
