package mxc.demo.campus.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// https://dzone.com/articles/jpa-implementation-patterns-4

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;

import com.fasterxml.jackson.annotation.JsonBackReference;

import mxc.demo.campus.validator.AmountPaid;

/**
 * Domain entity for a user who is also a student at the university.
 *
 * You need your hashcode()/equals() for paging/sorting because the returned
 * data is manipulated as a Map. Note that I'm not using Lombok.
 */
@Entity
@DiscriminatorValue("S")
public class Student extends User {

	/**
	 * Whether the student is studying externally (off-campus).
	 */
	private boolean external = false;
	
	/**
	 * The amount this student has paid so far towards their course fees.
	 */
	//@Convert(converter = MoneyConverter.class)
	//private MonetaryAmount amountPaid;
	@AmountPaid
	//@JsonSerialize(using=JsonMoneySerialiser.class)
	private BigDecimal amountPaid;
	
	// TODO enrolmentDate
//	@Temporal(javax.persistence.TemporalType.DATE)
//    @Past
//    protected Date enrolmentDate;
	
	/**
	 * The courses in which this student is enrolled.
	 */
	@ManyToMany(mappedBy="students")
	@JsonBackReference
	private List<Course> courses = new ArrayList<>();

	public Student(String userId, String password, String firstName, String lastName, String emailAddress, boolean external, BigDecimal amountPaid) {
		super(userId, password, firstName, lastName, emailAddress, UserRole.Student);
		this.external = external;
		this.amountPaid = amountPaid;
	}

	public Student() {
		setRole(UserRole.Student);
	}

	@PreRemove
	private void removeFromOwner() {
		courses.forEach(c -> c.removeStudent(this));
	}
	
	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public List<Course> getCourses() {
		return Collections.unmodifiableList(courses);
	}

	@Override
	public String toString() {
		return "Student [" + super.toString() + ", external=" + external + ", amountPaid="
				+ amountPaid + "]";
	}

}
