package mxc.demo.campus.bootstrap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import mxc.demo.campus.domain.Admin;
import mxc.demo.campus.domain.Course;
import mxc.demo.campus.domain.Lecturer;
import mxc.demo.campus.domain.Student;
import mxc.demo.campus.repositories.UserRepository;
import mxc.demo.campus.services.CourseService;
import mxc.demo.campus.services.LecturerService;
import mxc.demo.campus.services.MoneyService;
import mxc.demo.campus.services.StudentService;

/**
 * Adding some start-up data into our H2 in-memory database.
 * This is a development artifact and should not be delivered into a PRoduction environment (profiles).
 */
@Component
public class CampusLoader implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger logger = Logger.getLogger(CampusLoader.class);

	// Development only!!
	public static final String ADMIN_USERNAME = "adminUser";
	public static final String ADMIN_PASSWORD = "admin";
	
	private static final String[] titles = {"Prof.", "Dr"};
	
	@Autowired
	private MoneyService moneyService;
	
	@Autowired
	@NotNull
	private UserRepository userRepo;
	
	@Autowired
	@NotNull
	private StudentService studentService;
	
	@Autowired
	@NotNull
	private LecturerService lecturerService;
		
	@Autowired
	@NotNull
	private CourseService courseService;
	
	//private MonetaryAmountFactory<?> amountFactory = Monetary.getDefaultAmountFactory().setCurrency("AUD");

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		
		List<Student> persistedStudents = new ArrayList<>();
		{
			Student[] students = createStudents();	
			logger.info("Created " + students.length + " students, now saving to repository");
			for ( Student student : students ) {
				Student persistedStudent = studentService.save(student);
				System.out.println("Saved " + persistedStudent);
				persistedStudents.add(persistedStudent);
			}
		}
		
		List<Course> persistedCourses = new ArrayList<>();
		{
			Course[] courses = createCourses();
			logger.info("Created " + courses.length + " courses, now saving to repository");
			for ( Course course : courses ) {
				Course persistedCourse = courseService.save(course); // Now has an primary key
				persistedCourses.add(persistedCourse);
			}
		}

		// Add first 3 students to 1st course
		Course course = persistedCourses.get(0);
		for ( int i = 0; i < 3; i++ )
			course = addStudentToCourse(persistedStudents.get(i), course);
		// Add next 2 student to 2nd course
		course = persistedCourses.get(1);
		for ( int i = 3; i < 5; i++) {
			Student student = persistedStudents.get(i);
			student = studentService.getStudentById(student.getId());
			course = addStudentToCourse(student, course);
		}
		// Add first 2 students to 3rd course
		course = persistedCourses.get(2);
		for ( int i = 0; i < 2; i++ ) {
			Student student = persistedStudents.get(i);
			student = studentService.getStudentById(student.getId());
			course = addStudentToCourse(student, course);
		}

		// Add 1 student to 4th course
		course = persistedCourses.get(3);
		Student student = persistedStudents.get(3);
		student = studentService.getStudentById(student.getId());
		course = addStudentToCourse(student, course);

		List<Lecturer> persistedLecturers = new ArrayList<>();
		{
			Lecturer[] lecturers = createLecturers();
			logger.info("Created "+ lecturers.length + "lecturers, now saving to repo");
			for ( Lecturer lecturer : lecturers ) {
				System.out.println("Saving " + lecturer.getUserId() + " with email " + lecturer.getEmailAddress());
				Lecturer persistedLecturer = lecturerService.save(lecturer);
				persistedLecturers.add(persistedLecturer);
			}
		}
		
		// Get lecturers to manage courses.
		int n = 0;
		for ( Course pc : persistedCourses ) {
			course = courseService.getCourseById(pc.getId());
			Lecturer lecturer = persistedLecturers.get(n++);
			lecturer = lecturerService.getLecturerById(lecturer.getId());
			n %= persistedLecturers.size();
			addLecturerToCourse(lecturer,course);
		}
		
		// Add a single Admin user.
		{
			String userId = ADMIN_USERNAME;
			String password = ADMIN_PASSWORD;
			Admin admin = new Admin(userId, password, "Jo", "Smith", "jsmith@someprovider.com");
			userRepo.save(admin);
		}
	}
	
	private Course addLecturerToCourse(Lecturer lecturer, Course course) {
		course.setLecturer(lecturer);
		return courseService.save(course);
	}
	
	private Course addStudentToCourse(Student student, Course course) {
		course.addStudent(student);
		return courseService.save(course);
	}
	
	private Course[] createCourses() {
				
		Course[] courses = {
				createCourse("Computer Science 1", createMoney(250))
				, createCourse("Computer Science 2", createMoney(270))
				, createCourse("Maths 1", createMoney(240))
				, createCourse("Maths 2", createMoney(245))
				, createCourse("Physics 1", createMoney(265.30))
				, createCourse("Biology 1", createMoney(240.50))
				, createCourse("Music 1", createMoney(204))
				, createCourse("Philosophy 1", createMoney(192.50))
				, createCourse("Philosophy 2", createMoney(220.50))
				, createCourse("Civil Engineering 1", createMoney(250))
				, createCourse("Civil Engineering 2", createMoney(270))
				, createCourse("English 1", createMoney(190))
				, createCourse("English 2", createMoney(190))
				, createCourse("English 3", createMoney(200))
				, createCourse("Mandarin Chinese 1", createMoney(190))
				, createCourse("Archeology 1", createMoney(250))
				, createCourse("Chemistry 1", createMoney(250))
				, createCourse("Geology 1", createMoney(240))
		};
		
		String[] descriptions = {"A terrific course", "An even better course", "Possibly the best",
				"Nobody likes this one", "This one is mandatory", "Controversial",
				"Strugging to get takers"} ;
		
		for ( int i = 0; i < courses.length; i++ ) {
			int idx = i% descriptions.length;
			String description = descriptions[idx];
			courses[i].setDescription(description);
		}
		
		return courses;
	}
	
	private Student[] createStudents() {
		
		Student[] students = {
				createStudent("Julie","Banks",44.20)
				,createStudent("Matthew","Craig",7.50)
				,createStudent("Charles","Smith",0)
				,createStudent("Melissa", "West", 0)
				,createStudent("Ashara","Singh", 0)
				,createStudent("Julie","Nguyen", 0)
				,createStudent("Michael","Lee", 0)
				,createStudent("Daniel","Trident", 0)
				,createStudent("Philip","Summers", 0)
				,createStudent("Alison","Combs", 0)
				,createStudent("Felicity","Myers", 0)
				,createStudent("Ananta","Saer", 0)
				,createStudent("Kylie","Mason", 0)
				,createStudent("Richard","Hope", 0)
				,createStudent("Timothy","Hope", 11)
				,createStudent("Shane","Williams", 15)
				,createStudent("Simon","Myers", 5)
				,createStudent("David","Peterson", 17)
				,createStudent("Damien","Summers", 16)
				,createStudent("Jessica","Smith", 15)
				,createStudent("Rachel","Brown", 0)
				,createStudent("Rebecca","Brown", 0)
				,createStudent("Annette","Green", 14)
				,createStudent("Xavier","Paris", 13)
				,createStudent("Yvonne","Winters", 0)
				,createStudent("Colin","Winters", 0)
				,createStudent("Fabian","East", 12)
				,createStudent("Gregory","London", 11)
				,createStudent("Jeremy","Keswick", 0)
				,createStudent("Kevin","Anderson", 0)
				,createStudent("Luke","Burns", 10)
				,createStudent("Sylvia","Burns", 9)
				,createStudent("Theresa","Leader", 0)
				,createStudent("Kieran","Lyle", 0)
				//,createStudent("","",0)
			};
						
			return students;
	}
	
	private Lecturer[] createLecturers() {
		
		Lecturer[] lecturers = {
				createLecturer("Graham", "O'Reilly", 120000)
				,createLecturer("Neil", "Wentworth", 110000)
				,createLecturer("Janette", "Simons", 120000)
				,createLecturer("Phyllis", "Ang", 125000)
				,createLecturer("Robert", "Zhang", 125000)
				,createLecturer("Theresa", "Wilson", 120000)
				,createLecturer("Richard", "Chen", 110000)
				,createLecturer("Francis", "Ericson", 110000)
				,createLecturer("Ahmed", "Rahal", 115000)
				,createLecturer("Miriam", "Salib", 120000)
				,createLecturer("Craig", "Phillips", 120000)
				,createLecturer("Matthew", "St John", 120000)
				,createLecturer("Daphne", "Mason", 120000)
				,createLecturer("Leanne", "Seymour", 120000)
				,createLecturer("Sarah", "Nahas", 120000)
				,createLecturer("Gordon", "Liu", 120000)
				,createLecturer("Michel", "Lamour", 120000)
				,createLecturer("Gina", "Bianci", 120000)
				,createLecturer("Emily", "Peters", 120000)
				,createLecturer("Adrian", "Lamb", 120000)
		};
		
		return lecturers;
	}
	
	private Lecturer createLecturer(String firstName, String lastName, Number _salary) {
		
		String emailAddress = createEmail(firstName, lastName, "otherprovider.com");

		int idx = Math.abs((firstName + lastName).hashCode()) % 2;
		String title = titles[idx];
		
		BigDecimal salary = createMoney(_salary);
		
		String userId = UserCredentialsGenerator.generateUserName(firstName, lastName);
		String password = UserCredentialsGenerator.generatePassword(firstName, lastName);
		
		return new Lecturer(userId, password, title, firstName, lastName, emailAddress, salary);
	}
	
	private String createEmail(String firstName, String lastName, String domain) {
		String _firstName = firstName.replace(' ', '_');
		String _lastName = lastName.replace(' ', '_');		
		String emailAddress = _lastName + _firstName.toLowerCase().charAt(0)
				+ '@' + domain;
		return emailAddress;
	}
	
	private Student createStudent(String firstName, String lastName, Number amountPaid) {
		
		String emailAddress = createEmail(firstName, lastName, "someProvider.com");
		
		boolean isExternal = (firstName + lastName).hashCode() % 2 == 0;
		BigDecimal moneyAmountPaid = createMoney(amountPaid);
		String userId = UserCredentialsGenerator.generateUserName(firstName, lastName);
		String password = UserCredentialsGenerator.generatePassword(firstName, lastName);
		
		return new Student(userId, password, firstName, lastName, emailAddress, isExternal, moneyAmountPaid);
	}
	
	private Course createCourse(String name, BigDecimal money) {
		
		Course course = new Course();
		course.setName(name);
		course.setCost(money);
		return course;
	}
	
	private BigDecimal createMoney(Number amount) {
		return moneyService.createMoney(amount);
	}

}
