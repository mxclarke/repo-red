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
 * Validates the name of a course.
 */
@NotNull
@Size(min=CourseName.MIN_LENGTH, max=CourseName.MAX_LENGTH)
@Pattern(regexp=CourseName.PATTERN)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CourseName {
	static final int MIN_LENGTH = 7;
	static final int MAX_LENGTH = 63;
	static final String PATTERN = "^([A-Za-z0-9]((-[A-Za-z0-9])|('[A-Za-z])|( [A-Za-z0-9])|[A-Za-z0-9])+)$";
		
    String message() default "must be alphanumeric and embedded hyphens, apostrophes and spaces, with length " + MIN_LENGTH + " - " + MAX_LENGTH;
    
    Class<?>[] groups() default {};
     
    Class<? extends Payload>[] payload() default {};
}

