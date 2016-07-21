package mxc.demo.campus.services;

import java.util.Collection;

import org.apache.log4j.Logger;

//import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mxc.demo.campus.domain.User;

/**
 * Spring Security uses this service to check a user's credentials and
 * authorisation. This class must be registered in the configured
 * WebSecurityConfigurerAdapter.
 * 
 * @see mxc.demo.campus.services.configuration.SecurityConfiguration
 */
@Service
public class SpringUserDetailsService implements UserDetailsService {
	
	private static final Logger logger = Logger.getLogger(SpringUserDetailsService.class);

    private final UserService userService;

    @Autowired
    public SpringUserDetailsService(UserService userService) {
        this.userService = userService;
    }
    
    @Transactional
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    	User user = userService.getUserByUserName(userName);
    	if ( user == null ) {
    		throw new UsernameNotFoundException(String.format("User with userId=%s was not found", userName));
    	}
				
		return adapt(userName, user);
	}

    private UserDetails adapt(String userName, User user) {
		String password = user.getPassword();
		boolean enabled = Boolean.TRUE;
		boolean accountNonExpired = Boolean.TRUE;
		boolean credentialsNonExpired = Boolean.TRUE;
		boolean accountNonLocked = Boolean.TRUE;
		
		String role = user.getRole().getWithRolePrefix();
		Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(role);
		
		logger.debug("SpringUserDetailsService authenticating user " + user);
		org.springframework.security.core.userdetails.User springUser = new
				org.springframework.security.core.userdetails.User(userName,
						password,
						enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
						authorities);

		return springUser;
    }
}
