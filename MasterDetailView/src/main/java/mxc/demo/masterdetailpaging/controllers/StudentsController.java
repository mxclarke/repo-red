package mxc.demo.masterdetailpaging.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StudentsController {

	private final Logger logger = Logger.getLogger(StudentsController.class);
	
	private static final String campus = "University of the Cliffs of Insanity";
		
	/**
	 * Action a request for the view showing the students' grid.
	 * @param model the model to which additional data for the view can be added
	 * @return the view
	 */
	@RequestMapping("/students/serverside-data")
	String studentsServerSide(Model model) {
		logger.info("Hit StudentsController.serverside-data");
	
		model.addAttribute("campus", campus);

		return "studentsServerSide";
	}
}
