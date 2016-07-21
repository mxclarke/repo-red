package mxc.demo.campus.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mxc.demo.campus.domain.Course;


public interface CourseRepository extends CrudRepository<Course, Integer> {

	@Query("SELECT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = (:id)")
    public Course findByIdAndFetchStudents(@Param("id") Integer id);

}
