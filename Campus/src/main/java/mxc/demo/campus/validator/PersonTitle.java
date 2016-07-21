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
 * Validates a person's title, e.g. "Ms", "Prof." etc.
 * COnsider changing this to an enumeration.
 */
@NotNull
@Size(min=PersonTitle.MIN_LENGTH, max=PersonTitle.MAX_LENGTH)
@Pattern(regexp=PersonTitle.PATTERN)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PersonTitle {
	static final int MIN_LENGTH = 0;
	static final int MAX_LENGTH = 5;
	static final String PATTERN = "^[A-Za-z]+([.]){0,1}$";
			
    String message() default "must be valid title with length " + MIN_LENGTH + " - " + MAX_LENGTH;
    
    Class<?>[] groups() default {};
     
    Class<? extends Payload>[] payload() default {};
   	
}

