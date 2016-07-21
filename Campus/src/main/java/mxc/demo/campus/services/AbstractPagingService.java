package mxc.demo.campus.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import mxc.demo.campus.dto.ColumnDTO;
import mxc.demo.campus.dto.OrderDTO;
import mxc.demo.campus.repositories.GenericPagingRepository;

/**
 * Superclass for services which require paging, filtering and sorting over a UI
 * collection element, such as a table.
 */
public abstract class AbstractPagingService<T, K extends Serializable> {

	private static final Logger logger = Logger.getLogger(AbstractPagingService.class);

	private final FilterBuilder filterBuilder;
 
    public AbstractPagingService(FilterBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
    }
    
	public Page<T> getData(GenericPagingRepository<T, K> repo, Pageable pageable) {
		return repo.findAll(pageable);
	}

	public Page<T> getData(GenericPagingRepository<T, K> repo, Predicate predicate, Pageable pageable) {
		return repo.findAll(predicate, pageable);
	}	

	/**
	 * Retrieve a page of data.
	 * 
	 * @param repo the repository the data will be retrieved from
	 * @param start (zero-indexed) index into the data, which must be >= 0
	 * @param length length of each page, which must be > 0
	 * @param columns metadata supplied by the client for each field, which may be null
	 * @param orderings list of ordering information from the client's request, which may be null
	 * 
	 * @return the given page of data, which may be empty but not null
	 */
	public Page<T> getData(GenericPagingRepository<T, K> repo, int start, int length, 
			List<ColumnDTO> columns, Iterable<OrderDTO> orderings) {

		// Determine the filtering predicates, if any.
		BooleanBuilder booleanBuilder = columns == null 
				? new BooleanBuilder()
				: getFilteringPredicate(filterBuilder, columns);

		// Determine the column orderings, if any.
		List<Sort.Order> springColumnOrders = orderings == null
				? new ArrayList<Sort.Order>()
				: getColumnOrderings(columns, orderings);

		// Determine the page.
		int pageIndex = length > 0 ? start / length : 0; // defensive coding,
															// length should be
															// > 0 as per
															// preconditions
		logger.debug("start = " + start + ", length = " + length + ", pageIndex=" + pageIndex);
		
		Pageable pageable = springColumnOrders.isEmpty() 
				? new PageRequest(pageIndex, length) // without sorting
				: new PageRequest(pageIndex, length, new Sort(springColumnOrders)); // with sorting

		// Request the data from the repo.
		Page<T> page = booleanBuilder.hasValue() 
				? getData(repo, booleanBuilder.getValue(), pageable) // with filtering
				: getData(repo, pageable); // without filtering

		return page;
	}

	/**
	 * Adapt the per-column filtering information from the DTO to a
	 * BooleanBuilder object, potentially containing a Predicate, as expected by
	 * the repository.
	 * 
	 * @param columns
	 *            Non-null metadata supplied by the client for each field
	 *            
	 * @return the filtering predicate to be supplied to the repository's query
	 *         method, which may or may not be empty
	 */
	private BooleanBuilder getFilteringPredicate(final FilterBuilder filterBuilder, final List<ColumnDTO> columns) {

		// The predicate (return value) for filtering the results.
		BooleanBuilder booleanBuilder = new BooleanBuilder();
				
		// Loop through all the columns to extract search/filtering information,
		// adding it to the predicate. NOTE: regex is possible but by default is off.
		for (ColumnDTO column : columns) {
			String searchValue = column.isSearchable() ? column.getSearch().getValue() : "";
			if (!StringUtils.isEmpty(searchValue)) {
				String columnRef = column.getData();
				BooleanExpression expression = filterBuilder.getFilterExpression(columnRef, searchValue);
				if ( expression != null ) {
					booleanBuilder.and(expression);
				}
			}
		}

		return booleanBuilder;
	}
	
	/**
	 * Adapt the sorting information from the JQuery Datatables DTO to a Sort
	 * object as expected by the Spring repository.
	 * 
	 * @param columns
	 *            list of column metadata from the client's request
	 * @param orderings
	 *            list of ordering information from the client's request
	 * @return ordering information that can be used to access the repository
	 */
	public List<Sort.Order> getColumnOrderings(List<ColumnDTO> columns, Iterable<OrderDTO> orderings) {

		List<Sort.Order> orders = new ArrayList<>();

		for (OrderDTO orderDTO : orderings) {		
			int column = orderDTO.getColumn();
			// Assuming what we get back from the client is correct!
			String property = columns.get(column).getData();
			Sort.Direction direction = Sort.Direction.fromStringOrNull(orderDTO.getDir().toUpperCase());
			// THe property could possibly be null, e.g. a view button column.
			if (direction != null && property != null) {
				Sort.Order order = new Sort.Order(direction, property);
				orders.add(order);
			}
		}

		return orders;
	}
}
