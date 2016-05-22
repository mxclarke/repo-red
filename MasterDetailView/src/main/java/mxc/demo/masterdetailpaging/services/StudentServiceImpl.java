package mxc.demo.masterdetailpaging.services;

import java.util.ArrayList;
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
	 * @param start Starting index into the total list of students for this requested page,
	 * assumed to be >= 0
	 * @param length Number of students for this requested page, assumed to be > 0
	 * @param columns Metadata supplied by the client for each field
	 * @param orderings Per-column sorting information as requested by the client
	 * 
	 * @return the next page of students
	 */
	@Override
	public Page<Student> getStudents(int start, int length, 
			List<ColumnDTO> columns, Iterable<OrderDTO> orderings) {

		// The generated metamodel for Student.
		QStudent qStudent = QStudent.student;
		// The predicate for filtering the results.
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		// Loop through all the columns to extract search/filtering information, adding
		// it to the predicate. NOTE: regex is possible but by default is off.
		// TODO: we don't really want literal strings here. 
		for ( ColumnDTO column : columns ) {
			String searchValue = column.isSearchable() ? column.getSearch().getValue() : "";
			if ( !StringUtils.isEmpty(searchValue) ) {
				String columnRef = column.getData();
				switch( columnRef ) {
				case "lastName":
					booleanBuilder.and(qStudent.lastName.containsIgnoreCase(searchValue));
					break;
				case "firstName":
					booleanBuilder.and(qStudent.firstName.containsIgnoreCase(searchValue));
					break;
				case "external" :
					// TODO: it will be interesting to see what happens when you change the
					// values to "yes" and "no", or otherwise internationalise it.
					booleanBuilder.and(qStudent.external.stringValue().containsIgnoreCase(searchValue));
					//booleanBuilder.and(qStudent.external.eq(Boolean.valueOf(searchValue)));
					break;
				case "studentId":
					booleanBuilder.and(qStudent.studentId.containsIgnoreCase(searchValue));
					break;
				default:
					logger.error("Unknown column ref " + columnRef + "!!");
				}
			}
		}
			
		// Determine the column orderings, if any.
		Sort sort = getColumnOrderings(columns,orderings);

		// Determine the page.
		int pageIndex = length > 0 ? start/length : 0; 
    	Pageable pageable = new PageRequest(pageIndex, length, sort);
   
    	// Request the data from the repo.
    	Page<Student> page = null; 
		if ( booleanBuilder.hasValue() ) {
			page = getStudents(booleanBuilder.getValue(), pageable);
		} else {
			page = getStudents(pageable);
		}
		
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
	
	@Override
	public Student getStudentById(int id) {
		return repo.findOne(id);
	}

	@Override
	public Student saveStudent(Student student) {
		return repo.save(student);
	}

	@Override
	public void deleteStudent(int id) {
		repo.delete(id);		
	}

	@Override
	public Student getStudentByLastName(String lastName) {
		return repo.findByLastName(lastName);
	}
	
	/**
	 * Adapt the sorting information from the DTO to a Sort object as expected by
	 * the repository.
	 * 
	 * This is pretty generic (as far as the DTOs are concerned), so could probably go
	 *  into a utility class for JQuery Datatables.
	 *  
	 * @param columns list of column metadata from the client's request
	 * @param orderings list of ordering information from the client's request
	 * @return ordering information that can be used to access the repository
	 */
	private Sort getColumnOrderings(List<ColumnDTO> columns, Iterable<OrderDTO> orderings) {
		
		List<Sort.Order> orders = new ArrayList<>();
		
		for ( OrderDTO orderDTO : orderings ) {
			int column = orderDTO.getColumn();
			// Assuming what we get back from the client is correct!
			String property = columns.get(column).getData();
			Sort.Direction direction = Sort.Direction.fromStringOrNull(orderDTO.getDir().toUpperCase());
			//Sort.Direction direction = Sort.Direction.valueOf(orderDTO.getDir());
			if ( direction != null ) {
				Sort.Order order = new Sort.Order(direction, property);
				orders.add(order);
			}
		}
		
		Sort sort = new Sort(orders);
		return sort;
	}
}
