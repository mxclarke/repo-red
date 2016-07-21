package mxc.demo.campus.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mysema.query.types.expr.BooleanExpression;

import mxc.demo.campus.domain.QLecturer;

/**
 * A FilterBuilder for a Lecturer entity.
 *
 */
@Component("lecturers")
public class FilterBuilderLecturerImpl implements FilterBuilder {

	private final FilterFactory filterFactory;
	
	// Maps a column name to a function that takes a user-defined search value and returns a filter
	private Map<String, Function<String, BooleanExpression>> lecturerMap = new HashMap<>();
	
	@Autowired
	public FilterBuilderLecturerImpl(FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
		buildMap();
	}

	private void buildMap() {
		QLecturer lecturer = QLecturer.lecturer;

		lecturerMap.put(lecturer.firstName.getMetadata().getName(), filterFactory.getLambdaForString(lecturer.firstName) );
		lecturerMap.put(lecturer.lastName.getMetadata().getName(), filterFactory.getLambdaForString(lecturer.lastName) );
		lecturerMap.put(lecturer.userId.getMetadata().getName(), filterFactory.getLambdaForString(lecturer.userId) );
		lecturerMap.put(lecturer.title.getMetadata().getName(), filterFactory.getLambdaForString(lecturer.title) );
		lecturerMap.put(lecturer.salary.getMetadata().getName(), filterFactory.getLambdaForMoney(lecturer.salary) );
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.repositories.QueryHelper#getQuery(java.lang.String, java.lang.String)
	 */
	@Override
	public BooleanExpression getFilterExpression(String columnRef, String searchValue) {
		// query object map for the role and column
		Function<String, BooleanExpression> fn = lecturerMap.get(columnRef);
			
		if ( fn != null ) {
			// apply the lambda, providing it with searchval
			BooleanExpression expression = fn.apply(searchValue);
			// return the resulting expression
			return expression;
		} else {
			return null;
		}
	}

}
