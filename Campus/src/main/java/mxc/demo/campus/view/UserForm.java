package mxc.demo.campus.view;

import mxc.demo.campus.validator.CampusEmail;
import mxc.demo.campus.validator.PersonName;
import mxc.demo.campus.validator.UserId;

/**
 * Base class for forms that are typically exported from server to client, but
 * which can also hold data to be returned to the server for updating.
 */
public class UserForm {

	private long id;

	@UserId
	private String userId;

	@PersonName
	private String firstName;

	@PersonName
	private String lastName;

	@CampusEmail
	private String emailAddress;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserForm [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", emailAddress=" + emailAddress + "]";
	}

}
