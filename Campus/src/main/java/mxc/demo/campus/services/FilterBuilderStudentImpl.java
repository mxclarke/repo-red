package mxc.demo.campus.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mysema.query.types.expr.BooleanExpression;

import mxc.demo.campus.domain.QStudent;
import mxc.demo.campus.domain.QUser;

/**
 * A FilterBuilder for a Student entity.
 */
@Component("students")
public class FilterBuilderStudentImpl implements FilterBuilder {
	
	// QUser.user: user.firstName.getMetadata().getName() == "firstName"
	
	private final FilterFactory filterFactory;
	
	// Maps a column name to a function that takes a user-defined search value and returns a filter
	private Map<String, Function<String, BooleanExpression>> studentMap = new HashMap<>();
	
	@Autowired
	public FilterBuilderStudentImpl(FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
		buildMap();
	}
	
	private void buildMap() {

		QStudent student = QStudent.student;
		
		studentMap.put(student.firstName.getMetadata().getName(), filterFactory.getLambdaForString(student.firstName) );
		studentMap.put(student.lastName.getMetadata().getName(), filterFactory.getLambdaForString(student.lastName) );
		studentMap.put(student.userId.getMetadata().getName(), filterFactory.getLambdaForString(student.userId) );
		studentMap.put(student.amountPaid.getMetadata().getName(), filterFactory.getLambdaForMoney(student.amountPaid) );
		studentMap.put(student.external.getMetadata().getName(), filterFactory.getLambdaForBoolean(student.external) );
	}

	/**
	 * 
	 * @param role
	 * @param columnRef e.g. "lastName", "firstName", etc
	 * @param searchValue
	 * @return
	 */
	public BooleanExpression getFilterExpression(String columnRef, String searchValue) {
				
		// query object map for the role and column
		Function<String, BooleanExpression> fn = studentMap.get(columnRef);
			
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
