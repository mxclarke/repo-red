package mxc.demo.masterdetailpaging.controllers;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mxc.demo.masterdetailpaging.domain.Student;
import mxc.demo.masterdetailpaging.dto.JQueryDatatablesRequest;
import mxc.demo.masterdetailpaging.services.StudentService;
import org.springframework.util.Assert;

/**
 * Controller for accessing Student data, rather than the views.
 * 
 * Note: @RestController adds @ResponseBody to @Controller.
 * The @ResponseBody annotation, typically used by web-services, tells Spring MVC 
 * to return an object (i.e. write the object into the response body)
 * rather than rendering the model into a view and returning the view.
 * 
 * Spring uses message converters. In this case, Jackson 2 is in the classpath
 * (thanks to spring-boot-starter-web in pom), so the Jackson message converter
 * will handle the conversion to JSON to be returned to the client.
 */

@RestController
public class StudentsRestController {

	private final Logger logger = Logger.getLogger(StudentsRestController.class);
	
	private final StudentService studentService;
	
	@Autowired
	public StudentsRestController(StudentService studentService) {
		Assert.notNull(studentService);
		this.studentService = studentService;
	}

	/**
	 * Handles a POST request from a JQuery Datables client, returning a page of
	 * Student objects. Handles filtering and sorting as well as paging.
	 * 
	 * @param request information, including paging information, posted from the 
	 * JQuery Datatables client, indicating page, search and ordering information
	 * for the next required page of data.
	 * 
	 * @return the data (to be converted into JSON) expected by JQuery Datatables, for the 
	 * next filtered and sorted page of students, this being a subset of the total list 
	 * of Student objects
	 */
	@RequestMapping(value = "/students/getPagedStudents", method=RequestMethod.POST)
	public Map<String, Object> getStudents(
			@RequestBody final JQueryDatatablesRequest request) {
		
		logger.info("Accessed StudentsRestController.getStudents");
		
		Assert.notNull(request);

		// Starting index for the requested page.
		int start = request.getStart();
		// Number of items for the requested page.
		int length = request.getLength();
		// This is a synchronisation value that just needs to be returned to the client.
		int draw = request.getDraw();
		logger.debug("Start = " + start + ", length = " + length + ", draw = " + draw);
		
		// Access the repo to get the next page of students, according to the filtering
		// and ordering criteria.
		Page<Student> page = studentService.getStudents(start, length, 
				request.getColumns(), request.getOrders());
		logger.info("Page " + page + " returned from repo");
	     
		// Construct the results object to return to the client.
        Map<String,Object> returnMap = new HashMap<>();
  
        returnMap.put("data",page.getContent());
        returnMap.put("draw",draw);
        returnMap.put("recordsTotal",page.getTotalElements());
        returnMap.put("recordsFiltered",page.getTotalElements());
 
        return returnMap;		
	}
}
