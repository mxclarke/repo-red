package mxc.demo.masterdetailpaging.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * Sets up Spring Security for our app. I've made a couple of changes to the 
 * default.
 *
 */

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		// This will allow us to run local Tomcat without authorisation, so we don't
    	// have to supply credentials when we browse to localhost:8080
		// Normally, we would set up authorisation instead.
        httpSecurity.authorizeRequests().antMatchers("/").permitAll();
        
        // Cross-Site Request Forgery (CSRF) is a type of attack that occurs when a malicious 
        // Web site, email, blog, instant message, or program causes a user's Web browser to 
        // perform an unwanted action on a trusted site for which the user is currently 
        // authenticated. By default, Spring Security blocks any such requests unless the
        // appropriate handshake mechanism has been set up. In short, Spring sends a token
        // to your site and, if the site allegedly posts back to the server, it must supply
        // that original token as proof of identity. See here:
        // http://stackoverflow.com/questions/32567812/expected-csrf-token-not-found-has-your-session-expired-spring-security-csr
        httpSecurity.csrf()
        .csrfTokenRepository(csrfTokenRepository());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.ignoring().antMatchers("/css/**");
	}

	private CsrfTokenRepository csrfTokenRepository() { 
	    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository(); 
	    repository.setSessionAttributeName("_csrf");
	    return repository; 
	}
}
