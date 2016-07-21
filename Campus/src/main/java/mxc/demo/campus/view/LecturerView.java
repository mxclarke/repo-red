package mxc.demo.campus.view;

import java.math.BigDecimal;

public class LecturerView extends UserForm {
	
	private String title;
	
	private BigDecimal salary;
	
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
	/**
	 * @return the salary
	 */
	public BigDecimal getSalary() {
		return salary;
	}
	/**
	 * @param salary the salary to set
	 */
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LecturerView [title=" + title + ", salary=" + salary + ", toString()=" + super.toString() + "]";
	}
}
