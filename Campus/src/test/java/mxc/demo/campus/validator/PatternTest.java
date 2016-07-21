package mxc.demo.campus.validator;

import javax.validation.constraints.AssertFalse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.fasterxml.jackson.core.sym.Name;


public class PatternTest {

	//starts with alpha 
	// may contain alpha, ' if surrounded by alpha, - if surrounded by alpha, single space
	// ends with alpha 

	private static final String PATTERN0 = "^([A-Za-z](([A-Za-z]-[A-Za-z])|([A-Za-z]'[A-Za-z])|([A-Za-z] [A-Za-z])|[A-Za-z])+)$";
	private static final String PATTERN1 = "^([A-Za-z]((-[A-Za-z])|('[A-Za-z])|( [A-Za-z])|[A-Za-z])+)$";
	static final String PATTERN2 = "^[A-Za-z0-9]+$";

	static final String PATTERN_TITLE = "^[A-Za-z]+([.]){0,1}$";

	@Test
	public void testTitle() {
		pTitleFalse("");
		pTitleFalse(" ");
		pTitleFalse("a b");
		pTitleFalse("a-b");
		pTitleFalse("a,b");
		pTitleFalse("ab,");
		pTitleFalse(",ab");
		pTitleFalse("9ab");
		pTitleFalse("ab9");
		pTitleFalse("a9b");
		pTitleFalse("a.b");
		pTitleFalse(".ab");
		pTitleTrue("a");
		pTitleTrue("ab");
		pTitleTrue("a.");
		pTitleTrue("ab.");

	}
	
	@Test
	public void testUserId() {
		
		pUserIdFalse("");
		pUserIdFalse(" ");
		pUserIdFalse("a b");
		pUserIdFalse("a-b");
		pUserIdFalse("a,b");
		pUserIdFalse("ab,");
		
		pUserIdTrue("a");
		pUserIdTrue("6");
		pUserIdTrue("ab");
		pUserIdTrue("67");
		pUserIdTrue("a6b");

	}
	
	@Test
	public void testName() {
		
		pNameFalse("");
		pNameFalse("9");
		pNameFalse("b");
		pNameFalse("B");
		pNameFalse("-");
		pNameFalse("'");
		pNameFalse(" ");
		pNameFalse("%");
		
		pNameTrue("AB");
		pNameTrue("ABCdef");
		
		pNameTrue("A B C d e f");
		pNameTrue("AB Cde f");
		pNameFalse("A  B");
		pNameFalse("A  B");
		pNameFalse(" AB");
		pNameFalse("AB ");
		
		pNameTrue("a'b");
		pNameTrue("a'b'c'd");
		pNameTrue("ab'ce'dd");
		pNameFalse("A''B");
		pNameFalse("A'''B");
		pNameFalse("A'bcd''Bc");
		pNameFalse("'abc");
		pNameFalse("abc'");
		
		pNameTrue("a-b");
		pNameTrue("a-b-c-d");
		pNameTrue("ab-bb-dd");
		pNameFalse("A--B");
		pNameFalse("A---B");
		pNameFalse("A-bcd--Bc");
		pNameFalse("-abc");
		pNameFalse("abc-");
		
		pNameFalse("ab- dd");
		pNameFalse("ab -dd");
		pNameFalse("ab' dd");
		pNameFalse("ab 'dd");
		pNameFalse("ab'-dd");
		pNameFalse("ab-'dd");
		
		pNameFalse("ab-,dd");
		pNameFalse("ab,dd");
		pNameFalse("ab;dd");
	}
	
	private void pNameFalse(String n) {
		String pattern = PersonName.PATTERN; // PATTERN1
		Assert.assertFalse(n.matches(pattern));
	}
	private void pNameTrue(String n) {
		String pattern = PersonName.PATTERN; // PATTERN1
		Assert.assertTrue(n.matches(pattern));
	}
	private void pUserIdFalse(String n) {
		String pattern = UserId.PATTERN; //PATTERN2
		Assert.assertFalse(n.matches(pattern));
	}
	private void pUserIdTrue(String n) {
		String pattern = UserId.PATTERN; //PATTERN2
		Assert.assertTrue(n.matches(pattern));
	}
	
	private void pTitleFalse(String n) {
		String pattern = PersonTitle.PATTERN; // PATTERN_TITLE
		Assert.assertFalse(n.matches(pattern));
	}
	private void pTitleTrue(String n) {
		String pattern = PersonTitle.PATTERN; // PATTERN_TITLE
		Assert.assertTrue(n.matches(pattern));
	}
}
