package mxc.demo.masterdetailpaging.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the home page.
 *
 */
@Controller
public class IndexController {
	
	@RequestMapping("/")
	public String index() {
		return "index";
	}

}
