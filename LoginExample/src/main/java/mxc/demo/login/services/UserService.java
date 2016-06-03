package mxc.demo.login.services;

import java.util.Optional;

import mxc.demo.login.domain.User;

public interface UserService {
	
	Optional<User> getUserById(long id);
	
	Optional<User> getUserByUserName(String userId);

    Iterable<User> getUsers();
}
