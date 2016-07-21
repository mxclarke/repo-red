package mxc.demo.campus.services;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public interface SecurityService {
	
	boolean canAccessUser(User currentUser, Long userId);
	
	boolean checkPassword(String username, String password);
	
	boolean isCurrentUserAdmin(Authentication authentication);
	
	boolean isCurrentUserAdmin(Collection<? extends GrantedAuthority> authorities);
	
	boolean isCurrentUserAdmin();
}
