package mxc.demo.campus.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import mxc.demo.campus.validator.CourseDescription;
import mxc.demo.campus.validator.CourseName;

/**
 * Domain entity representing a course at the university.
 */
@Entity
public class Course {
	
	@Id
	@GeneratedValue
	private int id;
	
	/**
	 * TODO I think it would be better to have a unique identifier (separate to the
	 * persistence ID) for courses as the immutable key, although the name should
	 * still also be unique. Change equals/hashcode accordingly.
	 */
	@CourseName
	@Column(unique=true, updatable=false)
	private String name;
	
	@CourseDescription
	private String description;
		
	@NotNull
	//@Convert(converter = MoneyConverter.class)
	//@JsonSerialize(using=JsonMoneySerialiser.class)
	private BigDecimal cost;

	/**
	 * THe students who are enrolled in this course.
	 */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinTable(name="COURSE_STUDENTS")
	@JsonManagedReference
	private List<Student> students = new ArrayList<>();
	
	/**
	 * The supervising lecturer for this course.
	 */
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JsonManagedReference
	private Lecturer lecturer;

	public Course(String name, String description, BigDecimal cost) {
		this.name = name;
		this.description = description;
		this.cost = cost;
	}
	
	public Course() {	
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public List<Student> getStudents() {
		return Collections.unmodifiableList(students);
	}
	
	public void addStudent(Student student) {
		this.students.add(student);
	}
	
	public void removeStudent(Student student) {
		this.students.remove(student);
	}
	
	public Lecturer getLecturer() {
		return lecturer;
	}
	
	public void setLecturer(Lecturer lecturer) {
		this.lecturer = lecturer;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", description=" + description 
				+ ", cost=" + cost + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
