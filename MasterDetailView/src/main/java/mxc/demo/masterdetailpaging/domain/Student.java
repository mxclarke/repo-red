package mxc.demo.masterdetailpaging.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotBlank;

/**
 * A Student entity.
 * 
 * You need your hashcode()/equals() for paging/sorting because the returned
 * data is manipulated as a Map. Note that I'm not using Lombok.
 */
@Entity
public class Student {

	@Id
	@GeneratedValue
	private int id;

	@NotBlank
	private String studentId;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;

	/**
	 * Whether the student is studying externally (off-campus).
	 */
	private boolean external = false;

	public Student(String studentId, String firstName, String lastName, boolean external) {
		this.studentId = studentId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.external = external;
	}

	protected Student() {
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", studentId=" + studentId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", external=" + external + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (external ? 1231 : 1237);
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((studentId == null) ? 0 : studentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (external != other.external)
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (studentId == null) {
			if (other.studentId != null)
				return false;
		} else if (!studentId.equals(other.studentId))
			return false;
		return true;
	}
}
