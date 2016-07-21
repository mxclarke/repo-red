package mxc.demo.campus.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import mxc.demo.campus.services.CourseService;
import mxc.demo.campus.services.LecturerService;

/**
 * Controls access to the admin "home" screen.
 *
 * <ul>
 * <li>"/admin" = admin/adminview: admin home, showing views and controls
 *      for students, lecturers and courses</li>
 * </ul>
 */
@Controller
public class AdminController {
	
	private static final Logger logger = Logger.getLogger(AdminController.class);

	@Value("${campusName}")
	private String campusName;
	
	private final CourseService courseService;
	
	private final LecturerService lecturerService;
	
	/**
	 * With more recent versions of Spring you don't actually need the @Autowired
	 * annotation when using constructor DI, but I've left it in for reasons of
	 * clarity.
	 * @param courseService
	 * @param lecturerService
	 */
	@Autowired
	public AdminController(CourseService courseService, LecturerService lecturerService) {
		this.courseService = courseService;
		this.lecturerService = lecturerService;
	}

	/**
	 * Handles request for the main admin view, or admin "home". 
	 * 
	 * We don't need to send across the list
	 * of students, since the view sends a request for JSON paged data back to 
	 * this server. We only send across the list of courses and the list of
	 * lecturers, since these lists are considered to be short enough to not
	 * warrant server-side paging.
	 * @param model
	 * @return the view:  "admin/adminview" 
	 */
	@RequestMapping("/admin")
	@PreAuthorize("hasRole('ROLE_Admin')")
	public String getAdminView(Model model) {
		logger.debug("Accessing main admin view for campus " + campusName);

		model.addAttribute("campusName",campusName);
		
		model.addAttribute("courses", courseService.getCourses());
		
		model.addAttribute("lecturers", lecturerService.getLecturers());
		
		// We do not add students to the model because student access is paged, and will
		// be returned to the view via Ajax calls.
		
		return "admin/adminview";
	}
	

}
