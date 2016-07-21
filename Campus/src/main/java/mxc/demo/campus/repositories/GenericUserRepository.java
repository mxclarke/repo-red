package mxc.demo.campus.repositories;

import org.springframework.data.repository.NoRepositoryBean;

import mxc.demo.campus.domain.User;

@NoRepositoryBean
public interface GenericUserRepository<T extends User> extends GenericPagingRepository<T, Long> {

	T findByUserId(String userId);
}
