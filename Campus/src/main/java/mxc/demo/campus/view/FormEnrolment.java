package mxc.demo.campus.view;

import java.math.BigDecimal;

import mxc.demo.campus.domain.Course;

/**
 * A sub-component of other forms, usually as part of a collection.
 * Shows information about one course and whether or not a student
 * is enrolled in it. Allows studet or admin to update the enrolled field.
 * Not used to edit the actual courses.
 */
public class FormEnrolment {

	private int id;
	
	private String name;
	
	private String description;
	
	private BigDecimal cost;
	
	private boolean enrolled;

	public FormEnrolment() {}
	
	public FormEnrolment(int id, String name, String description, BigDecimal cost, boolean enrolled) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.enrolled = enrolled;
	}
	
	public FormEnrolment(Course course, boolean isEnrolled) {
		this(course.getId(), course.getName(), course.getDescription(), course.getCost(), isEnrolled);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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

	/**
	 * @return the cost
	 */
	public BigDecimal getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	/**
	 * @return the isEnrolled
	 */
	public boolean isEnrolled() {
		return enrolled;
	}

	/**
	 * @param enrolled the enrolled to set
	 */
	public void setEnrolled(boolean enrolled) {
		this.enrolled = enrolled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FormEnrolment [id=" + id + ", name=" + name + ", description=" + description + ", cost=" + cost
				+ ", enrolled=" + enrolled + "]";
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
		FormEnrolment other = (FormEnrolment) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
