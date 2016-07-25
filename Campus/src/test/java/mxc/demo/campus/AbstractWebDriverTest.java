package mxc.demo.campus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * Extend this class for all your tests, then further extend as concrete classes for
 * each of the browsers you want to support.
 * 
 * @WebIntegrationTest enables this test class to launch the app, so you don't
 * have to run it separately. See here: 
 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications
 *
 * Also if you want to use mockito
 * http://stackoverflow.com/questions/24431427/multiple-runwith-statements-in-junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CampusApplication.class)
@WebIntegrationTest(value = "server.port=9000")
@TestPropertySource(locations="classpath:test.properties")
public abstract class AbstractWebDriverTest {
	
	static final String BASE_URL = "http://localhost:9000";
	static final String LOGIN_URL = BASE_URL + "/login";
	static final String STUDENT_URL = BASE_URL + "/student";
	static final String LECTURER_URL = BASE_URL + "/lecturer";
	static final String ADMIN1_URL = BASE_URL + "/admin";
	static final String ADMIN2_URL = BASE_URL + "/admin/secondPage";
	static final String STUDENT_VIEW_A_URL = STUDENT_URL + "/1";

	static final String TITLE_TAG = "title";
	
	static final String HOME_TITLE = "Campus app";
	static final String LOGIN_TITLE = "Login form";

	static final String STUDENT_VIEW_TITLE = "Student";
	static final String STUDENT_EDIT_TITLE = "Student edit";
	static final String LECTURER_VIEW_TITLE = "Lecturer";
	static final String ADMIN1_VIEW_TITLE = "Administration";
	static final String ADMIN2_VIEW_TITLE = "Administration Details";
	static final String ACCESS_DENIED_TITLE = "Campus Access Denied";

	static final String PAGE_LOGIN_INPUT_USERNAME_ID = "username";
	static final String PAGE_LOGIN_INPUT_PASSWORD_ID = "password";

	static final String PAGE_LOGIN_SUBMIT_CSS = ".mxcLoginSubmit input";
	
	static final String PAGE_HEADING_LOGOUT_BN_ID = "mxcHeaderBn";
	static final String PAGE_HEADING_USER_CLASS = "mxcHeaderUserArea";
	
	static final String PAGE_HEADING_USER_NAME_ID = "mxcHeaderUsername";
	static final String PAGE_HEADING_USER_ROLES_ID = "mxcHeaderRole";

	static final String PAGE_HEADING_LOGOUT_ID = "mxcHeaderLogoutButton";
	
	static final String USER1_USERID = "jash"; // not enrolled
	static final String USER1_PASSWORD = "pwjash";
	static final String USER2_USERID = "aash"; // enrolled in at least 1 course
	static final String USER2_PASSWORD = "pwaash";
	static final String USER3_USERID = "jebs"; // enrolled
	static final String USER3_PASSWORD = "pwjebs";
	static final String ADMIN_USERID = "adminUser";
	static final String ADMIN_PASSWORD = "admin";


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
	protected void clickIt(WebDriverWait wait, WebElement clickable) {
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
	protected WebElement verifyPage(WebDriver driver, WebDriverWait wait, String expectedTitle) {
    
    	wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(TITLE_TAG)));
    	// It can be a bit flakey, so wait for footer to be loaded as well.
     	wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("footer")));
	   
    	WebElement title = driver.findElement(By.tagName(TITLE_TAG));
    	assertEquals(expectedTitle, driver.getTitle());
    	return title;
    }
	
	/**
	 * Asserts that, as far as the UI is concerned, the given user is logged in.
	 * This should work for any page in the app.
	 * @param driver
	 * @param wait
	 * @param username
	 * @param role
	 */
    protected void verifyPageUser(WebDriver driver, WebDriverWait wait, String username, UserRole role) {

    	WebElement userNameElement = driver.findElement(By.id(PAGE_HEADING_USER_NAME_ID));
    	assertEquals(username, userNameElement.getText());
    	WebElement roleElement = driver.findElement(By.id(PAGE_HEADING_USER_ROLES_ID));
    	String expectedRole = "[ROLE_" + role.name() + ']';

    	assertEquals(expectedRole, roleElement.getText());
    }
    
    /**
     * Logs the user in.
     * Preconditions: user should be on the login page
     * @param driver
     * @param wait
     * @param username
     * @param password
     */
    protected void login(WebDriver driver, WebDriverWait wait, String username, String password) {
 
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
	protected void logout(WebDriver driver, WebDriverWait wait) {
		WebElement logoutBn = driver.findElement(By.className(PAGE_HEADING_LOGOUT_BN_ID));
        clickIt(wait, logoutBn);
	}
	
	/**
	 * Asserts that, as far as the UI is concerned, nobody is logged in.
	 * @param driver
	 * @param wait
	 */
    protected void verifyNobodyLoggedIn(WebDriver driver, WebDriverWait wait) {  
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
     * Get next page of rows in a JQuery Datatables table
     * @param driver
     * @param wait
     * @param tableId the "id" for the JQuery Datatables table
     * @return a list of rows for the next page
     */
    protected List<WebElement> getNextPage(WebDriver driver, WebDriverWait wait, String tableId) {
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
    
    /**
     * Searches rows in an HTML table for a particular cell in a particular row
     * 
     * @param rows the rows of the table that we are searching
     * @param findRowPredicate predicate to find a row
     * @param findCellPredicate predicate to find a cell within a row
     * @return the relevant cell in the page, or null if not found
     */
    protected WebElement searchDatatablePage(List<WebElement> rows, 
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
    
    /**
     * Checks that the text contents of each web element in the given list matches
     * the list of String parameters, and fails on assertion if this is not the case
     * @param elements a list of web elements
     * @param values the list of string parameters that the elements are expected to match
     */
	protected void checkListContents(final List<WebElement> elements, String...values) {
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
}
