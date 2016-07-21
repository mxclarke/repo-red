package mxc.demo.campus.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Exceptions thrown from controller methods are redirected here, which in turn
 * redirects to DEFAULT_ERROR_VIEW.
 * 
 * THanks to http://stackoverflow.com/questions/23580509/how-to-write-a-proper-global-error-handler-with-spring-mvc-spring-boot
 */
@ControllerAdvice
public class ExceptionHandlerController {

    private static final String DEFAULT_ERROR_VIEW = "/error/error";
    private static final String ACCESS_DENIED_VIEW = "/error/accessdenied";
    
	private static final Logger logger = Logger.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) {
    	
    	logger.error("Controller exception from " + request.getRequestURL() 
    		+ " with message " + e.getMessage(), e);
   
    	// TODO put this in its own controller method -- let Spring do the matching
    	ModelAndView mav = (e instanceof AccessDeniedException)
    			? new ModelAndView(ACCESS_DENIED_VIEW)
    			: new ModelAndView(DEFAULT_ERROR_VIEW);

        mav.addObject("datetime", new Date());
        mav.addObject("exception", e);
        mav.addObject("url", request.getRequestURL());
        return mav;
    }
}
