package mxc.demo.campus.view;

import java.math.BigDecimal;
import java.util.List;

/**
 * The RO student's view that the server exports to the client.
 */
public class StudentView extends UserForm {

	private boolean external = false;

	// @JsonSerialize(using=JsonMoneySerialiser.class)
	private BigDecimal amountPaid;

	private List<ViewCourse> courses;

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

	public List<ViewCourse> getCourses() {
		return courses;
	}

	public void setCourses(List<ViewCourse> courses) {
		this.courses = courses;
	}

	@Override
	public String toString() {
		return "StudentView [external=" + external + ", amountPaid=" + amountPaid + ", toString()=" + super.toString()
				+ "]";
	}

}
