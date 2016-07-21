package mxc.demo.campus.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import mxc.demo.campus.validator.AmountPaid;

/**
 * The student editing form that the server will export to the client,
 * and which holds data returned to the server for updating.
 * It contains a list of ALL courses including flags to show which ones
 * the student is enrolled in.
 */
public class StudentForm extends UserForm {

	private boolean external = false;
	
	@JsonSerialize(using=JsonMoneySerialiser.class)
	@AmountPaid
	private BigDecimal amountPaid;
	
	private List<FormEnrolment> enrolments = new ArrayList<>();

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

	public List<FormEnrolment> getEnrolments() {
		return enrolments;
	}

	public void setEnrolments(List<FormEnrolment> enrolments) {
		this.enrolments = enrolments;
	}

	@Override
	public String toString() {
		return "StudentForm [external=" + external + ", amountPaid=" + amountPaid + ", enrolments=" + enrolments
				+ ", toString()=" + super.toString() + "]";
	}
}

