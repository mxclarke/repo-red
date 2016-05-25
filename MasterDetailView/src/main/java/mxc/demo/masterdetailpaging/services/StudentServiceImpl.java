package mxc.demo.masterdetailpaging.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;

import mxc.demo.masterdetailpaging.domain.QStudent;
import mxc.demo.masterdetailpaging.domain.Student;
import mxc.demo.masterdetailpaging.dto.ColumnDTO;
import mxc.demo.masterdetailpaging.dto.OrderDTO;
import mxc.demo.masterdetailpaging.repositories.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

	private final Logger logger = Logger.getLogger(StudentServiceImpl.class);

	private final StudentRepository repo;

	@Autowired
	public StudentServiceImpl(StudentRepository repo) {
		Assert.notNull(repo);
		this.repo = repo;
	}

	/**
	 * @return all students
	 */
	@Override
	public Iterable<Student> getStudents() {
		return repo.findAll();
	}

	/**
	 * Retrieves the next page of students, filtered and sorted.
	 * 
	 * @param start
	 *            Starting index into the total list of students for this
	 *            requested page, assumed to be >= 0
	 * @param length
	 *            Number of students for this requested page, assumed to be > 0
	 * @param columns
	 *            Non-null metadata supplied by the client for each field
	 * @param orderings
	 *            Non-null per-column sorting information as requested by the
	 *            client
	 * 
	 * @return the next page of students
	 */
	@Override
	public Page<Student> getStudents(final int start, final int length, final List<ColumnDTO> columns,
			final Iterable<OrderDTO> orderings) {

		// Check pre-conditions.
		Assert.isTrue(start >= 0, "Parameter 'start' is " + start + " but should be >= 0");
		Assert.isTrue(length > 0, "Parmeter 'length' is " + length + " but should be > 0");
		Assert.notNull(columns);
		Assert.notNull(orderings);

		BooleanBuilder booleanBuilder = getFilteringPredicate(columns);

		// Determine the column orderings, if any.
		List<Sort.Order> springColumnOrders = SpringDTOAdapter.instance.getColumnOrderings(columns, orderings);

		// Determine the page.
		int pageIndex = length > 0 ? start / length : 0; // defensive coding,
															// length should be
															// > 0 as per
															// preconditions
		Pageable pageable = springColumnOrders.isEmpty() ? new PageRequest(pageIndex, length) // without
																								// sorting
				: new PageRequest(pageIndex, length, new Sort(springColumnOrders)); // with
																					// sorting

		// Request the data from the repo.
		Page<Student> page = booleanBuilder.hasValue() ? getStudents(booleanBuilder.getValue(), pageable) // with
																											// filtering
				: getStudents(pageable); // without filtering

		return page;
	}

	@Override
	public Page<Student> getStudents(Predicate predicate, Pageable pageable) {
		return repo.findAll(predicate, pageable);
	}

	@Override
	public Page<Student> getStudents(Pageable pageable) {
		return repo.findAll(pageable);
	}

	/**
	 * Adapt the per-column filtering information from the DTO to a
	 * BooleanBuilder object, potentially containing a Predicate, as expected by
	 * the repository.
	 * 
	 * @param columns
	 *            Non-null metadata supplied by the client for each field
	 * @return the filtering predicate to be supplied to the repository's query
	 *         method
	 */
	private BooleanBuilder getFilteringPredicate(final List<ColumnDTO> columns) {
		Assert.notNull(columns);

		// The predicate (return value) for filtering the results.
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		// The generated metamodel for Student.
		QStudent qStudent = QStudent.student;

		// Loop through all the columns to extract search/filtering information,
		// adding
		// it to the predicate. NOTE: regex is possible but by default is off.
		for (ColumnDTO column : columns) {
			String searchValue = column.isSearchable() ? column.getSearch().getValue() : "";
			if (!StringUtils.isEmpty(searchValue)) {
				String columnRef = column.getData();
				switch (columnRef) {
				case "lastName":
					booleanBuilder.and(qStudent.lastName.containsIgnoreCase(searchValue));
					break;
				case "firstName":
					booleanBuilder.and(qStudent.firstName.containsIgnoreCase(searchValue));
					break;
				case "external":
					// TODO: it will be interesting to see what happens when you
					// change the
					// values to "yes" and "no", or otherwise internationalise
					// it.
					booleanBuilder.and(qStudent.external.stringValue().containsIgnoreCase(searchValue));
					// booleanBuilder.and(qStudent.external.eq(Boolean.valueOf(searchValue)));
					break;
				case "studentId":
					booleanBuilder.and(qStudent.studentId.containsIgnoreCase(searchValue));
					break;
				default:
					logger.error("Unknown column ref " + columnRef + "!!");
				}
			}
		}

		return booleanBuilder;
	}
}
