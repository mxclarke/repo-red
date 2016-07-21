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
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

/**
 * Validate's a user's email address.
 *
 */
@NotNull
@Size(min=CampusEmail.MIN_LENGTH, max=CampusEmail.MAX_LENGTH)
@Email
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CampusEmail {
	
	static final int MIN_LENGTH = 3;
	static final int MAX_LENGTH = 254;
	
    String message() default "must be valid email address with length " + MIN_LENGTH + " - " + MAX_LENGTH;

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
