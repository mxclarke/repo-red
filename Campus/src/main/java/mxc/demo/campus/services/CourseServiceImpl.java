package mxc.demo.campus.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import mxc.demo.campus.domain.Course;
import mxc.demo.campus.domain.Lecturer;
import mxc.demo.campus.exception.ParseException;
import mxc.demo.campus.repositories.CourseRepository;
import mxc.demo.campus.view.ViewCourse;

/* (non-Javadoc)
 * @see mxc.demo.campus.services.CourseService
 */
@Service
public class CourseServiceImpl implements CourseService {

	private final CourseRepository repo;
	
	private final BeanManagerService beanManagerService;
	
	@Autowired
	public CourseServiceImpl(CourseRepository repo, BeanManagerService beanManagerService) {
	    	this.repo = repo;
	    	this.beanManagerService = beanManagerService;
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.CourseService#listAllCourses()
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<ViewCourse> getCourses() {
		Iterable<Course> courses = repo.findAll();
		
		List<ViewCourse> viewCourses = 
			StreamSupport.stream(courses.spliterator(), false)
			.map(c -> getCourse(c))
			.collect(Collectors.toList());
		
		return viewCourses;
 
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.CourseService#getCourseById(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Course getCourseById(Integer id) {
		return repo.findOne(id);
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.CourseService#getCourseByIdWithStudents(int)
	 */
	@Override
	@Transactional(readOnly = true)
	public Course getCourseByIdWithStudents(int id) {
		return repo.findByIdAndFetchStudents(id);
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.CourseService#saveCourse(mxc.demo.campus.domain.Course)
	 */
	@Override
	@Transactional
	public Course save(Course course) {
		return repo.save(course);
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.CourseService#deleteCourse(java.lang.Integer)
	 */
	@Override
	@Transactional
	public void delete(Integer id) {
		repo.delete(id);
	}
	
	private ViewCourse getCourse(final Course course) {
		
		try {
			ViewCourse viewCourse = beanManagerService.mapToInterface(course, ViewCourse.class);
			
			Lecturer supervisor = course.getLecturer();
			if ( supervisor != null ) {
				if ( StringUtils.isEmpty(supervisor.getTitle()) ) {
					viewCourse.setLecturerName(String.format("%s %s", supervisor.getFirstName(), supervisor.getLastName()));
				} else {
					viewCourse.setLecturerName(String.format("%s %s %s", 
							supervisor.getTitle(), supervisor.getFirstName(), supervisor.getLastName()));
				}
			}
			
			return viewCourse;

		} catch ( IOException ex ) {
			throw new ParseException("There was an error in the server while attempting to build page with " + course);
		}
	}
}
