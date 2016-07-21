package mxc.demo.campus.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import mxc.demo.campus.validator.LecturerSalary;
import mxc.demo.campus.validator.PersonTitle;

/**
 * Domain entity for a user who has administrator privileges.
 * 
 * You need your hashcode()/equals() for paging/sorting because the returned
 * data is manipulated as a Map. Note that I'm not using Lombok.
 */
@Entity
@DiscriminatorValue("L")
public class Lecturer extends User {
	
	/**
	 * The lecturer's title, e.g. Dr, Prof. and so on.
	 */
	@PersonTitle
	private String title;
	
	/**
	 * The lecturer's salary.
	 */
	//@Convert(converter = MoneyConverter.class)
	//@JsonSerialize(using=JsonMoneySerialiser.class)
	@LecturerSalary
	private BigDecimal salary;
	
	/**
	 * THe courses that this lecturer supervises.
	 */
	@OneToMany(mappedBy="lecturer")
	@JsonBackReference
	private List<Course> courses = new ArrayList<>();

	public Lecturer(String userId, String password, String title, String firstName, String lastName, String emailAddress, BigDecimal salary) {
		super(userId, password, firstName, lastName, emailAddress, UserRole.Lecturer);
		this.title = title;
		this.salary = salary;
	}

	public Lecturer() {
		setRole(UserRole.Lecturer);
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public List<Course> getCourses() {
		return Collections.unmodifiableList(courses);
	}

	@Override
	public String toString() {
		return "Lecturer [" + super.toString() + ", salary="
				+ salary + "]";
	}
}
