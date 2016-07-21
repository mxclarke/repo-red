package mxc.demo.campus.services;

import java.util.Collection;

import mxc.demo.campus.domain.Course;
import mxc.demo.campus.view.ViewCourse;

/**
 * Service for retrieving and modfifying courses.
 */
public interface CourseService {

	/**
	 * Retrieves courses and returns as ViewCourse objects. Note: we cannot
	 * return the Course entity as it has cyclic dependencies, and in particular
	 * has a reference to Lecturer, some of whose data (such as salary) needs
	 * protection.
	 * 
	 * @return a collection of all the existing courses as ViewCourse
	 */
	Collection<ViewCourse> getCourses();

	Course getCourseById(Integer id);

	Course getCourseByIdWithStudents(int id);

	Course save(Course course);

	void delete(Integer id);
}
