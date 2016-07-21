package mxc.demo.campus.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

public class PasswordHashValidator implements ConstraintValidator<PasswordHash, String> {
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(PasswordHash arg0) {		
	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		
		if ( StringUtils.isEmpty(password) || StringUtils.containsWhitespace(password) ) {
			return true;
		}
		
		if ( password.length() != PasswordHash.LENGTH )
			return false;
				
		return true;
	}
}
