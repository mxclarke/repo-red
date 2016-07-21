package mxc.demo.campus;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
	
//	private static final String PAGE_HOME_STUDENT_LINK_ID = "student";
//	private static final String PAGE_HOME_LECTURER_LINK_ID = "lecturer";
//	private static final String PAGE_HOME_ADMIN_LINK_ID = "admin";
	
//	private static final String PAGE_ADMIN_PAGE2_LINK_ID = "someotheradminpage";
	
	private static final String PAGE_LOGIN_INPUT_USERNAME_ID = "username";
	private static final String PAGE_LOGIN_INPUT_PASSWORD_ID = "password";
	private static final String PAGE_LOGIN_SUBMIT_CSS = ".mxcLoginSubmit input";
	private static final String PAGE_LOGIN_INVALID_MSG_CLASS = "mxcLoginInvalidText";
	private static final String PAGE_LOGIN_SUCCESSFUL_LOGOUT_MSG_CLASS = "mxcLogoutSuccessful";
	
	private static final String USER1_USERID = "jash";
	private static final String USER1_PASSWORD = "pwjash";
	private static final String USER2_USERID = "aash";
	private static final String USER2_PASSWORD = "pwaash";
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
	@Test
	public void testOrdinaryUserAfterAdmin() {
				
		driver.get(LOGIN_URL);
		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id(PAGE_HEADING_ID)));
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
    	// It can be a bit flakey, so wait for an actual element with an id.
    	// Wait until we at least have a page from this app -- we can test header elements.
	   // wait.until(
      	//	   ExpectedConditions.presenceOfElementLocated(By.id(PAGE_HEADING_ID)));
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

	        // Sometimes Selenimum seems to have timing issues.
	        wait.until(ExpectedConditions.stalenessOf(submitBn));
	}

	/**
	 * Logs the user out.
	 * Preconditions: user is on any application page, and is logged in.
	 * @param driver
	 * @param wait
	 */
	private void logout(WebDriver driver, WebDriverWait wait) {
		WebElement logoutBn = driver.findElement(By.className(PAGE_HEADING_LOGOUT_BN_ID));

		//WebElement logoutBn = driver.findElement(By.id(PAGE_HEADING_LOGOUT_BN_ID));
       // WebElement logoutBn = driver.findElement(By.cssSelector(PAGE_HEADING_LOGOUT_CSS));
        clickIt(wait, logoutBn);
       // logoutBn.click();
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
