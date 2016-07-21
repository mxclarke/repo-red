package mxc.demo.campus.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mxc.demo.campus.domain.UserRole;
import mxc.demo.campus.repositories.UserRepository;

@Service("campusSecurityService")
public class SecurityServiceImpl implements SecurityService {

	private final UserRepository userRepo;

	private final UserDetailsService springUserDetailsService;
	
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public SecurityServiceImpl(UserRepository userRepo, UserDetailsService springUserDetailsService,
			BCryptPasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.springUserDetailsService = springUserDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public boolean canAccessUser(User currentUser, Long userId) {
		if (currentUser == null || userId == null) {
			return false;
		} else {
			// Get the authorities that have been granted to the current user.
			Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
			if (authorities.stream()
					.anyMatch(a -> a.getAuthority().equalsIgnoreCase(UserRole.Admin.getWithRolePrefix()))) {
				// Administrator can access any user.
				return true;
			} else {
				// Check the user's ID.
				mxc.demo.campus.domain.User user = userRepo.findOne(userId);
				return (user != null && user.getUserId().equals(currentUser.getUsername()));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.SecurityService#checkPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkPassword(final String username, final String password) {
		if ( username == null || password == null ) {
			return false;
		} else {
			UserDetails userDetails = springUserDetailsService.loadUserByUsername(username);
			String encodedPassword = userDetails.getPassword();
			return passwordEncoder.matches(password, encodedPassword);
		}
	}

	@Override	
	public boolean isCurrentUserAdmin(Authentication authentication) {
		return isCurrentUserAdmin(authentication.getAuthorities());
	}
	
	@Override
	public boolean isCurrentUserAdmin(Collection<? extends GrantedAuthority> authorities) {
		String adminRoleStr = UserRole.Admin.getWithRolePrefix();
		return (authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(adminRoleStr)));
	}
	
	@Override
	public boolean isCurrentUserAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && isCurrentUserAdmin(authentication);
	}

}
