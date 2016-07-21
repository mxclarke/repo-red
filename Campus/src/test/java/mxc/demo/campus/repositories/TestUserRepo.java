package mxc.demo.campus.repositories;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.h2.util.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import mxc.demo.campus.CampusApplication;
import mxc.demo.campus.bootstrap.UserCredentialsGenerator;
import mxc.demo.campus.domain.Student;
import mxc.demo.campus.domain.User;
import mxc.demo.campus.services.MoneyService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CampusApplication.class)
public class TestUserRepo {

	@Autowired
	@NotNull
	private UserRepository userRepo;
	
	// TODO mock the money service to isolate the user repo
	@Autowired
	@NotNull
	private MoneyService moneyService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	  
	@Test
	public void testCreateStudent() {
		Number amountPaid = 42;
		Student s = createStudent("Jo", "Bloggs", amountPaid);
		Assert.assertTrue(s.getId() == 0);
		s = userRepo.save(s);
		long id = s.getId();
		Assert.assertTrue(id != 0);
		Assert.assertTrue(s.getFirstName().equals("Jo"));
		User u = userRepo.findOne(id);
		Assert.assertTrue(u.getId() == id);
		Assert.assertTrue(s.getFirstName().equals("Jo"));
		s = (Student)u;
		BigDecimal moneyAmountPaid = createMoney(amountPaid);
		System.out.println("AMT PAID IS " + s.getAmountPaid() + ", comp with " + moneyAmountPaid);
		Assert.assertTrue(s.getAmountPaid().compareTo(moneyAmountPaid) == 0);
		
		// Now try create one without a firstname or lastname
		s = createStudent(null, "Smith", 43);
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);
		s = createStudent("Firstie", null, 43);
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);
		
		s = createStudent("Jill", "Brown", 10, null, "jb123", "jb@hello.com");
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);
		s = createStudent("Jill", "Brown", 10, "", "jb123", "jb@hello.com");
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);
		
		s = createStudent("Jill", "Brown", 10, "abc123", null, "jb@hello.com");
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);
		s = createStudent("Jill", "Brown", 10, "abc123", "", "jb@hello.com");
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);

		s = createStudent("Jill", "Brown", 10, "abc123", "j1j1j1j1", null);
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);
		s = createStudent("Jill", "Brown", 10, "abc123", "j1j1j1j1", "");
		thrown.expect(javax.validation.ConstraintViolationException.class);
		s = userRepo.save(s);
		Assert.assertTrue(s == null);

	}
	
	private Student createStudent(String firstName, String lastName, Number amountPaid) {
		
		String _firstName = (StringUtils.isNullOrEmpty(firstName)) ? "f" : firstName;
		String _lastName = (StringUtils.isNullOrEmpty(lastName)) ? "l" : lastName;
		String emailAddress = createEmail(_firstName, _lastName, "someProvider.com");
		
		boolean isExternal = (_firstName + _lastName).hashCode() % 2 == 0;
		BigDecimal moneyAmountPaid = createMoney(amountPaid);
		String userId = UserCredentialsGenerator.generateUserName(_firstName, _lastName);
		String password = UserCredentialsGenerator.generatePassword(_firstName, _lastName);
		
		return new Student(userId, password, firstName, lastName, emailAddress, isExternal, moneyAmountPaid);
	}
	
	private Student createStudent(String firstName, String lastName, Number amountPaid,
			String userId, String password, String emailAddress) {
		
		String _firstName = (StringUtils.isNullOrEmpty(firstName)) ? "f" : firstName;
		String _lastName = (StringUtils.isNullOrEmpty(lastName)) ? "l" : lastName;
		//String emailAddress = createEmail(_firstName, _lastName, "someProvider.com");
		
		boolean isExternal = (_firstName + _lastName).hashCode() % 2 == 0;
		BigDecimal moneyAmountPaid = createMoney(amountPaid);
		//String userId = UserCredentialsGenerator.generateUserName(_firstName, _lastName);
		//String password = UserCredentialsGenerator.generatePassword(_firstName, _lastName);
		
		return new Student(userId, password, firstName, lastName, emailAddress, isExternal, moneyAmountPaid);
	}
	private String createEmail(String firstName, String lastName, String domain) {
		
		String _firstName = firstName.replace(' ', '_');
		String _lastName = lastName.replace(' ', '_');		
		String emailAddress = _lastName + _firstName.toLowerCase().charAt(0)
				+ '@' + domain;
		return emailAddress;
	}
	private BigDecimal createMoney(Number amount) {
		return moneyService.createMoney(amount);
	}
}
