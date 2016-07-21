package mxc.demo.campus.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mxc.demo.campus.domain.Student;
import mxc.demo.campus.exception.NoSuchUserException;
import mxc.demo.campus.exception.ParseException;
import mxc.demo.campus.services.BeanManagerService;
import mxc.demo.campus.services.SecurityService;
import mxc.demo.campus.services.StudentService;
import mxc.demo.campus.view.StudentForm;
import mxc.demo.campus.view.StudentView;
import mxc.demo.campus.view.ViewCourse;

/**
 * Controls access to student-related pages.
 * 
 * <ul>
 * <li> student: gets the view for a student (student home page)<\li>
 * <li> student/{id}: gets the view for a student (owner or admin)<\li>
 * <li> /edit/{id}: gets the form for editing a student (owner or admin)<\li>
 * <li> /update/{id}: handles post request for student edits (owner or admin)<\li>
 * <li> /delete/{id}: handles request to delete a student (admin)<\li>
 * </ul>

 * Still to implement:
 * new: to return the form for creating a student
 */
@Controller
@RequestMapping("/student")
//@SessionAttributes("sessionStudent")
public class StudentsController {

	private static final Logger logger = Logger.getLogger(StudentsController.class);
	
	/** Restrictions on incoming student update fields for admin */
	private static final String[] adminDisallowedFields = {"id", "userId"};
	/** Restrictions on incoming student update fields for student owner */
	private static final String[] ownerAllowedFields = {"emailAddress", "external", "enrolments"};

	private final StudentService studentService;

	private final BeanManagerService beanManagerService;
	
	private final SecurityService securityService;

	@Autowired
	public StudentsController(StudentService studentService, BeanManagerService beanManagerService,
			SecurityService securityService) {
		this.studentService = studentService;
		this.beanManagerService = beanManagerService;
		this.securityService = securityService;
	}
	
	/**
	 * Handles request from student or admin to display the RO view of a student.
	 * If the current user is a student, must be the same as the student
	 * being requested.
	 * 
	 * The view contains their details, including the courses in which they 
	 * are enrolled.
	 * 
	 * @param id
	 *            the user ID of the student
	 * @return "/student/studentview" with student data attached
	 */
	
	@RequestMapping(value = "/{id}")
	@PreAuthorize("@campusSecurityService.canAccessUser(principal, #id)")
    public String getStudentView(@PathVariable Integer id, Model model) {
    	logger.debug("Accessing the form to display existing student with id" + id);
    	    	
    	Student student = studentService.getStudentById(id);
		if ( student != null ) {
			return getStudentModelAndView(student, model);
		} else {
			// This should be logged by ExceptionHandlerController
			throw new NoSuchUserException("User with id " + id + " cannot be located");
		}
    }
    
	/**
	 * Handles request from a student to display his/her home page, which
	 * is also the RO view of the student.
	 * 
	 *  The view contains their
	 * details, including the courses in which they are enrolled. It is RO, but
	 * has an edit button to take them to an editing page.
	 * 
	 * @param principal
	 *            current user (the student)
	 * @return "/student/studentview" with student data attached
	 */
	@RequestMapping("")
	@PreAuthorize("hasRole('ROLE_Student')")
	public String getStudentView(Principal principal, Model model) {
		logger.debug("Accessing main view for student " + principal.getName());

		// Get current user's ID, and redirect to the student form.
		String userId = principal.getName();

		Student student = studentService.getStudentByUserNameWithCourses(userId);
		if (student != null) {
			return getStudentModelAndView(student, model);
		} else {
			// This should be logged by ExceptionHandlerController
			throw new NoSuchUserException("User " + userId + " cannot be located");
		}
	}
	
	/**
	 * @param student
	 * @param model
	 * @return the model and RO view for the given student
	 */
	private String getStudentModelAndView(Student student, Model model) {
		// This is an entity, but we only want the view, to avoid cyclic dependencies
		// (THymeleaf) and only send across what we want.
		try {
			StudentView studentView = new StudentView();
			beanManagerService.copyPropertiesExcept(student, studentView);
    		List<ViewCourse> courses = beanManagerService.mapToInterface(student.getCourses(), ViewCourse.class);
    		studentView.setCourses(courses);
    		model.addAttribute("student", studentView);
    		return "/student/studentview";
  		} catch ( IOException e ) {
			throw new ParseException("There was an error in the server while trying to return a page for the student " 
					+ student.getUserId(), e);
		}		
	}

	/**
	 * Handles request for an editor for the given student. Access is restricted
	 * according to the named service method.
	 * 
	 * @param id
	 *            the unique id of a student
	 * @param model
	 * @return the view "student/studentform"
	 */
	@RequestMapping(value = "/edit/{id}", 
			method = {RequestMethod.GET, RequestMethod.POST})
	@PreAuthorize("@campusSecurityService.canAccessUser(principal, #id)")
	public String editForm(@PathVariable Long id, Model model) {
		logger.debug("Accessing the form to edit existing student with id" + id);
		
		StudentForm studentForm = studentService.getStudentByIdWithCourses(id);
		
		if ( studentForm == null ) {
			// This should be logged by ExceptionHandlerController
			throw new NoSuchUserException("User with id " + id + " cannot be located");
		} else {
			model.addAttribute("student", studentForm);
			model.addAttribute("sessionStudent", studentForm);
			
			return "student/studentform";
		}
	}
	
	/**
	 * Configure auto-binding. Currently trims whitespace from incoming String values.
	 * 
	 * Could be used to restrict incoming fields, in which case session attributes would
	 * also be needed to copy back in the event of form error.
	 * 
	 * Can also be used to set validators. This application is validating via custom annotations.
	 */
	 @InitBinder
	 public void initBinder(WebDataBinder binder, WebRequest request) {
		 
		 // TODO I might reinstate this as well as session attributes
		 // since the alternative is a number of (unnecessary) hidden inputs
		 // on the client form

//		 String adminRole = UserRole.Admin.getWithRolePrefix();
//		 String studentRole = UserRole.Student.getWithRolePrefix();
//		 if ( request.isUserInRole(adminRole) ) {
//			 binder.setDisallowedFields("id", "userId");
//		 } else if ( request.isUserInRole(studentRole) ) {
//			 binder.setAllowedFields("emailAddress", "external", "enrolments*");
//		 }
		 
		 binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	 }
	 

	/**
	 * Update an existing student, but only if current user is admin or 
	 * the student being updated.
	 * 
	 * Expects all fields to be returned from form, including those that are
	 * RO.
	 * 
	 * @param studentData
	 * @param result
	 * @returns redirection to "/admin" if principal is admin, or "/student" if
	 *          principal is this same student, or else returns view
	 *          "student/studentform" on validation error.
	 */
	 @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	@PreAuthorize("@campusSecurityService.canAccessUser(principal, #id)")
	public String updateStudent(@PathVariable Long id,
				@ModelAttribute("student") @Valid StudentForm studentForm, BindingResult result,
				Authentication authentication,
				RedirectAttributes redirectAttributes) {
		 logger.debug("Saving or updating a student from data " + studentForm);
		
		if ( result.hasErrors() ) {
			result.getAllErrors().forEach(logger::debug);
			return "student/studentform";
		} else {
			StudentForm cleanForm = new StudentForm();
			cleanForm.setId(id);
			boolean isAdmin = securityService.isCurrentUserAdmin(authentication);
			if ( isAdmin ) {
				beanManagerService.copyPropertiesExcept(studentForm, cleanForm, adminDisallowedFields);
			} else {
				beanManagerService.copyOnlyProperties(studentForm, cleanForm, ownerAllowedFields);
			}
			
			// Now save those fields for which the user has permissions.
			Student student = studentService.saveOrUpdateStudent(cleanForm);
			
			// The view may or may not display a message to this effect.
			redirectAttributes.addFlashAttribute("updateMessage", "Student successfully modified");
			
			logger.info("Sucessfully updated student " + student);
			
			return "redirect:/student/" + student.getId();
		}
	}
	 
	 @RequestMapping("/delete/{id}")
	 @PreAuthorize("hasRole('ROLE_Admin')")
	 public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		 logger.info("Deleting student with id " + id);
		 
		 studentService.delete(id);
		 logger.info("Sucessfully deleted student with id " + id);
		 
		 // The view may or may not display a message to this effect.
		 redirectAttributes.addFlashAttribute("updateMessage", "Student successfully deleted");
			
		 // Reload the admin's page, which lists all the students.
		 return "redirect:/admin";
	 }

}
