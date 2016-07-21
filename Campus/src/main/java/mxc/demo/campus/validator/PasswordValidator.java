package mxc.demo.campus.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

public class PasswordValidator implements ConstraintValidator<Password, String> {
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(Password arg0) {
		
	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		
		// We theoretically "allow" a null or empty password, so that users who are not
		// changing their password can send back a blank.
		if ( StringUtils.isEmpty(password) ) {
			return true;
		}
		
		if ( StringUtils.containsWhitespace(password) ) {
			return false;
		}
		
		if ( password.length() < Password.MIN_LENGTH )
			return false;
		
		// TODO more. At the moment the passwords are easy to spare the developer pain.
		// Should use profiling to deal with this.
		
		return true;
	}
}
