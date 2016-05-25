package mxc.demo.masterdetailpaging.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.google.common.collect.Iterables;

import mxc.demo.masterdetailpaging.domain.Student;
import mxc.demo.masterdetailpaging.dto.ColumnDTO;
import mxc.demo.masterdetailpaging.dto.OrderDTO;
import mxc.demo.masterdetailpaging.dto.SearchDTO;
import mxc.demo.masterdetailpaging.repositories.StudentRepository;

/**
 * Unit tests class StudentServiceImpl.
 * 
 * Currently tests paging
 * TODO test also filtering and ordering
 */
public class TestStudentService {

	// THe studentRepository is a dependency of the student service
	@Mock
	private StudentRepository studentRepository;

	private StudentService studentService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		assertNotNull(studentRepository);
		studentService = new StudentServiceImpl(studentRepository);
	}

	@Test
	public void testGetWithoutFilteringOrdering() {

		assertNotNull(studentService);

		testWithoutFilteringOrdering(15, 5); // numStudents, pageLen
		testWithoutFilteringOrdering(5, 5); // numStudents, pageLen
		// boundary condition
		testWithoutFilteringOrdering(11, 5); // numStudents, pageLen
		// boundary condition
		testWithoutFilteringOrdering(14, 5); // numStudents, pageLen
		// Test single element
		testWithoutFilteringOrdering(1, 3); // numStudents, pageLen
		testWithoutFilteringOrdering(0, 5); // numStudents, pageLen
		// Test empty list
		testWithoutFilteringOrdering(0, 5, new ArrayList<Student>(), 0);
	}

	private void testWithoutFilteringOrdering(int numStudents, int pageLen, List<Student> students, int pageIndex) {

		Pageable pageable = new PageRequest(pageIndex, pageLen);

		int fromIndex = pageLen * pageIndex; // inclusive
		int toIndex = pageLen * (pageIndex + 1);
		toIndex = Math.min(toIndex, numStudents); // exclusive
		System.out.println("fromidx " + fromIndex + ", toIn " + toIndex);
		List<Student> expectedContent = students.subList(fromIndex, toIndex);

		Page<Student> expectedPage = new PageImpl<>(expectedContent, pageable, numStudents);
		when(studentRepository.findAll(pageable)).thenReturn(expectedPage);

		Page<Student> actualPage = studentService.getStudents(pageable);

		assertNotNull(actualPage);
		System.out.println("Actual results from auxiliary method");
		actualPage.getContent().forEach(student -> System.out.println(student));

		Student[] expectedArray = expectedPage.getContent().toArray(new Student[0]);
		Student[] actualArray = actualPage.getContent().toArray(new Student[0]);
		assertArrayEquals(expectedArray, actualArray);
		assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());

		// Now check the main method -- this is the one that is called from the
		// UI
		// TO do this, model the JQuery Datatables input data
		boolean columnSearchable = Boolean.TRUE;
		boolean regex = Boolean.FALSE;
		SearchDTO columnSearch = new SearchDTO("", regex);
		boolean columnOrderable = Boolean.TRUE;
		List<ColumnDTO> columns = new ArrayList<>();
		columns.add(new ColumnDTO("studentId", "", columnSearchable, columnOrderable, columnSearch, regex));
		columns.add(new ColumnDTO("firstName", "", columnSearchable, columnOrderable, columnSearch, regex));
		List<OrderDTO> orderings = new ArrayList<>();

		// By default, Datatables loads with the first column in ascending order
		// orderings.add(new OrderDTO(0, "asc"));
		// Because we're passing in an ordering, we expect a different Pageable
		// to be called, i.e.
		// one with a Sort for that ordering:
		// List<Sort.Order> springColumnOrderings =
		// SpringDTOAdapter.instance.getColumnOrderings(columns, orderings);
		// Pageable sortedPageable = new PageRequest(pageIndex, pageLen, new
		// Sort(springColumnOrderings));
		// Need to change expected page so that the sorting occurs, and then:
		// when(studentRepository.findAll(pageable)).thenReturn(expectedPage);

		actualPage = studentService.getStudents(fromIndex, pageLen, columns, orderings);
		assertNotNull(actualPage);
		System.out.println("Actual results from principal method");
		actualPage.getContent().forEach(student -> System.out.println(student));
		assertArrayEquals(expectedArray, actualArray);
		assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
	}

	private void testWithoutFilteringOrdering(int numStudents, int pageLen) {
		List<Student> students = generateStudents(numStudents);

		when(studentRepository.findAll()).thenReturn(students);
		assertEquals(numStudents, Iterables.size(studentRepository.findAll()));

		int numberOfPages = (numStudents % pageLen == 0) ? numStudents / pageLen : numStudents / pageLen + 1;
		System.out.println("NPAGES " + numberOfPages);

		for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
			testWithoutFilteringOrdering(numStudents, pageLen, students, pageIndex);
		}
	}

	private List<Student> generateStudents(int numStudents) {
		List<Student> students = new ArrayList<>();

		for (int i = 0; i < numStudents; i++) {
			int n = i + 1;
			String idx = Integer.toString(n);
			String first = "Student" + n;
			String last = "Smith" + n;
			boolean external = i % 2 == 0;

			Student student = new Student(idx, first, last, external);
			students.add(student);
		}

		return students;
	}

}
