package mxc.demo.campus.repositories;

import java.io.Serializable;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The generic JPA repository for any domain entities that might require
 * server-side paging..
 * 
 * In most cases your Spring repository will only require the definition of an
 * interface -- you don't have to implement it, and you get a great deal of
 * functionality for free.
 * 
 * The CrudRepository is suitable for most of the things you want to do, including, 
 * of course, CRUD operations. However, I needed the extra functionality of
 * PagingAndSortingRepository, which is a sub-interface of CrudRepository.
 *
 * But you want to be able to filter as well. So for this we also extend
 * QueryDslPredicateExecutor, after adding the required dependencies to the pom.
 * Unfortunately, this is not straightforward and in particular has issues
 * around m2e, so see my comments in the pom for more details.
 */
@NoRepositoryBean
public interface GenericPagingRepository<T, K extends Serializable> 
		extends PagingAndSortingRepository<T, K>
		, QueryDslPredicateExecutor<T> {

}
