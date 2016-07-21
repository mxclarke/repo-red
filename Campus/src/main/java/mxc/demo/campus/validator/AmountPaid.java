package mxc.demo.campus.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Validates the amount a student has paid towards their fees, to be within range.
 */
@NotNull
@Min(AmountPaid.MIN)
@Max(AmountPaid.MAX)
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AmountPaid {
	static final int MIN = -1000;
	static final int MAX = 100000;
		
    String message() default "must be between " + MIN + " and " + MAX;
    
    Class<?>[] groups() default {};
     
    Class<? extends Payload>[] payload() default {};
   	
}
