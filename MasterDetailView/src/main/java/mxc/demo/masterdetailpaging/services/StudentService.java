package mxc.demo.masterdetailpaging.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mysema.query.types.Predicate;

import mxc.demo.masterdetailpaging.domain.Student;
import mxc.demo.masterdetailpaging.dto.ColumnDTO;
import mxc.demo.masterdetailpaging.dto.OrderDTO;

/**
 * The Student service, i.e. the business-logic layer between the controllers
 * and the data layer.
 */
public interface StudentService {
	
	Iterable<Student> getStudents();
	
	Page<Student> getStudents(Pageable pageable);
	
	Page<Student> getStudents(int start, int length, 
			List<ColumnDTO> columns, Iterable<OrderDTO> ordering);
	
	Page<Student> getStudents(Predicate predicate, Pageable pageable);
		
	Student getStudentById(int id);
	
	Student saveStudent(Student student);
	
	void deleteStudent(int id);
	
	Student getStudentByLastName(String lastName);
}
