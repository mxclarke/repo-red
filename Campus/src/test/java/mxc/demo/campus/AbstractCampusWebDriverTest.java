package mxc.demo.campus;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import mxc.demo.campus.domain.UserRole;

/**
 * Integration tests for logging in and testing user access in the Campus application.
 * 
 * Should be sub-classed for specific web drivers.
 */
public abstract class AbstractCampusWebDriverTest extends AbstractWebDriverTest {
	
	private static final String PAGE_LOGIN_INVALID_MSG_CLASS = "mxcLoginInvalidText";
	private static final String PAGE_LOGIN_SUCCESSFUL_LOGOUT_MSG_CLASS = "mxcLogoutSuccessful";
	
	private WebDriver driver;
	private WebDriverWait wait;
		
	@Before
	public void setup() {
		driver = createWebDriver();
		wait = new WebDriverWait(driver, 20);
	}
	
	@After
	public void tearDown() {
		if ( driver != null )
			driver.quit();
	}
	
	protected abstract WebDriver createWebDriver();
	
	/**
	 * Tests that an anonymous user can access home and the login page, but
	 * no others.
	 */
	//@Ignore
	@Test
	public void testAnonymousAccess() {
		
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
}
