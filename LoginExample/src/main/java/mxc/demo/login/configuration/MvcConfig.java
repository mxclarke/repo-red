package mxc.demo.login.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*
 * Maps paths to views.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	/**
	 * Stands in lieue of a Controller, since our path-to-view
	 * mappings are very simple.
	 */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    	registry.addViewController("/").setViewName("home");
        registry.addViewController("/pleb").setViewName("plebview");
        registry.addViewController("/admin").setViewName("admin/adminview");
        registry.addViewController("/admin/secondPage").setViewName("admin/anotheradminview");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/accessdenied").setViewName("error/accessdenied");
    }
}
