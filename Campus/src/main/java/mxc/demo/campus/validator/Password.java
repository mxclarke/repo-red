package mxc.demo.campus.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates a user's password prior to hashing.
 *
 */
@Constraint(validatedBy = PasswordValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Password {
	public static final int MIN_LENGTH = 4;
	//public static final int MAX_LENGTH = 250;
	
    String message() default "must be at least " + MIN_LENGTH + " characaters";
    
    Class<?>[] groups() default {};
     
    Class<? extends Payload>[] payload() default {};
}

