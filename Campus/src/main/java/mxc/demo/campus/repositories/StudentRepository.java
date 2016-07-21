package mxc.demo.campus.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mxc.demo.campus.domain.Student;

/**
 * The JPA repository for users who are students.
 */
public interface StudentRepository extends GenericUserRepository<Student> {
		
	@Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = (:id)")
    Student findByIdAndFetchCourses(@Param("id") Long id);
	
	@Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.userId = (:userId)")
	Student findByUserIdAndFetchCourses(@Param("userId") String userId);
}