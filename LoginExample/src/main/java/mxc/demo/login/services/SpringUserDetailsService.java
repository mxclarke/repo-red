package mxc.demo.login.services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import mxc.demo.login.domain.User;

/**
 * Spring Security uses this service to check a user's credentials and
 * authorisation. This class must be registered in the configured
 * WebSecurityConfigurerAdapter.
 * @see mxc.demo.login.services.configuration.SecurityConfiguration
 */
@Service
public class SpringUserDetailsService implements UserDetailsService {
	
	private static final Logger logger = Logger.getLogger(SpringUserDetailsService.class);
	
    private final UserService userService;

    @Autowired
    public SpringUserDetailsService(UserService userService) {
        this.userService = userService;
    }
    
    // This method is annotated with @Transactional because entity objects have 
    // lazy fetching and may need to do so.
    @Transactional
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		User user = userService.getUserByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with userId=%s was not found", userName)));
		
		return adapt(userName, user);
	}

    private UserDetails adapt(String userName, User user) {
		String password = user.getPassword();
		boolean enabled = Boolean.TRUE;
		boolean accountNonExpired = Boolean.TRUE;
		boolean credentialsNonExpired = Boolean.TRUE;
		boolean accountNonLocked = Boolean.TRUE;
		
		// Do you need this prefix? See here: 
		// http://docs.spring.io/spring-security/site/docs/4.1.0.RELEASE/reference/htmlsingle/#appendix-faq-role-prefix
		String role = "ROLE_" + user.getRole().name();
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
		
		System.out.println("SpringUserDetailsService authenticating user " + user);
		org.springframework.security.core.userdetails.User springUser = new
				org.springframework.security.core.userdetails.User(userName,
						password,
						enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
						authorities);
		logger.info("UserDetails object = " + springUser);
		
		return springUser;
    }
}
