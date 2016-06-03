package mxc.demo.login.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import mxc.demo.login.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByUserId(String userId);
}