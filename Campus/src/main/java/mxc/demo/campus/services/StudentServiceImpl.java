package mxc.demo.campus.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.Predicate;

import mxc.demo.campus.domain.Course;
import mxc.demo.campus.domain.Student;
import mxc.demo.campus.dto.ColumnDTO;
import mxc.demo.campus.dto.OrderDTO;
import mxc.demo.campus.repositories.CourseRepository;
import mxc.demo.campus.repositories.StudentRepository;
import mxc.demo.campus.view.FormEnrolment;
import mxc.demo.campus.view.StudentForm;

/* (non-Javadoc)
 * @see mxc.demo.campus.services.StudentService
 */
@Service
@Transactional(readOnly = true)
public class StudentServiceImpl extends AbstractPagingService<Student, Long> 
		implements StudentService {

	private static final Logger logger = Logger.getLogger(StudentServiceImpl.class);
	
	private final StudentRepository repo;
	
	private final CourseRepository courseRepo;
		
	private final BeanManagerService beanManagerService;
	
	@Autowired
    public StudentServiceImpl(
    		StudentRepository repo, 
    		CourseRepository courseRepo,
    		@Qualifier("students") FilterBuilder studentFilterBuilder,
    		BeanManagerService beanManagerService) {
    	super(studentFilterBuilder);
    	this.repo = repo;
    	this.courseRepo = courseRepo;
    	this.beanManagerService = beanManagerService;
    }
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#getStudentById(long)
	 */
	@Override
	public Student getStudentById(long id) {
		Student student = repo.findOne(id);
		return student;
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#getStudentByUserName(java.lang.String)
	 */
	@Override
	public Student getStudentByUserName(String userId) {
		Student student = repo.findByUserId(userId);
		return student;
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#getStudentAndCourses(long)
	 */
	@Override
	public StudentForm  getStudentByIdWithCourses(long id) {
		
		Student student = repo.findByIdAndFetchCourses(id);
		List<Course> studentCourses = student.getCourses();
		
		StudentForm studentForm = new StudentForm();
		beanManagerService.copyPropertiesExcept(student, studentForm, "role", "courses");
			
		List<FormEnrolment> formEnrolments = new ArrayList<>();
			
		Iterable<Course> allCourses = courseRepo.findAll();
		for ( Course course : allCourses ) {
			FormEnrolment formEnrolment = new FormEnrolment();
			// Copy the lecturer as well, since that should also be a column on the form
			beanManagerService.copyPropertiesExcept(course, formEnrolment, "students", "lecturer.salary");
				
			//StudentEnrolmentProjection enrolmentProjection = beanManagerService.mapToInterface(course, StudentEnrolmentProjection.class);
			if ( studentCourses.contains(course) ) {
				formEnrolment.setEnrolled(Boolean.TRUE);
			} else {
				formEnrolment.setEnrolled(Boolean.FALSE);
			}
			formEnrolments.add(formEnrolment);
		}
		studentForm.setEnrolments(formEnrolments);

		return studentForm;
	}

	@Override
	public Student getStudentByUserNameWithCourses(String userId) {	
		return repo.findByUserIdAndFetchCourses(userId);
	}
		
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#save(mxc.demo.campus.domain.Student)
	 */
	@Override
	@Modifying @Transactional
	public Student save(Student student) {
		return repo.save(student);
	}
	
	/* (non-Javadoc)
	 * * @see mxc.demo.campus.services.StudentService#saveOrUpdateStudent(mxc.demo.campus.domain.Student)
	 */
	@Override
	@Modifying @Transactional
	public Student saveOrUpdateStudent(StudentForm studentForm) {
		
		logger.debug("Updating student id = " + studentForm.getId() + ", username = " + studentForm.getUserId());
				
       	Student student = repo.findByIdAndFetchCourses(studentForm.getId());
    	if ( student == null ) {
    		// It's a new one. The data so far will be everything in courseData.
    		logger.info("Creating new student");
    		student = new Student();
    	} // otherwise, it's an update.
    	
    	// Either way, we do a property copy of everything that the client has changed. HOwever,
    	// the user is not allowed to update every field, and with the exception of "id" which is
    	// a primitive those non-editable fields will come in as null, because they have been blocked 
    	// on the controller. We want to prevent those from beng copied, otherwise they'll overwrite
    	// existing data.
    	List<String> nullProperties = beanManagerService.getNullPropertyNames(studentForm);
    	Collections.addAll(nullProperties, "id");
    	logger.debug("The properties not to copy are:");
    	nullProperties.forEach(logger::debug);
    	beanManagerService.copyPropertiesExcept(studentForm, student, nullProperties.toArray(new String[0]));

    	// BeanUtils doesn't do deep copy, so we also have to consider the courses.
    	List<Course> persistedCourses = student.getCourses();
    	Map<Integer, Course> persistedCourseMap =
    			persistedCourses.stream().collect(Collectors.toMap(Course::getId,
    					Function.identity()));
    	Set<Course> coursesToPersist = new HashSet<>(); 
   
    	for ( FormEnrolment enrolment : studentForm.getEnrolments() ) {
    		logger.debug("Next enrolment is "+ enrolment);
   			Course persistedCourse = persistedCourseMap.get(enrolment.getId());
    		boolean alreadyEnrolled = persistedCourse != null; 
    		if ( enrolment.isEnrolled() ) { // enrolled in selected state on the UI
    			if ( !alreadyEnrolled ) {
    				logger.debug("We need to enrol this student");
    				// Load course and add the student.
    				persistedCourse = courseRepo.findOne(enrolment.getId()); 
    				persistedCourse.addStudent(student);
    				coursesToPersist.add(persistedCourse);
    			}
    		} else { // enrolled in deselected state on the UI
    			if ( alreadyEnrolled ) {
    				logger.debug("We need to remove this student's enrolment");
    				persistedCourse.removeStudent(student);  
    				coursesToPersist.add(persistedCourse);
    			}
    		}
    	}
 
    	student = this.save(student);
    	logger.debug("Student has been saved as " + student);
        	
    	return student;
	}

	@Override
	@Modifying @Transactional
    public void delete(long id) {
		repo.delete(id);
	}
    
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#getStudents(org.springframework.data.domain.Pageable)
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Student> getStudents(Pageable pageable) {
		return super.getData(repo, pageable);
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#getStudents(com.mysema.query.types.Predicate, org.springframework.data.domain.Pageable)
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Student> getStudents(Predicate predicate, Pageable pageable) {
		return super.getData(repo, predicate, pageable);
	}

	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.StudentService#getStudents(int, int, java.util.List, java.lang.Iterable)
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Student> getStudents(int start, int length, 
			List<ColumnDTO> columns, Iterable<OrderDTO> orderings) {
		return super.getData(repo, start, length, columns, orderings);
	}
}
