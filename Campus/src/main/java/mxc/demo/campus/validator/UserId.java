package mxc.demo.campus.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Validates a user's username.
 *
 */
@NotNull
@Size(min=UserId.MIN_LENGTH, max=UserId.MAX_LENGTH)
@Pattern(regexp=UserId.PATTERN)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE  })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserId {
	
	static final int MIN_LENGTH = 2;
	static final int MAX_LENGTH = 15;
	
	static final String PATTERN = "^[A-Za-z0-9]+$";
	
    String message() default "must be alphanumeric with length " + MIN_LENGTH + " - " + MAX_LENGTH;
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};

}
