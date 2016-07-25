package mxc.demo.campus;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import mxc.demo.campus.domain.UserRole;

/**
 * Integration tests for student editing in the Campus application.
 * 
 * Should be sub-classed for specific web drivers.
 *
 */
public abstract class AbstractStudentWebDriverTest extends AbstractWebDriverTest {

	private static final String STUDENT_EDIT_BN_CSS = "form input[value=\"Edit details\"";
	private static final String STUDENT_SAVE_BN_CSS = "form input[value=\"Save\"";
	private static final String STUDENT_PASSWORD_BN_CSS = "form input[value=\"Change password\"]";

	private WebDriver driver;
	private WebDriverWait wait;
		
	@Before
	public void setup() {
		driver = createWebDriver();
		//driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 20);
	}
	
	@After
	public void tearDown() {
		if ( driver != null )
			driver.quit();
	}
	
	protected abstract WebDriver createWebDriver();
	
	private static final String getEditingStudentEnrolledCourseCss(String courseName) {
		String css = "tr td input[value=\"" + courseName + "\"]";
		return css;
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
				rows = getNextPage(driver, wait, "studentsTable");
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
}
