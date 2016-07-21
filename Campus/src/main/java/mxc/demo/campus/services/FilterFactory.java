package mxc.demo.campus.services;

import java.math.BigDecimal;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.BooleanPath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

/**
 * Used to build FilterBuilder implementations. Constructs "typical" predicates
 * for column types, e.g. string, boolean, money, date.
 * 
 * @see mxc.demo.campus.services.FilterBuilder
 */
@Component
public class FilterFactory {
	
	private static final Logger logger = Logger.getLogger(FilterFactory.class);

	@Autowired
	private MoneyService moneyService;
		
	Function<String, BooleanExpression> getLambdaForString(StringPath stringPath) {
		return searchVal -> { return stringPath.containsIgnoreCase(searchVal); };
	}
	
	Function<String, BooleanExpression> getLambdaForBoolean(BooleanPath flagPath) {

		return searchVal -> { return flagPath.eq(Boolean.valueOf(searchVal)); };
	}

	/**
	 * WHen the user filters on a money column, it has to match the amount.
	 * @param amount the amount in each row of the money column
	 * @return a function which compares the user's text with each cell in the money column
	 */
	Function<String, BooleanExpression> getLambdaForMoney(NumberPath<BigDecimal> amount) {
		
		return searchVal -> { 
			BigDecimal userAmount = moneyService.parse(searchVal);
			if ( userAmount == null ) {
				// they've entered garbage
				return null;
			} else {
				return amount.eq(userAmount);
			}
		};
	}
	
	
	// TODO others - money, date
}