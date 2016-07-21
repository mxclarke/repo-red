package mxc.demo.campus.controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mxc.demo.campus.domain.Course;
import mxc.demo.campus.services.BeanManagerService;
import mxc.demo.campus.services.CourseService;

/**
 * Controls access to course-related pages.
 * TODO: courses are under construction and not yet tested.
 * 
 * <ul>
 * <li> "/course" = redirect:course/{id}
 * <li> "/courses/new" = /course/courseform: editable view for a new course</li>
 * <li> "/course/{id} = /course/courseshow: RO view of course</li>
 * <li> "/course/edit/{id} = /course/courseform: editable view of course</li>
 * <li> "/course/delete/{id} = redirect:/courses</li>
 * </ul>
 */
@Controller
public class CoursesController {
	
	private static final Logger logger = Logger.getLogger(CoursesController.class);

	private final CourseService courseService;
	
	private final BeanManagerService beanManagerService;

	@Autowired
    public CoursesController(CourseService courseService, BeanManagerService beanManagerService) {
		this.courseService = courseService;
		this.beanManagerService = beanManagerService;
	}
	
	/**
	 * Handles request for the form to create a new course.
	 * Only admins can create courses.
	 * @param model
	 * @return the view "/course/courseform"
	 */
    @RequestMapping("/course/new")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public String newCourse(Model model){
    	logger.debug("Accessing the form to create a new course");    	
        model.addAttribute("course", new Course());
        return "/course/courseform";
    }
    
    /**
     * Handles request to display a RO view of a course.
     * @param id the unique ID of the course
     * @param model
     * @return the view "courseshow"
     */
	@RequestMapping(value = "/course/{id}", method = RequestMethod.GET)
    public String getCourseView(@PathVariable Integer id, Model model) {
    	logger.debug("Accessing the form to display existing course with id" + id);
    	Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "/course/courseshow";
    }
    
	/**
	 * Handles request to display an editing form for a course.
	 * Only admins can edit courses.
	 * @param id the unique ID of the course
	 * @param model
	 * @return the view "/course/courseform"
	 */
    @RequestMapping(value="/course/edit/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_Admin')")
    public String editForm(@PathVariable Integer id, Model model) {
    	logger.debug("Accessing the form to edit existing course with id" + id);

    	Course course = courseService.getCourseById(id);
    	logger.debug("Course to edit: " + course);
        model.addAttribute("course", courseService.getCourseById(id));
        
        return "/course/courseform";
    }
    
    /**
     * THis method handles the creation of new courses, and updating existing
     * courses. 
     * 
     * The following should also be considered for complex types:
     * http://dozer.sourceforge.net/documentation/about.html
     * 
     * For information on @ModelAttribute, see here:
     * http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-modelattrib-method-args
     * 
     */
    @RequestMapping(value = "/course", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_Admin')")
    public String saveOrUpdateCourse(@ModelAttribute @Valid Course courseData, BindingResult result) {
    	logger.debug("Savd or update the course " + courseData);
    	
        if (result.hasErrors()) {
        	// You can manipulate the errors, and if you need to add more to display to the user, see here:
        	// http://stackoverflow.com/questions/12107503/adding-error-message-to-spring-3-databinder-for-custom-object-fields
        	List<ObjectError> errors = result.getAllErrors();
        	errors.forEach(logger::debug);
        	// TODO return look at <error> on form
            return "/course/courseform";
        } else {
        	 // User's input is acceptable.
        	// TODO shift most of this down into the service layer.
        	Course course = courseService.getCourseById(courseData.getId());
        	if ( course == null ) {
        		// It's a new one. The data so far will be everything in courseData.
        		course = courseData;
        	} else {
        		// It's an update.
        		beanManagerService.copyOnlyProperties(courseData, course,  "name", "description", "cost");
        	}

        	course = courseService.save(course);
        	logger.debug("Updated course is " + course);
        	
        	// TODO
        	return "redirect:/course/" + course.getId();
        }
    }
        
	/**
	 * Handles request to delete a course and return to the main list.
	 * Only admins can delete courses.
	 * @param  id the unique ID of the course
	 * @return the redirected view to the path "/courses"
	 */
    @RequestMapping("/course/delete/{id}")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public String delete(@PathVariable Integer id) {
    	logger.debug("Requesting deletion of the course with id" + id);
    	courseService.delete(id);
    	return "redirect:/admin";
        //return "redirect:/course/courses";
    }
}
