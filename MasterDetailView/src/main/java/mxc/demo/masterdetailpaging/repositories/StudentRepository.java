package mxc.demo.masterdetailpaging.repositories;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import mxc.demo.masterdetailpaging.domain.Student;

/**
 * The JPA repository for the Student data.
 * 
 * In most cases your Spring repository will only require the definition of an
 * interface -- you don't have to implement it, and you get a great deal of
 * functionality for free.
 * 
 * Note that I started out with CrudRepository, which is suitable for most of
 * the things you want to do, including, of course, CRUD operations. However,
 * for the Datatable's paging etc, I needed the extra functionality of
 * PagingAndSortingRepository, which is a sub-interface of CrudRepository.
 *
 * But you want to be able to filter as well. So for this we also extend
 * QueryDslPredicateExecutor, after adding the required dependencies to the pom.
 * Unfortunately, this is not straightforward and in particular has issues
 * around m2e, so see the pom for more details.
 *
 */
public interface StudentRepository
		extends PagingAndSortingRepository<Student, Integer>, QueryDslPredicateExecutor<Student> {

	// Custom query method, which you do not have to implement, since the naming
	// convention has been followed.
	Student findByLastName(String lastName);
}
