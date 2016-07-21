package mxc.demo.campus.configuration;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import mxc.demo.campus.domain.UserRole;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger logger = Logger.getLogger(CustomLoginSuccessHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		logger.debug("defaulttargeturl = " + this.getDefaultTargetUrl());
		
		// Aother thing you can do here is get the HttpSession from the request and set some Session attributes,
		// just as the user's username, role, email and so on. 
		// We are not doing that -- we're just redirecting based on the user's role(s).
		
	       String targetUrl = determineTargetUrl(authentication);
	       
	        if (response.isCommitted()) {
	        	logger.warn("Response has already been committed. Unable to redirect to " + targetUrl);
	            return;
	        }
	        logger.debug("redirecting as " + targetUrl);
	        
	       // this.setUseReferer(true);
	        
	        redirectStrategy.sendRedirect(request, response, targetUrl);
	}
	
    /*
     * Extracts the roles of user trying to log in, whose credentials have
     * already been validated, and returns the appropriate URL for their
     * role.
     */
    protected String determineTargetUrl(Authentication authentication) {
 
		// Get the authorities that have been granted to the current user.
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		if ( authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(UserRole.Student.getWithRolePrefix())) ) {
			return "/student";
		} else if ( authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(UserRole.Lecturer.getWithRolePrefix())) ) {
			return "/lecturer";
		} else if ( authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(UserRole.Admin.getWithRolePrefix())) ) {
			return "/admin";
		} else {
        	return "/accessdenied";
        }
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
 
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
}
