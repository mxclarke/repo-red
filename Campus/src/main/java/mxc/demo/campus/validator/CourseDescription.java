package mxc.demo.campus.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Validates a person's first name or last name.
 *
 */
@Size(min=CourseDescription.MIN_LENGTH, max=CourseDescription.MAX_LENGTH)
@Pattern(regexp=CourseDescription.PATTERN)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CourseDescription {
	static final int MIN_LENGTH = 0;
	static final int MAX_LENGTH = 255;
	static final String PATTERN = "^([A-Za-z0-9]((-[A-Za-z0-9])|('[A-Za-z])|( [A-Za-z0-9])|[A-Za-z0-9])+)$";
		
    String message() default "must be alphanumeric and embedded hyphens, apostrophes and spaces, with length " + MIN_LENGTH + " - " + MAX_LENGTH;
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
   	
}

