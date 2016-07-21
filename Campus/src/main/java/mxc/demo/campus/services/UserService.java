package mxc.demo.campus.services;

import mxc.demo.campus.domain.User;

public interface UserService {

	User getUserById(long id);
	
	/**
	 * Required by Spring Security for authenticating a user.
	 * @param userId
	 * @return
	 */
	User getUserByUserName(String userId);
	
	User save(User user);
}
