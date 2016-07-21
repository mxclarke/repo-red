package mxc.demo.campus.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mysema.query.types.Predicate;

import mxc.demo.campus.domain.Student;
import mxc.demo.campus.dto.ColumnDTO;
import mxc.demo.campus.dto.OrderDTO;
import mxc.demo.campus.view.StudentForm;

/**
 * Service for retrieving and modfifying users who are students.
 */
public interface StudentService {
	
	Student getStudentById(long id);
	
	Student getStudentByUserName(String userId);
	
	StudentForm getStudentByIdWithCourses(long id);
	
	Student getStudentByUserNameWithCourses(String userId);
	
	Student save(Student student);
	
	/**
	 * Saves a new or edited student, including updating their enrolments.
	 * 
	 * @param studentForm will only have those fields populated that the user
	 * 	can legitimately set or modify
	 * 
	 * @return the student entity after saving to the repository
	 */
	Student saveOrUpdateStudent(StudentForm studentForm);
	
    void delete(long id);

	/**
	 * @param pageable
	 * @return the next page of students, with neither filtering nor sorting
	 */
	Page<Student> getStudents(Pageable pageable);
	
	/**
	 * @param predicate defines filters over the Student fields
	 * @param pageable
	 * @return the next page of students, with filtering but no sortin
	 */
	Page<Student> getStudents(Predicate predicate, Pageable pageable);
	
	/**
	 * Retrieves the next page of students, filtered and sorted.
	 * 
	 * @param start
	 *            Starting index into the total list of users for this
	 *            requested page, assumed to be >= 0
	 * @param length
	 *            Number of users for this requested page, assumed to be > 0
	 * @param columns
	 *            Non-null metadata supplied by the client for each field
	 * @param orderings
	 *            Non-null per-column sorting information as requested by the
	 *            client
	 * 
	 * @return the next page of students, filtered and sorted
	 */
	Page<Student> getStudents(int start, int length, 
			List<ColumnDTO> columns, Iterable<OrderDTO> orderings);
    // Todo: create
	

}
