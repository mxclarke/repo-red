package mxc.demo.login;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
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

import mxc.demo.login.domain.UserRole;

/**
 * Functional tests using Selenium.
 * 
 * @WebIntegrationTest enables this test class to launch the app, so you don't
 * have to run it separately. See here: 
 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LoginApplication.class)
@WebIntegrationTest(value = "server.port=9000")
@TestPropertySource(locations="classpath:test.properties")
public class WebDriverIntegrationTest {
	
	private static final String BASE_URL = "http://localhost:9000";
	private static final String LOGIN_URL = BASE_URL + "/login";
	private static final String ACCESS_DENIED_URL = BASE_URL + "/accessdenied";
	private static final String PLEB_URL = BASE_URL + "/pleb";
	private static final String ADMIN1_URL = BASE_URL + "/admin";
	private static final String ADMIN2_URL = BASE_URL + "/admin/secondPage";

	private static final String HOME_TITLE = "Login example app";
	private static final String LOGIN_TITLE = "Login form";
	private static final String PLEB_VIEW_TITLE = "Pleb view";
	private static final String ADMIN1_VIEW_TITLE = "Administration";
	private static final String ADMIN2_VIEW_TITLE = "Administration Details";
	private static final String ACCESS_DENIED_TITLE = "Login example access denied";

	private WebDriver driver;
	private WebDriverWait wait;
		
	@Before
	public void setup() {
		driver = new FirefoxDriver();
		wait = new WebDriverWait(driver, 10);
	}
	
	@After
	public void tearDown() {
		if ( driver != null )
			driver.quit();
	}
	
	//@Ignore
	@Test
	public void testAnonymousAccess() {
		driver.get(ADMIN2_URL);
		// Wait until we at least have a page from this app -- we can test header elements.
	    wait.until(
      		   ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
	    verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(BASE_URL);
		verifyPage(driver, wait, HOME_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(LOGIN_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(ACCESS_DENIED_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(PLEB_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(BASE_URL);
		verifyPage(driver, wait, HOME_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(ADMIN1_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
	}
	
	//@Ignore
	@Test
	public void testOrdinaryUser() {
				
        driver.get(BASE_URL);
        // Wait until we at least have a page from this app.
        wait.until(
       		   ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
        verifyPage(driver, wait, HOME_TITLE);
        
        WebElement plebBn = wait.until(
        		   ExpectedConditions.presenceOfElementLocated(By.id("pleb")));
       // wait.until(ExpectedConditions.elementToBeClickable(plebBn));
        // Check that, as far as the UI is concerned, no one is logged in.
        verifyNobodyLoggedIn(driver, wait);
        clickIt(wait, plebBn);
       // plebBn.click(); // should take u to login page
        
        //verifyLoginPage(driver, wait);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        login(driver, wait, "jash", "pwjash"); // should go to plebview
        
        verifyPage(driver, wait, PLEB_VIEW_TITLE);
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);
       
        // jash now tries to access admin page #2 -- should result in access denied
        driver.get(ADMIN2_URL);
        verifyPage(driver, wait, ACCESS_DENIED_TITLE);
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);
        
        // User hits the back button - should take you to the ordinary view
        driver.navigate().back();
        verifyPage(driver, wait, PLEB_VIEW_TITLE);
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);
        
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
        driver.get(PLEB_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // ANother user logs in aash
        login(driver, wait, "aash", "pwaash"); // should go to plebview
        verifyPage(driver, wait, PLEB_VIEW_TITLE);
        verifyPageUser(driver, wait, "aash", UserRole.Pleb);
        // And signs out
        logout(driver, wait);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        
        // Original user logs in again
        login(driver, wait, "jash", "pwjash"); 
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);        
	}
	
	//@Ignore
	@Test
	public void testBogusLogin() {
		
        driver.get(BASE_URL);
        // Wait until we at least have a page from this app.
        wait.until(
       		   ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
        verifyPage(driver, wait, HOME_TITLE);
        
        WebElement plebBn = wait.until(
        		   ExpectedConditions.presenceOfElementLocated(By.id("pleb")));
       // wait.until(ExpectedConditions.elementToBeClickable(plebBn));
        // Check that, as far as the UI is concerned, no one is logged in.
        verifyNobodyLoggedIn(driver, wait);
        clickIt(wait, plebBn);
       // plebBn.click(); // should take u to login page
        
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
        login(driver, wait, "jash", "wrongpassword"); 
        // should go to login page with invalid message
        verifyInvalidLogin(driver, wait);
        verifyNobodyLoggedIn(driver, wait);
        
        // Try to access page anyway
        driver.get(PLEB_URL);
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
        // Wait until we at least have a page from this app.
        wait.until(
       		   ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
        verifyPage(driver, wait, LOGIN_TITLE);
        
        login(driver, wait, "adminUser", "admin");
        verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
        verifyPageUser(driver, wait, "adminUser", UserRole.Uber); 
        
        driver.get(LOGIN_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        login(driver, wait, "adminUser", "admin");
        // SHould have no effect -- still logged in and goes to home page
        verifyPage(driver, wait, HOME_TITLE);
        verifyPageUser(driver, wait, "adminUser", UserRole.Uber); 
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
        verifyPageUser(driver, wait, "adminUser", UserRole.Uber);
        
        // Now go back to login and try to log in as someone else
        driver.get(LOGIN_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        login(driver, wait, "jash", "pwjash");
        // SHould be logged in as the new user, and on the home page
        verifyPage(driver, wait, HOME_TITLE);
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);
        // ANd can't get at the admin page
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, ACCESS_DENIED_TITLE);
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);
        // BUt should be able to access ordinary page
        driver.get(PLEB_URL);
        verifyPage(driver, wait, PLEB_VIEW_TITLE);
        verifyPageUser(driver, wait, "jash", UserRole.Pleb);
        
        // SIgns out...
        this.logout(driver, wait);
        verifyLoggedOut(driver, wait);
        verifyNobodyLoggedIn(driver, wait);
        driver.get(ADMIN1_URL);
        verifyPage(driver, wait, LOGIN_TITLE);
        verifyNobodyLoggedIn(driver, wait);
	}
	
	//@Ignore
	@Test
	public void testAdmin() {
		
		driver.get(BASE_URL);
		// Wait until we at least have a page from this app.
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
		verifyPage(driver, wait, HOME_TITLE);

		WebElement adminBn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("admin")));
		//wait.until(ExpectedConditions.elementToBeClickable(adminBn));
		// Check that, as far as the UI is concerned, no one is logged in.
		verifyNobodyLoggedIn(driver, wait);
		//adminBn.click(); // should take u to login page
        clickIt(wait, adminBn);

		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		login(driver, wait, "adminUser", "admin"); 

		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);
		
		// Go to the second admin page.
		WebElement otherBn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("someotheradminpage")));
		//wait.until(ExpectedConditions.elementToBeClickable(otherBn));
		//otherBn.click();
        clickIt(wait, otherBn);
		verifyPage(driver, wait, ADMIN2_VIEW_TITLE);
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);
		
		// Go back -- should be okay
		driver.navigate().back();
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);
		
		// Try to access an ordinary page -- should be okay
		driver.get(PLEB_URL);
		verifyPage(driver, wait, PLEB_VIEW_TITLE);
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);
		driver.get(BASE_URL);
		verifyPage(driver, wait, HOME_TITLE);
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);

		// Log out. Try to access an admin page and ordinary page, shoudl be preveneted
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
		driver.get(PLEB_URL);
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);	
	}
	
	//@Ignore
	@Test
	public void testOrdinaryUserAfterAdmin() {
				
		driver.get(LOGIN_URL);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);	

		login(driver, wait, "adminUser", "admin");
		verifyPage(driver, wait, HOME_TITLE);  // IT'S GOIN GLOGIN INSTEAD
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);

		WebElement adminBn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("admin")));
		//wait.until(ExpectedConditions.elementToBeClickable(adminBn));
		//adminBn.click();
        clickIt(wait, adminBn);
		verifyPage(driver, wait, ADMIN1_VIEW_TITLE);
		verifyPageUser(driver, wait, "adminUser", UserRole.Uber);
		
		// admin logs out
		this.logout(driver, wait);
		this.verifyLoggedOut(driver, wait);
		
		// ordinary user logs in
		login(driver, wait, "aash", "pwaash");
		verifyPage(driver, wait, HOME_TITLE);
		verifyPageUser(driver, wait, "aash", UserRole.Pleb);
		
		driver.get(ADMIN1_URL);
		verifyPage(driver, wait, ACCESS_DENIED_TITLE);
		verifyPageUser(driver, wait, "aash", UserRole.Pleb);
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
	 * You'll note that the SO post goes back to 2012, so this suggests that some things
	 * have been a little unstable in Selenium WD for quite some time.
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
    
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("title")));
    	WebElement title = driver.findElement(By.tagName("title"));
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
 
	        WebElement userNameField = driver.findElement(By.id("username"));
	        WebElement passwordField = driver.findElement(By.id("password"));
	        wait.until(ExpectedConditions.elementToBeClickable(userNameField));
	        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
	        
	        userNameField.sendKeys(username);
	        passwordField.sendKeys(password);
	        
	        WebElement submitBn = driver.findElement(By.cssSelector(".mxcLoginSubmit input"));
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
        WebElement logoutBn = driver.findElement(By.cssSelector(".mxcHeaderButton input"));
        //wait.until(ExpectedConditions.elementToBeClickable(logoutBn));
        clickIt(wait, logoutBn);

       // logoutBn.click();
	}
	
	/**
	 * Asserts that, as far as the UI is concerned, nobody is logged in.
	 * @param driver
	 * @param wait
	 */
    private void verifyNobodyLoggedIn(WebDriver driver, WebDriverWait wait) {  
    	List<WebElement> list = driver.findElements(By.className("mxcHeaderUserDetails"));
    	assertEquals(0, list.size());
    	list = driver.findElements(By.id("mxcHeaderUsername"));
    	assertEquals(0, list.size());
    	list = driver.findElements(By.id("mxcHeaderRole"));
    	assertEquals(0, list.size());
    	list = driver.findElements(By.id("mxcHeaderLogoutButton"));
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
        List<WebElement> list = driver.findElements(By.className("mxcLoginInvalidText"));
        assertEquals(1, list.size());		
	}
	

	/**
     * Asserts that the current page is the login page and it is displaying
     * a message indicating that the user has just logged out.
     */
	private void verifyLoggedOut(WebDriver driver, WebDriverWait wait) {
        verifyPage(driver, wait, LOGIN_TITLE);
        List<WebElement> list = driver.findElements(By.className("mxcLogoutSuccessful"));
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

    	WebElement userNameElement = driver.findElement(By.id("mxcHeaderUsername"));
    	assertEquals(username, userNameElement.getText());
    	WebElement roleElement = driver.findElement(By.id("mxcHeaderRole"));
    	String expectedRole = "[ROLE_" + role.name() + ']';

    	assertEquals(expectedRole, roleElement.getText());
    }
    
    /**
     * Preconditions: no one is logged in
     * @param driver
     * @param wait
     */
    /*
	private void testAnonymousAccess(WebDriver driver, WebDriverWait wait) {
		driver.get(ADMIN2_URL);
		// Wait until we at least have a page from this app -- we can test header elements.
	    wait.until(
      		   ExpectedConditions.presenceOfElementLocated(By.id("mxcHeaderId")));
	    verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(BASE_URL);
		//wait.until(ExpectedConditions.stalenessOf(lastTitle));
		//lastTitle = verifyHomePage(driver, wait);
		verifyPage(driver, wait, HOME_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(LOGIN_URL);
		//wait.until(ExpectedConditions.stalenessOf(lastTitle));
		verifyPage(driver, wait, LOGIN_TITLE);
		
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(ACCESS_DENIED_URL);
		//wait.until(ExpectedConditions.stalenessOf(lastTitle));
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(PLEB_URL);
		//wait.until(ExpectedConditions.stalenessOf(lastTitle));
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(BASE_URL);
		//wait.until(ExpectedConditions.stalenessOf(lastTitle));
		verifyPage(driver, wait, HOME_TITLE);
		verifyNobodyLoggedIn(driver, wait);
		
		driver.get(ADMIN1_URL);
		//wait.until(ExpectedConditions.stalenessOf(lastTitle));
		verifyPage(driver, wait, LOGIN_TITLE);
		verifyNobodyLoggedIn(driver, wait);
	}
	*/
    
}
