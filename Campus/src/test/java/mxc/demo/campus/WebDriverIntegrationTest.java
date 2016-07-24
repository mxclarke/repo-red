package mxc.demo.campus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mxc.demo.campus.domain.UserRole;

/**
 * Functional tests using Selenium.
 * 
 * NOTE: this test only works because I had to remove Selenium from test scope in the pom, 
 * i.e. since Firefox updated to version 47. The FF update required an update of Selenium 
 * to version 2.53.1. However, Selenium 2.53.1 has a dependency on a newer version of the 
 * artifact xml-apis than had already been pulled in by spring-boot-starter-data-jpa, 
 * i.e. 1.0.b2. An attempt to simply exclude the 1.0.b2 version from the maven-failsafe-plugin 
 * didn't work. An attempt to exclude 1.0.b2 from spring-boot-starter-data-jpa then reintroduce 
 * as a specific dependency 1.4.01 also didn't work.
 * To complicate things, Selenium requires xml-apis:1.4.01, yet xml-apis:2.0.2
 * exists. 
 * 
 * Another issue is that Chrome/IE seem to require the download of special drivers. I have not
 * yet tried this out.
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
public class WebDriverIntegrationTest {
	
	private static final String BASE_URL = "http://localhost:9000";
	private static final String LOGIN_URL = BASE_URL + "/login";
	private static final String ACCESS_DENIED_URL = BASE_URL + "/accessdenied";
	private static final String STUDENT_URL = BASE_URL + "/student";
	private static final String LECTURER_URL = BASE_URL + "/lecturer";
	private static final String ADMIN1_URL = BASE_URL + "/admin";
	private static final String ADMIN2_URL = BASE_URL + "/admin/secondPage";
	private static final String STUDENT_VIEW_A_URL = STUDENT_URL + "/1";

	private static final String TITLE_TAG = "title";
	
	private static final String HOME_TITLE = "Campus app";
	private static final String LOGIN_TITLE = "Login form";
	private static final String STUDENT_VIEW_TITLE = "Student";
	private static final String STUDENT_EDIT_TITLE = "Student edit";
	private static final String LECTURER_VIEW_TITLE = "Lecturer";
	private static final String ADMIN1_VIEW_TITLE = "Administration";
	private static final String ADMIN2_VIEW_TITLE = "Administration Details";
	private static final String ACCESS_DENIED_TITLE = "Campus Access Denied";
	
	private static final String PAGE_HEADING_ID = "mxcHeaderId";
	private static final String PAGE_HEADING_LOGOUT_ID = "mxcHeaderLogoutButton";
	//private static final String PAGE_HEADING_LOGOUT_CSS = ".mxcHeaderButton input";
	private static final String PAGE_HEADING_LOGOUT_BN_ID = "mxcHeaderBn";
	private static final String PAGE_HEADING_USER_DETAILS_CLASS = "mxcHeaderArea"; //"mxcHeaderUserDetails";
	private static final String PAGE_HEADING_USER_CLASS = "mxcHeaderUserArea";
	private static final String PAGE_HEADING_USER_NAME_ID = "mxcHeaderUsername";
	private static final String PAGE_HEADING_USER_ROLES_ID = "mxcHeaderRole";
	
	private static final String STUDENT_EDIT_BN_CSS = "form input[value=\"Edit details\"";
	private static final String STUDENT_SAVE_BN_CSS = "form input[value=\"Save\"";
	private static final String STUDENT_PASSWORD_BN_CSS = "form input[value=\"Change password\"]";
	

//	private static final String PAGE_HOME_STUDENT_LINK_ID = "student";
//	private static final String PAGE_HOME_LECTURER_LINK_ID = "lecturer";
//	private static final String PAGE_HOME_ADMIN_LINK_ID = "admin";
	
//	private static final String PAGE_ADMIN_PAGE2_LINK_ID = "someotheradminpage";
	
	private static final String PAGE_LOGIN_INPUT_USERNAME_ID = "username";
	private static final String PAGE_LOGIN_INPUT_PASSWORD_ID = "password";
	private static final String PAGE_LOGIN_SUBMIT_CSS = ".mxcLoginSubmit input";
	private static final String PAGE_LOGIN_INVALID_MSG_CLASS = "mxcLoginInvalidText";
	private static final String PAGE_LOGIN_SUCCESSFUL_LOGOUT_MSG_CLASS = "mxcLogoutSuccessful";
	
	private static final String USER1_USERID = "jash"; // not enrolled
	private static final String USER1_PASSWORD = "pwjash";
	private static final String USER2_USERID = "aash"; // enrolled in at least 1 course
	private static final String USER2_PASSWORD = "pwaash";
	private static final String USER3_USERID = "jebs"; // enrolled
	private static final String USER3_PASSWORD = "pwjebs";
	private static final String ADMIN_USERID = "adminUser";
	private static final String ADMIN_PASSWORD = "admin";


	private WebDriver driver;
	private WebDriverWait wait;
		
	@Before
	public void setup() {
		driver = new FirefoxDriver();
		//driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 20);
	}
	
	@After
	public void tearDown() {
		if ( driver != null )
			driver.quit();
	}
	
	private static final String getEditingStudentEnrolledCourseCss(String courseName) {
		String css = "tr td input[value=\"" + courseName + "\"]";
		return css;
	}
	
	/**
	 * Tests that an anonymous user can access home and the login page, but
	 * no others.
	 */
	//@Ignore
	@Test
	public void testAnonymousAccess() {
		
		System.out.println("Attempting to test anon user");
		driver.get(BASE_URL);
		verifyPage(driver, wait, HOME_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		// Tries to go to admin page. Should be presented w login screen.
		driver.get(ADMIN1_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(STUDENT_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(STUDENT_URL + "/2");
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(BASE_URL);
		verifyPage(driver, wait, HOME_TITLE);
		verifyNobodyLoggedIn(driver, wait);
	}
	
	/**
	 * Tests the custom login handler, i.e. users are taken to appropriate
	 * landing page after login.
	 */
	//@Ignore
	@Test
	public void testLogin() {
		driver.get(STUDENT_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		// Student logs in.
		login(driver, wait, USER1_USERID, USER1_PASSWORD); // should go to studentview
        verifyPage(driver, wait, STUDENT_VIEW_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
        
        // They sign out - should take them to login page
        logout(driver, wait);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // Admin logs in.
        login(driver, wait, ADMIN_USERID, ADMIN_PASSWORD);
        verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
        verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin); 
        
        // They sign out - should take them to login page
        logout(driver, wait);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
 
	}
	
	/**
	 * Tests invalid logins.
	 */
	//@Ignore
	@Test
	public void testBogusLogin() {
		
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
        login(driver, wait, USER1_USERID, "wrongpassword"); 
        // should go to login page with invalid message
        verifyInvalidLogin(driver, wait);
        verifyNobodyLoggedIn(driver, wait);
        
        // Try to access page anyway
        driver.get(STUDENT_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // Check empty login (submit button should really be disabled in this event).
        login(driver, wait, "", ""); 
        // should go to login page with invalid message
        verifyInvalidLogin(driver, wait);
        verifyNobodyLoggedIn(driver, wait);
        
	}
	
	/**
	 * Tests what happens if somebody is logged in, they go to the login page and
	 * a) try to log in again
	 * b)  try to log in as someone else
	 * We expect that logging in again will have no effect (i.e. they are still logged
	 * in as before).
	 * We expect that logging in as someone else will result in a successful login, and
	 * the person will be effectively logged out on that browser.
	 *
	 */
	//@Ignore
	@Test
	public void testDoubleLogin() {
        driver.get(ADMIN1_URL);
       verifyPage(driver, wait, LOGIN_TITLE);
        
        login(driver, wait, ADMIN_USERID, ADMIN_PASSWORD);
        verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
        verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin); 
        
        driver.get(LOGIN_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        login(driver, wait, ADMIN_USERID, ADMIN_PASSWORD);
        // SHould have no effect -- still logged in and goes to admin view
        verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
        //verifyPage(driver, wait, HOME_TITLE);
        verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin); 
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
        verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
        
        // Now go back to login and try to log in as some student
        driver.get(LOGIN_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        login(driver, wait, USER1_USERID, USER1_PASSWORD);
        // SHould be logged in as the new user, and on the student page
        verifyPage(driver, wait, STUDENT_VIEW_TITLE );
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
        // ANd can't get at the admin page
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, ACCESS_DENIED_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
        // BUt should be able to access ordinary page
        driver.get(STUDENT_URL);
        verifyPage(driver, wait, STUDENT_VIEW_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
        
        // SIgns out...
        this.logout(driver, wait);
        verifyLoggedOut(driver, wait);
        verifyNobodyLoggedIn(driver, wait);
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
	}
	
	/**
	 * Tests two student users, that they can access student view once
	 * authenticated, cannot access admin views, and cannot access student
	 * view once logged out.
	 */
	//@Ignore
	@Test
	public void testStudentAccess() {
		
        driver.get(LOGIN_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        login(driver, wait, USER1_USERID, USER1_PASSWORD); // should go to studentview
        
        verifyPage(driver, wait, STUDENT_VIEW_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
        
        // jash now tries to access admin page #2 -- should result in access denied
        driver.get(ADMIN2_URL);
        verifyPage(driver, wait, ACCESS_DENIED_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);

        // User hits the back button - should take you to the ordinary view
        driver.navigate().back();
        verifyPage(driver, wait, STUDENT_VIEW_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
        
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, ACCESS_DENIED_TITLE);
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);

        // They sign out - should take them to login page
        logout(driver, wait);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // Even though they're signed out, they try to get back into their account
        driver.navigate().back();
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        // Force back home
        driver.get(BASE_URL);
        verifyPage(driver, wait, HOME_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        driver.get(STUDENT_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // ANother user logs in aash
        login(driver, wait, USER2_USERID, USER2_PASSWORD); // should go to studentview
        verifyPage(driver, wait, STUDENT_VIEW_TITLE);
        verifyPageUser(driver, wait, USER2_USERID, UserRole.Student);
        // And signs out
        logout(driver, wait);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // Original user logs in again
        login(driver, wait, USER1_USERID, USER1_PASSWORD); 
        verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);    
        
        // TODO also test that one student cannot access another's edit/view pages
	}
	
	/**
	 * Tests that the admin user can access everything, but is denied access
	 * once they log out.
	 */
	//@Ignore
	@Test
	public void testAdminAccess() {
		
        driver.get(LOGIN_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
		login(driver, wait, ADMIN_USERID, ADMIN_PASSWORD); 

		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		
		// Force url to the second admin page.
		driver.get(ADMIN2_URL);
		verifyPage(driver, wait, ADMIN2_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		
		// Go back -- should be okay
		driver.navigate().back();
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		
		// Try to access student view page -- should be okay
		driver.get(STUDENT_VIEW_A_URL);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		driver.get(BASE_URL);
		verifyPage(driver, wait, HOME_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);

		// Log out. Try to access an admin page and student view page, shoudl be preveneted
		this.logout(driver, wait);
		this.verifyLoggedOut(driver, wait);
		// Try going back to the admin sceen
		driver.navigate().back(); // back to home
		driver.navigate().back();  // back to ordinary user
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);		
		driver.get(ADMIN1_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		driver.get(ADMIN2_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);				
		driver.get(STUDENT_VIEW_A_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);	
	}
	
	/**
	 * An admin user logs in and logs out. An ordinary user then logs in
	 * and tries to access an admin page. Access should be denied.
	 */
	//@Ignore
	@Test
	public void testOrdinaryUserAfterAdmin() {
				
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);	

		login(driver, wait, ADMIN_USERID, ADMIN_PASSWORD);
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		
		// admin logs out
		this.logout(driver, wait);
		this.verifyLoggedOut(driver, wait);
		
		// ordinary user logs in
		login(driver, wait, USER2_USERID, USER2_PASSWORD);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, USER2_USERID, UserRole.Student);
		
		driver.get(ADMIN1_URL);
		verifyPage(driver, wait, ACCESS_DENIED_TITLE);
		verifyPageUser(driver, wait, USER2_USERID, UserRole.Student);
	}
	
	/**
	 * Note: we should have specific test data, but for now I am using what's in bootstrap.
	 * User1 jash is not enrolled. 
	 * User2 aash is enrolled in Compsci 2.
	 * User3 jebs is enrolled in Compsci 1 and Maths 1.
	 */
	@Test
	public void testUpdateEnrolledCourses() {
		
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);	
		
		// Change jash's details, add 2 courses.
		login(driver, wait, USER1_USERID, USER1_PASSWORD);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, USER1_USERID, UserRole.Student);
		{				
			Collection<String> currentCourseNames = new ArrayList<>();
			Collection<String> newCourseNames = Arrays.asList("Mandarin Chinese 1", "Archaeology 1");
			Collection<String> removeCourseNames = new ArrayList<>();
			updateStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
			checkUpdatedStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
		}

		// Don't bother logging out -- go straight to user 2 aash
		// aash is already enrolled in Comp sci 2. We want him to pull out of
		// that course, and instead enrol in Physics 1, Philosophy 2, Maths 2 and Archaeology
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		login(driver, wait, USER2_USERID, USER2_PASSWORD);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, USER2_USERID, UserRole.Student);
		{
			Collection<String> currentCourseNames = Arrays.asList("Computer Science 2");
			Collection<String> newCourseNames = Arrays.asList("Physics 1", "Philosophy 2", "Maths 2", "Archaeology 1");
			Collection<String> removeCourseNames = Arrays.asList("Computer Science 2");
			updateStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
			checkUpdatedStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
		}
		
		// Now jebs -- deselect ONE of her courses, leaving the other, and add a couple more.
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		login(driver, wait, USER3_USERID, USER3_PASSWORD);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, USER3_USERID, UserRole.Student);
		{
			Collection<String> currentCourseNames = Arrays.asList("Computer Science 1", "Maths 1");
			Collection<String> newCourseNames = Arrays.asList("Biology 1", "Music 1");
			Collection<String> removeCourseNames = Arrays.asList("Computer Science 1");
			updateStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
			checkUpdatedStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
		}
		
		// Now get admin to update the first two students, also checking that their
		// current courses are as we expect.
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		login(driver, wait, ADMIN_USERID, ADMIN_PASSWORD);
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		
		// find the student and click on the button to go to the student view page
		boolean studentFound = adminGetEditStudentPage(USER1_USERID);
		assertTrue(studentFound);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		{				
			Collection<String> currentCourseNames = Arrays.asList("Mandarin Chinese 1", "Archaeology 1");
			Collection<String> newCourseNames = Arrays.asList("English 1");
			Collection<String> removeCourseNames = Arrays.asList("Archaeology 1");
			updateStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
			checkUpdatedStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
		}
		
		driver.get(ADMIN1_URL);
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		// find the student and click on the button to go to the student view page
		studentFound = adminGetEditStudentPage(USER2_USERID);
		assertTrue(studentFound);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		verifyPageUser(driver, wait, ADMIN_USERID, UserRole.Admin);
		{
			Collection<String> currentCourseNames = Arrays.asList("Physics 1", "Philosophy 2", "Maths 2", "Archaeology 1");
			Collection<String> newCourseNames = Arrays.asList("Music 1", "Biology 1");
			Collection<String> removeCourseNames = Arrays.asList("Physics 1", "Philosophy 2");
			updateStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
			checkUpdatedStudentCourses(currentCourseNames, removeCourseNames, newCourseNames);
		}
	}
	
	// Preconditions: current page = admin landing page; students section visible
	// Postconditions: if successful, current page = student's view page
	// Returns true if the button was found and clicked
	private boolean adminGetEditStudentPage(String username) {
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		// TODO verify that they're on the Students section
		
		// The easiest way to deal with the students table, which is paged, is to just
		// grab all the (visible) rows and iterate through, applying the next page until
		// we have found our student.
		String css = "#studentsTable tr";
		List<WebElement> rows = driver.findElements(By.cssSelector(css));
		while ( !rows.isEmpty() ) {
			Predicate<WebElement> checkRowPredicate = cell -> username.equals(cell.getText());
			Predicate<WebElement> findButtonPredicate = cell -> cell.findElement(By.tagName("button")) != null;
			WebElement tdWithButton = searchDatatablePage(rows, checkRowPredicate, //checkRowPredicate(username),
					findButtonPredicate); //findButtonPredicate());
			if ( tdWithButton == null ) {
				// get next page
				rows = getNextPage("studentsTable");
			} else {
				WebElement button = tdWithButton.findElement(By.tagName("button"));
				this.clickIt(wait, button);
				verifyPage(driver, wait, STUDENT_VIEW_TITLE);
				return true;
			}
		}
		// row for this student was not found
		return false;
	}

	// Returns teh relevant cell in the page, or null if not found
	private WebElement searchDatatablePage(List<WebElement> rows, 
			Predicate<WebElement> findRowPredicate, Predicate<WebElement> findCellPredicate) {
		for ( WebElement row : rows ) {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			if ( cells != null ) { // row might be a heading
				if ( cells.stream().anyMatch(findRowPredicate) ) {
					// This is the row we want.
					Optional<WebElement> optional = cells.stream().filter(findCellPredicate).findFirst();
					if ( optional.isPresent() ) {
						return optional.get();
					}
				}
			}
		}
		// Not found in this page
		return null;
	}
	
	private List<WebElement> getNextPage(String tableId) {
		String id = tableId + '_' + "next";
		WebElement we = driver.findElement(By.id(id));
		if ( we == null ) {
			return new ArrayList<>();
		} else {
			// get child and click
			WebElement clickable = we.findElement(By.tagName("a"));
			if ( clickable == null ) {
				return new ArrayList<>();
			} else {
				clickable.click();
				wait.until(ExpectedConditions.stalenessOf(clickable));
				String css = "#" + tableId + " tr";
				List<WebElement> rows = driver.findElements(By.cssSelector(css));
				return rows;
			}
		}
	}
		
	// Removes and adds courses to a student's list of enrolments
	// Preconditions: current page = student's view page
	// Postconditions: current page = student's view page
	private void updateStudentCourses(final Collection<String> currentCourseNames, 
			final Collection<String> removeCourseNames,
			final Collection<String> newCourseNames) {
		
		// Sanity check, that the current courses on the view page agree with currentCourseNames.
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		List<WebElement> courses = driver.findElements(By.className("mxcEnrolledCourseName"));
		checkListContents(courses, currentCourseNames.toArray(new String[0]));

        WebElement editBn = driver.findElement(By.cssSelector(STUDENT_EDIT_BN_CSS));
        clickIt(wait, editBn);
        verifyPage(driver, wait, STUDENT_EDIT_TITLE);

        // Check the current courses.
        for ( String currentCourseName : currentCourseNames ) {
        	WebElement checkbox = getEditingStudentCheckboxForCourse(currentCourseName);
        	assertTrue(checkbox.getAttribute("checked") != null);
        }
        // Removals
        for ( String removeCourseName : removeCourseNames ) {
        	WebElement checkbox = getEditingStudentCheckboxForCourse(removeCourseName);
        	assertTrue(checkbox.getAttribute("checked") != null);
        	checkbox.click();        	
        }
        // Add any new courses.
        for ( String newCourseName : newCourseNames ) {
        	WebElement checkbox = getEditingStudentCheckboxForCourse(newCourseName);
        	assertTrue(checkbox.getAttribute("checked") == null);
        	checkbox.click();
        }
 	
		// Save the student details and expect to return to view
        WebElement submitBn = driver.findElement(By.cssSelector(STUDENT_SAVE_BN_CSS));
        clickIt(wait, submitBn);
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);		
	}
	
	// Checks that the courses listed on the student's view page have been updated
	// in accordance with the given parameters
	// Preconditions: current page = student's view page
	// Postconditions: current page = student's view page
	private void checkUpdatedStudentCourses(final Collection<String> currentCourseNames, 
			final Collection<String> removeCourseNames,
			final Collection<String> newCourseNames) {
		verifyPage(driver, wait, STUDENT_VIEW_TITLE);
		List<WebElement> courses = driver.findElements(By.className("mxcEnrolledCourseName"));
		Collection<String> updatedCourseNames = new ArrayList<>(currentCourseNames);
		updatedCourseNames.removeAll(removeCourseNames);
		updatedCourseNames.addAll(newCourseNames);
		checkListContents(courses, updatedCourseNames.toArray(new String[0]));
	}
	
	private void checkListContents(final List<WebElement> elements, String...values) {
		final Set<String> elementValues = new HashSet<>();
		elements.forEach(c -> {
			String contents = c.getText();
			elementValues.add(contents);
		});
		assertTrue(values.length == elementValues.size());
		for ( String value : values ) {
			assertTrue(elementValues.contains(value));
		}
	}
	
	private WebElement getEditingStudentCheckboxForCourse(String courseName) {
		String css = getEditingStudentEnrolledCourseCss(courseName);
		WebElement td = driver.findElement(By.cssSelector(css));

		String id = td.getAttribute("id");
		// expecting the id to be something like "enrolments14.name"
		int idx = id.indexOf('.');
		assertTrue(idx > 0); // exists and is not the first letter
		String prefix = id.substring(0, idx);
		
		WebElement checkbox = driver.findElement(By.id(prefix + ".enrolled1"));
		// Which should be an input
		assertTrue("input".equals(checkbox.getTagName()));
		return checkbox;
	}
	

	/**
	 * Selenium's WebElement.click() method is a bit unreliable, as evidenced here:
	 * https://github.com/seleniumhq/selenium-google-code-issue-archive/issues/6112.
	 * I ran into a (roughly) similar problem in testOrdinaryUserAfterAdmin(). I started
	 * the method from LOGIN_URL, rather than BASE_URL. THis should make no difference.
	 * THe user (admin in this case) tries to log in using my login() method, which had
	 * already been heavily used with no problems. However, when called from 
	 * tetOrdinaryUsesrAfterAdmin(), the click on the submit button simply didn't happen, 
	 * causing the following statements to fail. 
	 * I assumed it was my fault, and spent quite a bit of time investigating, checking it
	 * manually, etc. I then put a get call to BASE_URL at the start of the method,
	 * and suddenly everything worked. Weird, huh.
	 * A bit of googling around, and I came across this:
	 * http://stackoverflow.com/questions/11676790/click-command-in-selenium-webdriver-does-not-work
	 * I removed the call to the base url, and replaced the click. Now it works. 
	 * (You'll note that the SO post goes back to 2012.)
	 * Therefore, I am using the (apparently) more reliable way of doing things
	 * throughout this code, until I hear of a better plan.
	 * 
	 */
	private void clickIt(WebDriverWait wait, WebElement clickable) {
		wait.until(ExpectedConditions.elementToBeClickable(clickable));
		clickable.sendKeys(Keys.ENTER);
		wait.until(ExpectedConditions.stalenessOf(clickable));
	}
	
    /**
     * Asserts that the page just loaded has the given title.
     * @param driver
     * @param wait
     * @param expectedTitle
     * @return the title of the page just verified
     */
    private WebElement verifyPage(WebDriver driver, WebDriverWait wait, String expectedTitle) {
    
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(TITLE_TAG)));
    	// It can be a bit flakey, so wait for footer to be loaded as well.
     	wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("footer")));
	   
    	WebElement title = driver.findElement(By.tagName(TITLE_TAG));
    	assertEquals(expectedTitle, driver.getTitle());
    	return title;
    }
	
    /**
     * Logs the user in.
     * Preconditions: user should be on the login page
     * @param driver
     * @param wait
     * @param username
     * @param password
     */
	private void login(WebDriver driver, WebDriverWait wait, String username, String password) {
 
	        WebElement userNameField = driver.findElement(By.id(PAGE_LOGIN_INPUT_USERNAME_ID));
	        WebElement passwordField = driver.findElement(By.id(PAGE_LOGIN_INPUT_PASSWORD_ID));
	        wait.until(ExpectedConditions.elementToBeClickable(userNameField));
	        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
	        
	        userNameField.sendKeys(username);
	        passwordField.sendKeys(password);
	        
	        WebElement submitBn = driver.findElement(By.cssSelector(PAGE_LOGIN_SUBMIT_CSS));
	        clickIt(wait, submitBn);
	}

	/**
	 * Logs the user out.
	 * Preconditions: user is on any application page, and is logged in.
	 * @param driver
	 * @param wait
	 */
	private void logout(WebDriver driver, WebDriverWait wait) {
		WebElement logoutBn = driver.findElement(By.className(PAGE_HEADING_LOGOUT_BN_ID));
        clickIt(wait, logoutBn);
	}
	
	/**
	 * Asserts that, as far as the UI is concerned, nobody is logged in.
	 * @param driver
	 * @param wait
	 */
    private void verifyNobodyLoggedIn(WebDriver driver, WebDriverWait wait) {  
    	List<WebElement> list = driver.findElements(By.className(PAGE_HEADING_USER_CLASS)); //PAGE_HEADING_USER_DETAILS_CLASS));
    	if ( list.size() > 0 ) {
    		WebElement we = list.get(0);
    		System.out.println("THERE IS 1 " + we.getTagName() + ", " + we);
    	}
    	assertEquals(0, list.size());
    	list = driver.findElements(By.id(PAGE_HEADING_USER_NAME_ID));
    	assertEquals(0, list.size());
    	list = driver.findElements(By.id(PAGE_HEADING_USER_ROLES_ID));
    	assertEquals(0, list.size());
    	list = driver.findElements(By.id(PAGE_HEADING_LOGOUT_ID));
    	assertEquals(0, list.size());
    }
    
    /**
     * Asserts that the current page is the login page and it is displaying
     * a message indicating an invalid login attempt.
     * @param driver
     * @param wait
     */
	private void verifyInvalidLogin(WebDriver driver, WebDriverWait wait) {
        verifyPage(driver, wait, LOGIN_TITLE);
        List<WebElement> list = driver.findElements(By.className(PAGE_LOGIN_INVALID_MSG_CLASS));
        assertEquals(1, list.size());		
	}
	
	/**
     * Asserts that the current page is the login page and it is displaying
     * a message indicating that the user has just logged out.
     */
	private void verifyLoggedOut(WebDriver driver, WebDriverWait wait) {
        verifyPage(driver, wait, LOGIN_TITLE);
        List<WebElement> list = driver.findElements(By.className(PAGE_LOGIN_SUCCESSFUL_LOGOUT_MSG_CLASS));
        assertEquals(1, list.size());		
	}
	
	/**
	 * Asserts that, as far as the UI is concerned, the given user is logged in.
	 * This should work for any page in the app.
	 * @param driver
	 * @param wait
	 * @param username
	 * @param role
	 */
    private void verifyPageUser(WebDriver driver, WebDriverWait wait, String username, UserRole role) {

    	WebElement userNameElement = driver.findElement(By.id(PAGE_HEADING_USER_NAME_ID));
    	assertEquals(username, userNameElement.getText());
    	WebElement roleElement = driver.findElement(By.id(PAGE_HEADING_USER_ROLES_ID));
    	String expectedRole = "[ROLE_" + role.name() + ']';

    	assertEquals(expectedRole, roleElement.getText());
    }
}
