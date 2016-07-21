package mxc.demo.campus.services;

import com.mysema.query.types.expr.BooleanExpression;

/**
 * Assists in building predicates for given columns, which can then be used
 * by queries to JPA repositories implementing QueryDslPredicateExecutor.
 * 
 *  @see mxc.demo.campus.repositories.GenericPagingRepository
 * @see mxc.demo.campus.services.AbstractPagingService
 */
public interface FilterBuilder {
	
	/**
	 * Maps a column name (e.g. from a table) and a user-defined search expression
	 * to a Query DSL expression.
	 * 
	 * @param columnRef e.g. "lastName", "firstName", etc
	 * @param searchValue a search expression for that column as entered by the user
	 * 
	 * @return a filter that can be applied to each cell in the column
	 */
	BooleanExpression getFilterExpression(String columnRef, String searchValue);
}
