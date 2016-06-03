package mxc.demo.login.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mxc.demo.login.domain.User;
import mxc.demo.login.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
	@Override
	public Optional<User> getUserById(long id) {
		return Optional.ofNullable(userRepository.findOne(id));
	}

	@Override
	public Optional<User> getUserByUserName(String userId) {
		return userRepository.findByUserId(userId);
	}

	@Override
	public Iterable<User> getUsers() {
		return userRepository.findAll();
	}
}
