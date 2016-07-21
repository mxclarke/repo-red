package mxc.demo.campus.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*
 * Maps paths to views.
 * 
 * @see mxc.demo.campus.controllers
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    	registry.addViewController("/").setViewName("home");
        
        registry.addViewController("/lecturer").setViewName("/lecturer/lecturerview");
        registry.addViewController("/admin/secondPage").setViewName("admin/anotheradminview");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/accessdenied").setViewName("error/accessdenied");
        registry.addViewController("/expired").setViewName("error/userexpired");
   
    }
}
