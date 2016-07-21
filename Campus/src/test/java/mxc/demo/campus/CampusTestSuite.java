package mxc.demo.campus;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import mxc.demo.campus.repositories.TestUserRepo;
import mxc.demo.campus.validator.PatternTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	PatternTest.class
	, TestUserRepo.class
	, WebDriverIntegrationTest.class
})
public class CampusTestSuite {
}
