package mxc.demo.campus.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mxc.demo.campus.domain.User;
import mxc.demo.campus.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	private final UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;		
	}
	
	@Override
	public User getUserById(long id) {
		return userRepository.findOne(id);
	}

     
	/* (non-Javadoc)
	 * @see mxc.demo.campus.services.UserService#getUserByUserName(java.lang.String)
	 */
	@Override
	public User getUserByUserName(String userId) {

		return userRepository.findByUserId(userId);
	}	
	
	@Override
	@Modifying @Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

}
